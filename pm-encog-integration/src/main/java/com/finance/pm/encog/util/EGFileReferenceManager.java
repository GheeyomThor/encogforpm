package com.finance.pm.encog.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.finance.pm.encog.application.InputOutputDescription;
import com.finance.pm.encog.application.NetworkDescription;

public class EGFileReferenceManager {

	private static Logger LOGGER = Logger.getLogger(EGFileReferenceManager.class.getName());

	static String PATHNAME = System.getProperty("installdir") + File.separator + "neural" + File.separator + "egDescription.txt";
	private static final String sep = " is defined by ";

	/**
	 * Will try and find an existing match to return or create, inserts and returns a new one if no match is found.
	 * It will always return the last match found.
	 * XXXXXX matches any char.
	 * @param inputOutputDescription
	 * @param networkDescription
	 * @return
	 */
	public synchronized String[] encogFileNameReGenerator(
			Optional<InputOutputDescription> inputOutputDescription,
			Optional<NetworkDescription> networkDescription) {

		if (!inputOutputDescription.isPresent() && !networkDescription.isPresent()) {
			return new String[] {CsvImportExport.runStamp.toString(), "NaN"};
		}

		if(inputOutputDescription.isPresent() && networkDescription.isPresent()) {

			String[] foundEntry = findEntry(inputOutputDescription.get(), networkDescription.get());
			
			if (foundEntry != null) {//Existing line
				return foundEntry;
			}

			//New line
			String fileDescr = networkDescription.get().toString() + " " + inputOutputDescription.get().toString();
			if (fileDescr.contains("X")) throw new RuntimeException("Can't insert pattern description into egDescription.txt : "+fileDescr);

			LOGGER.info("No entry was found for description (creating) "+fileDescr+".");
			UUID appRunUUID = CsvImportExport.runStamp;
			UUID fileUUID = UUID.randomUUID();
			String newFileName = appRunUUID+"_"+ fileUUID;
			String newEntry = newFileName+ sep + fileDescr;
			String[] entryDescr = new String[] {newFileName , newEntry};
			this.updateEncogFileNameDescriptions(entryDescr);
			return entryDescr;
		}

		throw new RuntimeException("Inconsistent call. Should not be here.");

	}

	public static String[] findEntry(InputOutputDescription inputOutputDescription, NetworkDescription networkDescription) {

		String[] foundEntry = null;

		//Check existing(and return latest)
		String fileDescr = networkDescription.toString() + " " + inputOutputDescription.toString();
		File egDescription = new File(PATHNAME);
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(egDescription))) {
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				String[] split = line.split(sep);
				if (split.length != 2) throw new RuntimeException("Invalid entry in Encog description file : "+line);

				//Compare side by side
				Boolean sameLine = true;
				String lineFileDescr = split[1];
				if (lineFileDescr.length() != fileDescr.length()) continue;
				for (int i = 0; i < fileDescr.length(); i++) {
					if (fileDescr.charAt(i) != 'X' && lineFileDescr.charAt(i) != 'X' && fileDescr.charAt(i) != lineFileDescr.charAt(i)) {
						sameLine = false;
						break;
					}
				}
				if (sameLine) {
					LOGGER.info("Found existing description "+split[1]+" matching "+ lineFileDescr+" :\n\t "+split[0]);
					foundEntry = new String[] {split[0], line}; 
				}
			}
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return foundEntry;
	}

	/**
	 * Write the line as requested without any other check.
	 * @param entryDescr
	 */
	private void updateEncogFileNameDescriptions(String[] entryDescr) {

		LOGGER.info("Adding new entry for description "+entryDescr[1]+ " :\n\t " +entryDescr[0]);
		File egDescription = new File(PATHNAME);
		try (BufferedWriter bufferedReader = new BufferedWriter(new FileWriter(egDescription, true))) {
			bufferedReader.write(entryDescr[1]);
			bufferedReader.newLine();
		}  catch (Exception e){
			throw new RuntimeException(e);
		}

	}

	/** 
	 * XXX: This will only take in account ONE XXXXXXXX substitution mask (Only one mask is allowed at a time)
	 * It should be called with XXXXXXXX date mask to invalidate all entries matching.
	 * //TODO count the number of masks and create the endDescrSubstitution accordingly.
	 * @param inputOutputDescription
	 * @param networkDescription
	 */
	public static void invalidateEntries(InputOutputDescription inputOutputDescription, NetworkDescription networkDescription) {

		Path path = Paths.get(PATHNAME);
		try {
			String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			String fileDescrRegExp = (networkDescription.toString() + " " + inputOutputDescription.toString()).replaceAll("X+", "(.*)") + "\\n";
			fileDescrRegExp = fileDescrRegExp.replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]");
			String endDescrSubstitution = inputOutputDescription.toString().replaceAll("X+", "\\$1");
			String fileDescrDirtySubstitution = networkDescription.toString() + " " + endDescrSubstitution+ " YDirtyY\n";
			content = content.replaceAll(fileDescrRegExp, fileDescrDirtySubstitution);
			Files.write(path, content.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}

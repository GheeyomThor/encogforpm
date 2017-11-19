package com.finance.pm.encog.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Optional;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.finance.pm.encog.application.InputOutputDescription;
import com.finance.pm.encog.application.NetworkDescription;

public class EGFileReferenceManager {

    private static Logger LOGGER = Logger.getLogger(EGFileReferenceManager.class.getName());

    private static final String sep= " is defined by ";

    public synchronized String[] encogFileNameGenerator(
            Optional<InputOutputDescription> inputOutputDescription,
            Optional<NetworkDescription> networkDescription) {

        if (!inputOutputDescription.isPresent() && !networkDescription.isPresent()) {
            return new String[] {CsvImportExport.runStamp.toString(), "NaN"};
        }

        if(inputOutputDescription.isPresent() && networkDescription.isPresent()) {

            String[] foundEntry = findEntry(inputOutputDescription.get(), networkDescription.get());
            if (foundEntry != null) return foundEntry;

            //New line
            String fileDescr = networkDescription.get().toString() + " " + inputOutputDescription.get().toString();
            LOGGER.info("No entry was found for description "+fileDescr+".");
            UUID appRunUUID = CsvImportExport.runStamp;
            UUID fileUUID = UUID.randomUUID();
            String newFileName = appRunUUID+"_"+ fileUUID;
            String newEntry = newFileName+ sep + fileDescr;
            String[] entryDescr = new String[] {newFileName , newEntry};
            this.updateEncogFileNameDescriptions(entryDescr);
            return entryDescr;
        }

        throw new RuntimeException("Inconsistent call");

    }

    public static String[] findEntry(InputOutputDescription inputOutputDescription, NetworkDescription networkDescription) {

        String[] foundEntry = null;

        //Check existing(and return latest)
        String fileDescr = networkDescription.toString() + " " + inputOutputDescription.toString();
        File egDescription = new File(System.getProperty("installdir") + File.separator + "egDescription.txt");
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
                    LOGGER.info("Found already existing description "+split[1]+" matching "+ lineFileDescr+" :\n\t "+split[0]);
                    foundEntry = new String[] {split[0], line}; 
                }
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return foundEntry;
    }

    void updateEncogFileNameDescriptions(String[] entryDescr) {

        LOGGER.info("Adding new entry for description "+entryDescr[1]+ " :\n\t " +entryDescr[0]);
        File egDescription = new File(System.getProperty("installdir") + File.separator + "egDescription.txt");
        try (BufferedWriter bufferedReader = new BufferedWriter(new FileWriter(egDescription, true))) {
            bufferedReader.write(entryDescr[1]);
            bufferedReader.newLine();
        }  catch (Exception e){
            throw new RuntimeException(e);
        }

    }

}

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
    
    public String[] encogFileNameGenerator(
            Optional<InputOutputDescription> inputOutputDescription,
            Optional<NetworkDescription> networkDescription) {
        
        if (!inputOutputDescription.isPresent() && !networkDescription.isPresent()) {
            return new String[] {CsvImportExport.runStamp.toString(), "NaN"};
        }
        
        if(inputOutputDescription.isPresent() && networkDescription.isPresent()) {
            String fileDescr = networkDescription.get().toString() + " " + inputOutputDescription.get().toString();
            File egDescription = new File(System.getProperty("installdir") + File.separator + "egDescription.txt");
            String sep = " is defined by ";

            //Check existing(and return latest)
            String[] found = null;
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(egDescription))) {
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] split = line.split(sep);
                    if (split.length != 2) throw new RuntimeException("Invalid entry in Encog description file : "+line);
                    if (split[1].equals(fileDescr)) {
                        LOGGER.info("Found already calculated description "+split[1]+".");
                        found = new String[] {split[0], line};
                    }
                }
            } catch (Exception e){
                throw new RuntimeException(e);
            }
            if (found != null) return found;

            //New line
            LOGGER.info("No entry for description "+fileDescr+".");
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

    void updateEncogFileNameDescriptions(String[] entryDescr) {

        //Writing new line
        File egDescription = new File(System.getProperty("installdir") + File.separator + "egDescription.txt");
        try (BufferedWriter bufferedReader = new BufferedWriter(new FileWriter(egDescription, true))) {
            bufferedReader.write(entryDescr[1]);
            bufferedReader.newLine();
        }  catch (Exception e){
            throw new RuntimeException(e);
        }

    }

}

package com.finance.pm.encog.util.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;

import com.finance.pm.encog.util.CsvImportExport;

public class MLDataPairCsvImportExport implements CsvImportExport<Integer> {

    private static Logger LOGGER = Logger.getLogger(MLDataPairCsvImportExport.class);

    @Override
    public void exportData(String baseFileName, String exportFileNameExt, Map<Integer, double[]> map) {

        //runStamp+"_"+exportFileNameExt
        String pathname = baseFileName+"_"+exportFileNameExt;
        File exportFile = new File(System.getProperty("installdir") + File.separator + "neural" + File.separator + pathname+".csv");

        try (FileWriter fileWriter = new FileWriter(exportFile);
                BufferedWriter bufferWriter = new BufferedWriter(fileWriter)) {

            map.entrySet().stream().forEach(entry -> {

                try {
                    //double[] ideals = entry.getKey().getIdealArray();
                    //String idealsString = Arrays.toString(ideals);
                    //bufferWriter.write(idealsString.substring(1, idealsString.length() - 1));
                    bufferWriter.write(entry.getKey());

                    bufferWriter.write(", ");

                    double[] value = entry.getValue();
                    String valueString = Arrays.toString(value);
                    bufferWriter.write(valueString.substring(1, valueString.length() - 1));

                    bufferWriter.newLine();

                } catch (IOException e) {
                    LOGGER.error("Could export training ios ", e);
                    throw new RuntimeException(e);
                }

            });

        } catch (Exception e) {
            LOGGER.error("Could export training ios ", e);
            throw new RuntimeException(e);
        }

    }

}

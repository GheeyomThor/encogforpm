package com.finance.pm.encog.util.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.encog.ml.data.MLDataPair;

import com.finance.pm.encog.application.InputOutputDescription;
import com.finance.pm.encog.application.NetworkDescription;
import com.finance.pm.encog.util.CsvImportExport;
import com.finance.pm.encog.util.EGFileReferenceManager;

public class MLDataPairCsvImportExport implements CsvImportExport<MLDataPair> {

    private static Logger LOGGER = Logger.getLogger(MLDataPairCsvImportExport.class);

    @Inject
    EGFileReferenceManager egFileReferenceManager;

    @Override
    public void exportData(Optional<InputOutputDescription> iODescr, Optional<NetworkDescription> netDescr, String exportFileNameExt, Map<MLDataPair, double[]> map) {

        //runStamp+"_"+exportFileNameExt
        String pathname = egFileReferenceManager.encogFileNameGenerator(iODescr, netDescr)[0]+"_"+exportFileNameExt;
        File exportFile = new File(System.getProperty("installdir")+File.separator+pathname+".csv");

        try (FileWriter fileWriter = new FileWriter(exportFile);
                BufferedWriter bufferWriter = new BufferedWriter(fileWriter)) {

            map.entrySet().stream().forEach(entry -> {

                try {
                    //bufferWriter.write(String.format("%f, ", entry.getKey().getIdealArray()[0]));
                    double[] key = entry.getKey().getIdealArray();
                    String keyString = Arrays.toString(key);
                    bufferWriter.write(keyString.substring(1, keyString.length() - 1));

                    bufferWriter.write(", ");

                    double[] value = entry.getValue();
                    String valueString = Arrays.toString(value);
                    bufferWriter.write(valueString.substring(1, valueString.length() - 1));

                    bufferWriter.newLine();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });

        } catch (Exception e) {
            LOGGER.error("Could export training ios ", e);
            throw new RuntimeException(e);
        }

    }

}

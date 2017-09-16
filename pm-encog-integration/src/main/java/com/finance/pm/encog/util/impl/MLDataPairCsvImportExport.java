package com.finance.pm.encog.util.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.encog.ml.data.MLDataPair;

import com.finance.pm.encog.util.CsvImportExport;

public class MLDataPairCsvImportExport implements CsvImportExport<MLDataPair> {

    @Override
    public void exportData(File exportFile, Map<MLDataPair, double[]> map) {

        try (FileWriter fileWriter = new FileWriter(exportFile);
                BufferedWriter bufferWriter = new BufferedWriter(fileWriter)) {

            map.entrySet().stream().forEach(entry -> {

                try {
                    bufferWriter.write(String.format("%f, ", entry.getKey().getIdealArray()[0]));
                    double[] value = entry.getValue();
                    String valueString = Arrays.toString(value);
                    bufferWriter.write(valueString.substring(1, valueString.length() - 1));
                    bufferWriter.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}

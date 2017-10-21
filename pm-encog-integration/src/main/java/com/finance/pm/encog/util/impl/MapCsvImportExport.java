package com.finance.pm.encog.util.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;

import com.finance.pm.encog.application.InputOutputDescription;
import com.finance.pm.encog.application.NetworkDescription;
import com.finance.pm.encog.util.CsvImportExport;
import com.finance.pm.encog.util.EGFileReferenceManager;

public class MapCsvImportExport implements CsvImportExport<Date> {
    
    @Inject
    EGFileReferenceManager egFileReferenceManager;

    @Override
    public void exportData(Optional<InputOutputDescription> iODescr, Optional<NetworkDescription> netDescr, String exportFileNameExt, Map<Date, double[]> map) {
        
        //runStamp+"_"+exportFileNameExt
        String pathname = egFileReferenceManager.encogFileNameGenerator(iODescr, netDescr)[0]+"_"+exportFileNameExt;
        File exportFile = new File(System.getProperty("installdir")+File.separator+pathname+".csv");

        try (FileWriter fileWriter = new FileWriter(exportFile);
                BufferedWriter bufferWriter = new BufferedWriter(fileWriter)) {

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            map.entrySet().stream().forEach(entry -> {

                try {
                    bufferWriter.write(String.format("%s, ", dateFormatter.format(entry.getKey())));
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

    @Override
    public SortedMap<Date, double[]> importData(File exportFile) {

        SortedMap<Date, double[]> map = new TreeMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(exportFile))) {

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] rowSplit = line.split(",");
                double[] array = Arrays.asList(rowSplit).subList(1, rowSplit.length).stream()
                        .mapToDouble(x -> Double.valueOf(x)).toArray();
                map.put(dateFormatter.parse(rowSplit[0]), array);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return map;
    }

}

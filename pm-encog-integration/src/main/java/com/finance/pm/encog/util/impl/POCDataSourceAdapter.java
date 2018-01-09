package com.finance.pm.encog.util.impl;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.finance.pm.encog.util.DataSourceAdapter;

/**
 * Class importing pre recorded training data from the file system for proof of
 * concept only
 *
 */
public class POCDataSourceAdapter implements DataSourceAdapter {

    private MapCsvImportExport mapCsvImportExport;

    @Inject
    public POCDataSourceAdapter(MapCsvImportExport mapCsvImportExport) {
        super();
        this.mapCsvImportExport = mapCsvImportExport;
    }

    @Override
    public List<double[]> getTrainingOutputs() {
        URL outputs = this.getClass().getResource("/idealOutputs.csv");
        return new ArrayList<>(mapCsvImportExport.importData(new File(outputs.getPath())).values());
    }

    @Override
    public List<double[]> getTrainingInputs() {
        URL inputs = this.getClass().getResource("/trainingInputs.csv");
        return new ArrayList<>(mapCsvImportExport.importData(new File(inputs.getPath())).values());
    }

    @Override
    public List<String> getInputEventsDescription() {
        return Arrays.asList((new String[26]));
    }

    @Override
    public List<Date> getTrainingInputsDatesList() {
        URL inputs = this.getClass().getResource("/trainingInputs.csv");
        return new ArrayList<>(mapCsvImportExport.importData(new File(inputs.getPath())).keySet());
    }

    @Override
    public File getDataFile() {
        return null;
    }

}

package com.finance.pm.encog.util.impl;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

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
    public SortedMap<Date, double[]> geTrainingOutputs() {
        URL inputs = this.getClass().getResource("/idealOutputs.csv");
        return mapCsvImportExport.importData(new File(inputs.getPath()));
    }

    @Override
    public SortedMap<Date, double[]> geTrainingInputs() {
        URL outputs = this.getClass().getResource("/trainingInputs.csv");
        return mapCsvImportExport.importData(new File(outputs.getPath()));
    }

    @Override
    public List<String> getInputEventsDescription() {
        return Arrays.asList((new String[26]));
    }

}

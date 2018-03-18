package com.finance.pm.encog.data.impl;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.model.normalisation.NormalisationStrategySetter;
import org.encog.util.csv.CSVFormat;

import com.finance.pm.encog.data.DataSetLoader;
import com.finance.pm.encog.util.CsvImportExport;
import com.finance.pm.encog.util.DataSourceAdapter;
import com.google.inject.Inject;

//TODO include a lead and lag window as in Temporal data set
public class VersatileDataSetLoader implements DataSetLoader {

    @Inject
    DataSourceAdapter dataDataAdapter;

    @Inject
    CsvImportExport<Date> analysedExporter;

    @Override
    public VersatileMLDataSet loadData(
            ColumnType inputColumnType, ColumnType outputColumnType, 
            int lagWindowSize, int leadWindowSize,
            String modelType, String modelArchitecture) {

        List<String> inputEventsDescription = dataDataAdapter.getInputEventsDescription();

        VersatileDataSource source;
        File ioFile = dataDataAdapter.getDataFile();
        if (ioFile != null) {
            source = new CSVDataSource(ioFile, false, CSVFormat.DECIMAL_POINT);
        } else {
            List<double[]> trainingInputValues = dataDataAdapter.getTrainingInputs();
            List<double[]> trainingIdealValues = dataDataAdapter.getTrainingOutputs();
            source = new VersatileMapDataSource(trainingInputValues, trainingIdealValues);
        }

        VersatileMLDataSet dataSet = new VersatileMLDataSet(source);

        for (int i = 0; i < inputEventsDescription.size(); i++) {
            dataSet.defineSourceColumn(inputEventsDescription.get(i), i, inputColumnType);
        }

        ColumnDefinition outputColumn = dataSet.defineSourceColumn("trends", inputEventsDescription.size(), outputColumnType);
        dataSet.defineSingleOutputOthersInput(outputColumn);

        //Analyze the data, determine the min/max/mean/sd of every column.
        //This is done using the VersatileDataSource for data source and NormalizationHelper data description
        dataSet.analyze();

        NormalisationStrategySetter normalisationHelperStrategyBuilder = 
                new NormalisationStrategySetter()
                .withMethod(modelType)
                .withArchitecture(modelArchitecture);

        normalisationHelperStrategyBuilder.setStrategyTo(dataSet);

        //Now normalise the data (create a normalised matrix in the VersatileMLDataSet). 
        dataSet.normalize();

        dataSet.setLagWindowSize(lagWindowSize);
        dataSet.setLeadWindowSize(leadWindowSize);

        return dataSet;
    }

}

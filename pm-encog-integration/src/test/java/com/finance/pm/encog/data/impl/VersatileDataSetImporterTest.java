package com.finance.pm.encog.data.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.junit.Before;
import org.junit.Test;

import com.finance.pm.encog.data.DataSetLoader;
import com.finance.pm.encog.guice.EncogServiceModule;
import com.finance.pm.encog.util.DataSourceAdapter;
import com.finance.pm.encog.util.impl.MapCsvImportExport;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class VersatileDataSetImporterTest {
    
    @Inject @Named("versatile")
    private DataSetLoader temporalDataSetImporter;
    @Inject
    private MapCsvImportExport mapCsvImportExport;
    
    private DataSourceAdapter dataSourceMock;

    @Before
    public void setUp() throws Exception {
        dataSourceMock = mock(DataSourceAdapter.class);
        Injector injector = Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                bind(DataSourceAdapter.class).toInstance(dataSourceMock);
            }

        }, new EncogServiceModule());
        injector.injectMembers(this);
    }

    @Test
    public void testImportData() {

        // Given
        URL inputsFile = this.getClass().getResource("/trainingInputs.csv");
        List<double[]> inputsData = new ArrayList<>(mapCsvImportExport.importData(new File(inputsFile.getPath())).values());

        URL outputs = this.getClass().getResource("/idealOutputs.csv");
        List<double[]>  outputsData = new ArrayList<>(mapCsvImportExport.importData(new File(outputs.getPath())).values());

        String[] inputColumnsNames = new String[26];
        for(int i = 0 ; i < inputColumnsNames.length; i++) {
            inputColumnsNames[i] = "col"+i;
        }
        when(dataSourceMock.getInputEventsDescription()).thenReturn(Arrays.asList(inputColumnsNames));
        when(dataSourceMock.getTrainingInputs()).thenReturn(inputsData);
        when(dataSourceMock.getTrainingOutputs()).thenReturn(outputsData);

        // When
        int inputWindowSize = 0; //Not used yet
        int predictWindowSize = 0; //Not used yet
        VersatileMLDataSet dataSet = (VersatileMLDataSet) temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous, inputWindowSize, predictWindowSize);
        EncogModel model = new EncogModel(dataSet);
        model.selectMethod(dataSet, MLMethodFactory.TYPE_FEEDFORWARD);
        dataSet.normalize();
        
        // Then
        assertEquals(inputsData.size(), dataSet.getRecordCount());
        assertEquals(outputsData.size(), dataSet.getRecordCount());
//        assertEquals(26, dataSet.getInputSize());
//        assertEquals(1, dataSet.getIdealSize());
    }

}

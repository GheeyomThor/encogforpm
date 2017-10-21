package com.finance.pm.encog.data.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.inject.Named;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.finance.pm.encog.data.DataSetLoader;
import com.finance.pm.encog.guice.EncogServiceModule;
import com.finance.pm.encog.util.DataSourceAdapter;
import com.finance.pm.encog.util.impl.MapCsvImportExport;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class TemporalDataSetImporterTest {

    @Inject @Named("temporal")
    private DataSetLoader temporalDataSetImporter;
    @Inject
    private MapCsvImportExport mapCsvImportExport;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
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
        URL inputsFile = this.getClass().getResource("/idealOutputs.csv");
        List<double[]> inputsData = new ArrayList<double[]>(mapCsvImportExport.importData(new File(inputsFile.getPath())).values());

        URL outputs = this.getClass().getResource("/trainingInputs.csv");
        List<double[]> outputsData = new ArrayList<double[]>(mapCsvImportExport.importData(new File(outputs.getPath())).values());

        when(dataSourceMock.getInputEventsDescription()).thenReturn(Arrays.asList((new String[26])));
        when(dataSourceMock.getTrainingInputs()).thenReturn(inputsData);
        when(dataSourceMock.getTrainingOutputs()).thenReturn(outputsData);

        // When
        int inputWindowSize = 12;
        int predictWindowSize = 1;
        MLDataSet dataSet = temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous, inputWindowSize, predictWindowSize);

        // Then
        assertEquals(inputsData.size() - inputWindowSize - predictWindowSize, dataSet.getRecordCount());
        assertEquals(outputsData.size() - inputWindowSize - predictWindowSize, dataSet.getRecordCount());
        assertEquals(1, dataSet.getIdealSize());
        assertEquals(26 * inputWindowSize, dataSet.getInputSize());
    }

    @Test
    public void testImportDataCorruptedInputsMissing() {

        // Given
        URL inputsFile = this.getClass().getResource("/idealOutputs.csv");
        List<double[]> inputsData = new ArrayList<double[]>(mapCsvImportExport.importData(new File(inputsFile.getPath())).values());
        int randomKey = pickRandomKey(inputsData);
        inputsData.remove(randomKey);

        URL outputs = this.getClass().getResource("/trainingInputs.csv");
        List<double[]> outputsData = new ArrayList<double[]>(mapCsvImportExport.importData(new File(outputs.getPath())).values());

        when(dataSourceMock.getInputEventsDescription()).thenReturn(Arrays.asList((new String[26])));
        when(dataSourceMock.getTrainingInputs()).thenReturn(inputsData);
        when(dataSourceMock.getTrainingOutputs()).thenReturn(outputsData);

        // Expect
        thrown.expect(RuntimeException.class);

        // When
        int inputWindowSize = 12;
        int predictWindowSize = 1;
        temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous, inputWindowSize, predictWindowSize);

    }

    private int pickRandomKey(List<double[]> inputsData) {
        Random random = new Random();
        return random.nextInt(inputsData.size());
    }

    @Test(expected = RuntimeException.class)
    public void testImportDataCorruptedInputsNull() {

        // Given
        URL inputsFile = this.getClass().getResource("/idealOutputs.csv");
        List<double[]> inputsData = new ArrayList<double[]>(mapCsvImportExport.importData(new File(inputsFile.getPath())).values());
        int randomKey = pickRandomKey(inputsData);
        inputsData.set(randomKey, null);

        URL outputs = this.getClass().getResource("/trainingInputs.csv");
        List<double[]> outputsData = new ArrayList<double[]>(mapCsvImportExport.importData(new File(outputs.getPath())).values());

        when(dataSourceMock.getInputEventsDescription()).thenReturn(Arrays.asList((new String[26])));
        when(dataSourceMock.getTrainingInputs()).thenReturn(inputsData);
        when(dataSourceMock.getTrainingOutputs()).thenReturn(outputsData);

        // When
        int inputWindowSize = 12;
        int predictWindowSize = 1;
        temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous, inputWindowSize, predictWindowSize);

    }

    @Test(expected = RuntimeException.class)
    public void testImportDataCorruptedInputElement() {

        // Given
        URL inputsFile = this.getClass().getResource("/idealOutputs.csv");
        List<double[]> inputsData = new ArrayList<double[]>(mapCsvImportExport.importData(new File(inputsFile.getPath())).values());
        int randomKey = pickRandomKey(inputsData);
        inputsData.set(randomKey, new double[42]);

        URL outputs = this.getClass().getResource("/trainingInputs.csv");
        List<double[]> outputsData = new ArrayList<double[]>(mapCsvImportExport.importData(new File(outputs.getPath())).values());

        when(dataSourceMock.getInputEventsDescription()).thenReturn(Arrays.asList((new String[26])));
        when(dataSourceMock.getTrainingInputs()).thenReturn(inputsData);
        when(dataSourceMock.getTrainingOutputs()).thenReturn(outputsData);

        // When
        int inputWindowSize = 12;
        int predictWindowSize = 1;
        temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous, inputWindowSize, predictWindowSize);

    }

    @Test(expected = RuntimeException.class)
    public void testImportDataCorruptedDescription() {

        // Given
        URL inputsFile = this.getClass().getResource("/idealOutputs.csv");
        List<double[]> inputsData = new ArrayList<double[]>(mapCsvImportExport.importData(new File(inputsFile.getPath())).values());

        URL outputs = this.getClass().getResource("/trainingInputs.csv");
        List<double[]> outputsData = new ArrayList<double[]>(mapCsvImportExport.importData(new File(outputs.getPath())).values());

        List<String> inputEventsDescription = dataSourceMock.getInputEventsDescription();
        inputEventsDescription.remove(new Random().nextInt(inputEventsDescription.size()));

        when(inputEventsDescription).thenReturn(Arrays.asList((new String[26])));
        when(dataSourceMock.getTrainingInputs()).thenReturn(inputsData);
        when(dataSourceMock.getTrainingOutputs()).thenReturn(outputsData);

        // When
        int inputWindowSize = 12;
        int predictWindowSize = 1;
        temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous, inputWindowSize, predictWindowSize);

    }

}

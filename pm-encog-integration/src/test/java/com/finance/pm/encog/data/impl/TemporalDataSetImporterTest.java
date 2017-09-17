package com.finance.pm.encog.data.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;

import org.encog.ml.data.MLDataSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.finance.pm.encog.data.DataImporter;
import com.finance.pm.encog.guice.EncogServiceModule;
import com.finance.pm.encog.util.DataSourceAdapter;
import com.finance.pm.encog.util.impl.MapCsvImportExport;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class TemporalDataSetImporterTest {

    @Inject
    private DataImporter temporalDataSetImporter;
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
        SortedMap<Date, double[]> inputsData = mapCsvImportExport.importData(new File(inputsFile.getPath()));

        URL outputs = this.getClass().getResource("/trainingInputs.csv");
        SortedMap<Date, double[]> outputsData = mapCsvImportExport.importData(new File(outputs.getPath()));

        when(dataSourceMock.getInputEventsDescription()).thenReturn(Arrays.asList((new String[26])));
        when(dataSourceMock.geTrainingInputs()).thenReturn(inputsData);
        when(dataSourceMock.geTrainingOutputs()).thenReturn(outputsData);

        // When
        int inputWindowSize = 12;
        int predictWindowSize = 1;
        MLDataSet dataSet = temporalDataSetImporter.importData(inputWindowSize, predictWindowSize);

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
        SortedMap<Date, double[]> inputsData = mapCsvImportExport.importData(new File(inputsFile.getPath()));
        Date randomKey = pickRandomKey(inputsData);
        inputsData.remove(randomKey);

        URL outputs = this.getClass().getResource("/trainingInputs.csv");
        SortedMap<Date, double[]> outputsData = mapCsvImportExport.importData(new File(outputs.getPath()));

        when(dataSourceMock.getInputEventsDescription()).thenReturn(Arrays.asList((new String[26])));
        when(dataSourceMock.geTrainingInputs()).thenReturn(inputsData);
        when(dataSourceMock.geTrainingOutputs()).thenReturn(outputsData);

        // Expect
        thrown.expect(RuntimeException.class);

        // When
        int inputWindowSize = 12;
        int predictWindowSize = 1;
        MLDataSet dataSet = temporalDataSetImporter.importData(inputWindowSize, predictWindowSize);

    }

    private Date pickRandomKey(SortedMap<Date, double[]> inputsData) {
        Random random = new Random();
        List<Date> keys = new ArrayList<Date>(inputsData.keySet());
        Date randomKey = keys.get(random.nextInt(keys.size()));
        return randomKey;
    }

    @Test(expected = RuntimeException.class)
    public void testImportDataCorruptedInputsNull() {

        // Given
        URL inputsFile = this.getClass().getResource("/idealOutputs.csv");
        SortedMap<Date, double[]> inputsData = mapCsvImportExport.importData(new File(inputsFile.getPath()));
        Date randomKey = pickRandomKey(inputsData);
        inputsData.put(randomKey, null);

        URL outputs = this.getClass().getResource("/trainingInputs.csv");
        SortedMap<Date, double[]> outputsData = mapCsvImportExport.importData(new File(outputs.getPath()));

        when(dataSourceMock.getInputEventsDescription()).thenReturn(Arrays.asList((new String[26])));
        when(dataSourceMock.geTrainingInputs()).thenReturn(inputsData);
        when(dataSourceMock.geTrainingOutputs()).thenReturn(outputsData);

        // When
        int inputWindowSize = 12;
        int predictWindowSize = 1;
        MLDataSet dataSet = temporalDataSetImporter.importData(inputWindowSize, predictWindowSize);

    }

    @Test(expected = RuntimeException.class)
    public void testImportDataCorruptedInputElement() {

        // Given
        URL inputsFile = this.getClass().getResource("/idealOutputs.csv");
        SortedMap<Date, double[]> inputsData = mapCsvImportExport.importData(new File(inputsFile.getPath()));
        Date randomKey = pickRandomKey(inputsData);
        inputsData.put(randomKey, new double[42]);

        URL outputs = this.getClass().getResource("/trainingInputs.csv");
        SortedMap<Date, double[]> outputsData = mapCsvImportExport.importData(new File(outputs.getPath()));

        when(dataSourceMock.getInputEventsDescription()).thenReturn(Arrays.asList((new String[26])));
        when(dataSourceMock.geTrainingInputs()).thenReturn(inputsData);
        when(dataSourceMock.geTrainingOutputs()).thenReturn(outputsData);

        // When
        int inputWindowSize = 12;
        int predictWindowSize = 1;
        MLDataSet dataSet = temporalDataSetImporter.importData(inputWindowSize, predictWindowSize);

    }

    @Test(expected = RuntimeException.class)
    public void testImportDataCorruptedDescription() {

        // Given
        URL inputsFile = this.getClass().getResource("/idealOutputs.csv");
        SortedMap<Date, double[]> inputsData = mapCsvImportExport.importData(new File(inputsFile.getPath()));

        URL outputs = this.getClass().getResource("/trainingInputs.csv");
        SortedMap<Date, double[]> outputsData = mapCsvImportExport.importData(new File(outputs.getPath()));

        List<String> inputEventsDescription = dataSourceMock.getInputEventsDescription();
        inputEventsDescription.remove(new Random().nextInt(inputEventsDescription.size()));

        when(inputEventsDescription).thenReturn(Arrays.asList((new String[26])));
        when(dataSourceMock.geTrainingInputs()).thenReturn(inputsData);
        when(dataSourceMock.geTrainingOutputs()).thenReturn(outputsData);

        // When
        int inputWindowSize = 12;
        int predictWindowSize = 1;
        MLDataSet dataSet = temporalDataSetImporter.importData(inputWindowSize, predictWindowSize);

    }

}

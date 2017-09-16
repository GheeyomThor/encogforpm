package com.finance.pm.encog.data.impl;

import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.finance.pm.encog.data.DataImporter;
import com.finance.pm.encog.util.DataSourceAdapter;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class TemporalDataSetImporterTest {

    @Inject
    private DataImporter temporalDataSetImporter;

    @Mock
    private DataSourceAdapter dataSourceAdapter;

    protected Injector injector = Guice.createInjector(new AbstractModule() {
        @Override
        protected void configure() {
            bind(DataSourceAdapter.class).toInstance(dataSourceAdapter);
        }
    });

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testImportData() {

        // Given
        when(dataSourceAdapter.getInputEventsDescription()).thenReturn(new ArrayList<>());

        // TODO temporalDataSetImporter.importData(0, 0);
    }

}

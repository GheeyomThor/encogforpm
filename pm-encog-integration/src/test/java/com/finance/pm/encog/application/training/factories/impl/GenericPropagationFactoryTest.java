package com.finance.pm.encog.application.training.factories.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.junit.Before;
import org.junit.Test;

import com.finance.pm.encog.application.nnetwork.factories.NnFactory;
import com.finance.pm.encog.data.impl.TemporalDataSetImporter;
import com.finance.pm.encog.guice.EncogServiceModule;
import com.finance.pm.encog.guice.POCAdapterModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class GenericPropagationFactoryTest {

    @Inject
    private GenericPropagationFactory genericPropagationFactory;
    @Inject
    private NnFactory nnFactory;
    @Inject
    private TemporalDataSetImporter temporalDataSetImporter;

    @Before
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new POCAdapterModule(), new EncogServiceModule());
        injector.injectMembers(this);
    }

    @Test
    public void test() {
        try {
            // Given
            MLMethod networkMethod = nnFactory.create("?:B->SIGMOID->25:B->SIGMOID->?", 26, 12);
            MLDataSet dataSet = temporalDataSetImporter.importData(12, 1);

            // When
            MLTrain networkTrain = genericPropagationFactory.create(networkMethod, dataSet, "LR=0.7", "MOM=0.3");

            // Then
            assertEquals(TrainingImplementationType.Iterative, networkTrain.getImplementationType());

        } catch (Exception e) {
            fail(String.format("Should not raise exception %s", e.getMessage()));
        }
    }

}

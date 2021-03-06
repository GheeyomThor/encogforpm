package com.finance.pm.encog.application.training.factories.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.train.MLTrain;
import org.junit.Before;
import org.junit.Test;

import com.finance.pm.encog.application.nnetwork.method.NnFactory;
import com.finance.pm.encog.application.nnetwork.propagation.PropagationFactory;
import com.finance.pm.encog.application.nnetwork.propagation.impl.GenericPropagationFactory;
import com.finance.pm.encog.data.impl.TemporalDataSetLoader;
import com.finance.pm.encog.guice.EncogServiceModule;
import com.finance.pm.encog.guice.POCAdapterModule;
import com.finance.pm.encog.guice.Training;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

public class PropagationFactoryTest {

    @Inject
    private PropagationFactory propagationFactory;
    @Inject
    private NnFactory nnFactory;
    @Inject
    @Training
    private TemporalDataSetLoader temporalDataSetImporter;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testInjectedRProp() {
        try {
            // Given
            Injector injector = Guice.createInjector(new POCAdapterModule(), new EncogServiceModule());
            injector.injectMembers(this);

            MLDataSet dataSet = temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous, 12, 1);
            MLMethod networkMethod = nnFactory.create("?:B->SIGMOID->25:B->SIGMOID->?", dataSet.getInputSize(),
                    dataSet.getIdealSize());

            // When
            MLTrain networkTrain = propagationFactory.create(networkMethod, dataSet, null);

            // Then
            assertEquals(TrainingImplementationType.Iterative, networkTrain.getImplementationType());

        } catch (Exception e) {
            fail(String.format("Should not raise exception %s", e.getMessage()));
        }
    }

    @Test
    public void testGenericBProp() {
        try {
            // Given
            Injector injector = Guice.createInjector(new POCAdapterModule(),
                    Modules.override(new EncogServiceModule()).with(new AbstractModule() {

                        @Override
                        protected void configure() {
                            bind(PropagationFactory.class).to(GenericPropagationFactory.class);

                        }

                    }));
            injector.injectMembers(this);

            MLDataSet dataSet = temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous, 12, 1);
            MLMethod networkMethod = nnFactory.create("?:B->SIGMOID->25:B->SIGMOID->?", dataSet.getInputSize(),
                    dataSet.getIdealSize());

            // When
            MLTrain networkTrain = propagationFactory.create(networkMethod, dataSet, MLTrainFactory.TYPE_BACKPROP,
                    "LR=0.7", "MOM=0.3");

            // Then
            assertEquals(TrainingImplementationType.Iterative, networkTrain.getImplementationType());

        } catch (Exception e) {
            fail(String.format("Should not raise exception %s", e.getMessage()));
        }
    }

    @Test(expected = RuntimeException.class)
    public void testGenericMissingArgument() throws Exception {

        // Given
        Injector injector = Guice.createInjector(new POCAdapterModule(),
                Modules.override(new EncogServiceModule()).with(new AbstractModule() {

                    @Override
                    protected void configure() {
                        bind(PropagationFactory.class).to(GenericPropagationFactory.class);

                    }

                }));
        injector.injectMembers(this);

        MLDataSet dataSet = temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous, 12, 1);
        MLMethod networkMethod = nnFactory.create("?:B->SIGMOID->25:B->SIGMOID->?", dataSet.getInputSize(),
                dataSet.getIdealSize());

        // When
        MLTrain networkTrain = propagationFactory.create(networkMethod, dataSet, null);

        // Then
        assertEquals(TrainingImplementationType.Iterative, networkTrain.getImplementationType());

    }

}

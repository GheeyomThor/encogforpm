package com.finance.pm.encog.application.nnetwork;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import javax.inject.Inject;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.junit.Before;
import org.junit.Test;

import com.finance.pm.encog.application.nnetwork.method.LayerDescription;
import com.finance.pm.encog.application.nnetwork.method.NnFactory;
import com.finance.pm.encog.application.nnetwork.method.impl.BasicNetworkFactory;
import com.finance.pm.encog.application.nnetwork.method.impl.GenericFeedForwardNetworkFactory;
import com.finance.pm.encog.application.nnetwork.propagation.impl.GenericPropagationFactory;
import com.finance.pm.encog.application.nnetwork.propagation.impl.ResilientPropagationFactory;
import com.finance.pm.encog.data.impl.TemporalDataSetLoader;
import com.finance.pm.encog.guice.EncogServiceModule;
import com.finance.pm.encog.guice.POCAdapterModule;
import com.finance.pm.encog.guice.Training;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class PropagationTrainingMethodBuilderTest {

    private PropagationTrainingBuilder propMLBuilder;
    
    @Inject
    @Training
    private TemporalDataSetLoader temporalDataSetImporter;

    
    @Before
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new POCAdapterModule(), new EncogServiceModule());
        injector.injectMembers(this);
        propMLBuilder = new PropagationTrainingBuilder();
    }
    
    
    @Test(expected=RuntimeException.class)
    public void testMissConfig() {
        
        //Given
        propMLBuilder.withArchitecture("?:B->SIGMOID->25:B->SIGMOID->?");
        MLDataSet mlDataSet = temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous, 12, 1);
        propMLBuilder.withDataSet(mlDataSet);
        propMLBuilder.withPropagationFactory(new GenericPropagationFactory());
        propMLBuilder.withPropagationType(MLTrainFactory.TYPE_BACKPROP);
        
        //When
        propMLBuilder.build();

    }
    
    @Test
    public void testGenericBasicNetworkFactory() {
        
        //Given
        MLDataSet mlDataSet = temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous, 12, 1);
        propMLBuilder.withDataSet(mlDataSet);
        
        LinkedList<LayerDescription> topology = new LinkedList<>();
        topology.add(new LayerDescription(mlDataSet.getInputSize(), ActivationSigmoid.class));
        topology.add(new LayerDescription(25, ActivationSigmoid.class));
        topology.add(new LayerDescription(mlDataSet.getIdealSize(), ActivationSigmoid.class));
        propMLBuilder.withArchitecture(topology);
        
        NnFactory mlMethod = new BasicNetworkFactory();
        propMLBuilder.withMethodFactory(mlMethod);
        propMLBuilder.withPropagationFactory(new GenericPropagationFactory());
        propMLBuilder.withPropagationType(MLTrainFactory.TYPE_BACKPROP);
        
        //When
        propMLBuilder.build();

    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void testGenericBasicNetworkFactoryUnsuportedArchitecture() {
        
        //Given
        propMLBuilder.withArchitecture("?:B->SIGMOID->25:B->SIGMOID->?");
        MLDataSet mlDataSet = temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous,12, 1);
        propMLBuilder.withDataSet(mlDataSet);
        NnFactory mlMethod = new BasicNetworkFactory();
        propMLBuilder.withMethodFactory(mlMethod);
        propMLBuilder.withPropagationFactory(new GenericPropagationFactory());
        propMLBuilder.withPropagationType(MLTrainFactory.TYPE_BACKPROP);
        
        //When
        propMLBuilder.build();

    }
    
    @Test
    public void testGenericFeedForwardNetworkFactory() {
        
        //Given
        propMLBuilder.withArchitecture("?:B->SIGMOID->25:B->SIGMOID->?");
        MLDataSet mlDataSet = temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous, 12, 1);
        propMLBuilder.withDataSet(mlDataSet);
        NnFactory mlMethod = new GenericFeedForwardNetworkFactory();
        propMLBuilder.withMethodFactory(mlMethod);
        propMLBuilder.withPropagationFactory(new GenericPropagationFactory());
        propMLBuilder.withPropagationType(MLTrainFactory.TYPE_BACKPROP);
        
        //When
        MLTrain mlTrain = propMLBuilder.build();
        
        //Then
        assertEquals(mlDataSet, mlTrain.getTraining());
        assertEquals(BasicNetwork.class, mlTrain.getMethod().getClass());
        
    }
    
    @Test(expected=RuntimeException.class)
    public void testGenericPropagationFactoryNoPropagationType() {
        
        //Given
        propMLBuilder.withArchitecture("");
        MLDataSet mlDataSet = temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous, 12, 1);
        propMLBuilder.withDataSet(mlDataSet);
        NnFactory mlMethod = new GenericFeedForwardNetworkFactory();
        propMLBuilder.withMethodFactory(mlMethod);
        propMLBuilder.withPropagationFactory(new GenericPropagationFactory());
        
        //When
        propMLBuilder.build();
        
    }
    
    @Test
    public void testResilientPropagationFactory() {
        
        //Given
        propMLBuilder.withArchitecture("?:B->SIGMOID->25:B->SIGMOID->?");
        MLDataSet mlDataSet = temporalDataSetImporter.loadData(ColumnType.continuous, ColumnType.continuous, 12, 1);
        propMLBuilder.withDataSet(mlDataSet);
        NnFactory mlMethod = new GenericFeedForwardNetworkFactory();
        propMLBuilder.withMethodFactory(mlMethod);
        propMLBuilder.withPropagationFactory(new ResilientPropagationFactory());
        
        //When
        MLTrain mlTrain = propMLBuilder.build();
        
        //Then
        assertEquals(mlDataSet, mlTrain.getTraining());
        assertEquals(BasicNetwork.class, mlTrain.getMethod().getClass());
        
    }
    
    @Test(expected=RuntimeException.class)
    public void testArchitectureMissconfiguration() {
        //When
        propMLBuilder.withArchitecture("?:B->SIGMOID->25:B->SIGMOID->?");
        propMLBuilder.withArchitecture(new LinkedList<LayerDescription>());
    }

}

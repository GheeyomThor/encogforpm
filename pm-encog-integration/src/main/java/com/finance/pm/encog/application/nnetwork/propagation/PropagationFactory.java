package com.finance.pm.encog.application.nnetwork.propagation;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.training.propagation.Propagation;

/**
 * A factory abstraction to create propagation training method
 *
 */
public interface PropagationFactory {

    /**
     * @param network Network method defining the topology of the neural network
     * @param dataSet Inputs and ideal data that will be used for training
     * @param propagationType will be propagation type as in {@link Propagation} and {@link MLTrainFactory} </br>
     * Propagation type can be optional when inferred by injection of the propagation implementation
     * @param args additional arguments related to the specific propagation each in the form : "name=value"
     */
    MLTrain create(MLMethod network, MLDataSet dataSet, String propagationType, String... args);

}

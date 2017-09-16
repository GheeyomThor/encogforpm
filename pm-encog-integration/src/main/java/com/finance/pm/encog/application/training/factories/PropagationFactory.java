package com.finance.pm.encog.application.training.factories;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;

/**
 * A factory abstraction to create propagation training method
 *
 */
public interface PropagationFactory {

    MLTrain create(MLMethod network, MLDataSet dataSet, String... args);

}

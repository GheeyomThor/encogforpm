package com.finance.pm.encog.application.training;

import java.io.File;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;

/**
 * Train a network using a data set.
 *
 */
public interface NnTrainer {

    File train(MLMethod network, MLDataSet trainingSet);

}

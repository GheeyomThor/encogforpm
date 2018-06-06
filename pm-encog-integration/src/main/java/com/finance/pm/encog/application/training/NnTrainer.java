package com.finance.pm.encog.application.training;

import java.io.File;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;

/**
 * Train a network using a data set.
 *
 */
public interface NnTrainer {

    File train(MLTrain mlTrain, MLDataSet trainingSet,
                String typeFeedforward, String modelArchitecture, String trainingType, String trainingArgs,
                String resultBaseFileName);

}

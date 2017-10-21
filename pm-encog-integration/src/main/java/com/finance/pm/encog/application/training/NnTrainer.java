package com.finance.pm.encog.application.training;

import java.io.File;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.train.MLTrain;

/**
 * Train a network using a data set.
 *
 */
public interface NnTrainer {

    default File train(MLTrain mlTrain, MLDataSet trainingSet, String resultBaseFileName) {
        return train(mlTrain, trainingSet, MLMethodFactory.TYPE_FEEDFORWARD, null, null, null, resultBaseFileName);
    };

    File train(MLTrain mlTrain, MLDataSet trainingSet,
                String typeFeedforward, String modelArchitecture, String trainingType, String trainingArgs,
                String resultBaseFileName);

}

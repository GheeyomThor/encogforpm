package com.finance.pm.encog.application.training.impl;

import java.io.File;
import java.util.UUID;

import javax.inject.Inject;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.persist.EncogDirectoryPersistence;

import com.finance.pm.encog.application.training.NnTrainer;
import com.finance.pm.encog.application.training.factories.PropagationFactory;

/**
 * Train and save the network on the file system for later reuse. The
 * propagation method is parameterised by injection
 */
public class PropagationTrainer implements NnTrainer {

    private static final int MAX_ITERATIONS = Integer.MAX_VALUE;

    private PropagationFactory propagationTrainerFactory;

    @Inject
    private PropagationTrainer(PropagationFactory propagationTrainerFactory) {
        super();
        this.propagationTrainerFactory = propagationTrainerFactory;
    }

    public File train(MLMethod network, MLDataSet trainingSet) {

        //Propagation type is not necessary here as it is inferred by injection of the propagation implementation
        MLTrain mlTrain = propagationTrainerFactory.create(network, trainingSet, null);

        int epoch = 1;
        do {
            mlTrain.iteration();
            epoch++;
        } while (mlTrain.getError() > 0.01 && epoch < MAX_ITERATIONS);

        File file = new File(System.getProperty("user.dir") + File.separator + UUID.randomUUID() + ".EG");
        EncogDirectoryPersistence.saveObject(file, network);

        return file;

    }

}

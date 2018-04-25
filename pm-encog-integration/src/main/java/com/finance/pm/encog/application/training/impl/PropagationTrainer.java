package com.finance.pm.encog.application.training.impl;

import java.io.File;

import org.apache.log4j.Logger;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.persist.EncogDirectoryPersistence;

import com.finance.pm.encog.application.training.NnTrainer;

/**
 * Train and save the network on the file system for later reuse. The
 * propagation method is parameterized by injection
 */
public class PropagationTrainer implements NnTrainer {
	
	private static Logger LOGGER = Logger.getLogger(PropagationTrainer.class);

	private static final int MAX_ITERATIONS = 1000;

	@Override
	public File train(MLTrain mlTrain, MLDataSet trainingSet,
			String typeFeedforward, String modelArchitecture, String trainingType, String trainingArgs,
			String resultBaseFileName) {

		int epoch = 1;
		do {
			mlTrain.iteration();
			LOGGER.info("Iteration #"+epoch+", Training Error: "+mlTrain.getError());
			epoch++;
		} while (mlTrain.getError() > 0.01 && epoch < MAX_ITERATIONS);

		File file = new File(System.getProperty("installdir") + File.separator + "neural" + File.separator + resultBaseFileName + ".EG");
		EncogDirectoryPersistence.saveObject(file, mlTrain.getMethod());

		return file;

	}

}

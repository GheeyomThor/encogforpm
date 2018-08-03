package com.finance.pm.encog.application.training.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.persist.EncogDirectoryPersistence;

import com.finance.pm.encog.application.training.NnTrainer;
import com.finance.pm.encog.util.LogStatusReportable;

/**
 * Train and save the network on the file system for later reuse. The
 * propagation method is parameterized by injection
 */
public class PropagationTrainer implements NnTrainer {

	private static Logger LOGGER = Logger.getLogger(PropagationTrainer.class);

	private static final int MAX_ITERATIONS = 1000;
	private static final double THRESHOLD = 0.001;

	@Override
	public File train(MLTrain mlTrain, MLDataSet trainingSet,
			String modelMethod, String modelArchitecture, String trainingType, String trainingArgs,
			String resultBaseFileName) {

		Map<String, String> trainingArgsMap = parseTrainingArgs(trainingArgs);

		try (LogStatusReportable report = new LogStatusReportable(resultBaseFileName)) {

			int maxIterations = (trainingArgsMap.containsKey("maxIterations"))?Integer.valueOf(trainingArgsMap.get("maxIterations")):MAX_ITERATIONS;

			int epoch = 1;
			do {
				mlTrain.iteration();
				report.print("Iteration #"+epoch+", Training Error: "+mlTrain.getError());
				epoch++;
			} while (mlTrain.getError() > THRESHOLD && epoch < maxIterations);

			report.finalModel(mlTrain.getMethod());
			report.print("Training input length (nb rows) : " + trainingSet.size() + ".Input width : " + trainingSet.getInputSize() + ", ideal width : " + trainingSet.getIdealSize());

			File file = new File(System.getProperty("installdir") + File.separator + "neural" + File.separator + resultBaseFileName + ".EG");
			EncogDirectoryPersistence.saveObject(file, mlTrain.getMethod());

			return file;

		} catch (Exception e) {
			LOGGER.error(e);
			throw new RuntimeException(e);
		}

	}

	private Map<String, String> parseTrainingArgs(String trainingArgs) {
		return Arrays.stream(trainingArgs.split(",")).collect(Collectors.toMap(p -> p.split("=")[0].trim(), p -> p.split("=")[1].trim()));
	}

}

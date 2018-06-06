package com.finance.pm.encog.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.encog.Encog;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.train.MLTrain;

import com.finance.pm.encog.application.nnetwork.PropagationTrainingBuilder;
import com.finance.pm.encog.application.nnetwork.method.NnFactory;
import com.finance.pm.encog.application.nnetwork.propagation.PropagationFactory;
import com.finance.pm.encog.application.prediction.NnPredictor;
import com.finance.pm.encog.application.training.NnTrainer;
import com.finance.pm.encog.data.DataSetLoader;
import com.finance.pm.encog.guice.Temporal;
import com.finance.pm.encog.guice.Training;
import com.finance.pm.encog.guice.Validation;
import com.finance.pm.encog.util.CsvImportExport;
import com.google.inject.name.Named;

/**
 * Service to train and run predictions
 */
public class EncogService {

	private static Logger LOGGER = Logger.getLogger(EncogService.class);

	private DataSetLoader oneFoldTemporalDataLoader;

	private DataSetLoader versatileTrainingDataLoader;

	private NnFactory ofNetworkFactory;
	private PropagationFactory ofPropagationFactory;
	private NnTrainer oneFoldTrainer;
	private NnTrainer crossValidationTrainer;

	private NnPredictor oneFoldPredictor;
	private NnPredictor crossValidationPredictor;

	private CsvImportExport<Integer> normalizedExporter;

	@Inject
	public EncogService( 
			NnFactory networkFactory, PropagationFactory propagationFactory, 
			@Temporal DataSetLoader oneFoldTemporalDataLoader,
			@Named("temporal") NnTrainer oneFoldTrainer,
			@Named("temporal") NnPredictor oneFoldPredictor,

			@Training DataSetLoader versatileTrainingDataLoader,
			@Named("versatile") NnTrainer crossValidationTrainer,
			@Validation NnPredictor crossValidationPredictor,

			CsvImportExport<Integer> normalizedExporter) {

		super();
		this.ofNetworkFactory = networkFactory;
		this.ofPropagationFactory = propagationFactory;
		this.oneFoldTemporalDataLoader = oneFoldTemporalDataLoader;
		this.oneFoldTrainer = oneFoldTrainer;
		this.oneFoldPredictor = oneFoldPredictor;

		this.versatileTrainingDataLoader = versatileTrainingDataLoader;
		this.crossValidationTrainer = crossValidationTrainer;
		this.crossValidationPredictor = crossValidationPredictor;

		this.normalizedExporter = normalizedExporter;

	}

	/**
	 * A one of train and predict computation. Mainly for proof of concept
	 * 
	 * @param architecture
	 *            For example "? : B−>SIGMOID−>4:B−>SIGMOID−>?" will create a
	 *            neural network.</br>
	 *            see
	 *            {@link com.finance.pm.encog.application.nnetwork.method.impl.GenericFeedForwardNetworkFactory}
	 * @param inputSize
	 *            input layer size. The output layer size is fixed to one.
	 * @param lagWindowSize
	 *            see {@link DataSetLoader}
	 * @return The predicted output
	 * @throws Exception
	 */
	public List<double[]> oneFoldTrainAndCompute(InputOutputDescription iODescription, NetworkDescription networkDescription, String resultsBaseFileName)
			throws Exception {

		synchronized(EncogService.class) {
			LOGGER.info("Importing data");
			MLDataSet trainingSet = oneFoldTemporalDataLoader.loadData(iODescription.getInputType(), iODescription.getOutputType(), iODescription.getLagWindowSize(), iODescription.getLeadWindowSize());

			LOGGER.info("Creating network method and training");
			//TODO remove guice injection and use MLTrainFactory.create instead to pickup the correct method and propagation
			MLTrain mlTrain = new PropagationTrainingBuilder()
					.withMethodFactory(ofNetworkFactory).withArchitecture(networkDescription.getModelArchitecture())
					.withDataSet(trainingSet).withPropagationFactory(ofPropagationFactory)
					.build();

			LOGGER.info("Training network");
			File trainedEg = oneFoldTrainer.train(mlTrain, trainingSet, null, null, null, networkDescription.getTrainingArgs(), resultsBaseFileName);

			LOGGER.info("Running predictions");
			List<double[]> prediction = oneFoldPredictor.compute(trainedEg, trainingSet);

			LOGGER.info("Encog. One fold Training done.");
			Encog.getInstance().shutdown();

			return prediction;
		}

	}

	public List<double[]> crossValidationAndCompute(
			InputOutputDescription iODescr, NetworkDescription netDescr,
			String resultsBaseFileName) throws Exception {

		synchronized(EncogService.class) {
			//if (iODescr.getLeadWindowSize() > 1) throw new OperationNotSupportedException();

			LOGGER.info("Importing Training data");
			VersatileMLDataSet trainingSet = 
					(VersatileMLDataSet) versatileTrainingDataLoader
					.loadData(iODescr.getInputType(), iODescr.getOutputType(), iODescr.getLagWindowSize(), iODescr.getLeadWindowSize(), netDescr.getMethodType(), netDescr.getModelArchitecture());

			LOGGER.info("Training network using cross validation and find the best method");
			File trainedEg = crossValidationTrainer.train(null, trainingSet, netDescr.getMethodType(), netDescr.getModelArchitecture(), netDescr.getTrainingType(), netDescr.getTrainingArgs(), resultsBaseFileName);

			LOGGER.info("Running predictions");
			List<double[]> prediction = crossValidationPredictor.versatileDataSetCompute(trainedEg, trainingSet.getNormHelper(), iODescr.getLagWindowSize());

			LOGGER.info("Encog "+resultsBaseFileName+". Cross Validation Training done.");
			Encog.getInstance().shutdown();

			if (LOGGER.isDebugEnabled()) exportNormalysed(trainingSet, resultsBaseFileName);

			return prediction;
		}

	}

	private void exportNormalysed(MLDataSet data, String baseFileName) {

		LinkedHashMap<Integer, double[]> analysedInputs = new LinkedHashMap<>();
		LinkedHashMap<Integer, double[]> analysedOutputs = new LinkedHashMap<>();
		int i = 0;
		for (MLDataPair pair : data) {
			analysedInputs.put(i++, pair.getInputArray());
			analysedOutputs.put(i, pair.getIdealArray());
		}
		normalizedExporter.exportData(baseFileName, "trainingInputs_EncogNormalised", analysedInputs);
		normalizedExporter.exportData(baseFileName, "trainingOutputs_EncogNormalised", analysedOutputs);

	}


	public List<double[]> trainForNewOnlyAndcompute(InputOutputDescription iODescription, NetworkDescription networkDescription, String resultsBaseFileName) throws Exception {

		synchronized(EncogService.class) {
			List<double[]> prediction;

			String egPath = System.getProperty("installdir") + File.separator + "neural" + File.separator + resultsBaseFileName+".EG";
			File trainedEg = new File(egPath);
			if (trainedEg.exists()) {

				LOGGER.info("File "+trainedEg.getAbsolutePath()+" was found on the file system : reusing");

				String normPath = System.getProperty("installdir") + File.separator + "neural" + File.separator + resultsBaseFileName+".Norm";
				try (FileInputStream fis = new FileInputStream(normPath); ObjectInputStream objectInputStream = new ObjectInputStream(fis)) {

					NormalizationHelper normHelper = (NormalizationHelper) objectInputStream.readObject();

					LOGGER.info("Running predictions");
					prediction = crossValidationPredictor.versatileDataSetCompute(trainedEg, normHelper, iODescription.getLagWindowSize());

				} catch (Exception e) {
					throw new RuntimeException(e);
				}

			} else {

				LOGGER.info("File "+trainedEg.getAbsolutePath()+" was NOT found on the file system : retraining");
				prediction = crossValidationAndCompute(iODescription, networkDescription, resultsBaseFileName);

			}

			LOGGER.info("Encog "+resultsBaseFileName+". Prediction computation done.");
			try {
				Encog.getInstance().shutdown();
			} catch (Exception e) {
				LOGGER.warn("Encog service did an improper shutdown", e);
			}

			return prediction;
		}

	}

}

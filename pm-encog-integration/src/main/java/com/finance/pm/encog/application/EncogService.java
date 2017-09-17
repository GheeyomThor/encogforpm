package com.finance.pm.encog.application;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.encog.Encog;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;

import com.finance.pm.encog.application.nnetwork.PropagationTrainingMethodBuilder;
import com.finance.pm.encog.application.nnetwork.propagation.PropagationFactory;
import com.finance.pm.encog.application.nnetwork.topology.NnFactory;
import com.finance.pm.encog.application.prediction.NnPredictor;
import com.finance.pm.encog.application.training.NnTrainer;
import com.finance.pm.encog.data.DataImporter;

/**
 * Service to train and run predictions
 */
public class EncogService {

    private static Logger LOGGER = Logger.getLogger(EncogService.class.getName());

    private DataImporter dataImporter;
    private NnFactory networkFactory;
    private PropagationFactory propagationFactory;
    private NnTrainer trainer;
    private NnPredictor predictor;

    @Inject
    public EncogService(DataImporter dataImporter, NnFactory networkFactory, PropagationFactory propagationFactory,
            NnTrainer trainer, NnPredictor predicator) {
        super();
        this.dataImporter = dataImporter;
        this.networkFactory = networkFactory;
        this.propagationFactory = propagationFactory;
        this.trainer = trainer;
        this.predictor = predicator;
    }

    /**
     * A one of train and predict computation. Mainly for proof of concept
     * 
     * @param architecture
     *            For example "? : B−>SIGMOID−>4:B−>SIGMOID−>?" will create a
     *            neural network.</br>
     *            see
     *            {@link com.finance.pm.encog.application.nnetwork.topology.impl.GenericFeedForwardNetworkFactory}
     * @param inputSize
     *            input layer size. The output layer size is fixed to one.
     * @param lagWindowSize
     *            see {@link DataImporter}
     * @return The predicted output
     * @throws Exception
     */
    public LinkedHashMap<MLDataPair, double[]> oneFoldTrainAndCompute(String architecture, int lagWindowSize)
            throws Exception {

        LOGGER.info("Importing data");
        MLDataSet trainingSet = dataImporter.importData(lagWindowSize, 1);

        LOGGER.info("Creating network method and training");
        MLTrain mlTrain = new PropagationTrainingMethodBuilder().withMethodFactory(networkFactory)
                .withArchitecture(architecture).withDataSet(trainingSet).withPropagationFactory(propagationFactory)
                .build();

        LOGGER.info("Training network");
        File trainedEg = trainer.train(mlTrain, trainingSet);

        LOGGER.info("Running predictions");
        LinkedHashMap<MLDataPair, double[]> prediction = predictor.compute(trainedEg, trainingSet);

        LOGGER.info("All done.");
        Encog.getInstance().shutdown();

        return prediction;

    }

}

package com.finance.pm.encog.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.encog.Encog;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnType;
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
import com.finance.pm.encog.util.EGFileReferenceManager;
import com.google.inject.name.Named;

/**
 * Service to train and run predictions
 */
public class EncogService {

    private static Logger LOGGER = Logger.getLogger(EncogService.class);

    private DataSetLoader temporalDataLoader;

    private DataSetLoader versatileTrainingDataLoader;

    private NnFactory networkFactory;
    private PropagationFactory propagationFactory;
    private NnTrainer trainer;
    private NnTrainer crossValidationTrainer;

    private NnPredictor predictor;
    private NnPredictor crossValidationPredictor;

    private EGFileReferenceManager egFileReferenceManager;
    private CsvImportExport<Integer> normalizedExporter;

    @Inject
    public EncogService( 
            NnFactory networkFactory, PropagationFactory propagationFactory, 
            @Temporal DataSetLoader temporalDataImporter,
            @Named("temporal") NnTrainer trainer,
            @Named("temporal") NnPredictor predictor,
            @Training DataSetLoader versatileTrainingDataLoader,
            @Named("versatile") NnTrainer crossValidationTrainer,
            @Validation NnPredictor crossValidationPredictor,
            EGFileReferenceManager egFileReferenceManager, CsvImportExport<Integer> normalizedExporter) {
        super();
        this.temporalDataLoader = temporalDataImporter;
        this.networkFactory = networkFactory;
        this.propagationFactory = propagationFactory;
        this.trainer = trainer;
        this.predictor = predictor;

        this.versatileTrainingDataLoader = versatileTrainingDataLoader;
        this.crossValidationTrainer = crossValidationTrainer;
        this.crossValidationPredictor = crossValidationPredictor;

        this.egFileReferenceManager = egFileReferenceManager;
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
    public LinkedHashMap<Integer, double[]> oneFoldTrainAndCompute(String architecture, int lagWindowSize)
            throws Exception {

        LOGGER.info("Importing data");
        MLDataSet trainingSet = temporalDataLoader.loadData(ColumnType.continuous, ColumnType.nominal, lagWindowSize,
                1);

        LOGGER.info("Creating network method and training");
        MLTrain mlTrain = new PropagationTrainingBuilder().withMethodFactory(networkFactory)
                .withArchitecture(architecture).withDataSet(trainingSet).withPropagationFactory(propagationFactory)
                .build();

        LOGGER.info("Training network");
        File trainedEg = trainer.train(mlTrain, trainingSet, CsvImportExport.runStamp.toString());

        LOGGER.info("Running predictions");
        LinkedHashMap<Integer, double[]> prediction = predictor.compute(trainedEg, trainingSet);

        LOGGER.info("Encog. One fold Training done.");
        Encog.getInstance().shutdown();

        return prediction;

    }

    public List<double[]> crossValidationAndCompute(
            InputOutputDescription iODescr, NetworkDescription netDescr,
            String resultsBaseFileName) throws Exception {

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

        if (LOGGER.isDebugEnabled()) exportNormalysed(trainingSet, iODescr, netDescr);

        return prediction;

    }

    private void exportNormalysed(VersatileMLDataSet data, InputOutputDescription iODescr, NetworkDescription netDescr ) {

        LinkedHashMap<Integer, double[]> analysedInputs = new LinkedHashMap<>();
        LinkedHashMap<Integer, double[]> analysedOutputs = new LinkedHashMap<>();
        //Iterator<MLDataPair> dataIterator = data.iterator();
        int i = 0;
        for (MLDataPair pair : data) {
            //double[] in = pair.getInputArray();
            //double[] out = pair.getIdealArray();
            //MLDataPair nextMlDataPair = dataIterator.next();
            analysedInputs.put(i++, pair.getInputArray());
            analysedOutputs.put(i, pair.getIdealArray());
        }
        normalizedExporter.exportData(Optional.of(iODescr), Optional.of(netDescr), "trainingInputs_EncogNormalised", analysedInputs);
        normalizedExporter.exportData(Optional.of(iODescr), Optional.of(netDescr), "trainingOutputs_EncogNormalised", analysedOutputs);

    }


    public List<double[]> trainForNewOnlyAndcompute(InputOutputDescription iODescription, NetworkDescription networkDescription) throws Exception {

        List<double[]> prediction;

        String[] egFileDescr = egFileReferenceManager.encogFileNameGenerator(Optional.of(iODescription), Optional.of(networkDescription));
        LOGGER.info("Encog file description : "+egFileDescr[1]);
        LOGGER.info("Encog file to be used : "+egFileDescr[0]);
        File trainedEg = new File(System.getProperty("installdir") + File.separator + egFileDescr[0]+".EG");
        if (trainedEg.exists()) {

            LOGGER.info("File "+trainedEg.getAbsolutePath()+" was found on the file system : re using");

            try (FileInputStream fis = new FileInputStream(egFileDescr[0]+".Norm"); ObjectInputStream objectInputStream = new ObjectInputStream(fis)) {

                NormalizationHelper normHelper = (NormalizationHelper) objectInputStream.readObject();

                LOGGER.info("Running predictions");
                prediction = crossValidationPredictor.versatileDataSetCompute(trainedEg, normHelper, iODescription.getLagWindowSize());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {

            LOGGER.info("File "+trainedEg.getAbsolutePath()+" was NOT found on the file system : retraining");
            prediction = crossValidationAndCompute(iODescription, networkDescription, egFileDescr[0]);

        }

        LOGGER.info("Encog "+egFileDescr[0]+". Prediction computation done.");
        try {
            Encog.getInstance().shutdown();
        } catch (Exception e) {
            LOGGER.warn("Encog service did an improper shutdown", e);
        }

        return prediction;

    }

}

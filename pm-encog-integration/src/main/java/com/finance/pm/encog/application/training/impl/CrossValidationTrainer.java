package com.finance.pm.encog.application.training.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.model.EncogModel;
import org.encog.ml.model.normalisation.NormalisationStrategySetter;
import org.encog.ml.model.training.TrainingSpecification;
import org.encog.ml.model.training.TrainingSpecificationBuilder;
import org.encog.ml.train.MLTrain;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;

import com.finance.pm.encog.application.training.NnTrainer;
import com.finance.pm.encog.util.LogStatusReportable;

//TODO add flexibility for the choice or TrainingType and normalisation
public class CrossValidationTrainer implements NnTrainer {

    private static Logger LOGGER = Logger.getLogger(CrossValidationTrainer.class);

    @Override
    public File train(MLTrain mlTrain, MLDataSet trainingSet, 
            String methodType, String modelArchitecture, String trainingType, String trainingArgs, 
            String resultBaseFileName) {

        VersatileMLDataSet dataSet = (VersatileMLDataSet) trainingSet;

        EncogModel model = new EncogModel(dataSet);

        try (LogStatusReportable report = new LogStatusReportable(resultBaseFileName)) {

            // Send any output to the console.
            model.setReport(report);

            // Hold back some data for a final validation.
            // Shuffle the data into a random ordering.
            // Use a seed of 1001 so that we always use the same holdback and will
            // get more consistent results.
            model.holdBackValidation(0.3, false, 1001);

            TrainingSpecificationBuilder methodSpecificationBuilder = new TrainingSpecificationBuilder()
                    .withMethod((methodType)).withArchitecture(modelArchitecture)
                    .withTrainingType(trainingType).witTrainingArgs(trainingArgs);

            TrainingSpecification trainingSpec = methodSpecificationBuilder.build(dataSet);

            //FIXME Quirk : this selecMethod ignores training type and arguments ... hence needs fixing in Encog
            model.selectMethod(dataSet, methodType, trainingSpec.getModelArchitecture(), trainingSpec.getTrainingType(), trainingSpec.getTrainingArgs());
            model.selectTraining(dataSet, trainingSpec.getTrainingType(), trainingSpec.getTrainingArgs());

            //FIXME Quirk : the strategy is reset in model.selectMethod ... hence needs fixing in Encog
            //dataSet.getNormHelper().setStrategy(configurationM.suggestNormalizationStrategy(dataSet, suggestModelArchitecture));
            NormalisationStrategySetter normalisationStrategySetter = 
                    new NormalisationStrategySetter().withMethodSpecification(trainingSpec.getMethodSpecification());
            normalisationStrategySetter.setStrategyTo(dataSet);

            // Use a 5-fold cross-validated train. Return the best method found.
            MLRegression bestMethod = (MLRegression) model.crossvalidate(5, false);

            // Display the training and validation errors.
            report.regressionError(EncogUtility.calculateRegressionError(bestMethod, model.getTrainingDataset()), EncogUtility.calculateRegressionError(bestMethod, model.getValidationDataset()));

            //Display Helper
            report.helper(dataSet.getNormHelper().toString());

            // Display the final model.
            report.finalModel(bestMethod);

            String egPathName = System.getProperty("installdir") + File.separator + resultBaseFileName + ".EG";
            LOGGER.info("Saving training network in " + egPathName);
            File egFile = new File(egPathName);
            try {
                EncogDirectoryPersistence.saveObject(egFile, bestMethod);
            } catch (Exception e1) {
                LOGGER.error("Could not save the network serialisation " + egPathName, e1);
                throw new RuntimeException(e1);
            }

            String normPathName = System.getProperty("installdir") + File.separator + resultBaseFileName + ".Norm";
            LOGGER.info("Saving training normaliser in " + normPathName);
            File normFile = new File(normPathName);
            try (FileOutputStream fos = new FileOutputStream(normFile); ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos)) {
                objectOutputStream.writeObject(dataSet.getNormHelper());
            } catch (Exception e) {
                LOGGER.error("Could not save the normaliser serialisation " + normPathName, e);
                throw new RuntimeException(e);
            }

            return egFile;

        } catch (IOException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }

    }

}

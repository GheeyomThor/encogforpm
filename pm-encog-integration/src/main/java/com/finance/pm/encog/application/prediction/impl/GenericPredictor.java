package com.finance.pm.encog.application.prediction.impl;

import java.io.File;
import java.util.LinkedHashMap;

import org.encog.ml.MLClassification;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.persist.EncogDirectoryPersistence;

import com.finance.pm.encog.application.prediction.NnPredictor;

/**
 * Generic predictor
 *
 */
public class GenericPredictor implements NnPredictor {

    public LinkedHashMap<MLDataPair, double[]> compute(File networkFile, MLDataSet trainingSet) {

        MLRegression network = (MLRegression) EncogDirectoryPersistence.loadObject(networkFile);
        LinkedHashMap<MLDataPair, double[]> outputs = new LinkedHashMap<>();
        for (MLDataPair pair : trainingSet) {
            MLData output = network.compute(pair.getInput());
            outputs.put(pair, output.getData());
        }

        return outputs;

    }

    @Override
    public LinkedHashMap<MLDataPair, double[]> classifiy(File networkFile, MLDataSet trainingSet) {

        MLClassification network = (MLClassification) EncogDirectoryPersistence.loadObject(networkFile);
        LinkedHashMap<MLDataPair, double[]> outputs = new LinkedHashMap<>();
        for (MLDataPair pair : trainingSet) {
            int output = network.classify(pair.getInput());
            outputs.put(pair, new double[] { output });
        }

        return outputs;
    }

}

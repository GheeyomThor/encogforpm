package com.finance.pm.encog.application.prediction.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.encog.ml.MLClassification;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.persist.EncogDirectoryPersistence;

import com.finance.pm.encog.application.prediction.NnPredictor;
import com.finance.pm.encog.util.DataSourceAdapter;

/**
 * Generic predictor
 *
 */
public class GenericPredictor implements NnPredictor {

    public List<double[]> compute(File networkFile, MLDataSet trainingSet) {

        MLRegression network = (MLRegression) EncogDirectoryPersistence.loadObject(networkFile);
        List<double[]> outputs = new ArrayList<>();
        for (MLDataPair pair : trainingSet) {
            MLData output = network.compute(pair.getInput());
            outputs.add(output.getData());
        }
        return outputs;
    }

    @Override
    public List<double[]> versatileDataSetPredict(File trainedEg, NormalizationHelper normHelper, int lagWindowSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LinkedHashMap<MLDataPair, double[]> classify(File networkFile, MLDataSet trainingSet) {

        MLClassification network = (MLClassification) EncogDirectoryPersistence.loadObject(networkFile);
        LinkedHashMap<MLDataPair, double[]> outputs = new LinkedHashMap<>();
        for (MLDataPair pair : trainingSet) {
            int output = network.classify(pair.getInput());
            outputs.put(pair, new double[] { output });
        }

        return outputs;
    }

    @Override
    public DataSourceAdapter getDataSourceAdapter() {
       throw new UnsupportedOperationException();
    }


}

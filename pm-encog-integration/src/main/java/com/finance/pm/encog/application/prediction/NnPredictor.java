package com.finance.pm.encog.application.prediction;

import java.io.File;
import java.util.LinkedHashMap;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

/**
 * To run prediction on a existing trained network. Output can be either for
 * classification or regression.
 *
 */
public interface NnPredictor {

    /**
     * Regression prediction
     * 
     * @param trainedEg
     *            File containing a previously trained network
     * @param trainingSet
     *            Input data
     * @return predicted output data
     */
    LinkedHashMap<MLDataPair, double[]> compute(File trainedEg, MLDataSet trainingSet);

    /**
     * Classification prediction
     * 
     * @param trainedEg
     *            File containing a previously trained network
     * @param trainingSet
     *            Input data
     * @return predicted output data
     */
    LinkedHashMap<MLDataPair, double[]> classifiy(File trainedEg, MLDataSet trainingSet);

}

package com.finance.pm.encog.application.prediction;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.versatile.NormalizationHelper;

import com.finance.pm.encog.util.DataSourceAdapter;

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
	List<double[]> compute(File trainedEg, MLDataSet trainingSet);
    
    List<double[]> versatileDataSetPredict(File trainedEg, NormalizationHelper normHelper, int lagWindowSize);

    /**
     * Classification prediction
     * 
     * @param trainedEg
     *            File containing a previously trained network
     * @param trainingSet
     *            Input data
     * @return predicted output data
     */
    LinkedHashMap<MLDataPair, double[]> classify(File trainedEg, MLDataSet trainingSet);

    DataSourceAdapter getDataSourceAdapter();



}

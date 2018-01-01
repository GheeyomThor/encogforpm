package com.finance.pm.encog.application.prediction.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.VectorWindow;

import com.finance.pm.encog.application.prediction.NnPredictor;
import com.finance.pm.encog.data.impl.VersatileMapDataSource;
import com.finance.pm.encog.util.DataSourceAdapter;
import com.google.inject.Inject;

public class CrossValidationPredictor implements NnPredictor {

    //TODO :? pass this as a parameter of the compute : ?issue with Guice injection?
    @Inject
    DataSourceAdapter pmDataAdapter;


    @Override
    public List<double[]> versatileDataSetCompute(File trainedEg, NormalizationHelper normHelper, int lagWindowSize) {

        MLRegression network = (MLRegression) EncogDirectoryPersistence.loadObject(trainedEg);

        List<double[]> validationInputValues = pmDataAdapter.getTrainingInputs();

        VersatileDataSource source = new VersatileMapDataSource(validationInputValues);

        List<double[]> result = new ArrayList<>();

        MLData input = normHelper.allocateInputVector(lagWindowSize + 1);
        double[] slice = new double[normHelper.calculateNormalizedInputCount()];
        VectorWindow window = new VectorWindow(lagWindowSize + 1);
        String[] line;

        source.rewind();
        while((line = source.readLine()) != null) {

            normHelper.normalizeInputVector(line, slice, true);
            window.add(slice);

            if (window.isReady()) {
                window.copyWindow(input.getData(), 0);
                MLData output = network.compute(input);

                String[] predictedStr = normHelper.denormalizeOutputVectorToString(output);
                //System.out.println(output);
                double[] predicted =  Arrays.stream(predictedStr).mapToDouble(Double::parseDouble).toArray();
                result.add(predicted);
            }

        }

        return result;
    }

    @Override
    public LinkedHashMap<Integer, double[]> compute(File trainedEg,  MLDataSet normTrainingSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LinkedHashMap<MLDataPair, double[]> classify(File trainedEg, MLDataSet trainingSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataSourceAdapter getDataSourceAdapter() {
        return pmDataAdapter;
    }


}

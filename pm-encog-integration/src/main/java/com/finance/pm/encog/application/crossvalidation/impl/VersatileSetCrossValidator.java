package com.finance.pm.encog.application.crossvalidation.impl;

import org.encog.ml.MLMethod;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;

import com.finance.pm.encog.application.crossvalidation.CrossValidator;

/**
 * Specific cross validation.</br>
 * Before we fit the model we hold back part of the data for a validation
 * set.</br>
 * We choose to hold back 30%. We chose to randomise the data set with a fixed
 * seed value.</br>
 * This fixed seed ensures that we get the same training and validation sets
 * each time.</br>
 * This is a matter of preference. If you want a random sample each time then
 * pass in the current time for the seed.</br>
 * We also establish the lead and lag window sizes. Finally, we fit the model
 * with a k-fold cross-validation of size 5.
 */
@Deprecated
public class VersatileSetCrossValidator implements CrossValidator {

    @Override
    public MLMethod nnMethodSelectionByCrossValidation(VersatileMLDataSet dataSet, int lagWindowSize) {

        
        dataSet.setLeadWindowSize(1);
        dataSet.setLagWindowSize(lagWindowSize);

        EncogModel model = new EncogModel(dataSet);
        
        //Data partition
        model.holdBackValidation(.3, false, 1001);
        
        ///Method Type and Training Type creation
        //see also public void selectMethod(VersatileMLDataSet dataset, String methodType, String methodArgs, String trainingType, String trainingArgs)
        //For FEEDFORWARD : default Activation is TANH and default trainingType is RPROP @see FeedforwardConfig
        model.selectMethod(dataSet, MLMethodFactory.TYPE_FEEDFORWARD);
        model.selectTrainingType(dataSet);

        MLMethod bestMethod = model.crossvalidate(5, false);

        return bestMethod;

    }

}

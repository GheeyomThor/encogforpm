package com.finance.pm.encog.application.crossvalidation.impl;

import org.encog.ml.MLMethod;
import org.encog.ml.data.versatile.VersatileMLDataSet;
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
public class VersatileSetCrossValidator implements CrossValidator {

    @Override
    public MLMethod crossValidate(EncogModel model, VersatileMLDataSet dataSet, int lagWindowSize) {

        dataSet.setLeadWindowSize(1);
        dataSet.setLagWindowSize(lagWindowSize);

        model.holdBackValidation(.3, false, 1001);
        model.selectTrainingType(dataSet);

        MLMethod method = model.crossvalidate(5, false);

        return method;

    }

}

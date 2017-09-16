package com.finance.pm.encog.application.crossvalidation;

import org.encog.ml.MLMethod;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.model.EncogModel;

/**
 * Cross validation utility. As a constraint from Encog only
 * {@link VersatileMLDataSet} is supported.
 *
 */
public interface CrossValidator {
    /**
     * 
     * @param model
     *            Initial network
     * @param dataSet
     *            Inputs and ideals for training. Only a
     *            {@link VersatileMLDataSet} is supported and some additional
     *            work is need to for integration
     * @param lagWindowSize
     *            Specifies the number of rows to be processed in the input as
     *            part of the input vector </br>
     *            This is useful for time related inputs.
     * @return Trained network
     */
    MLMethod crossValidate(EncogModel model, VersatileMLDataSet dataSet, int lagWindowSize);

}

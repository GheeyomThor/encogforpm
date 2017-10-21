package com.finance.pm.encog.application.crossvalidation;

import org.encog.ml.MLMethod;
import org.encog.ml.data.versatile.VersatileMLDataSet;

/**
 * Cross validation utility. As a constraint from Encog only
 * {@link VersatileMLDataSet} is supported.
 *
 */
//TODO Improve base (BRANCH):
//Event definitions should always be ordered in the same way (ex : by event def Id)

//TODO Improve inputs (BRANCH) :
//Try a third drv (no need for inverse yet ..)
//Unit Test all Output generator and test them against Encog
//Fix the Acc : check correct Acc sums? day skipped? Acc0 breaks? cf. in progress unit tests
                //The progressiveEventOccWeight is dented as seems to duplicate previous values in order to fill gaps => can it be flatten/smoothed? 
//Play with SMA period (and consequently the lag fix) and Acc (when tested and fixed)
//Related to Acc fix : what about skipped calendar days in some cases?? Should I use effective data instead of weekend gap fix?
//Noted surprising result using classification with a continuous output?? ie should I use classification instead of regression?
//re check nominal in and out puts
//Try ln instead of log10 in house trend?
//Try House Trend smoothed with lagange interpolation
//Mystery : why is HouseTrendDrv.houseTrendOfHouseTrend giving significantly smaller values then HouseTrend2ndSmoother?

//TODO Neural calculation :
//How to override the RPROPType to RPROPType.iRPROPp in ResilientPropagationFactory?
//add other implementations with other network types (ex : Bayesian, Markov ..)

@Deprecated
public interface CrossValidator {
    /**
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
    MLMethod nnMethodSelectionByCrossValidation(VersatileMLDataSet dataSet, int lagWindowSize);

}

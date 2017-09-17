package com.finance.pm.encog.application.training.factories.impl;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.encog.ml.MLMethod;
import org.encog.ml.MLResettable;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.RequiredImprovementStrategy;
import org.encog.neural.networks.training.propagation.manhattan.ManhattanPropagation;

import com.finance.pm.encog.application.training.factories.PropagationFactory;

/**
 * Generic propagation training method wrapper for Encog {@link MLTrainFactory}
 * TODO To generalise outside ResilientPropagation and TemporalDataSet (cf. input size inconsistencies with BackPropagation training)
 */
public class GenericPropagationFactory implements PropagationFactory {

    @Override
    public MLTrain create(MLMethod network, MLDataSet dataSet, String... args) {

        MLTrainFactory trainFactory = new MLTrainFactory();
        String argString = Arrays.stream(args).collect(Collectors.joining(","));
        MLTrain mlTrain = trainFactory.create(network, dataSet, MLTrainFactory.TYPE_RPROP, argString);

        if (network instanceof MLResettable && !(mlTrain instanceof ManhattanPropagation)) {
            mlTrain.addStrategy(new RequiredImprovementStrategy(500));
        }

        return mlTrain;
    }

}
package com.finance.pm.encog.application.nnetwork.propagation.impl;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.encog.ml.MLMethod;
import org.encog.ml.MLResettable;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.RequiredImprovementStrategy;
import org.encog.neural.networks.training.propagation.manhattan.ManhattanPropagation;

import com.finance.pm.encog.application.nnetwork.propagation.PropagationFactory;

/**
 * Generic propagation training method wrapper for Encog {@link MLTrainFactory}
 */
public class GenericPropagationFactory implements PropagationFactory {

    @Override
    public MLTrain create(MLMethod network, MLDataSet dataSet, String propagationType, String... args) {

        if (propagationType == null) throw new RuntimeException();

        MLTrainFactory trainFactory = new MLTrainFactory();
        String argString = (args == null)?null:Arrays.stream(args).collect(Collectors.joining(","));
        MLTrain mlTrain = trainFactory.create(network, dataSet, propagationType, argString);

        if (network instanceof MLResettable && !(mlTrain instanceof ManhattanPropagation)) {
            mlTrain.addStrategy(new RequiredImprovementStrategy(500));
        }

        return mlTrain;
    }

}

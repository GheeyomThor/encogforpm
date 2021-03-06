package com.finance.pm.encog.application.nnetwork.propagation.impl;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.RPROPType;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import com.finance.pm.encog.application.nnetwork.propagation.PropagationFactory;

/**
 * Resilient propagation training method.
 *
 */
public class ResilientPropagationFactory implements PropagationFactory {

    @Override
    public Propagation create(MLMethod network, MLDataSet dataSet, String propagationType, String... args) {

        ResilientPropagation resilientPropagation = new ResilientPropagation((ContainsFlat) network, dataSet);
        resilientPropagation.setRPROPType(RPROPType.iRPROPp);

        return resilientPropagation;
    }

}

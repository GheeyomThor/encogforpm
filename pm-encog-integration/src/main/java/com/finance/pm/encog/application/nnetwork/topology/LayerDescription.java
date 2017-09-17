package com.finance.pm.encog.application.nnetwork.topology;

import org.encog.engine.network.activation.ActivationFunction;

/**
 * External representation for a layer as a mean of network topology
 * description.
 *
 */
public class LayerDescription {

    private Integer neuronsCount;
    private Class<? extends ActivationFunction> activationFunction;

    public LayerDescription(Integer neuronsCount, Class<? extends ActivationFunction> activationFunction) {
        super();
        this.neuronsCount = neuronsCount;
        this.activationFunction = activationFunction;
    }

    public Integer getNeuronsCount() {
        return neuronsCount;
    }

    public ActivationFunction getActivationFunction() {
        ActivationFunction newInstance;
        try {
            newInstance = activationFunction.newInstance();
            return newInstance;
        } catch (InstantiationException | IllegalAccessException e) {
           throw new RuntimeException(e);
        }
    }

}

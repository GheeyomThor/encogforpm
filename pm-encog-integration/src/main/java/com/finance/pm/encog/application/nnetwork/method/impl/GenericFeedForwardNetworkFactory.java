package com.finance.pm.encog.application.nnetwork.method.impl;

import org.encog.ml.MLMethod;
import org.encog.ml.factory.MLMethodFactory;

import com.finance.pm.encog.application.nnetwork.method.NnFactory;

/**
 * Generic wrapper around {@link MLMethodFactory} </br>
 * Creates a neural network. Bias neurons are placed on the input and hidden
 * layers. </br>
 * As is typical for neural networks, there are no bias neurons on the output
 * layer.
 */
public class GenericFeedForwardNetworkFactory implements NnFactory {

    /**
     * @param architecture
     *            For example "? : B−>SIGMOID−>4:B−>SIGMOID−>?" will create a
     *            neural network. with two input neurons and one output neuron.
     *            There are four hidden neurons. The sigmoid activation function
     *            is used between both the input and hidden neuron, as well
     *            between the hidden and output layer. You may notice the two
     *            question marks in the neural network architecture string.
     *            These will be filled in by the input and output layer sizes
     *            specified in the create method and are optional. You can
     *            hard-code the input and output sizes. In this case the numbers
     *            specified in the create call will be ignored.
     */
    @Override
    public MLMethod create(String architecture, int inputSize, int outputSize) {

        MLMethodFactory methodFactory = new MLMethodFactory();
        MLMethod network = methodFactory.create(MLMethodFactory.TYPE_FEEDFORWARD, architecture, inputSize, outputSize);

        return network;
    }

}

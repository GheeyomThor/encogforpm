package com.finance.pm.encog.application.nnetwork.factories;

import java.util.LinkedList;

import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;

import com.finance.pm.encog.application.nnetwork.factories.impl.GenericFeedForwardNetworkFactory;

/**
 * 
 * There are several factors to consider when choosing an activation function.
 * <ul>
 * <li>Firstly, it is important to consider how the type of neural network being
 * used dictates the activation function required.</li>
 * <li>Secondly, consider the necessity of training the neural network using
 * propagation.</li> Propagation training requires an activation function that
 * provides a derivative.
 * <li>Finally, consider the range of output numbers to be used.</li>
 * </ul>
 * Some activation functions deal with only positive numbers or numbers in a
 * particular range.
 *
 */
public interface NnFactory {

    /**
     * PM compliance method
     * 
     * @param topology
     *            describes each layer of the network
     * @return {@link BasicNetwork}
     * @throws Exception
     */
    default BasicNetwork create(LinkedList<LayerDescription> topology) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * @see GenericFeedForwardNetworkFactory
     */
    default MLMethod create(String architecture, int inputSize, int outputSize) throws Exception {
        throw new UnsupportedOperationException();
    }

}

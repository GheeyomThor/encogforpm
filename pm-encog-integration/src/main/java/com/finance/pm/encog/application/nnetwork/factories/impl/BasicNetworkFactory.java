package com.finance.pm.encog.application.nnetwork.factories.impl;

import java.util.LinkedList;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

import com.finance.pm.encog.application.nnetwork.factories.LayerDescription;
import com.finance.pm.encog.application.nnetwork.factories.NnFactory;

/**
 * {@link BasicNetwork} creator
 *
 */
public class BasicNetworkFactory implements NnFactory {

    @Override
    public BasicNetwork create(LinkedList<LayerDescription> topology) throws Exception {

        BasicNetwork basicNetwork = new BasicNetwork();

        LayerDescription inLayer = topology.removeFirst();
        basicNetwork.addLayer(new BasicLayer(null, false, inLayer.getNeuronsCount()));

        LayerDescription outLayer = topology.removeLast();

        topology.forEach(layer -> {
            try {
                basicNetwork.addLayer(new BasicLayer(layer.getActivationFunction(), false, layer.getNeuronsCount()));
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        basicNetwork.addLayer(new BasicLayer(outLayer.getActivationFunction(), false, outLayer.getNeuronsCount()));

        basicNetwork.getStructure().finalizeStructure();
        basicNetwork.reset();

        return basicNetwork;
    }

}

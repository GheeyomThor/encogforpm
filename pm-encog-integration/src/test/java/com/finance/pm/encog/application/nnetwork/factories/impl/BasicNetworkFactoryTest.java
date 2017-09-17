package com.finance.pm.encog.application.nnetwork.factories.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.LinkedList;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.junit.Before;
import org.junit.Test;

import com.finance.pm.encog.application.nnetwork.factories.LayerDescription;
import com.finance.pm.encog.guice.EncogServiceModule;
import com.finance.pm.encog.guice.POCAdapterModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class BasicNetworkFactoryTest {

    @Inject
    private BasicNetworkFactory basicNetworkFactory;

    @Before
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new POCAdapterModule(), new EncogServiceModule());
        injector.injectMembers(this);
    }

    @Test
    public void testCreate() {

        // Given
        LinkedList<LayerDescription> topology = new LinkedList<>();
        topology.add(new LayerDescription(22, ActivationSigmoid.class));
        topology.add(new LayerDescription(44, ActivationSigmoid.class));
        topology.add(new LayerDescription(2, ActivationSigmoid.class));

        try {
            // When
            BasicNetwork network = basicNetworkFactory.create(topology);

            // Then
            assertEquals(3, network.getLayerCount());
            assertEquals(22, network.getInputCount());
            assertEquals(2, network.getOutputCount());
            assertEquals(44, network.getLayerNeuronCount(1));

        } catch (Exception e) {
            fail(String.format("Should not raise exception %s", e.getMessage()));
        }

    }

}

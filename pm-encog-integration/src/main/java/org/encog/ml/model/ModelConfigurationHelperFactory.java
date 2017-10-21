package org.encog.ml.model;

import java.util.HashMap;
import java.util.Map;

import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.config.FeedforwardConfig;
import org.encog.ml.model.config.MethodConfig;
import org.encog.ml.model.config.NEATConfig;
import org.encog.ml.model.config.PNNConfig;
import org.encog.ml.model.config.RBFNetworkConfig;
import org.encog.ml.model.config.SVMConfig;

public class ModelConfigurationHelperFactory {
    
    private static ModelConfigurationHelperFactory singleton;
    /**
     * The standard configurations for each method type.
     */
    private final Map<String, MethodConfig> methodConfigurations = new HashMap<String, MethodConfig>();
    
   
    private ModelConfigurationHelperFactory() {
        super();
        this.methodConfigurations.put(MLMethodFactory.TYPE_FEEDFORWARD, new FeedforwardConfig());
        this.methodConfigurations.put(MLMethodFactory.TYPE_SVM, new SVMConfig());
        this.methodConfigurations.put(MLMethodFactory.TYPE_RBFNETWORK, new RBFNetworkConfig());
        this.methodConfigurations.put(MLMethodFactory.TYPE_NEAT,new NEATConfig());
        this.methodConfigurations.put(MLMethodFactory.TYPE_PNN, new PNNConfig());
    }
    
    public static ModelConfigurationHelperFactory getInsance() {
        if (ModelConfigurationHelperFactory.singleton == null) {
            ModelConfigurationHelperFactory.singleton = new ModelConfigurationHelperFactory();
        }
        return ModelConfigurationHelperFactory.singleton;
    }

    public MethodConfig getMethodConfigurationFor(String methodType) {
        return ModelConfigurationHelperFactory.getInsance().getMethodConfigurations().get(methodType);
    }

    private Map<String, MethodConfig> getMethodConfigurations() {
        return methodConfigurations;
    }

}

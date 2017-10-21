package org.encog.ml.model.method;

import org.encog.ml.model.config.MethodConfig;

public class MethodSpecification {

    private MethodConfig methodConfig;
    private String methodType;
    private String modelArchitecture;


    MethodSpecification(MethodConfig methodConfig, String methodType, String modelArchitecture) {
        super();
        this.methodConfig = methodConfig;
        this.methodType = methodType;
        this.modelArchitecture = modelArchitecture;
    }

    public MethodConfig getMethodConfig() {
        return methodConfig;
    }
    
    public String getMethodType() {
        return methodType;
    }

    public  String getModelArchitecture() {
        return modelArchitecture;
    }

}

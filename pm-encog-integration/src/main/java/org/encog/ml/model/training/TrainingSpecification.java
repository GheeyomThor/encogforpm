package org.encog.ml.model.training;

import org.encog.ml.model.config.MethodConfig;
import org.encog.ml.model.method.MethodSpecification;

public class TrainingSpecification {
    
    private MethodSpecification methodSpecification;
    private String trainingType;
    private String trainingArgs;
    
    
    TrainingSpecification(MethodSpecification methodSpecification, String trainingType, String trainingArgs) {
        super();
        this.methodSpecification = methodSpecification;
        this.trainingType = trainingType;
		this.trainingArgs = trainingArgs;
    }
    

    public MethodConfig getMethodConfig() {
        return methodSpecification.getMethodConfig();
    }
    
    public MethodSpecification getMethodSpecification() {
        return methodSpecification;
    }

    public String getMethodType() {
        return methodSpecification.getMethodType();
    }

    public String getModelArchitecture() {
        return methodSpecification.getModelArchitecture();
    }

    public String getTrainingType() {
        return trainingType;
    }

    public String getTrainingArgs() {
        return trainingArgs;
    }

    
}

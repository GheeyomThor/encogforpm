package org.encog.ml.model.training;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.model.method.MethodSpecification;
import org.encog.ml.model.method.MethodSpecificationBuilder;

public class TrainingSpecificationBuilder {
    
    private String methodType;
    private String modelArchitecture;
    private String trainingType;
    private String trainingArgs;
    
    public TrainingSpecificationBuilder withMethod(String methodType) {
        this.methodType = methodType;
        return this;
    }
    
    public TrainingSpecificationBuilder withArchitecture(String modelArchitecture) {
        this.modelArchitecture = modelArchitecture;
        return this;
    }

    public TrainingSpecificationBuilder withTrainingType(String trainingType) {
        this.trainingType = trainingType;
        return this;
    }

    public TrainingSpecificationBuilder withTrainingArgs(String trainingArgs) {
        this.trainingArgs = trainingArgs;
        return this;
    }


    public TrainingSpecification build(MLDataSet dataSet) {
        
        if (methodType == null) {
            throw new RuntimeException("Please either specify either a method type or a method type and an architecture");
        }
        
        MethodSpecificationBuilder methodSpecificationBuilder = new MethodSpecificationBuilder().withMethod(methodType).withArchitecture(modelArchitecture);
        MethodSpecification methodSpecification = methodSpecificationBuilder.build(dataSet);
        
        if (trainingType == null) {
            trainingType = methodSpecification.getMethodConfig().suggestTrainingType();
        }

        if (trainingArgs == null) {
            trainingArgs =  methodSpecification.getMethodConfig().suggestTrainingArgs(trainingType);
        }

        return new TrainingSpecification(methodSpecification, trainingType, trainingArgs);

    }


}

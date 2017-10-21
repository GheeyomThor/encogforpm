package com.finance.pm.encog.application;

public class NetworkDescription {
    
    private String methodType;
    private String modelArchitecture;
    private String trainingType;
    private String trainingArgs;

    public NetworkDescription(String methodType, String modelArchitecture, String trainingType, String trainingArgs) {
        this.methodType = methodType;
        this.modelArchitecture = modelArchitecture;
        this.trainingType = trainingType;
        this.trainingArgs = trainingArgs;
    }

    public String getMethodType() {
        return methodType;
    }

    public String getModelArchitecture() {
        return modelArchitecture;
    }

    public String getTrainingType() {
        return trainingType;
    }

    public String getTrainingArgs() {
        return trainingArgs;
    }

    @Override
    public String toString() {
        return String.format(
                "NetworkDescription [methodType=%s, modelArchitecture=%s, trainingType=%s, trainingArgs=%s]",
                methodType, modelArchitecture, trainingType, trainingArgs);
    }
}
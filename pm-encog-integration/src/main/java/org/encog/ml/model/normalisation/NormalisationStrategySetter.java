package org.encog.ml.model.normalisation;

import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.normalizers.strategies.NormalizationStrategy;
import org.encog.ml.model.method.MethodSpecification;
import org.encog.ml.model.method.MethodSpecificationBuilder;

public class NormalisationStrategySetter {

    private String methodType;
    private String modelArchitecture;
    private MethodSpecification methodSpecification;
    private NormalizationStrategy normalizationStrategy;

    public NormalisationStrategySetter withMethod(String methodType) {
        this.methodType = methodType;
        return this;
    }

    public NormalisationStrategySetter withArchitecture(String modelArchitecture) {
        this.modelArchitecture = modelArchitecture;
        return this;
    }

    public NormalisationStrategySetter withNormalisationStrategy(NormalizationStrategy normalizationStrategy) {
        this.normalizationStrategy = normalizationStrategy;
        return this;
    }

    public NormalisationStrategySetter withMethodSpecification(MethodSpecification methodSpecification) {
        this.methodSpecification = methodSpecification;
        return this;
    }

    public VersatileMLDataSet setStrategyTo(VersatileMLDataSet dataSet) {

        if ((normalizationStrategy == null && methodType == null) && methodSpecification == null) {
            throw new RuntimeException("Please either specify either a method type or a method type and an architecture or a methodSpecification or a strategy");
        }

        if (normalizationStrategy != null) {
            dataSet.getNormHelper().setStrategy(normalizationStrategy);
        } else {
            if (methodSpecification == null) {
                MethodSpecificationBuilder methodSpecificationBuilder = new MethodSpecificationBuilder().withMethod(methodType).withArchitecture(modelArchitecture);
                methodSpecification = methodSpecificationBuilder.build(dataSet);
            }
            dataSet.getNormHelper().setStrategy(methodSpecification.getMethodConfig().suggestNormalizationStrategy(dataSet, methodSpecification.getModelArchitecture()));
        }

        return dataSet;

    }

}

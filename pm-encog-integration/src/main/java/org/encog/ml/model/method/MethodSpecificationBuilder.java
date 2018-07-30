package org.encog.ml.model.method;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.model.ModelConfigurationHelperFactory;
import org.encog.ml.model.config.MethodConfig;

public class MethodSpecificationBuilder {

	private String methodType;
	private String modelArchitecture;

	public MethodSpecificationBuilder withMethod(String methodType) {
		this.methodType = methodType;
		return this;
	}

	public MethodSpecificationBuilder withArchitecture(String modelArchitecture) {
		this.modelArchitecture = modelArchitecture;
		return this;
	}


	public MethodSpecification build(MLDataSet dataSet) {

		if (methodType == null) {
			throw new RuntimeException("Please either specify either a method type or a method type and an architecture");
		}

		MethodConfig configuration = ModelConfigurationHelperFactory.getInsance().getMethodConfigurationFor(methodType);

		if (modelArchitecture == null) {
			if (dataSet instanceof VersatileMLDataSet) {
				modelArchitecture =  configuration.suggestModelArchitecture((VersatileMLDataSet) dataSet);
			} else {
				throw new UnsupportedOperationException("No architecture suggestion available for a data set of type "+dataSet.getClass().getSimpleName() + ".Please specify an Architeccture.");
			}
		}

		return new MethodSpecification(configuration, methodType, modelArchitecture);

	}

}

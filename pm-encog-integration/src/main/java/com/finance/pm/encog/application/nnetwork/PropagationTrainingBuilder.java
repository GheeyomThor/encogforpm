package com.finance.pm.encog.application.nnetwork;

import java.util.LinkedList;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.training.propagation.Propagation;

import com.finance.pm.encog.application.nnetwork.method.LayerDescription;
import com.finance.pm.encog.application.nnetwork.method.NnFactory;
import com.finance.pm.encog.application.nnetwork.propagation.PropagationFactory;
import com.finance.pm.encog.application.nnetwork.propagation.impl.GenericPropagationFactory;

public class PropagationTrainingBuilder {

    private NnFactory nnFactory;
    private MLDataSet dataSet;
    private LinkedList<LayerDescription> architecture;
    private String architectureString;
    private PropagationFactory propagationFactory;
    private String propagationType;
    private String[] propagationArgs;

    public PropagationTrainingBuilder withMethodFactory(NnFactory nnFactory) {
        this.nnFactory = nnFactory;
        return this;
    }

    public PropagationTrainingBuilder withDataSet(MLDataSet dataSet) {
        this.dataSet = dataSet;
        return this;
    }

    //TODO mapping architecture list object <-> architecture string
    public PropagationTrainingBuilder withArchitecture(LinkedList<LayerDescription> architecture) {
        if (architectureString != null) throw new RuntimeException("Architecture already defined");
        this.architecture = architecture;
        return this;
    }


    public PropagationTrainingBuilder withArchitecture(String achitectureString) {
        if (architecture != null) throw new RuntimeException("Architecture already defined");
        this.architectureString = achitectureString;
        return this;
    }


    public PropagationTrainingBuilder withPropagationFactory(PropagationFactory propagationFactory) {
        this.propagationFactory = propagationFactory;
        return this;
    }

    /**
     * @param propagationType will be propagation type as in {@link Propagation} and {@link MLTrainFactory} </br>
     * Propagation type can be optional when inferred by injection of the propagation implementation
     */
    public PropagationTrainingBuilder withPropagationType(String propagationType) {
        this.propagationType = propagationType;
        return null;
    }

    /**
     * 
     * @param args additional arguments related to the specific propagation each in the form : "name=value"
     */
    public PropagationTrainingBuilder withPropagationParameters(String... args) {
        this.propagationArgs = args;
        return null;
    }

    public MLTrain build() {

        if (    nnFactory == null || (architecture == null && architectureString == null) ||
                dataSet == null || propagationFactory == null
                ) {
            throw new RuntimeException("Incomplete initialisation.");
        }
        if (propagationFactory instanceof GenericPropagationFactory && propagationType == null) {
            throw new RuntimeException("Unspecified propagation type for "+GenericPropagationFactory.class);
        }

        MLMethod mlMethod;
        if (architectureString != null) {
            mlMethod= nnFactory.create(architectureString, dataSet.getInputSize(), dataSet.getIdealSize());
        } else {
            mlMethod = nnFactory.create(architecture);
        }
        MLTrain mlTrain = propagationFactory.create(mlMethod, dataSet, propagationType, propagationArgs);

        return mlTrain;
    }

}

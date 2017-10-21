package com.finance.pm.encog.guice;

import java.lang.annotation.Annotation;

import com.finance.pm.encog.application.prediction.NnPredictor;
import com.finance.pm.encog.application.prediction.impl.CrossValidationPredictor;
import com.finance.pm.encog.data.DataSetLoader;
import com.finance.pm.encog.data.impl.VersatileDataSetLoader;
import com.google.inject.PrivateModule;

public abstract class VersatileDataLoaderModule extends PrivateModule {
    
    private final Class<? extends Annotation> annotation;

    public VersatileDataLoaderModule(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }
    
    @Override
    protected void configure() {
        bind(DataSetLoader.class).annotatedWith(annotation).to(VersatileDataSetLoader.class);
        expose(DataSetLoader.class).annotatedWith(annotation);
        bind(NnPredictor.class).annotatedWith(annotation).to(CrossValidationPredictor.class);
        expose(NnPredictor.class).annotatedWith(annotation);

        bindDataAdapter();
        
    }
    
    public abstract void bindDataAdapter();

}

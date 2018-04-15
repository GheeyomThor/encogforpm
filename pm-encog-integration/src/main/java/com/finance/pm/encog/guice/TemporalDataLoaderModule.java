package com.finance.pm.encog.guice;

import com.finance.pm.encog.application.prediction.NnPredictor;
import com.finance.pm.encog.application.prediction.impl.CrossValidationPredictor;
import com.finance.pm.encog.data.DataSetLoader;
import com.finance.pm.encog.data.impl.TemporalDataSetLoader;
import com.finance.pm.encog.data.impl.VersatileDataSetLoader;
import com.google.inject.PrivateModule;

public abstract class TemporalDataLoaderModule extends PrivateModule {

    @Override
    protected void configure() {

//    	OptionalBinder.newOptionalBinder(binder(), Key.get(NnPredictor.class, Validation.class));
//    	OptionalBinder.newOptionalBinder(binder(), Key.get(DataSetLoader.class, Training.class));
    	bind(DataSetLoader.class).annotatedWith(Training.class).to(VersatileDataSetLoader.class);
		expose(DataSetLoader.class).annotatedWith(Training.class);
		bind(NnPredictor.class).annotatedWith(Validation.class).to(CrossValidationPredictor.class);
		expose(NnPredictor.class).annotatedWith(Validation.class);

        bind(DataSetLoader.class).annotatedWith(Temporal.class).to(TemporalDataSetLoader.class);
        expose(DataSetLoader.class).annotatedWith(Temporal.class);

		bindDataAdapter();
	}

	public abstract void bindDataAdapter();

}

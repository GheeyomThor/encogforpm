package com.finance.pm.encog.guice;

import java.util.Date;

import org.encog.ml.data.MLDataPair;

import com.finance.pm.encog.application.nnetwork.method.NnFactory;
import com.finance.pm.encog.application.nnetwork.method.impl.GenericFeedForwardNetworkFactory;
import com.finance.pm.encog.application.nnetwork.propagation.PropagationFactory;
import com.finance.pm.encog.application.nnetwork.propagation.impl.ResilientPropagationFactory;
import com.finance.pm.encog.application.prediction.NnPredictor;
import com.finance.pm.encog.application.prediction.impl.GenericPredictor;
import com.finance.pm.encog.application.training.NnTrainer;
import com.finance.pm.encog.application.training.impl.CrossValidationTrainer;
import com.finance.pm.encog.application.training.impl.PropagationTrainer;
import com.finance.pm.encog.util.CsvImportExport;
import com.finance.pm.encog.util.EGFileReferenceManager;
import com.finance.pm.encog.util.impl.MLDataPairCsvImportExport;
import com.finance.pm.encog.util.impl.MapCsvImportExport;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Guice module
 */
public class EncogServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        
        //CSV Import export
        bind(new TypeLiteral<CsvImportExport<MLDataPair>>() {}).to(MLDataPairCsvImportExport.class);
        bind(new TypeLiteral<CsvImportExport<Date>>() {}).to(MapCsvImportExport.class);
        
        //Eg files
        bind(EGFileReferenceManager.class);

        //One fold POC with temporal data set
        bind(PropagationFactory.class).to(ResilientPropagationFactory.class);
        bind(NnFactory.class).to(GenericFeedForwardNetworkFactory.class);
        bind(NnTrainer.class).annotatedWith(Names.named("temporal")).to(PropagationTrainer.class);
        bind(NnPredictor.class).annotatedWith(Names.named("temporal")).to(GenericPredictor.class);
         
        //Cross Validation with versatile data set @see DataLoaderModule for DataSetLoader and NnPredictor bindings
        bind(NnTrainer.class).annotatedWith(Names.named("versatile")).to(CrossValidationTrainer.class);

    }

}

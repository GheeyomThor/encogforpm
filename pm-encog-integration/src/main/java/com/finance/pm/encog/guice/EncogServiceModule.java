package com.finance.pm.encog.guice;

import java.util.Date;

import org.encog.ml.data.MLDataPair;

import com.finance.pm.encog.application.nnetwork.factories.NnFactory;
import com.finance.pm.encog.application.nnetwork.factories.impl.GenericFeedForwardNetworkFactory;
import com.finance.pm.encog.application.prediction.NnPredictor;
import com.finance.pm.encog.application.prediction.impl.GenericPredictor;
import com.finance.pm.encog.application.training.NnTrainer;
import com.finance.pm.encog.application.training.factories.PropagationFactory;
import com.finance.pm.encog.application.training.factories.impl.ResilientPropagationFactory;
import com.finance.pm.encog.application.training.impl.PropagationTrainer;
import com.finance.pm.encog.data.DataImporter;
import com.finance.pm.encog.data.impl.TemporalDataSetImporter;
import com.finance.pm.encog.util.CsvImportExport;
import com.finance.pm.encog.util.impl.MLDataPairCsvImportExport;
import com.finance.pm.encog.util.impl.MapCsvImportExport;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Guice module
 */
public class EncogServiceModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(DataImporter.class).to(TemporalDataSetImporter.class);
        bind(new TypeLiteral<CsvImportExport<MLDataPair>>() {}).to(MLDataPairCsvImportExport.class);
        bind(new TypeLiteral<CsvImportExport<Date>>() {}).to(MapCsvImportExport.class);

        bind(PropagationFactory.class).to(ResilientPropagationFactory.class);
        bind(NnFactory.class).to(GenericFeedForwardNetworkFactory.class);

        bind(NnTrainer.class).to(PropagationTrainer.class);

        bind(NnPredictor.class).to(GenericPredictor.class);

        //bind(CrossValidator.class).to(VersatileSetCrossValidator.class);

    }

}

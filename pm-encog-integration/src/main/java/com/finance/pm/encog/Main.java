package com.finance.pm.encog;

import com.finance.pm.encog.application.EncogService;
import com.finance.pm.encog.guice.DefaultTemporalDataLoaderModule;
import com.finance.pm.encog.guice.EncogServiceModule;
import com.finance.pm.encog.guice.POCAdapterModule;
import com.finance.pm.encog.util.CsvImportExport;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class Main {

    public static void main(String[] args) throws Exception {

        // For this Proof of concept we use pre recorded training data from the file system
        // The input size is (26 * (lag window size)) and ideal output size 1.
        Injector injector = Guice.createInjector(new POCAdapterModule(), new DefaultTemporalDataLoaderModule(), new EncogServiceModule());
        EncogService encogService = injector.getInstance(EncogService.class);

        // Compute
        //FIXME LinkedHashMap<Integer, double[]> prediction = encogService.oneFoldTrainAndCompute("?:B->SIGMOID->25:B->SIGMOID->?", 12);

        // Export prediction
        CsvImportExport<Integer> csvImportExport = injector.getInstance(Key.get(new TypeLiteral<CsvImportExport<Integer>>() {}));
        //FIXME csvImportExport.exportData(Optional.empty(), Optional.empty(), "prediction", prediction);

    }

}

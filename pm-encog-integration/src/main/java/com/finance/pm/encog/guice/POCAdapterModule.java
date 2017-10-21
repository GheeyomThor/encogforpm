package com.finance.pm.encog.guice;

import com.finance.pm.encog.util.DataSourceAdapter;
import com.finance.pm.encog.util.impl.POCDataSourceAdapter;
import com.google.inject.AbstractModule;

/**
 * For binding a specific class to the data source adapter
 * {@link DataSourceAdapter}
 */
public class POCAdapterModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new VersatileDataLoaderModule(Training.class) {
            
            @Override
            public void bindDataAdapter() {
                bind(DataSourceAdapter.class).to(POCDataSourceAdapter.class);
            }
        });
        install(new VersatileDataLoaderModule(Validation.class) {
            
            @Override
            public void bindDataAdapter() {
                bind(DataSourceAdapter.class).to(POCDataSourceAdapter.class);
            }
        });

    }

}

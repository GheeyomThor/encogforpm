package com.finance.pm.encog.guice;

import com.finance.pm.encog.data.DataSetLoader;
import com.finance.pm.encog.data.impl.TemporalDataSetLoader;
import com.finance.pm.encog.util.DataSourceAdapter;
import com.finance.pm.encog.util.impl.POCDataSourceAdapter;
import com.google.inject.PrivateModule;

public class TemporalDataLoaderModule extends PrivateModule {

    @Override
    protected void configure() {

        bind(DataSetLoader.class).annotatedWith(Temporal.class).to(TemporalDataSetLoader.class);
        expose(DataSetLoader.class).annotatedWith(Temporal.class);

        bind(DataSourceAdapter.class).to(POCDataSourceAdapter.class);

    }

}

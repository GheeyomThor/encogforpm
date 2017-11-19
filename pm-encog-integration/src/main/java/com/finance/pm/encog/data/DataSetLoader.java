package com.finance.pm.encog.data;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.factory.MLMethodFactory;

/**
 * This will import the training input data and their corresponding ideals into
 * a {@link org.encog.ml.data.versatile.VersatileMLDataSet} ready for
 * training.</br>
 * It uses two “windows,” a future window and a past window. Both windows must
 * have a window size, which is the amount of data that is either predicted or
 * is needed to predict. </br>
 * Implementation may vary.
 */
public interface DataSetLoader {

    default MLDataSet loadData(
            ColumnType inputColumnType, ColumnType outputColumnType, 
            int lagWindowSize, int leadWindowSize) {

        return this.loadData(inputColumnType, outputColumnType, lagWindowSize, leadWindowSize, MLMethodFactory.TYPE_FEEDFORWARD, null);

    }

    MLDataSet loadData(
            ColumnType inputColumnType, ColumnType outputColumnType, 
            int lagWindosSize, int predictWindowSize,
            String typeFeedforward, String modelArchitecture);

}

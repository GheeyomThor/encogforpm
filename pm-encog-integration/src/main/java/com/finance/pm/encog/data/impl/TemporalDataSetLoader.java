package com.finance.pm.encog.data.impl;

import java.util.List;

import org.encog.ml.data.temporal.TemporalDataDescription;
import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.ml.data.temporal.TemporalPoint;
import org.encog.ml.data.versatile.columns.ColumnType;

import com.finance.pm.encog.data.DataSetLoader;
import com.finance.pm.encog.util.DataSourceAdapter;
import com.google.inject.Inject;

/**
 * Data are imported through the {@link DataSourceAdapter} and are expected
 * already normalised and hence used RAW without additional normalisation. </br>
 * Improvement is needed for normalisation : take a look at Encog
 * {@link org.encog.mathutil.Equilateral} normalisation for the input instead of
 * an adhoc normalisation.
 */
public class TemporalDataSetLoader implements DataSetLoader {

    @Inject
    DataSourceAdapter pmDataAdapter;

    public TemporalDataSetLoader() {
        super();
    }

    @Override
    public TemporalMLDataSet loadData(
            ColumnType inputColumnType, ColumnType outputColumnType, 
            int lagWindowSize, int leadWindowSize,
            String typeFeedforward, String modelArchitecture) {

        List<double[]> trainingInputValues = pmDataAdapter.getTrainingInputs();
        List<double[]> trainingIdealValues = pmDataAdapter.getTrainingOutputs();

        if (trainingInputValues.size() != trainingIdealValues.size()) {
            throw new RuntimeException("Inputs and outputs must comply with each other. Size differ.");
        }

        TemporalMLDataSet temporalMLDataSet = new TemporalMLDataSet(lagWindowSize, leadWindowSize);

        // We add one input description for each event
        pmDataAdapter.getInputEventsDescription().stream().forEach(event -> {
            TemporalDataDescription desc = new TemporalDataDescription(TemporalDataDescription.Type.RAW, true, false);
            temporalMLDataSet.addDescription(desc);
        });

        // One output description for the ideal
        TemporalDataDescription desc = new TemporalDataDescription(TemporalDataDescription.Type.RAW, false, true);
        temporalMLDataSet.addDescription(desc);

        // Creating the Temporal points for each input vector and its ideal.
        for (int dateIndex = 0; dateIndex < trainingIdealValues.size(); dateIndex++) {

            TemporalPoint point = new TemporalPoint(temporalMLDataSet.getDescriptions().size());
            point.setSequence(dateIndex);

            // inputs
            double[] inputData = trainingInputValues.get(dateIndex);
            for (int j = 0; j < inputData.length; j++) {
                point.setData(j, inputData[j]);
            }

            // ideals
            // At this stage we only are interested by one value for the output
            point.setData(inputData.length, trainingIdealValues.get(dateIndex)[0]);

            temporalMLDataSet.getPoints().add(point);

        }

        temporalMLDataSet.generate();

        return temporalMLDataSet;

    }

}

package com.finance.pm.encog.data.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
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

	private static Logger LOGGER = Logger.getLogger(TemporalDataSetLoader.class);

	@Inject
	DataSourceAdapter pmDataAdapter;

	//private double trainFoldsRatio = 2d/3d;

	public TemporalDataSetLoader() {
		super();
	}

	@Override
	public TemporalMLDataSet loadData(
			ColumnType inputColumnType, ColumnType outputColumnType, 
			int lagWindowSize, int leadWindowSize,
			String modelType, String modelArchitecture) {

		List<double[]> trainingInputValues = pmDataAdapter.getTrainingInputs();
		List<double[]> trainingIdealValues = pmDataAdapter.getTrainingOutputs();

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
		int tIdealsMaxLength = trainingIdealValues.size();
		int vInputMaxLength = trainingInputValues.size();
		double trainFoldsRatio = pmDataAdapter.getTrainFoldsRatio();
		tIdealsMaxLength = Math.min(tIdealsMaxLength, (int)(((double)vInputMaxLength)*trainFoldsRatio));
		List<Date> inputsDatesList = pmDataAdapter.getTrainingInputsDatesList();
		LOGGER.info("Trainning fold: from "+inputsDatesList.get(0)+" to "+inputsDatesList.get(tIdealsMaxLength-1));
		LOGGER.info("Validation fold: from "+inputsDatesList.get(0)+" to "+inputsDatesList.get(vInputMaxLength-1));

		for (int dateIndex = 0; dateIndex < vInputMaxLength; dateIndex++) {

			TemporalPoint point = new TemporalPoint(temporalMLDataSet.getDescriptions().size());
			point.setSequence(dateIndex);

			// inputs
			double[] inputData = trainingInputValues.get(dateIndex);
			for (int j = 0; j < inputData.length; j++) {
				point.setData(j, inputData[j]);
			}

			// ideals
			// At this stage we only are interested by one value for the output
			if (dateIndex < tIdealsMaxLength) point.setData(inputData.length, trainingIdealValues.get(dateIndex)[0]);

			temporalMLDataSet.getPoints().add(point);

		}

		temporalMLDataSet.generate();

		return temporalMLDataSet;

	}

	@Override
	public DataSourceAdapter getPmDataAdapter() {
		return pmDataAdapter;
	}

}

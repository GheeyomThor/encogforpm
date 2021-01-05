package com.finance.pm.encog.data.impl;

import java.util.Arrays;
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

		if (lagWindowSize < 1) throw new UnsupportedOperationException("Lag size < 1 is not supported.");

		List<double[]> trainingInputValues = pmDataAdapter.getTrainingInputs();
		List<double[]> trainingIdealValues = pmDataAdapter.getTrainingOutputs();

		//The temporal dataSet (generate) will take lagWindowSize inputs from 0 and leadWindowSize outputs from 1 (shift 1)
		//I guess this works when the ideal is part of the input and we went to guess the next ideal.
		//In our case, we need to apply -1 to the output as they are not supposed to be shifted this way.
		int idealShift = 1;
		TemporalMLDataSet temporalMLDataSet = new TemporalMLDataSet(lagWindowSize, leadWindowSize);

		// We add one input description for each event
		pmDataAdapter.getInputEventsDescription().stream().forEach(event -> {
			TemporalDataDescription desc = new TemporalDataDescription(TemporalDataDescription.Type.RAW, true, false);
			temporalMLDataSet.addDescription(desc);
		});

		// One output description for the ideal
		if (leadWindowSize > 1) throw new UnsupportedOperationException("Lead size > 1 is not supported. Please fix.");
		TemporalDataDescription desc = new TemporalDataDescription(TemporalDataDescription.Type.RAW, false, true);
		temporalMLDataSet.addDescription(desc);

		// Creating the Temporal points for each input vector and its ideal.
		int nbOutputEntries = trainingIdealValues.size() - lagWindowSize;
		int nbInputEntries = trainingInputValues.size() - lagWindowSize - idealShift;
		LOGGER.info("Number of usable inputs: " + nbInputEntries + ". Number of usable ideals: " + nbOutputEntries + ".");

		List<Date> inputsDatesList = pmDataAdapter.getTrainingInputsDatesList();
		LOGGER.info("Trainig inputs keys span: from " + inputsDatesList.get(0) + " to " + inputsDatesList.get(inputsDatesList.size()-1));
		int maxIterationsIdealShift;
		if (leadWindowSize == 0) { //Validation set
			LOGGER.info("Validation fold only: from " + inputsDatesList.get(lagWindowSize)+" to " + inputsDatesList.get(nbInputEntries + lagWindowSize + idealShift -1));
			maxIterationsIdealShift = nbInputEntries + lagWindowSize + idealShift;
		} else { //Training set
			double trainFoldsRatio = pmDataAdapter.getTrainFoldsRatio();
			int nbTrainingInputEntries = (int) Math.min(nbOutputEntries, (long) (((double) nbInputEntries)*trainFoldsRatio));
			LOGGER.info("Number of training inputs: " + nbTrainingInputEntries + ".");
			LOGGER.info("Training fold: from " + inputsDatesList.get(lagWindowSize) + " to " + inputsDatesList.get(nbTrainingInputEntries + lagWindowSize + idealShift -1));
			maxIterationsIdealShift = nbTrainingInputEntries + lagWindowSize + idealShift;
		}

		// Note the shift idealShift in trainingIdealValues: The temporal dataSet (generate) will take lagWindowSize inputs from 0 and leadWindowSize outputs from 1 (shift 1)
		for (int dateIndex = idealShift; dateIndex < maxIterationsIdealShift; dateIndex++) {

			TemporalPoint point = new TemporalPoint(temporalMLDataSet.getDescriptions().size());
			point.setSequence(dateIndex-idealShift);

			// inputs
			double[] inputData = trainingInputValues.get(dateIndex);
			for (int j = 0; j < inputData.length; j++) {
				double oneInputColumn = inputData[j];
				if (Double.isNaN(oneInputColumn)) throw new RuntimeException("Invalid input: " + Arrays.toString(inputData));
				point.setData(j, oneInputColumn);
			}

			// ideals
			if (leadWindowSize > 0) { //Training data
				// At this stage we only are interested by one value for the output
				double oneOutputColumn = trainingIdealValues.get(dateIndex-idealShift)[0];
				if (Double.isNaN(oneOutputColumn)) throw new RuntimeException("Invalid ideal: " + Arrays.toString(trainingIdealValues.get(dateIndex-1)));
				point.setData(inputData.length, oneOutputColumn);
			}

			temporalMLDataSet.getPoints().add(point);

		}

		TemporalPoint additionnalPointToWorkArroundTheIdealShift = new TemporalPoint(temporalMLDataSet.getDescriptions().size());
		additionnalPointToWorkArroundTheIdealShift.setData(temporalMLDataSet.getPoints().get(temporalMLDataSet.getPoints().size()-1).getData());
		additionnalPointToWorkArroundTheIdealShift.setSequence(maxIterationsIdealShift-idealShift);
		temporalMLDataSet.getPoints().add(additionnalPointToWorkArroundTheIdealShift);

		temporalMLDataSet.generate();

		if (LOGGER.isDebugEnabled()) 
			TemporalMLDataSet.toList(temporalMLDataSet).stream().forEach(pair -> LOGGER.info(Arrays.toString(pair.getInputArray()) + " -> " + Arrays.toString(pair.getIdealArray())));

		LOGGER.info("DataSet input size: "+ temporalMLDataSet.getData().size());
		LOGGER.info("DataSet first pair: " + Arrays.toString(temporalMLDataSet.getData().get(0).getInputArray()) + " -> " + Arrays.toString(temporalMLDataSet.getData().get(0).getIdealArray()));
		LOGGER.info("DataSet last pair: " + Arrays.toString(temporalMLDataSet.getData().get(temporalMLDataSet.getData().size()-1).getInputArray()) + " -> " + Arrays.toString(temporalMLDataSet.getData().get(temporalMLDataSet.getData().size()-1).getIdealArray()));
		return temporalMLDataSet;

	}

	@Override
	public DataSourceAdapter getPmDataAdapter() {
		return pmDataAdapter;
	}

}

package com.finance.pm.encog.data.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.encog.ml.data.versatile.sources.VersatileDataSource;

/**
 * Implementation of the {@link VersatileDataSource} to allow lists of objects
 * as source </br>
 * This does not support headers (for example as first element of each lists)
 * Only support lists of arrays so far.
 *
 * @param <T>
 *            type if the inputs
 * @param <X>
 *            type of the outputs
 */
public class VersatileMapDataSource implements VersatileDataSource {

    private int readingCursor;
    private int inputRowSize;
    private int outputRowSize;
    private List<double[]> trainingInputValues;
    private List<double[]> trainingIdealValues;

    public VersatileMapDataSource(List<double[]> trainingInputValues) {

        readingCursor = 0;

        double[] inputFirstLine = trainingInputValues.get(0);
        this.inputRowSize = inputFirstLine.length;
        this.trainingInputValues = trainingInputValues;

    }


    public VersatileMapDataSource(List<double[]> trainingInputValues, List<double[]> trainingIdealValues) {

        readingCursor = 0;

        double[] inputFirstLine = trainingInputValues.get(0);
        double[] outputFirstLine = trainingIdealValues.get(0);

        this.trainingInputValues = trainingInputValues;
        this.inputRowSize = inputFirstLine.length;

        this.trainingIdealValues = trainingIdealValues;
        this.outputRowSize = outputFirstLine.length;

        if (trainingInputValues.size() != trainingIdealValues.size()) {
            throw new RuntimeException("Inconsistent input output sizes");
        }
        

    }

    @Override
    public String[] readLine() {

        if(readingCursor == this.trainingInputValues.size()) return null;

        List<String> line = new ArrayList<String>();

        List<String> inputs = Arrays.stream(this.trainingInputValues.get(readingCursor)).boxed().map(d -> d.toString()).collect(Collectors.toList());
        if ( inputs.size() != inputRowSize ) {
            throw new RuntimeException(String.format("Inconsistent learning data set at line %d", readingCursor));
        }
        line.addAll(inputs);

        if (this.trainingIdealValues != null) {
            List<String> outputs = Arrays.stream(this.trainingIdealValues.get(readingCursor)).boxed().map(d -> d.toString()).collect(Collectors.toList());
            if ( outputs.size() != outputRowSize ) {
                throw new RuntimeException(String.format("Inconsistent learning data set at line %d", readingCursor));
            }
            line.addAll(outputs);
        }

        readingCursor++;

        return line.toArray(new String[0]);
    }

    @Override
    public void rewind() {
        readingCursor = 0;
    }

    @Override
    public int columnIndex(String name) {
        return -1;
    }

}

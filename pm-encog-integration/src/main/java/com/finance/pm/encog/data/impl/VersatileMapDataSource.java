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
    private int inputSize;
    private int outputSize;

    List<List<String>> inputsRows;
    List<List<String>> outputsRows;

    public VersatileMapDataSource(List<double[]> trainingInputValues) {

        readingCursor = 0;

        double[] inputFirstLine = trainingInputValues.get(0);
        inputsRows = 
                trainingInputValues.stream().map(
                        doubleArray -> Arrays.stream(doubleArray).boxed()
                        .map(d -> d.toString()).collect(Collectors.toList()))
                .collect(Collectors.toList());
        this.inputSize = inputFirstLine.length;

    }


    public VersatileMapDataSource(List<double[]> trainingInputValues, List<double[]> trainingIdealValues) {

        readingCursor = 0;

        double[] inputFirstLine = trainingInputValues.get(0);
        double[] outputFirstLine = trainingIdealValues.get(0);

        inputsRows = 
                trainingInputValues.stream().map(
                        doubleArray -> Arrays.stream(doubleArray).boxed()
                        .map(d -> d.toString()).collect(Collectors.toList()))
                .collect(Collectors.toList());
        this.inputSize = inputFirstLine.length;

        outputsRows = 
                trainingIdealValues.stream().map(
                        doubleArray -> Arrays.stream(doubleArray).boxed()
                        .map(d -> d.toString()).collect(Collectors.toList()))
                .collect(Collectors.toList());
        this.outputSize = outputFirstLine.length;

        if (this.inputsRows.size() != this.outputsRows.size()) {
            throw new RuntimeException("Inconsistent input output sizes");
        }

    }

    @Override
    public String[] readLine() {

        if(readingCursor == this.inputsRows.size()) return null;

        List<String> line = new ArrayList<String>();

        List<String> inputs = this.inputsRows.get(readingCursor);
        if ( inputs.size() != inputSize ) {
            throw new RuntimeException(String.format("Inconsistent learning data set at line %d", readingCursor));
        }
        line.addAll(inputs);

        if (this.outputsRows != null) {
            List<String> outputs = this.outputsRows.get(readingCursor);
            if ( outputs.size() != outputSize ) {
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

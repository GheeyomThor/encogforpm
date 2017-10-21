package com.finance.pm.encog.application;

import java.util.Arrays;

import org.encog.ml.data.versatile.columns.ColumnType;

public class InputOutputDescription {
    private ColumnType inputType;
    private ColumnType outputType;
    private int lagWindowSize;
    private int leadWindowSize;
    private String[] ioCalculationParams;

    public InputOutputDescription(ColumnType inputType, ColumnType outputType, int lagWindowSize, int leadWindowSize, String... ioCalculationParams) {
        this.inputType = inputType;
        this.outputType = outputType;
        this.lagWindowSize = lagWindowSize;
        this.leadWindowSize = leadWindowSize;
        this.ioCalculationParams = ioCalculationParams;
    }

    public ColumnType getInputType() {
        return inputType;
    }

    public ColumnType getOutputType() {
        return outputType;
    }

    public int getLagWindowSize() {
        return lagWindowSize;
    }

    public int getLeadWindowSize() {
        return leadWindowSize;
    }

    @Override
    public String toString() {
        return String.format(
                "InputOutputDescription [inputType=%s, outputType=%s, lagWindowSize=%s, leadWindowSize=%s, ioCalculationParams=%s]",
                inputType, outputType, lagWindowSize, leadWindowSize, Arrays.toString(ioCalculationParams));
    }
    
    
}
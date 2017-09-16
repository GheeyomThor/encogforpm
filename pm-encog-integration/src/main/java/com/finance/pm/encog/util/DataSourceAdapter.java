package com.finance.pm.encog.util;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;

/**
 * Adapter to any data source of inputs and ideal training data To be injected
 * by the service consumer
 *
 */
public interface DataSourceAdapter {

    public SortedMap<Date, double[]> geTrainingOutputs();

    public SortedMap<Date, double[]> geTrainingInputs();

    // For convenience only as the size of input can derived from the training
    // input Map
    public List<String> getInputEventsDescription();

}

package com.finance.pm.encog.util;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Adapter to any data source of inputs and ideal training data To be injected
 * by the service consumer
 *
 */
public interface DataSourceAdapter {

    public List<double[]> getTrainingOutputs();

    public List<double[]> getTrainingInputs();

    // For convenience only as the size of input can derived from the training
    // input Map
    public List<String> getInputEventsDescription();
    
    public  List<Date> getTrainingInputsDatesList();

    public File getDataFile();

}

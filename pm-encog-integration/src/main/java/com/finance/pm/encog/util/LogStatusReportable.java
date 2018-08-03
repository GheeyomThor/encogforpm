package com.finance.pm.encog.util;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.encog.StatusReportable;
import org.encog.ml.MLMethod;

public class LogStatusReportable implements StatusReportable, Closeable {
    
    private static Logger LOGGER = Logger.getLogger(LogStatusReportable.class);
   
    private BufferedWriter bufferedWriter;

    public LogStatusReportable(String resultBaseFileName) throws IOException {
        File rFile = new File(System.getProperty("installdir") + File.separator + "neural" + File.separator + resultBaseFileName + ".txt");
        bufferedWriter = new BufferedWriter(new FileWriter(rFile));
    }

    @Override
    public void report(int total, int current, String message) {
        if (total == 0) {
            print(current + " : " + message);
        } else {
            print(current + "/" + total + " : " + message);
        }

    }

    public void regressionError(double trainingRegressionError, double validationRegressionError) {
        print("Training error : " + trainingRegressionError);
        print("Validation error : " + validationRegressionError);
    }

    public void finalModel(MLMethod bestMethod) {
        print("Final model: " + bestMethod);
    }

    public void helper(String helper) {
        print("Helper : "+helper);
    }
	
	public void sizes(int totalInputRows, int trainingSize, int validationSize, int inputWidth, int idealWidth) {
		print("Training input length (nb rows) : " + totalInputRows+ ", inc. : training folds : "+trainingSize+ " and validation fold "+validationSize +
				".Input width : " + inputWidth + ", ideal width : " + idealWidth);
		
	}
    
    public void print(Object ... objects) {
        Arrays.asList(objects).stream().forEach( o -> {
            LOGGER.info(o.toString());
            try {
                bufferedWriter.write(o.toString());
                bufferedWriter.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void close() throws IOException {
       bufferedWriter.close();
    }



}

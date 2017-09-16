package com.finance.pm.encog.util;

import java.io.File;
import java.util.Map;

public interface CsvImportExport<T> {

    void exportData(File exportFile, Map<T, double[]> map);

    default Map<T, double[]> importData(File exportFile) {
        throw new UnsupportedOperationException();
    }

}

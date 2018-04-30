package com.finance.pm.encog.util;

import java.io.File;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;

public interface CsvImportExport<T> {

	UUID runStamp = UUID.randomUUID();

	void exportData(String baseFileName, String exportFileNameExt, Map<T, double[]> map);

	default SortedMap<T, double[]> importData(File exportFile) {
		throw new UnsupportedOperationException();
	}

}

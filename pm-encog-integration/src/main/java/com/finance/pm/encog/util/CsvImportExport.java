package com.finance.pm.encog.util;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.UUID;

import com.finance.pm.encog.application.InputOutputDescription;
import com.finance.pm.encog.application.NetworkDescription;

public interface CsvImportExport<T> {

	UUID runStamp = UUID.randomUUID();

	void exportData(Optional<InputOutputDescription> iODescr, Optional<NetworkDescription> netDescr, String exportFileNameExt, Map<T, double[]> map);

	default SortedMap<T, double[]> importData(File exportFile) {
		throw new UnsupportedOperationException();
	}

}

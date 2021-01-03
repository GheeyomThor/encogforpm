package com.finance.pm.encog.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.encog.ml.data.versatile.columns.ColumnType;

public class InputOutputDescription {
	private String reference;
	private String method;
	private ColumnType inputType;
	private ColumnType outputType;
	private Integer lagWindowSize;
	private Integer leadWindowSize;
	private String[] ioCalculationParams;

	public InputOutputDescription(String reference, String method, ColumnType inputType, ColumnType outputType, Integer lagWindowSize, Integer leadWindowSize, String... ioCalculationParams) {
		this.reference = reference;
		this.method = method;
		this.inputType = inputType;
		this.outputType = outputType;
		this.lagWindowSize = lagWindowSize;
		this.leadWindowSize = leadWindowSize;
		this.ioCalculationParams = ioCalculationParams;
	}
	
	public String getReference() {
		return reference;
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
		return String.format("InputOutputDescription [method=%s, ioCalculationParams=%s]", method, Arrays.toString(ioCalculationParams));
	}

	public String toEntry() {
		boolean allNonNull =  (method != null) && Arrays.stream(ioCalculationParams).allMatch(s -> s != null);
		if (!allNonNull) {
			throw new RuntimeException("InputOutputDescription that contains null values can't evaluate as entry : "+this.toString());
		}
		return String.format("InputOutputDescription [method=%s, ioCalculationParams=%s]", method, Arrays.toString(ioCalculationParams));
	}

	public String toPattern() {
		List<String> ioCalcParamsPatterns = Arrays.stream(ioCalculationParams).map(s -> (s == null)?"(.*)":Pattern.quote(s)).collect(Collectors.toList());
		return String.format(
				"InputOutputDescription \\[method=%s, ioCalculationParams=%s\\]", (method == null)?"(.*)":Pattern.quote(method), ioCalcParamsPatterns.toString().replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]"));
	}
	
	public String toSubstitution(AtomicInteger groupNumberSeed) {
		String methodSubstitution = (method == null)?"$"+groupNumberSeed.getAndIncrement():method;
		List<String> ioCalculationParamsSubs = new ArrayList<>();
		for (int i=0; i < ioCalculationParams.length; i++) {
			ioCalculationParamsSubs.add((ioCalculationParams[i] == null)?"$"+groupNumberSeed.getAndIncrement():ioCalculationParams[i]);
		}
		return String.format("InputOutputDescription [method=%s, ioCalculationParams=%s]", methodSubstitution, ioCalculationParamsSubs.toString());
	}

}
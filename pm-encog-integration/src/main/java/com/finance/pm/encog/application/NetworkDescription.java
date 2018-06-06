package com.finance.pm.encog.application;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class NetworkDescription {

	private String methodType;
	private String modelArchitecture;
	private String trainingType;
	private String trainingArgs;

	public NetworkDescription(String methodType, String modelArchitecture, String trainingType, String trainingArgs) {
		this.methodType = methodType;
		this.modelArchitecture = modelArchitecture;
		this.trainingType = trainingType;
		this.trainingArgs = trainingArgs;
	}

	public String getMethodType() {
		return methodType;
	}

	public String getModelArchitecture() {
		return modelArchitecture;
	}

	public String getTrainingType() {
		return trainingType;
	}

	public String getTrainingArgs() {
		return trainingArgs;
	}

	@Override
	public String toString() {
		return String.format(
				"NetworkDescription [methodType=%s, modelArchitecture=%s, trainingType=%s, trainingArgs=%s]",
				methodType, modelArchitecture, trainingType, trainingArgs);
	}

	public String toEntry() { //FIXME handle null as for InputOutputDescription and replace null values significance using optional
		return toString();
	}

	public String toPattern() { //FIXME a null value should signify (.*) as in InputOutputDescription
		return Pattern.quote(toString());
	}

	public String toSubstitution(AtomicInteger groupNumberSeed) { //FIXME a null value should signify \\$groupNumber as in InputOutputDescription
		return toString();
	}
}
package com.finance.pm.encog.application;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.encog.ml.factory.MLMethodFactory;

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

	public NetworkDescription(String nnTrainingArgs, int inputWidth, int outputWidth, int nnLagWindowSize, int nnLeadWindowSize) {

		//ex : maxIterations=300,xxx=yyy,...,nnArchitecture=?:B->TANH->f1.5:B->TANH->?,nnMethod=feedforward,nnTrainType=rprop,INIT_UPDATE=0.1,MAX_STEP=50,...
		Map<String, String> trainingParamsMap = parseTrainingArgs(nnTrainingArgs);

		//Architecture Example
		//?:B->TANH->4:B->TANH->?
		//Means :
		//layer 1. '?:B' 		Activation==default(linear),Count==default(inputWitdh),Bias==true
		//layer 2. 'TANH->4:B' 	Activation==TANH,Count==4,Bias==true
		//layer 3. 'TANH->?'	Activation==TANH,Count==default(outputWidth),Bias==false
		//@see MethodConfig.class for other examples.
		String rowArchitectureDescrString = (trainingParamsMap.containsKey("nnArchitecture"))?trainingParamsMap.get("nnArchitecture"):"?:B->TANH->f1.5:B->TANH->?"; //"?:B->TANH->"+ (int) hiddenLayerCount +":B->TANH->?";
		modelArchitecture = rowArchitectureDescrString;
		Pattern p = Pattern.compile("f([0-9]\\.[0-9])");
		Matcher matcher = p.matcher(rowArchitectureDescrString);
		while (matcher.find()) {
			String matchedGroup = matcher.group(1);
			double hiddenLayerFactor = Double.valueOf(matchedGroup);
			int hiddenLayerCount = (int) (((double) (inputWidth * nnLagWindowSize + outputWidth * nnLeadWindowSize)) * hiddenLayerFactor);
			modelArchitecture = modelArchitecture.replaceFirst("f" + matchedGroup, hiddenLayerCount + "");
		}

		//	Others
		methodType = (trainingParamsMap.containsKey("nnMethod"))?trainingParamsMap.get("nnMethod"):MLMethodFactory.TYPE_FEEDFORWARD; //MLMethodFactory.TYPE_FEEDFORWARD;
		trainingType = trainingParamsMap.get("nnTrainType"); //can be null //@see MLTrainFactory. Defaults to "rprop" ie ResilientPropagation for MLMethodFactory.TYPE_FEEDFORWARD @see MethodConfig
		trainingArgs = nnTrainingArgs; //Propagation training args subset is flatMapped into the full set of args //@see MLTrainFactory. Defaults to ""

	}

	private Map<String, String> parseTrainingArgs(String trainingArgs) {
		return Arrays.stream(trainingArgs.split(",")).collect(Collectors.toMap(p -> p.split("=")[0].trim(), p -> p.split("=")[1].trim()));
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
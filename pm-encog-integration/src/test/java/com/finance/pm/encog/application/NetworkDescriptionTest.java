package com.finance.pm.encog.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class NetworkDescriptionTest {

    @Test
    public void construct1() {
        //Given
        String trainArgs = "maxIterations=300,nnArchitecture=?:B->TANH->f1.5:B->TANH->?,nnMethod=myMethodIndeed,nnTrainType=myTrainingTypeIndeed,INIT_UPDATE=0.1,MAX_STEP=50";

        //When
        int inputWidth = 10;
        int outputWidth = 1;
        int nnLagWindowSize = 42;
        int nnLeadWindowSize = 1;
        NetworkDescription networkDescription = new NetworkDescription(trainArgs, inputWidth, outputWidth, nnLagWindowSize, nnLeadWindowSize);


        //Then
        int hiddenCount = (int) (((double)(inputWidth*nnLagWindowSize+outputWidth*nnLeadWindowSize))*1.5);
        assertEquals("?:B->TANH->"+hiddenCount+":B->TANH->?", networkDescription.getModelArchitecture());
        assertEquals("myMethodIndeed", networkDescription.getMethodType());
        assertEquals("myTrainingTypeIndeed", networkDescription.getTrainingType());
        assertEquals(trainArgs, networkDescription.getTrainingArgs());

    }

    @Test
    public void construct2() {
        //Given
        String trainArgs = "maxIterations=300,nnArchitecture=?:B->TANH->f1.5->TANH->?,nnMethod=myMethodIndeed,nnTrainType=myTrainingTypeIndeed,INIT_UPDATE=0.1,MAX_STEP=50";

        //When
        int inputWidth = 10;
        int outputWidth = 1;
        int nnLagWindowSize = 42;
        int nnLeadWindowSize = 1;
        NetworkDescription networkDescription = new NetworkDescription(trainArgs, inputWidth, outputWidth, nnLagWindowSize, nnLeadWindowSize);


        //Then
        int hiddenCount = (int) (((double)(inputWidth*nnLagWindowSize+outputWidth*nnLeadWindowSize))*1.5);
        assertEquals("?:B->TANH->"+hiddenCount+"->TANH->?", networkDescription.getModelArchitecture());
        assertEquals("myMethodIndeed", networkDescription.getMethodType());
        assertEquals("myTrainingTypeIndeed", networkDescription.getTrainingType());
        assertEquals(trainArgs, networkDescription.getTrainingArgs());

    }

    @Test
    public void construct3() {
        //Given
        String trainArgs = "maxIterations=300,nnArchitecture=?:B->TANH->10->TANH->?,nnMethod=myMethodIndeed,nnTrainType=myTrainingTypeIndeed,INIT_UPDATE=0.1,MAX_STEP=50";

        //When
        int inputWidth = 10;
        int outputWidth = 1;
        int nnLagWindowSize = 42;
        int nnLeadWindowSize = 1;
        NetworkDescription networkDescription = new NetworkDescription(trainArgs, inputWidth, outputWidth, nnLagWindowSize, nnLeadWindowSize);


        //Then
        int hiddenCount = 10;
        assertEquals("?:B->TANH->"+hiddenCount+"->TANH->?", networkDescription.getModelArchitecture());
        assertEquals("myMethodIndeed", networkDescription.getMethodType());
        assertEquals("myTrainingTypeIndeed", networkDescription.getTrainingType());
        assertEquals(trainArgs, networkDescription.getTrainingArgs());

    }

    @Test
    public void construct4() {
        //Given
        String trainArgs = "maxIterations=300,nnArchitecture=?:B->TANH->f2.5->TANH->f1.5->TANH->20->TANH->?,nnMethod=myMethodIndeed,nnTrainType=myTrainingTypeIndeed,INIT_UPDATE=0.1,MAX_STEP=50";

        //When
        int inputWidth = 10;
        int outputWidth = 1;
        int nnLagWindowSize = 42;
        int nnLeadWindowSize = 1;
        NetworkDescription networkDescription = new NetworkDescription(trainArgs, inputWidth, outputWidth, nnLagWindowSize, nnLeadWindowSize);


        //Then
        int hiddenCount1 = (int) (((double)(inputWidth*nnLagWindowSize+outputWidth*nnLeadWindowSize))*2.5);
        int hiddenCount2 = (int) (((double)(inputWidth*nnLagWindowSize+outputWidth*nnLeadWindowSize))*1.5);
        int hiddenCount3 = 20;
        assertEquals("?:B->TANH->"+hiddenCount1+"->TANH->"+hiddenCount2+"->TANH->"+hiddenCount3+"->TANH->?", networkDescription.getModelArchitecture());
        assertEquals("myMethodIndeed", networkDescription.getMethodType());
        assertEquals("myTrainingTypeIndeed", networkDescription.getTrainingType());
        assertEquals(trainArgs, networkDescription.getTrainingArgs());

    }
    
    @Test
    public void construct5() {
        //Given
        String trainArgs = "maxIterations=300";

        //When
        int inputWidth = 10;
        int outputWidth = 1;
        int nnLagWindowSize = 42;
        int nnLeadWindowSize = 1;
        NetworkDescription networkDescription = new NetworkDescription(trainArgs, inputWidth, outputWidth, nnLagWindowSize, nnLeadWindowSize);

        //Then
        int hiddenCount = (int) (((double)(inputWidth*nnLagWindowSize+outputWidth*nnLeadWindowSize))*1.5);
        assertEquals("?:B->TANH->"+hiddenCount+":B->TANH->?", networkDescription.getModelArchitecture());
        assertEquals("feedforward", networkDescription.getMethodType());
        assertNull(networkDescription.getTrainingType());
        assertEquals(trainArgs, networkDescription.getTrainingArgs());

    }


}
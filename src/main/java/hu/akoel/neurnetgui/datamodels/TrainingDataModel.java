package hu.akoel.neurnetgui.datamodels;

import hu.akoel.neurnetgui.verifiers.MutableDouble;
import hu.akoel.neurnetgui.verifiers.MutableInteger;
import hu.akoel.neurnetgui.verifiers.MutableString;

public class TrainingDataModel {
	public static final int DEFAULT_MAXLOOP = 10000000;
	public static final String DEFAULT_MAXMSE = "0.00002";
	public static final double DEFAULT_ALPHA = 0.3;
	public static final double DEFAULT_MOMENTUM = 0.0;
	
	public static final int DEFAULT_HANDLEERRORCOUNTER = 20000;
	
	public MutableInteger maxTrainingLoop = new MutableInteger( DEFAULT_MAXLOOP );
	public MutableString maxMeanSquaredError = new MutableString( DEFAULT_MAXMSE );
	public MutableDouble learningRate = new MutableDouble( DEFAULT_ALPHA );
	public MutableDouble momentum = new MutableDouble( DEFAULT_MOMENTUM );	
	
	public MutableInteger loopsAfterHandleError = new MutableInteger( DEFAULT_HANDLEERRORCOUNTER );
}

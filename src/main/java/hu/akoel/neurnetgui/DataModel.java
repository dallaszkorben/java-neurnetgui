package hu.akoel.neurnetgui;

public class DataModel {
	public static final int DEFAULT_MAXLOOP = 1000000;
	public static final double DEFAULT_MAXMSE = 0.00003;
	public static final double DEFAULT_ALPHA = 0.3;
	public static final double DEFAULT_MOMENTUM = 0.0;
	
	public MutableInteger maxTrainingLoop = new MutableInteger( DEFAULT_MAXLOOP );
	public MutableDouble maxMeanSquaredError = new MutableDouble( DEFAULT_MAXMSE );
	public MutableDouble learningRate = new MutableDouble( DEFAULT_ALPHA );
	public MutableDouble momentum = new MutableDouble( DEFAULT_MOMENTUM );	
}

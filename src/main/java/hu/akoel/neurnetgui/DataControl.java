package hu.akoel.neurnetgui;

public class DataControl {
	public static final int DEFAULT_MAXLOOP = 1000;
	public static final double DEFAULT_MAXMSE = 0.001;	
	
	public MutableInteger maxTrainingLoop = new MutableInteger( DEFAULT_MAXLOOP );
	public MutableDouble maxMeanSquaredError = new MutableDouble( DEFAULT_MAXMSE );
}

package hu.akoel.neurnetgui.datamodels;

import hu.akoel.neurnetgui.verifiers.MutableInteger;

public class ConstructionDataModel {
	public static final int DEFAULT_ACTUAL_LAYER = 0;
	
	public MutableInteger actualLayer = new MutableInteger( DEFAULT_ACTUAL_LAYER );
	
}

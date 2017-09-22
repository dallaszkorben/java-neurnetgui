package hu.akoel.neurnetgui;

public class NeuronDescriptor {

	public static enum NeuronType{
		NORMAL
	}
	
	public static enum TransferFunction{
		SIGMOID
	}
	
	NeuronType neuronType;
	TransferFunction transferFunction;
	
	public NeuronDescriptor( NeuronType neuronType, TransferFunction transferFunction ){
		this.neuronType = neuronType;
		this.transferFunction = transferFunction;
	}
}

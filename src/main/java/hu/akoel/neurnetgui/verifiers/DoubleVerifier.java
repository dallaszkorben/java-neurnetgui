package hu.akoel.neurnetgui.verifiers;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

public class DoubleVerifier extends InputVerifier{
	private MutableDouble dataModelDouble;
	private Double minimumValue;
	private Double maximumValue;

	public DoubleVerifier(MutableDouble dataModelDouble){
		this.dataModelDouble = dataModelDouble;
	}

	public DoubleVerifier(MutableDouble dataModelDouble, Double minimumValue ){
		this.dataModelDouble = dataModelDouble;
		this.minimumValue = minimumValue;
	}
	
	public DoubleVerifier(MutableDouble dataModelDouble, Double minimumValue, Double maximumValue){
		this.dataModelDouble = dataModelDouble;
		this.minimumValue = minimumValue;
		this.maximumValue = maximumValue;
	}
	
	@Override
	public boolean verify(JComponent inputComponent) {
		String originalString = String.valueOf( dataModelDouble.getValue() );
		JTextField inputField = (JTextField)inputComponent;
		String possibleString = inputField.getText();
		Double possibleDouble;
		try{
			possibleDouble = Double.valueOf( possibleString );
			if( null != minimumValue && possibleDouble < minimumValue ){
				inputField.setText( originalString );
				return false;
			}else if( null != maximumValue && possibleDouble > maximumValue ){
				inputField.setText( originalString );
				return false;
			}			
		}catch( NumberFormatException e ){
			inputField.setText( originalString );
			return false;
		}
		dataModelDouble.setValue( possibleDouble );
		return true;	
	}	
}

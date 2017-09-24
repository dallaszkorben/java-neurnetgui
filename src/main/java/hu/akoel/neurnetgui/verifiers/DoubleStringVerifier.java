package hu.akoel.neurnetgui.verifiers;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

public class DoubleStringVerifier extends InputVerifier{
	private MutableString dataModelDoubleString;
	private Double minimumValue;
	private Double maximumValue;
	
	public DoubleStringVerifier(MutableString dataModelDouble, Double minimumValue, Double maximumValue){
		this.dataModelDoubleString = dataModelDouble;
		this.minimumValue = minimumValue;
		this.maximumValue = maximumValue;
	}
	
	@Override
	public boolean verify(JComponent inputComponent) {
		String originalString = String.valueOf( dataModelDoubleString.getValue() );
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
		dataModelDoubleString.setValue( possibleString );
		return true;	
	}	
}
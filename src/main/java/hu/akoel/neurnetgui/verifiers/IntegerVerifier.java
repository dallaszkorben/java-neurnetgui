package hu.akoel.neurnetgui.verifiers;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

public class IntegerVerifier extends InputVerifier{
	private MutableInteger dataModelInteger;
	private Integer minimumValue;
	private Integer maximumValue;

	public IntegerVerifier(MutableInteger dataModelInteger){
		this.dataModelInteger = dataModelInteger;
	}

	public IntegerVerifier( MutableInteger dataModelInteger, Integer minimumValue ){
		this.dataModelInteger = dataModelInteger;
		this.minimumValue = minimumValue;
	}

	public IntegerVerifier( MutableInteger dataModelInteger, Integer minimumValue, Integer maximumValue ){
		this.dataModelInteger = dataModelInteger;
		this.minimumValue = minimumValue;
		this.maximumValue = maximumValue;
	}
	
	@Override
	public boolean verify(JComponent inputComponent) {		
		String originalString = String.valueOf( dataModelInteger.getValue() );
		JTextField inputField = (JTextField)inputComponent;
		String possibleString = inputField.getText();
		Integer possibleInt;
		try{
			possibleInt = Integer.valueOf( possibleString );
			if( null != minimumValue && possibleInt < minimumValue ){
				inputField.setText( originalString );
				return false;
			}else if( null != maximumValue && possibleInt > maximumValue ){
				inputField.setText( originalString );
				return false;
			}			
		}catch( NumberFormatException e ){
			inputField.setText( originalString );
			return false;
		}
		dataModelInteger.setValue( possibleInt );
		return true;
	}
	
}
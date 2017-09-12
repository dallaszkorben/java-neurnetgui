package hu.akoel.neurnetgui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import hu.akoel.neurnet.handlers.DataHandler;
import hu.akoel.neurnet.listeners.IActivityListener;
import hu.akoel.neurnet.listeners.ICycleListener;
import hu.akoel.neurnet.network.Network;
import hu.akoel.neurnet.resultiterator.IResultIterator;


public class TrainingControlPanel extends JPanel{
	private static final long serialVersionUID = 8909396748536386035L;
	
	private DataControl dataControl;
	private Network network;
	private DataHandler trainingDataHandler;
	
	private static final int LOOP_FIELD_COLUMNS = 9;
	private static final int MSE_FIELD_COLUMNS = 7;

	public JTextField actualMSEField;
	public JTextField actualLoopField;

	public TrainingControlPanel(Network network, DataHandler trainingDataHandler, DataControl dataControl){
		super();
		
		this.network = network;
		this.trainingDataHandler = trainingDataHandler;
		this.dataControl = dataControl;
		
		this.setBorder( BorderFactory.createLoweredBevelBorder());
		this.setLayout( new GridBagLayout());
		GridBagConstraints controlConstraints = new GridBagConstraints();
		

		//
		// Define fields
		//
		
		// Max Loop
		JLabel maxLoopLabel = new JLabel( Common.getTranslated("control.label.maxtrainingloop") + ":");
		JTextField maxLoopField = new JTextField();
		maxLoopField.setEditable( true );
		maxLoopField.setColumns(LOOP_FIELD_COLUMNS);
		maxLoopField.setText( String.valueOf( dataControl.DEFAULT_MAXLOOP ) );		
		maxLoopField.setInputVerifier( new IntegerVerifier( dataControl.maxTrainingLoop, 1 ) );
		
		// Mean Squared Error (MSE)
		JLabel maxMSELabel = new JLabel( Common.getTranslated("control.label.maxmse") + ":");
		JTextField maxMSEField = new JTextField();
		maxMSEField.setEditable( true );
		maxMSEField.setColumns(MSE_FIELD_COLUMNS);
		maxMSEField.setText( String.valueOf( dataControl.DEFAULT_MAXMSE ) );		
		maxMSEField.setInputVerifier( new DoubleVerifier( dataControl.maxMeanSquaredError, 0.0 ) );

		// actual Loop
		JLabel actualLoopLabel = new JLabel( Common.getTranslated("control.label.actualtrainingloop") + ":");
		actualLoopField = new JTextField();
		actualLoopField.setEditable( false );
		actualLoopField.setColumns(LOOP_FIELD_COLUMNS);

		// Mean Squared Error (MSE)
		JLabel actualMSELabel = new JLabel( Common.getTranslated("control.label.actualmse") + ":");
		actualMSEField = new JTextField();
		actualMSEField.setEditable( false );
		actualMSEField.setColumns(MSE_FIELD_COLUMNS);
		
		// Start button
		JButton startButton = new JButton( Common.getTranslated("control.button.start") );
		startButton.setBackground( Color.green );
		
		// Stop button
		JButton stopButton = new JButton( Common.getTranslated("control.button.stop") );
		stopButton.setBackground( Color.red );
		stopButton.setEnabled(false);
		
		stopButton.addActionListener( new StopButtonListener(network) );
		startButton.addActionListener( new StartButtonListener(network, trainingDataHandler, dataControl, startButton, stopButton) );

		
		//
		// Place fields
		//
		
		//Max loop
		int row = 0;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 10;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( maxLoopLabel, controlConstraints );
		
		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( maxLoopField, controlConstraints );
		
		// Mean Squared Error (MSE)
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 10;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( maxMSELabel, controlConstraints );
		
		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( maxMSEField, controlConstraints );
		
		// Actual loop
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 10;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( actualLoopLabel, controlConstraints );
		
		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( actualLoopField, controlConstraints );

		// Actual MSE
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 10;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( actualMSELabel, controlConstraints );
		
		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( actualMSEField, controlConstraints );

		// Start button
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 0;
		controlConstraints.gridwidth = 2;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( startButton, controlConstraints );
		
		// Stop button
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 0;
		controlConstraints.gridwidth = 2;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( stopButton, controlConstraints );
		
		
		
		
		// Filler
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 10;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 1;
		controlConstraints.fill = GridBagConstraints.VERTICAL;
		this.add(new JLabel(), controlConstraints );
		
		this.network.setTrainingCycleListener( new TrainingLoopListener( dataControl, actualLoopField, actualMSEField ) );
		this.network.setActivityListener( new TrainingActivitiListener( startButton, stopButton ) );
		
	}
	
	
	
}



class StartButtonListener implements ActionListener {
	private Network network;
	private DataHandler trainingDataHandler;
	private DataControl dataControl;
	private JButton startButton;
	private JButton stopButton;
	public StartButtonListener(Network network, DataHandler trainingDataHandler, DataControl dataControl, JButton startButton, JButton stopButton ){
		this.network = network;
		this.trainingDataHandler = trainingDataHandler;
		this.dataControl = dataControl;
		this.startButton = startButton;
		this.stopButton = stopButton;
	}
	
	public void actionPerformed(ActionEvent e) {
		startButton.setEnabled( false );
		stopButton.setEnabled(true);
		network.setMaxTrainingLoop( dataControl.maxTrainingLoop.getValue() );
		network.setMaxTotalMeanSquareError( dataControl.maxMeanSquaredError.getValue());
		
		StartTrainingRunnable startingRunnable = new StartTrainingRunnable(network, trainingDataHandler);
		startingRunnable.start();
		
		//SwingUtilities.invokeLater(new StartTrainingRunnable(network, trainingDataHandler) );
	}
}

class StopButtonListener implements ActionListener {
	private Network network;

	public StopButtonListener( Network network ){
		this.network = network;
	}
	
	public void actionPerformed(ActionEvent e) {
		network.stopTraining();
	}	
}

class StartTrainingRunnable extends Thread {
	private Network network;
	private DataHandler trainingDataHandler;
	public StartTrainingRunnable(Network network, DataHandler trainingDataHandler){
		this.network = network;
		this.trainingDataHandler = trainingDataHandler;
	}
	public void run() {
		network.executeTraining(false, trainingDataHandler);
	}
}

/**
 * This listener get a trigger after every loop
 * to print out the values
 * 
 * @author akoel
 *
 */
class TrainingLoopListener implements ICycleListener{
	private DataControl dataControl;
	private JTextField actualLoop;
	private JTextField actualMSE;
	public TrainingLoopListener( DataControl dataControl, JTextField actualLoop, JTextField actualMSE ){
		this.dataControl = dataControl;
		this.actualLoop = actualLoop;
		this.actualMSE = actualMSE;
	}
	
	public void handlerError(int cycleCounter, double totalMeanSquareError, ArrayList<IResultIterator> resultIteratorArray) {
		if( cycleCounter % 100000 == 0 ){
			this.actualLoop.setText( String.valueOf( cycleCounter ) );
			String stringFormat = String.valueOf( dataControl.maxMeanSquaredError.getValue() );
			stringFormat = "#0.00" + new String(new char[stringFormat.length()]).replace('\0', '0');			
			this.actualMSE.setText( String.valueOf( Common.getFormattedDecimal( totalMeanSquareError, stringFormat ) ) );
		}
	}
}

/**
 * This listener get a trigger when the 
 * first loop started and when the
 * last loop finished
 * 
 * @author akoel
 *
 */
class TrainingActivitiListener implements IActivityListener{
	private JButton startButton;
	private JButton stopButton;
	public TrainingActivitiListener( JButton startButton, JButton stopButton ){
		this.startButton = startButton;
		this.stopButton = stopButton;				
	}
	public void started() {		
	}
	public void stopped() {
		stopButton.setEnabled( false );
		startButton.setEnabled( true );		
	}
}




class DoubleVerifier extends InputVerifier{
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


class IntegerVerifier extends InputVerifier{
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
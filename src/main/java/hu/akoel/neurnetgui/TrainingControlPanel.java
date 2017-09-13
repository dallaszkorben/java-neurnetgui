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
	
	private DataModel dataModel;
	private Network network;
	private DataHandler trainingDataHandler;
	
	private static final int LOOP_FIELD_COLUMNS = 9;
	private static final int MSE_FIELD_COLUMNS = 7;
	private static final int LEARNINGRATE_FIELD_COLUMNS = 6;
	private static final int MOMENTUM_FIELD_COLUMNS = 6;

	public JTextField momentumField;
	public JTextField learningRateField;
	public JTextField actualMSEField;
	public JTextField actualLoopField;

	public TrainingControlPanel(Network network, DataHandler trainingDataHandler, DataModel dataModel){
		super();
		
		this.network = network;
		this.trainingDataHandler = trainingDataHandler;
		this.dataModel = dataModel;
		
		this.setBorder( BorderFactory.createLoweredBevelBorder());
		this.setLayout( new GridBagLayout());
		GridBagConstraints controlConstraints = new GridBagConstraints();
		

		//
		// Define fields
		//
		
		// Learning Rate
		JLabel learningRateLabel = new JLabel( Common.getTranslated("control.label.learningrate") + ":");
		JTextField learningRateField = new JTextField();
		learningRateField.setEditable( true );
		learningRateField.setColumns(LEARNINGRATE_FIELD_COLUMNS);
		learningRateField.setText( String.valueOf( dataModel.learningRate.getValue() ) );		
		learningRateField.setInputVerifier( new DoubleVerifier( dataModel.learningRate, 0.0, 1.0 ) );
		
		// Momentum
		JLabel momentumLabel = new JLabel( Common.getTranslated("control.label.momentum") + ":");
		JTextField momentumField = new JTextField();
		momentumField.setEditable( true );
		momentumField.setColumns(MOMENTUM_FIELD_COLUMNS);
		momentumField.setText( String.valueOf( dataModel.momentum .getValue()) );		
		momentumField.setInputVerifier( new DoubleVerifier( dataModel.momentum, 0.0, 1.0 ) );
		
		// Max Loop
		JLabel maxLoopLabel = new JLabel( Common.getTranslated("control.label.maxtrainingloop") + ":");
		JTextField maxLoopField = new JTextField();
		maxLoopField.setEditable( true );
		maxLoopField.setColumns(LOOP_FIELD_COLUMNS);
		maxLoopField.setText( String.valueOf( dataModel.maxTrainingLoop.getValue() ) );		
		maxLoopField.setInputVerifier( new IntegerVerifier( dataModel.maxTrainingLoop, 1 ) );
		
		// Mean Squared Error (MSE)
		JLabel maxMSELabel = new JLabel( Common.getTranslated("control.label.maxmse") + ":");
		JTextField maxMSEField = new JTextField();
		maxMSEField.setEditable( true );
		maxMSEField.setColumns(MSE_FIELD_COLUMNS);		
		String stringFormat = String.valueOf( dataModel.maxMeanSquaredError.getValue() );
		stringFormat = "#0." + new String(new char[stringFormat.length()]).replace('\0', '0');		
		maxMSEField.setText( String.valueOf( Common.getFormattedDecimal( dataModel.maxMeanSquaredError.getValue(), stringFormat ) ) );		
		maxMSEField.setInputVerifier( new DoubleVerifier( dataModel.maxMeanSquaredError, 0.0 ) );

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
		startButton.addActionListener( new StartButtonListener(network, trainingDataHandler, dataModel, startButton, stopButton) );

		
		//
		// Place fields
		//
		//Alpha
		int row = 0;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 10;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( learningRateLabel, controlConstraints );
		
		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( learningRateField, controlConstraints );

		//Momentum
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 10;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( momentumLabel, controlConstraints );
		
		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( momentumField, controlConstraints );
		
		//Max loop
		row++;
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
		
		this.network.setTrainingCycleListener( new TrainingLoopListener( dataModel, actualLoopField, actualMSEField ) );
		this.network.setActivityListener( new TrainingActivitiListener( startButton, stopButton ) );
		
	}
	
	
	
}



class StartButtonListener implements ActionListener {
	private Network network;
	private DataHandler trainingDataHandler;
	private DataModel dataModel;
	private JButton startButton;
	private JButton stopButton;
	public StartButtonListener(Network network, DataHandler trainingDataHandler, DataModel dataModel, JButton startButton, JButton stopButton ){
		this.network = network;
		this.trainingDataHandler = trainingDataHandler;
		this.dataModel = dataModel;
		this.startButton = startButton;
		this.stopButton = stopButton;
	}
	
	public void actionPerformed(ActionEvent e) {
		startButton.setEnabled( false );
		stopButton.setEnabled(true);
		network.setLearningRate( dataModel.learningRate.getValue());
		network.setMomentum( dataModel.momentum.getValue() );
		network.setMaxTrainingLoop( dataModel.maxTrainingLoop.getValue() );
		network.setMaxTotalMeanSquareError( dataModel.maxMeanSquaredError.getValue());
		
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
	private DataModel dataControl;
	private JTextField actualLoop;
	private JTextField actualMSE;
	public TrainingLoopListener( DataModel dataModel, JTextField actualLoop, JTextField actualMSE ){
		this.dataControl = dataModel;
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
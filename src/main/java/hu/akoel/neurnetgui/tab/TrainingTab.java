package hu.akoel.neurnetgui.tab;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.axis.Axis;
import hu.akoel.mgu.axis.Axis.AxisPosition;
import hu.akoel.mgu.grid.Grid;
import hu.akoel.mgu.values.DeltaValue;
import hu.akoel.mgu.values.PixelPerUnitValue;
import hu.akoel.mgu.values.PositionValue;
import hu.akoel.mgu.values.SizeValue;
import hu.akoel.mgu.values.TranslateValue;
import hu.akoel.mgu.values.ZoomRateValue;
import hu.akoel.neurnet.handlers.DataHandler;
import hu.akoel.neurnet.listeners.IActivityListener;
import hu.akoel.neurnet.listeners.ILoopListener;
import hu.akoel.neurnet.network.Network;
import hu.akoel.neurnet.resultiterator.IResultIterator;
import hu.akoel.neurnetgui.DataModel;
import hu.akoel.neurnetgui.MutableDouble;
import hu.akoel.neurnetgui.MutableInteger;
import hu.akoel.neurnetgui.MutableString;
import hu.akoel.neurnetgui.accessories.Common;

public class TrainingTab extends JPanel{
	private static final long serialVersionUID = 8909396748536386035L;
	
	private DataModel dataModel;
	private Network network;
	private DataHandler trainingDataHandler;
	
	private static final int LOOP_FIELD_COLUMNS = 9;
	private static final int MSE_FIELD_COLUMNS = 7;
	private static final int LEARNINGRATE_FIELD_COLUMNS = 6;
	private static final int MOMENTUM_FIELD_COLUMNS = 6;
	private static final int LOOPSAFTERHANDLEERROR_FIELD_COLUMNS = 6;
	
	private ArrayList<ErrorGraphDataPairs> errorGraphDataList = new ArrayList<ErrorGraphDataPairs>();
	
	public JTextField momentumField;
	public JTextField learningRateField;
	public JTextField actualMSEField;
	public JTextField actualLoopField;
	public JTextField loopsAfterHandleErrorField;
	public MCanvas errorCanvas;

	public TrainingTab(Network network, DataHandler trainingDataHandler, DataModel dataModel){
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
		JLabel learningRateLabel = new JLabel( Common.getTranslated("training.label.learningrate") + ":");
		JTextField learningRateField = new JTextField();
		learningRateField.setEditable( true );
		learningRateField.setColumns(LEARNINGRATE_FIELD_COLUMNS);
		learningRateField.setText( String.valueOf( dataModel.learningRate.getValue() ) );		
		learningRateField.setInputVerifier( new DoubleVerifier( dataModel.learningRate, 0.0, 1.0 ) );
		
		// Momentum
		JLabel momentumLabel = new JLabel( Common.getTranslated("training.label.momentum") + ":");
		JTextField momentumField = new JTextField();
		momentumField.setEditable( true );
		momentumField.setColumns(MOMENTUM_FIELD_COLUMNS);
		momentumField.setText( String.valueOf( dataModel.momentum .getValue()) );		
		momentumField.setInputVerifier( new DoubleVerifier( dataModel.momentum, 0.0, 1.0 ) );
		
		// Max Loop
		JLabel maxLoopLabel = new JLabel( Common.getTranslated("training.label.maxtrainingloop") + ":");
		JTextField maxLoopField = new JTextField();
		maxLoopField.setEditable( true );
		maxLoopField.setColumns(LOOP_FIELD_COLUMNS);
		maxLoopField.setText( String.valueOf( dataModel.maxTrainingLoop.getValue() ) );		
		maxLoopField.setInputVerifier( new IntegerVerifier( dataModel.maxTrainingLoop, 1 ) );
		
		// Mean Squared Error (MSE)
		JLabel maxMSELabel = new JLabel( Common.getTranslated("training.label.maxmse") + ":");
		JTextField maxMSEField = new JTextField();
		maxMSEField.setEditable( true );
		maxMSEField.setColumns(MSE_FIELD_COLUMNS);		
		maxMSEField.setText( dataModel.maxMeanSquaredError.getValue() );
		maxMSEField.setInputVerifier( new DoubleStringVerifier( dataModel.maxMeanSquaredError, 0.0, 0.01 ) );

		// Loop After handle Error
		JLabel loopsAfterHandleErrorLabel = new JLabel( Common.getTranslated("training.label.loopsafterhandleerror") + ":");
		loopsAfterHandleErrorField = new JTextField();
		loopsAfterHandleErrorField.setEditable( true );
		loopsAfterHandleErrorField.setColumns(LOOPSAFTERHANDLEERROR_FIELD_COLUMNS);
		loopsAfterHandleErrorField.setText( String.valueOf( dataModel.loopsAfterHandleError.getValue() ) );
		loopsAfterHandleErrorField.setInputVerifier( new IntegerVerifier( dataModel.loopsAfterHandleError, 100, dataModel.maxTrainingLoop.getValue() ) );

		// actual Loop
		JLabel actualLoopLabel = new JLabel( Common.getTranslated("training.label.actualtrainingloop") + ":");
		actualLoopField = new JTextField();
		actualLoopField.setEditable( false );
		actualLoopField.setEnabled( false );
		actualLoopField.setColumns(LOOP_FIELD_COLUMNS);

		// Mean Squared Error (MSE)
		JLabel actualMSELabel = new JLabel( Common.getTranslated("training.label.actualmse") + ":");
		actualMSEField = new JTextField();
		actualMSEField.setEditable( false );
		actualMSEField.setEnabled( false );
		actualMSEField.setColumns(MSE_FIELD_COLUMNS);

		// Start button
		JButton startButton = new JButton( Common.getTranslated("training.button.start") );
		startButton.setBackground( Color.green );
		
		// Stop button
		JButton stopButton = new JButton( Common.getTranslated("training.button.stop") );
		stopButton.setBackground( Color.red );
		stopButton.setEnabled( false );

		// Reset button
		JButton resetWeightsButton = new JButton( Common.getTranslated("training.button.resetweights") );
		resetWeightsButton.setBackground( Color.yellow );
		resetWeightsButton.setEnabled( true );
		
		stopButton.addActionListener( new StopButtonListener(network) );
		startButton.addActionListener( new StartButtonListener(network, trainingDataHandler, dataModel, errorGraphDataList, startButton, stopButton, resetWeightsButton ) );
		resetWeightsButton.addActionListener( new ResetWeightsButtonListener(network)); 

		// Error graph		
		errorCanvas = ErrorGraph.getCanvas( dataModel.loopsAfterHandleError.getValue(), Double.valueOf( dataModel.maxMeanSquaredError.getValue() ) );
		errorCanvas.addPainterListenerToHighest( new ErrorGraphPainterListener(network, errorGraphDataList), MCanvas.Level.ABOVE);
		
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
		controlConstraints.weightx = 0;
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

		// Loop After handle Error
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 10;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( loopsAfterHandleErrorLabel, controlConstraints );
		
		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( loopsAfterHandleErrorField, controlConstraints );

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
		
		// Reset Weights button
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 0;
		controlConstraints.gridwidth = 2;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( resetWeightsButton, controlConstraints );
		
		// Graph
		
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 0;
		controlConstraints.gridwidth = 2;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weightx = 1;
		controlConstraints.weighty = 1;
		controlConstraints.fill = GridBagConstraints.BOTH;
		this.add( errorCanvas, controlConstraints );
		
		
		// Filler
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 10;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 1;
		controlConstraints.fill = GridBagConstraints.VERTICAL;
		//this.add(new JLabel(), controlConstraints );
		
		this.network.setTrainingLoopListener( new TrainingLoopListener( dataModel, actualLoopField, actualMSEField, errorCanvas, errorGraphDataList ) );
		this.network.setActivityListener( new TrainingActivitiListener( startButton, stopButton, resetWeightsButton ) );
		
	}	
}



class ErrorGraphPainterListener implements PainterListener{
	private ArrayList<ErrorGraphDataPairs> errorGraphDataList;
	private Network network;
	
	public ErrorGraphPainterListener(Network network, ArrayList<ErrorGraphDataPairs> errorGraphDataList){
		this.network = network;
		this.errorGraphDataList = errorGraphDataList;
	}
	
	public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
		Iterator<ErrorGraphDataPairs> graphListIterator = errorGraphDataList.iterator();

		g2.setColor( Color.red );
		g2.setStroke( new BasicStroke(1));
		
		PositionValue previous = new PositionValue(0, 0);
		if( graphListIterator.hasNext() ){
			previous.setX( errorGraphDataList.get(0).getLoop() );
			previous.setY( errorGraphDataList.get(0).getValue() );
		}
		
		while( graphListIterator.hasNext() ){
			ErrorGraphDataPairs errorDataPairs = graphListIterator.next();			
			double y = errorDataPairs.getValue();
			double x = errorDataPairs.getLoop();
			g2.drawLine(previous.getX(), previous.getY(), x, y);
			previous.setX(x);
			previous.setY(y);
		}
		
		// Transform the Canvas to have the pencil to the right-bottom(1/4 height) position
		if( !network.isTrainingStopped() && !errorGraphDataList.isEmpty() ){
			double lastDataValue = errorGraphDataList.get( errorGraphDataList.size() - 1 ).getValue();
			double neededYDifference = canvas.getWorldYLengthByPixel( canvas.getHeight() ) / 4;
			double yOffset = neededYDifference -lastDataValue;
			
			double lastDataPosition = errorGraphDataList.get( errorGraphDataList.size() - 1 ).getLoop();
			int xDiffInPixel = canvas.getWidth() - 15;
			double neededXDifference = canvas.getWorldXLengthByPixel( xDiffInPixel );
			double xOffset = neededXDifference - lastDataPosition;			

			canvas.setWorldTranslateX( xOffset );
			canvas.setWorldTranslateY( yOffset );
		}
	}

	public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {
		//canvas.moveLeft( 1 );
	}	
}

class ErrorGraph{
	private double DEL = 50;
	
	private static MCanvas myCanvas = null;
	int smallestVisibleLoop;
	double smallestVisibleError;	
	
	private ErrorGraph(int handleErrorCounter, double maxMeanSquaredError){
		this.smallestVisibleLoop = handleErrorCounter;
		this. smallestVisibleError = maxMeanSquaredError / DEL;
		//String stringFormat = String.valueOf( maxMeanSquaredError );		 
	}
		
	private void generateCanvas(){		
		
		Border border = BorderFactory.createLoweredBevelBorder();
		Color color = Color.black;
		PossiblePixelPerUnits pppU = new PossiblePixelPerUnits( 
				new PixelPerUnitValue(1.0/smallestVisibleLoop, 1.0/smallestVisibleError),				                                                                      
				new ZoomRateValue(1.2, 1.2));		
		
		TranslateValue positionToMiddle = null;//new TranslateValue(3000000, 0.00005);
		
		//SizeValue boundSize = new SizeValue(0.0, 0.0, 100000000.0, 1.0);
		myCanvas = new MCanvas(border, color, pppU, positionToMiddle );

		//Grid
		Color gridColor = new Color(0, 55, 0);
		int gridWidth = 1;
		//DeltaValue gridDelta = new DeltaValue( 1000000.0, 0.00001 );
		DeltaValue gridDelta = new DeltaValue( smallestVisibleLoop * DEL, smallestVisibleError * DEL );
		Grid.PainterPosition gridPosition = Grid.PainterPosition.DEEPEST;
		Grid.Type gridType = Grid.Type.SOLID;
		Grid myGrid;
		myGrid = new Grid( myCanvas, gridType, gridColor, gridWidth, gridPosition, gridDelta );
		myGrid.turnOn();

		//Axis		
		Color axiscolor = new Color(0, 100, 0);
		int axisWidthInPixel = 1;
		AxisPosition axisPosition = Axis.AxisPosition.AT_LEFT;
		Axis.PainterPosition painterPosition = Axis.PainterPosition.HIGHEST;
		Axis axis = new Axis(myCanvas, axisPosition, axiscolor, axisWidthInPixel, painterPosition);
		axis.turnOn();
		
		myCanvas.addPainterListenerToDeepest( new PainterListener() {
			
			public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
				double x0 = 0.0;//canvas.getWorldXByPixel(0);
				double x1 = canvas.getWorldXByPixel( canvas.getWidth() - 1);
				double y0 = 0.0;
				double y1 = canvas.getWorldYByPixel( 0 );
				g2.setColor( Color.green );
				g2.setStroke( new BasicStroke(1));
				g2.drawLine( x0, y0, x1, y0 );
				g2.drawLine( x0, y0, x0, y1 );
			}
			
			public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {					
			}
		}, MCanvas.Level.ABOVE );
		
		myCanvas.repaint();				
	}
	
	public static MCanvas getCanvas( int handleErrorCounter, double maxMeanSquaredError ){
		if( null == myCanvas ){
			(new ErrorGraph( handleErrorCounter, maxMeanSquaredError )).generateCanvas();
		}
		return myCanvas;
	}
}


class StartButtonListener implements ActionListener {
	private Network network;
	private DataHandler trainingDataHandler;
	private DataModel dataModel;
	private JButton startButton;
	private JButton stopButton;
	private JButton resetWeightsButon;
	private ArrayList<ErrorGraphDataPairs> errorGraphDataList;
	
	public StartButtonListener(Network network, DataHandler trainingDataHandler, DataModel dataModel, ArrayList<ErrorGraphDataPairs> errorGraphDataList, JButton startButton, JButton stopButton, JButton resetWeightsButon ){
		this.network = network;
		this.trainingDataHandler = trainingDataHandler;
		this.dataModel = dataModel;
		this.errorGraphDataList = errorGraphDataList;
		this.startButton = startButton;
		this.stopButton = stopButton;
		this.resetWeightsButon = resetWeightsButon;
	}
	
	public void actionPerformed(ActionEvent e) {
		startButton.setEnabled( false );
		stopButton.setEnabled(true);
		resetWeightsButon.setEnabled( false );
		
		network.setLearningRate( dataModel.learningRate.getValue());
		network.setMomentum( dataModel.momentum.getValue() );
		network.setMaxTrainingLoop( dataModel.maxTrainingLoop.getValue() );
		network.setMaxTotalMeanSquareError( Double.valueOf( dataModel.maxMeanSquaredError.getValue() ) );
		
		StartTrainingRunnable startingRunnable = new StartTrainingRunnable(network, trainingDataHandler, errorGraphDataList );
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

class ResetWeightsButtonListener implements ActionListener{

	private Network network;

	public ResetWeightsButtonListener( Network network ){
		this.network = network;
	}
	
	public void actionPerformed(ActionEvent e) {
		network.resetWeights();
	}	
	
}

class StartTrainingRunnable extends Thread {
	private Network network;
	private DataHandler trainingDataHandler;
	private ArrayList<ErrorGraphDataPairs> errorGraphDataList;
	
	public StartTrainingRunnable(Network network, DataHandler trainingDataHandler, ArrayList<ErrorGraphDataPairs> errorGraphDataList ){
		this.network = network;
		this.trainingDataHandler = trainingDataHandler;
		this.errorGraphDataList = errorGraphDataList;
	}
	public void run() {
		if( errorGraphDataList.size() > 0 ){
			((TrainingLoopListener)network.getTrainingLoopListener()).setOffset( errorGraphDataList.get(errorGraphDataList.size() - 1 ).getLoop() );
		}
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
class TrainingLoopListener implements ILoopListener{
	private DataModel dataModel;
	private JTextField actualLoop;
	private JTextField actualMSE;
	private ArrayList<ErrorGraphDataPairs> errorGraphDataList;
	private MCanvas errorCanvas;
	private int offset = 0;
	
	public TrainingLoopListener( DataModel dataModel, JTextField actualLoop, JTextField actualMSE, MCanvas errorCanvas, ArrayList<ErrorGraphDataPairs> errorGraphDataList ){
		this.dataModel = dataModel;
		this.actualLoop = actualLoop;
		this.actualMSE = actualMSE;
		this.errorCanvas = errorCanvas;
		this.errorGraphDataList = errorGraphDataList;
	}
	
	public void setOffset( int offset ){
		this.offset = offset;
	}
	
	public void handlerError(int loopCounter, double totalMeanSquareError, ArrayList<IResultIterator> resultIteratorArray) {
		if( loopCounter % dataModel.loopsAfterHandleError.getValue() == 0 ){
			this.actualLoop.setText( String.valueOf( offset + loopCounter ) );
			//String stringFormat = String.valueOf( offset + dataModel.maxMeanSquaredError.getValue() );
				
			//stringFormat = "#0.00" + new String(new char[stringFormat.length()]).replace('\0', '0');

			String stringFormat =Common.getDecimalFormat(dataModel.maxMeanSquaredError.getValue(), 2); 
			this.actualMSE.setText( String.valueOf( Common.getFormattedDecimal( totalMeanSquareError, stringFormat ) ) );			
			
			errorGraphDataList.add( new ErrorGraphDataPairs( offset + loopCounter, totalMeanSquareError ));

/*			//Canvas transformation to be at right-middle the actual active point
			if( !errorGraphDataList.isEmpty() ){
				double lastDataValue = errorGraphDataList.get( errorGraphDataList.size() - 1 ).getValue();

				double lastDataPosition = errorGraphDataList.get( errorGraphDataList.size() - 1 ).getLoop();
				int lastPixelPosition = errorCanvas.getWidth() - 15;
				double lastWorldPosition = errorCanvas.getWorldXLengthByPixel( lastPixelPosition );
				double difference = lastWorldPosition - lastDataPosition;			

				//errorCanvas.setWorldTranslateX( errorCanvas.getWorldXLengthByPixel( errorCanvas.getWidth() ) - lastDataPosition );
				errorCanvas.setWorldTranslateX( difference );
				errorCanvas.setWorldTranslateY( errorCanvas.getWorldYLengthByPixel( errorCanvas.getHeight() ) / 2 -lastDataValue );
			}
*/			
			errorCanvas.revalidateAndRepaintCoreCanvas();

		}
	}
}

class ErrorGraphDataPairs{
	private double value;
	private int loop;
	
	public ErrorGraphDataPairs( int loop, double value ){
		this.value = value;
		this.loop = loop;
	}
	
	public int getLoop(){
		return loop;
	}
	
	public double getValue(){
		return value;
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
	private JButton resetWeightButton;
	
	public TrainingActivitiListener( JButton startButton, JButton stopButton, JButton resetWeightButton ){
		this.startButton = startButton;
		this.stopButton = stopButton;				
		this.resetWeightButton = resetWeightButton;
	}
	public void started() {		
	}
	public void stopped() {
		stopButton.setEnabled( false );
		startButton.setEnabled( true );	
		resetWeightButton.setEnabled( true );
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

class DoubleStringVerifier extends InputVerifier{
	private MutableString dataModelDoubleString;
	private Double minimumValue;
	private Double maximumValue;

	//public DoubleStringVerifier(MutableString dataModelDouble){
	//	this.dataModelDoubleString = dataModelDouble;
	//}
	
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
package hu.akoel.neurnetgui.tab;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

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
import hu.akoel.mgu.values.TranslateValue;
import hu.akoel.mgu.values.ZoomRateValue;
import hu.akoel.neurnet.handlers.DataHandler;
import hu.akoel.neurnet.layer.Layer;
import hu.akoel.neurnet.listeners.IActivityListener;
import hu.akoel.neurnet.listeners.ILoopListener;
import hu.akoel.neurnet.network.Network;
import hu.akoel.neurnet.neuron.Neuron;
import hu.akoel.neurnet.resultiterator.IResultIterator;
import hu.akoel.neurnetgui.accessories.Common;
import hu.akoel.neurnetgui.datamodels.TrainingDataModel;
import hu.akoel.neurnetgui.networkcanvas.NetworkCanvas;
import hu.akoel.neurnetgui.networkcanvas.NetworkModelChangeListener;
import hu.akoel.neurnetgui.verifiers.IntegerVerifier;
import hu.akoel.neurnetgui.verifiers.DoubleVerifier;
import hu.akoel.neurnetgui.verifiers.DoubleStringVerifier;

interface TrainingTabCommunication{
	public void setNetwork( Network network );
	public Network getNetwork();
	public JButton getStartButton();
	public JButton getStopButton();
	public JButton getResetWeightsButton();	

	public void setTrainingDataSize( String fileName );
	public void setTrainingDataFile( String size );
	
	public NetworkCanvas getNetworkCanvas();
	public TrainingDataModel getTrainingDataModel();
	public ArrayList<ErrorGraphDataPairs> getErrorGraphDataList();
	
	public DataHandler getDataHandler();
	public void setDataHandler( DataHandler dataHandler );
	
}

public class TrainingTab extends JPanel implements TrainingTabCommunication{
	private static final long serialVersionUID = 8909396748536386035L;
	
	private NetworkCanvas networkCanvas;
	private TrainingDataModel dataModel;
	
	private static final int LOOP_FIELD_COLUMNS = 9;
	private static final int MSE_FIELD_COLUMNS = 7;
	private static final int LEARNINGRATE_FIELD_COLUMNS = 6;
	private static final int MOMENTUM_FIELD_COLUMNS = 6;
	private static final int LOOPSAFTERHANDLEERROR_FIELD_COLUMNS = 6;
	
	private ArrayList<ErrorGraphDataPairs> errorGraphDataList = new ArrayList<ErrorGraphDataPairs>();
	
	private DataHandler dataHandler = null; 
	
	private JTextField momentumField;
	private JTextField learningRateField;
	private JTextField actualMSEField;
	private JTextField actualLoopField;
	private JTextField loopsAfterHandleErrorField;
	private JTextField fileField;
	private JTextField sizeField;
	
	public MCanvas errorCanvas;
	
	private JButton startButton;
	private JButton stopButton;
	private JButton resetWeightsButton;
	
	private Network network;

	public TrainingTab( NetworkCanvas networkCanvas, TrainingDataModel dataModel ){
		super();
		
		this.networkCanvas = networkCanvas;
		this.dataModel = dataModel;
		
		this.setBorder( BorderFactory.createLoweredBevelBorder());
		this.setLayout( new GridBagLayout());
		GridBagConstraints controlConstraints = new GridBagConstraints();
		GridBagConstraints trainingDataControlConstraints = new GridBagConstraints();	

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

//
		//Training Data
		JPanel trainingDataContainer = new JPanel();
		TitledBorder trainingDataBorder = new TitledBorder( Common.getTranslated( "training.title.data" ) );
		trainingDataBorder.setTitleJustification( TitledBorder.CENTER);
		trainingDataContainer.setBorder( trainingDataBorder );
		trainingDataContainer.setLayout( new GridBagLayout() );

		//File
		JLabel fileLabel = new JLabel( Common.getTranslated("training.label.file") + ":");
		fileField = new JTextField();
		fileField.setEditable( false );
		fileField.setEnabled( false );
		
		//Size
		JLabel sizeLabel = new JLabel( Common.getTranslated("training.label.size") + ":");
		sizeField = new JTextField();
		sizeField.setEditable( false );
		sizeField.setEnabled( false );
		
		//Select button
		JButton selectButton = new JButton( Common.getTranslated( "training.button.select" ) );
		
		//View button
		JButton viewButton = new JButton( Common.getTranslated( "training.button.view" ) );

//		
		
		// Start button
		startButton = new JButton( Common.getTranslated("training.button.start") );
		startButton.setBackground( Color.green );
		
		// Stop button
		stopButton = new JButton( Common.getTranslated("training.button.stop") );
		stopButton.setBackground( Color.red );
		stopButton.setEnabled( false );

		// Reset button
		resetWeightsButton = new JButton( Common.getTranslated("training.button.resetweights") );
		resetWeightsButton.setBackground( Color.yellow );
		resetWeightsButton.setEnabled( true );

		selectButton.addActionListener( new SelectButtonListener( this ) );
		startButton.addActionListener( new StartButtonListener( this ) );
		stopButton.addActionListener( new StopButtonListener( this ) );
		resetWeightsButton.addActionListener( new ResetWeightsButtonListener( this ));
 
		// Error graph		
		errorCanvas = ErrorGraph.getCanvas( dataModel.loopsAfterHandleError.getValue(), Double.valueOf( dataModel.maxMeanSquaredError.getValue() ) );
		errorCanvas.addPainterListenerToHighest( new ErrorGraphPainterListener( this ), MCanvas.Level.ABOVE);
		
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

		
//		
// --- Training Data section ---
//		
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 10;
		controlConstraints.gridwidth = 2;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( trainingDataContainer, controlConstraints );

		int trainingDataRow = -1;
		
		//File
		trainingDataRow++;
		trainingDataControlConstraints.gridx = 0;
		trainingDataControlConstraints.gridy = trainingDataRow;
		trainingDataControlConstraints.ipadx = 10;
		trainingDataControlConstraints.anchor = GridBagConstraints.CENTER;
		trainingDataControlConstraints.weighty = 0;
		trainingDataControlConstraints.gridwidth = 1;
		trainingDataControlConstraints.fill = GridBagConstraints.HORIZONTAL;
		trainingDataContainer.add( fileLabel, trainingDataControlConstraints );
		
		trainingDataControlConstraints.gridx = 1;
		trainingDataControlConstraints.gridy = trainingDataRow;
		trainingDataControlConstraints.anchor = GridBagConstraints.CENTER;
		trainingDataControlConstraints.weighty = 0;
		trainingDataControlConstraints.weightx = 1;
		trainingDataControlConstraints.gridwidth = 2;
		trainingDataControlConstraints.fill = GridBagConstraints.HORIZONTAL;
		trainingDataContainer.add( fileField, trainingDataControlConstraints );

		//Size
		trainingDataRow++;
		trainingDataControlConstraints.gridx = 0;
		trainingDataControlConstraints.gridy = trainingDataRow;
		trainingDataControlConstraints.ipadx = 10;
		trainingDataControlConstraints.anchor = GridBagConstraints.CENTER;
		trainingDataControlConstraints.weightx = 0;
		trainingDataControlConstraints.weighty = 0;
		trainingDataControlConstraints.gridwidth = 1;
		trainingDataControlConstraints.fill = GridBagConstraints.HORIZONTAL;
		trainingDataContainer.add( sizeLabel, trainingDataControlConstraints );
				
		trainingDataControlConstraints.gridx = 1;
		trainingDataControlConstraints.gridy = trainingDataRow;
		trainingDataControlConstraints.anchor = GridBagConstraints.CENTER;
		trainingDataControlConstraints.weightx = 1;
		trainingDataControlConstraints.weighty = 0;
		trainingDataControlConstraints.gridwidth = 2;
		trainingDataControlConstraints.fill = GridBagConstraints.HORIZONTAL;
		trainingDataContainer.add( sizeField, trainingDataControlConstraints );
		
		//Select button
		trainingDataRow++;
		trainingDataControlConstraints.gridx = 1;
		trainingDataControlConstraints.gridy = trainingDataRow;
		trainingDataControlConstraints.anchor = GridBagConstraints.CENTER;
		trainingDataControlConstraints.weightx = 0;
		trainingDataControlConstraints.weighty = 0;
		trainingDataControlConstraints.gridwidth = 1;
		trainingDataControlConstraints.fill = GridBagConstraints.NONE;
		trainingDataContainer.add( selectButton, trainingDataControlConstraints );
		
		//View button
		trainingDataControlConstraints.gridx = 2;
		trainingDataControlConstraints.gridy = trainingDataRow;
		trainingDataControlConstraints.anchor = GridBagConstraints.EAST;
		trainingDataControlConstraints.weightx = 0;
		trainingDataControlConstraints.weighty = 0;
		trainingDataControlConstraints.gridwidth = 1;
		trainingDataControlConstraints.fill = GridBagConstraints.NONE;
		trainingDataContainer.add( viewButton, trainingDataControlConstraints );

// ---
		
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
		
		TrainingLoopListener trainingLoopListener = new TrainingLoopListener( dataModel, actualLoopField, actualMSEField, errorCanvas, errorGraphDataList );
		TrainingActivitiListener trainingActivitiListener = new TrainingActivitiListener( startButton, stopButton, resetWeightsButton );
		
		networkCanvas.addNetworkModelChangeListener( new MyNetworkModelChangeListener( this, trainingLoopListener, trainingActivitiListener ) );
	}
	
	public void setTrainingDataSize( String size ){
		sizeField.setText( size );
	}
	
	public void setTrainingDataFile( String fileName ){
		fileField.setText( fileName );
	}

	public DataHandler getDataHandler(){
		return dataHandler;
	}
	
	public void setDataHandler( DataHandler dataHandler ){
		this.dataHandler = dataHandler;
	}
	
	public void setNetwork( Network network ){
		this.network = network;
	}
	
	public Network getNetwork(){
		return network;
	}
	
	public JButton getStartButton(){
		return startButton;
	}
	
	public JButton getStopButton(){
		return stopButton;
	}
	
	public JButton getResetWeightsButton(){
		return resetWeightsButton;
	}

	public NetworkCanvas getNetworkCanvas(){
		return networkCanvas;
	}
	
	public TrainingDataModel getTrainingDataModel(){
		return dataModel;
	}
	
	public ArrayList<ErrorGraphDataPairs> getErrorGraphDataList(){
		return errorGraphDataList;
	}

}

/**
 * This Listener will be activated when the NetworkModel changed
 * 
 * @author akoel
 *
 */
class MyNetworkModelChangeListener implements NetworkModelChangeListener{
	
	TrainingTabCommunication trainingTabCommunication;
	TrainingLoopListener trainingLoopListener;
	TrainingActivitiListener trainingActivitiListener;

	public MyNetworkModelChangeListener( 
			TrainingTabCommunication trainingTabCommunication,
			TrainingLoopListener trainingLoopListener, 
			TrainingActivitiListener trainingActivitiListener ){
		this.trainingTabCommunication = trainingTabCommunication;
		this.trainingLoopListener = trainingLoopListener;
		this.trainingActivitiListener = trainingActivitiListener;
	}

	public void elementChanged( NetworkCanvas networkCanvas ) {
		Network network = networkCanvas.getNetwork();
		trainingTabCommunication.setNetwork(network);
		
		//TODO torolni a grafikont

		if( null != network ){
			network.setTrainingLoopListener( trainingLoopListener );
			network.setActivityListener( trainingActivitiListener );
		}else{
			//TODO letiltani a Start gombot
			//TODO letiltani a Reset gombot
			//TODO ha aktiv, akkor letiltani a Stop gobot	
			
			//TODO Delete DataHandler
		}	
	}		
}

class ErrorGraphPainterListener implements PainterListener{
	private TrainingTabCommunication trainingTabCommunication;
		
	public ErrorGraphPainterListener(TrainingTabCommunication trainingTabCommunication ){
		this.trainingTabCommunication = trainingTabCommunication;
	}
	
	public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
		Iterator<ErrorGraphDataPairs> graphListIterator = trainingTabCommunication.getErrorGraphDataList().iterator();
		ArrayList<ErrorGraphDataPairs> errorGraphDataList = trainingTabCommunication.getErrorGraphDataList();
		
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
		if( null != trainingTabCommunication.getNetwork() && !trainingTabCommunication.getNetwork().isTrainingStopped() && !errorGraphDataList.isEmpty() ){
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

	public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {}	
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

/**
 * Selects the Training Data File and 
 * generates the DataHandler out of it
 * 
 * @author akoel
 *
 */
class SelectButtonListener implements ActionListener {

	TrainingTab trainingTab;
	
	public SelectButtonListener( TrainingTab trainingTabCommunication ){
		this.trainingTab = trainingTabCommunication;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		JFileChooser fc;
		if ( null == trainingTab.getTrainingDataModel().trainingDataDirectory ) {
			fc = new JFileChooser(System.getProperty("user.dir"));
		} else {
			fc = new JFileChooser( trainingTab.getTrainingDataModel().trainingDataDirectory );
		}

		fc.setDialogTitle("Load training data");

		// Filter to use
		FileNameExtensionFilter filter = new FileNameExtensionFilter("csv", "csv");
		fc.setFileFilter(filter);

		// Does not allow the use of "All" filter
		fc.setAcceptAllFileFilterUsed(false);

		// Starts the Dialog window
		int returnVal = fc.showOpenDialog( trainingTab );
		
		// Selected the file
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			File file = fc.getSelectedFile();
			String filePath = file.getPath();

			// The extension is CVS regardless
			//if (!filePath.toLowerCase().endsWith(".csv")) {
			//	file = new File(filePath + ".csv");
			//}

			DataHandler dataHandler = generatesDataHandler( file );
			
			//The file is not correct
			if( null == dataHandler ){
				trainingTab.setTrainingDataFile( "" );
				trainingTab.setTrainingDataSize( "" );

			//Tha DataHandler is generated
			}else{	

dataHandler.reset();				
while ( dataHandler.hasNext() ){
	dataHandler.takeNext();
	System.err.println( "Input: " + dataHandler.getInput( 0 ) + " Output: "  +dataHandler.getExpectedOutput( 0 ));
}
dataHandler.reset();				
				
				
				
				
				//I write back the trainingDataDirectory
				trainingTab.getTrainingDataModel().trainingDataDirectory = fc.getCurrentDirectory();
			
				trainingTab.setTrainingDataFile( file.getName() );
				trainingTab.setTrainingDataSize( String.valueOf( dataHandler.getSize() ) );
			}

			trainingTab.setDataHandler(dataHandler);

		}
	}
	
	private DataHandler generatesDataHandler( File file ){
		String line = "";
        String ioSplitBy = ";;";
        String neuronsSplitBy = ";";
        
        boolean wrongFormat = false;
        int inputs = -1;
        int outputs = -1;
        
        MyDataHandler dataHandler = null;
        
        ArrayList<double[]> inputArrayList = new ArrayList<double[]>();
        ArrayList<double[]> outputArrayList = new ArrayList<double[]>();

/*        MyDataHandler myDataHandler = new MyDataHandler(
				new double[][]{{0.1}, {0.15}, {0.2}, {0.35}, {0.4}, {0.45}, {0.5} }, 
				new double[][]{{0.1}, {0.5}, {0.45}, {0.4}, {0.35}, {0.2}, {0.15} }
		);        
*/        
        BufferedReader br = null;
		try {
	        br = new BufferedReader( new FileReader( file ) );
        	while( ( line = br.readLine() ) != null ){
			
				String[] ioArray = line.split(ioSplitBy);

				//If there is no 1 input and 1 output section
				if( ioArray.length != 2 ){
					throw new WrongCSVFormat();
				}

				//Inputs
				String[] inputArray = ioArray[0].split( neuronsSplitBy );
				if( inputArray.length < 1 ){
					throw new WrongCSVFormat();
				}else if( inputs == -1 ){
					inputs = inputArray.length;
				}else if( inputs != inputArray.length ){
					throw new WrongCSVFormat();
				}
				
				//Outputs
				String[] outputArray = ioArray[1].split( neuronsSplitBy );
				if( outputArray.length < 1 ){
					throw new WrongCSVFormat();
				}else if( outputs == -1 ){
					outputs = outputArray.length;
				}else if( outputs != outputArray.length ){
					throw new WrongCSVFormat();
				}
				
				double[] inputLine = new double[inputs];
				for( int i = 0; i < inputArray.length; i++ ){
					inputLine[ i ] = Double.valueOf( inputArray[ i ] );
				}
				inputArrayList.add( inputLine );				
				
				double[] outputLine = new double[outputs];
				for( int i = 0; i < outputArray.length; i++ ){
					outputLine[ i ] = Double.valueOf( outputArray[ i ] );
				}
				outputArrayList.add( outputLine );
			}
        	
        	double[][] input = new double[ inputArrayList.size()][];
        	for( int i = 0; i < inputArrayList.size(); i++ ){
        		input[ i ] = inputArrayList.get( i );
        	}
        	
        	double[][] output = new double[ outputArrayList.size()][];
        	for( int i = 0; i < outputArrayList.size(); i++ ){
        		output[ i ] = outputArrayList.get( i );
        	}

        	dataHandler = new MyDataHandler(input, output);
        	
		
		} catch (WrongCSVFormat e){
			wrongFormat = true;
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			wrongFormat = true;
			e.printStackTrace();
		} catch (IOException e) {
			wrongFormat = true;
			e.printStackTrace();
		} finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                	wrongFormat = true;
                    e.printStackTrace();
                }
            }
        }

		if( wrongFormat ){
			return null;
		}
		
		return dataHandler;
	}
	
	class MyDataHandler extends DataHandler {
		double[][] input;
		double[][] output;
		int pointer = -1;
		
		public MyDataHandler( double[][] input, double[][] output ){
			this.input = input;
			this.output = output;
		}
				
		public double getExpectedOutput(int outputNeuronIndex) {
			return output[pointer][outputNeuronIndex];
		}
		
		public double getInput(int inputNeuronIndex) {
			return input[pointer][inputNeuronIndex];
		}
		
		@Override
		public void takeNext() {
			pointer++;
		}
		
		@Override
		public void reset() {
			pointer = -1;
		}
		
		@Override
		public boolean hasNext() {
			if( pointer + 1 < input.length){
				return true;
			}else{
				return false;
			}
		}
		
		@Override
		public int getSize() {
			return input.length;
		}

		@Override
		public int getInputs() {
			if( input.length > 0 ){
				return input[0].length;
			}
			return 0;
		}

		@Override
		public int getOutputs() {
			if( output.length > 0 ){
				return output[0].length;
			}
			return 0;
		}
	}
	
}

class WrongCSVFormat extends Exception{
	private static final long serialVersionUID = 8578405306750401849L;
}

class StartButtonListener implements ActionListener {
	TrainingTabCommunication trainingTabCommunication;
	
	public StartButtonListener( TrainingTabCommunication trainingTabCommunication ){
		this.trainingTabCommunication = trainingTabCommunication;
	}
	
	public void actionPerformed(ActionEvent e) {
		
/*		MyDataHandler myDataHandler = new MyDataHandler(
				new double[][]{{0.1}, {0.15}, {0.2}, {0.35}, {0.4}, {0.45}, {0.5} }, 
				new double[][]{{0.1}, {0.5}, {0.45}, {0.4}, {0.35}, {0.2}, {0.15} }
		);
*/		
		DataHandler dataHandler = trainingTabCommunication.getDataHandler();
		
		Network network = trainingTabCommunication.getNetwork();
		
		//If it not possible to generate Network
		if( null == network || null == dataHandler ){
			return;
		}
		
		//TODO check if the input and output neuron numbers comply with the DataHandler
		
		trainingTabCommunication.getStartButton().setEnabled( false );
		trainingTabCommunication.getStopButton().setEnabled(true);
		trainingTabCommunication.getResetWeightsButton().setEnabled( false );

		TrainingDataModel dataModel = trainingTabCommunication.getTrainingDataModel();
		
		network.setLearningRate( dataModel.learningRate.getValue());
		network.setMomentum( dataModel.momentum.getValue() );
		network.setMaxTrainingLoop( dataModel.maxTrainingLoop.getValue() );
		network.setMaxTotalMeanSquareError( Double.valueOf( dataModel.maxMeanSquaredError.getValue() ) );
		
		StartTrainingRunnable startingRunnable = new StartTrainingRunnable(network, dataHandler, trainingTabCommunication.getErrorGraphDataList() );
		startingRunnable.start();
		
		//SwingUtilities.invokeLater(new StartTrainingRunnable(network, trainingDataHandler) );
	}
}

class StopButtonListener implements ActionListener {
	private TrainingTabCommunication trainingTabCommunication;

	public StopButtonListener( TrainingTabCommunication trainingTabCommunication ){
		this.trainingTabCommunication = trainingTabCommunication;
	}
	
	public void actionPerformed(ActionEvent e) {
		trainingTabCommunication.getNetwork().stopTraining();
	}	
}

class ResetWeightsButtonListener implements ActionListener{

	private TrainingTabCommunication trainingTabCommunication;

	public ResetWeightsButtonListener( TrainingTabCommunication trainingTabCommunication ){
		this.trainingTabCommunication = trainingTabCommunication;
	}
	
	public void actionPerformed(ActionEvent e) {
		trainingTabCommunication.getNetwork().resetWeights();
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
	private TrainingDataModel dataModel;
	private JTextField actualLoop;
	private JTextField actualMSE;
	private ArrayList<ErrorGraphDataPairs> errorGraphDataList;
	private MCanvas errorCanvas;
	private int offset = 0;
	
	public TrainingLoopListener( TrainingDataModel dataModel, JTextField actualLoop, JTextField actualMSE, MCanvas errorCanvas, ArrayList<ErrorGraphDataPairs> errorGraphDataList ){
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

			//Add the new Error to the list
			errorGraphDataList.add( new ErrorGraphDataPairs( offset + loopCounter, totalMeanSquareError ));
			
			//Reprint the graph
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










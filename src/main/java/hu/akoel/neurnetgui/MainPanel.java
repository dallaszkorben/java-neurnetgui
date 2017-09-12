package hu.akoel.neurnetgui;



import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.swing.UIManager.*;

import hu.akoel.neurnet.handlers.DataHandler;
import hu.akoel.neurnet.layer.Layer;
import hu.akoel.neurnet.network.Network;
import hu.akoel.neurnet.neuron.Neuron;

/**
 * Hello world!
 *
 */
public class MainPanel extends JFrame{

	private static final long serialVersionUID = 141335331958462407L;

	private static final int DEFAULT_WIDTH = 900;
	private static final int DEFAULT_HEIGHT = 600; 
	
	private String version;
	private DataControl dataControl;
	
	public MainPanel( DataControl dataControl, String version ){
    	this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    	
    	this.dataControl = dataControl;
    	this.version = version;
    	this.setTitle("");
    	this.setUndecorated( false );
    	this.setSize( DEFAULT_WIDTH, DEFAULT_HEIGHT );
    	this.createBufferStrategy( 1 );
    	
    	try {
    	    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
    	        if ("Nimbus".equals(info.getName())) {
    	            UIManager.setLookAndFeel(info.getClassName());
    	            break;
    	        }
    	    }
    	} catch (Exception e) {
    	    // If Nimbus is not available, you can set the GUI to another look and feel.
    	}
    
    	JMenuBar menuBar;
    	JMenu fileMainMenu;
    	JMenu helpMainMenu;
    	
    	// Create the menu bar
    	menuBar = new JMenuBar();
    	
    	// File
    	fileMainMenu = new JMenu( Common.getTranslated("menu.file") );
    	fileMainMenu.setMnemonic( KeyEvent.VK_F);
    	menuBar.add( fileMainMenu );
    	
    	// Separator
    	fileMainMenu.addSeparator();
    	
    	//Help
    	menuBar.add( Box.createHorizontalGlue());
    	helpMainMenu = new JMenu( Common.getTranslated("menu.help") );
    	helpMainMenu.setMnemonic( KeyEvent.VK_H );
    	menuBar.add( helpMainMenu );
    	
    	this.setJMenuBar(menuBar);
    	
    	// Status Line
    	
    	
    	
    	
    	
    	
    	MyDataHandler myDataHandler = new MyDataHandler(
    			new double[][]{{0.1}, {0.15}, {0.2}, {0.35}, {0.4}, {0.45}, {0.5} }, 
    			new double[][]{{0.1}, {0.15}, {0.2}, {0.35}, {0.4}, {0.45}, {0.5} }
    	);
    	int inputLayerSize = 1;
    	int innerLayerSize = 3;
    	int outputLayerSize = 1;
    	Layer inputLayer = new Layer();
    	for( int i = 0; i < inputLayerSize; i++ ){
    		inputLayer.addNeuron(new Neuron());
    	}
    	Layer innerLayer = new Layer();
    	for( int i = 0; i < innerLayerSize; i++ ){
    		innerLayer.addNeuron(new Neuron());
    	}
    	Layer outputLayer = new Layer();
    	for( int i = 0; i < outputLayerSize; i++ ){
    		outputLayer.addNeuron(new Neuron());
    	}
    	Network myNetwork = new Network();
    	myNetwork.addLayer( inputLayer );
    	myNetwork.addLayer( innerLayer );
    	myNetwork.addLayer( outputLayer );
    	
    	TrainingControlPanel controlPanel = new TrainingControlPanel( myNetwork, myDataHandler, dataControl );
    	
    	this.getContentPane().setLayout( new BorderLayout(10, 10));
    	this.getContentPane().add(controlPanel, BorderLayout.EAST);
    	
    	this.setVisible( true );
    }
	
	public void setTitle( String title ){
		super.setTitle( "neurnetGUI " + version + title );
	}
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
	public double getSize() {
		return input.length;
	}
};

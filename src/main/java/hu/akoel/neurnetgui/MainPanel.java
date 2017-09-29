package hu.akoel.neurnetgui;



import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UIManager.*;

import hu.akoel.neurnet.handlers.DataHandler;
import hu.akoel.neurnet.layer.Layer;
import hu.akoel.neurnet.network.Network;
import hu.akoel.neurnet.neuron.Neuron;
import hu.akoel.neurnetgui.accessories.Common;
import hu.akoel.neurnetgui.datamodels.ConstructionDataModel;
import hu.akoel.neurnetgui.datamodels.TrainingDataModel;
import hu.akoel.neurnetgui.networkcanvas.NetworkCanvas;
import hu.akoel.neurnetgui.tab.TabbedPanelContainer;

/**
 * Hello world!
 *
 */
public class MainPanel extends JFrame{

	private static final long serialVersionUID = 141335331958462407L;

	private static final int DEFAULT_WIDTH = 900;
	private static final int DEFAULT_HEIGHT = 600; 	
	private static final int DEFAULTSETTINFTABBEDPANEL = 350;
	
	private JPanel containerPanel;
	
	private String version;
	private TrainingDataModel trainingDataModel;
	private ConstructionDataModel constructionDataModel;

	//NetworkCanvas
	private NetworkCanvas networkCanvas;	
	
	public MainPanel( TrainingDataModel trainingDataModel, ConstructionDataModel constructionDataModel, String version ){
    	this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    	
    	this.trainingDataModel = trainingDataModel;
    	this.constructionDataModel = constructionDataModel;    	
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
    	
    	// NetworkCanvas
    	networkCanvas = new NetworkCanvas();
    	//TODO myNetwork and myDataHandler will be removed as parameters
    	TabbedPanelContainer settingTabbedPanel = new TabbedPanelContainer( networkCanvas, trainingDataModel, constructionDataModel );
    	
    	// Preparation for the SplitPane
    	this.containerPanel = new JPanel();
    	this.containerPanel.setLayout( new BoxLayout( containerPanel, BoxLayout.Y_AXIS ) );
    	this.containerPanel.add( settingTabbedPanel );
//    	this.containerPanel.add(Box.createVerticalGlue());
//    	this.containerPanel.add( controlPanel );
    	
    	JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, networkCanvas, containerPanel );
    	splitPane.setOneTouchExpandable( true );
    	splitPane.setDividerLocation( DEFAULT_WIDTH - DEFAULTSETTINFTABBEDPANEL );
    	splitPane.setResizeWeight( 1.0 );
    	
    	// Placing SplitPane
    	this.getContentPane().setLayout( new BorderLayout(10, 10));
    	this.getContentPane().add(splitPane, BorderLayout.CENTER);
    	
    	this.setVisible( true );
    }
	
	public void setTitle( String title ){
		super.setTitle( "neurnetGUI " + version + title );
	}
}
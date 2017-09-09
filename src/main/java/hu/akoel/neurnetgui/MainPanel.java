package hu.akoel.neurnetgui;



import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.swing.UIManager.*;

/**
 * Hello world!
 *
 */
public class MainPanel extends JFrame{

	private static final long serialVersionUID = 141335331958462407L;

	private static final int DEFAULT_WIDTH = 900;
	private static final int DEFAULT_HEIGHT = 800; 
	
	private String version;
	
	public MainPanel( String version ){
    	this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    	
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
    	
    	
    	JButton button = new JButton("hello");
    	this.add( button );
    	
    	this.setVisible( true );
    }
	
	public void setTitle( String title ){
		super.setTitle( "neurnetGUI " + version + title );
	}
}

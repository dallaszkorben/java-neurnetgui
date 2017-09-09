package hu.akoel.neurnetgui;

import javax.swing.SwingUtilities;

public class NeurmetGUI {

	public static void main( String[] args ){
		
		final String version = "1.0.0";

		Common.loadSettings();
		
		SwingUtilities.invokeLater( new Runnable(){

			public void run() {
				new MainPanel( version );
				
			}
			
		});
	}
}

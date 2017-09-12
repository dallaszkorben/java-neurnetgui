package hu.akoel.neurnetgui;

import javax.swing.SwingUtilities;

public class NeurmetGUI {

	public static void main( String[] args ){
		
		String version = "1.0.0";

		Common.loadSettings();
		
		DataControl dataControl = new DataControl();
	
//		SwingUtilities.invokeLater( new InvokeLaterRunnable(dataControl, version) );
		SwingUtilities.invokeLater( new Runnable(){

			public void run() {
				String version = "1.0.0";
				DataControl dataControl = new DataControl();
				new MainPanel(dataControl, version);
				
			}
			
		});
		
	}
	
}

class InvokeLaterRunnable implements Runnable {
	private String version;
	private DataControl dataControl;
	public InvokeLaterRunnable(DataControl dataControl, String version){
		this.dataControl = dataControl;
		this.version = version;
	}
	public void run() {
		new MainPanel(dataControl, version);
	}
}

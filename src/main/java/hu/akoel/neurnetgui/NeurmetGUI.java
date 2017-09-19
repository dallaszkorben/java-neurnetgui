package hu.akoel.neurnetgui;

import javax.swing.SwingUtilities;

import hu.akoel.neurnetgui.accessories.Common;

public class NeurmetGUI {

	public static void main( String[] args ){
		
		String version = "1.0.0";

		Common.loadSettings();
		
		DataModel dataControl = new DataModel();
	
//		SwingUtilities.invokeLater( new InvokeLaterRunnable(dataControl, version) );
		SwingUtilities.invokeLater( new Runnable(){

			public void run() {
				String version = "1.0.0";
				DataModel dataControl = new DataModel();
				new MainPanel(dataControl, version);
				
			}
			
		});
		
	}
	
}

class InvokeLaterRunnable implements Runnable {
	private String version;
	private DataModel dataControl;
	public InvokeLaterRunnable(DataModel dataControl, String version){
		this.dataControl = dataControl;
		this.version = version;
	}
	public void run() {
		new MainPanel(dataControl, version);
	}
}

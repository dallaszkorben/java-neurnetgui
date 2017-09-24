package hu.akoel.neurnetgui;

import javax.swing.SwingUtilities;

import hu.akoel.neurnetgui.accessories.Common;
import hu.akoel.neurnetgui.datamodels.ConstructionDataModel;
import hu.akoel.neurnetgui.datamodels.TrainingDataModel;

public class NeurmetGUI {

	public static void main( String[] args ){
		
		String version = "1.0.0";

		Common.loadSettings();
		
		TrainingDataModel dataControl = new TrainingDataModel();
	
//		SwingUtilities.invokeLater( new InvokeLaterRunnable(dataControl, version) );
		SwingUtilities.invokeLater( new Runnable(){

			public void run() {
				String version = "1.0.0";
				TrainingDataModel trainingDataModel = new TrainingDataModel();
				ConstructionDataModel constructionDataModel = new ConstructionDataModel();
				new MainPanel(trainingDataModel, constructionDataModel, version);
				
			}
			
		});
		
	}
	
}

class InvokeLaterRunnable implements Runnable {
	private String version;
	private TrainingDataModel trainingDataModel;
	private ConstructionDataModel constructionDataModel;
	public InvokeLaterRunnable(TrainingDataModel trainingDataModel, ConstructionDataModel constructionDataModel, String version){
		this.trainingDataModel = trainingDataModel;
		this.constructionDataModel = constructionDataModel;
		this.version = version;
	}
	public void run() {
		new MainPanel(trainingDataModel, constructionDataModel, version);
	}
}

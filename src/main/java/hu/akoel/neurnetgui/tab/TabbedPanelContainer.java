package hu.akoel.neurnetgui.tab;

import hu.akoel.neurnet.handlers.DataHandler;
import hu.akoel.neurnet.network.Network;
import hu.akoel.neurnetgui.accessories.Common;
import hu.akoel.neurnetgui.accessories.CompositeIcon;
import hu.akoel.neurnetgui.accessories.VTextIcon;
import hu.akoel.neurnetgui.datamodels.ConstructionDataModel;
import hu.akoel.neurnetgui.datamodels.TrainingDataModel;
import hu.akoel.neurnetgui.networkcanvas.NetworkCanvas;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

public class TabbedPanelContainer extends JTabbedPane{

	private static final long serialVersionUID = -8066765323009172519L;

	private TrainingTab trainingTab;
	private ConstructionTab constructionTab;
		
	CompositeIcon trainingTabIcon;
	CompositeIcon constructionTabIcon;
	
	public TabbedPanelContainer( NetworkCanvas networkCanvas, TrainingDataModel trainingDataModel, ConstructionDataModel constructionDataModel ){
		super(RIGHT);
		
		trainingTab = new TrainingTab( networkCanvas, trainingDataModel );
		VTextIcon trainingTabTextIcon = new VTextIcon(trainingTab, Common.getTranslated("training.title"), VTextIcon.ROTATE_LEFT);
		Icon trainingTabGraphicIcon = UIManager.getIcon("FileView.computerIcon");
		trainingTabIcon = new CompositeIcon( trainingTabGraphicIcon, trainingTabTextIcon );

		constructionTab = new ConstructionTab( networkCanvas, constructionDataModel );
		VTextIcon constructionTabTextIcon = new VTextIcon(constructionTab, Common.getTranslated("construction.title"), VTextIcon.ROTATE_LEFT);
		Icon constructionTabGraphicIcon = UIManager.getIcon("FileView.computerIcon");
		constructionTabIcon = new CompositeIcon( constructionTabGraphicIcon, constructionTabTextIcon );


		this.addTab( null, trainingTabIcon, trainingTab );
		this.addTab( null, constructionTabIcon, constructionTab );

		this.setSelectedIndex( 1 );
	}
}

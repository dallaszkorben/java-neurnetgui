package hu.akoel.neurnetgui.tab;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import hu.akoel.mgu.drawnblock.FillOvalElement;
import hu.akoel.mgu.drawnblock.FillRectangleElement;
import hu.akoel.mgu.drawnblock.OvalElement;
import hu.akoel.mgu.sprite.Appearance;
import hu.akoel.mgu.sprite.Magnet;
import hu.akoel.mgu.sprite.MagnetType;
import hu.akoel.mgu.sprite.RectangleElement;
import hu.akoel.mgu.sprite.Sprite;
import hu.akoel.mgu.values.PositionValue;
import hu.akoel.mgu.values.RangeValueInPixel;
import hu.akoel.mgu.values.SizeValue;
import hu.akoel.neurnetgui.NeuronDescriptor;
import hu.akoel.neurnetgui.NeuronDescriptor.NeuronType;
import hu.akoel.neurnetgui.NeuronDescriptor.TransferFunction;
import hu.akoel.neurnetgui.accessories.Common;
import hu.akoel.neurnetgui.networkcanvas.NetworkCanvas;

public class ConstructionTab  extends JPanel{
	private static final long serialVersionUID = -102436776877486921L;
	
	private static final double SPACE_FOR_ONE_NEURON = 1.0;
	
	private NetworkCanvas networkCanvas;
	
	public ConstructionTab( NetworkCanvas networkCanvas ){
		this.networkCanvas = networkCanvas;
		
		this.setBorder( BorderFactory.createLoweredBevelBorder());
		this.setLayout( new GridBagLayout());
		GridBagConstraints controlConstraints = new GridBagConstraints();		
		
		//
		// Define fields
		//
		
		// Place new Layer button
		JButton placeNewLayerButton = new JButton( Common.getTranslated("training.button.start") );
		placeNewLayerButton.setEnabled( true );
		//placeNewLayerButton.setBackground( Color.green );
		
		placeNewLayerButton.addActionListener( new PlaceNewLayerButtonButtonListener( networkCanvas, this )); 
		
		
		
		//
		// Place fields
		//
		// Place new Layer button
		int row = -1;
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 0;
		controlConstraints.gridwidth = 2;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( placeNewLayerButton, controlConstraints );

		// Filler
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 10;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 1;
		controlConstraints.fill = GridBagConstraints.VERTICAL;
		this.add(new JLabel(), controlConstraints );
	}

	
	private static final MagnetType layerToNeuronMagnetType = new MagnetType("LayerToNeuronMagnetType");
	private static final MagnetType neuronToLayerMagnetType = new MagnetType("NeuronToLayerMagnetType");
	
	public void constructLayer( NeuronDescriptor neuronDescriptor, int numberOfNeurons ){
		double layerHeight = numberOfNeurons * SPACE_FOR_ONE_NEURON;
		double layerWidth = SPACE_FOR_ONE_NEURON;
		double layerMinY = -( layerHeight / 2 );
		double layerMinX = -( layerWidth / 2 );
		
		//
		//Define Layer
		//
		Sprite layerSprite = new Sprite( new SizeValue( layerMinX, layerMinY, layerMinX + layerWidth, layerMinY + layerHeight ) );
		
		FillRectangleElement layerBaseElement = new FillRectangleElement(	layerMinX, layerMinY, layerWidth, layerHeight, new Appearance( new Color( 40, 140, 180), new BasicStroke(1f) ) );
		layerBaseElement.setSelectedAppearance( new Appearance( new Color( 0, 0, 255 ), new BasicStroke(1f) ));
		layerBaseElement.setShadowAppearance( new Appearance( new Color( 70, 70, 70 ), new BasicStroke(1f) ));
		RectangleElement layerBorderElement = new RectangleElement(	layerMinX, layerMinY, layerWidth, layerHeight, new Appearance( new Color( 0, 30, 180), new BasicStroke(1f) ) );
		layerBorderElement.setFocusAppearance( new Appearance( Color.blue, new BasicStroke(3f) ) );
		
		layerSprite.addElement( layerBaseElement );
		layerSprite.addElement( layerBorderElement );	
		
		double toNeuronMagnetX = 0;
		double baseToNeuronMagnetY = layerMinY + SPACE_FOR_ONE_NEURON / 2;
		for( int i = 0; i < numberOfNeurons; i++ ){
			double toNeuronMagnetY = baseToNeuronMagnetY + i * SPACE_FOR_ONE_NEURON;
			FillOvalElement layerToNeuronMagnetElement = new FillOvalElement( 0.0, 0.0, 0.04, new Appearance( Color.red, new BasicStroke(1) ) );
			
			Magnet toNeuronEastMagnet = new Magnet(layerSprite, layerToNeuronMagnetType, 90.0, new RangeValueInPixel( 20, 20 ), new PositionValue( toNeuronMagnetX, toNeuronMagnetY ) );
			toNeuronEastMagnet.addPossibleMagnetTypeToConnect( neuronToLayerMagnetType  );
			toNeuronEastMagnet.addElement( layerToNeuronMagnetElement );

			layerSprite.addMagnet( toNeuronEastMagnet );
		}
		
		layerSprite.setPosition( 0, 0 );
		networkCanvas.addSprite(layerSprite);

		//
		//Define Neurons
		//
		double toLayerMagnetX = 0;
		double toLayerMagnetY = 0;
		for( int i = 0; i < numberOfNeurons; i++ ){
			double neuronDiameter = SPACE_FOR_ONE_NEURON - SPACE_FOR_ONE_NEURON * 0.2;
			double neuronRadius = neuronDiameter / 2;
			double neuronMinY = -( neuronRadius );
			double neuronMinX = -( neuronRadius );
			
			Sprite neuronSprite = new Sprite( new SizeValue( neuronMinX, neuronMinY, neuronMinX + neuronDiameter, neuronMinY + neuronDiameter ), false );
			
			FillOvalElement neuronBaseElement = new FillOvalElement( toLayerMagnetX, toLayerMagnetY, neuronRadius, new Appearance( Color.yellow, new BasicStroke(1f) ) );
			neuronBaseElement.setSelectedAppearance( new Appearance( Color.green, new BasicStroke(5) ) );
			OvalElement neuronBorderElement = new OvalElement( toLayerMagnetX, toLayerMagnetY, neuronRadius, new Appearance( Color.yellow, new BasicStroke(1f) ) );
			neuronBorderElement.setFocusAppearance( new Appearance( Color.red, new BasicStroke(3) ) );

			double neuronPositionY = baseToNeuronMagnetY + i * SPACE_FOR_ONE_NEURON;
			neuronSprite.addElement( neuronBaseElement );		
			neuronSprite.addElement( neuronBorderElement );
			
			//Define Neurons Magnet
			FillOvalElement neuronToLayerMagnetElement = new FillOvalElement( 0.0, 0.0, 0.04, new Appearance( Color.yellow, new BasicStroke(1) ) );
			
			Magnet toLayerWestMagnet = new Magnet( neuronSprite, neuronToLayerMagnetType, 270.0, new RangeValueInPixel( 20, 20 ), new PositionValue( toLayerMagnetX, toLayerMagnetY ) );
			toLayerWestMagnet.addPossibleMagnetTypeToConnect( layerToNeuronMagnetType  );
			toLayerWestMagnet.addElement( neuronToLayerMagnetElement );
			neuronSprite.addMagnet( toLayerWestMagnet );
			
			
			
			
			
			
			
			
			neuronSprite.setPosition( toLayerMagnetX, neuronPositionY );			
			networkCanvas.addSprite(neuronSprite);
		}
	}
}

class PlaceNewLayerButtonButtonListener implements ActionListener{
	private NetworkCanvas networkCanvas;
	private ConstructionTab constructionTab;

	public PlaceNewLayerButtonButtonListener( NetworkCanvas networkCanvas, ConstructionTab constructionTab ){
		this.networkCanvas = networkCanvas;
		this.constructionTab = constructionTab;
	}
	
	public void actionPerformed(ActionEvent e) {

		constructionTab.constructLayer( new NeuronDescriptor( NeuronType.NORMAL, TransferFunction.SIGMOID ), 5 );
		
		//A Canvas ujrarajzolasa, az uj Sprite megjelenites miatt
		networkCanvas.revalidateAndRepaintCoreCanvas();
		
	}
}




package hu.akoel.neurnetgui.tab;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import hu.akoel.mgu.sprite.Appearance;
import hu.akoel.mgu.sprite.Magnet;
import hu.akoel.mgu.sprite.MagnetType;
import hu.akoel.mgu.sprite.Sprite;
import hu.akoel.mgu.sprite.elements.FillOvalElement;
import hu.akoel.mgu.sprite.elements.FillRectangleElement;
import hu.akoel.mgu.sprite.elements.OvalElement;
import hu.akoel.mgu.sprite.elements.RectangleElement;
import hu.akoel.mgu.values.PositionValue;
import hu.akoel.mgu.values.RangeValueInPixel;
import hu.akoel.mgu.values.SizeValue;
import hu.akoel.neurnetgui.NeuronDescriptor;
import hu.akoel.neurnetgui.NeuronDescriptor.NeuronType;
import hu.akoel.neurnetgui.NeuronDescriptor.TransferFunction;
import hu.akoel.neurnetgui.datamodels.ConstructionDataModel;
import hu.akoel.neurnetgui.networkcanvas.NetworkCanvas;
import hu.akoel.neurnetgui.verifiers.IntegerVerifier;


/**
 * Constract a RAW NeuronSprite without position or appearance
 * 
 * @author akoel
 *
 */
class NeuronContainer{
	
	public static final double SPACE_FOR_ONE_NEURON = 1.0;
	public static final double NEURON_DIAMETER = 0.8;
	
	public static final MagnetType LAYER_TO_NEURON_MAGNET = new MagnetType("LayerToNeuronMagnetType");
	public static final MagnetType NEURON_TO_LAYER_MAGNET = new MagnetType("NeuronToLayerMagnetType");
	
	private Sprite neuronSprite;
	private NeuronDescriptor neuronDescriptor;
	
	public NeuronContainer( NeuronDescriptor neuronDescriptor ){
		this.neuronDescriptor = neuronDescriptor;
		
		double neuronRadius = NEURON_DIAMETER / 2;
		double neuronMinY = -( neuronRadius );
		double neuronMinX = -( neuronRadius );
		double toLayerMagnetX = 0;
		double toLayerMagnetY = 0;
		
		neuronSprite = new Sprite( 1, new SizeValue( neuronMinX, neuronMinY, neuronMinX + NEURON_DIAMETER, neuronMinY + NEURON_DIAMETER ), false );
		
		FillOvalElement neuronBaseElement = new FillOvalElement( toLayerMagnetX, toLayerMagnetY, neuronRadius, new Appearance( Color.yellow, new BasicStroke(1f) ) );
		neuronBaseElement.setSelectedAppearance( new Appearance( Color.green, new BasicStroke(5) ) );
		OvalElement neuronBorderElement = new OvalElement( toLayerMagnetX, toLayerMagnetY, neuronRadius, new Appearance( Color.yellow, new BasicStroke(1f) ) );
		neuronBorderElement.setFocusAppearance( new Appearance( Color.red, new BasicStroke(3) ) );

//		double neuronPositionY = baseToNeuronMagnetY + i * SPACE_FOR_ONE_NEURON;
		neuronSprite.addElement( neuronBaseElement );		
		neuronSprite.addElement( neuronBorderElement );
		
		//Define Neurons Magnet
		FillOvalElement neuronToLayerMagnetElement = new FillOvalElement( 0.0, 0.0, 0.04, new Appearance( Color.yellow, new BasicStroke(1) ) );
		
		Magnet toLayerWestMagnet = new Magnet( neuronSprite, NEURON_TO_LAYER_MAGNET, 270.0, new RangeValueInPixel( 20, 20 ), new PositionValue( toLayerMagnetX, toLayerMagnetY ) );
		toLayerWestMagnet.addPossibleMagnetTypeToConnect( LAYER_TO_NEURON_MAGNET  );
		toLayerWestMagnet.addElement( neuronToLayerMagnetElement );
		neuronSprite.addMagnet( toLayerWestMagnet );
		
		//Position is not set yet. It going to be set when it added to the Layer		
	}
	
	public Sprite getNeuron(){
		return neuronSprite;
	}
	
	public NeuronDescriptor getNeuronDescriptor(){
		return neuronDescriptor;
	}
}

class LayerContainer{
	private Sprite layerSprite;
	private ArrayList<NeuronContainer> neuronList = new ArrayList<NeuronContainer>();
	
	private FillRectangleElement layerBaseElement;
	private RectangleElement layerBorderElement;
	
	private double getHeight( double numberOfNeurons ){
		return numberOfNeurons * NeuronContainer.SPACE_FOR_ONE_NEURON;
	}
	
	public LayerContainer( NetworkCanvas networkCanvas  ){
		double layerHeight = getHeight( 0.5 ); 
		double layerWidth = NeuronContainer.SPACE_FOR_ONE_NEURON;
		double layerMinY = -( layerHeight / 2 );
		double layerMinX = -( layerWidth / 2 );
		
		layerSprite = new Sprite( 0, new SizeValue( layerMinX, layerMinY, layerMinX + layerWidth, layerMinY + layerHeight ) );
		
		layerBaseElement = new FillRectangleElement( layerMinX, layerMinY, layerWidth, layerHeight, new Appearance( new Color( 40, 140, 180), new BasicStroke(1f) ) );
		layerBaseElement.setSelectedAppearance( new Appearance( new Color( 0, 0, 255 ), new BasicStroke(1f) ));
		layerBorderElement = new RectangleElement(	layerMinX, layerMinY, layerWidth, layerHeight, new Appearance( new Color( 0, 30, 180), new BasicStroke(1f) ) );
		layerBorderElement.setFocusAppearance( new Appearance( Color.blue, new BasicStroke(3f) ) );
		
		layerSprite.addElement( layerBaseElement );
		layerSprite.addElement( layerBorderElement );
		
		//Add the Layer to the Canvas
		networkCanvas.addSprite(layerSprite);
	}
	
	public void addNeuronContainer( NeuronContainer neuronContainer, NetworkCanvas networkCanvas ){
		neuronList.add( neuronContainer );
		networkCanvas.addSprite( neuronContainer.getNeuron() );
		
		double layerHeight = getHeight( neuronList.size() ); 
		double layerWidth = NeuronContainer.SPACE_FOR_ONE_NEURON;
		double layerMinY = -( layerHeight / 2 );
		double layerMinX = -( layerWidth / 2 );
		
		layerSprite.setBoundBoxXMin( layerMinX );
		layerSprite.setBoundBoxXMax( layerMinX + layerWidth );
		layerSprite.setBoundYMin( layerMinY );
		layerSprite.setBoundYMax( layerMinY + layerHeight );

		layerBaseElement.setSize( layerMinX, layerMinY, layerWidth, layerHeight );
		layerBorderElement.setSize( layerMinX, layerMinY, layerWidth, layerHeight );
		
		//Add a new magnet
		double toNeuronMagnetX = 0;
		double baseToNeuronMagnetY = layerMinY + NeuronContainer.SPACE_FOR_ONE_NEURON / 2;
		int i = neuronList.size() - 1;

		double toNeuronMagnetY = baseToNeuronMagnetY + i * NeuronContainer.SPACE_FOR_ONE_NEURON;
		FillOvalElement layerToNeuronMagnetElement = new FillOvalElement( 0.0, 0.0, 0.04, new Appearance( Color.red, new BasicStroke(1) ) );
			
		Magnet toNeuronEastMagnet = new Magnet(layerSprite, NeuronContainer.LAYER_TO_NEURON_MAGNET, 90.0, new RangeValueInPixel( 20, 20 ), new PositionValue( toNeuronMagnetX, toNeuronMagnetY ) );
		toNeuronEastMagnet.addPossibleMagnetTypeToConnect( NeuronContainer.NEURON_TO_LAYER_MAGNET );
		toNeuronEastMagnet.addElement( layerToNeuronMagnetElement );

		layerSprite.addMagnet( toNeuronEastMagnet );
		
		//Set position of the Layer
		layerSprite.setPosition( 0, 0 );
		
		//Set position of the Layers's Magnets and Neurons
/*		for( i = 0; i < neuronList.size(); i++ ){
			toNeuronMagnetY = baseToNeuronMagnetY + i * NeuronContainer.SPACE_FOR_ONE_NEURON;
			
			//New positions for the Neurons
			Sprite neuronSprite = neuronList.get( i ).getNeuron();
			neuronSprite.setPosition( toNeuronMagnetX, toNeuronMagnetY );	
			networkCanvas.doArangeSpritePositionByMagnet( layerSprite );
		}
*/		
		i = 0;
		ArrayList<Magnet> magnetList = layerSprite.getMagnetList();
		for( Magnet magnet: magnetList ){
			if( magnet.getType().equals( NeuronContainer.LAYER_TO_NEURON_MAGNET ) ){
				
				toNeuronMagnetY = baseToNeuronMagnetY + i * NeuronContainer.SPACE_FOR_ONE_NEURON;
				
				//New positions for the Neurons
				Sprite neuronSprite = neuronList.get( i ).getNeuron();
				neuronSprite.setPosition( toNeuronMagnetX, toNeuronMagnetY );	
				
//System.err.println( i + ". magnet " + toNeuronMagnetY);				
				//New position for the Magnet
				magnet.setRelativePositionToSpriteZeroPoint( new PositionValue( toNeuronMagnetX, toNeuronMagnetY ) );
				
				i++;
			}
		}
		
		//Make the connection between Layer and Neurons
		networkCanvas.doArangeSpritePositionByMagnet( layerSprite );
//System.err.println("=====");		
	}
	
	public Iterator<NeuronContainer> getIterator(){
		return neuronList.iterator();
	}
	
	public Sprite getLayer(){
		return layerSprite;
	}
	
	public NeuronContainer getNeuronContainer( int index ){
		return neuronList.get(index);
	}
}

class LayerContainerList{
	ArrayList<LayerContainer> layerContainerList = new ArrayList<LayerContainer>();
	
	public void addLayerContainer( LayerContainer layerContainer ){
		this.layerContainerList.add( layerContainer );
	}
	
	public Iterator<LayerContainer> getIterator(){
		return layerContainerList.iterator();
	}
	
	public LayerContainer getLayerContainer( int index ){
		return layerContainerList.get( index );
	}
}

public class ConstructionTab  extends JPanel{
	private static final long serialVersionUID = -102436776877486921L;
	
	private static final double SPACE_FOR_ONE_NEURON = 1.0;
	
	private static final double DISTANCE_OF_LAYERS = 12.0;
	
	private NetworkCanvas networkCanvas;
	private ConstructionDataModel dataModel;
	
	private LayerContainerList layerContainerList = new LayerContainerList(); 
	
	public ConstructionTab( NetworkCanvas networkCanvas, ConstructionDataModel dataModel ){
		super();
		
		this.networkCanvas = networkCanvas;
		this.dataModel = dataModel;
		
		this.setBorder( BorderFactory.createLoweredBevelBorder());
		this.setLayout( new GridBagLayout());
		GridBagConstraints controlConstraints = new GridBagConstraints();		
		
		//
		// Define fields
		//
		
		// Actual Layer
		//TODO translate it
		JLabel actualLayerLabel = new JLabel( "Actual Layer:");
		JTextField actualLayerField = new JTextField();
		actualLayerField.setEditable( true );
		actualLayerField.setColumns( 5 );
		actualLayerField.setText( String.valueOf( dataModel.actualLayer.getValue() ) );		
		actualLayerField.setInputVerifier( new IntegerVerifier( dataModel.actualLayer, 0 ) );
		
		// Insert new Neuron button
		//TODO translate it
		JButton insertNewNeuronButton = new JButton( "Insert new Neuron" );
		insertNewNeuronButton.setEnabled( true );
		//placeNewLayerButton.setBackground( Color.green );
						
		// Place new Layer button
		//TODO translate it
		JButton placeNewLayerButton = new JButton( "Place new Layer" );
		placeNewLayerButton.setEnabled( true );
		//placeNewLayerButton.setBackground( Color.green );
		
		placeNewLayerButton.addActionListener( new PlaceNewLayerButtonListener( networkCanvas, this, dataModel ) ); 
		insertNewNeuronButton.addActionListener( new InsertNewNeuronButtonListener( networkCanvas, this, dataModel ) );
		
		
		//
		// Place fields
		//
		int row = -1;
				
		//Actual Layer
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 10;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( actualLayerLabel, controlConstraints );
		
		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.weightx = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( actualLayerField, controlConstraints );

		// Insert new Neuron button
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 0;
		controlConstraints.gridwidth = 2;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( insertNewNeuronButton, controlConstraints );
				
		// Place new Layer button
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
		
		controlConstraints.gridx = 2;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 10;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weightx = 1;
		controlConstraints.fill = GridBagConstraints.BOTH;
		this.add(new JLabel(), controlConstraints );
	}

	
	
	
	public void insertNeuron( NeuronDescriptor neuronDescriptor, int actualLayer ){

		NeuronContainer neuronContainer = new NeuronContainer( neuronDescriptor );
		
		layerContainerList.getLayerContainer( actualLayer ).addNeuronContainer(neuronContainer, networkCanvas);
		
		networkCanvas.revalidateAndRepaintCoreCanvas();
	}
	
	public void constructLayer( NeuronDescriptor neuronDescriptor, int numberOfNeurons ){
		
		//Create a new Empty Layer
		LayerContainer layerContainer = new LayerContainer(networkCanvas);
		
		//Added to the List
		layerContainerList.addLayerContainer(layerContainer);
		
		//Canvas refreshed
		networkCanvas.revalidateAndRepaintCoreCanvas();
	}
}

class PlaceNewLayerButtonListener implements ActionListener{
	private NetworkCanvas networkCanvas;
	private ConstructionTab constructionTab;
	private ConstructionDataModel dataModel;

	public PlaceNewLayerButtonListener( NetworkCanvas networkCanvas, ConstructionTab constructionTab, ConstructionDataModel dataModel ){
		this.networkCanvas = networkCanvas;
		this.constructionTab = constructionTab;
		this.dataModel = dataModel;
	}
	
	public void actionPerformed(ActionEvent e) {

		constructionTab.constructLayer( new NeuronDescriptor( NeuronType.NORMAL, TransferFunction.SIGMOID ), 5 );
		
		//A Canvas ujrarajzolasa, az uj Sprite megjelenites miatt
		//networkCanvas.revalidateAndRepaintCoreCanvas();		
	}
}

class InsertNewNeuronButtonListener implements ActionListener{
	private NetworkCanvas networkCanvas;
	private ConstructionTab constructionTab;
	private ConstructionDataModel dataModel;
	
	public InsertNewNeuronButtonListener( NetworkCanvas networkCanvas, ConstructionTab constructionTab, ConstructionDataModel dataModel ){
		this.networkCanvas = networkCanvas;
		this.constructionTab = constructionTab;
		this.dataModel = dataModel;
	}
	
	public void actionPerformed(ActionEvent e) {

		constructionTab.insertNeuron( new NeuronDescriptor( NeuronType.NORMAL, TransferFunction.SIGMOID ), dataModel.actualLayer.getValue() );
		
		//A Canvas ujrarajzolasa, az uj Sprite megjelenites miatt
		//networkCanvas.revalidateAndRepaintCoreCanvas();		
	}	
	
}





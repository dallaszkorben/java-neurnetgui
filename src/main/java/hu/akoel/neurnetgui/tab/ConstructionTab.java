package hu.akoel.neurnetgui.tab;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

	public static final int LEVEL_NEURON = 2;

	public static final double SPACE_FOR_ONE_NEURON = 1.0;
	public static final double NEURON_DIAMETER = 0.8;
	
	private static final Color NEURON_MAGNET_COLOR = Color.yellow;
	private static final double NEUROM_MAGNET_RADIUS = 0.04;
	
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
		
		neuronSprite = new Sprite( LEVEL_NEURON, new SizeValue( neuronMinX, neuronMinY, neuronMinX + NEURON_DIAMETER, neuronMinY + NEURON_DIAMETER ), false );
		
		FillOvalElement neuronBaseElement = new FillOvalElement( toLayerMagnetX, toLayerMagnetY, neuronRadius, new Appearance( Color.yellow, new BasicStroke(1f) ) );
		neuronBaseElement.setSelectedAppearance( new Appearance( Color.green, new BasicStroke(5) ) );
		OvalElement neuronBorderElement = new OvalElement( toLayerMagnetX, toLayerMagnetY, neuronRadius, new Appearance( Color.yellow, new BasicStroke(1f) ) );
		neuronBorderElement.setFocusAppearance( new Appearance( Color.red, new BasicStroke(3) ) );

		neuronSprite.addElement( neuronBaseElement );		
		neuronSprite.addElement( neuronBorderElement );
		
		//Define Neurons Magnet
		FillOvalElement neuronToLayerMagnetElement = new FillOvalElement( 0.0, 0.0, NEUROM_MAGNET_RADIUS, new Appearance( NEURON_MAGNET_COLOR, new BasicStroke(1) ) );
		
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

	/**
	 * When a Layer position is changed it will automatically call this method
	 * to change the Inner Neurons positions regarding
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition( double x, double y ){
		neuronSprite.setPosition( new PositionValue(x, y));
	}
}

class LayerContainer{
	public static final int LEVEL_LAYER = 1;
	
	public static final MagnetType LAYER_TO_RULER_MAGNET = new MagnetType("LayerToRulerMagnetType");
	public static final MagnetType RULER_TO_LAYER_MAGNET = new MagnetType("RulerToLayerMagnetType");
	
	private static final Color LAYER_TO_RULE_MAGNET_COLOR = Color.magenta;
	private static final double LAYER_TO_RULE_MAGNET_RADIUS = 0.07;
	private static final Color LAYER_TO_MEURON_MAGNET_COLOR = Color.cyan;
	private static final double LAYER_TO_NEURON_MAGNET_RADIUS = 0.04;

	
	
	private NetworkCanvas networkCanvas;
	private Sprite layerSprite;
	private ArrayList<NeuronContainer> neuronList = new ArrayList<NeuronContainer>();
	
	private FillRectangleElement layerBaseElement;
	private RectangleElement layerBorderElement;
	
	private double getHeight( double numberOfNeurons ){
		return numberOfNeurons * NeuronContainer.SPACE_FOR_ONE_NEURON;
	}
	
	public LayerContainer( NetworkCanvas networkCanvas ){
		this.networkCanvas = networkCanvas;

		double layerHeight = getHeight( 0.5 ); 
		double layerWidth = NeuronContainer.SPACE_FOR_ONE_NEURON;
		double layerMinY = -( layerHeight / 2 );
		double layerMinX = -( layerWidth / 2 );
		
		layerSprite = new Sprite( LEVEL_LAYER, new SizeValue( layerMinX, layerMinY, layerMinX + layerWidth, layerMinY + layerHeight ) );
		
		layerBaseElement = new FillRectangleElement( layerMinX, layerMinY, layerWidth, layerHeight, new Appearance( new Color( 40, 140, 180), new BasicStroke(1f) ) );
		layerBaseElement.setSelectedAppearance( new Appearance( new Color( 0, 0, 255 ), new BasicStroke(1f) ));
		layerBorderElement = new RectangleElement(	layerMinX, layerMinY, layerWidth, layerHeight, new Appearance( new Color( 0, 30, 180), new BasicStroke(1f) ) );
		layerBorderElement.setFocusAppearance( new Appearance( Color.blue, new BasicStroke(3f) ) );
		
		//Magnet to Ruller - It's position will not change
		FillOvalElement layerToRullerMagnetElement = new FillOvalElement( 0.0, 0.0, LAYER_TO_RULE_MAGNET_RADIUS, new Appearance( LAYER_TO_RULE_MAGNET_COLOR, new BasicStroke(1) ) );
		Magnet toRullerSouthMagnet = new Magnet( layerSprite, LAYER_TO_RULER_MAGNET, 180.0, new RangeValueInPixel( 20, 20 ), new PositionValue( 0, 0 ) );
		toRullerSouthMagnet.addPossibleMagnetTypeToConnect( RULER_TO_LAYER_MAGNET );
		toRullerSouthMagnet.addElement( layerToRullerMagnetElement );
		layerSprite.addMagnet( toRullerSouthMagnet );		
		
		layerSprite.addElement( layerBaseElement );
		layerSprite.addElement( layerBorderElement );
		
		//Add the Layer to the Canvas
		networkCanvas.addSprite(layerSprite);
		
	}
	
	public void addNeuronContainer( NeuronContainer neuronContainer ){
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
		
		//New Magnet to Neuron
		FillOvalElement layerToNeuronMagnetElement = new FillOvalElement( 0.0, 0.0, LAYER_TO_NEURON_MAGNET_RADIUS, new Appearance( LAYER_TO_MEURON_MAGNET_COLOR, new BasicStroke(1) ) );
		Magnet toNeuronEastMagnet = new Magnet( layerSprite, NeuronContainer.LAYER_TO_NEURON_MAGNET, 90.0, new RangeValueInPixel( 20, 20 ), new PositionValue( toNeuronMagnetX, toNeuronMagnetY ) );
		toNeuronEastMagnet.addPossibleMagnetTypeToConnect( NeuronContainer.NEURON_TO_LAYER_MAGNET );
		toNeuronEastMagnet.addElement( layerToNeuronMagnetElement );
		
		layerSprite.addMagnet( toNeuronEastMagnet );

		
		//Set position of the Layer
		//layerSprite.setPosition( 0, 0 );
		
		//Change position of the Layers's Magnets and Neurons
		i = 0;
		ArrayList<Magnet> magnetList = layerSprite.getMagnetList();
		for( Magnet magnet: magnetList ){
			if( magnet.getType().equals( NeuronContainer.LAYER_TO_NEURON_MAGNET ) ){
				
				toNeuronMagnetY = baseToNeuronMagnetY + i * NeuronContainer.SPACE_FOR_ONE_NEURON;
				
				//New positions for the Neurons
				Sprite neuronSprite = neuronList.get( i ).getNeuron();
				neuronSprite.setPosition( layerSprite.getPosition().getX() + toNeuronMagnetX, layerSprite.getPosition().getY() + toNeuronMagnetY );	
			
				//New position for the Magnet
				magnet.setRelativePositionToSpriteZeroPoint( new PositionValue( toNeuronMagnetX, toNeuronMagnetY ) );
				
				i++;
			}
		}
		
		//Make the connection between Layer and Neurons
		networkCanvas.doArangeSpritePositionByMagnet( layerSprite );
	}
	
	/**
	 * When the Ruler position is changed (typically when new layer added) it will
	 * automatically call this method to change the Layers position regarding
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition( double x, double y ){
		
		//Change the Layer position
		layerSprite.setPosition( new PositionValue(x, y));
		
		//Chang it's Neuron's positions
		for( NeuronContainer neuronContainer: neuronList){
			ArrayList<Magnet> magnetList = neuronContainer.getNeuron().getMagnetList();
			//I expecting ONLY 1 Magnet for every Neuron
			for( Magnet magnet: magnetList ){
				Magnet layerMagnet = magnet.getConnectedTo();
				PositionValue magnetRelPos = layerMagnet.getRelativePositionToSpriteZeroPoint();
				neuronContainer.setPosition( x + magnetRelPos.getX(), y + magnetRelPos.getY() );				
			}
		}
	
		//Make the connection between Layer and Neurons
		networkCanvas.doArangeSpritePositionByMagnet( layerSprite );
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

/**
 * 
 * @author akoel
 *
 */
class LayerContainerList{
	public static final int LEVEL_RULER = 0;
	
	private static final double DISTANCE_OF_LAYERS = 5.0;
	private static final double RULER_HEIGHT = 0.2;	//TODO modify it to ZERO
	
	private static final Color RULER_MAGNET_COLOR = Color.green;
	private static final double RULER_MAGNET_RADIUS = 0.04;
	
	private NetworkCanvas networkCanvas;
	private ArrayList<LayerContainer> layerContainerList = new ArrayList<LayerContainer>();
	private Sprite rulerSprite;
	private FillRectangleElement layerBaseElement;
	//private RectangleElement layerBorderElement;
	
	private double getXPosition( double indexOfLayer ){
		return indexOfLayer * DISTANCE_OF_LAYERS;
	}
	
	public LayerContainerList( NetworkCanvas networkCanvas ){
		this.networkCanvas = networkCanvas;
		
		//Ruler has no height so it is invisible !
		double layerHeight = RULER_HEIGHT;  
		double layerWidth = 0; //getXPosition( 0 ); 
		double layerMinY = -( layerHeight / 2 );
		double layerMinX = 0;
		
		rulerSprite = new Sprite( LEVEL_RULER, new SizeValue( layerMinX, layerMinY, layerMinX + layerWidth, layerMinY + layerHeight ) );
		
		layerBaseElement = new FillRectangleElement( layerMinX, layerMinY, layerWidth, layerHeight, new Appearance( new Color( 40, 140, 180), new BasicStroke(1f) ) );
		layerBaseElement.setSelectedAppearance( new Appearance( new Color( 0, 0, 255 ), new BasicStroke(1f) ));
//		layerBorderElement = new RectangleElement(	layerMinX, layerMinY, layerWidth, layerHeight, new Appearance( new Color( 0, 30, 180), new BasicStroke(1f) ) );
//		layerBorderElement.setFocusAppearance( new Appearance( Color.blue, new BasicStroke(3f) ) );
		
		rulerSprite.addElement( layerBaseElement );
//		rullerSprite.addElement( layerBorderElement );
		
		//Default position of the Ruler
		rulerSprite.setPosition( 0, 0 );
		
		//Add the Layer to the Canvas
		networkCanvas.addSprite( rulerSprite );		
	}
	
	public void addLayerContainer( LayerContainer layerContainer ){
		this.layerContainerList.add( layerContainer );
		networkCanvas.addSprite( layerContainer.getLayer() );

		//Ruler has no height so it is invisible !!
		double layerHeight = RULER_HEIGHT;
		double layerWidth = getXPosition( layerContainerList.size() - 1 );
		double layerMinY = -( layerHeight / 2 );
		double layerMinX = 0;
		
		rulerSprite.setBoundBoxXMin( layerMinX );
		rulerSprite.setBoundBoxXMax( layerMinX + layerWidth );
		rulerSprite.setBoundYMin( layerMinY );
		rulerSprite.setBoundYMax( layerMinY + layerHeight );

		layerBaseElement.setSize( layerMinX, layerMinY, layerWidth, layerHeight );
//		layerBorderElement.setSize( layerMinX, layerMinY, layerWidth, layerHeight );
		
		//Add a new magnet
		int i = layerContainerList.indexOf( layerContainer ); //layerContainerList.size() - 1;
		double toLayerMagnetY = 0;
		double toLayerMagnetX = getXPosition( i );
		FillOvalElement rullerToLayerMagnetElement = new FillOvalElement( 0.0, 0.0, RULER_MAGNET_RADIUS, new Appearance( RULER_MAGNET_COLOR, new BasicStroke(1) ) );
			
		//Magnet to Layer
		Magnet toLayerNorthMagnet = new Magnet(rulerSprite, LayerContainer.RULER_TO_LAYER_MAGNET, 0.0, new RangeValueInPixel( 20, 20 ), new PositionValue( toLayerMagnetX, toLayerMagnetY ) );
		toLayerNorthMagnet.addPossibleMagnetTypeToConnect( LayerContainer.LAYER_TO_RULER_MAGNET );
		toLayerNorthMagnet.addElement( rullerToLayerMagnetElement );

		rulerSprite.addMagnet( toLayerNorthMagnet );
		
		//Change the position of the last placed Layer to the last position
		layerContainer.setPosition( 
				rulerSprite.getPosition().getX() + toLayerMagnetX, 
				rulerSprite.getPosition().getY() + toLayerMagnetY );		
		
		//Make the connection between Layer and Neurons
		networkCanvas.doArangeSpritePositionByMagnet( rulerSprite );		
	}

	private void fixThePositions(){
		
		//Delete Magnet connections between Ruler and Layers
		Collection<Magnet> magnetsToRemove = new HashSet<Magnet>();
		Iterator<Magnet> magnetIterator = rulerSprite.getMagnetList().iterator();
		while( magnetIterator.hasNext() ){
			Magnet magnet = magnetIterator.next();
			if( magnet.getType().equals( LayerContainer.RULER_TO_LAYER_MAGNET ) ){
				magnet.setConnectedTo( null );
				magnetsToRemove.add( magnet );
			}
		}
		
		//Delete Ruler's Magnets to Layer
		ArrayList<Magnet> magnetListOfRuler = rulerSprite.getMagnetList();
		magnetListOfRuler.removeAll( magnetsToRemove );
		
		//Resize the Ruler
		double layerHeight = RULER_HEIGHT;
		double layerWidth = getXPosition( layerContainerList.size() - 1 );
		double layerMinY = -( layerHeight / 2 );
		double layerMinX = 0;
		
		rulerSprite.setBoundBoxXMin( layerMinX );
		rulerSprite.setBoundBoxXMax( layerMinX + layerWidth );
		rulerSprite.setBoundYMin( layerMinY );
		rulerSprite.setBoundYMax( layerMinY + layerHeight );

		layerBaseElement.setSize( layerMinX, layerMinY, layerWidth, layerHeight );
		
		//Repositioning the layers
		for( int i = 0; i < layerContainerList.size(); i++ ){
			double toLayerMagnetX = getXPosition( i );
			double toLayerMagnetY = 0;			

			FillOvalElement rullerToLayerMagnetElement = new FillOvalElement( 0.0, 0.0, RULER_MAGNET_RADIUS, new Appearance( RULER_MAGNET_COLOR, new BasicStroke(1) ) );

			//Magnet to Layer
			Magnet toLayerNorthMagnet = new Magnet(rulerSprite, LayerContainer.RULER_TO_LAYER_MAGNET, 0.0, new RangeValueInPixel( 20, 20 ), new PositionValue( toLayerMagnetX, toLayerMagnetY ) );
			toLayerNorthMagnet.addPossibleMagnetTypeToConnect( LayerContainer.LAYER_TO_RULER_MAGNET );
			toLayerNorthMagnet.addElement( rullerToLayerMagnetElement );

			rulerSprite.addMagnet( toLayerNorthMagnet );
			
			//Reposition the Layer
			layerContainerList.get( i ).setPosition( rulerSprite.getPosition().getX() + toLayerMagnetX, rulerSprite.getPosition().getY() + toLayerMagnetY );
								
		}
		//Make the connection between Layer and Neurons
		networkCanvas.doArangeSpritePositionByMagnet( rulerSprite );
		
	}
	
	public Iterator<LayerContainer> getIterator(){
		return layerContainerList.iterator();
	}
	
	public LayerContainer getLayerContainer( int index ){
		return layerContainerList.get( index );
	}
	
	public void deleteLayer( LayerContainer layerContainer ){

		//Remove NeuronSprites from Canvas
		Iterator<NeuronContainer> neuronIterator = layerContainer.getIterator();
		while( neuronIterator.hasNext() ){
			NeuronContainer neuronContainer = neuronIterator.next();
			networkCanvas.removeSprite( neuronContainer.getNeuron() );
		}
		
		//Remove LayerSprite from Canvas
		networkCanvas.removeSprite( layerContainer.getLayer() );
		
		//Remove LayerContainer from the LayerConatinerList
		layerContainerList.remove( layerContainer );
		
		//Fix the Ruler's size/magnets + repositioning Layers
		fixThePositions();
		
	}
}









/**
 * 
 * @author akoel
 *
 */
public class ConstructionTab  extends JPanel{
	private static final long serialVersionUID = -102436776877486921L;
	
	private NetworkCanvas networkCanvas;
	private ConstructionDataModel dataModel;
	
	private LayerContainerList layerContainerList; 
	
	public ConstructionTab( NetworkCanvas networkCanvas, ConstructionDataModel dataModel ){
		super();
		
		this.networkCanvas = networkCanvas;
		this.dataModel = dataModel;
		
		this.setBorder( BorderFactory.createLoweredBevelBorder());
		this.setLayout( new GridBagLayout());
		GridBagConstraints controlConstraints = new GridBagConstraints();		
		
		//Place down the Ruller
		layerContainerList = new LayerContainerList( networkCanvas );
		networkCanvas.revalidateAndRepaintCoreCanvas();
		
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
		
		// Delete Layer button
		//TODO translate it
		JButton deleteLayerButton = new JButton( "Delete Layer" );
		deleteLayerButton.setEnabled( true );
		//placeNewLayerButton.setBackground( Color.green );
		
		placeNewLayerButton.addActionListener( new PlaceNewLayerButtonListener( networkCanvas, this, dataModel ) ); 
		insertNewNeuronButton.addActionListener( new InsertNewNeuronButtonListener( networkCanvas, this, dataModel ) );
		deleteLayerButton.addActionListener( new DeleteLayerButtonListener( networkCanvas, this, dataModel ) ); 
		
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
		
		// Delete Layer button
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 0;
		controlConstraints.gridwidth = 2;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( deleteLayerButton, controlConstraints );

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

	public LayerContainerList getLayerContainerList(){
		return layerContainerList;
	}
	
	
	public void insertNeuron( NeuronDescriptor neuronDescriptor, LayerContainer actualLayer ){

		NeuronContainer neuronContainer = new NeuronContainer( neuronDescriptor );
		
		//layerContainerList.getLayerContainer( actualLayer ).addNeuronContainer( neuronContainer );
		actualLayer.addNeuronContainer( neuronContainer );
		
		networkCanvas.revalidateAndRepaintCoreCanvas();
	}
	
/*	private void constructLayer( NeuronDescriptor neuronDescriptor ){
		constructLayer( neuronDescriptor, 0 );
	}
*/	
	public void constructLayer( NeuronDescriptor neuronDescriptor, int numberOfNeurons ){
		
		//Create a new Empty Layer
		LayerContainer layerContainer = new LayerContainer(networkCanvas);
		
		//Added to the List
		layerContainerList.addLayerContainer(layerContainer);
		
		for( int i = 0; i < numberOfNeurons; i++ ){
			insertNeuron( neuronDescriptor, layerContainer );
		}
		
		//Canvas refreshed
		networkCanvas.revalidateAndRepaintCoreCanvas();
	}
	
	public void deleteLayer( LayerContainer actualLayer ){

		layerContainerList.deleteLayer( actualLayer );
		
		networkCanvas.revalidateAndRepaintCoreCanvas();
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

		LayerContainerList layerList = constructionTab.getLayerContainerList();
		LayerContainer layer = layerList.getLayerContainer( dataModel.actualLayer.getValue() );

		//constructionTab.insertNeuron( new NeuronDescriptor( NeuronType.NORMAL, TransferFunction.SIGMOID ), dataModel.actualLayer.getValue() );
		constructionTab.insertNeuron( new NeuronDescriptor( NeuronType.NORMAL, TransferFunction.SIGMOID ), layer );
		
		//A Canvas ujrarajzolasa, az uj Sprite megjelenites miatt
		//networkCanvas.revalidateAndRepaintCoreCanvas();		
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

		constructionTab.constructLayer( new NeuronDescriptor( NeuronType.NORMAL, TransferFunction.SIGMOID ), 2 );
		
		//A Canvas ujrarajzolasa, az uj Sprite megjelenites miatt
		//networkCanvas.revalidateAndRepaintCoreCanvas();		
	}
}

class DeleteLayerButtonListener implements ActionListener{
	private NetworkCanvas networkCanvas;
	private ConstructionTab constructionTab;
	private ConstructionDataModel dataModel;

	public DeleteLayerButtonListener( NetworkCanvas networkCanvas, ConstructionTab constructionTab, ConstructionDataModel dataModel ){
		this.networkCanvas = networkCanvas;
		this.constructionTab = constructionTab;
		this.dataModel = dataModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		LayerContainerList layerList = constructionTab.getLayerContainerList();
		LayerContainer layer = layerList.getLayerContainer( dataModel.actualLayer.getValue() );

		constructionTab.deleteLayer( layer );
		
		//A Canvas ujrarajzolasa, az uj Sprite megjelenites miatt
		//networkCanvas.revalidateAndRepaintCoreCanvas();		
	}
}


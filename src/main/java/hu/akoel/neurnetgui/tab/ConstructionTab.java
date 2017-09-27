package hu.akoel.neurnetgui.tab;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import hu.akoel.neurnetgui.NeuronDescriptor;
import hu.akoel.neurnetgui.NeuronDescriptor.NeuronType;
import hu.akoel.neurnetgui.NeuronDescriptor.TransferFunction;
import hu.akoel.neurnetgui.datamodels.ConstructionDataModel;
import hu.akoel.neurnetgui.networkcanvas.NetworkCanvas;
import hu.akoel.neurnetgui.verifiers.IntegerVerifier;

/**
 * 
 * @author akoel
 *
 */
public class ConstructionTab  extends JPanel{
	private static final long serialVersionUID = -102436776877486921L;
	
	private NetworkCanvas networkCanvas;
	private ConstructionDataModel dataModel;
	
	public ConstructionTab( NetworkCanvas networkCanvas, ConstructionDataModel dataModel ){
		super();
		
		this.networkCanvas = networkCanvas;
		this.dataModel = dataModel;
		
		this.setBorder( BorderFactory.createLoweredBevelBorder());
		this.setLayout( new GridBagLayout());
		GridBagConstraints controlConstraints = new GridBagConstraints();		
		
		//Place down the Ruller
		networkCanvas.placeDownRuler();
		
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
		
		placeNewLayerButton.addActionListener( new PlaceNewLayerButtonListener( networkCanvas, dataModel ) ); 
		insertNewNeuronButton.addActionListener( new InsertNewNeuronButtonListener( networkCanvas, dataModel ) );
		deleteLayerButton.addActionListener( new DeleteLayerButtonListener( networkCanvas, dataModel ) ); 
		
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

}


class InsertNewNeuronButtonListener implements ActionListener{
	private NetworkCanvas networkCanvas;
	private ConstructionDataModel dataModel;
	
	public InsertNewNeuronButtonListener( NetworkCanvas networkCanvas, ConstructionDataModel dataModel ){
		this.networkCanvas = networkCanvas;
		this.dataModel = dataModel;
	}
	
	public void actionPerformed(ActionEvent e) {

		networkCanvas.insertNeuron( new NeuronDescriptor( NeuronType.NORMAL, TransferFunction.SIGMOID ), dataModel.actualLayer.getValue() );
		
	}	
}

class PlaceNewLayerButtonListener implements ActionListener{
	private NetworkCanvas networkCanvas;
	private ConstructionDataModel dataModel;

	public PlaceNewLayerButtonListener( NetworkCanvas networkCanvas, ConstructionDataModel dataModel ){
		this.networkCanvas = networkCanvas;
		this.dataModel = dataModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		networkCanvas.insertLayer( new NeuronDescriptor( NeuronType.NORMAL, TransferFunction.SIGMOID ), 1 );
	}
}

class DeleteLayerButtonListener implements ActionListener{
	private NetworkCanvas networkCanvas;
	private ConstructionDataModel dataModel;

	public DeleteLayerButtonListener( NetworkCanvas networkCanvas, ConstructionDataModel dataModel ){
		this.networkCanvas = networkCanvas;
		this.dataModel = dataModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		networkCanvas.deleteLayer( dataModel.actualLayer.getValue() );
	}
}


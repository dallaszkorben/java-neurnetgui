package hu.akoel.neurnetgui.customelements;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import hu.akoel.neurnetgui.accessories.Common;

public class CustomErrorDialog {
	 
    public static void showDialog( Component parentComponent, String title, String message, String details ) {
        
    	final JPanel content = new JPanel();
    	content.setBorder( new EmptyBorder( 10, 10, 10, 10) );
    	content.setLayout( new GridBagLayout() );
    	final GridBagConstraints controlConstraints = new GridBagConstraints();

    	int row = -1;
    	
    	//
    	// Message
    	//
    	JTextArea messageArea = new JTextArea();
    	messageArea.setText( message );
    	messageArea.setBorder( new EmptyBorder( 10, 10, 10, 10) );
    	messageArea.setBackground( new Color(parentComponent.getBackground().getRGB()) );
    	
    	row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.ipadx = 10;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.BOTH;
		content.add( messageArea, controlConstraints );
		

		final JDialog dialog = new JOptionPane(
                content,
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION).createDialog( parentComponent, title );

        //If there are Details
        if( null != details && !details.trim().isEmpty() ){ 	

    		//
    		// Details
    		//
    		JTextPane detailsPane = new JTextPane();
            detailsPane.setContentType("text/html"); 
            detailsPane.setText(details);
            detailsPane.setEditable(false);
    		final JScrollPane detailsScrollPane = new JScrollPane( detailsPane );
            detailsScrollPane.setAlignmentX(0);
            detailsScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );
            detailsScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
            
        	//
        	// Checkbox
        	//
        	JCheckBox cb = new JCheckBox(new AbstractAction() {
			 
        		private static final long serialVersionUID = 1L;
 
        		{
        			this.putValue(Action.SELECTED_KEY, false);
        			this.putValue(Action.NAME, Common.getTranslated( "exception.csv.details.label.showdetails") );
        		}
 
        		@Override
        		public void actionPerformed(ActionEvent e) {
        			if ((Boolean) this.getValue(Action.SELECTED_KEY)) {
        				content.add( detailsScrollPane, controlConstraints );
        			} else {
        				content.remove( detailsScrollPane );
        			}
        			content.invalidate();
        			dialog.invalidate();
        			dialog.pack();
        		}
        	});		
		
        	row++;
        	controlConstraints.gridx = 0;
        	controlConstraints.gridy = row;
        	controlConstraints.ipadx = 10;
        	controlConstraints.anchor = GridBagConstraints.WEST;
        	controlConstraints.weighty = 0;
        	controlConstraints.fill = GridBagConstraints.NONE;
        	content.add( cb, controlConstraints );

        	//
        	// Details
        	//
        	row++;
        	controlConstraints.gridx = 0;
        	controlConstraints.gridy = row;
        	controlConstraints.ipadx = 10;
        	controlConstraints.anchor = GridBagConstraints.WEST;
        	controlConstraints.weighty = 0;
        	controlConstraints.fill = GridBagConstraints.BOTH;
        	//content.add( detailsScrollPane, controlConstraints );
        }
    	
    	
    	dialog.pack();
        //dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

    }
}

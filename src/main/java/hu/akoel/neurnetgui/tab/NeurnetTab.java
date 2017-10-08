package hu.akoel.neurnetgui.tab;

import javax.swing.JPanel;

public abstract class NeurnetTab extends JPanel{
	private static final long serialVersionUID = 7050532275933173294L;

	public abstract void selected( NeurnetTab previouslySelected );

}

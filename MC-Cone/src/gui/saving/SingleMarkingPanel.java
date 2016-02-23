package gui.saving;


import gui.Color_schema;
import information.Fonts;
import information.MarkingLayer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import operators.CheckBoxIcon;

/**
 * The Class SingleMarkingPanel. Contains name of MarkingLayer, checkboxes to select it and grid.
 */
public class SingleMarkingPanel extends JPanel{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4238874393288341772L;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/** The save check box. */
	private JCheckBox saveCheckBox;
	
	/** The MarkingLayer. */
	protected MarkingLayer mLayer;
	
	/** The marking label panel. */
	private JPanel markingLabelPanel;
	
	/** The JPanel for checkbox to select should GRID to be drawn. */
	private JPanel gridDrawingSelection;


	/**
	 * Instantiates a new single marking panel.
	 *
	 * @param mLayer the MarkingLayer
	 */
	public SingleMarkingPanel(MarkingLayer mLayer){
		this.mLayer=mLayer;

		try {
			this.setOpaque(true);
			this.setMaximumSize(new Dimension(2000,40));
			this.setPreferredSize(new Dimension(400,40));
			this.setMinimumSize(new Dimension(200,40));
			this.setBackground(Color_schema.dark_35);
			this.setBorder(BorderFactory.createLineBorder(Color_schema.dark_50, 1));
			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			Icon checkBoxIcon=new CheckBoxIcon();
			// checkBox for selecting
			saveCheckBox=new JCheckBox(checkBoxIcon);		
			saveCheckBox.setSelected(true);
			saveCheckBox.setBackground(Color_schema.dark_35);
			saveCheckBox.setMaximumSize(new Dimension(25,25));
			saveCheckBox.setPreferredSize(new Dimension(25,25));
			saveCheckBox.setMinimumSize(new Dimension(25,25));
			saveCheckBox.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					try {
						setGridCheckBoxEnableState(((JCheckBox)e.getSource()).isSelected());
					} catch (Exception e1) {
						LOGGER.severe("Error in setting grid check box selection!");
						e1.printStackTrace();
					}
					
				}
			});
		

			// marking title
			JLabel markingLabel = new JLabel(getMarkingName());		
			markingLabelPanel = new JPanel();
			markingLabelPanel.setLayout(new BorderLayout());
			markingLabelPanel.setMaximumSize(new Dimension(2000, 40));
			markingLabel.setFont(Fonts.b15);
			markingLabel.setForeground(Color_schema.white_230);
			int markinglabelwidth=markingLabel.getFontMetrics(Fonts.b15).stringWidth(markingLabel.getText());

			markingLabelPanel.setPreferredSize(new Dimension(markinglabelwidth+50,40));
			markingLabelPanel.add(markingLabel);
			markingLabelPanel.setBackground(Color_schema.dark_35);
			gridDrawingSelection = initDrawGridCheckBoxPanel();

			this.add(Box.createRigidArea(new Dimension(30,0)));
			this.add(saveCheckBox);
			this.add(Box.createRigidArea(new Dimension(5,0)));
			this.add(markingLabelPanel);
			if(gridDrawingSelection != null){
				this.add(Box.createHorizontalGlue());
				this.add(gridDrawingSelection);
			}
		
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * Returns the MarkingLayer.
	 *
	 * @return the marking layer
	 * @throws Exception the exception
	 */
	public MarkingLayer getMarkingLayer() throws Exception {
		return mLayer;
	}

	/**
	 * Returns the ID of MarkingLayer.
	 *
	 * @return the marking layer id
	 * @throws Exception the exception
	 */
	public int getMarkingLayerID() throws Exception{
		return this.mLayer.getLayerID();
	}

	/**
	 * Returns the name of MarkingLayer.
	 *
	 * @return the marking name
	 * @throws Exception the exception
	 */
	public String getMarkingName() throws Exception {
		return this.mLayer.getLayerName();
	}
	
	/**
	 * Initializes the JPanel for Grid CheckBox.
	 *
	 * @return the JPanel
	 * @throws Exception the exception
	 */
	protected JPanel initDrawGridCheckBoxPanel() throws Exception{
		return null;
	}

	/**
	 * Checks if is CheckBox selected.
	 *
	 * @return true, if is selected
	 * @throws Exception the exception
	 */
	public boolean isSelected() throws Exception{
		return this.saveCheckBox.isSelected();
	}



	/**
	 * Sets the background color for showing successfull saving (dark green).
	 *
	 * @param savedSuccessfully the new background color by successfull saving
	 * @throws Exception the exception
	 */
	protected void setBGColorBySuccessfullSaving(boolean savedSuccessfully) throws Exception{
		if(savedSuccessfully){
			this.setBackground(Color_schema.darkest_green);
			this.saveCheckBox.setBackground(Color_schema.darkest_green);
			this.markingLabelPanel.setBackground(Color_schema.darkest_green);
			if(this.gridDrawingSelection != null) // set color of grid panel if found
				this.gridDrawingSelection.setBackground(Color_schema.darkest_green);
		}
		else{
			this.setBackground(Color_schema.dark_35);
			this.saveCheckBox.setBackground(Color_schema.dark_35);
			this.markingLabelPanel.setBackground(Color_schema.dark_35);
			if(this.gridDrawingSelection != null) // set color of grid panel if found
				this.gridDrawingSelection.setBackground(Color_schema.dark_35);
		}
	}

	/**
	 * Sets the CheckBox enable state (enabled / unenabled).
	 *
	 * @param state the new CheckBox state
	 * @throws Exception the exception
	 */
	public void setCheckBoxEnableState(boolean state) throws Exception{			
		this.saveCheckBox.setEnabled(state);
		this.repaint();
	}
	
	/**
	 * Sets the grid check box enable state.
	 *
	 * @param state the new grid check box enable state
	 * @throws Exception the exception
	 */
	protected void setGridCheckBoxEnableState(boolean state) throws Exception{

		if(this.gridDrawingSelection != null){ // set color of grid panel if found
			this.gridDrawingSelection.setVisible(state);
			this.repaint();
		}
				
	}


	/**
	 * Sets the CheckBox selected.
	 *
	 * @param selected the new selected
	 * @throws Exception the exception
	 */
	public void setSelected(boolean selected) throws Exception{
		this.saveCheckBox.setSelected(selected);
		this.repaint();
	}
	

}

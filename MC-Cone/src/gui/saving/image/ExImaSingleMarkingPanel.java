package gui.saving.image;

import java.awt.Dimension;
import java.awt.Insets;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import operators.CheckBoxIcon;
import gui.Color_schema;
import gui.saving.SingleMarkingPanel;
import information.Fonts;
import information.MarkingLayer;

/**
 * The Class ExImaSingleMarkingPanel. Extends SingleMarkingPanel and shows selectors for MarkingLayer to be drawn on image. 
 */
public class ExImaSingleMarkingPanel extends SingleMarkingPanel{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1223670385210740176L;
	
	/** The draw grid check box. */
	private JCheckBox drawGridCheckBox=null;
	
	/** The draw grid panel. */
	private JPanel drawGridPanel=null;

	/** The draw grid label. */
	private JLabel drawGridLabel=null;
	

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");



	/**
	 * Instantiates a new Panel.
	 *
	 * @param mlayer the MarkingLayer
	 */
	public ExImaSingleMarkingPanel(MarkingLayer mlayer){
		super(mlayer);
	}
/*	
	protected void setBGColorBySuccessfullSaving(boolean savedSuccessfully) throws Exception{
		super.setBGColorBySuccessfullSaving(savedSuccessfully);
		if(this.drawGridCheckBox != null && this.drawGridLabel != null && this.drawGridPanel != null){
			if(savedSuccessfully){
	
				this.drawGridCheckBox.setBackground(Color_schema.darkest_green);
				this.drawGridLabel.setBackground(Color_schema.darkest_green);
			}
			else{
	
				this.drawGridCheckBox.setBackground(Color_schema.dark_35);
				this.drawGridPanel.setBackground(Color_schema.dark_35);
				this.drawGridLabel.setBackground(Color_schema.dark_35);
			}
		}
	}
*/
	/* (non-Javadoc)
	 * @see gui.saving.SingleMarkingPanel#initDrawGridCheckBoxPanel()
	 */
	protected JPanel initDrawGridCheckBoxPanel(){
		try {
			drawGridCheckBox=null;
			if(this.mLayer.getGridProperties() != null && this.mLayer.getGridProperties().isGridON()){
			drawGridPanel = new JPanel();
			drawGridPanel.setMaximumSize(new Dimension(110,40));
			drawGridPanel.setPreferredSize(new Dimension(110,40));
			drawGridPanel.setMinimumSize(new Dimension(110,40));
			drawGridPanel.setLayout(new BoxLayout(drawGridPanel, BoxLayout.LINE_AXIS));
			drawGridCheckBox = new JCheckBox(new CheckBoxIcon());
			drawGridCheckBox.setSelected(false);
			drawGridCheckBox.setBackground(Color_schema.dark_40);
			drawGridCheckBox.setMaximumSize(new Dimension(20,20));
			drawGridCheckBox.setPreferredSize(new Dimension(20,20));
			drawGridCheckBox.setMinimumSize(new Dimension(20,20));
			drawGridCheckBox.setMargin(new Insets(0, 0, 0, 0));

			drawGridLabel = new JLabel("Draw Grid");
			drawGridLabel.setFont(Fonts.p15);
			drawGridLabel.setForeground(Color_schema.white_180);
			drawGridPanel.add(Box.createRigidArea(new Dimension(5,0)));
			drawGridPanel.add(drawGridCheckBox);
			drawGridPanel.add(Box.createRigidArea(new Dimension(5,0)));
			drawGridPanel.add(drawGridLabel);
			drawGridPanel.add(Box.createRigidArea(new Dimension(5,0)));

			return drawGridPanel;
			}
			else
				return null;
		} catch (Exception e) {
			LOGGER.severe("Error in initializing grid check box panel!");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Checks if CheckBox selected for drawing MarkingLayer on image.
	 *
	 * @return true, if CheckBox is selected
	 * @throws Exception the exception
	 */
	protected boolean isDrawCheckBoxSelected() throws Exception{
		if(this.drawGridCheckBox != null){
			return this.drawGridCheckBox.isSelected();
		}
		return false;
	}


}

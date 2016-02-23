package gui;

import information.Fonts;
import information.ID;
import information.MarkingLayer;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * Class GlobalMarkingProperties shows window, where user can set MarkingLayer Properties: Marking color, shape, size, thickness and transparency.
 * Given settings will affect to all MarkingLayers in all ImageLayers.
 * @author Antti Kurronen
 *
 */
public class GlobalMarkingProperties extends MarkingProperties{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4126323147844935368L;

	/**
	 * Class constructor.
	 * @param frame JFrame parent frame
	 * @param gui GUI main window of program
	 * @param point Point where mouse pressed button to open this window
	 * @param markingLayerList ArrayList of MarkingLayers, which settings will be modified.
	 */
	public GlobalMarkingProperties(JFrame frame, GUI gui, Point point, ArrayList<MarkingLayer> markingLayerList) {
		super(frame, gui, point, markingLayerList);
	}
	/**
	 * Hides Dialog window and saves the changes made to MarkingLayer.
	 * @param saveChanges boolean value should the changes be saved to MarkingLayer
	 */
	protected void hideDialog(boolean saveChanges){
		try {
			//LOGGER.fine("color selected: " +colorChooser.getColor().toString());
			if(this.markingLayerList != null && this.markingLayerList.size()>0 && saveChanges){
					try {
						Iterator<MarkingLayer> mIterator = this.markingLayerList.iterator();
						while(mIterator.hasNext()){

							saveChanges(mIterator.next());
						}

					} catch (Exception e) {
						LOGGER.severe("Error in saving marking properties to MarkingLayer " +e.getClass().toString() + " :" +e.getMessage());

					}
					// update the GUI ImageLayerInfo JPanel
					gui.updateAllMarkingPanelProperties();
					
				}
		} catch (Exception e) {
			LOGGER.severe("Error in saving changes in Marking properties");
			e.printStackTrace();
		}
		this.setVisible(false);
		dispose();
	}

	/* (non-Javadoc)
	 * @see gui.MarkingProperties#initMarkingPropertiesPanel()
	 */
	protected void initMarkingPropertiesPanel(){

		try {
			if(this.markingLayerList != null && this.markingLayerList.size()>0){
				MarkingLayer mLayer = this.markingLayerList.get(0);
				if(mLayer != null){

					this.setSelectedColor(mLayer.getColor());
					this.setSelectedSize(mLayer.getSize());
					this.setSelectedThickness(mLayer.getThickness());
					this.setSelectedOpacity(changeUnderZeroFloatToInt(mLayer.getOpacity()));
					this.setSelectedShapeID(mLayer.getShapeID());
				}
				else{
					gui.showMessage("Could not set ImageLayer Properties", "Not any MarkingLayers found. Not changed properties.",ID.OK);
					hideDialog(false);
				}
			}
			else{
				gui.showMessage("Could not set ImageLayer Properties", "Not any MarkingLayers found. Not changed properties.",ID.OK);
				hideDialog(false);
			}
			this.panelHeight=300;
			initDialog();
			this.revalidate();
			this.repaint();
		} catch (Exception e) {
			LOGGER.severe("Error in initialozing Panel");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see gui.MarkingProperties#initUPPanels()
	 */
	protected JPanel initUPPanels(){
		try {
			// contains title and colorchooser panels
						JPanel upperBackPanel= new JPanel();
						upperBackPanel.setLayout(new BoxLayout(upperBackPanel, BoxLayout.PAGE_AXIS));
						upperBackPanel.setMaximumSize(new Dimension(panelWidth,60));
						upperBackPanel.setMinimumSize(new Dimension(panelWidth,60));
						upperBackPanel.setPreferredSize(new Dimension(panelWidth,60));
						upperBackPanel.add(initTitlePanel("EDIT MARKING PROPERTIES"));

						JPanel infoPanel=new JPanel();
						infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
						infoPanel.setMaximumSize(new Dimension(panelWidth,30));
						infoPanel.setMinimumSize(new Dimension(panelWidth,30));
						infoPanel.setPreferredSize(new Dimension(panelWidth,30));

						JLabel info = new JLabel("Sets properties of all MarkingLayers");
						info.setFont(Fonts.b14);
						infoPanel.add(info);

						upperBackPanel.add(infoPanel);
						return upperBackPanel;
		} catch (Exception e) {
			LOGGER.severe("Error in initialozing UpperPanel");
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see gui.MarkingProperties#saveChanges(information.MarkingLayer)
	 */
	protected void saveChanges(MarkingLayer mLayer){
		try {
			if(this.getSelectedSize()>0){
				// set size value
				mLayer.setSize(this.getSelectedSize());
			}

			if(this.getSelectedThickness()>0){
				// set thickness value
				mLayer.setThickness(this.getSelectedThickness());
			}

			if(this.getSelectedOpacity()> 0.0F){
				// set opacity value
				mLayer.setOpacity(changeIntToFloat(this.getSelectedOpacity()));
			}
		} catch (Exception e) {
			LOGGER.severe("Error in saving changes of Marking Properties!");
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see gui.MarkingProperties#setUpComboBoXPanel()
	 */
	protected void setUpComboBoXPanel() throws Exception{
		comboBoxPanel=null;
	}

}

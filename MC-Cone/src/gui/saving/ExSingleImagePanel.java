package gui.saving;


import javax.swing.JPanel;

import information.ImageLayer;

/**
 * The Class ExSingleImagePanel. Export Single Image Panel.
 */
public class ExSingleImagePanel extends SingleImagePanel{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 219163881842016069L;


	/**
	 * Instantiates a new ExSingleImagePanel.
	 *
	 * @param imageLayer the ImageLayer
	 * @param saverDialog the saver dialog
	 */
	public ExSingleImagePanel(ImageLayer imageLayer, SaverDialog saverDialog) {
		super(imageLayer, saverDialog);
		
	}
	
	/* (non-Javadoc)
	 * @see gui.saving.SingleImagePanel#initFilePathPanelWithLabel()
	 */
	protected JPanel initFilePathPanelWithLabel() throws Exception{
		return null;
	}

	/* (non-Javadoc)
	 * @see gui.saving.SingleImagePanel#setProperFileForMarkings(java.lang.String, boolean, int)
	 */
	public void setProperFileForMarkings(String properFileForMarkings, boolean showMessage, int fileValidityID) throws Exception{
		// do nothing
	}

}

/*
 * 
 */
package gui.saving;

import javax.swing.JPanel;
import information.ImageLayer;

/**
 * The Class SMSingleImagePanel extends SingleImagePanel for showing information when saving markings.
 */
public class SMSingleImagePanel extends SingleImagePanel{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3848111232129859454L;

	/**
	 * Instantiates a new SM single image panel.
	 *
	 * @param imageLayer the image layer
	 * @param saverDialog the saver dialog
	 */
	public SMSingleImagePanel(ImageLayer imageLayer, SaverDialog saverDialog) {
		super(imageLayer, saverDialog);
		
	}

	/* (non-Javadoc)
	 * @see gui.saving.SingleImagePanel#initFilePathPanelWithLabel()
	 */
	protected JPanel initFilePathPanelWithLabel() throws Exception{
		return initFilePathPanel("Browse xml-file");
	}

}

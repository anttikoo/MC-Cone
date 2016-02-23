package gui.saving;

import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JFrame;
import operators.XMLreadManager;

/**
 * The Class MarkingsSelectFileDialog.
 */
public class MarkingsSelectFileDialog extends SelectFileDialog {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1477952851342793486L;
	
	/** The names of ImageLayers for xml search. */
	private ArrayList<String> imageLayerNamesForXMLSearch;


	/**
	 * Instantiates a dialog for selecting xml-files where to save markings.
	 *
	 * @param frame the frame
	 * @param path the path
	 * @param backPanel the back panel
	 * @param fileType the file type
	 */
	public MarkingsSelectFileDialog(JFrame frame, String path, JComponent backPanel, int fileType) {
		super(frame, path, backPanel, fileType);
		
	}

	/* (non-Javadoc)
	 * @see gui.saving.SelectFileDialog#hasImageLayersFound(java.lang.String)
	 */
	protected boolean hasImageLayersFound(String fileName) throws Exception{
		XMLreadManager readManager=new XMLreadManager();
		return readManager.foundImageLayer(fileName, imageLayerNamesForXMLSearch);
	}

	/* (non-Javadoc)
	 * @see gui.saving.SelectFileDialog#setImageLayerNamesForXMLSearch(java.util.ArrayList)
	 */
	public void setImageLayerNamesForXMLSearch(ArrayList<String> list) throws Exception{
		this.imageLayerNamesForXMLSearch=list;
	}

}

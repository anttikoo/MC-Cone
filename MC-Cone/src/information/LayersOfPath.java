package information;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * The Class LayersOfPath. Contains list of ImageLayers and file path where the data of ImageLayers are saved. 
 */
public class LayersOfPath {
	
	/** The xmlpath. */
	private String xmlpath;
	
	/** The list of ImageLayers. */
	private ArrayList<ImageLayer> imageLayerList;
	
	/** The file state. */
	private int fileState;
	
	/** The overwrite file. */
	private boolean overwriteFile=false;
	
	/** The Constant LOGGER. */
	final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/**
	 * Instantiates a new LayersOfPath.
	 *
	 * @param path the file path
	 * @param state the state of file
	 * @param overwrite the boolean allowing to overwrite
	 */
	public LayersOfPath(String path, int state, boolean overwrite){
		try {
			this.setXmlpath(path);
			this.imageLayerList=new ArrayList<ImageLayer>();
			this.fileState=state;
			this.setOverwriteFile(overwrite);
		} catch (Exception e) {
			LOGGER.severe("Error in initialing Layers Of Path");
			e.printStackTrace();
		}
		
	}

	/**
	 * Adds the ImageLayer to list.
	 *
	 * @param imageLayer the ImageLayer
	 */
	public void addImageLayer(ImageLayer imageLayer) {
		this.imageLayerList.add(imageLayer);
	}

	/**
	 * Returns the file state.
	 *
	 * @return the file state
	 */
	public int getFileState() {
		return fileState;
	}

	/**
	 * Returns the list of ImageLayers.
	 *
	 * @return the list of ImageLayers
	 */
	public ArrayList<ImageLayer> getImageLayerList() {
		return imageLayerList;
	}
	
	/**
	 * Returns the file path of xml-file.
	 *
	 * @return the xml-file path
	 */
	public String getXmlpath() {
		return xmlpath;
	}

	/**
	 * Checks if is overwrite file.
	 *
	 * @return true, if is overwrite file
	 * @throws Exception the exception
	 */
	public boolean overwriteFile() throws Exception {
		return overwriteFile;
	}

	/**
	 * Sets the list of ImageLayers.
	 *
	 * @param imageLayerList the new list of ImageLayers
	 */
	public void setImageLayerList(ArrayList<ImageLayer> imageLayerList) {
		this.imageLayerList = imageLayerList;
	}

	/**
	 * Sets the overwrite file.
	 *
	 * @param overwriteFile the new overwrite file
	 * @throws Exception the exception
	 */
	public void setOverwriteFile(boolean overwriteFile) throws Exception {
		this.overwriteFile = overwriteFile;
	}

	/**
	 * Sets the xml-file path.
	 *
	 * @param xmlpath the new xml-file path
	 */
	public void setXmlpath(String xmlpath) {
		this.xmlpath = xmlpath;
	}
}

package information;

import java.util.ArrayList;

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
	
	/**
	 * Instantiates a new LayersOfPath
	 *
	 * @param path the path
	 * @param state the state
	 */
	public LayersOfPath(String path, int state){
		this.setXmlpath(path);
		this.imageLayerList=new ArrayList<ImageLayer>();
		this.fileState=state;
		
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
	 * Sets the list of ImageLayers.
	 *
	 * @param imageLayerList the new list of ImageLayers
	 */
	public void setImageLayerList(ArrayList<ImageLayer> imageLayerList) {
		this.imageLayerList = imageLayerList;
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

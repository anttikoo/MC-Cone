package information;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;
import operators.MarkingLayerComparator;

/**
 * The Class ImageLayer. Contains data: unique ID, image paths, MarkingLayers. 
 */
public class ImageLayer {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The ID of ImageLayer. */
	private int layerID;
	
	/** The image file path. */
	private String imageFilePath;
	
	/** The markings file path. */
	private String markingsFilePath;
	
	/** The export image path. */
	private String exportImagePath;
	
	/** The marking layer list. */
	private ArrayList<MarkingLayer> markingLayerList;
	
	/** The is selected. */
	private boolean isSelected=false;
	
	private boolean madeChanges=true;
	


	/**
	 * Instantiates a new ImageLayer. First ID will be set as -1 and later unique ID is given if ImageLayer is finalized.
	 *
	 * @param imagePath the image path
	 */
	public ImageLayer(String imagePath){
		try {
			this.setLayerID(-1); // initialize the layerID to negative by default
			this.setImageFilePath(imagePath);
			this.markingLayerList = new ArrayList<MarkingLayer>();
		} catch (Exception e) {
			LOGGER.severe("Error in initializing ImageLayer!");
			e.printStackTrace();
		}
	}

	/**
	 * Adds a new MarkigLayer by copying all data.
	 *
	 * @param ml the ml
	 */
	public void addMarkingLayer(MarkingLayer ml){

		try {

			// Creates a brand new Markinglayer and copy all values to it from ml because may be deleted elsewhere
			MarkingLayer mark = new MarkingLayer(ml.getLayerName());

			mark.setColor(ml.getColor());
			mark.setCoordinateList(ml.getCoordinateList());
			mark.setShapeID(ml.getShapeID());
			if(ml.getGridProperties() != null)
				mark.setGridProperties(ml.getGridProperties());
			if(ml.getLayerID()>0)
				mark.setLayerID(ml.getLayerID());

			this.markingLayerList.add(mark);
			LOGGER.fine("create marking 4 " + ml.getLayerID() + " " + ml.getLayerName() + " size" + this.markingLayerList.size());

			sortMarkingLayers();
			setMadeChanges(true); // set that has made changes to this ImageLayer
		} catch (Exception e) {
			LOGGER.severe("Error in adding new MarkingLayer " +e.getClass().toString() + " :" +e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Returns the all IDs of MarkingLayers.
	 *
	 * @return the ArrayList of IDs of MarkingLayers
	 */
	public ArrayList<Integer> getAllMarkingLayerIDs(){
		try {
			ArrayList<Integer> mLayerIDlist= new ArrayList<Integer>();

			Iterator<MarkingLayer> mIterator = getMarkingLayers().iterator();
			while(mIterator.hasNext()){
				mLayerIDlist.add(((MarkingLayer)mIterator.next()).getLayerID());
			}
			return mLayerIDlist;
		} catch (Exception e) {
			LOGGER.severe("Error in getting all MarkingLayer IDs!");
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Returns the export image path.
	 *
	 * @return the export image path
	 * @throws Exception the exception
	 */
	public String getExportImagePath() throws Exception {
		return exportImagePath;
	}

	public MarkingLayer getFirstVisibleMarkingLayer(){
		if(this.markingLayerList != null && this.markingLayerList.size()>0){
			Iterator<MarkingLayer> iIterator = this.markingLayerList.iterator();
			while(iIterator.hasNext()){
				MarkingLayer ml = (MarkingLayer)iIterator.next();
				if(ml.isVisible()){ // MarkingLayer name
					return ml;
				}

			}
		}
		return null;
	}

	/**
	 * Returns the folder of image.
	 *
	 * @return the folder of image
	 */
	public String getFolderOfImage(){
		try {
			File file = new File(this.imageFilePath);
			if(file.exists()){
				return file.getParent();
			}
			return "";
		} catch (Exception e) {
			LOGGER.severe("Error in getting Imagefile name from ImageLayer " +e.getClass().toString() + " :" +e.getMessage());
			return "";
		}
	}

	  /**
  	 * Returns the image file name.
  	 *
  	 * @return the image file name
  	 * @throws Exception the exception
  	 */
  	public String getImageFileName() throws Exception{
		return getImageFileName(this.imageFilePath);
	}
  	
	/**
	 * Method returns the name part of file if file exists.
	 *
	 * @param path the path
	 * @return the file name without full path of file.
	 */
	public String getImageFileName(String path){
		try {
			File file = new File(path);
			if(file.exists()){ //exists
				return file.getName();
			}
			return "";
		} catch (Exception e) {
			LOGGER.severe("Error in getting Imagefile name from ImageLayer " +e.getClass().toString() + " :" +e.getMessage());
			return "";
		}
	}

	/**
	 * Returns the image file name without extension.
	 *
	 * @return the image file name without extension
	 */
	public String getImageFileNameWithoutExtension(){
		try {
			File file = new File(this.imageFilePath);
			if(file.exists()){
			//	LOGGER.fine("givin filename: " +file.getName());
				return removeExtension(file.getName());
			}
			return "";
		} catch (Exception e) {
			LOGGER.severe("Error in getting Imagefile name from ImageLayer " +e.getClass().toString() + " :" +e.getMessage());
			return "";
		}
	}

	/**
	 * Returns the image file path.
	 *
	 * @return the image file path
	 * @throws Exception the exception
	 */
	public String getImageFilePath() throws Exception{
		return imageFilePath;
	}

	/**
	 * Returns the image full file path without extension.
	 *
	 * @return the image full file path without extension
	 */
	public String getImageFullFilePathWithoutExtension(){
		try {
			File file = new File(this.imageFilePath);
			if(file.exists()){
			//	LOGGER.fine("givin filename: " +file.getName());
				return removeExtension(file.getAbsolutePath());
			}
			return "";
		} catch (Exception e) {
			LOGGER.severe("Error in getting Imagefile name from ImageLayer " +e.getClass().toString() + " :" +e.getMessage());
			return "";
		}
	}

	/**
	 * Returns the ID of ImageLayer .
	 *
	 * @return the layer id
	 * @throws Exception the exception
	 */
	public int getLayerID() throws Exception {
		return layerID;
	}
	
	/**
	 * Returns the MarkingLayer.
	 *
	 * @param mLayerID ID of MarkingLayer
	 * @return the marking layer
	 */
	public MarkingLayer getMarkingLayer(int mLayerID){
		try {
			// go through imageLayerList and return if layerIDs match
			if (this.markingLayerList != null && this.markingLayerList.size() > 0) {
				Iterator<MarkingLayer> iterator = this.markingLayerList.iterator();
				while (iterator.hasNext()) {
					MarkingLayer ma= (MarkingLayer) iterator.next();
					if (ma.getLayerID() == mLayerID){
						return ma;
					}

				}
			}
			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in finding MarkingLayer " +e.getMessage());
			return null;
		}
	}

	/**
	 * Returns the all  MarkingLayers.
	 *
	 * @return the marking layers
	 * @throws Exception the exception
	 */
	public ArrayList<MarkingLayer> getMarkingLayers() throws Exception{
		return this.markingLayerList;
	}

	/**
	 * Returns the file path where markings are saved (xml-file) previously.
	 *
	 * @return the markings file path
	 * @throws Exception the exception
	 */
	public String getMarkingsFilePath() throws Exception {
		return markingsFilePath;
	}

	/**
	 * Checks for is GRID USED.
	 *
	 * @return true, if successful
	 */
	public boolean hasGridOn(){
		try {
			Iterator<MarkingLayer> mIterator = getMarkingLayers().iterator();
			while(mIterator.hasNext()){
				MarkingLayer layer=mIterator.next();
				if(layer.getGridProperties()!=null && layer.getGridProperties().isGridON())
					return true;
			}

			return false;
		} catch (Exception e) {
			LOGGER.severe("Error in checking is Grid ON!");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Return true if found ID of MarkingLayer from markingLayerList of this ImageLayer.
	 *
	 * @param mLayerID ID of MarkingLayer
	 * @return true, if found MarkingLayer
	 * @throws Exception the exception
	 */
	public boolean hasMarkingLayer(int mLayerID) throws Exception{
		if(getMarkingLayer(mLayerID) != null)
			return true;
		return false;
	}

	/**
	 * Checks for same image name.
	 *
	 * @param iPath the i path
	 * @return true, if successful
	 */
	public boolean hasSameImageName(String iPath){
		try {
			if(iPath != null && iPath.length() >0 )
				if(getImageFileName().equals(getImageFileName(iPath)))
					return true;
			return false;
		} catch (Exception e) {
			LOGGER.severe("Error in comparing names of images!");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Checks if is made changes.
	 *
	 * @return true, if is made changes
	 * @throws Exception the exception
	 */
	public boolean isMadeChanges()throws Exception {
		if(madeChanges)
			return madeChanges;
		else
			return isUnsavedMarkingLayers();
	
	}

	/**
	 * Checks if is MarkingLayer in list of this ImageLayer.
	 *
	 * @param checkMarkingLayer the check marking layer
	 * @return true, if is marking layer in list
	 */
	public boolean isMarkingLayerInList(MarkingLayer checkMarkingLayer){
		try {
			if(this.markingLayerList != null && this.markingLayerList.size()>0){
				Iterator<MarkingLayer> iIterator = this.markingLayerList.iterator();
				while(iIterator.hasNext()){
					MarkingLayer ml = (MarkingLayer)iIterator.next();
					if(ml != null && ml.getLayerName().equalsIgnoreCase(checkMarkingLayer.getLayerName())){
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			LOGGER.severe("Error in searching MarkingLayer from ImageLayer: " +e.getClass().toString() + " :" +e.getMessage());
			return true; // to be on the safe side  return true when error happens
		}
	}



	/**
	 * Checks if is ImageLayer selected.
	 *
	 * @return true, if is selected
	 */
	public boolean isSelected() {
		return isSelected;
	}
	
	/**
	 * Checks if is unsaved marking layers.
	 *
	 * @return boolean is any unsaved MarkingLayer in list
	 */
	private boolean isUnsavedMarkingLayers(){
		try {
			if(this.markingLayerList != null && this.markingLayerList.size()>0){
				Iterator<MarkingLayer> iIterator = this.markingLayerList.iterator();
				while(iIterator.hasNext()){
					MarkingLayer ml = (MarkingLayer)iIterator.next();
					if(ml.isMadeChanges()){ // MarkingLayer visibility
						return true;
					}
				}
			}
			return false; // all saved or no any markingLayers
		} catch (Exception e) {
			LOGGER.severe("Error in checking is unsaved MarkingLayers!");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Checks if is visible marking layers.
	 *
	 * @return true, if is visible marking layers
	 */
		
	public boolean isVisibleMarkingLayers() {
		try {
			if(this.markingLayerList != null && this.markingLayerList.size()>0){
				Iterator<MarkingLayer> iIterator = this.markingLayerList.iterator();
				while(iIterator.hasNext()){
					MarkingLayer ml = (MarkingLayer)iIterator.next();
					if(ml.isVisible()){ // MarkingLayer visibility
						return true;
					}
				}						
			}
			else{
				return false;
			}
			
			return false;
		} catch (Exception e) {
			LOGGER.severe("Error in checking is any visible MarkingLayers in ImageLayer!");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Makes a copy of ImageLayer.
	 *
	 * @return the image layer
	 */
	public ImageLayer makeCopy(){
		try {
			// create new ImageLayer
			ImageLayer copyImageLayer = new ImageLayer(this.getImageFilePath());
			// set filePath of Markings file
			if(markingsFilePath != null && markingsFilePath.length()>0)
				copyImageLayer.setMarkingsFilePath(this.getMarkingsFilePath());
			// create and set copy of MarkingLayer -list
			if(this.markingLayerList != null && this.markingLayerList.size()>0){
				Iterator<MarkingLayer> iIterator = this.markingLayerList.iterator();
				while(iIterator.hasNext()){ // go through MarkingLayers
					MarkingLayer ml = (MarkingLayer)iIterator.next();
					if(ml != null){
						MarkingLayer copyMarkingLayer = ml.makeCopy(); // create copy of MarkingLayer
						if(copyMarkingLayer != null)
							copyImageLayer.addMarkingLayer(copyMarkingLayer);
					}
				}
			}
			return copyImageLayer;
		} catch (Exception e) {
			LOGGER.severe("Error in creating copy of ImageLayer: " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}
	}



	/**
	 * Removes the all marking layers from ImageLayer.
	 *
	 * @throws Exception the exception
	 */
	public void removeAllMarkingLayers() throws Exception{
		this.markingLayerList.clear();
		setMadeChanges(true); // set that has made changes to this ImageLayer
	}

	/**
	 * Removes the extension from file path.
	 *
	 * @param str the String of file path
	 * @return the string of path without extension
	 */
	private String removeExtension (String str) {
	        try {
				// Handle null case specially.
				if (str == null) return null;

				// Get position of last '.'.
				int pos = str.lastIndexOf(".");

				// If there wasn't any '.' just return the string as is.
				if (pos == -1) return str;

				// Otherwise return the string, up to the dot.
				return str.substring(0, pos);
			} catch (Exception e) {
				LOGGER.severe("Error in removingExtension " +e.getClass().toString() + " :" +e.getMessage());
				return str;
			}
	    }

	/**
	 * Removes the marking layer from list of ImageLayer.
	 *
	 * @param markinglayer the markinglayer
	 */
	public void removeMarkingLayer(MarkingLayer markinglayer){
		try {
			if(markinglayer !=null && this.markingLayerList != null && this.markingLayerList.size()>0){
				Iterator<MarkingLayer> iIterator = this.markingLayerList.iterator();
				while(iIterator.hasNext()){
					MarkingLayer ml = (MarkingLayer)iIterator.next();
					if(ml.getLayerName().equals(markinglayer.getLayerName())){ // MarkingLayer name
						iIterator.remove();
						setMadeChanges(true); // set that has made changes to this ImageLayer
					}

				}
				sortMarkingLayers();
				
			}
		} catch (Exception e) {
			LOGGER.severe("Error in removing MarkingLayer!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the export image path.
	 *
	 * @param exportImagePath the new export image path
	 * @throws Exception the exception
	 */
	public void setExportImagePath(String exportImagePath) throws Exception{
		this.exportImagePath = exportImagePath;
	}

	/**
	 * Sets the image file path.
	 *
	 * @param imageFilePath the new image file path
	 * @throws Exception the exception
	 */
	public void setImageFilePath(String imageFilePath) throws Exception {
		this.imageFilePath = imageFilePath;
	}

	/**
	 * Sets the ImageLayer ID.
	 *
	 * @param layerID the new layer id
	 * @throws Exception the exception
	 */
	public void setLayerID(int layerID) throws Exception{
		this.layerID = layerID;
		setMadeChanges(true); // set that has made changes to this ImageLayer
	}

	public void setMadeChanges(boolean madeChanges) {
		try {
			this.madeChanges = madeChanges;
		} catch (Exception e) {
			LOGGER.severe("Error in adding information has any changes made!");
			e.printStackTrace();
		}
	}

	/**
	 * Sets the list of MarkingLayers.
	 *
	 * @param markingLayerList the new marking layer list
	 * @throws Exception the exception
	 */
	public void setMarkingLayerList(ArrayList<MarkingLayer> markingLayerList) throws Exception{
		this.markingLayerList = markingLayerList;
		setMadeChanges(true); // set that has made changes to this ImageLayer
	}
	
	/**
	 * Sets the markings file path.
	 *
	 * @param markingsFilePath the new markings file path
	 * @throws Exception the exception
	 */
	public void setMarkingsFilePath(String markingsFilePath) throws Exception{
		this.markingsFilePath = markingsFilePath;
	}

	/**
	 * Sets the ImageLayer as selected.
	 *
	 * @param isSelected the new selected
	 * @throws Exception the exception
	 */
	public void setSelected(boolean isSelected) throws Exception{
		this.isSelected = isSelected;
	}
	
	/**
	 * Marks the successfully made savings.
	 *
	 * @param mLayerIDs the new successfully saved marking layers
	 */
	public void setSuccessfullySavedMarkingLayers(ArrayList<Integer> mLayerIDs){
		try {
			Iterator<Integer> iIterator = mLayerIDs.iterator();
			while(iIterator.hasNext()){
				int mID = iIterator.next();
				if(mID >0){
					MarkingLayer mLayer = getMarkingLayer(mID);
					if(mLayer != null)
						mLayer.setMadeChanges(false); // all saved			
				}

			}
			
			// check is all MarkingLayers saved
			if(!isUnsavedMarkingLayers()){
				setMadeChanges(false); // all data of ImageLayer is saved
			}
			else{
				setMadeChanges(true); // some data is not saved
			}
		} catch (Exception e) {
			LOGGER.severe("Error in setting successfull savings to ImageLayer!");
			e.printStackTrace();
		}
		
		
		
	}

	/**
	 * Sort marking layers.
	 *
	 * @throws Exception the exception
	 */
	public void sortMarkingLayers() throws Exception{
		if(this.markingLayerList != null && this.markingLayerList.size()>0)
			Collections.sort(this.markingLayerList, new MarkingLayerComparator());
	}


}

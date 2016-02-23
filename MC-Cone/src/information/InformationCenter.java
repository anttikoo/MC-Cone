package information;

import gui.grid.SingleGridSize;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import managers.TaskManager;

/**
 * InformationCenter contains all saved information: ImageLayers and their images and markings.
 *
 * @author Antti
 */
public class InformationCenter {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The image layer list. */
	private ArrayList<ImageLayer> imageLayerList;
	
	/** The selected image layer. */
	private ImageLayer selectedImageLayer;
	
	/** The selected marking layer. */
	private MarkingLayer selectedMarkingLayer;
	
	/** The visible marking layer list. */
	private ArrayList<MarkingLayer> visibleMarkingLayerList;
	
	/** The layer id. This id is used in identifying ImageLayers and MarkingLayers 
	 * <-> GUI panels and layers of images and markings. */
	private int layerID = 1; 
	
	/** The present folder. */
	private String presentFolder;
	
	/** The shape list. */
	private int[] shapeList;
	
	/** The next free shape. */
	private int nextFreeShape=0;
	
	/** The color list. */
	private Color[] colorList;
	
	/** The next free color. */
	private int nextFreeColor=0;
	
	/** The present image dimension. */
	private Dimension presentImageDimension;
	
	/** The single grid size list. */
	private ArrayList<SingleGridSize> singleGridSizeList;
	


	/**
	 *  Class constructor.
	 *
	 * @param tm the tm
	 */
	public InformationCenter(TaskManager tm){
		try {
			setUpFreeShapes();
			setUpFreeColors();
			this.setImageLayerList(new ArrayList<ImageLayer>());
			this.setSelectedImageLayer(null);
			this.setVisibleMarkingLayerList(new ArrayList<MarkingLayer>());
			this.presentFolder= System.getProperty("user.home"); // may not work in some windows ?
			LOGGER.fine("present folder: "+ this.presentFolder);
			presentImageDimension =null;
			singleGridSizeList=null;
		} catch (Exception e) {
			LOGGER.severe("Error in initializing InformationCenter!");
			e.printStackTrace();
		}

	}

	/**
	 * Adds the ImageLayer.
	 *
	 * @param il the ImageLayer
	 * @throws Exception the exception
	 */
	public void addImageLayer(ImageLayer il) throws Exception{
		addSingleImageLayer(il);
		setProperSelectedImageLayer(); // check and set selected ImageLayer if needed
		setProperSelectedMarkingLayer(); // check and set selected MarkingLayer if needed
		updateVisibleMarkingLayerList(); // if made changes may need to change visibility of MarkingLayers
	
	}

	/**
	 * Adds List of ImageLayer objects to InformationCenter.imageLayerList
	 * @param layers Arraylist of ImageLayers that are added
	 */
	public void addImageLayers(ArrayList<ImageLayer> layers){
		try {
			if(layers != null && layers.size()>0){

				Iterator<ImageLayer> iterator = layers.iterator();
				while(iterator.hasNext()){
					ImageLayer im = (ImageLayer)iterator.next();
					addSingleImageLayer(im); // add single ImageLayer to list
				}
				createFirstMarkingLayerToImageLayers();
				//check if selectedImageLayer is null -> set first ImageLayer as selected
				setProperSelectedImageLayer();
				setProperSelectedMarkingLayer();
				updateVisibleMarkingLayerList();
				
				


			}
		} catch (Exception e) {
			LOGGER.severe("Error in adding ImageLayers: " +e.getClass().toString() + " :" +e.getMessage());
		}
	}

	/**
	 * Adds the MarkingLayer to visible list.
	 *
	 * @param mLayerID the ID of MarkingLayer
	 * @throws Exception the exception
	 */
	private void addMarkingLayerToVisibleList(int mLayerID) throws Exception{
		MarkingLayer markingLayer=getMarkingLayer(mLayerID);
		// add the MarkingLayer to visibleMarkingLayer list only if not exists
		if(markingLayer != null && !isInVisibleMarkingList(markingLayer.getLayerID())){
			this.visibleMarkingLayerList.add(markingLayer);
		}
	}

	/**
	 * Adds single ImageLayer to InformationCenter.imageLayerList if no image with same name already in list.
	 * Gives unique IDs to ImageLayer and its MarkinLayers. Sets the ImageLayer selected if no any ImageLayer is selected already.
	 * @param layer ImageLayer that is added to list
	 */
	private void addSingleImageLayer(ImageLayer layer){

		try {
			if (layer != null && layer.getImageFileName() != null && layer.getImageFileName().length() > 2) {
				// check is the layer already in list
				if (this.imageLayerList != null && this.imageLayerList.size() > 0) {
					Iterator<ImageLayer> iterator = this.imageLayerList.iterator();
					while (iterator.hasNext()) {
						ImageLayer im = (ImageLayer) iterator.next();
						if (im.getImageFileName().equalsIgnoreCase( layer.getImageFileName())) {
							LOGGER.warning("Warning: image " + layer.getImageFileName() + " is already in list");
							return; // the ImageLayer with same name is already in list -> not adding
						}
					}
					// not found ImageLayer with same name -> add to list
					finalizeIDs(layer); // give unique id to Imagelayer and its MarkingLayers
					this.imageLayerList.add(layer); // add to ImageLayer -list

				} else { // adding first ImageLayer in list
					// give unique id to Imagelayer and its MarkingLayers
					finalizeIDs(layer);

					// add to ImageLayer -list
					this.imageLayerList.add(layer);

				}
			
			}
			else{
				LOGGER.warning("Warning: imageLayer has no image name. Not added to list");
			}
		} catch (Exception e) {
			LOGGER.severe("Error in adding single ImageLayer: " +e.getClass().toString() + " :" +e.getMessage());
		}

	}

	/**
	 * Calculates different sizes of possibly GRIDs by using present image dimension.
	 *
	 * @throws Exception the exception
	 */
	private void calculateGridsizes() throws Exception{

		if(this.presentImageDimension != null && this.presentImageDimension.height>0 && this.presentImageDimension.width>0){
			singleGridSizeList=new ArrayList<SingleGridSize>();
			int[] sizes = new int[] {2,3,4,5,6,7,8,9,10};
			int smallerSize= Math.min(this.presentImageDimension.width, this.presentImageDimension.height);
			int biggerSize= Math.max(this.presentImageDimension.width, this.presentImageDimension.height);

			for(int i=0;i<sizes.length;i++){
				// size (in pixels) of grid cell. (width and height is same)
				int gSide= (int)(smallerSize/(sizes[i]+0.2));
				// margin of grid where grid starts. For width or height depending is width or height bigger.
				int gap = (int)((smallerSize -gSide*sizes[i])/2); // gap at vertical or horizontal direction
				// number of rows or columns depending is width or height bigger.
				int bsn = (int)((biggerSize-gap*2)/gSide);
				// gap of direction which size  is bigger than other direction (width or heigh of image)
				int biggerSizeAlign= (int)((biggerSize-bsn*gSide)/2); 

				if(this.presentImageDimension.width> this.presentImageDimension.height){
					this.singleGridSizeList.add(new SingleGridSize(gSide, gap, biggerSizeAlign, sizes[i], bsn));
				}
				else{
					this.singleGridSizeList.add(new SingleGridSize(gSide, biggerSizeAlign, gap, bsn, sizes[i]));
				}
			}
		}
	}

	/**
	 * Creates the first default MarkingLayer to ImageLayers.
	 */
	private void createFirstMarkingLayerToImageLayers(){
		try {
			if(this.imageLayerList != null && this.imageLayerList.size()>0){

				Iterator<ImageLayer> iterator = this.imageLayerList.iterator();
				while(iterator.hasNext()){
					ImageLayer im = (ImageLayer)iterator.next();
					if(im.getMarkingLayers() == null || im.getMarkingLayers().size()==0){ // if not MarkingLayer found -> add
						createNewMarkingLayer(im.getLayerID());
						
					}
				}
			
				
			}
		} catch (Exception e) {
			LOGGER.severe("Error in creating MarkingLayers ot ImageLayers: " + " :" +e.getMessage());
		}
	}

	/**
	 * Creates the new marking layer.
	 *
	 * @param iLayerID the ImageLayer ID
	 * @return the MarkingLayer
	 */
	public MarkingLayer createNewMarkingLayer(int iLayerID){
		try {

			ImageLayer im = getImageLayerByID(iLayerID);
			if(im != null){
				int idvalue = getUnReservedLayerID();	// get the unique markingLayerID
				MarkingLayer ml = new MarkingLayer("CellType_"+idvalue, getNextFreeShape(), getNextFreeColor());

				ml.setLayerID(idvalue);

				im.addMarkingLayer(ml);
				// Check and set the selectedMarkingLayer if needed
				if(this.getSelectedMarkingLayer() == null)
					setProperSelectedMarkingLayer();
				// in beginning the MarkingLayer is visible -> set to visibleLayerlist
				addMarkingLayerToVisibleList(ml.getLayerID());
				
								
				return ml;
			}
			return null;

		} catch (Exception e) {
			LOGGER.severe("Error in creating new markinglayer " +e.getMessage());
			return null;
		}
	}

	/**
	 * Finalize ImageLayer and its MarkingLayer IDs.
	 *
	 * @param layer the ImageLayer
	 * @throws Exception the exception
	 */
	private void finalizeIDs(ImageLayer layer) throws Exception{


		// finalize ImageLayer layerID. If negative then set proper id value
		if(layer.getLayerID()<0){
			layer.setLayerID(this.getUnReservedLayerID());

		}
		// finalize MarkingLayers of this ImageLayer if any
		if(layer.getMarkingLayers() != null && layer.getMarkingLayers().size()>0){

			Iterator<MarkingLayer> iterator = layer.getMarkingLayers().iterator();
			while (iterator.hasNext()) {
				MarkingLayer ma = (MarkingLayer) iterator.next();
				if (ma.getLayerID()<0) { // layerId not yet given to MarkingLayer
					ma.setLayerID(this.getUnReservedLayerID());
				}

			}
		}

	}

	/**
	 * Goes through all Imagelayers and return all MarkingLayers of them.
	 *
	 * @return array of all MarkingLayers in all ImageLayers
	 * @throws Exception the exception
	 */
	public ArrayList<MarkingLayer> getAllMarkingLayers() throws Exception{
		try {
			ArrayList<MarkingLayer> tempMarkingLayerList = new ArrayList<MarkingLayer>();

			if(this.imageLayerList != null && this.imageLayerList.size()>0){
				Iterator<ImageLayer> iterator = this.imageLayerList.iterator();
				while (iterator.hasNext()) {
					ImageLayer im = (ImageLayer) iterator.next();
					if(im.getMarkingLayers() != null && im.getMarkingLayers().size() >0)	{

						Iterator<MarkingLayer> mIterator = im.getMarkingLayers().iterator();
						while(mIterator.hasNext()){
							tempMarkingLayerList.add(mIterator.next());
						}
					}
				}
			}
			return tempMarkingLayerList;
		} catch (Exception e) {
			LOGGER.severe("Error in receiving all MarkingLayers " +e.getMessage());
			return null;
		}

	}

	/**
	 * returns ImageLayer by given ImageLayer ID.
	 *
	 * @param ilayerID ID of ImageLayer
	 * @return the ImageLayer by id
	 */
	public ImageLayer getImageLayerByID(int ilayerID){
		try {
			// go through imageLayerList and return if layerIDs match
			if (this.imageLayerList != null && this.imageLayerList.size() > 0) {
				Iterator<ImageLayer> iterator = this.imageLayerList.iterator();
				while (iterator.hasNext()) {
					ImageLayer im = (ImageLayer) iterator.next();
					if (im.getLayerID() == ilayerID){
						return im;
					}

				}
			}
			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in finding ImageLayer " +e.getMessage());
			return null;
		}
	}

	/**
	 * returns ImageLayer, which has given MarkingLayer (MarkingLayer ID is given).
	 *
	 * @param markingLayerID ID of MarkingLayer.
	 * @return the ImageLayer
	 */
	public ImageLayer getImageLayerByMarkingLayerID(int markingLayerID){
		try {
			// go through imageLayerList and return if layerIDs match
			if (this.imageLayerList != null && this.imageLayerList.size() > 0) {
				Iterator<ImageLayer> iterator = this.imageLayerList.iterator();
				while (iterator.hasNext()) {
					ImageLayer im = (ImageLayer) iterator.next();
					if (im.hasMarkingLayer(markingLayerID)){
						return im;
					}

				}
			}
			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in finding ImageLayer " +e.getMessage());
			return null;
		}
	}


	/**
	 * Returns the list of ImageLayers.
	 *
	 * @return the list of ImageLayers
	 * @throws Exception the exception
	 */
	public ArrayList<ImageLayer> getImageLayerList()throws Exception {
		return imageLayerList;
	}


	/**
	 * Returns the MarkingLayer.
	 *
	 * @param mLayerID the ID of MarkingLayer
	 * @return the marking layer
	 */
	public MarkingLayer getMarkingLayer(int mLayerID){
		try {
			// go through all ImageLayers
			Iterator<ImageLayer> imageLayerIterator=this.imageLayerList.iterator();
			while(imageLayerIterator.hasNext()){
				ImageLayer im= imageLayerIterator.next();
				if(im != null){
					// go through MarkingLayers and if match with given mLayerID -> return it
					Iterator<MarkingLayer> iterator = im.getMarkingLayers().iterator();
					while (iterator.hasNext()) {
						MarkingLayer ma = (MarkingLayer) iterator.next();
						if (ma.getLayerID() == mLayerID) {
							return ma;
						}
					}
				}
		}
			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in fetching MarkingLayer " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}
	}

	/**
	 * Returns the MarkingLayer by ImageLayer ID and MarkingLayer ID.
	 *
	 * @param iLayerID the ID of ImageLayer
	 * @param mLayerID the ID of MarkingLayer
	 * @return the MarkingLayer
	 */
	public MarkingLayer getMarkingLayer(int iLayerID, int mLayerID){
		try {
			ImageLayer im= getImageLayerByID(iLayerID);
			if(im != null){ // found ImageLayer
				Iterator<MarkingLayer> iterator = im.getMarkingLayers().iterator();
				while (iterator.hasNext()) {
					MarkingLayer ma = (MarkingLayer) iterator.next();
					if (ma.getLayerID() == mLayerID) {
						return ma;
					}
				}
			}
			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in fetching MarkingLayer " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}
	}

	/**
	 * Returns the next free color that is not used lately.
	 *
	 * @return the next free color
	 * @throws Exception the exception
	 */
	private Color getNextFreeColor() throws Exception{
		Color color= colorList[nextFreeColor++];
		if(nextFreeColor == colorList.length)
			nextFreeColor=0;
		return color;
	}


	/**
	 * Returns the next free shape that is not used lately.
	 *
	 * @return the next free shape
	 * @throws Exception the exception
	 */
	private int getNextFreeShape() throws Exception{

		int shape= shapeList[nextFreeShape++];
		if(nextFreeShape==shapeList.length)
			nextFreeShape=0;
		return shape;

	}

	/**
	 * Returns the next marking layer of ImageLayer.
	 *
	 * @param mLayerID the MarkingLayer ID
	 * @return the next marking layer, null if not found
	 * @throws Exception the exception
	 */
	private MarkingLayer getNextMarkingLayer(int mLayerID) throws Exception{
		boolean returnNext=false;
		ImageLayer im= getImageLayerByMarkingLayerID(mLayerID);
		
		if(im != null){
			// go through MarkingLayers
			Iterator<MarkingLayer> iterator = im.getMarkingLayers().iterator();
			while (iterator.hasNext()) {
				MarkingLayer ma = (MarkingLayer) iterator.next();
				
				if(returnNext) // previous was the found layer -> return this one
					return ma;
				
				if (ma.getLayerID() == mLayerID) {
					returnNext=true;
				}

				
			}
		}
		return null;
		
	}

	/**
	 * Returns the folder that is used latest.
	 *
	 * @return the present folder
	 * @throws Exception the exception
	 */
	public String getPresentFolder()throws Exception {
		return presentFolder;
	}


	/**
	 * Returns the present image dimension.
	 *
	 * @return the present image dimension
	 */
	public Dimension getPresentImageDimension() {
		return presentImageDimension;
	}

	/**
	 * Returns the previous marking layer.
	 *
	 * @param mLayerID the MarkingLayer ID
	 * @return the previous marking layer, null if not found
	 */
	private MarkingLayer getPreviousMarkingLayer(int mLayerID) throws Exception{
		MarkingLayer previousMarkingLayer=null;
		ImageLayer im= getImageLayerByMarkingLayerID(mLayerID);
		
		if(im != null){
			// go through MarkingLayers
			Iterator<MarkingLayer> iterator = im.getMarkingLayers().iterator();
			while (iterator.hasNext()) {
				MarkingLayer ma = (MarkingLayer) iterator.next();
			
				if (ma.getLayerID() == mLayerID) {
					return previousMarkingLayer;
				}
				previousMarkingLayer=ma;
			}
		}
		return null;
		
	}

	/**
	 * Returns the selected ImageLayer.
	 *
	 * @return the selected ImageLayer
	 * @throws Exception the exception
	 */
	public ImageLayer getSelectedImageLayer() throws Exception{
		return this.selectedImageLayer;
	}
	
	
	
	
	


	/**
	 * Returns ID of ImageLayer which is below selected ImageLayer in ImageLayerInfo and descending in list of ImageLayers.
	 *
	 * @return the ImageLayer descending selected ImageLayer. Negative value returned if not found.
	 */
	public int getSelectedImageLayerOneDown(){
		try {
			if(getSelectedImageLayer() != null){
				int selected_iLayer_id = getSelectedImageLayer().getLayerID();
				for (int i = 0; i < this.imageLayerList.size(); i++) {
					ImageLayer il=this.imageLayerList.get(i);
					if(il.getLayerID()== selected_iLayer_id){
						if(i+1<this.imageLayerList.size()){
							return this.imageLayerList.get(i+1).getLayerID();
						}
						return -1;
					}

				}
			}
			return -1; // not found
		} catch (Exception e) {
			LOGGER.severe("Error in getting ImageLayer below!");
			e.printStackTrace();
			return -1;
		}

	}
	
	/**
	 * Returns ID of ImageLayer which is above selected ImageLayer in ImageLayerInfo and preceding in list of ImageLayers.
	 *
	 * @return the ImageLayer preceding selected ImageLayer. Negative value returned if not found.
	 */
	public int getSelectedImageLayerOneUp(){
		try {
			if(getSelectedImageLayer() != null){
				int selected_iLayer_id = getSelectedImageLayer().getLayerID();
				int previous_ID=selected_iLayer_id;
				Iterator<ImageLayer> iIterator= this.imageLayerList.iterator();
				while(iIterator.hasNext()){
					ImageLayer il=iIterator.next();
					if(il.getLayerID() == selected_iLayer_id)
						return previous_ID;
					else{
						previous_ID=il.getLayerID();
					}
				}
			}
			return -1; // not found
		} catch (Exception e) {
			LOGGER.severe("Error in getting Selected upper ImageLayer!");
			e.printStackTrace();
			return -1;
		}

	}
	
	/**
	 * Returns the selected MarkingLayer.
	 *
	 * @return the selected marking layer
	 * @throws Exception the exception
	 */
	public MarkingLayer getSelectedMarkingLayer()  throws Exception{
		return selectedMarkingLayer;
	}
	
	/**
	 * Returns ID of MarkingLayer which is below selected MarkingLayer in ImageLayerInfo and descending in list of MarkingLayers.
	 *
	 * @return the MarkingLayer descending selected MarkingLayer. Negative value returned if not found.
	 */
	public int getSelectedMarkingLayerOneDown(){
		try {
			if(getSelectedMarkingLayer() != null){
				int selected_mLayer_id = getSelectedMarkingLayer().getLayerID();
				ArrayList<MarkingLayer> allMarkingLayers = getAllMarkingLayers();
				for (int i = 0; i < allMarkingLayers.size(); i++) {
					MarkingLayer ml=allMarkingLayers.get(i);
					if(ml.getLayerID()== selected_mLayer_id){
						if(i+1<allMarkingLayers.size()){
							return allMarkingLayers.get(i+1).getLayerID();
						}
						return -1;
					}

				}
			}
			return -1; // not found
		} catch (Exception e) {
			LOGGER.severe("Error in MarkingLayer one below!");
			e.printStackTrace();
			return -1;
		}

	}

	/**
	 * Returns ID of MarkingLayer which is above selected MarkingLayer in ImageLayerInfo and preceding in list of MarkingLayers.
	 *
	 * @return the MarkingLayer preceding selected MarkingLayer. Negative value returned if not found.
	 */
	public int getSelectedMarkingLayerOneUp(){
		try {
			if(getSelectedMarkingLayer() != null){
				int selected_mLayer_id = getSelectedMarkingLayer().getLayerID();
				int previous_ID=selected_mLayer_id;
				Iterator<MarkingLayer> iIterator= getAllMarkingLayers().iterator();
				while(iIterator.hasNext()){
					MarkingLayer ml=iIterator.next();
					if(ml.getLayerID() == selected_mLayer_id)
						return previous_ID;
					else{
						previous_ID=ml.getLayerID();
					}

				}
			}
			return -1; // not found
		} catch (Exception e) {
			LOGGER.severe("Error in getting selected upper MarkingLayer!");
			e.printStackTrace();
			return -1;
		}

	}

	/**
	 * Returns the single grid size list.
	 *
	 * @return the single grid size list
	 * @throws Exception the exception
	 */
	public ArrayList<SingleGridSize> getSingleGridSizeList() throws Exception{
		return singleGridSizeList;
	}




	/**
	 *  Gives next free layerId value and increases the value to given next time.
	 *
	 * @return layerID (int) the next free id value
	 * @throws Exception the exception
	 */
	public int getUnReservedLayerID()throws Exception {
		return layerID++;
	}

	/**
	 * Returns the list of visible MarkingLayers.
	 *
	 * @return the list of visible MarkingLayers
	 * @throws Exception the exception
	 */
	public ArrayList<MarkingLayer> getVisibleMarkingLayerList() throws Exception {
		return visibleMarkingLayerList;
	}

	/**
	 *  Searches image name from list of ImageLayers and returns true if found else false.
	 * @param imageName the string image name which is searched from imageLayerList
	 * @return boolean value is the image name found from imageLayerList
	 */
	public boolean imageNameAlreadyUsed(String imageName){
		try {
			// go through imageLayerList
			if (this.imageLayerList != null && this.imageLayerList.size() > 0) {
				Iterator<ImageLayer> iterator = this.imageLayerList.iterator();
				while (iterator.hasNext()) {
					ImageLayer im = (ImageLayer) iterator.next();
					if (im.getImageFileName().equalsIgnoreCase(imageName)){
						LOGGER.warning("Image " + im.getImageFileName() +" already in imagelist");
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			LOGGER.severe("Error in cheking is image already in imagelist");
			return true; // be sure that only unique image files can be in  list
		}
	}

	/**
	 * Checks if is image dimension allowed. Only images with same dimension is allowed to be open synchronously.
	 *
	 * @param dim the dim
	 * @return true, if is allowed image dimension
	 * @throws Exception the exception
	 */
	public boolean isAllowedImageDimension(Dimension dim)throws Exception{
		if(dim == null)
			return false;

		if(presentImageDimension == null){
			return true;
		}
		if(presentImageDimension.width == dim.width && presentImageDimension.height== dim.height)
			return true;

		return false;
	}


	/**
	 * Checks is the given MarkingLayer in list of visible Markings.
	 *
	 * @param mlayerID ID for MarkingLayer, which visibility is examined.
	 * @return true if MarkingLayer is in visible list, false otherwise.
	 * @throws Exception the exception
	 */
	private boolean isInVisibleMarkingList(int mlayerID) throws Exception{
			// if no any visible markings -> return false
		if(this.visibleMarkingLayerList == null || this.visibleMarkingLayerList.size()==0)
			return false;

		Iterator<MarkingLayer> mIterator = this.visibleMarkingLayerList.iterator();
		while(mIterator.hasNext()){
			if(mIterator.next().getLayerID()==mlayerID)
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the boolean madeChanges.
	 *
	 * @return true, if is made changes
	 * @throws Exception the exception
	 */
	public boolean isMadeChanges() throws Exception{
		if(this.imageLayerList != null && this.imageLayerList.size()>0){
			Iterator<ImageLayer> iterator = this.imageLayerList.iterator();
			while (iterator.hasNext()) {
				ImageLayer im = (ImageLayer) iterator.next();
				if(im.isMadeChanges())
					return true;
			}
			
		}
		return false;
		
		
	}

	/**
	 * Checks if is selected image layer.
	 *
	 * @param iLayerID the ID of ImageLayer
	 * @return true, if is selected ImageLayer
	 * @throws Exception the exception
	 */
	public boolean isSelectedImageLayer(int iLayerID)throws Exception{
		if(this.selectedImageLayer != null && this.selectedImageLayer.getLayerID()== iLayerID)
			return true;
		return false;
	}
	
	/**
	 * Checks if is selected MarkingLayer.
	 *
	 * @param mLayerID the ID of MarkingLayer
	 * @return true, if is selected MarkingLayer
	 */
	public boolean isSelectedMarkingLayer(int mLayerID){
		try {
			if(this.selectedMarkingLayer!= null && this.selectedMarkingLayer.getLayerID()== mLayerID)
				return true;
			return false;
		} catch (Exception e) {
			LOGGER.severe("Error in checking is selected MarkingLayers!");
			e.printStackTrace();
			return false;
		}
	}
	
	

	/**
	 * Move selected marking layer.
	 *
	 * @param direction the direction
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean moveSelectedMarkingLayer(int direction) throws Exception{
		
		MarkingLayer selectedMarkingLayer= getSelectedMarkingLayer();
		if(selectedMarkingLayer != null){
			MarkingLayer switchMarkingLayer = getNextMarkingLayer(selectedMarkingLayer.getLayerID());
			if(direction == ID.MOVE_UP)
				switchMarkingLayer = getPreviousMarkingLayer(selectedMarkingLayer.getLayerID());
			
			if(switchMarkingLayer != null && switchMarkingLayer.getOrder()>0){
				int tempOrder = selectedMarkingLayer.getOrder();
				selectedMarkingLayer.setOrder(switchMarkingLayer.getOrder());
				switchMarkingLayer.setOrder(tempOrder);
				getImageLayerByMarkingLayerID(selectedMarkingLayer.getLayerID()).sortMarkingLayers();
				
				return true;
					
			}
			else{
				return false;
			}
				
			
			
		}
		return false;
		
	}


	/**
	 * Removes the ImageLayer by given ID.
	 *
	 * @param imageLayerID the ID of ImageLayer
	 */
	public void removeImageLayer(int imageLayerID){
		try {
			boolean changeSelectedMarkingLayer=false;
			// go through imageLayerList
			if (this.imageLayerList != null && this.imageLayerList.size() > 0) {
				ImageLayer im = getImageLayerByID(imageLayerID);

				if(getSelectedMarkingLayer() != null && im.hasMarkingLayer(getSelectedMarkingLayer().getLayerID()))
					changeSelectedMarkingLayer=true; // has to set the selectedMarkingLayer
				// remove ImageLayer
				this.imageLayerList.remove(im);
				im=null;
			
					//is the removed ImageLayer selectedImageLayer
					// set the next imageLayer as selected and if no imagelayer -> null
				if(this.selectedImageLayer.getLayerID()== imageLayerID){
					this.setSelectedImageLayer(null);
					if(this.imageLayerList != null && this.imageLayerList.size()>0){
						LOGGER.fine("Removed image; size of list: " +this.imageLayerList.size());
						// set proper selectedImageLayer
						this.setProperSelectedImageLayer();
					}
					else{
						LOGGER.fine("Removed image; empty list");
						this.setSelectedImageLayer(null);
						this.presentImageDimension=null; // no images -> set the present dimension to null;
						this.singleGridSizeList=null; // no images -> no any grid dimensions
					}
				}
				else{ // if no any images left -> set selected imagelayer null
					if(this.imageLayerList == null || this.imageLayerList.size()<=0){
						LOGGER.fine("Removed image; size of list: " +this.imageLayerList.size());
						this.setSelectedImageLayer(null);
						this.presentImageDimension=null; // no images -> set the present dimension to null;
					}
				}

				// set the proper selectedMarkingLayer -> happens when deleting imageLayer which is not selected
				if(changeSelectedMarkingLayer && this.selectedImageLayer != null)
					setProperSelectedMarkingLayer(); 

			}
			else{
				LOGGER.warning("Removing ImageLayer which not exist. Bug.");
			}

			LOGGER.fine("Finished Removing ImageLayer");
		} catch (Exception e) {
			LOGGER.severe("InformationCenter: Error in removing ImageLayer: "+e.getClass().toString() + " " +e.getLocalizedMessage());

		}
	}

	/**
	 * Removes the MarkingLayer by given ImageLayer ID and MarkingLayer ID.
	 *
	 * @param imageLayerID the ID of ImageLayer
	 * @param markingLayerID the ID of MarkingLayer
	 */
	public void removeMarkingLayer(int imageLayerID, int markingLayerID){
		try {
			// go through imageLayerList

			ImageLayer im = getImageLayerByID(imageLayerID);

				// search and destroy the markinglayer if any in im.markingLayerList
				if(im != null && im.getMarkingLayers() != null && im.getMarkingLayers().size()>0){
					Iterator<MarkingLayer> m_iterator = im.getMarkingLayers().iterator();
					search:
					while (m_iterator.hasNext()) { // go through MarkingLayers
						MarkingLayer ma = (MarkingLayer) m_iterator.next();
						if (ma.getLayerID()== markingLayerID) {
							// if the removed MarkingLayer is selected set selectedMarkingLayer as null
							if(ma.getLayerID()==this.selectedMarkingLayer.getLayerID())
								this.setSelectedMarkingLayer(null);
							m_iterator.remove();
							ma=null;
							
							break search; // stop going through more ImageLayers
						}

					} // while (m_iterator.hasNext())

					// check is the selectedMarkingLayer removed and select new one if needed
					if(this.getSelectedMarkingLayer() == null)
						setProperSelectedMarkingLayer();
					
				

			}
			else{
				LOGGER.warning("Removing ImageLayer which not exist. Bug.");
			}
		} catch (Exception e) {
			LOGGER.severe("Error in removing ImageLayer");
		}
	}

	/**
	 * Sets the all ImageLayer objects to unselected and object selectedImageLayer as null.
	 *
	 * @throws Exception the exception
	 */
	private void setAllImageLayersUnSelected() throws Exception{
		if(this.imageLayerList != null && this.imageLayerList.size()>0){
			Iterator<ImageLayer> iterator = this.imageLayerList.iterator();
			while (iterator.hasNext()) {
				iterator.next().setSelected(false);

			}
			this.selectedImageLayer=null;
		}
	}



	/**
	 *  Sets all MarkingLayers to unselected.
	 *
	 * @throws Exception the exception
	 */
	private void setAllMarkingLayersUnselected() throws Exception{
		ArrayList<MarkingLayer> allMarkingLayers= getAllMarkingLayers();
		if(allMarkingLayers != null && allMarkingLayers.size()>0){
			Iterator<MarkingLayer> mIterator = allMarkingLayers.iterator();
			while(mIterator.hasNext()){
				mIterator.next().setSelected(false);
			}
		}
	
	}

	/**
	 * Sets new list of ImageLayers: replaces the older one after checking correctness of new ImageLayers by method addImageLayers.
	 * If null parameter is given: creates new empty list
	 * @param iLayerList ArrayList containing the ImageLayer objects
	 */
	public void setImageLayerList(ArrayList<ImageLayer> iLayerList)  {
		try {
			if(iLayerList != null && iLayerList.size()>0){
				ImageLayer sil= getSelectedImageLayer();
				int selectesdILayerID =-1;
				int selectedMarkingLayerID=-1;
				if(sil != null)
					selectesdILayerID =sil.getLayerID();

				MarkingLayer sml=getSelectedMarkingLayer();
				if(sml != null)
					selectedMarkingLayerID=sml.getLayerID();

				this.imageLayerList=new ArrayList<ImageLayer>(); // remove all earlier elements from list
				this.selectedImageLayer=null;
				this.selectedMarkingLayer=null;
				this.visibleMarkingLayerList = new ArrayList<MarkingLayer>();
				addImageLayers(iLayerList); // add list by checking also the correctness of layers (the have positive layerIDs)

				sil=getImageLayerByID(selectesdILayerID);
				if(sil != null)
					setSelectedImageLayer(sil);
				sml=getMarkingLayer(selectedMarkingLayerID);
				if(sml != null)
					setSelectedMarkingLayer(sml);

				// set visible markinglayers
				if(getAllMarkingLayers() != null && getAllMarkingLayers().size()>0){
				Iterator<MarkingLayer> mlayerIterator = getAllMarkingLayers().iterator();
					while(mlayerIterator.hasNext()){
						MarkingLayer ml=mlayerIterator.next();
						if(ml.isVisible()){
							addMarkingLayerToVisibleList(ml.getLayerID());
						}
					}
				}
				
			}
			else
				this.imageLayerList = new ArrayList<ImageLayer>();
		} catch (Exception e) {
			LOGGER.severe("Error in setting ImageLayerList!");
			e.printStackTrace();
			this.imageLayerList = new ArrayList<ImageLayer>();
		}
	}

	

	/**
	 * Sets the name of MarkingLayer.
	 *
	 * @param iLayerID the ID of ImageLayer
	 * @param mLayerID the ID of MarkingLayer
	 * @param markingName the name of MarkingLayer
	 */
	public void setMarkingLayerName(int iLayerID, int mLayerID, String markingName){
		try {
			if(iLayerID>0 && mLayerID>0 && markingName != null && markingName.length()>0){
				ImageLayer im = getImageLayerByID(iLayerID);
				if(im != null && im.getMarkingLayers() != null){
					im.getMarkingLayer(mLayerID).setLayerName(markingName);
				
				}
			}
		} catch (Exception e) {
			LOGGER.severe("Error in setting MarkingLayerName " +e.getClass().toString() + " :" +e.getMessage());
		}
	}

	/**
	 * Sets the visiblity of MarkingLayer by given ID.
	 *
	 * @param mLayerID the ID of MarkingLayer
	 * @param visible the boolean for visibility
	 * @throws Exception the exception
	 */
	public void setMarkingLayerVisibility(int mLayerID, boolean visible) throws Exception{
		MarkingLayer markingLayer=getMarkingLayer(mLayerID);
		if(visible){
			if(markingLayer != null && !isInVisibleMarkingList(markingLayer.getLayerID())){
				this.visibleMarkingLayerList.add(markingLayer);

			}
			markingLayer.setVisible(true);

		}
		else{
			if(markingLayer != null && isInVisibleMarkingList(markingLayer.getLayerID())){
				this.visibleMarkingLayerList.remove(markingLayer);
			}

			markingLayer.setVisible(false);
		}

	}

	/**
	 * Sets the present folder that is used latest.
	 *
	 * @param presentFolder the new present folder
	 */
	public void setPresentFolder(String presentFolder) {
		this.presentFolder = presentFolder;
	}


	/**
	 * Sets the present image dimension.
	 *
	 * @param dim the new present image dimension
	 * @throws Exception the exception
	 */
	public void setPresentImageDimension(Dimension dim) throws Exception{
		if(this.presentImageDimension == null){
			this.presentImageDimension=dim;
			calculateGridsizes();
		}

	}
	/**
	 * Sets the proper selected ImageLayer.
	 *
	 * @throws Exception the exception
	 */
	private void setProperSelectedImageLayer() throws Exception{

		if(this.getImageLayerList() != null && this.getImageLayerList().size()>0){
			if(getSelectedImageLayer() == null){
				// first unselect all
				setAllImageLayersUnSelected();

				//select the first ImageLayer as selected
				ImageLayer selectedImageLayer= this.getImageLayerList().get(0);
				selectedImageLayer.setSelected(true);
				setSelectedImageLayer(selectedImageLayer);
			}
		}
	}


	/**
	 * Sets the selectedMarkingLayer from selected ImageLayer. If no any markingLayers in selected ImageLayer, then set null.
	 *
	 * @throws Exception the exception
	 */
	private void setProperSelectedMarkingLayer() throws Exception{
		ImageLayer selectedIL= getSelectedImageLayer();
		if(selectedIL != null && selectedIL.getMarkingLayers() != null && selectedIL.getMarkingLayers().size()>0){
			// check is the selectedMarkingLayer already ok
			if(this.selectedMarkingLayer != null && selectedIL.hasMarkingLayer(this.selectedMarkingLayer.getLayerID())){
					// everything is ok -> set visible and then return
			//		setMarkingLayerVisibility(this.selectedMarkingLayer.getLayerID(), true);
					return;
				}

			// if reached here the selectedMarkingPanel is not right one -> select first visible MarkingLayer of selectedImageLayer and if not found -> any
			MarkingLayer firstMLayer = selectedIL.getFirstVisibleMarkingLayer();
			if(firstMLayer==null)
				firstMLayer = selectedIL.getMarkingLayers().get(0);
			if(firstMLayer != null){
				// set all MarkingLayers as unselected
				setAllMarkingLayersUnselected();
				// set the selected MarkingLayer
				this.setSelectedMarkingLayer(firstMLayer);
				// if firstmLayer is not visible -> set to visible
				if(!isInVisibleMarkingList(firstMLayer.getLayerID())){
		//			setMarkingLayerVisibility(firstMLayer.getLayerID(), true);
				//	addMarkingLayerToVisibleList(firstMLayer.getLayerID());
				//	firstMLayer.setVisible(true);
					
				}
				firstMLayer.setSelected(true);
			}
			else{ // no MarkingLayers of selectedImageLayer
				// set all MarkingLayers not selected
				setAllMarkingLayersUnselected();
				this.setSelectedMarkingLayer(null);

			}

		}
		else // selected ImageLayer doesn't have MarkingLayers -> get firstMarkingLayer and set it selectd
		{
				ArrayList<MarkingLayer> mLayerList = getAllMarkingLayers();
				if(mLayerList != null && mLayerList.size()>0){
					setSelectedMarkingLayer(mLayerList.get(0));
				}
				else{ // no any MarkingLayers
					this.setSelectedMarkingLayer(null);
				}
		}
	}

	/**
	 * Sets the given ImageLayer as selectedImageLayer and checks is the selectedMarkingLayerProper.
	 * If selectedMarkingLayer is not proper (not found) the new one is selected from MarkinLayers of selectedImageLayer.
	 *
	 * @param selectedImageLayer The ImageLayer to set as selectedImageLayer
	 * @throws Exception the exception
	 */
	private void setSelectedImageLayer(ImageLayer selectedImageLayer) throws Exception {
		this.selectedImageLayer = selectedImageLayer;
		this.setProperSelectedMarkingLayer();
	}

	/**
	 * Sets the selected ImageLayer if not exist.
	 *
	 * @return boolean value has selected ImageLayer been updated
	 */
	public boolean setSelectedImageLayerIfNotExist(){

		try {
			ImageLayer sil= getSelectedImageLayer();
			if(sil == null){ // no selected ImageLayer

				if(this.imageLayerList == null || this.imageLayerList.size() == 0){ // no any ImageLayer to set as selected
					LOGGER.fine("no selected imageLayer or layers in list ");
					return false;
				}
				else{ // selected ImageLayer is null but in imageLayerList should  contain at least one ImageLayer
					this.setSelectedImageLayer(this.imageLayerList.get(0)); // take the first ImageLayer as selected
					return true;
				}
			}
			else{
				// selectedImageLayer was found -> check it really exists
				if(getImageLayerByID(sil.getLayerID()) == null){
					// not found the selectedImageLayer -> set new one if any ImageLayers in list
					if(this.imageLayerList != null && this.imageLayerList.size()>0){
						this.setSelectedImageLayer(this.imageLayerList.get(0)); // take the first ImageLayer as selected
						return true;
					}
				}
			}
			// found selected imageLayer and it was in list (should be always in list)
			return false;
		} catch (Exception e) {
			LOGGER.severe("Error cheking and selecting selectedImageLayer: " +e.getClass().toString() + " :" +e.getMessage());
			return false;
		}
	}

	/**
	 * Sets the selected MarkingLayer.
	 *
	 * @param sMarkingLayer the new selected marking layer
	 * @throws Exception the exception
	 */
	public void setSelectedMarkingLayer(MarkingLayer sMarkingLayer)  throws Exception {
		if(sMarkingLayer != null){
			setAllMarkingLayersUnselected();
			this.selectedMarkingLayer = sMarkingLayer;
			this.selectedMarkingLayer.setSelected(true);
		}
		else{
			setAllMarkingLayersUnselected();
			this.selectedMarkingLayer=null;
		}

	}
	
	/**
	 * Sets the successfully made savings to MarkingLayers.
	 *
	 * @param mLayerIDs the ids of MarkingLayers where successfully made savings
	 * @throws Exception the exception
	 */
	public void setSuccessfullyMadeSavings(ArrayList<Integer> mLayerIDs) throws Exception{
		if(this.imageLayerList != null && this.imageLayerList.size()>0){
			Iterator<ImageLayer> iterator = this.imageLayerList.iterator();
			while (iterator.hasNext()) {
				ImageLayer im = (ImageLayer) iterator.next();
				im.setSuccessfullySavedMarkingLayers(mLayerIDs);
				
			}
		}	
	}

	/**
	 * Sets the colors where default colors are selected.
	 */
	private void setUpFreeColors(){
		colorList=new Color[]{
		new Color(255,0,0),
		new Color(153,153,255),
		new Color(255,255,153),
		new Color(51,255,51),
		new Color(0,255,255),
		new Color(255,0,255)
		};
	}

	/**
	 * Sets the list of shapes that are used as to select default shape for MarkingLayers.
	 */
	private void setUpFreeShapes(){
		shapeList=new int[]{ID.SHAPE_CROSS,ID.SHAPE_SQUARE, ID.SHAPE_PLUS,ID.SHAPE_DIAMOND,ID.SHAPE_CIRCLE, ID.SHAPE_OVAL, ID.SHAPE_TRIANGLE};
	}

	/**
	 * Sets the list of visible MarkingLayers.
	 *
	 * @param visibleMarkingLayerList the list of visible MarkingLayers
	 * @throws Exception the exception
	 */
	public void setVisibleMarkingLayerList(ArrayList<MarkingLayer> visibleMarkingLayerList) throws Exception {
		this.visibleMarkingLayerList = visibleMarkingLayerList;
	}

	/**
	 * Update present image dimension of marking panels.
	 */
	public void updatePresentImageDimensionOfMarkingPanels(){
		try {
			if(this.getPresentImageDimension() != null){
				Iterator<MarkingLayer> mIterator = getAllMarkingLayers().iterator();
				while(mIterator.hasNext()){
					MarkingLayer mLayer = mIterator.next();
					if(mLayer != null && mLayer.getGridProperties() != null){
						mLayer.getGridProperties().setPresentImageDimension(this.getPresentImageDimension());
					}
				}
			}
		} catch (Exception e) {
			LOGGER.severe("Error in updating present image dimension!");
			e.printStackTrace();
		}

	}

	/**
	 * Update selected ImageLayer.
	 *
	 * @param iLayerID the ID of ImageLayer
	 * @throws Exception the exception
	 */
	public void updateSelectedImageLayer(int iLayerID) throws Exception{
		ImageLayer im = getImageLayerByID(iLayerID);
		if(im != null){
			// set all ImageLayers first unselected
			setAllImageLayersUnSelected();

			setSelectedImageLayer(im);
			im.setSelected(true);
		}
	}

	/*
	private void updateVisibilityOfImageLayer(int iLayerID){
		// go through all markingLayers of ImageLayer -> if any visible -> set imageLayer visible
		ImageLayer il = getImageLayerByID(iLayerID);
		ArrayList<MarkingLayer> markingLayers=il.getMarkingLayers();
		Iterator<MarkingLayer> mIterator = markingLayers.iterator();
		while(mIterator.hasNext()){
			if(mIterator.next().isVisible()){
				il.setIsVisibleMarkingLayers(true); // found
				return;
			}
				
		}
		//not found any visible MarkigLayers
		il.setIsVisibleMarkingLayers(false);
		
		
	}
*/
	/**
	 * Update the list of visible MarkingLayers.
	 *
	 * @throws Exception the exception
	 */
	private void updateVisibleMarkingLayerList() throws Exception{
		ArrayList<MarkingLayer> allML = getAllMarkingLayers();
		this.visibleMarkingLayerList.clear();
		if(allML != null && allML.size() > 0){
			Iterator<MarkingLayer> mIterator = allML.iterator();
			while(mIterator.hasNext()){
				MarkingLayer ml = mIterator.next();
				if(ml.isVisible())
					this.visibleMarkingLayerList.add(ml);
			}
		}
	}


}

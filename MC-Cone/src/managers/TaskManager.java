package managers;
import gui.GUI;
import gui.ProgressBallsDialog;
import gui.ShadyMessageDialog;
import gui.grid.SingleGridSize;
import information.GridProperties;
import information.ID;
import information.ImageLayer;
import information.InformationCenter;
import information.MarkingLayer;
import information.PositionedImage;
import information.ScreenCoordinatesOfMarkingLayer;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import org.imgscalr.Scalr;
import operators.PreCounterThread;
import operators.XMLreadManager;


/**
 * Mediates all commands and information between GUI and working classes.
 * @author Antti Kurronen
 */
public class TaskManager {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The XmlReadManager for reading XML. */
	private XMLreadManager xmlManager;
	
	/** The information center. */
	private InformationCenter informationCenter;
	
	/** The layer visual manager. Contains methods for manipulating images. */
	private LayerVisualManager layerVisualManager;
	
	/** The GUI. */
	private GUI gui; // object of graphical interface
	
	/** The PreCountThreadManager for managing precounting process. */
	private PreCountThreadManager preCountThreadManager = null;
	
	
	/**
	 * Class constructor.
	 *
	 * @param gui the GUI
	 * @throws Exception the exception
	 */
	public TaskManager(GUI gui) throws Exception{
		this.gui = gui;
		this.xmlManager = new XMLreadManager();
		this.informationCenter = new InformationCenter(this);
		this.layerVisualManager = new LayerVisualManager();


	}

	/**
	 * Adds the ImageLayer.
	 *
	 * @param layer the ImageLayer
	 * @throws Exception the exception
	 */
	public void addImageLayer(ImageLayer layer)  throws Exception{
		informationCenter.addImageLayer(layer);
		setPresentImageDimensionFromSingleImageLayer(layer);
	}

	/**
	 * Adds the ImageLayers.
	 *
	 * @param layers the list of ImageLayers
	 * @throws Exception the exception
	 */
	public void addImageLayers(ArrayList<ImageLayer> layers)  throws Exception{
		this.informationCenter.addImageLayers(layers);
		setPresentImageDimensionFromImageLayerList(layers);
		this.informationCenter.updatePresentImageDimensionOfMarkingPanels();



	}

	/**
	 * Adds the single marking point.
	 *
	 * @param pointAtScreen the point at screen
	 * @return true, if successfully save the point
	 * @throws Exception the exception
	 */
	public boolean addSingleMarking(Point pointAtScreen)  throws Exception{
		return this.layerVisualManager.addSingleMarkingCoordinate(pointAtScreen, this.informationCenter.getSelectedMarkingLayer());
	}

	/**
	 * Change grid cell selection.
	 *
	 * @param p the Point
	 * @throws Exception the exception
	 */
	public void changeGridCellSelection(Point p)  throws Exception{
		if(this.informationCenter.getSelectedMarkingLayer() != null &&
				this.informationCenter.getSelectedMarkingLayer().getGridProperties() != null && this.informationCenter.getSelectedMarkingLayer().getGridProperties().isGridON()){
			this.layerVisualManager.changeSelectedRectangleOfGridProperty(this.informationCenter.getSelectedMarkingLayer().getGridProperties(), p);
		}
	}
	
	/**
	 * Changes selectedImageLayer in InformationCenter and selected BufferedImage object of LayerVisualManager.
	 *
	 * @param iLayerID Identifier (int) of ImageLayer that is set as selected
	 * @throws Exception the exception
	 */
	public void changeSelectedImageLayer(int iLayerID) throws Exception{

		this.informationCenter.updateSelectedImageLayer(iLayerID);
		setSelectedImage(this.informationCenter.getSelectedImageLayer().getImageFilePath());
	}

	/**
	 * Sets PreCountManager-object to null.
	 *
	 * @throws Exception the exception
	 */
	public void cleanPrecountingManager() throws Exception{
		this.preCountThreadManager = null;
	}

	/**
	 * Creates the image file from given File.
	 *
	 * @param imageFile the image file
	 * @return the created buffered image
	 * @throws Exception the exception
	 */
	public BufferedImage createImageFile(File imageFile) throws Exception{
		if(imageFile != null)
			return this.layerVisualManager.readImageFile(imageFile);
		else
			return null;
	}

	/**
	 * Creates the new MarkingLayer.
	 *
	 * @param imageLayerID the ID of ImageLayer
	 * @return the MarkingLayer
	 * @throws Exception the exception
	 */
	public MarkingLayer createNewMarkingLayer(int imageLayerID)  throws Exception{
		return informationCenter.createNewMarkingLayer(imageLayerID);

	}

	/**
	 * Returns a new calculated image when image dragged.
	 *
	 * @param movement the movement at screen
	 * @return the new positioned image
	 */
	public PositionedImage dragLayers(Point movement){
		return this.layerVisualManager.dragLayers(movement);
	}
	
	/**
	 * Returns the all MarkingLayers.
	 *
	 * @return the all MarkingLayers
	 * @throws Exception the exception
	 */
	public ArrayList<MarkingLayer> getAllMarkingLayers()  throws Exception{
		return informationCenter.getAllMarkingLayers();
	}
	
	/**
	 * Returns the converted grid properties.
	 *
	 * @return the converted grid properties
	 * @throws Exception the exception
	 */
	public GridProperties getConvertedGridProperties() throws Exception{
		MarkingLayer ml =getSelectedMarkingLayer();
		if(ml != null)
			return this.layerVisualManager.convertLayerGridPropertiesToPanel(ml.getGridProperties());
		else
			return null;

	}

	/**
	 * Returns the image dimension of given image file.
	 *
	 * @param file the image file
	 * @return the image dimension of given image
	 * @throws Exception the exception
	 */
	public Dimension getImageDimension(File file) throws Exception{
		return this.layerVisualManager.getImageDimension(file);
	}

	/**
	 * Returns ImageLayer by given ID.
	 *
	 * @param iLayerID ID of ImageLayer
	 * @return the ImageLayer by id
	 * @throws Exception the exception
	 */
	public ImageLayer getImageLayerByID(int iLayerID)  throws Exception{
		return informationCenter.getImageLayerByID(iLayerID);
	}

	/**
	 * returns ImageLayer, which has given MarkingLayer (MarkingLayer ID is given).
	 *
	 * @param markingLayerID ID of MarkingLayer.
	 * @return the ImageLayer by MarkingLayer id
	 * @throws Exception the exception
	 */
	public ImageLayer getImageLayerByMarkingLayerID(int markingLayerID)  throws Exception{
		return informationCenter.getImageLayerByMarkingLayerID(markingLayerID);
	}

	/**
	 * Returns the list of ImageLayers.
	 *
	 * @return the list of ImageLayers
	 * @throws Exception the exception
	 */
	public ArrayList<ImageLayer> getImageLayerList()  throws Exception{
		return informationCenter.getImageLayerList();
	}

	/**
	 * Returns the MarkingLayer.
	 *
	 * @param mLayerID the ID of MarkingLayer
	 * @return the MarkingLayer
	 * @throws Exception the exception
	 */
	public MarkingLayer getMarkingLayer(int mLayerID) throws Exception{
		return this.informationCenter.getMarkingLayer(mLayerID);
	}

	/**
	 * Returns the MarkingLayer by given ImageLayer ID and MarkingLayer ID.
	 *
	 * @param iLayerID the ID of ImageLayer
	 * @param mLayerID the ID of MarkingLayer
	 * @return the MarkingLayer
	 * @throws Exception the exception
	 */
	public MarkingLayer getMarkingLayer(int iLayerID, int mLayerID) throws Exception{
		return this.informationCenter.getMarkingLayer(iLayerID, mLayerID);
	}

	/**
	 * Returns the list of MarkingLayers, which data found from  xml-file by given ImageLayers.
	 *
	 * @param xmlFile the xml file
	 * @param imageLayerList the list of ImageLayers
	 * @return the markings of xml
	 * @throws Exception the exception
	 */
	public ArrayList<ImageLayer> getMarkingsOfXML(File xmlFile, ArrayList<ImageLayer> imageLayerList) throws Exception{
		return xmlManager.getMarkingsOfXML(xmlFile, imageLayerList);

	}

	/**
	 * Returns the list of MarkingLayers, which data found from xml-file by given single ImageLayer.
	 *
	 * @param xmlFile the xml file
	 * @param imageLayer the ImageLayer
	 * @return the markings of xml
	 * @throws Exception the exception
	 */
	public ImageLayer getMarkingsOfXML(File xmlFile, ImageLayer imageLayer) throws Exception{
		return xmlManager.getMarkingsOfXML(xmlFile, imageLayer);
	}

	/**
	 * Returns the PreCountThreadManager.
	 *
	 * @return the PreCountThreadManager
	 * @throws Exception the exception
	 */
	public PreCountThreadManager getPrecountThreadManager() throws Exception{
		return this.preCountThreadManager;
	}

	/**
	 * Returns the present folder.
	 *
	 * @return the present folder
	 * @throws Exception the exception
	 */
	public String getPresentFolder() throws Exception{
		return informationCenter.getPresentFolder();
	}
	
	/**
	 * Returns the present image dimension.
	 *
	 * @return the present image dimension
	 * @throws Exception the exception
	 */
	public Dimension getPresentImageDimension() throws Exception {
		return this.informationCenter.getPresentImageDimension();
	}

	/**
	 * Returns the refreshed image.
	 *
	 * @return the refreshed image
	 * @throws Exception the exception
	 */
	public PositionedImage getRefreshedImage() throws Exception{
		return this.layerVisualManager.getRefreshedImage();

	}

	/**
	 * Returns the scaling mode.
	 *
	 * @param image_width the image width
	 * @param image_height the image height
	 * @param iPanelDimension the dimension of ImagePanel
	 * @return the scaling mode FIT_TO_WIDTH or FIT_TO_HEIGHT
	 * @throws Exception the exception
	 */
	public Scalr.Mode getScalingMode(int image_width, int image_height, Dimension iPanelDimension) throws Exception{
		return this.layerVisualManager.getScalingMode(image_width, image_height, iPanelDimension);
	}

	/**
	 * Returns the screen coordinates of selected MarkingLayer.
	 *
	 * @return the screen coordinates of selected MarkingLayer
	 * @throws Exception the exception
	 */
	public ArrayList<Point> getScreenCoordinatesOfSelectedMarkingLayer() throws Exception{
		return this.layerVisualManager.getScreenPointsOfMarkingLayer(this.informationCenter.getSelectedMarkingLayer());
	}

	/**
	 * Returns the screen coordinates of visible MarkingLayers.
	 *
	 * @return the screen coordinates of visible MarkingLayers
	 * @throws Exception the exception
	 */
	public ArrayList<ScreenCoordinatesOfMarkingLayer> getScreenCoordinatesOfVisibleMarkingLayers() throws Exception{
		return this.layerVisualManager.getScreenPointsOfMarkingLayers(this.informationCenter.getVisibleMarkingLayerList());
	}

	/**
	 * Returns the selected ImageLayer.
	 *
	 * @return the selected ImageLayer
	 * @throws Exception the exception
	 */
	public ImageLayer getSelectedImageLayer() throws Exception{
		return this.informationCenter.getSelectedImageLayer();
	}

	/**
	 * Returns the selected ImageLayer at above or below.
	 *
	 * @param upOrDownID the up or down id
	 * @return the selected ImageLayer at up or down
	 * @throws Exception the exception
	 */
	public int getSelectedImageLayerAtUpOrDown(int upOrDownID) throws Exception{
		if(upOrDownID == ID.MOVE_DOWN){
			return this.informationCenter.getSelectedImageLayerOneDown();
		}
		else{
			return this.informationCenter.getSelectedImageLayerOneUp();
		}
	}

	/**
	 * Returns the image path of selected ImageLayer.
	 *
	 * @return the image path of selected ImageLayer
	 * @throws Exception the exception
	 */
	public String getSelectedImagePath() throws Exception{
		ImageLayer layer=informationCenter.getSelectedImageLayer();
		if(layer != null)
			return layer.getImageFilePath();
		else
			return null;
	}

	/**
	 * Returns the selected MarkingLayer.
	 *
	 * @return the selected MarkingLayer
	 * @throws Exception the exception
	 */
	public MarkingLayer getSelectedMarkingLayer() throws Exception{
		return this.informationCenter.getSelectedMarkingLayer();
	}

	/**
	 * Returns the selected MarkingLayer at up or down.
	 *
	 * @param upOrDownID the up or down id
	 * @return the selected MarkingLayer at up or down
	 * @throws Exception the exception
	 */
	public int getSelectedMarkingLayerAtUpOrDown(int upOrDownID) throws Exception{
		if(upOrDownID == ID.MOVE_DOWN){
			return this.informationCenter.getSelectedMarkingLayerOneDown();
		}
		else{
			return this.informationCenter.getSelectedMarkingLayerOneUp();
		}
	}


	/**
	 * Returns the shape size multiplier.
	 *
	 * @return the shape size multiplier
	 * @throws Exception the exception
	 */
	public double getShapeSizeMultiplier() throws Exception{

		return this.layerVisualManager.getSizeMultiplier();
	}

	/**
	 * Returns the single grid size list.
	 *
	 * @return the single grid size list
	 * @throws Exception the exception
	 */
	public ArrayList<SingleGridSize> getSingleGridSizeList() throws Exception {
		return this.informationCenter.getSingleGridSizeList();
	}

	/**
	 * Returns the sub image.
	 *
	 * @param middlepoint the middlepoint
	 * @param size the size
	 * @return the sub image
	 * @throws Exception the exception
	 */
	private BufferedImage getSubImage(Point middlepoint, int size) throws Exception{
		return this.layerVisualManager.getSubImage(middlepoint, size);
	}


	/**
	 * Returns the visible MarkingLayers.
	 *
	 * @return the visible MarkingLayers
	 * @throws Exception the exception
	 */
	public ArrayList<MarkingLayer> getVisibleMarkingLayers() throws Exception{
		return informationCenter.getVisibleMarkingLayerList();
	}

	/**
	 * Returns the zoomed image.
	 *
	 * @param midP the mid p
	 * @param zoomValue the zoom value
	 * @return the zoomed image
	 * @throws Exception the exception
	 */
	public PositionedImage getZoomedImage(Point midP, double zoomValue) throws Exception{
		return this.layerVisualManager.getZoomedImage(midP, zoomValue);
	}
	
	
	/**
	 * Checks is any ImageLayers stored at InformationCenter.
	 *
	 * @return true, if found
	 * @throws Exception the exception
	 */
	public boolean hasAnyImageLayers() throws Exception{
		if(informationCenter.getImageLayerList() != null && informationCenter.getImageLayerList().size()>0)
			return true;
		else
			return false;
		
	}
	
	/**
	 * Checks is any MarkingLayers stored in ImageLayers at InformationCenter.
	 *
	 * @return true, if found
	 * @throws Exception the exception
	 */
	public boolean hasAnyMarkinglayers() throws Exception{
		if(informationCenter.getAllMarkingLayers() != null && informationCenter.getAllMarkingLayers().size()>0)
			return true;
		
		return false;
	}

	/**
	 * Checks is image name already used.
	 *
	 * @param imageName the image name
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean imageNameAlreadyUsed(String imageName) throws Exception{
		return informationCenter.imageNameAlreadyUsed(imageName);
	}

	/**
	 * Checks if is allowed image dimension.
	 *
	 * @param file the file
	 * @return true, if is allowed image dimension
	 * @throws Exception the exception
	 */
	public boolean isAllowedImageDimension(File file) throws Exception{
		Dimension dim=this.layerVisualManager.getImageDimension(file);
		if( dim != null)
		return this.informationCenter.isAllowedImageDimension(dim);
		else
			return false;
	}


	/**
	 * Checks if is made changes.
	 *
	 * @return true, if is made changes
	 * @throws Exception the exception
	 */
	public boolean isMadeChanges() throws Exception{
		return this.informationCenter.isMadeChanges();
	}

	/**
	 * Checks if is ImageLayer selected.
	 *
	 * @param iLayerID the ID of ImageLayer
	 * @return true, if is selected ImageLayer
	 * @throws Exception the exception
	 */
	public boolean isSelectedImageLayer(int iLayerID) throws Exception{
		return this.informationCenter.isSelectedImageLayer(iLayerID);
	}

	/**
	 * Checks if is MarkingLayer selected.
	 *
	 * @param mLayerID the ID of MarkingLayer
	 * @return true, if is selected MarkingLayer
	 * @throws Exception the exception
	 */
	public boolean isSelectedMarkingLayer(int mLayerID) throws Exception{
		return this.informationCenter.isSelectedMarkingLayer(mLayerID);
	}

	/**
	 * Checks if is selected MarkingLayer visible.
	 *
	 * @return true, if is selected MarkingLayer visible
	 * @throws Exception the exception
	 */
	public boolean isSelectedMarkingLayerVisible() throws Exception{
		if(this.informationCenter.getSelectedMarkingLayer() == null)
			return false;
		return this.informationCenter.getSelectedMarkingLayer().isVisible();
	}
	
	/**
	 * Move selected marking layer.
	 *
	 * @param direction the direction
	 * @return true, if successful moving
	 * @throws Exception the exception
	 */
	public boolean moveSelectedMarkingLayer(int direction) throws Exception{
		return this.informationCenter.moveSelectedMarkingLayer(direction);
	
	}

	/**
	 * Sets PreCountThreadManager to start precounting process. Needs point and size for getting image of picked cell,
	 *  which is used in precounting.
	 *
	 * @param middlePoint the middle point
	 * @param size the size
	 * @param pbd the ProgressBallsDialog
	 * @throws Exception the exception
	 */
	public void precountCells(Point middlePoint, int size, ProgressBallsDialog pbd) throws Exception{
		BufferedImage subImage = getSubImage(middlePoint, size);
		
		if(subImage != null && this.informationCenter.getSelectedImageLayer() != null && this.informationCenter.getSelectedMarkingLayer() != null){
			//LOGGER.fine("subimage size" +subImage.getWidth() +" height" +subImage.getHeight());
			int iLayerID= this.informationCenter.getSelectedImageLayer().getLayerID();
			int mLayerID= this.informationCenter.getSelectedMarkingLayer().getLayerID();

			if(preCountThreadManager != null){

				// set new ProgressBallDialog
				this.preCountThreadManager.setProgressBallDialog(pbd);

				// add new subimage (from cell)
				this.preCountThreadManager.setNewSubImage(subImage);


				this.preCountThreadManager.initPreCounterThread();

				// start counting
				this.preCountThreadManager.startCounting();


			}
			else{
				BufferedImage originalImage = this.layerVisualManager.getOriginalImage();
				if(originalImage != null){
					try{
					// create new PreCountThreadManager
					PreCounterThread preCountThread =new PreCounterThread(subImage, originalImage, this);
					this.preCountThreadManager=new PreCountThreadManager(preCountThread,pbd, iLayerID, mLayerID);
					// start the counting
					this.preCountThreadManager.startCounting();

					}catch(Exception e){

					}
				}

			}

		}
		else{
			LOGGER.warning("Couldn't create subimage from selection");
		}

	}


	/**
	 * Refreshes the graphical user interface.
	 *
	 * @throws Exception the exception
	 */
	public void refreshLayersAndGUI() throws Exception{
		this.gui.refreshLayersAndGUI();
	}

	/**
	 * Removes the ImageLayer.
	 *
	 * @param layerID the ID of ImageLayer
	 * @throws Exception the exception
	 */
	public void removeImageLayer(int layerID) throws Exception{
		informationCenter.removeImageLayer(layerID);
	}

	/**
	 * Removes the MarkingLayer.
	 *
	 * @param imageLayerID the ID of ImageLayer
	 * @param markingLayerID the id of MarkingLayer
	 * @throws Exception the exception
	 */
	public void removeMarkingLayer(int imageLayerID, int markingLayerID) throws Exception{
		informationCenter.removeMarkingLayer(imageLayerID, markingLayerID);
	}

	/**
	 * Removes the single marking.
	 *
	 * @param pointAtScreen the point at screen
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean removeSingleMarking(Point pointAtScreen) throws Exception{
		return this.layerVisualManager.removeSingleMarkingCoordinate(pointAtScreen, this.informationCenter.getSelectedMarkingLayer());
	}


	/**
	 * Sets the list of ImageLayers.
	 *
	 * @param layers the new list of ImageLayers
	 * @throws Exception the exception
	 */
	public void setImageLayerList(ArrayList<ImageLayer> layers) throws Exception{
		this.informationCenter.setImageLayerList(layers);
		setPresentImageDimensionFromImageLayerList(layers);
		this.informationCenter.updatePresentImageDimensionOfMarkingPanels();

	}

	/**
	 * Sets the image panel dimension.
	 *
	 * @param vpd the new image panel dimension
	 * @throws Exception the exception
	 */
	public void setImagePanelDimension(Dimension vpd) throws Exception{
		this.layerVisualManager.setImagePanelDimension(vpd);
	}



	/**
	 * Sets the name of MarkingLayer.
	 *
	 * @param iLayerID the ID of ImageLayer
	 * @param mLayerID the ID of MarkingLayer
	 * @param markingName the name of MarkingLayer
	 * @throws Exception the exception
	 */
	public void setMarkingLayerName(int iLayerID, int mLayerID, String markingName) throws Exception{
		informationCenter.setMarkingLayerName(iLayerID, mLayerID, markingName);
		
	}

	/**
	 * Sets the MarkingLayer selected.
	 *
	 * @param mLayerID the ID of MarkingLayer
	 * @return the MarkingLayer
	 * @throws Exception the exception
	 */
	public MarkingLayer setMarkingLayerSelected(int mLayerID) throws Exception{
		MarkingLayer sMarkingLayer=this.informationCenter.getMarkingLayer(mLayerID);
		if(sMarkingLayer != null){
			this.informationCenter.setSelectedMarkingLayer(sMarkingLayer);
			return sMarkingLayer;
		}
		else
			return null;
	}

	/**
	 * Sets the MarkingLayer visibility.
	 *
	 * @param mLayerID the m layer id
	 * @param visible the visible
	 * @throws Exception the exception
	 */
	public void setMarkingLayerVisibility(int mLayerID, boolean visible) throws Exception{
		this.informationCenter.setMarkingLayerVisibility(mLayerID, visible);
	}

	/**
	 * Sets the present folder.
	 *
	 * @param folder the new present folder
	 * @throws Exception the exception
	 */
	public void setPresentFolder(String folder) throws Exception{
		this.informationCenter.setPresentFolder(folder);
	}

	/**
	 * Sets the present image dimension from list of ImageLayers.
	 *
	 * @param layers the list of ImageLayers
	 * @throws Exception the exception
	 */
	private void setPresentImageDimensionFromImageLayerList(ArrayList<ImageLayer> layers) throws Exception{
		boolean added=false;
		if(layers != null && layers.size()>0){
			Iterator<ImageLayer> iIterator=layers.iterator();
			while(!added && iIterator.hasNext()){
				ImageLayer layer=iIterator.next();
				added=setPresentImageDimensionFromSingleImageLayer(layer);
			}
		}
	}

	/**
	 * Sets the present image dimension from single ImageLayer.
	 *
	 * @param layer the ImageLayer
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private boolean setPresentImageDimensionFromSingleImageLayer(ImageLayer layer) throws Exception{
		if(layer != null && layer.getImageFilePath() != null && layer.getImageFilePath().length()>0){
			File imagefile = new File(layer.getImageFilePath());
			if(imagefile != null){
				try {
					this.informationCenter.setPresentImageDimension(this.layerVisualManager.getImageDimension(imagefile));
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Sets the selected image.
	 *
	 * @param imagePath the path of new selected image
	 * @throws Exception the exception
	 */
	public void setSelectedImage(String imagePath) throws Exception{
		this.layerVisualManager.setSelectedBufferedImage(imagePath);
	}

	/**
	 * Sets the ImageLayer selected if not exist yet.
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean setSelectedImageLayerIfNotExist() throws Exception{
		return this.informationCenter.setSelectedImageLayerIfNotExist();
	}

	/**
	 * Sets the selected MarkingLayer coordinates.
	 *
	 * @param coordinates the new selected MarkingLayer coordinates
	 * @throws Exception the exception
	 */
	public void setSelectedMarkingLayerCoordinates(ArrayList<Point> coordinates) throws Exception{
		this.informationCenter.getSelectedMarkingLayer().setCoordinateList(coordinates);
	}
	
	/**
	 * Sets the successfully made savings to MarkingLayers.
	 *
	 * @param mLayerIDs the ids of MarkingLayers where successfully made savings
	 * @throws Exception the exception
	 */
	public void setSuccessfullyMadeSavings(ArrayList<Integer> mLayerIDs) throws Exception{
		this.informationCenter.setSuccessfullyMadeSavings(mLayerIDs);
	}

	/**
	 * Shows message to user.
	 *
	 * @param title the title
	 * @param message the message
	 * @throws Exception the exception
	 */
	public  void showMessageToUser(String title, String message) throws Exception{
		ShadyMessageDialog dialog = new ShadyMessageDialog(this.gui, title, message, ID.OK, this.gui);
		dialog.showDialog();
		dialog=null;
	}

	/**
	 * FOR TESTING PURPOSES : add coordinates to second.
	 *
	 * @param coordinates the coordinates
	 * @throws Exception the exception
	 */
	public void testADDCoordinatesToSecond(ArrayList<Point> coordinates) throws Exception{
		this.informationCenter.getAllMarkingLayers().get(1).setCoordinateList(coordinates);
	}

	/**
	 * Sets the selected image to layerVisualManager.
	 *
	 * @throws Exception the exception
	 */
	public void updateImageOfSelectedImageLayer() throws Exception{
		ImageLayer selectedLayer = this.informationCenter.getSelectedImageLayer();
		if(selectedLayer != null)
			setSelectedImage(selectedLayer.getImageFilePath());
		else
			setSelectedImage(null);
	}

	/**
	 * Update selected MarkingPanel and ImageLayerInfos.
	 *
	 * @throws Exception the exception
	 */
	public void updateSelectedMarkingPanelAndImageLayerInfos() throws Exception{
		// updates selectedMarkingPanel and ImageLayerInfos
		gui.updateCoordinatesOfSelectedMarkingPanel(); 

	}

}

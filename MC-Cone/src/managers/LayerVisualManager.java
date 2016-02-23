package managers;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import org.imgscalr.Scalr;
import gui.file.Utils;
import information.GridProperties;
import information.MarkingLayer;
import information.PositionedImage;
import information.PositionedRectangle;
import information.ScreenCoordinatesOfMarkingLayer;


/**
 * Contains functionality for image handling. 
 * The view of image at ImagePanel is a relative part of original image (relativeViewRectangle), which is scaled to ImagePanel.
 * When making changes (zoom, move, etc.) has the point at ImagePanel be converted to Point in relativeView and then converted to point 
 * at original image. In same way the image that will be shown in ImagePanel will be first calculated at present relative view and then
 * taken the right sized and positioned image from original image: scaled to the ImagePanel.
 * @author Antti Kurronen
 *
 */
public class LayerVisualManager {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The image panel dimension. */
	private Dimension imagePanelDimension; // the dimension of actual size of ImagePanel at screen (the topleft position is always 0,0)
	
	//the relative size of ImagePanel referred to actual size of originalImage. Is the dimension of present view of original image
	// this dimension and relativeImageCoordinate are changed when wanted to get right part of original image shown.
	private Rectangle relativeViewRectangle;
	private BufferedImage originalImage;


	/**
	 * Adds the single marking coordinate.
	 *
	 * @param screenPoint the screen point
	 * @param selectedMarkingLayer the selected marking layer
	 * @return true, if successful
	 */
	public boolean addSingleMarkingCoordinate(Point screenPoint, MarkingLayer selectedMarkingLayer){
		if(selectedMarkingLayer != null ){
			Point pointAtImage= convertRelativePointAsPointAtImage(convertScreenPointToRelativePoint(screenPoint));
			if(pointAtImage != null && selectedMarkingLayer != null){
				return selectedMarkingLayer.addSingleCoordinate(pointAtImage);
			}
		}
		return false;
	}

	/**
	 * Calculates relative dimensions of image to fit to image panel. 
	 *
	 * @param scalingMode the scaling mode (FIT_TO_HEIGHT or FIT_TO_WIDTH)
	 * @param original_width the original image width
	 * @param original_height the original image height
	 * @return the rectangle with new width and height
	 */
	private Rectangle calculateRelativeDimensionToImagePanel(Scalr.Mode scalingMode, int original_width, int original_height){
		try {
			int relativeHeight;
			int relativeWidth;
			if(scalingMode == Scalr.Mode.FIT_TO_HEIGHT){
				relativeHeight=original_height;
				relativeWidth=(int)((double)this.imagePanelDimension.getWidth()/((double)this.imagePanelDimension.getHeight()/(double)relativeHeight));
			}
			else{
				relativeWidth=original_width;
				relativeHeight=(int)((double)this.imagePanelDimension.getHeight()/((double)this.imagePanelDimension.getWidth()/(double)relativeWidth));
			}
			// create and set the rectangle with calculated width and height and default topleft corner position (0,0)
			return new Rectangle(relativeWidth,relativeHeight);
		} catch (Exception e) {
			LOGGER.severe("Error in calculating RelatiViewRectangle dimension:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}

	/**
	 * Calculates and returns relative view rectangle focused on focus point at image panel. 
	 * Calculates the size of Rectangle and the position of top left corner of rectangle.
	 * Calculation is made in different stages: 
	 * 1. FocusPoint position at screen is converted to relative position of image panel.
	 * 2. Relative focusPoint position is converted to position at image
	 * 3. New size of relative Rectangle on image is calculated
	 * 4. New position of relative Rectangle on image is calculated.
	 *
	 * @param focusPoint the focus point in ImagePanel
	 * @param zoomValue the zoom value
	 * @return the rectangle
	 */
	private Rectangle calculateRelativeViewRectangleFocusedOnFocusPoint(Point2D focusPoint, double zoomValue){
		try {

			// calculate relative location of fpoint from topleft corner of imagePanel
			Point2D relativeFocusPoint = new Point2D.Double(focusPoint.getX()/this.imagePanelDimension.getWidth(),
					focusPoint.getY()/this.imagePanelDimension.getHeight());

			//convert the midpoint to relative coordinates
			focusPoint=convertScreenPointToRelativePoint(focusPoint);

			// convert midPoint as Point at the image
			focusPoint.setLocation(this.relativeViewRectangle.x+focusPoint.getX(), this.relativeViewRectangle.y+focusPoint.getY());

			/* calculate the new size of relativeImagePanelRectangle -> this is temporary object until processing can start*/

			// check the reasonable orientation
			Scalr.Mode  scalingMode= (Scalr.Mode)(getScalingMode(this.originalImage.getWidth(), this.originalImage.getHeight(),this.imagePanelDimension));
			int w;
			int h;
			if(scalingMode == Scalr.Mode.FIT_TO_HEIGHT){ // the new size will fit to height: maximum height is same as image height but width can be bigger
				h= (int)(this.relativeViewRectangle.getHeight()*(1.0/zoomValue));
				if(h > this.originalImage.getHeight())
					h = this.originalImage.getHeight();
				w= (int)((double)h*(this.imagePanelDimension.getWidth()/this.imagePanelDimension.getHeight()));
			}
			else{  // the new size will fit to width: maximum width is same as image width but height can be bigger
				w= (int)(this.relativeViewRectangle.getWidth()*(1.0/zoomValue));
				if(w > this.originalImage.getWidth())
					w= this.originalImage.getWidth();
				h = (int)((double)w*(this.imagePanelDimension.getHeight()/this.imagePanelDimension.getWidth()));
			}

			Rectangle tempRelativeViewRectangle=new Rectangle(w, h);

			/* calculate the top left corner of rectangle */

			int topLeft_x = (int)((focusPoint.getX()-tempRelativeViewRectangle.getWidth()*relativeFocusPoint.getX()));
			int topLeft_y = (int)(( focusPoint.getY()-tempRelativeViewRectangle.getHeight()*relativeFocusPoint.getY()));
			if(tempRelativeViewRectangle.width> this.originalImage.getWidth()){
				topLeft_x=0; // position of relativeView starts at left
			}
			else{ // the relativeView width is smaller than the image width
				// relativeView goes outside of image at left
				if(topLeft_x < 0)
					topLeft_x = 0;
				// relativeView goes outside of image at right side
				else if(topLeft_x +tempRelativeViewRectangle.width> this.originalImage.getWidth())
					topLeft_x = this.originalImage.getWidth()-tempRelativeViewRectangle.width;
				}
			if(tempRelativeViewRectangle.height > this.originalImage.getHeight()){
				topLeft_y=0;
			}
			else{ // relativeView height is smaller than image height
				// relativeView goes outside of image at up
				if(topLeft_y < 0){
					topLeft_y =0;
				}
				// relativeView goes outside of image at bottom
				else if(topLeft_y +tempRelativeViewRectangle.height > this.originalImage.getHeight())
					topLeft_y = this.originalImage.getHeight()-tempRelativeViewRectangle.height;

			}
			tempRelativeViewRectangle.setLocation(topLeft_x,topLeft_y);

			return tempRelativeViewRectangle;
		} catch (Exception e) {
			LOGGER.severe("Error in calculating temporary relativeImagePanelRectangle:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}

	}

	/**
	 * Changes GRID rectangle selection state. 
	 * Converts screen point to point at image and changes the selection of Rectangle which found at image point.
	 *
	 * @param gp the GridProperty
	 * @param p the Point
	 * @throws Exception the exception
	 */
	public void changeSelectedRectangleOfGridProperty(GridProperties gp, Point p)  throws Exception{
		Point imagePoint = convertScreenPointToImagePoint(p);
		gp.changeSelectionOfPositionedRectangle(imagePoint);
	}

	/**
	 * Converts image distance to screen distance.
	 *
	 * @param imageDistance the image distance
	 * @return the double distance at screen
	 */
	private double convertImageDistanceToScreenDistance(int imageDistance){
		// from screen to relativeRectangle
		return (imageDistance/this.relativeViewRectangle.getWidth()*this.imagePanelDimension.getWidth());
	}

	/**
	 * Converts image point to relative point.
	 *
	 * @param imagePoint the image point
	 * @return the relative point
	 */
	private Point convertImagePointToRelativePoint(Point imagePoint){
		if (imagePoint != null){
			Point relativePoint = new Point(imagePoint.x-this.relativeViewRectangle.x, imagePoint.y-this.relativeViewRectangle.y);
			if(relativePoint.x>=0 && relativePoint.y >= 0)
				return relativePoint;
		}
		return null;
	}


	/**
	 * Converts image point to screen point.
	 *
	 * @param imagePoint the image point
	 * @return the point at screen
	 */
	private Point convertImagePointToScreenPoint(Point imagePoint){
		if(imagePoint != null){
			Point relativePoint = convertImagePointToRelativePoint(imagePoint);
			if(relativePoint != null)
				return convertRelativePointToScreenPoint(relativePoint);
		}
		return null;
	}

	/**
	 * Converts layer GridProperties from image to image panel. A new GridProperties object will be created and returned.
	 *
	 * @param gp the GridProperties object
	 * @return the GridProperties object
	 */
	public GridProperties convertLayerGridPropertiesToPanel(GridProperties gp){
		try {
			if(gp != null){
				GridProperties gp_converted=new GridProperties();
				gp_converted.setGridON(gp.isGridON());
				// for GRID add vertical lines converted from image to screen
				Iterator<Integer> rowIterator = gp.getRowLineYs().iterator();
				while(rowIterator.hasNext()){
					int y= rowIterator.next();
					gp_converted.addRowLineY(convertYinImageToYinScreen(y));

				}
				//for GRID add horizontal lines converted from image to screen
				Iterator<Integer> columnIterator = gp.getColumnLineXs().iterator();
				while(columnIterator.hasNext()){
					int x= columnIterator.next();
					gp_converted.addColumnLineX(convertXinImageToXinScreen(x));
				}
				//for GRID create PositioneRectangles converted from image to screen.
				Iterator<PositionedRectangle> recIterator = gp.getPositionedRectangleList().iterator();
				while(recIterator.hasNext()){
					PositionedRectangle pr =recIterator.next();
					Rectangle rec2=convertRecAtImageToRecAtScreen(pr); // get the position and size for screen
					if(rec2 != null){
						PositionedRectangle screenPR=new PositionedRectangle(rec2.x, rec2.y, rec2.width, rec2.height, pr.getRow(), pr.getColumn(), pr.isSelected());
						gp_converted.addSinglePositionedRectangle(screenPR);
						screenPR=null;
					}
					rec2=null;

				}
				// set line lengths to fit image panel
				gp_converted.setHorizontalLineLength((int)convertImageDistanceToScreenDistance(this.originalImage.getWidth()));
				gp_converted.setVerticalLineLength((int)convertImageDistanceToScreenDistance(this.originalImage.getHeight()));

				// calculate outer Rectangles
				gp_converted.calculateOuterRectangles();
				
				return gp_converted;
			}
			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in converting GridProperties from image to screen");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Converts size and position of Rectangle at image to Rectangle at screen.
	 *
	 * @param rec the Rectangle
	 * @return the converted Rectangle
	 * @throws Exception the exception
	 */
	private Rectangle convertRecAtImageToRecAtScreen(Rectangle rec) throws Exception{

		if(rec.intersects(this.relativeViewRectangle)){ // does rectangles overlap
			Rectangle2D recNew= rec.createIntersection(this.relativeViewRectangle);
			return convertRelativeRectangleToScreenRectangle((Rectangle) recNew);
		}
		return null;
	}

	/**
	 * Converts relative height to ImagePanel height.
	 *
	 * @param relativeHeight the relative height
	 * @return the int height of ImagePanel
	 */
	private int convertRelativeHeightToPanelHeight(int relativeHeight){
		double d= this.imagePanelDimension.getHeight()/this.relativeViewRectangle.getHeight();// this.imagePanelDimension.getHeight()/this.relativeViewRectangle.getHeight());
		return (int)(relativeHeight*d);
	}

	/**
	 * Convert relative length to ImagePanellength.
	 *
	 * @param relativeLength the relative length
	 * @return the int relative length
	 */
	private int convertRelativeLengthToPanelLength(int relativeLength){
		double d= this.imagePanelDimension.getWidth()/this.relativeViewRectangle.getWidth();// this.imagePanelDimension.getHeight()/this.relativeViewRectangle.getHeight());
		return (int)(relativeLength*d);
	}

	/**
	 * Convert relative point as point at image.
	 *
	 * @param relativePoint the relative point
	 * @return the point relative Point
	 */
	private Point convertRelativePointAsPointAtImage(Point2D relativePoint){
		if(relativePoint != null)
			return new Point(this.relativeViewRectangle.x+(int)relativePoint.getX(), this.relativeViewRectangle.y+(int)relativePoint.getY());
		return null;
	}


	/**
	 * Convert relative point to screen point.
	 *
	 * @param relativePoint the Point at relative Rectangle
	 * @return the Point at screen
	 */
	private Point convertRelativePointToScreenPoint(Point relativePoint){

		try {
			AffineTransform scaleTransform = AffineTransform.getScaleInstance(this.imagePanelDimension.getWidth()/this.relativeViewRectangle.getWidth(), this.imagePanelDimension.getHeight()/this.relativeViewRectangle.getHeight());
			Point2D p = new Point2D.Double(relativePoint.getX(), relativePoint.getY());
			Point2D screenPoint2D =scaleTransform.transform(p, null);
			return new Point((int)screenPoint2D.getX(),(int)screenPoint2D.getY());
		} catch (Exception e) {
			LOGGER.severe("LayerVisualManager Error in converting relative point to screen point:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}

	/**
	 * Converts relative rectangle to screen rectangle.
	 *
	 * @param rectangle the relative rectangle
	 * @return the rectangle at screen
	 * @throws Exception the exception
	 */
	private Rectangle convertRelativeRectangleToScreenRectangle(Rectangle rectangle) throws Exception{

		try {

			if(rectangle != null && rectangle.getLocation() != null && rectangle.width>0 && rectangle.height>0)
				return new Rectangle(convertImagePointToScreenPoint(rectangle.getLocation()),
				new Dimension(convertRelativeLengthToPanelLength(rectangle.width),convertRelativeHeightToPanelHeight(rectangle.height)));
			else
				return null;
		} catch (Exception e) {
			LOGGER.severe("LayerVisualManager Error in converting relative point to screen point:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}

	/**
	 * Convert screen distance to image distance.
	 *
	 * @param screenDistance the screen distance
	 * @return the double
	 */
	private double convertScreenDistanceToImageDistance(int screenDistance){
		// from screen to relativeRectangel
		return (screenDistance/this.imagePanelDimension.getWidth()*this.relativeViewRectangle.getWidth());
	}

	/**
	 * Converts a screen point to point at image.
	 *
	 * @param sp the Point at screen
	 * @return the Point at original image
	 */
	public Point convertScreenPointToImagePoint(Point sp){
		return convertRelativePointAsPointAtImage(convertScreenPointToRelativePoint(sp));
	}

	/**
	 * Converts screen point to relative point.
	 *
	 * @param screenPoint the screen point
	 * @return the point2 d
	 */
	private Point2D convertScreenPointToRelativePoint(Point2D screenPoint){
	
			AffineTransform scaleTransform = AffineTransform.getScaleInstance(this.relativeViewRectangle.getWidth()/this.imagePanelDimension.getWidth(), this.relativeViewRectangle.getHeight()/this.imagePanelDimension.getHeight());
			Point2D relativePoint =scaleTransform.transform(screenPoint, null);
			return relativePoint;
		}

	/**
	 * Convert x position in image to x position in screen.
	 *
	 * @param x the x
	 * @return the int
	 */
	private int convertXinImageToXinScreen(int x){
		int relativeX= x-this.relativeViewRectangle.x;

			double d= this.imagePanelDimension.getWidth()/this.relativeViewRectangle.getWidth();// this.imagePanelDimension.getHeight()/this.relativeViewRectangle.getHeight());
		return (int)(relativeX*d);
	}

	/**
	 * Converts position y in image to position y at screen.
	 *
	 * @param y the y position in image
	 * @return the int y position at screen
	 */
	private int convertYinImageToYinScreen(int y){
		int relativeY= y-this.relativeViewRectangle.y;

			double d= this.imagePanelDimension.getHeight()/this.relativeViewRectangle.getHeight();
		return (int)(relativeY*d);
	}

	/**
	 * Calculates and creates a new image to be shown in ImagePanel when dragged layers.
	 *
	 * @param movement the Point where position of image is moved at screen
	 * @return the PositionedImage
	 */
	public PositionedImage dragLayers(Point movement){
		try {
			// check that all needed objects exist
			if(this.originalImage == null || this.relativeViewRectangle == null || this.imagePanelDimension == null)
			return null;

			Point2D movementAtImage= convertScreenPointToRelativePoint(movement);


			// calculate temperary relativeImagePanelDimension -> save it as relativeImagePanelDimension when image is created successfully
			Rectangle tempRelativeViewRectangle= moveRelativeViewRectangle(movementAtImage);

			// tempRelativeViewRectangle is proportional to ImagePanel size -> height or width may be bigger than originalImage -> calculate dimension of real cropped image
			Dimension imageDimension= getImageDimensionInsideRectangle(tempRelativeViewRectangle);
			// too small image
			if(imageDimension.getWidth() <20|| imageDimension.getHeight() <20)
				return new PositionedImage(null);

			// crop the image
			BufferedImage 	processedImage = Scalr.crop(this.originalImage, tempRelativeViewRectangle.x, tempRelativeViewRectangle.y, imageDimension.width, imageDimension.height, null);
			

			return scaleToImagePanel(processedImage, tempRelativeViewRectangle);

		} catch (Exception e) {
			LOGGER.severe("Error in dragging image:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}


	/**
	 * Returns the image dimension.
	 *
	 * @param imageFile the image file
	 * @return the image dimension
	 * @throws Exception the exception
	 */
	public Dimension getImageDimension(File imageFile)throws Exception{
			BufferedImage image = readImageFile(imageFile);
			if(image != null){
				Dimension dim =new Dimension(image.getWidth(),image.getHeight());
				image=null;
				return dim;
			}
			throw new NullPointerException();
	}

	/**
	 * Returns the image dimension inside relative rectangle. If width or height of rectangle is bigger than image -> image width or height is returned.
	 *
	 * @param rec the relative Rectangle
	 * @return the image dimension inside rectangle
	 */
	private Dimension getImageDimensionInsideRectangle(Rectangle rec){
		try {
			int h=rec.height;
			int w=rec.width;
			if(rec.height>this.originalImage.getHeight())
				h= this.originalImage.getHeight();
			if(rec.width>this.originalImage.getWidth())
				w=this.originalImage.getWidth();
			return new Dimension(w,h);
		} catch (Exception e) {
			LOGGER.severe("Error in zooming image:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}

	/**
	 * Returns the original image.
	 *
	 * @return the original image
	 */
	public BufferedImage getOriginalImage() {
		return this.originalImage;
	}

	/**
	 * Returns the refreshed image which is scaled and positioned newly to ImagePanel.
	 *
	 * @return the refreshed image
	 */
	public PositionedImage getRefreshedImage(){
		// check than needed image and the panel size are initialized
			if(this.originalImage == null || this.imagePanelDimension == null)
				return null;
		// is relativeViewRectangleInitialized
		if(this.relativeViewRectangle == null){
			return initAndscaleToImagePanel();
		}
		else{
			return getZoomedImage(new Point2D.Double(this.imagePanelDimension.getWidth()/2,this.imagePanelDimension.getHeight()/2), 1.0);
		}
	}

	/**
	 * Returns the relative to image panel rectangle.
	 *
	 * @return the relative to image panel rectangle
	 */
	public Rectangle getRelativeToImagePanelRectangle() {
		return relativeViewRectangle;
	}


	/**
	 * Calculates which scaling method is better to scale image with original orientation and fit to ImagePanel in best way.
	 * @param image_width the width of image which best scaling method is examined
	 * @param image_height the height of image which best scaling method is examined
	 * @param iPanelDimension the Dimension of ImagePanel.
	 * @return Scalr.Mode Enum object either FIT_TO_HEIGHT or FIT_TO_WIDTH
	 */
	public Scalr.Mode getScalingMode(int image_width, int image_height, Dimension iPanelDimension){

		try {
			if (image_width >0 && image_height> 0  && iPanelDimension != null && iPanelDimension.getWidth() >0) {

				double image_proportion = (double)image_width / (double)image_height;
				double panel_proportion = iPanelDimension.getWidth()/ iPanelDimension.getHeight();

				if (image_proportion > panel_proportion)
					return Scalr.Mode.FIT_TO_WIDTH;
				else
					return Scalr.Mode.FIT_TO_HEIGHT;
			}
			return Scalr.Mode.FIT_TO_WIDTH;
		} catch (Exception e) {
			LOGGER.severe("Error in calculating scale method:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return Scalr.Mode.FIT_TO_WIDTH;
		}
	}

	/**
	 * Returns the screen points of marking layer.
	 *
	 * @param mLayer the m layer
	 * @return the screen points of marking layer
	 */
	public ArrayList<Point> getScreenPointsOfMarkingLayer(MarkingLayer mLayer){
		ArrayList<Point> tempPointList=new ArrayList<Point>(); // list for keeping points inside relativeViewRectangle
		Iterator<Point> pointIterator= mLayer.getCoordinateList().iterator();
		while(pointIterator.hasNext()){
			Point imagePoint=pointIterator.next();
			if(this.relativeViewRectangle.contains(imagePoint)){
				tempPointList.add(convertImagePointToScreenPoint(imagePoint));
			}
		}
		return tempPointList;
	}

	/**
	 * Returns the screen points of marking layers.
	 *
	 * @param mLayers ArrayList of MarkingLayers which coordinates are converted
	 * @return the screen points of marking layers
	 */
	public ArrayList<ScreenCoordinatesOfMarkingLayer> getScreenPointsOfMarkingLayers(ArrayList<MarkingLayer> mLayers){
		try {
			ArrayList<ScreenCoordinatesOfMarkingLayer> screenCoordinatesOfMarkingLayerList = new ArrayList<ScreenCoordinatesOfMarkingLayer>();
			if(mLayers != null && mLayers.size()>0){
				Iterator<MarkingLayer> mlayerIterator= mLayers.iterator();
				while(mlayerIterator.hasNext()){
					MarkingLayer ml= mlayerIterator.next();
					if(ml.getCoordinateList() != null && ml.getCoordinateList().size()>0){
						screenCoordinatesOfMarkingLayerList.add(new ScreenCoordinatesOfMarkingLayer(getScreenPointsOfMarkingLayer(ml), ml.getLayerID()));
					}
				}
				return screenCoordinatesOfMarkingLayerList;
			}
			return null;
		} catch (Exception e) {
			LOGGER.severe("LayerVisualManager Error in converting relative point to screen point of several MarkingLayers: " +e.getMessage());
			return null;
		}
	}

	/**
	 * Returns the size multiplier.
	 *
	 * @return the size multiplier
	 */
	public double getSizeMultiplier(){
		return (((double)this.originalImage.getWidth())/this.imagePanelDimension.getWidth());
	}
	
	/**
	 * Returns a part of image by given middle point at screen and size at screen.
	 *
	 * @param middlepoint the middle point at screen
	 * @param size the size of image at screen
	 * @return the sub image
	 */
	public BufferedImage getSubImage(Point middlepoint, int size){
		try {
			if(this.originalImage != null){
				Point2D imagePoint = convertRelativePointAsPointAtImage(convertScreenPointToRelativePoint(middlepoint));
				double subImageSize = convertScreenDistanceToImageDistance(size);
				int x = (int)(imagePoint.getX() - subImageSize/2);
				int y = (int)(imagePoint.getY() - subImageSize/2);

				return this.originalImage.getSubimage(x, y, (int)subImageSize, (int)subImageSize);
			}
			else{
				return null;
			}
		} catch (Exception e) {
			LOGGER.severe("Error in getting subImage from");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param focusPoint Point at screen where focus will be changed
	 * @param zoomValue double value how much will be zoomed in or out. eg. 0.8 or 1.2
	 * @return PositionedImage which contains the zoomed image and it's top left corner location
	 */
	public PositionedImage getZoomedImage(Point2D focusPoint, double zoomValue){
		BufferedImage 	processedImage;
		Rectangle tempRelativeViewRectangle;
		try {
			// check that all needed objects exist
			if(this.originalImage == null || this.relativeViewRectangle == null || this.imagePanelDimension == null)
			return null;

		//  make own calculations for scaling either height or width

			// calculate temporary relativeImagePanelDimension -> save it as relativeImagePanelDimension when image is created successfully
			tempRelativeViewRectangle= calculateRelativeViewRectangleFocusedOnFocusPoint(focusPoint, zoomValue);

			// tempRelativeViewRectangle is proportional to ImagePanel size -> height or width may be bigger than originalImage -> calculate dimension of real cropped image
			Dimension imageDimension= getImageDimensionInsideRectangle(tempRelativeViewRectangle);
			if(imageDimension.getWidth() < 20 || imageDimension.getHeight() <20)
				return new PositionedImage(null);

			// crop the image
			processedImage = Scalr.crop(this.originalImage, tempRelativeViewRectangle.x, tempRelativeViewRectangle.y, imageDimension.width, imageDimension.height, null);

			// scale to ImagePanel
			return scaleToImagePanel(processedImage, tempRelativeViewRectangle);


		} catch (Exception e) {
			LOGGER.severe("Error in zooming image:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
		finally{
			processedImage=null;
			tempRelativeViewRectangle=null;
		}
	}

	/**
	 * Initializes and scales image to image panel.
	 *
	 * @return the positioned image
	 */
	private PositionedImage initAndscaleToImagePanel(){
		BufferedImage scaledImage;
		try {

			//  make own calculations for scaling either height or width
			Scalr.Mode  scalingMode= (Scalr.Mode)(getScalingMode(this.originalImage.getWidth(), this.originalImage.getHeight(),this.imagePanelDimension));

			// set up the quality/speed constant
			Scalr.Method processingQuality = Scalr.Method.ULTRA_QUALITY;

			// get new image scaled to Dimension of ImagePanel
			scaledImage= Scalr.resize(this.originalImage, processingQuality, scalingMode, this.imagePanelDimension.width, this.imagePanelDimension.height, null);

			if(scaledImage != null){
				// calculate the initial dimension of relativeViewRectangle where topleft corner coordinate is (0,0)
				this.setRelativeViewRectangle(calculateRelativeDimensionToImagePanel(scalingMode, this.originalImage.getWidth(), this.originalImage.getHeight()));
				// return PositionedImage with scaled image and default topleft corner position (0,0)
				return new PositionedImage(scaledImage);
			}
			else
				return null;

		} catch (IllegalArgumentException e) {
			LOGGER.severe("Error Arguments in scaling image:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		} catch (ImagingOpException e) {
			LOGGER.severe("Error in imagingOP when scaling image:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
		finally{
			scaledImage=null;
		}
	}

	/**
	 * Moves relative view rectangle at original image by given movement at image.
	 *
	 * @param movement the movement at image
	 * @return the moved Rectangle
	 */
	private Rectangle moveRelativeViewRectangle(Point2D movement){
		Rectangle tempRelativeViewRectangle = new Rectangle(this.relativeViewRectangle.x+(int)movement.getX(), this.relativeViewRectangle.y+(int)movement.getY(),
				this.relativeViewRectangle.width, this.relativeViewRectangle.height);

		if(tempRelativeViewRectangle.x + tempRelativeViewRectangle.width > this.originalImage.getWidth())
			tempRelativeViewRectangle.x = this.originalImage.getWidth()-tempRelativeViewRectangle.width;

		if(tempRelativeViewRectangle.y+tempRelativeViewRectangle.height > this.originalImage.getHeight())
			tempRelativeViewRectangle.y= this.originalImage.getHeight()-tempRelativeViewRectangle.height;
		if(tempRelativeViewRectangle.x<0)
			tempRelativeViewRectangle.x=0;
		if(tempRelativeViewRectangle.y<0)
			tempRelativeViewRectangle.y=0;

		return tempRelativeViewRectangle;

	}

	/**
	 * Reads the image file, creates and returns a BufferedImage. Tif-files are created by packet JAI and other formats with ImageIO.
	 *
	 * @param file the image File
	 * @return the created BufferedImage
	 * @throws Exception the exception
	 */
	public BufferedImage readImageFile(File file) throws Exception{

		if(Utils.getExtension(file).equals(Utils.tif) || Utils.getExtension(file).equals(Utils.tiff)){
			PlanarImage pim=null;
			pim= JAI.create("fileload", file.getAbsolutePath());
			if(pim != null)
				return pim.getAsBufferedImage();
			else
				throw new Exception();
		}
		else{
		//	return ImageIO.read(file);
			BufferedImage in = ImageIO.read(file);
			BufferedImage newImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);

			Graphics2D g = newImage.createGraphics();
			g.drawImage(in, 0, 0, null);
			g.dispose();
			in=null;
			return newImage;
		}
	}

	/**
	 * Removes the single marking coordinate.
	 *
	 * @param screenPoint the screen point
	 * @param selectedMarkingLayer the selected marking layer
	 * @return true, if successful
	 */
	public boolean removeSingleMarkingCoordinate(Point screenPoint, MarkingLayer selectedMarkingLayer){
		if(selectedMarkingLayer != null ){
			Point pointAtImage= convertRelativePointAsPointAtImage(convertScreenPointToRelativePoint(screenPoint));
			if(pointAtImage != null && selectedMarkingLayer != null){
				return selectedMarkingLayer.removeSingleCoordinate(pointAtImage);

			}
		}
		return false;

	}

	/**
	 * Scales image to ImagePanel and finalizes the temporary relative Rectangle to current relative Rectangle.
	 *
	 * @param processedImage the image to be scaled
	 * @param tempRelativeView the unfinalized relative Rectangle
	 * @return the PositionedImage scaled image
	 */
	private PositionedImage scaleToImagePanel(BufferedImage processedImage, Rectangle tempRelativeView){
		BufferedImage scaledImage;
		try {

			//  make own calculations should image being scaled either to height or width
			Scalr.Mode  scalingMode= (Scalr.Mode)(getScalingMode(processedImage.getWidth(), processedImage.getHeight(),this.imagePanelDimension));

			// set up the quality/speed constant -> AUTOMATIC seems to produce good quality images
			Scalr.Method processingQuality = Scalr.Method.AUTOMATIC;


			// get new image scaled to Dimension of ImagePanel
			scaledImage= Scalr.resize(processedImage, processingQuality, scalingMode, this.imagePanelDimension.width, this.imagePanelDimension.height, null);

			if(scaledImage != null){
				// save the used tempRelativeView as relativeViewRectangle
				this.setRelativeViewRectangle(tempRelativeView);
				// return PositionedImage with scaled image and position converted to screen
				return new PositionedImage(scaledImage, convertRelativePointToScreenPoint(new Point(tempRelativeView.x, tempRelativeView.y)));
			}
			else
				return null;

		} catch (IllegalArgumentException e) {
			LOGGER.severe("Error Arguments in scaling image:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		} catch (ImagingOpException e) {
			LOGGER.severe("Error in imagingOP when scaling image:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
		finally{scaledImage=null;}

	}

	/**
	 * Sets the image panel dimension.
	 *
	 * @param ipd the new image panel dimension
	 */
	public void setImagePanelDimension(Dimension ipd){
		this.imagePanelDimension=ipd;
	}

	/**
	 * Sets the original image to shown.
	 *
	 * @param originalImageToShown the new original image to shown
	 */
	public void setOriginalImageToShown(BufferedImage originalImageToShown) {

		if(this.originalImage != null && originalImageToShown != null && (this.originalImage.getWidth() != originalImageToShown.getWidth()
				|| this.originalImage.getHeight() != originalImageToShown.getHeight() ) ){
			// refresh the relativeViewRectangle because the image size has changed and no earlier calculations can be used
			this.setRelativeViewRectangle(null);
		}
		this.originalImage = originalImageToShown;
	}

	/**
	 * Sets the relative view rectangle.
	 *
	 * @param relativeToImagePanelRect the new relative view rectangle
	 */
	public void setRelativeViewRectangle(Rectangle relativeToImagePanelRect) {
		this.relativeViewRectangle = relativeToImagePanelRect;
	}


	/**
	 * Sets the selected buffered image that will be shown in ImagePanel.
	 *
	 * @param imagePath the path of new image
	 * @throws Exception the exception
	 */
	public void setSelectedBufferedImage(String imagePath) throws Exception{
		File imageFile;
		try {
			if(imagePath != null){
			imageFile=new File(imagePath);
			this.setOriginalImageToShown(readImageFile(imageFile));
			}
			else{
				this.setOriginalImageToShown(null);
			}
		} catch (IOException e) {
			LOGGER.severe("Error in getting image from String path:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			this.setOriginalImageToShown(null);
		}
		finally{imageFile=null;}
	}
}

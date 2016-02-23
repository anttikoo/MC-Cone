/*
 * 
 */
package operators;

import gui.Color_schema;
import gui.file.Utils;
import gui.panels.GridPanel;
import gui.saving.ImageSet.SingleDrawImagePanel;
import information.Fonts;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import managers.TaskManager;
import com.sun.media.jai.codec.TIFFEncodeParam;

/**
 * The Class ImageCreator. A Class for creating image to file. Supported image files are gif, jpg, png and tif.
 */
public class ImageCreator implements Runnable {

	/** The size multiplier. */
	private double sizeMultiplier;
	
	/** The task manager. */
	private TaskManager taskManager;
	
	/** The gap. */
	private int gap;
	
	/** The rows. */
	private int rows;
	
	/** The columns. */
	private int columns;
	
	/** The list of SingleDrawImagePanels. */
	private ArrayList<SingleDrawImagePanel> sdpList;
	
	/** The image dimension. */
	private Dimension imageDimension;
	
	/** The single image dimension. */
	private Dimension singleImageDimension;
	
	/** The path of exported image. */
	private String path;
	
	/** The continue creating. */
	private boolean continueCreating=false;
	
	/** The image set created successfully. */
	private boolean imageSetCreatedSuccessfully=false;
	
	/** The Thread of creating set of images. */
	private Thread imageSetCreatorThread;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/**
	 * Instantiates a new ImageCreator.
	 *
	 * @param sizeMultiply the size multiplier
	 * @param taskManager the TaskManager
	 */
	public ImageCreator(double sizeMultiply, TaskManager taskManager){
		this.taskManager=taskManager;
		this.sizeMultiplier=sizeMultiply;
	}

	/**
	 * Creates the BufferedImage.
	 *
	 * @param imageLayer the image layer
	 * @param mLayerIDlist the m layer i dlist
	 * @return the buffered image
	 */
	public BufferedImage createBufferedImage(ImageLayer imageLayer, ArrayList<Integer> mLayerIDlist) {
		try {

			ArrayList<Integer>  drawnLayers = new ArrayList<Integer>();
			if(imageLayer.getImageFilePath() != null){
				File imageFile=new File(imageLayer.getImageFilePath());

				// read image
				BufferedImage bi=ImageIO.read(imageFile);

				if(bi != null){
					singleImageDimension=new Dimension(bi.getWidth(),bi.getHeight()); // set single image dimension for painting grid
					ArrayList<MarkingLayer> layersToDraw=getPrintingLayerList(imageLayer, mLayerIDlist);
					if(layersToDraw != null && layersToDraw.size()>0){
						int maxShapeSize=getMaxShapeSize(layersToDraw);
						BufferedImage biggerImage= new BufferedImage(bi.getWidth(),bi.getHeight()+maxShapeSize+15,BufferedImage.TYPE_INT_ARGB);


						Graphics2D g2d = biggerImage.createGraphics();
						g2d.setColor(Color_schema.grey_100);
						g2d.fillRect(0, 0, biggerImage.getWidth(), biggerImage.getHeight());
						g2d.drawImage(bi,0,0,bi.getWidth(),bi.getHeight(),0,0,bi.getWidth(),bi.getHeight(),Color_schema.grey_100,null);


						// draw marking example and title of MarkingLayers to bottom of image
						int label_x =maxShapeSize;
						int label_y= (int) (bi.getHeight()+maxShapeSize/2+5);
						for (Iterator<MarkingLayer> iterator = layersToDraw.iterator(); iterator.hasNext();) {
							MarkingLayer markingLayer = (MarkingLayer) iterator.next();
							drawnLayers.add(drawSingleMarkingLayer(g2d, markingLayer, new Point(label_x,label_y)));
							double markingSize=(markingLayer.getSize()*sizeMultiplier);
							label_x+=(int)markingSize;
							label_x=drawMarkingLayerLabel(g2d, label_x, label_y, markingLayer.getLayerName(), markingLayer.getColor(), Fonts.b22);
							if(label_x>bi.getWidth()-100){
								label_x=maxShapeSize;
								label_y=(int)(bi.getHeight()-maxShapeSize/2-5);
							}
						}
						return biggerImage;
					}
				}
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.severe("IOError in creating image at export!: "+e.getMessage());
			return null;
		}
		catch (Exception e) {
			LOGGER.severe("Error in creating image at export!: "+e.getMessage());
		e.printStackTrace();
		return null;
	}
	}

	/**
	 * Creates the image.
	 *
	 * @param imageLayer the ImageLayer
	 * @param mLayerIDlist the ID list of MarkingLayers to be drawn
	 * @param imagePath the image path for new image.
	 * @return the array the ID list of MarkingLayers with successfull drawing
	 * @throws Exception the exception
	 */
	public ArrayList<Integer> createImage(ImageLayer imageLayer, ArrayList<Integer> mLayerIDlist, String imagePath) throws Exception{
		try {
			ArrayList<Integer>  drawnLayers = new ArrayList<Integer>();
			if(imageLayer.getImageFilePath() != null){
				File imageFile=new File(imageLayer.getImageFilePath());

				BufferedImage bi=taskManager.createImageFile(imageFile);

				if(bi != null){
					singleImageDimension=new Dimension(bi.getWidth(),bi.getHeight());
					ArrayList<MarkingLayer> layersToDraw=getPrintingLayerList(imageLayer, mLayerIDlist);
					if(layersToDraw != null && layersToDraw.size()>0){
						int maxShapeSize=getMaxShapeSize(layersToDraw);
						BufferedImage biggerImage= new BufferedImage(bi.getWidth(),bi.getHeight()+maxShapeSize+15,BufferedImage.TYPE_INT_RGB);

						Graphics2D g2d = biggerImage.createGraphics();
						g2d.setColor(Color_schema.grey_100);
						g2d.fillRect(0, 0, biggerImage.getWidth(), biggerImage.getHeight());
						g2d.drawImage(bi,0,0,bi.getWidth(),bi.getHeight(),0,0,bi.getWidth(),bi.getHeight(),Color_schema.grey_100,null);

						// draw marking example and title of MarkingLayers to bottom of image
						int label_x =maxShapeSize;
						int label_y= (int) (bi.getHeight()+maxShapeSize/2+5);
						for (Iterator<MarkingLayer> iterator = layersToDraw.iterator(); iterator.hasNext();) {
							MarkingLayer markingLayer = (MarkingLayer) iterator.next();
							drawnLayers.add(drawSingleMarkingLayer(g2d, markingLayer, new Point(label_x,label_y)));
							double markingSize=(markingLayer.getSize()*sizeMultiplier);
							label_x+=(int)markingSize;
							label_x=drawMarkingLayerLabel(g2d, label_x, label_y, markingLayer.getLayerName(), markingLayer.getColor(), new Font("Consolas", Font.BOLD, 25));
							if(label_x>bi.getWidth()-100){
								label_x=maxShapeSize;
								label_y=(int)(bi.getHeight()-maxShapeSize/2-5);
							}
						}
						// write to file and return successfully drawn MarkingLayers
						if(writeToFile(biggerImage, imagePath))
							return drawnLayers;
						else
							return null;


					}
					
				}
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates the image of set of images.
	 *
	 * @return true, if successful
	 */
	public boolean createImageSet(){

		try {
			gap =(int)(gap*this.sizeMultiplier); // gap scaled to resultsize


			BufferedImage biggerImage= new BufferedImage(imageDimension.width,imageDimension.height,BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = biggerImage.createGraphics();
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, biggerImage.getWidth(), biggerImage.getHeight());

			int x=gap, y=gap;
			for(int r = 1; r<= rows;r++){
				if(!continueCreating) // user may cancel the writing
					return false;

				int maxHeightOfPanel=0, y_recent = y;
				for (int c = 1; c <= columns; c++) {
					if(!continueCreating) //user may cancel the writing
						return false;
					SingleDrawImagePanel sdp = getSDPatPosition(r, c, sdpList);
					if(sdp.getImage() != null){
						int titlePaneHeight=(int)(sdp.getTitlePanelHeight()*this.sizeMultiplier);
						Font drawingFont = new Font(sdp.getFont().getFamily(), sdp.getFont().getStyle(), (int)(sdp.getFont().getSize()*sizeMultiplier));
						// draw title
						drawImagetitle(g2d, x, y_recent,sdp.getTitle(), Color_schema.dark_20, drawingFont);
						g2d.setPaint(Color.white);
						y_recent+=titlePaneHeight;

						int imageWidth = (int)(sdp.getScaledImageDimension().getWidth()*sizeMultiplier);
						int imageHeight= (int)(sdp.getScaledImageDimension().getHeight()*sizeMultiplier);

						BufferedImage bi = sdp.getScaledImage(new Dimension(imageWidth, imageHeight));
						g2d.drawImage(bi,x,y_recent,x+bi.getWidth(),y_recent+ bi.getHeight(),0,0,bi.getWidth(),bi.getHeight(),null);

						if(titlePaneHeight+bi.getHeight() > maxHeightOfPanel)
							maxHeightOfPanel=titlePaneHeight+bi.getHeight();

					}
					else{
						if(sdp.getPanelSize().getHeight()*sizeMultiplier > maxHeightOfPanel){
							maxHeightOfPanel=(int)(sdp.getPanelSize().getHeight()*sizeMultiplier);
						}

					}

					x+=(int)(sdp.getPanelSize().getWidth()*sizeMultiplier)+gap; // set position to next column
					y_recent=y; // set y position to upper side of the row
				}

				y+=maxHeightOfPanel+gap; // go one grid lower
				x=gap; // start in left side
			}
			if(continueCreating) //user may cancel the writing
				return writeToFile(biggerImage, path); // write to file
			
			return false; // not saved the file
		} catch (Exception e) {
			LOGGER.severe("Error in creating ImageSet");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Draws a GRID to Graphics2D object.
	 *
	 * @param gd the Graphics2D
	 * @param mLayer the MarkingLayer to be drawn
	 * @throws Exception the exception
	 */
	private void drawGrid(Graphics2D gd, MarkingLayer mLayer) throws Exception{
		if(mLayer.isGridON() && singleImageDimension != null){
			GridPanel gridPanel=new GridPanel();
			gridPanel.setGridProperties(mLayer.getGridProperties());
			gridPanel.setBounds(0, 0, singleImageDimension.width, singleImageDimension.height);
			// draw the GRID
			gridPanel.drawGrid(gd);
		}
	}

	/**
	 * Draws a title to Graphics2D object.Calculates the needed space for text.
	 *
	 * @param g2d the g2d
	 * @param x the horizontal position
	 * @param y the vertical position
	 * @param label the label
	 * @param color the color of text
	 * @param font the font of text
	 * @return the int the height of needed space for text
	 * @throws Exception the exception
	 */
	private int drawImagetitle(Graphics2D g2d, int x, int y, String label, Color color, Font font) throws Exception{
	      g2d.setFont(font);
	      FontMetrics fontMetrics = g2d.getFontMetrics();
	      int stringWidth = fontMetrics.stringWidth(label);
	      int stringHeight = fontMetrics.getAscent();
	      int y_b=(int)(y+stringHeight-5); // lowest characters are at least 5 pixels above the image
	      g2d.setPaint(color);
	      g2d.drawString(label, x,y_b);
	      return x+stringWidth+30;
	}

	/**
	 * Draws MarkingLayer label to a Graphics2D object. Calculates the needed space for text.
	 *
	 * @param g2d the Graphics 2D
	 * @param x the horizontal position
	 * @param y the vertical position
	 * @param label the label
	 * @param color the color of text
	 * @param font the font
	 * @return the int height of needed space for label
	 * @throws Exception the exception
	 */
	private int drawMarkingLayerLabel(Graphics2D g2d, int x, int y, String label, Color color, Font font) throws Exception{
	      g2d.setFont(font);
	      FontMetrics fontMetrics = g2d.getFontMetrics();
	      int stringWidth = fontMetrics.stringWidth(label); // get width of text
	      int stringHeight = fontMetrics.getAscent(); // get height
	      int y_b=(int)(y+stringHeight/2);
	      g2d.setPaint(color);
	      g2d.drawString(label, x,y_b); // draw String
	      return x+stringWidth+30;
	}


	/**
	 * Draws single MarkingLayer to Graphics2D object.
	 *
	 * @param g2d the Graphics2D object
	 * @param ml the MarkingLayer
	 * @param labelMarkPoint the position of drawed shape
	 * @return the int ID of successfully drawed MarkingLayer 
	 * @throws Exception the exception
	 */
	private int drawSingleMarkingLayer(Graphics2D g2d, MarkingLayer ml, Point labelMarkPoint) throws Exception{
		if(ml.isDrawGridToImage())
			drawGrid(g2d, ml);
		int size=(int)(ml.getSize()*sizeMultiplier);
		int thickness= (int)(ml.getThickness()*sizeMultiplier);
		if(thickness<1)
			thickness=1;
		ShapeDrawer shapeDrawer=new ShapeDrawer(ml, size, thickness, ml.getOpacity(), ml.getColor());
		Iterator<Point> coordinateIterator=null;
		if(ml.isDrawGridToImage()){
			coordinateIterator =ml.getCoordinateList().iterator();
		}
		else
			coordinateIterator =ml.getCoordinateListIgnoringGrid().iterator();

		 switch(ml.getShapeID())
         {
             case ID.SHAPE_OVAL:
	     		while(coordinateIterator.hasNext()){
	     			Point c = coordinateIterator.next();
	     			shapeDrawer.drawOval(g2d, c.x, c.y);
	     		}
	     		shapeDrawer.drawOval(g2d, labelMarkPoint.x, labelMarkPoint.y);
                break;
             case ID.SHAPE_DIAMOND:
	     		while(coordinateIterator.hasNext()){
	     			Point c = coordinateIterator.next();
	     			shapeDrawer.drawDiamond(g2d, c.x, c.y);
	     		}
	     		shapeDrawer.drawDiamond(g2d, labelMarkPoint.x, labelMarkPoint.y);
                 break;
             case ID.SHAPE_PLUS:
	     		while(coordinateIterator.hasNext()){
	     			Point c = coordinateIterator.next();
	     			shapeDrawer.drawPlus(g2d, c.x, c.y);
	     		}
	     		shapeDrawer.drawPlus(g2d, labelMarkPoint.x, labelMarkPoint.y);
                 break;
             case ID.SHAPE_CROSS:
	     		while(coordinateIterator.hasNext()){
	     			Point c = coordinateIterator.next();
	     			shapeDrawer.drawCross(g2d, c.x, c.y);
	     		}
	     		shapeDrawer.drawCross(g2d, labelMarkPoint.x, labelMarkPoint.y);
                 break;
             case ID.SHAPE_SQUARE:
	     		while(coordinateIterator.hasNext()){
	     			Point c = coordinateIterator.next();
	     			shapeDrawer.drawSquare(g2d, c.x, c.y);
	     		}
	     		shapeDrawer.drawSquare(g2d, labelMarkPoint.x, labelMarkPoint.y);
                break;

             case ID.SHAPE_TRIANGLE:
		     	while(coordinateIterator.hasNext()){
		     		Point c = coordinateIterator.next();
		     		shapeDrawer.drawTriangle(g2d, c.x, c.y);
		     	}
		     	shapeDrawer.drawTriangle(g2d, labelMarkPoint.x, labelMarkPoint.y);
		     	break;
             default : // circle

	     		while(coordinateIterator.hasNext()){
	     			Point c = coordinateIterator.next();
	     			shapeDrawer.drawCircle(g2d, c.x, c.y);
	     		}
	     		shapeDrawer.drawCircle(g2d, labelMarkPoint.x, labelMarkPoint.y);
             	break;
         }
		 return ml.getLayerID();
	}


	/**
	 * Returns the max shape size of MarkingLayers in given list.
	 *
	 * @param mLayers the list of MarkingLayers
	 * @return the maximum shape size
	 * @throws Exception the exception
	 */
	private int getMaxShapeSize(ArrayList<MarkingLayer> mLayers) throws Exception{
		int max_size=0;
		for (Iterator<MarkingLayer> iterator = mLayers.iterator(); iterator.hasNext();) {
			MarkingLayer markingLayer = (MarkingLayer) iterator.next();
			if(markingLayer.getSize()>max_size)
				max_size=markingLayer.getSize();
		}
		return max_size;
	}

	/**
	 * Returns the list of MarkingLayers which has selected to be drawn.
	 *
	 * @param il the ImageLayer
	 * @param selected_mLayers List of IDs of selected MarkingLayers
	 * @return the printing layer list
	 * @throws Exception the exception
	 */
	private ArrayList<MarkingLayer> getPrintingLayerList(ImageLayer il, ArrayList<Integer> selected_mLayers) throws Exception{
		ArrayList<MarkingLayer> layersToDraw = new ArrayList<MarkingLayer>();
		for (Iterator<Integer> iterator = selected_mLayers.iterator(); iterator.hasNext();) {
			int selected_mLayerID = (int) iterator.next();
			// go through MarkingLayer of single ImageLayer
			Iterator<MarkingLayer> mIterator= il.getMarkingLayers().iterator();
			while(mIterator.hasNext()){
				MarkingLayer ml=mIterator.next();
				if(ml.getLayerID()==selected_mLayerID){
					layersToDraw.add(ml);
				}
			}
		}
		return layersToDraw;
	}

	/**
	 * Returns the SingleDrawImagePanel at position (row, column) of set of images.
	 *
	 * @param r the row
	 * @param c the c
	 * @param sdpList the sdp list
	 * @return the SD pat position
	 * @throws Exception the exception
	 */
	private SingleDrawImagePanel getSDPatPosition(int r, int c, ArrayList<SingleDrawImagePanel> sdpList) throws Exception{
		Iterator<SingleDrawImagePanel> sdpIterator=sdpList.iterator();
		while(sdpIterator.hasNext()){
			SingleDrawImagePanel sdp=(SingleDrawImagePanel)sdpIterator.next();
			if(sdp.getGridPosition() != null && sdp.getGridPosition()[0] == r && sdp.getGridPosition()[1] == c)
				return sdp;
		}
		return null;
	}

	/**
	 * Initializes the image set properties.
	 *
	 * @param gap the gap
	 * @param rows the rows
	 * @param columns the columns
	 * @param sdpList the SingleDrawImagePanel list
	 * @param imageDimension the image dimension to be exported
	 * @param path the path of exported Image
	 * @throws Exception the exception
	 */
	public void initImageSetProperties(int gap, int rows, int columns, ArrayList<SingleDrawImagePanel> sdpList, Dimension imageDimension, String path) throws Exception{
		this.gap=gap;
		this.rows=rows;
		this.columns=columns;
		this.sdpList=sdpList;
		this.imageDimension=imageDimension;
		this.path=path;
		this.continueCreating=true;
		this.imageSetCreatorThread=new Thread(this, "imageset");
	}

	/**
	 * Checks if is continue creating.
	 *
	 * @return true, if is continue creating
	 * @throws Exception the exception
	 */
	public boolean isContinueCreating() throws Exception {
		return continueCreating;
	}

	/**
	 * Checks if is image set created successfully.
	 *
	 * @return true, if is image set created successfully
	 * @throws Exception the exception
	 */
	public boolean isImageSetCreatedSuccessfully()throws Exception{
		return imageSetCreatedSuccessfully;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			this.imageSetCreatedSuccessfully= createImageSet();
			this.setContinueCreating(false);
		} catch (Exception e) {
			LOGGER.severe("Error in starting creating Image Set !");
			e.printStackTrace();
		}

	}

	/**
	 * Sets the continue creating.
	 *
	 * @param continueCreating the new continue creating
	 * @throws Exception the exception
	 */
	public void setContinueCreating(boolean continueCreating) throws Exception {
		this.continueCreating = continueCreating;
	}


	/**
	 * Starts Thread of ImageSetCreator.
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean startImageSetCreatorThread() throws Exception{
		this.setContinueCreating(true);
		this.imageSetCreatorThread.start();
		return isImageSetCreatedSuccessfully();
	}

	/**
	 * Writes image to file.
	 *
	 * @param bi the bi
	 * @param imagePath the image path
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private boolean writeToFile(BufferedImage bi, String imagePath) throws Exception {
		try {
			// if tif write in own method
			String extension=Utils.getExtension(imagePath);
			if(extension.equalsIgnoreCase("tiff") || extension.equalsIgnoreCase("tif")){
				return writeToTIff(bi, imagePath);
			}
			else{ // not tif
				return writeToNormalFile(bi, imagePath, extension);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Writes to normal files: gif, jpg, png.
	 *
	 * @param bi the BufferedImage
	 * @param imagePath the image path
	 * @param extension the extension of file
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private boolean writeToNormalFile(BufferedImage bi, String imagePath, String extension) throws Exception{

	    try {

			File outputfile = new File(imagePath);
			if(!outputfile.exists())
				outputfile.createNewFile();
			ImageIO.write(bi, extension, outputfile);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Writes to tiff to file.
	 *
	 * @param bi the BufferedImage
	 * @param path the path
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private boolean  writeToTIff(BufferedImage bi, String path) throws Exception{

		try {
			TIFFEncodeParam params = new TIFFEncodeParam();
			FileOutputStream fos = new FileOutputStream(path);
			JAI.create("encode", bi, fos, "TIFF",params);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
}

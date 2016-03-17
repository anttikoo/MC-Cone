package operators;

import information.CentroidFromNpoints;
import information.ColorChannelVectors;
import information.ID;
import information.ImageColorChannels;
import information.SharedVariables;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import managers.PreCountThreadManager;
import managers.TaskManager;
import math.geom2d.Point2D;
import gui.ProgressBallsDialog;

/**
 * The Class PreCounterThread. The cells of original image are counted by using image of picked cells. 
 * Colors or picked cell are split to each channels (alpha, red, green, blue). Each channel value is spread for wider color range and saved to colorList.
 * For example red value for some pixel is 200, then the value is spread for 3 values down and 3 values up (197,198,199,200,201,201,202).
 * When color channel values are determined the original image will be processed.
 *  Not all pixels will be determined. The gap value tells how many pixels are let as undetermined between determined pixels.
 *  If determined pixels match with colorList color: collected. The collected pixel positions are grouped to form individual cell positions.
 *  
 */
public class PreCounterThread implements Runnable{

	
	
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The cancelled inside. Boolean is precounting stopped by program. */
	private boolean cancelledInside=false;
	
	/** The cell size max scaling factor. When user picks a cell -> all cell sizes that are 25% bigger than picked one are taken to countings. */
	private double cellSizeMaxScalingFactor=1.2;
	
	/** The cell size min scaling factor. When user picks a cell -> all cell sizes that are  at most 25% smaller than picked one are taken to countings. */
	private double cellSizeMinScalingFactor=0.8;
	
	/** The color list. List of different colors that are tried to find from image.  */
	private ArrayList<Integer> colorList;
	

	/** The continue counting. Boolean to check has user stopped precounting or is precounting ready. */
	private boolean continueCounting=true;

	private ArrayList<Point> copy_of_current_finalCoordinates;
	
	/** The counter thread. Thread doing the counting. */
	private Thread counterThread;
		
	/** The current_color list. Used for finding cells */
	private ArrayList<Integer> current_colorList;
	
	/** The current_final centroid coordinates. Results of this run of Thread. If everything will go ok -> there coordinates saved to finalCenteroidCoordinates.  */
	private ArrayList<Point> current_finalCentroidCoordinates;
	
	/** The current_final coordinates.  */
	private ArrayList<Point> current_finalCoordinates;
	
	/** The current_gap. A gap between pixels that are evaluated.  */
	private int current_gap =2;
	
	/** The current_max_cell_size. */
	private int current_max_cell_size;
	/** The current_max_coordinate_number_in_cell. */
	private int current_max_coordinate_number_in_cell=Integer.MIN_VALUE;
	
	/** The current_min_cell_size. */
	private int current_min_cell_size;
	
	private ExecutorService executor= null;
	
	/** The final centroid coordinates. These coordinates are the final results and markings are painted in this positions of image. */
	private ArrayList<Point> finalCentroidCoordinates;
	
	/** The forced_gap. If problem with too small gap, then forced it bigger*/
	private int forced_gap=0;
	
	/** The global_min_cell_diameter. This is global minimum for cell diameter */
	private final int global_min_cell_diameter =SharedVariables.GLOBAL_MIN_CELL_DIAMETER; // 
	
	/** The global_min_coordinate_number_in_cell. This is global minimum for coordinate number. */
	private final int global_min_coordinate_number_in_cell =SharedVariables.GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL;
	
	/** The max_cell_size. only initial value which is changed when user picks bigger cells */
	private int max_cell_size=0;
	
	/** The max_coordinate_number_in_cell. */
	private int max_coordinate_number_in_cell=Integer.MIN_VALUE;
	
	/** The max cell number in cell group. Prevents that stack of coordinates will not be too big. */
	private int maxCellNumberInCellGroup=10;
	
	/** The min_cell_size. */
	private int min_cell_size=Integer.MAX_VALUE;  
	
	/** The min_distance_between_cells_boundaries. */
	private int min_distance_between_cells_boundaries=5; 
	
	/** The original image. The image where cells will be precounted. */
	private BufferedImage originalImage;
	
	/** The original image pixels. */
	private byte[] originalImagePixels;
	
	/** The PreCountThreadManager. Manages outside this Thread and progress Thread. */
	private PreCountThreadManager pctm;
	
	/** The pixel_color_relaxation. How much each color channel of pixel will be spread. */
	private int pixel_color_relaxation=3; // from 0 - 5 is ok
	
	private ThreadPoolExecutor poolExecutor=null;
	

	/** The sub image. The image that user picks inside the rectangle. */
	private BufferedImage subImage;
	
	
	/** The sub image pixels. */
	private byte[] subImagePixels;



	/** The task manger. */
	private TaskManager taskManger;;
	
	/**
	 * Instantiates a new PreCounterThread.
	 *
	 * @param subImage the sub image
	 * @param originalImage the original image
	 * @param taskManager the task manager
	 */
	public PreCounterThread(BufferedImage subImage, BufferedImage originalImage , TaskManager taskManager){
		try {
			
			this.taskManger=taskManager;
			this.subImage=subImage;
			this.originalImage=originalImage;
			this.counterThread=new Thread(this, "counter");
			this.colorList=new ArrayList<Integer>();
			this.finalCentroidCoordinates = new ArrayList<Point>();
			setUpMultiThreading();
			this.forced_gap=0;
		} catch (Exception e) {
			LOGGER.severe("Error in initializing Precounter!");
			e.printStackTrace();
			this.cancelledInside=true;
		}
	}

	/**
	 * Aborts the  execution.
	 *
	 * @param title the title
	 * @param message the message
	 */
	private void abortExecution(String title, String message){
		try {
			if(cancelledInside)
				this.taskManger.showMessageToUser(title, message);
		} catch (Exception e) {
			LOGGER.severe("Error in cancelling precount execution!");
			e.printStackTrace();
		}
		clean();
	}
	

	/**
	 * Adds the colors to colorList if identical color not found.
	 *
	 * @param newColorList the new color list
	 */
	private void addColorsIfNotFound(ArrayList<Integer> newColorList) throws Exception{

		for (Iterator<Integer> iterator = newColorList.iterator(); iterator.hasNext();) {
			int colorInt = (int) iterator.next();
			if(!this.current_colorList.contains(colorInt)){
				this.current_colorList.add(colorInt);
			}
		}

	}

	/**
	 * Calculates centroid of group of WeightPoints.
	 *
	 * @param weightPointList the weight point list
	 * @return the point
	 */
	private Point calculateCentroid(ArrayList<WeightPoint> weightPointList ){
		   try {
			if(weightPointList != null && weightPointList.size()>0){
				   double sum_Of_weights=0;

				   double sum_Of_X_distance_weights=0;
				   double sum_Of_Y_distance_weights=0;

				   Iterator<WeightPoint> wIterator= weightPointList.iterator();
				   while(wIterator.hasNext()){
					   WeightPoint wp = wIterator.next();
					   sum_Of_weights+= wp.getWeight();
					   sum_Of_X_distance_weights += wp.getPoint().x*wp.getWeight();
					   sum_Of_Y_distance_weights += wp.getPoint().y*wp.getWeight();

				   }

				   int x_coord= (int)(sum_Of_X_distance_weights/sum_Of_weights);
				   int y_coord = (int)(sum_Of_Y_distance_weights/sum_Of_weights);
				   return new Point(x_coord, y_coord);
			   }
			   return null;
		} catch (Exception e) {
			LOGGER.severe("Error in calculating centroids!");
			e.printStackTrace();
			return null;
		}
	   }

	/**
	 * Determines colors channels into vectors. Alpha, red, green and blue are separated channels. 
	 * Results is smoothed with MovinAverage to remove dispersion of color values.
	 * 
	 *
	 * @param channels the channels of colors
	 * @param mod_x the horizontal direction change
	 * @param mod_y the vertical direction change
	 * @return the color channel vectors
	 * @throws Exception the exception
	 */
	private ColorChannelVectors calculateColorsForVectors(ImageColorChannels channels, int mod_x, int mod_y) throws Exception {
		int maxLength=((int)(channels.getHeight()/2));
 		ColorChannelVectors colorVectors=new ColorChannelVectors(maxLength );
		Point midPoint= new Point((int)(channels.getWidth()/2), (int)(channels.getHeight()/2));
		int[] alpha=new int[maxLength];
		int[] red=new int[maxLength];
		int[] green=new int[maxLength];
		int[] blue=new int[maxLength];
		double[] alpha_ma=new double[maxLength];
		double[] red_ma=new double[maxLength];
		double[] green_ma=new double[maxLength];
		double[] blue_ma=new double[maxLength];
		for(int x=midPoint.x, y=midPoint.y, i=0; x < channels.getWidth() && x >= 0 && y<channels.getHeight() && y >= 0 && i<colorVectors.getSize();x+=mod_x, y+=mod_y, i++){
			if(channels.useAlpha()){
				alpha[i]= channels.getAlpha(x,y);
				red[i]= channels.getRed(x,y);
				green[i] = channels.getGreen(x,y);
				blue[i] = channels.getBlue(x,y);
			}
			else{

				red[i]= channels.getRed(x,y);
				green[i] = channels.getGreen(x,y);
				blue[i] = channels.getBlue(x,y);
			}
			if(!continueCounting)
				return null;
		}

		// smooth data with moving average -> removes dispersion of color values

            MovingAverage ma = new MovingAverage(channels.getHeight()/10);
            int i=0;
            for (int x : red) {
                ma.newNum(x);
                red_ma[i]=ma.getAvg();
                i++;
                if(!continueCounting)
					return null;
            }

            i=0;
            ma = new MovingAverage(channels.getHeight()/10);
            for (int x : green) {
                ma.newNum(x);
        
                green_ma[i]=ma.getAvg();
                i++;
                if(!continueCounting)
					return null;
            }
  
            i=0;
            ma = new MovingAverage(channels.getHeight()/10);    
            for (int x : blue) {
                ma.newNum(x);	         
                blue_ma[i]=ma.getAvg();
                i++;
                if(!continueCounting)
					return null;
            }
		            
		    colorVectors.setOriginalData(alpha, red, green, blue);
		    colorVectors.setMoveAveragedData(alpha_ma, red_ma, green_ma, blue_ma);
		return colorVectors;
	}



	/**
	 * Goes trough the pixels of original image and checks is the color found from colorList. 
	 * If found -> coordinate of pixel saved to list.
	 *
	 * @throws Exception the exception
	 */
	private void calculateCoordinates() throws Exception{
		   getOriginalImageAsByteArray(this.originalImage);
	
		   this.current_finalCoordinates=new ArrayList<Point>();
		   int w= this.originalImage.getWidth();
		   int h = this.originalImage.getHeight();
	
		   // go through colorMatrix
		   for(int r =0;r<h;r+=current_gap){ // rows
			   for(int c =0;c<w;c+=current_gap){ // columns
				   if(!continueCounting){
					   return;
				   }
				   if(hasCellColor(r,c,w)){ // the pixels is same as some color in picked cell
					   Point p = new Point(c,r);
					   if(!this.current_finalCoordinates.contains(p))
					   {
	
						   this.current_finalCoordinates.add(p);
					   }
				   }
			   } // for columns
			   int progress = (int) Math.round(((double)r/(double)h)*10);
			   this.pctm.updateProgressBar(progress);
		   } // for rows
		   Collections.sort(this.current_finalCoordinates, new CoordinateComparator());
	   }




	/**
	 * Goes trough the pixels of original image and checks is the color found from colorList. 
	 * If found -> coordinate of pixel saved to list.
	 *
	 * @throws Exception the exception
	 */
	private void calculateCoordinatesMultiThreaded() throws Exception{
		   getOriginalImageAsByteArray(this.originalImage);
		   ArrayList<CreateCoordinatesTask> createTaskList=new ArrayList<CreateCoordinatesTask>();
		   final ArrayBlockingQueue<Runnable> queue =new ArrayBlockingQueue<Runnable>(SharedVariables.availableProcessors);
		   this.current_finalCoordinates=new ArrayList<Point>();
		   int w= this.originalImage.getWidth();
		   int h = this.originalImage.getHeight();
		   
		   int pixelRows=(int) Math.round((((double)h)/(double)SharedVariables.availableProcessors)); // count how many rows made, splitted to each available processor
		   LOGGER.fine("Rows in splitted image: "+pixelRows+ " image width: "+w);
		   
		   // go through whole byte array by collecting bytes to smaller byte arrays. New byte array contains as many bytes as image has pixels in pixelRows.
		   // create Threads and add to list
		   boolean continueFor=true;
		   for(int startIndex=0, endIndex =(pixelRows*w)*3-1, rowCounter=0;endIndex<this.originalImagePixels.length &&  continueFor;  rowCounter+=pixelRows){
			   try{ // startIndex += endIndex+1, endIndex += (pixelRows*w)*3,
				   if(continueCounting){
					   if(endIndex == this.originalImagePixels.length-1) // at last index
						   continueFor=false;
						   
					   int rows= (endIndex-startIndex+1)/(3*w);
					   System.out.println(" index: " +endIndex + " of "+this.originalImagePixels.length);
					   createTaskList.add(new CreateCoordinatesTask(rows, w, 
							   Arrays.copyOfRange(this.originalImagePixels, startIndex, endIndex+1),  // has to add 1 to get last index included
							   rowCounter, this.current_gap, this.current_colorList));
					
					   // set start and end indexes
					   startIndex += (pixelRows*w)*3; 
					   endIndex += (pixelRows*w)*3;
					   
					   if(endIndex  >= this.originalImagePixels.length) // if overflow -> set to last inder of array
						   endIndex=this.originalImagePixels.length-1;
	   
				   }
				   else
					   return;
			   }
			   catch(Exception e){
				   LOGGER.severe("Error in creating CreateCoordinatesTask!");
				  cancelInside();
				   
				   e.printStackTrace();
			   }
		   }
		   
		
		   
		if (createTaskList.size() > 0) {
			// create executor poolsize 10, keepAlive 10 minutes.
			poolExecutor = new ThreadPoolExecutor(10, 10, 600, TimeUnit.SECONDS, queue);
			Iterator<CreateCoordinatesTask> taskIterator = createTaskList.iterator();
			while (taskIterator.hasNext()) { // add tasks to executor and start executing them
				
				poolExecutor.execute(taskIterator.next());
			}
			poolExecutor.shutdown();
			// update progress until all tasks are terminated or user cancels the precounting
			while (continueCounting && !poolExecutor.isTerminated()) {
				

				updateCreatingProgress((int)poolExecutor.getCompletedTaskCount()); // send the value
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			} 
		}
			// in cancelled outside -> shut down executor
			if(!continueCounting){
				if(!poolExecutor.isTerminated()){
					poolExecutor.shutdownNow();
					
				}
				return;

			}
			
			// collect the coordinates
			Iterator<CreateCoordinatesTask> taskCheckIterator = createTaskList.iterator();
			while (taskCheckIterator.hasNext()) {
				CreateCoordinatesTask singleTask = taskCheckIterator.next();
				if(singleTask.getCoordinates() != null && singleTask.getCoordinates().size() > 0)
					this.current_finalCoordinates.addAll(singleTask.getCoordinates());
						
			}
 
		   // sort coordinates although they should be in right order
		   Collections.sort(this.current_finalCoordinates, new CoordinateComparator());
	   }
	
	
	
	/**
	 * Calculates outer radius area.
	 *
	 * @param countNumber the count number
	 * @param radius_bigger the radius_bigger
	 * @param radius_smaller the radius_smaller
	 * @return the double
	 */
	private double calculateOuterRadiusArea(int countNumber, int radius_bigger, int radius_smaller){
		   try {
			double areaBigger= Math.PI*(double)radius_bigger*(double)radius_bigger;
			   double areaSmaller= Math.PI*(double)radius_smaller*(double)radius_smaller;
			   return ((double)countNumber)/(areaBigger-areaSmaller);
		} catch (Exception e) {
			LOGGER.severe("Error in calucalating outer radius area in precounting!");
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Cancels the counting. Mediates the canceling to PreCountThreadManager.
	 */
	public void cancelInside(){
		this.continueCounting=false;
		this.cancelledInside=true;

		if(this.pctm != null)
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					try {
						pctm.cancelCounting();
					} catch (Exception e) {
						LOGGER.severe("Error in cancelling counting by program!");
						e.printStackTrace();
					}

				}
			});
	}

	/**
	 * The counting is cancelled outside by user.
	 *
	 * @throws Exception the exception
	 */
	public void cancelOutside() throws Exception{
		this.continueCounting=false;
		
		if(this.executor != null)
			this.executor.shutdown();

		if(this.pctm != null)
			this.pctm.cancelCounting();


	}

	/**
	 * Checks over bounds of color values. The range should be inside 0-255.
	 *
	 * @param colorValue the color value
	 * @return the int
	 * @throws Exception the exception
	 */
	private int checkOverBounds(int colorValue) throws Exception{
		if(colorValue<0)
			return 0;
		if(colorValue>255)
			return 250;
		return colorValue;
	}

	/**
	 * Cleans the counting thread variables to start a fresh counting.
	 */
	private void clean(){
		try {
			this.cancelledInside=false;
			this.current_finalCoordinates=null;
			this.current_colorList=null;
			this.current_finalCentroidCoordinates=null;
			this.current_max_cell_size=0;
		
		} catch (Exception e) {
			LOGGER.severe("Error in cleaning Precounter!");
			e.printStackTrace();
		}
	}
	

	/**
	 * Collects the inner cell colors from sub image that user has picked.
	 *
	 * @param radius the maximum distance from middle point
	 * @throws Exception the exception
	 */
	private void collectInnerCellColorsFromSubImage(int radius) throws Exception{
		getSubImageAsByteArray(this.subImage);

		if(this.subImagePixels != null){
			int w=this.subImage.getWidth();
			int h=this.subImage.getHeight();
			Point midPoint=new Point((int)(w/2),(int)(h/2));
	
			for(int x=midPoint.x-radius; x<midPoint.x+radius;x++){ // go from midpoint to edges
	
				int delta_y = (int)(Math.sqrt((Math.pow(radius, 2)) - Math.pow((x-midPoint.x),2)));
				int start_y= midPoint.y -delta_y;
				int end_y = midPoint.y +delta_y;
	
				for(int y=start_y;y<end_y;y++){
					int index = (y + x*w)*3;
					int argb =  ( 255 << 24) | ((int)(this.subImagePixels[index+2] & 0xFF) << 16) | ((int)(this.subImagePixels[index + 1] & 0xFF) << 8) | ((int)this.subImagePixels[index] & 0xFF);
					addColorsIfNotFound(getRelaxedColors(this.pixel_color_relaxation, argb));
				}
			}
		}
	}

	/**
	 * Compares is point too close to any point at pool of current coordinates.
	 *
	 * @param midPoint the mid point
	 * @return true, if successful
	 */
	private boolean compareIsTooClose(Point midPoint, ArrayList<Point> pointListToCheck){
		try {
			if (midPoint != null && pointListToCheck != null && pointListToCheck.size()>0) {
				Iterator<Point> coordIterator = pointListToCheck.iterator();
				while (coordIterator.hasNext()) {
					Point p = coordIterator.next();
					if (midPoint.x > p.x - this.current_max_cell_size && midPoint.x < p.x + this.current_max_cell_size)
						if (midPoint.y > p.y - this.current_max_cell_size
								&& midPoint.y < p.y + this.current_max_cell_size)
							return true;
				} 
			}
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	


	/**
	 * Convert the BufferedImage to image type for precounting.
	 *
	 * @param src the source BufferedImage
	 * @param bufImgType the image type
	 * @return the BufferedImage
	 * @throws Exception the exception
	 */
	private BufferedImage convert(BufferedImage src, int bufImgType) throws Exception {
		    BufferedImage img= new BufferedImage(src.getWidth(), src.getHeight(), bufImgType);
		    Graphics2D g2d= img.createGraphics();
		    g2d.drawImage(src, 0, 0, null);
		    g2d.dispose();
		    return img;
		}
	
	/**
	 * Converts image to channels for alpha, red, green and blue.
	 *
	 * @param imageIn the source image
	 * @return the image color channels
	 */
	private ImageColorChannels convertImageToChannels(BufferedImage imageIn) {
	
	      try {
	    	  BufferedImage image = convert(imageIn, BufferedImage.TYPE_3BYTE_BGR);
			final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			  final int width = image.getWidth();
			  final int height = image.getHeight();
			  ImageColorChannels channels=new ImageColorChannels(height, width);
			  final boolean hasAlphaChannel = image.getAlphaRaster() != null;
	
			  LOGGER.fine("types: " +pixels[0]+ " "+ pixels[1]+" "+pixels[2]);
	
			  // the image is not 4 bit bufferedImage -> make that way it is!!!!
			  if (hasAlphaChannel) {
				  LOGGER.fine("using alpha channel");
				  channels.setUseAlpha(true);
			     final int pixelLength = 4;
			     for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
			        channels.addAlpha(row,col,(((int) pixels[pixel] & 0xff) << 24)); // alpha
			        channels.addBlue(row,col,((int) pixels[pixel + 1] & 0xff)); // blue
			        channels.addGreen(row,col,(((int) pixels[pixel + 2] & 0xff) << 8)); // green
			        channels.addRed(row,col,(((int) pixels[pixel + 3] & 0xff) << 16)); // red
			        col++;
			        if (col == width) {
			           col = 0;
			           row++;
			        }
			        if(!continueCounting) // user has cancelled the precounting
			        	return null;
			     }
			  } else {
				  LOGGER.fine("No alpha channel");
			     final int pixelLength = 3;
			     for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
			        channels.addRed(row,col,((int) (pixels[pixel + 2] & 0xFF) ));  // red
			        channels.addGreen(row,col, (( (int) (pixels[pixel + 1]  & 0xFF) )) ); // green
			        channels.addBlue(row,col,((int) (pixels[pixel]  & 0xFF)));  // blue
			        col++;
			        if (col == width) {
			           col = 0;
			           row++;
			        }
			        if(!continueCounting) // user has cancelled the precounting
			        	return null;
			     }
			  }
			  return channels;
		} catch (Exception e) {
			LOGGER.severe("Error in transforming image to matrix");
			e.printStackTrace();
			this.cancelledInside=true;
			return null;
		}
	   }
	
	/**
	 * Counts number of coordinates that are inside the cell.
	 *
	 * @param radius the radius of cell
	 * @param gap_candidate the gap between inspected pixels
	 * @return the amount of found coordinates
	 */
	private int countCoordinatesInCell(double radius, int gap_candidate){
		double area = Math.PI*Math.pow(radius, 2);
		return (int)(area/Math.pow(gap_candidate,2));

	}
	
	
	/**
	 * Creates groups from current coordinates by using distances between them. Basically coordinates close to each other are in same cell.
	 */
	
	private void createPointGroups(){
		   try {
			this.copy_of_current_finalCoordinates=new ArrayList<Point>();
			   this.copy_of_current_finalCoordinates.addAll(this.current_finalCoordinates);
			   this.current_finalCentroidCoordinates=new ArrayList<Point>();
			   this.current_finalCentroidCoordinates.addAll(this.finalCentroidCoordinates); // add all previous final Centroid coordinates
			   Collections.sort(this.copy_of_current_finalCoordinates, new CoordinateComparator());
			   int coordinateAmount = this.copy_of_current_finalCoordinates.size();
			   while(this.continueCounting && copy_of_current_finalCoordinates.size()>0 ){ // Points are removed from list in method getNeighbourPoints
				   int randomIndex=(int)(Math.random()*copy_of_current_finalCoordinates.size());
				   Point firstPoint = copy_of_current_finalCoordinates.get(randomIndex); // get randomly the first point
				   Stack<Point> stack  = new Stack<Point>();
				   stack.push(firstPoint);
				   copy_of_current_finalCoordinates.remove(firstPoint);
				//   LOGGER.fine("all coordinates: "+copy_of_current_finalCoordinates.size());
				   // recursively get points to list
				   ArrayList<Point> groupPoints = getPoints(stack, new ArrayList<Point>());

				   if(groupPoints != null && groupPoints.size() >= this.global_min_coordinate_number_in_cell ){//&& groupPoints.size() < this.current_max_coordinate_number_in_cell){
					   createWeightedPointGroup(groupPoints);
				   }
				   
				   // show progress
				   if(this.copy_of_current_finalCoordinates.size() == 0){
					   this.pctm.updateProgressBar(100);
				   }
				   else{
					   int value = (int) Math.round(((((double)this.copy_of_current_finalCoordinates.size())/((double)coordinateAmount)))*90);
					   this.pctm.updateProgressBar(100-value);
				   }
			   }
		} catch (Exception e) {
			LOGGER.severe("Error in creating point groups in precounting.");
			e.printStackTrace();
		}
	   }

	/**
	 * Creates groups from current coordinates by using distances between them. Basically coordinates close to each other are in same cell.
	 * Uses multithreading by givin task to executor
	 */
	private void createPointGroupsMultiThreaded(){
		try {
			
			this.current_finalCentroidCoordinates=new ArrayList<Point>();
			this.current_finalCentroidCoordinates.addAll(this.finalCentroidCoordinates); // add all previous final Centroid coordinates
			
			ArrayList<CalculateCoordinatesTask> taskList= new ArrayList<CalculateCoordinatesTask>();
			// check size of list
			if(this.current_finalCoordinates != null){
				if(this.current_finalCoordinates.size() <2000){
					// do single thread with whole list
					CalculateCoordinatesTask calculateTask = new CalculateCoordinatesTask(this, this.current_finalCoordinates, 
							this.current_gap, this.current_max_cell_size, this.current_max_coordinate_number_in_cell, this.current_min_cell_size,
							this.maxCellNumberInCellGroup, this.min_distance_between_cells_boundaries, 1);
					taskList.add(calculateTask);
				}
				else{ // several threads because lot of coordinates
					
										
					ArrayList<int[]> indexes = getIndexesForCreatingCoordinates(); // get indexes how to make parts for threads
					int counter=1;
					Iterator<int[]> indexIterator = indexes.iterator();
					while(indexIterator.hasNext()){
						int[] indexStartEnd=indexIterator.next();
						// create list of coordinates
						ArrayList<Point> sublist = new ArrayList<Point>(this.current_finalCoordinates.subList(indexStartEnd[0], indexStartEnd[1]));
						
						// create tasks and add to tasklist
						if(sublist != null && sublist.size() >0 ){
							CalculateCoordinatesTask calculateTask = new CalculateCoordinatesTask(this, sublist, 
									this.current_gap, this.current_max_cell_size, this.current_max_coordinate_number_in_cell, this.current_min_cell_size,
									this.maxCellNumberInCellGroup, this.min_distance_between_cells_boundaries, counter++);
							taskList.add(calculateTask);	
						}
	
					}
	
				}
					// create executor
					executor = Executors.newFixedThreadPool(taskList.size());
					Iterator<CalculateCoordinatesTask> taskIterator = taskList.iterator();
					while(taskIterator.hasNext()){ // add tasks to executor and start executing them
						executor.execute(taskIterator.next());				
					}
					executor.shutdown();
					
					// update progress until all tasks are terminated or user cancels the precounting
					while(continueCounting && !executor.isTerminated()){
						int progress = 0;
						Iterator<CalculateCoordinatesTask> taskCheckIterator = taskList.iterator();
						while(taskCheckIterator.hasNext()){
							CalculateCoordinatesTask singleTask = taskCheckIterator.next();
							if(!singleTask.isContinueCounting() && singleTask.isShouldForceGAPbigger()){ // is aborted by single thread (some error)
							
								this.setContinueCounting(false);
								this.forced_gap=this.current_gap+1;
							}
							progress += (singleTask).getProgressedCoordinates();	// get progress of each task			
						}
						
						updateCalculatingProgress(progress); // send the value
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						}
					}
					
					if(!continueCounting){
						if(!executor.isTerminated()){
							executor.shutdownNow();
							
						}
						
						return;

					}
				
				// combine the results
					
					//if maximum size of cell has been changed -> remove all current_finalCentroidCoordinates
					if(this.current_max_cell_size > this.max_cell_size*this.cellSizeMaxScalingFactor){
						this.current_finalCentroidCoordinates.clear();
					}
					
					// go through finalized tasks
					for(int i=0;i<taskList.size();i++){
						CalculateCoordinatesTask singleTask = taskList.get(i);
						CalculateCoordinatesTask nextTask=null;
						if(i<taskList.size()-1)
							nextTask = taskList.get(i+1);
						ArrayList<CentroidFromNpoints> centroidCoordinates = singleTask.getCentroidCoordinates();
						Iterator<CentroidFromNpoints> centroidIterator = centroidCoordinates.iterator();
						// go through points
						while(centroidIterator.hasNext()){

							// check that next task has no centroids too close or they are not composed from more points
			
							CentroidFromNpoints midPoint = centroidIterator.next();
							
							if(!compareIsTooClose(midPoint, this.current_finalCentroidCoordinates)){ // check is too close to point at final centroids.
								
								// not too close -> check is too close to centroids of next task
								if(i<taskList.size()-1){ // not yet in last position of tasklist -> check towards to next task
								
									if(nextTask != null){
										if(!nextTask.compareIsTooCloseOrMinor(midPoint,this.current_max_cell_size)){ // check is not too close or has bigger number of coordinates
											this.current_finalCentroidCoordinates.add(midPoint);										
										}	
									}	
								}
								else{ // in last position
									// one cell
									 this.current_finalCentroidCoordinates.add(midPoint);
									
								}
							}
						}

					}
					
					/*
					Iterator<CalculateCoordinatesTask> finalizedTaskCheckIterator = taskList.iterator();
					while(finalizedTaskCheckIterator.hasNext()){
					
						CalculateCoordinatesTask singleTask = finalizedTaskCheckIterator.next();
						ArrayList<CentroidFromNpoints> centroidCoordinates = singleTask.getCentroidCoordinates();
						for (int i =0; i<centroidCoordinates.size();i++){
							
							
							
						}
						
						
						Iterator<CentroidFromNpoints> centroidIterator = centroidCoordinates.iterator();
						while(centroidIterator.hasNext()){
							Point midPoint = centroidIterator.next();
							if(!compareIsTooClose(midPoint, this.current_finalCentroidCoordinates)){
								 // one cell
								 this.current_finalCentroidCoordinates.add(midPoint);
							 }
						}
						
						
					}	*/
			}
		} catch (Exception e) {
			LOGGER.severe("Error in precounting cells multithreaded!");
			e.printStackTrace();
		}
	
	}

	/**
	 * Creates the weighted point group. First gets the list of weighted points. 
	 * Then calculates centroids of weighted points to get the middle point -> saved as the new final coordinate.
	 *
	 * @param pointGroups the point groups
	 * @throws Exception the exception
	 */
	private void createWeightedPointGroup(ArrayList<Point> pointGroups) throws Exception{
		   // create initial weighted list
		   ArrayList<WeightPoint> weightPointList = createWeightPointList(pointGroups);
	
		   if(weightPointList.size()>=this.global_min_coordinate_number_in_cell){
				int rounds =0;
				// sort list of weighted points
				Collections.sort(weightPointList, new WeightPointComparator());
				// go trough weight points
				outerLoop:
				while(weightPointList.size()>=global_min_coordinate_number_in_cell && rounds<=10){
					// calculate center of points
					 Point midPoint =calculateCentroid(weightPointList);
					 // count circular data
					 MaxDistancePoint[] maxDistanceValues=getMaxDistanceRoundValues(midPoint, this.current_max_cell_size, weightPointList);
					 // if amount of points is not exceeding maximum amount of point in cell and if user has selected strict precounting then check is cell circular
					 if(weightPointList.size()<= this.current_max_coordinate_number_in_cell && (!SharedVariables.useStrickSearch || SharedVariables.useStrickSearch && isCircular(maxDistanceValues))){
						 if(!compareIsTooClose(midPoint, this.current_finalCentroidCoordinates)){
							 // one cell
							 this.current_finalCentroidCoordinates.add(midPoint);
						 }
						   break outerLoop;
					   }
						else{// possible cluster of cells -> try to separate them
	
							WeightPoint w = getWeightPointWithBiggestDistance(maxDistanceValues);
							if(w==null)
								w = weightPointList.get(weightPointList.size()-1); // get last point of list
	
							WeightPoint b =null;
							if(w != null){
								// find nearest point with biggest weight
								int counter=0;
								do{
									counter++;
									if(b != null)
										w=b;
									b = getWeightPointWithBiggestWeightAtDistance(w, weightPointList, this.current_min_cell_size/2); // finds the biggest weight point at distance
								}while((w.x != b.x || w.y != b.y) && counter<100);
	
								if(w.x == b.x && w.y == b.y){ // w has the biggest weight -> near at center of cell
									int radius=this.current_min_cell_size/2;
									Point p = calculateCentroid(getPointsInside(w.getPoint(), radius, weightPointList));
									if(p == null)
										p=w.getPoint();
	
	
									ArrayList<WeightPoint> selectedPointsForCell=new ArrayList<WeightPoint>();
									ArrayList<WeightPoint> candidatePointList=getPointsInside(p, radius, weightPointList); // get points at distance of radius
									if(candidatePointList != null)
									if(candidatePointList != null && candidatePointList.size()>= 1 &&
											candidatePointList.size() <=this.current_max_coordinate_number_in_cell){
	
										if(candidatePointList.size() >=global_min_coordinate_number_in_cell && isCircular(p, candidatePointList)){ //
											selectedPointsForCell.addAll(candidatePointList);
										}
											double averagePointsPerArea = ((double)candidatePointList.size())/(Math.PI*(double)radius*(double)radius);
											double pointsPerArea=averagePointsPerArea;
											int pointsBefore= candidatePointList.size();
	
											// search the correct cell size from min size to max size
											candidateLoop:
											while(pointsPerArea*1.5 > averagePointsPerArea && radius <=this.current_max_cell_size/2){
												radius+=this.current_gap; // grow radius with gap value
												if(candidatePointList != null && candidatePointList.size()>=this.global_min_coordinate_number_in_cell)
													p=calculateCentroid(candidatePointList);
												candidatePointList=getPointsInside(p, radius, weightPointList);
												if(candidatePointList==null){
													break;
												}
												else{  // calculate how many points are at area grown in last loop.
													pointsPerArea=calculateOuterRadiusArea(candidatePointList.size()-pointsBefore, radius, radius-this.current_gap);
													if(pointsPerArea*5 > averagePointsPerArea){ // not reached to cell boundary yet because lot of cells
														averagePointsPerArea=((double)candidatePointList.size())/(Math.PI*(double)radius*(double)radius);
														pointsBefore=candidatePointList.size();
	
														if(candidatePointList.size() >=global_min_coordinate_number_in_cell &&
															candidatePointList.size() <= this.current_max_coordinate_number_in_cell && isCircular(p, candidatePointList)){ // is points in circle
															selectedPointsForCell.clear();
															selectedPointsForCell.addAll(candidatePointList);
	
														}
													}
													else{
														break candidateLoop;
													}
												}
											}
											// create midpoint from selectedPoints
											if(selectedPointsForCell != null && selectedPointsForCell.size()>0 && 
											selectedPointsForCell.size()>=global_min_coordinate_number_in_cell &&
											selectedPointsForCell.size()<=current_max_coordinate_number_in_cell){
												// calculate the centroid of cells -> the final point to be saved.
												midPoint = calculateCentroid(selectedPointsForCell);
												if(!compareIsTooClose(midPoint,this.current_finalCentroidCoordinates))
													this.current_finalCentroidCoordinates.add(midPoint);
											}
											weightPointList.removeAll(candidatePointList); // remove selected points
											Collections.sort(weightPointList, new WeightPointComparator());
	
									}
								} // not
						}
					}
					rounds++;			
				}
		   }else{
			   LOGGER.fine("WeightPoint list too small");
		   }
	   }

	/**
	 * Creates the weighted points from given list of points. 
	 * The weight of point is calculated by amount of neighbor points and how far they are. 
	 * The weight of point is proportional to distance to other points. Lot of points close -> big weight.
	 * 
	 *
	 * @param pointGroups the point groups
	 * @return the array list
	 */
	private ArrayList<WeightPoint> createWeightPointList(ArrayList<Point> pointGroups){
		   try {
			ArrayList<WeightPoint> weightPointList = new ArrayList<WeightPoint>();
			   if(pointGroups != null && pointGroups.size()>0)
					 for (int j=0;j<pointGroups.size()-1;j++) {
							Point point = pointGroups.get(j);
							WeightPoint wpoint = new WeightPoint(point);
							// set boundaries for binary search
							int lowerBoundValue=Math.max(0,(int)wpoint.getPoint().x-this.current_max_cell_size/2);
							int upperBoundValue=(int)(wpoint.getPoint().x+this.current_max_cell_size/2);
							// find first and last index of points in pointGroups which are close to wpoint in horizontal level
							int[] bounds=startEndBinarySearch(pointGroups, lowerBoundValue, upperBoundValue);
							if(bounds != null && bounds[0] >=0 && bounds[1] <pointGroups.size() && bounds[0] <= bounds[1]){
								for (int i = bounds[0]; i <= bounds[1]; i++) {
									// calculate distance
									double distance = wpoint.getPoint().distance((Point)pointGroups.get(i));
									if( distance < this.current_max_cell_size/2 && distance > 0){
										   wpoint.increaseWeight(1/distance); // increase weight by distance
									}
								}
								weightPointList.add(wpoint);
							}
			   }
			   return weightPointList;
		} catch (Exception e) {
			LOGGER.severe("Error in creating weightPoint list in precounting!");
			e.printStackTrace();
			return null;
		}
	   }

	/**
	 * Finds colors from colorList collections using binary search.
	 *
	 * @param colorInt the color int
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private boolean foundColor(int colorInt) throws Exception{
	
		  if(Collections.binarySearch(this.current_colorList, colorInt) >=0)
			  return true;
		  return false;
	 }
	
	/**
	    * Returns the color vectors in different 8 angles starting from middle of picked cell and ending to 8 directions.
	    *
	    * @return the channel vectors for 8 angles
	    * @throws Exception the exception
	    */
	   private ArrayList<ColorChannelVectors> get8Angles() throws Exception{
		ArrayList<ColorChannelVectors> angleVectors=new ArrayList<ColorChannelVectors>();
		ImageColorChannels channels = convertImageToChannels(this.subImage);
		if(channels != null){
	
	
			for(int i=-1;i<=1;i++){ // left-right direction
				for(int j=-1;j<=1;j++){ // up_down direction
					if(!(i==0 && j==0)){
						angleVectors.add(calculateColorsForVectors(channels, i, j));
					}
	
				if(!continueCounting)
					return null;
				}
			}
	
			return angleVectors;
		}
		continueCounting=false;
		return null;
	}

	/**
	    * Determines the different colors of picked cell. 
	    * Goes through ColorChannelVectors and calculates the maximum sum of slopes value for each channel value at position i in vector.
	    *
	    * @param angleVectors the angle vectors
	    * @throws Exception the exception
	    */
	   private void getDifferentColors(ArrayList<ColorChannelVectors> angleVectors) throws Exception{
		this.current_colorList=new ArrayList<Integer>();
		if(this.colorList.size()>0)
			this.current_colorList.addAll(this.colorList);
		this.current_max_coordinate_number_in_cell=this.max_coordinate_number_in_cell;
		this.current_max_cell_size=this.max_cell_size;
		this.current_min_cell_size=this.min_cell_size;
	
	
		if(angleVectors != null && angleVectors.size()>0){
			int sumOfMaxKindexes = 0; 	// inital value
			int kNumber=0;				// inital value
			int minCellSize =Integer.MAX_VALUE;
			Iterator<ColorChannelVectors> vIterator = angleVectors.iterator();
			while(vIterator.hasNext()){
				double maxK=0;
				int indexOfMaxK=0;
				ColorChannelVectors colorVector=vIterator.next();
				// go through vectors and for each color count k -> sum k of three colors -> save
				int startGap =(int)(colorVector.getSize()/20); 
				if(startGap<3) // if start gap too small set to 3 -> length of slope is 3
					startGap=3;
				int endGap=(int)(colorVector.getSize()/4); 
				if(endGap<5) // if end gap too small set to 5 -> length of slope is 5
					endGap=5;
				// count maximum slope value and index of it
				for(int gap = startGap;gap<endGap;gap++){ // try different length of slopes
					for(int i=gap+1;i<colorVector.getSize()-gap-1;i++){ //
						int start_x=i-gap; // start of slope
						int end_x =i+gap;  // end of slope
						double kValueSum= colorVector.getKvalueAt(start_x, end_x); // get the k value by color change between start and end point
						if(kValueSum > maxK){
							maxK=kValueSum;
							indexOfMaxK=start_x;
						}
					}
				}
				// store int colors from 0 -> indexOfMax from vectors
				if(indexOfMaxK>0){
					sumOfMaxKindexes+=indexOfMaxK; // add for sum
					kNumber++;
					if(indexOfMaxK<minCellSize)
						minCellSize=indexOfMaxK; // stores the smallest cell size from middle to border of cell
					for(int i= 0;i<indexOfMaxK;i++){
						int colorInt = colorVector.getFullColorInt_original(i);
						addColorsIfNotFound(getRelaxedColors(this.pixel_color_relaxation, colorInt)); // collect if not already in list.
	
					}
				}
			}
			if(kNumber <1){
				cancelInside();
				return;
			}
	
			int cell_size=(int)(sumOfMaxKindexes*2/kNumber); // count average cell diameter kNumber
			if(cell_size < this.global_min_cell_diameter){
				LOGGER.warning("Too small cell picked in cell counting");
				cancelInside(); // too small cell -> stop
				return;
			}
				// calculate maximum cell size if bigger than previous cell size
			if(cell_size*this.cellSizeMaxScalingFactor > this.current_max_cell_size)
				this.current_max_cell_size= (int)(cell_size*this.cellSizeMaxScalingFactor);
			
			// calculate minimum cell size if smaller than previous cell size
			if(cell_size*this.cellSizeMinScalingFactor < this.current_min_cell_size)
				this.current_min_cell_size = Math.max((int)(cell_size*this.cellSizeMinScalingFactor), this.global_min_cell_diameter);
	
			// check that difference is big enough
			if(this.current_max_cell_size-this.current_min_cell_size <10){
				this.current_max_cell_size=this.current_min_cell_size+10;
			}
	
			this.current_gap=2;
			//initalize the current_max_coordinate_number_in_cell
			this.current_max_coordinate_number_in_cell=countCoordinatesInCell(((double)this.current_max_cell_size)/2.0, this.current_gap);
			
			int imageSize = this.originalImage.getHeight()*this.originalImage.getWidth();
			
			
			// eveluate gap value -> how many pixels are skipped
			int gap_candidate=this.forced_gap;
			doLoop:
			do{
				if(gap_candidate>=SharedVariables.MAX_GAP){				
					break doLoop;
				}
				
				/*
				 * TESTING GAP 1
				 */
				
			//	if(forced_gap==0 &&  gap_candidate==1) 
			//		break doLoop;
				

				/* set gap to 1 when: 
				 * - current_cell_size is not too big
				 * -imagesize is not too big
				 * - at least 4 threads can be used
				 * -THIS MAY BE QUITE SLOW, but gives better results when very small cells
				 */
				if(forced_gap==0 && 
						imageSize < SharedVariables.IMAGE_SIZE_MEDIUM && 
						this.current_min_cell_size < SharedVariables.GAPPING_CELL_SIZE_SMALL &&  
						SharedVariables.availableProcessors>=8 && 
						gap_candidate==1) 
					break doLoop;
				
				
				
				/* set gap to 2 when: 
				 * -imagesize is smaller than ~ 10MP
				 * - at least 8 threads can be used
				 */
				
				if(forced_gap < 3 && 
						imageSize < SharedVariables.IMAGE_SIZE_BIG && 
						SharedVariables.availableProcessors>=8 && 
						gap_candidate==2) 
					break doLoop;
				
				/* set gap to 2 when: 
				 * - current_cell_size is very small
				 * -imagesize is very big
				 * - at least 2 threads can be used
				 * -THIS MAY BE QUITE SLOW, but gives better results when very small cells
				 */
				
				if(forced_gap < 3 && 
						imageSize < SharedVariables.IMAGE_SIZE_MEDIUM && 
						this.current_min_cell_size < SharedVariables.GAPPING_CELL_SIZE_SMALL &&  
						SharedVariables.availableProcessors>=2 && 
						gap_candidate==2) 
					break doLoop;
				
				/* set gap to 2 when: 
				 * -imagesize is medium
				 * - at least 4 threads can be used
				 */
				if(forced_gap < 3 && 
						imageSize < SharedVariables.IMAGE_SIZE_MEDIUM &&
						SharedVariables.availableProcessors>=4 && 
						gap_candidate==2) 
					break doLoop;
				
				// count new gap by increasing by one
				gap_candidate++;
				
				int minCoordinatesInCell=countCoordinatesInCell(((double)this.current_min_cell_size)/2.0, gap_candidate);
				if(minCoordinatesInCell >= this.global_min_coordinate_number_in_cell){ // have enough coordinates inside cell of minimum size
					this.current_gap=gap_candidate;
					this.current_max_coordinate_number_in_cell=countCoordinatesInCell(((double)this.current_max_cell_size)/2.0, this.current_gap);
	
				}
				else{
					// don't change gap values
					break doLoop;
				}
	
	
			}while(gap_candidate<20);
			
			
	
			// collect colors from whole image by using minCellSize
			collectInnerCellColorsFromSubImage(minCellSize);
			
			
			//remove background colors from list
			removeBackgroundColorsFromCurrentColorList(angleVectors, kNumber);
	
			// sort list
			Collections.sort(this.current_colorList);
		//	LOGGER.info("Maximum cell size: "+this.current_max_cell_size);
		//	LOGGER.info("Minimum cell size: "+this.current_min_cell_size);
		}
	}

	/**
	    * Returns the image as byte array.
	    *
	    * @param imageIn the image in
	    * @return the image as byte array
	    * @throws Exception the exception
	    */
	   private byte[] getImageAsByteArray(BufferedImage imageIn) throws Exception{
		   BufferedImage image = convert(imageIn, BufferedImage.TYPE_3BYTE_BGR); // convert to 3byte bgr image
			return ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	
	   }

	/**
	 * Returns the indexes for creating coordinates. If group too small and maximum cell size can't fit in it -> enlarge groups.
	 *
	 * @return the indexes for creating coordinates
	 */
	private ArrayList<int[]> getIndexesForCreatingCoordinates(){
		try {
			int size=0;
			int divider=2;
			ArrayList<int[]> indexes = new ArrayList<int[]>();
			if(this.current_finalCoordinates != null && this.current_finalCoordinates.size()>0){
				// probably big amount of points
				if(this.current_finalCoordinates.size()>3000){
					divider=SharedVariables.availableProcessors;
					
				}
						

				//check if too small parts -> change divider smaller
				boolean checkParts=true;
				while(divider>1 && checkParts){
					
					if(divider == 1){
						indexes.add(new int[] {0,this.current_finalCoordinates.size()-1}); // whole list
						break;
					}
					
					size =  (int) Math.round((((double)this.current_finalCoordinates.size())/(double)divider));
					checkParts=false;
					indexes = new ArrayList<int[]>(); // empty array
			
					// go through coordinates to get optimum number of parts (threads). If group too small and maximum cell can't fit in it -> enlarge groups
					for(int indexStart=0, indexEnd=size; indexEnd<this.current_finalCoordinates.size() && indexStart < indexEnd;indexStart+=size){
						// is the difference between first and last value bigger than maximum cell size
						if(this.current_finalCoordinates.get(indexEnd).x - this.current_finalCoordinates.get(indexStart).x < this.current_max_cell_size*2){ 
							// too small part -> divider has to be smaller
							divider--;
							if(divider == 1){
								indexes.clear();
								indexes.add(new int[] {0,this.current_finalCoordinates.size()-1}); // whole list
								checkParts=false; // stop checking
							}
							else{
								checkParts=true; // continue checking
							}
						
							break; // break for loop
							
						}
						int overlapStartIndex=0; // set the overlapping startIndex
						if(indexStart>0){
							int startValue = this.current_finalCoordinates.get(indexStart).x-this.max_cell_size;
							int endValue =  this.current_finalCoordinates.get(indexEnd).x;

							int[] overlappedIndexes = startEndBinarySearch(this.current_finalCoordinates, startValue, endValue);
							overlapStartIndex=overlappedIndexes[0]; // set the starting index for overlapping with previous part
							
						}
						
						indexes.add(new int[] {overlapStartIndex,indexEnd}); // add to list indexes of new part of point list.
			
						if(indexEnd + size >= this.current_finalCoordinates.size()){ // set the end index for next for loop
							indexEnd=this.current_finalCoordinates.size()-1;
						}
						else{
							indexEnd +=size;
						}
						
						
						
					}
					
				}
			}
			
			return indexes;
		} catch (Exception e) {
			LOGGER.severe("Error in counting group sizes for threads of CreateCoordinates!");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	    * Returns the max distance round values. The points are separated
	    *
	    * @param mP the point where other points are compared
	    * @param circleSize the circle size
	    * @param pList the list
	    * @return the max distance round values
	    */
	   private MaxDistancePoint[] getMaxDistanceRoundValues(Point mP, int circleSize, ArrayList<WeightPoint> pList){
	
		   try {
			MaxDistancePoint[] maxPoints=new MaxDistancePoint[8];
			   for (Iterator<WeightPoint> iterator = pList.iterator(); iterator.hasNext();) {
					WeightPoint searchPoint = iterator.next();
					int sliceNumber= isInsideWhichSlice(mP, searchPoint, circleSize);
					if(sliceNumber>0 && sliceNumber <=8){
						double distance = mP.distance(searchPoint);
						if(maxPoints[sliceNumber-1] == null || maxPoints[sliceNumber-1].getDistance() < distance){
							maxPoints[sliceNumber-1] = new MaxDistancePoint(searchPoint, distance);
						}
					}
			   }
			   return maxPoints;
		} catch (Exception e) {
			LOGGER.severe("Error in getting maximum distance values!");
			e.printStackTrace();
			return null;
		}
	   }


   /**
    * Returns the neighbor points that are close enough to given point.
    *
    * @param p the Point which neighbors are calculated
    * @return the neighbor points
    * @throws Exception the exception
    */
   private ArrayList<Point> getNeighbourPoints(Point p) throws Exception{
	   ArrayList<Point> neighbours=new ArrayList<Point>();
	   // calculate the max distance 
	   int maxDistance=(int)(this.current_max_cell_size/this.min_distance_between_cells_boundaries);
	   int lowerBoundValue=Math.max(p.x-maxDistance,0); // minimum bounds for binary search
	   int upperBoundValue=p.x+maxDistance;				// maximum bounds for binary search
	   int[] startEndIndexes=startEndBinarySearch(this.copy_of_current_finalCoordinates, lowerBoundValue, upperBoundValue);
	   if(startEndIndexes != null && startEndIndexes[0] >= 0
			   && startEndIndexes[1] <= this.copy_of_current_finalCoordinates.size()-1 && startEndIndexes[0] < startEndIndexes[1])

	   for (int i = startEndIndexes[0]; i <= startEndIndexes[1]; i++) {
		   Point point = this.copy_of_current_finalCoordinates.get(i);
		   if(point != null)
			if(point.distance(p) <maxDistance){
				neighbours.add(point);	
			}
	   }

		this.copy_of_current_finalCoordinates.removeAll(neighbours);
		return neighbours;
   }

   /**
    * Returns the original image as byte array.
    *
    * @param imageIn the image in
    * @throws Exception the exception
    */
   private void getOriginalImageAsByteArray(BufferedImage imageIn) throws Exception{
		this.originalImagePixels = getImageAsByteArray(imageIn);

   }

   /**
    *
    * Recursively collects all neighbor Points for Points at stack
 * @param stack Stack containing Points
 * @param groupPoints ArrayList of Points which are
 * @return ArrayList of Points that has been collected in recursion
 */
private ArrayList<Point> getPoints(Stack<Point> stack, ArrayList<Point> groupPoints){

	   try {
		if(stack.isEmpty())
			   return groupPoints;
		//   LOGGER.fine("stack: "+stack.size());
		   Point firstPoint = stack.pop();
		   groupPoints.add(firstPoint);

		   stack.addAll(getNeighbourPoints(firstPoint)); // adds Points to stack and removes from copy_of_current_finalCoordinates
		   if(stack.size()> this.current_max_coordinate_number_in_cell*this.maxCellNumberInCellGroup) // if too much of coordinates in stack
			   return null;
		   return getPoints(stack, groupPoints);
	} catch (Exception e) {
		
		e.printStackTrace();
		return null;
	}
   }

   /**
    * Returns the points which distance to given mid point is smaller than given maximum distance.
    *
    * @param mPoint the mid point
    * @param distance the distance
    * @param weightPointList the list of Weightpoints
    * @return the points inside
    * @throws Exception the exception
    */
   public ArrayList<WeightPoint> getPointsInside(Point mPoint, int distance, ArrayList<WeightPoint> weightPointList) throws Exception{
	Point2D midPoint = new Point2D(mPoint.x,mPoint.y);
	  ArrayList<WeightPoint> pointsInside=new ArrayList<WeightPoint>();
	  	//binarysearch
		int lowerBoundValue=Math.max(0,midPoint.getAsInt().x-distance);
		int upperBoundValue=midPoint.getAsInt().x+distance;
		int[] bounds=startEndBinarySearchWithWeightPoints(weightPointList, lowerBoundValue, upperBoundValue);
		if(bounds != null && bounds[0] >=0 && bounds[1] <weightPointList.size() && bounds[0] <= bounds[1]){
			for (int i = bounds[0]; i <= bounds[1]; i++) {
				WeightPoint wPoint=weightPointList.get(i);
				if(midPoint.distance(wPoint.getPoint2D()) <= distance*1.01){	// 1% bigger distance to be sure of collecting the midPoint to the result
					pointsInside.add(wPoint);
				}
			}
		}
	   return pointsInside;
}



   /**
    * Returns the reduced noise color. R
    * educes to every tenth value for example 30 , 50 ,100, 110, etc.
    * Checks that each color channel value is between 0-250.
    *
    * @param colorInt the color as Integer
    * @return the reduced noise color as Integer
    * @throws Exception the exception
    */
   private Integer getReducedNoiseColor(int colorInt) throws Exception{
	int red   = (colorInt >> 16) & 0xff;
	int green = (colorInt >>  8) & 0xff;
	int blue  = (colorInt      ) & 0xff;
	red = checkOverBounds(red - ( red % 10)); // reduces to every tenth value for example 30 , 50 ,100, 110, etc.
	green = checkOverBounds(green - ( green % 10 )); // reduces to every tenth value for example 30 , 50 ,100, 110, etc.
	blue = checkOverBounds(blue - ( blue % 10 ) ); // reduces to every tenth value for example 30 , 50 ,100, 110, etc.

	return (255 << 24) | (red << 16) | ( green << 8) | blue;
}

   /**
    * Returns the reduced noise color with relaxation. 
    * Gets smaller or bigger Integer value of channel color.
    * Checks that channel values are between 0-255.
    *
    * @param colorInt the color int
    * @param multiply the multiply
    * @return the reduced noise color with relaxation
    * @throws Exception the exception
    */
   private Integer getReducedNoiseColorWithRelaxation(int colorInt, int multiply) throws Exception{
	   colorInt-= colorInt % 10;
	   colorInt += multiply*10;
	   return checkOverBounds(colorInt);
}

   /**
    * Returns the relaxed colors. Changes the given color Integer to different channels of colors (red, green, blue).
    * Relaxes each channel by getting a wider range of integer values for each channel.
    *
    * @param relaxation the relaxation
    * @param colorInt the color int
    * @return the relaxed colors
    * @throws Exception the exception
    */
   private ArrayList<Integer> getRelaxedColors(int relaxation, int colorInt) throws Exception{
	ArrayList<Integer> integerList= new ArrayList<Integer>();
	int red_relaxed;
	int green_relaxed;
	int blue_relaxed;
	// get channels
	int red   = (colorInt >> 16) & 0xff;
	int green = (colorInt >>  8) & 0xff;
	int blue  = (colorInt      ) & 0xff;

	// relax the channels and combine to Color Integer. The Relaxation does same relaxation to all channels. For example +1 for red, green and blue.
	for(int i=-relaxation;i<= relaxation;i++){
		red_relaxed=getReducedNoiseColorWithRelaxation(red, i);
		green_relaxed=getReducedNoiseColorWithRelaxation(green, i);
		blue_relaxed=getReducedNoiseColorWithRelaxation(blue, i);
		int argb =  (255 << 24) | (red_relaxed << 16) | ( green_relaxed << 8) | blue_relaxed;
		integerList.add(argb);
	}
	return integerList;
}


   /**
    * Returns the relaxed colors with a very wide range of color. Changes the given color Integer to different channels of colors (red, green, blue).
    * Relaxes each channel by getting a wider range of integer values for each channel and creates a all combinations of them.
    *
    * @param relaxation the relaxation
    * @param colorInt the color int
    * @return the relaxed colors
    * @throws Exception the exception
    */
   @SuppressWarnings("unused")
private ArrayList<Integer> getRelaxedColorsWideRange(int relaxation, int colorInt) throws Exception{
	ArrayList<Integer> integerList= new ArrayList<Integer>();
	int red_relaxed;
	int green_relaxed;
	int blue_relaxed;

	int red   = (colorInt >> 16) & 0xff;
	int green = (colorInt >>  8) & 0xff;
	int blue  = (colorInt      ) & 0xff;

	for(int i=-relaxation;i<= relaxation;i++){
		red_relaxed=getReducedNoiseColorWithRelaxation(red, i);
		for(int j=-relaxation;j<= relaxation;j++){
			green_relaxed=getReducedNoiseColorWithRelaxation(green, j);
			for(int k=-relaxation;k<= relaxation;k++){
				blue_relaxed=getReducedNoiseColorWithRelaxation(blue, k);
				int argb =  (255 << 24) | (red_relaxed << 16) | ( green_relaxed << 8) | blue_relaxed;
				integerList.add(argb);
			}
		}
	}
	return integerList;
}

   /**
    * Returns the sub image as byte array.
    *
    * @param imageIn the image of picked cell
    * @throws Exception the exception
    */
   private void getSubImageAsByteArray(BufferedImage imageIn) throws Exception{
		this.subImagePixels = getImageAsByteArray(imageIn);

  }

   /**
 * Returns the thread status.
 *
 * @return the thread status
 * @throws Exception the exception
 */
public String getThreadStatus() throws Exception{
	return this.counterThread.getState().toString();
}


   /**
 * Returns the weight point with biggest distance.
 *
 * @param maxDistanceList the max distance list
 * @return the weight point with biggest distance
 */
private WeightPoint getWeightPointWithBiggestDistance(MaxDistancePoint[] maxDistanceList){
	try {
	
		   MaxDistancePoint maxDistancePoint=null;
		   for (int i = 0; i < maxDistanceList.length; i++) {
			if(maxDistanceList[i] != null && (maxDistancePoint == null ||
					(maxDistancePoint != null && maxDistanceList[i].getDistance() > maxDistancePoint.getDistance()))){
				maxDistancePoint=maxDistanceList[i];
			}
		}
		   return maxDistancePoint;
	} catch (Exception e) {
		LOGGER.severe("Error in getting WeightPoint of biggest distance!");
		e.printStackTrace();
		return null;
	}
   }
   
   /**
    * Returns the weight point with biggest weight at distance.
    *
    * @param midPoint the mid point
    * @param weightPointList the weight point list
    * @param distance the distance
    * @return the weight point with biggest weight at distance
    * @throws Exception the exception
    */
   private WeightPoint getWeightPointWithBiggestWeightAtDistance(WeightPoint midPoint, ArrayList<WeightPoint> weightPointList, int distance) throws Exception{
	   WeightPoint maxWeightPoint=null;
	   for (Iterator<WeightPoint> iterator = weightPointList.iterator(); iterator.hasNext();) {
		WeightPoint weightPoint = (WeightPoint) iterator.next();
		if(weightPoint != null && (maxWeightPoint == null || maxWeightPoint != null && weightPoint.getWeight() >= maxWeightPoint.getWeight())){
			if(midPoint.distance(weightPoint) <= distance)
				maxWeightPoint = weightPoint;
		}
	}
	   if(maxWeightPoint == null)
		   return getWeightPointWithBiggestWeightAtDistance(midPoint, weightPointList, (int)(distance*2));
	   return maxWeightPoint;
   }
    

   /**
    * Checks is color at image position found from colorList. The color value is reduced
    *
    * @param r the row
    * @param c the column
    * @param w the width of image
    * @return true, if found the color
    * @throws Exception the exception
    */
   private boolean hasCellColor(int r, int c, int w) throws Exception{

	   int index = (c + r*w)*3;

	   int argb =  ( 255 << 24) | ((int)(this.originalImagePixels[index+2] & 0xFF) << 16) | ((int)(this.originalImagePixels[index + 1] & 0xFF) << 8) | ((int)this.originalImagePixels[index] & 0xFF);
	   int reducedNoiseArgb = getReducedNoiseColor(argb);
	   if(foundColor(reducedNoiseArgb))
		   return true; // found color
	   else
		   return false; // color not found
   }


	/**
	    * Initializes the Thread.
	    *
	    * @throws Exception the exception
	    */
	   public void initThread() throws Exception{
		this.setContinueCounting(true);
		this.cancelledInside=false;
		this.counterThread=new Thread(this, "counter");
	}

	/**
	    * Calculates is given searchPoint inside a triangle composed from given points.
	    *
	    * @param midPoint the one corner for triangle
	    * @param searchPoint the point to be searched
	    * @param second the second triangle point
	    * @param third the third triangle point
	    * @return true, if found point inside triangle
	    * @throws Exception the exception
	    */
	   private boolean inside(Point midPoint, Point searchPoint, Point second, Point third) throws Exception{
		   int[] xList= new int[] {midPoint.x, second.x, third.x};
		   int[] yList= new int[] {midPoint.y, second.y, third.y};
	
		   Polygon slicePolygon=new Polygon(xList,yList,3);
	
		   return slicePolygon.contains(searchPoint);
	   }

   /**
    * Checks if is cancelled by program. In other words,has this Thread stopped the counting.
    *
    * @return true, if is cancelled inside
    * @throws Exception the exception
    */
   public boolean isCancelledInside() throws Exception {
		return cancelledInside;
	}

   /**
    * Checks does the group of point form a circular shape.
    *
    * @param maxPoints the points
    * @return true, if is circular
    * @throws Exception the exception
    */
   private boolean isCircular(MaxDistancePoint[] maxPoints) throws Exception{
	   double maxDistance = Double.MIN_VALUE; // initial values
	   double minDistance = Double.MAX_VALUE; // initial values

	   for (int i = 0; i < maxPoints.length; i++) {
		MaxDistancePoint mdp= maxPoints[i];
		if(mdp == null)
			return false;
		if(mdp.getDistance()> maxDistance){
			maxDistance=mdp.getDistance();
		}
		if(mdp.getDistance() < minDistance){
			minDistance=mdp.getDistance();
		}
	}

	   if(maxDistance > minDistance*SharedVariables.GLOBAL_CIRCULARITY) // if difference bigger than xx % -> not circular
		   return false;
	   return true;
   }


   /**
    * Checks if is the shape composed from WeightPoints circular.
    *
    * @param midPoint the mid point
    * @param weightPointList the weight point list
    * @return true, if is circular
    */
   private boolean isCircular(Point midPoint, ArrayList<WeightPoint> weightPointList) throws Exception{
	   MaxDistancePoint[] maxDistanceValues=getMaxDistanceRoundValues(midPoint, this.current_max_cell_size,weightPointList);
	   return isCircular(maxDistanceValues);
   }


   /**
    * Checks if is counting.
    *
    * @return true, if is counting
    * @throws Exception the exception
    */
   public boolean isCounting()  throws Exception{
	return continueCounting;
}

   /**
    * Divides the circle given by midPoint and circle size to 8 slices and calculates in which slice the searched point is located.
    *
    * @param midPoint the mid point
    * @param searchPoint the search point
    * @param circleSize the circle size
    * @return the int 
    */
   private int isInsideWhichSlice(Point midPoint, Point searchPoint, int circleSize){
	   try {
		int circlePointX=Integer.MAX_VALUE;
		   int circlePointY= Integer.MAX_VALUE;
		   int vPointX=midPoint.x;
		   int vPointY=Integer.MAX_VALUE;
		   int hPointX=Integer.MAX_VALUE;
		   int hPointY=midPoint.y;
		   int quarter=0;

		   // go through locations separated to four slices
		   if(searchPoint.x >= midPoint.x){ // on the right from midpoint
			   circlePointX= (int) (midPoint.x + Math.sin(45)*circleSize);
			   hPointX=midPoint.x +circleSize; // calculate third point for triangle
			   quarter=1;
		   }
		   else{ // on the left from midpoint
			   circlePointX= (int) (midPoint.x - Math.sin(45)*circleSize);
			   hPointX=midPoint.x - circleSize; // calculate third point for triangle
			   quarter=3;
		   }

		   if(searchPoint.y >= midPoint.y){ // over midpoint y
			   circlePointY= (int) (midPoint.y + Math.sin(45)*circleSize);
			   vPointY=midPoint.y + circleSize; // calculate third point for triangle
			   if(quarter!=1)
				   quarter=4;
		   }
		   else{ // below midpoint y
			   circlePointY= (int) (midPoint.y - Math.sin(45)*circleSize);
			   vPointY=midPoint.y - circleSize; // calculate third point for triangle
			   if(quarter ==1)
				   quarter=2;

		   }

		   // go through the locations separated to 8 slices
		   Point circlePoint=new Point(circlePointX,circlePointY);

		   switch (quarter) {
		case 1:
			// first quarter -> check when split the quarter to two triangles:
			Point thirdPoint =new Point(vPointX,vPointY); // the middle point in quarter slice
			// check is inside the first triangle
			if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
				return 1;
			}
			else{ // check is inside the second triangle
				thirdPoint= new Point(hPointX,hPointY);
				if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
					return 2;
				}
				else{
					return -1; // not found
				}
			}

		case 2:
			// second quarter -> check when split the quarter to two triangles:
			thirdPoint =new Point(hPointX,hPointY); // the middle point in quarter slice
			// check is inside the first triangle
			if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
				return 3;
			}
			else{ // check is inside the second triangle
				thirdPoint= new Point(vPointX,vPointY);
				if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
					return 4;
				}
				else{
					return -1; // not found
				}
			}

		case 3:
			// third quarter -> check when split the quarter to two triangles:
			thirdPoint =new Point(vPointX,vPointY); // the middle point in quarter slice
			// check is inside the first triangle
			if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
				return 5;
			}
			else{ // check is inside the second triangle
				thirdPoint= new Point(hPointX,hPointY);
				if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
					return 6;
				}
				else{
					return -1; // not found
				}
			}
		case 4:
			// fourth quarter -> check when split the quarter to two triangles:
			thirdPoint =new Point(hPointX,hPointY); // the middle point in quarter slice
			// check is inside the first triangle
			if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
				return 7;
			}
			else{ // check is inside the second triangle
				thirdPoint= new Point(vPointX,vPointY);
				if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
					return 8;
				}
				else{
					return -1; // not found
				}
			}
		}

		 return -1; // not found
	} catch (Exception e) {
		LOGGER.severe("Error in determining slices of given cell!");
		e.printStackTrace();
		return -1;
	}


   }

   private void removeBackgroundColorsFromCurrentColorList(ArrayList<ColorChannelVectors> angleVectors, int indexOfCellBoundary){
	   try {
		ArrayList<Integer> foundBGcolors=new ArrayList<Integer>();
		ArrayList<Integer> singleAngelColors = new ArrayList<Integer>();
		ArrayList<Integer> removeFoundBGcolors=new ArrayList<Integer>();
		   
		   if(this.current_colorList != null && this.current_colorList.size() > 0 && angleVectors != null && indexOfCellBoundary > 3 && angleVectors.size() > indexOfCellBoundary){
			   // Go end indexes
			   Iterator<ColorChannelVectors> cvIterator = angleVectors.iterator();
			   while(cvIterator.hasNext()){
				   ColorChannelVectors colorVector = cvIterator.next();
				   singleAngelColors = new ArrayList<Integer>(); // create empty list for single angle
				   if(colorVector != null){
					   // go through colors of vector
					   int startIndex = (indexOfCellBoundary+colorVector.getSize())/2;
					   for(int i=startIndex;i<colorVector.getSize();i++){
						   int colorInt = colorVector.getFullColorInt_original(i); // get color Integer from vector
						   ArrayList<Integer> relaxedColors =  getRelaxedColors(this.pixel_color_relaxation, colorInt); // get relaxed colors
						   if(relaxedColors != null && relaxedColors.size()>0){
							   Iterator<Integer> rIterator =relaxedColors.iterator();
							   while(rIterator.hasNext()){
								   int rColor= rIterator.next();
								   if(!singleAngelColors.contains(rColor)){ // not contain yet that relaxed color
									   singleAngelColors.add(rColor); // add to single list
									   
								   }
							   }
							   
						   }
						  				   					   
					   }
				   foundBGcolors.addAll(singleAngelColors); // add all found colors to found list
			   }
			   }
			   
			   Collections.sort(foundBGcolors); // sort list
			   
			   // go through and collect only colors with 6 or more occurrences
			   Iterator<Integer> cIterator = foundBGcolors.iterator();
			   int presentColor=Integer.MIN_VALUE;
			   int occurrences=0;
			   while(cIterator.hasNext()){
				   int colorInt=cIterator.next();
				   if(colorInt == presentColor){ // match
					   occurrences++;
				   }
				   else{ // moved to new color in list
					   if(occurrences>5) // at least 6 vectors had previous color
						   removeFoundBGcolors.add(presentColor);
					   presentColor=colorInt; 
					   occurrences=1;
				   }
				   
			   }
			   
			   // remove colors if found from background
			   Iterator<Integer> currentIterator= this.current_colorList.iterator();
			   while(currentIterator.hasNext()){
				   int currColor=currentIterator.next();
				   if(removeFoundBGcolors.contains(currColor)){ // is found
					   currentIterator.remove(); // remove
				   }
			   }
		   
		   }
		   
	} catch (Exception e) {
		LOGGER.severe("Error in removing background colors!");
		e.printStackTrace();
	}
	   
	   
   }

   /* 
    * Starts the calculation of precounting. Phases:
    * 1. Collect colors of picked example cell
    * 2. Collect coordinates of pixels from original image which color matches phase 1 colors
    * 3. Group coordinates and calculate centroids which will be the coordinates for markings.
    * @see java.lang.Runnable#run()
    */
   @Override
	public void run() {
		try {
			long start= System.currentTimeMillis();
			// set boolean values for correct running
		
			continueCounting=true;
			
			// get 8 angle data from midpoint to edge of subImage
			
			if(continueCounting){
				ArrayList<ColorChannelVectors> angleVectors = get8Angles();
				if(angleVectors != null && angleVectors.size()>0){
					// get the colors
					LOGGER.fine("start calculating differentColors: "+angleVectors.size()+ "Colors: "+this.colorList.size());
					getDifferentColors(angleVectors);
	
					if(!continueCounting){
						abortExecution("No Colors", "Couldn't determine colors of picked cell. try another cell.");
						return;
					}
	
					if(this.current_colorList != null && this.current_colorList.size()>0){
								
						LOGGER.fine("start calculating coordinates: colorlist: "+current_colorList.size()+ " using gap: " +this.current_gap+ " min:"+this.global_min_coordinate_number_in_cell+ " max: "+this.current_max_coordinate_number_in_cell);
						LOGGER.fine(" min cell size: "+this.current_min_cell_size+ " max cell size: "+this.current_max_cell_size);
						// go through the image pixels of original image and give coordinates multithreaded or not
						if(SharedVariables.useMultiThreadingInCalculatingCoordinates)
							calculateCoordinatesMultiThreaded();
						else	
							calculateCoordinates();
		
						if(!continueCounting){
							abortExecution("No Coordinates", "Couldn't calculate pixel coordinates for cells. Check Log from Help-menu!.");
							return;
						}
						if(this.current_finalCoordinates != null && this.current_finalCoordinates.size()>=this.global_min_coordinate_number_in_cell){ // number of Points should be more than 10
							LOGGER.fine("start clustering: "+current_finalCoordinates.size());
							
							// Is multithreading on
							if(SharedVariables.useMultThreadingIncreatingPointGroups)
								createPointGroupsMultiThreaded();						
							else
								createPointGroups();
		
							LOGGER.fine("end of grouping coordinates");
							if(!continueCounting){
								
								if(this.forced_gap >0){ // single thread stopped and forced gap is bigger than zero
									cancelInside();
									
									abortExecution("Problems in counting cells", "Trying with different settings. Pick a new Cell!");	
									clean();
								}
								else{ //
									abortExecution("No Cells", "No any cells found.");									
								}
								return;
							}
							if(this.current_finalCentroidCoordinates != null && this.current_finalCentroidCoordinates.size()>0){
								this.finalCentroidCoordinates=this.current_finalCentroidCoordinates;
	
								this.max_cell_size=this.current_max_cell_size;
								this.min_cell_size=this.current_min_cell_size;
								this.max_coordinate_number_in_cell=this.current_max_coordinate_number_in_cell;
								this.taskManger.setSelectedMarkingLayerCoordinates(this.finalCentroidCoordinates);
	
								this.taskManger.updateSelectedMarkingPanelAndImageLayerInfos();
								// saved found colors
								this.colorList.clear();
								this.colorList.addAll(this.current_colorList);
						
								forced_gap=0; // not use forced gap next time, because wass successfull counting
								cancelInside();
								clean();
							}
							else{
								
								
								cancelInside();
								// inform user that couldn't locate cells

								abortExecution("No Cells", "No any cells found. Possibly cells are too small to count!");
								return;
								
							}
						}
						else{
							// inform user that couldn't get colors
							abortExecution("No Cells", "No any cells found. Possibly too small picked cell.");
							return;
						}
							
						
					}	
					else{
						// inform user that couldn't get colors
						abortExecution("No Colors", "Couldn't determine colors of picked cell. Possibly too small cell!");
						return;
	
					}
				}
				else{
					// inform user that couldn't get colors
					abortExecution("No Colors", "Couldn't determine colors of picked cell. Possibly too small cell!");
	
					return;
				}
	
			}
			else{
				abortExecution("Counting not started", "Counting didn't start. Try again.");
	
				return;
			}
			long end = System.currentTimeMillis();
			long elapsedTime = end-start;
			TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.MILLISECONDS);
			LOGGER.fine("ended counter thread in time: " +(end-start)+" ms");
			
		} catch (Exception e) {
			abortExecution("Error occurred", "Error in precounting cells."+e.getMessage());
			cancelInside();
			e.printStackTrace();
		}
	
	}


   
   /**
    * Sets the cancelled inside.
    *
    * @param cancelledInside the new cancelled inside
    * @throws Exception the exception
    */
   public void setCancelledInside(boolean cancelledInside) throws Exception{
	this.cancelledInside = cancelledInside;
}

 
   /**
    * Sets the continue counting.
    *
    * @param continueCounting the new continue counting
    * @throws Exception the exception
    */
   public void setContinueCounting(boolean continueCounting) throws Exception{
	this.continueCounting = continueCounting;
}


   /**
    * Sets the PreCountThreadManager.
    *
    * @param pctm the new manager
    * @throws Exception the exception
    */
   public void setManager(PreCountThreadManager pctm) throws Exception{
	this.pctm=pctm;
}



   /**
 * Sets the progress dialog.
 *
 * @param pbd the new ProgressBallsDialog
 * @throws Exception the exception
 */
public void setProgressBallDialog(ProgressBallsDialog pbd) throws Exception{
}



	/**
	 * Sets the sub image.
	 *
	 * @param subImage the new sub image
	 * @throws Exception the exception
	 */
	public void setSubImage(BufferedImage subImage) throws Exception{
		this.subImage=subImage;
	}

	private void setUpMultiThreading() throws Exception{
		int processors = Runtime.getRuntime().availableProcessors();
		SharedVariables.setAvailableProcessors(processors);
		LOGGER.info("available processors: " +processors);
		
	}

	
	/**
	 * Starts the Thread for counting.
	 *
	 * @throws Exception the exception
	 */
	public void startCounting() throws Exception{
		this.counterThread.start();
	}

	/**
	 * Uses binary search to find starting and ending indexes of pointList which points are inside given lowerBoundValue and upperBoundValue.
	 * Uses only horizontal values in search.
	 *
	 * @param pointList the point list
	 * @param lowerBoundValue the lower bound value
	 * @param upperBoundValue the upper bound value
	 * @return the int[] list of found
	 */
	private int[] startEndBinarySearch(ArrayList<Point> pointList, int lowerBoundValue, int upperBoundValue){
			 try {
				int imin=0;
				 int imax=pointList.size()-1;
				 int imid=(int)(imax-imin)/2;
				 int[] indexes=new int[] {-1,-1};

				 lowerLoop:
				 while(imax>=imin){
					 imid=(int)imin+(imax-imin)/2; // get midpoint
					 if(pointList.get(imid).x == lowerBoundValue){ // found lower bound, but has the one index smaller value smaller or same
						 if(imid == 0 || pointList.get(imid-1).x < lowerBoundValue){
							 // found the lowerBound
							 indexes[0]=imid;
							 break lowerLoop;
						 }
						 else{ // there are same values in smaller indexes of list
							 imax=imid-1;
						 }
					 }
					 else{
						 if(pointList.get(imid).x < lowerBoundValue){
						 	if(imid < pointList.size()-1){
						 		if(pointList.get(imid+1).x >= lowerBoundValue){
						 			// found the lowerbound
						 			indexes[0]=imid+1;
						 			break lowerLoop;
						 		}
						 		else{
						 			imin=imid+1;
						 		}
						 	}
						 	else{
						 		return null; // list not containing given values
						 	}
					 }
					 else{

						 if(imid > 0){
							 if(pointList.get(imid-1).x < lowerBoundValue){
								 indexes[0]=imid;
						 			break lowerLoop;
							 }
							 else{
								 imax=imid-1;
							 }
						 }
						 else{
							 // in first index return it->
							 indexes[0]=imid;
							 break lowerLoop;
						 }
					 }}
				 } // while loop for finding lowerBound

				 if(indexes[0] <0 || indexes[0] > pointList.size()-1)
					 return null;

				 // upperBound search
				 imin=indexes[0]+1; // one bigger than found index for lowerbound
				 imax=pointList.size()-1; // end of list
				 upperLoop:
				 while(imax>=imin){
					 imid=(int)imin+(imax-imin)/2; // get midpoint
					 
					 if(imid< pointList.size()-1){ // is imid smaller or than list size
						 if(pointList.get(imid).x == upperBoundValue){

							 if(imid == pointList.size()-1 || pointList.get(imid+1).x > upperBoundValue){ // is in end or no same value in next index
								 indexes[1]=imid;
								 break upperLoop;
							 }
							 else{
								 imin=imid+1;
							 }
						 }
						 else { 
							 if(pointList.get(imid).x < upperBoundValue){ // value smaller than upperboundvalue
							
								 if(pointList.get(imid+1).x > upperBoundValue){
									 indexes[1]=imid;
									 break upperLoop;
								 }
								 else{
									 imin=imid+1;
								 }
							 
							 
								 if(imid > pointList.size()-1){ // index overflow, should never happen!
									 if(indexes[0] < pointList.size()-1){ // lower index is not in last index
										 // in last index
										 indexes[1]=pointList.size()-1; // set the last index
										 break upperLoop; 
									 }
									 else
										 return null; // not found because index not bigger than lowerBound index
								 } // in last index
								 else{
									 indexes[1]=imid;
									 break upperLoop;
								 }
								 
	
							 }else{
								 if(imid > indexes[0]){ // index bigger than lower bound
									if(pointList.get(imid-1).x <= upperBoundValue){
										indexes[1]=imid-1;
										break upperLoop;
									}
									else{
										imax=imid-1;
									}
								 }
								 else{
									 return null; // not found because index not bigger than lowerBound index
								 }
							 }
	
						 }
					 }
					 else{ // index went too big -> should never happen
						 indexes[1]=pointList.size()-1;
						 break upperLoop;
					 }
				 }
				 

				 return indexes;
			} catch (Exception e) {
				LOGGER.severe("Error in binary search for searching positions of pixels!");
				e.printStackTrace();
				return null;
			}
		}

	/**
	 * Uses binary search to find starting and ending indexes of pointList which points are inside given lowerBoundValue and upperBoundValue.
	 * Uses only horizontal values in search.
	 *
	 * @param pointList the point list
	 * @param lowerBoundValue the lower bound value
	 * @param upperBoundValue the upper bound value
	 * @return the int[]
	 */
	private int[] startEndBinarySearchWithWeightPoints(ArrayList<WeightPoint> pointList, int lowerBoundValue, int upperBoundValue){
		try {
			ArrayList<Point> basicPointList= new ArrayList<Point>();
			if(pointList != null){
			   for (Iterator<WeightPoint> iterator = pointList.iterator(); iterator.hasNext();) {
				WeightPoint weightPoint = (WeightPoint) iterator.next();
				basicPointList.add((Point)weightPoint);

			   }
			   return startEndBinarySearch(basicPointList, lowerBoundValue, upperBoundValue);
			}

			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in starting to find weightpoints by binary search!");
			e.printStackTrace();
			return null;
		}
   }

	public void updateCalculatingProgress(int progressValue){
		if(this.current_finalCoordinates != null && this.current_finalCoordinates.size() > 0){
			int value = 10 + (int) Math.round((((double)progressValue)/((double)this.current_finalCoordinates.size()))*90);
			   this.pctm.updateProgressBar(value);
				
		}
		
	}
	
	public void updateCreatingProgress(int progressValue){
		this.pctm.updateProgressBar(progressValue);
	}
	
	/**
	 * The Class MovingAverage. Contains double values in linked list and methods for counting average of values.
	 */
	private class MovingAverage {
		private final int period;
		private double sum;
		private final Queue<Double> window = new LinkedList<Double>();

    /**
     * Instantiates a new moving average.
     *
     * @param period the period
     * @throws Exception the exception
     */
    public MovingAverage(int period)  throws Exception{
        assert period > 0 : "Period must be a positive integer";
        this.period = period;
    }

    /**
     * Returns the average of values window list.
     *
     * @return the average of values.
     * @throws Exception the exception
     */
    public double getAvg()  throws Exception{
        if (window.isEmpty()) return 0; // technically the average is undefined
        return sum / window.size();
    }

    /**
     * Adds new value to window list and updates the sum.
     *
     * @param num the value
     * @throws Exception the exception
     */
    public void newNum(double num)  throws Exception {
        sum += num;
        window.add(num);
        if (window.size() > period) {
            sum -= window.remove();
        }
    }


}
}

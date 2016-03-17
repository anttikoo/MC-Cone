package operators;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * The Class CreateCoordinatesTask.
 */
public class CreateCoordinatesTask implements Runnable{
	
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The current_coordinates. */
	private ArrayList<Point> current_coordinates=null;
	
	/** The image width. */
	private int imageWidth =0;
	
	/** The image height. */
	private int imageHeight =0;
	
	/** The image bytes. */
	private byte[] imageBytes=null;
	
	/** The rows before. */
	private int rowsBefore=0;
	
	/** The continue counting. */
	private boolean continueCounting=false;
	
	/** The current_gap. */
	private int current_gap=2;
	
	/** The current_color list. Used for finding cells */
	private ArrayList<Integer> current_colorList;


	
	/**
	 * Instantiates a new creates the coordinates task. Reads given byte of pixels, compares colors to given colorlist and if found saves as Point.
	 *
	 * @param h the height of image composed from bytes
	 * @param w the width of image
	 * @param imageBytes the image bytes
	 * @param rowsBefore the rows before this part of image
	 * @param currentGap the current gap
	 * @param colorList the color list
	 */
	public CreateCoordinatesTask(int h, int w, byte[] imageBytes, int rowsBefore, int currentGap, ArrayList<Integer> colorList) {
		this.imageWidth=w;
		this.imageHeight=h;
		this.imageBytes=imageBytes;
		this.rowsBefore=rowsBefore;
		this.current_gap=currentGap;
		this.current_colorList=colorList;
	}
	
	@Override
	public void run() {
		this.continueCounting=true;
		calculateCoordinates();
		
	}
	
	
	/**
	 * Checks if is continue counting.
	 *
	 * @return true, if is continue counting
	 */
	public boolean isContinueCounting() {
		return continueCounting;
	}

	/**
	 * Sets the continue counting.
	 *
	 * @param continueCounting the new continue counting
	 */
	public void setContinueCounting(boolean continueCounting) {
		this.continueCounting = continueCounting;
	}

	/**
	 * Goes trough the pixels of original image and checks is the color found from colorList. 
	 * If found -> coordinate of pixel saved to list.
	 *
	 * @throws Exception the exception
	 */
	private void calculateCoordinates(){
		  try {
			   this.current_coordinates=new ArrayList<Point>();

			   // go through colorMatrix
			   for(int r =0;r<this.imageHeight;r+=current_gap){ // rows
				   for(int c =0;c<this.imageWidth;c+=current_gap){ // columns
					   if(!continueCounting){
						   return;
					   }
					   try {
						if(hasCellColor(r,c,this.imageWidth)){ // the pixels is same as some color in picked cell
							   Point p = new Point(c,r+this.rowsBefore); // column value is right but the row value r has to be fixed
							   if(!this.current_coordinates.contains(p))
							   {
								   this.current_coordinates.add(p);
							   }
						   }
					} catch (Exception e) {
						LOGGER.severe("Error in checking has image single color!");
						e.printStackTrace();
					}
				   } // for columns
   
			   } // for rows
			  
		} catch (Exception e) {
			LOGGER.severe("Error in calculating coordinates!");
			e.printStackTrace();
		}
	   }
	
	   /**
   	 * Checks is color at image position found from colorList. The color value is reduced
   	 *
   	 * @param r the row
   	 * @param c the column
   	 * @param w the width of image
   	 * @return true, if found the color
   	 */
	   private boolean hasCellColor(int r, int c, int w){
		   int index = (c + r*w)*3; // count the index. Each pixel consists three bytes
		   try {
			
			   int argb =  ( 255 << 24) | ((int)(this.imageBytes[index+2] & 0xFF) << 16) | ((int)(this.imageBytes[index + 1] & 0xFF) << 8) | ((int)this.imageBytes[index] & 0xFF);
			   int reducedNoiseArgb = getReducedNoiseColor(argb);
			   if(foundColor(reducedNoiseArgb))
				   return true; // found color
			   else
				   return false; // color not found
		} catch (Exception e) {
			LOGGER.severe("Error in checking has cell color: "+index + " "+ " gap:"+ current_gap+ " size: "+(this.imageBytes.length-1) + " error: "+e.getMessage());
			e.printStackTrace();
			return false;
		}
	   }
	   
	   
	   public ArrayList<Point> getCoordinates(){
		   return this.current_coordinates;
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

}

package information;

import java.util.logging.Logger;

/**
 * The Class ColorChannelVectors. Collects data from colors separated to red, green and blue.
 */
public class ColorChannelVectors {
	
	/** The red_original. Original red colors. */
	private int[] red_original;    
	
	/** The green_original. Original green colors. */
	private int[] green_original;  
	
	/** The blue_original. Original blue colors. */
	private int[] blue_original;   
	
	/** The full color int_original. */
	private int[] fullColorInt_original;
	
	/** The alpha_ma. Vector of moved average of alphas. */
	private double[] alpha_ma; //
	
	/** The red_ma. Vector of moved average of red. */
	private double[] red_ma;   
	
	/** The green_ma. Vector of moved average of green. */
	private double[] green_ma; 
	
	/** The blue_ma.Vector of moved average of blue. */
	private double[] blue_ma;  
	
	/** The size. Size of vector. */
	private int size; 
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/**
	 * Instantiates a new color channel vectors.
	 *
	 * @param size the size for vector
	 */
	public ColorChannelVectors(int size){
		this.size=size;	
	}
	
	
	/**
	 * Returns the moved averaged alpha at position.
	 *
	 * @param x the position in vector
	 * @return the alpha value
	 */
	public double getAlphaAt(int x){
		return this.alpha_ma[x];
	}
	
	/**
	 * Returns the moved averaged blue at position.
	 *
	 * @param x the position in vector
	 * @return the double blue value
	 */
	public double getBlueAt(int x){
		return this.blue_ma[x];
	}
	
	/**
	 * Returns the full original color at position.
	 *
	 * @param index the index
	 * @return the full color as Integer
	 */
	public int getFullColorInt_original(int index) {
		return fullColorInt_original[index];
	}
	
	/**
	 * Returns the moved averaged blue at position.
	 *
	 * @param x the position in vector
	 * @return the double green value
	 */
	public double getGreenAt(int x){
		return this.green_ma[x];
	}

	/**
	 * Counts and returns the sum of slopes of  for red, green and blue vectors. 
	 * A slope for every color vector is counted by:  ( colorInt(at position end)- colorInt(at position start)) / (end - start).
	 * If single slope is negative: multiplied with -1.
	 *
	 * @param start_x the position at start
	 * @param end_x the the position at end
	 * @return the sum of slopes 
	 */
	public double getKvalueAt(int start_x, int end_x){
		
		try {
			if(start_x < end_x && start_x >=0 && end_x <this.size){
				
				double redK = (red_ma[end_x]-red_ma[start_x])/(end_x-start_x);
				if(redK<0)
					redK *=-1;
				double greenK = (green_ma[end_x]-green_ma[start_x])/(end_x-start_x);
				if(greenK<0)
					greenK *=-1;
				double blueK = (blue_ma[end_x]-blue_ma[start_x])/(end_x-start_x);
				if(blueK<0)
					blueK *=-1;
				return (redK+greenK + blueK);
				
			}
			else 
				return 0;
		} catch (Exception e) {
			LOGGER.severe("Error in gettin k value from color channel!");
			e.printStackTrace();
			return 0;
		}
		
	}
	
	/**
	 * Returns the moved averaged red value at position.
	 *
	 * @param x the position in vector
	 * @return the double red value
	 * @throws Exception the exception
	 */
	public double getRedAt(int x) throws Exception{
		return this.red_ma[x];
	}
	
	/**
	 * Returns the size of vector.
	 *
	 * @return the size
	 * @throws Exception the exception
	 */
	public int getSize() throws Exception {
		return size;
	}
	
	/**
	 * Sets the vector for full colors. Contains whole colors as Integer.
	 *
	 * @param fullColorInt_original the vector for new full color
	 * @throws Exception the exception
	 */
	public void setFullColorInt_original(int[] fullColorInt_original) throws Exception {
		this.fullColorInt_original = fullColorInt_original;
	}
	
	/**
	 * Sets the move averaged data.
	 *
	 * @param a the alpha value
	 * @param r the red value
	 * @param g the green value
	 * @param b the blue value
	 * @throws Exception the exception
	 */
	public void setMoveAveragedData(double[] a, double[] r, double[] g, double[] b) throws Exception{	
		this.alpha_ma=a;
		this.red_ma=r;
		this.green_ma=g;
		this.blue_ma=b;
	}

	/**
	 * Sets the original color channel data. Separately to red, green, blue.
	 *
	 * @param a the alpha value
	 * @param r the red value
	 * @param g the green value
	 * @param b the blue value
	 * @throws Exception the exception
	 */
	public void setOriginalData(int[] a, int[] r, int[] g, int[] b) throws Exception{	
		this.red_original=r;
		this.green_original=g;
		this.blue_original=b;
		
		setOriginalDataAsSingleInt();
	}
	
	/**
	 * Sets the original data as single Integer.
	 * Gets separately red, green and blue channels and counts the full Integer value.
	 *
	 * @throws Exception the exception
	 */
	private void setOriginalDataAsSingleInt() throws Exception{
		this.fullColorInt_original=new int[this.red_original.length];
		int argb=0;
		
			for (int i = 0; i < fullColorInt_original.length; i++) {
				argb =  (255 << 24) | (red_original[i] << 16) | ( green_original[i] << 8) | blue_original[i];
				fullColorInt_original[i]=argb;
		}	
		
	}

	/**
	 * Sets the single data point.
	 *
	 * @param a the alpha value
	 * @param r the red value
	 * @param g the green value
	 * @param b the blue value
	 * @param i the index
	 * @throws Exception the exception
	 */
	public void setSingleDataPoint(double a, double r, double g, double b, int i) throws Exception{	
		this.alpha_ma[i]=a;
		this.red_ma[i]=r;
		this.green_ma[i]=g;
		this.blue_ma[i]=b;
	}
	
	/**
	 * Sets the single data point.
	 *
	 * @param r the red value
	 * @param g the green value
	 * @param b the blue value
	 * @param i the index
	 * @throws Exception the exception
	 */
	public void setSingleDataPoint(double r, double g, double b, int i) throws Exception{	
		
		this.red_ma[i]=r;
		this.green_ma[i]=g;
		this.blue_ma[i]=b;
	}
		
	/**
	 * Sets the size.
	 *
	 * @param size the new size
	 * @throws Exception the exception
	 */
	public void setSize(int size) throws Exception {
		this.size = size;
	}
	
	
}

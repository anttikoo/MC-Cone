package information;

/**
 * The Class ImageColorChannels. Contains four matrixes collecting each alpha, red, green and blue color values of image pixels. 
 */
public class ImageColorChannels {
	
	/** The alpha. */
	private int[][] alpha;
	
	/** The red. */
	private int[][] red;
	
	/** The green. */
	private int[][] green;
	
	/** The blue. */
	private int[][] blue;
	
	/** The width. */
	private int width;
	
	/** The height. */
	private int height;
	
	/** The use alpha. */
	private boolean useAlpha=false;
	
	/**
	 * Instantiates a new ImageColorChannel.
	 *
	 * @param h the height of image: matrix.
	 * @param w the width of image: matrix.
	 */
	public ImageColorChannels(int h, int w){
		this.setAlpha(new int[h][w]);
		this.setRed(new int[h][w]);
		this.setGreen(new int[h][w]);
		this.setBlue(new int[h][w]);	
		setWidth(w);
		setHeight(h);
	}
	
	/**
	 * Adds the alpha value to position r,c at alpha matrix.
	 *
	 * @param r the row
	 * @param c the column
	 * @param value the alpha value
	 * @throws Exception the exception
	 */
	public void addAlpha(int r, int c, int value) throws Exception {
		
			this.alpha[r][c]=value;
		
		
	}
	
	/**
	 * Adds the blue value to position r,c at blue matrix.
	 *
	 * @param r the row
	 * @param c the column
	 * @param value the blue value
	 * @throws Exception the exception
	 */
	public void addBlue(int r, int c, int value) throws Exception{
		this.blue[r][c]=value;
	}
	
	
	/**
	 * Adds the green value to position r,c at green matrix.
	 *
	 * @param r the row
	 * @param c the column
	 * @param value the green value
	 * @throws Exception the exception
	 */
	public void addGreen(int r, int c, int value) throws Exception{
		this.green[r][c]=value;
	}
	
	/**
	 * Adds the red value to position r,c at red matrix.
	 *
	 * @param r the row
	 * @param c the column
	 * @param value the red value
	 * @throws Exception the exception
	 */
	public void addRed(int r, int c, int value ) throws Exception{
		this.red[r][c]=value;
	}


	/**
	 * Returns the alpha matrix.
	 *
	 * @return the alpha
	 */
	public int[][] getAlpha() {
		return alpha;
	}
	
	/**
	 * Returns the alpha value at position r,c.
	 *
	 * @param r the r
	 * @param c the c
	 * @return the alpha
	 */
	public int getAlpha(int r, int c) {
		return alpha[r][c];
	}


	/**
	 * Returns the blue matrix.
	 *
	 * @return the blue
	 */
	public int[][] getBlue() {
		return blue;
	}


	/**
	 * Returns the blue at position r,c.
	 *
	 * @param r the r
	 * @param c the c
	 * @return the blue
	 */
	public int getBlue(int r, int c) {
		return blue[r][c];
	}
	
	/**
	 * Returns the green matrix.
	 *
	 * @return the green
	 */
	public int[][] getGreen() {
		return green;
	}


	/**
	 * Returns the green Integer at position r,c.
	 *
	 * @param r the r
	 * @param c the c
	 * @return the green
	 */
	public int getGreen(int r, int c) {
		return green[r][c];
	}


	/**
	 * Returns the height.
	 *
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Returns the red matrix.
	 *
	 * @return the red
	 */
	public int[][] getRed() {
		return red;
	}


	/**
	 * Returns the red Integer at position r,c..
	 *
	 * @param r the r
	 * @param c the c
	 * @return the red
	 */
	public int getRed(int r, int c){
		return red[r][c];
	}


	/**
	 * Returns the width.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Sets the alpha matrix.
	 *
	 * @param alpha the new alpha
	 */
	public void setAlpha(int[][] alpha) {
		this.alpha = alpha;
	}


	/**
	 * Sets the blue matrix.
	 *
	 * @param blue the new blue
	 */
	public void setBlue(int[][] blue) {
		this.blue = blue;
	}

	/**
	 * Sets the green matrix.
	 *
	 * @param green the new green
	 */
	public void setGreen(int[][] green) {
		this.green = green;
	}

	/**
	 * Sets the height.
	 *
	 * @param height the new height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Sets the red matrix.
	 *
	 * @param red the new red
	 */
	public void setRed(int[][] red) {
		this.red = red;
	}

	/**
	 * Sets the boolean useAlpha.
	 *
	 * @param useAlpha the new use alpha
	 */
	public void setUseAlpha(boolean useAlpha) {
		this.useAlpha = useAlpha;
	}

	/**
	 * Sets the width.
	 *
	 * @param width the new width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Returns boolean useAlpha if alpha channel is used in image.
	 *
	 * @return true, if successful
	 */
	public boolean useAlpha() {
		return useAlpha;
	}

	
}

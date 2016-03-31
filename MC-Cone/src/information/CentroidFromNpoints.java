package information;

import java.awt.Point;
import java.util.logging.Logger;

/**
 * The Class CentroidFromNpoints. Contains a Centeroid point and from how many points it is composed.
 */
public class CentroidFromNpoints extends Point{

	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3096112387384468511L;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/** The number of coordinates. */
	private int numberOfCoordinates=0;
	
	public CentroidFromNpoints(Point point, int amount){
		super(point);
		try {
			this.setNumberOfCoordinates(amount);
		} catch (Exception e) {
			LOGGER.severe("Error in creating CenteoidFromNPoints");
			e.printStackTrace();
		}
	}

	/**
	 * Returns the number of coordinates.
	 *
	 * @return the number of coordinates
	 * @throws Exception the exception
	 */
	public int getNumberOfCoordinates() throws Exception{
		return numberOfCoordinates;
	}

	/**
	 * Sets the number of coordinates.
	 *
	 * @param numberOfCoordinates the new number of coordinates
	 * @throws Exception the exception
	 */
	public void setNumberOfCoordinates(int numberOfCoordinates) throws Exception {
		this.numberOfCoordinates = numberOfCoordinates;
	}
	
	
}

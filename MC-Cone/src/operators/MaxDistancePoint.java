package operators;

// TODO: Auto-generated Javadoc
/**
 * The Class MaxDistancePoint.
 */
public class MaxDistancePoint extends WeightPoint{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3314178055602162501L;
	/** The distance. */
	private double distance=-1;
	
	/**
	 * Instantiates a new max distance point.
	 *
	 * @param wpoint the WieghtPoint
	 * @param distance the distance
	 * @throws Exception the exception
	 */
	public MaxDistancePoint(WeightPoint wpoint, double distance) throws Exception{
		super(wpoint);
		this.setDistance(distance);
	}
	
	/**
	 * Returns the distance.
	 *
	 * @return the distance
	 * @throws Exception the exception
	 */
	public double getDistance() throws Exception {
		return distance;
	}
	
	/**
	 * Sets the distance.
	 *
	 * @param distance the new distance
	 * @throws Exception the exception
	 */
	public void setDistance(double distance) throws Exception {
		this.distance = distance;
	}

}

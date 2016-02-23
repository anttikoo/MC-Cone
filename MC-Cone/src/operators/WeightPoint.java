package operators;

import java.awt.Point;
import math.geom2d.Point2D;


/**
 * The Class WeightPoint. Contains a weight value for point.
 */
public class WeightPoint extends Point{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2531752602427087825L;
	/** The weight. */
	private double weight;

	/**
	 * Instantiates a new WeightPoint by given Point.
	 *
	 * @param point the Point
	 * @throws Exception the exception
	 */
	public WeightPoint(Point point) throws Exception{
		super(point);
		this.setWeight(1);
		setPoint(point);
	}

	/**
	 * Instantiates a new WeightPoint by given WeightPoint.
	 *
	 * @param wp the WeightPoint
	 * @throws Exception the exception
	 */
	public WeightPoint(WeightPoint wp) throws Exception{
		super(wp.getPoint());
		this.setWeight(wp.getWeight());
		//setPoint(point);
	}

	/**
	 * Decreases weight by dividing with given double value.
	 *
	 * @param d the double weight
	 * @throws Exception the exception
	 */
	public void decreaseWeight(double d) throws Exception{
		this.weight=this.weight/d;
	}

	/**
	 * Distance.
	 *
	 * @param p2 the p2
	 * @return the double
	 * @throws Exception the exception
	 */
	public double distance(WeightPoint p2) throws Exception{

		return this.getPoint().distance(p2.getPoint());
	}

	/**
	 * Returns the point.
	 *
	 * @return the Point
	 * @throws Exception the exception
	 */
	public Point getPoint() throws Exception {
		return this;
	}

	/**
	 * Returns the point2 d.
	 *
	 * @return the point2 d
	 * @throws Exception the exception
	 */
	public Point2D getPoint2D() throws Exception {
		return new Point2D(this.x, this.y);
	}

	/**
	 * Returns the weight.
	 *
	 * @return the double weight
	 * @throws Exception the exception
	 */
	public double getWeight()  throws Exception{
		return weight;
	}

	/**
	 * Increases weight by summing with given double value.
	 *
	 * @param increase the increase
	 * @throws Exception the exception
	 */
	public void increaseWeight(double increase) throws Exception{
		this.setWeight(this.getWeight() + increase);
	}

	/**
	 * Sets the point.
	 *
	 * @param p the new point
	 * @throws Exception the exception
	 */
	public void setPoint(Point p)  throws Exception{
		this.x=p.x;
		this.y=p.y;
	}

	/**
	 * Sets the weight.
	 *
	 * @param weight the new weight
	 * @throws Exception the exception
	 */
	public void setWeight(double weight)  throws Exception{
		this.weight = weight;
	}
}

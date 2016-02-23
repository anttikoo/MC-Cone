package information;

import java.awt.Point;
import java.util.ArrayList;

/**
 * The Class ScreenCoordinatesOfMarkingLayer.
 */
public class ScreenCoordinatesOfMarkingLayer {
	
	/** The list of coordinates. */
	private ArrayList<Point> coordinates;
	
	/** The id. ID of MarkingLayer which coordinate this is. */
	private int id; 
	
	/**
	 * Instantiates a new screen coordinates of marking layer.
	 *
	 * @param points ArrayList of  points
	 * @param id the ID of MarkingLayer
	 */
	public ScreenCoordinatesOfMarkingLayer(ArrayList<Point> points, int id){
		this.coordinates=points;
		this.id=id;
	}

	/**
	 * Adds a single coordinate.
	 *
	 * @param point the point
	 */
	public void addCoordinate(Point point){
		if(point != null)
			this.coordinates.add(point);
	}

	/**
	 * Returns the coordinates.
	 *
	 * @return the ArrayList of coordinates
	 */
	public ArrayList<Point> getCoordinates() {
			return coordinates;
	}
	
	/**
	 * Returns the ID.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

}

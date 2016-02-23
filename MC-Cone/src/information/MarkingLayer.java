package information;


import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;
import operators.CoordinateComparator;


/**
 * The Class MarkingLayer. 
 * Contains data: 
 * coordinates of markings, 
 * shape of markings
 * size of markings
 * color of markings
 * type (id) of markings
 * thickness of markings
 * opacity of markings
 * visibility of markings
 * selected MarkingLayer
 * GRID of MarkingLayer
 * GRID visibility.
 * Contains methods to manipulate that data.
 */
public class MarkingLayer{

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The coordinate list. */
	private ArrayList<Point> coordinateList;
	
	/** The layer id. */
	private int layerID;
	
	/** The order. */
	private int order;
	
	/** The layer name. */
	private String layerName;
	
	/** The color. */
	private Color color;
	
	/** The shape id. */
	private int shapeID;
	
	/** The size. */
	private int size; // size of marking
	
	/** The thickness. */
	private int thickness;
	
	/** The opacity. */
	private float opacity;
	
	/** The is visible. */
	private boolean isVisible=true;
	
	/** The is selected. */
	private boolean isSelected=false;
	
	/** The grid properties. */
	private GridProperties gridProperties=null;
	
	/** The draw grid to image. */
	private boolean drawGridToImage=false;
	
	/** The made changes. Has any changes made that are not saved*/
	private boolean madeChanges=true;


	/**
	 * Instantiates a new MarkingLayer.
	 *
	 * @param name the name of MarkingLayer
	 */
	public MarkingLayer(String name){
		try {
			this.setLayerID(-1); // initialize the id to negative value
			this.setLayerName(name);
			this.setColor(new Color(100,255,0)); // should change to get color which is not used yet
			this.setShapeID(ID.SHAPE_CIRCLE); // initial value -> changed later
			this.coordinateList = new ArrayList<Point>();
			this.size=20;	// initial value -> changed later
			this.thickness=2; // initial value -> changed later
			this.opacity=1.0F; // initial value -> changed later
		} catch (Exception e) {
			LOGGER.severe("Error in initializing MarkingLayer!");
			e.printStackTrace();
		}
		
	}

	/**
	 * Instantiates a new MarkingLayer.
	 *
	 * @param name the name of MarkingLayer
	 * @param shape the shape type of marking
	 * @param color the color of marking
	 */
	public MarkingLayer(String name, int shape, Color color){
		try {
			this.setLayerID(-1); // initialize the id to negative value
			this.setLayerName(name);
			this.setColor(color); // should change to get color which is not used yet
			this.setShapeID(shape); // initial value -> changed later
			this.coordinateList = new ArrayList<Point>();
			this.size=20;	// initial value -> changed later
			this.thickness=2; // initial value -> changed later
			this.opacity=1.0F; // initial value -> changed later
		} catch (Exception e) {
			LOGGER.severe("Error in initializing MarkingLayer!");
			e.printStackTrace();
		}
	}

	/**
	 * Adds the single coordinate.
	 *
	 * @param coordinate the coordinate
	 * @return true, if successful
	 */
	public boolean addSingleCoordinate(Point coordinate){
		
		if(this.gridProperties != null && this.gridProperties.isGridON()){

			if(!gridProperties.isPointInsideSelectedRectangle(coordinate) ){
				return false;
			}
		}
		this.coordinateList.add(coordinate);

		Collections.sort(this.coordinateList, new CoordinateComparator());
		setMadeChanges(true);
		return true;
	}

	/**
	 * Adds a coordinate given by string.
	 *
	 * @param stringPoint the string point
	 */
	public void addStringPoint(String stringPoint){

		try {
			LOGGER.fine("trying convert to Point:_"+stringPoint+"_");;
			if(stringPoint != null && stringPoint.length()>2 && stringPoint.contains(",")){
				String[] sv = stringPoint.split(",");
				if(sv.length==2){
					try{
						Point p = new Point(Integer.parseInt(sv[0].trim()), Integer.parseInt(sv[1].trim()));
						this.coordinateList.add(p);
					}
					catch(Exception ex){
						// problem in converting from string to Point
						LOGGER.warning(" MarkingLayer: Problems in converting String to Point. Value not converted");
					}
				}
			}

		} catch (Exception e) {
			LOGGER.severe("Error in creating Point from String:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());

		}

	}

	/**
	 * Clears coordinate list.
	 *
	 * @throws Exception the exception
	 */
	public void clearCoordinateList() throws Exception{
		this.coordinateList.clear();
		setMadeChanges(true);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
	
	/**
	 * Returns the color.
	 *
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Returns the coordinate list. Checks is GRID active and returns only coordinates in selected GRID cells.
	 *
	 * @return the coordinate list
	 */
	public ArrayList<Point> getCoordinateList() {
		if(this.gridProperties != null && this.gridProperties.isGridON()){
			ArrayList<Point> pointList = new ArrayList<Point>();
			Iterator<Point> pIterator = this.coordinateList.iterator();
			while(pIterator.hasNext()){
				Point p= pIterator.next();
				if(gridProperties.isPointInsideSelectedRectangle(p))
					pointList.add(p);
			}
			return pointList;
		}
		else
		return coordinateList;
	}


	/**
	 * Returns the coordinate list independent is GRID ON.
	 *
	 * @return the coordinate list independent is GRID ON.
	 * @throws Exception the exception
	 */
	public ArrayList<Point> getCoordinateListIgnoringGrid() throws Exception {

		return coordinateList;
	}

	/**
	 * Counts and returns the amount of coordinates. If GRID is active: counts proportional number of countings.
	 *
	 * @return the Integer counts
	 * @throws Exception the exception
	 */
	public int getCounts() throws Exception{
		if(this.gridProperties != null && this.gridProperties.isGridON()){
			double multiplyer = this.gridProperties.calculateSelectedRectangleAreaRelation();
			return (int)Math.round(((double)getCoordinateList().size()*multiplyer)); // round method returns closest long but don't worry: size of coordinatelist can't exceed max integer value
		}
		else
			return getCoordinateList().size();
	}
	
	/**
	 * Returns the grid properties.
	 *
	 * @return the grid properties
	 * @throws Exception the exception
	 */
	public GridProperties getGridProperties() throws Exception{
		return gridProperties;
	}
	
	/**
	 * Returns the ID of MarkingLayer.
	 *
	 * @return the ID of MarkingLayer
	 * @throws Exception the exception
	 */
	public int getLayerID() throws Exception {
		return layerID;
	}
	
	/**
	 * Returns the name of MarkingLayer.
	 *
	 * @return the name of MarkingLayer
	 * @throws Exception the exception
	 */
	public String getLayerName() throws Exception {
		return layerName;
	}


	/**
	 * Returns the Manhattan distance between two point. Faster than euclidean distance.
	 *
	 * @param p1 the Point 1
	 * @param p2 the point 2
	 * @return the mManhattan distance
	 * @throws Exception the exception
	 */
	private double getManhattanDistance(Point p1, Point p2){

		try {
			int x_distance= p1.x-p2.x;
			if(x_distance <0)
				x_distance*=-1;

			int y_distance= p1.y-p2.y;
			if(y_distance < 0)
				y_distance*=-1;

				return x_distance+y_distance;
		} catch (Exception e) {
			LOGGER.severe("Error in calculating Manhattan distance!");
			e.printStackTrace();
			return Integer.MAX_VALUE;
		}

	}

	/**
	 * Returns the opacity.
	 *
	 * @return the opacity
	 * @throws Exception the exception
	 */
	public float getOpacity() throws Exception {
		return opacity;
	}

	/**
	 * Returns the order.
	 *
	 * @return the order
	 * @throws Exception the exception
	 */
	public int getOrder() throws Exception{
		return order;
	}

	/**
	 * Returns the shape id.
	 *
	 * @return the shape id
	 * @throws Exception the exception
	 */
	public int getShapeID() throws Exception {
		return shapeID;
	}
	
	/**
	 * Returns the size.
	 *
	 * @return the size
	 * @throws Exception the exception
	 */
	public int getSize() throws Exception {
		return size;
	}

	

	/**
	 * Returns the string color.
	 *
	 * @return the string color
	 * @throws Exception the exception
	 */
	public String getStringColor() throws Exception{
		return Integer.toString(color.getRGB());
	}

	/**
	 * Returns the list of Points as String.
	 *
	 * @return the string point list
	 */
	public ArrayList<String> getStringPointList(){
		ArrayList<String> pointStringList = new ArrayList<String>();
		try {
			if(this.coordinateList != null && this.coordinateList.size() >0){
				Iterator<Point> iIterator = this.coordinateList.iterator();
				while(iIterator.hasNext()){
					Point p = (Point)iIterator.next();
					String ps= ""+p.x+","+p.y;
					pointStringList.add(ps);
				}
			}
			return pointStringList;
		} catch (Exception e) {
			LOGGER.severe("Error in creating StringPoint:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}

	}

	/**
	 * Returns the thickness.
	 *
	 * @return the thickness
	 */
	public int getThickness() {
		return this.thickness;
	}


	/**
	 * Checks if is draw grid to image.
	 *
	 * @return true, if is draw grid to image
	 */
	public boolean isDrawGridToImage() {
		return drawGridToImage;
	}

	/**
	 * Checks if is grid on (active).
	 *
	 * @return true, if is grid on (active)
	 */
	public boolean isGridON(){
			if(this.gridProperties != null && gridProperties.isGridON())
				return true;
	
			return false;
		}

	public boolean isMadeChanges() {
		return madeChanges;
	}



/**
 * Checks if is this MarkingLayer selected.
 *
 * @return true, if is selected
 */
public boolean isSelected() {
	return isSelected;
}

	/**
	 * Checks if is this MarkingLayer visible.
	 *
	 * @return true, if is visible
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * Creates and returns a copy of this MarkingLayer.
	 *
	 * @return the marking layer
	 */
	public MarkingLayer makeCopy(){
		try {
			// create new object with same name as this one
			MarkingLayer copyMarkingLayer = new MarkingLayer(this.getLayerName());
			// set layerID
			copyMarkingLayer.setLayerID(this.getLayerID());
			// set color
			copyMarkingLayer.setColor(this.getColor());
			// set shape
			copyMarkingLayer.setShapeID(this.getShapeID());
			// set coordinatelist: go through list and create new Point of each

			if(this.coordinateList != null && this.coordinateList.size()>0){
			ArrayList<Point> copyPointList= makeCopyPoints(this.coordinateList);
			 copyMarkingLayer.setCoordinateList(copyPointList);
			}

			if(this.gridProperties != null)
				copyMarkingLayer.setGridProperties(this.getGridProperties());
			return copyMarkingLayer;
		} catch (Exception e) {
			LOGGER.severe("Error in creating copy of MarkingLayer:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}

	/**
	 * Make and returns a copy of list of coordinates.
	 *
	 * @param originalList the original list
	 * @return the array list
	 */
	private ArrayList<Point> makeCopyPoints(ArrayList<Point> originalList){
		ArrayList<Point> copyPointList= new ArrayList<Point>();
		if(originalList != null && originalList.size()>0){
			Iterator<Point> iIterator = originalList.iterator();
			while(iIterator.hasNext()){
				Point oldPoint = (Point)iIterator.next();
				Point newPoint =new Point();
				newPoint.setLocation(oldPoint.getX(), oldPoint.getY());
				copyPointList.add(newPoint);
			}

			return copyPointList;
		}
		return null;
	}



	/**
	 * Removes the single coordinate from list of coordinates.
	 *
	 * @param coordinate the coordinate to be removed
	 * @return true, if successful
	 */
	public boolean removeSingleCoordinate(Point coordinate){

		try {
			// find the closest point of list
			Point candidatePoint=null; //=new Point(Integer.MAX_VALUE,Integer.MAX_VALUE);
			double candidateDistance=Integer.MAX_VALUE;
			Iterator<Point> iIterator = this.coordinateList.iterator();

			while(iIterator.hasNext()){
				Point presentPoint= iIterator.next();
				double presentDistance= getManhattanDistance(presentPoint, coordinate);
				if(presentDistance< candidateDistance){
					candidatePoint=presentPoint;
					candidateDistance=presentDistance;
				}
			}
			if(candidateDistance < SharedVariables.DISTANCE_TO_REMOVE){
				if(this.gridProperties != null && this.gridProperties.isGridON())
					if(!gridProperties.isPointInsideSelectedRectangle(candidatePoint) ){
						return false;
					}
					LOGGER.fine("Removing marking coordinate");
					this.coordinateList.remove(candidatePoint);
					// sort the coordinate list although the order shouldn't been changed
					Collections.sort(this.coordinateList, new CoordinateComparator());
					setMadeChanges(true); // set that has made changes to this MarkingLayer
					return true;


			}
			return false;

		} catch (Exception e) {
			LOGGER.severe("Error removing SingleCoordinate");
			return false;
		}

	}

	/**
	 * Sets the marking color.
	 *
	 * @param color the new color
	 * @throws Exception the exception
	 */
	public void setColor(Color color) throws Exception {
		
		this.color = color;
		setMadeChanges(true); // set that has made changes to this MarkingLayer
	}

	/**
	 * Sets the coordinate list.
	 *
	 * @param coordinateList the new coordinate list
	 */
	public void setCoordinateList(ArrayList<Point> coordinateList) {
		if(coordinateList != null && coordinateList.size()>0){
			this.coordinateList = makeCopyPoints(coordinateList);
			Collections.sort(this.coordinateList, new CoordinateComparator());
			setMadeChanges(true);
		}
	}

	/**
	 * Sets the draw grid to image.
	 *
	 * @param drawGridToImage the new draw grid to image
	 */
	public void setDrawGridToImage(boolean drawGridToImage) {
		this.drawGridToImage = drawGridToImage;
		setMadeChanges(true); // set that has made changes to this MarkingLayer
	}

	/**
	 * Sets the grid properties.
	 *
	 * @param gProperties the new grid properties
	 * @throws Exception the exception
	 */
	public void setGridProperties(GridProperties gProperties)  throws Exception{
		if(gProperties != null){
			
			GridProperties gridProperty=new GridProperties(gProperties.getPresentImageDimension());
			
			gridProperty.setGridON(gProperties.isGridON());
			gridProperty.setRowLinesYList(gProperties.getRowLineYs());
			gridProperty.setColumnLinesXList(gProperties.getColumnLineXs());
			gridProperty.setPositionedRectangleList(gProperties.getPositionedRectangleList());
			gridProperty.setRandomPercent(gProperties.getRandomProcent());
	
			this.gridProperties = gridProperty;
			setMadeChanges(true); // set that has made changes to this MarkingLayer
		}
	}

	/**
	 * Sets the ID of this MarkingLayer.
	 *
	 * @param layerID the new layer id
	 * @throws Exception the exception
	 */
	public void setLayerID(int layerID) throws Exception{
		this.layerID = layerID;
		this.order=this.layerID;
		setMadeChanges(true); // set that has made changes to this MarkingLayer
	}

	/**
	 * Sets the name of this MarkingLayer.
	 *
	 * @param layerName the name of this MarkingLayer
	 * @throws Exception the exception
	 */
	public void setLayerName(String layerName) throws Exception{
		this.layerName = layerName;
		setMadeChanges(true); // set that has made changes to this MarkingLayer
	}

	/**
	 * Sets the made changes.
	 *
	 * @param madeChanges the new made changes
	 */
	public void setMadeChanges(boolean madeChanges){
		try {
			this.madeChanges = madeChanges;
		} catch (Exception e) {
			LOGGER.severe("Error in saving information, is any changes made!");
			e.printStackTrace();
		}
	}

	/**
	 * Sets the opacity.
	 *
	 * @param opacity the new opacity
	 * @throws Exception the exception
	 */
	public void setOpacity(float opacity) throws Exception{
		this.opacity = opacity;
		setMadeChanges(true); // set that has made changes to this MarkingLayer
	}
	
	/**
	 * Sets the order.
	 *
	 * @param order the new order
	 * @throws Exception the exception
	 */
	public void setOrder(int order) throws Exception{
		this.order = order;
		
	}

	/**
	 * Sets the MarkingLayer as selected/unselected.
	 *
	 * @param selected the new state is MarkingLayer selected
	 * @throws Exception the exception
	 */
	public void setSelected(boolean selected) throws Exception{
		this.isSelected = selected;
	}

	/**
	 * Sets the shape id.
	 *
	 * @param shape the new shape id
	 * @throws Exception the exception
	 */
	public void setShapeID(int shape) throws Exception{
		this.shapeID = shape;
		setMadeChanges(true); // set that has made changes to this MarkingLayer
	}

	/**
	 * Sets the size.
	 *
	 * @param size the new size
	 * @throws Exception the exception
	 */
	public void setSize(int size) throws Exception{
		this.size = size;
		setMadeChanges(true); // set that has made changes to this MarkingLayer
	}

	/**
	 * Sets the string color.
	 *
	 * @param colorS the new string color
	 * @throws Exception the exception
	 */
	public void setStringColor(String colorS) throws Exception{
		this.color = new Color(Integer.parseInt(colorS));
	}

	/**
	 * Sets the string point list.
	 *
	 * @param stringPointList the new string point list
	 */
	public void setStringPointList(ArrayList<String> stringPointList){
		int errors=0;
		try {

			if(stringPointList != null && stringPointList.size() >0){
				Iterator<String> iIterator = stringPointList.iterator();
				while(iIterator.hasNext()){

					String s = (String)iIterator.next();
					if(s != null && s.length()>2 && s.contains(",")){
						String[] sv = s.split(",");
						if(sv.length==2){
							try{
								Point p = new Point(Integer.parseInt(sv[0].trim()), Integer.parseInt(sv[1].trim()));
								this.coordinateList.add(p);
								
							}
							catch(Exception ex){
								// problem in converting from string to Point
								errors++;
							}
						}
					}

				}
				setMadeChanges(true); // set that has made changes to this MarkingLayer

				}
			LOGGER.warning(" MarkingLayer: Problems in converting String to Point. " + errors + " values not converted");

		} catch (Exception e) {
			LOGGER.severe("Error in creating PointList from StringList:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());

		}

	}

	/**
	 * Sets the thickness.
	 *
	 * @param thickness the new thickness
	 * @throws Exception the exception
	 */
	public void setThickness(int thickness) throws Exception {
		this.thickness = thickness;
		setMadeChanges(true); // set that has made changes to this MarkingLayer
	}

	/**
	 * Sets the visibility of markings.
	 *
	 * @param isVisible the new visible
	 * @throws Exception the exception
	 */
	public void setVisible(boolean isVisible) throws Exception{
		this.isVisible = isVisible;
	}



}

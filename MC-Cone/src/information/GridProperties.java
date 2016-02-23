package information;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;



/**
 * One object of Gridproperties contains information of grid for one @see MarkingLayer.
 * Gridproperties determines lines of grid, visible and unsivible rectangles of grid.
 * @author Antti Kurronen
 *
 */
public class GridProperties {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The grid on. */
	private boolean gridON;
	
	/** The row line y. */
	private ArrayList<Integer> rowLineY;
	
	/** The column line x. */
	private ArrayList<Integer> columnLineX;
	
	/** The positioned rectangle list. */
	private ArrayList<PositionedRectangle> positionedRectangleList;
	
	/** The vertical line length. Used for restrict the vertical line length. */
	private int verticalLineLength=0; 
	
	/** The horizontal line length. Used for restrict the horizontal line length. */
	private int horizontalLineLength=0; 
	
	private ArrayList<Rectangle> outerOfGridRectangleList;
	
	/** The present image dimension. */
	private Dimension presentImageDimension=null;
	
	/** The random percent. */
	private int randomPercent=50;


	/**
	 * Instantiates a new grid properties.
	 */
	public GridProperties(){
		try {
			setGridON(false);
			this.rowLineY = new ArrayList<Integer>();
			this.columnLineX = new ArrayList<Integer>();
			this.positionedRectangleList=new ArrayList<PositionedRectangle>();
			this.outerOfGridRectangleList=new ArrayList<Rectangle>();
		} catch (Exception e) {
			LOGGER.severe("Error in initializing GridProperties !");
			e.printStackTrace();
		}

	}

	/**
	 * Instantiates a new grid properties.
	 *
	 * @param iDimension the i dimension
	 */
	public GridProperties(Dimension iDimension){
		try {
			setGridON(false);
			this.rowLineY = new ArrayList<Integer>();
			this.columnLineX = new ArrayList<Integer>();
			this.positionedRectangleList=new ArrayList<PositionedRectangle>();
			this.outerOfGridRectangleList=new ArrayList<Rectangle>();
			if(iDimension != null){
				this.setPresentImageDimension(iDimension);
				this.horizontalLineLength=iDimension.width;
				this.verticalLineLength=iDimension.height;
			}
		} catch (Exception e) {
			LOGGER.severe("Error in initializing GridProperties !");
			e.printStackTrace();
		}
	}

	/**
	 * Adds the column line at horizonal position x.
	 *
	 * @param x the x
	 * @throws Exception the exception
	 */
	public void addColumnLineX(int x) throws Exception{
		this.columnLineX.add(x);
	}


	/**
	 * Adds the row line at vertical position y.
	 *
	 * @param y the y
	 * @throws Exception the exception
	 */
	public void addRowLineY(int y) throws Exception{
		this.rowLineY.add(y);
	}


/**
 * Adds the single outer of grid rectangle.
 *
 * @param rec the Rectangle
 * @throws Exception the exception
 */
public void addSingleOuterOfGridRectangle(Rectangle rec)  throws Exception{
	this.outerOfGridRectangleList.add(rec);
}


	
	/**
	 * Adds the single positioned rectangle.
	 *
	 * @param positionedRectangle the positioned rectangle
	 * @throws Exception the exception
	 */
	public void addSinglePositionedRectangle(PositionedRectangle positionedRectangle)  throws Exception {
			this.positionedRectangleList.add(positionedRectangle);
		}
	
	/**
	 * Calculates outer rectangles showing space outside of GRID.
	 *
	 * @throws Exception the exception
	 */
	public void calculateOuterRectangles()  throws Exception{
		this.outerOfGridRectangleList=new ArrayList<Rectangle>();
		if(rowLineY != null && rowLineY.size()>0 && columnLineX != null && columnLineX.size()>0 && verticalLineLength >0 && horizontalLineLength >0){
			int upY=this.rowLineY.get(0);
			int downY = this.rowLineY.get(rowLineY.size()-1);
			int leftX = this.columnLineX.get(0);
			int rightX = this.columnLineX.get(this.columnLineX.size()-1);
			// calculate Rectangles
			Rectangle up = new Rectangle(0,0, this.horizontalLineLength, upY);
			Rectangle down = new Rectangle(0,downY, this.horizontalLineLength,verticalLineLength-downY);
			Rectangle left = new Rectangle(0,upY, leftX,downY-upY);
			Rectangle right = new Rectangle(rightX,upY,horizontalLineLength-rightX, downY-upY);
			//add to list
			this.outerOfGridRectangleList.add(up);
			this.outerOfGridRectangleList.add(down);
			this.outerOfGridRectangleList.add(left);
			this.outerOfGridRectangleList.add(right);
	
		}		
	}

	/**
	 * Calculates selected rectangle area relation.
	 *
	 * @return the double
	 */
	public double calculateSelectedRectangleAreaRelation(){
		
		try{
		if(this.positionedRectangleList != null && this.positionedRectangleList.size()>0){
			int count_selected=0;
			int count_unselected=0;
			Dimension pRectangleDimension=null;

			Iterator<PositionedRectangle> rIterator = this.positionedRectangleList.iterator();
			while(rIterator.hasNext()){
				PositionedRectangle pr=rIterator.next();
				if(pRectangleDimension == null)
					pRectangleDimension=pr.getSize();
				if(pr.isSelected())
					count_selected++;
				else
					count_unselected++;
			}
			
			if(this.presentImageDimension != null &&  pRectangleDimension != null && count_selected>0){
				return (((double)count_selected+(double)count_unselected)/(double)count_selected)*
						((this.presentImageDimension.getWidth()*this.presentImageDimension.getHeight())/
								(this.positionedRectangleList.size()*(pRectangleDimension.getHeight()*pRectangleDimension.getWidth())));
			}
			else
				return 0;
		}
		else
			return 0;
		}catch(Exception e){
			LOGGER.severe("Error in counting gridResults!" +e.getMessage());
			return 0;
		}
	}

	/**
	 * Changes the selection of rectangle at given imagePoint. If rectangle is originally unselected it will be selected vice versa.
	 * If there are only one selected rectangle in grid, it can't be unselected, because always has to be at least one rectangle selected.
	 *
	 * @param imagePoint Point at image
	 * @throws Exception the exception
	 */
	public void changeSelectionOfPositionedRectangle(Point imagePoint) throws Exception{
		Iterator<PositionedRectangle> recIterator = positionedRectangleList.iterator();
		while(recIterator.hasNext()){
			PositionedRectangle rec =recIterator.next();
			if(rec.contains(imagePoint)){
				// set rectangle as selected or unselected
				if(!rec.isSelected() || countSelectedRectangles() > 1)
				rec.setSelected(!rec.isSelected());
				
				//update the percentValue to round to the nearest multiple of 5
				this.randomPercent= (int)((Math.round((((double)countSelectedRectangles())/ ((double)this.positionedRectangleList.size())*100))/5)*5);

				
			}
		}
	}
	
	/**
	 * Check random percent by selected rectangles.
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean checkRandomPercentBySelectedRectangles()  throws Exception{
		int selectedRectangles = countSelectedRectangles();
		
		if(selectedRectangles >0){
			if((int)(Math.ceil((((double)this.randomPercent)/100)*(double)getGridColumnCount()*(double)getGridRowCount())) == selectedRectangles){
				return true;
			}
			
			
		}
		return false;
			
		
		
	}

	/**
	 * Counts amount of selected rectangles.
	 *
	 * @return the int
	 * @throws Exception the exception
	 */
	private int countSelectedRectangles() throws Exception{
		if(this.positionedRectangleList != null && this.positionedRectangleList.size()>0){
			int counter=0;
			Iterator<PositionedRectangle> recIterator = positionedRectangleList.iterator();
			while(recIterator.hasNext()){
				PositionedRectangle rec =recIterator.next();
				if(rec.isSelected()){
					counter++;

				}
			}
			return counter;
		}
		return 0;
	}

	/**
	 * Returns the column line horizontal values.
	 *
	 * @return the column line x values
	 * @throws Exception the exception
	 */
	public ArrayList<Integer> getColumnLineXs()  throws Exception{
		return columnLineX;
	}

	/**
	 * Returns the grid column count.
	 *
	 * @return the grid column count
	 * @throws Exception the exception
	 */
	public int getGridColumnCount()  throws Exception{
		if(this.columnLineX != null && this.columnLineX.size()>0)
		return this.columnLineX.size()-1;
		else
			return 0;
	}


	/**
	 * Returns the grid row count.
	 *
	 * @return the grid row count
	 */
	public int getGridRowCount(){
		if(this.rowLineY != null && this.rowLineY.size()>0)
		return this.rowLineY.size()-1;
		else
			return 0;
	}

	/**
	 * Returns the horizontal line length.
	 *
	 * @return the horizontal line length
	 */
	public int getHorizontalLineLength() {
		return horizontalLineLength;
	}

	/**
	 * Returns the outer of grid rectangle list.
	 *
	 * @return the outer of grid rectangle list
	 */
	public ArrayList<Rectangle> getOuterOfGridRectangleList() {
		return outerOfGridRectangleList;
	}
	
	/**
	 * Returns the positioned rectangle list.
	 *
	 * @return the positioned rectangle list
	 */
	public ArrayList<PositionedRectangle> getPositionedRectangleList() {
		return positionedRectangleList;
	}

	/**
	 * Returns the present image dimension.
	 *
	 * @return the present image dimension
	 */
	public Dimension getPresentImageDimension() {
		return presentImageDimension;
	}

	/**
	 * Returns the random procent.
	 *
	 * @return the random procent
	 */
	public int getRandomProcent() {
		return randomPercent;
	}

	/**
	 * Returns the row line vertical values.
	 *
	 * @return the row line y values.
	 */
	public ArrayList<Integer> getRowLineYs() {
		return rowLineY;
	}
	
	/**
	 * Returns the vertical line length.
	 *
	 * @return the vertical line length
	 */
	public int getVerticalLineLength() {
		return verticalLineLength;
	}

	/**
	 * Checks if is grid visible (ON).
	 *
	 * @return true, if is grid on
	 */
	public boolean isGridON() {
		return gridON;
	}

	/**
	 * Checks if is point inside any rectangle.
	 *
	 * @param p the Point
	 * @param searchSelected the search selected
	 * @return true, if is point inside any rectangle
	 */
	private boolean isPointInsideAnyRectangle(Point p, boolean searchSelected){
		if(this.positionedRectangleList != null && positionedRectangleList.size()>0){
			Iterator<PositionedRectangle> rIterator = this.positionedRectangleList.iterator();
			while(rIterator.hasNext()){
				PositionedRectangle pr=rIterator.next();
				if(pr.contains(p)){
					if(searchSelected){
						if(pr.isSelected())
							return true;
						else
							return false;
					}
					else{
						if(pr.isSelected())
							return false;
						else
							return true;
					}

				}
			}
		}
		return false;
	}


	/**
	 * Checks if is point inside selected rectangle.
	 *
	 * @param p the Point
	 * @return true, if is point inside selected rectangle
	 */
	public boolean isPointInsideSelectedRectangle(Point p){
		return isPointInsideAnyRectangle(p, true);
	}

	/**
	 * Checks if is point inside un selected rectangle.
	 *
	 * @param p the p
	 * @return true, if is point inside un selected rectangle
	 */
	public boolean isPointInsideUnSelectedRectangle(Point p){
		return isPointInsideAnyRectangle(p, false);
	}

	/**
	 * Checks if is selected grid cell at.
	 *
	 * @param r the row
	 * @param c the column
	 * @return true, if is selected grid cell at
	 */
	public boolean isSelectedGridCellAt(int r, int c){
		Iterator<PositionedRectangle> iIterator =this.positionedRectangleList.iterator();
		while(iIterator.hasNext()){
			PositionedRectangle pr= iIterator.next();
			if(pr.hasPosition(r, c))
				return pr.isSelected();
		}
		return false;
	}
	

	/**
	 * Sets the column lines x list.
	 *
	 * @param xlist the new column lines x list
	 */
	public void setColumnLinesXList(ArrayList<Integer> xlist) {
		this.columnLineX = xlist;
	}

	/**
	 * Sets the grid on.
	 *
	 * @param gridON the new grid on
	 */
	public void setGridON(boolean gridON) {
		this.gridON = gridON;
	}
	
	/**
	 * Sets the horizontal line length.
	 *
	 * @param l the new horizontal line length
	 */
	public void setHorizontalLineLength(int l) {
		this.horizontalLineLength = l;
	}

	/**
	 * Sets the outer of grid rectangle list.
	 *
	 * @param outerOfGridRectangleList the new outer of grid rectangle list
	 */
	public void setOuterOfGridRectangleList(ArrayList<Rectangle> outerOfGridRectangleList) {
		this.outerOfGridRectangleList = outerOfGridRectangleList;
	}
	
	/**
	 * Sets the positioned rectangle list.
	 *
	 * @param positionedRectangleList the new positioned rectangle list
	 */
	public void setPositionedRectangleList(ArrayList<PositionedRectangle> positionedRectangleList) {
		this.positionedRectangleList = positionedRectangleList;
	}
	
	/**
	 * Sets the present image dimension.
	 *
	 * @param presentImageDimension the new present image dimension
	 */
	public void setPresentImageDimension(Dimension presentImageDimension) {
		this.presentImageDimension = presentImageDimension;

		this.horizontalLineLength=presentImageDimension.width;
		this.verticalLineLength=presentImageDimension.height;
	}

	/**
	 * Sets the random percent.
	 *
	 * @param randomProcent the new random percent
	 */
	public void setRandomPercent(int randomProcent) {
		this.randomPercent = randomProcent;
	}

	/**
	 * Sets the row lines y list.
	 *
	 * @param yList the new row lines y list
	 */
	public void setRowLinesYList(ArrayList<Integer> yList) {
		this.rowLineY = yList;
	}
	
	/**
	 * Sets the vertical line length.
	 *
	 * @param l the new vertical line length
	 */
	public void setVerticalLineLength(int l) {
		this.verticalLineLength = l;
	}

}

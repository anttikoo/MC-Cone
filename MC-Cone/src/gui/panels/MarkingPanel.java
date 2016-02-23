package gui.panels;

import information.ID;
import information.MarkingLayer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.swing.JPanel;
import operators.ShapeDrawer;

/**
 * The Class MarkingPanel. Draws the markings of single MarkingLayer over image. 
 */
public class MarkingPanel extends JPanel {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6331797710957894440L;

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The ID of MarkingLayer. */
	private int id;
	
	/** The paint color. */
	private Color paintColor;
	
	/** The thickness. */
	private float thickness;
	
	/** The shape ID. ID.SHAPE_CIRCLE, ID.SHAPE_CROSS, etc... */
	private int shapeID; 
	
	/** The coordinate list. */
	private ArrayList<Point> coordinateList;
	
	/** The is visible. shows id marking visible. */
	private boolean isVisible=true;
	
	/** The g2d. */
	private Graphics2D g2d;
	
	/** The shape drawer. Used for drawing the marking shapes */
	private ShapeDrawer shapeDrawer;


	/**
	 * Instantiates a new marking panel.
	 *
	 * @param mLayer the MarkingLayer
	 */
	public MarkingPanel(MarkingLayer mLayer){
		try {
			this.setOpaque(false); // layer has to be transparent
			this.id=mLayer.getLayerID();
			this.paintColor=mLayer.getColor();
			this.thickness=mLayer.getThickness();
			mLayer.getOpacity();
			this.shapeID= mLayer.getShapeID();
			mLayer.getSize();
			setCoordinateList(new ArrayList<Point>());
			// set cursor to hair cursor
			this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			this.shapeDrawer=new ShapeDrawer(mLayer, mLayer.getSize(), mLayer.getThickness(), mLayer.getOpacity(), mLayer.getColor());
		} catch (Exception e) {
			LOGGER.severe("Error in initializing MarkingPanel!");
			e.printStackTrace();
		}
	}

	/**
	 *  Calculates closest Point from coordinatelist to given point. Calculates Manhattan distance.
	 *
	 * @param p Point where the list of coordinate Points are compared
	 * @param distanceParameter the distance parameter
	 * @return return the closest point from coordinateList if the distance is small enough: @see information.SharedVariables.DISTANCE_TO_REMOVE.
	 */
	public Point getClosestMarkingPoint(Point p, int distanceParameter){
		try {
			if(isVisible){ // return null if not visible
				int min_distance=Integer.MAX_VALUE;
				Point min_point=null;
				Iterator<Point> pIterator = this.coordinateList.iterator();
				while(pIterator.hasNext()){
					Point i= pIterator.next();
					int x_distance= p.x-i.x;
					if(x_distance <0)
						x_distance*=-1;

					int y_distance= p.y-i.y;
					if(y_distance < 0)
						y_distance*=-1;
					if(x_distance+y_distance < min_distance){
						min_distance=x_distance+y_distance;
						if(min_distance <= distanceParameter)
							min_point=i;
					}
				}
				if(min_point != null)
			//	System.out.println("given point: "+p.x +" " +p.y + " found: " +min_point.x+ " "+ min_point.y);
				return min_point;
				}
			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in MarkingPanel: getting closest Marking Point: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the coordinate list.
	 *
	 * @return the coordinate list
	 * @throws Exception the exception
	 */
	public ArrayList<Point> getCoordinateList() throws Exception {
		return coordinateList;
	}

	/**
	 * Returns the ID of the MarkingLayer of this MarkingPanel.
	 *
	 * @return the id
	 * @throws Exception the exception
	 */
	public int getId()  throws Exception{
		return id;
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#isVisible()
	 */
	public boolean isVisible(){
		return isVisible;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		try {
			if(isVisible){
				g2d = (Graphics2D) g.create();
				g2d.setPaint(this.paintColor);
				g2d.setStroke(new BasicStroke(this.thickness)); // set thickness
				RenderingHints rh= new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHints(rh);
				Iterator<Point> coordinateIterator =this.coordinateList.iterator();

					 switch(this.shapeID)
			         {
			             case ID.SHAPE_OVAL:
				     		while(coordinateIterator.hasNext()){
				     			Point c = coordinateIterator.next();
				     			this.shapeDrawer.drawOval(g2d,c.x, c.y);
				     		}
			                break;
			             case ID.SHAPE_DIAMOND:
				     		while(coordinateIterator.hasNext()){
				     			Point c = coordinateIterator.next();
				     			this.shapeDrawer.drawDiamond(g2d,c.x, c.y);
				     		}
			                 break;
			             case ID.SHAPE_PLUS:
				     		while(coordinateIterator.hasNext()){
				     			Point c = coordinateIterator.next();
				     			this.shapeDrawer.drawPlus(g2d,c.x, c.y);
				     		}
			                 break;
			             case ID.SHAPE_CROSS:
				     		while(coordinateIterator.hasNext()){
				     			Point c = coordinateIterator.next();
				     			this.shapeDrawer.drawCross(g2d,c.x, c.y);
				     		}
			                 break;
			             case ID.SHAPE_SQUARE:
				     		while(coordinateIterator.hasNext()){
				     			Point c = coordinateIterator.next();
				     			this.shapeDrawer.drawSquare(g2d,c.x, c.y);
				     		}
			                break;

			             case ID.SHAPE_TRIANGLE:
					     	while(coordinateIterator.hasNext()){
					     		Point c = coordinateIterator.next();
					     		this.shapeDrawer.drawTriangle(g2d,c.x, c.y);
					     	}
					     	break;
			             default : // circle

				     		while(coordinateIterator.hasNext()){
				     			Point c = coordinateIterator.next();
				     			this.shapeDrawer.drawCircle(g2d,c.x, c.y);
				     		}
			             	break;
			         }
				}

				g2d.dispose();
		} catch (Exception e) {
			LOGGER.severe("Error in painting MarkingPanel!");
			e.printStackTrace();
			if(g2d != null)
				g2d.dispose();
		}

}


	/**
	 * Sets the coordinate list.
	 *
	 * @param coordinateList the new coordinate list
	 * @throws Exception the exception
	 */
	public void setCoordinateList(ArrayList<Point> coordinateList) throws Exception {
		this.coordinateList = coordinateList;
	}

	/**
	 * Sets the marking panel properties.
	 *
	 * @param mLayer the new marking panel properties
	 * @throws Exception the exception
	 */
	public void setMarkingPanelProperties(MarkingLayer mLayer) throws Exception{	
		this.paintColor=mLayer.getColor();
		this.thickness=mLayer.getThickness();
		mLayer.getOpacity();
		this.shapeID= mLayer.getShapeID();
		mLayer.getSize();
		this.shapeDrawer=new ShapeDrawer(mLayer, mLayer.getSize(), mLayer.getThickness(), mLayer.getOpacity(), mLayer.getColor());
	}

	/**
	 * Sets the new cursor.
	 *
	 * @param cursor the new new cursor
	 * @throws Exception the exception
	 */
	public void setNewCursor(Cursor cursor) throws Exception{
		this.setCursor(cursor);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	public void setVisible(boolean isVisible){
		this.isVisible = isVisible;
	}
}

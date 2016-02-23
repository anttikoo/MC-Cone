package operators;

import information.ID;
import information.MarkingLayer;
import information.SharedVariables;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

/**
 * The Class ShapeDrawer. Draws shape to Graphics2D object with given size, color, thickness and opacity.
 */
public class ShapeDrawer {
	
	/** The stroke thick. Thicker stroke. */
	private BasicStroke strokeThick;
	
	/** The stroke thin. Thin stroke. */
	private BasicStroke strokeThin;
	
	/** The rule_alpha. */
	private int rule_alpha;
	
	/** The shape color. */
	private Color shapeColor;
	
	/** The is preview. Is the shape drawn on preview window. */
	private boolean isPreview=false;
	
	/** The thickness. */
	private float thickness;
	
	/** The opacity. */
	private float opacity;
	
	/** The shape ID.  ID.SHAPE_CIRCLE, ID.SHAPE_CROSS, etc... */
	private int shapeID; 
	
	/** The shape size. */
	private int shapeSize;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");


	/**
	 * Instantiates a new shape drawer. Sets the isPreview to true and causes to print the marking without thick lines.
	 *
	 * @param shapeID the shape id
	 * @param size the size
	 * @param thick the thick
	 * @param opa the opa
	 * @param shapeColor the shape color
	 */
	public ShapeDrawer(int shapeID, int size, float thick, float opa, Color shapeColor){
		try {
			this.shapeColor=shapeColor;
			this.rule_alpha=SharedVariables.transparencyModeOVER;
			this.thickness=thick;
			this.opacity= opa;
			this.setShapeID(shapeID);
			this.shapeSize=size;
			this.strokeThin=new BasicStroke(this.thickness);
			this.strokeThick=new BasicStroke(this.thickness*3);
			this.isPreview=true;
		} catch (Exception e) {
			LOGGER.severe("Error in initializing ShapeDrawer!");
			e.printStackTrace();
		}
	}

	/**
	 * Instantiates a new shape drawer.
	 *
	 * @param ml the MarkingLayer
	 * @param size the size of shape
	 * @param thick the thickness of shape
	 * @param opacity the opacity of shape
	 * @param shapeColor the shape color
	 */
	public ShapeDrawer(MarkingLayer ml, int size, int thick, float opacity, Color shapeColor){
		try {
			this.shapeColor=shapeColor;
			this.rule_alpha=SharedVariables.transparencyModeOVER;
			this.thickness=thick;
			this.opacity= opacity;
			this.setShapeID(ml.getShapeID());
			this.shapeSize=size;
			this.strokeThin=new BasicStroke(this.thickness);
			this.strokeThick=new BasicStroke(this.thickness*3);
		} catch (Exception e) {
			LOGGER.severe("Error in initializing ShapeDrawer!");
			e.printStackTrace();
		}

	}
	
	/**
	 * Decreases thickness.
	 *
	 * @throws Exception the exception
	 */
	public void decreaseThickness() throws Exception{
		this.thickness-=2;
		this.strokeThick=new BasicStroke((thickness*3));
		this.strokeThin = new BasicStroke((this.thickness));
	}

	/**
	 * Draws circle.
	 *
	 * @param g2d the Graphics2D object.
	 * @param x the top left horizontal point of shape
	 * @param y the y top left vertical point of shape
	 * @return the Graphics2D object.
	 * @throws Exception the exception
	 */
	public Graphics2D drawCircle(Graphics2D g2d, int x, int y) throws Exception{
		x=(int)((double)x-((double)shapeSize)/2);
		y=(int)((double)y-((double)shapeSize)/2);
		
		g2d.setPaint(this.shapeColor);
		
		 // print thick part only if not showin preview marking style
    	if(!isPreview){ 
			g2d.setStroke(strokeThick); // set thickness bigger
		     g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));
			 // draw transparent thick circle
			 g2d.draw(new Ellipse2D.Double(x, y, shapeSize, shapeSize));	 
    	}
    	
    	//draw thin circle
		 g2d.setStroke(strokeThin);
		 g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
		 g2d.draw(new Ellipse2D.Double(x, y, shapeSize, shapeSize));

		 return g2d;
	}
	
	/**
	 * Draws a cross.
	 *
	 * @param g2d the Graphics2D object.
	 * @param x the top left horizontal point of shape
	 * @param y the y top left vertical point of shape
	 * @return the Graphics2D object.
	 * @throws Exception the exception
	 */
	public Graphics2D drawCross(Graphics2D g2d, int x, int y) throws Exception{
		Point[] cornerPoints = getCornerPoints(x, y);
		Point upleft =cornerPoints[0];
		Point upright = cornerPoints[1];
		Point downright = cornerPoints[2];
		Point downleft = cornerPoints[3];


	 

	     GeneralPath polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4);

	    	polyline.moveTo (upleft.x, upleft.y);
	    	polyline.lineTo(downright.x, downright.y);
	    	polyline.moveTo (downleft.x, downleft.y);
	    	polyline.lineTo(upright.x, upright.y);
   	
	    	g2d.setPaint(this.shapeColor);
	    	
	    	// print thick part only if not showin preview marking style
	    	if(!isPreview){  
		    	g2d.setStroke(strokeThick); // set thickness bigger  
			    g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));	    	
		    	// draw the line
		    	g2d.draw(polyline);
	    	}
			g2d.setStroke(strokeThin);
			g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
			g2d.draw(polyline);

			return g2d;
	}
	
	/**
	 * Draw diamond.
	 *
	 * @param g2d the Graphics2D object.
	 * @param x the top left horizontal point of shape
	 * @param y the y top left vertical point of shape
	 * @return the Graphics2D object.
	 * @throws Exception the exception
	 */
	public Graphics2D drawDiamond(Graphics2D g2d, int x, int y) throws Exception{

		Point[] middlePoints = getMiddlePoints(x, y);
		Point up =middlePoints[0];
		Point right = middlePoints[1];
		Point down = middlePoints[2];
		Point left = middlePoints[3];

		GeneralPath polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4);

    	polyline.moveTo (up.x, up.y);
    	polyline.lineTo(right.x, right.y);
    	polyline.lineTo(down.x, down.y);
    	polyline.lineTo(left.x, left.y);
    	polyline.lineTo(up.x, up.y);
   	
    	g2d.setPaint(this.shapeColor);
    	
    	// print thick part only if not showin preview marking style
    	if(!isPreview){    	
	    	g2d.setStroke(strokeThick); // set thickness bigger
	    	g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));
	    	// draw the line
	    	g2d.draw(polyline);
    	}

		g2d.setStroke(strokeThin);
		g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
		g2d.draw(polyline);

		return g2d;


	}

	/**
	 * Draws a oval.
	 *
	 * @param g2d the Graphics2D object.
	 * @param x the top left horizontal point of shape
	 * @param y the y top left vertical point of shape
	 * @return the Graphics2D object.
	 * @throws Exception the exception
	 */
	public Graphics2D drawOval(Graphics2D g2d, int x, int y) throws Exception{
		x=(int)((double)x-((double)shapeSize)/2);
		y=(int)((double)y-((double)shapeSize)/2);
		
		g2d.setPaint(this.shapeColor);
		
		// print thick part only if not showin preview marking style
    	if(!isPreview){  
			g2d.setStroke(strokeThick); // set thickness bigger
		    g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));		    
		    // draw thick ellipse
			g2d.draw(new Ellipse2D.Double(x, y+shapeSize*0.2, shapeSize, shapeSize*0.6));
    	}
    	
		g2d.setStroke(strokeThin);
		g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
		// draw transparent thin rectangle
		g2d.draw(new Ellipse2D.Double(x, y+shapeSize*0.2, shapeSize, shapeSize*0.6));

		return g2d;
	}
	
	
	/**
	 * Draws a plus.
	 *
	 * @param g2d the Graphics2D object.
	 * @param x the top left horizontal point of shape
	 * @param y the y top left vertical point of shape
	 * @return the Graphics2D object.
	 * @throws Exception the exception
	 */
	public Graphics2D drawPlus(Graphics2D g2d, int x, int y) throws Exception{
		Point[] middlePoints = getMiddlePoints(x, y);
		Point up =middlePoints[0];
		Point right = middlePoints[1];
		Point down = middlePoints[2];
		Point left = middlePoints[3];


	 g2d.setStroke(strokeThick); // set thickness bigger

	

	     GeneralPath polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4);

	    	polyline.moveTo (up.x, up.y);
	    	polyline.lineTo(down.x, down.y);
	    	polyline.moveTo (left.x, left.y);
	    	polyline.lineTo(right.x, right.y);

	    	g2d.setPaint(this.shapeColor);
	    	
	    	// print thick part only if not showin preview marking style
	    	if(!isPreview){  
	    	    g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));
	    	    g2d.setStroke(strokeThick);
	    	    // draw the line
	 	    	g2d.draw(polyline);
	    	}

			g2d.setStroke(strokeThin);
			g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
			g2d.draw(polyline);

			return g2d;

	}

	/**
	 * Draw shape.
	 *
	 * @param g2d the g2d
	 * @param x the x
	 * @param y the y
	 * @return the graphics2 d
	 * @throws Exception the exception
	 */
	public Graphics2D drawShape(Graphics2D g2d, int x, int y) throws Exception{
		switch (shapeID){
			case ID.SHAPE_CIRCLE:
				return drawCircle(g2d,x, y);
				
			case ID.SHAPE_CROSS:
				return drawCross(g2d,x, y);
				
			case ID.SHAPE_DIAMOND:
				return drawDiamond(g2d,x, y);	
				
			case ID.SHAPE_OVAL:
				return drawOval(g2d,x, y);	
				
			case ID.SHAPE_PLUS:
				return drawPlus(g2d,x, y);	
				
			case ID.SHAPE_SQUARE:
				return drawSquare(g2d,x, y);
				
			case ID.SHAPE_TRIANGLE:
				return drawTriangle(g2d,x, y);	
					
		}	
		return g2d; // should newer be reached here
	}

	/**
	 * Draws a square.
	 *
	 * @param g2d the Graphics2D object.
	 * @param x the top left horizontal point of shape
	 * @param y the y top left vertical point of shape
	 * @return the Graphics2D object.
	 * @throws Exception the exception
	 */
	public Graphics2D drawSquare(Graphics2D g2d, int x, int y) throws Exception{
		x=(int)((double)x-((double)shapeSize)/2);
		y=(int)((double)y-((double)shapeSize)/2);

		
	    g2d.setPaint(this.shapeColor);
	 // print thick part only if not showin preview marking style
    	if(!isPreview){ 
		    // draw thick rectangle
		    g2d.draw(new Rectangle2D.Double(x, y, shapeSize, shapeSize));
			g2d.setStroke(strokeThick); // set thickness bigger
		    g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));
		    g2d.draw(new Rectangle2D.Double(x, y, shapeSize, shapeSize));
    	}

		g2d.setStroke(strokeThin);
		g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
		// draw transparent thin rectangle
		g2d.draw(new Rectangle2D.Double(x, y, shapeSize, shapeSize));

		return g2d;
	}
	
	/**
	 * Draws triangle.
	 *
	 * @param g2d the Graphics2D object.
	 * @param x the top left horizontal point of shape
	 * @param y the y top left vertical point of shape
	 * @return the Graphics2D object.
	 * @throws Exception the exception
	 */
	public Graphics2D drawTriangle(Graphics2D g2d, int x, int y) throws Exception{
		Point up = new Point((x),(int)((double)y-((double)shapeSize)/2));
		Point downright = new Point((int)((double)x+((double)shapeSize)/2),(int)((double)y+((double)shapeSize)/2));
		Point downleft = new Point((int)((double)x-((double)shapeSize)/2),(int)((double)y+((double)shapeSize)/2));

		g2d.setStroke(strokeThick); // set thickness bigger
		
	    GeneralPath polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4);

	    	polyline.moveTo (up.x, up.y);
	    	polyline.lineTo(downright.x, downright.y);
	    	polyline.lineTo(downleft.x, downleft.y);
	    	polyline.closePath();
	    	g2d.setPaint(this.shapeColor);
	    	
	    	
			 // print thick part only if not showin preview marking style
	    	if(!isPreview){ 
		    	g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));
		    	g2d.setStroke(strokeThick);
		    	// draw the line
		    	g2d.draw(polyline);
	    	}
			g2d.setStroke(strokeThin);
			g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
			g2d.draw(polyline);

			return g2d;
	}

	/**
	 * Returns the corner points.
	 *
	 * @param x the horizontal position
	 * @param y the vertical position
	 * @return the corner points
	 * @throws Exception the exception
	 */
	private Point[] getCornerPoints(int x,int y) throws Exception{
		Point upLeft = new Point((int)((double)x-((double)shapeSize)/2),(int)((double)y-((double)shapeSize)/2));
		Point upright = new Point((int)((double)x+((double)shapeSize)/2),(int)((double)y-((double)shapeSize)/2));
		Point downright = new Point((int)((double)x+((double)shapeSize)/2),(int)((double)y+((double)shapeSize)/2));
		Point downleft = new Point((int)((double)x-((double)shapeSize)/2),(int)((double)y+((double)shapeSize)/2));
		return new Point[]{upLeft,upright,downright,downleft};
	}
	
	/**
	 * Returns the middle point by given positions and shape size.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the middle points
	 * @throws Exception the exception
	 */
	private Point[] getMiddlePoints(int x,int y) throws Exception{
		Point up = new Point((x),(int)((double)y-((double)shapeSize)/2));
		Point right = new Point((int)((double)x+((double)shapeSize)/2),y);
		Point down = new Point((x),(int)((double)y+((double)shapeSize)/2));
		Point left = new Point((int)((double)x-((double)shapeSize)/2),y);
		return new Point[]{up,right,down,left};
	}

	/**
	 * Returns the opacity.
	 *
	 * @return the opacity
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * Returns the rule_alpha. Used for AlphaComposite.
	 *
	 * @return the rule_alpha
	 */
	public int getRule_alpha() {
		return rule_alpha;
	}

	/**
	 * Returns the shape color.
	 *
	 * @return the shape color
	 */
	public Color getShapeColor() {
		return shapeColor;
	}
	
	

	/**
	 * Returns the shape id.
	 *
	 * @return the shape id
	 */
	public int getShapeID() {
		return shapeID;
	}

	/**
	 * Returns the shape size.
	 *
	 * @return the shape size
	 */
	public int getShapeSize() {
		return shapeSize;
	}

	/**
	 * Returns the thickness.
	 *
	 * @return the thickness
	 */
	public float getThickness() {
		return thickness;
	}

	/**
	 * Increasse thickness.
	 *
	 * @throws Exception the exception
	 */
	public void increaseThickness() throws Exception{
		this.thickness+=2;
		this.strokeThick=new BasicStroke((thickness*3));
		this.strokeThin = new BasicStroke((this.thickness));
	}


	/**
	 * Sets the opacity.
	 *
	 * @param opacity the new opacity
	 */
	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	/**
	 * Sets the rule_alpha for AlphaComposite.
	 *
	 * @param rule_alpha the new rule_alpha
	 */
	public void setRule_alpha(int rule_alpha) {
		this.rule_alpha = rule_alpha;
	}

	/**
	 * Sets the shape color.
	 *
	 * @param shapeColor the new shape color
	 */
	public void setShapeColor(Color shapeColor) {
		this.shapeColor = shapeColor;
	}

	/**
	 * Sets the shape id.
	 *
	 * @param shapeID the new shape id
	 */
	public void setShapeID(int shapeID) {
		this.shapeID = shapeID;
	}
	
	/**
	 * Sets the shape size.
	 *
	 * @param shapeSize the new shape size
	 */
	public void setShapeSize(int shapeSize) {
		this.shapeSize = shapeSize;
	}

	/**
	 * Sets the thickness.
	 *
	 * @param thickness the new thickness
	 * @throws Exception the exception
	 */
	public void setThickness(float thickness) throws Exception {
		this.thickness = thickness;
		this.strokeThin=new BasicStroke(this.thickness);
		this.strokeThick=new BasicStroke(this.thickness*3);
	}
}

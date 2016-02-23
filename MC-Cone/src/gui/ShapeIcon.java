package gui;

import information.ID;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import javax.swing.Icon;

/**
 * The Class ShapeIcon. Marking shape is painted to icon with given color, shape, etc.
 */
public class ShapeIcon implements Icon {
	
	/** The shape type. */
	private int shapeType;
	
	/** The width. */
	private int width;
	
	/** The height. */
	private int height;
	
	/** The color. */
	private Color color;
	
	/** The opacity. */
	private float opacity;
	
	/** The stroke big. */
	private  BasicStroke strokeBig;
    
    /** The stroke thin. */
    private BasicStroke strokeThin;
    
    /** The rule_alpha for dimming graphics with Alphacomposite. */
    private int rule_alpha;
    
    /** The background color. */
    private Color backgroundColor;
    
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/**
	 * Instantiates a new shape icon.
	 *
	 * @param type the type
	 */
	public ShapeIcon(int type){
		super();
		this.setShapeType(type);
	}

	/**
	 * Instantiates a new shape icon.
	 *
	 * @param type the type
	 * @param width the width
	 * @param height the height
	 * @param color the color
	 * @param bg the bg
	 */
	public ShapeIcon(int type, int width, int height, Color color, Color bg){
		super();
		try {
			this.setShapeType(type);
			this.setWidth(width);
			this.setHeight(height);
			this.setColor(color);
			this.opacity= 1.0f; // by now the opacity parts can't be painted right
			strokeBig = new BasicStroke(3.0f);
			strokeThin= new BasicStroke(1.0f);
			this.backgroundColor=bg;
		} catch (Exception e) {
			LOGGER.severe("Error in initializing shape icon!");
			e.printStackTrace();
		}


	}


	/* Paints the icon depending on type of shape.
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = null;
		
		try {
			g2d = (Graphics2D) g.create();
			
			rule_alpha=AlphaComposite.SRC_OVER;
			int rule_back = AlphaComposite.SRC;
			
			// set thickness
			g2d.setStroke(strokeThin); 

			x = 3; // set margin
			y = 3;
			int shapeWidth=getIconWidth()-6;
			int shapeHeight = getIconHeight()-6;

			g2d.setPaint(this.backgroundColor);
			g2d.setComposite(AlphaComposite.getInstance(rule_back, this.opacity));
			g2d.fillRect(0,0 , getIconWidth(), getIconHeight());
			g2d.setPaint(this.getColor()); // set color

			switch(this.shapeType)
			{
			    case ID.SHAPE_OVAL:
			        drawOval(x, y, shapeWidth, shapeHeight, g2d);
			       break;
			    case ID.SHAPE_DIAMOND:
			        //s = new Ellipse2D.Double(x, y, shapeWidth, shapeHeight);
			    	drawDiamond(x, y, shapeWidth, shapeHeight, g2d);
			        break;
			    case ID.SHAPE_PLUS:
			        drawPlus(x, y, shapeWidth, shapeHeight, g2d);
			        break;
			    case ID.SHAPE_CROSS:
			        drawCross(x, y, shapeWidth, shapeHeight, g2d);
			        break;
			    case ID.SHAPE_SQUARE:
			    	//  s = new Rectangle2D.Double(x, y, shapeWidth, shapeHeight);
			    	drawSquare(x, y, shapeWidth, shapeHeight, g2d);
			       break;
			    case ID.SHAPE_TRIANGLE:
			    	//  s = new Rectangle2D.Double(x, y, shapeWidth, shapeHeight);
			    	drawTriangle(x, y, shapeWidth, shapeHeight, g2d);
			       break;

			    default : // circle
			       // s = new Ellipse2D.Double(x, y, shapeWidth, shapeHeight);
			    	drawCircle(x, y, shapeWidth, shapeHeight, g2d);
			    	break;
			}

			g2d.dispose();
		} catch (Exception e) {
			LOGGER.severe("Error in !");
			e.printStackTrace();
			if(g2d != null)
				g2d.dispose();
		}
	}

	
	/**
	 * Draws a diamond.
	 *
	 * @param x the x
	 * @param y the y
	 * @param shapeWidth the shape width
	 * @param shapeHeight the shape height
	 * @param g2d the g2d
	 * @throws Exception the exception
	 */
	private void drawDiamond(int x, int y, int shapeWidth, int shapeHeight ,Graphics2D g2d) throws Exception{
		Point up = new Point((int)shapeWidth/2+x,y);
		Point right = new Point(shapeWidth+x,(int)shapeHeight/2+y);
		Point down = new Point((int)shapeWidth/2+x, shapeHeight+y);
		Point left = new Point(x,(int)shapeHeight/2+y);


	    g2d.setStroke(strokeBig); // set thickness bigger

        g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
        // draw transparent fat circle
		g2d.draw(new Line2D.Double(up, right));
		g2d.draw(new Line2D.Double(right, down));
		g2d.draw(new Line2D.Double(down, left));
		g2d.draw(new Line2D.Double(left, up));

		g2d.setStroke(strokeThin);
		 g2d.setComposite(AlphaComposite.getInstance(rule_alpha, 1.0f));
		g2d.draw(new Line2D.Double(up, right));
		g2d.draw(new Line2D.Double(right, down));
		g2d.draw(new Line2D.Double(down, left));
		g2d.draw(new Line2D.Double(left, up));
	}

	/**
	 * Draws a plus.
	 *
	 * @param x the x
	 * @param y the y
	 * @param shapeWidth the shape width
	 * @param shapeHeight the shape height
	 * @param g2d the g2d
	 * @throws Exception the exception
	 */
	private void drawPlus(int x, int y, int shapeWidth, int shapeHeight ,Graphics2D g2d) throws Exception{
		Point up = new Point((int)shapeWidth/2+x,y);
		Point right = new Point(shapeWidth+x,(int)shapeHeight/2+y);
		Point down = new Point((int)shapeWidth/2+x, shapeHeight+y);
		Point left = new Point(x,(int)shapeHeight/2+y);
	    g2d.setStroke(strokeBig); // set thickness bigger
        g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
        // draw transparent fat circle
    	g2d.draw(new Line2D.Double(up,down));
		g2d.draw(new Line2D.Double(right, left));

		g2d.setComposite(AlphaComposite.getInstance(rule_alpha, 1.0f));
		g2d.setStroke(strokeThin);
		g2d.draw(new Line2D.Double(up,down));
		g2d.draw(new Line2D.Double(right, left));

	}

	/**
	 * Draws a cross.
	 *
	 * @param x the x
	 * @param y the y
	 * @param shapeWidth the shape width
	 * @param shapeHeight the shape height
	 * @param g2d the g2d
	 * @throws Exception the exception
	 */
	private void drawCross(int x, int y, int shapeWidth, int shapeHeight ,Graphics2D g2d) throws Exception{
		Point upleft = new Point(x,y);
		Point upright = new Point(shapeWidth+x,y);
		Point downleft = new Point(x, shapeHeight+y);
		Point downright = new Point(shapeWidth+x,shapeHeight+y);
		g2d.setStroke(strokeThin);
		g2d.draw(new Line2D.Double(upleft, downright));
		g2d.draw(new Line2D.Double(upright, downleft));

	    g2d.setStroke(strokeBig); // set thickness bigger
        g2d.setComposite(AlphaComposite.getInstance(this.rule_alpha, this.opacity));
        // draw transparent fat circle
		g2d.draw(new Line2D.Double(upleft, downright));
		g2d.draw(new Line2D.Double(upright, downleft));
	}

	/**
	 * Draws an oval.
	 *
	 * @param x the x
	 * @param y the y
	 * @param shapeWidth the shape width
	 * @param shapeHeight the shape height
	 * @param g2d the g2d
	 * @throws Exception the exception
	 */
	private void drawOval(int x, int y, int shapeWidth, int shapeHeight ,Graphics2D g2d) throws Exception{

		g2d.draw(new Ellipse2D.Double(x, y+shapeHeight*0.2, shapeWidth, shapeHeight*0.6));
	    g2d.setStroke(strokeBig); // set thickness bigger
        g2d.setComposite(AlphaComposite.getInstance(this.rule_alpha, this.opacity));
        // draw transparent fat rectangle
        g2d.draw(new Ellipse2D.Double(x, y+shapeHeight*0.2, shapeWidth, shapeHeight*0.6));
	}

	/**
	 * Draws a square.
	 *
	 * @param x the x
	 * @param y the y
	 * @param shapeWidth the shape width
	 * @param shapeHeight the shape height
	 * @param g2d the g2d
	 * @throws Exception the exception
	 */
	private void drawSquare(int x, int y, int shapeWidth, int shapeHeight ,Graphics2D g2d) throws Exception{

		// draw thin rectangle
        g2d.draw(new Rectangle2D.Double(x, y, shapeWidth, shapeHeight));
	    g2d.setStroke(strokeBig); // set thickness bigger
        g2d.setComposite(AlphaComposite.getInstance(this.rule_alpha, this.opacity));
        // draw transparent fat rectangle
        g2d.draw(new Rectangle2D.Double(x, y, shapeWidth, shapeHeight));
	}

	/**
	 * Draws a circle.
	 *
	 * @param x the x
	 * @param y the y
	 * @param shapeWidth the shape width
	 * @param shapeHeight the shape height
	 * @param g2d the g2d
	 * @throws Exception the exception
	 */
	private void drawCircle(int x, int y, int shapeWidth, int shapeHeight ,Graphics2D g2d) throws Exception{


		// draw transparent thick circle
	    g2d.setStroke(strokeBig); // set thickness bigger
        g2d.setComposite(AlphaComposite.getInstance(this.rule_alpha, this.opacity));

        g2d.draw(new Ellipse2D.Double(x, y, shapeWidth, shapeHeight));
      //draw thin circle

        g2d.setStroke(strokeThin);
        g2d.setComposite(AlphaComposite.getInstance(rule_alpha, 1.0f));
        g2d.draw(new Ellipse2D.Double(x, y, shapeWidth, shapeHeight));
	}

	/**
	 * Draws a triangle.
	 *
	 * @param x the x
	 * @param y the y
	 * @param shapeWidth the shape width
	 * @param shapeHeight the shape height
	 * @param g2d the g2d
	 */
	private void drawTriangle(int x, int y, int shapeWidth, int shapeHeight ,Graphics2D g2d){
		Point up = new Point((int)((double)x+((double)shapeWidth)/2),y);
		Point downright = new Point((int)((double)x+((double)shapeWidth)),(int)((double)y+((double)shapeHeight)));
		Point downleft = new Point(x,(int)((double)y+((double)shapeHeight)));

		g2d.setStroke(strokeBig); // set thickness bigger

		g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));

		GeneralPath polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4);

    	polyline.moveTo (up.x, up.y);
    	polyline.lineTo(downright.x, downright.y);

    	polyline.lineTo(downleft.x, downleft.y);
    	polyline.closePath();

    	// draw the line
    	g2d.draw(polyline);

		g2d.setStroke(strokeThin);
		g2d.setComposite(AlphaComposite.getInstance(rule_alpha, 1.0f));
		g2d.draw(polyline);;
	}


	@Override
	public int getIconWidth() {
		
			return this.width;
		
	}

	@Override
	public int getIconHeight() {	
		return this.height;
	}



	/**
	 * Sets the width of icon.
	 *
	 * @param width the new width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Gets the shape type.
	 *
	 * @return the shape type
	 */
	public int getShapeType() {
		return shapeType;
	}

	/**
	 * Sets the shape type.
	 *
	 * @param shapeType the new shape type
	 */
	public void setShapeType(int shapeType) {
		this.shapeType = shapeType;
	}


	/**
	 * Sets the height of icon.
	 *
	 * @param height the new height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Gets the color of shape.
	 *
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the color.
	 *
	 * @param color the new color
	 */
	public void setColor(Color color) {
		this.color = color;
	}



}

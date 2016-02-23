package operators;


import information.ID;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class MarkingShape extends JPanel {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8257666570165902871L;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/** The shape type. */
	private int shapeType=-1;
	
	/** The horizontal position x. */
	private int x;
	
	/** The vertical position y. */
	private int y;
	
	/** The width. */
	private int width;
	
	/** The height. */
	private int height;
	
	/** The color. */
	private Color color;
	
	/** The transparency. */
	private float transparency;
	
	/** The thickness. */
	private float thickness;
	
	public MarkingShape(int type){
		super();
		this.setShapeType(type);	
	}
	
	public MarkingShape(int type, int width, int height, Color color, 
								float transparency, float thick, int x, int y){
		super();
		try {
			this.setShapeType(type);
			this.setWidth(width);
			this.setHeight(height);
			this.setColor(color);
			this.transparency=transparency;
			this.thickness=thick;
			this.x=x;
			this.y=y;
			this.setSize(this.getWidth()+2, this.getHeight()+2);
			this.setLayout(null);
			this.setBorder(BorderFactory.createEmptyBorder());
		} catch (Exception e) {
			LOGGER.severe("Error in initializing MarkingShape!");
			e.printStackTrace();
		}
		
			
	}

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = null;
        
        try {
			g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.transparency)); // set transparency
			g2.setPaint(this.getColor()); // set color
			BasicStroke stroke = new BasicStroke(this.thickness);
			g2.setStroke(stroke); // set thickness

			
      
			    x = 2; // set margin
			    y = 2;
			    Shape s = null;
			    switch(this.shapeType)
			    {
			        case ID.SHAPE_OVAL:
			            s = new Ellipse2D.Double(x, y+getHeight()*0.2, getWidth()-4, getHeight()*0.6-4);
			            break;
			        case ID.SHAPE_DIAMOND:
			            s = new Ellipse2D.Double(x, y, getWidth()-4, getHeight()-4);
			            break;
			        case ID.SHAPE_PLUS:
			            s = new Ellipse2D.Double(x, y, getWidth()-4, getHeight()-4);
			            break;
			        case ID.SHAPE_CROSS:
			            s = new Ellipse2D.Double(x, y, getWidth()-4, getHeight()-4);
			            break;
			        case ID.SHAPE_SQUARE:
			            s = new Rectangle2D.Double(x, y, getWidth()-4, getHeight()-4);
			            break;
			            
			        default : // circle
			            s = new Ellipse2D.Double(x, y, getWidth()-4, getHeight()-4);
			            break;
			    }
			    if(shapeType > -1)
			    
			        g2.draw(s);
			    g2.dispose();
		} catch (Exception e) {
			LOGGER.severe("Error in painting shape!");
			e.printStackTrace();
			if(g2 != null)
				g2.dispose();
		}
        
    }

	public int getWidth(){
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getShapeType() {
		return shapeType;
	}

	public void setShapeType(int shapeType) {
		this.shapeType = shapeType;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}

package gui.panels;

import gui.Color_schema;
import information.ID;
import information.MarkingLayer;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.logging.Logger;

import javax.swing.JPanel;
import operators.ShapeDrawer;




/**
 * The Class HighlightPanel. Draws highlight shape on shape when mouse hovered over.
 */
public class HighlightPanel extends JPanel{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -9164935656538356196L;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/** The highlighted point. */
	private Point highlightPoint;
	
	/** The thickness. */
	private float thickness;
	
	/** The opacity. */
	private float opacity;
	
	/** The shape id. ID.SHAPE_CIRCLE, ID.SHAPE_CROSS, etc... */
	private int shapeID;
	
	/** The Graphics2D object. */
	private Graphics2D g2d;
	
	/** The shape drawer. */
	private ShapeDrawer shapeDrawer=null;

	
	private boolean isVisible=true;
	
	/**
	 * Instantiates a new highlight panel.
	 */
	public HighlightPanel() {
		this.opacity= 0.7f;
		this.setOpaque(false);
		
	}

	/**
	 * Sets the MarkingLayer, which markings are viewed.
	 *
	 * @param layer the new layer
	 */
	public void setLayer(MarkingLayer layer){
		try {
			if(layer != null){
				this.shapeDrawer=new ShapeDrawer(layer, layer.getSize()+2, layer.getThickness(),this.opacity, layer.getColor());
				this.shapeID=layer.getShapeID();
				this.isVisible=layer.isVisible();
			}
			else{
				this.shapeDrawer=null;
				this.shapeID=ID.UNDEFINED;
				this.isVisible=false;
			}
		} catch (Exception e) {
			LOGGER.severe("Error in setting MarkingLayer on HighlighPanel!");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g){
		
		super.paintComponent(g);
		
		try {
			if(this.highlightPoint != null && this.shapeDrawer !=null && this.isVisible){
			//	System.out.println("drawing!!!");
				g2d = (Graphics2D) g.create();
				g2d.setPaint(Color_schema.orange_medium);
				g2d.setStroke(new BasicStroke(this.thickness*5)); // set thickness
				RenderingHints rh= new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHints(rh);


					 switch(this.shapeID)
			         {
			             case ID.SHAPE_OVAL:
				     			this.shapeDrawer.drawOval(g2d,highlightPoint.x, highlightPoint.y);
				     			g2d.setPaint(Color_schema.white_230);
				     			this.shapeDrawer.increaseThickness();
				     			this.shapeDrawer.drawOval(g2d,highlightPoint.x, highlightPoint.y);
			                break;
			             case ID.SHAPE_DIAMOND:


				     			this.shapeDrawer.drawDiamond(g2d,highlightPoint.x, highlightPoint.y);
				     			g2d.setPaint(Color_schema.white_230);
				     			this.shapeDrawer.increaseThickness();
				     			this.shapeDrawer.drawDiamond(g2d,highlightPoint.x, highlightPoint.y);

			                 break;
			             case ID.SHAPE_PLUS:
				     			this.shapeDrawer.drawPlus(g2d,highlightPoint.x, highlightPoint.y);
				     			g2d.setPaint(Color_schema.white_230);
				     			this.shapeDrawer.increaseThickness();
				     			this.shapeDrawer.drawPlus(g2d,highlightPoint.x, highlightPoint.y);

			                 break;
			             case ID.SHAPE_CROSS:
				     			this.shapeDrawer.drawCross(g2d,highlightPoint.x, highlightPoint.y);
				     			g2d.setPaint(Color_schema.white_230);
				     			this.shapeDrawer.increaseThickness();
				     			this.shapeDrawer.drawCross(g2d,highlightPoint.x, highlightPoint.y);
			                 break;

			             case ID.SHAPE_SQUARE:
				     			this.shapeDrawer.drawSquare(g2d,highlightPoint.x, highlightPoint.y);
				     			g2d.setPaint(Color_schema.white_230);
				     			this.shapeDrawer.increaseThickness();
				     			this.shapeDrawer.drawSquare(g2d,highlightPoint.x, highlightPoint.y);
			                break;

			             case ID.SHAPE_TRIANGLE:
					     		this.shapeDrawer.drawTriangle(g2d,highlightPoint.x, highlightPoint.y);
					     		g2d.setPaint(Color_schema.white_230);
				     			this.shapeDrawer.increaseThickness();
				     			this.shapeDrawer.drawTriangle(g2d,highlightPoint.x, highlightPoint.y);
					     	break;

			             default : // circle
				     			this.shapeDrawer.drawCircle(g2d,highlightPoint.x, highlightPoint.y);
				     			g2d.setPaint(Color_schema.white_230);
				     			this.shapeDrawer.increaseThickness();
				     			this.shapeDrawer.drawCircle(g2d,highlightPoint.x, highlightPoint.y);
			             	break;
			         }

					 this.shapeDrawer.decreaseThickness();
				g2d.dispose();
			}
		} catch (Exception e) {
			LOGGER.severe("Error in painting HighlightPanel!");
			e.printStackTrace();
			if(g2d != null)
				g2d.dispose();
		}
}


	/**
	 * Updates highlighted point.
	 *
	 * @param highlightPoint the highlight Point
	 * @throws Exception the exception
	 */
	public void updateHighlightPoint(Point highlightPoint) throws Exception{

		this.highlightPoint = highlightPoint;

	}


}

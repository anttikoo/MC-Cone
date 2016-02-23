package gui;

import information.SharedVariables;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.logging.Logger;

import javax.swing.JPanel;
import operators.ShapeDrawer;

/**
 * The Class PreviewShapePanel. Draws a preview of style of marking to left of given parent position. 
 */
public class PreviewShapePanel extends JPanel{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7121873037752670757L;

	/** The shape drawer. Draws the shape of marking*/
	private ShapeDrawer shapeDrawer;
	
	/** The Rectangle of the visible panel. */
	private Rectangle recOfBackPanel;
	
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/**
	 * Instantiates a new preview shape panel. Height will be taken from given Rectangle recOfBackPanel.
	 *
	 * @param thickness the thickness of the shape
	 * @param opacity the opacity of the shape
	 * @param shapeID the id of the shape
	 * @param shapeSize the size of the shape
	 * @param color the color of the shape
	 * @param recOfBackPanel the Rectangle of visible dialog
	 * @param recOfVisibleWindow the Rectangle of the whole visible window
	 */
	public PreviewShapePanel(float thickness, float opacity, int shapeID, int shapeSize, Color color, Rectangle recOfBackPanel, Rectangle recOfVisibleWindow){		
		try {
			this.setOpaque(false); // layer has to be transparent
			this.setBackground(new Color(0,0,0,0));
			
			this.recOfBackPanel=recOfBackPanel;
			//setPanelBounds(this.recOfBackPanel);	
			shapeDrawer=new ShapeDrawer(shapeID, shapeSize, thickness, opacity, color);
		} catch (Exception e) {
			LOGGER.severe("Error in initializing Preview panel to marking!");
			e.printStackTrace();
		}
	
		
	}
	
	/**
	 * Sets the Panel Bounds.
	 *
	 * @param backPanelRectangle the new Bounds of panel
	 * @throws Exception the exception
	 */
	public void setPanelBounds(Rectangle backPanelRectangle) throws Exception{
		this.setBounds(new Rectangle((int)backPanelRectangle.getX()-300, (int)backPanelRectangle.getY(), 300,(int)this.recOfBackPanel.getHeight()));		
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = null;
		try {
			g2d = (Graphics2D) g.create();
			g2d.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER, 1.0F));			// THIS WORKING IN LINUX	
			g2d.setPaint(Color_schema.dark_30);
			g2d.setStroke(new BasicStroke(shapeDrawer.getThickness())); // set thickness
			RenderingHints rh= new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHints(rh);
			this.shapeDrawer.drawShape(g2d,this.shapeDrawer.getShapeSize()+50, this.shapeDrawer.getShapeSize()+50);
			
			g2d.dispose();
		} catch (Exception e) {
			LOGGER.severe("Error in painting Preview panel!");
			e.printStackTrace();
			if(g2d != null)
				g2d.dispose();
		}
		
	}
	
	/**
	 * Sets the shape thickness.
	 *
	 * @param thickness Float the new shape thickness
	 * @throws Exception the exception
	 */
	public void setShapeThickness(float thickness) throws Exception{
		this.shapeDrawer.setThickness(thickness);
	}
	
	/**
	 * Sets the shape size.
	 *
	 * @param size the new shape size
	 * @throws Exception the exception
	 */
	public void setShapeSize(int size) throws Exception{
		this.shapeDrawer.setShapeSize(size);
	}
	
	/**
	 * Sets the shape opacity.
	 *
	 * @param opacity the new shape opacity
	 * @throws Exception the exception
	 */
	public void setShapeOpacity(float opacity) throws Exception{
		this.shapeDrawer.setOpacity(opacity);
	}
	
	/**
	 * Sets the shape id.
	 *
	 * @param shapeID the new shape id
	 * @throws Exception the exception
	 */
	public void setShapeID(int shapeID) throws Exception{
		this.shapeDrawer.setShapeID(shapeID);
	}
	
	/**
	 * Sets the shape color.
	 *
	 * @param c the new shape color
	 * @throws Exception the exception
	 */
	public void setShapeColor(Color c) throws Exception{
		this.shapeDrawer.setShapeColor(c);
	}
}

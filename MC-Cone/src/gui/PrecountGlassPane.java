package gui;


import information.ID;
import information.SharedVariables;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;


/**
 * The Class PrecountGlassPane is used to show the precounting selector. 
 * Selector contains circle where the cell should be fitted in and around the circle is a rectangle which shouldn't contain other cells.
 */
public class PrecountGlassPane extends JComponent{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4401382058309328948L;

	/** The rectangle size. The size of the rectangle used in picking cell. */
	private int rectangleSize=100;
	
	/** The double panel. Source from GUI */
	private JSplitPane doublePanel;
	
	/** The center point. */
	private Point centerPoint=null;
	
	/** The menubar. Source from GUI. */
	private JMenuBar menubar;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/**
	 * Instantiates a new precount glass pane.
	 *
	 * @param gui the GUI main window
	 * @param doublePanel the double panel of mainwindow 
	 * @param menubar the menubar of main window
	 * @param guiListener the listener of GUI
	 */
	public PrecountGlassPane(GUI gui, JSplitPane doublePanel, JMenuBar menubar, GUIListener guiListener){
		try {
			this.doublePanel=doublePanel;
			this.menubar=menubar;
			this.setBackground(Color_schema.dark_50);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			this.setOpaque(false);

			//add listeners
			addMouseListener(guiListener);
			addMouseMotionListener(guiListener);
			addMouseWheelListener(guiListener);
			guiListener.addKeyInputMap(this, ID.GLASS_PANE);
		} catch (Exception e) {
			LOGGER.severe("Error in initializing precounting components!");
			e.printStackTrace();
		}

	}

	 /**
 	 * Determine right panel size and location.
 	 *
 	 * @return the rectangle Rectangle containing position and size of right panel (Info of ImageLayers).
 	 * @throws Exception the exception
 	 */
 	private Rectangle determineRightPanelBounds() throws Exception{
		 Point dp = doublePanel.getLocation();
		 dp.setLocation(dp.getLocation().x+doublePanel.getDividerLocation(),dp.getLocation().y+menubar.getHeight());
		 return new Rectangle(dp, new Dimension(doublePanel.getRightComponent().getWidth()+ doublePanel.getDividerSize(), doublePanel.getHeight()));
	 }

	 /**
 	 * Gets the center point.
 	 *
 	 * @return the center point
 	 * @throws Exception the exception
 	 */
	public Point getCenterPoint() throws Exception{
		return centerPoint;
	}


	/**
	 * Gets the rectangle size.
	 *
	 * @return the rectangle size
	 * @throws Exception the exception
	 */
	public int getRectangleSize() throws Exception{
		return rectangleSize;
	}

	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
		// Apply our own painting effect
	    Graphics2D g2d = null; 
		try {
			g2d = (Graphics2D) g.create();

			// 70% transparent Alpha
			g2d.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER, 0.7F));	
			g2d.setColor(Color_schema.dark_40);
			g2d.fill(determineRightPanelBounds());
			g2d.fill(this.menubar.getBounds());
			g2d.setStroke(new BasicStroke(2.0f)); 
		//	g2d.dispose();

			  if(centerPoint != null){
				  g2d.setColor(Color.red);
				   //inner circle
				  g2d.drawRoundRect(centerPoint.x-rectangleSize/2, centerPoint.y-rectangleSize/2, rectangleSize, rectangleSize,rectangleSize/10,rectangleSize/10);

				  //outer rectangle
				  g2d.drawOval(centerPoint.x-rectangleSize/4, centerPoint.y-rectangleSize/4, rectangleSize/2, rectangleSize/2);
			   }
			   g2d.dispose();
		} catch (Exception e) {
			LOGGER.severe("Error in painting precount components!");
			e.printStackTrace();
			if(g2d != null)
				g2d.dispose();
		}
	    }

	/**
	 * Sets the center point.
	 *
	 * @param circleCenterPoint the new center point
	 * @throws Exception the exception
	 */
	public void setCenterPoint(Point circleCenterPoint) throws Exception{
		this.centerPoint = circleCenterPoint;
	}

	/**
	 * Sets the rectangle size.
	 *
	 * @param circleSize the new rectangle size
	 * @throws Exception the exception
	 */
	public void setRectangleSize(int circleSize) throws Exception{
		this.rectangleSize = circleSize;
	}




}

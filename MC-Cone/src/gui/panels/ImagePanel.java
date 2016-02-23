package gui.panels;

import gui.Color_schema;
import information.PositionedImage;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Contains the image and the painting methods for it.
 * @author Antti Kurronen
 *
 */
public class ImagePanel extends JPanel {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8441712306543933488L;

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The image to be shown. */
	private BufferedImage image_to_shown;
	
	/** The location Point. */
	private Point location; // the top left corner of image to be drawn

	
	/**
	 *  Class constructor.
	 *
	 * @param im the image of panel
	 * @param id the id
	 */
	public ImagePanel(BufferedImage im, int id){
		try {
			this.image_to_shown = im;
			this.setBackground(Color_schema.dark_40);
			this.setBorder(BorderFactory.createEmptyBorder());
			this.setLayout(null);
			this.location=new Point(0,0);
		} catch (Exception e) {
			LOGGER.severe("Error in initializing ImagePanel!");
			e.printStackTrace();
		}
		
	}

	/**
	 *  Class constructor.
	 */
	public ImagePanel(){
		try {
			this.image_to_shown=null;
			this.setBackground(Color_schema.dark_40);
			this.setBorder(BorderFactory.createEmptyBorder());
			this.setLayout(null);
			this.location=new Point(0,0);
		} catch (Exception e) {
			LOGGER.severe("Error in initializing ImagePanel!");
			e.printStackTrace();
		}

	}

	/**
	 * Sets the buffered image.
	 *
	 * @param im the new buffered image
	 * @throws Exception the exception
	 */
	public void setBufferedImage(BufferedImage im) throws Exception{
		this.image_to_shown = im;

	}

	
	/**
	 * Sets the image location.
	 *
	 * @param p the new image location
	 * @throws Exception the exception
	 */
	public void setImageLocation(Point p) throws Exception{
		this.location=p;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			if(this.image_to_shown != null)
				//g.drawImage(this.image_to_shown,0,0,this.getWidth(),this.getHeight(),null);
			g.drawImage(this.image_to_shown, (int)this.location.getX(), (int)this.location.getY(), Color_schema.dark_40, null);
			g.dispose();
		} catch (Exception e) {
			LOGGER.severe("Error in painting ImagePanel!");
			e.printStackTrace();
			g.dispose();
		}
	}

	/**
	 * Sets the image and position.
	 *
	 * @param pi the new image and position
	 * @throws Exception the exception
	 */
	public void setImageAndPosition(PositionedImage pi) throws Exception{
		if(pi != null){
			this.setImageLocation(pi.getPosition());
			this.setBufferedImage(pi.getImage());
			pi=null;
		}
	}

	/**
	 * Sets the image.
	 *
	 * @param pi the new PositionedImage
	 * @throws Exception the exception
	 */
	public void setImage(PositionedImage pi) throws Exception{
		if(pi != null){
			this.setBufferedImage(pi.getImage());
			pi=null;
		}
		else{
			this.setBufferedImage(null);
		}
	}



}

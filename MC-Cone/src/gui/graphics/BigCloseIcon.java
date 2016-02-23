package gui.graphics;


import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * The Class BigCloseIcon. Icon for close buttons. Size 18px.
 */
public class BigCloseIcon implements Icon {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The selected. Is Icon selected.*/
	private boolean selected=false;
	
	/**
	 * Instantiates a new big close icon.
	 *
	 * @param selected boolean is button of icon selected
	 */
	public BigCloseIcon(boolean selected){
		this.selected=selected;
	}
	  
  	/* (non-Javadoc)
  	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
  	 */
  	public void paintIcon(Component component, Graphics g, int x, int y) {
		  x =0;y =0;
		  Graphics2D g2d = null;
		  
	    try {
			g2d = (Graphics2D)g.create();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
			if(this.selected)
			    g2d.drawImage(createImage("/images/close_25_selected.png","info"), x, y, component);
			else
			    g2d.drawImage(createImage("/images/close_25.png","info"), x, y, component);

			g2d.dispose();
		} catch (Exception e) {
			LOGGER.severe("Error in painting big icon!");
			e.printStackTrace();
			if(g2d != null)
				g2d.dispose();
		}
	  }
	  
  	/* (non-Javadoc)
  	 * @see javax.swing.Icon#getIconWidth()
  	 */
  	public int getIconWidth() {
	    return 18;
	  }
	  
  	/* (non-Javadoc)
  	 * @see javax.swing.Icon#getIconHeight()
  	 */
  	public int getIconHeight() {
	    return 18;
	  }

	  /**
  	 * Creates the Image from given file path.
  	 *
  	 * @param path the path
  	 * @param description the description
  	 * @return the image
  	 */
  	private Image createImage(String path, String description) {
	        try {
				URL imageURL = BigCloseIcon.class.getResource(path);
				Image icn = null;


				if (imageURL == null) {
				    if(icn==null){
				      
				        icn = new ImageIcon (BigCloseIcon.class.getResource(path.replace("..",""))).getImage();
				        if(icn != null)
				            return icn;

				    }
				     return null;
				} else {
				    return (new ImageIcon(imageURL, description)).getImage();
				}
			} catch (Exception e) {
				LOGGER.severe("Error in creating image from path!");
				e.printStackTrace();
				return null;
			}
	    }

	}
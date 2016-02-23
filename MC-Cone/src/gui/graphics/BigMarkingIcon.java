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
 * The Class BigMarkingIcon. Icon for close button. Size 25px.
 */
public class BigMarkingIcon implements Icon {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The selected. Is Icon selected.*/
	private boolean selected=false;
	
	/**
	 * Instantiates a new big marking icon.
	 *
	 * @param selected boolean is button of icon selected
	 */
	public BigMarkingIcon(boolean selected){
		this.selected=selected;
	}
	  
  	/* (non-Javadoc)
  	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
  	 */
  	public void paintIcon(Component component, Graphics g, int x, int y) {
		  x =0;y =0;
		  Graphics2D g2d =null;
	    try {
			g2d = (Graphics2D)g.create();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
			if(this.selected)
			    g2d.drawImage(createImage("/images/mplus_selected.png","info"), x, y, component);
			else
			    g2d.drawImage(createImage("/images/mplus.png","info"), x, y, component);

			g2d.dispose();
		} catch (Exception e) {
			LOGGER.severe("Error in painting big Marking icon!");
			e.printStackTrace();
			if(g2d != null)
				g2d.dispose();
		}
	  }
	  
  	/* (non-Javadoc)
  	 * @see javax.swing.Icon#getIconWidth()
  	 */
  	public int getIconWidth() {
	    return 25;
	  }
	  
  	/* (non-Javadoc)
  	 * @see javax.swing.Icon#getIconHeight()
  	 */
  	public int getIconHeight() {
	    return 25;
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
				URL imageURL = BigMarkingIcon.class.getResource(path);
				Image icn = null;


				if (imageURL == null) {
				    if(null==icn){
				      
				        icn = new ImageIcon (BigMarkingIcon.class.getResource(path.replace("..",""))).getImage();
				        if(null!=icn)
				            return icn;

				    }
				     return null;
				} else {
				    return (new ImageIcon(imageURL, description)).getImage();
				}
			} catch (Exception e) {
				LOGGER.severe("Error in creating image!");
				e.printStackTrace();
				return null;
			}
	    }

	}
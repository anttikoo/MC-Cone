package gui;

import information.SharedVariables;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * Class ContentPane is used as ContentPane for dialogs to get black dimming outside of window.
 * @author Antti Kurronen
 *
 */
public class ContentPane extends JPanel{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2680468042227015564L;
	/** The Constant LOGGER. for Logging purposes */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	

	 /**
	 * Class constructor
	 */
	public ContentPane() {
	        setOpaque(false);
	       
	    }
	
	 /**
 	 * Class constructor.s
 	 *
 	 * @param layout the GridBagLayout
 	 */
	public ContentPane(GridBagLayout layout) {
		try {
			this.setLayout(layout);
			setOpaque(false);
		//	this.setBackground(Color_schema.dark_30);
		} catch (Exception e) {
			LOGGER.severe("ERROR in initializing ContentPane");
			e.printStackTrace();
		}
	}
	

	
	    @Override
	    protected void paintComponent(Graphics g) {

	        try {
	        	
	       
				// Allow super to paint
				super.paintComponent(g);
	
				
				// Apply our own painting effect
				Graphics2D g2d = (Graphics2D) g.create();
				
		        g2d.setRenderingHint(
		                RenderingHints.KEY_ANTIALIASING,
		                RenderingHints.VALUE_ANTIALIAS_ON);
				
				
				// 70% transparent Alpha
			
				Composite com = AlphaComposite.getInstance(SharedVariables.usedDimmingMode, 0.8f);

				g2d.setComposite(com);
		
							
				g2d.setColor(this.getBackground());
			
		
				g2d.fill(getBounds());
		
				
				g2d.dispose();
			
				
			} catch (Exception e) {
				LOGGER.severe("Error in painting black background of Dialog" + e.getClass().toString() + " : " +e.getMessage());
			}

	    }
	    
	    
	    
}

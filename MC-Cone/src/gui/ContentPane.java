package gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.util.logging.Logger;
import javax.swing.JPanel;
import information.ID;
import information.SharedVariables;

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
				
				// 70% transparent Alpha for linux and unix when java 1.7. In windows and OS X the color value with alpha will work ok.
				if(SharedVariables.operationSystem == ID.OS_LINUX_UNIX && SharedVariables.getJavaVersion() < 1.8){
					
			        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
					Composite com = AlphaComposite.getInstance(SharedVariables.usedDimmingMode, 0.6f);
					g2d.setComposite(com);
					g2d.setColor(new Color(0,0,0));
					
				}
				else{
					g2d.setColor(new Color(0,0,0,60));
				}

				g2d.fill(getBounds());				
				g2d.dispose();
						
			} catch (Exception e) {
				LOGGER.severe("Error in painting black background of Dialog" + e.getClass().toString() + " : " +e.getMessage());
			}

	    }
	    
	    
	    
}

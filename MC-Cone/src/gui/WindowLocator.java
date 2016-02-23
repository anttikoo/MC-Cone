package gui;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import information.ID;
import information.SharedVariables;

/**
 * The Class WindowLocator. A class for calculating good bounds from parent object.
 */
public class WindowLocator {

	/**
	 * Gets the visible window bounds.
	 *
	 * @param comp the comp
	 * @return Rectangle the visible window bounds
	 * @throws Exception the exception
	 */
	public static Rectangle getVisibleWindowBounds(Component comp) throws Exception{
		
		Rectangle parentRectangle=comp.getBounds();
		if(SharedVariables.operationSystem == ID.OS_LINUX_UNIX){ // in linux may the unity menu bar affect to painting of dimming 
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			Rectangle windowBounds = ge.getMaximumWindowBounds();
			
			int windowLeftX = windowBounds.x;				
			int x=parentRectangle.x;		
			int y= parentRectangle.y;
			int width=parentRectangle.width;
			int height=parentRectangle.height;
			
			// GUI over window		
			int guiX= parentRectangle.x;
			int guiWidth= parentRectangle.width;
				
			if(guiX<windowLeftX){
				x=windowLeftX;
				width = guiWidth-(windowLeftX-guiX);
			}
			
			return new Rectangle(x,y,width,height);
					
		}
		
		// normally returned only bounds of comp
		return parentRectangle;

	}

}

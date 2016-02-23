package operators;

import information.InformationCenter;

import java.net.URL;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

/**
 * The Class GetResources. A Helper class for getting ImageIcon from given path.
 */
public class GetResources {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/**
	 * Returns the image icon.
	 *
	 * @param path the path
	 * @return the ImageIcon
	 */
	public static ImageIcon getImageIcon(String path){

		try {
			URL url = InformationCenter.class.getResource(path);
			ImageIcon img = new ImageIcon(url);
			return img;
		} catch (Exception e) {
			LOGGER.severe("Error in adding SingleMarkingPanel " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}
}
}

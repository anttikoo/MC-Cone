package gui.file;


import java.io.File;
import java.util.logging.Logger;

import javax.swing.filechooser.FileFilter;

/**
 * The Class TIFFfilter. Contains methods for accepting .tif files or folder 
 * and getting the description of file.
 */
public class TIFFfilter extends FileFilter {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        try {
			if (f.isDirectory()) {
			    return true;
			}

			String extension = Utils.getExtension(f);
			if (extension != null) {
			    if (extension.equals(Utils.tiff) ||
			        extension.equals(Utils.tif)) {
			            return true;
			    } else {
			        return false;
			    }
			}

			return false;
		} catch (Exception e) {
			LOGGER.severe("Error in checking file extension!");
			e.printStackTrace();
			return false;
		}
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    //The description of this filter
    public String getDescription() {
        return ".tif";
    }
}
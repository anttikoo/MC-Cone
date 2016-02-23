package gui.file;


import java.io.File;
import java.util.logging.Logger;

import javax.swing.filechooser.FileFilter;

/**
 * The Class CSVfilter. Contains methods for accepting file as .csv file or folder 
 * and getting the description of file.
 */
public class CSVfilter extends FileFilter {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    //Accept all directories and xml files
    public boolean accept(File f) {
        try {
			if (f.isDirectory()) {
			    return true;
			}

			String extension = Utils.getExtension(f);
			if (extension != null) {
			    if (extension.equals(Utils.csv)) {
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
        return ".csv";
    }
}

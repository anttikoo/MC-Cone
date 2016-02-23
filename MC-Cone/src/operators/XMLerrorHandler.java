package operators;

import java.util.logging.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The Class XMLerrorHandler. Detects the warnings and errors in parsing xml-file 
 * and if any error found saves information that error has been found.
 */
public class XMLerrorHandler implements ErrorHandler {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");	
	
	/** The found errors. Has any error occurred. */
	private boolean foundErrors=false;

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException e) throws SAXException {
    	LOGGER.severe("Error in  " +e.getClass().toString() + " :" +e.getMessage()+ " line: " + e.getLineNumber());
    	this.foundErrors=true;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException e) throws SAXException {
    	LOGGER.severe("Error in  " +e.getClass().toString() + " :" +e.getMessage()+ " line: " + e.getLineNumber());
    	this.foundErrors=true;
    }

    /**
	 * Checks if is errors found.
	 *
	 * @return true, if is errors found
	 */
	public boolean isErrorsFound() {
		return foundErrors;
	}

	/* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException e) throws SAXException {
    	LOGGER.warning("Warning at  " +e.getClass().toString() + " :" +e.getMessage()+ " line: " + e.getLineNumber());
    	
    }

	
}
package operators;

import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML Handler for validating xml file. Parses the file, but doesn't save the information anywhere. 
 * If everything fine: no errors and file validated.
 * @author Antti Kurronen
 *
 */
public class ValidationHandler extends DefaultHandler {

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");	
	
	/** The is color. */
	private boolean isColor =false;
	
	/** The is single coordinate. */
	private boolean isSingleCoordinate =false;
	
	/** The is shape. */
	private boolean isShape =false;
	
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
 
        if (isColor) {
            //age element, set Employee age
            @SuppressWarnings("unused")
			String color=(new String(ch, start, length)); // color is not used
            isColor = false;
        } else if (isSingleCoordinate) {
            @SuppressWarnings("unused")
			String Point=(new String(ch, start, length)); // point is not used
            isSingleCoordinate = false;
        } else if (isShape) {
        	@SuppressWarnings("unused")
			int shapeID = getIntFromString(new String(ch, start, length)); // shape is not used
        	
            isShape = false;
        }
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("imagelayer")) {
            //do nothing
           
        }
        else if (qName.equalsIgnoreCase("markinglayer")) {
        	 // do nothing
             }      	
        }
    
    
    /**
     * Returns the int from string.
     *
     * @param s the String
     * @return the int from string
     */
    private int getIntFromString(String s){
    	try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			LOGGER.warning("Error in converting string to int: " +e.getClass().toString() + " :" +e.getMessage());
			return -1;
		}
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
 
        if (qName.equalsIgnoreCase("imagelayer")) {
            @SuppressWarnings("unused")
			String i_name = attributes.getValue("imagename");     
         // try to find image name from imageLayerList or single Imagelayer -> if found set as selectedImageLayer
         //   searchImageLayer(i_name); 

        } else  { // go only lower nodes if wanted ImageLayer was found and set as selected
            //set boolean values for fields, will be used in setting Employee variables
           
	         if (qName.equalsIgnoreCase("markinglayer")) { 
	        	       	 
	            //create new markinglayer to add information
	        	 @SuppressWarnings("unused")
				String mn= (attributes.getValue("markingname"));	// just get attribute and don't do anything with it        	
	          //  this.isMarkingLayer= true;
	            
	        } else if (qName.equalsIgnoreCase("color")) {
	           isColor = true;
	        } else if (qName.equalsIgnoreCase("singlecoordinate")) {
	            isSingleCoordinate = true;
	        } else if (qName.equalsIgnoreCase("shape")) {
	            isShape = true;
	        }
        }
    }
	
}

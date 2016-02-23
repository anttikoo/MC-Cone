package operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML Handler for checking does xml file contains ImageLayer element and markingLayers related to it.
 * @author Antti Kurronen
 *
 */
public class SearchImageLayersHandler extends DefaultHandler {

	/** The list of ImageLayer names. */
	private ArrayList<String> imageLayerNameList = null;
	
	/** The found image layer. */
	private boolean foundImageLayer=false;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/**
	 * Instantiates a new search image layers handler.
	 *
	 * @param imageLayerNameList the list of names of ImageLayers
	 */
	public SearchImageLayersHandler(ArrayList<String> imageLayerNameList){
		this.imageLayerNameList=imageLayerNameList;
	}

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase("imagelayer")) {
            String i_name = attributes.getValue("imagename").trim();
         // try to find image name from imageLayerList or single Imagelayer -> if found set as selectedImageLayer
            searchImageLayer(i_name);

        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
    	//do nothing
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
    	// do nothing
    }



    /**
     * Searches ImageLayer.
     *
     * @param i_name the name of ImageLayer
     * @throws SAXException the SAX exception
     */
    private void searchImageLayer(String i_name) throws SAXException{
    	try {
			// go through imageLayerList
			if(imageLayerNameList != null && imageLayerNameList.size()>0){

				Iterator<String> iIterator = this.imageLayerNameList.iterator();
				while(iIterator.hasNext()){
					String layerName = (String)iIterator.next();
					if(layerName.equals(i_name)){ // ImageLayerPath
						this.setFoundImageLayer(true);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.severe("Error in going trough imageLayerList " +e.getClass().toString() + " :" +e.getMessage());
			throw new SAXException();
		}

    }

	/**
	 * Checks boolean has ImageLayer found.
	 *
	 * @return true, if is found image layer
	 */
	public boolean isFoundImageLayer(){
		return foundImageLayer;
	}

	/**
	 * Sets the boolean ha ImageLayer found.
	 *
	 * @param foundImageLayer the new found image layer
	 */
	public void setFoundImageLayer(boolean foundImageLayer) {
		this.foundImageLayer = foundImageLayer;
	}

}

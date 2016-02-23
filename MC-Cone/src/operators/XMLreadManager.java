package operators;

import gui.GUI;
import information.ImageLayer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * The Class XMLreadManager. Parser for xml file containing information of ImageLayers and its MarkingLayers.
 * SaxParser is used in parsing.
 */
public class XMLreadManager {

/** The Constant LOGGER. */
private final static Logger LOGGER = Logger.getLogger("MCCLogger");

/** The SAXParserFactory for parsing XML document. */
private SAXParserFactory saxParserFactory;

/** The SAXParser. */
private SAXParser saxParser;

	/**
	 * Instantiates a new XMLreadManager.
	 */
	public XMLreadManager(){
		initSAXparser();
	}

	/**
	 * Parses xml-file to find is any ImageLayers found with given list of names of ImageLayers.
	 *
	 * @param xmlFileName the path of XML file.
	 * @param imageLayerNameList the list of names of ImageLayers
	 * @return true, if successfully found data of ImageLayer
	 */
	public boolean foundImageLayer(String xmlFileName, ArrayList<String> imageLayerNameList){
	    try {
	    	// Handler for ImageLayers
	        SearchImageLayersHandler searchHandler = new SearchImageLayersHandler(imageLayerNameList); 
	        // XMLReader
	        XMLReader reader = saxParser.getXMLReader(); 
	        // XMLerrorHandler
	        XMLerrorHandler errorHandler=new XMLerrorHandler();
	        // set how to validate the xml file against dtd -> errors will be handled in XMLerrorHandler
	        reader.setEntityResolver(new EntityResolver() { 
	
				/* (non-Javadoc)
				 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
				 */
				@Override
				public InputSource resolveEntity(String publicId, String systemId)
				        throws SAXException, IOException {
					LOGGER.fine("publicID: "+publicId + " systemID: "+systemId);
				    if (systemId.contains("layers.dtd")) {
				    	return new InputSource(GUI.class.getResourceAsStream("/information/dtd/layers.dtd"));
				    }
				    else{
				    	return null;
				    }
				}
	        });
	        reader.setErrorHandler(errorHandler);
	        reader.setContentHandler(searchHandler);
	        reader.parse(new InputSource(xmlFileName)); // parse -> validates and reads if valid.
	        //Get list of ImageLayers which may contain imported markings
	        if(!errorHandler.isErrorsFound())
	        	return searchHandler.isFoundImageLayer();
	        else
	        	return false;

	    } catch (SAXException | IOException e) {
	    	LOGGER.severe("Error in searching ImageLayers!" + e.getMessage());
	    	return false;
	    }
	}

	/**
	 *  Parses xml-file to find given list of ImageLayers and updated data of them.
	 *
	 * @param xmlFile the xml-file
	 * @param imageLayerList the list of ImageLayers
	 * @return the list of ImageLayers with updated data
	 */
	public ArrayList<ImageLayer> getMarkingsOfXML(File xmlFile, ArrayList<ImageLayer> imageLayerList){
		    try {
		    	// Handler for ImageLayers
		        MarkingsHandler handler = new MarkingsHandler(imageLayerList);
		        // XMLReader
		        XMLReader reader = saxParser.getXMLReader();
		        // XMLerrorHandler
		        XMLerrorHandler errorHandler=new XMLerrorHandler();
		        // set entityResolver to validate the xml file against dtd -> errors will be handled in XMLerrorHandler
		        reader.setEntityResolver(new EntityResolver() {
				    @Override
				    public InputSource resolveEntity(String publicId, String systemId)
				            throws SAXException, IOException {
				    	LOGGER.fine("publicID: "+publicId + " systemID: "+systemId);
				        if (systemId.contains("layers.dtd")) {
				        	return new InputSource(GUI.class.getResourceAsStream("/information/dtd/layers.dtd")); // get the dtd-file
				        }
				        else{
				        	return null;
				        }
				    }
				});
		        reader.setErrorHandler(errorHandler);
		        reader.setContentHandler(handler);
		        reader.parse(new InputSource(xmlFile.getAbsolutePath()));  // parse -> validates and reads if valid.
		        //Get list of ImageLayers which may contain imported markings
		        if(!errorHandler.isErrorsFound())
		        	return handler.getImageLayerList();
		        else
		        	return null;
	
		    } catch (SAXException | IOException e) {
		    	LOGGER.severe("Error in importing markings for array of ImageLayers " +e.getClass().toString() + " :" +e.getMessage());
		    	return null;
		    }
		    catch(Exception ex){
		    	LOGGER.severe("Error in importing markings for array of ImageLayers ");
		    	ex.printStackTrace();
		    	return null;
		    }
	}

		/**
		 * Parses xml-file to find given single found ImageLayer and updated data of it.
		 *
		 * @param xmlFile the xml-file
		 * @param imageLayer the image layer
		 * @return the ImageLayer with updated data
		 */
		public ImageLayer getMarkingsOfXML(File xmlFile, ImageLayer imageLayer){
		    try {
		    	// Handler for ImageLayers
		        MarkingsHandler handler = new MarkingsHandler(imageLayer);
		        // XMLReader
		        XMLReader reader = saxParser.getXMLReader();
		        // set entityResolver to validate the xml file against dtd -> errors will be handled in XMLerrorHandler
		        reader.setEntityResolver(new EntityResolver() {
					@Override
					public InputSource resolveEntity(String publicId, String systemId)
							throws SAXException, IOException {
							LOGGER.fine("publicID: "+publicId + " systemID: "+systemId);
						    if (systemId.contains("layers.dtd")) {
						    	return new InputSource(GUI.class.getResourceAsStream("/information/dtd/layers.dtd"));	
						    }
						    else{
						    	LOGGER.warning("Reading Markings, but not found dtd file:");
						    	return null;
							}
						}
				});
		        // set XMLerrorHandler
		        reader.setErrorHandler(new XMLerrorHandler());
		        reader.setContentHandler(handler);
		        reader.parse(new InputSource(xmlFile.getAbsolutePath())); // parse -> validates and reads file if valid
		        
		        //Get processed ImageLayer -> may contain markings imported from xml file
		       return  handler.getImageLayer();
		
		    } catch (SAXException | IOException e) {
		    	LOGGER.severe("Error in importing markings for single ImageLayers " +e.getClass().toString() + " :" +e.getMessage());
		        return null;
		    }
		    catch(Exception ex){
		    	LOGGER.severe("Error in reading xml and updating ImageLayer!");
		    	ex.printStackTrace();
		    	return null;
		    }
		}
		
	    /**
    	 * Initializes the SaxParser.
    	 */
    	private void initSAXparser(){
			saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(true);
			saxParserFactory.setNamespaceAware(false);
			try{
			saxParser = saxParserFactory.newSAXParser();

			} catch (ParserConfigurationException | SAXException e) {
				LOGGER.severe("Error in initializing xml parser!");
		        e.printStackTrace();
		    }
		}

	    /**
	     * Method for validate xml-file.
	     * @param xmlFile The File which is validated.
	     * @return true if file is valid, false otherwise.
	     */
	    public boolean isFileValid(File xmlFile){
	  	    try {
	  	    	// Handler for ImageLayers
	  	        ValidationHandler handler = new ValidationHandler();
	  	      // set XMLReader
	  	        XMLReader reader = saxParser.getXMLReader();

	  	        XMLerrorHandler errorHandler=new XMLerrorHandler();
	  	      // set entityResolver to validate the xml file against dtd -> errors will be handled in XMLerrorHandler
		        reader.setEntityResolver(new EntityResolver() {
		            @Override
		            public InputSource resolveEntity(String publicId, String systemId)
		                    throws SAXException, IOException {
		            	LOGGER.fine("publicID: "+publicId + " systemID: "+systemId);
		                if (systemId.contains("layers.dtd")) {
		                	return new InputSource(GUI.class.getResourceAsStream("/information/dtd/layers.dtd"));
		                }
		                else{
		                	LOGGER.fine("not found dtd");
		                	return null;
		                }
		            }
		        });
	  	        reader.setErrorHandler(errorHandler);
	  	        reader.setContentHandler(handler);
	  	        reader.parse(new InputSource(xmlFile.getAbsolutePath())); // validates file

	  	        //return boolean is valid file
	  	        LOGGER.fine("validated the file, found errors: "+errorHandler.isErrorsFound());
	  	       return !errorHandler.isErrorsFound();

	  	    } catch (SAXException | IOException e) {
	  	    	LOGGER.severe("Error in validating xml-file" +e.getClass().toString() + " :" +e.getMessage());
	  	    	return false;
	  	    }

	}


	    /**
    	 * Checks if is stream valid.
    	 *
    	 * @param bStream the ByteStream
    	 * @return true, if is stream of xml-file  valid
    	 */
    	public boolean isStreamValid(ByteArrayOutputStream bStream){
		  	    try {
		  	        ValidationHandler handler = new ValidationHandler();
		  	        XMLReader reader = saxParser.getXMLReader();

		  	        XMLerrorHandler errorHandler=new XMLerrorHandler();


			        reader.setEntityResolver(new EntityResolver() {
			            @Override
			            public InputSource resolveEntity(String publicId, String systemId)
			                    throws SAXException, IOException {

			                if (systemId.contains("layers.dtd")) {

			                	return new InputSource(getClass().getResourceAsStream("/information/dtd/layers.dtd"));

			                }
			                else{
			                	return null;
			                }
			            }
			        });

		  	        reader.setErrorHandler(errorHandler);
		  	        reader.setContentHandler(handler);
		  	        reader.parse(new InputSource(new ByteArrayInputStream(bStream.toByteArray())));
	
		  	        //return boolean is valid file
		  	      LOGGER.fine("validated the stream, found errors: "+errorHandler.isErrorsFound());
		  	       return !errorHandler.isErrorsFound();

		  	    } catch (SAXException | IOException e) {
		  	    	LOGGER.severe("Error in Checking validation of stream: " +e.getClass().toString() + " :" +e.getMessage());
		  	    	return false;
		  	    }

		}

}

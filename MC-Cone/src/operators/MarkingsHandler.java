package operators;

import information.GridProperties;
import information.ImageLayer;
import information.MarkingLayer;
import information.PositionedRectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML Handler for checking does xml file contains ImageLayer element and its markingLayers.
 * @author Antti Kurronen
 *
 */
public class MarkingsHandler extends DefaultHandler {

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The image layer list. */
	private ArrayList<ImageLayer> imageLayerList = null;
	
	/** The single image layer. */
	private ImageLayer singleImageLayer=null;
	
	/** The selected image layer. */
	private ImageLayer selectedImageLayer=null;
	
	/** The selected marking layer. */
	private MarkingLayer selectedMarkingLayer=null;
	
	/** The grid property. */
	private GridProperties gridProperty=null;
	
	/** The is color. */
	private boolean isColor =false;
	
	/** The is single coordinate. */
	private boolean isSingleCoordinate =false;
	
	/** The is thickness. */
	private boolean isThickness =false;
	
	/** The is opacity. */
	private boolean isOpacity =false;
	
	/** The is size. */
	private boolean isSize =false;
	
	/** The is shape. */
	private boolean isShape =false;
	
	/** The is grid_on. */
	private boolean isGrid_on=false;
	
	/** The is column line. */
	private boolean isX =false;
	
	/** The is row line. */
	private boolean isY =false;
	
	/**
	 * Instantiates a new markings handler.
	 *
	 * @param imageLayerList  ArrayList of ImageLayer objects which markings are searched
	 */
	public MarkingsHandler(ArrayList<ImageLayer> imageLayerList){
		this.imageLayerList=imageLayerList;
	}

	/**
	 * @param singleImageLayer One ImageLayer object which marking layers are searched
	 */
	public MarkingsHandler(ImageLayer singleImageLayer){
		this.singleImageLayer=singleImageLayer;
	}


	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
    public void characters(char ch[], int start, int length) throws SAXException {

        if (isColor) {
            //age element, set Employee age
            try {
				this.selectedMarkingLayer.setStringColor(new String(ch, start, length));
			} catch (Exception e) {
				LOGGER.severe("Error in reading color for MarkingLayer!");
				e.printStackTrace();
			}
            isColor = false;
        } else if (isSingleCoordinate) {
            this.selectedMarkingLayer.addStringPoint(new String(ch, start, length));
            isSingleCoordinate = false;
        } else if (isShape) {
        	try {
				int shapeID = getIntFromString(new String(ch, start, length));
				if(shapeID >0)
					this.selectedMarkingLayer.setShapeID(shapeID);
				isShape = false;
			} catch (Exception e) {
				LOGGER.severe("Error in setting shapeID to MarkingLayer!");
				e.printStackTrace();
				isShape=false;
			}
        }else if (isSize) {
        	try {
				int size = getIntFromString(new String(ch, start, length));
				if(size >0)
					this.selectedMarkingLayer.setSize(size);
			} catch (Exception e) {
				LOGGER.severe("Error in setting size to selected MarkingLayer!");
				e.printStackTrace();
			}
            isSize = false;
        }else if (isOpacity) {
        	try {
				float opacity = getFloatFromString(new String(ch, start, length));
				if(opacity >0.0f)
					this.selectedMarkingLayer.setOpacity(opacity);
			} catch (Exception e) {
				LOGGER.severe("Error in setting opacity to selected MarkingLayer!");
				e.printStackTrace();
			}
            isOpacity = false;
        }else if (isThickness) {
        	try {
				int thickness= getIntFromString(new String(ch, start, length));
				if(thickness > 0)
					this.selectedMarkingLayer.setThickness(thickness);
			} catch (Exception e) {
				LOGGER.severe("Error in setting thickness to selected MarkingLayer!");
				e.printStackTrace();
			}
            isThickness = false;
        }
        else if (isGrid_on) {
        	try {
				String value=new String(ch, start, length);
				if(value.equalsIgnoreCase(XMLtags.value_true))
					this.gridProperty.setGridON(true);
				else if(value.equalsIgnoreCase(XMLtags.value_false))
					this.gridProperty.setGridON(false);
			} catch (Exception e) {
				LOGGER.severe("Error in getting grid on off -value from xml !");
				e.printStackTrace();
			}
            isGrid_on= false;
        }
        else if (isX) {
        	try {
				int x = getIntFromString(new String(ch, start, length));
				this.gridProperty.addColumnLineX(x);
				isX= false;
			} catch (Exception e) {
				LOGGER.severe("Error in reading grid column lines from xml!");
				e.printStackTrace();
				isX=false;
			}
        }
        else if (isY) {
        	try {
				int y = getIntFromString(new String(ch, start, length));
				this.gridProperty.addRowLineY(y);
				isY= false;
			} catch (Exception e) {
				LOGGER.severe("Error in reading grid row lines from xml!");
				e.printStackTrace();
				isY=false;
			}
        }
    }


    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase(XMLtags.imagelayer)) {
            //Got all information for selectedImagelayer -> set null;
            this.selectedImageLayer = null;
          //  this.isFoundImageLayer=false;
        }
        else if (qName.equalsIgnoreCase(XMLtags.markinglayer)) {
        	 	try {
					if(this.selectedImageLayer != null && this.selectedMarkingLayer != null){
						this.selectedImageLayer.addMarkingLayer(this.selectedMarkingLayer);
          }
				} catch (Exception e) {
					LOGGER.severe("Error in adding MarkingLayer from xml!");
					e.printStackTrace();
				}

        	this.selectedMarkingLayer = null;
        }
        else if (qName.equalsIgnoreCase(XMLtags.grid)) {
       	 try {
			if(this.selectedMarkingLayer != null){
				 this.selectedMarkingLayer.setGridProperties(this.gridProperty);
				 this.gridProperty=null;
			  }
		} catch (Exception e) {
			LOGGER.severe("Error in setting GridProperty to selected MarkingLayer when read from xml file!");
			e.printStackTrace();
		}
        }
    }

    /**
     * Returns the boolean from string.
     *
     * @param s the String
     * @return the boolean from string
     * @throws Exception the exception
     */
    private boolean getBooleanFromString(String s) throws Exception{
    	if(s.equals(XMLtags.value_true))
    		return true;
    	else
    		return false;
    }

    /**
     * Returns the float from string.
     *
     * @param s the String
     * @return the float from string
     * @throws Exception the exception
     */
    private Float getFloatFromString(String s)  throws Exception{
    	try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			LOGGER.fine("Error in converting string to int: " +e.getClass().toString() + " :" +e.getMessage());
			return 1.0f; // if error happens 1.0 is good solution (gives 100 % opacity and 1.0 thickness)
		}
    }

    /**
     * Returns the ImageLayer.
     *
     * @return the ImageLayer
     * @throws Exception the exception
     */
    public ImageLayer getImageLayer() throws Exception{
    	return this.singleImageLayer;
    }
    
    /**
     * Returns the list of ImageLayers.
     *
     * @return the list of ImageLayers
     * @throws Exception the exception
     */
    public ArrayList<ImageLayer> getImageLayerList() throws Exception{
    	return this.imageLayerList;
    }

    /**
     * Returns the int from string.
     *
     * @param s the String
     * @return the Integer from string
     * @throws Exception the exception
     */
    private int getIntFromString(String s) throws Exception{
    	try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			LOGGER.fine("Error in converting string to int: " +e.getClass().toString() + " :" +e.getMessage());
			return -1;
		}
    }

 
    /**
     * Searches ImageLayer by name.
     *
     * @param i_name the name of the ImageLayer
     * @throws SAXException the SAX exception
     */
    private void searchImageLayer(String i_name) throws SAXException{
    	try {
    		// selectedImageLayer should be already null,
    		// because in start it is null and after another ImageLayer node it will be set as null
    		selectedImageLayer=null;
			// go through imageLayerList
			if(imageLayerList != null && imageLayerList.size()>0){

				Iterator<ImageLayer> iIterator = this.imageLayerList.iterator();
				while(iIterator.hasNext()){
					ImageLayer il = (ImageLayer)iIterator.next();
					if(il.getImageFilePath().equals(i_name)){ // ImageLayerPath
						selectedImageLayer = il;
					}
				}
			}
			else{
				if(singleImageLayer.getImageFileName().equalsIgnoreCase(i_name)){ // ImageLayerPath
					LOGGER.fine("selected single imagelayer in markinghandler");
					selectedImageLayer=singleImageLayer;

				}
			}

		} catch (Exception e) {
			LOGGER.severe("Error in going trough imageLayerList " +e.getClass().toString() + " :" +e.getMessage());
			selectedImageLayer=null;
			throw new SAXException();
		}

    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase(XMLtags.imagelayer)) {
            String i_name = attributes.getValue(XMLtags.imagename);
         // try to find image name from imageLayerList or single Imagelayer -> if found set as selectedImageLayer
            searchImageLayer(i_name);

        } else if ( this.selectedImageLayer != null) { // go only lower nodes if wanted ImageLayer was found and set as selected
            //set boolean values for fields, will be used in setting Employee variables

	         if (qName.equalsIgnoreCase(XMLtags.markinglayer)) {

	            //create new markinglayer to add information
	        	 this.selectedMarkingLayer = new MarkingLayer(attributes.getValue(XMLtags.markingname));

	        } else if (qName.equalsIgnoreCase(XMLtags.color)) {
	           isColor = true;
	        } else if (qName.equalsIgnoreCase(XMLtags.singlecoordinate)) {
	            isSingleCoordinate = true;
	        } else if (qName.equalsIgnoreCase(XMLtags.shape)) {
	            isShape = true;
	        }else if (qName.equalsIgnoreCase(XMLtags.size)) {
	            isSize = true;
	        }else if (qName.equalsIgnoreCase(XMLtags.opacity)) {
	            isOpacity = true;
	        }else if (qName.equalsIgnoreCase(XMLtags.thickness)) {
	            isThickness = true;
	        }else if (qName.equalsIgnoreCase(XMLtags.grid)) {
	        	this.gridProperty=new GridProperties(); // init GridProperties
	        }
	        else if (qName.equalsIgnoreCase(XMLtags.grid_on)) {
	            isGrid_on = true;
	        }
	        else if (qName.equalsIgnoreCase(XMLtags.x)) {
	            isX = true;
	        }
	        else if (qName.equalsIgnoreCase(XMLtags.y)) {
	            isY = true;
	        } 
	        else if (qName.equalsIgnoreCase(XMLtags.rec)) {
	        	if(this.gridProperty != null){
	        		try {
						PositionedRectangle posRec=new PositionedRectangle(
						getIntFromString(attributes.getValue(XMLtags.x)),
						getIntFromString(attributes.getValue(XMLtags.y)),
						getIntFromString(attributes.getValue(XMLtags.width)),
						getIntFromString(attributes.getValue(XMLtags.height)),
						getIntFromString(attributes.getValue(XMLtags.row)),
						getIntFromString(attributes.getValue(XMLtags.column)),
						getBooleanFromString(attributes.getValue(XMLtags.selected)));
						this.gridProperty.addSinglePositionedRectangle(posRec);
					} catch (Exception e) {
						LOGGER.severe("Error in adding positioned rectangle to gridproperties!");
						e.printStackTrace();
					}
	        	}
	        }
        }
    }
}

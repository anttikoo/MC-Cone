package operators;

import information.GridProperties;
import information.ID;
import information.ImageLayer;
import information.LayersOfPath;
import information.MarkingLayer;
import information.PositionedRectangle;
import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



/**
 * Writes brand new or modified xml-file. Uses StaX for reading and writing the file.
 * @author Antti Kurronen
 *
 */
public class XMLwriteManager {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The StartElement. */
	private StartElement se;
	
	/** The EndElement. */
	private EndElement ee;
	
	/** The XMLEventReader. */
	private XMLEventReader xer;
	
	/** The XMLEventWriter. */
	private XMLEventWriter xew;
	
	/** The XMLEvent. */
	private XMLEvent end;
	
	/** The XMLEventFactory. */
	private XMLEventFactory xef;
	
	/** The layers of path. */
	private LayersOfPath layersOfPath;
	
	/** The Attribute. */
	private Attribute att;
	
	/** The saving type. For example ID.OVERWRITE. */
	private int savingType;
	
	/** The unsaved marking layers. */
	private ArrayList<Integer>unsavedMarkingLayers;
	
	/** The successfully saved marking layers. */
	private ArrayList<Integer>successfullySavedMarkingLayers;
	
	/** The byte stream for writing XML document to file. */
	private ByteArrayOutputStream byteStream;
	
	/** The XMLReadManager for reading XML document with StaX.. */
	private XMLreadManager rm;

	
	/**
	 * Instantiates a new XMLWriteManager.
	 */
	public XMLwriteManager(){
		try {
			this.layersOfPath=null;
			this.savingType=ID.UNDEFINED;
			this.unsavedMarkingLayers=new ArrayList<Integer>();
			this.successfullySavedMarkingLayers=new ArrayList<Integer>();
			rm = new XMLreadManager();
		} catch (Exception e) {
			LOGGER.severe("Error in initializing XMLWriteManager!");
			e.printStackTrace();
		}
	}

	/**
	 * Closes XMLEventReader input.
	 *
	 * @throws Exception the exception
	 */
	private void closeInput() throws Exception{
		xer.close();
	}

	/**
	 * Closes XMLEventWriter output.
	 *
	 * @throws Exception the exception
	 */
	private void closeOutput() throws Exception{
		xew.flush();
		xew.close();
		byteStream.flush();
	}

	/**
	 * Combine content of ImageLayer with xml-file. Reads xml-file file 
	 *
	 * @param iLayer the ImageLayer
	 */
	private void combineImageLayerContentWithXMLfile(ImageLayer iLayer){

		try {
			// go through markingLayers of xml file
			while(xer.hasNext()){
				XMLEvent event= xer.nextEvent();
				if(event.isStartElement() && event.asStartElement().getName().equals(XMLtags.qnameMarkingLayer)){
					se=event.asStartElement();
					String mLayerName=se.getAttributeByName(XMLtags.qnameMarkingName).getValue();
					MarkingLayer mLayer = getMarkingLayerByName(mLayerName, iLayer);
					if(mLayer != null){ // MarkingLayer found -> either overwrite or skip
						int overwriteOrSkip= ID.UNDEFINED;
						if(savingType != ID.UNDEFINED) // remembering the rule from previous selection
							overwriteOrSkip=savingType;
						else{ // undefined -> ask from user
						// ask from user should overwrite MarkingLayer
						overwriteOrSkip=inquireSavingType(); // construct later
						if(overwriteOrSkip == ID.OVERWRITE_REMEMBER_SELECTION || overwriteOrSkip == ID.SKIP_REMEMBER_SELECTION)
							this.savingType=overwriteOrSkip; // remembers next time
						}
						if(overwriteOrSkip==ID.OVERWRITE || overwriteOrSkip == ID.OVERWRITE_REMEMBER_SELECTION){
							// overwrite -> write from MarkingLayer-object
							if(writeMarkingLayer(mLayer)){
								iLayer.removeMarkingLayer(mLayer); // written successfully -> remove
								successfullySavedMarkingLayers.add(mLayer.getLayerID());
							}
							else{ // error in saving MarkingLayer
								unsavedMarkingLayers.add(mLayer.getLayerID());
							}
							skipReadingMarkingLayer(); // XMLreader goes to endElement of markinglayer
						}
						else{ // skip -> Write from XML-file
							writeMarkingLayerFromXMLfile(se);
						}
					}
					else{// not found the MarkingLayer -> write from file
						writeMarkingLayerFromXMLfile(se);
					}
				}else // has read the whole imageLayer content from xml-file
					if(event.isEndElement() && event.asEndElement().getName().equals(XMLtags.qnameImageLayer)){

						// write the rest MarkingLayers of iLayer
						Iterator<MarkingLayer> mIterator =iLayer.getMarkingLayers().iterator();
						while(mIterator.hasNext()){
							MarkingLayer markingLayer= mIterator.next();
							if(writeMarkingLayer(markingLayer)){
								mIterator.remove(); // written successfully -> remove
								successfullySavedMarkingLayers.add(markingLayer.getLayerID());
							}
							else{ // error in saving MarkingLayer
								unsavedMarkingLayers.add(markingLayer.getLayerID());
							}
						}
						xew.add(event); // write endElement </imagelayer>
						return; // go back for reading imagelayers
					}

			}
		} catch (XMLStreamException e) {
			LOGGER.severe("Error in combining ImageLayer data with data of xml-file!");
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.severe("Error in combining ImageLayer data with data of xml-file!");
			e.printStackTrace();
		}
	}

	/**
	 * Returns the DTTEvent.
	 *
	 * @return the DTD
	 */
	private DTD getDTDevent(){
		try {
		/*  NOT USED
			InputStream in= getClass().getResourceAsStream("/information/dtd/layers.dtd");
			Scanner s = new Scanner(in).useDelimiter("\\A");
			String dtdFIleString= s.hasNext() ? s.next() : "";	
			in.close();
			s.close();
		*/
			 
		String fileStr = "<!DOCTYPE layers SYSTEM \"layers.dtd\">";
		return xef.createDTD(fileStr);
		
		} catch (Exception e) {
			LOGGER.severe("error in getting DTDEvent!");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the ImageLayer by given name.
	 *
	 * @param iName the name of ImageLayer
	 * @return the ImageLayer
	 * @throws Exception the exception
	 */
	private ImageLayer getImageLayerByName(String iName) throws Exception{
		if(iName != null && iName.length()>0 && this.layersOfPath.getImageLayerList() != null
				&& this.layersOfPath.getImageLayerList().size()>0){
			Iterator<ImageLayer> iIterator = this.layersOfPath.getImageLayerList().iterator();
			while(iIterator.hasNext()){
				ImageLayer iLayer= iIterator.next();
				if(iName.equals(iLayer.getImageFileName()))
					return iLayer;
			}
		}
		return null;


	}

	/**
	 * Returns the MarkingLayer by name.
	 *
	 * @param mName the name of MarkingLayer
	 * @param iLayer the ImageLayer
	 * @return the MarkingLayer
	 * @throws Exception the exception
	 */
	private MarkingLayer getMarkingLayerByName(String mName, ImageLayer iLayer) throws Exception{
		if(mName != null && mName.length()>0 && iLayer.getMarkingLayers() != null && iLayer.getMarkingLayers().size()>0){
			// go through all markinglayers of imageLayer
			Iterator<MarkingLayer> mIterator = iLayer.getMarkingLayers().iterator();
			while(mIterator.hasNext()){
				MarkingLayer ml= mIterator.next();
				if(mName.equals(ml.getLayerName()))
					return ml;
			}
		}
		return null;

	}

	/**
	 * Returns the successfully saved marking layers.
	 *
	 * @return the successfully saved marking layers
	 * @throws Exception the exception
	 */
	public ArrayList<Integer> getSuccessfullySavedMarkingLayers() throws Exception{
		return successfullySavedMarkingLayers;
	}

	/**
	 * Returns the unsaved marking layers.
	 *
	 * @return the unsaved marking layers
	 * @throws Exception the exception
	 */
	public ArrayList<Integer> getUnsavedMarkingLayers() throws Exception {
		return unsavedMarkingLayers;
	}

	/**
	 * Initializes the input from given file.
	 *
	 * @param filePath the file path
	 */
	private void initInput(String filePath){

		try {
			XMLInputFactory inputFactory=XMLInputFactory.newInstance();
			InputStream in = new FileInputStream(filePath);

			inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
			inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			inputFactory.setProperty(XMLInputFactory.IS_VALIDATING, false);

			xer = inputFactory.createXMLEventReader(in);

		} catch (FileNotFoundException e) {
			LOGGER.severe("XML-file not found! " +e.getMessage());
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
		catch (XMLStreamException e) {
			LOGGER.severe("Error in reading xml-file: stream error. "+e.getMessage());
		e.printStackTrace();
		}
	}

	/**
	 * Initializes the output.
	 */
	private void initOutput(){
		 try {
			// create an XMLOutputFactory
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			byteStream = new ByteArrayOutputStream();
			// create eventWriter
			xew = outputFactory.createXMLEventWriter(byteStream);

			xef = XMLEventFactory.newInstance();
			end = xef.createDTD("\n");
			xef.createDTD("\t");

		 } catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inquire saving type. !! This method is not completed.!!
	 * !!Waiting to testing and would users want to select for every MarkingLayer to overwrite !!
	 *
	 * @return the int ID of savingType
	 * @throws Exception the exception
	 */
	private int inquireSavingType() throws Exception{
		return ID.OVERWRITE; // overwrites always

	}

	/**
	 * Read and write.
	 *
	 * @throws Exception the exception
	 */
	private void readAndWrite() throws Exception{

		while(xer.hasNext()){
			XMLEvent event= xer.nextEvent();

			if(event.isStartElement()){
				se= event.asStartElement();
				if(se.getName().equals(XMLtags.qnameImageLayer)){
					String iLayerName= se.getAttributeByName(XMLtags.qnameImageName).getValue();
					ImageLayer iLayer=getImageLayerByName(iLayerName);
					if(iLayer != null){ // ImageLayer found -> save StartElement and go for markinglayers
						xew.add(se); // write StartElement
						xew.add(end);
						combineImageLayerContentWithXMLfile(iLayer); // write the whole ImageLayer data with combined to data from xml-file.
						removeImageLayer(iLayer); // saved ImageLayer -> remove from list


					}else{ // ImageLayer not found -> write only the StartElement to file
						xew.add(se); // startElement
					}

				}
				else{// if(se.getName().equals(qnameLayers)){ // layers
					xew.add(se); // startElement
				}

			}
			else
				if(event.isEndDocument()){
					// write rest imageLayers from LayersOfPath object
					// write end document tag to end
					xew.add(event);
					return;

				}
				else if(event.getEventType() == XMLEvent.DTD){
					LOGGER.fine("XML: DTD");
					xew.add(event);
					xew.add(end);

					}
					else // end elements
						if(event.isEndElement()){
	
							EndElement ee = event.asEndElement();
		
							if(ee.getName().equals(XMLtags.qnameLayers)){
								if(this.layersOfPath.getImageLayerList()!= null && this.layersOfPath.getImageLayerList().size()>0){
									Iterator<ImageLayer> iIterator=this.layersOfPath.getImageLayerList().iterator();
									while(iIterator.hasNext()){
										ImageLayer iLayer=iIterator.next();
										writeImageLayerFromImageLayer(iLayer);						
									}
								}
							}
							xew.add(ee);
	
						}
						else{// write all other events identically to file
							if(event.isStartDocument())
							{
								xew.add(event);
								xew.add(end);
							}
							else
								xew.add(event);
						}
		}
		LOGGER.fine("XML data written to memory successfully");
	}


	/**
	 * Removes the ImageLayer from ArrayList.
	 *
	 * @param iLayer the i layer
	 * @throws Exception the exception
	 */
	private void removeImageLayer(ImageLayer iLayer) throws Exception{
		if(iLayer != null && this.layersOfPath.getImageLayerList() != null
				&& this.layersOfPath.getImageLayerList().size()>0){
			this.layersOfPath.getImageLayerList().remove(iLayer);

		}
	}

	/**
	 * Skips reading MarkingLayer. Reads xml-document until end element od MarkingLayer is reached.
	 *
	 * @throws Exception the exception
	 */
	private void skipReadingMarkingLayer() throws Exception{
		try {
			while(xer.hasNext()){
				XMLEvent event=xer.nextEvent();
				if(event.isEndElement() && event.asEndElement().getName().equals(XMLtags.qnameMarkingLayer))
					return;
			}
		} catch (XMLStreamException e) {
			LOGGER.severe("Error in writing XML file: skipping Marking Layer produced error: " +e.getMessage());
		}

	}

	/**
	 * Starts the writing of given ImageLayer- objects into given XML-file. If file exist, the information is append or overWritten depending of
	 * existence of ImageLayers and MarkingLayers in XML-file.
	 * @param layersOfPathIn LayersOfPath, which contains the XML-file path where from read xml-data and where to save the data.
	 * @return Boolean value true if saving was made, false otherwise.
	 */
	public boolean startWritingProcess(LayersOfPath layersOfPathIn){
		try {
			this.layersOfPath =layersOfPathIn;
			this.unsavedMarkingLayers=new ArrayList<Integer>();
			if(this.layersOfPath != null && this.layersOfPath.getXmlpath() != null){
				File f= new File(this.layersOfPath.getXmlpath());


				// file already exists and it is right format -> update this file
				if(f.exists() && layersOfPath.getFileState() == ID.FILE_OK ){ // file exist and it is writable+valid
					initInput(this.layersOfPath.getXmlpath());
					initOutput();
					readAndWrite(); // saves as stream to byteStream
					closeInput();
					closeOutput();
				}
				else{ // file doesn't exist
					LOGGER.info("Creating file "+this.layersOfPath.getXmlpath());
					if(layersOfPath.getFileState() == ID.FILE_NEW_FILE)
						f.createNewFile();
					// init only output
					initOutput();
					// start writing
					writeNewDocument();
					closeOutput();
				}
				// save Stream to file
				this.layersOfPath=null;
				return writeStreamToFile(f);

			}
			this.layersOfPath=null;
			return false; // nothing saved

		} catch (Exception e) {
			LOGGER.severe("Error in writing Markings! "+ e.getMessage() );
			e.printStackTrace();
			this.layersOfPath=null;
			this.successfullySavedMarkingLayers.clear(); // the writing was not successfull
			return false;
		}
	}

	/**
	 * Writes grid data to xml file inside <grid> tags.
	 *
	 * @param gridProperty the GridProperties
	 * @throws Exception the exception
	 */
	private void writeGridToMarkingLayer(GridProperties gridProperty) throws Exception{
			// write starelement
			xew.add(xef.createStartElement("", "", XMLtags.grid));
			xew.add(end);

			xew.add(xef.createStartElement("", "", XMLtags.grid_on));
			if(gridProperty.isGridON())
			xew.add(xef.createCharacters(XMLtags.value_true));
			else
				xew.add(xef.createCharacters(XMLtags.value_false));
			xew.add(xef.createEndElement("", "", XMLtags.grid_on));
			xew.add(end);

			if(gridProperty.getRowLineYs() != null && gridProperty.getRowLineYs().size()>0 &&
					gridProperty.getColumnLineXs() != null && gridProperty.getColumnLineXs().size()>0){

				xew.add(xef.createStartElement("", "", XMLtags.lines));
				xew.add(end);
				// write vertical lines
				for (Iterator<Integer> iterator = gridProperty.getColumnLineXs().iterator(); iterator.hasNext();) {
					int x = (int) iterator.next();
					xew.add(xef.createStartElement("", "", XMLtags.x));
					xew.add(xef.createCharacters(""+x));
					xew.add(xef.createEndElement("", "", XMLtags.x));
					xew.add(end);
				}
				// write horizontal lines
				for (Iterator<Integer> iterator = gridProperty.getRowLineYs().iterator(); iterator.hasNext();) {
					int y = (int) iterator.next();
					xew.add(xef.createStartElement("", "", XMLtags.y));
					xew.add(xef.createCharacters(""+y));
					xew.add(xef.createEndElement("", "", XMLtags.y));
					xew.add(end);
				}

				xew.add(xef.createEndElement("", "", XMLtags.lines));
				xew.add(end);
			}

			if(gridProperty.getPositionedRectangleList() != null && gridProperty.getPositionedRectangleList().size()>0){
				xew.add(xef.createStartElement("", "", XMLtags.rectangles));
				xew.add(end);
				// write Rectangles of GRID
				for (Iterator<PositionedRectangle> iterator = gridProperty.getPositionedRectangleList().iterator(); iterator.hasNext();) {
					PositionedRectangle pRectangle = iterator.next();
					String selection=XMLtags.value_false;
					if(pRectangle.isSelected())
						selection=XMLtags.value_true;
					xew.add(xef.createStartElement("", "", XMLtags.rec));
					att = xef.createAttribute(XMLtags.selected, selection);
					xew.add(att);
					// write attributes
					xew.add(xef.createAttribute(XMLtags.column, ""+pRectangle.getColumn()));
					xew.add(xef.createAttribute(XMLtags.row, ""+pRectangle.getRow()));
					xew.add(xef.createAttribute(XMLtags.x, ""+pRectangle.x));
					xew.add(xef.createAttribute(XMLtags.y, ""+pRectangle.y));
					xew.add(xef.createAttribute(XMLtags.width, ""+pRectangle.width));
					xew.add(xef.createAttribute(XMLtags.height, ""+pRectangle.height));
					xew.add(xef.createEndElement("", "", XMLtags.rec));
					xew.add(end);
				}

				xew.add(xef.createEndElement("", "", XMLtags.rectangles));
				xew.add(end);
			}

			xew.add(xef.createEndElement("", "", XMLtags.grid));
			xew.add(end);

	}

	/**
	 *  Writes the information of single ImageLayer-object to XML file inside <imagelayer> tags.
	 *
	 * @param iLayer The ImageLayer-object, which information is written to xml-file.
	 * @throws Exception the exception
	 */
	private void writeImageLayerFromImageLayer(ImageLayer iLayer) throws Exception{

		try {
			se= xef.createStartElement("", "", XMLtags.imagelayer);
			xew.add(se);
			att = xef.createAttribute(XMLtags.imagename, iLayer.getImageFileName());
			xew.add(att);
			xew.add(end);

			if(iLayer.getMarkingLayers() != null && iLayer.getMarkingLayers().size()>0){

				Iterator<MarkingLayer> mIterator= iLayer.getMarkingLayers().iterator();
				while(mIterator.hasNext()){
					MarkingLayer ml=mIterator.next();
					if(writeMarkingLayer(ml)){
						mIterator.remove(); // saved the xml -> remove from list
						successfullySavedMarkingLayers.add(ml.getLayerID());
					}
					else{ // error in saving
						unsavedMarkingLayers.add(ml.getLayerID());
					}
				}
			}

			//  add endElement of Imagelayer
			ee=xef.createEndElement("", "", XMLtags.imagelayer);
			xew.add(ee);
			xew.add(end);


		} catch (XMLStreamException e) {
			LOGGER.warning("Error in saving markings of ImageLayer "+iLayer.getImageFileName()+ " to xml-file");

		}

	}

	/**
	 *  Writes all information of MarkingLayer-object to XML-file inside <markinglayer> tags.
	 *
	 * @param mLayer MarkingLayer-object, which information is saved to XML-file.
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private boolean writeMarkingLayer(MarkingLayer mLayer){
		try {
			//xew.add(tab);
			se= xef.createStartElement("", "", XMLtags.markinglayer);
			xew.add(se);
			att = xef.createAttribute(XMLtags.markingname, mLayer.getLayerName());
			xew.add(att);
			xew.add(end);


			// add color
			if(mLayer.getColor() != null){
				xew.add(xef.createStartElement("", "", XMLtags.color));
				xew.add(xef.createCharacters(mLayer.getStringColor()));
				xew.add(xef.createEndElement("", "", XMLtags.color));
				xew.add(end);
			}
			// add opacity
			if(mLayer.getOpacity() >0){
				xew.add(xef.createStartElement("", "", XMLtags.opacity));
				xew.add(xef.createCharacters(""+mLayer.getOpacity()));
				xew.add(xef.createEndElement("", "", XMLtags.opacity));
				xew.add(end);
			}
			// add size
			if(mLayer.getSize() >0){
				xew.add(xef.createStartElement("", "", XMLtags.size));
				xew.add(xef.createCharacters(""+mLayer.getSize()));
				xew.add(xef.createEndElement("", "", XMLtags.size));
				xew.add(end);
			}

			// add shape
			if(mLayer.getShapeID() >0){
				xew.add(xef.createStartElement("", "", XMLtags.shape));
				xew.add(xef.createCharacters(""+mLayer.getShapeID()));
				xew.add(xef.createEndElement("", "", XMLtags.shape));
				xew.add(end);
			}


			// add thickness
			if(mLayer.getThickness()>0){
				xew.add(xef.createStartElement("", "", XMLtags.thickness));
				xew.add(xef.createCharacters(""+mLayer.getThickness()));
				xew.add(xef.createEndElement("", "", XMLtags.thickness));
				xew.add(end);
			}

			// add coordinates
			if(mLayer.getCoordinateList() != null && mLayer.getCoordinateList().size()>0){
				xew.add(xef.createStartElement("", "", XMLtags.coordinates));
				xew.add(end);

				Iterator<Point> cIterator = mLayer.getCoordinateList().iterator();
				while(cIterator.hasNext()){
					Point p = cIterator.next();
					xew.add(xef.createStartElement("", "", XMLtags.singlecoordinate));
					xew.add(xef.createCharacters(""+p.x+","+p.y));
					xew.add(xef.createEndElement("", "", XMLtags.singlecoordinate));
					xew.add(end);
				}
				xew.add(xef.createEndElement("", "", XMLtags.coordinates));
				xew.add(end);
			}
			// add GridProperties
			if(mLayer.getGridProperties() != null){
				writeGridToMarkingLayer(mLayer.getGridProperties());
			}



				//  add endElement of MarkingLayer
				ee=xef.createEndElement("", "", XMLtags.markinglayer);
				xew.add(ee);
				xew.add(end);

				return true;
		} catch (Exception e) {
			LOGGER.warning("Error in saving markings to xml-file");
			return false;
		}

	}

	/**
	 * Write marking layer from XML-file.
	 *
	 * @param firstElement the first element
	 */
	private void writeMarkingLayerFromXMLfile(StartElement firstElement){

		try {
			xew.add(firstElement); // write the startelement
			while(xer.hasNext()){
				XMLEvent event=xer.nextEvent();
				xew.add(event);
				if(event.isEndElement() && event.asEndElement().getName().equals(XMLtags.qnameMarkingLayer))
					return;
			}
		} catch (XMLStreamException e) {
			LOGGER.severe("Error in writing XML file: copying markinglayer from file produced error: " +e.getMessage());
		}
	}

	/**
	 *  Writes all ImageLayer-objects of layersOfPath to XML-file.
	 */
	private void writeNewDocument() throws Exception{
		// create
		StartDocument startDocument = xef.createStartDocument();
		xew.add(startDocument);
		xew.add(end);
		xew.add(getDTDevent());
		xew.add(end);
		xew.add(xef.createStartElement("", "", XMLtags.layers));
		xew.add(end);
	
	   // add inserting ImageLayers
	
	   Iterator<ImageLayer> iterator = this.layersOfPath.getImageLayerList().iterator();
	   while(iterator.hasNext()){
		   ImageLayer iLayer = iterator.next();
		  writeImageLayerFromImageLayer(iLayer);
	   }
	
	   xew.add(xef.createEndElement("", "", XMLtags.layers));
	   xew.add(end);
	   EndDocument endDocument=xef.createEndDocument();
	   xew.add(endDocument);



	}

	/**
	 * Writes stream to file. The xml-code is given as ByteArrayOutPutStream and saved to xml-file.
	 *
	 * @param file the File to be saved.
	 * @return true, if saving successful
	 */
	private boolean writeStreamToFile(File file){
		try {
			
			rm=new XMLreadManager();
			if(rm.isStreamValid(byteStream)){
				FileOutputStream fStreamIn = new FileOutputStream(file);
				fStreamIn.write(byteStream.toByteArray());
				fStreamIn.flush();
				fStreamIn.close();
				byteStream.close();
				return true;
			}
			else{
				byteStream.close();
				return false;
			}
		}
		 catch (Exception e) {
			 LOGGER.severe("Error in writingStream to file: " +e.getMessage());
			return false;
		}
	}

}

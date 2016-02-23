package gui.saving.ImageSet;

import information.ImageLayer;
import information.MarkingLayer;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import operators.ImageCreator;
import gui.GUI;
import gui.saving.ExportResults;
import gui.saving.image.ExImaSingleImagePanel;

/**
 * The Class SelectAndCreateImageFiles extends ExportResult to export image files.
 */
public class SelectAndCreateImageFiles extends ExportResults {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6587765426740803107L;
	
	/** The created images. The list of BUfferedImageWithName -objects. */
	private ArrayList<BufferedImageWithName> createdImages;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	
	/**
	 * Instantiates a new SelectAndCreateImageFiles.
	 *
	 * @param d the d
	 * @param gui the gui
	 * @param iList the i list
	 * @param savingTypeID the saving type id
	 */
	public SelectAndCreateImageFiles(JDialog d, GUI gui,ArrayList<ImageLayer> iList, int savingTypeID) {
		super(d, gui, iList, savingTypeID);
	}
	
	/**
	 * Class constructor. Shows window where user can select ImageLayers and MarkingLayers to be exported.
	 * @param frame parent JFrame
	 * @param gui GUI object of Graphical interface
	 * @param iList	ArrayList of ImageLayers that are opened in main program
	 * @param savingTypeID int type of savingType
	 */
	public SelectAndCreateImageFiles(JFrame frame, GUI gui,ArrayList<ImageLayer> iList, int savingTypeID) {
		super(frame, gui, iList, savingTypeID);
		
	}

	/**
	 * Returns the created buffered images.
	 *
	 * @return the created buffered images
	 * @throws Exception the exception
	 */
	public ArrayList<BufferedImageWithName> getCreatedBufferedImages() throws Exception{
		return this.createdImages;
	}

	/* (non-Javadoc)
	 * @see gui.saving.ExportResults#initBrowsingPanelWithLabel()
	 */
	protected JPanel initBrowsingPanelWithLabel() throws Exception{
		return null;
	}

	/* (non-Javadoc)
	 * @see gui.saving.ExportResults#initImageViewPanelWithTitle()
	 */
	protected JPanel initImageViewPanelWithTitle() throws Exception{
		return initImageViewPanel("Select ImageLayer and MarkingLayers");
	}

	/* (non-Javadoc)
	 * @see gui.saving.ExportResults#initSaveButtonWithTitle()
	 */
	protected JPanel initSaveButtonWithTitle() throws Exception{
		return initSaveButton("Create Images");
	}

	/* (non-Javadoc)
	 * @see gui.saving.ExportResults#startSavingProcess(int, int)
	 */
	protected void startSavingProcess(int exportID, int exportType){
		try {
			createdImages=new ArrayList<BufferedImageWithName>();
				double sizeMultiplier=gui.getSizeMultiplier();
				ImageCreator imageCreator=new ImageCreator(sizeMultiplier, this.gui.taskManager);
				//go through all panels and pick imageLayers and MarkingLayers which are taken to images
				Component[] imPanelList= imageScrollPanel.getComponents();
				if(imPanelList != null && imPanelList.length>0 ){
					// create new LayersOfPath objects and add them to list
					 for (int i = 0; i < imPanelList.length; i++) {
						ExImaSingleImagePanel imp= (ExImaSingleImagePanel)imPanelList[i];
						
						ArrayList<MarkingLayer> selectedMarkingLayers = imp.getAllSelectedMarkingLayers();
						if(selectedMarkingLayers != null && selectedMarkingLayers.size()>0){
							imp.updateGridDrawing();
						BufferedImage created = imageCreator.createBufferedImage(imp.getImageLayer(), imp.getAllSelectedMarkingLayerIDs());
						if(created != null) // creation successful -> add to list.
							this.createdImages.add(new BufferedImageWithName(created, imp.getImageLayerName()));

						}
					 }
				}
				cancelSelected();

			} catch (Exception e) {
				
				LOGGER.severe("Error in starting saving in CreateImageFiles!");
				
				e.printStackTrace();
			}
	}

}

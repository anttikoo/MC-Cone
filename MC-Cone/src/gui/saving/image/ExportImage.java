package gui.saving.image;

import gui.GUI;
import gui.ShadyMessageDialog;
import gui.saving.SaverDialog;
import gui.saving.SingleImagePanel;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import operators.ImageCreator;

/**
 * The Class ExportImage. Extends SaverDialog for opening window for selecting images and MarkingLayers to be exported.
 */
public class ExportImage extends SaverDialog{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6547300499249743511L;
	
	/** The Constant LOGGER. */
	final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	
	
	/**
	 * Instantiates a new object of ExportImage
	 *
	 * @param frame the frame
	 * @param gui the gui
	 * @param iList the i list
	 */
	public ExportImage(JFrame frame, GUI gui, ArrayList<ImageLayer> iList) {
		super(frame, gui, iList, ID.EXPORT_IMAGE);
	}

	
	
	
	
	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#checkFileValidity(java.io.File)
	 */
	protected int checkFileValidity(File file) throws Exception{
		int validity = super.checkFileValidity(file);
		
		if(isFilePathFound(file.getAbsolutePath())){
			return ID.FILE_FILE_SELECT_BY_ANOTHER;
		}
		return validity;
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#createSingleImagePanel(information.ImageLayer)
	 */
	protected SingleImagePanel createSingleImagePanel(ImageLayer layer) throws Exception{
		return new ExImaSingleImagePanel(layer,this);
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#initBrowsingPanel(int, java.lang.String)
	 */
	protected JPanel initBrowsingPanel(int savingID, String label) throws Exception{
		return null;
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#initBrowsingPanelWithLabel()
	 */
	protected JPanel initBrowsingPanelWithLabel() throws Exception{
		return null;//initBrowsingPanel(0,"Show Grid of MarkingLayers if used.");
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#initImageViewPanelWithTitle()
	 */
	protected JPanel initImageViewPanelWithTitle() throws Exception{
		return initImageViewPanel("Export Image With Markings");
	}
	
	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#initSaveButtonWithTitle()
	 */
	protected JPanel initSaveButtonWithTitle() throws Exception{
		return initSaveButton("Export Image");
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#selectPathForSingleImagePanel(java.lang.String, int)
	 */
	protected void selectPathForSingleImagePanel(String properPath, int panelID) throws Exception{
		if(this.selectFileDialog == null)
			initSelectFileDialog();
		this.selectFileDialog.setProperFolderPathForSaving(properPath);
		this.selectFileDialog.setVisible(true);

		if(this.selectFileDialog.fileWritingType != ID.CANCEL && this.selectFileDialog.fileWritingType != ID.ERROR){
			setFileWritingType(this.selectFileDialog.getFileWritingType());
			setFilePathForSingleImagePanel(this.selectFileDialog.getSelectedFilePath(), panelID);
		}
			this.selectFileDialog.setVisible(false);
			this.selectFileDialog.dispose();
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#setSaveButtonEnabledByFileValidity(int)
	 */
	protected void setSaveButtonEnabledByFileValidity(int vID){ //vID not used
		try {
			// go through all SingleImagePanels and if any has fileValidity ok -> enable saveButton
			Component[] imPanelList= imageScrollPanel.getComponents();

			if(imPanelList != null && imPanelList.length>0){
				 for (int i = 0; i < imPanelList.length; i++) {
					 int filVal=((SingleImagePanel)imPanelList[i]).getFileValidity();
					if(filVal==ID.FILE_OK || filVal == ID.FILE_NEW_FILE){
						this.saveJButton.setEnabled(true);
						return;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.severe("Error in setting save button enabled when checkded file validity!");
			e.printStackTrace();
		}
		this.saveJButton.setEnabled(false);
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#startSavingProcess(int, int)
	 */
	protected void startSavingProcess(int exportID, int exportType){
		try {
			boolean wantedExport=false;
			ShadyMessageDialog dialog;
			setAllMarkingLayerBackgroundsToDefault();
			double sizeMultiplier=gui.getSizeMultiplier();
			ImageCreator imageCreator=new ImageCreator(sizeMultiplier, this.gui.taskManager);
			boolean filePathAccepted=false;
			boolean hasSelectedMarkingLayers=false;

			ArrayList<Integer> successfullIDs=new ArrayList<Integer>();

			//go through all panels and pick imageLayers and MarkingLayers which will be saved
			Component[] imPanelList= imageScrollPanel.getComponents();
			if(imPanelList != null && imPanelList.length>0 ){
				// create new LayersOfPath objects and add them to list
				 for (int i = 0; i < imPanelList.length; i++) {
					ExImaSingleImagePanel imp= (ExImaSingleImagePanel)imPanelList[i];
					if(imp.isSelected()){
						wantedExport=true;
						ArrayList<MarkingLayer> selectedMarkingLayers = imp.getAllSelectedMarkingLayers();
						// check first is the file path accepted
						if( (imp.getFileValidity() == ID.FILE_OK || imp.getFileValidity() == ID.FILE_NEW_FILE))
							filePathAccepted=true;
						if(selectedMarkingLayers != null && selectedMarkingLayers.size()>0 ){
							hasSelectedMarkingLayers=true;
							if( (imp.getFileValidity() == ID.FILE_OK || imp.getFileValidity() == ID.FILE_NEW_FILE)){
								// update the grid drawing to MarkingLayers of ImageLayer in imp
								imp.updateGridDrawing();
								ArrayList<Integer> savedMarkingIDList=imageCreator.createImage(imp.getImageLayer(), imp.getAllSelectedMarkingLayerIDs(), imp.getProperFilePathForSaving());
								if(savedMarkingIDList != null && savedMarkingIDList.size()>0){
									successfullIDs.addAll(savedMarkingIDList);
									// save to ImageLayer the exporting path -> remembers next time when starting exporting
									imp.getImageLayer().setExportImagePath(imp.getProperFilePathForSaving());

								}
							}
						}
				 	}

				 }

				 setSuccesfullSavingBackgrounds(successfullIDs);

				if(successfullIDs.size() >0){
					dialog = new ShadyMessageDialog(this, "Exporting succesfull", "Successfully exported MarkingLayers are at green background.  ", ID.OK, this);
					dialog.showDialog();

				}
				else{
					String info =  "unacceptable file path and lack of selected MarkingLayers.";
					if(wantedExport){
						if(filePathAccepted && !hasSelectedMarkingLayers){
							info =  "lack of selected MarkingLayers.";
						}
						else{
							if(!filePathAccepted && hasSelectedMarkingLayers)
							info =  "unacceptable file path.";
						}
					}
					else
						info =  " no any images selected.";
					dialog = new ShadyMessageDialog(this, "No Images Exported", "No any images exported possible due to "+info, ID.OK, this);
					dialog.showDialog();
				}
				dialog=null;

			}


		} catch (Exception e) {
			LOGGER.severe("Error in Exporting results: "+ e.getMessage());
			e.printStackTrace();
		}


	}



}

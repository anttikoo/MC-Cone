package gui.saving;

import gui.GUI;
import gui.ShadyMessageDialog;
import gui.file.FileManager;
import information.ID;
import information.ImageLayer;
import information.LayersOfPath;
import information.MarkingLayer;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;

import operators.XMLreadManager;
import operators.XMLwriteManager;

/**
 * The Class SaveMarkings extends SaverDialog. 
 * Opens dialog window for selecting ImageLayers and MarkingLayers to save data in xml-file.
 * 
 */
public class SaveMarkings extends SaverDialog{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1239754004585220377L;

	/** The xml writing manager. */
	private XMLwriteManager xmlWriteManager;
	
	/** The names of ImageLayers for file selection. */
	private ArrayList<String> imageLayersNamesForFileSelection;

	/**
	 * Instantiates a new object of SaveMarkings.
	 *
	 * @param frame the parent JFrame
	 * @param gui the GUI
	 * @param iList the list of ImageLayers
	 */
	public SaveMarkings(JFrame frame, GUI gui, ArrayList<ImageLayer> iList) {
		super(frame, gui, iList,ID.SAVE_MARKINGS);
		this.imageLayersNamesForFileSelection=null;
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#checkFileValidity(java.io.File)
	 */
	protected int checkFileValidity(File file){
		return FileManager.checkFile(file, SelectFileDialog.createFileFilterList(this.savingType), true);
	}

	/**
	 * Collect all names of ImageLayers.
	 *
	 * @return the ArrayList of names of ImageLayers.
	 * @throws Exception the exception
	 */
	private ArrayList<String> collectAllImageLayerNames() throws Exception{
		ArrayList<String> list=new ArrayList<String>();
		Component[] imPanelList= imageScrollPanel.getComponents();
		//go through all panels of ImageLayers
		if(imPanelList != null && imPanelList.length>0){
			 for (int i = 0; i < imPanelList.length; i++) {
				list.add(((SingleImagePanel)imPanelList[i]).getImageLayerName());
			}
		}

		return list;
	}

	/**
	 * Returns a name of selected ImageLayer given by layer ID.
	 *
	 * @param id the id of ImageLayer.
	 * @return the ArrayList containing single name of selected ImageLayer.
	 * @throws Exception the exception
	 */
	private ArrayList<String> collectSingleSelectedImageLayerName(int id) throws Exception{
		ArrayList<String> list=new ArrayList<String>();
		Component[] imPanelList= imageScrollPanel.getComponents();

		if(imPanelList != null && imPanelList.length>0){
			 for (int i = 0; i < imPanelList.length; i++) {
				if(((SingleImagePanel)imPanelList[i]).hasSelectedMarkingLayer() && ((SingleImagePanel)imPanelList[i]).getLayerID() == id){
					list.add(((SingleImagePanel)imPanelList[i]).getImageLayerName());
				}
			}
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#createSingleImagePanel(information.ImageLayer)
	 */
	protected SingleImagePanel createSingleImagePanel(ImageLayer layer) throws Exception{
		return new SMSingleImagePanel(layer, this);
	}


	/**
	 * Checks for image layers found by given file path.
	 *
	 * @param fileName the path of the file.
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean hasImageLayersFound(String fileName) throws Exception{
		XMLreadManager readManager=new XMLreadManager();
	//	return XMLreadManager.foundImageLayer(file, imageLayersNamesForFileSelection);
		return readManager.foundImageLayer(fileName, imageLayersNamesForFileSelection);
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#initBrowsingPanelWithLabel()
	 */
	protected JPanel initBrowsingPanelWithLabel() throws Exception{
		return initBrowsingPanel(this.savingType, "Browse file for all");
	}


	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#initImageViewPanelWithTitle()
	 */
	public JPanel initImageViewPanelWithTitle() throws Exception{
		return initImageViewPanel("Save Markings");
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#initSaveButtonWithTitle()
	 */
	protected JPanel initSaveButtonWithTitle() throws Exception{
		return initSaveButton("Save Markings");
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#initSelectFileDialog()
	 */
	protected void initSelectFileDialog() throws Exception{
		String path = gui.getPresentFolder();
		if(path== null)
			path = System.getProperty("user.home");
		this.selectFileDialog = new MarkingsSelectFileDialog(this.gui, path, this.backPanel, this.savingType);
	}
	
	
	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#selectPathForAllSingleImagePanels()
	 */
	protected void selectPathForAllSingleImagePanels() throws Exception{
		if(this.selectFileDialog == null)
			initSelectFileDialog();
		//get proper path of first SingleImagePanel
		String path = getfirstProperPathOfSingleImagePanels();
		this.selectFileDialog.setProperFilePathForSaving(path);
		this.selectFileDialog.setImageLayerNamesForXMLSearch(collectAllImageLayerNames());
	//	this.selectFileDialog.setMultiFileSelectionEnabled(false);
		this.selectFileDialog.setVisible(true);
		if(this.selectFileDialog.fileWritingType != ID.CANCEL && this.selectFileDialog.fileWritingType != ID.ERROR){
			setFileWritingType(this.selectFileDialog.getFileWritingType());

			setFilePathsForAll(this.selectFileDialog.getSelectedFilePath());
			setMarkingLayerBackgroundsToDefault(ID.IMAGELAYER_UNDEFINED); // sets for all imagePanels

		}
		this.selectFileDialog.setVisible(false);
		this.selectFileDialog.dispose();
		this.imageLayersNamesForFileSelection=null;


	}
	
	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#selectPathForSingleImagePanel(java.lang.String, int)
	 */
	protected void selectPathForSingleImagePanel(String properPath, int panelID) throws Exception{
		if(this.selectFileDialog == null)
			initSelectFileDialog();
		this.selectFileDialog.setProperFilePathForSaving(properPath);
		this.selectFileDialog.setImageLayerNamesForXMLSearch(collectSingleSelectedImageLayerName(panelID));
		this.selectFileDialog.setVisible(true);

		if(this.selectFileDialog.fileWritingType != ID.CANCEL && this.selectFileDialog.fileWritingType != ID.ERROR){
			setFileWritingType(this.selectFileDialog.getFileWritingType());
			setFilePathForSingleImagePanel(this.selectFileDialog.getSelectedFilePath(), panelID);
			setMarkingLayerBackgroundsToDefault(panelID); // sets for single imagepanel
		}
			this.selectFileDialog.setVisible(false);
		this.selectFileDialog.dispose();
		this.imageLayersNamesForFileSelection=null;

	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#setSaveButtonEnabledByFileValidity(int)
	 */
	protected void setSaveButtonEnabledByFileValidity(int vID) throws Exception{ // vID is not used
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
		this.saveJButton.setEnabled(false);
	}

	
	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#startSavingProcess(int, int)
	 */
	protected void startSavingProcess(int savingID, int exportType){
		boolean wantedToSave=false;
		try {
			ShadyMessageDialog dialog;
			setAllMarkingLayerBackgroundsToDefault();
			ArrayList<Integer> successFullysavedLayers=new ArrayList<Integer>();
			ArrayList<LayersOfPath> layerOfPathList=new ArrayList<LayersOfPath>();
			//go through all panels and pick imageLayers and MarkingLayers which will be saved
			Component[] imPanelList= imageScrollPanel.getComponents();
			if(imPanelList != null && imPanelList.length>0 ){
				// create new LayersOfPath objects and add them to list
				 for (int i = 0; i < imPanelList.length; i++) {
					 SingleImagePanel imp= (SingleImagePanel)imPanelList[i];
					 if(imp.isSelected()){
						 wantedToSave=true;
						 if(imp.getFileValidity() == ID.FILE_OK || imp.getFileValidity() == ID.FILE_NEW_FILE){
							ArrayList<MarkingLayer> selectedMarkingLayers=imp.getAllSelectedMarkingLayers();
							if(selectedMarkingLayers != null && selectedMarkingLayers.size()>0){
	
								// create new Imagelayer
								ImageLayer newIlayer=new ImageLayer(imp.getImagePath());
								// add selected MarkingLayers
								newIlayer.setMarkingLayerList(selectedMarkingLayers);
	
								if(layerOfPathList.size()>0){ // contains already some objects
									boolean madeSave=false;
									// go through objects of pathLayerList
									Iterator<LayersOfPath> pathLayerIterator= layerOfPathList.iterator();
									while(pathLayerIterator.hasNext()){
	
										LayersOfPath lop = (LayersOfPath)pathLayerIterator.next();
										if(lop.getXmlpath().equals(imp.getProperFilePathForSaving())){
											 // found LayersOfPath with right xml path -> add the ImageLayer here
											lop.addImageLayer(newIlayer);
											madeSave=true;
										}
	
									}
									if(!madeSave){ // not found the proper LayersOfPath for the ImageLayer -> create new
										LayersOfPath newLOP = new LayersOfPath(imp.getProperFilePathForSaving(), imp.getFileValidity());
										newLOP.addImageLayer(newIlayer);
										layerOfPathList.add(newLOP);
									}
								}
								else{ // empty
									// create new LayersOfPath object
									LayersOfPath newLOP = new LayersOfPath(imp.getProperFilePathForSaving(), imp.getFileValidity());
									newLOP.addImageLayer(newIlayer);
									layerOfPathList.add(newLOP);
	
								}
	
							}else{
								LOGGER.warning("No markings selected from ImageLayer: "+imp.getImageLayerName());
							}
	
						}else{
							LOGGER.warning("File Saving Path: "+imp.getProperFilePathForSaving()+ " is not valid. Change saving file.");
						}				
				 	}
				}
			}
			// all selected MarkingLayers are added to ImageLayers which are added to LayersOfPath -objects
			// start saving content of each LayersOfPath to xml-file
			if(layerOfPathList.size()>0){

				Iterator<LayersOfPath> layIterator= layerOfPathList.iterator();
				while(layIterator.hasNext()){
					LayersOfPath lop = layIterator.next();
					if(xmlWriteManager == null){
						xmlWriteManager=new XMLwriteManager();
					}
					if(xmlWriteManager.startWritingProcess(lop)){
						successFullysavedLayers.addAll(xmlWriteManager.getSuccessfullySavedMarkingLayers());
						updateImageLayerXMLpath(lop, xmlWriteManager.getSuccessfullySavedMarkingLayers());
					}			
				}

				setSuccesfullSavingBackgrounds(successFullysavedLayers);
				if(successFullysavedLayers.size()>0){
			//		gui.setMadeChanges(false); // the user has saved at least one MarkingLayer -> no asking to save when quitting MC-Cone (if no changes made before quit)
					gui.setSuccessfullyMadeSavings(successFullysavedLayers);
					if(notInformedSuccessfullSaving){
						dialog = new ShadyMessageDialog(this, "Saving succesfull", "Successfully saved markings are shown green.  ", ID.OK, this);
						dialog.showDialog();
					}
				}
			}
			else{
				if(wantedToSave){
					LOGGER.warning("No Markings selected to save!");
					dialog = new ShadyMessageDialog(new JFrame(), "Saving not succesfull", "Check the file path is acceptable and at least one MarkingLayer is selected  ", ID.OK, this);
					dialog.showDialog();
				}
				else{
					LOGGER.warning("No any ImageLayers selected to save!");
					dialog = new ShadyMessageDialog(new JFrame(), "Not selected anything to save", "Select at least one ImageLayer and MarkingLayer to be saved.", ID.OK, this);
					dialog.showDialog();
				}
			}
			dialog=null;
			
		} catch (Exception e) {
			LOGGER.severe("Error in starting to save Markings: "+ e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Sets the xml-file path to ImageLayer-object. 
	 * Paths are given in LayersOfPath -object containing path and list of ImageLayers which data has been saved successfully.
	 *
	 * @param lop LayersOfPath object
	 * @param successfullysavedMarkings IDs of the successfully saved MarkingLayers.
	 * @throws Exception the exception
	 */
	private void updateImageLayerXMLpath(LayersOfPath lop, ArrayList<Integer> successfullysavedMarkings) throws Exception{

		Iterator<ImageLayer> iIterator = this.imageLayerList.iterator();
		while(iIterator.hasNext()){
			ImageLayer iLayer=iIterator.next();
			for (Iterator<Integer> iterator = successfullysavedMarkings.iterator(); iterator.hasNext();) {
				int mLayerID= (int) iterator.next();
				if(iLayer.hasMarkingLayer(mLayerID)){

					iLayer.setMarkingsFilePath(lop.getXmlpath());
					break;
				}

			}
		}
	}
	
	/**
	 * Updates saveButton state by calling method for checking is any file path for saving valid.
	 *
	 * @throws Exception the exception
	 */
	protected void updateSaveButtonState() throws Exception{
		setSaveButtonEnabledByFileValidity(ID.UNDEFINED); //
	}
}

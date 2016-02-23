package gui.saving;

import gui.saving.SaverDialog;
import gui.Color_schema;
import gui.GUI;
import gui.saving.SelectFileDialog;
import gui.saving.image.ExImaSingleImagePanel;
import gui.MouseListenerCreator;
import gui.ShadyMessageDialog;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The Class ExportResults a dialog for exporting selected results.
 */
public class ExportResults extends SaverDialog{ 
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2357110452039051014L;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/** The byte stream for to export. */
	private ByteArrayOutputStream byteStream;
	
	/** The JLabel for saving path. */
	private JLabel savingPathJLabel;

	
	/**
	 * Instantiates a new dialog for exporting results. Parent Object is JDialog.
	 *
	 * @param d oarent JDialog
	 * @param gui the gui
	 * @param iList list of ImageLayers which data to be saved.
	 * @param eID type of export (csv, txt, clipboard)
	 */
	public ExportResults(JDialog d, GUI gui, ArrayList<ImageLayer> iList, int eID){
		super(d, gui, iList, eID);
	}
	
	/**
	 * Instantiates a new dialog for exporting results. Parent Object is JFrame
	 *
	 * @param frame the frame
	 * @param gui the gui
	 * @param iList list of ImageLayers which data to be saved.
	 * @param eID type of export (csv, txt, clipboard)
	 */
	public ExportResults(JFrame frame, GUI gui, ArrayList<ImageLayer> iList, int eID){
		super(frame, gui, iList, eID);
	}


	/**
	 * Copies result bytestream to clipboard.
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private boolean copyToClipboard() throws Exception{
		String myString = byteStream.toString();
		if(myString != null && myString.length()>0){
			StringSelection stringSelection = new StringSelection (myString);
			Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
			clpbrd.setContents (stringSelection, null);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#createSingleImagePanel(information.ImageLayer)
	 */
	protected SingleImagePanel createSingleImagePanel(ImageLayer layer) throws Exception{
		if(this.savingType == ID.EXPORT_PREVIEW_IMAGES)
			return new ExImaSingleImagePanel(layer, this);
		else
			return new ExSingleImagePanel(layer,this);
	}

	/**
	 * Returns the proper file path for saving.
	 *
	 * @param iLayer the ImageLayer
	 * @return the proper file path String for saving
	 * @throws Exception the exception
	 */
	protected String getProperFilePathForSaving(ImageLayer iLayer) throws Exception{
		if(iLayer != null){
			if(iLayer.getMarkingsFilePath() != null && iLayer.getMarkingsFilePath().length()>0)
					return iLayer.getMarkingsFilePath(); // give the markingsFile which has been used to import markings

			else
				if(iLayer.getImageFilePath() != null && iLayer.getImageFilePath().length()>0){
					return  iLayer.getFolderOfImage(); // just give folder

				}
		}
			return System.getProperty("user.home");
	}


	/**
	 * Returns the selected button text.
	 *
	 * @param buttonGroup the button group
	 * @return the selected button text
	 * @throws Exception the exception
	 */
	public String getSelectedButtonText(ButtonGroup buttonGroup) throws Exception {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
            	LOGGER.fine("found selected: "+ button.getActionCommand());
                return button.getActionCommand();
            }
        }

        return null;
    }




		/**
		 * Returns the title string of dialog.
		 *
		 * @param id the id
		 * @return the title string
		 * @throws Exception the exception
		 */
		private String getTitleString(int id) throws Exception{
			if (id== ID.FILE_TYPE_TEXT_FILE)
				return "EXPORT RESULTS TO TAB-DELIMITED TEXT FILE";
			else if (id== ID.FILE_TYPE_CSV)
					return "EXPORT RESULTS TO CSV-FILE";
				else if (id== ID.CLIPBOARD)
					return "EXPORT RESULTS TO CLIPBOARD";
				else if (id== ID.EXPORT_IMAGE)
					return "EXPORT IMAGES WITH MARKINGS";
			return null;


		}


		/**
		 * Checks for selected MarkingLayers in below image layers.
		 *
		 * @param startIndex the start index
		 * @return true, if successful
		 * @throws Exception the exception
		 */
		private boolean hasSelectedMarkinglayersInBelowImageLayers(int startIndex) throws Exception{
			Component[] imPanelList= imageScrollPanel.getComponents();
			for (int i = startIndex; i < imPanelList.length; i++) {
				SingleImagePanel imp= (SingleImagePanel)imPanelList[i];
				ArrayList<MarkingLayer> selectedMarkingLayers = imp.getAllSelectedMarkingLayers();
				if(selectedMarkingLayers != null && selectedMarkingLayers.size()>0)
					return true;
			}
			return false;
		}



	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#initBrowsingPanel(int, java.lang.String)
	 */
	protected JPanel initBrowsingPanel(int exportID, String label){
		try {
			JPanel downBrowsingPanel = new JPanel();
			downBrowsingPanel.setLayout(new BoxLayout(downBrowsingPanel, BoxLayout.LINE_AXIS));
			downBrowsingPanel.setBackground(Color_schema.dark_30);
			downBrowsingPanel.setMaximumSize(new Dimension(5000, 50));
			downBrowsingPanel.setPreferredSize(new Dimension(800, 50));
			downBrowsingPanel.setMinimumSize(new Dimension(200, 50));
			JLabel saveToLabel = new JLabel("Save to file: ");
			saveToLabel.setFont(new Font("Consolas", Font.BOLD,16));
			downBrowsingPanel.add(Box.createRigidArea(new Dimension(20,0)));
			downBrowsingPanel.add(saveToLabel);
			downBrowsingPanel.add(Box.createRigidArea(new Dimension(20,0)));
			savingPathJLabel = new JLabel("");
			savingPathJLabel.setFont(new Font("Consolas", Font.PLAIN,16));
			downBrowsingPanel.add(savingPathJLabel);
			downBrowsingPanel.add(Box.createHorizontalGlue());


			// Select file path for all ImageLayers
			selectFileJButton = new JButton(label);
			selectFileJButton.setPreferredSize(new Dimension(150,30));
			selectFileJButton.setMaximumSize(new Dimension(150,30));
			selectFileJButton.setContentAreaFilled(false);
			selectFileJButton.setBackground(Color_schema.dark_20);
			selectFileJButton.setFont(new Font("Consolas", Font.BOLD,16));
			selectFileJButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					try {
						selectPathForAllSingleImagePanels();
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
				}
			});
			selectFileJButton.setFocusable(false);
			MouseListenerCreator.addMouseListenerToNormalButtons(selectFileJButton);
			downBrowsingPanel.add(Box.createRigidArea(new Dimension(20,0)));
			downBrowsingPanel.add(selectFileJButton);
			downBrowsingPanel.add(Box.createRigidArea(new Dimension(20,0)));

			if(exportID != ID.CLIPBOARD)
				return downBrowsingPanel;
			else
				return null;
		} catch (Exception e) {
			LOGGER.fine("Error initializing BrowsingPanel: "+ e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#initBrowsingPanelWithLabel()
	 */
	protected JPanel initBrowsingPanelWithLabel() throws Exception{
		return initBrowsingPanel(this.savingType, "Browse");
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#initImageViewPanelWithTitle()
	 */
	protected JPanel initImageViewPanelWithTitle() throws Exception{
		return initImageViewPanel(getTitleString(this.savingType));
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#initSaveButtonWithTitle()
	 */
	protected JPanel initSaveButtonWithTitle() throws Exception{
		return initSaveButton("Export results");
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#initSelectFileDialog()
	 */
	protected void initSelectFileDialog() throws Exception{
		String path = System.getProperty("user.home");
		this.selectFileDialog = new SelectFileDialog(this.gui, path, this.backPanel, this.savingType);
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#isFilePathPanelShown()
	 */
	protected boolean isFilePathPanelShown(){
		return false;
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
		this.selectFileDialog.setVisible(true);
		if(this.selectFileDialog.fileWritingType != ID.CANCEL && this.selectFileDialog.fileWritingType != ID.ERROR){

			setExportingPath(this.selectFileDialog.getSelectedFilePath());
			setFileWritingType(this.selectFileDialog.getFileWritingType());
			// refresh backgroundColors of MarkingLayers
			setMarkingLayerBackgroundsToDefault(ID.IMAGELAYER_UNDEFINED);

		}
		this.selectFileDialog.setVisible(false);
		this.selectFileDialog.dispose();

	}

	/**
	 * Checks and sets the exporting path.
	 *
	 * @param ePath the new exporting path
	 * @throws Exception the exception
	 */
	protected void setExportingPath(String ePath) throws Exception {
		int fileValidity = checkFileValidity(new File(ePath));

		informUserFromFileValidity(fileValidity, savingPathJLabel, true);
		savingPathJLabel.setText(ePath.trim());
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#setSaveButtonEnabledByFileValidity(int)
	 */
	protected void setSaveButtonEnabledByFileValidity(int fileValidity) throws Exception{
		// check
		if(fileValidity == ID.FILE_OK || fileValidity == ID.FILE_NEW_FILE)
			this.saveJButton.setEnabled(true);
		else
			this.saveJButton.setEnabled(true);
	}

	/* (non-Javadoc)
	 * @see gui.saving.SaverDialog#startSavingProcess(int, int)
	 */
	protected void startSavingProcess(int exportID, int exportType){
		try {
			setAllMarkingLayerBackgroundsToDefault();
			boolean successFullyExported = false;
			boolean wantedToExport=false;
			ArrayList<Integer> successfullIDs=new ArrayList<Integer>();
			LOGGER.fine("exportID:"+exportID);
			byteStream=new ByteArrayOutputStream();
			if(exportType==ID.APPEND)
				writeEmptyLine();
				//go through all panels and pick imageLayers and MarkingLayers which will be saved
				Component[] imPanelList= imageScrollPanel.getComponents();
				if(imPanelList != null && imPanelList.length>0 ){
					// create new LayersOfPath objects and add them to list
					 for (int i = 0; i < imPanelList.length; i++) {
						SingleImagePanel imp= (SingleImagePanel)imPanelList[i];
						if(imp.isSelected()){
							wantedToExport=true; // at least one ImageLayer was selected to be exported
							ArrayList<MarkingLayer> selectedMarkingLayers = imp.getAllSelectedMarkingLayers();
							if(selectedMarkingLayers != null && selectedMarkingLayers.size()>0){
								if(exportID==ID.FILE_TYPE_CSV){
									if(i==0  && exportType != ID.APPEND)
									writeCSVLineTitle();
								}
								else if(exportID == ID.FILE_TYPE_TEXT_FILE || exportID == ID.CLIPBOARD){
									if(i==0  && exportType != ID.APPEND)
										writeTXTorClipboardTitle();
									writeTXTorClipboardLine(imp.getImageLayerName());
								}
	
	
								for (Iterator<MarkingLayer> iterator = selectedMarkingLayers.iterator(); iterator.hasNext();) {
									MarkingLayer mLayer = (MarkingLayer) iterator.next();
									if(exportID==ID.FILE_TYPE_CSV){
											writeCSVLine(imp.getImageLayerName(), mLayer.getLayerName(), mLayer.getCounts());
									}
									else if(exportID == ID.FILE_TYPE_TEXT_FILE || exportID == ID.CLIPBOARD){
										writeTXTorClipboardLine(mLayer.getLayerName(), mLayer.getCounts());
										}
	
									successfullIDs.add(mLayer.getLayerID());
								}
								if(i<imPanelList.length-1 && hasSelectedMarkinglayersInBelowImageLayers(i+1) && exportID != ID.FILE_TYPE_CSV){
									writeEmptyLine();
									writeEmptyLine();
								}
	
							}
						
					 	}

					 }

					 // write the byte array to file

						 if(exportID == ID.FILE_TYPE_TEXT_FILE || exportID == ID.FILE_TYPE_CSV ) {
							successFullyExported= writeByteArrayToFile(exportType);
					 	}
						 else{
							 successFullyExported = copyToClipboard();

						 }


					 byteStream.close();
					 
					 // set colors of dialog and show messages to show successfull exports
					 if(wantedToExport && successFullyExported){
						 setSuccesfullSavingBackgrounds(successfullIDs);
						 ShadyMessageDialog dialog = new ShadyMessageDialog(this, "Exporting succesfull", "Successfully exported MarkingLayers are at green background.  ", ID.OK, this);
						 dialog.showDialog();
						 dialog=null;
					 }
					 else{
						 if(wantedToExport){
							 ShadyMessageDialog dialog = new ShadyMessageDialog(this, "Exporting not succesfull", "Exporting not successfull propably due to unacceptable file path.", ID.OK, this);
							 dialog.showDialog();
							 dialog=null;
						 }
						 else{
							 ShadyMessageDialog dialog = new ShadyMessageDialog(this, "Nothing to Export", "Not selected any results to be exported!", ID.OK, this);
							 dialog.showDialog();
							 dialog=null;
						 }
					 }
				}


			} catch (Exception e) {
				LOGGER.severe("Error in Exporting results: "+ e.getMessage());
				e.printStackTrace();
			}
	}

	/**
	 * Writes a byte array to file.
	 *
	 * @param exportType the type of export
	 * @return true, if successfully written
	 * @throws Exception the exception
	 */
	private boolean writeByteArrayToFile(int exportType) throws Exception{

		 if(this.savingPathJLabel != null && this.savingPathJLabel.getText() != null && this.savingPathJLabel.getText().length()>0){
			 boolean append=false;
			if(exportType == ID.APPEND){
				append=true;
			}
				// write to file
				 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this.savingPathJLabel.getText(), append)));
				    out.println(byteStream.toString());
				    out.close();

				    return true; // succesfull
		 }
		 else{
			 LOGGER.warning("Exporting not succesfull: file not valid!");
			 return false;
		 }

	}

	/**
	 * Write csv line.
	 *
	 * @param imageLayerName the image layer name
	 * @param markingLayerName the marking layer name
	 * @param counts the counts
	 * @throws Exception the exception
	 */
	private void writeCSVLine(String imageLayerName, String markingLayerName, int counts) throws Exception{
		String line = "\n"+imageLayerName + ","+markingLayerName+","+counts;
		byteStream.write(line.getBytes());

	}

	/**
	 * Write csv line title.
	 *
	 * @throws Exception the exception
	 */
	private void writeCSVLineTitle() throws Exception{
		String line = "ImageLayer,MarkingLayer,Counts";
		byteStream.write(line.getBytes());

	}


	/**
	 * Writes empty line.
	 *
	 * @throws Exception the exception
	 */
	private void writeEmptyLine() throws Exception{
		String line = "\n";
		byteStream.write(line.getBytes());
	}

	/**
	 * Write txt or clipboard line.
	 *
	 * @param imageLayerName the image layer name
	 * @throws Exception the exception
	 */
	private void writeTXTorClipboardLine(String imageLayerName) throws Exception{
		String line = imageLayerName;
		byteStream.write(line.getBytes());

	}

	/**
	 * Write txt or clipboard line.
	 *
	 * @param markingLayerName the marking layer name
	 * @param counts the counts
	 * @throws Exception the exception
	 */
	private void writeTXTorClipboardLine(String markingLayerName, int counts) throws Exception{
		String line = "\n"+markingLayerName+"\t"+counts;
		byteStream.write(line.getBytes());

	}

	/**
	 * Write txt or clipboard title.
	 *
	 * @throws Exception the exception
	 */
	private void writeTXTorClipboardTitle() throws Exception{
		String line = "Layers"+ "\t" +"Counts"+"\n";
		byteStream.write(line.getBytes());

	}
}

package gui;

import gui.file.ImageFilter;
import gui.file.OpenImageFilesDialog;
import gui.file.OpenMarkingFileDialog;
import gui.graphics.BigCloseIcon;
import gui.graphics.BigMarkingIcon;
import gui.graphics.MediumCloseIcon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import information.*;

/**
 * Class AddImageLayerDialog is a window where user can import image files as ImageLayers and import previously saved MarkingLayers.
 * Also deleting ImageLayers is possible in this window.
 * @author Antti Kurronen
 *
 */
/**
 * @author Antti Kurronen
 *
 */
public class AddImageLayerDialog extends JDialog{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6514281535063810182L;

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The GUI. */
	private GUI gui;
	
	/** The back panel. Contains dimming and the visible panel*/
	private JPanel backPanel;
	
	/** The cancel j button. */
	private JButton cancelJButton;
	
	/** The image scroll panel. */
	private JPanel imageScrollPanel;
	
	/** The list of ImageLayers. */
	private ArrayList<ImageLayer> dialogImageLayerList;
	
	/** The JScrollPane for scrolling images if a lot of them. */
	private JScrollPane imageScrollingPane;
	
	/** The create ImageLayers JButton. */
	private JButton createImageLayersJButton;
	
	/** The add image j button. */
	private JButton addImageJButton;
	
	/** The type of dialog. */
	private int typeOfDialog;
	
	/** The import allowed image dimension. */
	private Dimension importAllowedImageDimension=null;

	/** The visible dialog. The opened child dialog for this class */
	private JDialog visibleDialog=null;

	/** The shady message dialog. Shows messages */
	private ShadyMessageDialog shadyMessageDialog=null;
	
	/**
	 * Class constructor for only creating new ImageLayers and importing markings
	 * @param frame Owner JFrame
	 * @param gui GUI object where from this class is called.
	 */
	public AddImageLayerDialog(JFrame frame, GUI gui){

			super(frame, true);
			try{
				this.setResizable(false);
				typeOfDialog=ID.CREATE_NEW_IMAGELAYERS;
				dialogImageLayerList = new ArrayList<ImageLayer>();
				this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				this.gui=gui;
				initComponents();


				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						try {
							selectAndAddImages();
						} catch (Exception e) {
							LOGGER.severe("Error in selecting images!");
							e.printStackTrace();
						}
					}
				});


		} catch (Exception e) {
			LOGGER.severe("Error in adding new ImageLayer:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
		}
	}
	
	/**
	 * Class constructor which shows already open ImageLayers enabling managing markings.
	 *
	 * @param frame Owner JFrame
	 * @param gui GUI object where from this class is called.
	 * @param iList the list of ImageLayers
	 */
	public AddImageLayerDialog(JFrame frame, GUI gui, ArrayList<ImageLayer> iList){

			super(frame, true);
			try{
				this.typeOfDialog=ID.MANAGE_IMAGE_LAYERS;
				this.dialogImageLayerList =   makeCopyOfList(iList);
				this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				this.gui=gui;
				initComponents();
				updateImageList();


		} catch (Exception e) {
			LOGGER.severe("Error in adding new ImageLayer:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
		}
	}

	/**
	 * Class constructor for creating new ImageLayers from given image files.
	 *
	 * @param frame Owner JFrame
	 * @param gui GUI object where from this class is called.
	 * @param imageFiles the image files
	 */
	public AddImageLayerDialog(JFrame frame, GUI gui, File[] imageFiles){

			super(frame, true);
			try{
				typeOfDialog=ID.CREATE_NEW_IMAGELAYERS;
				dialogImageLayerList = new ArrayList<ImageLayer>();
				this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				this.gui=gui;
				initComponents();
				if(imageFiles != null && imageFiles.length>0)
					addImagesToImageLayerList(imageFiles);
				this.setVisible(true);


		} catch (Exception e) {
			LOGGER.severe("Error in adding new ImageLayer:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
		}
	}

	/**
	 * Creates ImageLayer objects to imageLayerList by inserting paths of parameter file(s).
	 * If path is already used in an imageLayer object, no new ImageLayer is created.
	 * @param imageFiles List of files, which path are used in creating  ImageLayer objects
	 */
	private void addImagesToImageLayerList(File[] imageFiles){
		try{
		if(imageFiles !=null && imageFiles.length>0)

		for(int i=0; i<imageFiles.length;i++){
			if(imageFiles[i] != null && imageFiles[i].exists() && imageFiles[i].getAbsolutePath().length() >3){
				if(isImageFile(imageFiles[i])){
					if(!imageNameAlreadyInList(imageFiles[i].getName()) && !gui.imageNameAlreadyUsed(imageFiles[i].getName())){
						dialogImageLayerList.add(new ImageLayer(imageFiles[i].getAbsolutePath())); // create new ImageLayer by giving the path of image
					}
					else{
						
						// inform user that image with same name is already used
						shadyMessageDialog = new ShadyMessageDialog(this, "Refused opening image", " Image name:  "+imageFiles[i].getName() + " is already open", ID.OK, this);	
						shadyMessageDialog.showDialog();									
					}
				} // if file is wrong format of the dimensio is wrong -> informed in isImageFile -method

			}else{
				// inform user that image with same name is already used
				shadyMessageDialog = new ShadyMessageDialog(this, "Refused opening image", " Image name:  "+imageFiles[i].getName() + " doesn't exist!", ID.OK, this);
				shadyMessageDialog.showDialog();

			}
		}
		updateImageList();
		shadyMessageDialog=null;
		imageFiles=null;
		}catch(Exception e){
			LOGGER.severe("Problems in adding images to list: "+e.getMessage());
			shadyMessageDialog=null;
		}

	}


	/**
	 * Saves imported markingLayers from copied ImageLayer to originalImageLayer. Before saving removes MarkingLayer with same name if found.
	 * @param originalIL @see ImageLayer the original ImageLayer, which is in list of present ImageLayers (shown in LAYERS-panel in GUI).
	 * @param copyIL @see ImageLayer the copyed Imagelayer where imported MarkingLayers are added in import part.
	 * @return boolean true if saved MarkingLayer to ImageLayer, otherwise false.
	 */
	private boolean addMarkingLayerIfNotExists(ImageLayer originalIL, ImageLayer copyIL){
		try {
			boolean madeSave=false; // is any markings saved
			if(originalIL!= null && originalIL.getMarkingLayers() != null
					&& copyIL != null && copyIL.getMarkingLayers() != null && copyIL.getMarkingLayers().size() >0){
				// go through all MarkingLayer objects of copyIL
				Iterator<MarkingLayer> mIterator = copyIL.getMarkingLayers().iterator();
				while(mIterator.hasNext()){
					MarkingLayer maCopy = (MarkingLayer)mIterator.next();
					// Check is MarkingLayer already in original ImageLayer
					if(originalIL.isMarkingLayerInList(maCopy)){
						// found same MarkingLayer (name) from both ImageLayers -> confirm overwrite
						
						shadyMessageDialog = new ShadyMessageDialog(this, "Imported MarkingLayer "+ maCopy.getLayerName()+ " already exists!", "OVERWRITE?", ID.YES_NO, this);
						if(shadyMessageDialog.showDialog() == ID.YES){
							LOGGER.fine("overwriting markingLayer: " + maCopy.getLayerName());
							//remove original
							originalIL.removeMarkingLayer(maCopy); // this method compares names of MarkingLayers and if same -> removed
							// add new one
							originalIL.addMarkingLayer(maCopy); // add new one
							madeSave=true;
						}
						shadyMessageDialog=null;

					}
					else{ // not found -> add MarkingLayer to original ImageLayer
						originalIL.addMarkingLayer(maCopy.makeCopy()); // for safety add copy of Markinglayer
						madeSave=true;
					}
				}
			}
			return madeSave;
		} catch (HeadlessException e) {
			shadyMessageDialog=null;
			LOGGER.severe("Error in adding Markings to ImageLayer " +e.getClass().toString() + " :" +e.getMessage());
			return false;
		}
		catch(Exception ex){
			shadyMessageDialog=null;
			LOGGER.severe("Error in adding Markings to ImageLayer ");
			return false;
		}
	}




	/** Adds markings from XML-file to wanted ImageLayer
	 * @param xmlFile External file containing markings for selected imageLayer
	 * @param imageLayerPath The absolute filepath of selected ImageLayer
	 */
	private void addMarkingsToSelectedImageLayer(File xmlFile, String imageLayerPath){

		try {
			int importedNumber=0;
			if(xmlFile !=null && xmlFile.exists()){
				Iterator<ImageLayer> iIterator = dialogImageLayerList.iterator();
				while(iIterator.hasNext()){
					ImageLayer im = (ImageLayer)iIterator.next();
					if(imageLayerPath != null && imageLayerPath.length()>0 && im.hasSameImageName(imageLayerPath) || imageLayerPath == null){ // ImageLayerPath match (the path is unique)
						// get all possible markinglayers for selected imageLayer

						//create new copy of ImageLayer im to make search of markings from xml file -> check duplicates afterwards
						ImageLayer copyImageLayer = im.makeCopy();
						// remove all MarkingLayers. Only MarkingLayers found from XML file are need to be in this object.
						copyImageLayer.removeAllMarkingLayers();

						 // Make search after search copyImageLayer will contain MarkingLayers found from XML
						gui.getMarkingOfXML(xmlFile, copyImageLayer);
						if(copyImageLayer.getMarkingLayers() != null && copyImageLayer.getMarkingLayers().size()>0){ // did any markinglayers were found
							// add found MarkingLayers. If found MarkingLayer exists -> confirm overwrite
							if(addMarkingLayerIfNotExists(im, copyImageLayer)
									&& (im.getMarkingsFilePath() == null || im.getMarkingsFilePath().length()==0))
								im.setMarkingsFilePath(xmlFile.getAbsolutePath());
							importedNumber++;
						}
						else{ // no markins found -> inform user

							if(imageLayerPath != null){
								shadyMessageDialog = new ShadyMessageDialog(this, "MARKINGS NOT FOUND!", "No markings for selected ImageLayer were found!", ID.OK, this);
								shadyMessageDialog.showDialog();
								shadyMessageDialog=null;
							}
						}
					}

				}
				if(imageLayerPath == null){ // imported for several ImageLayers
					if(dialogImageLayerList != null && dialogImageLayerList.size() > importedNumber){
						if(importedNumber >0){
							shadyMessageDialog = new ShadyMessageDialog(this, "Importing successfull!", "Imported markings for "+importedNumber+" of "+dialogImageLayerList.size()+ " ImageLayers.", ID.OK, this);
							shadyMessageDialog.showDialog();
							shadyMessageDialog=null;
						}
						else{
							shadyMessageDialog = new ShadyMessageDialog(this, "Importing not successfull!", "No any Markings found for ImageLayers.", ID.OK, this);
							shadyMessageDialog.showDialog();
							shadyMessageDialog=null;
						}
					}
				}
			}
			updateImageList();
		} catch (Exception e) {
			shadyMessageDialog=null;
			LOGGER.severe("Error in adding Markings to ImageLayer " +e.getClass().toString() + " :" +e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 * Hides window.
	 */
	private void cancelSelected(){
		try {
			this.setVisible(false);
		} catch (Exception e) {
			LOGGER.severe("ERROR in hiding dialog!");
			e.printStackTrace();
		}

	}

	/**
	 *  When closing the window this method sets window invisible and adds newly created ImageLayers to list of ImageLayers.
	 */
	private void continueCreatingImageLayers(){
		try {
			this.setVisible(false);
			gui.addImageLayerList(this.dialogImageLayerList);

			this.dispose();
		} catch (Exception e) {
			LOGGER.severe("ERROR in hiding dialog and saving ImageLayers!");
			e.printStackTrace();
		}
	
	}

	/**
	 * When closing the window this method sets window invisible and updates modified ImageLayers to list of ImageLayers.
	 */
	private void continueUpdatingImageLayers(){
		try {
			this.setVisible(false);
			this.gui.setImageLayerList(this.dialogImageLayerList);
			this.dispose();
		} catch (Exception e) {
			LOGGER.severe("ERROR in hiding dialog and updating ImageLayers!");
			e.printStackTrace();
		}
	}

	/**
	 * Creates ImageAndMarkingPanel from given ImageLayer and it's MarkingLayers.
	 *
	 * @param layer @see ImageLayer which data is used.
	 * @return @see ImageAndMarkingPanel a Panel showing information of given ImageLayer and it's MarkingLayers.
	 * @throws Exception the exception
	 */
	private ImageAndMarkingPanel createImagePanel(ImageLayer layer) throws Exception{
		return new ImageAndMarkingPanel(layer);
	}

	/**
	 * Removes temporary ImageLayer from list in AddImageLayerDialog.
	 *
	 * @param path String image path in ImageLayer to be deleted from list.
	 * @throws Exception the exception
	 */
	private void deleteImageLayer(String path) throws Exception{
		try {
			LOGGER.fine("delete: " +path);
			Iterator<ImageLayer> iIterator = dialogImageLayerList.iterator();
			while(iIterator.hasNext()){
				ImageLayer im = (ImageLayer)iIterator.next();
				if(im.getImageFilePath().equals(path)){
	
					// ask the user should the ImageLayer being deleted
	
					shadyMessageDialog  = new ShadyMessageDialog(this, "DELETE", "Delete ImageLayer:  "+im.getImageFileName(), ID.YES_NO, this);
					if(shadyMessageDialog.showDialog() == ID.YES){
						LOGGER.fine("deleted imageLayer: " + im.getImageFilePath());
						iIterator.remove();
					}
					shadyMessageDialog=null;
				}
			}
		} catch (Exception e) {
			shadyMessageDialog=null;
			LOGGER.severe("Error in deleting imageLayer from IMAGE LIST " +e.getClass().toString() + " :" +e.getMessage());
	
		}
	}


	/**
	 * Removes MarkingLayer from ImageLayer that is is in list of AddImageLayerDialog-object.
	 * @param imagePath String image path of ImageLayer
	 * @param markingName String name of MarkingLayer to be deleted.
	 */
	private void deleteMarkingLayer(String imagePath, String markingName){
		try {
			LOGGER.fine("delete: " +markingName);
			ImageLayer im = getImageLayer(imagePath);
			if(im != null && im.getMarkingLayers() != null && im.getMarkingLayers().size() >0){
	
				Iterator<MarkingLayer> mIterator = im.getMarkingLayers().iterator();
				while(mIterator.hasNext()){
					MarkingLayer ma = (MarkingLayer)mIterator.next();
					if(ma.getLayerName().equals(markingName)){
						// ask the user should the ImageLayer being deleted
						shadyMessageDialog = new ShadyMessageDialog(this, "DELETE", "Delete MarkingLayer:  "+ma.getLayerName(), ID.YES_NO, this);
						if(shadyMessageDialog.showDialog() == ID.YES){
							LOGGER.fine("deleted markingLayer: " + ma.getLayerName());
							mIterator.remove();
						}
						shadyMessageDialog=null;
					}
				}
			}
	
		} catch (Exception e) {
			shadyMessageDialog=null;
			LOGGER.severe("Error in deleting imageLayer from IMAGE LIST " +e.getClass().toString() + " :" +e.getMessage());
	
		}
	}






	/**
	 * Returns the size of BackPanel.
	 *
	 * @return Rectangle the size of BackPanel
	 * @throws Exception the exception
	 */
	public Rectangle getBackPanelSize() throws Exception{
		return this.backPanel.getBounds();
	}



/**
 * Counts and returns an overall sum of height of all ImagePanels and MarkingPanels.
 * @return int overall sum of height of all ImagePanels and MarkingPanels.
 */
private int getHeightOfAllImageAndMarkingPanels(){
	try {
		int heightValue=0;
		Iterator<ImageLayer> iIterator = dialogImageLayerList.iterator();
		while(iIterator.hasNext()){
			ImageLayer im = (ImageLayer)iIterator.next();
			if(im != null && im.getImageFileName().length()>0){
				heightValue+=45;
			}
			if(im.getMarkingLayers() != null && im.getMarkingLayers().size()>0){
			//	foundMarkingLayer=true;
				Iterator<MarkingLayer> mIterator = im.getMarkingLayers().iterator();
				while(mIterator.hasNext()){
					MarkingLayer ma= (MarkingLayer)mIterator.next();
					if(ma != null && ma.getLayerName().length() > 0){
						heightValue+=40;
					}
				}
			}

		}
		return heightValue;
	} catch (Exception e) {
		LOGGER.severe("Error in calculating height of ImageAndMarkingPanel " +e.getClass().toString() + " :" +e.getMessage());
		return -1;
	}
}

/**
 * Returns the image layer.
 *
 * @param path the path
 * @return the image layer
 * @throws Exception the exception
 */
/*
 * Creates ImageIcon from given image path.
 * @param path String image path
 * @return @see ImageIcon
 *
private ImageIcon getImageIcon(String path) {

		try {
			URL url = this.getClass().getResource(path);
			ImageIcon img = new ImageIcon(url);
			return img;
		} catch (Exception e) {
			LOGGER.severe("Error in getting imageIcon" +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}
}
*/




/**
 * Return ImageLayer searched by image path of ImageLayer.
 * @param path String image path of ImageLayer
 * @return @see ImageLayer found ImageLayer if found; otherwise null;
 */
private ImageLayer getImageLayer(String path){
	try {
		LOGGER.fine("get: " +path);
		Iterator<ImageLayer> iIterator = dialogImageLayerList.iterator();
		while(iIterator.hasNext()){
			ImageLayer im = (ImageLayer)iIterator.next();
			if(im.getImageFilePath().equals(path)){

				return im;
			}

		}
		return null;
	} catch (Exception e) {
		LOGGER.severe("Error in deleting imageLayer from IMAGE LIST " +e.getClass().toString() + " :" +e.getMessage());
		return null;
	}
}

/**
 * Returns  path of previously used folder.
 *
 * @return String path of previously used folder
 * @throws Exception the exception
 */
public String getPresentFolder()throws Exception{
	return gui.getPresentFolder();
}



/**
 *  Method for checking is image path already in an ImageLayer
 * @param item a path String which is searched from imageLayerList
 * @return boolean value true if path is already in an ImageLayer of imageLayerList
 */
private boolean imageNameAlreadyInList(String item){
	try {

		Iterator<ImageLayer> iIterator = dialogImageLayerList.iterator();
		while(iIterator.hasNext()){
			ImageLayer im = (ImageLayer)iIterator.next();
			if(im.getImageFileName().equals(item)){
				return true;
			}

		}
		return false;
	} catch (Exception e) {
		LOGGER.severe("Error in deleting imageLayer from IMAGE LIST " +e.getClass().toString() + " :" +e.getMessage());
		return true;
	}

}

/**
 * Sets an action for a JButton.
 * @param button JButton where the action is added
 * @param typeOfItem int type of action  @see information.ID
 * @throws Exception
 */
private void initActionsToButtons(JButton button, int typeOfItem) throws Exception{
	// deleting ImageLayer
	if(typeOfItem == ID.DELETE_IMAGELAYER)
	button.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				deleteImageLayer(((ImageAndMarkingPanel)((JPanel)((JPanel)((JButton)arg0.getSource()).getParent()).getParent()).getParent()).getPath());
				updateImageList();
			} catch (Exception e) {
				LOGGER.severe("ERROR in deleting ImageLayer!");
				e.printStackTrace();
			}
		}
	});


	// deleting MarkingLayer
		if(typeOfItem == ID.DELETE_MARKINGLAYER){
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						//remove markinglayer and update panels
						String imagelayerPath =((SingleMarking)((JButton)arg0.getSource()).getParent()).getImageLayerPath();
						String markingLayerName =((SingleMarking)((JButton)arg0.getSource()).getParent()).getMarkingName();
						// deletes the MarkingLayer of the ImageLayer
						deleteMarkingLayer(imagelayerPath, markingLayerName);
						// update visible list of window
						updateImageList();
					} catch (Exception e) {
						LOGGER.severe("ERROR in deleting of MarkingLayer!");
						e.printStackTrace();
					}
				}
			});
		}
		else

		// Browsing xml-file for adding new markinglayer to selected ImageLayer.
				if(typeOfItem == ID.OPEN_MARKING_FILE){
					button.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							try {
								// activate filechooser to open markings. Gives the image path of ImageLayer as parameter
								selectAndAddMarkings(((ImageAndMarkingPanel)((JPanel)((JPanel)((JButton)arg0.getSource()).getParent()).getParent()).getParent()).getPath(), false);
								// update visible list of window
								updateImageList();
							} catch (Exception e) {
								LOGGER.severe("ERROR in browsing xml-file!");
								e.printStackTrace();
							}
						}
					});
				}
				else
				// CREATE IMAGELAYERS -button
				if(typeOfItem == ID.CREATE_NEW_IMAGELAYERS){
					button.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							try {
								continueCreatingImageLayers(); // closes dialog and adds imagelayers to InformationCenter
							} catch (Exception e) {
								LOGGER.severe("ERROR in creating ImageLayers!");
								e.printStackTrace();
							}
						}
					});
				}
				else
					// UPDATE IMAGELAYERS -button
					if(typeOfItem == ID.MANAGE_IMAGE_LAYERS){
						button.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								try {
									continueUpdatingImageLayers();
								} catch (Exception e) {
									LOGGER.severe("ERROR in updating ImageLayers!");
									e.printStackTrace();
								}
							}
						});
					}
					else
						// CANCEL -button
						if(typeOfItem == ID.CANCEL){
							button.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent arg0) {
									try {
										cancelSelected();
									} catch (Exception e) {
										LOGGER.severe("ERROR in cancelling!");
										e.printStackTrace();
									}
								}
							});
						}
}

/**
 * Initializes components of window.
 */
private void initComponents(){	
	try {
		//init whole window to dim it
		this.setBounds(gui.getBounds());
		this.setUndecorated(true);
		this.setBackground(new Color(0,0,0,0));
		this.setContentPane(new ContentPane());
		this.getContentPane().setBackground(Color_schema.dark_30);
		this.getContentPane().setLayout(new GridBagLayout());
		//init backpanel where all funtions are shown.
		backPanel = new JPanel();
		backPanel.setBackground(new Color(0,0,0));
		backPanel.setLayout(new BorderLayout());
		backPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 5));
		backPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		backPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		backPanel.setMaximumSize(new Dimension((int)(this.getBounds().getWidth()*0.95), (int)(this.getBounds().getHeight()*0.95)));
		backPanel.setMinimumSize(new Dimension((int)(this.getBounds().getWidth()*0.5), (int)(this.getBounds().getHeight()*0.5)));
		backPanel.setPreferredSize(new Dimension((int)(this.getBounds().getWidth()*0.6), (int)(this.getBounds().getHeight()*0.6)));
		//if backpanel size too small -> use more space of whole screen.
		if(backPanel.getPreferredSize().getWidth()<800 || backPanel.getPreferredSize().getHeight() <500)
			backPanel.setPreferredSize(new Dimension((int)(this.getBounds().getWidth()*0.95), (int)(this.getBounds().getHeight()*0.95)));

		backPanel.add(initImageViewPanel(), BorderLayout.CENTER);
		backPanel.add(initDownPanel(),BorderLayout.PAGE_END);

		this.add(backPanel);
		this.validate();
		this.repaint();
	} catch (Exception e) {
		LOGGER.severe("ERROR in initializing Add ImageLayer Dialog Components!");
		e.printStackTrace();
	}
}

/**
 * Creates JPanel containing JButtons for accept or cancel managing layers.
 * @return JPanel which contains JButtons
 */
private JPanel initDownPanel(){

	try {
		// SETUP buttons of lowest part of tab
		JPanel downButtonPanel =new JPanel();
		downButtonPanel.setLayout(new BoxLayout(downButtonPanel, BoxLayout.LINE_AXIS));
		downButtonPanel.setBackground(Color_schema.dark_40);
		downButtonPanel.setMinimumSize(new Dimension((int)backPanel.getMinimumSize().getWidth(), 50));
		downButtonPanel.setPreferredSize(new Dimension(100,50));
		downButtonPanel.setMaximumSize(new Dimension(100,50));

		JPanel createImageLayerJPanel = new JPanel();
		createImageLayerJPanel.setLayout(new BorderLayout());
		createImageLayerJPanel.setPreferredSize(new Dimension(220,30));
		createImageLayerJPanel.setMaximumSize(new Dimension(220,30));
		createImageLayersJButton = new JButton("CREATE IMAGELAYERS");
		createImageLayersJButton.setEnabled(false);
		createImageLayersJButton.setPreferredSize(new Dimension(220,30));
		createImageLayersJButton.setFocusable(false);
		MouseListenerCreator.addKeyListenerToButton(createImageLayersJButton, ID.BUTTON_ENTER);

		if(this.typeOfDialog == ID.CREATE_NEW_IMAGELAYERS){
			initActionsToButtons(createImageLayersJButton, ID.CREATE_NEW_IMAGELAYERS);
		}
		else{
			createImageLayersJButton.setText("UPDATE IMAGELAYERS");
			initActionsToButtons(createImageLayersJButton, ID.MANAGE_IMAGE_LAYERS);
		}

		//addKeyListenerToButton(createImageLayersJButton); // when enter pressed -> this button activated

		MouseListenerCreator.addMouseListenerToNormalButtons(createImageLayersJButton); // changes the colors of button when pressed


		createImageLayerJPanel.add(createImageLayersJButton, BorderLayout.CENTER);
		JPanel cancelJpanel = new JPanel();
		cancelJpanel.setLayout(new BorderLayout());
		cancelJpanel.setPreferredSize(new Dimension(150,50));
		cancelJpanel.setMaximumSize(new Dimension(100,30));
		cancelJButton = new JButton("CANCEL");
		cancelJButton.setFocusable(false);
		cancelJButton.setForeground(Color_schema.orange_dark);
		initActionsToButtons(cancelJButton, ID.CANCEL);	// the action
		MouseListenerCreator.addMouseListenerToCancelButtons(cancelJButton); // changes the colors of button when pressed
		MouseListenerCreator.addKeyListenerToButton(cancelJButton, ID.BUTTON_CANCEL);

		cancelJButton.setPreferredSize(new Dimension(100,50));

		cancelJpanel.add(cancelJButton);
		downButtonPanel.add(Box.createRigidArea(new Dimension(20,0)));
		downButtonPanel.add(createImageLayerJPanel);
		downButtonPanel.add(Box.createHorizontalGlue());
		downButtonPanel.add(cancelJpanel);
		downButtonPanel.add(Box.createRigidArea(new Dimension(20,0)));
		return downButtonPanel;

	} catch (Exception e) {
		LOGGER.severe("Error in creating buttons of open images windown:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
		return null;
	}

}

/**
 * Initializes ImageViewPanel,where all ImageLayers and MarkingLayers are shown.
 * @return JPanel where components added.
 */
private JPanel initImageViewPanel(){
	// SETUP THE Panel which shows the list of images and markings
	try {

		JPanel iBackPanel = new JPanel();
	//	iBackPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		iBackPanel.setLayout(new BorderLayout());
		iBackPanel.setBackground(new Color(0,0,0));
		iBackPanel.setLayout(new BorderLayout());
		iBackPanel.setMaximumSize(this.getMaximumSize());
		iBackPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));

		JPanel upPanel = new JPanel();
		upPanel.setBackground(Color_schema.dark_30);
		upPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		upPanel.setMaximumSize(new Dimension(200,40));
		upPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		upPanel.setBorder(BorderFactory.createEmptyBorder());
		JLabel titleLabel = new JLabel("OPEN IMAGES");
		if(this.typeOfDialog == ID.MANAGE_IMAGE_LAYERS)
			titleLabel.setText("Manage ImageLayers and MarkingLayers");
		titleLabel.setFont(new Font("Consolas", Font.BOLD,20));
		titleLabel.setForeground(Color_schema.white_230);
		upPanel.add(titleLabel);
		iBackPanel.add(upPanel, BorderLayout.PAGE_START);

		// CENTER PANEL
		JPanel centerPanel=new JPanel();
		centerPanel.setBackground(Color_schema.dark_40);
		centerPanel.setLayout(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createEmptyBorder());
		JPanel scrollBiggerPanel = new JPanel();
		scrollBiggerPanel.setLayout(new BorderLayout());
		scrollBiggerPanel.setBorder(BorderFactory.createEmptyBorder());
		scrollBiggerPanel.setBackground(Color_schema.dark_40);

		imageScrollPanel = new JPanel();
		imageScrollPanel.setBackground(Color_schema.dark_40);
		imageScrollPanel.setMinimumSize(new Dimension(((int)(backPanel.getMinimumSize().getWidth()*0.4)), ((int)(backPanel.getMinimumSize().getHeight()*0.7)-40)));
		imageScrollPanel.setLayout(new BoxLayout(imageScrollPanel, BoxLayout.PAGE_AXIS));
		imageScrollingPane = new JScrollPane(imageScrollPanel);
		imageScrollingPane.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 2));
		imageScrollingPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		imageScrollingPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		imageScrollingPane.setPreferredSize(new Dimension((int)backPanel.getPreferredSize().getWidth()-20,(int)backPanel.getPreferredSize().getHeight()-140));
		imageScrollingPane.setMaximumSize(new Dimension((int)backPanel.getPreferredSize().getWidth()-20,(int)backPanel.getPreferredSize().getHeight()-140));
		imageScrollingPane.getVerticalScrollBar().setUnitIncrement(16);
		
		// SETUP Button and it's JPanel for adding selected pahths to list
		JPanel imageButtonJPanel = new JPanel();
		imageButtonJPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		imageButtonJPanel.setBackground(Color_schema.dark_30);
		imageButtonJPanel.setMinimumSize(new Dimension(200,50));
		imageButtonJPanel.setMaximumSize(new Dimension(200,50));

		addImageJButton = new JButton("ADD IMAGE");
		addImageJButton.setPreferredSize(new Dimension(150,30));
		addImageJButton.setBackground(Color_schema.dark_20);
		addImageJButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					selectAndAddImages();
				} catch (Exception e1) {
					LOGGER.severe("Error in selecting Images!");
					e1.printStackTrace();
				}
			}
		});
		addImageJButton.setFocusable(false);
	//	addMouseListenerToButtons(addImageJButton, ID.BUTTON_NORMAL);
		MouseListenerCreator.addMouseListenerToNormalButtons(addImageJButton);
		imageButtonJPanel.add(addImageJButton);


		JButton addMarkingsForAll = new JButton("Import Markings For All ImageLayers");
		addMarkingsForAll.setPreferredSize(new Dimension(350,30));
		addMarkingsForAll.setBackground(Color_schema.dark_20);
		addMarkingsForAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(dialogImageLayerList != null && dialogImageLayerList.size()>0)
						selectAndAddMarkings(dialogImageLayerList.get(0).getImageFilePath(),true);
					else{
						showMessage("No ImageLayers", "Not found ImageLayers where to add markings");

					}
				} catch (Exception e1) {
					LOGGER.severe("ERROR in importing Markings for all ImageLayers!");
					e1.printStackTrace();
				}
			}
		});
		addMarkingsForAll.setFocusable(false);
	//	addMouseListenerToButtons(addMarkingsForAll, ID.BUTTON_NORMAL);
		MouseListenerCreator.addMouseListenerToNormalButtons(addMarkingsForAll);
		imageButtonJPanel.add(addMarkingsForAll);


		scrollBiggerPanel.add(imageScrollingPane, BorderLayout.CENTER);
		centerPanel.add(scrollBiggerPanel, BorderLayout.CENTER);
		centerPanel.add(imageButtonJPanel, BorderLayout.PAGE_END);
		iBackPanel.add(centerPanel, BorderLayout.CENTER);

		return iBackPanel;
	} catch (Exception e) {
		LOGGER.severe("Error in creating imageViewPanel:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
		return null;
	}

}



	/** Checks is the given file proper image file (supported images : tiff, jpg, png...)
	 *  and is the image size same as already imported images. ONLY IMAGES WITH SAME PIXEL SIZE ARE ALLOWED TO OPEN!
	 *
	 * @param file the file to be checked
	 * @return boolean value is file proper image file
	 */
	private boolean isImageFile(File file){
		
		try {
			ImageFilter filterImage=new ImageFilter();
			if(filterImage.accept(file)){
				if(this.gui.isAllowedImageDimension(file) && isImportingAllowedImageDimension(file)){
					LOGGER.fine("image file has allowed dimension");
					return true;
				}
				else{
					LOGGER.warning("Dimension of selected file "+file.getName()+" differs from other images! Not imported!");
					shadyMessageDialog = new ShadyMessageDialog(this, "Refused opening image", " Dimension of selected file "+file.getName()+" differs from other images! Not imported!", ID.OK, this);
					shadyMessageDialog.showDialog();
					shadyMessageDialog=null;

					return false;
				}
			}
			else{
				LOGGER.warning("The image format of selected file "+file.getName()+" is not acceptable! Not imported");
				shadyMessageDialog = new ShadyMessageDialog(this, "Refused opening image", "The image format of selected file "+file.getName()+" is not acceptable! Not imported", ID.OK, this);
				shadyMessageDialog.showDialog();
				shadyMessageDialog=null;
				return false;
			}
		} catch (Exception e) {
			LOGGER.severe("Error in adding Markings to ImageLayer :" +e.getMessage());

			shadyMessageDialog = new ShadyMessageDialog(this, "Refused opening image", "The image  "+file.getName()+" may be broken! Not imported", ID.OK, this);
			shadyMessageDialog.showDialog();

			return false;
		}
		finally{shadyMessageDialog=null;}

	}

	/**
	 * Determines the dimension of given image file and compares it to present used  dimension of images. 
	 * If there is no present dimension is new dimension saved using dimension of given image and true returned.
	 * If there is present dimension and dimension of given image is same -> true; otherwise false.
	 * @param file @see File a image file which dimension is determined
	 * @return boolean true if dimension of given image file is accepted otherwise false
	 */
	private boolean isImportingAllowedImageDimension(File file) {
		try {
			Dimension iDimension=this.gui.taskManager.getImageDimension(file);

			if(this.importAllowedImageDimension == null){
				this.importAllowedImageDimension=iDimension;
				return true;
			}
			if(iDimension != null && this.importAllowedImageDimension.width == iDimension.width && this.importAllowedImageDimension.height == iDimension.height)
				return true;

			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Gets a list ImageLayers and creates a full copy of all of them (not references to objects).
	 * @param iLayer @see ArrayList<ImageLayer> of ImageLayers 
	 * @return @see ArrayList<ImageLayer> of ImageLayers 
	 */
	private ArrayList<ImageLayer> makeCopyOfList(ArrayList<ImageLayer> iLayer){

		try {
			ArrayList<ImageLayer> copyList = new ArrayList<ImageLayer>();
			if(iLayer != null && iLayer.size()>0){
				Iterator<ImageLayer> iterator = iLayer.iterator();
				while(iterator.hasNext()){
					ImageLayer im = (ImageLayer)iterator.next();
					if(im != null){
						// make full copy of ImageLayer
						ImageLayer copyImageLayer = im.makeCopy();
						if(copyImageLayer != null)
							copyList.add(copyImageLayer);
					}
				}


			}
			return copyList;
		} catch (Exception e) {
			LOGGER.severe("Error in creating copy of imageLayerList " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}

	}

	/**
	 * Opens a file dialog, which type depends on which file type will be opened.
	 *
	 * @throws Exception the exception
	 */
	private void selectAndAddImages() throws Exception{
		
	//	OpenImageFilesDialog 
		visibleDialog=new OpenImageFilesDialog(this, this.getBounds(), this.backPanel.getBounds(), gui.getPresentFolder());
		((OpenImageFilesDialog)visibleDialog).showDialog();
	//	visibleDialog.setVisible(true);
		gui.setPresentFolder(((OpenImageFilesDialog)visibleDialog).getPresentFolder());
		File[] imagefiles = ((OpenImageFilesDialog)visibleDialog).getSelectedFiles();
		if(imagefiles != null && imagefiles.length>0)
		addImagesToImageLayerList(imagefiles);
		visibleDialog=null;
	}



	/**
	 * Opens window where user can open xml-file for importing MarkingLayers to given ImageLayer or for all ImageLayers in list of AddImageLayersDialog-object.
	 * @param imageLayerPath Path for selected imageLayer or first ImageLayer of list
	 * @param isMarkingsForAll Boolean should add MarkingLayers to all ImageLayers if found any.
	 */
	private void selectAndAddMarkings(String imageLayerPath, boolean isMarkingsForAll){
		try {
			visibleDialog= new OpenMarkingFileDialog(this, this.getBounds(), this.backPanel.getBounds(),imageLayerPath);
			visibleDialog.setVisible(true);

			File[] markingFile = ((OpenMarkingFileDialog)visibleDialog).getSelectedFiles();
			if(markingFile != null && markingFile.length==1) {// only 1 marking file is allowed
				if(isMarkingsForAll)
					addMarkingsToSelectedImageLayer(markingFile[0], null); // setting markins to all ImageLayers
				else
					addMarkingsToSelectedImageLayer(markingFile[0], imageLayerPath);
			}
			visibleDialog=null;
		} catch (Exception e) {
			LOGGER.severe("ERROR in opening File dialog!");
			e.printStackTrace();
		}
		
	}


	/**
	 * Sets the allowed ImageDimension = present dimension of image(s) in main GUI.
	 *
	 * @param importAllowedImageDimension the allowed dimension
	 * @throws Exception the exception
	 */
	public void setImportAllowedImageDimension(Dimension importAllowedImageDimension) throws Exception {
		this.importAllowedImageDimension = importAllowedImageDimension;
	}




	/**
	 * Sets the panel position and child dialogs when user drags the main window. 
	 * This is only happening in Linux. In other OS the dragging is not possible, because modal dialogs are used.
	 */
	public void setPanelPosition(){
		try {
			this.setBounds(this.gui.getVisibleWindowBounds());
			if(this.visibleDialog != null)
				this.visibleDialog.setBounds(this.getBounds());
			
			if(this.shadyMessageDialog != null)
				this.shadyMessageDialog.setBounds(this.getBounds());
		} catch (Exception e) {
			LOGGER.severe("ERROR in setting Panel Position!");
			e.printStackTrace();
		}
	}

	/**
	 * Sets the folder that is previously used.
	 *
	 * @param folder String path of used folder
	 * @throws Exception the exception
	 */
	public void setPresentFolder(String folder) throws Exception{
		gui.setPresentFolder(folder);
	}


	/**
	 * Sets dialog visible.
	 *
	 * @throws Exception the exception
	 */
	public void showDialog() throws Exception{
		this.setVisible(true);
		this.repaint();
	
		
	}
	
	/**
	 * Opens message dialog with ok-button.
	 *
	 * @param title String title of message
	 * @param message String message
	 * @throws Exception the exception
	 */
	private void showMessage(String title, String message) throws Exception{
		shadyMessageDialog = new ShadyMessageDialog(this, title, message, ID.OK, this);
		shadyMessageDialog.showDialog();
		shadyMessageDialog=null;
	}
	/**
	 *  Refreshes the imageLayerList by going throw array of ImageLayers (+ MarkingLayer) -> creating JPanels and adding
	 *  them on imageScrollPanel (IMAGE LIST)
	 */
	private void updateImageList(){
		try {
			//boolean foundMarkingLayer=false;
			// remove all components
			if(imageScrollPanel.getComponentCount()>0)
				imageScrollPanel.removeAll();
			// add new components to list
			if(dialogImageLayerList.size()>0){
				// set buttons able to use if at least one image path is selected
				this.createImageLayersJButton.setEnabled(true);
				this.createImageLayersJButton.setSelected(true);
				
				String longestPathString="";
				String longestMarkingNameString="";
				Iterator<ImageLayer> iIterator = dialogImageLayerList.iterator();
				while(iIterator.hasNext()){
					ImageLayer im = (ImageLayer)iIterator.next();
					if(im.getImageFilePath().length() > longestPathString.length()){
						longestPathString= im.getImageFilePath();
					}
					if(im.getMarkingLayers() != null && im.getMarkingLayers().size()>0){
					//	foundMarkingLayer=true;
						Iterator<MarkingLayer> mIterator = im.getMarkingLayers().iterator();
						while(mIterator.hasNext()){
							MarkingLayer ma= (MarkingLayer)mIterator.next();
							if(ma.getLayerName().length() > longestMarkingNameString.length()){
								longestMarkingNameString = ma.getLayerName();
							}
						}
					}

				}

				JLabel lab = new JLabel(longestPathString); // test JLabel for counting width of string of different fonts
				int maxStringWidth=0;
				int maxMarkingStringWidth=0;
				maxStringWidth = lab.getFontMetrics(Fonts.b16).stringWidth(longestPathString);
				lab.setText(longestMarkingNameString);
				maxMarkingStringWidth=lab.getFontMetrics(Fonts.p15).stringWidth(longestMarkingNameString);
				if(maxMarkingStringWidth > maxStringWidth)
					maxStringWidth = maxMarkingStringWidth+100;

				imageScrollPanel.setPreferredSize(new Dimension(maxStringWidth+100,getHeightOfAllImageAndMarkingPanels()+10));
				Iterator<ImageLayer> iIterator2 = dialogImageLayerList.iterator();
				while(iIterator2.hasNext()){
					ImageLayer im = (ImageLayer)iIterator2.next();
					imageScrollPanel.add(createImagePanel(im));
				}
			}
			else{
				// set button unable to use if no no image paths selected
				this.createImageLayersJButton.setEnabled(false);
				this.createImageLayersJButton.setSelected(false);
				setImportAllowedImageDimension(null);


				// adjust preferable size of ScrollPane
				//scrollPanelImage.setPreferredSize(scrollPanelImage.getMinimumSize());
			}

			imageScrollPanel.revalidate();
			imageScrollingPane.revalidate();
			imageScrollingPane.repaint();

		} catch (Exception e) {
			LOGGER.severe("Error in updating IMAGE LIST " +e.getClass().toString() + " :" +e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * MarkingPanel is a JPanel which contains title of ImageLayer and list of titles of MarkingLayers. This panel is added to MarkingPanelList.
	 * @author Antti Kurronen
	 *
	 */
	private class ImageAndMarkingPanel extends JPanel{
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -3486848797936767446L;
		private String path; // image path
		private ArrayList<MarkingLayer> markingLayerList;
		private JButton closeJButton;
		private JButton browseJButton;
		private int oneImageTitleHeight=45;
		private int oneMarkingHeight=40;

		private ImageAndMarkingPanel(ImageLayer imageLayer){

			try {
				this.path = imageLayer.getImageFilePath();
				this.markingLayerList=imageLayer.getMarkingLayers();
				int markingListHeight =0;
				if(this.markingLayerList !=null && this.markingLayerList.size()>0)
					markingListHeight = oneMarkingHeight*this.markingLayerList.size();
				this.setMaximumSize(new Dimension(3000,oneImageTitleHeight+markingListHeight));
				this.setPreferredSize(new Dimension(400,oneImageTitleHeight+markingListHeight));
				this.setBackground(Color_schema.dark_30);
				this.setLayout(new BorderLayout());
				this.setBorder(BorderFactory.createLineBorder(Color_schema.dark_100, 1));

				// Set image title panel
				JPanel titlePanel = new JPanel();
				titlePanel.setMaximumSize(new Dimension(2000,oneImageTitleHeight));
				titlePanel.setPreferredSize(new Dimension(400,oneImageTitleHeight));
				titlePanel.setBackground(Color_schema.dark_40);
				titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));
				// title part (path)
				JLabel iPathLabel = new JLabel(this.path);
				Font consolas16= new Font("Consolas", Font.BOLD,16);
				JPanel iPathLabelPanel = new JPanel();
				iPathLabelPanel.setLayout(new BorderLayout());
				iPathLabelPanel.setMaximumSize(new Dimension(2000, oneImageTitleHeight));
				iPathLabel.setFont(consolas16);
				iPathLabel.setForeground(Color_schema.white_230);
				int labelwidth=iPathLabel.getFontMetrics(consolas16).stringWidth(iPathLabel.getText());
				titlePanel.setPreferredSize(new Dimension(labelwidth+100,oneImageTitleHeight));
				iPathLabelPanel.setPreferredSize(new Dimension(labelwidth+20,oneImageTitleHeight));

				this.setPreferredSize(new Dimension(labelwidth+100,oneImageTitleHeight+markingListHeight));
				iPathLabelPanel.add(iPathLabel);
				iPathLabelPanel.setBackground(Color_schema.dark_40);


				titlePanel.add(Box.createRigidArea(new Dimension(10,0)));
				titlePanel.add(iPathLabelPanel);
				titlePanel.add(Box.createHorizontalGlue()); // when panel gets bigger the horizontal extra space comes between title and deleting button


				// setup button for deleting this Image Layer
				JPanel buttonsImagePanel = new JPanel();
				buttonsImagePanel.setLayout(new BoxLayout(buttonsImagePanel, BoxLayout.LINE_AXIS));
				buttonsImagePanel.setMaximumSize(new Dimension(60,40));
				buttonsImagePanel.setPreferredSize(new Dimension(60,40));
				buttonsImagePanel.setMinimumSize(new Dimension(60,40));
				buttonsImagePanel.setBackground(Color_schema.dark_40);
				closeJButton = new JButton(new BigCloseIcon(false));
				closeJButton.setAlignmentY(Component.CENTER_ALIGNMENT);
				closeJButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
				closeJButton.setPreferredSize(new Dimension(25,25));
				closeJButton.setMaximumSize(new Dimension(25,25));
				closeJButton.setMargin(new Insets(0, 0,0, 0));
				closeJButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 1));
				closeJButton.setContentAreaFilled(false);
				closeJButton.setFocusable(false);
				closeJButton.setToolTipText("Remove image: " +imageLayer.getImageFileName());

				// set up listener for closeButton
			//	addMouseListenerToButtons(closeJButton, ID.BUTTON_CLOSE_25);
				MouseListenerCreator.addMouseListenerToBigCloseButtons(closeJButton);
				initActionsToButtons(closeJButton, ID.DELETE_IMAGELAYER);

				browseJButton = new JButton(new BigMarkingIcon(false));
				browseJButton.setAlignmentY(Component.CENTER_ALIGNMENT);
				browseJButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
				browseJButton.setPreferredSize(new Dimension(25,25));
				browseJButton.setMaximumSize(new Dimension(25,25));
				browseJButton.setMargin(new Insets(0, 0,0, 0));
				browseJButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 1));
				browseJButton.setContentAreaFilled(false);
				browseJButton.setFocusable(false);
				browseJButton.setToolTipText("Import Markings for image "+imageLayer.getImageFileName());
			//	addMouseListenerToButtons(browseJButton, ID.BUTTON_IMPORT_MARKINGS);
				MouseListenerCreator.addMouseListenerToImportMarkingsButton(browseJButton);
				initActionsToButtons(browseJButton, ID.OPEN_MARKING_FILE);


				buttonsImagePanel.add(browseJButton);
				buttonsImagePanel.add(Box.createRigidArea(new Dimension(5,0)));
				buttonsImagePanel.add(closeJButton);
				iPathLabel.validate();
				titlePanel.add(buttonsImagePanel);

				// add panels containing marking titles
				JPanel markingTableJPanel = new JPanel();
				markingTableJPanel.setLayout(new BoxLayout(markingTableJPanel, BoxLayout.PAGE_AXIS));
				markingTableJPanel.setMaximumSize(new Dimension(2000,oneMarkingHeight*markingLayerList.size()));
				markingTableJPanel.setPreferredSize(new Dimension(400,oneMarkingHeight*markingLayerList.size()));
				markingTableJPanel.setMinimumSize(new Dimension(400,oneMarkingHeight*markingLayerList.size()));
				markingTableJPanel.setBackground(Color_schema.dark_40);

				// Create and add markingpanels
				if(markingLayerList != null && markingLayerList.size()>0){
					Iterator<MarkingLayer> iIterator = markingLayerList.iterator();
					while(iIterator.hasNext()){
						MarkingLayer ml = (MarkingLayer)iIterator.next();
						if(ml.getLayerName().length()>0){ // MarkingLayer name
							// add markingTitles to markingTable
							markingTableJPanel.add(new SingleMarking(this.path, ml.getLayerName()));
						}
					}
				}
				this.add(titlePanel,BorderLayout.PAGE_START);
				this.add(markingTableJPanel,BorderLayout.CENTER);
				this.validate();
				
			} catch (Exception e) {
				LOGGER.severe("Error in construction of Marking list " +e.getClass().toString() + " :" +e.getMessage());
				e.printStackTrace();
			}
		}

		public String getPath() {
			return path;
		}



	}
	/**
	 * 
	 * Class SingleMarking is a JPanel containing information of a  MarkingPanel.
	 * @author Antti Kurronen
	 *
	 */
	private class SingleMarking extends JPanel{
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -4350091943683475060L;
		private String markingName;
		private String imageLayerPath;

		private SingleMarking(String imageLayerPath, String markingName){		
			try {
				this.setMarkingName(markingName);
				this.setImageLayerPath(imageLayerPath);
				this.setMaximumSize(new Dimension(2000,45));
				this.setPreferredSize(new Dimension(400,45));
				this.setBackground(Color_schema.dark_40);
				this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
				// marking title
				JLabel markingLabel = new JLabel(markingName);
				Font consolas15= new Font("Consolas", Font.PLAIN,15);
				JPanel markingLabelPanel = new JPanel();
				markingLabelPanel.setLayout(new BorderLayout());
				markingLabelPanel.setMaximumSize(new Dimension(2000, 40));
				markingLabel.setFont(consolas15);
				markingLabel.setForeground(Color_schema.white_230);
				int markinglabelwidth=markingLabel.getFontMetrics(consolas15).stringWidth(markingLabel.getText());

				markingLabelPanel.setPreferredSize(new Dimension(markinglabelwidth,40));
				markingLabelPanel.add(markingLabel);
				markingLabelPanel.setBackground(Color_schema.dark_40);


				this.add(Box.createRigidArea(new Dimension(10,0)));
				this.add(markingLabelPanel, BorderLayout.CENTER);
				this.add(Box.createHorizontalGlue()); // when panel gets bigger the horizontal extra space comes between title and deleting button


				// setup button for deleting this Image Layer
				JPanel removeMarkingPanel = new JPanel();
				removeMarkingPanel.setLayout(new BoxLayout(removeMarkingPanel, BoxLayout.LINE_AXIS));
				removeMarkingPanel.setMaximumSize(new Dimension(25,30));
				removeMarkingPanel.setPreferredSize(new Dimension(25,30));
				removeMarkingPanel.setMinimumSize(new Dimension(25,30));
				removeMarkingPanel.setBackground(Color_schema.dark_40);
				JButton deleteMarkingJButton = new JButton(new MediumCloseIcon(false));
				deleteMarkingJButton .setAlignmentY(Component.CENTER_ALIGNMENT);
				deleteMarkingJButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
				deleteMarkingJButton.setPreferredSize(new Dimension(20,20));
				deleteMarkingJButton.setMaximumSize(new Dimension(20,20));
				deleteMarkingJButton.setMargin(new Insets(0, 0,0, 0));
				deleteMarkingJButton.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
				deleteMarkingJButton.setContentAreaFilled(false);
				deleteMarkingJButton.setFocusable(false);
				deleteMarkingJButton.setToolTipText("Remove MarkingLayer: " +markingLabel.getText());

				// set up listener for closeButton	
				MouseListenerCreator.addMouseListenerToMediumCloseButtons(deleteMarkingJButton);
				initActionsToButtons(deleteMarkingJButton, ID.DELETE_MARKINGLAYER);

				this.add(deleteMarkingJButton);


			} catch (Exception e) {
				LOGGER.severe("Error in construction of SingleMarking " +e.getClass().toString() + " :" +e.getMessage());
		}

	}

		/**
		 * Returns a String path of image of the ImageLayer.
		 *
		 * @return String path of image of the ImageLayer
		 * @throws Exception the exception
		 */
		public String getImageLayerPath() throws Exception {
			return imageLayerPath;
		}

		/**
		 * Returns a String name of MarkingLayer.
		 *
		 * @return String name of MarkingLayer
		 * @throws Exception the exception
		 */
		public String getMarkingName() throws Exception {
			return markingName;
		}

		/**
		 * Sets a path of image of the ImageLayer.
		 *
		 * @param imageLayerPath String path of image of the ImageLayer
		 * @throws Exception the exception
		 */
		public void setImageLayerPath(String imageLayerPath) throws Exception {
			this.imageLayerPath = imageLayerPath;
		}

		/**
		 *  Sets a String name of MarkingLayer.
		 *
		 * @param markingName String name of MarkingLayer
		 * @throws Exception the exception
		 */
		public void setMarkingName(String markingName) throws Exception {
			this.markingName = markingName;
		}


	}

}

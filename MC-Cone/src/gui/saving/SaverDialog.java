package gui.saving;

import gui.Color_schema;
import gui.ContentPane;
import gui.GUI;
import gui.MouseListenerCreator;
import gui.ShadyMessageDialog;
import gui.WindowLocator;
import gui.file.FileManager;
import information.Fonts;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
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

/**
 * The Class SaverDialog. A dialog for saving and exporting. 
 * Saving: All data of MarkingLayers
 * Exporting: Results (csv, txt, clipboard), Images.
 */
public class SaverDialog extends JDialog{


/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8428423977175251157L;

final static Logger LOGGER = Logger.getLogger("MCCLogger");

/** The GUI. */
protected GUI gui;

/** The back panel. Shows the visible window. */
protected JPanel backPanel;

/** The cancel JButton. */
private JButton cancelJButton;

/** The image scroll panel. */
protected JPanel imageScrollPanel;

/** The list of ImageLayers. */
protected ArrayList<ImageLayer> imageLayerList;

/** The image scrolling pane. */
private JScrollPane imageScrollingPane;

/** The select file j button. */
protected JButton selectFileJButton;

/** The boolean for showing has successful saving informed already to user. */
protected boolean notInformedSuccessfullSaving;

/** The exporting path. */
public String exportingPath="";

/** The title label. */
private JLabel titleLabel;

/** The saving type. ID.SAVE_MARKINGS, ID.EXPORT_IMAGE, ID.FILE_TYPE_TEXT_FILE, ID.FILE_TYPE_CSV or ID.CLIPBOARD */
protected int savingType;  

/** The file writing type. ID.FILE_NEW_FILE, ID.OVERWRITE, ID.APPEND */
private int fileWritingType; 

/** The one image title height. */
protected int oneImageTitleHeight;

/** The one image path height. */
protected int oneImagePathHeight;

/** The one marking height. */
protected int oneMarkingHeight;

/** The select file dialog. */
protected SelectFileDialog selectFileDialog;

/** The save JButton. */
protected JButton saveJButton;

/** The parent component. */
private Component parentComponent=null;



	/**
	 * Instantiates a new SaverDialog.
	 *
	 * @param d the parent Dialog.
	 * @param gui the GUI
	 * @param iList the list of ImageLayers
	 * @param savingTypeID the ID of saving type (xml, csv, txt, clipboard, image)
	 */
	public SaverDialog(JDialog d, GUI gui, ArrayList<ImageLayer> iList, int savingTypeID){
		super(d, true);
		try{
			this.savingType=savingTypeID;
			this.parentComponent=d;
			setPanelHeights();
			notInformedSuccessfullSaving=true;
			this.imageLayerList =  iList;
			this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			this.gui=gui;
			initComponents();
		//	initKeyListenerToDialog(); -> this is done in MouseListenerCreator..
			setImageList();
			updateSaveButtonState();
			this.revalidate();
			this.setVisible(true);

		} catch (Exception e) {

			LOGGER.severe("Error in saving Markings:  " +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			e.printStackTrace();
		}
	}
	
	/**
	 * Instantiates a new SaverDialog.
	 *
	 * @param frame the prent JFrame
	 * @param gui the GUI
	 * @param iList the list of ImageLayers
	 * @param savingTypeID the ID of saving type
	 */
	public SaverDialog(JFrame frame, GUI gui, ArrayList<ImageLayer> iList, int savingTypeID){

		super(frame, true);
		try{
			this.savingType=savingTypeID;
			this.parentComponent=frame;
			setPanelHeights();
			notInformedSuccessfullSaving=true;
			this.imageLayerList =  iList;
			this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			this.gui=gui;
			initComponents();
		//	initKeyListenerToDialog(); -> this is done in MouseListenerCreator..
			setImageList();
			updateSaveButtonState();
			this.revalidate();
			this.setVisible(true);

		} catch (Exception e) {
			LOGGER.severe("Error in saving Markings:  " +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			e.printStackTrace();
		}
	}

	/**
	 * Closes SaverDialog.
	 */
	protected void cancelSelected(){
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					hideThis();
				} catch (Exception e) {
					LOGGER.severe("Error in cancelling saving dialog!");
					e.printStackTrace();
				}

			}
		});


	}

	
	/**
	 * Check file validity.
	 *
	 * @param file the file
	 * @return the int
	 */
	protected int checkFileValidity(File file) throws Exception{
		return FileManager.checkFile(file, SelectFileDialog.createFileFilterList(this.savingType), this.savingType==ID.SAVE_MARKINGS);
	}

	/**
	 * Creates the single image panel.
	 *
	 * @param layer the ImageLayer
	 * @return the singleImagePanel
	 * @throws Exception the exception
	 */
	protected SingleImagePanel createSingleImagePanel(ImageLayer layer) throws Exception{
		return null;//new SingleImagePanel(layer,this);
	}

	/**
	 * Returns the first proper saving path from singleImagePanels.
	 * If not found any, is "user.home" used.
	 *
	 * @return the first proper path of single image panels
	 * @throws Exception the exception
	 */
	protected String getfirstProperPathOfSingleImagePanels() throws Exception{
		//go through all panels and set path
		Component[] imPanelList= imageScrollPanel.getComponents();

		if(imPanelList != null && imPanelList.length>0){

			 for (int i = 0; i < imPanelList.length; i++) {
				if(((SingleImagePanel)imPanelList[i]).hasSelectedMarkingLayer() && ((SingleImagePanel)imPanelList[i]).getProperFilePathForSaving() != null){
					return ((SingleImagePanel)imPanelList[i]).getProperFilePathForSaving();
				}
			}
		}
		return System.getProperty("user.home");

	}


	/**
	 * Returns the sum of height of all image and marking panels.
	 *
	 * @return the height of all image and marking panels
	 */
	private int getHeightOfAllImageAndMarkingPanels(){
		try {
			int heightValue=0;
			Iterator<ImageLayer> iIterator = imageLayerList.iterator();
			while(iIterator.hasNext()){
				ImageLayer im = (ImageLayer)iIterator.next();
				if(im != null && im.getImageFileName().length()>0){
					heightValue+=(this.oneImageTitleHeight+this.oneImagePathHeight);
				}
				if(im.getMarkingLayers() != null && im.getMarkingLayers().size()>0){
				//	foundMarkingLayer=true;
					Iterator<MarkingLayer> mIterator = im.getMarkingLayers().iterator();
					while(mIterator.hasNext()){
						MarkingLayer ma= (MarkingLayer)mIterator.next();
						if(ma != null && ma.getLayerName().length() > 0){
							heightValue+=this.oneMarkingHeight;
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
	 * Returns the size and position of dialog.
	 *
	 * @return the marking saver bounds
	 * @throws Exception the exception
	 */
	public Rectangle getMarkingSaverBounds() throws Exception{
		return this.getBounds();
	}

	/**
	 * Returns the present folder, where saving will be made.
	 *
	 * @return the present folder
	 * @throws Exception the exception
	 */
	public String getPresentFolder() throws Exception{
		return gui.getPresentFolder();
	}

	/**
	 * Returns the saving type id (Integer). 
	 *
	 * @return the saving type ID.
	 * @throws Exception the exception
	 */
	public int getSavingID() throws Exception{
		return this.savingType;
	}

	/**
	 * Checks for image layers found with given file.
	 *
	 * @param file the file
	 * @return true, if successful
	 */
	public boolean hasImageLayersFound(File file){
		return true; // write code in extended class
	}

	/**
	 * Hides dialog.
	 *
	 * @throws Exception the exception
	 */
	private void hideThis() throws Exception{
		this.setVisible(false);
		this.dispose();
	}

	/**
	 * Informs user from file validity.
	 *
	 * @param fileValidity the file validity
	 * @param pathLabel the file saving path label
	 * @param showMessage the message shown to user
	 * @throws Exception the exception
	 */
	public void informUserFromFileValidity(int fileValidity, JLabel pathLabel, boolean showMessage) throws Exception{
		setSaveButtonEnabledByFileValidity(fileValidity);
		if(fileValidity == ID.FILE_OK || fileValidity == ID.FILE_NEW_FILE ){ // can update file or it is new file
			pathLabel.setForeground(Color_schema.white_230);
			pathLabel.setToolTipText("File path OK.");

		}
		else{
			pathLabel.setForeground(Color_schema.orange_dark);
			pathLabel.setToolTipText("Can't save to this file name. Select or create new file by pressing Browse-button.");

			if(showMessage)		{
				String type="";
				switch (fileValidity) {
				case ID.FILE_CANT_READ:
					type="Can't read file! Select other file for saving.";
					break;
				case ID.FILE_NOT_VALID:
					type="File is not valid XML file! Select other file for saving.";
					break;
				case ID.FILE_CANT_WRITE:
					type="Can't write to file! Select other file for saving.";
					break;
				case ID.FILE_NOT_RIGHT_FORMAT:
					type="FILE is not right format! Select other file for saving.";
					break;
				case ID.FILE_NOT_EXISTS:
					type="Can't create the file! Change the filename or folder.";
					break;
				case ID.FILE_IS_NOT_FILE:
					type="Only Folder path is given. Give file name.";
					break;
					
				case ID.FILE_FILE_SELECT_BY_ANOTHER:
					type="Given file path is already selected in another ImageLayer.";
					break;

				default:
					type="Can't read or write to file! Change the file for saving."; // this should never be shown.
					break;
				}


					ShadyMessageDialog dialog = new ShadyMessageDialog(this, "Invalid file", ""+type, ID.OK, this);
					dialog.showDialog();
					dialog=null;
				}
		}
	}



	/**
	 * Initializes an action for a JButton.
	 * @param button JButton where the action is added
	 * @param typeOfItem int type of action  @see information.ID
	 * @throws Exception
	 */
	protected void initActionsToButtons(JButton button, int typeOfItem) throws Exception{

						// save markings
						if(typeOfItem == ID.SAVE){
							button.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent arg0) {
									SwingUtilities.invokeLater(new Runnable() {
										
										@Override
										public void run() {
											
											try {
												startSavingProcess(savingType, fileWritingType);
											} catch (Exception e) {
												LOGGER.severe("ERROR in starting saving process!");
												e.printStackTrace();
											}
											
										}
									});
									
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
											SwingUtilities.invokeLater(new Runnable() {
												
												@Override
												public void run() {
													cancelSelected();
													
												}
											});
										} catch (Exception e) {
											LOGGER.severe("ERROR in cancelling saving process!");
											e.printStackTrace();
										}
										
									}
								});
							}
	}


	/**
	 * Initializes the panel containing JButton for browsing.
	 *
	 * @param savingID the saving id
	 * @param label the label
	 * @return the j panel
	 * @throws Exception the exception
	 */
	protected JPanel initBrowsingPanel(int savingID, String label) throws Exception{
		try {
			JPanel imageButtonJPanel = new JPanel();
			imageButtonJPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			imageButtonJPanel.setBackground(Color_schema.dark_35);
			imageButtonJPanel.setMinimumSize(new Dimension(200,50));
			imageButtonJPanel.setMaximumSize(new Dimension(200,50));

			// Select file path for all ImageLayers
			selectFileJButton = new JButton(label);
			selectFileJButton.setPreferredSize(new Dimension(250,30));
			selectFileJButton.setBackground(Color_schema.dark_20);
			initActionsToButtons(selectFileJButton, ID.OPEN_MARKING_FILE);
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
			imageButtonJPanel.add(selectFileJButton);
			return imageButtonJPanel;
		} catch (Exception e) {
			
			LOGGER.fine("Error initializing BrowsingPanel: "+ e.getMessage());
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Initializes the browsing panel with label given for JButton.
	 *
	 * @return the JPanel
	 * @throws Exception the exception
	 */
	protected JPanel initBrowsingPanelWithLabel() throws Exception{
		return initBrowsingPanel(this.savingType, "Browse");
	}

	/**
	 * Initializes the components of dialog.
	 *
	 * @throws Exception the exception
	 */
	private void initComponents() throws Exception{
		
		this.setBounds(WindowLocator.getVisibleWindowBounds(this.parentComponent));
		this.setUndecorated(true);
		this.setBackground(new Color(0,0,0,0)); // transparent color
		this.setContentPane(new ContentPane()); // set contentpane to get dimming
		this.getContentPane().setBackground(Color_schema.dark_30);
		this.getContentPane().setLayout(new GridBagLayout());

		backPanel = new JPanel();
		backPanel.setBackground(Color_schema.dark_30);
		backPanel.setLayout(new BorderLayout());

		backPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 5));
		backPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		backPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		backPanel.setMaximumSize(new Dimension((int)(this.getBounds().getWidth()*0.95), (int)(this.getBounds().getHeight()*0.95)));
		backPanel.setMinimumSize(new Dimension((int)(this.getBounds().getWidth()*0.5), (int)(this.getBounds().getHeight()*0.5)));
		backPanel.setPreferredSize(new Dimension((int)(this.getBounds().getWidth()*0.6), (int)(this.getBounds().getHeight()*0.6)));
		// too small size -> make bigger
		if(backPanel.getPreferredSize().getWidth()<800 || backPanel.getPreferredSize().getHeight() <500)
			backPanel.setPreferredSize(new Dimension((int)(this.getBounds().getWidth()*0.95), (int)(this.getBounds().getHeight()*0.95)));

		backPanel.add(initImageViewPanelWithTitle(), BorderLayout.PAGE_START);
		JPanel browsingPanel= initBrowsingPanelWithLabel();
		if(browsingPanel != null)
		backPanel.add(browsingPanel, BorderLayout.CENTER);
		backPanel.add(initDownPanel(),BorderLayout.PAGE_END);

		this.add(backPanel);
		this.validate();
		this.repaint();
	}




/**
 * Initializes the bottom panel containing buttons for saving and closing dialog.
 *
 * @return the j panel
 */
private JPanel initDownPanel(){

	try {
		// SETUP buttons of lowest part of tab
		JPanel downButtonPanel =new JPanel();
		downButtonPanel.setLayout(new BoxLayout(downButtonPanel, BoxLayout.LINE_AXIS));
		downButtonPanel.setBackground(Color_schema.dark_30);
		downButtonPanel.setMinimumSize(new Dimension((int)backPanel.getMinimumSize().getWidth(), 50));
		downButtonPanel.setPreferredSize(new Dimension(100,50));
		downButtonPanel.setMaximumSize(new Dimension(100,50));
		JPanel cancelJpanel = new JPanel();
		cancelJpanel.setLayout(new BorderLayout());
		cancelJpanel.setPreferredSize(new Dimension(150,50));
		cancelJpanel.setMaximumSize(new Dimension(100,30));
		cancelJButton = new JButton("CLOSE");
		cancelJButton.setFocusable(false);
		cancelJButton.setForeground(Color_schema.orange_dark);
		initActionsToButtons(cancelJButton, ID.CANCEL);	// the action
		MouseListenerCreator.addMouseListenerToCancelButtons(cancelJButton); // changes the colors of button when pressed
		MouseListenerCreator.addKeyListenerToButton(cancelJButton, ID.BUTTON_CANCEL);

		cancelJButton.setPreferredSize(new Dimension(100,50));
		cancelJpanel.add(cancelJButton);
		downButtonPanel.add(Box.createRigidArea(new Dimension(20,0)));
		downButtonPanel.add(initSaveButtonWithTitle());
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
 * Initializes the image view panel.
 *
 * @param title the title
 * @return the j panel
 */
public JPanel initImageViewPanel(String title){
	// SETUP THE Panel which shows the list of images and markings
	try {
		// Panel where other panels are added
		JPanel msBackPanel = new JPanel();
		msBackPanel.setLayout(new BorderLayout());
		msBackPanel.setBackground(Color_schema.dark_30);
		msBackPanel.setLayout(new BorderLayout());
		msBackPanel.setMaximumSize(this.getMaximumSize());
		msBackPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));

		// TitlePanel
		JPanel upTitleJPanel = new JPanel();
		upTitleJPanel.setBackground(Color_schema.dark_30);
		upTitleJPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		upTitleJPanel.setMaximumSize(new Dimension(200,40));
		upTitleJPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		upTitleJPanel.setBorder(BorderFactory.createEmptyBorder());
		titleLabel = new JLabel(title);

		titleLabel.setFont(new Font("Consolas", Font.BOLD,20));
		titleLabel.setForeground(Color_schema.white_230);
		upTitleJPanel.add(titleLabel);
		msBackPanel.add(upTitleJPanel, BorderLayout.PAGE_START);

		// CENTER PANEL
		JPanel centerPanel=new JPanel();
		centerPanel.setBackground(Color_schema.dark_30);
		centerPanel.setLayout(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createEmptyBorder());
		JPanel scrollBiggerPanel = new JPanel();
		scrollBiggerPanel.setLayout(new BorderLayout());
		scrollBiggerPanel.setBorder(BorderFactory.createEmptyBorder());
		scrollBiggerPanel.setBackground(Color_schema.dark_35);

		imageScrollPanel = new JPanel();
		imageScrollPanel.setBackground(Color_schema.dark_35);
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

		scrollBiggerPanel.add(imageScrollingPane, BorderLayout.CENTER);
		centerPanel.add(scrollBiggerPanel, BorderLayout.CENTER);
		msBackPanel.add(centerPanel, BorderLayout.CENTER);
	
		return msBackPanel;
	} catch (Exception e) {
		LOGGER.severe("Error in creating imageViewPanel:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
		return null;
	}

}

/**
 * Initializes the ImageViewPanel with given title.
 *
 * @return the JPanel
 * @throws Exception the exception
 */
protected JPanel initImageViewPanelWithTitle() throws Exception{
	return initImageViewPanel("Title");
}

/*
 * Initializes the key listener to dialog.
 */
/*private void initKeyListenerToDialog(){

	InputMap inputMap= (this.backPanel).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	inputMap.put(KeyStroke.getKeyStroke("pressed ENTER"), "enter_pressed");
	ActionMap actionMap = 	(this.backPanel).getActionMap();
	actionMap.put("enter_pressed", new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			startSavingProcess(savingType, fileWritingType);
		}
	});
}
*/


	/**
	 * Initializes the save button of dialog.
	 *
	 * @param saveTitle the save title
	 * @return the j panel
	 * @throws Exception the exception
	 */
	public JPanel initSaveButton(String saveTitle) throws Exception{
		JPanel saveDownLayerJPanel = new JPanel();
		saveDownLayerJPanel.setLayout(new BorderLayout());
		saveDownLayerJPanel.setPreferredSize(new Dimension(220,30));
		saveDownLayerJPanel.setMaximumSize(new Dimension(220,30));
		saveJButton = new JButton(saveTitle);
		if(this.savingType==ID.CLIPBOARD || this.savingType==ID.EXPORT_PREVIEW_IMAGES)
			saveJButton.setEnabled(true);
		else
			saveJButton.setEnabled(false);
	
		saveJButton.setPreferredSize(new Dimension(220,30));
		saveJButton.setFocusable(false);
	
		initActionsToButtons(saveJButton, ID.SAVE);
		MouseListenerCreator.addMouseListenerToNormalButtons(saveJButton); // changes the colors of button when pressed
		MouseListenerCreator.addKeyListenerToButton(saveJButton, ID.BUTTON_ENTER);
		saveDownLayerJPanel.add(saveJButton, BorderLayout.CENTER);
		return saveDownLayerJPanel;
	}

	/**
	 * Inits the save button with given title.
	 *
	 * @return the j panel
	 * @throws Exception the exception
	 */
	protected JPanel initSaveButtonWithTitle() throws Exception{
		return initSaveButton("Title");
	}

	/**
	 * Initializes the new SelectFileDialog.
	 *
	 * @throws Exception the exception
	 */
	protected void initSelectFileDialog() throws Exception{
		String path = gui.getPresentFolder();
		if(path== null)
			path = System.getProperty("user.home");
		this.selectFileDialog = new SelectFileDialog(this.gui, path, this.backPanel, this.savingType);
	}

	/**
	 * Checks if is browse panel shown.
	 *
	 * @return true, if is browse panel is shown.
	 */
	public boolean isBrowsePanelShown(){
		return true;
	}
											
	
	/**
	 * Checks if is file path found.
	 *
	 * @param filePath the file path
	 * @return true, if is file path found
	 * @throws Exception the exception
	 */
	public boolean isFilePathFound(String filePath) throws Exception{
		Component[] imPanelList= imageScrollPanel.getComponents();
		
		if(imPanelList != null && imPanelList.length>0 && filePath != null && filePath.length()>0){

			 for (int i = 0; i < imPanelList.length; i++) {
				String otherPath = ((SingleImagePanel)imPanelList[i]).getProperFilePathForSaving();
				if(otherPath != null && otherPath.length()>0 && otherPath.equals(filePath))
					return true;		
			}
		}
		return false;
		
	}


	/**
	 * Checks if is filePathPanel shown.
	 *
	 * @return true, if is file path panel shown
	 * @throws Exception the exception
	 */
	protected boolean isFilePathPanelShown() throws Exception{
		return true;
	}


	/**
	 * Select path for all single image panels. Same file path is given for all saved ImageLayers.
	 *
	 * @throws Exception the exception
	 */
	protected void selectPathForAllSingleImagePanels() throws Exception{
		// write code in extended class
	}


	/**
	 * Select a path for single image panel. A single file path for single ImageLayer.
	 *
	 * @param properPath the file path
	 * @param panelID the ID of ImagePanel
	 * @throws Exception the exception
	 */
	protected void selectPathForSingleImagePanel(String properPath, int panelID) throws Exception{
		// write code in extended class
	}
	
	/**
	 * Sets the all background colors of all SingleMarkingPanels to default.
	 *
	 * @throws Exception the exception
	 */
	protected void setAllMarkingLayerBackgroundsToDefault() throws Exception{
		//go through all panels and set path
		Component[] imPanelList= imageScrollPanel.getComponents();
		if(imPanelList != null && imPanelList.length>0){

			 for (int i = 0; i < imPanelList.length; i++) {
				 SingleImagePanel sip =(SingleImagePanel)imPanelList[i];

					 sip.setAllSingleMarkingPanelBGstoDefault(); // set to default
					 sip.repaint();
			}
		}
	}

	/**
	 * Sets the file path for single SingleImagePanel.
	 *
	 * @param path the path
	 * @param panelID the panel id
	 * @throws Exception the exception
	 */
	protected void setFilePathForSingleImagePanel(String path, int panelID) throws Exception{

		int fileValidity = checkFileValidity(new File(path));

		//go through all panels and set path
		Component[] imPanelList= imageScrollPanel.getComponents();

		if(imPanelList != null && imPanelList.length>0 && path != null && path.length()>0){

			 for (int i = 0; i < imPanelList.length; i++) {
				if( ((SingleImagePanel)imPanelList[i]).getLayerID()==panelID){
					((SingleImagePanel)imPanelList[i]).setProperFileForMarkings(path,true, fileValidity);
					((SingleImagePanel)imPanelList[i]).repaint();
					return;
				}
			}
		}

	}

	/**
	 * Sets the same file path for all SingleImagePanels.
	 *
	 * @param path the new file paths for all
	 * @throws Exception the exception
	 */
	protected void setFilePathsForAll(String path) throws Exception{
		//go through all panels and set path
		int fileValidity = checkFileValidity(new File(path));
		Component[] imPanelList= imageScrollPanel.getComponents();
		boolean notShowedMessage=true;
		if(imPanelList != null && imPanelList.length>0 && path != null && path.length()>0){

			 for (int i = 0; i < imPanelList.length; i++) {
				((SingleImagePanel)imPanelList[i]).setProperFileForMarkings(path,notShowedMessage, fileValidity);
				((SingleImagePanel)imPanelList[i]).repaint();
				if(notShowedMessage)
					notShowedMessage=false;
			}
		}

	}

	/**
	 * Sets the ID of file writing type. 
	 *
	 * @param t the ID for new file writing type
	 * @throws Exception the exception
	 */
	protected void setFileWritingType(int t) throws Exception{
		this.fileWritingType=t;
	}

	/**
	 *  Refreshes the imageLayerList by going throw array of ImageLayers (+ MarkingLayer) -> creating JPanels and adding
	 *  them on imageScrollPanel (IMAGE LIST)
	 */
	private void setImageList(){
		try {
			//boolean foundMarkingLayer=false;
			// remove all components
			if(imageScrollPanel.getComponentCount()>0)
				imageScrollPanel.removeAll();
			// add new components to list
			if(imageLayerList.size()>0){
				// set buttons able to use if at least one image path is selected



				String longestPathString="";
				String longestMarkingNameString="";
				Iterator<ImageLayer> iIterator = imageLayerList.iterator();
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
				// grow height of imageScrollPanel
				imageScrollPanel.setPreferredSize(new Dimension(maxStringWidth+100,getHeightOfAllImageAndMarkingPanels()+10));
				Iterator<ImageLayer> iIterator2 = imageLayerList.iterator();
				while(iIterator2.hasNext()){ // add SingleImagePanels
					ImageLayer im = (ImageLayer)iIterator2.next();
					imageScrollPanel.add(createSingleImagePanel(im));
				}
			}
			
			
			this.repaint();

		} catch (Exception e) {
			LOGGER.severe("Error in updating IMAGE LIST " +e.getClass().toString() + " :" +e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Sets the background colors to default for SingleMarkingPanels of given ImageLayer (ID given).
	 *
	 * @param iLayerID the new marking layer backgrounds to default
	 * @throws Exception the exception
	 */
	protected void setMarkingLayerBackgroundsToDefault(int iLayerID) throws Exception{
		//go through all panels and set path
		Component[] imPanelList= imageScrollPanel.getComponents();
		if(imPanelList != null && imPanelList.length>0){

			 for (int i = 0; i < imPanelList.length; i++) {
				 SingleImagePanel sip =(SingleImagePanel)imPanelList[i];
				 if(iLayerID== ID.IMAGELAYER_UNDEFINED || iLayerID==sip.getLayerID()){
					 sip.setAllSingleMarkingPanelBGstoDefault();
					 sip.repaint();
				 }
			}
		}

	}

	/**
	 * Sets the panel height.
	 *
	 * @throws Exception the exception
	 */
	private void setPanelHeights() throws Exception{
		oneImageTitleHeight=45;
		oneMarkingHeight=40;
		if(isFilePathPanelShown())
			oneImagePathHeight=30;
		else
			oneImagePathHeight=0;

	}

	/**
	 * Sets the present folder.
	 *
	 * @param folder the new present folder
	 * @throws Exception the exception
	 */
	public void setPresentFolder(String folder) throws Exception{
		gui.setPresentFolder(folder);
	}

	/**
	 * Sets the save button enabled by file validity.
	 *
	 * @param validityID the new save button enabled by file validity
	 * @throws Exception the exception
	 */
	protected void setSaveButtonEnabledByFileValidity(int validityID) throws Exception{
		// implement in extended class
	}


	/**
	 * Sets the successfully saving backgrounds of SingleMarkingPanels.
	 *
	 * @param successfullySavedMarkingPanelIDs the new succesfull saving backgrounds
	 * @throws Exception the exception
	 */
	public void setSuccesfullSavingBackgrounds(ArrayList<Integer> successfullySavedMarkingPanelIDs) throws Exception{
		//go through all panels and set path
		Component[] imPanelList= imageScrollPanel.getComponents();
		if(imPanelList != null && imPanelList.length>0 && successfullySavedMarkingPanelIDs != null && successfullySavedMarkingPanelIDs.size()>0){

			 for (int i = 0; i < imPanelList.length; i++) {
				((SingleImagePanel)imPanelList[i]).setSuccessfullSavingColor(successfullySavedMarkingPanelIDs); // set color
				((SingleImagePanel)imPanelList[i]).repaint();
			}
		}

	}

	/**
	 * Start saving process.
	 *
	 * @param savingID ID of type of saving (ID.SAVE_MARKINGS, ID.EXPORT_IMAGE...)
	 * @param exportType the id of export type  (ID.FILE_NEW_FILE, ID.OVERWRITE, ID.APPEND)
	 * @throws Exception the exception
	 */
	protected void startSavingProcess(int savingID, int exportType) throws Exception{
		// overwrite this method in extended class
	}
	
	/**
	 * Updates saveButton state.
	 *
	 * @throws Exception the exception
	 */
	protected void updateSaveButtonState() throws Exception{
		// create code in extended class.
	}
















}



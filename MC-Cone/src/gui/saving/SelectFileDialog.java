package gui.saving;

import gui.Color_schema;
import gui.ContentPane;
import gui.MouseListenerCreator;
import gui.ShadyMessageDialog;
import gui.file.CSVfilter;
import gui.file.FileManager;
import gui.file.GIFfilter;
import gui.file.JPGfilter;
import gui.file.PNGfilter;
import gui.file.TIFFfilter;
import gui.file.TXTfilter;
import gui.file.XMLfilter;
import information.Fonts;
import information.ID;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

/**
 * Dialog for selecting image files for adding to ImageLayer list or selecting marking (xml-file) for one selected image
 * @author Antti
 *
 */
public class SelectFileDialog extends JDialog{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2455724957529557314L;

	/** The Constant LOGGER. */
	final static Logger LOGGER = Logger.getLogger("MCCLogger");	
	
	/** The select file dialog back panel. */
	private JPanel selectFileDialogBackPanel;
	
	/** The file chooser. */
	private JFileChooser fileChooser;
	
	/** The JButton for cancel file choosing. */
	private JButton cancelFileChooserJButton;
	
	/** The JButton SELECT. */
	private JButton addFileChooserJButton;
	
	/** The proper saving file or folder path. */
	private String properSavingFileOrFolderPath;
	
	/** The file type. ID.EXPORT_IMAGE, ID.SAVE_MARKINGS, ID.FILE_TYPE_CSV or ID.FILE_TYPE_TEXT_FILE.*/
	private int fileType;
	
	/** The file filters. */
	private ArrayList<FileFilter> fileFilters;
	
	/** The selected file path. */
	private String selectedFilePath;
	
	/** The file writing type. ID.OVERWRITE, ID.APPEND or ID.FILE_NEW_FILE. */
	public int fileWritingType;  
	
	/** The back panel component. */
	private JComponent backPanelComponent;
	
	/** The owner frame. */
	private JFrame ownerFrame;


	/**
	 * Class constructor for Dialog. The Dialog is JFilechooser which gives to select image files
	 *
	 * @param frame owner frame
	 * @param path the path
	 * @param backPanel the back panel
	 * @param fileType the file type
	 */
	public SelectFileDialog(JFrame frame, String path, JComponent backPanel, int fileType){
		super(frame, true);
		try {
			this.ownerFrame=frame;
			this.backPanelComponent=backPanel;
			this.fileType=fileType;
			this.properSavingFileOrFolderPath= path;
			initFileFilters();
			initFileDialog();
		} catch (Exception e) {
			LOGGER.severe("Error in opening dialog for selecting images!");
			e.printStackTrace();
		}


	}

	/**
	 * Adds the action listener to JFilehooser.
	 *
	 * @throws Exception the exception
	 */
	private void addActionListenerToFileChooser() throws Exception{
		ActionListener actionListener =new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser fChooser=(JFileChooser)e.getSource();
					String commandString = e.getActionCommand();
					if(commandString.equals(JFileChooser.APPROVE_SELECTION)){
						if(fChooser.getSelectedFile().isDirectory()){

						}
						else{
							fileSelected();
						}
					}
					else{
						
						
					}
				} catch (Exception e1) {
					LOGGER.severe("Error in choosing file!");
					e1.printStackTrace();
				}

			}
		};
		fileChooser.addActionListener(actionListener);
	}




	/**
	 * Adds the actions to JButtons of file dialog.
	 *
	 * @param button the button
	 * @throws Exception the exception
	 */
	private void addActionsToFileDialogButtons(JButton button) throws Exception{
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					fileSelected();
				} catch (Exception e1) {
					LOGGER.severe("Error in selecting file!");
					e1.printStackTrace();
				}
			}
		});
	}

	/**
	 * Adds the file filters to JFilechooser.
	 *
	 * @throws Exception the exception
	 */
	private void addFileFilters() throws Exception{
		//boolean hasSetFileFilter=false;
		for (Iterator<FileFilter> iterator = fileFilters.iterator(); iterator.hasNext();) {
			FileFilter filter = (FileFilter) iterator.next();
			fileChooser.addChoosableFileFilter(filter);

			fileChooser.setFileFilter(filter); // sets the last file filter as selected
		}
	}
	
	/**
	 * Creates the list of all file filters. 
	 * File types: 
	 * data: xml 
	 * text: csv, txt
	 * images: gif, jpg, png, tiff
	 *
	 * @param id the id of file type
	 * @return the array list of file filters
	 */
	public static ArrayList<FileFilter> createFileFilterList(int id){
		try {
			ArrayList<FileFilter> filterList=new ArrayList<FileFilter>();
			switch (id) {
			case ID.SAVE_MARKINGS:
				filterList.add(new XMLfilter());
				break;
			case ID.FILE_TYPE_CSV:
				filterList.add(new CSVfilter());	
				break;
			case ID.FILE_TYPE_TEXT_FILE:
				filterList.add(new TXTfilter());
				break;
			case ID.EXPORT_IMAGE:
				filterList.add(new GIFfilter());
				filterList.add(new JPGfilter());
				filterList.add(new PNGfilter());
				filterList.add(new TIFFfilter());
			
				break;

			default:
				break;
			}

			return filterList;
		} catch (Exception e) {
			LOGGER.severe("Error in creating file filter list!");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 *  Checks the given file and saves the path and fileWritingType. If file extension is not given -> adds the extension
	 */
	private void fileSelected(){
		try {
			boolean newFile = false;
			File file=fileChooser.getSelectedFile();
			String fileNameText ="";
			//Get the text of  JTextField from filechooser
			fileNameText=  findTextOfJTextFieldFromFileChooser(fileChooser);
				
			String finalFileNamePath=null;
			if(file != null && fileNameText.equals(file.getName())){
				finalFileNamePath=file.getAbsolutePath();
			}
			else
			 // create file
			if(fileNameText != null && fileNameText.length()>0 && (file == null || !fileNameText.equals(file.getName()))){
					LOGGER.fine("curr "+fileChooser.getFileFilter().getDescription());
				
					File f=new File(fileChooser.getCurrentDirectory()+"/"+fileNameText);
					String fileName;
					if(!f.getAbsolutePath().endsWith(fileChooser.getFileFilter().getDescription())){
						String fixedName=fixFileName(f.getAbsolutePath());
						fileName =  fixedName+fileChooser.getFileFilter().getDescription();
						LOGGER.fine(fileName);

						f= new File(fileName);
						if(!f.exists()){ // not file found
							newFile=true; 
						}
					}
					else{ // has extension
						fileName =  f.getAbsolutePath();
						LOGGER.fine(fileName);
						f= new File(fileName);
						if(!f.exists()){
							newFile=true;
						}
					}

					if(fileName.contains(".") )
							if( FileManager.accept(fileName, fileChooser.getFileFilter())){
						finalFileNamePath=fileName;
					}
			}

			// if given some filepath
			if (finalFileNamePath != null) {
					if(fileType == ID.SAVE_MARKINGS){ // saving markings to xml-file
						boolean hasFoundImageLayer=false;
						if(!newFile){
							hasFoundImageLayer=hasImageLayersFound(finalFileNamePath); // checks the file if imagelayers has found
							LOGGER.fine("foundImagelayers: "+hasFoundImageLayer);
						}
						if (!hasFoundImageLayer || showOverwriteMessage("Given ImageLayer(s) found from XML-file!", "Update information of ImageLayer(s)?") ){ // if file exists should it be overwritten

								setSelectedFilePath(finalFileNamePath);
								if(newFile)
									setFileWritingType(ID.FILE_NEW_FILE);
								else
									setFileWritingType(ID.OVERWRITE);

						}else{ // user don't want to overwrite
							return;
						}
					} else if (fileType == ID.EXPORT_IMAGE) { // exporting image
						if (!newFile && showOverwriteMessage("Image file exists!", " Overwrite?") || newFile){ // if file exists should it be overwritten

							setSelectedFilePath(finalFileNamePath);
							if(newFile)
								setFileWritingType(ID.FILE_NEW_FILE);
							else
								setFileWritingType(ID.OVERWRITE);

					}else{
						return;
					}

					}
					else { // Export result to file (ID.FILE_TYPE_CSV or ID.FILE_TYPE_TEXT_FILE)
						if(!newFile){
							int selection = showAppendOverwriteMessage();
							if(selection == ID.CANCEL){
								return;
							}
							else{
								setFileWritingType(selection);
							}
						}
						setSelectedFilePath(finalFileNamePath);

					}
			} else{
				showFileMessage("File not valid", "Select new File.");
				LOGGER.warning("Given file is not accepted");
				return;
			}
			hideDialog();
		} catch (Exception e) {
			LOGGER.severe("Error in selecting file for markings: " +e.getMessage());
			e.printStackTrace();
			setFileWritingType(ID.ERROR);
			setSelectedFilePath(null);
			showFileMessage("Error in selecting file", "Try again.");
			hideDialog();
		}
	}
	
	/**
	 * Finds first JTextField from component recursively.
	 *
	 * @param comp the Component
	 * @return the Component JTextField if found, otherwise null;
	 */
	private Component findJTextField(Component comp) {
	    try {
			if (comp instanceof JTextField) return comp;
			if (comp instanceof Container) {
			    Component[] components = ((Container)comp).getComponents();
			    for(int i = 0; i < components.length; i++) {
			        Component child = findJTextField(components[i]);
			        if (child != null){ 
			        	//System.out.println(child.getClass().toString());
			        	return findJTextField(child);           	
			        }
			    }
			}
			
			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in finding Text field!");
			e.printStackTrace();
			return null;
		}
	}
	


	/**
	 * Finds Text of  JTextField from a JFileChooser.
	 *
	 * @param chooser the JFileChooser
	 * @return the string of text of JTextField
	 * @throws Exception the exception
	 */
	private String findTextOfJTextFieldFromFileChooser(JFileChooser chooser) throws Exception{
		
		// go through all children components
		for(int i = 0; i < ((JFileChooser)chooser).getComponentCount(); i++) {
			
			Component chooserComponent = findJTextField(fileChooser);
			
			if(chooserComponent != null && chooserComponent instanceof JTextField){
				JTextField chooserJTextField = (JTextField)chooserComponent;
				if(chooserJTextField != null && chooserJTextField.getText() != null && chooserJTextField.getText().length()>0){
					return chooserJTextField.getText().trim();
									
				}			
			}
		}
		return null;
		
	}
	
/**
 * Sets the text of j text field from file chooser.
 *
 * @param chooser the FileChooser
 * @param text the text
 * @throws Exception the exception
 */
private void setTextOfJTextFieldFromFileChooser(JFileChooser chooser, String text) throws Exception{
		
		// go through all children components
		for(int i = 0; i < ((JFileChooser)chooser).getComponentCount(); i++) {
			
			Component chooserComponent = findJTextField(fileChooser);
			
			if(chooserComponent != null && chooserComponent instanceof JTextField){
				JTextField chooserJTextField = (JTextField)chooserComponent;
				chooserJTextField.setText(text);
									
				}			
			}
	}
		

	/**
	 * Removes file extension from file name and returns it.
	 *
	 * @param fileName the file name
	 * @return the string file name without extension
	 * @throws Exception the exception
	 */
	private String fixFileName(String fileName) throws Exception{
		
		if(fileName != null && fileName.length()>1 && fileName.contains(".")){
			int index=fileName.lastIndexOf(".");
			return fileName.substring(0, index-1);
	
		}
		return fileName;
	}

	/**
	 * Returns the file filters.
	 *
	 * @return the file filters
	 * @throws Exception the exception
	 */
	public ArrayList<FileFilter> getFileFilters() throws Exception{
		return this.fileFilters;
	}

	/**
	 * Returns the file writing type. Overwrite, append...
	 *
	 * @return the file writing type
	 * @throws Exception the exception
	 */
	public int getFileWritingType() throws Exception{
		return this.fileWritingType;
	}

	/**
	 * Converts given File to directory if it is not already one.
	 *
	 * @param filePath the file path
	 * @return the folder of given file
	 * @throws Exception the exception
	 */
	private File getFolder(String filePath) throws Exception{
		try {
			File f = new File(filePath);
			if (f.exists()) {
				if (f.isDirectory()) {
					// return the folder
					return f;
				} else if (f.isFile()) {
					// the folderPath was a file -> get the folder of that file
					return new File(f.getParent());
				}
			}
			// file not exists return the home folder
			return new File(System.getProperty("user.home"));
		} catch (Exception e) {
			LOGGER.severe("Error in getting folder of file " +e.getClass().toString() + " :" +e.getMessage()+ " line: "+ e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}


/*  // MAY BE NEEDED LATER
	private String getMostCommonPath(File[] fileGroup){
		try {
			ArrayList<PathCount> countedPathList = new ArrayList<PathCount>();
			PathCount biggestPathCount = null;
			if(fileGroup != null && fileGroup.length>0){

				for (int j = 0; j < fileGroup.length; j++) {
					if(fileGroup[j] != null){
						if(fileGroup[j].exists()){ // file exist
							String folderPath="";
							if(fileGroup[j].isDirectory()){ // is folder
								folderPath=fileGroup[j].getAbsolutePath();
							}
							else if(fileGroup[j].isFile()){ // is file -> get folder
								folderPath= (getFolder(fileGroup[j].getAbsolutePath())).getAbsolutePath();
							}
							// add folder to list -> returns the PathCount object with highest count value
							biggestPathCount = updatePathCount(countedPathList, folderPath, biggestPathCount);
						}
					}
				}
			}
			else{
				return null;
			}

			return biggestPathCount.getFolderPath();
		} catch (Exception e) {
			LOGGER.severe("Error in gettin most common folderpath " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}

	}
*/
	/**
 * Returns the selected file path.
 *
 * @return the selected file path
 * @throws Exception the exception
 */
	public String getSelectedFilePath() throws Exception {
		return this.selectedFilePath;
	}


/**
 * Checks for ImageLayer with given file name found..
 *
 * @param fileName the file name
 * @return true, if successful
 */
protected boolean hasImageLayersFound(String fileName) throws Exception{
		return true; // implemented in extended class
	}

/**
 * Hides dialog by calling hideThis().
 */
private void hideDialog(){
	SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
			hideThis();

		}
	});
}

/**
 * Closes dialog.
 */
private void hideThis(){
	this.setVisible(false); // close dialog
	this.dispose();
}

/**
 * Initializes the file chooser panel.
 *
 * @return the j panel
 * @throws Exception the exception
 */
private JPanel initFileChooserPanel() throws Exception{
	try {

		JPanel fBackPanel = new JPanel();
		fBackPanel.setLayout(new BorderLayout());
		fBackPanel.setBackground(Color_schema.dark_40);
		fBackPanel.setLayout(new BorderLayout());
		fBackPanel.setMaximumSize(backPanelComponent.getMaximumSize());
		fBackPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));

		JPanel upPanel = new JPanel();
		upPanel.setBackground(Color_schema.dark_30);
		upPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		upPanel.setMaximumSize(new Dimension(100,40));
		upPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		upPanel.setBorder(BorderFactory.createEmptyBorder());

		JLabel titleLabel = new JLabel("SELECT FILE");

		titleLabel.setFont(Fonts.b20);
		titleLabel.setForeground(Color_schema.white_230);
		upPanel.add(titleLabel);
		fBackPanel.add(upPanel, BorderLayout.PAGE_START);

		// CENTER PANEL
		JPanel centerPanel=new JPanel();
		centerPanel.setBackground(Color_schema.dark_40);
		centerPanel.setLayout(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createEmptyBorder());

		// setup filechooser panels and JFilechooser
		JPanel imageChooserPanel = new JPanel();
		imageChooserPanel.setLayout(new BorderLayout(5,5));
		imageChooserPanel.setMinimumSize(new Dimension(((int)(selectFileDialogBackPanel.getMinimumSize().getWidth()*0.9)), ((int)(selectFileDialogBackPanel.getMinimumSize().getHeight()*0.7))));

		imageChooserPanel.setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 3));
		imageChooserPanel.setBackground(Color_schema.dark_40);

		fileChooser = new JFileChooser();

		fileChooser.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 2));
		fileChooser.setBackground(Color_schema.dark_40);
		fileChooser.setControlButtonsAreShown(false);
		fileChooser.setMultiSelectionEnabled(false); // several files are not allowed to select
		// set file filters for fileChooser
		addFileFilters();
		

		// add mouse listener to get select the the file by double click
		addActionListenerToFileChooser();
		// set the image file folder as current folder of jfilechooser
		setCurrentFileOrFolder();


		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setMaximumSize(new Dimension(1000,800)); // not so nice when going too large
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		imageChooserPanel.add(fileChooser, BorderLayout.CENTER);
		centerPanel.add(imageChooserPanel, BorderLayout.CENTER);

		// Setup buttons for adding file / cancel
		JPanel chooserButtonPanel = new JPanel();
		chooserButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		chooserButtonPanel.setBackground(Color_schema.dark_40);
		chooserButtonPanel.setPreferredSize(new Dimension(50,40));
		chooserButtonPanel.setMinimumSize(new Dimension(50,40));
		chooserButtonPanel.setMaximumSize(new Dimension(2000,40));
		chooserButtonPanel.setBorder(BorderFactory.createEmptyBorder());

		addFileChooserJButton = new JButton("SELECT");
		addFileChooserJButton.setPreferredSize(new Dimension(150,30));
		addFileChooserJButton.setFocusable(true);
		addActionsToFileDialogButtons(addFileChooserJButton);	// setup action when pressed
		MouseListenerCreator.addMouseListenerToNormalButtons(addFileChooserJButton); // setup color and border changes when button pressed

		cancelFileChooserJButton = new JButton("CANCEL");
		cancelFileChooserJButton.setPreferredSize(new Dimension(150,30));
		cancelFileChooserJButton.setBackground(Color_schema.dark_20);
		cancelFileChooserJButton.setForeground(Color_schema.orange_dark);
		cancelFileChooserJButton.setFocusable(false);
		cancelFileChooserJButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileWritingType=ID.CANCEL;
			hideDialog();
			}
		});

		MouseListenerCreator.addMouseListenerToCancelButtons(cancelFileChooserJButton);
		chooserButtonPanel.add(addFileChooserJButton);
		chooserButtonPanel.add(cancelFileChooserJButton);
		centerPanel.add(chooserButtonPanel, BorderLayout.PAGE_END);

		fBackPanel.add(centerPanel, BorderLayout.CENTER);

		return fBackPanel;
	} catch (Exception e) {
		LOGGER.severe("Error in creating openfiledialog:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
		e.printStackTrace();
		return null;
	}
}

/**
 * Initializes the file dialog.
 *
 * @throws Exception the exception
 */
private void initFileDialog() throws Exception{

		this.setBounds(ownerFrame.getBounds());
		this.setUndecorated(true); // no titlebar or buttons
		this.setBackground(new Color(0,0,0,0)); // transparent color
		this.setContentPane(new ContentPane());
		this.getContentPane().setBackground(Color_schema.dark_30);
		this.getContentPane().setLayout(new GridBagLayout());

		selectFileDialogBackPanel = new JPanel();
		selectFileDialogBackPanel.setBackground(new Color(0,0,0));
		selectFileDialogBackPanel.setLayout(new BorderLayout());

		selectFileDialogBackPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 5));
		selectFileDialogBackPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		selectFileDialogBackPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		selectFileDialogBackPanel.setMaximumSize(new Dimension((int)(backPanelComponent.getBounds().getWidth()*0.95), (int)(backPanelComponent.getBounds().getHeight()*0.95)));
		selectFileDialogBackPanel.setMinimumSize(new Dimension((int)(backPanelComponent.getBounds().getWidth()*0.7), (int)(backPanelComponent.getBounds().getHeight()*0.5)));
		selectFileDialogBackPanel.setPreferredSize(new Dimension((int)(backPanelComponent.getBounds().getWidth()*0.7), (int)(backPanelComponent.getBounds().getHeight()*0.7)));
		if(selectFileDialogBackPanel.getPreferredSize().getWidth()<500)
			selectFileDialogBackPanel.setPreferredSize(new Dimension((int)(backPanelComponent.getBounds().getWidth()*0.95), (int)(backPanelComponent.getBounds().getHeight()*0.95)));

		JPanel fileChooserPanel = initFileChooserPanel();
		selectFileDialogBackPanel.add(fileChooserPanel, BorderLayout.CENTER);
		initKeyListenerFileselection();
		this.add(selectFileDialogBackPanel);
		this.validate();
		this.repaint();
	}

	/**
	 * Initializes the file filters.
	 *
	 * @throws Exception the exception
	 */
	private void initFileFilters() throws Exception{
		this.fileFilters=createFileFilterList(this.fileType);

	}

	/**
	 * Initializes the key listener for file selection. When ENTER pressed -> the files will be selected.
	 *
	 * @throws Exception the exception
	 */
	private void initKeyListenerFileselection() throws Exception{

		InputMap inputMap= (this.selectFileDialogBackPanel).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke("pressed ENTER"), "enter_pressed");
		ActionMap actionMap = 	(this.selectFileDialogBackPanel).getActionMap();
		actionMap.put("enter_pressed", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 6212251846590558328L;

			@Override
			public void actionPerformed(ActionEvent e) {
				addFileChooserJButton.doClick();

			}

		});
	}

	/**
	 * Sets the current file or folder.
	 */
	public void setCurrentFileOrFolder(){
		try {
			if(this.properSavingFileOrFolderPath != null && this.properSavingFileOrFolderPath.length()>0){
				File f=new File(this.properSavingFileOrFolderPath);
				if(f.isDirectory())
				fileChooser.setCurrentDirectory(f);
				else{
					if(f.isFile() && f.exists() && fileChooser.accept(f)){

							fileChooser.setSelectedFile(f);
							showSelectedFileAtTextField();
					}
					else{
						f=f.getParentFile();
						if(f.exists()){
							fileChooser.setCurrentDirectory(f);
						}
						else{
							File folder=new File(System.getProperty("user.home"));
							if(folder.exists())
							fileChooser.setCurrentDirectory(folder);
						}
					}
				}

			}
			else{
				File folder=new File(System.getProperty("user.home"));
				if(folder.exists())
				fileChooser.setCurrentDirectory(folder);
			}
		} catch (Exception e) {
			LOGGER.severe("Error in setting current folder!");
			e.printStackTrace();
			if(fileChooser != null)
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		}
	}

	/**
	 * Sets the file writing type. ID.FILE_OVERWRITE, ID.FILE_APPEND...
	 *
	 * @param id the new ID for file writing type
	 * @throws Exception the exception
	 */
	private void setFileWritingType(int id){
		try {
			this.fileWritingType=id;
		} catch (Exception e) {
			LOGGER.severe("Error in setting type of file writing!");
			e.printStackTrace();
		}
	}

	/**
	 * Sets the image layer names searching .
	 *
	 * @param list the new image layer names used in xml search.
	 * @throws Exception the exception
	 */
	public void setImageLayerNamesForXMLSearch(ArrayList<String> list) throws Exception{
			// implement the code in extended class
		}

	/**
	 * Sets the proper file path for saving.
	 *
	 * @param path the new proper file path for saving
	 * @throws Exception the exception
	 */
	public void setProperFilePathForSaving(String path) throws Exception{
		this.properSavingFileOrFolderPath=path;
		setCurrentFileOrFolder();
	}

	 /**
 	 * Sets the proper folder path for saving.
 	 *
 	 * @param path the new proper folder path for saving
 	 * @throws Exception the exception
 	 */
 	public void setProperFolderPathForSaving(String path) throws Exception {	
		try {
			
			if(path != null && path.length()>0){
				File file = getFolder(path);
				setProperFilePathForSaving(file.getAbsolutePath());			
			}
				
		} catch (Exception e) {
		
			e.printStackTrace();
		}
	}

	 /**
 	 * Sets the selected file path.
 	 *
 	 * @param path the new selected file path
 	 * @throws Exception the exception
 	 */
 	private void setSelectedFilePath(String path) {
		try {
			this.selectedFilePath=path;
		} catch (Exception e) {
			LOGGER.severe("Error in setting selected file path!");
			e.printStackTrace();
		}
	}

 	
	 /**
 	 * Shows append/overwrite message.
 	 *
 	 * @return the int
 	 * @throws Exception the exception
 	 */
 	private int showAppendOverwriteMessage() throws Exception{
		// confirm overwrite
		ShadyMessageDialog dialog = new ShadyMessageDialog(this, "File exists", "Append or Overwrite?", ID.APPEND_OVERWRITE_CANCEL, this);
		return dialog.showDialog();


	}


	/**
	 * Opens and shows dialog.
	 */
	public void showDialog(){
		this.setVisible(true);
	
	}

	/**
	 * Show file message.
	 *
	 * @param title the title
	 * @param message the message
	 * @throws Exception the exception
	 */
	private void showFileMessage(String title, String message) {
		ShadyMessageDialog dialog = null;
		
		try {
			dialog = new ShadyMessageDialog(this, title, message, ID.OK, this);
			dialog.showDialog();
			dialog=null;
		} catch (Exception e) {
			LOGGER.severe("Error in showing message of saving file!");
			e.printStackTrace();
			if(dialog != null)
				dialog=null;
		}
	}



	/**
	 * Show overwrite confirm message.
	 *
	 * @param title the message title
	 * @param text the message text
	 * @return true, if accepted otherwise false
	 */
	private boolean showOverwriteMessage(String title, String text){
		// confirm overwrite
		ShadyMessageDialog dialog = null;
		try {
			dialog = new ShadyMessageDialog(this, title, text, ID.YES_NO, this);
			if(dialog.showDialog() == ID.YES){
				dialog=null;
				return true;
			}
			dialog=null;
			return false;
		} catch (Exception e) {
			LOGGER.severe("Error in showing overwrite message!");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Show selected file at text field.
	 *
	 * @throws Exception the exception
	 */
	private void showSelectedFileAtTextField() throws Exception{
		if(fileChooser.getSelectedFile() != null && fileChooser.getSelectedFile().isFile() && fileChooser.getSelectedFile().getName().length()>0){
		//	((JTextField)((Container)((Container)fileChooser.getComponent(3)).getComponent(0)).getComponent(1)).setText(fileChooser.getSelectedFile().getName());
			setTextOfJTextFieldFromFileChooser(this.fileChooser, fileChooser.getSelectedFile().getName());
		}
	}

}

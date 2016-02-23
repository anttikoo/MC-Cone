package gui.file;
import gui.Color_schema;
import gui.ContentPane;
import gui.MouseListenerCreator;
import information.ID;
import information.PathCount;
import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
	 * Dialog for selecting image files for adding to ImageLayer list or selecting marking (xml-file) for one selected image
	 * @author Antti
	 *
	 */
	public class OpenFileDialog extends JDialog{
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The Constant LOGGER. */
		private final static Logger LOGGER = Logger.getLogger("MCCLogger");
		
		/** The open dialog back panel. */
		private JPanel openDialogBackPanel;
		
		/** The file chooser. */
		protected JFileChooser fileChooser;
		
		/** The JButton for cancelling file choosing. */
		private JButton cancelFileChooserJButton;
		
		/** The add file chooser j button. */
		private JButton addFileChooserJButton;
		
		/** The path of the image of ImageLayer. */
		protected String imageLayerPath;
		
		/** The parent component bounds. */
		private Rectangle parentComponentBounds;
		
		/** The parent component back panel bounds. */
		private Rectangle parentComponentBackPanelBounds;
		
		/** The present folder. */
		protected String presentFolder;
		
		/** The selected files. */
		protected File[] selectedFiles=null;

		/**
		 * Instantiates a new open file dialog.
		 *
		 * @param d the owner JDialog
		 * @param parentComponentBounds the Bounds of parent component
		 * @param backPanelBounds the Bounds of visible panel
		 * @param presentFolder String the present folder
		 */
		public OpenFileDialog(JDialog d, Rectangle parentComponentBounds, Rectangle backPanelBounds, String presentFolder){
			super(d, true); 
			try {
				this.setResizable(false);
				this.parentComponentBounds=parentComponentBounds;
				this.parentComponentBackPanelBounds=backPanelBounds;
				this.presentFolder=getFolderString(presentFolder);
				initFileDialog();
			} catch (Exception e) {
				LOGGER.severe("Error in initializing OpenFileDialog!");
				e.printStackTrace();
			}
		}
		
		
		
		/**
		 * Class constructor for Dialog. The Dialog is JFilechooser which gives to select image files.
		 *
		 * @param frame owner JFrame
		 * @param parentComponentBounds the parent component bounds
		 * @param backPanelBounds the back panel bounds
		 * @param presentFolder the present folder
		 */
		public OpenFileDialog(JFrame frame, Rectangle parentComponentBounds, Rectangle backPanelBounds, String presentFolder){
			super(frame, true);
			try {
				this.setResizable(false);
				this.parentComponentBounds=parentComponentBounds;
				this.parentComponentBackPanelBounds=backPanelBounds;
				this.presentFolder=getFolderString(presentFolder);
				initFileDialog();
			} catch (Exception e) {
				LOGGER.severe("Error in initializing OpenFileDialog!");
				e.printStackTrace();
			}
		}


		/**
		 * Adds the actions to file dialog buttons.
		 *
		 * @param button the button
		 * @throws Exception the exception
		 */
		protected void addActionsToFileDialogButtons(JButton button) throws Exception{
			// create code to extended class
		}

		/**
		 * Adds the key listener to JButton.
		 *
		 * @param button the button
		 * @return the j button
		 * @throws Exception the exception
		 */
		private JButton addKeyListenerToButton(final JButton button) throws Exception{

			InputMap inputMap= (button).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			inputMap.put(KeyStroke.getKeyStroke("pressed ENTER"), "enter_pressed");
			ActionMap actionMap = 	(button).getActionMap();
			actionMap.put("enter_pressed", new AbstractAction() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					button.doClick();

				}

			});

			return button;
		}

		/**
		 * Converts given File to directory if it is not already one.
		 *
		 * @param filePath the file path
		 * @return the folder of given file
		 */
		protected File getFolder(String filePath){
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
				return null;
			} catch (Exception e) {
				LOGGER.severe("Error in getting folder of file " +e.getClass().toString() + " :" +e.getMessage()+ " line: "+ e.getStackTrace()[2].getLineNumber());
				return null;
			}
		}

		/**
		 * Gets the String of folder name from file path.
		 *
		 * @param filePath the file path
		 * @return the folder string
		 * @throws Exception the exception
		 */
		private String getFolderString(String filePath) throws Exception{
			return getFolder(filePath).getAbsolutePath();
		}

		/**
		 * Gets the most common path from list of previous files.
		 *
		 * @param fileGroup list of Files
		 * @return String the most common path
		 */
		protected String getMostCommonPath(File[] fileGroup){
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

		/**
		 * Gets the present folder.
		 *
		 * @return the present folder
		 * @throws Exception the exception
		 */
		public String getPresentFolder() throws Exception{
			return this.presentFolder;
		}


		/**
		 * Gets the selected files.
		 *
		 * @return the selected files
		 * @throws Exception the exception
		 */
		public File[] getSelectedFiles() throws Exception{
			return this.selectedFiles;
		}

		/**
		 * Gets the title of dialog.
		 *
		 * @return the window title
		 * @throws Exception the exception
		 */
		protected String getWindowTitle() throws Exception{
			return "Title";
		}

		/**
		 * Calls the hiding method to close dialog.
		 */
		protected void hideDialog() throws Exception{
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
		public void hideThis(){
			try{
			this.setVisible(false); // close dialog
			this.dispose();
			}
			catch(Exception e){
				this.dispose();
				LOGGER.severe("Error in hiding FileDIalog.");
				e.printStackTrace();
			}
		}

		/**
		 * Inits the file chooser JPanel.
		 *
		 * @return the Jpanel
		 */
		private JPanel initFileChooserPanel(){
			try {

				JPanel fBackPanel = new JPanel();
				fBackPanel.setLayout(new BorderLayout());
				fBackPanel.setBackground(Color_schema.dark_40);
				fBackPanel.setLayout(new BorderLayout());
				fBackPanel.setMaximumSize(openDialogBackPanel.getMaximumSize());
				fBackPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));

				JPanel upPanel = new JPanel();
				upPanel.setBackground(Color_schema.dark_30);
				upPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
				upPanel.setMaximumSize(new Dimension(100,40));
				upPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				upPanel.setBorder(BorderFactory.createEmptyBorder());
				JLabel titleLabel = new JLabel(getWindowTitle());
	
				titleLabel.setFont(new Font("Consolas", Font.BOLD,20));
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
				imageChooserPanel.setMinimumSize(new Dimension(((int)(openDialogBackPanel.getMinimumSize().getWidth()*0.9)), ((int)(openDialogBackPanel.getMinimumSize().getHeight()*0.7))));

				imageChooserPanel.setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 3));
				imageChooserPanel.setBackground(Color_schema.dark_40);

				fileChooser = new JFileChooser();

				fileChooser.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 2));
				fileChooser.setBackground(Color_schema.dark_40);
				fileChooser.setControlButtonsAreShown(false);
				fileChooser.setMultiSelectionEnabled(true); // several files are allowed to select
				setUpFilechooserSettings();

				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setMaximumSize(new Dimension(1000,800)); // not so nice when going too large

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
				addFileChooserJButton.setFocusable(false);
				addActionsToFileDialogButtons(addFileChooserJButton);	// setup action when pressed
				MouseListenerCreator.addMouseListenerToNormalButtons(addFileChooserJButton); // setup color and border changes when button pressed
				addKeyListenerToButton(addFileChooserJButton); // when Enter pressed -> this button activated
				MouseListenerCreator.addKeyListenerToButton(addFileChooserJButton, ID.BUTTON_ENTER);
				
				cancelFileChooserJButton = new JButton("CANCEL");
				cancelFileChooserJButton.setPreferredSize(new Dimension(150,30));
				cancelFileChooserJButton.setBackground(Color_schema.dark_20);
				cancelFileChooserJButton.setForeground(Color_schema.orange_dark);
				cancelFileChooserJButton.setFocusable(false);
				MouseListenerCreator.addKeyListenerToButton(cancelFileChooserJButton, ID.BUTTON_CANCEL);
				cancelFileChooserJButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
					try {
						hideDialog();
					} catch (Exception e1) {
						LOGGER.severe("Error in hiding the dialog!");
						e1.printStackTrace();
					}
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
				return null;
			}
		}

		/**
		 * Initializes the dialog opening a file.
		 */
		private void initFileDialog() throws Exception{

			this.setBounds(this.parentComponentBounds); //sets the size of window same as the parent window size
			this.setUndecorated(true); // no titlebar or buttons
			this.setBackground(new Color(0,0,0,0)); // transparent color
			this.setContentPane(new ContentPane());
			this.getContentPane().setBackground(Color_schema.dark_30);
			this.getContentPane().setLayout(new GridBagLayout());

			openDialogBackPanel = new JPanel();
			openDialogBackPanel.setBackground(new Color(0,0,0));
			openDialogBackPanel.setLayout(new BorderLayout());
			openDialogBackPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 5));
			openDialogBackPanel.setMaximumSize(new Dimension((int)(this.parentComponentBackPanelBounds.getWidth()*0.95), (int)(this.parentComponentBackPanelBounds.getHeight()*0.95)));
			openDialogBackPanel.setMinimumSize(new Dimension((int)(this.parentComponentBackPanelBounds.getWidth()*0.7), (int)(this.parentComponentBackPanelBounds.getHeight()*0.5)));
			openDialogBackPanel.setPreferredSize(new Dimension((int)(this.parentComponentBackPanelBounds.getWidth()*0.7), (int)(this.parentComponentBackPanelBounds.getHeight()*0.7)));
			if(openDialogBackPanel.getPreferredSize().getWidth()<500)
				openDialogBackPanel.setPreferredSize(new Dimension((int)(this.parentComponentBackPanelBounds.getWidth()*0.95), (int)(this.parentComponentBackPanelBounds.getHeight()*0.95)));
	
			JPanel fileChooserPanel = initFileChooserPanel();
			openDialogBackPanel.add(fileChooserPanel, BorderLayout.CENTER);
			this.add(openDialogBackPanel);
			this.validate();
			this.repaint();
		}

		/**
		 * Sets the present folder.
		 *
		 * @param path the new present folder
		 * @throws Exception the exception
		 */
		protected void setPresentFolder(String path) throws Exception{
			this.presentFolder=path;
		}

		/**
		 * Sets the up filechooser settings.
		 *
		 * @throws Exception the exception
		 */
		protected void setUpFilechooserSettings() throws Exception{
			//
		}
		
		public void showDialog(){
			this.setVisible(true);
			this.repaint();
		}

		/**
		 * Adds or updates PathCount objects of array which is used to calculate occurrences of folder paths
		 * @param countList the array of PathCount objects where the occurrences of paths are saved
		 * @param folderPath the path of folder which is added to array
		 * @return The PathCount object which has biggest count value by this far
		 */
		private PathCount updatePathCount(ArrayList<PathCount> countList, String folderPath,PathCount biggestCount){


			try {
				if(folderPath != null && folderPath.length()>2){
					if(countList.size()>0){
						boolean foundPath = false;
						Iterator<PathCount> iterator = countList.iterator();
						while(iterator.hasNext()){
							PathCount pc= iterator.next();
							if(pc.getFolderPath().equals(folderPath)){
								pc.addOne(); // increase the count value
								foundPath=true;
								if(biggestCount == null || biggestCount.getCount()<pc.getCount())
									biggestCount=pc;
							}

						}
						if(!foundPath) {// path not found -> add new PathCount object
							PathCount pcount= new PathCount(folderPath);
							countList.add(pcount);
							if(biggestCount == null || biggestCount.getCount()<pcount.getCount())
								biggestCount=pcount;
							}

					}
					else{ // add first object to list
						PathCount pcount= new PathCount(folderPath);
						countList.add(pcount);
						biggestCount=pcount;

					}
				}
				return biggestCount;
			} catch (Exception e) {
				LOGGER.severe("Error in updatePathCount " +e.getClass().toString() + " :" +e.getMessage());
				return null;
			}
		}

	}

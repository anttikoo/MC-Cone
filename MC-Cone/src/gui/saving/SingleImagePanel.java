package gui.saving;

import gui.Color_schema;
import gui.MouseListenerCreator;
import information.Fonts;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import operators.CheckBoxIcon;

/**
 * The Class SingleImagePanel. Contains name of ImageLayer, buttons for browsing and SingleMarkingPanels for
 * presenting MarkingLayers.
 */
public class SingleImagePanel extends JPanel{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1880336481160684640L;

	/** The Constant LOGGER. */
	final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The image path. */
	private String imagePath;
	
	/** The list of MarkingLayers. */
	private ArrayList<MarkingLayer> markingLayerList;
	
	/** The save single JButton. */
	private JButton saveSingleJButton;
	
	/** The JButton for browsing. */
	private JButton browseJButton;
	
	/** The one image title height. */
	private int oneImageTitleHeight;
	
	/** The one image path height. */
	private int oneImagePathHeight;
	
	/** The one marking height. */
	private int oneMarkingHeight;
	
	/** The proper file path. */
	private String properFilePath;
	
	/** The marking table JPanel. */
	protected JPanel markingTableJPanel;
	
	/** The file path label value. */
	private JLabel filePathLabelValue;
	
	/** The file validity. ID.FILE_CANT_READ, ID.FILE_NOT_VALID... */
	protected int fileValidity;
	
	/** The ImageLayer. */
	protected ImageLayer imageLayer;
	
	/** The saver dialog for saving file. */
	protected SaverDialog saverDialog;
	
	/** The check box to check saving image. */
	private JCheckBox saveImageCheckBox;


	/**
	 * Instantiates a new single image panel.
	 *
	 * @param imageLayer the image layer
	 * @param saverDialog the saver dialog
	 */
	public SingleImagePanel(ImageLayer imageLayer, SaverDialog saverDialog){
		try {
			this.saverDialog=saverDialog;
			initIAMPcomponents(imageLayer);


		} catch (Exception e) {
			LOGGER.severe("Error in construction of Marking list " +e.getClass().toString() + " :" +e.getMessage());
			e.printStackTrace();

		}
	}
	
	/**
	 * Check file validity.
	 *
	 * @throws Exception the exception
	 */
	protected void checkFileValidity() throws Exception{
		fileValidity =  saverDialog.checkFileValidity(new File(this.properFilePath));
		
		// check is file already selected to other 
	}

	/**
	 * Creates and returns a single marking panel for given MarkingLayer
	 *
	 * @param markingLayer the MarkingLayer
	 * @return the single marking panel
	 * @throws Exception the exception
	 */
	protected SingleMarkingPanel createSingleMarkingPanel(MarkingLayer markingLayer) throws Exception{
		return new SingleMarkingPanel(markingLayer);
	}

	/**
	 * Returns the all IDs of selected MarkingLayers.
	 *
	 * @return the all IDs of selected MarkingLayers.
	 * @throws Exception the exception
	 */
	public ArrayList<Integer> getAllSelectedMarkingLayerIDs() throws Exception{
		ArrayList<Integer> selectedMarkingLayerIDs=new ArrayList<Integer>();
		Component[] sMarkingList= markingTableJPanel.getComponents();
		if(sMarkingList != null && sMarkingList.length>0){
			for(int i=0;i<sMarkingList.length;i++){
				SingleMarkingPanel smp= (SingleMarkingPanel)sMarkingList[i];
				if(smp.isSelected()){
					selectedMarkingLayerIDs.add(smp.getMarkingLayer().getLayerID());
				}
			}
		}
		return selectedMarkingLayerIDs;
	}

	/**
	 * Returns the all selected MarkingLayers.
	 *
	 * @return the all selected MarkingLayers.
	 * @throws Exception the exception
	 */
	public ArrayList<MarkingLayer> getAllSelectedMarkingLayers() throws Exception{
		ArrayList<MarkingLayer> selectedMarkingLayers=new ArrayList<MarkingLayer>();
		Component[] sMarkingList= markingTableJPanel.getComponents();
		if(sMarkingList != null && sMarkingList.length>0){
			for(int i=0;i<sMarkingList.length;i++){
				SingleMarkingPanel smp= (SingleMarkingPanel)sMarkingList[i];
				if(smp.isSelected()){
					selectedMarkingLayers.add(smp.getMarkingLayer());
				}
			}
		}
		return selectedMarkingLayers;
	}
	
	/**
	 * Returns the file validity.
	 *
	 * @return the file validity
	 * @throws Exception the exception
	 */
	public int getFileValidity() throws Exception{
		return fileValidity;
	}

	/**
	 * Returns the ImageLayer.
	 *
	 * @return the image layer
	 * @throws Exception the exception
	 */
	public ImageLayer getImageLayer() throws Exception{
		return this.imageLayer;
	}



	/**
	 * Returns the name of ImageLayer.
	 *
	 * @return the image layer name
	 * @throws Exception the exception
	 */
	public String getImageLayerName() throws Exception{
		return imageLayer.getImageFileName();
	}



	/**
	 * Returns the image path.
	 *
	 * @return the image path
	 * @throws Exception the exception
	 */
	public String getImagePath() throws Exception{
		return imagePath;
	}

	/**
	 * Returns the layer id.
	 *
	 * @return the layer id
	 * @throws Exception the exception
	 */
	public int getLayerID() throws Exception{
		return this.imageLayer.getLayerID();
	}


	/**
	 * Returns the proper file path for saving.
	 *
	 * @return the proper file path for saving
	 * @throws Exception the exception
	 */
	public String getProperFilePathForSaving() throws Exception{
		return this.properFilePath;
	}



	/**
	 * Checks for selected marking layer.
	 *
	 * @return true, if at least one MarkingLayer is selected.
	 * @throws Exception the exception
	 */
	public boolean hasSelectedMarkingLayer() throws Exception{
		if(getAllSelectedMarkingLayers() != null && getAllSelectedMarkingLayers().size()>0)
			return true;
		return false;
	}

	/**
	 * Inform file validity. Opens message box for informing file validity.
	 *
	 * @param showMessage the show message
	 */
	private void informFileValidity(boolean showMessage){
		try {
			this.saverDialog.informUserFromFileValidity(this.fileValidity, this.filePathLabelValue, showMessage);
		} catch (Exception e) {
			LOGGER.severe("Error in informing file validity!");
			e.printStackTrace();
		}
	}

	

	/**
	 * Initializes the JPanel showing the file path.
	 *
	 * @param buttonTitle the button title
	 * @return the JPanel
	 * @throws Exception the exception
	 */
	protected JPanel initFilePathPanel(String buttonTitle) throws Exception{
		// setup Panel for showing and selecting path of file
		JPanel filePathJPanel = new JPanel();
		filePathJPanel.setLayout(new BoxLayout(filePathJPanel, BoxLayout.LINE_AXIS));
		filePathJPanel.setMaximumSize(new Dimension(2000,oneImagePathHeight));
		filePathJPanel.setPreferredSize(new Dimension(400,oneImagePathHeight));
		filePathJPanel.setBackground(Color_schema.dark_35);
		filePathJPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color_schema.dark_50));

		// create JLabels for path
		JLabel filePathLabel = new JLabel("Save to file:");
		filePathLabel.setFont(Fonts.p16);
		filePathLabel.setForeground(Color_schema.white_230);
		filePathLabelValue = new JLabel();

		filePathLabelValue.setFont(Fonts.p16);
		filePathLabelValue.setForeground(Color_schema.white_230);
		int labelwidthXML=filePathLabelValue.getFontMetrics(Fonts.b16).stringWidth(filePathLabelValue.getText());

		filePathJPanel.setPreferredSize(new Dimension(labelwidthXML+40,oneImagePathHeight));

		filePathJPanel.add(Box.createRigidArea(new Dimension(10,0)));
		filePathJPanel.add(filePathLabel);
		filePathJPanel.add(Box.createRigidArea(new Dimension(10,0)));
		filePathJPanel.add(filePathLabelValue);
		filePathJPanel.add(Box.createHorizontalGlue());

		browseJButton = new JButton(buttonTitle);
		browseJButton.setFont(Fonts.b15);
		int maxStringWidth = browseJButton.getFontMetrics(Fonts.b15).stringWidth(buttonTitle);
		browseJButton.setPreferredSize(new Dimension(maxStringWidth+20,25));
		browseJButton.setMaximumSize(new Dimension(maxStringWidth+20,25));
		browseJButton.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
		browseJButton.setContentAreaFilled(false);
		browseJButton.setFocusable(false);
		browseJButton.setToolTipText("Select file");
		browseJButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)  {
				try {
					saverDialog.selectPathForSingleImagePanel(properFilePath, imageLayer.getLayerID());
				} catch (Exception e1) {
				
					e1.printStackTrace();
				}
			}
		});
		MouseListenerCreator.addMouseListenerToNormalButtons(browseJButton);

		filePathJPanel.add(browseJButton);
		return filePathJPanel;
	}
	
	/**
	 * Initializes the file path panel with label.
	 *
	 * @return the JPanel
	 * @throws Exception the exception
	 */
	protected JPanel initFilePathPanelWithLabel() throws Exception{
		return initFilePathPanel("Browse");
	}

	/**
	 * Initializes the components of SingleImagePanel. Showing information of ImageLayers and MarkingLayers.
	 *
	 * @param imageLayer the image layer
	 * @throws Exception the exception
	 */
	private void  initIAMPcomponents(ImageLayer imageLayer) throws Exception{
		setHeights(); // set the heights for panels
		this.imageLayer=imageLayer;
		this.imagePath = imageLayer.getImageFilePath();
		this.markingLayerList=imageLayer.getMarkingLayers();
		this.properFilePath=initProperFilePathForSaving(imageLayer);
		this.fileValidity=ID.FILE_OK;

		int markingListHeight =0;
		if(this.markingLayerList !=null && this.markingLayerList.size()>0)
			markingListHeight = oneMarkingHeight*this.markingLayerList.size();
		this.setMaximumSize(new Dimension(3000,oneImageTitleHeight+oneImagePathHeight+markingListHeight));
		this.setPreferredSize(new Dimension(400,oneImageTitleHeight+oneImagePathHeight+markingListHeight));
		this.setBackground(Color_schema.dark_35);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createLineBorder(Color_schema.dark_100, 1));

		// panel for image information
		JPanel imageInformationJPanel= new JPanel();
		imageInformationJPanel.setMaximumSize(new Dimension(2000,oneImagePathHeight+oneImageTitleHeight));
		imageInformationJPanel.setPreferredSize(new Dimension(400,oneImagePathHeight+oneImageTitleHeight));
		imageInformationJPanel.setBackground(Color_schema.dark_35);
		imageInformationJPanel.setLayout(new BorderLayout());
		imageInformationJPanel.setBorder(BorderFactory.createLineBorder(Color_schema.dark_50));
		
		// CheckBox
		Icon checkBoxIcon=new CheckBoxIcon();
		// checkBox for selecting
		saveImageCheckBox=new JCheckBox(checkBoxIcon);
	
		saveImageCheckBox.setSelected(true);
		saveImageCheckBox.setBackground(Color_schema.dark_35);
		saveImageCheckBox.setMaximumSize(new Dimension(25,25));
		saveImageCheckBox.setPreferredSize(new Dimension(25,25));
		saveImageCheckBox.setMinimumSize(new Dimension(25,25));
		setListenerToImageCheckBox(); // listener to change of selections
		// Set image title panel
		JPanel titlePanel = new JPanel();
		titlePanel.setMaximumSize(new Dimension(2000,oneImageTitleHeight));
		titlePanel.setPreferredSize(new Dimension(400,oneImageTitleHeight));
		titlePanel.setBackground(Color_schema.dark_35);
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));
		// title part (path)
		JLabel imageTitle = new JLabel(this.imagePath);
		imageTitle.setFont(Fonts.b18);
		imageTitle.setForeground(Color_schema.white_230);
		int labelwidth=imageTitle.getFontMetrics(Fonts.b18).stringWidth(imageTitle.getText());

		titlePanel.setPreferredSize(new Dimension(labelwidth+100,oneImageTitleHeight));
		titlePanel.add(Box.createRigidArea(new Dimension(10,0)));
		titlePanel.add(this.saveImageCheckBox);
		titlePanel.add(imageTitle);

		imageInformationJPanel.add(titlePanel,BorderLayout.PAGE_START);

		JPanel filePathPanel = initFilePathPanelWithLabel();

		if(filePathPanel != null)
		imageInformationJPanel.add(initFilePathPanelWithLabel(), BorderLayout.CENTER);

		// add panels containing marking titles
		markingTableJPanel = new JPanel();
		markingTableJPanel.setLayout(new BoxLayout(markingTableJPanel, BoxLayout.PAGE_AXIS));
		markingTableJPanel.setMaximumSize(new Dimension(2000,oneMarkingHeight*markingLayerList.size()));
		markingTableJPanel.setPreferredSize(new Dimension(400,oneMarkingHeight*markingLayerList.size()));
		markingTableJPanel.setMinimumSize(new Dimension(400,oneMarkingHeight*markingLayerList.size()));
		markingTableJPanel.setBackground(Color_schema.dark_35);

		// Create and add markingpanels
		if(markingLayerList != null && markingLayerList.size()>0){
			Iterator<MarkingLayer> iIterator = markingLayerList.iterator();
			while(iIterator.hasNext()){
				MarkingLayer ml = (MarkingLayer)iIterator.next();
				if(ml.getLayerName().length()>0){ // MarkingLayer name
					// add markingTitles to markingTable
					markingTableJPanel.add(createSingleMarkingPanel(ml));
				}
			}
		}
		
		this.setPreferredSize(new Dimension(labelwidth+100,oneImageTitleHeight+oneImagePathHeight+markingListHeight));
		this.add(imageInformationJPanel,BorderLayout.PAGE_START);
		this.add(markingTableJPanel,BorderLayout.CENTER);
		this.validate();

		setProperFileForMarkings(this.properFilePath, false, ID.UNDEFINED);
	}
	
	/**
	 * Initializes the proper file path for saving.
	 *
	 * @param iLayer the i layer
	 * @return the string
	 * @throws Exception the exception
	 */
	protected String initProperFilePathForSaving(ImageLayer iLayer) throws Exception{
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
	 * Checks if is selected.
	 *
	 * @return true, if is selected
	 * @throws Exception the exception
	 */
	public boolean isSelected() throws Exception{
		return this.saveImageCheckBox.isSelected();
	}

	/**
	 * Sets the all marking layer selections.
	 *
	 * @param selected the new all marking layer selections
	 * @throws Exception the exception
	 */
	public void setAllMarkingLayerSelections(boolean selected) throws Exception{
		Component[] ssMarkingList= markingTableJPanel.getComponents();
		if(ssMarkingList != null && ssMarkingList.length>0){
			for(int i=0;i<ssMarkingList.length;i++){
				((SingleMarkingPanel)ssMarkingList[i]).setSelected(selected);
				((SingleMarkingPanel)ssMarkingList[i]).setCheckBoxEnableState(selected);
			}
		}
	}

	/**
	 * Sets the background colors of all SingleMarkingPanels to default.
	 *
	 * @throws Exception the exception
	 */
	public void setAllSingleMarkingPanelBGstoDefault() throws Exception{
		Component[] ssMarkingList= markingTableJPanel.getComponents();
		if(ssMarkingList != null && ssMarkingList.length>0){
			for(int i=0;i<ssMarkingList.length;i++){
				((SingleMarkingPanel)ssMarkingList[i]).setBGColorBySuccessfullSaving(false);
			}
		}
	}


	/**
	 * Sets the buttons enabled.
	 *
	 * @param enable the new buttons enabled
	 * @throws Exception the exception
	 */
	public void setButtonsEnabled(boolean enable) throws Exception{
		saveSingleJButton.setEnabled(enable);
		browseJButton.setEnabled(enable);
	}
	
	/**
	 * Sets the heights of JPanels of SingleImagePanel and SingleMarkingPanel.
	 *
	 * @throws Exception the exception
	 */
	private void setHeights() throws Exception{
		this.oneImageTitleHeight=this.saverDialog.oneImageTitleHeight;
		this.oneImagePathHeight=this.saverDialog.oneImagePathHeight;
		this.oneMarkingHeight=this.saverDialog.oneMarkingHeight;
	}

	/**
	 * Sets the listener to image check box.
	 *
	 * @throws Exception the exception
	 */
	private void setListenerToImageCheckBox() throws Exception{
		this.saveImageCheckBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				try {
					setAllMarkingLayerSelections(((JCheckBox)e.getSource()).isSelected());
				} catch (Exception e1) {
					LOGGER.severe("Error in checking checkbox!");
					e1.printStackTrace();
				}
				
			}
		});
		
	}



	/**
	 * Sets the path.
	 *
	 * @param path the new path
	 * @throws Exception the exception
	 */
	public void setPath(String path) throws Exception {
		this.imagePath = path;
	}

	/**
	 * Sets the proper file for markings.
	 *
	 * @param properFile the proper file
	 * @param showMessage the show message
	 * @param fileValidityID the file validity id
	 * @throws Exception the exception
	 */
	public void setProperFileForMarkings(String properFile, boolean showMessage, int fileValidityID) throws Exception {	
		this.properFilePath = properFile;
		this.filePathLabelValue.setText(this.properFilePath);
		this.fileValidity=fileValidityID;
		
		
		if(fileValidityID == ID.UNDEFINED)
			checkFileValidity();

		informFileValidity(showMessage);
		// now the target file changes -> change the markingLayer backgrounds to default
		// the color represents the successfully saved markings into properFileForMarkings -> has to refresh color
		setAllSingleMarkingPanelBGstoDefault();
		this.repaint();
	}

	/**
	 * Sets the CheckBox to selected.
	 *
	 * @param selected the new selected
	 * @throws Exception the exception
	 */
	public void setSelected(boolean selected) throws Exception{
		this.saveImageCheckBox.setSelected(selected);
		this.repaint();
	}

	/**
	 * Sets the checkbox selections of all marking layers.
	 *
	 * @param state the new selection of all marking layers
	 * @throws Exception the exception
	 */
	public void setSelectionOfAllMarkingLayers(boolean state) throws Exception{
		
		Component[] sMarkingList= markingTableJPanel.getComponents();
		if(sMarkingList != null && sMarkingList.length>0){
			for(int i=0;i<sMarkingList.length;i++){
				SingleMarkingPanel smp= (SingleMarkingPanel)sMarkingList[i];
				smp.setSelected(state);
			}

		}
	}

	/**
	 * Sets the background color of singleMarkingPanel to default.
	 *
	 * @param mLayerID the new single marking panel b gsto default
	 * @throws Exception the exception
	 */
	public void setSingleMarkingPanelBGstoDefault(int mLayerID) throws Exception{
		Component[] ssMarkingList= markingTableJPanel.getComponents();
		if(ssMarkingList != null && ssMarkingList.length>0){
			for(int i=0;i<ssMarkingList.length;i++){
				SingleMarkingPanel smp= (SingleMarkingPanel)ssMarkingList[i];
				if(smp.getMarkingLayerID() == mLayerID){
					smp.setBGColorBySuccessfullSaving(false);
					return;
				}
			}
		}
	}

	/**
	 * Sets the background color of successfully saved SingleMarkingPanels.
	 *
	 * @param successfullySavedList the List of IDs of SingleMarkingPanels
	 * @throws Exception the exception
	 */
	public void setSuccessfullSavingColor(ArrayList<Integer> successfullySavedList) throws Exception{

		Component[] ssMarkingList= markingTableJPanel.getComponents();
		if(ssMarkingList != null && ssMarkingList.length>0){
			for(int i=0;i<ssMarkingList.length;i++){
				int smpID= ((SingleMarkingPanel)ssMarkingList[i]).getMarkingLayerID();
				if(successfullySavedList.contains(new Integer(smpID))){
					((SingleMarkingPanel)ssMarkingList[i]).setBGColorBySuccessfullSaving(true);
				}
			
			}
		}
	}
	
	
	
	
}
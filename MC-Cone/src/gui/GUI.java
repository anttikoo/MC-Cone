package gui;
import gui.grid.GridPropertiesPanel;
import gui.panels.GridPanel;
import gui.panels.HighlightPanel;
import gui.panels.ImageLayerInfo;
import gui.panels.ImagePanel;
import gui.panels.MarkingPanel;
import gui.saving.ExportResults;
import gui.saving.SaveMarkings;
import gui.saving.ImageSet.ImageSetCreator;
import gui.saving.image.ExportImage;
import information.Fonts;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;
import information.PositionedImage;
import information.ScreenCoordinatesOfMarkingLayer;
import information.SharedVariables;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.iharder.dnd.FileDrop;
import operators.CheckBoxIcon;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import managers.TaskManager;
import managers.ProgramLogger;



/**
 * Graphical user interface of program.
 * "author Antti Kurronen
 */

public class GUI extends JFrame{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2411930638629679857L;
	
	/** The Constant LOGGER for Logging purposes. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The log frame. */
	public static LogFrame logFrame;
	
	/** The task manager. Mediates the job to worker classes*/
	public TaskManager taskManager;
	
	/** The menubar. */
	private JMenuBar menubar;
	
	/** The double panel. Contains all panels below menubar. */
	private JSplitPane doublePanel;
	
	/** The left panel.  Contains visualpanel and downBarPanel*/
	protected JPanel leftPanel;
	
	/** The visual panel. Contains the panel showing image, markings, grid.*/
	protected JPanel visualPanel;
	
	/** The down bar panel. Contains zoom slider and button for precounting*/
	protected JPanel downBarPanel;
	
	/** The layers. JLayeredPane containing all panels of visualPanel.*/
	private JLayeredPane layers;
	
	/** The image panel. Shows image*/
	private ImagePanel imagePanel;
	
	/** The right panel. Contains the info of the ImageLayers*/
	private JPanel rightPanel;
	
	
	/** The JPAnel for lists of ImageLayerInfos */
	private JPanel layerInfoListJPanel;
	
	/** The JPAnel for button adding Images at rightPanel */
	private JPanel addImageLayerJPanel;
	
	/** The JButton for precounting */
	private JButton preCountButton;
	
	/** The menu_show_all_markings. */
	private JMenuItem menu_show_all_markings;
	
	/** The menu_edit_set_marking_properties. */
	private JMenuItem menu_edit_set_marking_properties;
	
	/** The zoom slider. */
	private JSlider zoomSlider;
	
	/** The zoom value label. */
	private JLabel zoomValueLabel;
	
	/** The listener for GUI. */
	private GUIListener guiListener;
	
	/** The glass pane for precounting. Shows square and round for picking cell */
	private PrecountGlassPane glassPane;
	
	/** The slider panel. */
	private JPanel sliderPanel;
	
	/** The slider listener. */
	private SliderListener sliderListener;
	
	/** The highlight panel. Highligtning drawed on this JPanel.*/
	private HighlightPanel highlightPanel;
	
	/** The grid panel. */
	private GridPanel gridPanel;
	
	/** The right panel width. */
	private int rightPanelWidth=0;
	
	/** The listener for Components of GUI. This is used for getting modality work in linux. 
	 * Manages the printing of child panels, because in linux the modality is not working fully.*/
	private GUIcomponentListener guiComponentListener=null;
	
	/** The shady message dialog. */
	private ShadyMessageDialog shadyMessageDialog=null;
	
	/** The grid dialog. */
	private GridPropertiesPanel gridDialog=null;
	
	/** The menu grid hide. */
	private JMenuItem menu_grid_hide;
	
	/** The menu grid show. */
	private JMenuItem menu_grid_show;
	
	/** The menu grid. */
	private JMenu menu_grid;
	
	/** The menu item save markings. */
	private JMenuItem menu_item_save_markings;
	
	/** The menu export results. */
	private JMenu menu_export_results;
	
	/** The menu edit clear singlecountings. */
	private JMenuItem menu_edit_clear_single_countings;
	
	/** The menu edit clear all countings. */
	private JMenuItem menu_edit_clear_all_countings;
	
	/** The menu hide all markings. */
	private JMenuItem menu_hide_all_markings;
	
	/** The menu show. */
	private JMenu menu_show;
	
	/** The menu edit. */
	private JMenu menu_edit;
	
	/** The menu export images. */
	private JMenuItem menu_export_images;
	
	/** The zoom in button. */
	private JButton zoomInButton;
	
	/** The zoom out button. */
	private JButton zoomOutButton;


	/**
	 * Class constructor.
	 * Initializes Logging, fonts, sizes, listeners and components of the GUI.
	 */

	public GUI()
{		super("gui");
		//initialize LOGGING 
		initLogging(Level.INFO);
		
		// initialize fonts
		Fonts.initFonts();
		setUpOSsharedVariables();

		try {
			// create a object of TaskManager, which mediates the commands to classes containing the functionality
			this.taskManager=new TaskManager(this);
			
			//initialize size, colors and listeners of GUI		
			initWindowPropertiesAndListeners();

			//initialize menubar
			initMenubar();

			//init SplitPane containing two divided panels.
			initSplitPane();

			this.setVisible(true); // has to be done before initializing sizes!

			// setup window sizes
			initializeSizes();

			//init GlassPane used in precounting part
			initGlassPane();

			// insert listeners
			this.guiListener.setComponents(this, this.taskManager, this.getContentPane(), glassPane, this.imagePanel, this.preCountButton, this.layers, this.downBarPanel, this.zoomSlider, this.sliderPanel);
			
			/*
			 * FOR TESTING PURPOSES -> opens an image automatically -> path of image hard coded.
			 *	testing();
			 *  
			 */
			
			setMenuItemsEnabled(ID.IMAGELAYERS); // sets items unenabled, cos ImageLayers and MarkingLayers not found at startup
			
			LOGGER.info("Started MCcone! Wellcome.");	
			
			//refresh window
			this.repaint();

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe("Error in producing GUI: " + e.getMessage());
		}

	}
	
	/**
	 * Add actions to JMenuItems.
	 * @param item @see JMenuitem itme where action is added.
	 * @param itemType int type item (ID.MENU_ITEM_FILE_ADD_IMAGES, ID.MENU_ITEM_FILE_MANAGE_LAYERS, etc.)
	 */
	private void addActionsToMenuItems(JMenuItem item, int itemType){
		switch (itemType){
			case ID.MENU_ITEM_FILE_ADD_IMAGES:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						

						try {
							openAddImageLayerDialog(null); // open dialog with no given imagefiles
						} catch (Exception e) {
							
						 LOGGER.severe("Error in setting image: " +e.getMessage());
						 e.printStackTrace();
						}
					}
				});
				break;
			case ID.MENU_ITEM_FILE_MANAGE_LAYERS:
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
					try {
						manageImageLayersAndMarkings();
					} catch (Exception e1) {
						LOGGER.severe("Error in Managing Layers");
						e1.printStackTrace();
					}
					}
				});
				break;

			case ID.MENU_ITEM_FILE_SAVE_MARKINGS:
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
					try {
						saveMarkings();
					} catch (Exception e1) {
						LOGGER.severe("Error in saving markings!");
						e1.printStackTrace();
					}
					}
				});
				break;
			case ID.MENU_ITEM_FILE_QUIT:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							closeProgram();
						} catch (Exception e1) {
							LOGGER.severe("Error in closing program!");
							e1.printStackTrace();
						}

					}
				});
				break;
			case ID.MENU_ITEM_HIDE_ALL_MARKINGS:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							setVisibilityOfAllMarkingLayers(false);
						} catch (Exception e1) {
							LOGGER.severe("Error in setting all MarkingLayers unvisible!");
							e1.printStackTrace();
						}
					}
				});
				break;
			case ID.MENU_ITEM_SHOW_ALL_MARKINGS:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						try {
							setVisibilityOfAllMarkingLayers(true);
						} catch (Exception e1) {
							LOGGER.severe("Error in setting all MarkingLayers visible!");
							e1.printStackTrace();
						}

					}
				});
				break;
			case ID.MENU_ITEM_FILE_EXPORT_TO_CSV:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							exportResults(ID.FILE_TYPE_CSV);
						} catch (Exception e1) {
							LOGGER.severe("Error in exporting results to CSV file!");
							e1.printStackTrace();
						}

					}
				});
				break;

			case ID.MENU_ITEM_FILE_EXPORT_TO_TEXT_FILE:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							exportResults(ID.FILE_TYPE_TEXT_FILE);
						} catch (Exception e1) {
							LOGGER.severe("Error in exporting results to text file!");
							e1.printStackTrace();
						}

					}
				});
				break;

			case ID.MENU_ITEM_FILE_EXPORT_TO_CLIPBOARD:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						try {
							exportResults(ID.CLIPBOARD);
						} catch (Exception e1) {
							LOGGER.severe("Error in exporting results to clipboard!");
							e1.printStackTrace();
						}
					}
				});
				break;

			case ID.EXPORT_IMAGE:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						try {
							exportImages();
						} catch (Exception e1) {
							LOGGER.severe("Error in exporting images!");
							e1.printStackTrace();
						}
					}
				});
				break;

			case ID.EXPORT_IMAGE_SET:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						try {
							exportImageSet();
						} catch (Exception e1) {
							LOGGER.severe("Error in exporting image Set!");
							e1.printStackTrace();
						}
					}
				});
				break;


			case ID.MENU_ITEM_EDIT_SET_MARKING_PROPERTIES:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						try {
							Point guiPoint = new Point((int)getGUI().getBounds().getX()+50, (int)getGUI().getBounds().getY()+50);
							setPropertiesOfAllMarkinglayers(guiPoint);
						} catch (Exception e1) {
							LOGGER.severe("Error in setting marking properties!");
							e1.printStackTrace();
						}
					

					}
				});
				break;

			case ID.MENU_ITEM_EDIT_CLEAR_SINGLE_COUNTING:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							clearMarkingsOfSelectedMarkingLayer();
						} catch (Exception e1) {
							LOGGER.severe("Error in clearing single counting!");
							e1.printStackTrace();
						}
					}
				});
				break;


			case ID.MENU_ITEM_EDIT_CLEAR_ALL_COUNTINGS:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							clearMarkingsOfAllMarkingLayers();
						} catch (Exception e1) {
							LOGGER.severe("Error in clearing all countings!");
							e1.printStackTrace();
						}
					}
				});
				break;
			case ID.MENU_ITEM_GRID_PROPERTIES:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						try {
							Point guiPoint = new Point((int)getGUI().getBounds().getX()+50, (int)getGUI().getBounds().getY()+50);
							showGridPropertiesPanelForAllMarkingLayers(guiPoint, taskManager.getAllMarkingLayers());
						} catch (Exception e1) {
							LOGGER.severe("Error in setting grid properties to all MarkingLayers!");
							e1.printStackTrace();
						}
					}
				});
				break;

			case ID.FADE_GRID:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							diminishGrid();
						} catch (Exception e1) {
							LOGGER.severe("Error in fading grid!");
							e1.printStackTrace();
						}

					}
				});
				break;

			case ID.SHOW_GRID_OPAQUE:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						try {
							showGrid();
						} catch (Exception e1) {
							LOGGER.severe("Error in setting grid opaque!");
							e1.printStackTrace();
						}
					}
				});
				break;

			case ID.MENU_ITEM_INFO:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							Point guiPoint = new Point((int)getGUI().getBounds().getX()+50, (int)getGUI().getBounds().getY()+50);
							showInfo(guiPoint);
						} catch (Exception e1) {
							LOGGER.severe("Error in showing info!");
							e1.printStackTrace();
						}

					}
				});
				break;

			case ID.MENU_ITEM_HELP:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							showWebInstructions();
						} catch (Exception e1) {
							LOGGER.severe("Error in showing help!");
							e1.printStackTrace();
						}

					}
				});
				break;

			case ID.MENU_ITEM_LOG:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							logFrame.setBounds(((JMenuItem)e.getSource()).getX(), ((JMenuItem)e.getSource()).getY(), 400, 500);
							logFrame.setVisible(true);
						} catch (Exception e1) {
							LOGGER.severe("Error in showing log!");
							e1.printStackTrace();
						}

					}
				});
				break;
		}
	}
	
	/**
	 * Adds imported ImageLayers to InformationCenter. Updates the selected layers and GUI
	 *
	 * @param iLayerList Array of one or more ImageLayers.
	 * @throws Exception the exception
	 */
	public void addImageLayerList(ArrayList<ImageLayer> iLayerList) throws Exception{
		// set the ImageLayers through TaskManager -> InformationCenter.imageLayerList	(finalizes the layers -> gives ids)
		this.taskManager.addImageLayers(iLayerList);

		//updates the selected Layers and Refreshes GUI
		refreshLayersAndGUI();

		// refresh precouting components
		cleanPreCountingIfNecessary();
	}


	/**
	 * Adds a single marking to Point p, if no any marking found too close. 
	 *
	 * @param p Point at screen where mouse was pressed
	 * @return boolean true if marking was added; otherwise false
	 * @throws Exception the exception
	 */
	public boolean addSingleMarking(Point p) throws Exception{
		Point panelPoint =getClosestMarkingPointAtScreen(p, SharedVariables.DISTANCE_TO_ADD);
		if(panelPoint == null)
			return taskManager.addSingleMarking(p);
		return false;
	}

	/**
	 * Changes the selected ImageLayer one up or down in ImageLayerlist.
	 *
	 * @param directionID int ID.MOVE_DOWN or ID.MOVE.UP
	 * @throws Exception the exception
	 */
	public void changeSelectedImageLayerUpOrDown(int directionID) throws Exception{
		setSelectedImageLayerAndImage(this.taskManager.getSelectedImageLayerAtUpOrDown(directionID),ID.IMAGELAYER_CHANGE_IMAGELAYER);
	}
	
	/**
	 * Changes the selected MarkingLayer one up or down in MarkingLayerImageLayerlist.
	 *
	 * @param directionID int ID.MOVE_DOWN or ID.MOVE.UP
	 * @throws Exception the exception
	 */
	public void changeSelectedMarkingLayerUpOrDown(int directionID) throws Exception{
		setSelectedMarkingLayer(this.taskManager.getSelectedMarkingLayerAtUpOrDown(directionID));
	}

	/**
	 *  Refreshes the PrecountingManager Thread to initial state if necessary.
	 */
	private void cleanPreCountingIfNecessary(){
		try {
			// clean precoutingManager
			if(this.taskManager.getImageLayerList() == null || this.taskManager.getImageLayerList().size()==0 ||
					this.taskManager.getAllMarkingLayers()== null || this.taskManager.getAllMarkingLayers().size() == 0){
				this.taskManager.cleanPrecountingManager();
				this.preCountButton.setEnabled(false);
			}
			else{
				// at least one ImageLayer and MarkingLayer
				if(this.taskManager.getSelectedImageLayer() != null && this.taskManager.getSelectedMarkingLayer() != null)
					this.preCountButton.setEnabled(true);
				if(this.taskManager.getPrecountThreadManager() != null){
					if(this.taskManager.getPrecountThreadManager().getiLayerID() != this.taskManager.getSelectedImageLayer().getLayerID() ||
							this.taskManager.getPrecountThreadManager().getmLayerID() != this.taskManager.getSelectedMarkingLayer().getLayerID()){
						this.taskManager.cleanPrecountingManager();
					}

				}
			}
		} catch (Exception e) {
			LOGGER.severe("Error in cleaning precounting data!");
			e.printStackTrace();
		}

	}


	/**
	 *  Removes all markings of all MarkingLayers. Refreshes the MarkingPanels and ImageLayerInfos.
	 *
	 * @throws Exception the exception
	 */
	private void clearMarkingsOfAllMarkingLayers() throws Exception{
		ArrayList<MarkingLayer> allMarkingLayers=this.taskManager.getAllMarkingLayers();
		if(allMarkingLayers != null && allMarkingLayers.size()>0){
			shadyMessageDialog =new ShadyMessageDialog(this,"Clear Countings?", "Clear countings of all MarkingLayers?", ID.YES_NO, this);
			this.guiComponentListener.setChildDialog(shadyMessageDialog);
			if(shadyMessageDialog.showDialog() == ID.YES){
				for (Iterator<MarkingLayer> iterator = allMarkingLayers.iterator(); iterator.hasNext();) {
					MarkingLayer markingLayer = (MarkingLayer) iterator.next();
					markingLayer.clearCoordinateList();
				}
				updateImageLayerInfos();
				refreshMarkingPanels();
			}
			this.guiComponentListener.setChildDialog(null);
			shadyMessageDialog=null;
			
		}
		else{
			showMessage("No MarkingLayer!", "No any MarkingLayer found which counting to clear.", ID.OK);
		}
	}


	


	/**
	 *  Removes all markings of selected MarkingLayer. Refreshes the selected MarkingPanel and ImageLayerInfos.
	 *
	 * @throws Exception the exception
	 */
	private void clearMarkingsOfSelectedMarkingLayer() throws Exception{
		MarkingLayer sMlayer=this.taskManager.getSelectedMarkingLayer();
		if(sMlayer != null){
			ShadyMessageDialog dialog=new ShadyMessageDialog(this,"Clear Countings?", "Clear countings of MarkingLayer: "+sMlayer.getLayerName()+ "?", ID.YES_NO, this);
			if(dialog.showDialog() == ID.YES){
				sMlayer.clearCoordinateList();
				updateImageLayerInfos();
				updateCoordinatesOfSelectedMarkingPanel();
			}
		}
		else{
			showMessage("No MarkingLayer!", "No any MarkingLayer found which countings to clear.", ID.OK);
		}
	
	}

	/**
	 *  Closes the program after verifying from user should unsaved information be saved
	 */
	private void closeProgram(){
		try {
			// saving process
			if(this.taskManager.getImageLayerList() != null && this.taskManager.getImageLayerList().size() >0 && this.taskManager.isMadeChanges()){
				ShadyMessageDialog dialog=new ShadyMessageDialog(this,"Exiting MC-Cone", "Changes has been made. Save Markings?", ID.YES_NO_CANCEL, this);
			
				int selectionID = dialog.showDialog();
				if(selectionID == ID.YES){
					saveMarkings();

				}
				else if(selectionID == ID.CANCEL){
					return;
				}


			}
			else {
				ShadyMessageDialog dialog=new ShadyMessageDialog(this,"Exiting MC-Cone", "Do you really want to quit MC-Cone?", ID.YES_NO, this);
				int selectionID = dialog.showDialog();
				if(selectionID == ID.NO){
					return;
				}
				dialog=null;
			}

			// close program
			System.exit(0);
		} catch (Exception e) {
			
			LOGGER.severe("Error in saving process before closing program. "+ e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Creates new MarkingLayer to under ImageLayer by given ID. Creates also a new MarkingPanel and refreshes the ImageLayerInfos. 
	 *
	 * @param imageLayerID int ID of ImageLayer
	 * @throws Exception the exception
	 */
	public void createNewMarkingLayer(int imageLayerID) throws Exception{
		MarkingLayer ml= taskManager.createNewMarkingLayer(imageLayerID);
		if(ml.getLayerID()>0){

			MarkingPanel p=new MarkingPanel(ml);
			p.setBounds(this.imagePanel.getBounds()); // set the MarkingPanel size and position same as ImagePanel

			// add new MarkingPanel to JLayeredPane
			this.layers.add(p, JLayeredPane.DRAG_LAYER);
			// if ImageLayer of MarkingPanel is selected -> set the new MarkingPanel front of panels
			if(this.taskManager.isSelectedImageLayer(imageLayerID)){
				updateGridPanel();
				this.layers.moveToFront(this.gridPanel);
				this.highlightPanel.setLayer(this.taskManager.getSelectedMarkingLayer());

				this.layers.moveToFront(this.highlightPanel);
				this.layers.moveToFront(p);
			}
			p=null;
		
			// update imagelayerinfos
			updateImageLayerInfos();

			//refresh precountingComponents
			cleanPreCountingIfNecessary();
			
			setMenuItemsEnabled(ID.MARKINGLAYERS);

		}

	}

	/**
	 * Sets Grid to transparent.
	 *
	 * @throws Exception the exception
	 */
	private void diminishGrid() throws Exception{
		this.gridPanel.setExtraDimTransparency();
		this.gridPanel.repaint();
	}

	/**
	 * Organizes the dragging of image and visible markings at screen.
	 *
	 * @param e MouseEvent which has triggered the dragging.
	 * @throws Exception the exception
	 */
	public void dragLayers(MouseEvent e) throws Exception{
		int x= e.getX();
		int y= e.getY();
		// if dragging was started
		if(this.guiListener.getPreviousDraggingPoint() == null){
			this.guiListener.setPreviousDraggingPoint(new Point(x,y));
		}
		else{
			//count movement compared to previous point.
			Point movementXY= new Point(this.guiListener.getPreviousDraggingPoint().x -x,this.guiListener.getPreviousDraggingPoint().y - y);
			//get part of image to print to screen
			PositionedImage pi = this.taskManager.dragLayers(movementXY);
			if(pi != null && pi.getImage() != null){
				this.imagePanel.setImage(pi);
				updateCoordinatesOfVisibleMarkingPanels();
				updateGridPanel();
				removeHighLightPoint();
				this.layers.repaint();
			}
			this.guiListener.setPreviousDraggingPoint(new Point(x,y)); // set the previous dragging point to this one.
		}



	}

	/**
	 *  Opens a dialog to export image(s).
	 *
	 * @throws Exception the exception
	 */
	public void exportImages() throws Exception{
		if(taskManager.getImageLayerList() != null && taskManager.getImageLayerList().size()>0){

			@SuppressWarnings("unused")
			ExportImage exportImage = new ExportImage(this, this, taskManager.getImageLayerList());
		}
		else{
			showMessage( "Not starting exporting images", "Not exported images, because no images were found", ID.OK);

		}
	}

	/**
	 *  Opens a new dialog for selecting images and organizing images to create set of images.
	 *
	 * @throws Exception the exception
	 */
	private void exportImageSet() throws Exception{

			
			
			ImageSetCreator iCreator=new ImageSetCreator(this, this.taskManager, this);
			this.guiComponentListener.setChildDialog(iCreator);
			iCreator.showDialog();
			iCreator=null;
			this.guiComponentListener.setChildDialog(null);
			
			

	}
	
	/**
	 * Opens a dialog to export results.
	 *
	 * @param id int ID type of saving type (ID.FILE_TYPE_TEXT_FILE, ID.FILE_TYPE_CSV or ID.CLIPBOARD).
	 * @throws Exception the exception
	 */
	public void exportResults(int id) throws Exception{
		if(taskManager.getImageLayerList() != null && taskManager.getImageLayerList().size()>0){

			
			 new ExportResults(this, this, taskManager.getImageLayerList(), id);
		}
		else{
			showMessage( "Not starting saving", "Not saved markings, because no markings were found", ID.OK);

		}
	}



	/**
	 * Returns all Markinglayers.
	 *
	 * @return ArrayList of all MarkingLayers
	 * @throws Exception the exception
	 */
	public ArrayList<MarkingLayer> getAllMarkingLayers() throws Exception{
		return this.taskManager.getAllMarkingLayers();
	}

	/**
	 * Finds from selected MarkingPanel a marking closest to point where mouse was pressed. 
	 * If (Manhattan) distance between them is shorter than given limit the point is returned.
	 *
	 * @param p Point at screen where mouse button was pressed
	 * @param minDistance int minimum distance between single marking and where mouse was pressed
	 * @return Point if closed any close enough. Otherwise null;
	 * @throws Exception the exception
	 */
	private Point getClosestMarkingPointAtScreen(Point p, int minDistance) throws Exception{
		MarkingLayer selectedMarkingLayer= taskManager.getSelectedMarkingLayer();
		if(selectedMarkingLayer != null){
			//is any marking coordinates in  selected MarkingPanel 
			MarkingPanel selectedMarkingPanel = getMarkingPanelByLayerID(selectedMarkingLayer.getLayerID());
			if(selectedMarkingPanel != null && selectedMarkingPanel.getCoordinateList() != null)
				return selectedMarkingPanel.getClosestMarkingPoint(p, minDistance); // get the closest point
		}
		return null;

	}
	
	/**
	 * Returns the object of main window. Only one GUI object exists.
	 *
	 * @return GUI the main window object.
	 * @throws Exception the exception
	 */
	private GUI getGUI() throws Exception{
		return this;
	}

	/**
	 * Gets the guiComponentListener.
	 *
	 * @return the guiComponentListener
	 * @throws Exception the exception
	 */
	public GUIcomponentListener getGuiComponentListener() throws Exception {
		return guiComponentListener;
	}

	/**
	 * Gets the GUIcomponentListener of GUI if found.
	 *
	 * @return the GUI component listener
	 */
	public GUIcomponentListener getGUIComponentListener(){
		try {
			ComponentListener[] list = this.getComponentListeners();
			if(list != null && list.length>0){
				for (int i = 0; i < list.length; i++) {
					if(list[i].getClass().getName().toString().equals("gui.GUIcomponentListener")){				
						return (GUIcomponentListener)list[i];				
					}
				}		
			}		
			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in getting GUIComponent Listener!");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates ImageIcon from giving image path.
	 *
	 * @param path String path of image file
	 * @return ImageIcon the created icon.
	 * @throws Exception the exception
	 */
	public ImageIcon getImageIcon(String path) throws Exception{

			URL url = this.getClass().getResource(path);
			ImageIcon img = new ImageIcon(url);
			return img;


	}
	
	/**
	 * Returns ImageLayer by given ID.
	 *
	 * @param iLayerID ID of ImageLayer.
	 * @return the ImageLayer by id
	 * @throws Exception the exception
	 */
	public ImageLayer getImageLayerByID(int iLayerID) throws Exception{
		return this.taskManager.getImageLayerByID(iLayerID);
	}

	/**
	 * returns ImageLayer, which has given MarkingLayer (MarkingLayer ID is given).
	 *
	 * @param markingLayerID ID of MarkingLayer.
	 * @return the ImageLayer by ID of MarkingLayer
	 * @throws Exception the exception
	 */
	public ImageLayer getImageLayerByMarkingLayerID(int markingLayerID) throws Exception{
		return this.taskManager.getImageLayerByMarkingLayerID(markingLayerID);
	}
	
	

	/**
	 * Returns the MarkingLayer by given IDs.
	 *
	 * @param iLayerID ImageLayerID
	 * @param mLayerID MarkingLayerID
	 * @return the MarkingLayer
	 * @throws Exception the exception
	 */
	public MarkingLayer getMarkingLayer(int iLayerID, int mLayerID) throws Exception{
		return this.taskManager.getMarkingLayer(iLayerID, mLayerID);
	}



	/**
	 * Mediates call to TaskManager to update markings of several ImageLayers by giving as parameter a File (.xml) where from the markings are imported.
	 *
	 * @param xmlFile File which contains the markings
	 * @param imageLayerList ArrayList of ImageLayer where markings are added
	 * @return ArrayList of ImageLayers where has the markings added
	 * @throws Exception the exception
	 */
	public ArrayList<ImageLayer> getMarkingOfXML(File xmlFile, ArrayList<ImageLayer>imageLayerList) throws Exception{
	    return	taskManager.getMarkingsOfXML(xmlFile, imageLayerList);

	}

	/**
	 * Mediates call to TaskManager to update markings of one ImageLayers by giving as parameter a File (.xml) where from the markings are imported.
	 *
	 * @param xmlFile File which contains the markings
	 * @param imageLayer object where markings are added
	 * @return MarkingLayer object where has the markings added
	 * @throws Exception the exception
	 */
	public ImageLayer getMarkingOfXML(File xmlFile, ImageLayer imageLayer) throws Exception{
	    return	taskManager.getMarkingsOfXML(xmlFile, imageLayer);

	}

	/**
	 * Fetches and returns a MarkingPanel corresponding to given MarkingLayer ID.
	 *
	 * @param mLayerID int ID of MarkingLayer, which MarkingPanel is fetched.
	 * @return MarkingPanel corresponding to given MarkingLayer ID. If not found, null is returned.
	 * @throws Exception the exception
	 */
	private MarkingPanel getMarkingPanelByLayerID(int mLayerID) throws Exception{
		Component[] mPanels=this.layers.getComponents();
		if(mPanels != null && mPanels.length>1){ // ImagePanel is the first layer
			for(int i=0;i<mPanels.length;i++){
			//	if(mPanels[i].getClass().toString().equals(MarkingPanel.class.toString())){
				if(mPanels[i] instanceof MarkingPanel){
					if(((MarkingPanel)mPanels[i]).getId()== mLayerID){
						return (MarkingPanel)mPanels[i];
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the folder name which is previously used.
	 *
	 * @return String folder name which is previously used.
	 * @throws Exception the exception
	 */
	public String getPresentFolder() throws Exception{
		return taskManager.getPresentFolder();
	}


	/**
	 * Returns present dimension used by opened images.
	 *
	 * @return Dimension present dimension used by opened images.
	 * @throws Exception the exception
	 */
	public Dimension getPresentImageDimension() throws Exception {
		return this.taskManager.getPresentImageDimension();
	}


	/**
	 * Returns width of rightpanel.
	 *
	 * @return int width of rightpanel
	 * @throws Exception the exception
	 */
	public int getRightPanelWidth() throws Exception{
		return this.rightPanelWidth;
	}
	
	/**
	 * Returns screen size.
	 *
	 * @return Dimension screen size.
	 * @throws Exception the exception
	 */
	public Dimension getScreenSize() throws Exception{
		// return this.screenSize;
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	/**
	 * Returns a relation value between dimension of image and dimension of visible part of image.
	 *
	 * @return double size multiplier which is relation between dimension of image and dimension of visible part of image.
	 * @throws Exception the exception
	 */
	public double getSizeMultiplier() throws Exception{
		return this.taskManager.getShapeSizeMultiplier();
	}
	
	/**
	 * Calculates and returns position and size of window where dialog can be positioned.
	 * @return Rectangle size and position where dialog window can be positioned.
	 */
	public Rectangle getVisibleWindowBounds(){
		
		try {
			if(SharedVariables.operationSystem == ID.OS_LINUX_UNIX){ // in linux may the unity menu bar affect to painting of dimming 
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				Rectangle windowBounds = ge.getMaximumWindowBounds();
				
				int windowLeftX = windowBounds.x;	
				int x=this.getBounds().x;		
				int y= this.getBounds().y;
				int width=this.getBounds().width;
				int height=this.getBounds().height;
				// GUI over window

				int guiX= this.getBounds().x;		
				int guiWidth= this.getWidth();
				
				if(guiX<windowLeftX){
					x=windowLeftX;
					width = guiWidth-(windowLeftX-guiX);
				}	
				return new Rectangle(x,y,width,height);
			}	
			// normally 
			return this.getBounds();
		} catch (HeadlessException e) {
			LOGGER.severe("Error in calculting window size and position!");
			e.printStackTrace();
			return null;
		}


	}
	
	/**
	 * Returns the size and position of VisualPanel.
	 * @return Rectangle size of VisualPanel.
	 */
	public Rectangle getVisualPanelSize(){
		try {
			return this.visualPanel.getBounds();
		} catch (Exception e) {
			LOGGER.severe("Error in getting Visual Panel!");
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	/**
	 * Checks is given image name already in use.
	 *
	 * @param imageName String imageName, which duplicate using is checked.
	 * @return boolean true if name is already used. Otherwise false.
	 * @throws Exception the exception
	 */
	public boolean imageNameAlreadyUsed(String imageName) throws Exception{
		return taskManager.imageNameAlreadyUsed(imageName);
	}

	/**
	 * Initializes the JPanel and it's AddImage -JButton.
	 * @throws Exception
	 */
	private void initAddImageLayerButton() throws Exception{
		addImageLayerJPanel = new JPanel();
		addImageLayerJPanel.setLayout(new BoxLayout(addImageLayerJPanel, BoxLayout.PAGE_AXIS));
		addImageLayerJPanel.setMaximumSize(new Dimension(5000,40));
		addImageLayerJPanel.setPreferredSize(new Dimension(220,40));
		addImageLayerJPanel.setBackground(Color_schema.imageInfoPanel_bg);
		JButton addImageLayerJButton = new JButton("ADD NEW IMAGE");
		addImageLayerJButton.setFont(new Font("Consolas", Font.BOLD,18));
		addImageLayerJButton.setMaximumSize(new Dimension(220, 35));
		addImageLayerJButton.setMinimumSize(new Dimension(220, 35));
		addImageLayerJButton.setPreferredSize(new Dimension(220, 35));
		addImageLayerJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		addImageLayerJButton.setToolTipText("Create new Image Layer" );
		addImageLayerJButton.setFocusable(false);
	//	addMouseListenerForJButton(addImageLayerJButton);
		MouseListenerCreator.addMouseListenerToNormalButtons(addImageLayerJButton);
		addImageLayerJButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				openAddImageLayerDialog(null); // open dialog with no given imagefiles

			}
		});
		addImageLayerJPanel.add(Box.createRigidArea(new Dimension(0,5)));
		addImageLayerJPanel.add(addImageLayerJButton);
		addImageLayerJPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
	}

	/**
	 *  Initializes GlassPane, which is used in precounting cells.
	 *
	 * @throws Exception the exception
	 */
	private void initGlassPane() throws Exception{
		glassPane = new PrecountGlassPane(this, this.doublePanel, this.menubar, guiListener);
		this.setGlassPane(glassPane);
	}


	/**
	 * Initializes dimension of all panels in main window.
	 * @throws Exception
	 */
	private void initializeSizes() throws Exception{

		this.revalidate();
		// the size of doublepanel is already painted -> set preferred, maximum and minimum dimensions.
		doublePanel.setPreferredSize(doublePanel.getSize());
		doublePanel.setMinimumSize(new Dimension(doublePanel.getPreferredSize().width/2,doublePanel.getPreferredSize().height/2));
		if(doublePanel.getRightComponent().getWidth() <200){
			doublePanel.setResizeWeight(0.7);
		}
		this.revalidate();

		Rectangle doublePanelLeftBounds= doublePanel.getLeftComponent().getBounds();
		leftPanel.setBounds((int)doublePanelLeftBounds.getX(), (int)doublePanelLeftBounds.getY(), (int)doublePanelLeftBounds.getWidth(), (int)doublePanelLeftBounds.getHeight());
		leftPanel.revalidate();
		downBarPanel.setBounds(0, 0, (int)leftPanel.getBounds().getWidth(), 40);
		downBarPanel.revalidate();
		visualPanel.setBounds((int)leftPanel.getBounds().getX(),(int)leftPanel.getBounds().getY(),(int)leftPanel.getBounds().getWidth(), (int)(leftPanel.getBounds().getHeight()- downBarPanel.getBounds().getHeight()));
		visualPanel.revalidate();
		layers.setBounds(5,5,(int)visualPanel.getBounds().getWidth()-10,(int)visualPanel.getBounds().getHeight()-10);
		this.revalidate();
		imagePanel.setBounds(0,0,(int)layers.getBounds().getWidth(),(int)layers.getBounds().getHeight());
		this.highlightPanel.setBounds(this.imagePanel.getBounds());
		this.gridPanel.setBounds(this.imagePanel.getBounds());
		imagePanel.revalidate();
		LOGGER.fine("vp "+visualPanel.getWidth() + " " + visualPanel.getHeight());
		LOGGER.fine("ip "+imagePanel.getWidth() + " " + imagePanel.getHeight());
		this.rightPanelWidth=doublePanel.getSize().width- doublePanel.getRightComponent().getSize().width;
		this.updateImagePanelSize();
		this.repaint();

	}

	/**
	 *  Initializes the Logging system.
	 */
	private void initLogging(Level level){
		try {
			//create Log for showing log in JFrame and saving message line in file
			logFrame=new LogFrame();
			ProgramLogger.setup(logFrame, false);
			LOGGER.setLevel(level);
		} catch (Exception e1) {
			System.out.println("Error in logging");
			e1.printStackTrace();
		}

	}

	/**
	 *  Initializes the menubar components.
	 *
	 * @throws Exception the exception
	 */
	private void initMenubar() throws Exception{
		try {
			
			menubar =new JMenuBar();
			menubar.setMaximumSize(new Dimension(5000,30));

			// file-menu
			JMenu menu_file = new JMenu("File");
			menu_file.setMnemonic(KeyEvent.VK_ALT);
			JMenuItem menu_item_open_image =new JMenuItem("Add Image layer(s)");
			menu_item_open_image.setMnemonic(KeyEvent.VK_A);
			menu_item_open_image.setToolTipText("CTRL + A");
			addActionsToMenuItems(menu_item_open_image, ID.MENU_ITEM_FILE_ADD_IMAGES);

			JMenuItem menu_item_import_markings =new JMenuItem("Manage ImageLayers and Markings");
			menu_item_import_markings.setMnemonic(KeyEvent.VK_M);
			menu_item_import_markings.setToolTipText("CTRL + M");
			addActionsToMenuItems(menu_item_import_markings, ID.MENU_ITEM_FILE_MANAGE_LAYERS);

			menu_item_save_markings = new JMenuItem("Save Markings");
			menu_item_save_markings.setMnemonic(KeyEvent.VK_S);
			menu_item_save_markings.setToolTipText("CTRL + S");
			addActionsToMenuItems(menu_item_save_markings, ID.MENU_ITEM_FILE_SAVE_MARKINGS);

			menu_export_results = new JMenu("Export Results to");
			menu_export_results.setMnemonic(KeyEvent.VK_X);
			
			JMenuItem menu_item_csv_file=new JMenuItem("CSV-file");
			menu_item_csv_file.setMnemonic(KeyEvent.VK_V);
			menu_item_csv_file.setToolTipText("CTRL + V");

			JMenuItem menu_item_tab_delimited_file=new JMenuItem("Tab-delimited text file");
			menu_item_tab_delimited_file.setMnemonic(KeyEvent.VK_T);
			menu_item_tab_delimited_file.setToolTipText("CTRL + T");
			JMenuItem menu_item_tab_delimited_clipboard=new JMenuItem("Clipboard as Tab-delimited text");
			menu_item_tab_delimited_clipboard.setMnemonic(KeyEvent.VK_B);
			menu_item_tab_delimited_clipboard.setToolTipText("CTRL + B");
			
			menu_export_results.add(menu_item_csv_file);
			menu_export_results.add(menu_item_tab_delimited_file);
			menu_export_results.add(menu_item_tab_delimited_clipboard);

			menu_export_images = new JMenuItem("Export Images");
			menu_export_images.setMnemonic(KeyEvent.VK_E);
			menu_export_images.setToolTipText("CTRL + E");
			JMenuItem menu_export_set_images =new JMenuItem("Export Set of Images");
			menu_export_set_images.setMnemonic(KeyEvent.VK_I);
			

			addActionsToMenuItems(menu_item_csv_file, ID.MENU_ITEM_FILE_EXPORT_TO_CSV);
			addActionsToMenuItems(menu_item_tab_delimited_file, ID.MENU_ITEM_FILE_EXPORT_TO_TEXT_FILE);
			addActionsToMenuItems(menu_item_tab_delimited_clipboard, ID.MENU_ITEM_FILE_EXPORT_TO_CLIPBOARD);
			addActionsToMenuItems(menu_export_images, ID.EXPORT_IMAGE);
			addActionsToMenuItems(menu_export_set_images, ID.EXPORT_IMAGE_SET);

			JMenuItem menu_file_close_program = new JMenuItem("Quit program");
			menu_file_close_program.setMnemonic(KeyEvent.VK_Q);
			addActionsToMenuItems(menu_file_close_program, ID.MENU_ITEM_FILE_QUIT);

			menu_file.add(menu_item_open_image);
			menu_file.add(menu_item_import_markings);
			menu_file.add(menu_item_save_markings);
			menu_file.add(menu_export_results);
			menu_file.add(menu_export_images);
			menu_file.add(menu_export_set_images);
			menu_file.add(menu_file_close_program);

			menu_edit = new JMenu("Edit");
			menu_edit_set_marking_properties = new JMenuItem("Edit properties of all markings");
			menu_edit_set_marking_properties.setMnemonic(KeyEvent.VK_E);
			addActionsToMenuItems(menu_edit_set_marking_properties, ID.MENU_ITEM_EDIT_SET_MARKING_PROPERTIES);

			menu_edit_clear_single_countings = new JMenuItem("Clear countings of selected MarkingLayer");
			menu_edit_clear_single_countings.setMnemonic(KeyEvent.VK_L);
			addActionsToMenuItems(menu_edit_clear_single_countings, ID.MENU_ITEM_EDIT_CLEAR_SINGLE_COUNTING);

			menu_edit_clear_all_countings = new JMenuItem("Clear countings of all MarkingLayers");
			menu_edit_clear_all_countings.setMnemonic(KeyEvent.VK_R);
			addActionsToMenuItems(menu_edit_clear_all_countings, ID.MENU_ITEM_EDIT_CLEAR_ALL_COUNTINGS);

				
			JCheckBoxMenuItem useStrictPrecounting = new JCheckBoxMenuItem("Use Strict Precounting");
			
			useStrictPrecounting.setSelected(false);
			useStrictPrecounting.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					SharedVariables.setUseStrickSearch(((JCheckBoxMenuItem)e.getSource()).isSelected());
					
				}
			});
			
			menu_edit.add(menu_edit_set_marking_properties);
			menu_edit.add(menu_edit_clear_single_countings);
			menu_edit.add(menu_edit_clear_all_countings);
			menu_edit.add(useStrictPrecounting);
			
			menu_show = new JMenu("Show");
			menu_show.setMnemonic(KeyEvent.VK_S);
			menu_show_all_markings = new JMenuItem("Show all markings");
			addActionsToMenuItems(menu_show_all_markings, ID.MENU_ITEM_SHOW_ALL_MARKINGS);

			menu_show_all_markings.setMnemonic(KeyEvent.VK_A);
			menu_hide_all_markings = new JMenuItem("Hide all markings");
			addActionsToMenuItems(menu_hide_all_markings,ID.MENU_ITEM_HIDE_ALL_MARKINGS);

			menu_show.add(menu_show_all_markings);
			menu_show.add(menu_hide_all_markings);

			menu_grid = new JMenu("GRID");
			menu_grid.setMnemonic(KeyEvent.VK_G);
			JMenuItem menu_grid_properties = new JMenuItem("Set Grid Properties");
			menu_grid_properties.setMnemonic(KeyEvent.VK_P);
			menu_grid.add(menu_grid_properties);
			addActionsToMenuItems(menu_grid_properties, ID.MENU_ITEM_GRID_PROPERTIES);
			menu_grid_hide = new JMenuItem("Fade");
			menu_grid_hide.setMnemonic(KeyEvent.VK_F);
			addActionsToMenuItems(menu_grid_hide, ID.FADE_GRID);
			menu_grid_show = new JMenuItem("Set Opaque");
			menu_grid_show.setMnemonic(KeyEvent.VK_O);
			addActionsToMenuItems(menu_grid_show, ID.SHOW_GRID_OPAQUE);
			menu_grid.add(menu_grid_show);
			menu_grid.add(menu_grid_hide);

			//Help menu
			JMenu menu_help = new JMenu("Help");
			JMenuItem menu_help_show_log = new JMenuItem("Show Log");
			JMenuItem menu_help_show_manual = new JMenuItem("Show Web Instructions");	// opens web browser
			JMenuItem menu_help_info = new JMenuItem("About MC-Cone");

			addActionsToMenuItems(menu_help_show_log, ID.MENU_ITEM_LOG);
			addActionsToMenuItems(menu_help_show_manual, ID.MENU_ITEM_HELP);
			addActionsToMenuItems(menu_help_info, ID.MENU_ITEM_INFO);

			menu_help.add(menu_help_show_manual);
			menu_help.add(menu_help_show_log);
			menu_help.add(menu_help_info);

			// add all menus to menubar
			menubar.add(menu_file);
			menubar.add(menu_edit);
			menubar.add(menu_show);
			menubar.add(menu_grid);
			menubar.add(menu_help);
			
			

			// add menubar to JFrame
			this.setJMenuBar(menubar);
		} catch (Exception e) {
			LOGGER.severe("Error in creating GUI");
			throw new Exception();
		}


	}

	/**
	 *  Initializes the Splitpane which contains GUI components for showing images, markings and info of them in list.
	 *
	 * @throws Exception the exception
	 */
	private void initSplitPane() throws Exception {
		try{
			//create JSplitPane
			doublePanel = new JSplitPane();
			doublePanel.setResizeWeight(0.80);
			guiListener.addKeyInputMap(this.doublePanel, ID.WHOLE_GUI_FRAME);
			doublePanel.setMaximumSize(new Dimension(getScreenSize().width, getScreenSize().height));
			doublePanel.setBorder(BorderFactory.createEmptyBorder());

			 PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
			      public void propertyChange(PropertyChangeEvent changeEvent) {			        
			        String propertyName = changeEvent.getPropertyName();
			        if (propertyName.equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {	     
			          Integer last = (Integer) changeEvent.getNewValue();	         
			          visualPanel.setBounds(0, 0, (int)last, (int)(leftPanel.getBounds().getHeight()-downBarPanel.getBounds().getHeight()));
			          visualPanel.revalidate();
			          resizeLayerComponents();
			          rightPanelWidth=doublePanel.getSize().width-(int)last;
			          updateImageLayerInfos();
			          doublePanel.revalidate();
			          doublePanel.repaint();
			        }
			      }
			    };

			    doublePanel.addPropertyChangeListener(propertyChangeListener);

			// setup left side of JSplitPane
			leftPanel = new JPanel();
			leftPanel.setLayout(new BorderLayout(0,0));

			leftPanel.setBorder(BorderFactory.createEmptyBorder());
			leftPanel.setMinimumSize(new Dimension((int)(getScreenSize().width/4),75));

			// panel which visualizes the image and markings
			visualPanel = new JPanel();
			visualPanel.setBorder(BorderFactory.createEmptyBorder());
			visualPanel.setLayout(null);
			visualPanel.setBackground(Color_schema.dark_40);

			// down bar holds the scrolls for zoom
			downBarPanel = new JPanel();
			downBarPanel.setMaximumSize(new Dimension((int)(leftPanel.getMaximumSize().getWidth()),40));
			downBarPanel.setMinimumSize(new Dimension((int)(leftPanel.getPreferredSize().getWidth()),40));
			downBarPanel.setPreferredSize(new Dimension((int)(leftPanel.getPreferredSize().getWidth()),40));
			downBarPanel.setBackground(Color_schema.dark_40);
			downBarPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 2));
			downBarPanel.setLayout(new BoxLayout(downBarPanel, BoxLayout.LINE_AXIS));
			downBarPanel.add(setUpSLiderPanel(-200, 200, 0, 50, 100));
			JLabel zoomscrollProcent=new JLabel("%");
			zoomscrollProcent.setFont(new Font("Consolas", Font.BOLD,16));
			downBarPanel.add(Box.createHorizontalGlue());
			JLabel precountJLabel=new JLabel("PRECOUNTING:");
			precountJLabel.setFont(Fonts.b16);
			downBarPanel.add(precountJLabel);
			downBarPanel.add(Box.createRigidArea(new Dimension(10,0)));
			preCountButton = new JButton("Pick A New Cell");
			preCountButton.setFont(Fonts.b16);
			preCountButton.setMaximumSize(new Dimension(180, 28));
			preCountButton.setMinimumSize(new Dimension(180, 28));
			preCountButton.setPreferredSize(new Dimension(180, 28));
			preCountButton.setToolTipText("Precount cells for selected markinglayer" );
			preCountButton.setFocusable(false);
			preCountButton.setEnabled(false);
			preCountButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					startStopCellPicking();

				}
			});
		//	addMouseListenerForJButton(preCountButton);
			MouseListenerCreator.addMouseListenerToNormalButtons(preCountButton);
			

			downBarPanel.add(preCountButton);
			downBarPanel.add(Box.createRigidArea(new Dimension(20,0)));

			// layers
			layers = new JLayeredPane();
			layers.setLayout(null);
			layers.setBorder(BorderFactory.createEmptyBorder());

			// create imagePanel which has no any image in startup
			imagePanel = new ImagePanel();
			setCursorOverLeftPanel(ID.CURSOR_DEFAULT);
			this.imagePanel.addMouseListener(guiListener);
			this.imagePanel.addMouseMotionListener(guiListener);
			this.imagePanel.addMouseWheelListener(guiListener);
			guiListener.addKeyInputMap(this.imagePanel, ID.IMAGE_PANEL);
			// markingPanels are added dynamically when user adds new marking layers + markingPanels
			// add ImagePanel to layers
			layers.add(imagePanel,JLayeredPane.DEFAULT_LAYER);
			gridPanel = new GridPanel();
			layers.add(gridPanel, JLayeredPane.DRAG_LAYER);
			highlightPanel = new HighlightPanel();
			layers.add(highlightPanel, JLayeredPane.DRAG_LAYER);

			// attach panels
			visualPanel.add(layers);
			leftPanel.add(visualPanel,BorderLayout.CENTER);
			leftPanel.add(downBarPanel,BorderLayout.PAGE_END);

			doublePanel.setLeftComponent(leftPanel);

			// setup right side of JPlitPane
			rightPanel = new JPanel();
			rightPanel.setBackground(Color_schema.imagePanel_bg);
			rightPanel.setLayout(new BorderLayout(1,1));
			rightPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

			// add filedropping funtionality
			  new  FileDrop( rightPanel, new FileDrop.Listener()
			  {   public void  filesDropped( java.io.File[] files )
			      {
			          // handle file drop: open images
			         LOGGER.fine("File(s) dropped on GUI");
			         openAddImageLayerDialog(files); // open dialog with no given imagefiles
			      }   // end filesDropped
			  }); // end FileDrop.Listener

			  this.guiListener.addKeyInputMap(this.rightPanel, ID.RIGHT_PANEL);

			  // panel containing "LAYERS" -JLabel
			  JPanel layersLabelJPanel = new JPanel();
			  layersLabelJPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			  layersLabelJPanel.setBackground(Color_schema.imagePanel_bg);


			  JLabel layersJLabel = new JLabel("LAYERS");
			  layersJLabel.setFont(new Font("Consolas", Font.PLAIN,20));
			  layersJLabel.setForeground(Color_schema.white_230);
			 layersLabelJPanel.add(layersJLabel);

			// create JPanel for LayerInfos
				layerInfoListJPanel = new JPanel();
				layerInfoListJPanel.setBackground(Color_schema.imagePanel_bg);
				layerInfoListJPanel.setLayout(new BoxLayout(layerInfoListJPanel,BoxLayout.PAGE_AXIS));

			// create scrollpane for LayerInfos and add panel into it
			JScrollPane layersListJScrollPane = new JScrollPane(layerInfoListJPanel);
			layersListJScrollPane.setBackground(Color_schema.imagePanel_bg);
			layersListJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			layersListJScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			layersListJScrollPane.getVerticalScrollBar().setUnitIncrement(16);

			//attach Panels to right side panel of JSplittedPane
			rightPanel.add(layersLabelJPanel, BorderLayout.PAGE_START);
			rightPanel.add(layersListJScrollPane,BorderLayout.CENTER);
			rightPanel.setMinimumSize(new Dimension(280,75));
		
			initAddImageLayerButton();
			layerInfoListJPanel.add(addImageLayerJPanel,BorderLayout.PAGE_END);
			doublePanel.setRightComponent(rightPanel); // set the right component of JSplittedPane
			this.add(doublePanel);

		}catch(Exception e){
			LOGGER.severe("Error in initializing splitpane: " +e.getMessage());
			throw new Exception();
		}

	}

	/**
	 * Initializes GUI JFrame size, style and listeners.
	 * @throws Exception
	 */
	private void initWindowPropertiesAndListeners() throws Exception{

		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // maybe never used
			// change colors of menus and tooltips
			
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			
			UIManager.put("Button.background", Color_schema.dark_20);
			UIManager.put("Button.border", BorderFactory.createLineBorder(Color_schema.button_grey_border, 2));
			UIManager.put("Button.font", new Font("Consolas", Font.BOLD,16));
			UIManager.put("Button.foreground", Color_schema.white_230);			
			UIManager.put("RadioButton.background", Color_schema.dark_30);
			UIManager.put("RadioButton.font", new Font("Consolas", Font.BOLD,16));
			UIManager.put("RadioButton.foreground", Color_schema.white_230);
			UIManager.put("Button.select", Color_schema.orange_bright);
			UIManager.put("CheckBox.background", Color_schema.dark_40);
			UIManager.put("CheckBox.border", BorderFactory.createLineBorder(Color_schema.white_230, 1));
			UIManager.put("CheckBox.foreground", Color_schema.white_230);
			UIManager.put("CheckBox.interiorBackground", Color_schema.dark_40);
			UIManager.put("CheckBox.select", Color_schema.white_230);
			UIManager.put("ComboBox.selectionBackground", Color_schema.orange_medium);
			UIManager.put("ComboBox.selectionBackground", Color_schema.orange_medium);
			UIManager.put("Label.foreground", Color_schema.white_230);
			UIManager.put("Menu.background", Color_schema.dark_50);
			UIManager.put("Menu.selectionForeground", Color_schema.dark_50);
			UIManager.put("Menu.font", Fonts.b18);
			UIManager.put("Menu.foreground", Color_schema.white_230);
			UIManager.put("Menu.selectionBackground", Color_schema.menu_selection_bg);
			UIManager.put("MenuBar.selectionBackground", Color_schema.menu_selection_bg);
			UIManager.put("MenuBar.highlight", Color_schema.dark_50);
			UIManager.put("MenuBar.background", Color_schema.dark_50);
			UIManager.put("MenuBar.disabledForeground", Color_schema.white_230);
			UIManager.put("MenuBar.disabledBackground", Color_schema.dark_50);
			UIManager.put("PopupMenu.background", Color_schema.dark_50);
			UIManager.put("PopupMenu.font", Fonts.b16);
			UIManager.put("PopupMenu.foreground", Color_schema.white_230);
			UIManager.put("MenuBar.foreground", Color_schema.dark_100);
			UIManager.put("MenuItem.background", Color_schema.dark_50);
			UIManager.put("MenuItem.font", Fonts.b16);
			UIManager.put("MenuItem.foreground", Color_schema.white_230);
			UIManager.put("MenuItem.selectionBackground", Color_schema.menu_selection_bg) ; //Color_schema.color_menu_selection_bg);
			UIManager.put("CheckBoxMenuItem.background", Color_schema.dark_50);
			UIManager.put("CheckBoxMenuItem.font", Fonts.b16);
			UIManager.put("CheckBoxMenuItem.checkIcon", new CheckBoxIcon() );
			UIManager.put("CheckBoxMenuItem.foreground", Color_schema.white_230);
			UIManager.put("CheckBoxMenuItem.selectionBackground", Color_schema.menu_selection_bg) ; //Color_schema.color_menu_selection_bg);
			UIManager.put("Panel.background", Color_schema.dark_40);
			UIManager.put("ScrollBar.highlight", Color_schema.menu_selection_bg);
			UIManager.put("ScrollPane.background", Color_schema.dark_30);
			UIManager.put("Slider.background", Color_schema.dark_40);
			UIManager.put("SplitPane.dividerFocusColor", Color_schema.menu_selection_bg);
			UIManager.put("TabbedPane.background", Color_schema.dark_30);
			UIManager.put("TabbedPane.focus", Color_schema.button_orange_border);
			UIManager.put("TabbedPane.foreground", Color_schema.button_light_border);
			UIManager.put("TabbedPane.highlight", Color_schema.dark_30);
			UIManager.put("TabbedPane.selected", Color_schema.orange_dark);
			UIManager.put("TextField.inactiveForeground", Color_schema.white_230);
			UIManager.put("ToolTip.background", Color_schema.tooltip_bg);
			UIManager.put("ToolTip.font", Fonts.b14);
			
	
			guiListener = new GUIListener(this);

			guiComponentListener = new GUIcomponentListener(this);
			
			// add listener for resizing the JFrame -> resize the sizes of Splittedpane
			this.addComponentListener(guiComponentListener);
			
		
			
			//Close the program when window closed by the user
			this.addWindowListener(new WindowAdapter() {
			      public void windowClosing(WindowEvent e) {
			        // start saving process
			    	closeProgram();

			      }
			    });
			// key listeners		
			KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
	        manager.addKeyEventDispatcher(new MCkeyDispatcher(this.guiListener));
	       
			//set window normal and minimum size and position before maximization
			this.setSize(screenSize.width/2, screenSize.height/2);
			if(screenSize.width<800)
				this.setMinimumSize(new Dimension(screenSize.width-50,600));
			else
				this.setMinimumSize(new Dimension(800,600));
			
			this.setLocationRelativeTo(null);
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			
			// set window maximazed
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
			this.setTitle("MC-Cone - Manual Cell Counter");

			// set icon
			ImageIcon img = getImageIcon( "/images/MCcone_Small.png");
			this.setIconImage(img.getImage());
			
		} catch (Exception e) {
			LOGGER.severe("Error in setting GUI properties and listeners: " +e.getMessage());
			throw new Exception();
		}

	}

	/**
	 * Checks that given image file dimension is same as the dimension of previously used images. ONLY ONE IMAGE DIMENSION IS ALLOWED.
	 *
	 * @param file File image file which size is determined
	 * @return boolean true if allowed dimension. Otherwise false.
	 * @throws Exception the exception
	 */
	public boolean isAllowedImageDimension(File file) throws Exception{
		return this.taskManager.isAllowedImageDimension(file);
	}
	

	/**
	 *  Starts the progress to add ImageLayers or import MarkingLayers to ImageLayers if any ImageLayer present: User gives the image and/or markings from file in new Dialog window.
	 * ImageLayer(s) are created or updated and when done, GUI layers, ImagePanel and ImageLayerInfo is updated
	 */
	public void manageImageLayersAndMarkings(){
		try {

			// open dialog for selecting files
		//	JFrame dialogFrame = new JFrame("DialogFrame");
		
			AddImageLayerDialog addImage = new AddImageLayerDialog(this, this, taskManager.getImageLayerList());
			this.guiComponentListener.setChildDialog(addImage);
			addImage.showDialog();
			this.guiComponentListener.setChildDialog(null);
			addImage=null;
		} catch (Exception e) {
			LOGGER.severe("Error in adding new ImageLayer:  " +e.getClass().toString() + " :" +e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 *  Starts the progress to add one ImageLayer: User gives the image and possible markings from file in new Dialog window.
	 * ImageLayer(s) are created and when done, GUI layers, ImagePanel and ImageLayerInfo is updated
	 *
	 * @param fileList the list of files
	 */
	public void openAddImageLayerDialog(File[] fileList){
		try {
			if(fileList != null){
			
				// open dialog for selecting files	
				@SuppressWarnings("unused")
				AddImageLayerDialog addImage = new AddImageLayerDialog(this, getGUI(), fileList);
				
				addImage=null;
			}
			else{
			//	JFrame dialogFrame = new JFrame("DialogFrame");
				
				AddImageLayerDialog addImageDialog = new AddImageLayerDialog(this, this);
				this.guiComponentListener.setChildDialog(addImageDialog);
				addImageDialog.showDialog();
				addImageDialog=null;
				this.guiComponentListener.setChildDialog(null);
			}

		} catch (Exception e) {
			LOGGER.severe("Error in adding new ImageLayer:  " +e.getClass().toString() + " :" +e.getMessage());
		}
	}

	/**
	 *  Repaints the layers: ImagePanel, MarkingPanels, HightlightPanel, GridPanel, GlassPanel.
	 */
	public void paintLayers(){
		try {
			this.layers.repaint();
		} catch (Exception e) {
			LOGGER.severe("Error in painting Layers of GUI!");
			e.printStackTrace();
		}
	}

	/**
	 *  Checks does selected ImageLayer and visible MarkingLayers exist and after that updates GUI.
	 */
	public void refreshLayersAndGUI(){

		try {
			// check the selected ImageLayer and if no one selected -> select the first one in list
			this.taskManager.setSelectedImageLayerIfNotExist();
			// update the BufferedImage of LayerVisualManager
			this.taskManager.updateImageOfSelectedImageLayer();
			// Update GUI: ImageLayerInfos
			updateImageLayerInfos();
			// update markings of Highlightlayer
			setMarkingsOfHighlightLayer();

			// update the BufferedImage of ImagePanel
			this.imagePanel.setImage(this.taskManager.getRefreshedImage());
			this.imagePanel.repaint();

			// update markingLayers
			refreshMarkingPanels();
				
			setMenuItemsEnabled(ID.IMAGELAYERS);
			
			
		} catch (Exception e) {
			LOGGER.severe("Error in refreshing Layers and GUI components: "+e.getMessage());
			e.printStackTrace();
		}

	}



	/**
	 * Refresh the all MarkingPanels by first removing all layer and then recreating them again. 
	 * updates: imagePanel markingPanels, gridPanel and highlightPanel.
	 * Sets the selected MarkingPanel to front of layers.
	 */
	private void refreshMarkingPanels(){

		try {
			// remove panels from JLayeredPane
			this.layers.removeAll();
			// add imagePanel, gridPanel and highlightPanel
			this.layers.add(this.imagePanel, JLayeredPane.DEFAULT_LAYER);
			this.layers.add(this.gridPanel, JLayeredPane.DRAG_LAYER);
			this.layers.add(this.highlightPanel, JLayeredPane.DRAG_LAYER);

			// get all MarkingLayers
			ArrayList<MarkingLayer> mLayers = this.taskManager.getAllMarkingLayers();
			Iterator<MarkingLayer> iterator = mLayers.iterator();
			while (iterator.hasNext()) {
				MarkingLayer markingLayer = (MarkingLayer) iterator.next();
				MarkingPanel p=new MarkingPanel(markingLayer);
				p.setBounds(this.imagePanel.getBounds()); // set the MarkingPanel size and position same as ImagePanel

				// add new MarkingPanel to JLayeredPane
				this.layers.add(p, JLayeredPane.DRAG_LAYER);
				// if ImageLayer of MarkingPanel is selected -> set the new MarkingPanel front of panels
			}
			this.highlightPanel.setBounds(this.imagePanel.getBounds());
			this.gridPanel.setBounds(this.imagePanel.getBounds());
			updateGridPanel();
			setSelectedMarkingPanelToFront();
			updateCoordinatesOfVisibleMarkingPanels();
			updateCoordinatesOfSelectedMarkingPanel();
			
			//paint layers
			this.layers.repaint();


		} catch (Exception e) {
			LOGGER.severe("Error in removing MarkingPanel: " +e.getMessage());
		}

	}

	/**
	 * Hides the highlight of marking.
	 *
	 * @throws Exception the exception
	 */
	public void removeHighLightPoint() throws Exception{
		this.highlightPanel.updateHighlightPoint(null);

	}



	/**
	 * Removes the ImageLayer from list of ImageLayers at informationCenter. 
	 * Removes also the MarkingPanels corresponding to MarkingLayers of removed ImageLayer.Confirm dialog is shown to confirm removing.
	 * @param layerID int ID of ImageLayer, which is removed.
	 * @param layerName String name of ImageLayer.
	 */
	public void removeImageLayer(int layerID, String layerName){
		try
		{
			// ask the user should the ImageLayer being deleted
			ShadyMessageDialog dialog = new ShadyMessageDialog(this, "DELETE", "Delete ImageLayer:  "+layerName, ID.YES_NO, this);
			if(dialog.showDialog() == ID.YES){ // confirmed deleting the ImageLayer

			ArrayList<Integer> mLayersToRemove = taskManager.getImageLayerByID(layerID).getAllMarkingLayerIDs();

			// remove the imagelayer from list of InformationCenter
			taskManager.removeImageLayer(layerID);

			// remove markingPanels
			Iterator<Integer> mIDiterator = mLayersToRemove.iterator();
			while(mIDiterator.hasNext()){
				removeMarkingPanelByLayerID(mIDiterator.next());
			}

			// update imagelayerinfos
			updateImageLayerInfos();
			// refreshes the BufferedImage of LayerVisualManager
			this.taskManager.updateImageOfSelectedImageLayer();

			// update ImageLayer: if no image found null is returned and only background is paint in ImagePanel
			this.imagePanel.setImage(this.taskManager.getRefreshedImage());
				this.imagePanel.repaint();

			cleanPreCountingIfNecessary();
			setMenuItemsEnabled(ID.IMAGELAYERS);
			}

			dialog=null;
		} catch (HeadlessException e) {	
			LOGGER.severe("Error in removint ImageLayer: "+e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.severe("Error in removint ImageLayer: "+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Removes the MarkingLayer and corresponding MarkingPanel by given MarkingLayer ID. 
	 * Confirm dialog will be shown. ImageLayerInfo will be updated.
	 *
	 * @param imageLayerID int ID of ImageLayer which MarkingLayer is removed.
	 * @param mLayerID int ID of MarkingLayer, which is removed .
	 * @param markingLayerName the marking layer name
	 */
	public void removeMarkingLayer(int imageLayerID, int mLayerID, String markingLayerName){


		try {
			// ask the user should the MarkingLayer being deleted
			ShadyMessageDialog dialog = new ShadyMessageDialog(this, "DELETE", "Delete MarkingLayer:  "+markingLayerName, ID.YES_NO, this);
			if(dialog.showDialog() == ID.YES){
				// remove the MarkingLayer from list of InformationCenter
				taskManager.removeMarkingLayer(imageLayerID, mLayerID);
				// remove MarkingPanel
				removeMarkingPanelByLayerID(mLayerID);
	
				// update imagelayerinfos
				updateImageLayerInfos();
	
				// refresh precounting components
				cleanPreCountingIfNecessary();
				
				setMenuItemsEnabled(ID.MARKINGLAYERS);
				
				// refresh visibility of selected MarkingLayer -> MarkingPanel markings and GRID will be visible
				MarkingLayer layer= this.taskManager.getSelectedMarkingLayer();
				if(layer != null && layer.isVisible()){
					this.setMarkingLayerVisibility(layer.getLayerID(), true);
										
				}
				
			}
			dialog=null;
		} catch (Exception e) {
			LOGGER.severe("Error in removing MarkingLayer: " +e.getMessage());
		}
	}

	/**
	 * Fetches and removes a MarkingPanel corresponding to given MarkingLayer ID. 
	 * Removes first all layers and add all but MarkingPanel which corresponds to given MarkingLayer ID.
	 * @param mLayerID int ID of MarkingLayer, which MarkingPanel removed.
	 */
	private void removeMarkingPanelByLayerID(int mLayerID){

		try {

			MarkingPanel removePanel=getMarkingPanelByLayerID(mLayerID);
			this.layers.remove(removePanel);
			removePanel=null;
			// save panel-components to list
			Component[] mPanels=this.layers.getComponents();


			// ImageLayer is the first panel and array length is at least 1 + number of markingpanels
			if(mPanels != null && mPanels.length>0){
				// remove panels from JLayeredPane

				this.layers.removeAll();

				// go trough panels
				for(int i=0;i<mPanels.length;i++){ // ImagePanel is the first layer
					if(mPanels[i] instanceof MarkingPanel){

						if(((MarkingPanel)mPanels[i]).getId() != mLayerID ){ // this should never happen because removed in beginning of method
							this.layers.add(mPanels[i], JLayeredPane.DRAG_LAYER);
						}

					}
					else
						if(mPanels[i] instanceof ImagePanel){ // add ImagePanel
							this.layers.add(mPanels[i],JLayeredPane.DEFAULT_LAYER);
						}
						else
							if(mPanels[i] instanceof HighlightPanel){ // add HighlightPanel
								this.layers.add(mPanels[i],JLayeredPane.DRAG_LAYER);
							}
							else
								if(mPanels[i] instanceof GridPanel){ // add GridPanel
									this.layers.add(mPanels[i],JLayeredPane.DRAG_LAYER);
								}

				}
				mPanels=null;
				//move the MarkingPanel corresponding to selectedMarkingLayer to front of MarkingPanels
				setSelectedMarkingPanelToFront();

				// update MarkingLayer of highlightLayer
				this.highlightPanel.setLayer(this.taskManager.getSelectedMarkingLayer());
				//update grid
				updateGridPanel();

				//paint layers
				this.layers.repaint();

			}
		} catch (Exception e) {
			LOGGER.severe("Error in removing MarkingPanel: " +e.getMessage());
		}

	}


	/**
	 * Removes a single marking closest to Point p. If no any marking found close enough, no any removed. 
	 *
	 * @param p Point at screen where right mouse button was pressed
	 * @return boolean true if marking was removed; otherwise false
	 * @throws Exception the exception
	 */
	public boolean removeSingleMarking(Point p) throws Exception{
		Point panelPoint =getClosestMarkingPointAtScreen(p, SharedVariables.DISTANCE_TO_REMOVE);
		if(panelPoint != null)
			return taskManager.removeSingleMarking(panelPoint);
		return false;
	}

	/**
	 * Resizes components of graphical interface
	 */
	protected void resizeLayerComponents(){
		try{
			 layers.setBounds(5,5,(int)visualPanel.getBounds().getWidth()-10, (int)visualPanel.getBounds().getHeight()-10);
			 layers.revalidate();
			 imagePanel.setBounds(0,0,(int)layers.getBounds().getWidth(),(int)layers.getBounds().getHeight());
			 this.highlightPanel.setBounds(this.imagePanel.getBounds());
			 this.gridPanel.setBounds(this.imagePanel.getBounds());
			 Component[] comps= layers.getComponents();
			 for(int i=0;i<comps.length;i++){
				 if(comps[i] instanceof MarkingPanel){
					 LOGGER.fine("updated MarkingPanel Bounds");
					 ((MarkingPanel)comps[i]).setBounds(imagePanel.getBounds());
				 }
			 }
	
			  updateImagePanelSize();
	
			  imagePanel.setImage(taskManager.getRefreshedImage());
			  updateCoordinatesOfVisibleMarkingPanels();
			  updateGridPanel();
			  removeHighLightPoint();
	         layers.repaint();
	         updateImageLayerInfos();
	         
		}catch(Exception e){
			LOGGER.severe("Error in resizing maing window "+e.getMessage());
		}
	}

	/**
	 *  Opens dialog for selecting which markings to save.
	 *
	 * @throws Exception the exception
	 */
	public void saveMarkings() throws Exception{

		if(taskManager.getImageLayerList() != null && taskManager.getImageLayerList().size()>0){

			@SuppressWarnings("unused")
			SaveMarkings saveMarkingsDialog=new SaveMarkings(this, this, this.taskManager.getImageLayerList());
			
			saveMarkingsDialog=null;
		}
		else{
			showMessage( "Not starting saving", "Not saved markings, because no markings were found", ID.OK);

		}

	}



	/**
	 * Sets Cursor type when hovering over ImagePanel.
	 *
	 * @param typeOfCursor int ID of type of Cursor
	 * @throws Exception the exception
	 */
	public void setCursorOverLeftPanel(int typeOfCursor) throws Exception{
		switch(typeOfCursor){
			case ID.CURSOR_DEFAULT:
				this.imagePanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				if(this.taskManager != null && this.taskManager.getSelectedMarkingLayer() != null && this.taskManager.getSelectedMarkingLayer().getLayerID()>0)
					getMarkingPanelByLayerID(this.taskManager.getSelectedMarkingLayer().getLayerID()).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				break;
			case ID.CURSOR_HAND:
				
				this.imagePanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				if(this.taskManager != null && this.taskManager.getSelectedMarkingLayer() != null && this.taskManager.getSelectedMarkingLayer().getLayerID()>0)
					getMarkingPanelByLayerID(this.taskManager.getSelectedMarkingLayer().getLayerID()).setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));			
				break;
			case ID.CURSOR_CROSS_HAIR:
								
				this.imagePanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				if(this.taskManager != null && this.taskManager.getSelectedMarkingLayer() != null && this.taskManager.getSelectedMarkingLayer().getLayerID()>0)
				getMarkingPanelByLayerID(this.taskManager.getSelectedMarkingLayer().getLayerID()).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));			
				break;
				
		}
		
	}

	/**
	 * When user presses mouse on a Grid cell (SHIFT key pressed down at same time) this method sets the Grid cell as selected or unselected.
	 *
	 * @param p Point where mouse was pressed
	 * @throws Exception the exception
	 */
	public void setGridSelectedRectangle(Point p) throws Exception{
		this.taskManager.changeGridCellSelection(p);
		updateGridPanel();

		updateCoordinatesOfSelectedMarkingPanel();
		removeHighLightPoint();
	}

	/**
	 * Sets the PositionedImage to ImagePanel, which is painted.
	 *
	 * @param pi PositionedImage the image to set to ImagePanel.
	 * @throws Exception the exception
	 */
	public void setImage(PositionedImage pi) throws Exception{
		this.imagePanel.setImage(pi);
	}

	/**
	 * Replaces the array of ImageLayer s in InformationCenter with new list. After updating the list: the GUI is updated.
	 *
	 * @param iLayerList Array of ImageLayer s.
	 * @throws Exception the exception
	 */
	public void setImageLayerList(ArrayList<ImageLayer> iLayerList) throws Exception{
		this.taskManager.setImageLayerList(iLayerList); // replaces the existing ImageLayerList with new one finally in InformationCenter

		//updates the selected Layers and Refreshes GUI components and image
		refreshLayersAndGUI();

		// refresh precounting components
		cleanPreCountingIfNecessary();

	}



	
	/**
	 * Sets the successfully made savings to MarkingLayers.
	 *
	 * @param mLayerIDs the ids of MarkingLayers where successfully made savings
	 */
	public void setSuccessfullyMadeSavings(ArrayList<Integer> mLayerIDs){
		try {
			this.taskManager.setSuccessfullyMadeSavings(mLayerIDs);
		} catch (Exception e) {
			LOGGER.severe("Error in setting successfully made savings to ImageLayers and MarkingLayers!");
			e.printStackTrace();
		}
		
	}




	/**
	 * Modifies the name of MarkingLayer given by ID.
	 *
	 * @param iLayerID int ID of ImageLayer which MarkingLayeris modified.
	 * @param mLayerID int ID of MarkingLayer, which name is modified.
	 * @param markingName String the new name.
	 * @throws Exception the exception
	 */
	public void setMarkingLayerName(int iLayerID, int mLayerID, String markingName) throws Exception{
		taskManager.setMarkingLayerName(iLayerID, mLayerID, markingName);
	}

	/**
	 * Sets visibility of MarkingLayer.
	 *
	 * @param mLayerID int ID of MarkingLayer, which visibility is modified.
	 * @param visible boolean true if visible and false to invisible.
	 * @throws Exception the exception
	 */
	public void setMarkingLayerVisibility(int mLayerID, Boolean visible) throws Exception{
		// add or remove MarkingLayer from InformationCenter.visibleMarkingLayers
		taskManager.setMarkingLayerVisibility(mLayerID, visible);

		// set the visibility to MarkingPanel of MarkingLayer
		getMarkingPanelByLayerID(mLayerID).setVisible(visible);
		updateCoordinatesOfVisibleMarkingPanels();
		// if is selectedmarkingLayer and grid is on -> hide also grid
		if(taskManager.isSelectedMarkingLayer(mLayerID)){
			if(taskManager.getSelectedMarkingLayer().isGridON()){
				this.gridPanel.setShowGrid(visible);
			}
			// if setting visible or unvisible -> set highlight unvisible (no data in highlight)
				setMarkingsOfHighlightLayer();
			
			
		}
		this.layers.repaint();

	}

	/**
	 * Sets the selected MarkingLayer to highlightPanel -> HighlightPanel uses information of that MarkingLayer for highlighting.
	 *
	 * @throws Exception the exception
	 */
	private void setMarkingsOfHighlightLayer() throws Exception {
		MarkingLayer selectedMarkingLayer= taskManager.getSelectedMarkingLayer();
		this.highlightPanel.setLayer(selectedMarkingLayer);
	}
	
	/**
	 * Sets the menu items enabled or unenabled depending on has ImageLayers or MarkingLayers available.
	 *
	 * @param itemType the new menu items enabled
	 * @throws Exception the exception
	 */
	public void setMenuItemsEnabled(int itemType) throws Exception{
		
		// both imageLayers and MarkingLayers
		if(itemType==ID.MARKINGLAYERS || itemType==ID.IMAGELAYERS ){
			if(this.taskManager.hasAnyMarkinglayers()){
				this.menu_item_save_markings.setEnabled(true);
				this.menu_export_results.setEnabled(true);
				this.menu_edit.setEnabled(true);
				this.menu_edit_set_marking_properties.setEnabled(true);
				this.menu_edit_clear_all_countings.setEnabled(true);
				this.menu_edit_clear_single_countings.setEnabled(true);
				this.menu_show.setEnabled(true);
				this.menu_show_all_markings.setEnabled(true);
				this.menu_hide_all_markings.setEnabled(true);
				this.menu_grid.setEnabled(true);
							
			}
			else
			{
				this.menu_grid.setEnabled(false);
				this.menu_item_save_markings.setEnabled(false);
				this.menu_export_results.setEnabled(false);
				this.menu_edit.setEnabled(false);
				this.menu_edit_set_marking_properties.setEnabled(false);
				this.menu_edit_clear_all_countings.setEnabled(false);
				this.menu_edit_clear_single_countings.setEnabled(false);
				this.menu_show.setEnabled(false);
				this.menu_show_all_markings.setEnabled(false);
				this.menu_hide_all_markings.setEnabled(false);
						
			}
		}
		// ImageLayers -> set also export images 
		if(itemType== ID.IMAGELAYERS){
			if(taskManager.hasAnyImageLayers()){
				this.menu_export_images.setEnabled(true);
				this.zoomSlider.setEnabled(true);
				this.zoomInButton.setEnabled(true);
				this.zoomOutButton.setEnabled(true);
			}
			else{
				this.menu_export_images.setEnabled(false);
				this.zoomSlider.setEnabled(false);
				this.zoomInButton.setEnabled(false);
				this.zoomOutButton.setEnabled(false);
				
			}
		}	
	}

/**
 * Sets the folder name which is previously used.
 *
 * @param folder String folder name which is previously used.
 * @throws Exception the exception
 */
public void setPresentFolder(String folder) throws Exception{
	this.taskManager.setPresentFolder(folder);
}

/**
 * Opens a dialog to set size, thickness and transparency of all MarkingLayers if any MarkingLayers found. 
 *
 * @param guiPoint Point where mouse was pressed to menuitem.
 * @throws Exception the exception
 */
private void setPropertiesOfAllMarkinglayers(Point guiPoint) throws Exception{
	ArrayList<MarkingLayer> mLayerList=taskManager.getAllMarkingLayers();
	if(mLayerList != null && mLayerList.size()>0){
		GlobalMarkingProperties dialog = new GlobalMarkingProperties(this, getGUI(), guiPoint, mLayerList);
		this.guiComponentListener.setChildDialog(dialog);
		dialog.showDialog();
		this.guiComponentListener.setChildDialog(null);
	}
	else
		showMessage("No MarkingLayers", "No any MarkingLayer found for changing properties.", ID.OK);
}

/**
 * Updates properties (size, color, thickness ...) of all MarkingPanels by fetching updated properties from ImageLayer at InformationCenter.
 *
 * @throws Exception the exception
 */

private void setPropertiesOfAllMarkingPanels() throws Exception{
	// gothrough all markingPanels
	Component[] panelList=this.layers.getComponents();

	for(int i=0;i<panelList.length;i++){
		// ensure that only manipulate MarkingPanel objects
		if(panelList[i] instanceof MarkingPanel){
			int mLayerID=((MarkingPanel)panelList[i]).getId();
			MarkingLayer mLayer=taskManager.getMarkingLayer(mLayerID);
			if(mLayer != null){
				((MarkingPanel)panelList[i]).setMarkingPanelProperties(mLayer);
			}

		}
	}
	setMarkingsOfHighlightLayer();
}

/**
 * Updates properties (size, color, thickness ...) of MarkingPanel by fetching updated properties from ImageLayer at InformationCenter.
 *
 * @param mLayerID the ID of MarkingLayer
 * @throws Exception the exception
 */
private void setPropertiesOfMarkingPanel(int mLayerID) throws Exception{
	getMarkingPanelByLayerID(mLayerID).setMarkingPanelProperties(taskManager.getMarkingLayer(mLayerID));
	setMarkingsOfHighlightLayer();

}

/**
 * Updates the InformationCenter and LayerVisualManager for which ImageLayer is selected and which BufferedImage object is in LayerVisualManger.
 * Method updates the ImagePanel of GUI. This method is called always when selected (visible) ImageLayer is changed by user.
 * If ImageLayer is already selected is the visibility of  MarkingLayers changed if this method is called by eye-icon. 
 *
 * @param iLayerID ID of ImageLayer that will be the selected ImageLayer (visible)
 * @param selectionChangeType the selection change type
 */
public void setSelectedImageLayerAndImage(int iLayerID, int selectionChangeType) {
	 try {
		 ImageLayer iLayer =this.taskManager.getImageLayerByID(iLayerID);
		 	boolean visible=iLayer.isVisibleMarkingLayers(); // has ImageLayer any visible MarkingLayers
		 if(selectionChangeType != ID.IMAGELAYER_REFRESH_VISIBILITY){	
		 	if(!(this.taskManager.getImageLayerByID(iLayerID).isSelected())){
		 		this.taskManager.changeSelectedImageLayer(iLayerID);
		
		 		// scale the image with best quality (in LayerVisualManager) and send it to ImagePanel
		 		this.imagePanel.setImage(this.taskManager.getRefreshedImage());

		 	}
		 	
		 }else
			setVisibilityOfAllMarkingLayersOfSingleImageLayer(!visible, iLayerID);
			 
		 
		//update highlight panel
		this.highlightPanel.setLayer(this.taskManager.getSelectedMarkingLayer());
		this.highlightPanel.updateHighlightPoint(null);
		// update information in ImageLayerInfo
		updateImageLayerInfos();
		// refresh markingpanels (repaints also the imagepanel)
		refreshMarkingPanels();

		// clean the precountingThreadManager
		cleanPreCountingIfNecessary();
	} catch (Exception e) {
		LOGGER.severe("Error in changing selected ImageLayer: "+e.getMessage());
		e.printStackTrace();
	}
}

/**
 * Sets the given MarkingLayer to visible. GridPanel and highlightPanel are updated. All layers repainted.
 *
 * @param mLayerID int ID of MarkingLayer, which is been selected.
 * @throws Exception the exception
 */
public void setSelectedMarkingLayer(int mLayerID) throws Exception{
	// set the MarkingLayer selected in InfromationCenter
	MarkingLayer selectedMarkingLayer= this.taskManager.setMarkingLayerSelected(mLayerID);
	if(selectedMarkingLayer != null){ // If markinglayer has found and set selected

		// set selected MarkingPanel to visible if it is hided
		if(!this.taskManager.getMarkingLayer(mLayerID).isVisible()){
			setMarkingLayerVisibility(mLayerID, true);
		}
		updateGridPanel();
		this.highlightPanel.setLayer(selectedMarkingLayer);
		this.highlightPanel.updateHighlightPoint(null);
		// set selected MarkingPanel to front of JLayeredPane
		setSelectedMarkingPanelToFront();
		// repaint panels because the order has been changed
		this.layers.repaint();
		// updated LayerInfos
		updateImageLayerInfos();

		this.taskManager.cleanPrecountingManager();

		//clean precountingThreadManager, because MarkingLayer changed
		cleanPreCountingIfNecessary();
	}
}



	/**
	 * Moves the MarkingLayer corresponding to selectedMarkingLayer (@see InformationCenter) to front of JLayeredPane.
	 *
	 * @throws Exception the exception
	 */
	private void setSelectedMarkingPanelToFront() throws Exception{

		MarkingLayer selectedMarkingLayer = this.taskManager.getSelectedMarkingLayer();
		if(selectedMarkingLayer !=null){
			MarkingPanel panelToFront=getMarkingPanelByLayerID(selectedMarkingLayer.getLayerID());
			if(panelToFront != null){
				this.layers.moveToFront(this.gridPanel);
				this.layers.moveToFront(this.highlightPanel); // set the hightlightPanel behind to frontPanel
				this.layers.moveToFront(panelToFront);
			}
		}
	}



	/**
	 *  Determines which operation system is running and sets Graphical parameters accordingly.
	 */
	private void setUpOSsharedVariables(){

		try {
			String osString=System.getProperty("os.name").toLowerCase();
			LOGGER.info("OS:"+osString);
			if(osString.contains("win")){
				SharedVariables.setUsedDimmingModeToSrcOver();
				SharedVariables.setOS(ID.OS_WINDOWS);
			}
			else{
				if(osString.contains("mac")){
					SharedVariables.setUsedDimmingModeToSrcOut();
					SharedVariables.setOS(ID.OS_MAC);
				}
				else{ // unix or linux
					SharedVariables.setUsedDimmingModeToSrcIn();
					SharedVariables.setOS(ID.OS_LINUX_UNIX);
				}
			}
		} catch (Exception e) {
			LOGGER.severe("Error in setting OS specific settings!");
			e.printStackTrace();
		}
	}

	/**
	 * Creates JPanel containing JSlider for changing numerical properties of MarkingLayer.
	 * @param minValue the minimum value of slider
	 * @param maxValue the maximum value of slider
	 * @param initValue the selected value of slider in the beginning
	 * @param minorTicks the minor thicks of slider
	 * @param majorTicks the major thicks of slider
	 * @return a JPanel containing title, slider and label showing selected value
	 */
	private JPanel setUpSLiderPanel(int minValue, int maxValue, int initValue, int minorTicks, int majorTicks){
		try {
			Font fontConsolasBOLD16 = new Font("Consolas", Font.BOLD,16);
			Font fontConsolasPLAIN16 = new Font("Consolas", Font.PLAIN,16);

			JLabel zoom_label = new JLabel("ZOOM: ");
			zoom_label.setFont(fontConsolasBOLD16);

			sliderPanel = new JPanel();	
			sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.LINE_AXIS));
			ImageIcon imgOut = getImageIcon( "/images/out.png");
		
			zoomOutButton = new JButton(imgOut);
			zoomOutButton.setBorder(BorderFactory.createEmptyBorder());
			zoomOutButton.setFocusable(false);
			zoomOutButton.addActionListener(new ActionListener() {
				
			@Override
			public void actionPerformed(ActionEvent e) {
					try {
						zoomAndUpdateImage(new Point((int)(imagePanel.getWidth()/2), (int)(imagePanel.getHeight()/2)), 0.8, ID.IMAGE_PROCESSING_BEST_QUALITY);
					} catch (Exception e1) {
						LOGGER.severe("Error in zooming out!");
						e1.printStackTrace();
					}

					
				}
			});

			ImageIcon imgIn = getImageIcon( "/images/in.png");		
			zoomInButton = new JButton(imgIn);
			zoomInButton.setBorder(BorderFactory.createEmptyBorder());
			zoomInButton.setFocusable(false);
			zoomInButton.addActionListener(new ActionListener() {			
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						zoomAndUpdateImage(new Point((int)(imagePanel.getWidth()/2), (int)(imagePanel.getHeight()/2)), 1.25, ID.IMAGE_PROCESSING_BEST_QUALITY);
					} catch (Exception e1) {
						LOGGER.severe("Error in zooming in");
						e1.printStackTrace();
					}					
				}
			});

			sliderPanel.setMaximumSize(new Dimension(5000,30));
			sliderPanel.setMinimumSize(new Dimension(200,25));

			zoomSlider = new JSlider(JSlider.HORIZONTAL,minValue,maxValue, initValue);

			zoomValueLabel = new JLabel(""+initValue);
			zoomValueLabel.putClientProperty("name", ID.NUMBER_LABEL); // by adding this can JLabels be identified in statechanged -listener
			zoomValueLabel.setFont(fontConsolasPLAIN16);

			JLabel zoomscrollProcent=new JLabel("%");
			zoomscrollProcent.setFont(new Font("Consolas", Font.BOLD,16));

			zoomSlider.setMajorTickSpacing(majorTicks);
			zoomSlider.setMinorTickSpacing(minorTicks);
	

			sliderPanel.add(Box.createRigidArea(new Dimension(3,0)));
			sliderPanel.add(zoom_label);
			sliderPanel.add(Box.createRigidArea(new Dimension(5,0)));
			sliderPanel.add(zoomOutButton);
			sliderPanel.add(Box.createRigidArea(new Dimension(5,0)));
			sliderPanel.add(zoomSlider);
			sliderPanel.add(Box.createRigidArea(new Dimension(5,0)));
			sliderPanel.add(zoomInButton);
		

			zoomSlider.addChangeListener(new ChangeListener() {

			private double sliderZoomValue;

			@Override
			public void stateChanged(ChangeEvent e) {

				try {
						// zooming
						if(!guiListener.isIs_SPACE_pressed() && sliderListener.isSlidingON() && !sliderListener.isSliderTimerRunning()){
							sliderListener.startSliderTimer();
							LOGGER.fine("slider moved+ "+((JSlider)e.getSource()).getValue());
							int valueNow=((JSlider)e.getSource()).getValue();
						//	if(valueNow % 25 == 0 ){
							int previousValue=sliderListener.getStartValue();
							int difference=previousValue-valueNow;
							sliderZoomValue = 0;
							if(difference != 0){
								sliderZoomValue=1.25;
								if(difference > 0)
									sliderZoomValue= 0.8;
								zoomValueLabel.setText(""+zoomSlider.getValue());
									sliderListener.setStartValue(valueNow);
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {
										try {
											zoomAndUpdateImage(new Point((int)(imagePanel.getWidth()/2), (int)(imagePanel.getHeight()/2)), sliderZoomValue, ID.IMAGE_PROCESSING_BEST_QUALITY);
										} catch (Exception e) {
											LOGGER.severe("Error in zooming image!");
											e.printStackTrace();
										}


									}
								});

								}
							}
					} catch (Exception e1) {
						LOGGER.severe("Error in Zoom Slider");
						e1.printStackTrace();
					}
				
				}
			});

			sliderListener = new SliderListener(zoomValueLabel);
			zoomSlider.addMouseListener(sliderListener);
			zoomSlider.setMaximumSize(new Dimension(400,25));
			zoomSlider.setPreferredSize(new Dimension(400,25));
			zoomSlider.setMinimumSize(new Dimension(100,20));

			return sliderPanel;
		} catch (Exception e) {
			LOGGER.severe("Error in initializing sliders: " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}
	}

	/**
	 * Sets visibility of all MarkingLayers.
	 *
	 * @param visible boolean true/false to set visibility
	 * @throws Exception the exception
	 */
	public void setVisibilityOfAllMarkingLayers(boolean visible) throws Exception{
		ArrayList<MarkingLayer> allMarkingLayers=this.taskManager.getAllMarkingLayers();
		if(allMarkingLayers != null && allMarkingLayers.size()>0){
			for (Iterator<MarkingLayer> iterator = allMarkingLayers.iterator(); iterator.hasNext();) {
				MarkingLayer markingLayer = (MarkingLayer) iterator.next();
				setMarkingLayerVisibility(markingLayer.getLayerID(), visible);
			}
			updateImageLayerInfos();
		}
		else{
			showMessage("No MarkingLayer!", "No any MarkingLayer found which visibility to change.", ID.OK);
		}
	
	}
	
	/**
	 * Sets visibility of all MarkingLayers of single ImageLayer. Not updates GUI. 
	 *
	 * @param visible boolean true/false to set visibility
	 * @param iLayerID the i layer id
	 * @throws Exception the exception
	 */
	public void setVisibilityOfAllMarkingLayersOfSingleImageLayer(boolean visible, int iLayerID) throws Exception{
		ImageLayer iLayer = this.taskManager.getImageLayerByID(iLayerID);
		if(iLayer != null){
			ArrayList<MarkingLayer> allMarkingLayers=iLayer.getMarkingLayers();
			if(allMarkingLayers != null && allMarkingLayers.size()>0){
				for (Iterator<MarkingLayer> iterator = allMarkingLayers.iterator(); iterator.hasNext();) {
					MarkingLayer markingLayer = (MarkingLayer) iterator.next();
					setMarkingLayerVisibility(markingLayer.getLayerID(), visible);
				}
			//	updateImageLayerInfos();
			}
			else{
				showMessage("No MarkingLayer!", "No any MarkingLayer found which visibility to change.", ID.OK);
			}
		}
	}
	
	


	/**
	 * Sets Grid visible if the Grid is selected as ON.
	 *
	 * @throws Exception the exception
	 */
	private void showGrid() throws Exception{

		this.gridPanel.setBasicTransparency();
		this.gridPanel.repaint();
	}

	/**
	 * Opens a GridProperties Panel Dialog for modifying the Grid Properties of all MarkingLayers in given list of MarkingLayers.
	 * @param point Point where mouse was pressed the JButton to call this method.
	 * @param mLayerList ArrayList of all MarkingLayers.
	 */
	public void showGridPropertiesPanel(Point point, ArrayList<MarkingLayer> mLayerList){
		
		try {
			if(mLayerList != null && mLayerList.size()>0){
				gridDialog = new GridPropertiesPanel(this, this, point, mLayerList, taskManager.getSingleGridSizeList());		
				this.guiComponentListener.setChildDialog(gridDialog);
				
				gridDialog .showDialog();
				if(gridDialog.isMade_changes()){
					int mLayerID = mLayerList.get(0).getLayerID();
					if(mLayerID>0){
						// set selected
						this.taskManager.setMarkingLayerSelected(mLayerID);
						this.taskManager.setMarkingLayerVisibility(mLayerID,true);
					}
					
					
				}
				updateGridPanel();						
				gridDialog =null;
				guiComponentListener.setChildDialog(null);

			}
			else{
					showMessage("No MarkingLayers", "No any MarkingLayers found. Can't set Properties of the Grid. ",ID.OK);
			}
		} catch (Exception e) {
			LOGGER.severe("Error in Opening GridPropertiesPanel: "+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Organizes opening a GridPropertiesDialog for modifying the Grid Properties of all MarkingLayers.
	 * Updates ImageLayerInfos and MarkingPanels.
	 *
	 * @param point Point where mouse was pressed the JButton to call this method.
	 * @param mLayerList ArrayList of all MarkingLayers.
	 * @throws Exception the exception
	 */
	public void showGridPropertiesPanelForAllMarkingLayers(Point point, ArrayList<MarkingLayer> mLayerList) throws Exception{
		showGridPropertiesPanel(point, mLayerList);
		updateImageLayerInfos();
		refreshMarkingPanels();
	}

	/**
	 * Organizes opening a GridPropertiesDialog for modifying the Grid Properties of all MarkingLayers under Single ImageLayer.
	 * Updates ImageLayerInfos and MarkingPanels.
	 *
	 * @param point Point where mouse was pressed the JButton to call this method.
	 * @param iLayerID int ID of ImageLayer which MarkingLayers (GridProperties) are modified.
	 * @throws Exception the exception
	 */
	public void showGridPropertiesPanelForMarkingLayersOfImageLayer(Point point, int iLayerID) throws Exception{
		ImageLayer iLayer= this.taskManager.getImageLayerByID(iLayerID);
		if(iLayer!= null){
			ArrayList<MarkingLayer> mLayerList=iLayer.getMarkingLayers();
			if(mLayerList != null && mLayerList.size()>0){
				showGridPropertiesPanel(point, mLayerList);
			}
			else{
				showMessage("No MarkingLayer!", "No any MarkingLayer were found for changing properties of grid", ID.OK);
			}
		}
		iLayer=null;
		updateImageLayerInfos();
		refreshMarkingPanels();

	}

	/**
	 * Organizes opening a GridPropertiesDialog for modifying the Grid Properties of Single MarkingLayer.
	 * Updates ImageLayerInfos and MarkingPanels.
	 *
	 * @param point Point where mouse was pressed the JButton to call this method.
	 * @param mLayerID int ID of MarkingLayer which GridProperty is modified.
	 * @throws Exception the exception
	 */
	public void showGridPropertiesPanelForSingleMarkingLayer(Point point, int mLayerID) throws Exception{
		ArrayList<MarkingLayer> mLayerList = new ArrayList<MarkingLayer>();
		MarkingLayer mlayer=taskManager.getMarkingLayer(mLayerID);

		if(mlayer != null ){
			mLayerList.add(mlayer);
			if(mLayerList != null && mLayerList.size()>0){
				showGridPropertiesPanel(point, mLayerList);
			}
		}
		else{
			showMessage("No MarkingLayer!", "No any MarkingLayer were found for changing properties of grid.", ID.OK);
		}
		mlayer=null;
		mLayerList=null;
		
		updateImageLayerInfos();
		refreshMarkingPanels();
	}

	/**
	 * Opens info dialog showing information of MC-Cone.
	 *
	 * @param p Point where menu item was pressed.
	 * @throws Exception the exception
	 */
	private void showInfo(Point p) throws Exception{
		InfoDialog iDialog = new InfoDialog(this, this,p);
		this.guiComponentListener.setChildDialog(iDialog);
		iDialog.showDialog();
		this.guiComponentListener.setChildDialog(null);
	}



	/**
	 * Opens a message dialog.
	 * @param title String title of message.
	 * @param message String message.
	 * @param id int ID of type of buttons in message dialog.
	 */
	public void showMessage(String title, String message, int id){
		try {
			ShadyMessageDialog dialog = new ShadyMessageDialog(this, title, message, id, this);
			dialog.showDialog();
			dialog=null;
		} catch (Exception e) {
			LOGGER.severe("Error in showing message!");
			e.printStackTrace();
		}

	}

	/**
	 *  Opens web browser and web site http://www.mc-cone.com/web_tutorial.html. If Operation system doesn't allow opening web browere
	 *
	 * @throws Exception the exception
	 */
	private void showWebInstructions() throws Exception{
		String url ="http://mc-cone.com/instructions/";
		 if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)){
	            Desktop desktop = Desktop.getDesktop();
	            try {
	                desktop.browse(new URI(url));
	            } catch (IOException | URISyntaxException e) {
	            	showMessage("Instructions!", "Can't open the link. Possibly not supported by Operation system!", ID.OK);
					LOGGER.severe("Can't open the link. Not supported by Operation system!");
	                e.printStackTrace();
	            }
	        }else{
	            Runtime runtime = Runtime.getRuntime();
	            try {
	                runtime.exec("xdg-open " + url);
	            } catch (IOException e) {
	            	showMessage("Instructions!", "Can't open the link. Possibly not supported by Operation system!", ID.OK);
					LOGGER.severe("Can't open the link. Not supported by Operation system!");
	                e.printStackTrace();
	            }
	        }
	
	}


	/**
	 * Starts the precounting, opens a progress bar and calculates the cell positions.
	 *
	 * @param imagePanelPoint Middle Point of cell that user has picked
	 * @param size Size of picked cell
	 * @throws Exception the exception
	 */
	public void startCellCounting(Point imagePanelPoint, int size) throws Exception{

		// stop the picking and hides glasspane
		startStopCellPicking();

		// creates the ProgressBallsDialog
		ProgressBallsDialog pbd= new ProgressBallsDialog(this, "Counting Cells", "running part 1/2: finding pixels", ID.CANCEL, this);

		pbd.showDialog();

		// start counting
		this.taskManager.precountCells(imagePanelPoint, size, pbd);
		pbd=null;
	}

	/**
	 *  If cell picking thread is not running this method starts it. If the selected MarkingLayer contains markings, will a dialog confirm overwriting them.
	 *  Sets glassPane visible and shows the cell selecting rectangle and round over the image.
	 *  If cell picking was already running it will be stopped.
	 *  
	 */
	public void startStopCellPicking(){
		try {
			ShadyMessageDialog dialog;
			// check is the cell picking running already
			if(!this.guiListener.isCellPickingON()){
				if(this.taskManager.getSelectedImageLayer() != null){
					if(this.taskManager.getSelectedMarkingLayer() != null ){
						if(this.taskManager.getSelectedImageLayer().hasMarkingLayer(this.taskManager.getSelectedMarkingLayer().getLayerID())){
							if(this.taskManager.getSelectedMarkingLayer().getCounts()>0){
								// show confirm dialog if selected MarkingLayer contains markings.
								dialog = new ShadyMessageDialog(this, "Selected MarkingLayer contains markings", " Overwrite with precountings?", ID.YES_NO, this);
								if(dialog.showDialog() == ID.NO){
									dialog=null;
									return;
								}
							}

							// start picking the cell
							glassPane.setVisible(true);
							this.guiListener.setCellPickingON(true);
							glassPane.setRectangleSize(this.imagePanel.getWidth()/4);
							this.preCountButton.setText("Cancel Picking");
						//	this.stopPreCountButton.setEnabled(false);
							// Set cursor to circle
						//	setCircleCursorToPanels();
							this.preCountButton.setForeground(Color_schema.orange_dark);

						}
						else{
							dialog = new ShadyMessageDialog(this, "Selected ImageLayer has no selected MarkingLayer", "Select MarkingLayer under Selected ImageLayer.", ID.OK, this);
							dialog.showDialog();
						}
					}
					else{
						dialog = new ShadyMessageDialog(this, "No markinglayer", "Not found selected MarkingLayer where add precountings", ID.OK, this);
						dialog.showDialog();
					}
				}else{
					dialog = new ShadyMessageDialog(this, "No ImageLayer", "Not found ImageLayer for precounting", ID.OK, this);
					dialog.showDialog();

				}
			}
			else{
				// stop cell picking
				glassPane.setVisible(false);
				this.guiListener.setCellPickingON(false);
				this.preCountButton.setText("Pick A New Cell");
				this.preCountButton.setForeground(Color_schema.white_230);
			}
			dialog=null;
		} catch (Exception e) {
			LOGGER.severe("Error in starting or stopping picking cells!");
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * Move selected marking layer.
	 *
	 * @param direction the direction
	 * @throws Exception the exception
	 */
	public void moveSelectedMarkingLayer(int direction) throws Exception{
		if(this.taskManager.moveSelectedMarkingLayer(direction)){
			updateImageLayerInfos();
		}
		
	}
	

	/**
	 * For testing purposes opens one image at launch-> one ImageLayer and MarkingLayer created.
	 */
	@SuppressWarnings("unused")
	private void testing(){
		try {
			String path="/home/antti/4kuvaa/eka.jpg";
			if(SharedVariables.operationSystem== ID.OS_LINUX_UNIX)
				path="/home/antti/4kuvaa/eka.jpg";
			if(SharedVariables.operationSystem== ID.OS_MAC)
				path="/Users/Antti/Documents/kuva.jpeg";
			if(SharedVariables.operationSystem== ID.OS_WINDOWS)
				return;
			
				ImageLayer l = new ImageLayer(path);
				ArrayList<ImageLayer> list = new ArrayList<ImageLayer>();
				list.add(l);
				addImageLayerList(list);
		} catch (Exception e1) {
			LOGGER.severe("Error in testing!");
			e1.printStackTrace();
		}
	
			try {
				createNewMarkingLayer(taskManager.getSelectedImageLayer().getLayerID());
			} catch (Exception e) {
				LOGGER.severe("Testing method not working!");
				e.printStackTrace();
			}
		
	}

	/**
	 * Updates properties (color, size thickness, etc.) of all MarkingPanels.
	 *
	 * @throws Exception the exception
	 */
	public void updateAllMarkingPanelProperties() throws Exception{

		setPropertiesOfAllMarkingPanels();
		layers.repaint();

	}


	/**
	 *  Updates the coordinates of markings of selected MarkingPanel. Gets the coordinatelist formatted to screen and sets to markingPanel.
	 */
	public void updateCoordinatesOfSelectedMarkingPanel(){
		try {
			MarkingLayer selectedMarkingLayer = taskManager.getSelectedMarkingLayer();
			if(selectedMarkingLayer != null && selectedMarkingLayer.isVisible()){
				MarkingPanel selectedMarkingPanel= getMarkingPanelByLayerID(selectedMarkingLayer.getLayerID());
				if(selectedMarkingPanel != null && selectedMarkingPanel.isVisible()){
					// get and set the coordinates formatted to screen (coordinates at image -> coordinates at screen)
					selectedMarkingPanel.setCoordinateList(taskManager.getScreenCoordinatesOfSelectedMarkingLayer());

					layers.repaint();

					updateImageLayerInfos(); // for faster computing -> should update only SingleMarkingPanel (in future)
					
				}
			}
		} catch (Exception e) {
			LOGGER.severe("Error in updating selectedMarkingPanel:" +e.getMessage());
		}


	}

	
	/**
	 * Updates the coordinates of markings in MarkingPanels by calculating the points at image to the points at screen.
	 *
	 * @throws Exception the exception
	 */
	public void updateCoordinatesOfVisibleMarkingPanels() throws Exception{
		// the size or position of viewed area may have changed -> calculate new positions of visible markings
		if(layers.getComponentCount()>1){
			// go through the MarkingPanels of layers
			ArrayList<ScreenCoordinatesOfMarkingLayer> screenCoordinates = taskManager.getScreenCoordinatesOfVisibleMarkingLayers();
			if(screenCoordinates != null && screenCoordinates.size()>0){
				Iterator<ScreenCoordinatesOfMarkingLayer> screenCoordinateIterator= screenCoordinates.iterator();
				while(screenCoordinateIterator.hasNext()){
					ScreenCoordinatesOfMarkingLayer singleScreenCoordinates=screenCoordinateIterator.next();
					MarkingPanel mp = getMarkingPanelByLayerID(singleScreenCoordinates.getId()); // the correspoinding MarkingPanel to MarkingLayer
					if(mp != null){
						mp.setCoordinateList(singleScreenCoordinates.getCoordinates()); // set the updated coordinates to MarkingPanel
					}
				}
			}
		}
	}

	/**
	 *  Refreshes GridPanel if selected MarkingLayer has Grid set as visible.
	 *
	 * @throws Exception the exception
	 */
	public void updateGridPanel() throws Exception{
		this.gridPanel.setGridProperties(taskManager.getConvertedGridProperties());
		if(this.taskManager.getSelectedMarkingLayer() != null &&
				this.taskManager.getSelectedMarkingLayer().isVisible()
				&& this.taskManager.getSelectedMarkingLayer().isGridON())
			this.gridPanel.setShowGrid(true);
		this.gridPanel.repaint();
	}

	/**
	 * Highlights a single marking if found close the Point p.
	 *
	 * @param p Point where mouse was hovered
	 * @throws Exception the exception
	 */
	public void updateHighlight(Point p) throws Exception{

		Point panelPoint =getClosestMarkingPointAtScreen(p, SharedVariables.DISTANCE_TO_REMOVE);

			this.highlightPanel.updateHighlightPoint(panelPoint);
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					highlightPanel.repaint();
				}
			});
	}

	/**
	 * Refreshes the Info Panel at right of main GUI where information of LAYERS are shown. 
	 * Removes all panels of information and recreates them
	 */
	public void updateImageLayerInfos(){
		try {
			// update LayerInfos
			layerInfoListJPanel.removeAll(); // remove all ImageLayerPanels
			
			ArrayList<ImageLayer> finalizedImageLayers = taskManager.getImageLayerList();
			// Go through ImageLayer list
			if (finalizedImageLayers != null && finalizedImageLayers.size() > 0) {
				Iterator<ImageLayer> iterator = finalizedImageLayers.iterator();
				while (iterator.hasNext()) {
					ImageLayer im = (ImageLayer) iterator.next();
					// check that image file path is not null or too short. These may not be necessary, but better to keep it here.
					if(im != null && im.getImageFilePath() != null && im.getImageFilePath().length()>2){
						layerInfoListJPanel.add(new ImageLayerInfo(im ,this));
						layerInfoListJPanel.add(Box.createRigidArea(new Dimension(0,5)));
					}
				}
			}
			layerInfoListJPanel.add(addImageLayerJPanel); // add the removed ADD IMAGE JButton back to panel

			rightPanel.validate();
			rightPanel.repaint();
		} catch (Exception e) {
			LOGGER.severe("Error in updating Layer:  " +e.getClass().toString() + " :" +e.getMessage());
		}

	}

	/**
	 * Informs the LayerVisualManger of the changed dimension of ImagePanel.
	 * The dimension is needed in transformation of image to fit in ImagePanel.
	 * This method is called always when ImagePanel size is changed (Splitpane size changed or GUI window size changed)
	 *
	 * @throws Exception the exception
	 */
	private void updateImagePanelSize() throws Exception{
		this.taskManager.setImagePanelDimension(new Dimension((int)this.imagePanel.getBounds().getWidth(), (int)this.imagePanel.getBounds().getHeight()));

	}

	/**
	 * Updates properties (color, size thickness, etc.) of MarkingPanel, which id is given.
	 *
	 * @param mLayerID int ID of the MarkingLayer which information is used.
	 * @throws Exception the exception
	 */
	public void updateMarkingPanelProperties(int mLayerID) throws Exception{		
		// update the properties
		setPropertiesOfMarkingPanel(mLayerID);
		updateImageLayerInfos();
		layers.repaint();

	}


	/**
	 * Organizes the zooming of image in or out to given point and given zoom multiplier. Determines and refreshes also the markings, grid and highlight.
	 *
	 * @param midPoint Point where zooming will focus
	 * @param zoomValue double multiplier how much is zoomed (1.25 corresponds to 25% zoom)
	 * @param processinID int ID quality of Image (ID.IMAGE_PROCESSING_BEST_QUALITY, etc.)
	 * @throws Exception the exception
	 */
	public void zoomAndUpdateImage(Point midPoint, double zoomValue, int processinID) throws Exception{
		PositionedImage im = taskManager.getZoomedImage(midPoint, zoomValue);
		if(im != null && im.getImage() != null){
			this.imagePanel.setImage(im);
	
			updateCoordinatesOfVisibleMarkingPanels();
			updateGridPanel();
			removeHighLightPoint();
			this.layers.repaint();
		}
	
	
	}


}

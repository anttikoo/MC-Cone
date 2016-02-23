package gui.grid;

import information.Fonts;
import information.GridProperties;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;
import information.PositionedRectangle;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import gui.Color_schema;
import gui.GUI;
import gui.MouseListenerCreator;
import gui.PropertiesDialog;

/**
 * 
 * Dialog panel, which shows settings for selecting grid dimensions and visibility of grid and single cells of grid.
 * @author Antti Kurronen
 *
 */
public class GridPropertiesPanel extends PropertiesDialog {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4886201841981432510L;

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The list of MarkingLayers. */
	private ArrayList<MarkingLayer> markingLayerList;
	
	/** The grid on. Shows is GRID used or not */
	private boolean gridON;
	
	/** The ON button. */
	private JButton on_button;
	
	/** The OFF button. */
	private JButton off_button;
	
	/** The list of SingleGridSizes. */
	private ArrayList<SingleGridSize> gridSizes;
	
	/** The grid panel where example grid is. */
	private JPanel gridPanel;
	
	/** The grid combo box. */
	private JComboBox<String> gridComboBox;
	
	/** The slider for random selection. */
	private JSlider randomSlider;
	
	/** The back grid example panel. */
	private JPanel backGridExamplePanel;
	
	/** The grid label. */
	private JLabel gridLabel;
	
	/** The combo label. */
	private JLabel comboLabel;
	
	/** The template GridProprties. GridProperty which data is used as template for selected markingLayer(s)  */
	private GridProperties templateGP=null; 
	
	/** The slider label. */
	private JLabel sliderLabel;
	
	/** The slider value label. */
	private JLabel sliderValueLabel;
	
	/**
	 * Constructor of class.
	 * @param frame JFrame of owner
	 * @param gui GUI object
	 * @param point Point top left point location of window
	 * @param markingLayerList ArrayList of MarkingLayers, which are modified
	 * @param gridSizeList ArrayList of SingleGridSizes, determining the grid dimension.
	 */
	public GridPropertiesPanel(JFrame frame, GUI gui, Point point, ArrayList<MarkingLayer> markingLayerList, ArrayList<SingleGridSize> gridSizeList) {
		super(frame, gui, point);
	
		try {
			this.markingLayerList=markingLayerList;
			this.gridON= isAnyGridON();
			this.gridSizes=gridSizeList;
			this.templateGP= getFirstGridPropertiesFromAllMarkingLayers(0, 0);
			initDialog();
		} catch (Exception e) {
			LOGGER.severe("Error in initializing Window for Grid Properties!");
			e.printStackTrace();
		}
	
	}

	/* (non-Javadoc)
	 * @see gui.PropertiesDialog#initUPPanels()
	 */
	protected JPanel initUPPanels() throws Exception{

		JPanel upperBackPanel= new JPanel();
		upperBackPanel.setLayout(new BorderLayout());
		upperBackPanel.add(initTitlePanel("Set Grid for all MarkingLayers"), BorderLayout.PAGE_START);

		JPanel buttonTitlePanel = new JPanel();
		buttonTitlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 3));

		buttonTitlePanel.setBackground(Color_schema.dark_30);
		buttonTitlePanel.setMaximumSize(new Dimension(150,36));
		buttonTitlePanel.setMinimumSize(new Dimension(150,36));
		buttonTitlePanel.setPreferredSize(new Dimension(150,36));
		JLabel buttontitleJLabel= new JLabel("SET GRID ON/OFF:");
		buttontitleJLabel.setFont(Fonts.b14);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT,  20, 3));
		buttonPanel.setBorder(BorderFactory.createLineBorder(Color_schema.grey_100, 1));
		buttonPanel.setMaximumSize(new Dimension(150,36));
		buttonPanel.setMinimumSize(new Dimension(150,36));
		buttonPanel.setPreferredSize(new Dimension(150,36));
		buttonPanel.setBackground(Color_schema.dark_30);
		buttonPanel.add(buttontitleJLabel);

		on_button = new JButton(gui.getImageIcon("/images/on_unselected.png"));
		on_button.setMaximumSize(new Dimension(45,30));
		on_button.setMinimumSize(new Dimension(45,30));
		on_button.setPreferredSize(new Dimension(45,30));
		on_button.setFocusable(false);
		on_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					gridON=true;
					updateOnOffButtonIcons();
					enableComponents();
				} catch (Exception e1) {
					LOGGER.severe("Error in putting grid ON!");
					e1.printStackTrace();
				}

			}
		});

		on_button.setBorder(BorderFactory.createLineBorder(Color_schema.dark_30, 2));
		MouseListenerCreator.addMouseListenerToNormalButtonsWithBlackBorder(on_button);

		off_button = new JButton(gui.getImageIcon("/images/off_selected.png"));
		off_button.setMaximumSize(new Dimension(44,30));
		off_button.setMinimumSize(new Dimension(44,30));
		off_button.setPreferredSize(new Dimension(44,30));
		off_button.setFocusable(false);
		off_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					gridON=false;
					updateOnOffButtonIcons();
					enableComponents();
				} catch (Exception e1) {
					LOGGER.severe("Error in putting grid OFF!");
					e1.printStackTrace();
				}
			}
		});

		off_button.setBorder(BorderFactory.createLineBorder(Color_schema.dark_30, 2));
		MouseListenerCreator.addMouseListenerToNormalButtonsWithBlackBorder(off_button);

		updateOnOffButtonIcons();

		buttonPanel.add(on_button);
		buttonPanel.add(off_button);
		upperBackPanel.add(buttonPanel, BorderLayout.CENTER);


		return upperBackPanel;

	}

	/* (non-Javadoc)
	 * @see gui.PropertiesDialog#initCenterPanels()
	 */
	protected JPanel initCenterPanels() throws Exception{
		JPanel centerBackPanel = new JPanel();
		centerBackPanel.setLayout(new BoxLayout(centerBackPanel, BoxLayout.PAGE_AXIS));
		centerBackPanel.setMaximumSize(new Dimension(panelWidth,300));
		centerBackPanel.setMinimumSize(new Dimension(panelWidth,300));
		centerBackPanel.setPreferredSize(new Dimension(panelWidth,300));

		centerBackPanel.add(setUpComboBoxPanel());	
		centerBackPanel.add(setupSliderPanel());
		centerBackPanel.add(setUpGridExamplePanel());

		return centerBackPanel;
	}

	/**
	 * Creates JPanel containing combobox for size of the grid
	 * @return JCombobox object
	 */
	private JPanel setUpComboBoxPanel(){
		try{
			JPanel comboBoxPanel=new JPanel();
			comboBoxPanel.setLayout(new BoxLayout(comboBoxPanel, BoxLayout.LINE_AXIS));
			comboBoxPanel.setMaximumSize(new Dimension(panelWidth,36));
			comboBoxPanel.setMinimumSize(new Dimension(panelWidth,36));
			comboBoxPanel.setPreferredSize(new Dimension(panelWidth,36));
			JPanel comboLabelPanel=new JPanel();
			comboLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
			comboLabel = new JLabel("SELECT GRID DIMENSION:");
			comboLabel.setFont(Fonts.b14);
			comboLabelPanel.add(comboLabel);
			comboBoxPanel.add(Box.createRigidArea(new Dimension(20,0)));
			comboBoxPanel.add(comboLabel);
			comboBoxPanel.add(Box.createRigidArea(new Dimension(20,0)));
			comboBoxPanel.add(Box.createRigidArea(new Dimension(0,5)));
			
			gridComboBox = new JComboBox<String>();
			gridComboBox.setMaximumSize(new Dimension(80,25));
			gridComboBox.setPreferredSize(new Dimension(80,25));
			gridComboBox.setMinimumSize(new Dimension(80,25));
			gridComboBox.setMaximumRowCount(20);
			gridComboBox.setBackground(Color_schema.dark_20);
			gridComboBox.setForeground(Color_schema.white_230);
			gridComboBox.setFont(Fonts.b14);
	
			//go trough all gridsizes and add to Combobox
			Iterator<SingleGridSize> sgIterator = this.gridSizes.iterator();
			while(sgIterator.hasNext()){
				SingleGridSize sgs= sgIterator.next();
				String size= ""+sgs.getRows() + " x "+sgs.getColumns();
				gridComboBox.addItem(size);
	
			}
			if(gridComboBox.getItemCount() >0){
						
				if(this.templateGP != null){
	
					int index= getIndexOfGridSizeList(this.templateGP.getGridRowCount(), this.templateGP.getGridColumnCount());
					if(index >0)
						gridComboBox.setSelectedIndex(index);
					else
						gridComboBox.setSelectedIndex(0);
				}
				else{
					gridComboBox.setSelectedIndex(0);
				}
			}
	
			gridComboBox.addItemListener(new ItemListener() {
	
				@Override
				public void itemStateChanged(ItemEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
	
						@Override
						public void run() {
							updateGridDimensionFromComboBox(ID.GPANEL_GRID_SIZE_CHANGED);
						}
					});
				}
			});
			
			comboBoxPanel.add(gridComboBox);
			return comboBoxPanel;
		
		}catch(Exception e){
			LOGGER.severe("unable to create combobox for grid sizes "+e.getMessage());
			return new JPanel();
		}


	}
	
	/**
	 * Creates JPanel, where is JSlider for selecting for percent value of selected cells in grid.
	 *
	 * @return JPanel containing JSlider for percent value of selected cells in grid
	 * @throws Exception the exception
	 */
	private JPanel setupSliderPanel() throws Exception{
		
		JPanel percentSliderPanel=new JPanel();
		percentSliderPanel.setLayout(new BoxLayout(percentSliderPanel, BoxLayout.LINE_AXIS));
		percentSliderPanel.setMaximumSize(new Dimension(panelWidth,36));
		percentSliderPanel.setMinimumSize(new Dimension(panelWidth,36));
		percentSliderPanel.setPreferredSize(new Dimension(panelWidth,36));
		JPanel pSliderLabelPanel=new JPanel();
		pSliderLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
		sliderLabel = new JLabel("RANDOM FILL %:");
		sliderLabel.setFont(Fonts.b14);
		pSliderLabelPanel.add(sliderLabel);
		percentSliderPanel.add(Box.createRigidArea(new Dimension(10,0)));
		percentSliderPanel.add(pSliderLabelPanel);
		percentSliderPanel.add(Box.createRigidArea(new Dimension(10,0)));
		percentSliderPanel.add(Box.createRigidArea(new Dimension(0,5)));
		//setup randow Slider
		randomSlider=new JSlider(10, 100,50);
		

		randomSlider.setMinimumSize(new Dimension(200,30));
		randomSlider.setPreferredSize(new Dimension(200,30));
		randomSlider.setMaximumSize(new Dimension(200,30));
		
		randomSlider.setMajorTickSpacing(20);
		randomSlider.setMinorTickSpacing(5);
		randomSlider.setPaintTicks(true);
		randomSlider.setSnapToTicks(true);
		
		randomSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						sliderValueLabel.setText(""+randomSlider.getValue()+" %");
						//update grid, because amount of selected cells is changed
						updateGridDimensionFromComboBox(ID.GPANEL_RANDOM_PROCENT_CHANGED);

					}
				});
				
			}
		});
		
		if(this.templateGP != null && this.templateGP.getRandomProcent() >0)
			randomSlider.setValue(this.templateGP.getRandomProcent());
		
		percentSliderPanel.add(randomSlider);
		
		JPanel pSliderValueLabelPanel=new JPanel();
		pSliderValueLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 2));
		sliderValueLabel = new JLabel(""+this.randomSlider.getValue()+" %");
		sliderValueLabel.setFont(Fonts.b18);
		
		pSliderValueLabelPanel.add(sliderValueLabel);
		percentSliderPanel.add(pSliderValueLabelPanel);
		
		return percentSliderPanel;
		
	}

	/**
	 *  Setups the Preview Grid at startup: visible or invisible.
	 *
	 * @throws Exception the exception
	 */
	private void enableComponents() throws Exception{

			this.gridComboBox.setEnabled(gridON);
			if(gridON){
				this.gridPanel.setVisible(true);
				updateGridDimensionFromComboBox(ID.GPANEL_STARTUP);		
				this.gridPanel.setBackground(Color_schema.grey_150);
				this.gridLabel.setForeground(Color_schema.white_230);
				this.comboLabel.setForeground(Color_schema.white_230);
				this.sliderLabel.setForeground(Color_schema.white_230);
				this.sliderValueLabel.setForeground(Color_schema.white_230);
				this.randomSlider.setEnabled(true);
				this.randomSlider.setForeground(Color_schema.white_230);
				this.randomSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);

			}
			else{
				this.gridPanel.setVisible(false);
				this.gridPanel.setBackground(Color_schema.dark_40);
				this.gridLabel.setForeground(Color_schema.grey_100);
				this.comboLabel.setForeground(Color_schema.grey_100);
				this.sliderLabel.setForeground(Color_schema.grey_100);
				this.sliderValueLabel.setForeground(Color_schema.grey_100);
				this.randomSlider.setEnabled(false);
				this.randomSlider.setForeground(Color_schema.dark_40);
				this.randomSlider.putClientProperty("JSlider.isFilled", Boolean.FALSE);
		
			}
			this.backGridExamplePanel.repaint();		
	}

	/**
	 * Set ups the Grid example JPanel.
	 *
	 * @return JPanel the Grid example panel.
	 * @throws Exception the exception
	 */
	private JPanel setUpGridExamplePanel() throws Exception{
		backGridExamplePanel = new JPanel();
		backGridExamplePanel.setLayout(new BoxLayout(backGridExamplePanel, BoxLayout.PAGE_AXIS));
		backGridExamplePanel.setMaximumSize(new Dimension(panelWidth,320));
		backGridExamplePanel.setMinimumSize(new Dimension(panelWidth,320));
		backGridExamplePanel.setPreferredSize(new Dimension(panelWidth,320));
		backGridExamplePanel.setBorder(BorderFactory.createLineBorder(Color_schema.grey_100, 1));

		JPanel gridTitlePanel=new JPanel();
		gridTitlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
		gridTitlePanel.setMaximumSize(new Dimension(panelWidth,30));
		gridTitlePanel.setPreferredSize(new Dimension(panelWidth,30));
		gridTitlePanel.setMinimumSize(new Dimension(panelWidth,30));

		gridLabel = new JLabel("SELECT ACTIVE GRID CELLS:");
		gridLabel.setFont(Fonts.b14);
		gridTitlePanel.add(gridLabel);
		backGridExamplePanel.add(gridTitlePanel);

		gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(2, 2,1,1));
		gridPanel.setMaximumSize(new Dimension(panelWidth,280));
		gridPanel.setMinimumSize(new Dimension(panelWidth,280));
		gridPanel.setPreferredSize(new Dimension(panelWidth,280));
		gridPanel.setBackground(Color_schema.grey_150);
		backGridExamplePanel.add(gridPanel);
		enableComponents();

		return backGridExamplePanel;

	}

	
	/**
	 * Selects randomly which grid cells are selected.
	 *
	 * @param r int rows of grid
	 * @param c int columns of grid
	 * @throws Exception the exception
	 */
	private void setRandomGridShown(int r, int c) throws Exception{
		int rows = r;
		int columns = c;
		
		unselectAllCells(rows, columns); // set all cells unselected
		int cellsCount = rows*columns;
		double value=((double)this.randomSlider.getValue())/100.0;
		double selectedCells= Math.ceil(((double)cellsCount)*value); // round selected number to next upper integer value
		
		int partOfCellsCount= (int)selectedCells;
		if(partOfCellsCount==0)
			partOfCellsCount=1;
		
			for(int i=0;i<partOfCellsCount;i++){
				// check that unselected grid rectangles number is smaller than selected ones.
				if(countSelectedGridRectangle(rows, columns)<partOfCellsCount){
					GridRectangle gr = getRandomGridRectangle(rows, columns);
					if(gr != null){
						gr.setSelected(true);
						gr.updatePanel();
					}
				}
			}
		
	}
	
	
	/**
	 * Set all positions of GRID as unselected.
	 *
	 * @param rowCount amount of rows.
	 * @param columnCount amount of columns
	 * @throws Exception the exception
	 */
	private void unselectAllCells(int rowCount,int columnCount) throws Exception{
		for(int r=1;r<=rowCount;r++){
			for(int c=1;c<=columnCount;c++){
				GridRectangle gr= getGridRectangle(r, c);
				gr.setSelected(false);	
				gr.updatePanel();
			}		
		}
	}
	
	/**
	 * Returns the random grid rectangle.
	 *
	 * @param rowCount the row count
	 * @param columnCount the column count
	 * @return the random grid rectangle
	 */
	private GridRectangle getRandomGridRectangle(int rowCount, int columnCount){
		try {
			if(countUnselectedGridRectangle(rowCount, columnCount)>0){
			
				Random rand = new Random();
				int randomRow = rand.nextInt((rowCount - 1) + 1) + 1;
				int randomColumn = rand.nextInt((columnCount - 1) + 1) + 1;
				GridRectangle gr= getGridRectangle(randomRow, randomColumn);
				if(gr.isSelected())
					return getRandomGridRectangle(rowCount, columnCount);
				else
					return gr;
			}
			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in getting random gridrectangle!");
			e.printStackTrace();
			return null;
		}
			
	}
	
	/**
	 * Count amount of unselected grid rectangle.
	 *
	 * @param rowCount the row count
	 * @param columnCount the column count
	 * @return the int
	 * @throws Exception the exception
	 */
	private int countUnselectedGridRectangle(int rowCount, int columnCount) throws Exception{
		int count_unselected=0;
		
		for(int r=1;r<=rowCount;r++){
			for(int c=1;c<=columnCount;c++){
				GridRectangle gr= getGridRectangle(r, c);
				if(!gr.isSelected()){
					count_unselected++;
				}
			}		
		}
		return count_unselected;
		
	}
	
	/**
	 * Count amount of selected grid rectangle.
	 *
	 * @param rowCount the row count
	 * @param columnCount the column count
	 * @return the int
	 * @throws Exception the exception
	 */
	private int countSelectedGridRectangle(int rowCount, int columnCount) throws Exception{
		int count_selected=0;
		
		for(int r=1;r<=rowCount;r++){
			for(int c=1;c<=columnCount;c++){
				GridRectangle gr= getGridRectangle(r, c);
				if(gr.isSelected()){
					count_selected++;
				}
			}		
		}
		return count_selected;		
	}
	
	/**
	 *  
	 * Returns first Gridproperties of any markinglayers. 
	 * First searching visible Gridproperties from MarkingLayers, which Gridproperties are modified at same time.
	 * Then searching unvisible Gridproperties from MarkingLayers, which Gridproperties are modified at same time.
	 * Then searching visible Gridproperties from MarkingLayers under same Imagelayer.
	 * Then searching unvisible Gridproperties under same Imagelayer.
	 * Then searching visible Gridproperties from MarkingLayers under other Imagelayers.
	 * Then searching unvisible Gridproperties under same Imagelayer.
	 * Otherwise any Gridproperties is returned if found. If no any Gridproperties is not found, null is returned.
	 *
	 * @param r the row
	 * @param c the column
	 * @return Gridproperties
	 * @see GridProperties
	 */
	private GridProperties getFirstGridPropertiesFromAllMarkingLayers(int r, int c){
		try {
			boolean checkRandomProcent=false;
			if(r>0 && c>0)
				checkRandomProcent=true;
			
			GridProperties returnGP=null;
			
				returnGP= getFirstGridProperties(r,c, true, checkRandomProcent); // Just visible GridProperties from Markinglayers that are modified at the same time. 
				
				if(returnGP == null)
					returnGP = getFirstGridProperties(r,c, false, checkRandomProcent); // Unvisible GridProperties from Markinglayers that are modified at the same time. 
				if(returnGP == null){
					// Get GridProperties from markingLayers that are under same ImageLayer (not needed to be modified)
					// preferable visible, but returns also unvisible GridProperties
					returnGP= getGPFromMarkingLayerList(this.markingLayerList, r, c, checkRandomProcent); 
					if(returnGP == null){
						returnGP= getGPFromMarkingLayerList(this.gui.getAllMarkingLayers(), r,c,checkRandomProcent); 
						
					}		
				}	
			return returnGP;
		} catch (Exception e) {
			LOGGER.severe("Error in getting first grid properties from all MarkingLayers!");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Tries to Find GridProperties from all given MarkingLayers and markingLayers that are under same ImageLayer.
	 * Returns first GridProperties, that is found. Preferable visible @see GridProperties, but if not found, then first unvisible GridProperties is returned.
	 * Return null if not any Gridproperties are found.
	 * @param mLayerList list of MarkingLayers, which GridProperties are viewed.
	 * @param r integer for rows in grid
	 * @param c integer for columns in grid
	 * @return GridProperties
	 */
	private GridProperties getGPFromMarkingLayerList(ArrayList<MarkingLayer> mLayerList, int r, int c, boolean checkProcent){
		try{
	
		if(mLayerList != null){
			Iterator<MarkingLayer> miterator=mLayerList.iterator();
			while(miterator.hasNext()){
				MarkingLayer mlayer= miterator.next();
				
				ImageLayer iLayer = gui.getImageLayerByMarkingLayerID(mlayer.getLayerID());
				if(iLayer != null){
					ArrayList<MarkingLayer> mlayerlistOFSingleILayer = iLayer.getMarkingLayers();
					if(mlayerlistOFSingleILayer != null && mlayerlistOFSingleILayer.size()>0){
						Iterator<MarkingLayer> mSingleILiterator=mLayerList.iterator();
						while(mSingleILiterator.hasNext()){ // go through all MarkingLayers under iLayer				
							GridProperties gpSingle = mSingleILiterator.next().getGridProperties();
							// c and r is zero when initializing panel. And they are something else, when user selects row and column in combobox.
							if(gpSingle != null && gpSingle .isGridON() && ( (c==0 && r==0) || (gpSingle.getGridColumnCount() == c && gpSingle.getGridRowCount() == r) ) )
								if(!checkProcent || checkProcent && gpSingle.getRandomProcent()== this.randomSlider.getValue()) // check that percentSlider value is same
									if(gpSingle.checkRandomPercentBySelectedRectangles()) // check that random prosent is ok.
										return gpSingle; // found Gridproperties that is visible (ON) and under same ImageLayer
						}
						// try to find unvisible Gridproperties here, because not found any visible ones.
						Iterator<MarkingLayer> mSingleILiteratorUnvisible=mLayerList.iterator();
						while(mSingleILiteratorUnvisible.hasNext()){ // go through all MarkingLayers under iLayer						
							GridProperties gpSingle = mSingleILiteratorUnvisible.next().getGridProperties();
							// c and r is zero when initializing panel. And they are something else, when user selects row and column in combobox.
							if(gpSingle != null && ( (c==0 && r==0) || (gpSingle.getGridColumnCount() == c && gpSingle.getGridRowCount() == r) ))
								if(!checkProcent || checkProcent && gpSingle.getRandomProcent()== this.randomSlider.getValue()) // check that percentSlider value is same								
									if(gpSingle.checkRandomPercentBySelectedRectangles()) // check that random prosent is ok.
										return gpSingle; // found Gridproperties that is under same ImageLayer
						}					
					}
				}	
			}
		}
			return null;
		}
		catch (Exception e) {
			LOGGER.severe("Error in finding GridProperties from MarkingLayers:  " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}
	}



	/**
	 * Finds any GridProperties, that are found from given MarkingLayer-list (modified at same time as Gridproperties of this MarkingLayer)
	 * Returns found GridProperties, otherwise null is returned.
	 * 
	 * @return GridProperties
	 */
	private GridProperties getFirstGridProperties(int r, int c, boolean findON, boolean checkProcent){
		try {
			if(this.markingLayerList != null && this.markingLayerList.size()>0){
				Iterator<MarkingLayer> miterator=this.markingLayerList.iterator();
				while(miterator.hasNext()){
					MarkingLayer layer= miterator.next();
					if(layer.getGridProperties() != null)
					{	
							GridProperties gp=layer.getGridProperties();
							//if r and c >0 has gp include same values of columns and rows.
							if(gp != null && ((c==0 && r==0) || (gp.getGridColumnCount()==c && gp.getGridRowCount()==r) ) ){
								if( (findON && gp.isGridON() ) || !findON) // if searching GRID which is ON (visible) then check that it is ON.
									if(!checkProcent || checkProcent && gp.getRandomProcent()== this.randomSlider.getValue()) // check that percentSlider value is same
										if(gp.checkRandomPercentBySelectedRectangles()) // check that random procent is ok.
											return gp;
							}
					}						
				}
			}
			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in getting firs grid property from MarkingLayers!");
			e.printStackTrace();
			return null;
		}
	}



	/**
	 * Returns the first marking layer with grid on (active GRID).
	 *
	 * @return the first MarkingLayer with grid on
	 */
	private MarkingLayer getFirstMarkingLayerWithGridON(){
		try {
			if(this.markingLayerList != null && this.markingLayerList.size()>0){
			Iterator<MarkingLayer> miterator=this.markingLayerList.iterator();
			while(miterator.hasNext()){
				MarkingLayer layer= miterator.next();
				if(layer.getGridProperties() != null && layer.getGridProperties().isGridON())
					return layer;
			}
			}
			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in getting first MarkingLayer which Grid is ON!");
			e.printStackTrace();
			return null;
		}
	}


	private boolean isAnyGridON(){
		try {
			if(getFirstMarkingLayerWithGridON() != null){
				return true;
			}
			return false;
		} catch (Exception e) {
			LOGGER.severe("Error in checking is any grid on!");
			e.printStackTrace();
			return false;
		}
	}

	private int getIndexOfGridSizeList(int row, int column){
		try {
			if(this.gridSizes != null && gridSizes.size()>0){
				for(int i=0; i<this.gridSizes.size();i++){

					SingleGridSize sgs = this.gridSizes.get(i);
					if(sgs.getRows() == row && sgs.getColumns() == column){
						return i;
					}
				}
			}
			return -1;
		} catch (Exception e) {
			LOGGER.severe("Error in getting index of spesific grid size!");
			e.printStackTrace();
			return -1;
		}
	}
	
	
	/**
	 * Updates the preview grid dimension. This will be launched in startup, 
	 * when selected new index in dimension combobox or when randomSlider is moved.
	 *
	 * @param updateType the update type
	 */
	private void updateGridDimensionFromComboBox(int updateType){
		try {
			int index=0;
			SingleGridSize sgs=null;
			switch(updateType){
			// at startup of this GridPropertiesPanel
				case ID.GPANEL_STARTUP:
					if(this.templateGP != null)
						index = getIndexOfGridSizeList(this.templateGP.getGridRowCount(), this.templateGP.getGridColumnCount());
					break;
					 // user pressed new index of combobox
				case ID.GPANEL_GRID_SIZE_CHANGED:
					
					index = this.gridComboBox.getSelectedIndex();
					// get new template GP, because the column and row are changed
					if(this.gridSizes != null && this.gridSizes.size()>index){
						sgs= this.gridSizes.get(index);
						if(sgs != null){
							this.templateGP = getFirstGridPropertiesFromAllMarkingLayers(sgs.getRows(), sgs.getColumns());
						}
					}
					break;
					//random slider moved
				case ID.GPANEL_RANDOM_PROCENT_CHANGED:
					index = this.gridComboBox.getSelectedIndex();
					if(this.gridSizes != null && this.gridSizes.size()>index){
						sgs= this.gridSizes.get(index);
						if(sgs != null){
							this.templateGP = getFirstGridPropertiesFromAllMarkingLayers(sgs.getRows(), sgs.getColumns());
						}
					}
					break;
				
			
			}
			// if gridSizes has values -> update Grid
			if(this.gridSizes != null && this.gridSizes.size()>index){
				if(sgs==null)
					sgs= this.gridSizes.get(index);
				if(sgs != null){				
				//	if(((GridLayout)this.gridPanel.getLayout()).getRows() != sgs.getRows() || ((GridLayout)this.gridPanel.getLayout()).getColumns() != sgs.getColumns() ||this.gridPanel.getComponentCount() ==0){					
						updateGridSize(sgs.getRows(), sgs.getColumns());
				//	}

				}		
			}
		} catch (Exception e) {
			LOGGER.severe("Error in updating grid dimension and rectangles!");
			e.printStackTrace();
		}	
	}
	
	
	/**
	 * Updates Preview Grid dimension and repaints it.
	 *
	 * @param r the amount of rows
	 * @param c the amount of columns
	 * @throws Exception the exception
	 */
	private void updateGridSize(int r, int c) throws Exception{
		this.gridPanel.removeAll();
		this.gridPanel.revalidate();
		boolean usedTemplateGridProperties = false;
	
			this.gridPanel.setLayout(new GridLayout(r, c, 1, 1));
			this.gridPanel.revalidate();
			int maxSize=Math.min((int)(backGridExamplePanel.getPreferredSize().getWidth()/(double)c), (int)((backGridExamplePanel.getPreferredSize().getHeight()-35)/(double)r));
			int maxGridPanelWidth= maxSize*c;
			int maxGridPanelHeight= maxSize*r;
			this.gridPanel.setMaximumSize(new Dimension(maxGridPanelWidth,maxGridPanelHeight));
			this.gridPanel.setMinimumSize(new Dimension(maxGridPanelWidth,maxGridPanelHeight));
			this.gridPanel.setPreferredSize(new Dimension(maxGridPanelWidth,maxGridPanelHeight));
		
			// go through all positions of grid: rows (i) and columns (j)
			// add selected or unselected cell to grid positions
			for(int i=1;i<= r;i++){
				for(int j=1;j<= c;j++){
					if(this.templateGP != null && this.templateGP.getGridColumnCount()==c && this.templateGP.getGridRowCount()==r){
						boolean showGR=this.templateGP.isSelectedGridCellAt(i, j);
						gridPanel.add(new GridRectangle(i, j, showGR, this));
						
						usedTemplateGridProperties=true;
					}
					else
						gridPanel.add(new GridRectangle(i, j, false, this));
				}
			}
			if(!usedTemplateGridProperties) // no any present properties found -> create random selections
				setRandomGridShown(r,c);

		backGridExamplePanel.repaint();

	}
	
	
	/**
	 * Updates Icons of Buttons for ON and OFF demonstrating highlight effect.
	 */
	private void updateOnOffButtonIcons(){
		try {
			if(gridON){
				on_button.setIcon((gui.getImageIcon("/images/on_selected.png")));
				off_button.setIcon((gui.getImageIcon("/images/off_unselected.png")));
			}
			else{
				on_button.setIcon((gui.getImageIcon("/images/on_unselected.png")));
				off_button.setIcon((gui.getImageIcon("/images/off_selected.png")));
			}
			on_button.revalidate();
			off_button.revalidate();
			on_button.repaint();
			off_button.repaint();

		
		} catch (Exception e) {
			LOGGER.severe("Error in Buttons of GridProperties -view");
			e.printStackTrace();
		}
	}
	
	/**
	 * Count random percent by selected grid rectangles.
	 */
	public void countRandomPercentBySelectedGridRectangles(){
		try {
			SingleGridSize sgs = this.gridSizes.get(this.gridComboBox.getSelectedIndex());
			if(sgs != null){
				int r = sgs.getRows();
				int c = sgs.getColumns();
				
				double selected = (double)countSelectedGridRectangle(r, c);
				double unselected = (double)countUnselectedGridRectangle(r, c);
				
				//int randomPer = (int)( Math.ceil(selected/(selected+unselected)));
				int randomPer= (int)((Math.round((((double)selected)/ ((double)(selected+unselected))*100))/5)*5);

				if(randomPer >0 && randomPer <=100){
					this.randomSlider.setValue(randomPer);
					this.randomSlider.repaint();
				}
				
			}
		} catch (Exception e) {
			LOGGER.severe("Error in counting random percent by selected grid rectangles!");
			e.printStackTrace();
		}
		
	}

	
	/** 
	 * Hides the GridProperties dialog and saves (if wanted) the made changes to MarkingLayer -objects.
	 * @param saveChanges boolean will changes be saved.
	 */
	protected void hideDialog(boolean saveChanges){
		try {
			if(saveChanges){
				this.made_changes=true;
				// create gridProperty
				GridProperties gridProperty=new GridProperties(this.gui.getPresentImageDimension());
				gridProperty.setGridON(this.gridON);
				gridProperty.setRandomPercent(this.randomSlider.getValue()); // set random procent value
				SingleGridSize sgs = this.gridSizes.get(this.gridComboBox.getSelectedIndex());
				int x=sgs.getWidthAlign();
				int y=sgs.getHeightAlign();
				// add vertical lines and rectangles
				for(int r=1;r <= sgs.getRows();r++){
					gridProperty.addRowLineY(y);
					x=sgs.getWidthAlign(); // start new row at left
					for(int c=1; c<= sgs.getColumns();c++){
							GridRectangle gr = getGridRectangle(r, c);
							if(gr != null){							
								PositionedRectangle pRec = new PositionedRectangle(x, y, sgs.getGridCellSize(), sgs.getGridCellSize(), r,c, gr.isSelected());
								gridProperty.addSinglePositionedRectangle(pRec);
							}
					//	}
						x+=sgs.getGridCellSize();
					}
					y+=sgs.getGridCellSize();
				}

				gridProperty.addRowLineY(y); // rightmost line

				// add horizontal lines
				x=sgs.getWidthAlign();
				for(int c=1; c<= sgs.getColumns();c++){
					gridProperty.addColumnLineX(x);
					x+=sgs.getGridCellSize();
				}
				gridProperty.addColumnLineX(x); // lowest line
				


				

				// save the selected gridproperties to markinglayers
				Iterator<MarkingLayer> mIterator = this.markingLayerList.iterator();
				while(mIterator.hasNext()){
					MarkingLayer mlayer= mIterator.next();
					mlayer.setGridProperties(gridProperty);
				}

			}
			this.setVisible(false);
			this.dispose();
		} catch (Exception e) {
			LOGGER.severe("Error in closing window of Grid Properties!");
			e.printStackTrace();
			this.setVisible(false);
			this.dispose();
		}
	}

	/**
	 * Returns the grid rectangle with given value of row and column of grid.
	 *
	 * @param r the row
	 * @param c the column
	 * @return the grid rectangle
	 * @throws Exception the exception
	 */
	private GridRectangle getGridRectangle(int r, int c) throws Exception{
		if(this.gridPanel != null && this.gridPanel.getComponentCount()>0){

			Component[] grs= gridPanel.getComponents();
			//go through components
			for(int i=0; i<grs.length;i++){

				GridRectangle gr =(GridRectangle)grs[i];
				if(gr.getRow()==r && gr.getColumn()==c){
					return gr;
				}

			}
		}
		return null;
	}


}

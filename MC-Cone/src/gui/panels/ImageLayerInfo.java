/*
 * 
 */
package gui.panels;

import gui.Color_schema;
import gui.GUI;
import gui.MarkingProperties;
import gui.MouseListenerCreator;
import gui.ShapeIcon;
import information.Fonts;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 *
 * Panel for presenting information of one Image Layer and it's Markings.
 * @author Antti Kurronen
 *
 */
public class ImageLayerInfo extends JPanel{
	

/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4726272780653526727L;

/** The Constant LOGGER. */
private final static Logger LOGGER = Logger.getLogger("MCCLogger");

/** The ID of ImageLayer. */
private int imageLayerID;

/** The name of ImageLayer. */
private String imageLayerName;

/** The title panel. */
private JPanel titlePanel;

/** The title. */
private JLabel title;

/** The GUI. */
private GUI gui;

/** The marking area. */
private JPanel markingArea;

/** The JPanel for Button for adding new MarkingLayer. */
private JPanel addMarkingLayerJPanel;

/** The selected ImageLayer JButton. */
private JButton selectedImageLayerJButton;

	/**
	 * Class constructor
	 * @param imageLayer the ImageLayer which information will be shown
	 * @param gui the parent  GUI object for sending information
	 */
	public ImageLayerInfo(ImageLayer imageLayer, GUI gui){
		try {
			// set objects and information
			this.gui=gui;
			this.setImageLayer_ID(imageLayer.getLayerID());
			this.setImageLayerName(imageLayer.getImageFileName());

			//set panel
			this.setBackground(Color_schema.dark_40);
			this.setLayout(new BorderLayout(0,0));


			this.setMaximumSize(new Dimension(5000,75));
			this.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));

			// setup title for Image Layer
			this.titlePanel = new JPanel();
			this.titlePanel.setLayout(new BoxLayout(this.titlePanel, BoxLayout.LINE_AXIS));
			this.titlePanel.setMaximumSize(new Dimension(gui.getRightPanelWidth(),40));
			this.titlePanel.setPreferredSize(new Dimension(50,40));
			this.titlePanel.setBackground(Color_schema.dark_40);
			JPanel titleLabelJPanel = new JPanel();
			titleLabelJPanel.setLayout(new BoxLayout(titleLabelJPanel, BoxLayout.LINE_AXIS));
			titleLabelJPanel.setMaximumSize(new Dimension(gui.getRightPanelWidth()-120,40));
			titleLabelJPanel.setPreferredSize(new Dimension(gui.getRightPanelWidth()-120,40));
			titleLabelJPanel.setBackground(Color_schema.dark_40);

			selectedImageLayerJButton = new JButton();
			setEyeIcon();
	
			selectedImageLayerJButton.setAlignmentX(Component.LEFT_ALIGNMENT);
			selectedImageLayerJButton.setPreferredSize(new Dimension(40,35));
			selectedImageLayerJButton.setMaximumSize(new Dimension(40,35));
			selectedImageLayerJButton.setMinimumSize(new Dimension(40,35));
			selectedImageLayerJButton.setMargin(new Insets(0, 0, 0, 0));
			selectedImageLayerJButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 1));
			selectedImageLayerJButton.setContentAreaFilled(false);
			selectedImageLayerJButton.setFocusable(false);
			addActionsToJButtons(selectedImageLayerJButton, ID.SELECTED_IMAGELAYER_JBUTTON);
			MouseListenerCreator.addMouseListenerToButtonsWithDark40Border(selectedImageLayerJButton);
	
			// add gridIcon for imagelayer
			JButton gridButton=new JButton();
			gridButton.setPreferredSize(new Dimension(30,30));
			gridButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 2));

			gridButton.setContentAreaFilled(false);
			gridButton.setFocusable(false);
			addActionsToJButtons(gridButton, ID.IMAGELAYER_SET_GRID);
			MouseListenerCreator.addMouseListenerToButtonsWithDark40Border(gridButton);
			if(imageLayer.hasGridOn()){
				gridButton.setIcon(getImageIcon("/images/g_selected.png"));
			}
			else{
				gridButton.setIcon(getImageIcon("/images/g_unselected.png"));
			}
			this.title=new JLabel(splitTextIfTooLong(this.getImageLayerName()));
			Font rightFont = setRightTitleFont(title.getText(),Fonts.p20, false);
			this.title.setFont(rightFont);
			this.title.setMaximumSize(new Dimension(gui.getRightPanelWidth()-120,40));

			this.title.setForeground(Color_schema.white_230);
			if(imageLayer.isSelected()){
				rightFont = setRightTitleFont(title.getText(), Fonts.b20, true);
				this.title.setForeground(Color_schema.orange_bright);
				this.title.setFont(rightFont);
			}

			if(this.imageLayerName.length()> 50)
				this.title.setToolTipText(this.imageLayerName);
			addMouseListenerToTitle(this.title, ID.IMAGE_INFO);

			titleLabelJPanel.add(this.title);

			this.titlePanel.add(selectedImageLayerJButton);
			this.titlePanel.add(gridButton);
			this.titlePanel.add(Box.createRigidArea(new Dimension(5,0)));
			this.titlePanel.add(titleLabelJPanel);
			this.titlePanel.add(Box.createHorizontalGlue()); // when panel gets bigger the horizontal extra space comes between title and deleting button

			// setup button for deleting this Image Layer
			JPanel closeImagePanel = new JPanel();
			closeImagePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			closeImagePanel.setMaximumSize(new Dimension(25,40));
			closeImagePanel.setPreferredSize(new Dimension(25,40));
			closeImagePanel.setMinimumSize(new Dimension(25,40));
			closeImagePanel.setBackground(Color_schema.dark_40);
			JButton closeJButton = new JButton(getImageIcon("/images/close_big.png"));
			closeJButton.setAlignmentY(Component.CENTER_ALIGNMENT);
			closeJButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
			closeJButton.setPreferredSize(new Dimension(20,20));
			closeJButton.setMaximumSize(new Dimension(20,20));
			closeJButton.setMargin(new Insets(0, 0,0, 0));
			closeJButton.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
			closeJButton.setContentAreaFilled(false);
			closeJButton.setFocusable(false);
			closeJButton.setToolTipText("Delete Image Layer: " +this.title.getText());

			// set up listener for closeButton
			addMouseListenerToCloseButtons(closeJButton);
			addActionsToJButtons(closeJButton, ID.DELETE_IMAGELAYER);

			closeImagePanel.add(closeJButton, BorderLayout.CENTER);
			titlePanel.add(closeImagePanel);

			// markingArea will contain one or more individual MarkingPanels

			this.markingArea = new JPanel();
			this.markingArea.setLayout(new BoxLayout(this.markingArea, BoxLayout.PAGE_AXIS));
			this.markingArea.setBackground(Color_schema.dark_30);
			this.markingArea.setMaximumSize(new Dimension(5000,40));

			// add all MarkingLayers to markingsArea
			addMarkingLayers(imageLayer.getMarkingLayers());

			this.add(this.titlePanel, BorderLayout.PAGE_START);
			this.add(this.markingArea, BorderLayout.CENTER);
			initAddMarkingButton(); // add JButton for creating new Marking to bottom of ImageLayerInfo

		} catch (Exception e) {
			LOGGER.severe("Error in " +e.getClass().toString() + " :" +e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Adds an action to JButton
	 * @param button The button where the action is added
	 * @param typeOfAction int indentifier the type of action
	 */
	private void addActionsToJButtons(JButton button, int typeOfAction){

		if(typeOfAction == ID.DELETE_IMAGELAYER){
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						// remove Imagelayer by giving unique layerID and layername
						int layerID =((ImageLayerInfo)((JPanel)((JPanel)((JButton)e.getSource()).getParent()).getParent()).getParent()).getImageLayerID();
						String layerName=((ImageLayerInfo)((JPanel)((JPanel)((JButton)e.getSource()).getParent()).getParent()).getParent()).getImageLayerName();
					
						if(layerID >0)
							gui.removeImageLayer(layerID, layerName);
					} catch (Exception e1) {
						LOGGER.severe("Error in deleting ImageLayer: " +e1.getClass().toString() + " :" +e1.getMessage());

					}
				}
			});

		}
		else if(typeOfAction == ID.DELETE_MARKINGLAYER){
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						// remove Imagelayer by giving unique layerID and layername
						String markingLayerName=   ((SingleMarkingPanel)((JPanel)((JButton)e.getSource()).getParent()).getParent()).getMarking_layer_name();
						int mLayerID =   ((SingleMarkingPanel)((JPanel)((JButton)e.getSource()).getParent()).getParent()).getMarking_layer_id();
						int iLayerID= ((ImageLayerInfo)((JPanel)((SingleMarkingPanel)((JPanel)((JButton)e.getSource())
								.getParent()).getParent()).getParent()).getParent()).getImageLayerID();

							if(iLayerID >0)
							gui.removeMarkingLayer(iLayerID, mLayerID, markingLayerName);
					} catch (Exception e1) {
						LOGGER.severe("Error in deleting MarkingLayer" +e1.getClass().toString() + " :" +e1.getMessage());

					}
				}
			});


		}
		else // creating MarkingLayer
			if(typeOfAction == ID.CREATE_MARKINGLAYER){
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
							try {
								int iLayerID =  ((ImageLayerInfo)((JPanel)((JButton)arg0.getSource()).getParent().getParent())).getImageLayerID();
								if(iLayerID > 0){
									gui.createNewMarkingLayer(iLayerID);
								//	LOGGER.fine("create marking 1");
								}
							} catch (Exception e) {
								LOGGER.severe("Error in creating new MarkingLayer" +e.getClass().toString() + " :" +e.getMessage());
							}
					}
				});
			}
			else // creating MarkingLayer
				if(typeOfAction == ID.EDIT_MARKING_PROPERTIES){
					button.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
								try {

									int mLayerID= ((SingleMarkingPanel)((JButton)e.getSource()).getParent()).getMarking_layer_id();

									int iLayerID= ((ImageLayerInfo)((JPanel)((SingleMarkingPanel)((JButton)e.getSource()).getParent()).getParent()).getParent()).getImageLayerID();
								//	LOGGER.fine(" "+iLayerID);

										if(iLayerID >0 && mLayerID>0){
											MarkingLayer editingMarking = gui.getMarkingLayer(iLayerID, mLayerID);
											if(editingMarking != null){										
												MarkingProperties dialog = new MarkingProperties(gui, gui, ((JButton)e.getSource()).getLocationOnScreen(), editingMarking);
												gui.getGuiComponentListener().setChildDialog(dialog);
												dialog.showDialog();
												gui.getGuiComponentListener().setChildDialog(dialog); // set this dialog to GuiComponentListener
												dialog =null;
												gui.getGuiComponentListener().setChildDialog(null); // set childDialog to null
													
											}
										}
								} catch (Exception ex) {
									LOGGER.severe("Error in editing marking properties" +e.getClass().toString() + " : " +ex.getMessage());
								}
						}
					});
				}
				else // setting visibility of MarkingLayer
					if(typeOfAction == ID.SET_MARKING_LAYER_VISIBILITY){
						button.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
									try {
									//	LOGGER.fine("pressed visibility button");
										SingleMarkingPanel sPanel =(SingleMarkingPanel)((JButton)e.getSource()).getParent();
										boolean isVisible= sPanel.isVisibleLayer();
										int typeOfGrid = ID.GRID_NO_GRID;

										if(isVisible){
											sPanel.setVisibleLayer(false);
											((JButton)e.getSource()).setIcon(getImageIcon("/images/eye_closed_small.png"));
											if(sPanel.isGridON){
												typeOfGrid = ID.GRID_USED;													
												
											}
										}else{ // setting visible
											sPanel.setVisibleLayer(true);
											((JButton)e.getSource()).setIcon(getImageIcon("/images/eye_open_small.png"));											
											if(sPanel.isGridON){
												if(sPanel.isMarkingLayerSelected()){
													typeOfGrid =ID.GRID_SELECTED;
												}else{
													typeOfGrid = ID.GRID_USED;													
												}
											}	
																					
										}
																										
										sPanel.setGridButton(typeOfGrid);
										
										// set MarkingLayer visibilibity
										int mLayerID= sPanel.getMarking_layer_id();									
										gui.setMarkingLayerVisibility(mLayerID, !isVisible);
										
										// set ImageLayer icon 
										((ImageLayerInfo)((JPanel)((SingleMarkingPanel)((JButton)e.getSource()).getParent()).getParent()).getParent()).setEyeIcon();

									} catch (Exception ex) {
										LOGGER.severe("Error in editing marking properties" +e.getClass().toString() + " : " +ex.getMessage());
										ex.printStackTrace();
									}
							}
						});
					}
					else // setting title of MarkingLayer
						if(typeOfAction == ID.EDIT_MARKING_TITLE){
							button.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									try {
										int lastcaret = Math.max(((SingleMarkingPanel)((JButton)e.getSource()).getParent()).markingTitleJTextField.getText().length(), 0);
										((SingleMarkingPanel)((JButton)e.getSource()).getParent()).markingTitleJTextField.setCaretPosition(lastcaret);
										((SingleMarkingPanel)((JButton)e.getSource()).getParent()).markingTitleJTextField.setEnabled(true);
										((SingleMarkingPanel)((JButton)e.getSource()).getParent()).markingTitleJTextField.requestFocus();
										Font fontUsed= ((SingleMarkingPanel)((JButton)e.getSource()).getParent()).markingTitleJTextField.getFont();
										((SingleMarkingPanel)((JButton)e.getSource()).getParent()).markingTitleJTextField.setFont(new Font(fontUsed.getName(),Font.ITALIC,fontUsed.getSize()));
									} catch (Exception ex) {
										LOGGER.severe("Error in editing marking properties" +e.getClass().toString() + " : " +ex.getMessage());
										ex.printStackTrace();
									}
								}
							});
						}
						else // set imageLayer selected
							if(typeOfAction == ID.SELECTED_IMAGELAYER_JBUTTON){
								button.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
											try {
												int iLayerID = ((ImageLayerInfo)((JPanel)((JButton)e.getSource()).getParent()).getParent()).getImageLayerID();										
												gui.setSelectedImageLayerAndImage(iLayerID,ID.IMAGELAYER_REFRESH_VISIBILITY);

											} catch (Exception ex) {
												LOGGER.severe("Error in editing marking properties" +e.getClass().toString() + " : " +ex.getMessage());
												ex.printStackTrace();
											}
									}
								});
							}
							else // setting title of MarkingLayer
								if(typeOfAction == ID.IMAGELAYER_SET_GRID){
									button.addActionListener(new ActionListener() {
										@Override
										public void actionPerformed(ActionEvent e) {
											try {
												int iLayerID = ((ImageLayerInfo)((JPanel)((JButton)e.getSource()).getParent()).getParent()).getImageLayerID();												
												gui.showGridPropertiesPanelForMarkingLayersOfImageLayer(((JButton)e.getSource()).getLocationOnScreen(), iLayerID);

											} catch (Exception ex) {
												LOGGER.severe("Error in editing marking properties" +e.getClass().toString() + " : " +ex.getMessage());
												ex.printStackTrace();
											}
										}
									});
								}
								else // set grid for single MarkingLayer
									if(typeOfAction == ID.MARKINGLAYER_SET_GRID){
										button.addActionListener(new ActionListener() {
											@Override
											public void actionPerformed(ActionEvent e) {
												try {

													int mLayerID= ((SingleMarkingPanel)((JButton)e.getSource()).getParent()).getMarking_layer_id();

													if(mLayerID>0){
														gui.showGridPropertiesPanelForSingleMarkingLayer(((JButton)e.getSource()).getLocationOnScreen(), mLayerID);

													}

												} catch (Exception ex) {
													LOGGER.severe("Error in editing marking properties" +e.getClass().toString() + " : " +ex.getMessage());
													ex.printStackTrace();
												}
											}
										});
									}
	}

	/**
	 *  Adds MarkingLayers to markingArea JPanel.
	 *
	 * @param markingLayerList the marking layer list
	 * @throws Exception the exception
	 */
	private void addMarkingLayers(ArrayList<MarkingLayer> markingLayerList) throws Exception{

		if(markingLayerList != null && markingLayerList.size()>0){

			Iterator<MarkingLayer> iterator = markingLayerList.iterator();
			while (iterator.hasNext()) {
				MarkingLayer ma = (MarkingLayer) iterator.next();
				// if layerId not yet given to MarkingLayer
				if (ma.getLayerID() >0 && ma.getLayerName() != null && ma.getLayerName().length()>0) { 
					// increase the maximum height of area where SingleMarkingPanels are added
					this.markingArea.setMaximumSize(new Dimension((int)this.markingArea.getMaximumSize().getWidth(),(int)this.markingArea.getMaximumSize().getHeight()+25));
					// increase the maximum height of this SingleMarkingPanel
					this.setMaximumSize(new Dimension((int)this.getMaximumSize().getWidth(),(int)this.getMaximumSize().getHeight()+30));
					SingleMarkingPanel p = new SingleMarkingPanel(ma);
					this.markingArea.add(p);
					this.markingArea.add(Box.createRigidArea(new Dimension(0,5)));
				}

			}
		}

	}

	/**
	 * Adds the mouseListener to close buttons.
	 *
	 * @param button the button
	 * @throws Exception the exception
	 */
	private void addMouseListenerToCloseButtons(JButton button) throws Exception{
		button.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// do nothing

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 2));

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				((JButton)arg0.getSource()).setIcon(getImageIcon("/images/close_small_selected.png"));

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				((JButton)arg0.getSource()).setIcon(getImageIcon("/images/close_small.png"));

			}
		});
	}

	/**
	 * Adds the mouseListener to title.
	 *
	 * @param label the label
	 * @param type the type
	 */
	private void addMouseListenerToTitle(JLabel label, int type){
		if(type == ID.IMAGE_INFO)
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// do nothing

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// do nothing
			}

			@Override
			public void mousePressed(MouseEvent e) {
				try {
					int iLayerID=((ImageLayerInfo)((JPanel)((JPanel)((JLabel)e.getSource()).getParent()).getParent()).getParent()).getImageLayerID();
					//LOGGER.fine("Panel: " +((ImageLayerInfo)((JPanel)((JPanel)((JLabel)e.getSource()).getParent()).getParent()).getParent()).getImageLayerID());
					gui.setSelectedImageLayerAndImage(iLayerID, ID.IMAGELAYER_CHANGE_IMAGELAYER);
				} catch (Exception e1) {
					LOGGER.severe("Error in changing selected ImageLayer!");
					e1.printStackTrace();
				}

			}
			@Override
			public void mouseReleased(MouseEvent e) {
				// do nothing
			}
		});
		else
			if(type== ID.MARKING_INFO) // not doing anything yet
				label.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {
						// do nothing

					}

					@Override
					public void mouseEntered(MouseEvent e) {
						// do nothing
					}

					@Override
					public void mouseExited(MouseEvent e) {
						// do nothing
					}

					@Override
					public void mousePressed(MouseEvent e) {
						// do nothing -> this is done at SingleMarkingPanel ->
						//String iLayerID= e.getSource().getClass().toString();
						//LOGGER.fine("marking title:: " +((ImageLayerInfo)((JPanel)((JPanel)((JLabel)e.getSource()).getParent()).getParent()).getParent()).getImageLayerID());
						//gui.changeSelectedImageLayerAndImage(iLayerID);

					}
					@Override
					public void mouseReleased(MouseEvent e) {
						// do nothing
					}
				});
	}

	/**
	 * Creates the mouse listener to buttons.
	 *
	 * @param button the button
	 * @param typeOfButton the type of button
	 */
	private void createMouseListenerToButtons(JButton button, int typeOfButton){
		if(typeOfButton == ID.VISIBLEMARKINGJBUTTON){
		button.addMouseListener(new MouseListener() {
	
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// do nothing
	
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));
	
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));
	
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.orange_dark, 2));
	
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));
	
			}
		});
		}
		else if(typeOfButton==ID.SHAPEJBUTTON){
			button.addMouseListener(new MouseListener() {
	
				@Override
				public void mouseClicked(MouseEvent arg0) {
					// do nothing
	
				}
	
				@Override
				public void mouseEntered(MouseEvent arg0) {
					((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));
	
				}
	
				@Override
				public void mouseExited(MouseEvent arg0) {
					((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));
	
				}
	
				@Override
				public void mousePressed(MouseEvent arg0) {				
					((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.orange_dark, 1));
				}
	
				@Override
				public void mouseReleased(MouseEvent arg0) {
					((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));
				}
			});
		}else if(typeOfButton==ID.SELECTED_IMAGELAYER_JBUTTON){
			button.addMouseListener(new MouseListener() {
	
				@Override
				public void mouseClicked(MouseEvent arg0) {
					// do nothing
	
				}
	
				@Override
				public void mouseEntered(MouseEvent arg0) {
					((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));
	
				}
	
				@Override
				public void mouseExited(MouseEvent arg0) {
					((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 1));
	
				}
	
				@Override
				public void mousePressed(MouseEvent arg0) {
					
					((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.orange_dark, 1));
				}
	
				@Override
				public void mouseReleased(MouseEvent arg0) {
					
					((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 1));
				}
			});
		}
	
	}



	/**
	 * Creates and returns the image icon from given image path.
	 *
	 * @param path the path
	 * @return the image icon
	 */
	private ImageIcon getImageIcon(String path) {
	
			try {
				URL url = this.getClass().getResource(path);
				ImageIcon img = new ImageIcon(url);
				return img;
			} catch (Exception e) {
				LOGGER.severe("Error in adding SingleMarkingPanel " +e.getClass().toString() + " :" +e.getMessage());
				e.printStackTrace();
				return null;
			}
	}

/**
 * Returns the ID of ImageLayer.
 *
 * @return the image layer id
 * @throws Exception the exception
 */
public int getImageLayerID() throws Exception {
	return imageLayerID;
}

/**
 * Returns the image layer name.
 *
 * @return the image layer name
 * @throws Exception the exception
 */
public String getImageLayerName() throws Exception {
	return imageLayerName;
}

	/**
	 * Returns the width of JLabel with given text.
	 *
	 * @param font the font
	 * @param lab the lab
	 * @return the width of text
	 * @throws Exception the exception
	 */
	private int getWidthOfText(Font font, JLabel lab) throws Exception{

		try {
			return lab.getFontMetrics(font).stringWidth(lab.getText());
		} catch (Exception e) {
			LOGGER.severe("Error in  getting JLabel width: " +e.getClass().toString() + " :" +e.getMessage());
			return -1;
		}
	}

	/**
	 * Creates a JButton for adding new Marking Layer
	 * @throws Exception
	 */
	private void initAddMarkingButton() throws Exception{
		addMarkingLayerJPanel = new JPanel();
		addMarkingLayerJPanel.setLayout(new BoxLayout(addMarkingLayerJPanel, BoxLayout.PAGE_AXIS));
		addMarkingLayerJPanel.setMaximumSize(new Dimension(5000,35));
		addMarkingLayerJPanel.setPreferredSize(new Dimension(140, 35));
		addMarkingLayerJPanel.setBackground(Color_schema.dark_40);
		JButton addMarkingLayerJButton = new JButton("ADD NEW MARKING");
		addMarkingLayerJButton.setMaximumSize(new Dimension(220, 30));
		addMarkingLayerJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		addMarkingLayerJButton.setToolTipText("Create new Marking Layer for this Image Layer: " +getImageLayerName() );
		addMarkingLayerJButton.setBackground(Color_schema.dark_30);
		addMarkingLayerJButton.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
		addMarkingLayerJButton.setFocusable(false);

		addActionsToJButtons(addMarkingLayerJButton, ID.CREATE_MARKINGLAYER);
		addMarkingLayerJButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// do nothing

			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 2));

			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 2));
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
				((JButton)arg0.getSource()).setForeground(Color_schema.orange_dark);
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				((JButton)arg0.getSource()).setForeground(Color_schema.white_230);
			}
		});
		addMarkingLayerJPanel.add(Box.createRigidArea(new Dimension(0,2)));
		addMarkingLayerJPanel.add(addMarkingLayerJButton);
		addMarkingLayerJPanel.add(Box.createRigidArea(new Dimension(0,2)));

		this.add(addMarkingLayerJPanel,BorderLayout.PAGE_END);
	}
	

	
	/**
	 * Sets the eye icon for ImageLayerInfo -> ImageLayer title part.
	 *
	 * @throws Exception the exception
	 */
	private void setEyeIcon() throws Exception{
		ImageLayer iLayer = gui.getImageLayerByID(this.imageLayerID);
		if(iLayer != null)
			if(iLayer.isVisibleMarkingLayers())
				selectedImageLayerJButton.setIcon(getImageIcon("/images/eye_open.png"));
			else
				selectedImageLayerJButton.setIcon(getImageIcon("/images/eye_closed.png"));


	}

	/**
	 * Sets the ID of ImageLayer .
	 *
	 * @param layer_ID the new ID of ImageLayer
	 * @throws Exception the exception
	 */
	public void setImageLayer_ID(int layer_ID)throws Exception {
		this.imageLayerID = layer_ID;
	}
	
	/**
	 * Sets the name of ImageLayer.
	 *
	 * @param layer_name the new image layer name
	 * @throws Exception the exception
	 */
	public void setImageLayerName(String layer_name) throws Exception{
		this.imageLayerName = layer_name;
	}
	
	/**
	 * Changes font size for title text for getting it to fit to panel.
	 *
	 * @param text string which length affects to font size
	 * @param font the font
	 * @param bold the bold
	 * @return font which size has been set
	 * @throws Exception the exception
	 */
	private Font setRightTitleFont(String text, Font font, boolean bold) throws Exception{


			if(text != null && text.length()>20){
				if(bold){
					font=Fonts.b18;
				}
				else{
					font=Fonts.p18;
				}
				if(text.length()>30){
					if(bold){
						font=Fonts.b16;
					}
					else{
						font=Fonts.p16;
					}
					if(text.length()>40){
						if(bold){
							font=Fonts.b14;
						}
						else{
							font=Fonts.p14;
						}
					}
				}
			}
			return font;
	}


	/**
	 * Splits text if too long.
	 *
	 * @param text the text
	 * @return the string
	 * @throws Exception the exception
	 */
	private String splitTextIfTooLong(String text) throws Exception{
		try {
			if(text != null){
				if(text.length()>50){
				 // text is too long for title
					text = text.substring(0, 10) + "..." + text.substring(text.length()-10,text.length());
				}
				return text;

			}
			else{

				return "";
			}
		} catch (Exception e) {
			LOGGER.severe("Error splitting titles: " +e.getClass().toString() + " :" +e.getMessage());
			return text;
		}
	}
	
	/**
	 * Creates a JPanel containing information of single MarkingLayer.
	 * @author Antti Kurronen
	 *
	 */
	private class SingleMarkingPanel extends JPanel {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -3674159779176115654L;
		
		/** The marking_layer_id. */
		private int marking_layer_id;
		
		/** The is visible layer. */
		private boolean isVisibleLayer = true;
		
		/** The is grid on. */
		private boolean isGridON=false;
		
		/** The marking_layer_name. */
		private String marking_layer_name;
		
		/** The marking title j text field. */
		private JTextField markingTitleJTextField;
		
		/** The close j button. */
		private JButton closeJButton;
		
		/** The count j label. */
		private JLabel countJLabel;
		
		/** The marking layer. */
		private MarkingLayer markingLayer;
		
		/** The grid button. */
		private JButton gridButton;

		/**
		 * Instantiates a new single marking panel for info part.
		 *
		 * @param markingLayer the marking layer
		 */
		private SingleMarkingPanel(MarkingLayer markingLayer){
			try {
				this.markingLayer=markingLayer;
				setVisibleLayer(markingLayer.isVisible());
				setGridON(markingLayer.isGridON());
				this.setMarking_layer_name(markingLayer.getLayerName());
				this.marking_layer_id= markingLayer.getLayerID();
				this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
				this.setBorder(BorderFactory.createEmptyBorder());
				this.setBackground(Color_schema.dark_70);
				this.setMaximumSize(new Dimension(5000,25));
				this.setMinimumSize(new Dimension(20,25));
				JButton visibleMarkingJButton = new JButton();
				if(markingLayer.isVisible())
					visibleMarkingJButton.setIcon(getImageIcon("/images/eye_open_small.png"));
				else
					visibleMarkingJButton.setIcon(getImageIcon("/images/eye_closed_small.png"));

				visibleMarkingJButton.setAlignmentX(Component.LEFT_ALIGNMENT);
				visibleMarkingJButton.setPreferredSize(new Dimension(37,20));
				visibleMarkingJButton.setMargin(new Insets(0, 0, 0, 0));
				visibleMarkingJButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));
				visibleMarkingJButton.setContentAreaFilled(false);
				visibleMarkingJButton.setFocusable(false);
				visibleMarkingJButton.setToolTipText("Change visibility of MarkingLayer");

				addActionsToJButtons(visibleMarkingJButton, ID.SET_MARKING_LAYER_VISIBILITY);
				createMouseListenerToButtons(visibleMarkingJButton, ID.VISIBLEMARKINGJBUTTON);

				ShapeIcon icon = new ShapeIcon(markingLayer.getShapeID(), 22, 22, markingLayer.getColor(), this.getBackground());
				JButton shapeJButton = new JButton(icon);
		
				shapeJButton.setAlignmentX(Component.LEFT_ALIGNMENT);
				shapeJButton.setPreferredSize(new Dimension(22,27));
				shapeJButton.setMargin(new Insets(0, 0, 0, 0));
				shapeJButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));
				shapeJButton.setContentAreaFilled(false);
				shapeJButton.setFocusable(false);
				shapeJButton.setToolTipText("Edit shape and color of markings ");
				addActionsToJButtons(shapeJButton, ID.EDIT_MARKING_PROPERTIES);
				createMouseListenerToButtons(shapeJButton, ID.SHAPEJBUTTON);

				gridButton = new JButton();
				gridButton.setPreferredSize(new Dimension(25,25));
				gridButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));

				gridButton.setContentAreaFilled(false);
				gridButton.setFocusable(false);
				gridButton.setToolTipText("Edit grid of MarkingLayer");
				addActionsToJButtons(gridButton, ID.MARKINGLAYER_SET_GRID);
				MouseListenerCreator.addMouseListenerToButtonsWithMarkingWith70Border(gridButton);
				if(this.markingLayer.isGridON()){
					if(markingLayer.isSelected() && markingLayer.isVisible()){
						gridButton.setIcon(getImageIcon("/images/g_selected_red.png"));
					}
					else{
						gridButton.setIcon(getImageIcon("/images/g_selected.png"));					
					}
				}
				else{
					gridButton.setIcon(getImageIcon("/images/g_unselected_lighter.png"));
				}

				JButton editMarkingTitleJButton = new JButton();

				editMarkingTitleJButton.setIcon(getImageIcon("/images/editPen_small.png"));

				editMarkingTitleJButton.setAlignmentX(Component.LEFT_ALIGNMENT);
				editMarkingTitleJButton.setPreferredSize(new Dimension(25,25));
				editMarkingTitleJButton.setMargin(new Insets(0, 0, 0, 0));
				editMarkingTitleJButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));
				editMarkingTitleJButton.setContentAreaFilled(false);
				editMarkingTitleJButton.setToolTipText("Edit title of MarkingLayer");
				addActionsToJButtons(editMarkingTitleJButton, ID.EDIT_MARKING_TITLE);
				createMouseListenerToButtons(editMarkingTitleJButton, ID.VISIBLEMARKINGJBUTTON); // can use same listeners as to visibleMarkingButton


				countJLabel = new JLabel(""+markingLayer.getCounts());
				countJLabel.setFont(Fonts.b17);
				countJLabel.setForeground(Color_schema.markingInfoPanel_fg);
				countJLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

				markingTitleJTextField = new JTextField();
				markingTitleJTextField.setText(this.getMarking_layer_name());
				markingTitleJTextField.setCaretPosition(0);
				markingTitleJTextField.setEnabled(false);
				markingTitleJTextField.setFont(Fonts.p17);
				markingTitleJTextField.setForeground(Color_schema.white_230);
				markingTitleJTextField.setMaximumSize(new Dimension(gui.getRightPanelWidth()-170-getCountingTextWidth(),25));
				markingTitleJTextField.setPreferredSize(new Dimension(gui.getRightPanelWidth()-170-getCountingTextWidth(),25));

				if(markingLayer.isSelected()){
					markingTitleJTextField.setFont(Fonts.b18);
					markingTitleJTextField.setDisabledTextColor(Color_schema.orange_bright);
					markingTitleJTextField.setForeground(Color_schema.orange_bright);
				}
				markingTitleJTextField.setBackground(null);
				markingTitleJTextField.setBorder(null);
				markingTitleJTextField.setCaretColor(Color_schema.button_light_border);
				if(this.marking_layer_name.length()>50)
					markingTitleJTextField.setToolTipText(this.marking_layer_name);
				markingTitleJTextField.addCaretListener(new CaretListener() {

			        @Override
			        public void caretUpdate(CaretEvent e) {
			        	try {
							setMarking_layer_name(markingTitleJTextField.getText());

							closeJButton.setToolTipText("Delete Marking Layer: " +getMarking_layer_name());
						} catch (Exception e1) {
							LOGGER.severe("Error when updating title of MarkingLayer!");
							e1.printStackTrace();
						}

			        }
			    });

				markingTitleJTextField.addKeyListener(new KeyListener() {

					@Override
					public void keyPressed(KeyEvent arg0) {



					}

					@Override
					public void keyReleased(KeyEvent arg0) {
						try {
							int imageID = ((ImageLayerInfo)((JPanel)((JPanel)((JTextField)arg0.getSource())
									.getParent()).getParent()).getParent()).getImageLayerID();

							int markingID = ((SingleMarkingPanel)((JTextField)arg0.getSource()).getParent()).getMarking_layer_id();
							String markingName = ((JTextField)arg0.getSource()).getText();
							gui.setMarkingLayerName(imageID, markingID, markingName);
							if(arg0.getKeyCode()== KeyEvent.VK_ENTER){
								title.requestFocus();


							}
						} catch (Exception e) {
							LOGGER.severe("Error in changing name of MarkingLayer!");
							e.printStackTrace();
						}


					}

					@Override
					public void keyTyped(KeyEvent arg0) {
						// do nothing

					}
				});

				markingTitleJTextField.addFocusListener(new FocusListener() {

					@Override
					public void focusGained(FocusEvent e) {
						// do nothing

					}

					@Override
					public void focusLost(FocusEvent e) {
						try {
							markingTitleJTextField.setEnabled(false);

//	System.out.println("text par"+((JTextField)e.getSource()).getParent().getClass().toString());
							if(((SingleMarkingPanel)((JTextField)e.getSource()).getParent()).isMarkingLayerSelected())
								markingTitleJTextField.setFont(Fonts.b18);
							else
								markingTitleJTextField.setFont(Fonts.p17);
							
							markingTitleJTextField.setCaretPosition(0);
						} catch (Exception e1) {
							LOGGER.severe("Error when focus lost from title of MarkingLayer!");
							e1.printStackTrace();
						}
						
						
						
					}
				});



				markingTitleJTextField.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {
						// do nothing

					}

					@Override
					public void mouseEntered(MouseEvent e) {
						// do nothing

					}

					@Override
					public void mouseExited(MouseEvent e) {
						// do nothing

					}

					@Override
					public void mousePressed(MouseEvent e) {
						try {
							if(!markingTitleJTextField.isEnabled()){
								
								gui.setSelectedMarkingLayer(marking_layer_id);
							}
						} catch (Exception e1) {
							LOGGER.severe("Error in pressing title of MarkingLayer!");
							e1.printStackTrace();
						}

					}

					@Override
					public void mouseReleased(MouseEvent e) {
					// do nothing
					}
				});




				JPanel countingJPanel =new JPanel();
				countingJPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 1, 1));
				countingJPanel.setBackground(Color_schema.dark_70);
				countingJPanel.add(countJLabel);
				countingJPanel.setMaximumSize(new Dimension(5000,20));
				countingJPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
				countingJPanel.add(Box.createRigidArea(new Dimension(5,0)));

				//Add closing box to right of markinginfo	
				closeJButton = new JButton(getImageIcon("/images/close_small.png"));
				closeJButton.setAlignmentY(Component.CENTER_ALIGNMENT);
				closeJButton.setPreferredSize(new Dimension(18,18));
				closeJButton.setMargin(new Insets(0, 0, 0, 0));
				closeJButton.setBorder(BorderFactory.createEmptyBorder());
				closeJButton.setContentAreaFilled(false);
				closeJButton.setFocusable(false);
				closeJButton.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
				closeJButton.setToolTipText("Delete Marking Layer: " +getMarking_layer_name());
				MouseListenerCreator.addMouseListenerToSmallCloseButtons(closeJButton);
				addActionsToJButtons(closeJButton, ID.DELETE_MARKINGLAYER);

				countingJPanel.add(closeJButton);
				countingJPanel.add(Box.createRigidArea(new Dimension(3,0)));

				this.add(visibleMarkingJButton); //
				this.add(shapeJButton);
				this.add(gridButton);
				this.add(editMarkingTitleJButton);
				this.add(Box.createRigidArea(new Dimension(5,0)));
				this.add(markingTitleJTextField);
				this.add(Box.createHorizontalGlue());
				this.add(countingJPanel);



			} catch (Exception e) {
				// do nothing
				LOGGER.severe("Error in adding SingleMarkingPanel " +e.getClass().toString() + " :" +e.getMessage());
				e.printStackTrace();
			}
		}


		/**
		 * Returns the counting text width.
		 *
		 * @return the counting text width
		 * @throws Exception the exception
		 */
		private int getCountingTextWidth() throws Exception{
			return getWidthOfText(this.countJLabel.getFont(), this.countJLabel);
		}
		
		/**
		 * Sets the grid button.
		 *
		 * @param id the new grid button
		 * @throws Exception the exception
		 */
		public void setGridButton(int id) throws Exception{
			if(id == ID.GRID_SELECTED){
			this.gridButton.setIcon(getImageIcon("/images/g_selected_red.png"));
			}
			else if(id == ID.GRID_USED){
				this.gridButton.setIcon(getImageIcon("/images/g_selected.png"));
			}else{
				gridButton.setIcon(getImageIcon("/images/g_unselected_lighter.png"));
			}
		
		}


		/**
		 * Returns the ID of MarkingLayer.
		 *
		 * @return the marking_layer_id
		 * @throws Exception the exception
		 */
		public int getMarking_layer_id() throws Exception{
			return marking_layer_id;
		}

		/**
		 * Returns the name of MarkingLayer.
		 *
		 * @return the marking_layer_name
		 * @throws Exception the exception
		 */
		public String getMarking_layer_name() throws Exception{
			return marking_layer_name;
		}

		/*
		 * Checks if is grid on.
		 *
		 * @return true, if is grid on
		 * @throws Exception the exception
		 
		public boolean isGridON()throws Exception {
			return isGridON;
		}
*/
		/**
		 * Checks if is marking layer selected.
		 *
		 * @return true, if is marking layer selected
		 * @throws Exception the exception
		 */
		public boolean isMarkingLayerSelected() throws Exception{
			try {
				if(this.markingLayer!= null){
					return this.markingLayer.isSelected();
				}
				return false;
			} catch (Exception e) {
				LOGGER.severe("Error in checking is MarkingLayer selected !");
				e.printStackTrace();
				return false;
			}
		}


		/**
		 * Checks if is visible layer.
		 *
		 * @return true, if is visible layer
		 * @throws Exception the exception
		 */
		public boolean isVisibleLayer() throws Exception{
			return isVisibleLayer;
		}

		/**
		 * Sets the grid on.
		 *
		 * @param isGridON the new grid on
		 * @throws Exception the exception
		 */
		public void setGridON(boolean isGridON) throws Exception {
			this.isGridON = isGridON;
		}
		
		/**
		 * sets this Marking Layer name.
		 *
		 * @param marking_layer_name name for Marking Layer
		 * @throws Exception the exception
		 */
		public void setMarking_layer_name(String marking_layer_name) throws Exception{
			this.marking_layer_name = marking_layer_name;
		}

		/**
		 * Sets the layer visibility.
		 *
		 * @param isVisibleLayer the new visible layer
		 * @throws Exception the exception
		 */
		public void setVisibleLayer(boolean isVisibleLayer) throws Exception{
			this.isVisibleLayer = isVisibleLayer;
		}

		
	}

}

package gui;


import gui.panels.ImagePanel;
import information.ID;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

import managers.TaskManager;

/**
 * Class GUIListener contains all mouse and key listeners for GUI-object. 
 * Keys: CTRL, SHIFT, SPACE and several other keys and combinations.
 * Timers: Space pressed down. highlighting.
 * Mouse Listeners: clicked, dragged, entered, exited, pressed, released, wheel moved.
 * @author Antti Kurronen
 *
 */
public class GUIListener extends MouseInputAdapter {
	
	/** The CTRL pressed. */
	private boolean is_CTRL_pressed = false;
	
	/** The space pressed. */
	private boolean is_SPACE_pressed = false;
	
	/** The shift pressed. */
	private boolean is_SHIFT_pressed=false;
	
	/** The is cell picking on. */
	private boolean isCellPickingON=false;
	
	/** The previous dragging point.  */
	private Point previousDraggingPoint=null;
	
	/** The GUI. */
	private GUI gui;
	
	/** The task manager. */
	private TaskManager taskManager;
	
	/** The Timer for activating SPACE. */
	private Timer timerSPACEactivate;
	
	/** The Timer for inactivating SPACE. */
	private Timer timerSPACEinactivate;
	
	/** The Timer for highlighting markings. */
	private Timer hightLightTimer;
	
	/** The Timer for zooming. Zooming operations is restricted to every 100 ms.*/
	private Timer zoomTimer;
	
	/** The content pane. Source from GUI. */
	private Container contentPane;
	
	/** The glass pane. Source from GUI. */
	private PrecountGlassPane glassPane;
	
	/** The JButton for precount. Source from GUI. */
	private AbstractButton precountButton;
	
	/** The image panel. Source from GUI.*/
	private ImagePanel imagePanel;
	
	/** The JLayeredPane for layers. Source from GUI. */
	private JLayeredPane layers;
	
	/** The down bar panel. Source from GUI. */
	private JPanel downBarPanel;
	
	/** The zoom slider. Source from GUI. */
	private JSlider zoomSlider;

	/** The Constant LOGGER for Logging purposes. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/**
	 * A Class constructor. Inits the timers.
	 * @param gui GUI main window which is listened
	 */
	public GUIListener(GUI gui){
		try {
			this.gui=gui;
			initSPACEactions();
			initHighlightTimer();
			initZoomTimer();
		} catch (Exception e) {
			LOGGER.severe("Error in initializing Listener for Graphical user interface. Relaunch program.");
			e.printStackTrace();
		}
	}

	/**
	 * Adds shortcut keys to given components.
	 *
	 * @param component the component where shortcut keys are added.
	 * @param componentID the component ID
	 */
	public void addKeyInputMap(JComponent component, int componentID){
		try {
			if(componentID == ID.IMAGE_PANEL || componentID== ID.GLASS_PANE){
				InputMap inputMap= (component).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
				inputMap.put(KeyStroke.getKeyStroke("pressed SPACE"), "space_pressed");
				inputMap.put(KeyStroke.getKeyStroke("released SPACE"), "space_released");
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, KeyEvent.SHIFT_DOWN_MASK), "shift_pressed");
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,0,true), "shift_released");
				ActionMap actionMap = 	(component).getActionMap();

				actionMap.put("space_pressed", new AbstractAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						
						try {
							if(!timerSPACEactivate.isRunning()){
								timerSPACEactivate.start();	
								
							}
							if(timerSPACEinactivate.isRunning())
								timerSPACEinactivate.stop();
						} catch (Exception e1) {
							LOGGER.severe("Error in actions by pressed space key!");
							e1.printStackTrace();
						}

					}
				});

					actionMap.put("space_released",new AbstractAction() {

					/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						
						try {
							if(timerSPACEactivate.isRunning() && !timerSPACEinactivate.isRunning()){
								timerSPACEinactivate.start();
								
							}
						} catch (Exception e1) {
							LOGGER.severe("Error in actions by released space key!");
							e1.printStackTrace();
						}


					}
				});

					actionMap.put("shift_pressed", new AbstractAction() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								is_SHIFT_pressed=true;
							} catch (Exception e1) {
								LOGGER.severe("Error in action by pressed shift key!");
								e1.printStackTrace();
							}

						}
					});

					actionMap.put("shift_released", new AbstractAction() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								is_SHIFT_pressed=false;
							} catch (Exception e1) {
								LOGGER.severe("Error in action by released shift key!");
								e1.printStackTrace();
							}

						}
					});

			}else if(componentID == ID.RIGHT_PANEL){

				InputMap inputMap= (component).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
				// LEFT
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0,true), "left_pressed");
				// RIGHT
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0,true), "right_pressed");
				// UP
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0,true), "up_pressed");

				//DOWN
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0,true), "down_pressed");
				
				// PAGE UP
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0,true), "page_up_pressed");
				
				// PAGE DOWN
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0,true), "page_down_pressed");

				ActionMap actionMap = 	(component).getActionMap();


				actionMap.put("left_pressed", new AbstractAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 4L;

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							// change selected ImageLayer one up
							gui.changeSelectedImageLayerUpOrDown(ID.MOVE_UP);
						} catch (Exception e1) {
							LOGGER.severe("Error in moving selected ImageLayer");
							e1.printStackTrace();
						}
					}
				});

				actionMap.put("right_pressed", new AbstractAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 5L;

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							// change selected ImageLayer one down
							gui.changeSelectedImageLayerUpOrDown(ID.MOVE_DOWN);
						} catch (Exception e1) {
							LOGGER.severe("Error in moving selected ImageLayer");
							e1.printStackTrace();
						}
					}
				});

				actionMap.put("up_pressed", new AbstractAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 6L;

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							// change selected MarkingLayer one up
							gui.changeSelectedMarkingLayerUpOrDown(ID.MOVE_UP);
						} catch (Exception e1) {
							LOGGER.severe("Error in changing MarkingLayer!");
							e1.printStackTrace();
						}
					}
				});

				actionMap.put("down_pressed", new AbstractAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 7L;

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							// change selected MarkingLayer one down

							gui.changeSelectedMarkingLayerUpOrDown(ID.MOVE_DOWN);
						} catch (Exception e1) {
							LOGGER.severe("Error in changing MarkingLayer!");
							e1.printStackTrace();
						}
					}
				});
				
				actionMap.put("page_up_pressed", new AbstractAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 8L;

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							// change selected MarkingLayer one up

							gui.moveSelectedMarkingLayer(ID.MOVE_UP);
						} catch (Exception e1) {
							LOGGER.severe("Error in changing selected MarkingLayer!");
							e1.printStackTrace();
						}
					}
				});
				
				actionMap.put("page_down_pressed", new AbstractAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 9L;

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							// change selected MarkingLayer one down

							gui.moveSelectedMarkingLayer(ID.MOVE_DOWN);
						} catch (Exception e1) {
							LOGGER.severe("Error in changing MarkingLayer!");
							e1.printStackTrace();
						}
					}
				});
				
				

			} // whole window listening keys
			else if(componentID == ID.WHOLE_GUI_FRAME){

				InputMap inputMap= (component).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
				// CTRL
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, KeyEvent.CTRL_DOWN_MASK), "ctrl_pressed");
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL,0,true), "ctrl_released");
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "save_pressed");
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK), "export_images_pressed");
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK), "manage_layers_pressed");
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK), "add_layers_pressed");
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "export_csv_pressed");
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK), "export_tab_pressed");
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK), "export_clip_pressed");		
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK |KeyEvent.CTRL_DOWN_MASK ), "show_all_markings_pressed");
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.SHIFT_DOWN_MASK |KeyEvent.CTRL_DOWN_MASK ), "hide_all_markings_pressed");
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK), "zoom_out_pressed");
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK), "zoom_in_pressed");


				ActionMap actionMap = 	(component).getActionMap();


				actionMap.put("ctrl_pressed", new AbstractAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 12L;

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							is_CTRL_pressed=true;
						} catch (Exception e1) {
							LOGGER.severe("Error in action by pressed control key!");
							e1.printStackTrace();
						}

					}
				});

				actionMap.put("ctrl_released", new AbstractAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 13L;

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							is_CTRL_pressed=false;
						} catch (Exception e1) {
							LOGGER.severe("Error in action by released control key!");
							e1.printStackTrace();
						}

					}
				});
				//Save markings
				actionMap.put("save_pressed", new AbstractAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 14L;

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							gui.saveMarkings();
						} catch (Exception e1) {
							LOGGER.severe("Error in saving markings!");
							e1.printStackTrace();
						}

					}
				});
					// export csv
					actionMap.put("export_csv_pressed", new AbstractAction() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 15L;

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								gui.exportResults(ID.FILE_TYPE_CSV);
							} catch (Exception e1) {
								LOGGER.severe("Error in erporting results to CSV file");
								e1.printStackTrace();
							}

						}
					});
					// export images
					actionMap.put("export_images_pressed", new AbstractAction() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 16L;

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								gui.exportImages();
							} catch (Exception e1) {
								LOGGER.severe("Error in exporting images!");
								e1.printStackTrace();
							}

						}
					});
					// managing layers
					actionMap.put("manage_layers_pressed", new AbstractAction() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 17L;

						@Override
						public void actionPerformed(ActionEvent e) {
							
								try {
									gui.manageImageLayersAndMarkings();
								} catch (Exception e1) {
									LOGGER.severe("Error in action by pressed keys for managing Layers!");
									e1.printStackTrace();
								}
							

						}
					});
					// adding layers
					actionMap.put("add_layers_pressed", new AbstractAction() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 18L;

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								gui.openAddImageLayerDialog(null);
							} catch (Exception e1) {
								LOGGER.severe("Error in action by pressed key for adding layers!");
								e1.printStackTrace();
							}

						}
					});
					// exportin tab delimited file
					actionMap.put("export_tab_pressed", new AbstractAction() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 19L;

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								gui.exportResults(ID.FILE_TYPE_TEXT_FILE);
							} catch (Exception e1) {
								LOGGER.severe("Error in exporting result to text file");
								e1.printStackTrace();
							}

						}
					});
					// export results to clipboard
					actionMap.put("export_clip_pressed", new AbstractAction() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 20L;

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								gui.exportResults(ID.CLIPBOARD);
							} catch (Exception e1) {
								LOGGER.severe("Error in exporting results to clipboard!");
								e1.printStackTrace();
							}

						}
					});
					// hiding all markings
					actionMap.put("hide_all_markings_pressed", new AbstractAction() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 221L;

						@Override
						public void actionPerformed(ActionEvent e) {
								try {
									gui.setVisibilityOfAllMarkingLayers(false);
								} catch (Exception e1) {
									LOGGER.severe("Error in changing MarkingLayer unvisible!");
									e1.printStackTrace();
								}
							

						}
					});
					// showin all markings
					actionMap.put("show_all_markings_pressed", new AbstractAction() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 222L;

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								gui.setVisibilityOfAllMarkingLayers(true);
							} catch (Exception e1) {
								LOGGER.severe("Error in changing MarkingLayer visible!");
								e1.printStackTrace();
							}

						}
					});
					// zooming out
					actionMap.put("zoom_out_pressed", new AbstractAction() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 223L;

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								gui.zoomAndUpdateImage(new Point((int)(imagePanel.getWidth()/2), (int)(imagePanel.getHeight()/2)), 0.8, ID.IMAGE_PROCESSING_BEST_QUALITY);
							} catch (Exception e1) {
								LOGGER.severe("Error in changing MarkingLayer!");
								e1.printStackTrace();
							}
						
						}
					});
					// zooming in
					actionMap.put("zoom_in_pressed", new AbstractAction() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 224L;

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								gui.zoomAndUpdateImage(new Point((int)(imagePanel.getWidth()/2), (int)(imagePanel.getHeight()/2)), 1.25, ID.IMAGE_PROCESSING_BEST_QUALITY);
							} catch (Exception e1) {
								LOGGER.severe("Error in zooming!");
								e1.printStackTrace();
							}
						
						}
					});

			}
		} catch (Exception e) {
			LOGGER.severe("ERROR in keyboard keys. Keyboard buttons may not work!");
			e.printStackTrace();
		}

	}


	/**
	 * MouseEvents made on GlassPane are redirected to lower layer components if necessary. 
	 * @param e MouseEvents the mouse event which is forwarded.
	 */
	private void forwardGlassPaneEvent(MouseEvent e){

		Point glassPanePoint = e.getPoint();
		Point containerPoint = SwingUtilities.convertPoint(glassPane, glassPanePoint, contentPane);
		Point imagePanelPoint = SwingUtilities.convertPoint(contentPane, containerPoint, layers);
		Point downBarPoint = SwingUtilities.convertPoint(contentPane, containerPoint, downBarPanel);
		Point preCountButtonPoint = SwingUtilities.convertPoint(downBarPanel, downBarPoint, precountButton);

		if(containerPoint.y >=0){
			Component guiComponent=null;
				// check does imagePanel containg the point were mouse was pressed
				if(imagePanel.contains(imagePanelPoint)){
					guiComponent=imagePanel;
					switch (e.getID()){
					case MouseEvent.MOUSE_PRESSED:
						// start counting
						if(!is_CTRL_pressed && !is_SPACE_pressed){

							if(e.getButton()== MouseEvent.BUTTON1){
								try {
									// left mouse pressed -> start counting
									gui.startCellCounting(imagePanelPoint, this.glassPane.getRectangleSize());
								} catch (Exception e1) {
									LOGGER.severe("Error in starting precounting!");
									e1.printStackTrace();
								}
							}
						}
						else{
							try {
								// zooming is redirected to components under glasspane
								redirectEventToGUIComponents(e, glassPanePoint, guiComponent);
							} catch (Exception e1) {
								LOGGER.severe("Error in redirecting zoomin under glasspane!");
								e1.printStackTrace();
							}
						}
						break;

					case MouseEvent.MOUSE_MOVED:
						try {
							paintCircle(imagePanelPoint, glassPanePoint);
						} catch (Exception e1) {
							LOGGER.severe("Error in painting precountin picking circle when mouse moved!");
							e1.printStackTrace();
						}
						break;

					case MouseEvent.MOUSE_DRAGGED:
						try {
							paintCircle(imagePanelPoint, glassPanePoint);
							//redirect to components under glasspane
							redirectEventToGUIComponents(e, glassPanePoint, guiComponent);
						} catch (Exception e1) {
							LOGGER.severe("Error in painting precountin picking circle when mouse dragged!");
							e1.printStackTrace();
						}
						break;

					default:
						try {
							//redirect to components under glasspane
							redirectEventToGUIComponents(e, glassPanePoint, guiComponent);
						} catch (Exception e1) {
							LOGGER.severe("Error in redirecting default actions under glasspane!");
							e1.printStackTrace();
						}
						break;

				}

			}
				// check does downbarPanel containg the point were mouse was pressed
				else if(downBarPanel.contains(downBarPoint)){
					try {
						if(precountButton.contains(preCountButtonPoint)){
							guiComponent=precountButton;
							if(e.getID() == MouseEvent.MOUSE_RELEASED){			
								// redirect for button					
								gui.startStopCellPicking();
							}
						}
						else { // third option is zoomSlider
							guiComponent=zoomSlider;

							// redirect for slider
							redirectEventToGUIComponents(e, glassPanePoint, guiComponent);
						}
					} catch (Exception e1) {
						LOGGER.severe("Error in checking does downbar panel containg the point where mouse was pressed!");
						e1.printStackTrace();
					}
				}
		}
	}

	/**
	 * Gets the previous dragging point.
	 *
	 * @return the previous dragging point
	 * @throws Exception the exception
	 */
	
	public Point getPreviousDraggingPoint() throws Exception {
		return previousDraggingPoint;
	}

	/**
	 * Inits the highlight timer.
	 *
	 * @throws Exception the exception
	 */
	private void initHighlightTimer() throws Exception{
		this.hightLightTimer=new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(hightLightTimer.isRunning())
					hightLightTimer.stop();
			}
		});
	}


	/**
	 * Initializes the space action timers for activating and inactivating dragging.
	 *
	 * @throws Exception the exception
	 */
	private void initSPACEactions() throws Exception{
		timerSPACEactivate=new Timer(50,new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				is_SPACE_pressed=true;
				
				try {
					// set cursor hand cursor - dragging state
					gui.setCursorOverLeftPanel(ID.CURSOR_HAND);
				} catch (Exception e1) {
					LOGGER.severe("Error in changing cursor!");
					e1.printStackTrace();
				}
				

			}
		});

		timerSPACEinactivate = new Timer(110,new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// stop activate timer
					timerSPACEactivate.stop();
					is_SPACE_pressed=false;
					timerSPACEinactivate.stop();
					// set cross hair cursor - default state
					gui.setCursorOverLeftPanel(ID.CURSOR_CROSS_HAIR);
					previousDraggingPoint=null; // no more dragging -> initialize the previousdragging point
					// update visible image and markings
					gui.setImage(taskManager.getRefreshedImage());
					gui.updateCoordinatesOfVisibleMarkingPanels();
					
					gui.paintLayers();
				} catch (Exception e1) {
					LOGGER.severe("Error in space timer!");
					e1.printStackTrace();
				}

			}
		});
	}

	/**
	 * Initializes the timer for zooming.
	 *
	 * @throws Exception the exception
	 */
	private void initZoomTimer() throws Exception{
		this.zoomTimer=new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(zoomTimer.isRunning())
					zoomTimer.stop();

			}
		});
	}

	/**
	 * Returns true if precounting Thread is running. Otherwise false.
	 *
	 * @return boolean true if Precounting Thread is running.
	 * @throws Exception the exception
	 */
	public boolean isCellPickingON() throws Exception {
		return isCellPickingON;
	}

	/**
	 * Checks if is is space pressed.
	 *
	 * @return true, if space is pressed
	 */
	public boolean isIs_SPACE_pressed() {
		return is_SPACE_pressed;
	}

	/** 
	 * When mouse clicked over ImagePanel and ctrl is pressed down the zooming is launched. Left-button: zoom in; right-button: zoom out.
	 * If mouse was pressed on glasspane (Precounting running): he event is forwarded to method forwardGlassPaneEvent.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			if(e.getSource() instanceof ImagePanel){
				if(is_CTRL_pressed){
					double zoomValue= 0.8;
					//Button1 -> zoom in
					if(e.getButton() == MouseEvent.BUTTON1)
						zoomValue=1.25;
					gui.zoomAndUpdateImage(e.getPoint(), zoomValue, ID.IMAGE_PROCESSING_BEST_QUALITY);
				}
			}
			else if(e.getSource() instanceof PrecountGlassPane){
				forwardGlassPaneEvent(e);
			}
		} catch (Exception e1) {
			LOGGER.severe("Error in clicking mouse button!");
			e1.printStackTrace();
		}


	}

	/** 
	 * Mediates the mouseWheelMoved Event to wanted procedure. Events of PrecountGlassPane are forwarded to forwardGlassPaneEvent(..).
	 * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		try {
			if(e.getSource() instanceof ImagePanel){
				if(is_SPACE_pressed){
					gui.dragLayers(e);
					is_SPACE_pressed=false;
					if(timerSPACEactivate.isRunning())
					timerSPACEactivate.start(); // start counting from beginning
					
				}
			}else if(e.getSource() instanceof PrecountGlassPane){
				forwardGlassPaneEvent(e);
			}
		} catch (Exception e1) {
			LOGGER.severe("Error in dragging mouse!");
			e1.printStackTrace();
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// do nothing
	}

	/**
	 *  Mediates the mouseWheelMoved Event to wanted procedure. Events of PrecountGlassPane are forwarded to forwardGlassPaneEvent(..).
	 * @see java.awt.event.MouseAdapter#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		try {
			if(e.getSource() instanceof PrecountGlassPane){
				forwardGlassPaneEvent(e);

			}
			else if(e.getSource() instanceof ImagePanel){
				if(!hightLightTimer.isRunning()){
					hightLightTimer.start();
				//	System.out.println("moving");
					gui.updateHighlight(e.getPoint());
				}

			}
		} catch (Exception e1) {
			LOGGER.severe("Error in moving mouse!");
			e1.printStackTrace();
		}



	}

	/**
	 * Mediates the MousePressed Event to wanted procedure. Events of PrecountGlassPane are forwarded to forwardGlassPaneEvent(..).
	 * Computation of events of ImagePanel depends on which keys are pressed down or which threads are running at same time:
	 * CTRL-down: do nothing
	 * SPACE-down: dragging: set init dragging point.
	 * SHIFT-down: select/unselect grid cell.
	 * MOUSE-LEFT-button: add marking
	 * MOUSE-RIGHT-button: remove marking and it's highlight point.
	 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		try {
			if(e.getSource() instanceof ImagePanel){
			if(!is_CTRL_pressed){
					// started dragging
				if(previousDraggingPoint==null && timerSPACEactivate.isRunning()){
					previousDraggingPoint = new Point(e.getPoint().x, e.getPoint().y);
				}
				else{

						if(taskManager.isSelectedMarkingLayerVisible()){

							if(is_SHIFT_pressed){
								// setting selected Grids
								gui.setGridSelectedRectangle(e.getPoint());
							}
							else{
								// adding or deleting single marking
								if(e.getButton() == MouseEvent.BUTTON1){ // left button -> add marking
									if(gui.addSingleMarking(e.getPoint()))
										gui.updateCoordinatesOfSelectedMarkingPanel();
								}
								else { // right button -> remove marking

									if(gui.removeSingleMarking(e.getPoint())){
									gui.removeHighLightPoint();

									// update selectedMarkingPanel
									gui.updateCoordinatesOfSelectedMarkingPanel();
									}
								}
							}
						}
				}
			}
			}else if(e.getSource() instanceof PrecountGlassPane){
				forwardGlassPaneEvent(e);
			}
		} catch (Exception e1) {
			LOGGER.severe("Error in pressing mouse!");
			e1.printStackTrace();
		}
	}

	/**
	 * Mediates the MouseReleased Event to wanted procedure. Events of PrecountGlassPane are forwarded to forwardGlassPaneEvent(..).
	 * In ImagePanel made releasing sets previous dragging point as null: No More dragging.
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		try {
			if(e.getSource() instanceof ImagePanel){
				if(timerSPACEactivate.isRunning()){ //is dragging
					previousDraggingPoint=null; // dragging movement ended -> set starting point as null
				}
			}else if(e.getSource() instanceof PrecountGlassPane){
				forwardGlassPaneEvent(e);
			}
		} catch (Exception e1) {
			LOGGER.severe("Error in releasing mouse!");
			e1.printStackTrace();
		}

	}

	/**
	 * Mediates the mouseWheelMoved Event to wanted procedure. Events of PrecountGlassPane are forwarded to forwardGlassPaneEvent(..).
	 * Single wheel movement zooms 25% in or out depending wheel movement direction.
	 * @see java.awt.event.MouseAdapter#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

			try {
				if(e.getSource() instanceof ImagePanel){
						// if previous zooming has stopped can start new zooming
						if(!zoomTimer.isRunning()){
							zoomTimer.start();									
								double zoomValue=0.8;
								if(e.getPreciseWheelRotation() <0){ // e.getWheelRotation works only in linux
									zoomValue=1.25;
								}
								gui.zoomAndUpdateImage(e.getPoint(), zoomValue, ID.IMAGE_PROCESSING_BEST_QUALITY);
				
					}
				}else if(e.getSource() instanceof PrecountGlassPane){
					forwardGlassPaneEvent(e);
				}
			} catch (Exception e1) {
				LOGGER.severe("Error in mouse wheel scrolling!");
				e1.printStackTrace();
			}

	}

	/**
	 * Paints the circle and square for precounting if fits fully to imagePanel.
	 *
	 * @param imagePanelPoint Point at imagePanel where mouse focused
	 * @param glassPanePoint Point at glassPane where mouse focused
	 * @throws Exception the exception
	 */
	private void paintCircle(Point imagePanelPoint, Point glassPanePoint) throws Exception{
		Point up = new Point(imagePanelPoint.x, imagePanelPoint.y -glassPane.getRectangleSize()/2);
		Point right = new Point(imagePanelPoint.x+glassPane.getRectangleSize()/2, imagePanelPoint.y);
		Point down = new Point(imagePanelPoint.x, imagePanelPoint.y +glassPane.getRectangleSize()/2);
		Point left = new Point(imagePanelPoint.x-glassPane.getRectangleSize()/2, imagePanelPoint.y);

		if(imagePanel.contains(up) && imagePanel.contains(right) && imagePanel.contains(down) && imagePanel.contains(left)){
			// repaint the circle in new position
			glassPane.setCenterPoint(glassPanePoint);
			glassPane.repaint();
		}
		else{
			// hide circle
			glassPane.setCenterPoint(null);
			glassPane.repaint();
		}
	}



	/**
	 * Dispatches the triggered event to given component. Determines is the event mouseWheel event or some other.
	 *
	 * @param e MouseEvent the event triggered at glassPane.
	 * @param glassPanePoint Point the point where mouse triggered event.
	 * @param guiComponent Component the component of GUI where event is wanted to work.
	 * @throws Exception the exception
	 */
	private void redirectEventToGUIComponents(MouseEvent e, Point glassPanePoint, Component guiComponent) throws Exception{
	
		Point componentPoint = SwingUtilities.convertPoint(glassPane, glassPanePoint, guiComponent);
		if(e.getID()== MouseEvent.MOUSE_WHEEL){
			guiComponent.dispatchEvent(new MouseWheelEvent(guiComponent, e.getID(), e.getWhen(), e.getModifiers(),
					componentPoint.x, componentPoint.y, e.getClickCount(), e.isPopupTrigger(), ((MouseWheelEvent)e).getScrollType(),
					((MouseWheelEvent)e).getScrollAmount(), (int)(((MouseWheelEvent)e).getPreciseWheelRotation()*10))); 
		}
		else{

			guiComponent.dispatchEvent(new MouseEvent(guiComponent, e.getID(), e.getWhen(), e.getModifiers(),componentPoint.x, componentPoint.y, e.getClickCount(), e.isPopupTrigger()));

		}
	}

	/**
	 * Sets the variable is_CTRL_down to false;.
	 *
	 * @throws Exception the exception
	 */
	public void releaseCtrl() throws Exception{
		is_CTRL_pressed=false;
	}




	/**
	 *  Sets is the precounting Thread running.
	 *
	 * @param isCellPickingON boolean running or not running
	 * @throws Exception the exception
	 */
	public void setCellPickingON(boolean isCellPickingON) throws Exception {
		this.isCellPickingON = isCellPickingON;
	}

	/**
	 * Sets the components. Components are used to check in which part of gui the mouse was pressed.
	 *
	 * @param gui the gui
	 * @param tm the TaskManager
	 * @param contentPane the content pane at gui
	 * @param glassPane the glass pane of gui
	 * @param imagePanel the visible image panel at gui
	 * @param precountButton the precount button at gui
	 * @param layers JLayeredPane containing Layers at gui
	 * @param downBarPanel the down bar panel at gui
	 * @param zoomSlider the zoom slider at gui
	 * @param sliderPanel the slider panel at gui
	 * @throws Exception the exception
	 */
	public void setComponents(GUI gui, TaskManager tm, Container contentPane, PrecountGlassPane glassPane, ImagePanel imagePanel,
			AbstractButton precountButton, JLayeredPane layers, JPanel downBarPanel, JSlider zoomSlider, JPanel sliderPanel) throws Exception{
		this.gui=gui;
		this.taskManager=tm;
		this.contentPane=contentPane;
		this.glassPane=glassPane;
		this.imagePanel=imagePanel;
		this.precountButton=precountButton;
		this.layers=layers;
		this.downBarPanel=downBarPanel;
		this.zoomSlider=zoomSlider;
		
	}

	/**
	 * Sets the previous dragging point.
	 *
	 * @param previousDraggingPoint the new previous dragging point
	 */
	public void setPreviousDraggingPoint(Point previousDraggingPoint) {
		try {
			this.previousDraggingPoint = previousDraggingPoint;
		} catch (Exception e) {
			LOGGER.severe("Error in setting previous dragging point!");
			e.printStackTrace();
		}
	}


}

package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import gui.saving.ImageSet.ImageSetCreator;


/**
 * The listener interface for receiving GUIcomponent events.
 * The class that is interested in processing a GUIcomponent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's addGUIcomponentListener method. When
 * the GUIcomponent event occurs, that object's appropriate
 * method is invoked.
 *
 */
public class GUIcomponentListener implements ComponentListener{

/** The GUI. */
private GUI gui;

/** The Timer of waiting for painting. */
private Timer waitPaintingTimer;

/** The child dialog. */
private JDialog childDialog=null;

/** The Constant LOGGER for Logging purposes. */
private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/**
	 * Instantiates a new GU icomponent listener.
	 *
	 * @param gui the GUI object
	 */
	public GUIcomponentListener(GUI gui){
		try {
			this.gui=gui;
			initTimer();
		} catch (Exception e) {
			LOGGER.severe("Error in initializing GuiListener!");
			e.printStackTrace();
		}
			
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentHidden(ComponentEvent e) {

	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	public void componentMoved(ComponentEvent e) {
		try {
			if(this.childDialog != null && ( this.childDialog instanceof PropertiesDialog || this.childDialog instanceof AddImageLayerDialog)){
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						
						if(!waitPaintingTimer.isRunning()){
							waitPaintingTimer.start();
						}
						
					}
				});
				
			}
			
						
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentResized(ComponentEvent e) {
		try {
			// resize the lower panel
			gui.visualPanel.setBounds(0,0,(int)(gui.leftPanel.getBounds().getWidth()),(int)(gui.leftPanel.getBounds().getHeight()-gui.downBarPanel.getBounds().getHeight()));
			gui.visualPanel.revalidate();

			//	updateImagePanelSize();
			gui.resizeLayerComponents();
		} catch (Exception e1) {
			LOGGER.severe("Error in resizing window components!");
			e1.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentShown(ComponentEvent e) {

	}

	/**
	 * Gets the child dialog.
	 *
	 * @return the child dialog
	 * @throws Exception the exception
	 */
	public JDialog getChildDialog() throws Exception{
		return childDialog;
	}
	
	/**
	 * Inits the timer for different parent objects. Starts setting the panel bounds of parent object.
	 */
	private void  initTimer(){
		try {
			this.waitPaintingTimer = new Timer(100, new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if(childDialog != null){
							childDialog.setBounds(gui.getVisibleWindowBounds());
							
							if(childDialog instanceof PropertiesDialog)
							((PropertiesDialog)childDialog).setPanelPosition();
							
							if(childDialog instanceof AddImageLayerDialog)
								((AddImageLayerDialog)childDialog).setPanelPosition();
							
							if(childDialog instanceof ImageSetCreator)
								((ImageSetCreator)childDialog).setPanelPosition();
							
//		if(childDialog instanceof ShadyMessageDialog)
//			((ShadyMessageDialog)childDialog).setPanelPosition(gui.getVisibleWindowBounds());
							
							childDialog.repaint();
							waitPaintingTimer.stop();
						}
					} catch (Exception e1) {
						LOGGER.severe("Error in setting bounds of child dialogs!");
						e1.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			LOGGER.severe("Error in initializing painting timer!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the child dialog.
	 *
	 * @param childDialog the new child dialog
	 * @throws Exception the exception
	 */
	public void setChildDialog(JDialog childDialog) throws Exception {
		this.childDialog = childDialog;
	}

}

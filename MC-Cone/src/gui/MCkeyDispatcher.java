package gui;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 * Keyboard listener for CONTROL-Key. Mediates the releasing of CONTROL-key to GuiListener-object.
 *
 */
public class MCkeyDispatcher implements KeyEventDispatcher{

/** The listener for GUI. */
GUIListener gListener;

/** The Constant LOGGER. */
private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/**
	 * Constructor of class.
	 * @param gListener GuiListener @see gui.GuiListener
	 */
	public MCkeyDispatcher(GUIListener gListener){
		this.gListener=gListener;
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		try {
			if(e.getID() == KeyEvent.KEY_RELEASED){
				if(e.getKeyCode()== KeyEvent.VK_CONTROL){
					gListener.releaseCtrl();

				}
			}

			return false;
		} catch (Exception e1) {
			LOGGER.severe("Error in listening ctrl key releasing!");
			e1.printStackTrace();
			return false;
		}
	}

}

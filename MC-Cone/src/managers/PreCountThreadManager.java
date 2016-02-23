package managers;

import java.awt.image.BufferedImage;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import gui.ProgressBallsDialog;
import operators.PreCounterThread;

/**
 * The Class PreCountThreadManager. Manages the PreCounterThread and Thread of ProgressBallsDialog. When other is stopped another is also.
 */
public class PreCountThreadManager {

	private PreCounterThread preCounter;
	
	/** The progress balls dialog. */
	private ProgressBallsDialog progressBallsDialog;
	
	/** The ID of MarkingLayer. */
	private int mLayerID=-1;
	
	/** The ID of ImageLayer. */
	private int iLayerID=-1;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/**
	 * Instantiates a new PreCountThreadManager.
	 *
	 * @param preCounter the PreCounterThread
	 * @param progressBalls the ProgressBallsDialog
	 * @param iLayerID the ID of ImageLayer
	 * @param mLayerID the ID of MarkingLayer
	 */
	public PreCountThreadManager(PreCounterThread preCounter, ProgressBallsDialog progressBalls , int iLayerID, int mLayerID){
		try {
			this.setiLayerID(iLayerID);
			this.setmLayerID(mLayerID);
			this.progressBallsDialog=progressBalls;
			this.preCounter= preCounter;
			this.preCounter.setManager(this);
			this.progressBallsDialog.setManager(this);
		} catch (Exception e) {
			LOGGER.severe("Error in initializing Manager for precounting!");
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the PreCounterThread.
	 */
	public void initPreCounterThread(){
		try {
			this.preCounter.initThread();
		} catch (Exception e) {
			LOGGER.severe("Error in initializing Thread of precounting!");
			e.printStackTrace();
		}
	}

	/**
	 * Start the counting in PreCounterThread.
	 *
	 * @throws Exception the exception
	 */
	public void startCounting() throws Exception{
		LOGGER.fine(this.preCounter.getThreadStatus());
		this.preCounter.startCounting();
	}

	/**
	 * Cancels Threads of PreCounterThread and ProgressBallsDialog if running.
	 */
	public void cancelCounting(){
		LOGGER.fine("Manager: cancel counting! ");
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
			
				try {
					if(preCounter.isCounting())
						preCounter.cancelOutside();
				} catch (Exception e) {
					LOGGER.severe("Error in cancelling precounting by user !");
					e.printStackTrace();
				}
			}
		});
	SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					if(progressBallsDialog.isShowON())
						progressBallsDialog.stopPaintingAndClose();
				} catch (Exception e) {
					LOGGER.severe("Error in stopping progress window!");
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Sets the subImage to PreCounterThread. 
	 *
	 * @param subImage the sub image
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean setNewSubImage(BufferedImage subImage) throws Exception{
		if(this.preCounter != null){
			this.preCounter.setSubImage(subImage);
			return true;
		}
		return false;
	}

	/**
	 * Sets the ProgressBallsDialog.
	 *
	 * @param pbd the new ProgressBallsDialog
	 * @throws Exception the exception
	 */
	public void setProgressBallDialog(ProgressBallsDialog pbd) throws Exception{
		this.progressBallsDialog=pbd;
	}

	/**
	 * Returns the ID of MarkingLayer.
	 *
	 * @return the ID of MarkingLayer
	 * @throws Exception the exception
	 */
	public int getmLayerID() throws Exception {
		return mLayerID;
	}

	/**
	 * Sets the ID of MarkingLayer.
	 *
	 * @param mLayerID the new ID of MarkingLayer
	 * @throws Exception the exception
	 */
	public void setmLayerID(int mLayerID)  throws Exception{
		this.mLayerID = mLayerID;
	}

	/**
	 * Returns the ID of ImageLayer.
	 *
	 * @return the ID of ImageLayer
	 * @throws Exception the exception
	 */
	public int getiLayerID() throws Exception {
		return iLayerID;
	}

	/**
	 * Sets the ID of ImageLayer.
	 *
	 * @param iLayerID the new ID of ImageLayer
	 * @throws Exception the exception
	 */
	public void setiLayerID(int iLayerID)  throws Exception{
		this.iLayerID = iLayerID;
	}

}

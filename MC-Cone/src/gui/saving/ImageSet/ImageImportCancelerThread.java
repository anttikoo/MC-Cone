package gui.saving.ImageSet;

import java.util.logging.Logger;

import gui.ProgressBallsDialog;

/**
 * The Class ImageImportCancelerThread. This Class is unused in version 0.1. Probably used in future releases.
 */
public class ImageImportCancelerThread implements Runnable{

/** The progress dialog. */
private ProgressBallsDialog progressBalls;

/** The progress thread. */
private Thread progressThread;

/** The cancelled. Shows is importing of image canceled. */
private boolean cancelled=false;

/** The Constant LOGGER. */
private final static Logger LOGGER = Logger.getLogger("MCCLogger");


	/**
	 * Instantiates a new ImageImportCancelerThread.
	 *
	 * @param isc the isc
	 * @param pbd the pbd
	 */
	public ImageImportCancelerThread(ImageSetCreator isc, ProgressBallsDialog pbd){
	try {
		this.progressBalls= pbd;
		this.progressThread=new Thread(this, "cancelling");
	} catch (Exception e) {
		LOGGER.severe("Error in initializing ImageImportCancelerThread!");
		e.printStackTrace();
	}
	}
	
	
	/**
	 * Checks if is Thread cancelled.
	 *
	 * @return true, if is cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			this.setCancelled(false);
			while(progressBalls.isShowON()){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {			
					e.printStackTrace();
				}
			}

			setCancelled(true);
		} catch (Exception e) {
			LOGGER.severe("Error in start running image import canceller thread!");
			e.printStackTrace();
		}

	}
	
	/**
	 * Sets the Thread cancelled.
	 *
	 * @param cancelled the new cancelled
	 * @throws Exception the exception
	 */
	public void setCancelled(boolean cancelled) throws Exception{
		this.cancelled = cancelled;
	}

	/**
	 * Sets Thread running.
	 *
	 * @throws Exception the exception
	 */
	public void startWaiting() throws Exception{
		this.progressThread.start();
	}
}

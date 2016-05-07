package gui;

import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class ShadyMessageRunnable extends ShadyMessageDialog implements Runnable{
	/** The thread number. */
	private int threadNumber=1;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6400712039970080650L;
	
	/** The painter thread. */
	private Thread painterThread;
	
	/** The wait counter. */
	private int waitCounter=0;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	public ShadyMessageRunnable(JFrame frame, String title, String message,int id, Component comp){
		super(frame, title, message, id, comp);
		this.painterThread = new Thread(this, title+threadNumber++);
		this.validate();
	}

	public ShadyMessageRunnable(JDialog jdialog, String title, String message,int id, Component comp){
		super(jdialog, title, message, id, comp);
		this.painterThread = new Thread(this, title+threadNumber++);
		this.validate();
	}
	
	

	@Override
	public void run() {
		while(waitCounter<5){

			try {
				painterThread.sleep(20);
				waitCounter++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.severe("Error in painting message dialog!");
			}	

		}
		
	}
	
	public int showDialog(){
		this.validate();
		this.repaint();
		this.setVisible(true);		
		if(this.painterThread != null && this.painterThread.isAlive())
			this.painterThread.interrupt();	
	this.painterThread.start();
	return returnValue;
	}
}

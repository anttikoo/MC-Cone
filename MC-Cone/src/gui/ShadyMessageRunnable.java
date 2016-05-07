package gui;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class ShadyMessageRunnable extends ShadyMessageDialog implements Runnable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6400712039970080650L;
	/** The painter thread. */
	private Thread painterThread;
	
	public ShadyMessageRunnable(JFrame frame, String title, String message,int id, Component comp){
		super(frame, title, message, id, comp);
		this.validate();
	}

	public ShadyMessageRunnable(JDialog jdialog, String title, String message,int id, Component comp){
		super(jdialog, title, message, id, comp);
		this.validate();
	}
	
	

	@Override
	public void run() {
		
		
	}
}

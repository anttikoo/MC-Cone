package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import operators.ShinyProgressBar;

/**
 * The Class ProgressBarAndBallsDialog. Show progress bar and balls to show some progressing. User can stop the running task by pressing Cancel.
 */
public class ProgressBarAndBallsDialog extends ProgressBallsDialog{

	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6499326324787212498L;

	public ProgressBarAndBallsDialog(JDialog jdialog, String title, String message, int id, Component comp) {
		super(jdialog, title, message, id, comp);
		
	}
	
	/**
	 * Instantiates a new progress bar and balls dialog.
	 *
	 * @param jframe the jframe
	 * @param title the title
	 * @param message the message
	 * @param id the id
	 * @param comp the comp
	 */
	public ProgressBarAndBallsDialog(JFrame jframe, String title, String message, int id, Component comp) {
		super(jframe, title, message, id, comp);
		
	}
	
	/* (non-Javadoc)
	 * @see gui.ShadyMessageDialog#initMessagePanel()
	 */
	protected int initMessagePanel() throws Exception{
		super.initMessagePanel();
			
		// create progress bar
		JPanel progressPanel = new JPanel();
		progressPanel.setLayout(new FlowLayout());
		progressPanel.setBackground(Color_schema.dark_30);
		progressPanel.setPreferredSize(new Dimension(250, 30));
		
		progressBar = new ShinyProgressBar();
		progressBar.setPreferredSize(new Dimension(250,20));
		progressBar.setMinimumSize(new Dimension(250,20));
		progressBar.setMaximumSize(new Dimension(250,20));
		progressBar.setBounds(new Rectangle(0, 0, 250, 20));

		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setValue(0);
		progressBar.setBackground(Color_schema.dark_30);
		progressBar.setForeground(Color_schema.white_180);
	
		progressPanel.add(progressBar);
		messagePanel.setPreferredSize(new Dimension(250, 50));
		
		this.messagePanel.add(progressPanel);
	
		return 250; // width of progress balls
	}
	
	public void updateProgressBarValue(int percentValue){
    	this.progressBar.setValue(percentValue);
    }

	
	
}

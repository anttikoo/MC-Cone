package gui;

import information.ID;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import managers.PreCountThreadManager;


/**
 * The Class ProgressBallsDialog. Shows balls moving and shows that something is running.
 */
public class ProgressBallsDialog extends ShadyMessageDialog implements Runnable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6992628985272172668L;

	/** The painted ball. Shows which ball is presently painted */
	private int paintedBall=-1;
	
	/** The thread number. */
	private int threadNumber=1;
	
	/** The show on. Shows is the progress balls running. */
	private volatile boolean showON=true;
	
	/** The next ball bigger. Shows should the paintedBall be counted to bigger or smaller. */
	private boolean nextBallBigger=true;
	
	/** The g2d. */
	private Graphics2D g2d;
	private JPanel ballsPanel;
	
	/** The PrecountThreadManager. Manages the precounting and its progress Threads. */
	private PreCountThreadManager pctm;
	
	/** The painter thread. */
	private Thread painterThread;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	public ProgressBallsDialog(JFrame frame, String title, String message,int id, Component comp){
		super(frame, title, message, id, comp);

		try {
			initBalls();
			initButtonAction();
			this.painterThread = new Thread(this, title+threadNumber++);
			this.validate();
		} catch (Exception e) {
			LOGGER.severe("Error starting Progress dialog.");
			e.printStackTrace();
		}

	}
	
	public ProgressBallsDialog(JDialog jdialog, String title, String message,int id, Component comp){
		super(jdialog, title, message, id, comp);

		try {
			initBalls();
			initButtonAction();
			this.painterThread = new Thread(this, title+threadNumber++);
			this.validate();
		} catch (Exception e) {
			LOGGER.severe("Error starting Progress dialog.");
			e.printStackTrace();
		}

	}

	
	/* Overrides super class createButton -method. In ProgressBallsDialog only one button is created -> setup it here.
	 * @see gui.ShadyMessageDialog#createButton(int)
	 */
	protected JButton createButton(final int buttonID) throws Exception{
		JButton button=new JButton(getButtonText(buttonID));
		button.setPreferredSize(new Dimension(150,30));
		button.setBackground(Color_schema.dark_20);

		if(buttonID == ID.NO || buttonID == ID.CANCEL){
			button.setForeground(Color_schema.orange_dark);
			button.setBorder(BorderFactory.createLineBorder(Color_schema.orange_dark, 2));
			MouseListenerCreator.addMouseListenerToCancelButtons(button);
		}
		else{
			MouseListenerCreator.addMouseListenerToNormalButtons(button);
		}

		button.setFocusable(false);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopPaintingAndClose();
			}
		});
		return button;
	}

	/**
	 * Refresh dialog.
	 *
	 * @throws Exception the exception
	 */
	public void refreshDialog() throws Exception{
		setShowON(true);
		nextBallBigger=true;
		initBalls();
		this.painterThread = new Thread(this, "painter"+threadNumber);
	}


	/**
	 * Inits the button action.
	 *
	 * @throws Exception the exception
	 */
	private void initButtonAction() throws Exception{
			if(super.getFirstButton() != null){
				super.getFirstButton().removeActionListener(super.getFirstButton().getActionListeners()[0]);
				super.getFirstButton().addActionListener(new ActionListener() {
	
					@Override
					public void actionPerformed(ActionEvent e) {
						stopPaintingAndClose();
	
					}
				});
			}	
	}
	
	/**
	 * Initializes the components of window.
	 */
	private void initBalls(){
		try {

			ballsPanel = new JPanel();
			ballsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));
			ballsPanel.setBorder(null);
			ballsPanel.setBackground(super.getMessagePanel().getBackground());
			for(int i =0;i<6;i++){
				ballsPanel.add(new SingleBall());
			}
			// set messagepanel at super class
			super.getMessagePanel().setMaximumSize(new Dimension(200,100));
			super.getMessagePanel().removeAll();
			super.getMessagePanel().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
			super.getMessagePanel().add(ballsPanel);
			super.getMessagePanel().revalidate();
			super.revalidate();
			this.revalidate();
		} catch (Exception e) {
			LOGGER.severe("ERROR in initializing components of progress window!");
			e.printStackTrace();
		}


	}


	/* (non-Javadoc)
	 * @see gui.ShadyMessageDialog#showDialog()
	 */
	public int showDialog(){
		try {

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {			
					setVisible(true);
				}
			});
			setShowON(true);
		
			if(this.painterThread != null && this.painterThread.isAlive())
				this.painterThread.interrupt();
			this.painterThread.start();

			return ID.CANCEL;
		} catch (Exception e) {
			LOGGER.severe("Error in showing Progress dialog!");
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Stops the painting and closes window.
	 */
	public void stopPaintingAndClose(){
		try {

				LOGGER.fine("StopPaintingAndClose");
				setShowON(false);

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						if(pctm != null)
							pctm.cancelCounting();

					}
				});


				this.setVisible(false);
				this.dispose();

		} catch (Exception e) {
			LOGGER.severe("Error in stopping progress dialog");
			e.printStackTrace();
			setShowON(false);
		}

	}

	/**
	 * Sets the PreCountThreadManager.
	 *
	 * @param pctm the new manager
	 * @throws Exception the exception
	 */
	public void setManager(PreCountThreadManager pctm) throws Exception{
		this.pctm=pctm;
	}



	/**
	 * Draws the next ball to window.
	 */
	@SuppressWarnings("static-access")
	private void showNextBall(){
		try {
			// check if next ball can be more right (bigger) -> add paintedBall value.
			if(nextBallBigger){
				paintedBall++;
				if(paintedBall > 5){ // ball is rightmost -> start going to left
					paintedBall=4;
					nextBallBigger=false;
				}
			}
			else{
				paintedBall--;
				if(paintedBall < 0){ // ball is leftmost -> start going to right
					paintedBall=1;
					nextBallBigger=true;
				}
			}

			SingleBall ballComponent = (SingleBall)ballsPanel.getComponent(paintedBall);
		
			// paint the balls
			while(showON && ballComponent.getaRGBopacity() < 250){
				int opa= ballComponent.getaRGBopacity()+25;
				if(opa >255)
					opa=255;
				ballComponent.paintWithOpacity(opa);
				painterThread.sleep(80);
				

			}
			while(showON && ballComponent.getaRGBopacity() > 30){
				int opa= ballComponent.getaRGBopacity()-25;
				if(opa <30)
					opa=30;
				ballComponent.paintWithOpacity(opa);
				painterThread.sleep(80);
			

			}
		} catch (InterruptedException e) {
			
			e.printStackTrace();

		}
		catch (Exception e) {
			LOGGER.severe("Error in painting progress dialog balls");
			e.printStackTrace();
		}

	}


	/**
	 * Checks if is show on.
	 *
	 * @return true, if is show on
	 * @throws Exception the exception
	 */
	public boolean isShowON() throws Exception {
		return showON;
	}

	/**
	 * Sets the show on.
	 *
	 * @param showON the new show on
	 */
	public void setShowON(boolean showON){
		try {
			this.showON = showON;
		} catch (Exception e) {
			LOGGER.severe("Error in setting on or off the progress window!");
			e.printStackTrace();
		}
	}



	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		try {

			// starts painting the balls
			while(showON){
				showNextBall();
				painterThread.sleep(100);	
	
			}
			
			LOGGER.fine("ProgressBall Thread ended");
			painterThread.interrupt();
		
		} catch (InterruptedException e) {		
			e.printStackTrace();
			showON=false;
		}
	}



	/**
	 * The Class SingleBall.
	 */
	private class SingleBall extends JPanel{
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 4460095647221966962L;
		
		/** The argb opacity. */
		private int aRGBopacity=30;


		/**
		 * Instantiates a new single ball.
		 */
		private SingleBall(){
			try {
				this.setOpaque(false);
				this.setBorder(null);
				this.setMaximumSize(new Dimension(20,20));
				this.setPreferredSize(new Dimension(20,20));
				this.setMinimumSize(new Dimension(20,20));
				this.setBackground(Color_schema.dark_30);
				this.setLayout(null);
			} catch (Exception e) {
				LOGGER.severe("Error in initializing SingleBall of progress window!");
				e.printStackTrace();
			}
		}

		

		/**
		 * Sets the new integer for creating new argb color.
		 *
		 * @param newARGB the new argb
		 * @throws Exception the exception
		 */
		private void paintWithOpacity(int newARGB) throws Exception{

			this.aRGBopacity=newARGB;
			this.repaint();
		}		

		/**
		 * Gets the a rgb opacity.
		 *
		 * @return the a rgb opacity
		 * @throws Exception the exception
		 */
		public int getaRGBopacity() throws Exception{
			return aRGBopacity;
		}


		@Override
		public void paintComponent(Graphics g) {		
			super.paintComponents(g);
			try {
				g2d = (Graphics2D) g.create();
				g2d.setPaint(new Color(aRGBopacity,aRGBopacity,aRGBopacity));
				g2d.fillOval(2, 2, 16, 16);
				g2d.dispose();
			} catch (Exception e) {
				LOGGER.severe("Error in painting progress balls!");
				e.printStackTrace();
			}
		}
	}

}

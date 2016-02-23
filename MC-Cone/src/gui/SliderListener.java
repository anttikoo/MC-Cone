package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

/**
 * The listener interface for receiving slider events.
 * The class that is interested in processing a slider
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's addSliderListener method. When
 * the slider event occurs, that object's appropriate
 * method is invoked. A sliding Timer is started when sliding started. This
 * Timer restricts the updating of the image to every 100 ms.
 *
 */
public class SliderListener extends MouseInputAdapter{
	
	/** The sliding on. Shows is sliding moving*/
	private boolean slidingON=false;
	
	/** The start value. */
	private int startValue=0;

/** The value label. */
	JLabel valueLabel;
	
	/** The Timer for slider to restrict the process. */
	private Timer sliderTimer;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/**
	 * Instantiates a new slider listener.
	 *
	 * @param label the label
	 */
	public SliderListener(JLabel label){
	try {
		//	this.slider=slider;
			this.valueLabel=label;
			initZoomTimer();
	} catch (Exception e) {
		LOGGER.severe("Error in initializing slider listener!");
		e.printStackTrace();
	}
	}

	/**
	 * Gets the start value.
	 *
	 * @return the start value
	 */
	public int getStartValue() {
		return startValue;
	}

	/**
	 * Initializes the zoom timer.
	 */
	private void initZoomTimer(){
		this.sliderTimer=new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(sliderTimer.isRunning())
						sliderTimer.stop();
				} catch (Exception e1) {
					LOGGER.severe("Error in stopping slider timer!");
					e1.printStackTrace();
				}

			}
		});
	}

	/**
	 * Checks if is slider timer running.
	 *
	 * @return true, if is slider timer running
	 * @throws Exception the exception
	 */
	public boolean isSliderTimerRunning() throws Exception{
		return this.sliderTimer.isRunning();
	}

	/**
	 * Checks if is sliding on.
	 *
	 * @return true, if is sliding on
	 * @throws Exception the exception
	 */
	public boolean isSlidingON() throws Exception {
		return slidingON;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		try {
			this.setSlidingON(true);
		} catch (Exception e1) {
			LOGGER.severe("Error when pressed slider!");
			e1.printStackTrace();
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		try {
			this.setSlidingON(false);
			this.setStartValue(0);
			((JSlider)(e.getSource())).setValue(0);
			valueLabel.setText("0");
			this.sliderTimer.stop();
		} catch (Exception e1) {
			LOGGER.severe("Error when released mouse on slider!");
			e1.printStackTrace();
		}
		
	}
	
	/**
	 * Sets the sliding on.
	 *
	 * @param sliderPressed the new sliding on
	 * @throws Exception the exception
	 */
	public void setSlidingON(boolean sliderPressed) throws Exception{
		this.slidingON = sliderPressed;
	}
	
	/**
	 * Sets the start value.
	 *
	 * @param startValue the new start value
	 * @throws Exception the exception
	 */
	public void setStartValue(int startValue) throws Exception{
		this.startValue = startValue;
	}
	
	/**
	 * Start slider timer.
	 *
	 * @throws Exception the exception
	 */
	public void startSliderTimer() throws Exception{
		this.sliderTimer.start();
	}
}

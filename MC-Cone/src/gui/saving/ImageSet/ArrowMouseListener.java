package gui.saving.ImageSet;

import gui.Color_schema;
import information.SharedVariables;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 * The listener interface for receiving arrowMouse events.
 * The class that is interested in processing a arrowMouse
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's addArrowMouseListenermethod. When
 * the arrowMouse event occurs, that object's appropriate
 * method is invoked.
 *
 */
public class ArrowMouseListener implements MouseListener {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The scaling factor. A factor for calculating image height from width conversely. */
	public static double scalingFactor=0;
	
	/** The width field. */
	private JTextField widthField;
	
	/** The height field. */
	private JTextField heightField;
	
	/** The pressing width up timer. */
	private Timer pressingWidthUPTimer;
	
	/** The pressing width down timer. */
	private Timer pressingWidthDownTimer;
	
	/** The pressing height up timer. */
	private Timer pressingHeightUPTimer;
	
	/** The pressing height down timer. */
	private Timer pressingHeightDownTimer;
	
	/** The multiplier timer. */
	private Timer multiplierTimer;
	
	/** The changing values on. */
	private boolean changingValuesON=false;
	
	/** The value change. */
	private int valueChange=0;
	
	/** The max value change. */
	private int maxValueChange=30;
	
	/** The maximum image size. */
	private int maximumImageSize=SharedVariables.IMAGESET_EXPORT_MAX_RESOLUTION;



	/**
	 * Instantiates a new ArrowMouseListener.
	 *
	 * @param wf TextField of width for exported image
	 * @param hf TextField of height for exported image
	 */
	public ArrowMouseListener(JTextField wf, JTextField hf){
		try {
			this.widthField=wf;
			this.heightField=hf;
			initPressingTimers();
		} catch (Exception e) {
			LOGGER.severe("Error in initializing ArrowMouseListener!");
			e.printStackTrace();
		}

	}

	/**
	 * Initializes the Timers for pressing keys.
	 */
	private void initPressingTimers(){
		this.pressingWidthUPTimer=new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(!changingValuesON)
					updateField(valueChange, widthField, heightField, false);
				} catch (Exception e1) {
					LOGGER.severe("Error in increasing image width !");
					e1.printStackTrace();
				}
			}
		});
		this.pressingWidthDownTimer=new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(!changingValuesON)
					updateField(-valueChange, widthField, heightField, false);
				} catch (Exception e1) {
					LOGGER.severe("Error in decreasing image width !");
					e1.printStackTrace();
				}
			}
		});
		this.pressingHeightUPTimer=new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(!changingValuesON)
					updateField(valueChange, heightField, widthField, true);
				} catch (Exception e1) {
					LOGGER.severe("Error in increasing image height !");
					e1.printStackTrace();
				}
			}
		});
		this.pressingHeightDownTimer=new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(!changingValuesON)
					updateField(-valueChange, heightField, widthField, true);
				} catch (Exception e1) {
					LOGGER.severe("Error in decreasing image height !");
					e1.printStackTrace();
				}
			}
		});

		this.multiplierTimer=new Timer(200,new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(valueChange<maxValueChange)
						valueChange++;
				} catch (Exception e1) {
					LOGGER.severe("Error in changing multiplier when changing image resolution!");
					e1.printStackTrace();
				}

			}
		});


	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			stopTimers();
			if(((JButton)e.getSource()).getActionCommand().equals(SharedVariables.widthUp)){
				if(!changingValuesON)
				updateField(1, this.widthField, this.heightField, false);
			}
			else if(((JButton)e.getSource()).getActionCommand().equals(SharedVariables.widthDown)){
				if(!changingValuesON)
				updateField(-1, this.widthField, this.heightField, false);
			}
			else if(((JButton)e.getSource()).getActionCommand().equals(SharedVariables.heighthUp)){
				if(!changingValuesON)
				updateField(1, heightField, widthField, true);
			}
			else if(((JButton)e.getSource()).getActionCommand().equals(SharedVariables.heightDown)){
				if(!changingValuesON)
				updateField(-1, heightField, widthField, true);
			}
		} catch (Exception e1) {
			LOGGER.severe("Error when cliced arrows for changing image resolution!");
			e1.printStackTrace();
		}

	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		try {
			if(!this.multiplierTimer.isRunning())
				this.multiplierTimer.start();

			if(((JButton)e.getSource()).getActionCommand().equals(SharedVariables.widthUp)){
				if(!this.pressingWidthUPTimer.isRunning())
					this.pressingWidthUPTimer.start();
			}
			else if(((JButton)e.getSource()).getActionCommand().equals(SharedVariables.widthDown)){
				if(!this.pressingWidthDownTimer.isRunning())
					this.pressingWidthDownTimer.start();
			}
			else if(((JButton)e.getSource()).getActionCommand().equals(SharedVariables.heighthUp)){
				if(!this.pressingHeightUPTimer.isRunning())
					this.pressingHeightUPTimer.start();
			}
			else if(((JButton)e.getSource()).getActionCommand().equals(SharedVariables.heightDown)){
				if(!this.pressingHeightDownTimer.isRunning())
					this.pressingHeightDownTimer.start();
			}
		} catch (Exception e1) {
			LOGGER.severe("Error when pressed mouse for changing image resolution!");
			e1.printStackTrace();
		}


	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e)  {
		try {
			stopTimers();
		} catch (Exception e1) {
			LOGGER.severe("Error in  stopping timers of image resolution changers!");
			e1.printStackTrace();
		}

	}

	/**
	 * Sets the scaling factor used for calculating width from image height and height from image width.
	 *
	 * @param factor the new scaling factor
	 * @throws Exception the exception
	 */
	public void setScalingFactor(double factor) throws Exception{
		ArrowMouseListener.scalingFactor=factor;
	}

	/**
	 * Stops the timers.
	 */
	private void stopTimers(){
		try {
			if(this.multiplierTimer.isRunning()){
				this.multiplierTimer.stop();
				this.valueChange=0;
			}


			if(this.pressingWidthUPTimer.isRunning())
				this.pressingWidthUPTimer.stop();

			if(this.pressingWidthDownTimer.isRunning())
				this.pressingWidthDownTimer.stop();

			if(this.pressingHeightUPTimer.isRunning())
				this.pressingHeightUPTimer.stop();

			if(this.pressingHeightDownTimer.isRunning())
				this.pressingHeightDownTimer.stop();
		} catch (Exception e) {
			LOGGER.severe("Error in stopping timers of image resolution changing arrows!");
			e.printStackTrace();
		}
	}
	
	

	/**
	 * Updates textfields of resolution for exported image. If given JTextField is widthField then the divide parameter has to be false and conversely.
	 *
	 * @param resolutionchange the resolutionchange
	 * @param field_changed the field_changed
	 * @param field_calculated the field_calculated
	 * @param divide boolean true if count value of another textfield by diving otherwise false for multiplying
	 * @throws Exception the exception
	 */
	private void updateField(int resolutionchange, JTextField field_changed, JTextField field_calculated, boolean divide) throws Exception{
		changingValuesON=true;
		DocumentFilter filter1 =((PlainDocument)field_changed.getDocument()).getDocumentFilter();
		DocumentFilter filter2 =((PlainDocument)field_calculated.getDocument()).getDocumentFilter();
		if(filter1 instanceof ResolutionIntFilter){
			((ResolutionIntFilter)filter1).setUpdateAnotherField(false);
		}
		if(filter2 instanceof ResolutionIntFilter){
			((ResolutionIntFilter)filter2).setUpdateAnotherField(false);
		}
		
		if(field_changed.getText() != null && field_changed.getText().length()>0){

			try {
				int presentValue=Integer.parseInt(field_changed.getText().trim());
				presentValue+=resolutionchange;
				if(presentValue<=maximumImageSize && presentValue > 0){

					if(scalingFactor !=0){
						int secondValue=0;
						if(divide){
							secondValue= (int)(((double)presentValue)/scalingFactor);
						}
						else{

							secondValue= (int)(((double)presentValue)*scalingFactor);
						}
						if(secondValue <=maximumImageSize && secondValue > 0){
							field_changed.setText(""+presentValue);
							field_changed.setForeground(Color_schema.white_230);

							field_calculated.setText(""+secondValue);
							field_calculated.setForeground(Color_schema.white_230);
						}
						else{
							field_calculated.setForeground(Color_schema.orange_dark);

						}
					}
					else{

						field_changed.setText(""+presentValue);
						field_changed.setForeground(Color_schema.white_230);
					}
				}
				else{
					if(presentValue >maximumImageSize){
						presentValue=maximumImageSize;
					int secondValue =(int)(((double)presentValue)/scalingFactor);
					if(secondValue>maximumImageSize){
						if(divide){
							presentValue= (int)(((double)secondValue)*scalingFactor);
						}
						else{
							presentValue= (int)(((double)secondValue)/scalingFactor);

						}
						field_calculated.setForeground(Color_schema.orange_dark);
					}
					else{
						field_calculated.setForeground(Color_schema.white_230);
					}
					field_changed.setText(""+presentValue);
					field_changed.setForeground(Color_schema.white_230);

					field_calculated.setText(""+secondValue);

					field_changed.setForeground(Color_schema.orange_dark);
					}
				}

				changingValuesON=false;
				
				if(filter1 instanceof ResolutionIntFilter){
					((ResolutionIntFilter)filter1).setUpdateAnotherField(true);
				}
				if(filter2 instanceof ResolutionIntFilter){
					((ResolutionIntFilter)filter2).setUpdateAnotherField(true);
				}
			} catch (NumberFormatException e) {
				
				LOGGER.warning("Resolution has to be numerical value");
				changingValuesON=false;
				if(filter1 instanceof ResolutionIntFilter){
					((ResolutionIntFilter)filter1).setUpdateAnotherField(true);
				}
				if(filter2 instanceof ResolutionIntFilter){
					((ResolutionIntFilter)filter2).setUpdateAnotherField(true);
				}
			}


		}

	}



	/**
	 * Updates JTextFields of width and height.
	 *
	 * @throws Exception the exception
	 */
	public void updateResolutions() throws Exception{
		updateField(0,this.widthField, this.heightField, false);
	}

}

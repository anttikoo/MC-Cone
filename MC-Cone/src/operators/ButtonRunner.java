package operators;

import javax.swing.JButton;

/**
 * The Class ButtonRunner.
 */
public class ButtonRunner implements Runnable{
	JButton button=null;
	
	/**
	 * Instantiates a new button runner used to click a JButton
	 *
	 * @param butt the butt
	 */
	public ButtonRunner(JButton butt){
		button=butt;
	}
	@Override
	public void run() {
		try{
			
			button.doClick();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

}

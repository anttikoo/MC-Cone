package operators;

import gui.LogFrame;

import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;

/**
 * The Class programConsoleHandler. Prints messages to log.
 */
public class programConsoleHandler extends ConsoleHandler{
	
	/** The log frame. */
	private LogFrame logFrame;
	
	/**
	 * Instantiates a new program console handler.
	 *
	 * @param logFrame the log frame
	 */
	public programConsoleHandler(LogFrame logFrame){
		this.logFrame=logFrame;
	}

	/* (non-Javadoc)
	 * @see java.util.logging.ConsoleHandler#publish(java.util.logging.LogRecord)
	 */
	public void publish(LogRecord record){
		this.logFrame.printMessage(record);
	}


}

package operators;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * The Class SystemConsoleHandler. Publishes the messages with System.err.println or System.out.pringln.
 */
public class SystemConsoleHandler extends ConsoleHandler{


	/* (non-Javadoc)
	 * @see java.util.logging.ConsoleHandler#publish(java.util.logging.LogRecord)
	 */
	public void publish(LogRecord record){
		if(record.getLevel() == Level.SEVERE){
			System.err.println(record.getLevel().toString()+": "+record.getMessage());
		}
		else {
			System.out.println(record.getLevel().toString()+": "+record.getMessage());
		}


	}


}

package operators;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * A Formatter for modifying information of LogRecord object.
 * Sends information to two targets: 1. to print message in LogFrame and 2. save message to text-file.
 * @author Antti Kurronen
 */
public class TextFileFormatter extends Formatter{


	/* (non-Javadoc)
	 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
	 */
	@Override
	public String format(LogRecord rec) {

		try {

			// Writing to file
			 StringBuffer buf = new StringBuffer(1000);

			 // create caps for different type of messages
			 if (rec.getLevel().intValue() < Level.SEVERE.intValue()) {
			    	if (rec.getLevel().intValue() == Level.WARNING.intValue()) {
			    		buf.append(" ");
					 } else {
						 buf.append("  ");
					 }
			    }
			 // add level of message
			 buf.append(rec.getLevel());
			 buf.append(" ");

			 // add the message
			 buf.append(formatMessage(rec));
			 buf.append(" ");
			 // add time
			 buf.append("[" +getTime(rec.getMillis()) + "]");
			 buf.append(System.getProperty("line.separator"));

			 return  buf.toString();

		} catch (Exception e) {

			System.out.println("error in Doubleformatter");
			return null;
		}
	}

	/**
	 *  Converts time in time in milliseconds to year month day hour:minutes
	 *
	 * @param ms time in milliseconds
	 * @return time in String
	 */
	 private String getTime(long ms) {
		    try {
				SimpleDateFormat date_format = new SimpleDateFormat("yyyy MMM dd HH:mm");
				Date resultdate = new Date(ms);
				return date_format.format(resultdate);
			} catch (Exception e) {			
				e.printStackTrace();
				return null;
			}
		  }

}

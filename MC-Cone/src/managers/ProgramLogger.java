package managers;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import gui.LogFrame;
import operators.SystemConsoleHandler;
import operators.TextFileFormatter;
import operators.programConsoleHandler;

/**
 * Initializes Logger handler and formatter
 *
 *
 * @author Antti Kurronen
 *
 */
public class ProgramLogger {
  
  /** The file handler. */
  static private FileHandler fileHandler;
  
  /** The console handler. */
  static private ConsoleHandler consoleHandler;
  
  /** The system console handler. */
  static private ConsoleHandler systemConsoleHandler;
  
  /** The file formatter. */
  static private TextFileFormatter fileFormatter;


  /**
   *  Creates Handler and Formatter for logging and adds them to logger.
   *  Log messages are saved to text file and in document of LogFrame where log messages can be read.
   *
   * @param logFrame Logwindow is needed for constructing the TextFileFormatter
   * @param saveToFile the save to file
   * @throws IOException Signals that an I/O exception has occurred.
   */
  static public void setup(LogFrame logFrame, boolean saveToFile) throws IOException {

	//set the logger
    Logger logger = Logger.getLogger("MCCLogger");
    consoleHandler=new programConsoleHandler(logFrame);
    systemConsoleHandler=new SystemConsoleHandler();
    // add handler to logger
    logger.addHandler(consoleHandler);
    logger.addHandler(systemConsoleHandler);

    // saving to file
    if(saveToFile){
    	fileHandler = new FileHandler("Logging.txt",8000,1,true);
	    // Create Formatter
	    fileFormatter = new TextFileFormatter();
	    //add formatter to handler
	    fileHandler.setFormatter(fileFormatter);
	    logger.addHandler(fileHandler);
    }
  }
}

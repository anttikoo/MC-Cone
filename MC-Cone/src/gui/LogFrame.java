package gui;
import java.awt.BorderLayout;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


/**
 * Shows log messages of present session in own Frame.
 * @author Antti Kurronen
 */
public class LogFrame extends JFrame{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4057171876250311487L;

	/** The document to be styled. */
	private StyledDocument document;
	
	/** The set_ severe. Styles for SEVERE messages. */
	private SimpleAttributeSet set_SEVERE;
	
	/** The set_ warning. Styles for WARNING messages.*/
	private SimpleAttributeSet set_WARNING;
	
	/** The set_ info. Styles for INFO messages.*/
	private SimpleAttributeSet set_INFO;
	
	/** The set_ fine. Styles for FINE messages.*/
	private SimpleAttributeSet set_FINE;
	private JTextPane pane;


	/**
	 * Class constructor
	 * initializes the JFrame and its components. Initializes the used document styles for different message levels
	 */
	public LogFrame(){

		try {

			this.setTitle("LOG");
			// create styles for different messages
			document = new DefaultStyledDocument();

			set_SEVERE = new SimpleAttributeSet();
			StyleConstants.setBold(set_SEVERE, true);
			StyleConstants.setFontSize(set_SEVERE, 14);
			StyleConstants.setForeground(set_SEVERE, Color_schema.log_fg_SEVERE);

			set_WARNING = new SimpleAttributeSet();

			StyleConstants.setFontSize(set_WARNING, 14);
			StyleConstants.setForeground(set_WARNING, Color_schema.log_fg_WARNING);

			set_INFO = new SimpleAttributeSet();
			StyleConstants.setBold(set_INFO, false);
			StyleConstants.setFontSize(set_INFO, 12);
			StyleConstants.setForeground(set_INFO, Color_schema.log_fg_INFO);

			set_FINE= new SimpleAttributeSet();
			StyleConstants.setBold(set_FINE, false);
			StyleConstants.setFontSize(set_FINE, 12);
			StyleConstants.setForeground(set_FINE, Color_schema.white_180);

			// add text pane with scrolling as needed
			pane = new JTextPane();
			pane.setEditable(false);
			pane.setBackground(Color_schema.log_bg );
			//add scrollbar when needed
			JScrollPane scrollPane = new JScrollPane(pane);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			this.add(scrollPane, BorderLayout.CENTER);

			this.setSize(400,600);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * Adds the message to the document of LogFrame
	 * @param rec The record containing message and information of level of message
	 */
	public void printMessage(LogRecord rec){

		try {

			if(rec.getLevel().intValue() == Level.SEVERE.intValue()){
				document.insertString(document.getLength(), rec.getLevel() + ": " + rec.getMessage()+ "\n", set_SEVERE);

			}
			else
				if(rec.getLevel().intValue() == Level.WARNING.intValue()){
					document.insertString(document.getLength(), rec.getLevel() + ": " + rec.getMessage() + "\n", set_WARNING);

				}
				else if(rec.getLevel().intValue() == Level.INFO.intValue()){
					document.insertString(document.getLength(), rec.getLevel() + ": " + rec.getMessage()+ "\n", set_INFO);
				}
				else
					document.insertString(document.getLength(), rec.getLevel() + ": " + rec.getMessage()+ "\n", set_FINE);
		// set the document to JTextPane
		pane.setDocument(document);


		} catch (BadLocationException e) {

			e.printStackTrace();
		}
	}

}

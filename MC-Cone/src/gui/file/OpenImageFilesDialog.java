package gui.file;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * The Class OpenImageFilesDialog.
 */
public class OpenImageFilesDialog extends OpenFileDialog{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4739394346859180464L;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	public OpenImageFilesDialog(JFrame frame, Rectangle pcb, Rectangle pcbb, String presentFolder) {
		super(frame, pcb, pcbb, presentFolder);
	}
	
	/**
	 * Instantiates a new open image files dialog.
	 *
	 * @param d the d
	 * @param pcb the pcb
	 * @param pcbb the pcbb
	 * @param presentFolder the present folder
	 */
	public OpenImageFilesDialog(JDialog d, Rectangle pcb, Rectangle pcbb, String presentFolder) {
		super(d, pcb, pcbb, presentFolder);	
	}

	/* (non-Javadoc)
	 * @see gui.file.OpenFileDialog#getWindowTitle()
	 */
	protected String getWindowTitle() throws Exception{
		return "SELECT IMAGES";
	}

	/* (non-Javadoc)
	 * @see gui.file.OpenFileDialog#setUpFilechooserSettings()
	 */
	protected void setUpFilechooserSettings(){

		fileChooser.setMultiSelectionEnabled(true); // multiselection is allowed
		fileChooser.setFileFilter(new ImageFilter()); // Image filter in package gui.file
		// set the current folder of jfilechooser
		File f=getFolder(this.presentFolder);
		if(f != null){
			fileChooser.setCurrentDirectory(f);
		}
	}

	/* (non-Javadoc)
	 * @see gui.file.OpenFileDialog#addActionsToFileDialogButtons(javax.swing.JButton)
	 */
	protected void addActionsToFileDialogButtons(JButton button){
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(fileChooser.getSelectedFiles() != null && fileChooser.getSelectedFiles().length>0){
						hideDialog();
						selectedFiles=fileChooser.getSelectedFiles();
						// update present folder to ->-> informationCenter
						String commonFolder = getMostCommonPath(fileChooser.getSelectedFiles());
						if(commonFolder != null && commonFolder.length()>0){
							File file = new File(commonFolder);
							if(file.exists() && file.isDirectory()) // folder exists
								setPresentFolder(commonFolder);
						}

					}
				} catch (Exception e1) {
					LOGGER.severe("Error in selection of file(s)!");
					e1.printStackTrace();
				}
			}
		});


	}

}

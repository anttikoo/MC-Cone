package gui.file;

import information.ID;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.filechooser.FileFilter;
import operators.XMLreadManager;

/**
 * The Class FileManager. Contains methods for checking file.
 */
public class FileManager {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/**
	 * Checks the file that it exists, is a file, given filter accepts the file, 
	 * permissions for file are right and checks the file validity.
	 *
	 * @param file the file
	 * @param filters the filters
	 * @param checkXMLvalidity the check xml validity
	 * @return the int ID of state of the file.
	 */
	public static int checkFile(File file, ArrayList<FileFilter> filters, boolean checkXMLvalidity){
		try {
			if(file != null){
				if(file.exists()){
					if(file.isFile()){

						if(filterAccepts(filters, file)){

							if(file.canRead()){
								if(file.length()>0){ // is just created file
									if(file.canWrite()){
										if(checkXMLvalidity){ // if xml file -> check validity
											XMLreadManager rm=new XMLreadManager();
											if(rm.isFileValid(file)){
												return ID.FILE_OK; // can update the file
											}
											else
												return ID.FILE_NOT_VALID;
										}
										else
											return ID.FILE_OK;
									}else // if is not valid file
										return ID.FILE_CANT_WRITE;
								}else
									return ID.FILE_NEW_FILE;
							}else // if can't read
								return ID.FILE_CANT_READ;
						}else // if is not right format
							return ID.FILE_NOT_RIGHT_FORMAT;
					}else //if file is folder
						return ID.FILE_IS_NOT_FILE;
				}else // if file not exist
					return ID.FILE_NEW_FILE;
			}else
				return ID.FILE_CANT_READ;
		} catch (Exception e) {
			LOGGER.severe("Error in checking file validity!");
			e.printStackTrace();
			return ID.FILE_NOT_VALID;
		}
	}


	/**
	 * Checks does some FileFilter of given list accepts given file.
	 *
	 * @param filters the filters
	 * @param file the file
	 * @return true, if accepted
	 * @throws Exception the exception
	 */
	private static boolean filterAccepts(ArrayList<FileFilter> filters, File file) throws Exception{
		for (Iterator<FileFilter> iterator = filters.iterator(); iterator.hasNext();) {
			FileFilter fileFilter = (FileFilter) iterator.next();
			if(fileFilter.accept(file))
				return true;
		}
		return false;
	}

	/**
	 * Checks does some FileFilter of given list accepts given file (given as String path of file).
	 *
	 * @param fileName the file name
	 * @param filterList the list of filters
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public static boolean accept(String fileName, FileFilter[] filterList) throws Exception{
		if(filterList != null && filterList.length>0)
		for (int i = 0; i < filterList.length; i++) {
			FileFilter filter=filterList[i];
			if(filter.accept(new File(fileName))){
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks does given single FileFilter accepts given file (given as String path of file).
	 *
	 * @param fileName the file name
	 * @param filter the filter
	 * @return true, if successful
	 */
	public static boolean accept(String fileName, FileFilter filter){
		try {
			return accept(fileName, new FileFilter[] {filter});
		} catch (Exception e) {
			LOGGER.severe("Error in checking file!");
			e.printStackTrace();
			return false;
		}
	}
}

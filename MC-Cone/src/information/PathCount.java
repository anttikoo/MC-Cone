package information;

/**
 * The Class PathCount. Contains file path and amount how many times has saved to that path.
 */
public class PathCount {
	
	/** The count. */
	private int count;
	
	/** The folder path. */
	private String folderPath;
	
	
	/**
	 * Instantiates a new path count.
	 *
	 * @param folderPath the folder path
	 */
	public PathCount(String folderPath){
		this.setCount(1);
		this.setFolderPath(folderPath);
	}
	
	/**
	 * Adds the countings with one.
	 */
	public void addOne(){
		this.count++;
	}
	
	/**
	 * Returns the count.
	 *
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * Returns the folder path.
	 *
	 * @return the folder path
	 */
	public String getFolderPath() {
		return folderPath;
	}
	
	/**
	 * Sets the count.
	 *
	 * @param count the new count
	 */
	private void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * Sets the folder path.
	 *
	 * @param folderPath the new folder path
	 */
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}


}

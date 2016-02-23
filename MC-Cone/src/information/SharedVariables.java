package information;

import java.awt.AlphaComposite;

/**
 * The Class SharedVariables. Contains static variables.
 */
public class SharedVariables {
	public static final int IMAGESET_EXPORT_MAX_RESOLUTION = 5000;
	public static final int DISTANCE_TO_REMOVE = 20;
	public static final int DISTANCE_TO_ADD = 10;
	public static final int GLOBAL_MIN_CELL_DIAMETER = 6;
	public static final int GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL = 4;
	public static final double GLOBAL_CIRCULARITY = 1.8;
	public static final int MAX_GAP = 6;
	public static int operationSystem=ID.OS_WINDOWS;
	public static int transparencyModeSRC = AlphaComposite.SRC;
	public static int transparencyModeIN = AlphaComposite.SRC_IN;
	public static int transparencyModeOVER = AlphaComposite.SRC_OVER;
	public static int transparencyModeOut= AlphaComposite.SRC_OUT;
	public static int transparencyModeATOP= AlphaComposite.SRC_ATOP;
	public static int usedDimmingMode=transparencyModeIN;
	public static final String widthUp = "WIDTH_UP";
	public static final String widthDown = "WIDTH_DOWN";
	public static final String heighthUp = "HEIGHT_UP";
	public static final String heightDown = "HEIGHT_DOWN";
	public static boolean useStrickSearch=false;
	public static boolean canUserSelectGridRectangles=false;
	public static final String version = "0.1b";
	
	

	
	/**
	 * Checks if is use strick search.
	 *
	 * @return true, if is use strick search
	 */
	public static boolean isUseStrickSearch() {
		return useStrickSearch;
	}

	/**
	 * Sets the ID of operation system.
	 *
	 * @param osID the new ID of operation system
	 */
	public static void setOS(int osID){
		operationSystem=osID;
	}

	/**
	 * Sets the transparencyIN.
	 *
	 * @param trm the new transparency in
	 */
	public static void setTransParencyIn(int trm){
		transparencyModeIN=trm;
	}

	/**
	 * Sets the transparency over.
	 *
	 * @param trm the new transparency over
	 */
	public static void setTransParencyOver(int trm){
		transparencyModeOVER=trm;
	}
	
	/**
	 * Sets the used dimming mode to srcAtop.
	 */
	public static void setUsedDimmingModeToSrc(){
		usedDimmingMode=transparencyModeSRC;
	}
	
	/**
	 * Sets the used dimming mode to srcAtop.
	 */
	public static void setUsedDimmingModeToSrcAtop(){
		usedDimmingMode=transparencyModeATOP;
	}
	
	/**
	 * Sets the used dimming mode to srcIn.
	 */
	public static void setUsedDimmingModeToSrcIn(){
		usedDimmingMode=transparencyModeIN;
	}
	
	/**
	 * Sets the used dimming mode to srcOut.
	 */
	public static void setUsedDimmingModeToSrcOut(){
		usedDimmingMode=transparencyModeOut;
	}
	
	/**
	 * Sets the used dimming mode to srcOver.
	 */
	public static void setUsedDimmingModeToSrcOver(){
		usedDimmingMode=transparencyModeOVER;
	}
	
	/**
	 * Sets the use strick search.
	 *
	 * @param useStrickSearch the new use strick search
	 */
	public static void setUseStrickSearch(boolean useStrickSearch) {
		SharedVariables.useStrickSearch = useStrickSearch;
	}
}

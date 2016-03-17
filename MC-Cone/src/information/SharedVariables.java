package information;

import java.awt.AlphaComposite;

/**
 * The Class SharedVariables. Contains static variables.
 */
public class SharedVariables {
	
	
	/** The available processors. */
	public static int availableProcessors=1;
	
	/** The can user select grid rectangles. */
	public static boolean canUserSelectGridRectangles=false;
	
	/** The Constant GAPPING_CELL_SIZE_SMALL. */
	public static final int GAPPING_CELL_SIZE_SMALL = 10;
	
	/** The Constant GAPPING_CELL_SIZE_MEDIUM. */
	public static final int GAPPING_CELL_SIZE_MEDIUM = 30;
	
	/** The Constant GAPPING_CELL_SIZE_BIG. */
	public static final int GAPPING_CELL_SIZE_BIG = 50;
		
	/** The Constant IMAGE_SIZE_SMALL. Size in pixels. */
	public static final int IMAGE_SIZE_SMALL = 1000000;
	
	/** The Constant IMAGE_SIZE_MEDIUM. Size in pixels.*/
	public static final int IMAGE_SIZE_MEDIUM = 5000000;
	
	/** The Constant IMAGE_SIZE_BIG. Size in pixels. */
	public static final int IMAGE_SIZE_BIG = 10000000;
	
	/** The Constant IMAGE_SIZE_BIG. Size in pixels. */
	public static final int IMAGE_SIZE_EXTRA_BIG = 50000000;
	
	/** The Constant DISTANCE_TO_ADD. */
	public static final int DISTANCE_TO_ADD = 10;
	
	/** The Constant DISTANCE_TO_REMOVE. */
	public static final int DISTANCE_TO_REMOVE = 20;
	
	/** The Constant GLOBAL_CIRCULARITY. */
	public static final double GLOBAL_CIRCULARITY = 1.8;
	
	/** The Constant GLOBAL_MIN_CELL_DIAMETER. */
	public static final int GLOBAL_MIN_CELL_DIAMETER = 5;
	
	/** The Constant GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL. */
	public static final int GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL = 4;
	
	/** The Constant heightDown. */
	public static final String heightDown = "HEIGHT_DOWN";
	
	/** The Constant heighthUp. */
	public static final String heighthUp = "HEIGHT_UP";
	
	/** The Constant IMAGESET_EXPORT_MAX_RESOLUTION. */
	public static final int IMAGESET_EXPORT_MAX_RESOLUTION = 5000;
	
	/** The Constant MAX_GAP. */
	public static final int MAX_GAP = 6;
	
	/** The operation system. */
	public static int operationSystem=ID.OS_WINDOWS;
	
	/** The remember_answer_pc_ow. MessageBox of overwriting markings in Precounting. 
	 * If user select box remember the selection will be saved here. */
	public static int remember_answer_pc_ow = ID.UNDEFINED;
	
	/** The transparency mode atop. */
	public static int transparencyModeATOP= AlphaComposite.SRC_ATOP;
	
	/** The transparency mode in. */
	public static int transparencyModeIN = AlphaComposite.SRC_IN;
	
	/** The transparency mode out. */
	public static int transparencyModeOut= AlphaComposite.SRC_OUT;
	
	/** The transparency mode over. */
	public static int transparencyModeOVER = AlphaComposite.SRC_OVER;
	
	/** The transparency mode src. */
	public static int transparencyModeSRC = AlphaComposite.SRC;
	
	/** The used dimming mode. */
	public static int usedDimmingMode=transparencyModeIN;
	
	/** The use multi threading in calculating coordinates. */
	public static boolean useMultiThreadingInCalculatingCoordinates=false;
	
	/** The use mult threading increating point groups. */
	public static boolean useMultThreadingIncreatingPointGroups=false;

	/** The use strick search. */
	public static boolean useStrickSearch=false;

	/** The Constant version. */
	public static final String version = "0.9";

	/** The Constant widthDown. */
	public static final String widthDown = "WIDTH_DOWN";
	
	/** The Constant widthUp. */
	public static final String widthUp = "WIDTH_UP";
	
	
	/**
	 * Returns the remember_answer_pc.
	 *
	 * @return the remember_answer_pc_value
	 */
	public static int getRememberAnswerPC() {
		return remember_answer_pc_ow;
	}
	
	/**
	 * Checks if is use strick search.
	 *
	 * @return true, if is use strick search
	 */
	public static boolean isUseStrickSearch() {
		return useStrickSearch;
	}
	


	/**
	 * Sets the available processors.
	 *
	 * @param availableProcessors the new available processors
	 */
	public static void setAvailableProcessors(int availableProcessors) {
		SharedVariables.availableProcessors = availableProcessors;
	//	SharedVariables.availableProcessors=1;
		if(SharedVariables.availableProcessors>1){
			useMultiThreadingInCalculatingCoordinates=true;
			useMultThreadingIncreatingPointGroups=true;
		}
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
	 * Sets the remember_answer_pc.
	 *
	 * @param remember_answer_pc_value the new remember_answer_pc_value
	 */
	public static void setRememberAnswerPC(int remember_answer_pc_value) {
		SharedVariables.remember_answer_pc_ow = remember_answer_pc_value;
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

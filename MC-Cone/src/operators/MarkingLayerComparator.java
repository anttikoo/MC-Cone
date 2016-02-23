package operators;


import java.util.Comparator;
import java.util.logging.Logger;

import information.MarkingLayer;

/**
 * The Class MarkingLayerComparator.
 */
public class MarkingLayerComparator implements Comparator<MarkingLayer>{
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	
	public int compare(MarkingLayer ml1, MarkingLayer ml2) {
		try {
			if(ml1.getOrder() >0 && ml2.getOrder()>0){
				//  is same ->
				if(ml1.getOrder() == ml2.getOrder()){
					return 0;
				}
				// 
				else{ // first order smaller
					if(ml1.getOrder() < ml2.getOrder()) // first point has smaller x 
						return -1;		
				}
				return 1; // first bigger
			}
			else return 0;
		} catch (Exception e) {
			LOGGER.severe("Error in comparing MarkingLayers by order!");
			e.printStackTrace();
			return 0;
		}
	}

	

}

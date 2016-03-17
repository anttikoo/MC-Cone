package operators;

import java.util.Comparator;
import java.util.logging.Logger;

/**
 * The Class WeightPointComparator. Compares WeightPoints by the weight value.
 */
public class WeightPointComparator implements Comparator<WeightPoint>{
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/** Should use descending ordering. */
	private boolean descending =false;
	
	/**
	 * Instantiates a new weight point comparator.
	 */
	public WeightPointComparator() {
		this.descending=false; // ascending
	}
	
	/**
	 * Instantiates a new weight point comparator.
	 *
	 * @param descending boolean the descending ordering
	 */
	public WeightPointComparator(boolean descending) {
		this.descending=descending;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(WeightPoint p1, WeightPoint p2) {
		try {
			// compare weights and produces descending order
			if(p1.getWeight() > p2.getWeight()){ // first Point has bigger point
				if(descending){
					return 1;
				}
				else{
					return -1;
				}
			}
			
			else{
				if(p1.getWeight() < p2.getWeight()) // first point has smaller weight
					if(descending){
						return -1;
					}
					else{
						return 1;
					}
				
			}
			return 0; // equal point
		} catch (Exception e) {
			LOGGER.severe("Error in comparing WeightPoints!");
			e.printStackTrace();
			return 0;
		}
	}

}
package operators;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;
import java.util.logging.Logger;
import information.CentroidFromNpoints;
import information.SharedVariables;
import math.geom2d.Point2D;


/**
 * The Class CalculateCoordinatesTask.
 */
public class CalculateCoordinatesTask implements Runnable{
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/** The continue counting. */
	private boolean continueCounting=false;

	/** The current_coordinates. */
	private ArrayList<Point> current_coordinates;

	/** The current_gap. A gap between pixels that are evaluated.  */
	private int current_gap =2;

	/** The current_max_cell_size. */
	private int current_max_cell_size;

	/** The current_max_coordinate_number_in_cell. */
	private int current_max_coordinate_number_in_cell=Integer.MIN_VALUE;

	/** The current_min_cell_size. */
	private int current_min_cell_size;  

	/** The current centroid coordinates. Results of this run of Thread.  */
//	private ArrayList<Point> currentCentroidCoordinates; 
	
	private ArrayList<CentroidFromNpoints> currentCentroidCoordinates;

	/** The max cell number in cell group. Prevents that stack of coordinates will not be too big. */
	private int max_cell_number_in_cell_group=10;


	/** The global_min_coordinate_number_in_cell. This is global minimum for coordinate number. */
	//private final int global_min_coordinate_number_in_cell =SharedVariables.GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL;

	/** The min_distance_between_cells_boundaries. */
	private int min_distance_between_cells_boundaries=5;

	/** The precounter thread. */
	PreCounterThread precounterThread;
	
	/** The progressed coordinates. */
	private int progressedCoordinates=0;
	
	/** The should force gap bigger. */
	private boolean shouldForceGAPbigger=false;
	
	/** The skipped weight points. */
	private int skippedWeightPoints=0;

	/**
	 * Instantiates a new calculate coordinates task.
	 *
	 * @param pct the pct
	 * @param coordinates the coordinates
	 * @param currentGap the current gap
	 * @param currentMaxCellSize the current max cell size
	 * @param currentMaxCoordinateNumberInCell the current max coordinate number in cell
	 * @param currentMinCellSize the current min cell size
	 * @param maxCellNumberInCellGroup the max cell number in cell group
	 * @param minDinstanceBetweenCellBoundaries the min dinstance between cell boundaries
	 * @param threadNumber the number of threads
	 */
	public CalculateCoordinatesTask(PreCounterThread pct, ArrayList<Point> coordinates, int currentGap, int currentMaxCellSize, int currentMaxCoordinateNumberInCell, 
			int currentMinCellSize, int maxCellNumberInCellGroup, int minDinstanceBetweenCellBoundaries, int threadNumber){
		this.precounterThread=pct;
		this.current_coordinates=coordinates;
		this.current_gap = currentGap;
		this.current_max_cell_size = currentMaxCellSize;
		this.current_max_coordinate_number_in_cell=currentMaxCoordinateNumberInCell;
		this.current_min_cell_size=currentMinCellSize;
		this.max_cell_number_in_cell_group= maxCellNumberInCellGroup;
		this.min_distance_between_cells_boundaries=minDinstanceBetweenCellBoundaries;
		

	}

	/**
	 * Calculates centroid of group of WeightPoints.
	 *
	 * @param weightPointList the weight point list
	 * @return the point
	 */
	private Point calculateCentroid(ArrayList<WeightPoint> weightPointList ){
		try {
			if(weightPointList != null && weightPointList.size()>0){
				double sum_Of_weights=0;

				double sum_Of_X_distance_weights=0;
				double sum_Of_Y_distance_weights=0;

				Iterator<WeightPoint> wIterator= weightPointList.iterator();
				while(wIterator.hasNext()){
					WeightPoint wp = wIterator.next();
					sum_Of_weights+= wp.getWeight();
					sum_Of_X_distance_weights += wp.getPoint().x*wp.getWeight();
					sum_Of_Y_distance_weights += wp.getPoint().y*wp.getWeight();

				}

				int x_coord= (int)(sum_Of_X_distance_weights/sum_Of_weights);
				int y_coord = (int)(sum_Of_Y_distance_weights/sum_Of_weights);
				return new Point(x_coord, y_coord);
			}
			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in calculating centroids!");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Calculates outer radius area.
	 *
	 * @param countNumber the count number
	 * @param radius_bigger the radius_bigger
	 * @param radius_smaller the radius_smaller
	 * @return the double
	 */
	private double calculateOuterRadiusArea(int countNumber, int radius_bigger, int radius_smaller){
		try {
			double areaBigger= Math.PI*(double)radius_bigger*(double)radius_bigger;
			double areaSmaller= Math.PI*(double)radius_smaller*(double)radius_smaller;
			return ((double)countNumber)/(areaBigger-areaSmaller);
		} catch (Exception e) {
			LOGGER.severe("Error in calucalating outer radius area in precounting!");
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Compares is point too close to any point at pool of current coordinates.
	 *
	 * @param midPoint the mid point
	 * @param currentCentroidCoordinates the current centroid coordinates
	 * @param minDistance the minimum distance to points
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private boolean compareIsTooClose(Point midPoint, ArrayList<CentroidFromNpoints> currentCentroidCoordinates, int minDistance) throws Exception{
		Iterator<CentroidFromNpoints> coordIterator = currentCentroidCoordinates.iterator();
		while(coordIterator.hasNext()){
			Point p = coordIterator.next();
			if(isPointsTooClose(midPoint, p, minDistance))	// is too close			
					return true;					
		}
		return false;

	}
	
	/**
	 * Checks if is given points closer to each other than given distance.
	 *
	 * @param p1 the point 1
	 * @param p2 the point 2
	 * @param minDistance the minimum distance to points
	 * @return true, if is points too close
	 * @throws Exception the exception
	 */
	private boolean isPointsTooClose(Point p1, Point p2, int minDistance) throws Exception{
		if(p1 != null && p2 != null){
			if(p1.x > p2.x-minDistance && p1.x < p2.x+minDistance)
				if(p1.y > p2.y-minDistance && p1.y < p2.y+minDistance)
					return true;	
		}
		return false;

	}
	
	
	/**
	 * Compares is point too close to any point at pool of current coordinates. If is too close, checks which point has composed of bigger group of points.
	 *
	 * @param point the CentroidFromNpoints
	 * @param minDistance the minimum distance to points
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean compareIsTooCloseOrMinor(CentroidFromNpoints point, int minDistance) throws Exception{
		Iterator<CentroidFromNpoints> centroidIterator = this.currentCentroidCoordinates.iterator();
		while(centroidIterator.hasNext()){
			CentroidFromNpoints cPoint = centroidIterator.next();
			if(compareIsTooClose(cPoint, this.currentCentroidCoordinates, minDistance)){ // too close
				// check which one has more points
				if(isPointsTooClose(point, cPoint, minDistance)){
					if(point.getNumberOfCoordinates() < cPoint.getNumberOfCoordinates()){
						return true;
					}
					
				}
			}
			
		}
		return false;	
	}
	


	/**
	 * Creates groups from current coordinates by using distances between them. Basically coordinates close to each other are in same cell.
	 */

	private void createPointGroups(){
		try {

			Collections.sort(this.current_coordinates, new CoordinateComparator());
			currentCentroidCoordinates = new ArrayList<CentroidFromNpoints>();
			int sizeOfWholeList= this.current_coordinates.size();
			
			while(this.continueCounting && current_coordinates.size()>0 ){ // Points are removed from list in method getNeighbourPoints
				int randomIndex=(int)(Math.random()*current_coordinates.size());
				Point firstPoint = current_coordinates.get(randomIndex); // get randomly the first point
				Stack<Point> stack  = new Stack<Point>();
				stack.push(firstPoint);
				current_coordinates.remove(firstPoint);
				//   LOGGER.fine("all coordinates: "+copy_of_current_finalCoordinates.size());
				// recursively get points to list
				ArrayList<Point> groupPoints = getPoints(stack, new ArrayList<Point>());

				if(groupPoints != null && groupPoints.size() >= SharedVariables.GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL){//&& groupPoints.size() < this.current_max_coordinate_number_in_cell){
					createWeightedPointGroup(groupPoints);
					setProgressedCoordinates(sizeOfWholeList-this.current_coordinates.size()); // show progress to user
				}
				



			}
			LOGGER.fine("Skipped weightpoints because in small groups: "+this.skippedWeightPoints);
			
		} catch (Exception e) {
			LOGGER.severe("Error in creating point groups in precounting.");
			e.printStackTrace();
		}
	}

	/**
	 * Creates the weighted point group. First gets the list of weighted points. 
	 * Then calculates centroids of weighted points to get the middle point -> saved as the new final coordinate.
	 *
	 * @param pointGroups the point groups
	 * @throws Exception the exception
	 */
	private void createWeightedPointGroup(ArrayList<Point> pointGroups) throws Exception{
		// create initial weighted list
	//	ArrayList<WeightPoint> weightPointList = createWeightPointListRandom(pointGroups);
		ArrayList<WeightPoint> weightPointList = createWeightPointListPeakedRandom(pointGroups);

		if(weightPointList.size()>= SharedVariables.GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL){
			int rounds =0;
			// sort list of weighted points
			Collections.sort(weightPointList, new WeightPointComparator());
			// go trough weight points
			outerLoop:
				while(weightPointList.size()>= SharedVariables.GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL && rounds<=10){
					// calculate center of points by using weights
					CentroidFromNpoints midPoint = new CentroidFromNpoints(calculateCentroid(weightPointList), weightPointList.size());
					// count circular data
					MaxDistancePoint[] maxDistanceValues=getMaxDistanceRoundValues(midPoint, this.current_max_cell_size, weightPointList);
					// if amount of points is not exceeding maximum amount of point in cell and if user has selected strict precounting then check is cell circular
					if(weightPointList.size()<= this.current_max_coordinate_number_in_cell && (!SharedVariables.useStrickSearch || SharedVariables.useStrickSearch && isCircular(maxDistanceValues))){
						if(!compareIsTooClose(midPoint, this.currentCentroidCoordinates, this.current_max_cell_size)){
							// check is cell bigger than minimum size of cells
							if(isDistanceBiggerThanMinimum(weightPointList, this.current_min_cell_size)){						
								// add one cell
								this.currentCentroidCoordinates.add(midPoint);
							}
						}
						break outerLoop;
					}
					else{// possible cluster of cells -> try to separate them
					//	separateClusteredCellsMethodA(maxDistanceValues, weightPointList);
						separateClusteredCellsMethodB(weightPointList); // use B method
				}
					rounds++;			
				}
		}else{
		//	LOGGER.fine("WeightPoint list too small");
			skippedWeightPoints+= weightPointList.size();
		}
	}
	
	/**
	 * Separate clustered cells method B. Tries to form cells from given coordinates. 
	 *
	 * @param weightPointList the weight point list
	 * @throws Exception the exception
	 */
	private void separateClusteredCellsMethodB(ArrayList<WeightPoint> weightPointList) throws Exception{
		if(weightPointList != null && weightPointList.size()>0){
			// get randomly a point from list and find the local maximum of distance -> do it 100 rounds
			ArrayList<WeightPoint> foundLocalMaximumList = new ArrayList<WeightPoint>();
			
			// iterate 100 runs to get points with local maximum weights
			for(int i=0; i<100;i++){
				
				// get random WeightPoint
				 Random generator = new Random();
				 int index = generator.nextInt(weightPointList.size());
				 WeightPoint wp = weightPointList.get(index);
				 WeightPoint foundWP=null;
				 int counter=0;
				 
				 // start iteration to find WeightPoint with local maximum weight
				 do{
					 counter++;
					 foundWP = getWeightPointWithBiggestWeightAtDistance(wp, weightPointList, Math.max(this.current_gap*2,this.current_max_cell_size/2+this.min_distance_between_cells_boundaries));
					 if(foundWP.x == wp.x && foundWP.y == wp.y){ // reached to WeightPoint with biggest Weight
						 if(foundLocalMaximumList != null && foundLocalMaximumList.size()>0){
							 Iterator<WeightPoint> foundIterator= foundLocalMaximumList.iterator();
							 while(foundIterator.hasNext()){
								 WeightPoint possiblyMatch=foundIterator.next();
								 if(possiblyMatch.x == foundWP.x && possiblyMatch.y == foundWP.y){
									 possiblyMatch.increaseWeight(foundWP.getWeight());
									 break;
								 }
									 
							 }
							 // if reached here - not found possibly match
							 foundLocalMaximumList.add(foundWP); // found a new point with local maximum -> add to list
							 break;
						 }
						 else{ // add to empty list
							 foundLocalMaximumList.add(foundWP); // found local maximum -> add to list
							 break; 
						 }
						 
						 
					 }
					 // continue do loop to find local maximum
					 wp=foundWP;
		 
				 }while(counter<100);
			
			}
			
			
			// calculate average of weight
			Iterator<WeightPoint> wIterator = weightPointList.iterator();
					
			double weightSum=0;
			while(wIterator.hasNext()){
				weightSum += wIterator.next().getWeight();			
			}
			double average = weightSum/weightPointList.size();
			
			
			if(foundLocalMaximumList != null && foundLocalMaximumList.size()>0){
				// sort decsending
				Collections.sort(foundLocalMaximumList, new WeightPointComparator(true)); // descending ordering
				
				double weightTreshold = average + (foundLocalMaximumList.get(0).getWeight()-average)/9*10;
					
				while(foundLocalMaximumList.size() > 0){
					// get first point and remove all point at distance current_min_cell_size/2
					
					WeightPoint majorPoint = foundLocalMaximumList.get(0);
					ArrayList<WeightPoint> pointsInside = getPointsInside(majorPoint, this.current_min_cell_size/2, weightPointList);
					if(majorPoint.getWeight() > weightTreshold){
						// check that enough points over majorPoint
						
						if(pointsInside != null && pointsInside.size()>0 && 
								pointsInside.size()>=SharedVariables.GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL &&
										pointsInside.size()<=current_max_coordinate_number_in_cell){
							
							CentroidFromNpoints midPoint = new CentroidFromNpoints(majorPoint,pointsInside.size());
							// check is the midpoint too close to points at currentCentroidCoordinates
							if(!compareIsTooClose(midPoint,this.currentCentroidCoordinates, this.current_max_cell_size))
								this.currentCentroidCoordinates.add(midPoint);
						
						}
						
					}
					//	foundMajorLocalMaximumList.add(majorPoint);
					// remove from list
					weightPointList.removeAll(pointsInside);
					Collections.sort(weightPointList, new WeightPointComparator());
					
					foundLocalMaximumList.remove(majorPoint);
								
					ArrayList<WeightPoint> minorPoints=getPointsInside(majorPoint, this.current_min_cell_size/2, foundLocalMaximumList);
					
					if(minorPoints != null && minorPoints.size()>0)
						foundLocalMaximumList.removeAll(minorPoints);
						
				}				
			}				
		}	
	}
	
	/**
	 * Separate clustered cells method A. Tries to form cells from given coordinates. 
	 * Not used in this version. Should be combined somehow with method B.
	 *
	 * @param maxDistanceValues the max distance values
	 * @param weightPointList the weight point list
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unused")
	private void separateClusteredCellsMethodA(MaxDistancePoint[] maxDistanceValues, ArrayList<WeightPoint> weightPointList) throws Exception{
		
		WeightPoint w = getWeightPointWithBiggestDistance(maxDistanceValues); // get point that has longest distance from center
		if(w==null)
			w = weightPointList.get(weightPointList.size()-1); // get last point of list

		WeightPoint b =null;
		if(w != null){
			// find nearest point with biggest weight
			int counter=0;
			do{ // find point that has biggest weight
				counter++;
				if(b != null)
					w=b;
				b = getWeightPointWithBiggestWeightAtDistance(w, weightPointList, Math.max(this.current_gap*2,this.current_min_cell_size/2)); // finds the biggest weight point at distance
			}while((w.x != b.x || w.y != b.y) && counter<100); // if going to same coordinate -> found biggest weight

			if(w.x == b.x && w.y == b.y){ // w has the biggest weight -> near at center of cell
				int radius=this.current_min_cell_size/2;
				Point p = calculateCentroid(getPointsInside(w.getPoint(), radius, weightPointList));
				if(p == null)
					p=w.getPoint();


				ArrayList<WeightPoint> selectedPointsForCell=new ArrayList<WeightPoint>();
				ArrayList<WeightPoint> candidatePointList=getPointsInside(p, radius, weightPointList); // get points at distance of radius
				if(candidatePointList != null)
					if(candidatePointList != null && candidatePointList.size()>= 1 &&
					candidatePointList.size() <=this.current_max_coordinate_number_in_cell){

						if(candidatePointList.size() >= SharedVariables.GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL && isCircular(p, candidatePointList)){ //
							selectedPointsForCell.addAll(candidatePointList);
						}
						double averagePointsPerArea = ((double)candidatePointList.size())/(Math.PI*(double)radius*(double)radius);
						double pointsPerArea=averagePointsPerArea;
						int pointsBefore= candidatePointList.size();

						// search the correct cell size from min size to max size
						candidateLoop:
							while(pointsPerArea*1.5 > averagePointsPerArea && radius <=this.current_max_cell_size/2){
								radius+=this.current_gap; // grow radius with gap value
								if(candidatePointList != null && candidatePointList.size()>=SharedVariables.GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL)
									p=calculateCentroid(candidatePointList);
								candidatePointList=getPointsInside(p, radius, weightPointList);
								if(candidatePointList==null){
									break;
								}
								else{  // calculate how many points are at area grown in last loop.
									pointsPerArea=calculateOuterRadiusArea(candidatePointList.size()-pointsBefore, radius, radius-this.current_gap);
									if(pointsPerArea*5 > averagePointsPerArea){ // not reached to cell boundary yet because lot of cells
										averagePointsPerArea=((double)candidatePointList.size())/(Math.PI*(double)radius*(double)radius);
										pointsBefore=candidatePointList.size();

										if(candidatePointList.size() >=SharedVariables.GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL &&
												candidatePointList.size() <= this.current_max_coordinate_number_in_cell && isCircular(p, candidatePointList)){ // is points in circle
											selectedPointsForCell.clear();
											selectedPointsForCell.addAll(candidatePointList);

										}
									}
									else{
										break candidateLoop;
									}
								}
							}
						// create midpoint from selectedPoints
						if(selectedPointsForCell != null && selectedPointsForCell.size()>0 && 
								selectedPointsForCell.size()>=SharedVariables.GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL &&
								selectedPointsForCell.size()<=current_max_coordinate_number_in_cell && isDistanceBiggerThanMinimum(selectedPointsForCell, this.current_min_cell_size)){
							// calculate the centroid of cells -> the final point to be saved.
							CentroidFromNpoints midPoint = new CentroidFromNpoints(calculateCentroid(selectedPointsForCell),selectedPointsForCell.size());
							// check is the midpoint too close to points at currentCentroidCoordinates
							if(!compareIsTooClose(midPoint,this.currentCentroidCoordinates, this.current_max_cell_size))
								this.currentCentroidCoordinates.add(midPoint);
						}
						weightPointList.removeAll(candidatePointList); // remove selected points
						Collections.sort(weightPointList, new WeightPointComparator());

					}
			} // not
		}
	}
	
	/**
	 * Checks if is distance bigger than minimum.
	 *
	 * @param weightPoints the weight points
	 * @param minimumSize the minimum size
	 * @return true, if is distance bigger than minimum
	 * @throws Exception the exception
	 */
	private boolean isDistanceBiggerThanMinimum(ArrayList<WeightPoint> weightPoints, int minimumSize) throws Exception{
		int maxDistance =0;
		if(weightPoints != null && weightPoints.size() > 0){
			for(int i=0;i<weightPoints.size();i++){
				WeightPoint wp1 = weightPoints.get(i);
				for(int j=0;j<weightPoints.size();j++){
					WeightPoint wp2 = weightPoints.get(j);
					if(i!=j){
						double distance = wp1.distance(wp2);
						if(distance>maxDistance)
							maxDistance = (int)distance; // collect max distance
					}
					
				}
				
			}
			
			if(maxDistance >= minimumSize)
				return true;
			
			
		}
		return false;
		
		
	}

/*
	 * Creates the weighted points from given list of points. 
	 * The weight of point is calculated by amount of neighbor points and how far they are. 
	 * The weight of point is proportional to distance to other points. Lot of points close -> big weight.
	 * 
	 *
	 * @param pointGroups the point groups
	 * @return the array list
	 
	@SuppressWarnings("unused")
	private ArrayList<WeightPoint> createWeightPointList(ArrayList<Point> pointGroups){
		try {
			ArrayList<WeightPoint> weightPointList = new ArrayList<WeightPoint>();
			if(pointGroups != null && pointGroups.size()>0)
				for (int j=0;j<pointGroups.size()-1;j++) {
					Point point = pointGroups.get(j);
					WeightPoint wpoint = new WeightPoint(point);
					// set boundaries for binary search
					int lowerBoundValue=Math.max(0,(int)wpoint.getPoint().x-this.current_max_cell_size/2);
					int upperBoundValue=(int)(wpoint.getPoint().x+this.current_max_cell_size/2);
					// find first and last index of points in pointGroups which are close to wpoint in horizontal level
					int[] bounds=startEndBinarySearch(pointGroups, lowerBoundValue, upperBoundValue);
					if(bounds != null && bounds[0] >=0 && bounds[1] <pointGroups.size() && bounds[0] <= bounds[1]){
						for (int i = bounds[0]; i <= bounds[1]; i++) {
							// calculate distance
							double distance = wpoint.getPoint().distance((Point)pointGroups.get(i));
							if( distance < this.current_max_cell_size/2 && distance > 0){
								wpoint.increaseWeight(1/distance); // increase weight by distance
							}
						}
						weightPointList.add(wpoint);
					}
				}
			return weightPointList;
		} catch (Exception e) {
			LOGGER.severe("Error in creating weightPoint list in precounting!");
			e.printStackTrace();
			return null;
		}
	}
*/	
	
/*
	 * Creates the weighted points from given list of points. 
	 * The weight of point is calculated by amount of neighbor points and how far they are. 
	 * The weight of point is proportional to distance to other points. Lot of points close -> big weight.
	 * This method is unused - replaced with
	 *
	 * @param pointGroups the point groups
	 * @return the array list
	 
	@SuppressWarnings("unused")
	private ArrayList<WeightPoint> createWeightPointListRandom(ArrayList<Point> pointGroups){
		try {
			ArrayList<WeightPoint> weightPointList = new ArrayList<WeightPoint>();
			// create list if Weightpoints from Points
			Iterator<Point> pointsIterator = pointGroups.iterator();
			while(pointsIterator.hasNext()){
				WeightPoint wpoint = new WeightPoint(pointsIterator.next());
				weightPointList.add(wpoint);
			}
			
			for (int repeats = 0; repeats < 20; repeats++) {
				// randomize the list
				long seed = System.nanoTime();
				Collections.shuffle(weightPointList, new Random(seed));
				if (pointGroups != null && pointGroups.size() > 0 && weightPointList != null && weightPointList.size() > 0)
					for (int j = 0; j < weightPointList.size() - 1; j++) {

						WeightPoint wpoint = weightPointList.get(j);
						// set boundaries for binary search
						int lowerBoundValue = Math.max(0, (int) wpoint.getPoint().x - this.current_max_cell_size / 2);
						int upperBoundValue = (int) (wpoint.getPoint().x + this.current_max_cell_size / 2);
						// find first and last index of points in pointGroups which are close to wpoint in horizontal level
						int[] bounds = startEndBinarySearch(pointGroups, lowerBoundValue, upperBoundValue);
						if (bounds != null && bounds[0] >= 0 && bounds[1] < pointGroups.size() && bounds[0] <= bounds[1]) {
							for (int i = bounds[0]; i <= bounds[1]; i++) {
								// calculate distance
								double distance = wpoint.getPoint().distance((Point) pointGroups.get(i));
								
								if (distance < this.current_max_cell_size / 2 && distance > 0) {
									wpoint.increaseWeight(1 / distance); // increase weight by distance
								}
							}
						}
					} 
			}
			return weightPointList;
		} catch (Exception e) {
			LOGGER.severe("Error in creating weightPoint list in precounting!");
			e.printStackTrace();
			return null;
		}
	}
	
*/
	
	/**
	 * Creates the weighted points from given list of points. 
	 * The weight of point is calculated by amount of neighbor points and how far they are. 
	 * The weight of point is proportional to distance to other points. Lot of points close -> big weight.
	 *
	 * @param pointGroups the point groups
	 * @return the array list of WeightPoints
	 */
	private ArrayList<WeightPoint> createWeightPointListPeakedRandom(ArrayList<Point> pointGroups){
		try {
			ArrayList<WeightPoint> weightPointList = new ArrayList<WeightPoint>();
			ArrayList<WeightPoint> orderedWeightPointList = new ArrayList<WeightPoint>();
			// create list if Weightpoints from Points
			Iterator<Point> pointsIterator = pointGroups.iterator();
			while(pointsIterator.hasNext()){
				WeightPoint wpoint = new WeightPoint(pointsIterator.next());
				weightPointList.add(wpoint);
				orderedWeightPointList.add(wpoint);
			}
			Collections.sort(orderedWeightPointList,new WeightPointComparator());
			
			for (int repeats = 0; repeats < 20; repeats++) {
				// randomize the list
				long seed = System.nanoTime();
				Collections.shuffle(weightPointList, new Random(seed));
				if (pointGroups != null && pointGroups.size() > 0 && weightPointList != null && weightPointList.size() > 0)
					for (int j = 0; j < weightPointList.size() - 1; j++) {

						WeightPoint wpoint = weightPointList.get(j);
						// set boundaries for binary search
						int lowerBoundValue = Math.max(0, (int) wpoint.getPoint().x - this.current_max_cell_size / 2);
						int upperBoundValue = (int) (wpoint.getPoint().x + this.current_max_cell_size / 2);
						// find first and last index of points in pointGroups which are close to wpoint in horizontal level
						int[] bounds = startEndBinarySearchWithWeightPoints(orderedWeightPointList, lowerBoundValue, upperBoundValue);
						if (bounds != null && bounds[0] >= 0 && bounds[1] < pointGroups.size() && bounds[0] <= bounds[1]) {
							for (int i = bounds[0]; i <= bounds[1]; i++) {
								// calculate distance
								double distance = wpoint.getPoint().distance((Point) pointGroups.get(i));
								double weightOfPoint = orderedWeightPointList.get(i).getWeight();
								if (distance < this.current_max_cell_size / 2 && distance > 0) {
									wpoint.increaseWeight(weightOfPoint / distance); // increase weight by weight of neighbour
								}
							}
						}
					} 
			}
			return weightPointList;
		} catch (Exception e) {
			LOGGER.severe("Error in creating weightPoint list in precounting!");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns the centroid coordinates.
	 *
	 * @return the centroid coordinates
	 */
	public ArrayList<CentroidFromNpoints> getCentroidCoordinates(){
		return this.currentCentroidCoordinates;
		
	}

	/**
	 * Returns the max distance round values. The points are separated
	 *
	 * @param mP the point where other points are compared
	 * @param circleSize the circle size
	 * @param pList the list
	 * @return the max distance round values
	 */
	private MaxDistancePoint[] getMaxDistanceRoundValues(Point mP, int circleSize, ArrayList<WeightPoint> pList){

		try {
			MaxDistancePoint[] maxPoints=new MaxDistancePoint[8];
			for (Iterator<WeightPoint> iterator = pList.iterator(); iterator.hasNext();) {
				WeightPoint searchPoint = iterator.next();
				int sliceNumber= isInsideWhichSlice(mP, searchPoint, circleSize);
				if(sliceNumber>0 && sliceNumber <=8){
					double distance = mP.distance(searchPoint);
					if(maxPoints[sliceNumber-1] == null || maxPoints[sliceNumber-1].getDistance() < distance){
						maxPoints[sliceNumber-1] = new MaxDistancePoint(searchPoint, distance);
					}
				}
			}
			return maxPoints;
		} catch (Exception e) {
			LOGGER.severe("Error in getting maximum distance values!");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the neighbor points that are close enough to given point.
	 *
	 * @param p the Point which neighbors are calculated
	 * @return the neighbor points
	 * @throws Exception the exception
	 */
	private ArrayList<Point> getNeighbourPoints(Point p) throws Exception{
		ArrayList<Point> neighbours=new ArrayList<Point>();
		// calculate the max distance 
	//	int maxDistance= Math.min((int)((double)this.current_max_cell_size/(double)this.min_distance_between_cells_boundaries), this.current_min_cell_size);
		int maxDistance= (int)((double)this.current_max_cell_size/(double)this.min_distance_between_cells_boundaries);	
		int lowerBoundValue=Math.max(p.x-maxDistance,0); // minimum bounds for binary search
		int upperBoundValue=p.x+maxDistance;				// maximum bounds for binary search
		int[] startEndIndexes=startEndBinarySearch(this.current_coordinates, lowerBoundValue, upperBoundValue);

		if(startEndIndexes != null && startEndIndexes[0] >= 0
				&& startEndIndexes[1] <= this.current_coordinates.size()-1 && startEndIndexes[0] < startEndIndexes[1])

			for (int i = startEndIndexes[0]; i <= startEndIndexes[1]; i++) {
				Point point = this.current_coordinates.get(i);
				if(point != null)
					if(point.distance(p) <maxDistance){
						neighbours.add(point);	
					}
			}

		this.current_coordinates.removeAll(neighbours);
		return neighbours;
	}

	/**
	 * Recursively collects all neighbor Points for Points at stack.
	 *
	 * @param stack Stack containing Points
	 * @param groupPoints ArrayList of Points which are
	 * @return ArrayList of Points that has been collected in recursion
	 */
	private ArrayList<Point> getPoints(Stack<Point> stack, ArrayList<Point> groupPoints){

		try {
			if(!continueCounting) // stop search
				return null;
			if(stack.isEmpty())
				return groupPoints;
			//   LOGGER.fine("stack: "+stack.size());
			Point firstPoint = stack.pop();
			groupPoints.add(firstPoint);

			ArrayList<Point> neighbourPoints = null;
			try {
				neighbourPoints = getNeighbourPoints(firstPoint);
			} catch (StackOverflowError st) {
				LOGGER.severe("Error in precounting: too big stacks");
				continueCounting=false;
				setShouldForceGAPbigger(true);
			//	st.printStackTrace();
				return null;
			}
			
			if(neighbourPoints == null)
				return null; // could this be return groupPoints;
			stack.addAll(neighbourPoints); // adds Points to stack and removes from copy_of_current_finalCoordinates
			if(stack.size()> this.current_max_coordinate_number_in_cell*this.max_cell_number_in_cell_group) // if too much of coordinates in stack
				return null; // could this be return groupPoints;
			
			return getPoints(stack, groupPoints);
			
		} catch (Exception e) {
			continueCounting=false;
			LOGGER.severe("Error in grouping points!");
		//	e.printStackTrace();
			return null;
		}
	}


	/**
	 * Returns the points which distance to given mid point is smaller than given maximum distance.
	 *
	 * @param mPoint the mid point
	 * @param distance the distance
	 * @param weightPointList the list of Weightpoints
	 * @return the points inside
	 * @throws Exception the exception
	 */
	public ArrayList<WeightPoint> getPointsInside(Point mPoint, int distance, ArrayList<WeightPoint> weightPointList) throws Exception{
		Point2D midPoint = new Point2D(mPoint.x,mPoint.y);
		ArrayList<WeightPoint> pointsInside=new ArrayList<WeightPoint>();
		//binarysearch
		int lowerBoundValue=Math.max(0,midPoint.getAsInt().x-distance);
		int upperBoundValue=midPoint.getAsInt().x+distance;
		int[] bounds=startEndBinarySearchWithWeightPoints(weightPointList, lowerBoundValue, upperBoundValue);
		if(bounds != null && bounds[0] >=0 && bounds[1] <weightPointList.size() && bounds[0] <= bounds[1]){
			for (int i = bounds[0]; i <= bounds[1]; i++) {
				WeightPoint wPoint=weightPointList.get(i);
				if(midPoint.distance(wPoint.getPoint2D()) <= distance*1.01){	// 1% bigger distance to be sure of collecting the midPoint to the result
					pointsInside.add(wPoint);
				}
			}
		}
		return pointsInside;
	}

	/**
	 * Returns the progressed coordinates.
	 *
	 * @return the progressed coordinates
	 */
	public int getProgressedCoordinates() {
		return progressedCoordinates;
	}

	/**
	 * Returns the weight point with biggest distance.
	 *
	 * @param maxDistanceList the max distance list
	 * @return the weight point with biggest distance
	 */
	private WeightPoint getWeightPointWithBiggestDistance(MaxDistancePoint[] maxDistanceList){
		try {

			MaxDistancePoint maxDistancePoint=null;
			for (int i = 0; i < maxDistanceList.length; i++) {
				if(maxDistanceList[i] != null && (maxDistancePoint == null ||
						(maxDistancePoint != null && maxDistanceList[i].getDistance() > maxDistancePoint.getDistance()))){
					maxDistancePoint=maxDistanceList[i];
				}
			}
			return maxDistancePoint;
		} catch (Exception e) {
			LOGGER.severe("Error in getting WeightPoint of biggest distance!");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the weight point with biggest weight at distance.
	 *
	 * @param midPoint the mid point
	 * @param weightPointList the weight point list
	 * @param distance the distance
	 * @return the weight point with biggest weight at distance
	 * @throws Exception the exception
	 */
	private WeightPoint getWeightPointWithBiggestWeightAtDistance(WeightPoint midPoint, ArrayList<WeightPoint> weightPointList, int distance) throws Exception{
		WeightPoint maxWeightPoint=null;
		for (Iterator<WeightPoint> iterator = weightPointList.iterator(); iterator.hasNext();) {
			WeightPoint weightPoint = (WeightPoint) iterator.next();
			if(weightPoint != null && (maxWeightPoint == null || maxWeightPoint != null && weightPoint.getWeight() >= maxWeightPoint.getWeight())){
				if(midPoint.distance(weightPoint) <= distance)
					maxWeightPoint = weightPoint;
			}
		}
		// if not found -> increase distance
		if(maxWeightPoint == null)
			return getWeightPointWithBiggestWeightAtDistance(midPoint, weightPointList, (int)(distance*2));
		return maxWeightPoint;
	}

	/**
	 * Calculates is given searchPoint inside a triangle composed from given points.
	 *
	 * @param midPoint the one corner for triangle
	 * @param searchPoint the point to be searched
	 * @param second the second triangle point
	 * @param third the third triangle point
	 * @return true, if found point inside triangle
	 * @throws Exception the exception
	 */
	private boolean inside(Point midPoint, Point searchPoint, Point second, Point third) throws Exception{
		int[] xList= new int[] {midPoint.x, second.x, third.x};
		int[] yList= new int[] {midPoint.y, second.y, third.y};

		Polygon slicePolygon=new Polygon(xList,yList,3);

		return slicePolygon.contains(searchPoint);
	}

	/**
	 * Checks does the group of point form a circular shape.
	 *
	 * @param maxPoints the points
	 * @return true, if is circular
	 * @throws Exception the exception
	 */
	private boolean isCircular(MaxDistancePoint[] maxPoints) throws Exception{
		double maxDistance = Double.MIN_VALUE; // initial values
		double minDistance = Double.MAX_VALUE; // initial values

		for (int i = 0; i < maxPoints.length; i++) {
			MaxDistancePoint mdp= maxPoints[i];
			if(mdp == null)
				return false;
			if(mdp.getDistance()> maxDistance){
				maxDistance=mdp.getDistance();
			}
			if(mdp.getDistance() < minDistance){
				minDistance=mdp.getDistance();
			}
		}

		if(maxDistance > minDistance*SharedVariables.GLOBAL_CIRCULARITY) // if difference bigger than xx % -> not circular
			return false;
		return true;
	}

	/**
	 * Checks if is the shape composed from WeightPoints circular.
	 *
	 * @param midPoint the mid point
	 * @param weightPointList the weight point list
	 * @return true, if is circular
	 * @throws Exception the exception
	 */
	private boolean isCircular(Point midPoint, ArrayList<WeightPoint> weightPointList) throws Exception{
		MaxDistancePoint[] maxDistanceValues=getMaxDistanceRoundValues(midPoint, this.current_max_cell_size,weightPointList);
		return isCircular(maxDistanceValues);
	}

	/**
	 * Checks if is continue counting.
	 *
	 * @return true, if is continue counting
	 */
	public boolean isContinueCounting() {
		return continueCounting;
	}

	/**
	 * Divides the circle given by midPoint and circle size to 8 slices and calculates in which slice the searched point is located.
	 *
	 * @param midPoint the mid point
	 * @param searchPoint the search point
	 * @param circleSize the circle size
	 * @return the int 
	 */
	private int isInsideWhichSlice(Point midPoint, Point searchPoint, int circleSize){
		try {
			int circlePointX=Integer.MAX_VALUE;
			int circlePointY= Integer.MAX_VALUE;
			int vPointX=midPoint.x;
			int vPointY=Integer.MAX_VALUE;
			int hPointX=Integer.MAX_VALUE;
			int hPointY=midPoint.y;
			int quarter=0;

			// go through locations separated to four slices
			if(searchPoint.x >= midPoint.x){ // on the right from midpoint
				circlePointX= (int) (midPoint.x + Math.sin(45)*circleSize);
				hPointX=midPoint.x +circleSize; // calculate third point for triangle
				quarter=1;
			}
			else{ // on the left from midpoint
				circlePointX= (int) (midPoint.x - Math.sin(45)*circleSize);
				hPointX=midPoint.x - circleSize; // calculate third point for triangle
				quarter=3;
			}

			if(searchPoint.y >= midPoint.y){ // over midpoint y
				circlePointY= (int) (midPoint.y + Math.sin(45)*circleSize);
				vPointY=midPoint.y + circleSize; // calculate third point for triangle
				if(quarter!=1)
					quarter=4;
			}
			else{ // below midpoint y
				circlePointY= (int) (midPoint.y - Math.sin(45)*circleSize);
				vPointY=midPoint.y - circleSize; // calculate third point for triangle
				if(quarter ==1)
					quarter=2;

			}

			// go through the locations separated to 8 slices
			Point circlePoint=new Point(circlePointX,circlePointY);

			switch (quarter) {
			case 1:
				// first quarter -> check when split the quarter to two triangles:
				Point thirdPoint =new Point(vPointX,vPointY); // the middle point in quarter slice
				// check is inside the first triangle
				if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
					return 1;
				}
				else{ // check is inside the second triangle
					thirdPoint= new Point(hPointX,hPointY);
					if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
						return 2;
					}
					else{
						return -1; // not found
					}
				}

			case 2:
				// second quarter -> check when split the quarter to two triangles:
				thirdPoint =new Point(hPointX,hPointY); // the middle point in quarter slice
				// check is inside the first triangle
				if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
					return 3;
				}
				else{ // check is inside the second triangle
					thirdPoint= new Point(vPointX,vPointY);
					if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
						return 4;
					}
					else{
						return -1; // not found
					}
				}

			case 3:
				// third quarter -> check when split the quarter to two triangles:
				thirdPoint =new Point(vPointX,vPointY); // the middle point in quarter slice
				// check is inside the first triangle
				if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
					return 5;
				}
				else{ // check is inside the second triangle
					thirdPoint= new Point(hPointX,hPointY);
					if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
						return 6;
					}
					else{
						return -1; // not found
					}
				}
			case 4:
				// fourth quarter -> check when split the quarter to two triangles:
				thirdPoint =new Point(hPointX,hPointY); // the middle point in quarter slice
				// check is inside the first triangle
				if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
					return 7;
				}
				else{ // check is inside the second triangle
					thirdPoint= new Point(vPointX,vPointY);
					if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
						return 8;
					}
					else{
						return -1; // not found
					}
				}
			}

			return -1; // not found
		} catch (Exception e) {
			LOGGER.severe("Error in determining slices of given cell!");
			e.printStackTrace();
			return -1;
		}


	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try{
			setContinueCounting(true);
			createPointGroups();
		}
		catch(Exception e){
			LOGGER.severe("Error in start calculating coordinates!");
		}

	}

	/**
	 * Sets the continue counting.
	 *
	 * @param continueCounting the new continue counting
	 */
	public void setContinueCounting(boolean continueCounting) {
		this.continueCounting = continueCounting;
	}

	/**
	 * Sets the progressed coordinates.
	 *
	 * @param progressedCoordinates the new progressed coordinates
	 */
	public void setProgressedCoordinates(int progressedCoordinates) {
		this.progressedCoordinates = progressedCoordinates;
	}

	/**
	 * Uses binary search to find starting and ending indexes of pointList which points are inside given lowerBoundValue and upperBoundValue.
	 * Uses only horizontal values in search.
	 *
	 * @param pointList the point list
	 * @param lowerBoundValue the lower bound value
	 * @param upperBoundValue the upper bound value
	 * @return the int[] list of found
	 */
	private int[] startEndBinarySearch(ArrayList<Point> pointList, int lowerBoundValue, int upperBoundValue){
		try {
			int imin=0;
			int imax=pointList.size()-1;
			int imid=(int)(imax-imin)/2;
			int[] indexes=new int[] {-1,-1};

			lowerLoop:
				while(imax>=imin){
					imid=(int)imin+(imax-imin)/2; // get midpoint
					if(pointList.get(imid).x == lowerBoundValue){ // found lower bound, but has the one index smaller value smaller or same
						if(imid == 0 || pointList.get(imid-1).x < lowerBoundValue){
							// found the lowerBound
							indexes[0]=imid;
							break lowerLoop;
						}
						else{ // there are same values in smaller indexes of list
							imax=imid-1;
						}
					}
					else{
						if(pointList.get(imid).x < lowerBoundValue){
							if(imid < pointList.size()-1){
								if(pointList.get(imid+1).x >= lowerBoundValue){
									// found the lowerbound
									indexes[0]=imid+1;
									break lowerLoop;
								}
								else{
									imin=imid+1;
								}
							}
							else{
								return null; // list not containing given values
							}
						}
						else{

							if(imid > 0){
								if(pointList.get(imid-1).x < lowerBoundValue){
									indexes[0]=imid;
									break lowerLoop;
								}
								else{
									imax=imid-1;
								}
							}
							else{
								// in first index return it->
								indexes[0]=imid;
								break lowerLoop;
							}
						}}
				} // while loop for finding lowerBound

			if(indexes[0] <0 || indexes[0] > pointList.size()-1)
				return null;

			// upperBound search
			imin=indexes[0]+1; // one bigger than found index for lowerbound
			imax=pointList.size()-1; // end of list
			upperLoop:
				while(imax>=imin){
					imid=(int)imin+(imax-imin)/2; // get midpoint

					if(imid< pointList.size()-1){ // is imid smaller or than list size
						if(pointList.get(imid).x == upperBoundValue){

							if(imid == pointList.size()-1 || pointList.get(imid+1).x > upperBoundValue){
								indexes[1]=imid;
								break upperLoop;
							}
							else{
								imin=imid+1;
							}
						}
						else { 
							if(pointList.get(imid).x < upperBoundValue){ // value smaller than upperboundvalue

								if(pointList.get(imid+1).x > upperBoundValue){
									indexes[1]=imid;
									break upperLoop;
								}
								else{
									imin=imid+1;
								}


								if(imid > pointList.size()-1){ // index overflow, should never happen!
									if(indexes[0] < pointList.size()-1){ // lower index is not in last index
										// in last index
										indexes[1]=pointList.size()-1; // set the last index
										break upperLoop; 
									}
									else
										return null; // not found because index not bigger than lowerBound index
								} // in last index
								else{
									indexes[1]=imid;
									break upperLoop;
								}


							}else{
								if(imid > indexes[0]){ // index bigger than lower bound
									if(pointList.get(imid-1).x <= upperBoundValue){
										indexes[1]=imid-1;
										break upperLoop;
									}
									else{
										imax=imid-1;
									}
								}
								else{
									return null; // not found because index not bigger than lowerBound index
								}
							}

						}
					}
					else{ // index went too big -> should never happen
						indexes[1]=pointList.size()-1;
						break upperLoop;
					}
				}


			return indexes;
		} catch (Exception e) {
			LOGGER.severe("Error in binary search for searching positions of pixels!");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Uses binary search to find starting and ending indexes of pointList which points are inside given lowerBoundValue and upperBoundValue.
	 * Uses only horizontal values in search.
	 *
	 * @param pointList the point list
	 * @param lowerBoundValue the lower bound value
	 * @param upperBoundValue the upper bound value
	 * @return the int[]
	 */
	private int[] startEndBinarySearchWithWeightPoints(ArrayList<WeightPoint> pointList, int lowerBoundValue, int upperBoundValue){
		try {
			ArrayList<Point> basicPointList= new ArrayList<Point>();
			if(pointList != null){
				for (Iterator<WeightPoint> iterator = pointList.iterator(); iterator.hasNext();) {
					WeightPoint weightPoint = (WeightPoint) iterator.next();
					basicPointList.add((Point)weightPoint);

				}
				return startEndBinarySearch(basicPointList, lowerBoundValue, upperBoundValue);
			}

			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in starting to find weightpoints by binary search!");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Checks if is should force ga pbigger.
	 *
	 * @return true, if is should force ga pbigger
	 */
	public boolean isShouldForceGAPbigger() {
		return shouldForceGAPbigger;
	}

	/**
	 * Sets the should force ga pbigger.
	 *
	 * @param shouldForceGAPbigger the new should force gap bigger
	 */
	public void setShouldForceGAPbigger(boolean shouldForceGAPbigger) {
		this.shouldForceGAPbigger = shouldForceGAPbigger;
	}
}

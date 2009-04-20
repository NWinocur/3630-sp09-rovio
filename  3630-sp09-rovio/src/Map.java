public class Map {
	
	private MapStop[] stops;
	private int numStops;
	/*private int[] chance;*/
	
	public Map() {
		this.stops = new MapStop[30];
		this.numStops = 0;
		/*this.chance = new int[30];
		for (int i = 0; i < 30; i++) {
			this.chance[i] = 0;
		}*/
	}
	
	/** picks the most likely spot on the map where the robot is located
	* @param perceived the perceived MapStop (created during auto mode)
	* @return a known MapStop(from the map) that is most similar
	* algorithm: read inline comments
	*/
	public MapStop pickMostSimilarTo(MapStop perceived) {
		// make an array to store the probabilities of stops
		int[] probs = new int[this.numStops];
		// iterate over the stops in the map
		for (int i = 0; i < this.numStops; i++) {
			// set the prob of this stop to 0
			probs[i] = 0;
			// if key target colors match
			if (this.stops[i].getKeyTargetColor() == perceived.getKeyTargetColor()) {
				// start prob really big
				probs[i] += 100000;
				// get perceived views (from auto mode)
				MapView[] perceivedViews = perceived.getViews();
				// get known views (from map)
				MapView[] knownViews = this.stops[i].getViews();
				// iterate over views
				for (int j = 0; j < 3; j++) {
					// subtract difference between corresponding views from prob
					probs[i] -= differenceBetweenViews(perceivedViews[j], knownViews[j]);
				}
			}
		}
		// return stop with max prob
		int maxIndex = 0;
		for (int s = 0; s < this.numStops; s++) {
			if (probs[s] > probs[maxIndex]) {
				maxIndex = s;
			}
		}
		// print out the stop that was picked
		Waypoint spot = this.stops[maxIndex].getLocation();
		System.out.println("the perceived MapStop (TouristTrap) is most likely at " +
			String.format("(%f, %f).\n", spot.getX(), spot.getY()));
		// return the stop that was picked
		return this.stops[maxIndex];
	}
	
	
	/** calculate the difference between any two map views
	* difference is calculated as a sum of errors */
	public int differenceBetweenViews(MapView a, MapView b) {
		// default difference at 0
		int diff = 0;
		// add difference for left
		Histogram ah = a.getHistogram(0);
		Histogram bh = b.getHistogram(0);
		for (int color = 0; color < 5; color++) {
			diff += Math.abs(ah.getFreq(color) - bh.getFreq(color));
		}
		// add difference for middle
		ah = a.getHistogram(1);
		bh = b.getHistogram(1);
		for (int color = 0; color < 5; color++) {
			diff += Math.abs(ah.getFreq(color) - bh.getFreq(color));
		}
		// add difference for right
		ah = a.getHistogram(2);
		bh = b.getHistogram(2);
		for (int color = 0; color < 5; color++) {
			diff += Math.abs(ah.getFreq(color) - bh.getFreq(color));
		}
		// return total difference
		return diff;
	}
	
	/** creates a new map stop
	* @param location the location of this stop, where theta is while facing
	* the key target
	* @param keyTargetColor the color of the main landmark that this stop is
	* in front of
	* @return the newly created stop, in case more changes are needed */
	public MapStop createStop(Waypoint location, int keyTargetColor) {
		MapStop stop = new MapStop(location, keyTargetColor);
		this.addStop(stop);
		return stop;
	}
	
	/** adds an already created stop to the map */
	public void addStop(MapStop stop) {
		// regrow array if needed
		if (this.numStops == this.stops.length) {
			MapStop[] newStops = new MapStop[this.stops.length * 2];
			for (int i = 0; i < this.numStops; i++) {
				newStops[i] = this.stops[i];
			}
			this.stops = newStops;
		}
		// add new stop
		this.stops[this.numStops] = stop;
		this.numStops++;
	}
	
	/** print out the map (in raw data form as text) */
	public void printMap() {
		System.out.println(this.toString());
	}
	
	public String toString() {
		String s = "map: < ";
		for (int i = 0; i < numStops; i++) {
			s = s + this.stops[i].toString();
		}
		s = s + " >";
		return s;
	}
	
}

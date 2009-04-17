public class Map {
	
	private MapStop[] stops;
	private int numStops;
	
	public Map() {
		this.stops = new MapStop[20];
		this.numStops = 0;
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

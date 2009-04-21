/** represents one view at a stop in the map */
public class MapView {
	
	/* constants used to identify zones */
	public static final int LEFT = 0;
	public static final int MIDDLE = 1;
	public static final int RIGHT = 2;
	
	private Histogram[] zones;
	private int angle;
	
	/** constructor (initializes histograms)
	* @param angle the angle of the view, measured in degrees where 0 degrees is
	* facing the key target of the MapStop that this views corresponds to, with
	* positive counter-clockwise, and angles between 0 and 359 degrees */
	public MapView(int angle) {
		this.angle = angle;
		this.zones = new Histogram[3];
		// initialize histograms
		for (int i = 0; i < 3; i++) {
			this.zones[i] = new Histogram();
		}
	}
	
	public void setZone(int zone, Histogram h) {
		this.zones[zone] = h;
	}
	
	/** gets the angle that this view is facing, read constructor comments
	* for details */
	public int getAngle() {
		return this.angle;
	}
	
	/** gets a histogram (left, middle, or right zone of the image,
	* use static constants in this class to identify zones */
	public Histogram getHistogram(int zone) {
		return this.zones[zone];
	}
	
	public String rawString() {
		String s = String.format("%d ", this.angle);
		s = s + this.zones[0].rawString() + " ";
		s = s + this.zones[1].rawString() + " ";
		s = s + this.zones[2].rawString() + "";
		return s;
	}
	
	public String toString() {
		String s = String.format("\tmap view at angle %d: [ ", this.angle);
		s = s + "left: " + this.zones[0].toString() + ", ";
		s = s + "middle: " + this.zones[1].toString() + ", ";
		s = s + "right: " + this.zones[2].toString() + " ]\n";
		return s;
	}
	
}

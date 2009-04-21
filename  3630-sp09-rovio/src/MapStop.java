public class MapStop {
	
	/* constant stating number of views at each MapStop */
	public static final int NUM_VIEWS = 3;
	
	private Waypoint location;
	private int keyTargetColor;
	private MapView[] views;
	private int viewCount;
	
	public MapStop(Waypoint location, int keyTargetColor) {
		this.viewCount = 0;
		this.location = location;
		this.keyTargetColor = keyTargetColor;
		this.views = new MapView[NUM_VIEWS];
	}
	
	public int getKeyTargetColor() {
		return this.keyTargetColor;
	}
	
	public MapView[] getViews() {
		/*MapView[] a = new MapView[3];
		a[0] = this.getViewAt(90+45);
		a[1] = this.getViewAt(180);
		a[2] = this.getViewAt(180+45);
		return a;*/
		return this.views;
	}
	
	public Waypoint getLocation() {
		return this.location;
	}
	
	/** adds a MapView to this MapStop, be sure to add exactly NUM_VIEWS views */
	public void addView(MapView view) {
		if (this.viewCount < NUM_VIEWS) {
			this.views[this.viewCount] = view;
			viewCount++;
		}
	}
	
	/** get the MapView of the specified angle, returns null if there is no
	* MapView with this angle, angle must be exact */
	public MapView getViewAt(int angle) {
		for (int i = 0; i < viewCount; i++) {
			if (this.views[i].getAngle() == angle) {
				return this.views[i];
			}
		}
		return null;
	}
	
	public String rawString() {
		String s = String.format("%f,%f,%f,%d,", location.getX(), location.getY(), location.getTheta(), keyTargetColor);
		for (int i = 0; i < viewCount; i++) {
			s = s + this.views[i].rawString() + ",";
		}
		s = s + " \n";
		return s;
	}
	
	public String toString() {
		String s = "map stop at location ";
		s = s
				+ String.format("( %f, %f, %f )", location.getX(), location
						.getY(), location.getTheta());
		s = s + String.format(" with color %d and views: {\n", this.keyTargetColor);
		for (int i = 0; i < viewCount; i++) {
			s = s + this.views[i].toString() + " ";
		}
		s = s + " }";
		return s;
	}
	
}

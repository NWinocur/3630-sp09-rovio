import java.awt.image.BufferedImage;

public class Demo3Manual extends Planner {
	
	private ColorSpace cspace;
	private Map map;
	private ImageProc iproc;
	
	public Demo3Manual(Robot robot) {
		super(robot);
		super.currentPosition = new Waypoint(0, 0, 0);
		cspace = new ColorSpace();
		// hard code numbers in ColorSpace class, instead of auto-calibration
		cspace.setAllDefaults();
		this.map = new Map();
		this.iproc = new ImageProc();
	}
	
	public void makeMove() {
		// open GUI
		new ManualGUI(this);
		// wait for commands from GUI
		// halt
		while (true) {}
	}
	
	public void driveForward() {
		driveForward(0.125);
	}

	public void driveForward(double metersToDriveForward) {
		double d = metersToDriveForward;
		double angle = super.currentPosition.getTheta() * (Math.PI / ((double) 180));
		double dx = d * Math.cos(angle);
		double dy = d * Math.sin(angle);
		Waypoint tempGoal = new Waypoint(super.currentPosition.getX() + dx,
			super.currentPosition.getY() + dy, super.currentPosition.getTheta());
		super.driveTo(tempGoal);
	}
	
	public void turnLeft() {
		double angle = super.currentPosition.getTheta() + 90;
		super.driveTo(new Waypoint(super.currentPosition.getX(),
			super.currentPosition.getY(), angle));
	}
	
	public void turnRight() {
		double angle = super.currentPosition.getTheta() - 90;
		super.driveTo(new Waypoint(super.currentPosition.getX(),
			super.currentPosition.getY(), angle));
	}
	
	public void autoMode() {
		System.out.println(this.map.toString());
		// initialize automatic mode
		Demo3Automatic d = new Demo3Automatic(super.robot, this.cspace, this.map);
		//d.makeMove();
		System.out.println("automatic mode finished, now halting");
	}
	
	/** spins, taking pictures and building the map */
	public void mapStop() {
		// make a copy of the waypoint
		double keyAngle = currentPosition.getTheta();
		Waypoint location = new Waypoint(currentPosition.getX(),
			currentPosition.getY(), keyAngle);
		// get the key target color
		///////////// FIX THIS /////////////////////////
		/////// the ImageProc object is already initialized in the constructor and
		/////// is in a field in this class
		/////// set it to red for now:
		int keyTargetColor = 0;
		// make the map stop
		MapStop stop = new MapStop(location, keyTargetColor);
		// spin while making map views
		double currentAngle = keyAngle + 90 + 45;
		if (currentAngle >= 360) {
			currentAngle -= 360;
		}
		super.driveTo(new Waypoint(location.getX(), location.getY(), currentAngle));
		this.learnView(stop, currentAngle);
		currentAngle += 45;
		if (currentAngle >= 360) {
			currentAngle -= 360;
		}
		super.driveTo(new Waypoint(location.getX(), location.getY(), currentAngle));
		this.learnView(stop, currentAngle);
		currentAngle += 45;
		if (currentAngle >= 360) {
			currentAngle -= 360;
		}
		super.driveTo(new Waypoint(location.getX(), location.getY(), currentAngle));
		this.learnView(stop, currentAngle);
		currentAngle += 90 + 45;
		if (currentAngle >= 360) {
			currentAngle -= 360;
		}
		super.driveTo(new Waypoint(location.getX(), location.getY(), currentAngle));
	}
	
	public void learnView(MapStop stop, double angle) {
		MapView view = new MapView((int) angle);
		stop.addView(view);
		// adjust histograms from image
		////////// FIX THIS ///////////////
		////// get an image, segmented
		////// get each histogram and adjust numbers
		Histogram hLeft = view.getHistogram(MapView.LEFT);
		Histogram hMiddle = view.getHistogram(MapView.MIDDLE);
		Histogram hRight = view.getHistogram(MapView.RIGHT);
		// example: hLeft.incrementFreq(0); where 0 is red
	}
	
	/** adds color data used for color calibration
	* @param color number of human selected color of this marker [r, y, g, b, v]
	* @param i the image entirely filled with the color to sample */
	public void learnColor(int color, BufferedImage i) {
		cspace.sampleImage(color, i);
	}
	
	/** call this after all images have been learned, using learnColor */
	public void finishCalibrating() {
		cspace.calibrate();
	}
	
}

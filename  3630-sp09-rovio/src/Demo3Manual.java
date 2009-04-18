import java.awt.image.BufferedImage;

public class Demo3Manual extends Planner {
	
	private ColorSpace cspace;
	private Map map;
	
	public Demo3Manual(Robot robot) {
		super(robot);
		super.currentPosition = new Waypoint(0, 0, 0);
		cspace = new ColorSpace();
		// hard code numbers in ColorSpace class, instead of auto-calibration
		cspace.setAllDefaults();
		this.map = new Map();
	}
	
	public void makeMove() {
		// open GUI
		new ManualGUI(this);
		// wait for commands from GUI
		// run automatic mode
		Demo3Automatic d = new Demo3Automatic(super.robot, this.cspace, this.map);
		//d.makeMove();
		// halt
		while (true) {}
	}
	
	public void driveForward() {
		double d = 0.125;
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
	
	/** adds a marker to the map
	* @param color the human selected color of this marker [r, y, g, b, v] */
	public void learnMarker(int color, double x, double y) {
		
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

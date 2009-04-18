import java.awt.image.BufferedImage;

public class Demo3Automatic extends Planner {
	
	private ColorSpace cspace;
	private Map map;
	
	public Demo3Automatic(Robot robot, ColorSpace cspace, Map map) {
		super(robot);
		this.cspace = cspace;
		this.map = map;
	}
	
	public void makeMove() {
		Waypoint currentLocation = null;
		Waypoint goal = new Waypoint(0, 0, 0);
		// localize
		currentLocation = localize();
		// go to goal
		driveToGoal(currentLocation, goal);
		// halt
		while (true) {}
	}
	
	public Waypoint localize() {
		return new Waypoint(0, 0, 0);
	}
	
	public void driveToGoal(Waypoint current, Waypoint goal) {
		
	}
	
}

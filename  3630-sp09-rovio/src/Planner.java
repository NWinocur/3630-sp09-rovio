/**
* This should be implemented by any class that can control a robot to
* follow a path that leads to a goal.
*/
public abstract class Planner {
	
	/** the robot to control */
	protected Robot robot;
	/** current x, y, and theta of the robot */
	protected Waypoint currentPosition;
	
	public Planner(Robot robot) {
		this.robot = robot;
	}
	
	/** control the robot to move to the next point in the plan */
	public abstract void makeMove();
	
	/** drives from current point to point */
	protected abstract void driveTo(Waypoint point);
	
}

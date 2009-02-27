/**
* This should be implemented by any class that can control a robot to
* follow a path that leads to a goal.
*/
public abstract class Planner {
	
	/** the robot to control */
	protected Robot robot;
	/** current x, y, and theta of the robot */
	protected Waypoint currentPosition;
	private final int shortSleepAmountInMillis = 5000;
	private final int longSleepAmountInMillis = 7500;
	
	
	public Planner(Robot robot) {
		this.robot = robot;
	}
	

	public abstract void makeMove();
	
	/**
	 * drives from current position to point
	 * 
	 * @throws InterruptedException
	 */
	protected void driveTo(Waypoint point) {
		/*
		This method assumes that the robot class has the following methods:
			turn(double amountToTurn)
			drive(double distance)
		The second assumes a constant speed set inside the robot class.
		*/
		
		// check if already at point: if so, do nothing
		if (!((point.getX() == currentPosition.getX()) &&
			(point.getY() == currentPosition.getY()) &&
			(point.getTheta() == currentPosition.getTheta())))
		{
			// turn towards point
			double thetaPrime = this.currentPosition.getThetaPrime(point);
			double angleToTurn = this.currentPosition.angleBetween(thetaPrime);
			this.robot.turn(angleToTurn);
			currentPosition.setTheta(thetaPrime); // did turn towards next
													// waypoint, update theta
			RovioAPI.napTime(shortSleepAmountInMillis);
			// drive to point
			double hypot = this.currentPosition.distance(point);
			this.robot.drive(hypot);
			currentPosition.setX(point.getX()); // done driving, update xy
												// coords
			currentPosition.setY(point.getY());
			RovioAPI.napTime(shortSleepAmountInMillis);
			// turn towards final theta
			angleToTurn = this.currentPosition.angleBetween(point.getTheta());
			this.robot.turn(angleToTurn);
			currentPosition.setTheta(point.getTheta());// did turn towards goal
														// theta, update theta
			RovioAPI.napTime(longSleepAmountInMillis);
		}
		System.out.println("Planner.java just finished doing a DriveTo "
				+ point.toString() + ", printing MCU report");
		System.out.println(this.robot.getMCUReport().toString());
	}
	
}

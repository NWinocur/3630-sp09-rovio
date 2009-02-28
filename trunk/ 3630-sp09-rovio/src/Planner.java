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
	protected void driveTo(Waypoint goalPoint) {
		/*
		This method assumes that the robot class has the following methods:
			turn(double amountToTurn)
			drive(double distance)
		The second assumes a constant speed set inside the robot class.
		*/
		int amountTurned = 0;
		// check if already at point: if so, do nothing
		if (!((goalPoint.getX() == currentPosition.getX()) &&
			(goalPoint.getY() == currentPosition.getY()) &&
			(goalPoint.getTheta() == currentPosition.getTheta())))
		{
			// turn towards point
			double thetaPrime = this.currentPosition.getThetaPrime(goalPoint);
			double angleToTurn = this.currentPosition.angleBetween(thetaPrime);
			amountTurned = this.robot.turn(angleToTurn);
			currentPosition.setTheta(currentPosition.getTheta() + amountTurned); // did turn towards next
													// waypoint, update theta
			RovioAPI.napTime(shortSleepAmountInMillis);
			// drive to point
			double hypot = this.currentPosition.distance(goalPoint);
			this.robot.drive(hypot);
			currentPosition.setX(Math.cos((Math.PI / 180)
					* currentPosition.getX())); // done driving, update xy
												// coords
			currentPosition.setY(Math.sin((Math.PI / 180)
					* currentPosition.getY()));
			RovioAPI.napTime(shortSleepAmountInMillis);
			// turn towards final theta
			angleToTurn = this.currentPosition.angleBetween(goalPoint.getTheta());
			amountTurned = this.robot.turn(angleToTurn);
			currentPosition.setTheta(currentPosition.getTheta() + amountTurned);// did turn towards goal
														// theta, update theta
			RovioAPI.napTime(longSleepAmountInMillis);
			if (Math.abs(currentPosition.distance(goalPoint)) > 1) {
				driveTo(goalPoint);
			}
		}
		System.out.println("Planner.java just finished doing a DriveTo "
				+ goalPoint.toString() + ", printing MCU report");
		System.out.println(this.robot.getMCUReport().toString());
	}
	
}

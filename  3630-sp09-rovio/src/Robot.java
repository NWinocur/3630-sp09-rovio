public class Robot extends RovioAPI {
	
	public static final double SPEED = 1;
	public static final double TURN_SPEED = 1;
	
	public Robot(final RovioConnection connection) {
		super(connection);
	}
	
	public void turn(double amountToTurn) {
		// code to make robot turn
		/* amount to turn is measured in degrees between -180.0 and 180.0 where
		negative means to turn left and positive means to turn right */
		
	}
	
	/** tells the robot to drive forward a given distance, measured in meters
	at a constant speed given by SPEED */
	public void drive(double distance) {
		// code to drive
		
	}
	
}

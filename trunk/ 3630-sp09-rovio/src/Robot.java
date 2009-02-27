public class Robot extends RovioAPI {

	public static final double SPEED = 1;
	public static final double TURN_SPEED = 5;
	public static final double DISTANCE_SCALE = 20;
	public static final int ANGLEPERMESSAGE = 03;// this int * 15deg =
													// angle/turn
	public static final int MILLISBETWEENTURNMESSAGES = 350 * ANGLEPERMESSAGE;
	
	public Robot(final RovioConnection connection) {
		super(connection);
	}
	
	public void turn(double amountToTurn) {
		// code to make robot turn
		/*
		 * URLs come out in a form like
		 * rev.cgi?Cmd=nav&action=18&drive=##&speed=##&angle=## where drive is
		 * 17 if left, 18 if right and angle ## is int from 00 to 15 inclusive
		 */
		/*
		 * amount to turn is measured in degrees between -180.0 and 180.0 where
		 * pos means to turn CCW and neg means to turn CW
		 */
		int messages = (int) Math
				.round(amountToTurn / (15.0 * ANGLEPERMESSAGE));
		System.out
				.print("Robot.java has been instructed to turn by sending "
				+ messages + " messages");
		if (0 > amountToTurn) {
			System.out.print(", a right turn");
			for (int i = 0; i < Math.abs(messages); i++) {
				super.manualDrive(
						RovioConstants.DriveType.ROTATE_RIGHT_BY_20_DEGREES,
						(int) TURN_SPEED, ANGLEPERMESSAGE);
				RovioAPI.napTime(MILLISBETWEENTURNMESSAGES);
				
			}
		}
		else if (0 < amountToTurn) {
			System.out.print(", a left turn");
			for (int i = 0; i < Math.abs(messages); i++) {
				super.manualDrive(
						RovioConstants.DriveType.ROTATE_LEFT_BY_20_DEGREES,
						(int) TURN_SPEED, ANGLEPERMESSAGE);
				RovioAPI.napTime(MILLISBETWEENTURNMESSAGES);
			}
		}
		System.out.println("...messages sent.");
	}
	
	
	
	/** tells the robot to drive forward a given distance, measured in meters
	at a constant speed given by SPEED */
	public void drive(double distance) {
		double messages = distance * DISTANCE_SCALE;
		/*
		 * System.out
		 * .print("Robot.java has been instructed to drive forward by sending "
		 * + distance + "*" + DISTANCE_SCALE + " messages");
		 */
		for (int i = 0; i < messages; i++) {
			super.manualDrive(RovioConstants.DriveType.FORWARD, (int)SPEED);
		}
		// System.out.println("...messages sent.");
	}
	
}

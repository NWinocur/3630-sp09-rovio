public class Robot extends RovioAPI {

	public static final double SPEED = 1;
	public static final double TURN_SPEED = 5;
	public static final double DISTANCE_SCALE = 30.25;
	public static int ANGLEPERMESSAGE = 01;// this int*15deg==angle/turn
	public static int MILLISBETWEENTURNMESSAGES = 350 * ANGLEPERMESSAGE;
	private RovioConstants.DriveType CURRENTHEADPOS; 
	
	/**
	 * @return the cURRENTHEADPOS
	 */
	public RovioConstants.DriveType getCURRENTHEADPOS() {
		return this.CURRENTHEADPOS;
	}

	public Robot(final RovioConnection connection) {
		super(connection);
		this.duckAndCover();
	}
	
	public int turn(double amountToTurn) {
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
		int messages90 = 0, messages45 = 0, messages30 = 0, messages15 = 0;
		
		RovioConstants.DriveType leftOrRight = RovioConstants.DriveType.ROTATE_RIGHT_BY_20_DEGREES;
		if (0 < amountToTurn) {
			leftOrRight = RovioConstants.DriveType.ROTATE_LEFT_BY_20_DEGREES;	
		}
		
		System.out.print("Robot.turn(): amount to turn: " + amountToTurn);
		amountToTurn = Math.abs(amountToTurn);
		long roundedAmountToTurn = Math.round(amountToTurn);
		if (roundedAmountToTurn >= 90) {
			messages90 = (int) (roundedAmountToTurn / 90);
			roundedAmountToTurn = roundedAmountToTurn % 90;
		}		
		if (roundedAmountToTurn >= 45) {
			messages45 = (int) (roundedAmountToTurn / 45);
			roundedAmountToTurn = roundedAmountToTurn % 45;
		}
		if (roundedAmountToTurn >= 30) {
			messages30 = (int) (roundedAmountToTurn / 30);
			roundedAmountToTurn = roundedAmountToTurn % 30;
		}
		if (roundedAmountToTurn >= 15) {
			messages15 = (int) (roundedAmountToTurn / 15);
			roundedAmountToTurn = roundedAmountToTurn % 15;
		}
		/*for (int i = 0; i < Math.abs(messages); i++) {
			super.manualDrive(
					RovioConstants.DriveType.ROTATE_RIGHT_BY_20_DEGREES,
					(int) TURN_SPEED, ANGLEPERMESSAGE);
			RovioAPI.napTime(MILLISBETWEENTURNMESSAGES);
		}*/
		System.out.println("turning (deg:numMessages): 90:" + messages90 + " 45:" +
			messages45 + " 30:" + messages30 + " 15:"
				+ messages15);

		for (int i = 0; i < messages90; i++) {
			super.manualDrive(
					leftOrRight,
					(int) TURN_SPEED, 7);
			RovioAPI.napTime(MILLISBETWEENTURNMESSAGES * 7);
		}
		for (int i = 0; i < messages45; i++) {
			super.manualDrive(
					leftOrRight,
					(int) TURN_SPEED, 3);
			RovioAPI.napTime(MILLISBETWEENTURNMESSAGES * 3);
		}
		for (int i = 0; i < messages30; i++) {
			super.manualDrive(
					leftOrRight,
					(int) TURN_SPEED, 2);
			RovioAPI.napTime(MILLISBETWEENTURNMESSAGES * 2);
		}
		for (int i = 0; i < messages15; i++) {
			super.manualDrive(
					leftOrRight,
					(int) TURN_SPEED, 1);
			RovioAPI.napTime(MILLISBETWEENTURNMESSAGES);
		}
		int amountTurned = messages90 * 90 + messages45 * 45 + messages30 * 30
				+ messages15 * 15;
		if (leftOrRight == RovioConstants.DriveType.ROTATE_RIGHT_BY_20_DEGREES) {
			amountTurned = amountTurned * -1;
		}
		return amountTurned;
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
	
	public RovioConstants.DriveType duckAndCover() {
		super.manualDrive(RovioConstants.DriveType.HEAD_DOWN, (int) SPEED);
		RovioAPI.napTime(1000);
		this.CURRENTHEADPOS = RovioConstants.DriveType.HEAD_DOWN;
		RovioAPI.napTime(MILLISBETWEENTURNMESSAGES);
		return CURRENTHEADPOS;
	}
	
	public RovioConstants.DriveType lookMid()
	{
		super.manualDrive(RovioConstants.DriveType.HEAD_MIDDLE, (int) SPEED);
		RovioAPI.napTime(1250);
		return this.CURRENTHEADPOS = RovioConstants.DriveType.HEAD_MIDDLE;
		
	}
	
}

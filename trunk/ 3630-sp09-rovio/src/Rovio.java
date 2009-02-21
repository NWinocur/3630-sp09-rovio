

import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.URLEncoder;

/**
 * @author John Crawford
 *
 */
public class Rovio extends Authenticator implements RovioConstants, RovioAPIResponses {

	private final long sleepAmountInMillis = 500;
	private final long longSleepAmountInMillis = 2000;
	
	private final RovioAPI api;
	private final EncoderTracker tracker;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Rovio myRovio = null;
		
		// Create the Rovio
		try {
			myRovio = new Rovio("192.168.10.18", "admin", "cs3630");
//			myRovio.doCommandAndPrint(Command.REBOOT);
			myRovio.api.manualDrive(DriveType.HEAD_DOWN, 0);
//			myRovio.prettyPrintMCU(myRovio.doCommand(Command.GET_MCU_REPORT));
			myRovio.rotationTest();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public Rovio(String ipAddress, String username, String password) {
		api = new RovioAPI(new RovioConnection(ipAddress, username, password));
		
		RovioAPI trackerAPI = new RovioAPI(new RovioConnection(ipAddress, username, password));
		
		tracker = new EncoderTracker(trackerAPI);
	}	

	public void rotationExcercise() throws Exception {
		int numCmds = 10;							// number of commands to send
		api.getMCUReport();	// getting an MCU report clears Rovio encoder counters 
		shortPause();								// pause after sending to allow processing time
		System.out.println("Starting rotation excercise (right), sending " + numCmds + " commands...");
		for(int i = numCmds; i > 0; i--) {
			api.manualDrive(DriveType.ROTATE_RIGHT_BY_SPEED, Command.speed);
			shortPause();
		}
		System.out.println(api.getMCUReport());
		longPause();								// pause for a long time to allow Rovio to 'settle'
		api.getMCUReport();
		System.out.println("Starting rotation excercise (right), sending " + numCmds + " commands...");
		for(int i = numCmds; i > 0; i--) {
			api.manualDrive(DriveType.ROTATE_LEFT_BY_SPEED, Command.speed);
			shortPause();
		}
		longPause();								// pause for a long time to allow Rovio to 'settle'
		api.getMCUReport();
		
	}
	
	private void shortPause() throws InterruptedException {
		Thread.sleep(sleepAmountInMillis);
	}
	
	private void longPause() throws InterruptedException {
		Thread.sleep(longSleepAmountInMillis);
	}
	
	private void rotationTest() throws Exception {
		
		while(true){
			api.manualDrive(DriveType.ROTATE_RIGHT_BY_20_DEGREES, 2);
			tracker.update();
			System.out.println(tracker.getRearEncoder());
			shortPause();
		}
	}
	

	
	public enum Command {
		GET_REPORT("rev.cgi?Cmd=nav&action=1"),
		RESET_NAV_STATE_MACHINE("rev.cgi?Cmd=nav&action=17"),
		DRIVE_STOP("rev.cgi?Cmd=nav&action=18&drive=0&speed=" + Command.speed),
		DRIVE_FORWARD("rev.cgi?Cmd=nav&action=18&drive=1&speed=" + Command.speed),
		DRIVE_BACKWARD("rev.cgi?Cmd=nav&action=18&drive=2&speed=" + Command.speed),
		DRIVE_LEFT("rev.cgi?Cmd=nav&action=18&drive=3&speed=" + Command.speed),
		DRIVE_RIGHT("rev.cgi?Cmd=nav&action=18&drive=4&speed=" + Command.speed),
		DRIVE_ROTATE_LEFT("rev.cgi?Cmd=nav&action=18&drive=5&speed=" + Integer.toString(Command.speed)),
		DRIVE_ROTATE_RIGHT("rev.cgi?Cmd=nav&action=18&drive=6&speed=" + Command.speed),
		DRIVE_DIAG_FORWARD_LEFT("rev.cgi?Cmd=nav&action=18&drive=7&speed=" + Command.speed),
		DRIVE_DIAG_FORWARD_RIGHT("rev.cgi?Cmd=nav&action=18&drive=8&speed=" + Command.speed),
		DRIVE_DIAG_BACKWARD_LEFT("rev.cgi?Cmd=nav&action=18&drive=9&speed=" + Command.speed),
		DRIVE_DIAG_BACKWARD_RIGHT("rev.cgi?Cmd=nav&action=18&drive=10&speed=" + Command.speed),
		DRIVE_HEAD_UP("rev.cgi?Cmd=nav&action=18&drive=11&speed=" + Command.speed),
		DRIVE_HEAD_DOWN("rev.cgi?Cmd=nav&action=18&drive=12&speed=" + Command.speed),
		DRIVE_HEAD_MIDDLE("rev.cgi?Cmd=nav&action=18&drive=13&speed=" + Command.speed),
		DRIVE_ROTATE_LEFT_20_DEGREES("rev.cgi?Cmd=nav&action=18&drive=17&speed=" + Command.speed),
		DRIVE_ROTATE_RIGHT_20_DEGREES("rev.cgi?Cmd=nav&action=18&drive=18&speed=" + Command.speed),
		GET_MCU_REPORT("rev.cgi?Cmd=nav&action=20"),
		GET_TIME("GetTime.cgi"),
		GET_STATUS("GetStatus.cgi"),
		GET_LOG("GetLog.cgi"),
		GET_VER("GetVer.cgi"),
		REBOOT("Reboot.cgi"),
		GET_DATA("GetData.cgi");
		
		private String command;
		public static final int speed = 1;
		
		private Command(String command) {
			this.command = command;
		}
		
		public String toString() {
			return this.command;
		}
		
		public String getURLEncoding() throws UnsupportedEncodingException {
			return URLEncoder.encode(this.command, "UTF-8");
		}
	}

	
	
	public enum Wheel {
		LEFT,
		RIGHT,
		BACK
	}
	
	public enum DriveDirection {
		FORWARD,
		BACKWARD,
		LEFT,
		RIGHT,
		DIAG_FORWARD_LEFT,
		DIAG_FORWARD_RIGHT,
		DIAG_BACKWARD_LEFT,
		DIAG_BACKWARD_RIGHT
	}
	
	public enum RotateDirection {
		LEFT,
		RIGHT
	}
}

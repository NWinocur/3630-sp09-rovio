/**
 * 
 */
package cs3630;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author John Crawford
 *
 */
public class Rovio extends Authenticator {

	private String ipAddress;
	private String username;
	private String password;
	private URL rovioBaseURL;
	
	public Rovio(String ipAddress, String username, String password) throws MalformedURLException {
		this.ipAddress = ipAddress;
		this.username = username;
		this.password = password;
		rovioBaseURL =new URL( "http://" + this.ipAddress + "/");
		
		Authenticator.setDefault(this);
	}
	
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password.toCharArray());
	}
	
	public void printReport() throws IOException {
		
		String response;

		// Command to print a general report is: CommandString.GET_REPORT
		URL commandURL = new URL(this.rovioBaseURL, CommandString.GET_REPORT.toString());
		// Open an input stream using the command URL
		BufferedReader responseReader = new BufferedReader(new InputStreamReader(commandURL.openStream()));
		// Read the response from the input stream
		response = responseReader.readLine();
		// Print Rovio's response
		while(response != null) {
			System.out.println(response);
			response = responseReader.readLine();
		}
		// Done with the stream
		responseReader.close();
	}
	
	public void printMCUReport() throws IOException {
		
		String response;
		
		// Command to print an MCU report is: CommandString.GET_MCU_REPORT
		URL commandURL = new URL(this.rovioBaseURL, CommandString.GET_MCU_REPORT.toString());
		// Open an input stream using the command URL
		BufferedReader responseReader = new BufferedReader(new InputStreamReader(commandURL.openStream()));
		// Read the response from the input stream
		response = responseReader.readLine();
		// Print Rovio's response
		while(response != null) {
			System.out.println(response);
			response = responseReader.readLine();
		}
		// Done with the stream
		responseReader.close();
	}

	public void turnLeft20Degrees() throws IOException {
		
		// Command to turn left 20 degrees is: CommandString.DRIVE_ROTATE_LEFT_20_DEGREES
		doCommand(CommandString.DRIVE_ROTATE_LEFT_20_DEGREES);

	}

	public void turnRight20Degrees() throws IOException {
		
		doCommand(CommandString.DRIVE_ROTATE_RIGHT_20_DEGREES);

	}
	
	public void doCommand(CommandString command) throws IOException {
		String response;
		
		// Build the command
		URL commandURL = new URL(this.rovioBaseURL, command.toString());
		// Open an input stream using the command URL
		BufferedReader responseReader = new BufferedReader(new InputStreamReader(commandURL.openStream()));
		// Read the response from the input stream
		response = responseReader.readLine();
		// Print Rovio's response
		while(response != null) {
			System.out.println(response);
			response = responseReader.readLine();
		}
		// Done with the stream
		responseReader.close();
	}
	
	public byte getByteFromHexString(int offset, String hexString) throws Exception {
		
		// A byte is represented with 2 hex digits, check to see if we have at least that many digits in the string
		if(hexString.length() < 2) {
			throw new Exception("Invalid hex string specified.");
		}
		// Check to see if the offset is valid
		if(offset > (hexString.length() - 2)) {
			throw new Exception("Invalid offset specified.");
		}
		hexString = hexString.substring(offset, offset + 2);
		return Byte.parseByte(hexString, 16);
	}
	
	public short getShortFromHexString(int offset, String hexString) throws Exception {
		
		// A short is represented with 4 hex digits, check to see if we have at least that many digits in the string
		if(hexString.length() < 4) {
			throw new Exception("Invalid hex string specified.");
		}
		// Check to see if the offset is valid
		if(offset > (hexString.length() - 4)) {
			throw new Exception("Invalid offset specified.");
		}
		hexString = hexString.substring(offset, offset + 4);
		return Short.parseShort(hexString, 16);
	}

	public int getWheelTicks(Wheel w, String hexResponse) throws Exception {
		
		short ticks = 0;
		
		if(w == Wheel.LEFT) {
			ticks = getShortFromHexString(3, hexResponse);
		}
		else if(w == Wheel.RIGHT) {
			ticks = getShortFromHexString(6, hexResponse);
		}
		else if(w == Wheel.BACK) {
			ticks = getShortFromHexString(9, hexResponse);
		}
		
		return ticks;
	}
	
	public Direction getWheelDirection(Wheel w, String hexResponse) throws Exception {
		
		Direction d = Direction.LEFT;
		
		return d;
	}
	
	public void exerciseRovio() throws IOException, InterruptedException {
		
		// rotate left 20 degrees
		System.out.println("Rotate Left 20 Degrees...");
		doCommand(CommandString.DRIVE_ROTATE_LEFT_20_DEGREES);
		Thread.sleep(2000);
		// lift head up
		System.out.println("Lift head up...");
		doCommand(CommandString.DRIVE_HEAD_UP);
		Thread.sleep(2000);
		// rotate left 20 degrees
		System.out.println("Rotate Left 20 Degrees...");
		doCommand(CommandString.DRIVE_ROTATE_LEFT_20_DEGREES);
		Thread.sleep(2000);
		// put head down
		System.out.println("Put head down...");
		doCommand(CommandString.DRIVE_HEAD_DOWN);
		Thread.sleep(2000);
		// rotate right 20 degrees
		System.out.println("Rotate Right 20 Degrees...");
		doCommand(CommandString.DRIVE_ROTATE_RIGHT_20_DEGREES);
		Thread.sleep(2000);
		// lift head upmyRovio.printMCUReport();
		doCommand(CommandString.DRIVE_HEAD_UP);
		// rotate right 20 degrees
		System.out.println("Rotate Right 20 Degrees...");
		doCommand(CommandString.DRIVE_ROTATE_RIGHT_20_DEGREES);
		Thread.sleep(2000);
		// put head down
		System.out.println("Put head down...");
		doCommand(CommandString.DRIVE_HEAD_DOWN);
		Thread.sleep(2000);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Rovio myRovio = null;
		
		// Create the Rovio
		try {
			myRovio = new Rovio("192.168.10.18", "admin", "cs3630");
			// Ask Rovio to print a general report
			myRovio.printReport();
			// Ask Rovio to print an MCU report
			myRovio.printMCUReport();
			// Ask Rovio to exercise
			myRovio.exerciseRovio();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public enum ResponseCode {
		SUCCESS,
		FAILURE,
		ROBOT_BUSY,
		FEATURE_NOT_IMPLEMENTED,
		UNKNOWN_CGI_ACTION,
		NO_NS_SIGNAL,
		NO_EMPTY_PATH_AVAILABLE,
		FAILED_TO_READ_PATH,
		PATH_BASEADDRESS_NOT_INITIALIZED,
		PATH_NOT_FOUND,
		PATH_NAME_NOT_SPECIFIED,
		NOT_RECORDING_PATH,
		FLASH_NOT_INITIALIZED,
		FAILED_TO_DELETE_PATH,
		FAILED_TO_READ_FROM_FLASH,
		FAILED_TO_WRITE_TO_FLASH,
		FLASH_NOT_READY,
		NO_MEMORY_AVAILABLE,
		NO_MCU_PORT_AVAILABLE,
		NO_NS_PORT_AVAILABLE,
		NS_PACKET_CHECKSUM_ERROR,
		NS_UART_READ_ERROR,
		PARAMETER_OUTOFRANGE,
		NO_PARAMETER
	}
	
	public enum CommandString {
		GET_REPORT("rev.cgi?Cmd=nav&action=1"),
		RESET_NAV_STATE_MACHINE("rev.cgi?Cmd=nav&action=17"),
		DRIVE_STOP("rev.cgi?Cmd=nav&action=18&drive=0&speed=" + CommandString.speed),
		DRIVE_FORWARD("rev.cgi?Cmd=nav&action=18&drive=1&speed=" + CommandString.speed),
		DRIVE_BACKWARD("rev.cgi?Cmd=nav&action=18&drive=2&speed=" + CommandString.speed),
		DRIVE_LEFT("rev.cgi?Cmd=nav&action=18&drive=3&speed=" + CommandString.speed),
		DRIVE_RIGHT("rev.cgi?Cmd=nav&action=18&drive=4&speed=" + CommandString.speed),
		DRIVE_ROTATE_LEFT("rev.cgi?Cmd=nav&action=18&drive=5&speed=" + CommandString.speed),
		DRIVE_ROTATE_RIGHT("rev.cgi?Cmd=nav&action=18&drive=6&speed=" + CommandString.speed),
		DRIVE_DIAG_FORWARD_LEFT("rev.cgi?Cmd=nav&action=18&drive=7&speed=" + CommandString.speed),
		DRIVE_DIAG_FORWARD_RIGHT("rev.cgi?Cmd=nav&action=18&drive=8&speed=" + CommandString.speed),
		DRIVE_DIAG_BACKWARD_LEFT("rev.cgi?Cmd=nav&action=18&drive=9&speed=" + CommandString.speed),
		DRIVE_DIAG_BACKWARD_RIGHT("rev.cgi?Cmd=nav&action=18&drive=10&speed=" + CommandString.speed),
		DRIVE_HEAD_UP("rev.cgi?Cmd=nav&action=18&drive=11&speed=" + CommandString.speed),
		DRIVE_HEAD_DOWN("rev.cgi?Cmd=nav&action=18&drive=12&speed=" + CommandString.speed),
		DRIVE_HEAD_MIDDLE("rev.cgi?Cmd=nav&action=18&drive=13&speed=" + CommandString.speed),
		DRIVE_ROTATE_LEFT_20_DEGREES("rev.cgi?Cmd=nav&action=18&drive=17&speed=" + CommandString.speed),
		DRIVE_ROTATE_RIGHT_20_DEGREES("rev.cgi?Cmd=nav&action=18&drive=18&speed=" + CommandString.speed),
		GET_MCU_REPORT("rev.cgi?Cmd=nav&action=20"),
		GET_TIME("GetTime.cgi"),
		GET_STATUS("GetStatus.cgi"),
		GET_LOG("GetLog.cgi"),
		GET_VER("GetVer.cgi"),
		REBOOT("Reboot.cgi"),
		GET_DATA("GetData.cgi");
		
		private String command;
		public static int speed = 1;
		
		private CommandString(String command) {
			this.command = command;
		}
		
		public String toString() {
			return this.command;
		}
		
		public String getURLEncoding() throws UnsupportedEncodingException {
			return URLEncoder.encode(this.command, "UTF-8");
		}
	}

	public enum Direction {
		LEFT(0),
		RIGHT(1);
		
		private int value;
		private static final Map<Integer, Direction> lookup = new HashMap<Integer, Direction>();

		static {
			for(Direction d : EnumSet.allOf(Direction.class)) {
				lookup.put(d.getValue(), d);
			}
		}
		
		private Direction(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
		public static Direction get(int d) {
			return lookup.get(d);
		}
	}
	
	public enum Wheel {
		LEFT,
		RIGHT,
		BACK
	}
}

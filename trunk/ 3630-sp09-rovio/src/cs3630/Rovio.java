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
	private final long sleepAmountInMillis = 200;
	private final long longSleepAmountInMillis = 2000;
	
	private Thread encoderThread;
	private MCUReport mcuReport;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Rovio myRovio = null;
		
		// Create the Rovio
		try {
			myRovio = new Rovio("192.168.10.18", "admin", "cs3630");
//			myRovio.doCommandAndPrint(Command.REBOOT);
			System.out.println(myRovio.parseRespCode(myRovio.doCommand(Command.DRIVE_HEAD_DOWN)));
			myRovio.prettyPrintMCU(myRovio.doCommand(Command.GET_MCU_REPORT));
			myRovio.rotationTest();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public Rovio(String ipAddress, String username, String password) throws MalformedURLException {
		
		this.ipAddress = ipAddress;
		this.username = username;
		this.password = password;
		rovioBaseURL =new URL( "http://" + this.ipAddress + "/");
		
		Authenticator.setDefault(this);
		
		encoderThread = new Thread(new EncoderTask());
		encoderThread.setDaemon(true);
		encoderThread.start();
		
	}
	
	private class EncoderTask implements Runnable {
		public void run() {
			while (true) {
				mcuReport.update();
				this.wait();
			}
		}
	}
	
	protected PasswordAuthentication getPasswordAuthentication() {
		
		return new PasswordAuthentication(username, password.toCharArray());
		
	}

	public String doCommand(Command command) throws IOException {
		
		String response;
		
		// Build the command
		URL commandURL = new URL(this.rovioBaseURL, command.toString());
		// Open an input stream using the command URL
		BufferedReader responseReader = new BufferedReader(new InputStreamReader(commandURL.openStream()));
		// Read the response from the input stream
		response = responseReader.readLine();
		// Print Rovio's response
		while(responseReader.ready()) {
			response += "\n" + responseReader.readLine();
		}
		// Done with the stream
		responseReader.close();
		
		return response;
		
	}
	
	public String doCommandAndPrint(Command command) throws IOException {
		
		String response = this.doCommand(command);
		System.out.println(response);
		return response;
		
	}
	
	public short getShortFromHexString(int offset, String hexString) throws Exception {
		
		// A byte is represented with 2 hex digits, check to see if we have at least that many digits in the string
		if(hexString.length() < 2) {
			throw new Exception("Invalid hex string specified.");
		}
		// Check to see if the offset is valid
		if(offset > (hexString.length() - 2)) {
			throw new Exception("Invalid offset specified.");
		}
		hexString = hexString.substring(offset, offset + 2);
		return Short.parseShort(hexString, 16);
	}
	
	public int getIntFromHexString(int offset, String hexString) throws Exception {
		
		// A short is represented with 4 hex digits, check to see if we have at least that many digits in the string
		if(hexString.length() < 4) {
			throw new Exception("Invalid hex string specified.");
		}
		// Check to see if the offset is valid
		if(offset > (hexString.length() - 4)) {
			throw new Exception("Invalid offset specified.");
		}
		hexString = hexString.substring(offset, offset + 4);
		return Integer.parseInt(hexString, 16);
	}

	public int getWheelTicks(Wheel w, String mcuResponse) throws Exception {
		
		int ticks = 0;
		mcuResponse = this.parseMCUHex(mcuResponse);
		
		if(w == Wheel.LEFT) {
			ticks = getIntFromHexString(6, mcuResponse);
		}
		else if(w == Wheel.RIGHT) {
			ticks = getIntFromHexString(12, mcuResponse);
		}
		else if(w == Wheel.BACK) {
			ticks = getIntFromHexString(18, mcuResponse);
		}
		
		return ticks;
	}
	
	private String parseMCUHex(String mcuResponse) {
		
		String hexString = mcuResponse.substring(mcuResponse.length() - 30);
		return hexString;
		
	}
	
	public WheelDirection getWheelDir(Wheel w, String mcuResponse) throws Exception {

		WheelDirection d;
		mcuResponse = this.parseMCUHex(mcuResponse);
		
		if(w == Wheel.LEFT) {
			d = WheelDirection.get(this.getShortFromHexString(4, mcuResponse));
		}
		else if(w == Wheel.RIGHT) {
			d = WheelDirection.get(this.getShortFromHexString(10, mcuResponse));
		}
		else {	// w == Wheel.BACK
			d = WheelDirection.get(this.getShortFromHexString(16, mcuResponse));
		}
		return d;
		
	}
	
	public void rotationExcercise() throws Exception {
		
		int numCmds = 10;							// number of commands to send
		doCommand(Command.GET_MCU_REPORT);	// getting an MCU report clears Rovio encoder counters 
		shortPause();								// pause after sending to allow processing time
		System.out.println("Starting rotation excercise (right), sending " + numCmds + " commands...");
		for(int i = numCmds; i > 0; i--) {
			doCommand(Command.DRIVE_ROTATE_RIGHT);
			shortPause();
		}
		prettyPrintMCU(doCommand(Command.GET_MCU_REPORT));
		longPause();								// pause for a long time to allow Rovio to 'settle'
		doCommand(Command.GET_MCU_REPORT);
		System.out.println("Starting rotation excercise (right), sending " + numCmds + " commands...");
		for(int i = numCmds; i > 0; i--) {
			doCommand(Command.DRIVE_ROTATE_LEFT);
			shortPause();
		}
		longPause();								// pause for a long time to allow Rovio to 'settle'
		doCommand(Command.GET_MCU_REPORT);
		
	}
	
	private void prettyPrintMCU(String mcuResponse) throws Exception {

		String hexString = this.parseMCUHex(mcuResponse);
		System.out.print("Left Wh Rot Dir:\t" + WheelDirection.get(this.getShortFromHexString(4, hexString)) + "\n");
		System.out.print("Left Wh Num Ticks:\t" + this.getIntFromHexString(6, hexString) + "\n");
		System.out.print("Right Wh Rot Dir:\t" + WheelDirection.get(this.getShortFromHexString(10, hexString)) + "\n");
		System.out.print("Right Wh Num Ticks:\t" + this.getIntFromHexString(12, hexString) + "\n");
		System.out.print("Rear Wh Rot Dir:\t" + WheelDirection.get(this.getShortFromHexString(16, hexString)) + "\n");
		System.out.print("Rear Wh Num Ticks:\t" + this.getIntFromHexString(18, hexString) + "\n");
		System.out.print("Head Position:\t\t" + this.getShortFromHexString(24, hexString) + "\n");
		System.out.print("Battery State:\t\t" + this.getShortFromHexString(26, hexString) + "\n");

	}
	
	private void shortPause() throws InterruptedException {
		Thread.sleep(sleepAmountInMillis);
	}
	
	private void longPause() throws InterruptedException {
		Thread.sleep(longSleepAmountInMillis);
	}
	
	private ResponseCode parseRespCode(String cmdResponse) {
		
		cmdResponse = cmdResponse.substring(cmdResponse.length() - 1);
		return ResponseCode.get(Integer.parseInt(cmdResponse));
	}
	
	public long turn(RotateDirection dir, long deg) {
		
		if(dir == RotateDirection.LEFT) {
			
		}
		else {		// dir == RotateDirection.RIGHT
			
		}
		return 0;
		
	}
	
	private void rotationTest() throws Exception {
		
		int i = 1;
		
		while(getWheelTicks(Wheel.LEFT, (doCommandAndPrint(Command.GET_MCU_REPORT))) < 1){
			for(int j = 0; j < i; j++) {
				doCommand(Command.DRIVE_ROTATE_LEFT);
				shortPause();
			}
			longPause();
			i++;
		}
		System.out.println("Getting a tick registration took " + i + " turn commands.");
		
	}
	
	public enum ResponseCode {
		SUCCESS(0),
		FAILURE(1),
		ROBOT_BUSY(2),
		FEATURE_NOT_IMPLEMENTED(3),
		UNKNOWN_CGI_ACTION(4),
		NO_NS_SIGNAL(5),
		NO_EMPTY_PATH_AVAILABLE(6),
		FAILED_TO_READ_PATH(7),
		PATH_BASEADDRESS_NOT_INITIALIZED(8),
		PATH_NOT_FOUND(9),
		PATH_NAME_NOT_SPECIFIED(10),
		NOT_RECORDING_PATH(11),
		FLASH_NOT_INITIALIZED(12),
		FAILED_TO_DELETE_PATH(13),
		FAILED_TO_READ_FROM_FLASH(14),
		FAILED_TO_WRITE_TO_FLASH(15),
		FLASH_NOT_READY(16),
		NO_MEMORY_AVAILABLE(17),
		NO_MCU_PORT_AVAILABLE(18),
		NO_NS_PORT_AVAILABLE(19),
		NS_PACKET_CHECKSUM_ERROR(20),
		NS_UART_READ_ERROR(21),
		PARAMETER_OUTOFRANGE(22),
		NO_PARAMETER(23);
		
		private int value;
		private static final Map<Integer, ResponseCode> lookup = new HashMap<Integer, ResponseCode>();

		static {
			for(ResponseCode c : EnumSet.allOf(ResponseCode.class)) {
				lookup.put(c.getValue(), c);
			}
		}
		
		private ResponseCode(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
		public static ResponseCode get(int c) {
			return lookup.get(c);
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

	public enum WheelDirection {
		NOCHANGE(0),
		CLOCKWISE(2),
		COUNTERCLOCKWISE(5);
		
		private int value;
		private static final Map<Integer, WheelDirection> lookup = new HashMap<Integer, WheelDirection>();

		static {
			for(WheelDirection d : EnumSet.allOf(WheelDirection.class)) {
				lookup.put(d.getValue(), d);
			}
		}
		
		private WheelDirection(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
		public static WheelDirection get(int d) {
			return lookup.get(d);
		}
	}
	
	public enum HeadPosition {
		HIGH,
		MID,
		LOW
	}
	
	public enum BatteryState {
		NORMAL,
		NEED_CHARGE,
		DEAD
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

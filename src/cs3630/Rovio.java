/**
 * 
 */
package cs3630;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Rovio myRovio = null;
		
		// Create the Rovio
		try {
			myRovio = new Rovio("192.168.10.18", "admin", "cs3630");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			myRovio.doTurnTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doTurnTest() throws Exception {
		this.turnByNumber(RotateDirection.LEFT, Utils.angle180);
		this.pause(sleepAmountInMillis);
		this.pause(10000);
		this.turnByNumber(RotateDirection.RIGHT, Utils.angle180);
		this.pause(sleepAmountInMillis);
		this.pause(10000);
		
		System.out.println("Turning with angle of 1\n");
		
		this.turnByNumber(RotateDirection.LEFT, 1);
		this.pause(sleepAmountInMillis);
		this.turnByNumber(RotateDirection.LEFT, 1);
		this.pause(sleepAmountInMillis);
	}

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

	public String doCommand(Command command) throws IOException {
		
		return this.doCommand(command.toString());
		
	}
	
	public String doCommand(String command) throws IOException {
		
		String response;
		
		// Build the command
		URL commandURL = new URL(this.rovioBaseURL, command);
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
	
	public BufferedImage getImage() throws IOException {
		
		BufferedImage image;
		
		// Build the command
		URL commandURL = new URL(this.rovioBaseURL, "Jpeg/CamImg[0000].jpg");
		// Read the response from the input stream
		image = ImageIO.read(commandURL.openStream());
		return image;
		
	}
	
	public String doCommandAndPrint(Command command) throws IOException {
		
		String response = this.doCommand(command);
		System.out.println(response);
		return response;
		
	}

	private void pause(long sleepMillis) throws InterruptedException {
		Thread.sleep(sleepMillis);
	}
	
	@SuppressWarnings("unused")
	private ResponseCode parseRespCode(String cmdResponse) {
		
		cmdResponse = cmdResponse.substring(cmdResponse.length() - 1);
		return ResponseCode.get(Integer.parseInt(cmdResponse));
	}
	
	public void turnByPulse(RotateDirection dir, int numPulses) throws Exception {
		
		if(dir == RotateDirection.LEFT) {
			for(int i = 0; i < numPulses; i++){
				this.doCommand(Command.DRIVE_ROTATE_LEFT);
				this.pause(this.sleepAmountInMillis);
			}
		}
		else {		// dir == RotateDirection.RIGHT
			for(int i = 0; i < numPulses; i++){
				this.doCommand(Command.DRIVE_ROTATE_RIGHT);
				this.pause(this.sleepAmountInMillis);
			}
		}
	}

	public void turnByNumber(RotateDirection dir, int numAngle) throws Exception {
		
		String cmdLeft = "rev.cgi?Cmd=nav&action=18&drive=18&speed=" + Command.speed + "&angle=" + numAngle;
		String cmdRight = "rev.cgi?Cmd=nav&action=18&drive=17&speed=" + Command.speed + "&angle=" + numAngle;
		
		if(dir == RotateDirection.LEFT) {
				this.doCommand(cmdLeft);
				this.pause(this.sleepAmountInMillis);
		}
		else {		// dir == RotateDirection.RIGHT
				this.doCommand(cmdRight);
				this.pause(this.sleepAmountInMillis);
		}
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
		GET_DATA("GetData.cgi"),
		GET_IMAGE("Jpeg/CamImg[0000].jpg");
		
		private String command;
		public static final int speed = 9;
		
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

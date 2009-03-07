import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class RovioAPI implements RovioConstants, RovioAPIResponses {
	private final RovioConnection connection;

	public RovioAPI(final RovioConnection connection) {
		this.connection = connection;
	}
	
	public RovioConnection getConnection() {
		return connection;
	}
	
	/**
	 * Allows manual control over the Rovio's actions.
	 * 
	 * @param type	the type of movement to perform.
	 * @param speed	the speed at which to perform the given movement.
	 * 				Valid values range from <code>0</code> to <code>10</code>.
	 */
	public void manualDrive(final DriveType type, final int speed) {
		final int action = 18;
		final int typeValue = type.ordinal();
		try {
			InputStream in = getConnection().open(	"rev.cgi", "Cmd", "nav", "action", action,
													"drive", typeValue, "speed", speed);
			// TODO: Read the result from the stream.
			
			in.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage whatDoISee(final CameraResolution desiredRes) {
		try{
			getConnection().open("ChangeResolution.cgi", "ResType", desiredRes.ordinal());
			InputStream in = getConnection().open(
					"Jpeg/CamImg" + Math.round(Math.random() * 1000) + ".jpg");
			BufferedImage buffImage = ImageIO.read(in);
			in.close();
			return buffImage;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Allows an angle to be specified.
	 * Known angles are 45: 3, 90: 7, 135: 11, 180: 15.
	 * @param type
	 * @param speed
	 * @param angle the angle to turn.
	 */
	public void manualDrive(final DriveType type, final int speed, final int angle) {
		final int action = 18;
		final int typeValue = type.ordinal();
		try {
			InputStream in = getConnection().open(	"rev.cgi", "Cmd", "nav", "action", action,
													"drive", typeValue, "speed", speed, "angle", angle);
			// TODO: Read the result from the stream.
			
			in.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public MCUReport getMCUReport() {
		final int action = 20;
		
		try {
			final InputStream in = getConnection().open("rev.cgi", "Cmd", "nav", "action", action);
			final MCUReport report = new MCUReport(in);
			in.close();
			return report;
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void goHome() {
		final int action = 12;

		try {
			final InputStream in = getConnection().open("rev.cgi", "Cmd", "nav", "action", action);
			// TODO: Add response parsing.
			in.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void goHomeAndDock() {
		final int action = 13;

		try {
			final InputStream in = getConnection().open("rev.cgi", "Cmd", "nav", "action", action);
			// TODO: Add response parsing.
			in.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void napTime(int millisToSleep) {
		try {
			Thread.sleep((long) millisToSleep);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

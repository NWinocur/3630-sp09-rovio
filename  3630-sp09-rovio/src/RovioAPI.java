import java.io.*;

public class RovioAPI implements RovioConstants {
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
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
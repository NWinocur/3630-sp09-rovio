import java.io.*;

public class RovioAPI {
	private final RovioConnection connection;

	public RovioAPI(final RovioConnection connection) {
		this.connection = connection;
	}
	
	public RovioConnection getConnection() {
		return connection;
	}
	
	public void manualDrive(int direction, int speed) {
		final int action = 18;
		try {
			InputStream in = getConnection().open(	"rev.cgi", "Cmd", "nav", "action", action,
													"drive", direction, "speed", speed);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}

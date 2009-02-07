import java.util.List;
import java.io.InputStream;

/**
* This class is intended to contain the methods that run each program, including
* the main method.
*/
public abstract class Controller {
	
	public static void connectTheDots() {
//		Robot r = new Robot();
//		List<Waypoint> path = new List<Waypoint>();
//		// fill path from text file (or call a method in ConnectTheDots to do this)
//		//////////////////////////////////////////////// FIX THIS
//		Planner p = new ConnectTheDots(r, path);
//		p.makeMove();
	}
	
	/**
	* main method
	* @param args command-line arguments
	*/
	public static void main(String[] args) {
		//Controller.connectTheDots();
		RovioConnection rc = new RovioConnection("192.168.10.18", "admin", "cs3630");
		Robot r = new Robot(rc);
		try {
			// Query the robot's status
			InputStream in;
//			URL url = new URL(rovio.getHost() + "/rev.cgi?Cmd=nav&action=1");
//			in = rovio.open(url);
			in = rc.open("rev.cgi", "Cmd", "nav", "action", 1);
			
			// Display the result
			// Note: Use a Scanner to process this.
			int c = in.read();
			while(c >= 0) {
				System.out.print((char)c);
				c = in.read();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}

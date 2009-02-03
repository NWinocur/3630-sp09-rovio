import java.util.List;

/**
* This class is intended to contain the methods that run each program, including
* the main method.
*/
public abstract class Controller {
	
	public static void connectTheDots() {
		Robot r = new Robot();
		List<Waypoint> path = new List<Waypoint>();
		// fill path from text file (or call a method in ConnectTheDots to do this)
		//////////////////////////////////////////////// FIX THIS
		Planner p = new ConnectTheDots(r, path);
		p.makeMove();
	}
	
	/**
	* main method
	* @param args command-line arguments
	*/
	public static void main(String[] args) {
		Controller.connectTheDots();
	}
	
}

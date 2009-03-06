import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JFileChooser;



/**
 * This class is intended to contain the methods that run each program,
 * including the main method.
*/
public abstract class Controller {
	
	public static Planner connectTheDots(Robot r) {
		JFileChooser letsChooseAFile = new JFileChooser(
				"C:\\Documents and Settings");
		letsChooseAFile.showOpenDialog(null);
		List pathToTake = Controller.loadPathFromFile(letsChooseAFile
				.getSelectedFile());
		ConnectTheDots ctdDemo = new ConnectTheDots(r, pathToTake);
		return ctdDemo;
	}
	
	public static Planner ooPurtyColors(Robot r)
	{
		ooPurtyColors toReturn = new ooPurtyColors(r);
		return toReturn;
	}

	/**
	 * main method
	 * 
	 * @param args
	 *            command-line arguments
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		RovioConnection rc = new RovioConnection("192.168.10.18", "admin", "cs3630");
		System.out.println("connection established and authenticated");
		Robot r = new Robot(rc);
		// Planner planDemonstration = connectTheDots(r);
		Planner planDemonstration = ooPurtyColors(r);
		while (true) {
			planDemonstration.makeMove();
		}

		
	/*
	 * try { // Query the robot's status //InputStream in; // URL url = new
	 * URL(rovio.getHost() + "/rev.cgi?Cmd=nav&action=1"); // in =
	 * rovio.open(url); in = rc.open("rev.cgi", "Cmd", "nav", "action", 1);
	 * 
	 * // Display the result // Note: Use a Scanner to process this. int c =
	 * in.read(); while(c >= 0) { System.out.print((char)c); c = in.read();
	 * }
	 * 
	 * } catch(Exception e) { e.printStackTrace(); }
	 */
	}
	
	/* loads coordinates from a text file and creates a path of waypoints */
	static public List<Waypoint> loadPathFromFile(File filename) {
	    List<Waypoint> path = new Vector<Waypoint>();
	try {
		Scanner ls = new Scanner(filename);
		while (ls.hasNextLine()) {
		    Scanner s = new Scanner(ls.nextLine());
		s.useDelimiter(",");
		try {
		    Waypoint w = new Waypoint(s.nextDouble(), s.nextDouble(), s
			    .nextDouble());
		    path.add(w);
		    // System.out.println(w.toString());
		} catch (Exception e) {
		    System.out.println("input file is in the wrong format");
		    e.printStackTrace();
		}
		}
		return path;
	    } catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
	}
	
}

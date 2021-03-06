import java.util.List;
import java.util.ListIterator;

public class ConnectTheDots extends Planner {
	
	/** the path for the planner to drive the robot along */
	private List<Waypoint> path;
	private ListIterator<Waypoint> iterator;
	
	public ConnectTheDots(Robot robot, List<Waypoint> path) {
		super(robot);
		this.path = path;
		this.iterator = this.path.listIterator();
		this.currentPosition = new Waypoint(0, 0, 90);
	}
	
	/**
	 * control the robot to move to the next point in the plan
	 * 
	 * @throws InterruptedException
	 */
	public void makeMove() {
		while (this.iterator.hasNext()) {
			// System.out.println("ConnectTheDots.java is about to do super.driveTo(this.iterator.next())");
			super.driveTo(this.iterator.next());
		}
	}
	
}

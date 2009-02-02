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
		this.currentPosition = this.iterator.next();
	}
	
	/** control the robot to move to the next point in the plan */
	public void makeMove() {
		while (this.iterator.hasNext()) {
			this.driveTo(this.iterator.next());
		}
	}
	
	/** drives from current point to point */
	protected void driveTo(Waypoint point) {
		// check if already at point: if so, do nothing
		
		// turn towards point
		
		// drive to point
		
		// turn towards final theta
		
	}
	
}

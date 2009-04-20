import java.awt.image.BufferedImage;


public class Demo3Automatic extends Planner {
	
	private ColorSpace cspace;
	private Map map;
	private ImageProc d3aIP;
	private int confusedTurns;
	
	public Demo3Automatic(Robot robot, ColorSpace cspace, Map map) {
		super(robot);
		this.cspace = cspace;
		this.map = map;
		this.d3aIP = new ImageProc();
		this.confusedTurns = 0;
	}
	
	public void makeMove() {
		Waypoint currentLocation = null;
		Waypoint goal = new Waypoint(0, 0, 0);
		// localize
		Waypoint localizedLocation = localize();
		// go to goal
		driveToGoal(localizedLocation, goal);
		// halt
		while (true) {}
	}
	
	private BufferedImage actAsIfJustKidnapped() {
		BufferedImage rawImageArray[] = burstFire();
		BufferedImage noiseReduced = d3aIP.reduceNoise(d3aIP
				.average(rawImageArray));
		return d3aIP.segmentOutAllHues(noiseReduced);
	}
	
	public void balk() {
		double d = -1;


		

	}

	private void lookConfused() {
		if (0 == confusedTurns) {
			double d = -1;
			double angle = super.currentPosition.getTheta()
					* (Math.PI / ((double) 180));
			double dx = d * Math.cos(angle);
			double dy = d * Math.sin(angle);
			Waypoint tempGoal = new Waypoint(super.currentPosition.getX() + dx,
					super.currentPosition.getY() + dy, super.currentPosition
							.getTheta());
			currentPosition = new Waypoint(0, 0, 90);
			super.driveTo(tempGoal);
		} else {
			Waypoint tempGoal = new Waypoint(super.currentPosition.getX(),
					super.currentPosition.getY(), super.currentPosition
							.getTheta() - 15);
			super.driveTo(tempGoal);
		}
		confusedTurns = (confusedTurns + 1) % 25;
		System.out
				.println("\"Confused \", did \"confused\" action, confusedTurns is now == "
						+ confusedTurns);
	}
	
	public Waypoint localize() {
		// fact: we've been set down on ground
		// fact: we'll either have targets in view already or not
		// do: optics
		// do: noisereduction
		// do: segmentation of all hues
		BufferedImage justKidnappedBI = actAsIfJustKidnapped();
		Target bestTargetInView = pickBestTargetInView(justKidnappedBI);
		if (null == bestTargetInView) {
			lookConfused();// ask: any target in view?
		} else
			System.out.println("Chose a 'best' target");
		// if no targets in view, look confused to find any target.
		// if targets in view, pick one. Preferably "best" one.
		// todo: decide what constitutes "best."
		// do: turn towards picked target
		// fact: desirable target is in view
		
		// if longdist from target, use old code
		// elseif closedist from target, unsuitable, handle (john and I argue on
		// how)
		// elseif goodDist from target, rotate to put in focus (old code)
		// then if lined up, use new code; if not lined up, use old code
		
		return new Waypoint(0, 0, 0);
	}
	
	private Target pickBestTargetInView(BufferedImage allHueSegmentedImg) {
		Target toReturn;
		Target[] potentialTarget = new Target[5];
		for (int t = 0; t < potentialTarget.length; t++)
		{
			int targetHue = cspace.getTargetingData(t, 0);
			int targetHueWindow = cspace.getTargetingData(t, 1);
			int sat = cspace.getTargetingData(t, 2);
			potentialTarget[t] = d3aIP.targetFromSingleHueSegmentedImg(d3aIP
					.segmentOutAHue(allHueSegmentedImg, targetHue,
							targetHueWindow, sat), t);
		}
		
		
		return toReturn;
	}

	public void driveToGoal(Waypoint current, Waypoint goal) {
		
	}
	
}

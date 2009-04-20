import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;


public class Demo3Automatic extends Planner {

	private ColorSpace cspace;
	private Map map;
	private ImageProc d3aIP;
	private int confusedTurns;
	private DistanceTable distanceTable;

	public Demo3Automatic(Robot robot, ColorSpace cspace, Map map) {
		super(robot);
		super.currentPosition = new Waypoint(-1337, -1337, 42);
		this.cspace = cspace;
		this.map = map;
		this.d3aIP = new ImageProc();
		this.confusedTurns = 0;
		try {
			distanceTable = new DistanceTable(new File("distanceTable.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void makeMove() {
		System.out
				.println("D3Auto's makeMove just started, currentPos posited at "
						+ super.currentPosition.toString());
		Waypoint goal = new Waypoint(0, 0, 0);
		// localize
		Waypoint localizedLocation = null;
		do {
			localizedLocation = localize();
		} while (null == localizedLocation);
		super.currentPosition = localizedLocation;
		driveToGoal(goal);
		// halt
		while (true) {}
	}

	private BufferedImage actAsIfJustKidnapped() {
		BufferedImage rawImageArray[] = burstFire();
		BufferedImage noiseReduced = d3aIP.reduceNoise(d3aIP
				.average(rawImageArray));
		return d3aIP.segmentOutAllHues(noiseReduced);
	}

	private void lookConfused() {
		System.out.println("lookConfused(" + confusedTurns + ") called");
		if (0 == confusedTurns) {
			double d = -1;
			double angle = super.currentPosition.getTheta()
			* (Math.PI / ((double) 180));
			double dx = d * Math.cos(angle);
			double dy = d * Math.sin(angle);
			Waypoint tempGoal = new Waypoint(super.currentPosition.getX() + dx,
					super.currentPosition.getY() + dy, super.currentPosition
					.getTheta());
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
		System.out.println("Starting localize()");
		BufferedImage justKidnappedBI = actAsIfJustKidnapped();
		
		Target bestTargetInView = pickBestTargetInView(justKidnappedBI);
		// ImageProc.showImageAndPauseUntilOkayed(justKidnappedBI);
		
		if (null == bestTargetInView) {
			// if no targets in view, look confused to find any target.
			lookConfused();
			return null;
		} else
		{
			// if targets in view, pick one. Preferably "best" one.
			// todo: decide what constitutes "best." At the moment it's
			// "biggest"
			// looking for "biggest" without much checking for validity
			// could cause problems.
			System.out.println("Chose a 'best' target; has color "
					+ bestTargetInView.getTargetColor() + " and area "
					+ bestTargetInView.getAreaWithRespectToCorners());
		}

		if (useOldCodeToReturnTrueIfAligned(bestTargetInView)) {
			// //////FIX THIS
			// we're aligned with a target in here, and we can easily get its
			// color,
			// but we don't know *which* target of that color we're aligned
			// with
			// so new code for comparing sensed stuff with mapped stuff goes
			// here.
			// Once location is known, return it.
		}
		else
		{
			return null;
		}
		return null;
	}

	private boolean useOldCodeToReturnTrueIfAligned(Target targetPicked) {
		int avgHeight = targetPicked.getHeight();
		// alternate driving strategy: play chicken
		double distanceFromMarker = distanceTable.getDistance(avgHeight);
		System.out.println("Distance from marker: " + distanceFromMarker);
		boolean aligned = playChicken(targetPicked, distanceFromMarker);
		return aligned; 
	}

	/**
	 * driving strategy: play chicken idea: alternate between centering the
	 * marker in the view and driving parallel to the marker until the robot is
	 * in front of the marker, facing the marker such that it simply has to
	 * drive forward until it gets to the goal position as if it was playing
	 * chicken with the marker (get in front of it and then drive towards it
	 * 
	 * @return true if aligned and false otherwise this method only aligns it,
	 *         but does not drive it forward
	 */
	private boolean playChicken(Target targetPicked, double distanceFromMarker) {
		boolean aligned = false;
		// center marker in view
		int center = (int) Math.round(targetPicked.getCentroid().getX());
		if (center < 100) {
			// turn left 15 degrees
			super.currentPosition = new Waypoint(0, 0, 0);
			driveTo(new Waypoint(0, 0, 15));
		} else if (center > 540) {
			// turn right 15 degrees
			super.currentPosition = new Waypoint(0, 0, 0);
			driveTo(new Waypoint(0, 0, -15));
		} else {
			final double moveToMinDistance = 0.762;
			if (distanceFromMarker > moveToMinDistance) {
				super.currentPosition = new Waypoint(0, 0, 90);
				driveTo(new Waypoint(0, distanceFromMarker - moveToMinDistance,
						90));
				return false;
			}
			// marker is sufficiently centered
			// drive parallel to marker a short distance to make slope closer to
			// 0
			// top edge slope is a better indicator than bottom slope
			double slope = (targetPicked.getTopEdgeSlope() - targetPicked
					.getBottomEdgeSlope()) / 2;
			// slope allowed threshhold determined from angle table
			if (Math.abs(slope) > 0.03
					&& Math.abs(targetPicked.getTopEdgeSlope()
							- targetPicked.getBottomEdgeSlope()) > 0.10) {
				// sloped too much
				// move parallel to marker to make slope closer to 0
				super.currentPosition = new Waypoint(0, 0, 90);
				double distToMoveParallel;
				if (Math.abs(center - 320) < 146) {
					distToMoveParallel = 0.125;
				} else {
					distToMoveParallel = 0.25;
				}
				driveTo(new Waypoint(slope > 0 ? -distToMoveParallel
						: distToMoveParallel, 0, 90));
			} else {
				// nothing more needed except to drive straight at marker
				aligned = true;
			}
		}
		return aligned;
	}


	private Target pickBestTargetInView(BufferedImage allHueSegmentedImg) {
		Target toReturn;
		Target[] potentialTarget = new Target[5];

		for (int t = 0; t < potentialTarget.length; t++)
		{
			int targetHue = cspace.getTargetingData(t, 0);
			int targetHueWindow = cspace.getTargetingData(t, 1);
			int sat = cspace.getTargetingData(t, 2);
			BufferedImage singleHueSegmentedImg = d3aIP.segmentOutAHue(
					allHueSegmentedImg, targetHue, targetHueWindow, sat);
			// ImageProc.showImage(singleHueSegmentedImg);
			potentialTarget[t] = d3aIP.targetFromSingleHueSegmentedImg(
					singleHueSegmentedImg, t);
		}

		double biggestTargetArea = -1;
		int biggestTarget = -1;
		for (int t = 0; t < potentialTarget.length; t++) {
			if (null != potentialTarget[t]) {
				double areaHere = potentialTarget[t]
				                                  .getAreaWithRespectToCorners();
				if (Double.NaN != areaHere && areaHere > biggestTargetArea) {
					biggestTargetArea = areaHere;
					biggestTarget = t;
				}
			}
		}
		if (-1 == biggestTarget)
			return null;
		else
			return potentialTarget[biggestTarget];
	}

	public void driveToGoal(Waypoint goal) {
		// should decide which waypoint in map is closest to goal waypoint
		// instead of beelining
		
		super.driveTo(goal);
	}

}

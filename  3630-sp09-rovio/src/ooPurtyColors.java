import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class ooPurtyColors extends Planner {

	public static boolean showImages = false;

	static public void rgb2hsv(int r, int g, int b, int hsv[]) {
		// method taken from
		// http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm

		int min; // Min. value of RGB
		int max; // Max. value of RGB
		int delMax; // Delta RGB value

		if (r > g) {
			min = g;
			max = r;
		} else {
			min = r;
			max = g;
		}
		if (b > max)
			max = b;
		if (b < min)
			min = b;

		delMax = max - min;

		float H = 0, S;
		float V = max;

		if (delMax == 0) {
			H = 0;
			S = 0;
		} else {
			S = delMax / 255f;
			if (r == max)
				H = ((g - b) / (float) delMax) * 60;
			else if (g == max)
				H = (2 + (b - r) / (float) delMax) * 60;
			else if (b == max)
				H = (4 + (r - g) / (float) delMax) * 60;
		}

		hsv[0] = (int) (H);
		hsv[1] = (int) (S * 100);
		hsv[2] = (int) (V * 100);
	}

	public final float[] blur1 = { 1, 1, 1, 1, 1, 1, 1, 1, 1 };



	public final float[] blur2 = { 1, 2, 1, 2, 4, 2, 1, 2, 1 };


	public final int burstLength = 4;
	public int cameraBrightness;
	public RovioConstants.CameraResolution cameraResolution;
	private DistanceTable distanceTable;
	public final float[] edgeDetect1 = { -5, -5, -5, -5, 39, -5, -5, -5, -5 };
	public final float[] edgeDetect2Laplacian = { 0, 1, 0, 1, -4, 1, 0, 1, 0 };


	public final float[] edgeDetect3Laplacian = { -1, -1, -1, -1, 8, -1, -1,
			-1, -1 };
	public final float[] emboss = { -1, -1, 0, -1, 0, 1, 0, 1, 1 };
	public RovioConstants.HeadPosition headPosition;
	public final float[] horizLineDetect = { -1, -2, -1, 0, 0, 0, 1, 2, 1 };

	public final float[] interestPointDetector = { -1, -1, -1, -1, 8, -1, -1,
			-1, -1 };

	/* use this to initialize the planner but do not have the robot start moving yet */
	public ooPurtyColors(Robot robot) {
		super(robot);
		cameraResolution = RovioConstants.CameraResolution._640x480;
		cameraBrightness = 6;
		headPosition = RovioConstants.HeadPosition.LOW;

		try {
			distanceTable = new DistanceTable(new File("distanceTable.txt"));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	private boolean areAllCornerCoordsInView(boolean[] setOfBools) {
		for (int i = 0; i < setOfBools.length; i++) {
			if (false == setOfBools[i]) {
				return false;
			}
		}
		return true;
	}

	public BufferedImage average(BufferedImage[] images) {

		int n = images.length;

		// Assuming that all images have the same dimensions
		int w = images[0].getWidth();
		int h = images[0].getHeight();


		BufferedImage average = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_RGB);

		WritableRaster raster = average.getRaster()
		.createCompatibleWritableRaster();

		for (int y = 0; y < h; ++y)
			for (int x = 0; x < w; ++x) {

				float rsum = 0.0f;
				float gsum = 0.0f;
				float bsum = 0.0f;

				for (int i = 0; i < n; ++i) {
					rsum = rsum + images[i].getRaster().getSample(x, y, 0);
					gsum = gsum + images[i].getRaster().getSample(x, y, 1);
					bsum = bsum + images[i].getRaster().getSample(x, y, 2);
				}
				raster.setSample(x, y, 0, Math.round(rsum / n));
				raster.setSample(x, y, 1, Math.round(gsum / n));
				raster.setSample(x, y, 2, Math.round(bsum / n));

			}

		average.setData(raster);

		return average;
	}

	private int avgXofCorners(int[][] cornerCoords) {
		return (int) (Math
				.round((double) (((cornerCoords[0][0] + cornerCoords[2][0]) / 2) + ((cornerCoords[1][0] + cornerCoords[3][0]) / 2)) / 2));
	}

	private int avgYofCorners(int[][] cornerCoords) {
		return (int) (Math
				.round((double) (((cornerCoords[0][1] + cornerCoords[1][1]) / 2) + ((cornerCoords[2][1] + cornerCoords[3][1]) / 2)) / 2));
	}

	private BufferedImage[] burstFire(int imagesToTake) {
		BufferedImage imagesToReturn[] = new BufferedImage[imagesToTake];

		super.robot.whatDoISee(cameraResolution);// this first take is PURPOSELY
		// not being assigned
		// anywhere; using it to
		// throw out first image to
		// reduce ghosting in avg
		for (int n = 0; n < imagesToTake; n++){
			imagesToReturn[n] = super.robot.whatDoISee(cameraResolution);
			RovioAPI.napTime(5);
		}
		System.out.println("TAKING NEW PICTURE(S)");
		return imagesToReturn;

	}


	private double CheckBothDiagonals(int[][] cornerCoords, double desiredCertainty,
			BufferedImage hasPercievedTargetCorners, int targetHueWindow, int targetHue) {
		int toReturn = 0;
		if (isDiagonalOfTargetColor(cornerCoords[0][0], cornerCoords[0][1],
				cornerCoords[3][0], cornerCoords[3][1], desiredCertainty,
				hasPercievedTargetCorners, targetHueWindow, targetHue)) {
			toReturn++;
		}
		if (isDiagonalOfTargetColor(cornerCoords[2][0], cornerCoords[2][1],
				cornerCoords[1][0], cornerCoords[1][1], desiredCertainty,
				hasPercievedTargetCorners, targetHueWindow, targetHue)) {
			toReturn++;
		}
		return toReturn;
	}

	// ASSUMES SQUARE KERNEL
	private BufferedImage convolveBuffWithKernel(BufferedImage sourceImage,
			float[] kArray) {
		BufferedImage cmModdedImage = new BufferedImage(
				sourceImage.getWidth(),
				sourceImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		cmModdedImage.setData(sourceImage.getRaster());
		Kernel convKernel = new Kernel((int) Math.sqrt(kArray.length),
				(int) Math.sqrt(kArray.length), kArray);
		ConvolveOp ourConvolver = new ConvolveOp(convKernel,
				ConvolveOp.EDGE_NO_OP, null);
		BufferedImage toReturn = ourConvolver.createCompatibleDestImage(
				cmModdedImage, null);

		toReturn = ourConvolver.filter(cmModdedImage, toReturn);
		// toReturn.setData(ourConvolver.filter(sourceImage.getRaster(), null));
		return toReturn;
	}

	/**
	 * drives closer to the goal using our "driving to goal" strategy
	 * 
	 * @param currentPos
	 *            believed current position and orientation of the robot
	 * @param finalGoal
	 *            believed position of final goal
	 * @return true if robot is at goal, false if more moving might be needed
	 *         read notes in comment inside method
	 */
	private boolean driveCloserToGoal(Waypoint currentPos, Waypoint finalGoal) {
		/*
		 * "driving to goal" driving strategy detailed in next comment,
		 * coordinates may be with respect to the marker or the world coordinate
		 * frame, currentPos and finalGoal are assumed to have some uncertainty,
		 * this method may only call for the robot to move a certain maximum
		 * distance to get to the goal assuming that this method will be called
		 * again after recalculating the estimated currentPos and finalGoal,
		 * this method returns true if the robot is at the goal or false if this
		 * method needs to be called again
		 */

		double maxDistance = 8;

		/*
		 * algorithm for driving strategy: "driving to goal" if at goal
		 * correctOrientation return true if finalGoal is <= maxDistance from
		 * currentPos drive to finalGoal correctOrientation return true
		 * calculate a waypoint on path to goal that is <= maxDistance distance
		 * away drive to that point return false
		 */

		super.currentPosition = currentPos;
		if (currentPos.distance(finalGoal) < 1) {
			// correctOrientation();
			return true;
		}
		if (currentPos.distance(finalGoal) <= maxDistance) {
			driveTo(finalGoal);
			// correctOrientation();
			return true;
		}
		driveTo(pickPointOnWayToGoal(finalGoal, maxDistance));
		return false;
	}

	private long euclideanDistance(int i, int j, int x, int y) {
		return Math.round(Math.sqrt((i - x) * (i - x) + (j - y) * (j - y)));
	}

	private void findCornerCoords(final BufferedImage mostOrAllNoiseGone,
			int[][] cornerCoords) {
		final int imageWidth = mostOrAllNoiseGone.getWidth();
		final int imageHeight = mostOrAllNoiseGone.getHeight();

		// initialize corner coordinates to their opposites
		// the imminent for loop should overwrite them with real answers,
		// but initializing them prevents error it could get from fullblack img
		cornerCoords[0][0] = imageWidth; //initialize topLeftX to imageWidth
		cornerCoords[0][1] = imageHeight; //initialize topRightX to imageHeight
		cornerCoords[1][0] = 0;// init topRightX
		cornerCoords[1][1] = imageHeight;// init topRightY
		cornerCoords[2][0] = imageWidth;// init bottomLeftX
		cornerCoords[2][1] = 0;// init bottomLeftY
		cornerCoords[3][0] = 0;//init bottomRightX
		cornerCoords[3][1] = 0;//init bottomRightY

		// initialize "best" scores to worst scores possible for overwriting
		long topLeftScoreBest = euclideanDistance(0, 0, imageHeight, imageWidth);
		long topRightScoreBest = topLeftScoreBest;
		long bottomLeftScoreBest = topLeftScoreBest;
		long bottomRightScoreBest = topLeftScoreBest;

		for (int y = 1; y < imageHeight - 1; ++y) {
			for (int x = 1; x < imageWidth - 1; ++x) {
				if (howLonelyAmI(mostOrAllNoiseGone, x, y) <= 7) {
					long topLeftScoreHere, topRightScoreHere, bottomLeftScoreHere, bottomRightScoreHere;
					if (topLeftScoreBest > (topLeftScoreHere = euclideanDistance(
							0, 0, x, y)))
					{
						topLeftScoreBest = topLeftScoreHere;
						cornerCoords[0][0] = x;
						cornerCoords[0][1] = y;
					}
					else if (topRightScoreBest > (topRightScoreHere = euclideanDistance(
							imageWidth, 0, x, y))) {
						topRightScoreBest = topRightScoreHere;
						cornerCoords[1][0] = x;
						cornerCoords[1][1] = y;
					} else if (bottomLeftScoreBest > (bottomLeftScoreHere = euclideanDistance(
							0, imageHeight, x, y))) {
						bottomLeftScoreBest = bottomLeftScoreHere;
						cornerCoords[2][0] = x;
						cornerCoords[2][1] = y;
					} else if (bottomRightScoreBest > (bottomRightScoreHere = euclideanDistance(
							imageWidth, imageHeight, x, y))) {
						bottomRightScoreBest = bottomRightScoreHere;
						cornerCoords[3][0] = x;// init bottomRightX
						cornerCoords[3][1] = y;
					}
				}
			}
		}
	}

	private int howLonelyAmI(final BufferedImage imageToCheck, final int x, final int y) {
		// x and y passed here MUST be 0 > variable > image's max of that
		// dimension
		int numBlankNeighbors = 0;
		// final int width = imageToCheck.getWidth(), height =
		// imageToCheck.getHeight();
		final WritableRaster raster = imageToCheck.getRaster();
		for (int suby = y - 1; suby <= y + 1; suby++) {
			for (int subx = x - 1; subx <= x + 1; subx++) {
				// if(suby < 0 || subx < 0 || subx >= width || suby >= height )
				// {
				// numBlankNeighbors++;
				// } else {
				int rgbtot = raster.getSample(subx, suby, 0)
				+ raster.getSample(subx, suby, 1)
				+ raster.getSample(subx, suby, 2);
				if (rgbtot == 0)
					numBlankNeighbors++;
				// }
			}
		}
		return numBlankNeighbors;
	}

	/**
	 * checks if a diagonal line between two opposite corners of a proposed
	 * tradizoid mostly contains pixels of approximately the same color, corner
	 * 2 must be right of corner 1 {(c2x >= c1x) must be true}
	 * 
	 * @param c1x
	 *            x coordinate of corner 1
	 * @param c1y
	 *            y coordinate of corner 1
	 * @param c2x
	 *            x coordinate of corner 2
	 * @param c2y
	 *            y coordinate of corner 2
	 * @param allowedExceptions
	 *            the number of pixels allowed to be different along the line
	 * @param i
	 *            the image
	 * @param threshhold
	 *            allowed difference in hue to be considered the same color
	 * @return true if diagonal is mostly the same color as corners, false if
	 *         c2x==c1x, false otherwise
	 */
	public boolean isDiagonalOfTargetColor(int c1x, int c1y, int c2x, int c2y,
			double maxPercentBad, BufferedImage i, int threshhold, int targetHue) {
		int dx = c2x - c1x;
		int dy = c2y - c1y;
		if (dx == 0) {
			// System.out.print("dx == 0");
			return false;
		}
		if (c2x < c1x) {
			// System.out.print("c2x < c1x");
			return false; // to avoid an infinite loop
		}
		double slope = ((double) dy) / dx;
		int currentX = c1x;
		int r, g, b, currentY;
		int[] hsv;
		int numExceptions = 0;
		int pixelsTraversed = 0;
		while (currentX < c2x) {
			currentY = (int) Math.round((slope * (currentX - c1x)) + c1y);
			r = i.getRaster().getSample(currentX, currentY, 0);
			g = i.getRaster().getSample(currentX, currentY, 1);
			b = i.getRaster().getSample(currentX, currentY, 2);
			hsv = new int[3];
			rgb2hsv(r, g, b, hsv);
			if (Math.abs(hsv[0] - targetHue) > threshhold) {
				numExceptions++;
			}
			currentX++;
			pixelsTraversed++;
		}
		if (currentX == c2x
				&& ((double) numExceptions) / pixelsTraversed <= maxPercentBad) {
			return true;
		}
		return false;
	}

	private BufferedImage killLonelyPixelsInTheBlackness(BufferedImage rawImage) {
		int imageWidth = rawImage.getWidth();
		int imageHeight = rawImage.getHeight();

		BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_RGB);

		WritableRaster raster = toReturn.getRaster()
		.createCompatibleWritableRaster();

		for (int y = 1; y < imageHeight - 1; ++y) {
			for (int x = 1; x < imageWidth - 1; ++x) {
				if (howLonelyAmI(rawImage, x, y) >= 6) {
					raster.setSample(x, y, 0, 0);
					raster.setSample(x, y, 1, 0);
					raster.setSample(x, y, 2, 0);
				} else {
					raster.setSample(x, y, 0, rawImage.getRaster().getSample(x,
							y, 0));
					raster.setSample(x, y, 1, rawImage.getRaster().getSample(x,
							y, 1));
					raster.setSample(x, y, 2, rawImage.getRaster().getSample(x,
							y, 2));
				}
			}
		}
		toReturn.setData(raster);
		return toReturn;
	}

	private boolean looksLikeWeAreParked(BufferedImage hueSegmented,
			final int targetHue, final int targetHueWindow,
			final int minSatToBeUseful) {
		final int imageWidth = hueSegmented.getWidth();
		final int imageHeight = hueSegmented.getHeight();

		final double[][] hueArray = new double[imageWidth][imageHeight];
		final double[][] satArray = new double[imageWidth][imageHeight];

		int targetPixelsFound = 0;
		int r, g, b;
		int[] hsv;
		for (int y = 0; y < imageHeight; ++y) {

			for (int x = 0; x < imageWidth; ++x) {
				r = hueSegmented.getRaster().getSample(x, y, 0);
				g = hueSegmented.getRaster().getSample(x, y, 1);
				b = hueSegmented.getRaster().getSample(x, y, 2);
				hsv = new int[3];
				rgb2hsv(r, g, b, hsv);

				hueArray[x][y] = hsv[0];
				satArray[x][y] = hsv[1];
				if (targetHueWindow > Math.abs(hueArray[x][y] - targetHue)
						&& minSatToBeUseful < satArray[x][y]) {
					targetPixelsFound++;
				}
			}
		}
		double percentPixelsSatisfied = ((double)targetPixelsFound)/(imageWidth*imageHeight); 
		if (percentPixelsSatisfied >= 0.90) {
			System.out
			.println("Looks like " + percentPixelsSatisfied
					+ "of view is target hue");
			return true;
		}
		return false;
	}

	/*
	 * use this to actually move, either by one iteration or the entire program
	 * 
	 * @see Planner#makeMove()
	 */
	@Override
	public void makeMove() {


		// targetingData: array of [r, y, g, b, v][target, window, minsat]
		int[][] targetingData = new int[5][3];
		targetingData[0][0] = 18;
		targetingData[0][1] = 40; // was 20
		targetingData[0][2] = 30;
		targetingData[1][0] = 60;
		targetingData[1][1] = 15;
		targetingData[1][2] = 30;
		targetingData[2][0] = 120;
		targetingData[2][1] = 20;
		targetingData[2][2] = 22;
		targetingData[3][0] = 200;
		targetingData[3][1] = 50;
		targetingData[3][2] = 10;
		targetingData[4][0] = 275;
		targetingData[4][1] = 20;
		targetingData[4][2] = 5;
		int targetIntRYGBV = 0;
		int confusedTurns = 0;

		boolean finished = false;
		while (finished == false) {			
			int targetHue = targetingData[targetIntRYGBV][0];
			int targetHueWindow = targetingData[targetIntRYGBV][1];
			int minSatToBeUseful = targetingData[targetIntRYGBV][2];

			BufferedImage rawImageArray[] = burstFire(burstLength);
			BufferedImage noiseReduced = reduceNoise(rawImageArray);
			showImageAndPauseUntilOkayed(noiseReduced);

			BufferedImage hueSegmented = segmentOutAHue(noiseReduced,
					targetHue, targetHueWindow, minSatToBeUseful);
			if (null == hueSegmented) {
				System.out.println("Target not sighted, thus not segmented");
				confusedTurns = lookConfused(confusedTurns);
			}
			else if (looksLikeWeAreParked(hueSegmented, targetHue,
					targetHueWindow, minSatToBeUseful)) {
				super.robot.lookMid();
				BufferedImage parkCheckImageArray[] = burstFire(burstLength);
				BufferedImage noiseReducedParkCheck = reduceNoise(parkCheckImageArray);
				showImageAndPauseUntilOkayed(noiseReducedParkCheck);

				BufferedImage hueSegmentedParkCheck = segmentOutAHue(
						noiseReducedParkCheck, targetHue, targetHueWindow,
						minSatToBeUseful);
				if (null == hueSegmentedParkCheck) {
					super.robot.duckAndCover();
				} else if (looksLikeWeAreParked(hueSegmentedParkCheck,
						targetHue,
						targetHueWindow, minSatToBeUseful)) {
					System.exit(1);
				} else {
					super.robot.duckAndCover();
				}
				super.robot.duckAndCover();
				confusedTurns = lookConfused(confusedTurns);
			}
			else {
				confusedTurns = 0;

				BufferedImage segmentedImage = reduceNoise(hueSegmented);

				showImageAndPauseUntilOkayed(segmentedImage);

				int[][] cornerCoords = new int[4][2];
				// 4 by 2 matrix of coordinates for each corner.
				// first dimension of matrix is which pair:
				// top left, top right, bottom left, bottom right.
				// second dimension of matrix is x then y.
				findCornerCoords(segmentedImage, cornerCoords);

				System.out.println("Are all corner coordinates in view? "
						+ areAllCornerCoordsInView(whichCornerCoordsAreInView(cornerCoords)));
				System.out.println("# diagonals that seem to match hue:  "
						+ CheckBothDiagonals(cornerCoords, 0.95,
								segmentedImage, targetHueWindow, targetHue));

				BufferedImage cornersPaintedWhite = paintCornersWhite(
						segmentedImage, cornerCoords);
				System.out.println("Target height is "
						+ targetHeight(cornerCoords));
				showImageAndPauseUntilOkayed(cornersPaintedWhite);


				/*
				 * BufferedImage edgesFoundByConvolving =
				 * convolveBuffWithKernel( segmentedImage, edgeDetect1);
				 * showImageAndPauseUntilOkayed(edgesFoundByConvolving);
				 * edgesFoundByConvolving =
				 * convolveBuffWithKernel(segmentedImage, edgeDetect2Laplacian);
				 * showImageAndPauseUntilOkayed(edgesFoundByConvolving);
				 * edgesFoundByConvolving =
				 * convolveBuffWithKernel(segmentedImage, edgeDetect3Laplacian);
				 * showImageAndPauseUntilOkayed(edgesFoundByConvolving);
				 */



				// segment image
				// image description (features)
				// recognition/extraction

				// drive closer to the goal
				// Waypoint currentPosEstimate = new Waypoint(0, 0, 90);
				// Waypoint finalGoal = new Waypoint(0, 0, 90);
				// finished = driveCloserToGoal(currentPosEstimate, finalGoal);

				int avgHeight = targetHeight(cornerCoords);
				// alternate driving strategy: play chicken
				double distanceFromMarker = distanceTable.getDistance(avgHeight);
				System.out.println("Distance from marker: " + distanceFromMarker);
				boolean aligned = playChicken(cornerCoords, distanceFromMarker);
				if (aligned == true) {
					// we are now in front of marker (slopes ~ 0) and
					// marker is centered
					// use distance table to drive closer to goal

					// use distance table to get distance to marker, using
					// avgHeight
					// double distanceFromMarker =
					// distanceTable.getDistance(avgHeight);

					/*
					 * if(distanceFromMarker > 0.9144) { makeMove(); }
					 */

					super.currentPosition = new Waypoint(0, 0, 90);
					driveTo(new Waypoint(0, distanceFromMarker - 0.1016, 90));
					showImages = true;



					BufferedImage parkCheckImageArray[] = burstFire(burstLength);
					BufferedImage noiseReducedParkCheck = reduceNoise(parkCheckImageArray);
					showImageAndPauseUntilOkayed(noiseReducedParkCheck);

					BufferedImage hueSegmentedParkCheck = segmentOutAHue(
							noiseReducedParkCheck, targetHue,
							targetHueWindow, minSatToBeUseful);
					if (null == hueSegmentedParkCheck) {
						super.robot.duckAndCover();
					} else if (looksLikeWeAreParked(hueSegmentedParkCheck,
							targetHue, targetHueWindow, minSatToBeUseful)) {
						System.exit(1);
					} else {
						super.robot.duckAndCover();
					}
					super.robot.duckAndCover();
					confusedTurns = lookConfused(confusedTurns);
					showImages = false;
				}

				// once at goal, block until program is manually stopped
				// System.exit(1);
				//while (true) {}
			}
		}
		// we want to use red for right now
		//targetIntRYGBV = (targetIntRYGBV + 1) % 5;
		System.out.println(targetIntRYGBV);

	}

private int lookConfused(int confusedTurns) {
	if (0 == confusedTurns) {
		currentPosition = new Waypoint(0, 0, 90);
		driveTo(new Waypoint(0, -1, 90));
	} else {
		currentPosition = new Waypoint(0, 0, 90);
		driveTo(new Waypoint(0, 0, 90 - 15));
	}
	confusedTurns = (confusedTurns + 1) % 25;
	System.out
	.println("\"Confused \", did \"confused\" action, confusedTurns is now == "
			+ confusedTurns);
	return confusedTurns;

}
private BufferedImage medianFilterRadius1(BufferedImage rawImage) {
	int imageWidth = rawImage.getWidth();
	int imageHeight = rawImage.getHeight();

	BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight,
			BufferedImage.TYPE_INT_RGB);

	WritableRaster raster = toReturn.getRaster()
	.createCompatibleWritableRaster();

	for (int y = 1; y < imageHeight - 1; ++y) {
		for (int x = 1; x < imageWidth - 1; ++x) {
			int[] rNeighbors = whatDoNineNeighborsLookLike(rawImage, x, y, 0);
			int[] gNeighbors = whatDoNineNeighborsLookLike(rawImage, x, y, 1);
			int[] bNeighbors = whatDoNineNeighborsLookLike(rawImage, x, y, 2);
			// get array of nearby pixels' (0, 1, or 2) channel

			int rMedian = medianOfNine(rNeighbors);
			int gMedian = medianOfNine(gNeighbors);
			int bMedian = medianOfNine(bNeighbors);
			// toss that array to median method, get median value back
			// write median value of that channel to new image at pixel
			raster.setSample(x, y, 0, rMedian);
			raster.setSample(x, y, 1, gMedian);
			raster.setSample(x, y, 2, bMedian);
		}
	}
	toReturn.setData(raster);
	return toReturn;
}

private BufferedImage medianFilterRadius2(final BufferedImage rawImage) {
	final int imageWidth = rawImage.getWidth();
	final int imageHeight = rawImage.getHeight();

	final BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight,
			BufferedImage.TYPE_INT_RGB);

	final WritableRaster raster = toReturn.getRaster()
	.createCompatibleWritableRaster();

	int[] rNeighbors, gNeighbors, bNeighbors;
	int rMedian, gMedian, bMedian;
	for (int y = 2; y < imageHeight - 2; ++y) {
		for (int x = 2; x < imageWidth - 2; ++x) {
			rNeighbors = whatDoSeventeenNeighborsLookLike(rawImage,
					x, y, 0);
			gNeighbors = whatDoSeventeenNeighborsLookLike(rawImage,
					x, y, 1);
			bNeighbors = whatDoSeventeenNeighborsLookLike(rawImage,
					x, y, 2);
			// get array of nearby pixels' (0, 1, or 2) channel

			rMedian = medianOfSeventeen(rNeighbors);
			gMedian = medianOfSeventeen(gNeighbors);
			bMedian = medianOfSeventeen(bNeighbors);
			// toss that array to median method, get median value back
			// write median value of that channel to new image at pixel
			raster.setSample(x, y, 0, rMedian);
			raster.setSample(x, y, 1, gMedian);
			raster.setSample(x, y, 2, bMedian);
		}
	}
	toReturn.setData(raster);
	return toReturn;
}

/**
 * taken from http://www.jhlabs.com/ip/filters/MedianFilter.html Takes in an
 * array of size 9 (MUST be this size! relies on indexes 0 to 8
 * inclusive!!!) and returns its median value
 */
private int medianOfNine(int[] array) {
	int max, maxIndex;

	for (int i = 0; i < 4; i++) {
		max = 0;
		maxIndex = 0;
		for (int j = 0; j < 9; j++) {
			if (array[j] > max) {
				max = array[j];
				maxIndex = j;
			}
		}
		array[maxIndex] = 0;
	}
	max = 0;
	for (int i = 0; i < 9; i++) {
		if (array[i] > max)
			max = array[i];
	}
	return max;
}
private int medianOfSeventeen(int[] array) {
	int max, maxIndex;

	for (int i = 0; i < 8; i++) {
		max = 0;
		maxIndex = 0;
		for (int j = 0; j < 17; j++) {
			if (array[j] > max) {
				max = array[j];
				maxIndex = j;
			}
		}
		array[maxIndex] = 0;
	}
	max = 0;
	for (int i = 0; i < 17; i++) {
		if (array[i] > max)
			max = array[i];
	}
	return max;
}

private BufferedImage paintCornersWhite(BufferedImage lonePixelsGone,
		int[][] cornerCoords) {
	final int imageWidth = lonePixelsGone.getWidth();
	final int imageHeight = lonePixelsGone.getHeight();
	final BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight,
			BufferedImage.TYPE_INT_RGB);

	final WritableRaster raster = toReturn.getRaster()
	.createCompatibleWritableRaster();

	int xToPaint;
	int yToPaint;
	for (int i = 0; i < 4; i++) {
		xToPaint = cornerCoords[i][0];
		yToPaint = cornerCoords[i][1];
		raster.setSample(xToPaint, yToPaint, 0, 254);
		raster.setSample(xToPaint, yToPaint, 1, 254);
		raster.setSample(xToPaint, yToPaint, 2, 254);
	}

	xToPaint = avgXofCorners(cornerCoords);
	yToPaint = avgYofCorners(cornerCoords);
	raster.setSample(xToPaint, yToPaint, 0, 254);
	raster.setSample(xToPaint, yToPaint, 1, 254);
	raster.setSample(xToPaint, yToPaint, 2, 254);



	targetTopEdgeSlope(cornerCoords);
	targetBottomEdgeSlope(cornerCoords);
	targetHeight(cornerCoords);
	targetArea(cornerCoords);
	targetCenterXvsPhotoCenter(cornerCoords);

	toReturn.setData(raster);
	return toReturn;
}

/** picks a point on the way to the goal, but no more than maxDistance away */
private Waypoint pickPointOnWayToGoal(Waypoint finalGoal, double maxDistance) {
	// use similar triangles
	double fd = super.currentPosition.distance(finalGoal);
	double ratio = maxDistance / fd;
	System.out.println("pickPointOnWayToGoal is setting ratio of "
			+ maxDistance + "/" + fd + "==" + maxDistance / fd);
	double dx = ((finalGoal.getX() - super.currentPosition.getX()) * ratio);
	double dy = ((finalGoal.getY() - super.currentPosition.getY()) * ratio);
	Waypoint np = new Waypoint(super.currentPosition.getX() + dx,
			super.currentPosition.getY() + dy, super.currentPosition
			.getTheta());
	System.out
	.println("pickPointOnWayToGoal is returning " + np.toString());
	return np;
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
private boolean playChicken(int[][] cornerCoords, double distanceFromMarker) {
	boolean aligned = false;
	// center marker in view
	int center = avgXofCorners(cornerCoords);
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
		if(distanceFromMarker > moveToMinDistance) {
			super.currentPosition = new Waypoint(0, 0, 90);
			driveTo(new Waypoint(0, distanceFromMarker - moveToMinDistance, 90));
			return false;
		}
		// marker is sufficiently centered
		// drive parallel to marker a short distance to make slope closer to 0
		// top edge slope is a better indicator than bottom slope
		double slope = (targetTopEdgeSlope(cornerCoords) - targetBottomEdgeSlope(cornerCoords)) / 2;
		// slope allowed threshhold determined from angle table
		if (Math.abs(slope) > 0.03
				&& Math.abs(targetTopEdgeSlope(cornerCoords)
						- targetBottomEdgeSlope(cornerCoords)) > 0.10) {
			// sloped too much
			// move parallel to marker to make slope closer to 0
			super.currentPosition = new Waypoint(0, 0, 90);
			double distToMoveParallel;
			if (Math.abs(center-320) < 146)
			{
				distToMoveParallel = 0.125;
			}
			else {
				distToMoveParallel = 0.25;
			}
			driveTo(new Waypoint(slope > 0 ? -distToMoveParallel : distToMoveParallel, 0, 90));
		} else {
			// nothing more needed except to drive straight at marker
			aligned = true;
		}
	}
	return aligned;
}

private BufferedImage reduceNoise(BufferedImage singleNoisyImage) {
	return medianFilterRadius2(medianFilterRadius1(singleNoisyImage));
}

private BufferedImage reduceNoise(BufferedImage[] rawImages) {
	return reduceNoise(average(rawImages));
}


private BufferedImage segmentOutAHue(final BufferedImage noiseReduced,
		final int targetHue, final int targetHueWindow, final int minSatToBeUseful) {
	final int imageWidth = noiseReduced.getWidth();
	final int imageHeight = noiseReduced.getHeight();

	// float notEnoughColorInfoThreshold = 140.0f;

	final BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight,
			BufferedImage.TYPE_INT_RGB);

	final WritableRaster raster = toReturn.getRaster()
	.createCompatibleWritableRaster();

	final double[][] hueArray = new double[imageWidth][imageHeight];
	final double[][] satArray = new double[imageWidth][imageHeight];
	// double maxHueYet = 0; // was intended for normalization

	int targetPixelsWritten = 0;

	int r, g, b;
	int[] hsv;
	for (int y = 0; y < imageHeight; ++y) {

		for (int x = 0; x < imageWidth; ++x) {
			r = noiseReduced.getRaster().getSample(x, y, 0);
			g = noiseReduced.getRaster().getSample(x, y, 1);
			b = noiseReduced.getRaster().getSample(x, y, 2);
			hsv = new int[3];
			rgb2hsv(r, g, b, hsv);

			hueArray[x][y] = hsv[0];
			satArray[x][y] = hsv[1];
			if (targetHueWindow > Math.abs(hueArray[x][y] - targetHue)
					&& minSatToBeUseful < satArray[x][y])
			{
				raster.setSample(x, y, 0, r);
				raster.setSample(x, y, 1, g);
				raster.setSample(x, y, 2, b);
				targetPixelsWritten++;
			}
			else {
				raster.setSample(x, y, 0, 0);
				raster.setSample(x, y, 1, 0);
				raster.setSample(x, y, 2, 0);
			}
			// maxHueYet = Math.max(maxHueYet, hueArray[x][y]);
		}
	}
	if (25 > targetPixelsWritten) {
		System.out
		.println("WARNING: desired target too small or not seen, segment returning null");
		return null;
	}
	toReturn.setData(raster);
	return toReturn;
} 

public void showImage(final Image image) {
	if (showImages) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				ImageIcon icon = new ImageIcon(image);
				/*
				 * JFrame f = new JFrame("image preview"); JPanel p = new
				 * JPanel();
				 * f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				 * f.getContentPane().add(p); f.pack(); f.setVisible(true);
				 */
				JOptionPane.showMessageDialog(null, icon);
			}
		});
		t.start();
	}
}

public void showImageAndPauseUntilOkayed(final Image image) {
	if(showImages) {
		ImageIcon icon = new ImageIcon(image);
		JOptionPane.showMessageDialog(null, icon);
	}
}

private double targetArea(int[][] cornerCoords) {
	double a = euclideanDistance(cornerCoords[0][0], cornerCoords[0][1],
			cornerCoords[1][0], cornerCoords[1][1]);
	double b = euclideanDistance(cornerCoords[1][0], cornerCoords[1][1],
			cornerCoords[2][0], cornerCoords[2][1]);
	double c = euclideanDistance(cornerCoords[2][0], cornerCoords[2][1],
			cornerCoords[3][0], cornerCoords[3][1]);
	double d = euclideanDistance(cornerCoords[3][0], cornerCoords[3][1],
			cornerCoords[0][0], cornerCoords[0][1]);
	double s = (a + b + c + d) / 2.0;
	double p = euclideanDistance(cornerCoords[0][0], cornerCoords[0][1],
			cornerCoords[2][0], cornerCoords[2][1]);
	double q = euclideanDistance(cornerCoords[1][0], cornerCoords[1][1],
			cornerCoords[3][0], cornerCoords[3][1]);
	double K = Math.sqrt(4.0
			* p
			* p
			* q
			* q
			- ((double) (b * b + d * d - a * a - c * c) * (b * b + d * d
					- a * a - c * c))) / 4.0;
	System.out.println("a" + a + " b" + b + " c" + c + " d" + d + " q" + q
			+ " p" + p);
	System.out.println("Area is " + K);
	return K;
}

private double targetBottomEdgeSlope(int[][] cornerCoords)
{
	double toReturn = ((double) (cornerCoords[2][1] - cornerCoords[3][1]))
	/ ((double) (cornerCoords[2][0] - cornerCoords[3][0]));
	toReturn *= -1.0;
	System.out.println("Slope of bottom line is " + toReturn);
	return toReturn;
}

private double targetCenterXvsPhotoCenter(int[][] cornerCoords)
{
	int targetMidX = avgXofCorners(cornerCoords);
	System.out.println("Because target's center is at " + targetMidX
			+ " target is " + (targetMidX - 320) + " pixels from center");
	return targetMidX - 320;
}

private int targetHeight(int[][] cornerCoords)
{
	int avgYofTop2Corners = (int) (Math
			.round((double) (cornerCoords[0][1] + cornerCoords[1][1])) / 2);
	int avgYofBottom2Corners = (int) (Math
			.round((double) (cornerCoords[2][1] + cornerCoords[2][1]) / 2));
	return avgYofBottom2Corners - avgYofTop2Corners;
}

private double targetTopEdgeSlope(int[][] cornerCoords) {
	double toReturn = ((double) (cornerCoords[0][1] - cornerCoords[1][1]))
	/ ((double) (cornerCoords[0][0] - cornerCoords[1][0]));
	toReturn *= -1;
	System.out.println("Slope of top line is " + toReturn);
	return toReturn;
}

private int[] whatDoNineNeighborsLookLike(BufferedImage imageToCheck, int x,
		int y, int channelOfInterest) {
	// x and y passed here MUST be 0 > variable > image's max of that
	// dimension
	// channel of interest is 0 for r, 1 for g, 2 for b
	int[] toReturn = new int[9];
	int indexOfToReturnWeWillWrite = 0;
	for (int suby = y - 1; suby <= y + 1; suby++) {
		for (int subx = x - 1; subx <= x + 1; subx++) {
			toReturn[indexOfToReturnWeWillWrite] = imageToCheck.getRaster()
			.getSample(subx, suby, channelOfInterest);
			indexOfToReturnWeWillWrite++;
		}
	}
	return toReturn;
}

private int[] whatDoSeventeenNeighborsLookLike(BufferedImage imageToCheck,
		int x, int y, int channelOfInterest) {
	// x and y passed here MUST be 0 > variable > image's max of that
	// dimension
	// channel of interest is 0 for r, 1 for g, 2 for b
	int[] toReturn = new int[17];
	toReturn[0] = imageToCheck.getRaster().getSample(x - 2, y - 2,
			channelOfInterest);
	toReturn[1] = imageToCheck.getRaster().getSample(x, y - 2,
			channelOfInterest);
	toReturn[2] = imageToCheck.getRaster().getSample(x + 2, y - 2,
			channelOfInterest);
	toReturn[3] = imageToCheck.getRaster().getSample(x - 1, y - 1,
			channelOfInterest);
	toReturn[4] = imageToCheck.getRaster().getSample(x, y - 1,
			channelOfInterest);
	toReturn[5] = imageToCheck.getRaster().getSample(x + 1, y - 1,
			channelOfInterest);
	toReturn[6] = imageToCheck.getRaster().getSample(x - 2, y,
			channelOfInterest);
	toReturn[7] = imageToCheck.getRaster().getSample(x - 1, y,
			channelOfInterest);
	toReturn[8] = imageToCheck.getRaster().getSample(x, y,
			channelOfInterest);
	toReturn[9] = imageToCheck.getRaster().getSample(x + 1, y,
			channelOfInterest);
	toReturn[10] = imageToCheck.getRaster().getSample(x + 2, y,
			channelOfInterest);
	toReturn[11] = imageToCheck.getRaster().getSample(x - 1, y + 1,
			channelOfInterest);
	toReturn[12] = imageToCheck.getRaster().getSample(x, y + 1,
			channelOfInterest);
	toReturn[13] = imageToCheck.getRaster().getSample(x + 1, y + 1,
			channelOfInterest);
	toReturn[14] = imageToCheck.getRaster().getSample(x - 2, y + 2,
			channelOfInterest);
	toReturn[15] = imageToCheck.getRaster().getSample(x, y + 2,
			channelOfInterest);
	toReturn[16] = imageToCheck.getRaster().getSample(x + 2, y + 2,
			channelOfInterest);
	return toReturn;
}

private boolean[] whichCornerCoordsAreInView(int[][] cornerCoords)
{
	boolean[] toReturn = new boolean[4];
	int x;
	int y;
	int untrustworthyPicBoundary = 4;
	for (int i = 0; i < cornerCoords.length; i++) {
		x = cornerCoords[i][0];
		y = cornerCoords[i][1];
		if (x < untrustworthyPicBoundary
				|| Math.abs(640 - x) < untrustworthyPicBoundary) {
			toReturn[i] = false;
		}
		else if (y < untrustworthyPicBoundary
				|| Math.abs(480 - y) < untrustworthyPicBoundary) {
			toReturn[i] = false;
		} else
			toReturn[i] = true;
	}
	return toReturn;
}
}

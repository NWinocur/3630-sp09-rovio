import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class ooPurtyColors extends Planner {

	static private void rgb2hsv(int r, int g, int b, int hsv[]) {
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
	public final int targetHue = 18; // target color. Red is ~18 in our normal
										// meeting room
	public final int targetHueWindow = 5; // how far from targetHue is
											// acceptably close
	public final int minSatToBeUseful = 30; // min sat to trust camera
	

	public int cameraBrightness;
	public RovioConstants.CameraResolution cameraResolution;
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
		return imagesToReturn;

	}


	private BufferedImage color_filter(BufferedImage noiseReduced) {
		int imageWidth = noiseReduced.getWidth();
		int imageHeight = noiseReduced.getHeight();

		// float notEnoughColorInfoThreshold = 140.0f;

		BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_RGB);

		WritableRaster raster = toReturn.getRaster()
		.createCompatibleWritableRaster();

		double[][] hueArray = new double[imageWidth][imageHeight];
		double[][] satArray = new double[imageWidth][imageHeight];
		// double maxHueYet = 0; // was intended for normalization

		for (int y = 0; y < imageHeight; ++y) {

			for (int x = 0; x < imageWidth; ++x) {
				int r = noiseReduced.getRaster().getSample(x, y, 0);
				int g = noiseReduced.getRaster().getSample(x, y, 1);
				int b = noiseReduced.getRaster().getSample(x, y, 2);
				int[] hsv = new int[3];
				rgb2hsv(r, g, b, hsv);

				hueArray[x][y] = hsv[0];
				satArray[x][y] = hsv[1];
				if (targetHueWindow > Math.abs(hueArray[x][y] - targetHue)
						&& minSatToBeUseful < satArray[x][y])
				{
					raster.setSample(x, y, 0, r);
					raster.setSample(x, y, 1, g);
					raster.setSample(x, y, 2, b);
				}
				else {
					raster.setSample(x, y, 0, 0);
					raster.setSample(x, y, 1, 0);
					raster.setSample(x, y, 2, 0);
				}
				// maxHueYet = Math.max(maxHueYet, hueArray[x][y]);
			}
		}
		toReturn.setData(raster);
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

	/*
	 * use this to actually move, either by one iteration or the entire program
	 * 
	 * @see Planner#makeMove()
	 */
	@Override
	public void makeMove() {
		boolean finished = false;
		while (finished == false) {
			BufferedImage rawImage[] = burstFire(burstLength);
			BufferedImage noiseReduced = reduceNoise(rawImage);
			// showImage(noiseReduced);
			BufferedImage colorFiltered = color_filter(noiseReduced);
			showImageAndPauseUntilOkayed(colorFiltered);

			BufferedImage lonePixelsGone = killLonelyPixelsInTheBlackness(colorFiltered);
			lonePixelsGone = killLonelyPixelsInTheBlackness(lonePixelsGone);
			lonePixelsGone = killLonelyPixelsInTheBlackness(lonePixelsGone);
			lonePixelsGone = killLonelyPixelsInTheBlackness(lonePixelsGone);



			showImageAndPauseUntilOkayed(lonePixelsGone);
		
		
		
			int[][] cornerCoords = new int[4][2];
			// 4 by 2 matrix of coordinates for each corner.
			// first dimension of matrix is which pair:
			// top left, top right, bottom left, bottom right.
			// second dimension of matrix is x then y.
			findCornerCoords(lonePixelsGone, cornerCoords);
			System.out.println("Does diagonal seem to match hue? "
					+ isDiagonalOfTargetColor(cornerCoords[0][0],
							cornerCoords[0][1], cornerCoords[3][0],
							cornerCoords[3][1], 0.95, lonePixelsGone,
									targetHueWindow)
					+ " and "
					+ isDiagonalOfTargetColor(cornerCoords[2][0],
							cornerCoords[2][1], cornerCoords[1][0],
							cornerCoords[1][1], 0.95, lonePixelsGone,
									targetHueWindow));
		
			// BufferedImage cornersPaintedWhite =
			// paintCornersWhite(lonePixelsGone, cornerCoords);
			// showImageAndPauseUntilOkayed(cornersPaintedWhite);




		
			 BufferedImage edgesFoundByConvolving = convolveBuffWithKernel(
					colorFiltered, edgeDetect1);
			showImageAndPauseUntilOkayed(edgesFoundByConvolving);
			edgesFoundByConvolving = convolveBuffWithKernel(colorFiltered,
					edgeDetect2Laplacian);
			showImageAndPauseUntilOkayed(edgesFoundByConvolving);
			edgesFoundByConvolving = convolveBuffWithKernel(colorFiltered,
					edgeDetect3Laplacian);
			showImageAndPauseUntilOkayed(edgesFoundByConvolving);
			 

			// segment image
			// image description (features)
			// recognition/extraction
			
			// drive closer to the goal

			Waypoint currentPosEstimate = new Waypoint(0, 0, 90);
			Waypoint finalGoal = new Waypoint(1, 0, 0);
			finished = driveCloserToGoal(currentPosEstimate, finalGoal);
		}
	}
	
	/**
	* drives closer to the goal using our "driving to goal" strategy
	* @param currentPos believed current position and orientation of the robot
	* @param finalGoal believed position of final goal
	* @return true if robot is at goal, false if more moving might be needed
	* read notes in comment inside method
	*/
	private boolean driveCloserToGoal(Waypoint currentPos, Waypoint finalGoal) {
		/*
		"driving to goal" driving strategy detailed in next comment,
		coordinates may be with respect to the marker or the world coordinate frame,
		currentPos and finalGoal are assumed to have some uncertainty,
		this method may only call for the robot to move a certain maximum distance
		to get to the goal assuming that this method will be called again after
		recalculating the estimated currentPos and finalGoal,
		this method returns true if the robot is at the goal or false if this
		method needs to be called again
		*/
		
		double maxDistance = 0.5;
		
		/* algorithm for driving strategy: "driving to goal"
		if at goal
			correctOrientation
			return true
		if finalGoal is <= maxDistance from currentPos
			drive to finalGoal
			correctOrientation
			return true
		calculate a waypoint on path to goal that is <= maxDistance distance away
		drive to that point
		return false
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
			super.currentPosition.getY() + dy, super.currentPosition.getTheta());
		System.out
				.println("pickPointOnWayToGoal is returning " + np.toString());
		return np;
	}

	private int avgXofCorners(int[][] cornerCoords) {
		return (int) (Math
				.round((double) (cornerCoords[0][0] + cornerCoords[1][0]
						+ cornerCoords[2][0] + cornerCoords[3][0])) / 4);
	}
	
	private BufferedImage paintCornersWhite(BufferedImage lonePixelsGone,
			int[][] cornerCoords) {
		int imageWidth = lonePixelsGone.getWidth();
		int imageHeight = lonePixelsGone.getHeight();
		BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_RGB);

		WritableRaster raster = toReturn.getRaster()
		.createCompatibleWritableRaster();

		for (int i = 0; i < 4; i++) {
			int x = cornerCoords[i][0];
			int y = cornerCoords[i][1];
			raster.setSample(x, y, 0, 254);
			raster.setSample(x, y, 1, 254);
			raster.setSample(x, y, 2, 254);
			// System.out.println("Painting corner at X:" + x + ", Y:" + y);
		}
		
		System.out.println("Slope of top line is "
				+ ((double) (cornerCoords[0][1] - cornerCoords[1][1]))
				/ ((double) (cornerCoords[0][0] - cornerCoords[1][0])));
		System.out.println("Slope of bottom line is "
				+ ((double) (cornerCoords[2][1] - cornerCoords[3][1]))
				/ ((double) (cornerCoords[2][0] - cornerCoords[3][0])));
		double a = euclideanDistance(cornerCoords[0][0], cornerCoords[0][1], cornerCoords[1][0], cornerCoords[1][1]);
		double b = euclideanDistance(cornerCoords[1][0], cornerCoords[1][1], cornerCoords[2][0], cornerCoords[2][1]);
		double c = euclideanDistance(cornerCoords[2][0], cornerCoords[2][1], cornerCoords[3][0], cornerCoords[3][1]);
		double d = euclideanDistance(cornerCoords[3][0], cornerCoords[3][1],
				cornerCoords[0][0], cornerCoords[0][1]);
		double s = (a + b + c + d) / 2.0;
		double p = euclideanDistance(cornerCoords[0][0], cornerCoords[0][1],
				cornerCoords[2][0], cornerCoords[2][1]);
		double q = euclideanDistance(cornerCoords[1][0], cornerCoords[1][1],
				cornerCoords[3][0], cornerCoords[3][1]);
		double K = Math.sqrt(4.0 * p * p * q * q
				- ((double) (b * b + d * d - a * a - c * c) * (b * b + d * d
						- a * a - c * c))) / 4.0;
		System.out.println("a" + a + " b" + b + " c" + c + " d" + d + " q" + q
				+ " p" + p);
		System.out.println("Area is " + K);
		
		toReturn.setData(raster);
		return toReturn;
	}

	private void findCornerCoords(BufferedImage mostOrAllNoiseGone,
			int[][] cornerCoords) {
		int imageWidth = mostOrAllNoiseGone.getWidth();
		int imageHeight = mostOrAllNoiseGone.getHeight();

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

	private long euclideanDistance(int i, int j, int x, int y) {
		return Math.round(Math.sqrt((i - x) * (i - x) + (j - y) * (j - y)));
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

	private int howLonelyAmI(BufferedImage imageToCheck, int x, int y) {
		// x and y passed here MUST be 0 > variable > image's max of that
		// dimension
		int numBlankNeighbors = 0;
		for (int suby = y - 1; suby <= y + 1; suby++) {
			for (int subx = x - 1; subx <= x + 1; subx++) {
				int rgbtot = imageToCheck.getRaster().getSample(subx, suby, 0)
				+ imageToCheck.getRaster().getSample(subx, suby, 1)
						+ imageToCheck.getRaster().getSample(subx, suby, 2);
				if (rgbtot == 0)
					numBlankNeighbors++;
			}
		}
		return numBlankNeighbors;
	}

	private BufferedImage reduceNoise(BufferedImage[] rawImage) {
		return average(rawImage);
	}

	/**
	checks if a diagonal line between two opposite corners of a proposed
		tradizoid mostly contains pixels of approximately the same color,
		corner 2 must be right of corner 1 {(c2x >= c1x) must be true}
	@param c1x x coordinate of corner 1
	@param c1y y coordinate of corner 1
	@param c2x x coordinate of corner 2
	@param c2y y coordinate of corner 2
	@param allowedExceptions the number of pixels allowed to be different along
		the line
	@param i the image
	@param threshhold allowed difference in hue to be considered the same color
	@return true if diagonal is mostly the same color as corners, false if c2x==c1x,
		false otherwise
	*/
	public boolean isDiagonalOfTargetColor(int c1x, int c1y, int c2x, int c2y,
			double maxPercentBad, BufferedImage i, int threshhold) {
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

	public void showImage(final Image image) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				ImageIcon icon = new ImageIcon(image);
				/*
				 * JFrame f = new JFrame("image preview"); JPanel p = new
				 * JPanel(); f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				 * f.getContentPane().add(p); f.pack(); f.setVisible(true);
				 */
				JOptionPane.showMessageDialog(null, icon);
			}
		});

		t.start();
	}

	public void showImageAndPauseUntilOkayed(final Image image) {
		ImageIcon icon = new ImageIcon(image);
		JOptionPane.showMessageDialog(null, icon);
	}

}

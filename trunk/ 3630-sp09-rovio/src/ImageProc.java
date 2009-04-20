import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * 
 */

public class ImageProc {


	public static final float[] blur1kernel = { 1, 1, 1, 1, 1, 1, 1, 1, 1 };

	public static final float[] blur2kernel = { 1, 2, 1, 2, 4, 2, 1, 2, 1 };
	/**
	 * Change an HSV color to RGB color. We don't bother converting the alpha as
	 * that stays the same regardless of color space.
	 * 
	 * @param h
	 *            The h component of the color
	 * @param s
	 *            The s component of the color
	 * @param v
	 *            The v component of the color
	 * @param rgb
	 *            An array to return the RGB colour values in code taken from
	 *            http://www.koders.com/java/
	 *            fid698452C6AA108615D4A611B52D27A9F5819B39F5.aspx?s=idef%3Atree
	 */
	public static void hsv2rgb(float h, float s, float v, float[] rgb) {
		// final String INVALID_H_MSG =
		// "Invalid h (it has a value) value when s is zero";
		float r = 0;
		float g = 0;
		float b = 0;

		if (s == 0) {
			// this color in on the black white center line <=> h = UNDEFINED
			if (Float.isNaN(h)) {
				// Achromatic color, there is no hue
				r = v;
				g = v;
				b = v;
			} else {
				// throw new IllegalArgumentException(INVALID_H_MSG);
			}
		} else {
			if (h == 360) {
				// 360 is equiv to 0
				h = 0;
			}

			// h is now in [0,6)
			h = h / 60;

			int i = (int) Math.floor(h);
			float f = h - i; // f is fractional part of h
			float p = v * (1 - s);
			float q = v * (1 - (s * f));
			float t = v * (1 - (s * (1 - f)));

			switch (i) {
			case 0:
				r = v;
				g = t;
				b = p;

				break;

			case 1:
				r = q;
				g = v;
				b = p;

				break;

			case 2:
				r = p;
				g = v;
				b = t;

				break;

			case 3:
				r = p;
				g = q;
				b = v;

				break;

			case 4:
				r = t;
				g = p;
				b = v;

				break;

			case 5:
				r = v;
				g = p;
				b = q;

				break;
			}
		}

		// now assign everything....
		rgb[0] = r;
		rgb[1] = g;
		rgb[2] = b;
	}

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


	public static void showImage(final Image image) {

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
	public static void showImageAndPauseUntilOkayed(final Image image) {
		ImageIcon icon = new ImageIcon(image);
		JOptionPane.showMessageDialog(null, icon);
	}

	public final float[] edgeDetect1kernel = { -5, -5, -5, -5, 39, -5, -5, -5,
			-5 };
	public final float[] edgeDetect2LaplacianKernel = { 0, 1, 0, 1, -4, 1, 0,
			1, 0 };
	public final float[] edgeDetect3LaplacianKernel = { -1, -1, -1, -1, 8, -1,
			-1, -1, -1 };

	public final float[] embossKernel = { -1, -1, 0, -1, 0, 1, 0, 1, 1 };


	public final float[] horizLineDetectKernel = { -1, -2, -1, 0, 0, 0, 1, 2, 1 };

	public final float[] interestPointKernel = { -1, -1, -1, -1, 8, -1, -1, -1,
			-1 };

	public BufferedImage average(BufferedImage[] images) {

		int n = images.length;

		// Assuming that all images have the same dimensions
		int w = images[0].getWidth();
		int h = images[0].getHeight();

		BufferedImage average = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_RGB);

		WritableRaster raster = average.getRaster()
		.createCompatibleWritableRaster();

		Raster[] rasters = new Raster[images.length];
		for (int r = 0; r < images.length; r++) {
			rasters[r] = images[r].getRaster();
		}

		for (int y = 0; y < h; ++y)
			for (int x = 0; x < w; ++x) {

				float rsum = 0.0f;
				float gsum = 0.0f;
				float bsum = 0.0f;

				for (int i = 0; i < n; ++i) {
					rsum = rsum + rasters[i].getSample(x, y, 0);
					gsum = gsum + rasters[i].getSample(x, y, 1);
					bsum = bsum + rasters[i].getSample(x, y, 2);
				}
				raster.setSample(x, y, 0, Math.round(rsum / n));
				raster.setSample(x, y, 1, Math.round(gsum / n));
				raster.setSample(x, y, 2, Math.round(bsum / n));
			}
		average.setData(raster);
		return average;
	}

	private double checkBothDiagonals(Corner topL, Corner topR, Corner botL,
			Corner botR, double desiredCertainty,
			BufferedImage hasAnAllegedTarget, int targetHue, int targetHueWindow) {
		int toReturn = 0;
		if (isDiagonalOfTargetColor(topL, botR, desiredCertainty,
				hasAnAllegedTarget, targetHueWindow, targetHue)) {
			toReturn++;
		}
		if (isDiagonalOfTargetColor(botL, topR, desiredCertainty,
				hasAnAllegedTarget, targetHueWindow, targetHue)) {
			toReturn++;
		}
		return toReturn;
	}

	private int checkBothDiagonals(Target perceivedTarget,
			double desiredCertainty, BufferedImage hasPercievedTargetCorners) {
		int toReturn = 0;
		ColorSpace diagonalCSpace = new ColorSpace();
		int targetHue = diagonalCSpace.getTargetingData(perceivedTarget
				.getTargetColorInt(), 0);
		int targetHueWindow = diagonalCSpace.getTargetingData(perceivedTarget
				.getTargetColorInt(), 1);

		if (isDiagonalOfTargetColor(perceivedTarget.getTopLeft(),
				perceivedTarget.getBottomRight(), desiredCertainty,
				hasPercievedTargetCorners, targetHue, targetHueWindow)) {
			toReturn++;
		}
		if (isDiagonalOfTargetColor(perceivedTarget.getBottomLeft(),
				perceivedTarget.getTopRight(), desiredCertainty,
				hasPercievedTargetCorners, targetHue, targetHueWindow)) {
			toReturn++;
		}
		return toReturn;
	}

	/**
	 * takes in ALREADY-HUE-SEGMENTED bufferedImage, looks at middle "tictactoe"
	 * area, returns int corresponding to highest-scoring hue , 0-4 for rygbv.
	 * 
	 * @param imgWithCenteredTarget
	 * @return
	 */
	public int dominantColorInFocus(BufferedImage imgWithCenteredTarget) {
		final int imageWidth = imgWithCenteredTarget.getWidth();
		final int imageHeight = imgWithCenteredTarget.getHeight();

		Histogram focusHist = new Histogram();
		Corner upperLeft = new Corner((int) Math.round(imageWidth / 3.0), (int)Math.round(imageHeight / 3.0));
		Corner lowerRight = new Corner((int)Math.round(imageWidth * 2.0 / 3.0), (int)Math.round(imageHeight * 2.0 / 3.0));

		histOf(imgWithCenteredTarget, upperLeft, lowerRight, focusHist);

		int toReturn = focusHist.mostDominantColor();
		System.out.println("histOf tells dominantColorInFocus that color "
				+ toReturn + " is dominant");
		return toReturn;
	}

	public int dominantColorInWidescreen(BufferedImage imgToFindDominator) {
		final int imageWidth = imgToFindDominator.getWidth();
		final int imageHeight = imgToFindDominator.getHeight();

		Histogram wideHist = new Histogram();
		Corner upperLeft = new Corner(0, (int) Math.round(imageHeight / 3.0));
		Corner lowerRight = new Corner(imageWidth, (int) Math
				.round(imageHeight * 2.0 / 3.0));

		histOf(imgToFindDominator, upperLeft, lowerRight, wideHist);

		int toReturn = wideHist.mostDominantColor();
		System.out.println("histOf tells dominantColorInWidescreen that color "
				+ toReturn + " is dominant");
		return toReturn;
	}
	
	private long euclideanDistance(int i, int j, int x, int y) {
		return Math.round(Math.sqrt((i - x) * (i - x) + (j - y) * (j - y)));
	}

	/**
	 * compares middle "tic-tac-toe box" of two equally-sized bufferedImages; in
	 * practice, takes absolute value of their differences and looks at that
	 * result's Intensity
	 * 
	 * @param img1
	 * @param img2
	 * @return double that *should* be 0 to 1, untested. Higher #'s mean they're
	 *         same, lower means one's fullblack and other's fullwhite
	 */
	public BufferedImage focusCompare(BufferedImage img1,
			BufferedImage img2) {
		if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()){
			System.out
			.println("WARNING: focusCompare wasn't given equal sized images");
		}
		final int imageWidth = img1.getWidth();
		final int imageHeight = img1.getHeight();

		final Raster rast1 = img1.getRaster();
		final Raster rast2 = img2.getRaster();

		final BufferedImage comparison = new BufferedImage(imageWidth,
				imageHeight, BufferedImage.TYPE_INT_RGB);

		final WritableRaster raster = comparison.getRaster()
		.createCompatibleWritableRaster();

		int r, g, b;
		int[] hsv;
		int satSumSoFar = 0;
		int pixelsRecorded = 0;
		for (int y = (int) Math.round(imageHeight / 4.0); y < imageHeight * 2.0 / 3.0; ++y) {

			for (int x = (int) Math.round(imageWidth * 6.0 / 16.0); x < imageWidth * 10.0 / 16.0; ++x) {
				r = Math.abs(rast1.getSample(x, y, 0)
						- rast2.getSample(x, y, 0));
				g = Math.abs(rast1.getSample(x, y, 1)
						- rast2.getSample(x, y, 1));
				b = Math.abs(rast1.getSample(x, y, 2)
						- rast2.getSample(x, y, 2));
				hsv = new int[3];
				rgb2hsv(r, g, b, hsv);
				raster.setSample(x, y, 0, r);
				raster.setSample(x, y, 1, g);
				raster.setSample(x, y, 2, b);

				if (0 < hsv[1])
				{
					satSumSoFar++;

				}
				pixelsRecorded++;
			}
		}
		System.out.println("focus onethird alike by "
				+ (1.0 - (double) satSumSoFar / (double) pixelsRecorded));
		comparison.setData(raster);
		return comparison;
	}
	private void histOf(BufferedImage imgToHist, Corner upperLeft,
			Corner lowerRight, Histogram hist) {
		System.out.print("Gathering histogram data...");
		final int imageWidth = imgToHist.getWidth();
		ColorSpace ourColorSpace = new ColorSpace();
		Raster rasterToHist = imgToHist.getRaster();
		int r, g, b, h, s;
		int[] hsv;
		int yLesser = (int) upperLeft.getY();
		int yGreater = (int) lowerRight.getY();
		int xLesser = (int) upperLeft.getX();
		int xGreater = (int) lowerRight.getX();

		for (int y = yLesser; y < yGreater; ++y) {

			for (int x = xLesser; x < xGreater; ++x) {
				r = rasterToHist.getSample(x, y, 0);
				g = rasterToHist.getSample(x, y, 1);
				b = rasterToHist.getSample(x, y, 2);
				hsv = new int[3];
				rgb2hsv(r, g, b, hsv);
				h = hsv[0];
				s = hsv[1];
				for (int i = 0; i < 5; i++) {
					if (Math.abs(ourColorSpace.getTargetingData(i, 0) - h) < ourColorSpace
							.getTargetingData(i, 1)
							&& ourColorSpace.getTargetingData(i, 2) < s) {
						hist.incrementFreq(i);
					}
				}
			}
		}
		System.out.println("histogram incrementing complete");
	}

	public void histOf(BufferedImage imgToHist, int third, Histogram hist) {
		int imageWidth = imgToHist.getWidth();
		int yLesser = 0;
		int yGreater = imgToHist.getHeight();
		int xLesser = third * (int) Math.round(imageWidth / 3.0);
		int xGreater = (third + 1) * (int) Math.round(imageWidth / 3.0);
		Corner upperLeft = new Corner(xLesser, yLesser);
		Corner lowerRight = new Corner(xGreater, yGreater);
		histOf(imgToHist, upperLeft, lowerRight, hist);
	}

	private int howLonelyAmI(final BufferedImage imageToCheck, final int x,
			final int y) {
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

	private boolean isBlue(int hue, int sat) {
		ColorSpace ourColorSpace = new ColorSpace();
		if (Math.abs(ourColorSpace.getTargetingData(3, 0) - hue) < ourColorSpace
				.getTargetingData(3, 1)
				&& ourColorSpace.getTargetingData(3, 2) < sat) {
			return true;
		}
		return false;
	}

	private boolean isColorWorthSegmenting(double hue, double sat) {
		ColorSpace segmentHelper = new ColorSpace();
		int targetHueWindow;
		int targetHue;
		int minSatToBeUseful;
		for (int col = 0; col < 5; col++)
		{
			targetHue = segmentHelper.getTargetingData(col, 0);
			targetHueWindow = segmentHelper.getTargetingData(col, 1);
			minSatToBeUseful = segmentHelper.getTargetingData(col, 2);
			if ((targetHueWindow > Math.abs(hue - targetHue))
					&& (minSatToBeUseful < sat)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * checks if a diagonal line between two opposite corners of a proposed
	 * quadrilateral mostly contains pixels of approximately the same color,
	 * corner 2 must be right of corner 1 {(c2x >= c1x) must be true}
	 * 
	 * @param maxPercentBad
	 *            the % of pixels allowed to be different along the line
	 * @param i
	 *            the image
	 * @param threshhold
	 *            allowed difference in hue to be considered the same color
	 * @return true if diagonal is mostly the same color as corners, false if
	 *         c2x==c1x, false otherwise
	 */
	public boolean isDiagonalOfTargetColor(Corner leftCorner,
			Corner rightCorner, double maxPercentBad, BufferedImage i,
			int threshhold, int targetHue) {
		int dx = (int) (rightCorner.getX() - leftCorner.getX());
		int dy = (int) (rightCorner.getY() - leftCorner.getY());
		if (dx == 0) {
			// System.out.print("dx == 0");
			return false;
		}
		if (rightCorner.getX() < leftCorner.getX()) {
			// System.out.print("c2x < c1x");
			return false; // to avoid an infinite loop
		}
		double slope = ((double) dy) / dx;
		int currentX = (int) leftCorner.getX();
		int r, g, b, currentY;
		int[] hsv;
		int numExceptions = 0;
		int pixelsTraversed = 0;
		while (currentX < rightCorner.getX()) {
			currentY = (int) Math
			.round((slope * (currentX - leftCorner.getX()))
					+ leftCorner.getY());
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
		if (currentX == rightCorner.getX()
				&& ((double) numExceptions) / pixelsTraversed <= maxPercentBad) {
			return true;
		}
		return false;
	}

	private boolean isGreen(int hue, int sat) {
		ColorSpace ourColorSpace = new ColorSpace();
		if (Math.abs(ourColorSpace.getTargetingData(2, 0) - hue) < ourColorSpace
				.getTargetingData(2, 1)
				&& ourColorSpace.getTargetingData(2, 2) < sat) {
			return true;
		}
		return false;
	}

	private boolean isPurple(int hue, int sat) {
		ColorSpace ourColorSpace = new ColorSpace();
		if (Math.abs(ourColorSpace.getTargetingData(4, 0) - hue) < ourColorSpace
				.getTargetingData(4, 1)
				&& ourColorSpace.getTargetingData(4, 2) < sat) {
			return true;
		}
		return false;
	}

	private boolean isRed(int hue, int sat)
	{
		ColorSpace ourColorSpace = new ColorSpace();
		if (Math.abs(ourColorSpace.getTargetingData(0, 0) - hue) < ourColorSpace
				.getTargetingData(0, 1)
				&& ourColorSpace.getTargetingData(0, 2) < sat) {
			return true;
		}
		return false;
	}

	private boolean isYellow(int hue, int sat) {
		ColorSpace ourColorSpace = new ColorSpace();
		if (Math.abs(ourColorSpace.getTargetingData(1, 0) - hue) < ourColorSpace
				.getTargetingData(1, 1)
				&& ourColorSpace.getTargetingData(1, 2) < sat) {
			return true;
		}
		return false;
	}

	public BufferedImage makeSingularRawImage(BufferedImage[] fromOpticsBurst) {
		return average(fromOpticsBurst);
	}

	public BufferedImage medianFilterRadius1(BufferedImage rawImage) {
		int imageWidth = rawImage.getWidth();
		int imageHeight = rawImage.getHeight();

		BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_RGB);

		WritableRaster raster = toReturn.getRaster()
		.createCompatibleWritableRaster();

		for (int y = 1; y < imageHeight - 1; ++y) {
			for (int x = 1; x < imageWidth - 1; ++x) {
				int[] rNeighbors = whatDoNineNeighborsLookLike(rawImage, x, y,
						0);
				int[] gNeighbors = whatDoNineNeighborsLookLike(rawImage, x, y,
						1);
				int[] bNeighbors = whatDoNineNeighborsLookLike(rawImage, x, y,
						2);
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

	public BufferedImage medianFilterRadius2(final BufferedImage rawImage) {
		final int imageWidth = rawImage.getWidth();
		final int imageHeight = rawImage.getHeight();

		final BufferedImage toReturn = new BufferedImage(imageWidth,
				imageHeight, BufferedImage.TYPE_INT_RGB);

		final WritableRaster raster = toReturn.getRaster()
		.createCompatibleWritableRaster();

		int[] rNeighbors, gNeighbors, bNeighbors;
		int rMedian, gMedian, bMedian;
		for (int y = 2; y < imageHeight - 2; ++y) {
			for (int x = 2; x < imageWidth - 2; ++x) {
				rNeighbors = whatDoSeventeenNeighborsLookLike(rawImage, x, y, 0);
				gNeighbors = whatDoSeventeenNeighborsLookLike(rawImage, x, y, 1);
				bNeighbors = whatDoSeventeenNeighborsLookLike(rawImage, x, y, 2);
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

	public BufferedImage reduceNoise(BufferedImage singleNoisyImage) {
		System.out.print("Reducing noise...");
		//BufferedImage toReturn = medianFilterRadius2(medianFilterRadius1(singleNoisyImage));
		BufferedImage toReturn = medianFilterRadius1(singleNoisyImage);
		System.out.println("noise reduction complete");
		return toReturn;
	}

	/**
	 * @param img
	 * @param newW
	 * @param newH
	 * @return resized image code taken from
	 *         http://www.javalobby.org/articles/ultimate-image/?source=archives
	 *         not sure if it'll ever get used, but I'm curious about whether it
	 *         could work in case the histogram plan has issues
	 */
	public BufferedImage resize(BufferedImage img, int newW, int newH) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
		Graphics2D g = dimg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
		g.dispose();
		return dimg;
	}

	private BufferedImage segmentOutAHue(BufferedImage noiseReduced,
			int colorOfTargetInFocus) {
		ColorSpace segHelpCSpace = new ColorSpace();
		return segmentOutAHue(noiseReduced, segHelpCSpace.getTargetingData(
				colorOfTargetInFocus, 0), segHelpCSpace.getTargetingData(
						colorOfTargetInFocus, 1), segHelpCSpace.getTargetingData(
				colorOfTargetInFocus, 2));
	}

	public BufferedImage segmentOutAHue(final BufferedImage noiseReduced,
			final int targetHue, final int targetHueWindow,
			final int minSatToBeUseful) {
		final int imageWidth = noiseReduced.getWidth();
		final int imageHeight = noiseReduced.getHeight();

		final BufferedImage toReturn = new BufferedImage(imageWidth,
				imageHeight, BufferedImage.TYPE_INT_RGB);

		final WritableRaster rasterToReturn = toReturn.getRaster()
		.createCompatibleWritableRaster();
		final Raster noiseReducedRaster = noiseReduced.getRaster();

		final double[][] hueArray = new double[imageWidth][imageHeight];
		final double[][] satArray = new double[imageWidth][imageHeight];
		// double maxHueYet = 0; // was intended for normalization

		// int targetPixelsWritten = 0;

		int r, g, b;
		int[] hsv;
		for (int y = 0; y < imageHeight; ++y) {

			for (int x = 0; x < imageWidth; ++x) {
				r = noiseReducedRaster.getSample(x, y, 0);
				g = noiseReducedRaster.getSample(x, y, 1);
				b = noiseReducedRaster.getSample(x, y, 2);
				hsv = new int[3];
				rgb2hsv(r, g, b, hsv);

				hueArray[x][y] = hsv[0];
				satArray[x][y] = hsv[1];
				if (targetHueWindow > Math.abs(hueArray[x][y] - targetHue)
						&& minSatToBeUseful < satArray[x][y]) {
					rasterToReturn.setSample(x, y, 0, r);
					rasterToReturn.setSample(x, y, 1, g);
					rasterToReturn.setSample(x, y, 2, b);
					// targetPixelsWritten++;
				} else {
					rasterToReturn.setSample(x, y, 0, 0);
					rasterToReturn.setSample(x, y, 1, 0);
					rasterToReturn.setSample(x, y, 2, 0);
				}
				// maxHueYet = Math.max(maxHueYet, hueArray[x][y]);
			}
		}
		toReturn.setData(rasterToReturn);
		return toReturn;
	}

	public BufferedImage segmentOutAllHues(
			final BufferedImage noiseReduced) {
		System.out.print("Segmenting all hues...");
		final int imageWidth = noiseReduced.getWidth();
		final int imageHeight = noiseReduced.getHeight();

		final BufferedImage toReturn = new BufferedImage(imageWidth,
				imageHeight, BufferedImage.TYPE_INT_RGB);

		final WritableRaster raster = toReturn.getRaster()
		.createCompatibleWritableRaster();
		final Raster noiseReducedRaster = noiseReduced.getRaster();

		int r, g, b, h, s;
		int[] hsv;
		for (int y = 0; y < imageHeight; ++y) {

			for (int x = 0; x < imageWidth; ++x) {

				r = noiseReducedRaster.getSample(x, y, 0);
				g = noiseReducedRaster.getSample(x, y, 1);
				b = noiseReducedRaster.getSample(x, y, 2);
				hsv = new int[3];
				rgb2hsv(r, g, b, hsv);

				h = hsv[0];
				s = hsv[1];


				if (isColorWorthSegmenting(h, s)) {

					raster.setSample(x, y, 0, r);
					raster.setSample(x, y, 1, g);
					raster.setSample(x, y, 2, b);
				} else {
					raster.setSample(x, y, 0, 0);
					raster.setSample(x, y, 1, 0);
					raster.setSample(x, y, 2, 0);
				}
			}
		}
		toReturn.setData(raster);
		System.out.println("segmenting of all hues complete");
		return toReturn;
	}

	public double targetCenterXvsPhotoCenter(Target target) {
		double targetMidX = target.getCentroid().getX();
		System.out.println("Because target's center is at " + targetMidX
				+ " target is " + (targetMidX - 320) + " pixels from center");
		return targetMidX - 320;
	}
	public Target targetFromAllHueSegmentedImg(BufferedImage allHueSegmented) {
		int seeminglyDesirableColor = dominantColorInFocus(allHueSegmented);
		BufferedImage singleHueSegmented = segmentOutAHue(allHueSegmented,
				seeminglyDesirableColor);
		singleHueSegmented = reduceNoise(singleHueSegmented);
		return targetFromSingleHueSegmentedImg(singleHueSegmented,
				seeminglyDesirableColor);
	}

	public Target targetFromNoiseReducedImage(BufferedImage noiseReduced)
	{
		return targetFromAllHueSegmentedImg(segmentOutAllHues(noiseReduced));
	}

	public Target targetFromRawImg(BufferedImage rawImg) {
		return targetFromNoiseReducedImage(reduceNoise(rawImg));
	}

	/**
	 * method ALREADY EXPECTS bufferedImage passed to it to have been segmented
	 * for a single hue. Method doesn't care which, but it must have been done
	 * already. Color int passed to it is ONLY used in helping create the Target
	 * object returned, it does NOT specify which color the bufferedImage has
	 * been segmented for or which color target this method will "look" for.
	 * 
	 * @param oneTargetInFrame
	 * @param color
	 * @return
	 */
	public Target targetFromSingleHueSegmentedImg(
			final BufferedImage oneTargetInFrame,
			int color) {
		final int imageWidth = oneTargetInFrame.getWidth();
		final int imageHeight = oneTargetInFrame.getHeight();

		// initialize corner coordinates to their opposites
		// the imminent for loop should overwrite them with real answers,
		// but initializing them prevents error it could get from fullblack img
		Corner topLeft = new Corner(imageWidth, imageHeight);
		Corner topRight = new Corner(0, imageHeight);
		Corner bottomLeft = new Corner(imageWidth, 0);
		Corner bottomRight = new Corner(0, 0);

		// initialize "best" scores to worst scores possible for overwriting
		double topLeftScoreBest = topLeft.distance(bottomRight);
		double topRightScoreBest = topLeftScoreBest;
		double bottomLeftScoreBest = topLeftScoreBest;
		double bottomRightScoreBest = topLeftScoreBest;

		for (int y = 1; y < imageHeight - 1; ++y) {
			for (int x = 1; x < imageWidth - 1; ++x) {
				if (howLonelyAmI(oneTargetInFrame, x, y) <= 7) {
					long topLeftScoreHere, topRightScoreHere, bottomLeftScoreHere, bottomRightScoreHere;
					if (topLeftScoreBest > (topLeftScoreHere = euclideanDistance(
							0, 0, x, y))) {
						topLeftScoreBest = topLeftScoreHere;
						topLeft.setLocation(x, y);
					} else if (topRightScoreBest > (topRightScoreHere = euclideanDistance(
							imageWidth, 0, x, y))) {
						topRightScoreBest = topRightScoreHere;
						topRight.setLocation(x, y);
					} else if (bottomLeftScoreBest > (bottomLeftScoreHere = euclideanDistance(
							0, imageHeight, x, y))) {
						bottomLeftScoreBest = bottomLeftScoreHere;
						bottomLeft.setLocation(x, y);
					} else if (bottomRightScoreBest > (bottomRightScoreHere = euclideanDistance(
							imageWidth, imageHeight, x, y))) {
						bottomRightScoreBest = bottomRightScoreHere;
						bottomRight.setLocation(x, y);
					}
				}
			}
		}
		
		Target toReturn = new Target(color, bottomRight, bottomRight,
				bottomRight, bottomRight);
		if (0 < checkBothDiagonals(toReturn, 0.50, oneTargetInFrame)) {
			return toReturn;
		}
		else
		{
			return null;
		}
	}

	private int[] whatDoNineNeighborsLookLike(BufferedImage imageToCheck,
			int x, int y, int channelOfInterest) {
		// x and y passed here MUST be 0 > variable > image's max of that
		// dimension
		// channel of interest is 0 for r, 1 for g, 2 for b
		int[] toReturn = new int[9];
		int indexOfToReturnWeWillWrite = 0;
		Raster rasterToCheck = imageToCheck.getRaster();
		for (int suby = y - 1; suby <= y + 1; suby++) {
			for (int subx = x - 1; subx <= x + 1; subx++) {
				toReturn[indexOfToReturnWeWillWrite] = rasterToCheck.getSample(
						subx, suby, channelOfInterest);
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
		final Raster rasterToCheck = imageToCheck.getRaster();
		toReturn[0] = rasterToCheck.getSample(x - 2, y - 2,
				channelOfInterest);
		toReturn[1] = rasterToCheck.getSample(x, y - 2,
				channelOfInterest);
		toReturn[2] = rasterToCheck.getSample(x + 2, y - 2,
				channelOfInterest);
		toReturn[3] = rasterToCheck.getSample(x - 1, y - 1,
				channelOfInterest);
		toReturn[4] = rasterToCheck.getSample(x, y - 1,
				channelOfInterest);
		toReturn[5] = rasterToCheck.getSample(x + 1, y - 1,
				channelOfInterest);
		toReturn[6] = rasterToCheck.getSample(x - 2, y,
				channelOfInterest);
		toReturn[7] = rasterToCheck.getSample(x - 1, y,
				channelOfInterest);
		toReturn[8] = rasterToCheck.getSample(x, y,
				channelOfInterest);
		toReturn[9] = rasterToCheck.getSample(x + 1, y,
				channelOfInterest);
		toReturn[10] = rasterToCheck.getSample(x + 2, y,
				channelOfInterest);
		toReturn[11] = rasterToCheck.getSample(x - 1, y + 1,
				channelOfInterest);
		toReturn[12] = rasterToCheck.getSample(x, y + 1,
				channelOfInterest);
		toReturn[13] = rasterToCheck.getSample(x + 1, y + 1,
				channelOfInterest);
		toReturn[14] = rasterToCheck.getSample(x - 2, y + 2,
				channelOfInterest);
		toReturn[15] = rasterToCheck.getSample(x, y + 2,
				channelOfInterest);
		toReturn[16] = rasterToCheck.getSample(x + 2, y + 2,
				channelOfInterest);
		return toReturn;
	}

	/**
	 * compares middle-height band of two equally-sized bufferedImages; in
	 * practice, takes absolute value of their differences and looks at that
	 * result's Intensity
	 * 
	 * @param img1
	 * @param img2
	 * @return double that *should* be 0 to 1, untested. Higher #'s mean they're
	 *         same, lower means one's fullblack and other's fullwhite
	 */
	public BufferedImage widescreenCompare(BufferedImage img1,
			BufferedImage img2) {
		if (img1.getWidth() != img2.getWidth()
				|| img1.getHeight() != img2.getHeight()) {
			System.out
			.println("WARNING: widescreenCompare wasn't given equal sized images");
		}
		final int imageWidth = img1.getWidth();
		final int imageHeight = img1.getHeight();
		final Raster rast1 = img1.getRaster();
		final Raster rast2 = img2.getRaster();

		final BufferedImage comparison = new BufferedImage(imageWidth,
				imageHeight, BufferedImage.TYPE_INT_RGB);

		final WritableRaster raster = comparison.getRaster()
		.createCompatibleWritableRaster();

		int r, g, b;
		int[] hsv;
		int satSumSoFar = 0;
		int pixelsRecorded = 0;
		for (int y = (int) Math.round(imageHeight / 3.0); y < imageHeight * 2.0 / 3.0; ++y) {

			for (int x = 0; x < imageWidth; ++x) {
				r = Math.abs(rast1.getSample(x, y, 0)
						- rast2.getSample(x, y, 0));
				g = Math.abs(rast1.getSample(x, y, 1)
						- rast2.getSample(x, y, 1));
				b = Math.abs(rast1.getSample(x, y, 2)
						- rast2.getSample(x, y, 2));
				hsv = new int[3];
				rgb2hsv(r, g, b, hsv);
				raster.setSample(x, y, 0, r);
				raster.setSample(x, y, 1, g);
				raster.setSample(x, y, 2, b);

				if (0 < hsv[1])
				{
					satSumSoFar++;

				}
				pixelsRecorded++;
			}
		}


		System.out.println("midheight alike by "
				+ (1.0 - (double) satSumSoFar
						/ (double) pixelsRecorded));
		comparison.setData(raster);
		return comparison;
	}

}

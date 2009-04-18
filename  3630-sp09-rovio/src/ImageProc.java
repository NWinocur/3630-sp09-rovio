import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * 
 */

public class ImageProc {


	public static final float[] blur1kernel = { 1, 1, 1, 1, 1, 1, 1, 1, 1 };

	public static final float[] blur2kernel = { 1, 2, 1, 2, 4, 2, 1, 2, 1 };
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

	public double CheckBothDiagonals(Corner topL, Corner topR, Corner botL,
			Corner botR, double desiredCertainty,
			BufferedImage hasAnAllegedTarget, int targetHueWindow, int targetHue) {
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
		return medianFilterRadius2(medianFilterRadius1(singleNoisyImage));
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

	/**
	 * compares two equally-sized bufferedImages in practice, takes absolute
	 * value of their differences and looks at that result's Intensity
	 * 
	 * @param img1
	 * @param img2
	 * @return double from 0 to 1. 1 means they're exact same, 0 means one's
	 *         fullblack and other is fullwhite
	 */
	public BufferedImage percentAlike(BufferedImage img1, BufferedImage img2) {
		final int imageWidth = img1.getWidth();
		final int imageHeight = img1.getHeight();
		
		final BufferedImage comparison = new BufferedImage(imageWidth,
				imageHeight, BufferedImage.TYPE_INT_RGB);

		final WritableRaster raster = comparison.getRaster()
				.createCompatibleWritableRaster();
		
		int r, g, b;
		int[] hsv;
		int satSumSoFar = 0;
		int diffSumSoFar = 0;
		int maxSatPossible = 0;
		int pixelsRecorded = 0;
		for (int y = 0; y < imageHeight; ++y) {

			for (int x = 0; x < imageWidth; ++x) {
				r = Math.abs(img1.getRaster().getSample(x, y, 0)
						- img2.getRaster().getSample(x, y, 0));
				g = Math.abs(img1.getRaster().getSample(x, y, 1)
						- img2.getRaster().getSample(x, y, 1));
				b = Math.abs(img1.getRaster().getSample(x, y, 2)
						- img2.getRaster().getSample(x, y, 2));
				hsv = new int[3];
				rgb2hsv(r, g, b, hsv);
				raster.setSample(x, y, 0, r);
				raster.setSample(x, y, 1, g);
				raster.setSample(x, y, 2, b);
				
				diffSumSoFar += r + g + b;
				satSumSoFar += hsv[1];
				maxSatPossible = Math.max(hsv[1], maxSatPossible);
				pixelsRecorded++;
			}
		}
		
		System.out.println("DiffSum = " + diffSumSoFar + "; alike by "
				+ (1.0 - (double) satSumSoFar
						/ (double) (maxSatPossible * pixelsRecorded)));
		comparison.setData(raster);
		return comparison;
	}
	
	public BufferedImage segmentOutAHue(final BufferedImage noiseReduced,
			final int targetHue, final int targetHueWindow,
			final int minSatToBeUseful) {
		final int imageWidth = noiseReduced.getWidth();
		final int imageHeight = noiseReduced.getHeight();

		final BufferedImage toReturn = new BufferedImage(imageWidth,
				imageHeight, BufferedImage.TYPE_INT_RGB);

		final WritableRaster raster = toReturn.getRaster()
		.createCompatibleWritableRaster();

		final double[][] hueArray = new double[imageWidth][imageHeight];
		final double[][] satArray = new double[imageWidth][imageHeight];
		// double maxHueYet = 0; // was intended for normalization

		// int targetPixelsWritten = 0;

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
						&& minSatToBeUseful < satArray[x][y]) {
					raster.setSample(x, y, 0, r);
					raster.setSample(x, y, 1, g);
					raster.setSample(x, y, 2, b);
					// targetPixelsWritten++;
				} else {
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
	
	public BufferedImage segmentOutAllHues(final BufferedImage noiseReduced) {
		final int imageWidth = noiseReduced.getWidth();
		final int imageHeight = noiseReduced.getHeight();

		final BufferedImage toReturn = new BufferedImage(imageWidth,
				imageHeight, BufferedImage.TYPE_INT_RGB);

		final WritableRaster raster = toReturn.getRaster()
				.createCompatibleWritableRaster();

		int r, g, b, h, s;
		int[] hsv;
		for (int y = 0; y < imageHeight; ++y) {

			for (int x = 0; x < imageWidth; ++x) {
				r = noiseReduced.getRaster().getSample(x, y, 0);
				g = noiseReduced.getRaster().getSample(x, y, 1);
				b = noiseReduced.getRaster().getSample(x, y, 2);
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
		return toReturn;
	}

	public double targetCenterXvsPhotoCenter(Target target) {
		double targetMidX = target.getCentroid().getX();
		System.out.println("Because target's center is at " + targetMidX
				+ " target is " + (targetMidX - 320) + " pixels from center");
		return targetMidX - 320;
	}

	private int[] whatDoNineNeighborsLookLike(BufferedImage imageToCheck,
			int x, int y, int channelOfInterest) {
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
	
}

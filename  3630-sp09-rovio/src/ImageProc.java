import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * 
 */

public class ImageProc {

	/**
	 * 
	 */
	public ImageProc() {
		// TODO Auto-generated constructor stub
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
	
	public static final float[] blur1kernel = { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	public static final float[] blur2kernel = { 1, 2, 1, 2, 4, 2, 1, 2, 1 };
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

}

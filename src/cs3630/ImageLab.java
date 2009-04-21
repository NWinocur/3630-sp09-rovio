package cs3630;
import com.jhlabs.image.MedianFilter;

import java.awt.image.*;

public class ImageLab {
	
	public static final int minSaturation = 20;
	public static final int maxSaturation = 100;
	public static final int hueThreshold = 25;
	public static final int RED = 0;
	public static final int BLUE = 200;
	public static final int GREEN = 120;
	
	public static enum Side {
		LEFT,
		RIGHT,
		TOP,
		BOTTOM
	}
	
	public static BufferedImage medianFilter(BufferedImage image) {
		
		BufferedImage filteredImage = null;
		MedianFilter filter = new MedianFilter();
		return filter.filter(image, filteredImage);
		
	}
	
	public static BufferedImage medianFilter(BufferedImage img, int passes) {
		BufferedImage filteredImage = medianFilter(img);
		passes--;
		while(passes > 0) {
			filteredImage = medianFilter(filteredImage);
			passes--;
		}
		return filteredImage;
	}
	
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
	
	public static BufferedImage filterByHue(BufferedImage image, int hue) {
		return filterByHue(image, hue, minSaturation, maxSaturation, hueThreshold);
	}
	
	public static BufferedImage filterByHue(BufferedImage image, int hue, int minSat, int maxSat, int hueWindow) {
		
		final int w = image.getWidth();
		final int h = image.getHeight();

		final BufferedImage filteredImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		final WritableRaster filteredImageRaster = filteredImage.getRaster().createCompatibleWritableRaster();
		final Raster imageRaster = image.getRaster();

		final double[][] hueArray = new double[w][h];
		final double[][] satArray = new double[w][h];

		int r, g, b;
		int[] hsv;
		for (int y = 0; y < h; y++) {

			for (int x = 0; x < w; x++) {
				r = imageRaster.getSample(x, y, 0);
				g = imageRaster.getSample(x, y, 1);
				b = imageRaster.getSample(x, y, 2);
				hsv = new int[3];
				rgb2hsv(r, g, b, hsv);

				hueArray[x][y] = hsv[0];
				satArray[x][y] = hsv[1];
				if (hueWindow > Math.abs(hueArray[x][y] - hue)
						&& minSat < satArray[x][y]  && maxSat > satArray[x][y]) {
//					filteredImageRaster.setSample(x, y, 0, r);
//					filteredImageRaster.setSample(x, y, 1, g);
//					filteredImageRaster.setSample(x, y, 2, b);
					filteredImageRaster.setSample(x, y, 0, 255);
					filteredImageRaster.setSample(x, y, 1, 255);
					filteredImageRaster.setSample(x, y, 2, 255);
				} else {
					filteredImageRaster.setSample(x, y, 0, 0);
					filteredImageRaster.setSample(x, y, 1, 0);
					filteredImageRaster.setSample(x, y, 2, 0);
				}
			}
		}
		filteredImage.setData(filteredImageRaster);
		return filteredImage;
	}

	public static int measureBlackPixelsOnCenterHorizontal(Side side, BufferedImage targetImage) {
		
		int pixelCount = 0;
		final Raster imageRaster = targetImage.getRaster();
		final int w = targetImage.getWidth();
		final int h = targetImage.getHeight();
		
		int r, g, b;
		
		if(side == Side.RIGHT) {
			for (int x = w - 1; x >= 0; x--) {
				r = imageRaster.getSample(x, (h / 2), 0);
				g = imageRaster.getSample(x, (h / 2), 1);
				b = imageRaster.getSample(x, (h / 2), 2);
//				System.out.println("pixel: " + x + ", " + h/2 + " " + r + ", " + g + ", " + b);
				if((r == 0) && (b == 0) && (g == 0))
					pixelCount++;
				else
					break;
			}
		}
		else {
			for (int x = 0; x < w; x++) {
				r = imageRaster.getSample(x, (h / 2), 0);
				g = imageRaster.getSample(x, (h / 2), 1);
				b = imageRaster.getSample(x, (h / 2), 2);
				
				if((r == 0) && (b == 0) && (g == 0))
					pixelCount++;
				else
					break;
			}
		}
		
		return pixelCount;
	}
	
	public static int measureBlackPixelsOnCenterVertical(Side side, BufferedImage targetImage) {
		
		int pixelCount = 0;
		final Raster imageRaster = targetImage.getRaster();
		final int w = targetImage.getWidth();
		final int h = targetImage.getHeight();
		
		int r, g, b;
		
		if(side == Side.BOTTOM) {
			for (int y = h - 1; y >= 0; y--) {
				r = imageRaster.getSample((w / 2), y, 0);
				g = imageRaster.getSample((w / 2), y, 1);
				b = imageRaster.getSample((w / 2), y, 2);
				if((r == 0) && (b == 0) && (g == 0))
					pixelCount++;
				else
					break;
			}
		}
		else {
			for (int y = 0; y < h; y++) {
				r = imageRaster.getSample((w / 2), y, 0);
				g = imageRaster.getSample((w / 2), y, 1);
				b = imageRaster.getSample((w / 2), y, 2);
				if((r == 0) && (b == 0) && (g == 0))
					pixelCount++;
				else
					break;
			}
		}
		
		return pixelCount;
	}

	public static BufferedImage filterByFeatureSize(BufferedImage image, int featSize) {
		
		final int w = image.getWidth();
		final int h = image.getHeight();

		final BufferedImage filteredImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		final WritableRaster filteredImageRaster = filteredImage.getRaster().createCompatibleWritableRaster();
		Raster imageRaster = image.getRaster();

		int r, g, b;
		int pixelCount;
		
		// do rows
		for (int y = 0; y < h; y++) {
			pixelCount = 0;
			for (int x = 0; x < w; x++) {
				r = imageRaster.getSample(x, y, 0);
				g = imageRaster.getSample(x, y, 1);
				b = imageRaster.getSample(x, y, 2);
				
				// if it's not black, count it
				if((r != 0) || (b != 0) || (g != 0)) {
					filteredImageRaster.setSample(x, y, 0, 255);
					filteredImageRaster.setSample(x, y, 1, 255);
					filteredImageRaster.setSample(x, y, 2, 255);
					pixelCount++;
				}
				// else, it's black, and we need to check to see if there are enough
				// previous pixels on the line to keep
				else if(pixelCount > 0) {
					if(pixelCount < featSize) {
						int xi = x - pixelCount;
						for(int i = 0; i < pixelCount; i++) {
							filteredImageRaster.setSample(xi + i, y, 0, 0);
							filteredImageRaster.setSample(xi + i, y, 1, 0);
							filteredImageRaster.setSample(xi + i, y, 2, 0);
						}
					}
					else {
						pixelCount = 0;
						continue;
					}
				}
			}
			if(pixelCount > 0) {
				if(pixelCount < featSize) {
					int xi = w - pixelCount;
					for(int i = 0; i < pixelCount; i++) {
						filteredImageRaster.setSample(xi + i, y, 0, 0);
						filteredImageRaster.setSample(xi + i, y, 1, 0);
						filteredImageRaster.setSample(xi + i, y, 2, 0);
					}
				}
				else {
					pixelCount = 0;
					continue;
				}
			}
		}
		// now do columns
		filteredImage.setData(filteredImageRaster);
		imageRaster = filteredImage.getRaster();
		for (int x = 0; x < w; x++) {
			pixelCount = 0;
			for (int y = 0; y < h; y++) {
				r = imageRaster.getSample(x, y, 0);
				g = imageRaster.getSample(x, y, 1);
				b = imageRaster.getSample(x, y, 2);
				
				if((r != 0) || (b != 0) || (g != 0)) {
					filteredImageRaster.setSample(x, y, 0, 255);
					filteredImageRaster.setSample(x, y, 1, 255);
					filteredImageRaster.setSample(x, y, 2, 255);
					pixelCount++;
				}
				else if(pixelCount > 0) {
					if(pixelCount < featSize) {
						int yi = y - pixelCount;
						for(int i = 0; i < pixelCount; i++) {
							filteredImageRaster.setSample(x, yi + i, 0, 0);
							filteredImageRaster.setSample(x, yi + i, 1, 0);
							filteredImageRaster.setSample(x, yi + i, 2, 0);
						}
					}
					else {
						pixelCount = 0;
						continue;
					}
				}
			}
			if(pixelCount > 0) {
				if(pixelCount < featSize) {
					int yi = h - pixelCount;
					for(int i = 0; i < pixelCount; i++) {
						filteredImageRaster.setSample(x, yi + i, 0, 0);
						filteredImageRaster.setSample(x, yi + i, 1, 0);
						filteredImageRaster.setSample(x, yi + i, 2, 0);
					}
				}
				else {
					pixelCount = 0;
					continue;
				}
			}
		}
		filteredImage.setData(filteredImageRaster);
		return filteredImage;
	}

	public static int countWhitePixels (BufferedImage image) {
		
		int pixelCount = 0;
		final Raster imageRaster = image.getRaster();
		final int w = image.getWidth();
		final int h = image.getHeight();
		
		int r, g, b;
		
		for(int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				r = imageRaster.getSample(x, y, 0);
				g = imageRaster.getSample(x, y, 1);
				b = imageRaster.getSample(x, y, 2);

				if((r != 0) || (b != 0) || (g != 0))
					pixelCount++;
			}
		}
		return pixelCount;
	}
	
	public static float getSlope(BufferedImage image) {
		
		float slope = 0;
		float rise = 0, run = 0;
		
		int pixelCount = 0;
		int leftEdge = 0, rightEdge = 0;
		int leftHeight = 0, rightHeight = 0;
		final Raster imageRaster = image.getRaster();
		final int w = image.getWidth();
		final int h = image.getHeight();
		
		int r, g, b;
		
		for(int x = 0; x < w; x++) {
			pixelCount = 0;
			for(int y = 0; y < h; y++) {
				r = imageRaster.getSample(x, y, 0);
				g = imageRaster.getSample(x, y, 1);
				b = imageRaster.getSample(x, y, 2);
				if((r == 0) && (b == 0) && (g == 0))
					pixelCount++;
				else
					break;
			}
			if(pixelCount < h) {
				leftHeight = pixelCount;
				leftEdge = x;
				break;
			}
		}
		for(int x = (w - 1); x >= 0; x--) {
			pixelCount = 0;
			for(int y = 0; y < h; y++) {
				r = imageRaster.getSample(x, y, 0);
				g = imageRaster.getSample(x, y, 1);
				b = imageRaster.getSample(x, y, 2);
				if((r == 0) && (b == 0) && (g == 0))
					pixelCount++;
				else
					break;
			}
			if(pixelCount < h) {
				rightHeight = pixelCount;
				rightEdge = x;
				break;
			}
		}
		
		if(leftHeight != 0) {
			leftEdge = leftEdge + ((rightEdge - leftEdge) / 2) - 5;

			pixelCount = 0;
			for(int y = 0; y < h; y++) {
				r = imageRaster.getSample(leftEdge, y, 0);
				g = imageRaster.getSample(leftEdge, y, 1);
				b = imageRaster.getSample(leftEdge, y, 2);
				if((r == 0) && (b == 0) && (g == 0))
					pixelCount++;
				else
					break;
			}
			leftHeight = pixelCount;
		}
		
		if(rightHeight != 0) {
			rightEdge = rightEdge - ((rightEdge - leftEdge) / 2) + 5;
			pixelCount = 0;
			for(int y = 0; y < h; y++) {
				r = imageRaster.getSample(rightEdge, y, 0);
				g = imageRaster.getSample(rightEdge, y, 1);
				b = imageRaster.getSample(rightEdge, y, 2);
				if((r == 0) && (b == 0) && (g == 0))
					pixelCount++;
				else
					break;
			}
			rightHeight = pixelCount;
		}
		
		rise = (rightHeight - leftHeight);
		run = (rightEdge - leftEdge);
		
		if(run == 0)
			slope = 0;
		else
			slope = rise / run;
		
		return slope;
	}
}

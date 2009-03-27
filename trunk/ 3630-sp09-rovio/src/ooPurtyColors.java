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
		double maxHueYet = 0;
		int wantHueThisClose = 10;
		int targetHue = 120;
		
		for (int y = 0; y < imageHeight; ++y) {

			for (int x = 0; x < imageWidth; ++x) {
				int r = noiseReduced.getRaster().getSample(x, y, 0);
				int g = noiseReduced.getRaster().getSample(x, y, 1);
				int b = noiseReduced.getRaster().getSample(x, y, 2);
				int[] hsv = new int[3];
				rgb2hsv(r, g, b, hsv);
				
				hueArray[x][y] = hsv[0];
				satArray[x][y] = hsv[1];
				if (wantHueThisClose > Math.abs(hueArray[x][y] - targetHue))
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
				maxHueYet = Math.max(maxHueYet, hueArray[x][y]);
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
		BufferedImage rawImage[] = burstFire(burstLength);
		BufferedImage noiseReduced = reduceNoise(rawImage);
		// showImage(noiseReduced);
		BufferedImage colorFiltered = color_filter(noiseReduced);
		showImageAndPauseUntilOkayed(colorFiltered);
		BufferedImage lonePixelsGone = reduceNoise(colorFiltered);
		lonePixelsGone = reduceNoise(lonePixelsGone);
		lonePixelsGone = reduceNoise(lonePixelsGone);
		lonePixelsGone = reduceNoise(lonePixelsGone);
		lonePixelsGone = reduceNoise(lonePixelsGone);
		lonePixelsGone = reduceNoise(lonePixelsGone);
		lonePixelsGone = reduceNoise(lonePixelsGone);
		lonePixelsGone = reduceNoise(lonePixelsGone);
		lonePixelsGone = reduceNoise(lonePixelsGone);
		lonePixelsGone = reduceNoise(lonePixelsGone);
		lonePixelsGone = reduceNoise(lonePixelsGone);
		showImageAndPauseUntilOkayed(lonePixelsGone);
		
		
		
/*
		 * BufferedImage edgesFoundByConvolving = convolveBuffWithKernel(
		 * colorFiltered, edgeDetect1);
		 * showImageAndPauseUntilOkayed(edgesFoundByConvolving);
		 * edgesFoundByConvolving = convolveBuffWithKernel( colorFiltered,
		 * edgeDetect2Laplacian);
		 * showImageAndPauseUntilOkayed(edgesFoundByConvolving);
		 * edgesFoundByConvolving = convolveBuffWithKernel( colorFiltered,
		 * edgeDetect3Laplacian);
		 * showImageAndPauseUntilOkayed(edgesFoundByConvolving);
		 */
		
		// segment image
		// image description (features)
		// recognition/extraction
		
	}
	
	 private BufferedImage reduceNoise(BufferedImage rawImage) {
		 int imageWidth = rawImage.getWidth();
		 int imageHeight = rawImage.getHeight();
		 
			BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_RGB);

		WritableRaster raster = toReturn.getRaster()
				.createCompatibleWritableRaster();
			
		 for (int y = 1; y < imageHeight - 1; ++y) {
			for (int x = 1; x < imageWidth - 1; ++x) {
				int numBlankNeighbors = 0;
				for (int suby = y - 1; suby <= y + 1; suby++)
				{
					for (int subx = x - 1; subx <= x + 1; subx++)
					{
						int rgbtot = rawImage.getRaster().getSample(subx, suby,
								0)
								+ rawImage.getRaster().getSample(subx, suby, 1)
								+ rawImage.getRaster().getSample(subx, suby, 2);
						if (rgbtot == 0)
							numBlankNeighbors++;
					}
				}
				if (7 <= numBlankNeighbors) {
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

	private BufferedImage reduceNoise(BufferedImage[] rawImage) {
		return average(rawImage);
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

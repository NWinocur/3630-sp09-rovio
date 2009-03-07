import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class ooPurtyColors extends Planner {

	public RovioConstants.CameraResolution cameraResolution;
	public int cameraBrightness;
	public RovioConstants.HeadPosition headPosition;
	public final int burstLength = 4;

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

	/*
	 * use this to actually move, either by one iteration or the entire program
	 * 
	 * @see Planner#makeMove()
	 */
	@Override
	public void makeMove() {
		BufferedImage rawImage[] = burstFire(burstLength);
		BufferedImage noiseReduced = reduceNoise(rawImage);
		showImage(noiseReduced);
		
		// segment image
		// image description (features)
		// recognition/extraction
	}
	
	private BufferedImage[] burstFire(int imagesToTake) {
		BufferedImage imagesToReturn[] = new BufferedImage[imagesToTake];
		for (int n = 0; n < imagesToTake; n++){
			imagesToReturn[n] = super.robot.whatDoISee(cameraResolution);
			RovioAPI.napTime(5);
		}
		return imagesToReturn;
		
	}
	
	private BufferedImage reduceNoise(BufferedImage[] rawImage) {
		return average(rawImage);
	}

	private BufferedImage reduceNoise(BufferedImage rawImage) {
		return rawImage;// making this a do-nothing method for now
	}
	
	 public static BufferedImage average(BufferedImage[] images) {

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

	public void showImage(Image image) {
		ImageIcon icon = new ImageIcon(image);
		/*JFrame f = new JFrame("image preview");
		JPanel p = new JPanel();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(p);
		f.pack();
		f.setVisible(true);*/
		JOptionPane.showMessageDialog(null, icon);
	}

}

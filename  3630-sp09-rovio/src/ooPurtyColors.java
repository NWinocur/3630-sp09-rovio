import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class ooPurtyColors extends Planner {

	public RovioConstants.CameraResolution cameraResolution;
	public int cameraBrightness;
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

	/*
	 * use this to actually move, either by one iteration or the entire program
	 * 
	 * @see Planner#makeMove()
	 */
	@Override
	public void makeMove() {
		BufferedImage rawImage = super.robot.whatDoISee(cameraResolution); // get
																		// image
		showImage(rawImage);
		
		BufferedImage noiseReduced = reduceNoise(rawImage);
		showImage(noiseReduced);
		// segment image
		// image description (features)
		// recognition/extraction
	}
	
	private BufferedImage reduceNoise(BufferedImage rawImage) {
		return rawImage;// making this a do-nothing method for now
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

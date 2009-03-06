import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class ooPurtyColors extends Planner {

	public RovioConstants.CameraResolution cameraResolution;
	public int cameraBrightness;
	public RovioConstants.HeadPosition headPosition;
	
	/* use this to initialize the planner but do not have the robot start moving yet */
	public ooPurtyColors(Robot robot) {
		super(robot);
		cameraResolution = RovioConstants.CameraResolution._320x240;
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
		BufferedImage image = super.robot.whatDoISee(cameraResolution);
		showImage(image);
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

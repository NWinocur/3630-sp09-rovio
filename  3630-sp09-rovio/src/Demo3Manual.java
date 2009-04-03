import java.awt.image.BufferedImage;

public class Demo3Manual extends Planner {
	
	private ColorSpace cspace;
	
	public Demo3Manual(Robot robot) {
		super(robot);
		cspace = new ColorSpace();
	}
	
	public void makeMove() {
		// open GUI
		// wait for commands from GUI
		while (true) {}
	}
	
	/** adds a marker to the map
	* @param color the human selected color of this marker [r, y, g, b, v] */
	public void learnMarker(int color, double x, double y) {
		
	}
	
	/** adds color data used for color calibration
	* @param color number of human selected color of this marker [r, y, g, b, v]
	* @param i the image entirely filled with the color to sample */
	public void learnColor(int color, BufferedImage i) {
		cspace.sampleImage(color, i);
	}
	
	/** call this after all images have been learned, using learnColor */
	public void finishCalibrating() {
		cspace.calibrate();
	}
	
}

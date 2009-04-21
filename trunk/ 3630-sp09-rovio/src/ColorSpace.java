import java.awt.image.BufferedImage;

public class ColorSpace {
	
	/** targetingData: array of [r, y, g, b, v][target, window, minsat] */
	private int[][] targetingData = new int[5][3];
	/** calibrationData: array of [color][hueSum, frequency, minHue, maxHue, minSat] */
	private int[][] calibrationData = new int[5][5];

	/*
	 * public static final int[][] DEFTARGETINGDATA; static { DEFTARGETINGDATA =
	 * new int[][] { {18,15,30}, {60,15,30}, {120,20,22}, {200,50,10},
	 * {275,20,5} }; }
	 */
	
	public ColorSpace() {
		for (int color = 0; color < 5; color++) {
			calibrationData[color][4] = 255;
			calibrationData[color][2] = 355;
		}
		setAllDefaults();
	}
	
	/** use this method to get any piece of color targeting info
	* @param color an int to select a color (0-4) [r, y, g, b, v]
	* @param choice piece of info needed (0-2) [targetHue, window, minSat]
	* @return the piece of requested data */
	public int getTargetingData(int color, int choice) {
		return targetingData[color][choice];
	}
	
	public int[][] getTargetingData() {
		return targetingData;
	}
	
	private void threeInTheAfternoon() {
		targetingData[0][0] = 18;
		targetingData[0][1] = 29;
		targetingData[0][2] = 18;
		targetingData[1][0] = 60;
		targetingData[1][1] = 20;
		targetingData[1][2] = 16;
		targetingData[2][0] = 125;
		targetingData[2][1] = 26;
		targetingData[2][2] = 7;
		targetingData[3][0] = 200;
		targetingData[3][1] = 26;
		targetingData[3][2] = 26;
		targetingData[4][0] = 270;
		targetingData[4][1] = 25;
		targetingData[4][2] = 12;
	}
	
	/** useful for manually settings values */
	public void setAllDefaults() {
		nineInTheAfternoon();
	}

	private void nineInTheAfternoon() {
		targetingData[0][0] = 18;
		targetingData[0][1] = 30;
		targetingData[0][2] = 18;
		targetingData[1][0] = 60;
		targetingData[1][1] = 20;
		targetingData[1][2] = 28;
		targetingData[2][0] = 125;
		targetingData[2][1] = 26;
		targetingData[2][2] = 10;
		targetingData[3][0] = 200;
		targetingData[3][1] = 26;
		targetingData[3][2] = 24;
		targetingData[4][0] = 270;
		targetingData[4][1] = 28;
		targetingData[4][2] = 12;
	}
	
	
	private void samplePixel(int color, int measuredHue, int sat) {
		if (color == 0 && measuredHue > 180) {
			return;
		} else if (color == 4 && measuredHue < 180) {
			return;
		}
		calibrationData[color][0] += measuredHue;
		calibrationData[color][1]++;
		if (measuredHue < calibrationData[color][2]) {
			calibrationData[color][2] = measuredHue;
		}
		if (measuredHue > calibrationData[color][3]) {
			calibrationData[color][3] = measuredHue;
		}
		if (sat < calibrationData[color][4]) {
			calibrationData[color][4] = sat;
		}
	}
	
	public void sampleImage(int color, BufferedImage i) {
		for (int x = 0; x < 640; x++) {
			for (int y = 0; y < 480; y++) {
				int r = i.getRaster().getSample(x, y, 0);
				int g = i.getRaster().getSample(x, y, 1);
				int b = i.getRaster().getSample(x, y, 2);
				int[] hsv = new int[3];
				ImageProc.rgb2hsv(r, g, b, hsv);
				samplePixel(color, hsv[0], hsv[1]);
			}
		}
	}
	
	/** call this after at least one image of each color has been
	sampled by calling sampleImage() */
	public void calibrate() {
		// tempData: [color][minHue, maxHue]
		int[][] tempData = new int[5][2];
		for (int color = 0; color < 5; color++) {
			int hueSum = calibrationData[color][0];
			int frequency = calibrationData[color][1];
			int minHue = calibrationData[color][2];
			int maxHue = calibrationData[color][3];
			int minSat = calibrationData[color][4];
			// use Math.round to be more accurate than integer division
			int mean = (int) Math.round(((double) hueSum) / frequency);
			targetingData[color][0] = mean;
			targetingData[color][2] = minSat;
			tempData[color][0] = minHue;
			tempData[color][1] = maxHue;
		}
		// iterate again over all colors except red and violet
		for (int color = 1; color < 4; color++) {
			int prevColor = color - 1;
			int nextColor = color + 1;
			if (tempData[color][0] < tempData[prevColor][1]) {
				int temp = tempData[color][0];
				tempData[color][0] = tempData[prevColor][1];
				tempData[prevColor][1] = temp;
			}
			if (tempData[color][1] > tempData[nextColor][0]) {
				int temp = tempData[color][1];
				tempData[color][1] = tempData[nextColor][0];
				tempData[nextColor][0] = temp;
			}
		}
		// must iterate over the colors one more time
		for (int color = 0; color < 5; color++) {
			int min = tempData[color][0];
			int max = tempData[color][1];
			int highVariance = max - targetingData[color][0];
			int lowVariance = targetingData[color][0] - min;
			if (lowVariance < highVariance) {
				targetingData[color][1] = lowVariance;
			} else {
				targetingData[color][1] = highVariance;
			}
		}
	}
	
}

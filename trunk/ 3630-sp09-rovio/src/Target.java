import java.awt.Color;
import java.awt.Point;

/**
 * 
 */

public class Target {

	private Corner bottomLeft;
	private Corner bottomRight;
	private Color targetColor;
	private Corner topLeft;
	private Corner topRight;
	


	public Target(Color tColor) {
		targetColor = tColor;
		topLeft = null;
		topRight = null;
		bottomRight = null;
		bottomLeft = null;
	}

	public Target(Color tColor, Corner topL, Corner topR, Corner botL,
			Corner botR) {
		targetColor = tColor;
		topLeft = topL;
		topRight = topR;
		bottomRight = botR;
		bottomLeft = botL;
	}
	
	public Target(int color, Corner topL, Corner topR, Corner botR, Corner botL) {
		switch (color) {
		case 0:
			targetColor = Color.red;
		case 1:
			targetColor = Color.yellow;
		case 2:
			targetColor = Color.green;
		case 3:
			targetColor = Color.blue;
		case 4:
			targetColor = Color.magenta;
		}
		topLeft = topL;
		topRight = topR;
		bottomRight = botR;
		bottomLeft = botL;
	}

	public double getAreaWithRespectToCorners() {
		double a = bottomLeft.distance(bottomRight);
		double b = bottomLeft.distance(topLeft);
		double c = topLeft.distance(topRight);
		double d = topRight.distance(bottomRight);
		double s = (a + b + c + d) / 2.0;

		double p = topLeft.distance(bottomRight);
		double q = bottomLeft.distance(topRight);

		double K = (Math.sqrt(4.0 * p * p * q * q
				- (Math.pow((b * b) + (d * d) - (a * a) - (c * c), 2)))) / 4.0;
		return K;
	}

	public double getBottomEdgeSlope() {
		double toReturn = ((double) (bottomLeft.getY() - bottomRight.getY()))
				/ ((double) (bottomLeft.getX() - bottomRight.getY()));
		toReturn *= -1.0;
		System.out.println("Slope of bottom line is " + toReturn);
		return toReturn;
	}

	/**
	 * @return the bottomLeft
	 */
	public Corner getBottomLeft() {
		return this.bottomLeft;
	}

	/**
	 * @return the bottomRight
	 */
	public Corner getBottomRight() {
		return this.bottomRight;
	}

	public Point getCentroid() {
		int avgXofCorners = (int) (Math
				.round((double) (((topLeft.getX() + bottomLeft.getX()) / 2) + ((topRight
						.getX() + bottomRight.getX()) / 2)) / 2));
		int avgYofCorners = (int) (Math
				.round((double) (((topLeft.getY() + topRight.getY()) / 2) + ((bottomLeft
						.getY() + bottomRight.getY()) / 2)) / 2));
		return new Point(avgXofCorners, avgYofCorners);

	}

	public int getHeight() {
		int avgYofTop2Corners = (int) (Math.round((double)(topLeft.getY() + topRight.getY() )/ 2));
		int avgYofBottom2Corners = (int) (Math.round((double) (bottomLeft
				.getY() + bottomRight.getY()) / 2));
		return avgYofBottom2Corners - avgYofTop2Corners;
	}
	
	/**
	 * @return the targetColor
	 */
	public Color getTargetColor() {
		return this.targetColor;
	}

	public int getTargetColorInt() {
		if (this.targetColor.equals(Color.red))
			return 0;
		else if (this.targetColor.equals(Color.yellow))
			return 1;
		else if (this.targetColor.equals(Color.green))
			return 2;
		else if (this.targetColor.equals(Color.blue))
			return 3;
		else if (this.targetColor.equals(Color.magenta))
			return 4;
		else
			return -1;
	}

	public double getTopEdgeSlope() {
		double toReturn = ((double) (topLeft.getY() - topRight.getY()))
				/ ((double) (topLeft.getX() - topRight.getX()));
		toReturn *= -1;
		System.out.println("Slope of top line is " + toReturn);
		return toReturn;
	}

	/**
	 * @return the topLeft
	 */
	public Corner getTopLeft() {
		return this.topLeft;
	}
	
	/**
	 * @return the topRight
	 */
	public Corner getTopRight() {
		return this.topRight;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TargetColor is color#" + getTargetColorInt();
	}

}

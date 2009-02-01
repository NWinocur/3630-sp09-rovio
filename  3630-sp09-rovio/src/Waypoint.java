public class Waypoint {
	
	private double x;
	private double y;
	private double theta;
	
	public Waypoint(double x, double y, double theta) {
		this.x = x;
		this.y = y;
		this.theta = theta;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getTheta() {
		return theta;
	}
	
	public double distance(Waypoint nextPoint) {
		return distance(nextPoint.x, nextPoint.y);
	}
	
	public double distance(double x, double y) {
		int deltaX = x - this.x;
		int deltaY = y - this.y;
		int c2 = deltaX * deltaX + deltaY + deltaY;
		return Math.sqrt(c2);
	}
	
	public double angleBetween(double nextTheta) {
		return nextTheta - this.theta;
	}
	
}

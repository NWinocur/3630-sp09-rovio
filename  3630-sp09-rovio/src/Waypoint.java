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
		double deltaX = x - this.x;
		double deltaY = y - this.y;
		double c2 = deltaX * deltaX + deltaY + deltaY;
		return Math.sqrt(c2);
	}
	
	public double angleBetween(double nextTheta) {
		return nextTheta - this.theta;
	}
	
	public double getThetaPrime(Waypoint p) {
		double dx = p.getX() - this.x;
		double dy = p.getY() - this.y;
		double thetaPrime = Math.atan2(dy, dx);
		thetaPrime = thetaPrime * (180 / Math.PI);
		thetaPrime = thetaPrime - 90;
		thetaPrime = thetaPrime * (-1);
		return thetaPrime
	}
	
}

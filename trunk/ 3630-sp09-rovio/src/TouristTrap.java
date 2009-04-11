import java.awt.image.BufferedImage;

/**
 * 
 */

public class TouristTrap {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	private Target keyTarget;
	private Waypoint location;
	private TouristTrap nextTouristTrap;
	private TouristTrap prevTouristTrap;

	private BufferedImage[] rawImageArray;

	/**
	 * @param keyTarget
	 * @param location
	 * @param prevTouristTrap
	 */
	public TouristTrap(Target keyTarget, Waypoint location,
			TouristTrap prevTouristTrap) {
		this.keyTarget = keyTarget;
		this.location = location;
		this.prevTouristTrap = prevTouristTrap;
		this.rawImageArray = new BufferedImage[23];
		int arrayInitializer = 0;
		do {
			rawImageArray[arrayInitializer] = null;
			arrayInitializer++;
		} while (arrayInitializer < this.rawImageArray.length);
	}

	/**
	 * @return the keyTarget
	 */
	public Target getKeyTarget() {
		return this.keyTarget;
	}

	/**
	 * @return the location
	 */
	public Waypoint getLocation() {
		return this.location;
	}

	/**
	 * @return the nextTouristTrap
	 */
	public TouristTrap getNextTouristTrap() {
		return this.nextTouristTrap;
	}

	/**
	 * @return the prevTouristTrap
	 */
	public TouristTrap getPrevTouristTrap() {
		return this.prevTouristTrap;
	}

	/**
	 * @return the rawImageArray
	 */
	public BufferedImage[] getRawImageArray() {
		return this.rawImageArray;
	}

	/**
	 * @param nextTouristTrap
	 *            the nextTouristTrap to set
	 */
	public void setNextTouristTrap(TouristTrap nextTouristTrap) {
		this.nextTouristTrap = nextTouristTrap;
	}



	/**
	 * @param rawImageArray
	 *            the rawImageArray to set
	 */
	public void setRawImageArray(BufferedImage[] rawImageArray) {
		this.rawImageArray = rawImageArray;
	}

}

package cs3630;

public class Utils {
	
	public static final int angle45 = 3;
	public static final int angle90 = 7;
	public static final int angle135 = 11;
	public static final int angle180 = 15;
	
	protected static short getShortFromHexString(int offset, String hexString) throws Exception {
		
		// A byte is represented with 2 hex digits, check to see if we have at least that many digits in the string
		if(hexString.length() < 2) {
			throw new Exception("Invalid hex string specified.");
		}
		// Check to see if the offset is valid
		if(offset > (hexString.length() - 2)) {
			throw new Exception("Invalid offset specified.");
		}
		hexString = hexString.substring(offset, offset + 2);
		return Short.parseShort(hexString, 16);
	}
	
	protected static int getIntFromHexString(int offset, String hexString) throws Exception {
		
		// A short is represented with 4 hex digits, check to see if we have at least that many digits in the string
		if(hexString.length() < 4) {
			throw new Exception("Invalid hex string specified.");
		}
		// Check to see if the offset is valid
		if(offset > (hexString.length() - 4)) {
			throw new Exception("Invalid offset specified.");
		}
		hexString = hexString.substring(offset, offset + 4);
		return Integer.parseInt(hexString, 16);
	}
}

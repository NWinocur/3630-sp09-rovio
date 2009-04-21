/**
* stores color frequencies for an image or image segment,
* colors are identified by numbers according to the convention:
* 0=red, 1=yellow, 2=green, 3=blue, 4=violet
*/
public class Histogram {
	
	private int[] freqs;
	
	/** constructor: already called by MapView */
	public Histogram() {
		this.freqs = new int[5];
	}
	
	/** finds the most dominant color
	* @return the number that identifies the most dominant color
	* if there is a tie, the one that comes first is picked,
	* use <= instead of < if you want the last one to be picked in the event
	* of a tie */
	public int mostDominantColor() {
		int max = 0;
		for (int i = 0; i < 5; i++) {
			if (this.getFreq(i) > this.getFreq(max)) {
				max = i;
			}
		}
		return max;
	}
	
	/** gets the frequency of a certain color
	* @param color the number corresponding to the color being requested
	* @return the frequency of that color */
	public int getFreq(int color) {
		return this.freqs[color];
	}
	
	/** gets an array of frequency values */
	public int[] getFreqs() {
		return this.freqs;
	}
	
	/** sets the frequency of color to newFreq */
	public void setFreq(int color, int newFreq) {
		this.freqs[color] = newFreq;
	}
	
	public void incrementFreq(int color) {
		this.setFreq(color, this.getFreq(color) + 1);
	}
	
	public String rawString() {
		String s = "";
		for (int i = 0; i < 4; i++) {
			s = String.format(s + "%d" + "\\", this.freqs[i]);
		}
		s = String.format(s + "%d", this.freqs[4]);
		return s;
	}
	
	public String toString() {
		String s = "histogram: ( ";
		for (int i = 0; i < 4; i++) {
			s = String.format(s + "%d" + ", ", this.freqs[i]);
		}
		s = String.format(s + "%d )", this.freqs[4]);
		return s;
	}
	
}

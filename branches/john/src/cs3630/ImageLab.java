package cs3630;
import com.jhlabs.image.MedianFilter;

import java.awt.image.*;

public class ImageLab {
	
	public static BufferedImage medianFilter(BufferedImage image) {
		
		BufferedImage filteredImage = null;
		MedianFilter filter = new MedianFilter();
		return filter.filter(image, filteredImage);
		
	}
	
	public static void showImage(BufferedImage image) {
		
	}
}

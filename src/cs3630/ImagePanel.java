package cs3630;

import java.awt.Graphics;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class ImagePanel extends Panel {
	private static final long serialVersionUID = 1L;

	private BufferedImage image;

	public ImagePanel(String filename) {
		try {
			image = ImageIO.read(new File(filename));
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public ImagePanel(BufferedImage image) {
		this.image = image;
	}

	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	
	static public void showImage(BufferedImage image, String title) {
		JFrame frame = new JFrame(title);
		Panel panel = new ImagePanel(image);
		frame.getContentPane().add(panel);
		frame.setSize(640, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public void showImage(String title) {
		JFrame frame = new JFrame(title);
		Panel panel = new ImagePanel(this.image);
		frame.getContentPane().add(panel);
		frame.setSize(640, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public BufferedImage getImage() {
		return this.image;
	}

}

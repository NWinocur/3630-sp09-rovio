import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ManualGUI {
	
	/** time in miliseconds to ignore keyboard and button events after an event */
	public static final long INPUT_DELAY = 5;
	
	private Demo3Manual planner;
	private ButtonListener bl;
	private KeyBoardListener kl;
	private boolean isAsleep;
	
	private JPanel centerPanel;
	private JButton mapStopButton;
	private JButton autoModeButton;
	private JLabel status;
	private JButton driveForwardButton;
	private JButton turnLeftButton;
	private JButton turnRightButton;
	
	public ManualGUI(Demo3Manual planner) {
		this.planner = planner;
		this.bl = new ButtonListener();
		this.kl = new KeyBoardListener();
		this.isAsleep = false;
		JPanel panel = this.makeMainPanel();
		JFrame frame = new JFrame("Manual Robot Control");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	/** use this to change the status displayed in the GUI */
	public void setStatus(String newStatus) {
		this.status.setText("status: " + newStatus);
		this.status.paintImmediately(0, 0, status.getSize().width, status.getSize().height);
	}
	
	/** use this to see a GUI mockup */
	public static void main(String[] args) {
		ManualGUI g = new ManualGUI(null);
	}
	
	public JPanel makeMainPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(this.north(), BorderLayout.NORTH);
		panel.add(this.south(), BorderLayout.SOUTH);
		panel.add(this.center(), BorderLayout.CENTER);
		panel.add(this.west(), BorderLayout.WEST);
		return panel;
	}
	
	public JPanel north() {
		JPanel panel = new JPanel();
		this.mapStopButton = new JButton("at tourist trap");
		this.autoModeButton = new JButton("run automatic mode");
		panel.add(this.mapStopButton);
		panel.add(this.autoModeButton);
		this.mapStopButton.setActionCommand("mapStop");
		this.autoModeButton.setActionCommand("autoMode");
		this.mapStopButton.addActionListener(this.bl);
		this.mapStopButton.addKeyListener(this.kl);
		this.autoModeButton.addActionListener(this.bl);
		this.autoModeButton.addKeyListener(this.kl);
		return panel;
	}
	
	public JPanel center() {
		centerPanel = new JPanel();
		centerPanel.setPreferredSize(new Dimension(300, 480));
		this.status = new JLabel("status: waiting for command");
		centerPanel.add(this.status);
		return centerPanel;
	}
	
	public JPanel south() {
		JPanel panel = new JPanel();
		this.driveForwardButton = new JButton("drive forward");
		this.turnLeftButton = new JButton("turn left");
		this.turnRightButton = new JButton("turn right");
		panel.add(this.turnLeftButton);
		panel.add(this.driveForwardButton);
		panel.add(this.turnRightButton);
		this.turnLeftButton.setActionCommand("turnLeft");
		this.driveForwardButton.setActionCommand("driveForward");
		this.turnRightButton.setActionCommand("turnRight");
		this.turnLeftButton.addActionListener(this.bl);
		this.turnLeftButton.addKeyListener(this.kl);
		this.driveForwardButton.addActionListener(this.bl);
		this.driveForwardButton.addKeyListener(this.kl);
		this.turnRightButton.addActionListener(this.bl);
		this.turnRightButton.addKeyListener(this.kl);
		return panel;
	}
	
	public JPanel west() {
		return new ImagePreview();
	}
	
	public class ImagePreview extends JPanel {
		private BufferedImage canvas;
		public ImagePreview() {
			setPreferredSize(new Dimension(640, 480));
			canvas = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D canvas2 = (Graphics2D) g;
			canvas2.drawImage(canvas, null, 0, 0);
		}
		public void setCanvas(BufferedImage bi) {
			canvas = bi;
		}
	}
	
	private class InputDelay extends TimerTask {
		public void run() {
			isAsleep = false;
			setStatus("waiting for command");
		}
	}
	
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (isAsleep == false) {
				isAsleep = true;
				Timer t = new Timer();
				t.schedule(new InputDelay(), INPUT_DELAY);
				System.out.println("button event: " + e.getActionCommand());
				if (e.getActionCommand().equals("driveForward")) {
					setStatus("driving forward");
					planner.driveForward();
				} else if (e.getActionCommand().equals("turnLeft")) {
					setStatus("turning left");
					planner.turnLeft();
				} else if (e.getActionCommand().equals("turnRight")) {
					setStatus("turning right");
					planner.turnRight();
				} else if (e.getActionCommand().equals("mapStop")) {
					setStatus("map stop");
					planner.mapStop();
				} else if (e.getActionCommand().equals("autoMode")) {
					setStatus("automatic mode");
					planner.autoMode();
					// sleep forever (ignore GUI inputs)
					isAsleep = true;
				}
			}
		}
	}
	
	private class KeyBoardListener implements KeyListener {
		public void keyPressed(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {
			if (isAsleep == false) {
				isAsleep = true;
				Timer t = new Timer();
				t.schedule(new InputDelay(), INPUT_DELAY);
				System.out.println(String.format("keyboard event: %d\n", e.getKeyCode()));
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					setStatus("turning left");
					planner.turnLeft();
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					setStatus("turning right");
					planner.turnRight();
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					setStatus("driving forward");
					planner.driveForward();
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					setStatus("map stop");
					planner.mapStop();
				}
			}
		}
	}
	
}

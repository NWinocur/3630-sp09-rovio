import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ManualGUI {
	
	private Demo3Manual planner;
	private ButtonListener bl;
	private KeyBoardListener kl;
	
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
	}
	
	/** use this to see a GUI mockup */
	public static void main(String[] args) {
		ManualGUI g = new ManualGUI(null);
		g.setStatus("testing");
	}
	
	public JPanel makeMainPanel() {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(400, 300));
		panel.setLayout(new BorderLayout());
		panel.add(this.north(), BorderLayout.NORTH);
		panel.add(this.south(), BorderLayout.SOUTH);
		panel.add(this.center(), BorderLayout.CENTER);
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
		JPanel panel = new JPanel();
		this.status = new JLabel("status: ");
		panel.add(this.status);
		return panel;
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
	
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("driveForward")) {
				planner.driveForward();
			} else if (e.getActionCommand().equals("turnLeft")) {
				planner.turnLeft();
			} else if (e.getActionCommand().equals("turnRight")) {
				planner.turnRight();
			} else if (e.getActionCommand().equals("mapStop")) {
				//planner.mapStop();
			} else if (e.getActionCommand().equals("autoMode")) {
				//planner.autoMode();
			}
		}
	}
	
	private class KeyBoardListener implements KeyListener {
		public void keyPressed(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {
			System.out.println(String.format("keyboard event: %d\n", e.getKeyCode()));
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				planner.turnLeft();
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				planner.turnRight();
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				planner.driveForward();
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				//planner.mapStop();
			}
		}
	}
	
}

package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import clientserver.Client;

/**
 * Class for creating the menu to the game which allows the user to select
 * connection port through the use of swing.
 *
 * @author Kyal Bond
 *
 */
public class Menu implements MouseListener, ActionListener {
	private JFrame menuFrame;
	private JLabel menuLabel;
	private JTextField portNum;
	private JTextField address;
	private JTextField playerName;
	private JDialog serverDialog;
	private int imageCount = 0;
	private int portNumber = 4518;
	private boolean change;
	private Timer imageTimer;

	/**
	 * Setup JFrame and then setup components
	 */
	public Menu() {
		menuFrame = new JFrame("Harambe, Second Coming");
		menuFrame.setSize(1300, 930);
		menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuFrame.addMouseListener(this);
		menuFrame.setResizable(false);
		menuFrame.setVisible(true);

		setupGraphics();
	}

	/**
	 * Creates panels and labels for images to be drawn
	 */
	private void setupGraphics() {

		// Setup panel
		menuLabel = new JLabel();
		JPanel menuPanel = new JPanel();
		menuPanel.add(menuLabel);
		menuFrame.add(menuPanel);

		// Setup loop timers
		change = true;
		imageTimer = new Timer(100, this);
		imageTimer.start();
	}

	/**
	 * Loops images to produce small animation while waiting for user to click
	 */
	private void loopImage() {
		// Set current loop image and draw
		menuLabel.setIcon(images[imageCount]);
		menuFrame.revalidate();

		// Increment count depending on direction
		if (change)
			imageCount++;
		else
			imageCount--;
		// Change direction at end of array
		if (imageCount == 4) {
			change = false;
			imageCount = 3;
		} else if (imageCount == -1) {
			change = true;
			imageCount = 0;
		}
	}

	/**
	 * Create JDialog asking for port number and connect button
	 */
	private void selectPort() {
		// Create JDialog and setup options
		serverDialog = new JDialog();
		serverDialog.setTitle("Connection Details");
		serverDialog.setSize(450, 130);
		serverDialog.setLocationRelativeTo(menuFrame);
		serverDialog.setResizable(false);
		serverDialog.setModal(true);

		// Jtextfield for name input
		portNum = new JTextField();
		portNum.setText(portNumber + "");
		portNum.setPreferredSize(new Dimension(140, 30));

		// Jtextfield for name input
		address = new JTextField();
		address.setText("localhost");
		address.setPreferredSize(new Dimension(140, 30));

		// Jtextfield for name input
		playerName = new JTextField();
		playerName.setText("Username");
		playerName.setPreferredSize(new Dimension(140, 30));

		// JButton setup
		JButton connect = new JButton("Connect");
		connect.addActionListener(this);
		connect.setPreferredSize(new Dimension(140, 30));

		// Setup Jpanel
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(playerName);
		panel.add(address);
		panel.add(portNum);
		panel.add(connect);

		serverDialog.add(panel);
		serverDialog.setVisible(true);
	}

	/**
	 * When connect button is pressed, attempts to parse input information, if
	 * successfull creates a client.
	 */
	@SuppressWarnings("unused")
	private void connect() {
		Integer portNum = null;

		try {
			portNum = Integer.parseInt(this.portNum.getText());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(menuFrame, "Enter a port Number.", "Input Error",
					JOptionPane.WARNING_MESSAGE);
		}

		String serverAddress = this.address.getText();
		String username = this.playerName.getText();

		if (portNum != null) {
			Client c = new Client(serverAddress, portNum, username, menuFrame);
		}
	}

	/**
	 * Helper method for loading image icons.
	 *
	 * @param filename - filename of icon
	 * @return ImageIcon - icon of the filename
	 */
	public static ImageIcon makeImageIcon(String filename) {
		BufferedImage myPicture = null;
		try {
			myPicture = ImageIO.read(new File("assets/" + filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ImageIcon(myPicture);
	}

	/**
	 * Show port selecter
	 *
	 * @param arg0 - argument of the mouse listener
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		selectPort();
	}

	/**
	 * When timer goes off for looping image
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() instanceof Timer)
			loopImage();
		else {
			serverDialog.dispose();
			connect();
		}
	}

	/**
	 * Imports menu images for animation
	 */
	public static ImageIcon m1 = makeImageIcon("menu/m1.png");
	public static ImageIcon m2 = makeImageIcon("menu/m2.png");
	public static ImageIcon m3 = makeImageIcon("menu/m3.png");
	public static ImageIcon m4 = makeImageIcon("menu/m4.png");
	public static ImageIcon[] images = { m1, m2, m3, m4 };

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	public static void main(String[] args) {
		new Menu();
	}
}

package gui;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import clientserver.ClientController;
import items.Banana;
import items.Fish;
import items.FishingRod;
import items.FloatingDevice;
import items.Item;
import items.Key;
import items.Teleporter;
import tile.Tile;
import tile.WaterTile;

/**
 * Class for creating the main window handling all input including mouse, keys.
 * Draws the UI and the game window for the renderer.
 *
 * @author Kyal Bond
 *
 */
public class GUI implements KeyListener, ActionListener, MouseListener, MouseMotionListener {
	private ClientController controller;
	private JFrame gameFrame;
	private JPanel UIPanel;
	private JPanel inventorySlots;
	private JLabel gameLabel;
	private JLabel bananaCount;
	private JLabel harambeImage;
	private JPopupMenu popup;

	private ArrayList<JLabel> inventory;
	private Timer harambeTimer;
	private int harambeCount;

	/**
	 * Constructor for initializing the game window, fields and timer
	 *
	 * @param controller - controller of the GUI/Client
	 */
	public GUI(ClientController controller) {
		this.controller = controller;
		this.inventory = new ArrayList<JLabel>();

		// Setup frame
		gameFrame = new JFrame("Harambe, Second Coming");
		gameFrame.setSize(1150, 860);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setResizable(false);
		gameFrame.addKeyListener(this);

		prepareGUI();
		displayInventory();

		gameLabel.addMouseListener(this);
		gameLabel.addMouseMotionListener(this);
		gameFrame.setVisible(true);

		harambeTimer = new Timer(250, this);
		harambeCount = 0;

		playSound();
	}

	/**
	 * Prepares all the components within the frame, creating the renderer
	 * window and the user interface.
	 */
	private void prepareGUI() {
		// Prepare different areas on gui
		JPanel windowPanel = new JPanel(new FlowLayout());
		windowPanel.setBackground(MAINCOLOR);

		// Create window that game image will be displayed on
		gameLabel = new JLabel();
		gameLabel.setPreferredSize(new Dimension(1000, 800));

		// Create menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu gameBar = new JMenu("Game");
		JMenu helpBar = new JMenu("Help");

		// Sets each jmenu components
		createMenuBar(gameBar, helpBar);

		// Add the jmenus to menu bar
		menuBar.add(gameBar);
		menuBar.add(helpBar);
		gameFrame.setJMenuBar(menuBar);

		// Create items window
		UIPanel = new JPanel();
		UIPanel.setPreferredSize(new Dimension(130, 800));
		UIPanel.setBackground(MAINCOLOR);
		UIPanel.setBorder(BorderFactory.createLineBorder(MAINCOLOR2, 3));

		// Setup UI pane
		setupUI();

		// Add components to window panel
		windowPanel.add(gameLabel, BorderLayout.CENTER);
		windowPanel.add(UIPanel, BorderLayout.EAST);

		gameFrame.add(windowPanel);
	}

	/**
	 * Setup the user interface, creating inventory slots, harambe face and
	 * viewing arrows
	 */
	@SuppressWarnings("unchecked")
	private void setupUI() {
		// Setup up name panel
		JPanel namePanel = new JPanel(new FlowLayout());
		namePanel.setPreferredSize(new Dimension(120, 160));
		namePanel.setBackground(MAINCOLOR);

		// Create harambe image label
		harambeImage = new JLabel();
		harambeImage.setIcon(closeImage);

		// Displays username
		JLabel playerName = new JLabel();
		playerName.setText(controller.getName());
		playerName.setForeground(Color.WHITE);
		playerName.setFont(new Font("title", Font.BOLD, 20));

		namePanel.add(harambeImage);
		namePanel.add(playerName);

		// Setup inventory panel
		JPanel inventoryPanel = new JPanel();
		inventoryPanel.setPreferredSize(new Dimension(128, 490));
		inventoryPanel.setBorder(BorderFactory.createLineBorder(MAINCOLOR2, 2));
		inventoryPanel.setBackground(MAINCOLOR);

		// Setup Title
		JLabel inventory = new JLabel();
		inventory.setText("Inventory");
		inventory.setForeground(Color.WHITE);
		inventory.setFont(new Font("title", Font.BOLD, 19));
		Font font = inventory.getFont();
		@SuppressWarnings("rawtypes")
		Map attributes = font.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		inventory.setFont(font.deriveFont(attributes));

		// Setup inventory slots
		inventorySlots = new JPanel(new FlowLayout());
		inventorySlots.setPreferredSize(new Dimension(120, 320));
		inventorySlots.setBackground(MAINCOLOR);
		setupInventorySlots();

		// Add banana count
		JLabel bananaLabel = new JLabel();
		bananaLabel.setText("Bananas");
		bananaLabel.setForeground(Color.WHITE);
		bananaLabel.setFont(font.deriveFont(attributes));

		JLabel bananaImg = new JLabel();
		bananaImg.setIcon(bananaImage);

		// Create banana count
		bananaCount = new JLabel();
		bananaCount.setFont(new Font("title", Font.BOLD, 22));
		bananaCount.setText("x " + controller.getBananaCount());
		bananaCount.setForeground(Color.WHITE);

		inventoryPanel.add(inventory);
		inventoryPanel.add(inventorySlots);
		inventoryPanel.add(bananaLabel);
		inventoryPanel.add(bananaImg);
		inventoryPanel.add(bananaCount);

		// Setup view panel
		JLabel viewLabel = new JLabel();
		viewLabel.setText("View");
		viewLabel.setForeground(Color.WHITE);
		viewLabel.setFont(font.deriveFont(attributes));
		viewLabel.setPreferredSize(new Dimension(56, 40));

		JLabel leftArrowImg = new JLabel();
		leftArrowImg.setIcon(leftArrowImage);
		leftArrowImg.setToolTipText("Rotate view Left");
		leftArrowImg.setName("left");
		leftArrowImg.addMouseListener(this);

		JLabel rightArrowImg = new JLabel();
		rightArrowImg.setIcon(rightArrowImage);
		rightArrowImg.setToolTipText("Rotate view Right");
		rightArrowImg.setName("right");
		rightArrowImg.addMouseListener(this);

		JPanel viewPanel = new JPanel(new FlowLayout());
		viewPanel.setPreferredSize(new Dimension(120, 125));
		viewPanel.setBackground(MAINCOLOR);
		viewPanel.add(viewLabel);
		viewPanel.add(leftArrowImg);
		viewPanel.add(rightArrowImg);

		// Add components to UI panel
		UIPanel.add(namePanel);
		UIPanel.add(inventoryPanel);
		UIPanel.add(viewPanel);
	}

	/**
	 * Creates jmenu bar and fills it the jmenu items.
	 *
	 * @param gameBar
	 * @param helpBar
	 */
	private void createMenuBar(JMenu gameBar, JMenu helpBar) {
		// Game Bar setup
		JMenuItem quit = new JMenuItem("Quit");
		quit.setActionCommand("quit");
		quit.addActionListener(this);
		gameBar.add(quit);

		// Shortcuts setup
		JMenuItem shortcuts = new JMenuItem("Shortcuts");
		shortcuts.setActionCommand("shortcuts");
		shortcuts.addActionListener(this);
		helpBar.add(shortcuts);
	}

	/**
	 * Show shortcut dialog
	 */
	private void showShortCuts() {
		//Create JDialog
		JDialog pop = new JDialog();
		pop.setTitle("Shortcut Keys");
		pop.setSize(300,170);
		pop.setLocationRelativeTo(gameFrame);
		pop.setResizable(false);
		pop.setModal(true);

		//Title for panel
		JLabel title = new JLabel("                 Shortcuts");
		title.setFont(new Font("title", Font.BOLD, 20));

		//Panel setup
		GridLayout g = new GridLayout(6,0);
		g.setVgap(5);
		JPanel p = new JPanel(g);

		//Add components to panel
		p.add(title);
		p.add(new JLabel("              'W,A,S,D' = Move Player"));
		p.add(new JLabel("              'UP Arrow = 180 degree view change"));
		p.add(new JLabel("              'RIGHT Arrow' = 90 degree view change"));
		p.add(new JLabel("              'LEFT Arrow' = -90 degree view change"));

		//Add panel to dialog and show, dispose when user closes window
		pop.add(p);
		pop.setVisible(true);
		pop.dispose();
	}

	/**
	 * Show inventory items in inventory slots
	 */
	private void displayInventory() {
		clearInventory();
		ArrayList<Item> items = controller.getInventory();
		if (items != null) {
			for (Item i : items) {
				addItem(i);
			}
		}
	}

	/**
	 * Add item to a free inventory slot
	 *
	 * @param i
	 */
	private void addItem(Item i) {
		for (JLabel j : inventory) {
			if (j.getToolTipText() == null) {
				j.setToolTipText(i.getName() + ": " + i.getDescription());
				j.setIcon(getInventoryImage(i));
				return;
			}
		}
	}

	/**
	 * Clear inventory setting them to null (free)
	 */
	private void clearInventory() {
		for (JLabel j : inventory) {
			j.setToolTipText(null);
			j.setIcon(null);
		}
	}

	/**
	 * Setup the inventory slots add mouse adapters to them for popups
	 */
	private void setupInventorySlots() {
		int i = 0;
		while (i < 10) {
			JLabel slot = new JLabel();
			slot.setBorder(BorderFactory.createLineBorder(new Color(255, 182, 0), 4));
			slot.setPreferredSize(new Dimension(50, 50));
			slot.setName(i + "");

			// Add listener for popup
			slot.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						JLabel src = (JLabel) e.getSource();
						createPopupUI(e.getX(), e.getY(), Integer.parseInt(src.getName()));

					}
				}
			});

			inventorySlots.add(slot);
			inventory.add(slot);

			// Filler label
			JLabel slot2 = new JLabel();
			slot2.setPreferredSize(new Dimension(100, 5));
			slot2.setBackground(MAINCOLOR);
			if (i % 2 == 1)
				inventorySlots.add(slot2);
			i++;
		}
	}

	/**
	 * Create popup for inventory items and position
	 *
	 * @param x
	 * @param y
	 * @param parseInt
	 */
	private void createPopupUI(int x, int y, int parseInt) {
		JLabel label = inventory.get(parseInt);
		if (label.getToolTipText() != null) {

			Item i = controller.getInventoryItem(parseInt);
			popup = new JPopupMenu("tile");
			String desc;

			// Get key code if key and add to description
			if (i instanceof Key) {
				Key k = (Key) i;
				desc = k.getDescription() + " Code: " + k.getCode();
			} else {
				desc = i.getDescription();
			}

			// Create examine option
			JMenuItem examineObject = new JMenuItem("Examine");
			examineObject.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					controller.showMessage(desc);
				}
			});
			popup.add(examineObject);

			// If a usable item add an option
			if (i.isUsable()) {

				JMenuItem useObject;

				// Check if it is a floating device and add option
				if (i instanceof FloatingDevice) {

					if (controller.getPlayer().getHasFloatingDevice()) {
						useObject = new JMenuItem("Unequip");
					} else {
						useObject = new JMenuItem("Equip");
					}
				} else {
					useObject = new JMenuItem("Use");
				}
				useObject.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						controller.useItem(parseInt);
					}
				});

				// Check the user isn't in water
				if (!(i instanceof FloatingDevice && controller.getPlayer().getTile() instanceof WaterTile)) {
					popup.add(useObject);
				}
			}

			// If a banana, add option to siphon
			if (i instanceof Banana) {
				JMenuItem siphonObject = new JMenuItem("Siphon");
				siphonObject.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						controller.siphonBananaPlayer(parseInt);
					}
				});
				popup.add(siphonObject);
			}

			// Add drop option
			JMenuItem dropObject = new JMenuItem("Drop");
			dropObject.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					controller.dropItemPlayer(parseInt);
				}
			});

			// Only add drop if floating device
			if (!(i instanceof FloatingDevice && controller.getPlayer().getHasFloatingDevice())) {
				popup.add(dropObject);
			}
			popup.show(label, x, y);
		}
	}

	/**
	 * Create popup in game renderer window at position
	 *
	 * @param x
	 * @param y
	 */
	private void createPopupGame(int x, int y) {
		if (x < 1020) {
			popup = new JPopupMenu("tile");
			Tile t = controller.getTile(x, y);

			// Check tile at position
			if (t != null) {

				// Check if there is a game object and add
				if (t.getGameObject() != null) {
					JMenuItem examineObject = new JMenuItem("Examine " + t.getGameObject().getClass().getSimpleName());
					examineObject.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							controller.showMessage(t.getGameObject().getDescription());
						}
					});
					popup.add(examineObject);
				}

				JMenuItem examineItem = new JMenuItem("Examine ground");
				examineItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						controller.showMessage("Just a " + t.getClass().getSimpleName() + " here");
					}

				});
				popup.add(examineItem);

				// Add move to option
				if (t.getGameObject() == null) {
					popup.addSeparator();
					JMenuItem move = new JMenuItem("Move");
					move.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							checkClicked(x, y);
						}

					});
					popup.add(move);
				}
			}
		}
	}

	/**
	 * Get image of an item
	 *
	 * @param i - item
	 * @return - image icon for the item passed in
	 */
	public ImageIcon getInventoryImage(Item i) {
		if (i instanceof items.Key) {
			return keyInventoryImage;
		}
		if (i instanceof FloatingDevice) {
			return floatingDeviceInventoryImage;
		}
		if (i instanceof Banana) {
			return bananaInventoryImage;
		}
		if (i instanceof Teleporter) {
			return teleporterInventoryImage;
		}
		if (i instanceof Fish) {
			return fishInventoryImage;
		}
		if (i instanceof FishingRod) {
			return fishingRodInventoryImage;
		}
		return null;
	}

	/**
	 * Begin talking animation
	 */
	public void beginHarambeAnimation() {
		harambeTimer.start();
	}

	/**
	 * Change the image depending on count
	 */
	public void changeHarambeImage() {
		harambeCount++;
		int temp = harambeCount % 2;
		if (temp == 1)
			harambeImage.setIcon(openImage);
		else
			harambeImage.setIcon(closeImage);

		if (harambeCount == 16)
			endHarambeAnimation();
	}

	/**
	 * End talking animation
	 */
	public void endHarambeAnimation() {
		harambeTimer.stop();
		harambeImage.setIcon(closeImage);
		harambeCount = 0;
	}

	/**
	 * Update board displayed
	 *
	 * @param i - Buffered image of board
	 */
	public void showBoard(BufferedImage i) {
		gameLabel.setIcon(new ImageIcon(i));
		bananaCount.setText("x " + controller.getBananaCount());
		displayInventory();
	}

	/**
	 * Dispose of frame
	 */
	public void hideGUI() {
		gameFrame.dispose();
	}

	/**
	 * Moving to tile using dijkstras
	 *
	 * @param x
	 * @param y
	 */
	private void checkClicked(int x, int y) {
		if (x < 1000) {
			controller.moveWithUltimateDijkstras(x, y);
		}

	}

	/**
	 * Highlight tile that user is currently hovered over
	 *
	 * @param x
	 * @param y
	 */
	private void checkMoved(int x, int y) {
		if (x < 1000) {
			controller.selectTile(x, y);
		}
	}

	/**
	 * Shortcut keys setup
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		String key = KeyEvent.getKeyText(e.getKeyCode());
		if (key.equals("Left"))
			controller.rotateLeft();
		else if (key.equals("Right"))
			controller.rotateRight();
		else if (key.equals("Up")) {
			controller.rotateRight();
			controller.rotateRight();
		} else if (key.equals("W"))
			controller.moveSinglePos("N");
		else if (key.equals("A"))
			controller.moveSinglePos("W");
		else if (key.equals("S"))
			controller.moveSinglePos("S");
		else if (key.equals("D"))
			controller.moveSinglePos("E");
	}

	/**
	 * Action listener for quiting from jmenuitem and changing talking animation
	 * image
	 */
	@Override
	public void actionPerformed(ActionEvent action) {
		if ("quit".equals(action.getActionCommand())) {
			System.exit(0);
		}else if("shortcuts".equals(action.getActionCommand())){
			showShortCuts();
		}else {
			changeHarambeImage();
		}
	}

	/**
	 * Checking if popup or movement command
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() != gameLabel) {
			JLabel src = (JLabel) e.getSource();
			if (src.getName().equals("left"))
				controller.rotateLeft();
			else
				controller.rotateRight();
		} else {
			if (SwingUtilities.isRightMouseButton(e)
					|| (e.getX() >= 1025 && e.getX() <= 1135 && e.getY() >= 270 && e.getY() <= 585)) {
				createPopupGame(e.getX(), e.getY());
				if (popup != null) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			} else {
				checkClicked(e.getX(), e.getY());
			}
		}
	}

	/**
	 * For highlighting tile the user is currently hovering over
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		checkMoved(e.getX(), e.getY());
	}

	/**
	 * Plays sound while ingame
	 */
	public static synchronized void playSound() {
		try {
			File file = new File("assets/audio/audio.wav");
			Clip clip = AudioSystem.getClip();
			AudioInputStream Audio = AudioSystem.getAudioInputStream(file);
			clip.open(Audio);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Import images for frame
	 */
	public static ImageIcon keyInventoryImage = Menu.makeImageIcon("gui/inventory/key.png");
	public static ImageIcon floatingDeviceInventoryImage = Menu.makeImageIcon("gui/inventory/floatingDevice.png");
	public static ImageIcon bananaInventoryImage = Menu.makeImageIcon("gui/inventory/banana.png");
	public static ImageIcon teleporterInventoryImage = Menu.makeImageIcon("gui/inventory/teleporter.png");
	public static ImageIcon fishInventoryImage = Menu.makeImageIcon("gui/inventory/fish.png");
	public static ImageIcon fishingRodInventoryImage = Menu.makeImageIcon("gui/inventory/fishingRod.png");

	public static ImageIcon leftArrowImage = Menu.makeImageIcon("gui/leftArrow.png");
	public static ImageIcon rightArrowImage = Menu.makeImageIcon("gui/rightArrow.png");
	public static ImageIcon closeImage = Menu.makeImageIcon("gui/closeMouth.png");
	public static ImageIcon openImage = Menu.makeImageIcon("gui/openMouth.png");
	public static ImageIcon bananaImage = Menu.makeImageIcon("gui/banaga.png");

	/**
	 * Main colors used in frame
	 */
	public static final Color MAINCOLOR = new Color(5, 26, 37);
	public static final Color SECONDARYCOLOR = new Color(255, 182, 0);
	public static final Color MAINCOLOR2 = new Color(2, 13, 18);

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

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

	@Override
	public void mouseDragged(MouseEvent e) {
	}
}

package worldeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import iohandling.BoardWriter;

public class ToolSelectionFrame {

	WorldEditor editor;
	JComboBox toolCombo;
	String[] tools = { "Set Floor Type", "Add Game Object" };
	String[] floorTypes = { "grass", "water", "stone", "sand", "wood" };
	String[] gameObjects = { "tree", "fence", "chest", "wall", "key", "floaty", "banana", "building", "door",
			"teleporter", "NPC", "fish", "fishingrod" };

	/**
	 * Constructor for ToolSelectionFrame sets up tool frame for selecting tools
	 * and components.
	 *
	 * @param editor - the world editor object
	 */
	public ToolSelectionFrame(WorldEditor editor) {
		this.editor = editor;
		setupFloorPanel();
	}

	/**
	 * Setup floor panel to allow selection of floor type and allow changing to
	 * game object panel.
	 */
	private void setupFloorPanel() {
		JFrame floorFrame = new JFrame();
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(400, 140));
		panel.add(new JLabel("Select Tool:"));
		JRadioButton setFloorButton = new JRadioButton("Set Floor Type");
		setFloorButton.setSelected(true);
		setFloorButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.setTool("Set Floor Type");
			}

		});
		JRadioButton addObjectButton = new JRadioButton("Add Game Object");
		addObjectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.setTool("Add Game Object");
				floorFrame.dispose();
				setupObjectPanel();
			}

		});
		ButtonGroup bg = new ButtonGroup();
		bg.add(setFloorButton);
		bg.add(addObjectButton);
		panel.add(setFloorButton);
		panel.add(addObjectButton);
		panel.add(Box.createRigidArea(new Dimension(400, 10)));
		panel.add(new JLabel("Select Floor Type:"));
		JComboBox floorCombo = new JComboBox(floorTypes);
		floorCombo.setPreferredSize(new Dimension(150, 24));
		floorCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.setFloorType(floorTypes[floorCombo.getSelectedIndex()]);
			}

		});
		panel.add(floorCombo);
		panel.add(Box.createRigidArea(new Dimension(400, 10)));
		JButton button = new JButton("Save Map");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BoardWriter.writeBoard(editor.board, "map-new.txt");
			}
		});
		panel.add(button);

		panel.repaint();
		floorFrame.add(panel);
		floorFrame.setAlwaysOnTop(true);
		floorFrame.setLocation(1100, 0);
		floorFrame.pack();
		floorFrame.setVisible(true);
		floorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		editor.setFloorType(floorTypes[floorCombo.getSelectedIndex()]);
		editor.setTool("Set Floor Type");
	}

	/**
	 * Setup object panel to select game object type and give option to select
	 * floor selection panel.
	 */
	private void setupObjectPanel() {
		JFrame objectFrame = new JFrame();
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(400, 140));
		panel.add(new JLabel("Select Tool:"));
		JRadioButton setFloorButton = new JRadioButton("Set Floor Type");
		setFloorButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.setTool("Set Floor Type");
				objectFrame.dispose();
				setupFloorPanel();
			}

		});
		JRadioButton addObjectButton = new JRadioButton("Add Game Object");
		addObjectButton.setSelected(true);
		addObjectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.setTool("Add Game Object");
			}

		});
		ButtonGroup bg = new ButtonGroup();
		bg.add(setFloorButton);
		bg.add(addObjectButton);
		panel.add(setFloorButton);
		panel.add(addObjectButton);
		panel.add(Box.createRigidArea(new Dimension(400, 10)));
		panel.add(new JLabel("Select Object Type:"));
		JComboBox objectCombo = new JComboBox(gameObjects);
		objectCombo.setPreferredSize(new Dimension(150, 24));
		objectCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.setObjectType(gameObjects[objectCombo.getSelectedIndex()]);
			}
		});
		panel.add(objectCombo);
		panel.add(Box.createRigidArea(new Dimension(400, 10)));

		JButton button = new JButton("Save Map");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BoardWriter.writeBoard(editor.board, "map-new.txt");
			}
		});
		panel.add(button);

		panel.repaint();
		objectFrame.add(panel);
		objectFrame.setAlwaysOnTop(true);
		objectFrame.setLocation(1100, 0);
		objectFrame.pack();
		objectFrame.setVisible(true);
		objectFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		editor.setObjectType(gameObjects[objectCombo.getSelectedIndex()]);
	}

}

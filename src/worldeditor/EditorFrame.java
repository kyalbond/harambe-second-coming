package worldeditor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import core.GameSystem;
import core.Location;
import tile.Tile;
import util.Position;

/**
 * EditorFrame gives a graphical display for the world editor by displaying the
 * rendered image.
 *
 * @author carrjona
 *
 */
public class EditorFrame extends JFrame {

	JPanel panel;
	BufferedImage image;
	WorldEditor editor;

	/**
	 * Constructor for EditorFrame sets up basic graphical components and
	 * listeners.
	 *
	 * @param editor - the world editor object
	 */
	public EditorFrame(WorldEditor editor) {
		this.editor = editor;
		panel = new JPanel() {
			public void paint(Graphics g) {
				paintPanel((Graphics2D) g);
			}
		};
		panel.setPreferredSize(new Dimension(1000, 800));
		panel.addMouseListener(new EditorMouseListener());
		panel.addMouseMotionListener(new EditorMouseMotionListener());
		addKeyListener(new EditorKeyListener());
		add(panel);
		pack();
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Paints rendered image onto panel
	 *
	 * @param g - Graphics object
	 */
	public void paintPanel(Graphics2D g) {
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
	}

	/**
	 * Set image to new rendered image
	 *
	 * @param image - image to be drawn
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
		panel.repaint();
	}

	/**
	 * Keylistener for EditorFrame allows pressing 'H' to return to the origin
	 * square.
	 *
	 * @author carrjona
	 *
	 */
	private class EditorKeyListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent arg0) {

		}

		/**
		 * If press h, reset view to home location
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyChar() == 'h') {
				editor.resetView();
			}
		}

		@Override
		public void keyTyped(KeyEvent arg0) {

		}
	}

	/**
	 * EditorMouseMotionistener sets up a mouse motion listener used to handle
	 * dragging to fill tiles and game objects, as well as setting tiles to be
	 * highlighted when hovering mouse over them.
	 *
	 * @author carrjona
	 *
	 */
	private class EditorMouseMotionListener implements MouseMotionListener {

		/**
		 * process the tile being dragged over.
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			Location loc = editor.board.getLocationById(editor.currentLocation);
			Position selected = editor.renderer.isoToIndex(e.getX(), e.getY());
			editor.selectTile(selected);
			if (e.getButton() == 1 || e.getButton() == 0) {
				editor.processTile(selected.getX(), selected.getY());
			} else if (e.getButton() == 3) {
				editor.clearTile(selected.getX(), selected.getY());
			}
		}

		/**
		 * Selecting tiles and locations moved over
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			Location loc = editor.board.getLocationById(editor.currentLocation);
			Position selected = editor.renderer.isoToIndex(e.getX(), e.getY());
			Tile tile = editor.renderer.getTileAtPos(selected, loc);
			editor.selectTile(selected);
			editor.selectLocation(null);
			if (tile == null) {
				editor.selectTile(null);
				if (selected.getX() >= 0 && selected.getX() < loc.getTiles().length) {
					if (selected.getY() < 0) {
						editor.selectLocation(GameSystem.Direction.NORTH);
					}

					if (selected.getY() > loc.getTiles()[0].length) {
						editor.selectLocation(GameSystem.Direction.SOUTH);
					}
				}
				if (selected.getY() >= 0 && selected.getY() < loc.getTiles()[0].length) {
					if (selected.getX() < 0) {
						editor.selectLocation(GameSystem.Direction.WEST);
					}

					if (selected.getX() > loc.getTiles().length) {
						editor.selectLocation(GameSystem.Direction.EAST);
					}
				}
			}
		}
	}

	/**
	 * EditorMouseListener handles the processing of tiles being clicked on
	 * @author carrjona
	 *
	 */
	private class EditorMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		/**
		 * Process the tile being clicked on
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			Location loc = editor.board.getLocationById(editor.currentLocation);
			Position selected = editor.renderer.isoToIndex(e.getX(), e.getY());
			if (editor.renderer.getTileAtPos(new Position(selected.getX(), selected.getY()), loc) == null) {
				if (selected.getX() >= 0 && selected.getX() < loc.getTiles().length) {
					if (selected.getY() < 0) {
						editor.clickLocation(GameSystem.Direction.NORTH);
					}
					if (selected.getY() > loc.getTiles()[0].length) {
						editor.clickLocation(GameSystem.Direction.SOUTH);
					}
				}
				if (selected.getY() >= 0 && selected.getY() < loc.getTiles()[0].length) {
					if (selected.getX() < 0) {
						editor.clickLocation(GameSystem.Direction.WEST);
					}
					if (selected.getX() > loc.getTiles().length) {
						editor.clickLocation(GameSystem.Direction.EAST);
					}
				}
			}
			editor.selectTile(selected);
			if (e.getButton() == 1) {
				editor.processTile(selected.getX(), selected.getY());
			} else if (e.getButton() == 3) {
				editor.clearTile(selected.getX(), selected.getY());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

	}
}

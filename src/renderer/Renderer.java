package renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import core.Board;
import core.GameSystem.Direction;
import core.Location;
import gameobjects.Player;
import tile.DoorOutTile;
import tile.Tile;
import util.Position;

/**
 * Renderer object takes in a board and play and returns an image to be
 * displayed by the aplpication window.
 *
 * @author Jonathan Carr
 *
 */
public class Renderer {

	// Tile to be selected
	private Tile selected;
	// Indices of selected tile
	Point selectedPoint = null;

	// Location to be selected
	Direction selectedLocation = null;

	// Renderer images
	BufferedImage highlightTile;
	BufferedImage highlightLocation;
	BufferedImage playerSelect;
	BufferedImage speechBubble;

	// Direction to be viewed from
	public Direction viewingDir = Direction.NORTH;

	final int TILE_WIDTH = 45;

	// Offsets for drawing centered board
	int xOffset;
	int yOffset;

	// Message to be delivered in Harambe's speech bubble.
	String message = "";
	// Message will be shown until this time is reached.
	int messageTimer = -1;

	// Seconds in one day night cycle.
	int dayCycle = 180;

	// Map of String (filenames of images) to the images themselves.
	Map<String, BufferedImage> images;

	/**
	 * Constructor of renderer loads renderer images.
	 */
	public Renderer() {
		images = new HashMap<String, BufferedImage>();
		try {
			highlightTile = ImageIO.read(new File("assets/renderer/highlightTile.png"));
			highlightLocation = ImageIO.read(new File("assets/renderer/highlightLocation.png"));
			playerSelect = ImageIO.read(new File("assets/renderer/playerSelect.png"));
			speechBubble = ImageIO.read(new File("assets/renderer/speechBubble.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set message to be delivered by Harambe in speech bubble.
	 *
	 * @param msg
	 *            - message to be sent
	 * @param currentTime
	 *            - currentTime
	 * @param duration
	 *            - duration to last for
	 */
	public void setMessage(String msg, int currentTime, int duration) {
		message = msg;
		messageTimer = currentTime + duration;
	}

	/**
	 * Paint the baord based on the perspective of the player and return the
	 * rendered image.
	 *
	 * @param board
	 *            - board
	 * @param player
	 *            - player based on
	 * @param w
	 *            - width of board
	 * @param h
	 *            - height of board
	 * @param time
	 *            - curent time
	 * @return rendered board image
	 */
	public BufferedImage paintBoard(Board board, Player player, int w, int h, int time) {
		selectedPoint = null;
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(new Color(5, 26, 37));
		g.fillRect(0, 0, image.getWidth(), image.getHeight());

		Location loc = player.getLocation();
		Map<Point, Integer> map = board.mapLocations(loc.getId(), 0, 0, new HashMap<Point, Integer>());

		// Determine order to draw neighbouring locations
		int[] drawOrderX = null;
		int[] drawOrderY = null;
		switch (viewingDir) {
		case NORTH:
			int[] tempX1 = { -1, -1, 0, -1, 0, 1, 0, 1, 1 };
			int[] tempY1 = { 1, 0, 1, -1, 0, 1, -1, 0, -1 };
			drawOrderX = tempX1;
			drawOrderY = tempY1;
			break;
		case WEST:
			int[] tempX2 = { -1, 0, -1, 1, 0, -1, 1, 0, 1 };
			int[] tempY2 = { -1, -1, 0, -1, 0, 1, 0, 1, 1 };
			drawOrderX = tempX2;
			drawOrderY = tempY2;
			break;
		case SOUTH:
			int[] tempX3 = { 1, 1, 0, 1, 0, -1, 0, -1, -1 };
			int[] tempY3 = { -1, 0, -1, 1, 0, -1, 1, 0, 1 };
			drawOrderX = tempX3;
			drawOrderY = tempY3;
			break;
		case EAST:
			int[] tempX4 = { 1, 0, 1, -1, 0, 1, -1, 0, -1 };
			int[] tempY4 = { 1, 1, 0, 1, 0, -1, 0, -1, -1 };
			drawOrderX = tempX4;
			drawOrderY = tempY4;
			break;
		}

		// Draw neighbouring locations in order
		for (int i = 0; i < drawOrderX.length; i++) {
			Point p = new Point(drawOrderX[i], drawOrderY[i]);
			drawBoard(g, board, map, w, h, p, player);
		}
		int alpha = 0;

		// Check if player is indoors.
		boolean indoor = false;
		Location playerLoc = player.getLocation();
		Map<Point, Integer> locations = board.mapLocations(playerLoc.getId(), 0, 0, new HashMap<Point, Integer>());
		for (Integer i : locations.values()) {
			for (Tile[] ta : board.getLocationById(i).getTiles()) {
				for (Tile t : ta) {
					if (t instanceof DoorOutTile) {
						indoor = true;
					}
				}
			}
		}

		// If player is outdoors, determine lighting based on time of day
		if (!indoor) {
			int dayPhase = time % dayCycle;
			// One minutes of daytime
			if (dayPhase <= 60)
				alpha = 0;
			// Fade to dark over one 30 seconds
			if (dayPhase > 60 && dayPhase <= 90)
				alpha = ((dayPhase - 60) * 127 / 30);
			// One minute of darkness
			if (dayPhase > 90 && dayPhase <= 150)
				alpha = 127;
			// Fade back to light over 30 seconds
			if (dayPhase > 150 && dayPhase <= 180)
				alpha = 127 - ((dayPhase - 150) * 127 / 30);
			g.setColor(new Color(0, 0, 0, alpha));
			// System.out.println(time + "," + alpha);
			g.fillRect(0, 0, w, h);
		}
		// If there is a message to be displayed, draw speech bubble and text
		if (messageTimer >= time) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 28));
			g.drawImage(speechBubble, 0, 0, null);
			String text = message;
			// If text is too long, split it on appropriate spaces into new
			// lines
			for (int i = 45; i < text.length(); i++) {
				if (text.charAt(i) == ' ') {
					text = text.substring(0, i) + "\n" + text.substring(i + 1);
					i += 45;
				}
			}
			// Center each line
			int linenum = 0;
			for (String line : text.split("\n")) {
				FontMetrics fm = g.getFontMetrics();
				Rectangle2D r = fm.getStringBounds(line, g);
				int x = (speechBubble.getWidth() - (int) r.getWidth()) / 2;
				int y = (speechBubble.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
				g.drawString(line, x, y + linenum * 35);
				linenum++;
			}

		}
		return image;
	}

	/**
	 * Version of paint board that paints not based on a player, but a location.
	 * This method of painting is only used within the world editor, not the
	 * game itself.
	 *
	 * @param loc - location to be centered
	 * @param w - width
	 * @param h - height
	 * @return rendered board image
	 */
	public BufferedImage paintLocation(Location loc, int w, int h) {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(new Color(5, 26, 37));
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		Map<Point, Integer> map = loc.getBoard().mapLocations(loc.getId(), 0, 0, new HashMap<Point, Integer>());
		int[] drawOrderX = null;
		int[] drawOrderY = null;
		switch (viewingDir) {
		case NORTH:
			int[] tempX1 = { -1, -1, 0, -1, 0, 1, 0, 1, 1 };
			int[] tempY1 = { 1, 0, 1, -1, 0, 1, -1, 0, -1 };
			drawOrderX = tempX1;
			drawOrderY = tempY1;
			break;
		case WEST:
			int[] tempX2 = { -1, 0, -1, 1, 0, -1, 1, 0, 1 };
			int[] tempY2 = { -1, -1, 0, -1, 0, 1, 0, 1, 1 };
			drawOrderX = tempX2;
			drawOrderY = tempY2;
			break;
		case SOUTH:
			int[] tempX3 = { 1, 1, 0, 1, 0, -1, 0, -1, -1 };
			int[] tempY3 = { -1, 0, -1, 1, 0, -1, 1, 0, 1 };
			drawOrderX = tempX3;
			drawOrderY = tempY3;
			break;
		case EAST:
			int[] tempX4 = { 1, 0, 1, -1, 0, 1, -1, 0, -1 };
			int[] tempY4 = { 1, 1, 0, 1, 0, -1, 0, -1, -1 };
			drawOrderX = tempX4;
			drawOrderY = tempY4;
			break;
		}
		for (int i = 0; i < drawOrderX.length; i++) {
			Point p = new Point(drawOrderX[i], drawOrderY[i]);
			drawBoard(g, loc.getBoard(), map, w, h, p, null);
		}
		return image;
	}

	/**
	 * Draw board object from the correct perspective.
	 *
	 * @param g - graphics object
	 * @param board - board to be drawn
	 * @param map - map
	 * @param w - width
	 * @param h - height
	 * @param p - poit
	 * @param player - player
	 */
	public void drawBoard(Graphics2D g, Board board, Map<Point, Integer> map, int w, int h, Point p, Player player) {
		if (!map.containsKey(p)) {
			return;
		}
		calculateOffsets(board.getLocationById(map.get(new Point(0, 0))), w, h);
		switch (viewingDir) {
		case NORTH:
			drawBoardFromNorth(g, board, map, w, h, p, player);
			break;
		case SOUTH:
			drawBoardFromSouth(g, board, map, w, h, p, player);
			break;
		case EAST:
			drawBoardFromEast(g, board, map, w, h, p, player);
			break;
		case WEST:
			drawBoardFromWest(g, board, map, w, h, p, player);
			break;
		}
		drawSelected(g);
		drawSelectedLocation(g);
	}

	/**
	 * Draw location (all tiles and objects) from North perspective.
	 *
	 * @param g - graphics object
	 * @param board - board
	 * @param map - map
	 * @param w - width
	 * @param h - height
	 * @param p - point
	 * @param player - player
	 */
	public void drawBoardFromNorth(Graphics2D g, Board board, Map<Point, Integer> map, int w, int h, Point p,
			Player player) {
		for (int i = 0; i < board.getLocationById(map.get(p)).getTiles().length; i++) {
			for (int j = 0; j < board.getLocationById(map.get(p)).getTiles()[0].length; j++) {
				Point iso = twoDToIso((int) (i + p.getX() * 10), (int) (j - p.getY() * 10));
				drawTile(g, board.getLocationById(map.get(p)).getTiles()[i][j], iso, board.getLocationById(map.get(p)),
						new Position(i, j));
			}
		}
		for (int i = 0; i < board.getLocationById(map.get(p)).getTiles().length; i++) {
			for (int j = 0; j < board.getLocationById(map.get(p)).getTiles()[0].length; j++) {
				Point iso = twoDToIso((int) (i + p.getX() * 10), (int) (j - p.getY() * 10));
				drawObject(g, board.getLocationById(map.get(p)).getTiles()[i][j], iso,
						board.getLocationById(map.get(p)), new Position(i, j), player);
			}
		}
	}

	/**
	 * Draw location (all tiles and objects) from East perspective.
	 *
	 * @param g - graphics
	 * @param board - board
	 * @param map - map
	 * @param w - width
	 * @param h - height
	 * @param p - point
	 * @param player - player
	 */
	public void drawBoardFromEast(Graphics2D g, Board board, Map<Point, Integer> map, int w, int h, Point p,
			Player player) {
		for (int j = 0; j < board.getLocationById(map.get(p)).getTiles()[0].length; j++) {
			for (int i = 9; i >= 0; i--) {
				Point iso = twoDToIso((int) (i + p.getX() * 10), (int) (j - p.getY() * 10));
				drawTile(g, board.getLocationById(map.get(p)).getTiles()[i][j], iso, board.getLocationById(map.get(p)),
						new Position(i, j));
			}
		}
		for (int j = 0; j < board.getLocationById(map.get(p)).getTiles()[0].length; j++) {
			for (int i = 9; i >= 0; i--) {
				Point iso = twoDToIso((int) (i + p.getX() * 10), (int) (j - p.getY() * 10));
				drawObject(g, board.getLocationById(map.get(p)).getTiles()[i][j], iso,
						board.getLocationById(map.get(p)), new Position(i, j), player);
			}
		}
	}

	/**
	 * Draw location (all tiles and objects) from South perspective.
	 *
	 * @param g - graphics
	 * @param board - board
	 * @param map - map
	 * @param w - width
	 * @param h - height
	 * @param p - point
	 * @param player - player
	 */
	public void drawBoardFromSouth(Graphics2D g, Board board, Map<Point, Integer> map, int w, int h, Point p,
			Player player) {
		for (int i = 9; i >= 0; i--) {
			for (int j = 9; j >= 0; j--) {
				Point iso = twoDToIso((int) (i + p.getX() * 10), (int) (j - p.getY() * 10));
				drawTile(g, board.getLocationById(map.get(p)).getTiles()[i][j], iso, board.getLocationById(map.get(p)),
						new Position(i, j));
			}
		}
		for (int i = 9; i >= 0; i--) {
			for (int j = 9; j >= 0; j--) {
				Point iso = twoDToIso((int) (i + p.getX() * 10), (int) (j - p.getY() * 10));
				drawObject(g, board.getLocationById(map.get(p)).getTiles()[i][j], iso,
						board.getLocationById(map.get(p)), new Position(i, j), player);
			}
		}
	}

	/**
	 * Draw location (all tiles and objects) from West perspective.
	 *
	 * @param g - graphics
	 * @param board - board
	 * @param map - map
	 * @param w - width
	 * @param h - height
	 * @param p - point
	 * @param player - player
	 */
	public void drawBoardFromWest(Graphics2D g, Board board, Map<Point, Integer> map, int w, int h, Point p,
			Player player) {
		for (int j = 9; j >= 0; j--) {
			for (int i = 0; i < board.getLocationById(map.get(p)).getTiles().length; i++) {
				Point iso = twoDToIso((int) (i + p.getX() * 10), (int) (j - p.getY() * 10));
				drawTile(g, board.getLocationById(map.get(p)).getTiles()[i][j], iso, board.getLocationById(map.get(p)),
						new Position(i, j));
			}
		}
		for (int j = 9; j >= 0; j--) {
			for (int i = 0; i < board.getLocationById(map.get(p)).getTiles().length; i++) {
				Point iso = twoDToIso((int) (i + p.getX() * 10), (int) (j - p.getY() * 10));
				drawObject(g, board.getLocationById(map.get(p)).getTiles()[i][j], iso,
						board.getLocationById(map.get(p)), new Position(i, j), player);
			}
		}
	}

	/**
	 * Draw object of tile at position
	 *
	 * @param g - graphics
	 * @param tile - tile
	 * @param iso - iso
	 * @param loc - location
	 * @param pos - position
	 * @param player - player
	 */
	private void drawObject(Graphics2D g, Tile tile, Point iso, Location loc, Position pos, Player player) {
		if (tile.getGameObject() != null) {
			if (tile.getGameObject() instanceof Player) {
				if (tile.getGameObject() == player) {
					g.drawImage(playerSelect, iso.x, iso.y - playerSelect.getHeight(), null);
				}
			}
			BufferedImage gameObject = getImage(tile.getGameObject().getImage(loc, pos, viewingDir));
			g.drawImage(gameObject, iso.x, iso.y - gameObject.getHeight(), null);
		}
	}

	/**
	 * Get image object from map of fname to images. If image has not yet been
	 * loaded, load it and return it.
	 *
	 * @param fname
	 * @return image
	 */
	private BufferedImage getImage(String fname) {
		if (images.containsKey(fname)) {
			return images.get(fname);
		} else {
			try {
				images.put(fname, ImageIO.read(new File(fname)));
				return images.get(fname);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Draw tile at position
	 *
	 * @param g - graphics
	 * @param tile - tile
	 * @param iso - iso
	 * @param loc - location
	 * @param pos - position
	 */
	private void drawTile(Graphics2D g, Tile tile, Point iso, Location loc, Position pos) {
		BufferedImage floor = getImage(tile.getImage(viewingDir));
		if (tile == selected) {
			selectedPoint = iso;
		}
		if (floor != null) {
			g.drawImage(floor, iso.x, iso.y - floor.getHeight(), null);
		}

	}

	/**
	 * Highlight the tile selected
	 *
	 * @param g - graphics
	 */
	public void drawSelected(Graphics2D g) {
		if (selectedPoint != null) {
			g.drawImage(highlightTile, (int) selectedPoint.getX(),
					(int) selectedPoint.getY() - highlightTile.getHeight(), null);
		}
	}

	/**
	 * Highlight the location selected
	 *
	 * @param g - graphics
	 */
	public void drawSelectedLocation(Graphics2D g) {
		if (selectedLocation != null) {
			int locationSize = 10;
			Point iso;
			switch (selectedLocation) {
			case NORTH:
				iso = twoDToIso((-locationSize / 2), -(locationSize / 2));
				g.drawImage(highlightLocation, iso.x + TILE_WIDTH, iso.y - TILE_WIDTH, null);
				break;
			case EAST:
				iso = twoDToIso((locationSize / 2), (locationSize / 2));
				g.drawImage(highlightLocation, iso.x + TILE_WIDTH, iso.y - TILE_WIDTH, null);
				break;
			case WEST:
				iso = twoDToIso((-3 * locationSize / 2), (locationSize / 2));
				g.drawImage(highlightLocation, iso.x + TILE_WIDTH, iso.y - TILE_WIDTH, null);
				break;
			case SOUTH:
				iso = twoDToIso((-locationSize / 2), (3 * locationSize / 2));
				g.drawImage(highlightLocation, iso.x + TILE_WIDTH, iso.y - TILE_WIDTH, null);
				break;
			}
		}
	}

	/**
	 * Calculate x and y offsets to display location centered in screen
	 *
	 * @param loc - location
	 * @param w - width
	 * @param h - height
	 */
	public void calculateOffsets(Location loc, int w, int h) {
		int boardHeight = (int) ((loc.getTiles().length + loc.getTiles()[0].length - 1) * TILE_WIDTH
				* Math.sin(Math.PI / 6));
		xOffset = (int) (w / 2 - 2 * TILE_WIDTH * Math.sin(Math.PI / 6));
		yOffset = (int) ((h - boardHeight) / 2 + TILE_WIDTH * Math.cos(Math.PI / 6));
	}

	/**
	 * Return point (x,y) position on screen to display tile at index (i, j)
	 *
	 * @param i - x
	 * @param j - y
	 * @return point on screen
	 */
	public Point twoDToIso(int i, int j) {
		int x = 0, y = 0;
		switch (viewingDir) {
		case NORTH:
			x = TILE_WIDTH * i;
			y = TILE_WIDTH * j;
			break;
		case SOUTH:
			x = TILE_WIDTH * (10 - i - 1);
			y = TILE_WIDTH * (10 - j - 1);
			break;
		case EAST:
			x = TILE_WIDTH * j;
			y = TILE_WIDTH * (10 - i - 1);
			break;
		case WEST:
			x = TILE_WIDTH * (10 - j - 1);
			y = TILE_WIDTH * i;
			break;
		}
		Point tempPt = new Point(0, 0);
		tempPt.x = xOffset + (x - y);
		tempPt.y = yOffset + ((x + y) / 2);
		return (tempPt);
	}

	/**
	 * Return index of tile at the position x, y on the game screen
	 *
	 * @param x - x
	 * @param y - y
	 * @return poistion of tile
	 */
	public Position isoToIndex(int x, int y) {
		double a = (x - xOffset) / 2 + y - yOffset;
		double b = 2 * (y - yOffset) - a;

		int i = 0, j = 0;
		switch (viewingDir) {
		case NORTH:
			i = (int) Math.round(a / TILE_WIDTH);
			j = (int) Math.round(b / TILE_WIDTH + 1);
			break;
		case SOUTH:
			i = (int) Math.round(-1 * a / TILE_WIDTH + 9);
			j = (int) Math.round(-1 * b / TILE_WIDTH + 8);
			break;
		case EAST:
			i = (int) Math.round(-1 * b / TILE_WIDTH + 8);
			j = (int) Math.round(a / TILE_WIDTH);
			break;
		case WEST:
			i = (int) Math.round(b / TILE_WIDTH + 1);
			j = (int) Math.round(-1 * a / TILE_WIDTH + 9);
			break;
		}
		Position index = new Position(i, j);
		return index;
	}

	/**
	 * Select tile t
	 *
	 * @param t - t
	 */
	public void selectTile(Tile t) {
		setSelected(t);
	}

	/**
	 * Select tile pos of location loc
	 *
	 * @param pos - position
	 * @param loc - location
	 */
	public void selectTile(Position pos, Location loc) {
		setSelected(getTileAtPos(pos, loc));
	}

	/**
	 * Get tile at position pos in Location loc
	 *
	 * @param pos - position
	 * @param loc - location
	 * @return tile
	 */
	public Tile getTileAtPos(Position pos, Location loc) {
		if (pos.getX() >= 0 && pos.getY() >= 0 && pos.getX() < loc.getTiles().length
				&& pos.getY() < loc.getTiles()[0].length) {
			return loc.getTiles()[pos.getX()][pos.getY()];
		}
		return null;
	}

	/**
	 * Return selected tile
	 *
	 * @return tile
	 */
	public Tile getSelected() {
		return selected;
	}

	/**
	 * Select tile to be highlighed
	 *
	 * @param selected - selected
	 */
	public void setSelected(Tile selected) {
		this.selected = selected;
	}

	/**
	 * Highlight location in direction dir from centered location
	 *
	 * @param dir - direction
	 */
	public void selectLocation(Direction dir) {
		this.selectedLocation = dir;
	}

	/**
	 * Rotate counter clockwise
	 */
	public void rotateCounterClockwise() {
		viewingDir = Location.clockwiseDir(viewingDir);
	}

	/**
	 * Rotate viewing direction clockwise
	 */
	public void rotateClockwise() {
		viewingDir = Location.counterClockwiseDir(viewingDir);

	}
}
package worldeditor;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import core.Board;
import core.GameSystem;
import core.GameSystem.Direction;
import core.Location;
import gameobjects.Building;
import gameobjects.Chest;
import gameobjects.Door;
import gameobjects.Tree;
import gameobjects.Wall;
import gameobjects.Fence;
import gameobjects.NPC;
import iohandling.BoardParser;
import iohandling.BoardWriter;
import items.Banana;
import items.Fish;
import items.FishingRod;
import items.FloatingDevice;
import items.Key;
import items.Teleporter;
import renderer.Renderer;
import tile.DoorOutTile;
import tile.GrassTile;
import tile.SandTile;
import tile.StoneTile;
import tile.Tile;
import tile.WaterTile;
import tile.WoodTile;
import util.Position;

/**
 * World Editor class used to create the board that the game world is played on.
 *
 * @author Jonathan Carr
 *
 */
public class WorldEditor {

	Board board;
	int currentLocation = 0;
	EditorFrame frame;
	ToolSelectionFrame toolSelect;
	Renderer renderer;

	String tool = "none";
	String floor = "grass";
	private String gameObject = "tree";

	/**
	 * Constructor for WorldEditor, creates frame and toolselection windows.
	 */
	public WorldEditor() {
		renderer = new Renderer();
		board = BoardParser.parseBoardFName("map-new.txt");
		currentLocation = 0;
		frame = new EditorFrame(this);
		toolSelect = new ToolSelectionFrame(this);
		update();
	}

	/**
	 * Creates a location consisting of only grass squares, adds it to the board
	 * and returns the int id of the location.
	 *
	 * @return id of location
	 */
	public int createBlankLocation() {
		Location loc = new Location(board.getNextUniqueId(), "", new Tile[10][10], board);
		for (int i = 0; i < loc.getTiles().length; i++) {
			for (int j = 0; j < loc.getTiles()[0].length; j++) {
				loc.getTiles()[i][j] = new GrassTile(new Position(i, j), null);
			}
		}
		board.addLocation(loc.getId(), loc);
		return loc.getId();
	}

	/**
	 * Create blank location of only wooden tiles. Returns location id.
	 *
	 * @return locID - location id
	 */
	public int createIndoorLocation() {
		Location loc = new Location(board.getNextUniqueId(), "", new Tile[10][10], board);
		for (int i = 0; i < loc.getTiles().length; i++) {
			for (int j = 0; j < loc.getTiles()[0].length; j++) {
				loc.getTiles()[i][j] = new WoodTile(new Position(i, j), null);
			}
		}
		board.addLocation(loc.getId(), loc);
		return loc.getId();
	}

	/**
	 * Updates image in EditorFrame by rendering a new bard.
	 */
	public void update() {
		if (board.getLocationById(currentLocation) != null) {
			frame.setImage(renderer.paintLocation(board.getLocationById(currentLocation), frame.panel.getWidth(),
					frame.panel.getHeight()));
		}
	}

	/**
	 * Main method for running world editor.
	 *
	 * @param args - arguments
	 */
	public static void main(String[] args) {
		new WorldEditor();
	}

	/**
	 * Process a tile depending on the tool selected. Set tile, game object
	 * accordingly. also processes moving the world editor into and out of
	 * rooms.
	 *
	 * @param i
	 *            x position of tile
	 * @param j
	 *            y position of tile
	 */
	public void processTile(int i, int j) {
		if (i >= 0 && j >= 0 && i < board.getLocationById(currentLocation).getTiles().length
				&& j < board.getLocationById(currentLocation).getTiles()[0].length) {
			Tile tile = board.getLocationById(currentLocation).getTiles()[i][j];
			if (tile.getGameObject() instanceof Door) {
				System.out.println("Yay?");
				currentLocation = ((Door) tile.getGameObject()).getLocationID();
				System.out.print(currentLocation);
				currentLocation = ((Door) tile.getGameObject()).getLocationID();
				update();
				return;
			}
			if (tile instanceof DoorOutTile) {
				currentLocation = ((DoorOutTile) tile).getOutLocationID();
				System.out.println(currentLocation);
				update();
				return;
			}
			if (tool.equals("Set Floor Type")) {
				Tile newTile = null;
				switch (floor) {
				case "grass":
					newTile = new GrassTile(tile.getPos(), tile.getGameObject());
					break;
				case "stone":
					newTile = new StoneTile(tile.getPos(), tile.getGameObject());
					break;
				case "water":
					newTile = new WaterTile(tile.getPos(), tile.getGameObject());
					break;
				case "sand":
					newTile = new SandTile(tile.getPos(), tile.getGameObject());
					break;
				case "wood":
					newTile = new WoodTile(tile.getPos(), tile.getGameObject());
					break;
				}
				renderer.selectTile(newTile);
				board.getLocationById(currentLocation).getTiles()[i][j] = newTile;
			}
			if (tool.equals("Add Game Object")) {
				switch (gameObject) {
				case "tree":
					tile.setGameObject(new Tree());
					break;
				case "wall":
					tile.setGameObject(new Wall());
					break;
				case "fence":
					tile.setGameObject(new Fence());
					break;
				case "chest":
					tile.setGameObject(new Chest());
					break;
				case "key":
					tile.setGameObject(new Key("Key", 0));
					break;
				case "floaty":
					tile.setGameObject(new FloatingDevice("Floating Device"));
					break;
				case "banana":
					tile.setGameObject(new Banana("Banana"));
					break;
				case "building":
					tile.setGameObject(new Building());
					break;
				case "door":
					int id = createIndoorLocation();
					DoorOutTile doorOut = new DoorOutTile(new Position(5, 9), null, currentLocation,
							board.getLocationById(currentLocation)
									.getTileInDirection(new Position(i, j), Direction.SOUTH).getPos());
					System.out.println(currentLocation);

					board.getLocationById(id).getTiles()[5][9] = doorOut;
					tile.setGameObject(new Door(0, id));

					break;
				case "teleporter":
					tile.setGameObject(new Teleporter("Teleporter"));
					break;

				case "NPC":
					tile.setGameObject(new NPC("random", Direction.NORTH));
					break;
				case "fish":
					tile.setGameObject(new Fish("Fish"));
					break;
				case "fishingrod":
					tile.setGameObject(new FishingRod("Fishing Rod"));
					break;
				}

			}
			update();
		}
	}

	/**
	 * Set floor type to string input
	 *
	 * @param string - string of floor type
	 */
	public void setFloorType(String string) {
		this.floor = string;
	}

	/**
	 * Set tool type to string input
	 *
	 * @param string - string of tool type
	 */
	public void setTool(String string) {
		this.tool = string;
	}

	/**
	 * Set object type to string input
	 *
	 * @param string - object type
	 */
	public void setObjectType(String string) {
		this.gameObject = string;
	}

	/**
	 * set game object on tile to null
	 *
	 * @param i - x-pos of tile
	 * @param j - y-pos of tile
	 */
	public void clearTile(int i, int j) {
		if (i >= 0 && j >= 0 && i < board.getLocationById(currentLocation).getTiles().length
				&& j < board.getLocationById(currentLocation).getTiles()[0].length) {
			board.getLocationById(currentLocation).getTiles()[i][j].setGameObject(null);
		}
		update();
	}

	/**
	 * Set tile to selected in renderer (will show as highlighted white tile)
	 *
	 * @param selected - selected position
	 */
	public void selectTile(Position selected) {
		if (selected != null) {
			renderer.selectTile(new Position((int) selected.getX(), (int) selected.getY()),
					board.getLocationById(currentLocation));
			update();
		}
		selected = null;
	}

	/**
	 * Set location to selected i nrenderer (will show as highlighted white
	 * location)
	 *
	 * @param dir - direction to select location from
	 */
	public void selectLocation(GameSystem.Direction dir) {
		renderer.selectLocation(dir);
		update();
	}

	/**
	 * When clicking in direction adjacent to centered location, if no location
	 * exists add one. If location exists, center editor in that location.
	 *
	 * @param dir - direction to select location from
	 */
	public void clickLocation(Direction dir) {
		if (dir != null) {
			if (board.getLocationById(currentLocation).getNeighbours().get(dir) == null) {
				board.getLocationById(currentLocation).getNeighbours().put(dir, createBlankLocation());
				board.getLocationById(board.getLocationById(currentLocation).getNeighbours().get(dir)).getNeighbours()
						.put(Location.oppositeDir(dir), currentLocation);
				Map<Point, Integer> map = board.mapLocations(currentLocation, 0, 0, new HashMap<Point, Integer>());
				board.linkLocations(map);
			} else {
				currentLocation = board.getLocationById(currentLocation).getNeighbours().get(dir);
			}
		}
		update();
	}

	/**
	 * Merge two maps of points to locations.
	 * @param map1 - first map
	 * @param map2 - second map
	 * @return merged map - merged map
	 */
	public Map<Point, Location> mergeMaps(Map<Point, Location> map1, Map<Point, Location> map2) {
		Map<Point, Location> mergedMap = new HashMap<Point, Location>();
		for (Point p : map1.keySet()) {
			mergedMap.put(p, map1.get(p));
		}
		for (Point p : map2.keySet()) {
			mergedMap.put(p, map2.get(p));
		}
		return mergedMap;
	}

	/**
	 * Get location in a series of directions from the starting location.
	 * @param startingLoc - starting location
	 * @param directions - array of directions
	 * @return target location - location after going through array of directions
	 */
	public Location getLocationAt(int startingLoc, Direction[] directions) {
		int finalLoc = startingLoc;
		for (Direction d : directions) {
			if (board.getLocationById(finalLoc).getNeighbours().get(d) != null) {
				finalLoc = board.getLocationById(finalLoc).getNeighbours().get(d);
			} else {
				return null;
			}
		}
		return board.getLocationById(finalLoc);
	}

	/**
	 * Reset view to center on location 0.
	 */
	public void resetView() {
		currentLocation = 0;
		update();
	}

}
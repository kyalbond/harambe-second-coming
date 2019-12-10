package core;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import core.GameSystem.Direction;
import tile.Tile;
import util.Position;

/**
 * Location objects store a map of directions to neighbours and a 2D array of
 * Tile objects.
 *

 * @author Jonathan Carr, Jack Slater and Kyal Bond
 *
 */

public class Location {
	private Tile[][] tiles;
	private int id;
	private String name;
	private Board board;
	private Map<GameSystem.Direction, Integer> neighbours;

	/**
	 * Constructor for location class
	 *
	 * @param id
	 *            of location
	 * @param name
	 *            of location
	 * @param tiles
	 *            of location
	 * @param board
	 *            of location
	 */
	public Location(int id, String name, Tile[][] tiles, Board board) {
		this.tiles = tiles;
		this.name = name;
		this.id = id;
		this.neighbours = new HashMap<GameSystem.Direction, Integer>();
		this.board = board;
	}

	/**
	 * Get tiles of location
	 *
	 * @return tiles
	 */
	public Tile[][] getTiles() {
		return tiles;
	}

	/**
	 * Get name of location
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get neighbours of location
	 *
	 * @return map - gets neighbors of location
	 */
	public Map<GameSystem.Direction, Integer> getNeighbours() {
		return neighbours;
	}

	/**
	 * Get direction of neighbour location
	 *
	 * @param id
	 *            of location
	 * @return direction of location
	 */
	public GameSystem.Direction getDirectionOfLocation(int id) {
		for (GameSystem.Direction d : neighbours.keySet()) {
			if (id == neighbours.get(d))
				return d;
		}
		return null;
	}

	/**
	 * Get neighbour location in direction
	 *
	 * @param d - direction to find location with
	 * @return location - location returned from the direction
	 */
	public Location getLocationfromDirection(GameSystem.Direction d) {
		return board.getLocationById(neighbours.get(d));
	}

	/**
	 * Set neighbours map of location
	 *
	 * @param neighbours - locations around current location
	 */
	public void setNeighbours(Map<GameSystem.Direction, Integer> neighbours) {
		this.neighbours = neighbours;
	}

	/**
	 * Get id of location
	 *
	 * @return id - id of location
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get board of location
	 *
	 * @return board - board of location
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Get the tile in the direction d from position p. Takes into account
	 * location edges.
	 *
	 * @param pos - current pos
	 * @param d - direction to be moved
	 * @return tile - returned tile
	 */
	public Tile getTileInDirection(Position pos, Direction d) {
		Position p = null;
		if (d == Direction.NORTH) {
			p = new Position(pos.getX(), pos.getY() - 1);
			if (withinBounds(p)) {
				return getTiles()[p.getX()][p.getY()];
			}
			Location nextLoc = board.getLocationById(getNeighbours().get(Direction.NORTH));
			if (nextLoc != null) {
				return nextLoc.getTiles()[p.getX()][nextLoc.getTiles()[0].length - 1];
			}
		}
		if (d == Direction.SOUTH) {
			p = new Position(pos.getX(), pos.getY() + 1);
			if (withinBounds(p)) {
				return getTiles()[p.getX()][p.getY()];
			}
			Location nextLoc = board.getLocationById(getNeighbours().get(Direction.SOUTH));
			if (nextLoc != null) {
				return nextLoc.getTiles()[p.getX()][0];
			}
		}
		if (d == Direction.EAST) {
			p = new Position(pos.getX() + 1, pos.getY());
			if (withinBounds(p)) {
				return getTiles()[p.getX()][p.getY()];
			}

			board.getLocationById(null);
			Location nextLoc = board.getLocationById(getNeighbours().get(Direction.EAST));
			if (nextLoc != null) {
				return nextLoc.getTiles()[0][p.getY()];
			}
		}
		if (d == Direction.WEST) {
			p = new Position(pos.getX() - 1, pos.getY());
			if (withinBounds(p)) {
				return getTiles()[p.getX()][p.getY()];
			}
			Location nextLoc = board.getLocationById(getNeighbours().get(Direction.WEST));
			if (nextLoc != null) {
				return nextLoc.getTiles()[nextLoc.getTiles().length - 1][p.getY()];
			}
		}
		return null;
	}

	/**
	 * Returns true if position is within bounds of tiles array
	 *
	 * @param pos - position to check
	 * @return boolean - true if within bounds
	 */
	public boolean withinBounds(Position pos) {
		return pos.getX() >= 0 && pos.getY() >= 0 && pos.getX() < getTiles().length
				&& pos.getY() < getTiles()[0].length;
	}

	/**
	 * Get position of tile in location
	 *
	 * @param tile - tile to find position of
	 * @return position - position of the tile
	 */
	public Position getPositionOfTile(Tile tile) {
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				if (tiles[i][j] == tile) {
					return new Position(i, j);
				}
			}
		}
		return null;
	}

	/**
	 * Get tile at position, allows indexes out of bounds, getting tiles from
	 * neighbouring locations
	 *
	 * @param pos - position to find tile of
	 * @return tile - tile that is at the required position
	 */
	public Tile getTileAtPosition(Position pos) {
		Point p = new Point(pos.getX() / 10, -1 * (pos.getY() / 10));
		if (pos.getX() < 0) {
			p.x = p.x - 1;
		}
		if (pos.getY() < 0) {
			p.y = 1;
		}
		if (p.equals(new Point(0, 0))) {
			return tiles[pos.getX()][pos.getY()];
		}
		Map<Point, Integer> map = board.mapLocations(id, 0, 0, new HashMap<Point, Integer>());
		Location newLoc = board.getLocationById(map.get(p));
		if (newLoc != null) {
			return newLoc.getTileAtPositionInLoc(
					new Position((int) (-p.getX() * 10 + pos.getX()), (int) (10 * p.getY() + pos.getY())));
		}
		return null;
	}

	/**
	 * Get tile at position pos
	 *
	 * @param pos
	 *            position
	 * @return tile at position
	 */
	public Tile getTileAtPositionInLoc(Position pos) {
		if (withinBounds(pos)) {
			return tiles[pos.getX()][pos.getY()];
		}
		return null;
	}

	/**
	 * Get direction for dijkstra's algorithm from player to position.
	 *
	 * @param player - Tile of the current player
	 * @param position - Tile of current position
	 * @return direction - direction to move too
	 */
	public static Direction getDirDijkstras(Tile player, Tile position) {
		Position from = player.getPos();
		Position to = position.getPos();

		// Check boundry movement
		if (from.getX() == 9 && to.getX() == 0)
			return Direction.EAST;
		else if (from.getX() == 0 && to.getX() == 9)
			return Direction.WEST;
		else if (from.getY() == 0 && to.getY() == 9)
			return Direction.NORTH;
		else if (from.getY() == 9 && to.getY() == 0)
			return Direction.SOUTH;

		// Check normal movement
		if (from.getX() > to.getX() && from.getY() == to.getY())
			return Direction.WEST;
		else if (from.getX() < to.getX() && from.getY() == to.getY())
			return Direction.EAST;
		else if (from.getY() > to.getY() && from.getX() == to.getX())
			return Direction.NORTH;
		else if (from.getY() < to.getY() && from.getX() == to.getX())
			return Direction.SOUTH;
		else
			return null;
	}

	/**
	 * Get direction of tile t from position from. Returns null if tile is not
	 * adjacent to position
	 *
	 * @param from
	 *            position
	 * @param t
	 *            tile
	 * @return direction of tile from position
	 */
	public Direction getDirOfTile(Position from, Tile t) {
		if (getTileInDirection(from, Direction.NORTH) == t) {
			return Direction.NORTH;
		}
		if (getTileInDirection(from, Direction.SOUTH) == t) {
			return Direction.SOUTH;
		}
		if (getTileInDirection(from, Direction.EAST) == t) {
			return Direction.EAST;
		}
		if (getTileInDirection(from, Direction.WEST) == t) {
			return Direction.WEST;
		}
		return null;
	}

	/**
	 * Get direction relative to the observers viewing angle
	 *
	 * @param d
	 *            diection
	 * @param viewing
	 *            direction
	 * @return relative direction
	 */
	public static Direction getRelativeDirection(Direction d, Direction viewing) {
		int turns = 0;
		switch (viewing) {
		case NORTH:
			turns = 0;
			break;
		case EAST:
			turns = 1;
			break;
		case SOUTH:
			turns = 2;
			break;
		case WEST:
			turns = 3;
			break;
		}
		Direction dir = d;
		for (int i = 0; i < turns; i++) {
			d = clockwiseDir(d);
		}
		return d;
	}

	/**
	 * Get relative direction from viewing direction in the other direction
	 *
	 * @param d
	 *            direction
	 * @param viewing
	 *            direction
	 * @return direction
	 */
	public static Direction getOtherRelativeDirection(Direction d, Direction viewing) {
		int turns = 0;
		switch (viewing) {
		case NORTH:
			turns = 0;
			break;
		case EAST:
			turns = 1;
			break;
		case SOUTH:
			turns = 2;
			break;
		case WEST:
			turns = 3;
			break;
		}
		Direction dir = d;
		for (int i = 0; i < turns; i++) {
			d = counterClockwiseDir(d);
		}
		return d;
	}

	/**
	 * Get direction clockwise of direction
	 *
	 * @param d
	 *            direction input
	 * @return direction
	 */
	public static Direction clockwiseDir(Direction d) {
		if (d == Direction.NORTH) {
			return Direction.EAST;
		}
		if (d == Direction.EAST) {
			return Direction.SOUTH;
		}
		if (d == Direction.WEST) {
			return Direction.NORTH;
		}
		if (d == Direction.SOUTH) {
			return Direction.WEST;
		}
		return null;
	}

	/**
	 * Gets direction in counter clockwise direction
	 *
	 * @param d
	 *            direction input
	 * @return counter clockwise direction
	 */
	public static Direction counterClockwiseDir(Direction d) {
		if (d == Direction.NORTH) {
			return Direction.WEST;
		}
		if (d == Direction.EAST) {
			return Direction.NORTH;
		}
		if (d == Direction.WEST) {
			return Direction.SOUTH;
		}
		if (d == Direction.SOUTH) {
			return Direction.EAST;
		}
		return null;
	}

	/**
	 * Returns the direction in the opposite direction
	 *
	 * @param d
	 *            direction input
	 * @return opposite direction
	 */
	public static Direction oppositeDir(Direction d) {
		if (d == Direction.NORTH) {
			return Direction.SOUTH;
		}
		if (d == Direction.EAST) {
			return Direction.WEST;
		}
		if (d == Direction.WEST) {
			return Direction.EAST;
		}
		if (d == Direction.SOUTH) {
			return Direction.NORTH;
		}
		return null;
	}
}

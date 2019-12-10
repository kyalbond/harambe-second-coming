package core;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import core.GameSystem.Direction;
import gameobjects.Player;

/**
 * Board holds a collection players and locations.
 *
 * @author Jonathan, Jack
 *
 */

public class Board {
	private Map<String, Player> players;
	private Map<Integer, Location> locations;

	/**
	 * Constructor for board object Create new empty maps for locations and
	 * players
	 */
	public Board() {
		this.locations = new HashMap<Integer, Location>();
		this.players = new HashMap<String, Player>();
	}

	/**
	 * Get map of locations
	 *
	 * @return map of locations
	 */
	public Map<Integer, Location> getLocations() {
		return locations;
	}

	/**
	 * Set map of locations
	 *
	 * @param locations - locations to be set
	 */
	public void setLocations(Map<Integer, Location> locations) {
		this.locations = locations;
	}

	/**
	 * Get location by id
	 *
	 * @param id
	 *            of location
	 * @return location object
	 */
	public Location getLocationById(Integer id) {
		return locations.get(id);
	}

	/**
	 * Add location to map
	 *
	 * @param id
	 *            of location
	 * @param location
	 *            object
	 */
	public void addLocation(Integer id, Location location) {
		locations.put(id, location);
	}

	/**
	 * Get next integer not yet used by a location as an id
	 *
	 * @return next unique id
	 */
	public int getNextUniqueId() {
		int i = 0;
		while (locations.get(i) != null) {
			i++;
		}
		return i;
	}

	/**
	 * Take a map of locations and make sure all locations link to adjacent
	 * locations via their neighbours maps
	 *
	 * @param map
	 *            of locations
	 */
	public void linkLocations(Map<Point, Integer> map) {
		for (Point point : map.keySet()) {
			for (Point point2 : map.keySet()) {
				if (point.x == point2.x) {
					// NORTH OR SOUTH?
					if (point.y + 1 == point2.y) {
						// NORTH
						getLocationById(map.get(point)).getNeighbours().put(Direction.NORTH, map.get(point2));
					}
					if (point.y - 1 == point2.y) {
						// SOUTH
						getLocationById(map.get(point)).getNeighbours().put(Direction.SOUTH, map.get(point2));
					}
				}
				if (point.y == point2.y) {
					// WEST OR EAST?
					if (point.x + 1 == point2.x) {
						// EAST
						getLocationById(map.get(point)).getNeighbours().put(Direction.EAST, map.get(point2));
					}
					if (point.x - 1 == point2.x) {
						// WEST
						getLocationById(map.get(point)).getNeighbours().put(Direction.WEST, map.get(point2));
					}
				}
			}
		}
	}

	/**
	 * Recursively create a map of the relative positions of locations based on
	 * neighbours
	 *
	 * @param id
	 *            of current location
	 * @param x
	 *            coordinate relative to start
	 * @param y
	 *            coordinate relative to start
	 * @param map
	 *            of points to locations
	 *
	 * @return map of points to locations
	 */
	public Map<Point, Integer> mapLocations(int id, int x, int y, Map<Point, Integer> map) {
		Location loc = getLocationById(id);
		if (map.containsKey(new Point(x, y))) {
			return map;
		}
		map.put(new Point(x, y), id);
		for (Direction d : Direction.values()) {
			if (loc.getNeighbours().keySet().contains(d)) {
				if (!map.entrySet().contains(loc.getNeighbours().get(d))) {
					Point offset = getOffset(d);
					mapLocations(loc.getNeighbours().get(d), x + offset.x, y + offset.y, map);
				}
			}
		}
		return map;
	}

	/**
	 * Get point representation of offset of direction
	 * @param d - direction to be offset
	 * @return direction - direction that is offset
	 */
	public Point getOffset(Direction d) {
		switch (d) {
		case NORTH:
			return new Point(0, 1);
		case SOUTH:
			return new Point(0, -1);
		case EAST:
			return new Point(1, 0);
		case WEST:
			return new Point(-1, 0);
		}
		return null;
	}

	/**
	 * Add player to board
	 * @param userName - name of the player
	 * @param player - Player object of the client
	 */
	public void addPlayer(String userName, Player player) {
		players.put(userName, player);
	}

	/**
	 * Get player by username
	 * @param username - username which to find the player with
	 * @return player - player object that is returned from the username
	 */
	public Player getPlayer(String username) {
		return players.get(username);
	}

	/**
	 * Get map of players
	 * @return players
	 */
	public Map<String, Player> getPlayers() {
		return players;
	}
}

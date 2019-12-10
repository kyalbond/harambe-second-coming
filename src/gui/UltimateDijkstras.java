package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import javax.swing.Timer;

import clientserver.ClientController;
import core.GameSystem;
import core.Location;
import gameobjects.GameObject;
import items.Item;
import tile.Tile;
import tile.WaterTile;
import util.Position;

/**
 * Implements a varied version of Dijkstra's algorithm, getting a path from one
 * tile to another using the 8 locations around the players current location.
 *
 * @author Kyal Bond
 *
 */
public class UltimateDijkstras implements ActionListener {
	private ClientController controller;
	private Tile start;
	private Tile end;
	private Location startLocation;
	private Node[][] nodes;
	private Stack<Tile> path;
	private Timer timer;
	private int length;
	private Map<Integer, Integer> zoneLocId;

	public Tile oldTile;

	/**
	 * Method that sets all fields, creates timer and then calls the setup
	 * method
	 *
	 * @param controller - Controller of GUI/Client
	 * @param start - start tile
	 * @param startLocation - start location
	 * @param end - end tile
	 */
	public UltimateDijkstras(ClientController controller, Tile start, Location startLocation, Tile end) {
		this.controller = controller;
		this.start = start;
		this.end = end;
		this.startLocation = startLocation;
		this.setPath(null);
		this.length = 30;
		this.zoneLocId = new HashMap<Integer, Integer>();
		this.oldTile = null;
		timer = new Timer(400, this);

		setup();
	}

	/**
	 * Actual Dijkstras algorithm using the node array that has been setup
	 */
	public void createPath() {
		// Setup start and end nodes
		Node start = nodes[this.start.getPos().getX() + 10][this.start.getPos().getY() + 10];
		Node end = findRelativePosition();
		Node found = null;

		// Create queue for searching through nodes
		Queue<SearchNode> fringe = new PriorityQueue<SearchNode>(nodeSorter);
		fringe.offer(new SearchNode(0, start, null));

		// Checks every surrounding node and updates if the path to the node is
		// shorter than current path
		while (!fringe.isEmpty()) {
			SearchNode searchNode = fringe.poll();

			if (!searchNode.node.visited) {
				searchNode.node.visited = true;
				searchNode.node.pathFrom = searchNode.from;
				if (searchNode.node.equals(end)) {
					found = searchNode.node;
					break;
				}

				// Check neighbours to see if they have been visited/can be
				// visited
				for (Node n : searchNode.node.neighbours) {
					if (n != null) {
						GameObject o = n.t.getGameObject();
						if (!n.visited) {

							// Checks the different conditions for tiles
							if (o == null || o instanceof Item) {
								if (!(n.t instanceof WaterTile) || controller.getPlayer().getHasFloatingDevice()) {
									int cost = searchNode.costToHere + 1;
									fringe.offer(new SearchNode(cost, n, searchNode.node));
								}
							}
						}

					}
				}
			}

		}

		// Create path for nodes using stack
		Stack<Tile> path = new Stack<Tile>();
		while (found != null) {
			path.push(found.t);
			found = found.pathFrom;
		}

		// Pops the move the player is already on
		if (!path.isEmpty())
			path.pop();

		this.setPath(path);
	}

	/**
	 * Begins timer
	 */
	public void startTimer() {
		timer.start();
	}

	/**
	 * Setup the node array that dijkstras uses, getting the 8 locations around
	 * the player locations and loading the tiles into the array.
	 */
	private void setup() {
		nodes = new Node[length][length];

		Location main = null;
		Location secondary = null;

		// Booleans for corners
		boolean NW = false;
		boolean NE = false;
		boolean SE = false;
		boolean SW = false;

		// Setup up location standing on
		setupPartition(10, 10, startLocation);
		zoneLocId.put(new Integer(startLocation.getId()), 5);

		// NORTH LOCATIONS
		main = startLocation.getLocationfromDirection(GameSystem.Direction.NORTH);
		if (main != null) {
			setupPartition(10, 0, main);
			zoneLocId.put(new Integer(main.getId()), 2);

			// NORTH WEST
			secondary = main.getLocationfromDirection(GameSystem.Direction.WEST);
			if (!NW && secondary != null) {
				setupPartition(0, 0, secondary);
				NW = true;
				zoneLocId.put(new Integer(secondary.getId()), 1);
			}

			// NORTH EAST
			secondary = main.getLocationfromDirection(GameSystem.Direction.EAST);
			if (!NE && secondary != null) {
				setupPartition(20, 0, secondary);
				NE = true;
				zoneLocId.put(new Integer(secondary.getId()), 3);
			}
		}

		// EAST LOCATIONS
		main = startLocation.getLocationfromDirection(GameSystem.Direction.EAST);
		if (main != null) {
			setupPartition(20, 10, main);
			zoneLocId.put(new Integer(main.getId()), 6);

			// NORTH EAST
			secondary = main.getLocationfromDirection(GameSystem.Direction.NORTH);
			if (!NE && secondary != null) {
				setupPartition(20, 0, secondary);
				NE = true;
				zoneLocId.put(new Integer(secondary.getId()), 3);
			}

			// SOUTH EAST
			secondary = main.getLocationfromDirection(GameSystem.Direction.SOUTH);
			if (!SE && secondary != null) {
				setupPartition(20, 20, secondary);
				SE = true;
				zoneLocId.put(new Integer(secondary.getId()), 9);
			}
		}

		// SOUTH LOCATIONS
		main = startLocation.getLocationfromDirection(GameSystem.Direction.SOUTH);
		if (main != null) {
			setupPartition(10, 20, main);
			zoneLocId.put(new Integer(main.getId()), 8);

			// SOUTH WEST
			secondary = main.getLocationfromDirection(GameSystem.Direction.WEST);
			if (!SW && secondary != null) {
				setupPartition(0, 20, secondary);
				SW = true;
				zoneLocId.put(new Integer(secondary.getId()), 7);
			}

			// SOUTH EAST
			secondary = main.getLocationfromDirection(GameSystem.Direction.EAST);
			if (!SE && secondary != null) {
				setupPartition(20, 20, secondary);
				SE = true;
				zoneLocId.put(new Integer(secondary.getId()), 9);
			}
		}

		// WEST LOCATIONS
		main = startLocation.getLocationfromDirection(GameSystem.Direction.WEST);
		if (main != null) {
			setupPartition(0, 10, main);
			zoneLocId.put(new Integer(main.getId()), 4);

			// NORTH WEST
			secondary = main.getLocationfromDirection(GameSystem.Direction.NORTH);
			if (!NW && secondary != null) {
				setupPartition(0, 0, secondary);
				NW = true;
				zoneLocId.put(new Integer(secondary.getId()), 1);
			}

			// SOUTH WEST
			secondary = main.getLocationfromDirection(GameSystem.Direction.SOUTH);
			if (!SW && secondary != null) {
				setupPartition(0, 20, secondary);
				SW = true;
				zoneLocId.put(new Integer(secondary.getId()), 7);
			}
		}

		setupNeighbours();
	}

	/**
	 * Setup up the 10 by 10 location and a given point in node array
	 *
	 * @param xStart
	 * @param yStart
	 * @param location
	 */
	private void setupPartition(int xStart, int yStart, Location location) {
		// Create nodes for tiles
		int x = xStart;
		int y = yStart;
		while (y < yStart + 10) {
			x = xStart;
			while (x < xStart + 10) {
				Tile t = location.getTiles()[x % 10][y % 10];
				nodes[x][y] = new Node(false, x, y, t);
				x++;
			}
			y++;
		}
	}

	/**
	 * Adds neighbours to each node if possible
	 */
	private void setupNeighbours() {
		for (int y = 0; y < length; y++) {
			for (int x = 0; x < length; x++) {
				Node n = nodes[x][y];
				if (n != null) {
					try {
						n.neighbours.add(nodes[x][y - 1]);
					} catch (Exception e) {
					}
					try {
						n.neighbours.add(nodes[x][y + 1]);
					} catch (Exception e) {
					}
					try {
						n.neighbours.add(nodes[x + 1][y]);
					} catch (Exception e) {
					}
					try {
						n.neighbours.add(nodes[x - 1][y]);
					} catch (Exception e) {
					}
				}
			}
		}
	}

	/**
	 * Finds where the position the player wants to move is in respect to other
	 * locations.
	 *
	 * @return
	 */
	private Node findRelativePosition() {
		Integer endLocID = end.getLocationID();

		for (Integer id : zoneLocId.keySet()) {
			if (id.equals(endLocID)) {
				Position p = this.end.getPos();
				if (zoneLocId.get(id).equals(1))
					return nodes[p.getX()][p.getY()];
				else if (zoneLocId.get(id).equals(2))
					return nodes[p.getX() + 10][p.getY()];
				else if (zoneLocId.get(id).equals(3))
					return nodes[p.getX() + 20][p.getY()];
				else if (zoneLocId.get(id).equals(4))
					return nodes[p.getX()][p.getY() + 10];
				else if (zoneLocId.get(id).equals(5))
					return nodes[p.getX() + 10][p.getY() + 10];
				else if (zoneLocId.get(id).equals(6))
					return nodes[p.getX() + 20][p.getY() + 10];
				else if (zoneLocId.get(id).equals(7))
					return nodes[p.getX()][p.getY() + 20];
				else if (zoneLocId.get(id).equals(8))
					return nodes[p.getX() + 10][p.getY() + 20];
				else if (zoneLocId.get(id).equals(9))
					return nodes[p.getX() + 20][p.getY() + 20];
			}
		}
		return null;
	}

	/**
	 * Comparator for queue in dijkstras, bases priority on path length
	 */
	private Comparator<SearchNode> nodeSorter = new Comparator<SearchNode>() {
		@Override
		public int compare(SearchNode o1, SearchNode o2) {
			if (o1.costToHere > o2.costToHere)
				return 1;
			else if (o1.costToHere == o2.costToHere)
				return 0;
			else
				return -1;
		}
	};

	/**
	 * Action Listener for timer which pops a move
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (getPath() != null) {
			if (getPath().isEmpty()) {
				timer.stop();
			} else {
				controller.moveToPos(getPath().pop());
			}
		}
	}

	/**
	 * Draws the 9x9 location map
	 */
	public String toString() {
		String map = "\n\n";
		for (int y = 0; y < length; y++) {
			if (y % 10 == 0)
				map += "\n\n";
			for (int x = 0; x < length; x++) {
				if (x % 10 == 0)
					map += "\t";
				String n = "";
				if (nodes[x][y] == null)
					n = "n";
				else if (nodes[x][y].t.getGameObject() == null)
					n = "+";
				else
					n = "O";
				map += n + " ";
			}
			map += "\n";
		}
		return map;
	}

	/**
	 * Get path
	 *
	 * @return path - shows path of algorithm
	 */
	public Stack<Tile> getPath() {
		return path;
	}

	/**
	 * Set path
	 *
	 * @param path - sets path of algorithm
	 */
	public void setPath(Stack<Tile> path) {
		this.path = path;
	}
}

/**
 * SearchNode for dijkstras algorithm for cluedo
 *
 */
class SearchNode {
	public int costToHere;
	public Node node;
	public Node from;

	public SearchNode(int c, Node n, Node f) {
		costToHere = c;
		node = n;
		from = f;
	}
}

/**
 * Node for dijkstras algorithm, used in cluedo
 *
 */
class Node {
	public boolean visited;
	public int x;
	public int y;
	public HashSet<Node> neighbours;
	public Node pathFrom;
	public Tile t;

	public Node(boolean visited, int x, int y, Tile t) {
		this.visited = visited;
		this.x = x;
		this.y = y;
		neighbours = new HashSet<Node>();
		this.t = t;
	}

	public String toString() {
		return x + ":" + y + "\t|" + t;
	}

}
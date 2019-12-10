package clientserver;

import java.util.Scanner;

import core.Board;
import core.GameSystem;
import core.GameSystem.Direction;
import gameobjects.Player;
import iohandling.BoardWriter;
import items.Banana;
import items.Item;
import tile.Tile;
import util.Position;
/**
 * Class that is the controller between the server and game logic of the program, this holds the link between two main parts of the program, the server and
 * the game logic, this class contains a parser that parses the message sent from the server, updates the game board in regards of what was sent from the
 * client and sends something back through the server
 *
 * @author Simon Glew and Jack Slater
 */
public class ServerController {
	private GameSystem gameSystem;
	private Server server;

	/**
	 * Constructor that gets called when a server is made and creates a new game system object
	 *
	 * @param server - the server that the controller is connected to
	 */
	public ServerController(Server server) {
		this.server = server;
		this.gameSystem = new GameSystem(this);
	}

	/**
	 * Getter for getting the board from the game system to be send back through the server
	 *
	 * @return board - current board state of the game
	 */
	public Board requestBoard() {
		return gameSystem.getBoard();
	}

	/**
	 * Start of the top down parser that is used for deciphering what was sent from the client
	 *
	 * @param message - Message sent from the server
	 * @return String - action needed for sending correct information back to client from server
	 */
	public String parseInput(PlayerCommand message) {
		Scanner s = new Scanner(message.getMessage());

		if (s.hasNext()) {
			String action = s.next();
			if (action.equals("move")) {//If move
				return parseMoveCommand(s);
			} else if (action.equals("login")) {//If Login
				return parseLoginCommand(s);
			} else if (action.equals("drop")) {//If drop
				return parseDropItemCommand(s);
			} else if (action.equals("siphon")) {//If siphon
				return parseSiphonBananaCommand(s);
			} else if (action.equals("use")) {//If use
				return parseUseItemCommand(s);
			} else if (action.equals("pickup")) {//If pickup
				return parsePickupItemCommand(s);
			}

			s.close();
			return "false";//should get here
		}

		s.close();
		return "false";//shouldnt get here
	}

	/**
	 * Method that is called if pickup is the start of the player message, this finds the player and the tile the player is currently on and puts that item
	 * on the tile into the the players inventory
	 *
	 * @param s - Scanner at current point along message
	 * @return String - action needed for sending correct information back to client from server
	 */
	public String parsePickupItemCommand(Scanner s) {
		try {
			Player player = getPlayerByUserName(s.next());

			Tile t = gameSystem.getBoard().getLocationById(player.getLocation().getId())
					.getTileAtPosition(player.getPosition());

			if (t.getGameObject() == null) {
				return "false";
			}
			if (!(t.getGameObject() instanceof Item)) {
				return "false";
			}
			Item i = (Item) t.getGameObject();

			player.getInventory().add(i);
			return "true";
		} catch (Exception e) {
			return "false";
		}
	}

	/**
	 * Method that is called if login is the start of the player message, this checks if the player is already logged in and if not it puts the player at
	 * the correct spot, either in one of the spawn locations or in the position that the player was in before he disconnects
	 *
	 * @param s - Scanner at current point along message
	 * @return String - action needed for sending correct information back to client from server
	 */
	public String parseLoginCommand(Scanner s) {
		try {
			String name = s.next();
			Player p = gameSystem.getBoard().getPlayer(name);

			if (p != null && p.isLoggedIn()) {
				return "fail login";
			} else if (p != null && !p.isLoggedIn()) {
				gameSystem.getBoard().getLocationById(p.getLocation().getId()).getTileAtPosition(p.getPosition())
						.setGameObject(p);
				p.setLoggedIn(true);
				return "true";
			} else {
				if (!(gameSystem.getBoard().getLocationById(0).getTileAtPosition(new Position(5, 5))
						.getGameObject() instanceof Player)) {
					p = new Player(name, 0, new Position(5, 5), gameSystem.getBoard());
				} else if (!(gameSystem.getBoard().getLocationById(0).getTileAtPosition(new Position(4, 5))
						.getGameObject() instanceof Player)) {
					p = new Player(name, 0, new Position(4, 5), gameSystem.getBoard());
				} else if (!(gameSystem.getBoard().getLocationById(0).getTileAtPosition(new Position(4, 4))
						.getGameObject() instanceof Player)) {
					p = new Player(name, 0, new Position(4, 4), gameSystem.getBoard());
				} else if (!(gameSystem.getBoard().getLocationById(0).getTileAtPosition(new Position(5, 4))
						.getGameObject() instanceof Player)) {
					p = new Player(name, 0, new Position(5, 4), gameSystem.getBoard());
				}else{
					return "fail login";
				}
				gameSystem.getBoard().addPlayer(name, p);
				gameSystem.getBoard().getLocationById(p.getLocation().getId()).getTileAtPosition(p.getPosition())
						.setGameObject(p);
				p.setLoggedIn(true);
				return "true";
			}
		} catch (Exception e) {
			return "false";
		}
	}

	/**
	 * Method that is called if move is the start of the player message, this finds the player and the direction that the player wants to move and moves the
	 * player on the board if it is allowe
	 *
	 * @param s - Scanner at current point along message
	 * @return String - action needed for sending correct information back to client from server
	 */
	public String parseMoveCommand(Scanner s) {
		try {
			Player player = getPlayerByUserName(s.next());
			Direction direction = convertToDirection(s.next());

			if (direction == null)
				return "false";
			if (player == null)
				return "false";

			gameSystem.movePlayer(player, direction);
			return "true";

		} catch (Exception e) {
			return "false";
		}
	}

	/**
	 * Method that makes a string of the direction and returns it as a direction
	 *
	 * @param s - direction to convert to direction
	 * @return direction that the string relates to
	 */
	public Direction convertToDirection(String s) {
		if (s.toLowerCase().equals("north")) {
			return Direction.NORTH;
		}
		if (s.toLowerCase().equals("south")) {
			return Direction.SOUTH;
		}
		if (s.toLowerCase().equals("east")) {
			return Direction.EAST;
		}
		if (s.toLowerCase().equals("west")) {
			return Direction.WEST;
		}
		return null;
	}

	/**
	 * Method that is called if drop is the start of the player message, this finds the player and and the item you want to drop and drops the item onto the
	 * board
	 *
	 * @param s - Scanner at current point along message
	 * @return String - action needed for sending correct information back to client from server
	 */
	public String parseDropItemCommand(Scanner s) {
		try {
			Player player = getPlayerByUserName(s.next());
			int indexOfItem = s.nextInt();
			Item item = player.getInventory().get(indexOfItem);

			gameSystem.playerDropItem(player, item);
			return "true";
		} catch (Exception e) {
			return "false";
		}
	}

	/**
	 * Method that is called if siphon is the start of the player message, this finds the player and the banana you want to siphon and siphons the banana
	 * onto the board
	 *
	 * @param s - Scanner at current point along message
	 * @return String - action needed for sending correct information back to client from server
	 */
	public String parseSiphonBananaCommand(Scanner s) {
		try {
			Player player = getPlayerByUserName(s.next());
			int indexOfItem = s.nextInt();
			Item item = player.getInventory().get(indexOfItem);

			Banana b = (Banana) item;
			if (gameSystem.playerSiphonBanana(player, b)) {
				return "endgame";
			}
			return "true";
		} catch (Exception e) {
			return "false";
		}

	}

	/**
	 * Method that is called if use is the start of the player message, this finds the player and the item you want to use and uses the item
	 * on the current player
	 *
	 * @param s - Scanner at current point along message
	 * @return String - action needed for sending correct information back to client from server
	 */
	public String parseUseItemCommand(Scanner s) {
		try {
			Player player = getPlayerByUserName(s.next());
			int indexOfItem = s.nextInt();
			Item item = player.getInventory().get(indexOfItem);

			gameSystem.playerUseItem(player, item);
			return "true";
		} catch (Exception e) {
			return "false";
		}
	}

	/**
	 * Getter for a player when given a username of a player
	 *
	 * @param name - username of player you want
	 * @return player - player that uses that username
	 */
	public Player getPlayerByUserName(String name) {
		return gameSystem.getBoard().getPlayer(name);
	}

	/**
	 * Method that gets called each second of the time thread and changes the npc movement
	 *
	 * @param time - current server time
	 */
	public void tick(int time){
		gameSystem.tick(time);
	}

	/**
	 * Method that gets the current server time
	 *
	 * @return time - current Server time
	 */
	public int getServerTime(){
		return server.getServerTime();
	}

	/**
	 * Calls the broadcast method with passing in the correct ID that you want send the message too, this is used for the popups
	 *
	 * @param message - message that you want to send
	 * @param p - Player that you want to send the message to
	 */
	public void broadcastPlayerMessage(String message, Player p){
		server.broadcast(new Packet("popupOne", null ,message, 0), server.getID(p.getUserName()));
	}

	/**
	 * Calls the broadcast method with giving it a message to send, this is used for the popups
	 *
	 * @param message - message that you want to send
	 */
	public void broadcastGameMessage(String message){
		server.broadcast(new Packet("popup", null ,message, 0), 0);
	}

	/**
	 * Calls the broadcast method with giving it a message to send, this is used for the popups
	 *
	 * @param message - message that you want to send
	 * @param p - message you do not want sent to the player
	 */
	public void broadcastBarOnePlayer(String message, Player p){
		server.broadcast(new Packet("popupBarOne", null, message, 0), server.getID(p.getUserName()));
	}

	/**
	 * Calls the broadcast method with giving it a board to send
	 *
	 * @param b - board that you want to send
	 */
	public void broadcastBoard(Board b){
		server.broadcast(new Packet("board", BoardWriter.writeBoardToString(b), null, getServerTime()), 0);
	}
}

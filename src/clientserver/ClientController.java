 package clientserver;

import java.util.ArrayList;

import clientserver.*;
import core.Board;
import core.GameSystem.Direction;
import gui.GUI;
import gui.GameOver;
import gui.UltimateDijkstras;
import core.Location;
import gameobjects.Player;
import iohandling.BoardWriter;
import items.Item;
import renderer.Renderer;
import tile.Tile;
import util.Position;

/**
 * Class that is the controller between the client and GUI of the program, this holds the link between three main parts of the program, the client, GUI and
 * renderer of the GUI
 *
 * @author Simon Glew and Kyal Bond
 */
public class ClientController {
	private Client client;
	private GUI gui;
	private Renderer renderer;
	private Board board;
	private int time;
	private UltimateDijkstras uDijkstras;

	/**
	 * Constructor that gets called when the client is called, that makes this controller object
	 *
	 * @param c - Client connected to the controller
	 */
	public ClientController(Client c) {
		this.client = c;
		renderer = new Renderer();
		uDijkstras = null;
		drawBoard();
		time = 0;
	}

	/**
	 * Method that creates a new GUI object when called within the client, this allows us to wait for a successful login message before creating a GUI
	 */
	public void showGUI() {
		gui = new GUI(this);
	}

	/**
	 * Method that hides the GUI, this is used when the client is disconnected from the server
	 */
	public void hideGUI() {
		if (gui != null) {
			gui.hideGUI();
		}
	}

	/**
	 * Method that returns the name of client that the controller is connected to
	 *
	 * @return username - Client/Players username
	 */
	public String getName() {
		return client.getUsername();
	}

	/**
	 * Method that returns the amount of bananas of the player joined to the client
	 *
	 * @return bananaCount - Players number of bananas
	 */
	public int getBananaCount() {
		if(board == null)return 0;
		return board.getPlayer(getName()).getNumOfBananas();

	}

	/**
	 * Method that calls the rotate left method within the renderer, this rotates the view counterclockwise
	 */
	public void rotateLeft() {
		renderer.rotateCounterClockwise();
		drawBoard();
	}

	/**
	 * Method that calls the rotate right method within the renderer, this rotates the view clockwise
	 */
	public void rotateRight() {
		renderer.rotateClockwise();
		drawBoard();
	}

	/**
	 * Method that calls the sendMessage method within the client, this calls a method that sends a method to the server
	 *
	 * @param msg - Message to be send through the server
	 */
	public void sendMessage(PlayerCommand msg) {
		client.sendMessage(msg);
	}

	/**
	 * Method that draws the board using the renderer and showing it onto the GUI
	 */
	public void drawBoard() {
		if (board != null && gui != null) {
			gui.showBoard(renderer.paintBoard(board, board.getPlayer(client.getUsername()), 1000, 800, time));
		}
	}

	/**
	 * Method that stores the current board in a local variable board within the controller
	 *
	 * @param board - Current state of the board
	 */
	public void sendBoard(Board board) {
		this.board = board;
		drawBoard();
	}

	/**
	 * Method that highlights the tile at x,y on the gameFrame in GUI
	 *
	 * @param x - x-pos
	 * @param y - y-pos
	 */
	public void selectTile(int x, int y) {
		if (board != null) {
			Position p = renderer.isoToIndex(x, y);
			Tile t = board.getPlayer(getName()).getLocation().getTileAtPosition(p);
			renderer.selectTile(t);
			drawBoard();
		}
	}

	/**
	 * Gets tile at x,y on the gameFrame in GUI
	 *
	 * @param x x-pos
	 * @param y y-pos
	 * @return tile - Tile to get from the position
	 */
	public Tile getTile(int x, int y) {
		Position p = renderer.isoToIndex(x, y);
		return board.getPlayer(getName()).getLocation().getTileAtPosition(p);
	}

	/**
	 * Moves the player in the given direction once
	 *
	 * @param dir - direction to move
	 */
	public void moveSinglePos(String dir) {
		if(uDijkstras != null ){
			uDijkstras.setPath(null);
		}

		//Get real direction from string input
		Direction d = null;
		if (dir.equals("N"))
			d = Direction.NORTH;
		else if (dir.equals("E"))
			d = Direction.EAST;
		else if (dir.equals("S"))
			d = Direction.SOUTH;
		else if (dir.equals("W"))
			d = Direction.WEST;

		//Get direction if screen rotated
		Direction temp = Location.getRelativeDirection(d, renderer.viewingDir);

		Location loc = board.getPlayer(getName()).getLocation();
		Tile t = loc.getTileInDirection(board.getPlayer(getName()).getPosition(), temp);

		//Move player
		if (t != null) {
			Direction direction = Location.getDirDijkstras(board.getPlayer(getName()).getTile(), t);
			if (direction != null) {
				String command = "move " + getName() + " " + direction.toString();
				sendMessage(new PlayerCommand(command));
			}
			drawBoard();
		}
	}

	/**
	 * Move player to select position from current position using dijkstras algorithm
	 *
	 * @param x - x-pos to move
	 * @param y - y-pos to move
	 */
	public void moveWithUltimateDijkstras(int x, int y) {
		//Reset path if moving
		if (uDijkstras != null)
			uDijkstras.setPath(null);

		//Get tile at player position and x,y coord
		if (board != null) {
			Position p = renderer.isoToIndex(x, y);
			Location loc = board.getPlayer(getName()).getLocation();
			Tile t = loc.getTileAtPosition(p);
			renderer.selectTile(t);

			//Create path to destination and start timer to move
			if (t != null) {
				uDijkstras = new UltimateDijkstras(this, board.getPlayer(getName()).getTile(), loc, t);
				uDijkstras.createPath();

				uDijkstras.startTimer();
			}
		}
	}

	/**
	 * Moves Player to tile (for dijkstras algorithm)
	 *
	 * @param t - tile to move to
	 */
	public void moveToPos(Tile t) {
		Tile from = null;

		//Checks that players position is the correct position
		if(uDijkstras.oldTile == null){
			from = board.getPlayer(getName()).getTile();
			uDijkstras.oldTile = from;
		}else{
			from = uDijkstras.oldTile;
		}

		Direction d = Location.getDirDijkstras(from, t);

		//Move player to tile
		if (d != null) {
			String command = "move " + getName() + " " + d.toString();
			sendMessage(new PlayerCommand(command));
		}
		uDijkstras.oldTile = t;
		drawBoard();
	}

	/**
	 * Gets the inventory using the board and the name of the player and returns it
	 *
	 * @return inventory - Players inventory
	 */
	public ArrayList<Item> getInventory() {
		if (board != null) {
			return board.getPlayer(getName()).getInventory();
		}
		return null;
	}

	/**
	 * Method that updates the time variable when called from the server, this method gets called every second
	 *
	 * @param time - Current time of the server
	 */
	public void updateTime(int time){
		this.time = time;
		sendBoard(board);
	}

	/**
	 * Gets and returns the current time
	 *
	 * @return time - Current time of the server
	 */
	public int getTime(){
		return this.time;
	}

	/**
	 * Method that gets called from within GUI that will drop an item, this constructs a Player Command to be sent over the server
	 *
	 * @param index - index of item you want to drop
	 */
	public void dropItemPlayer(int index) {
		String name = getName();

		String command = "drop " + name + " " + index;
		sendMessage(new PlayerCommand(command));
	}

	/**
	 * Method that gets called from within GUI that will use an item, this constructs a Player Command to be sent over the server
	 *
	 * @param index - index of item you want to use
	 */
	public void useItem(int index) {
		String name = getName();

		String command = "use " + name + " " + index;
		sendMessage(new PlayerCommand(command));
	}

	/**
	 * Method that gets called from within GUI that will siphon a banana in your inventory, this constructs a Player Command to be sent over the server
	 *
	 * @param index - index of banana you want to siphon
	 */
	public void siphonBananaPlayer(int index) {
		String name = getName();

		String command = "siphon " + name + " " + index;
		sendMessage(new PlayerCommand(command));
	}

	/**
	 * Gets the inventory item at the required index and returns it
	 *
	 * @param i - index of item you want
	 * @return item at index i - either item or a null if no item is there
	 */
	public Item getInventoryItem(int i) {
		return board.getPlayer(getName()).getInventory().get(i);
	}

	/**
	 * Method that gets passed in a tile and if there is an item on that tile it constructs a Player Command to be sent over the server
	 *
	 * @param t - Tile that you want to pickup from
	 */
	public void pickupItemPlayer(Tile t) {
		String name = getName();
		if (t.getGameObject() != null) {
			String command = "pickup " + name;
			sendMessage(new PlayerCommand(command));
		}
	}

	/**
	 * Method that gets called from the client when a player has won, and creates a new game over window
	 *
	 * @param playerName - Name of winning player
	 */
	public void showEndGameScreen(String playerName) {
		new GameOver(playerName);
		sendMessage(new PlayerCommand("close"));
	}

	/**
	 * Returns the current player which the client is connected too
	 *
	 * @return currentPlayer - Player that is connected to the client
	 */
	public Player getPlayer() {
		return board.getPlayer(client.getUsername());
	}

	/**
	 * Shows a message onto the popup on the renderer screen
	 *
	 * @param s - String to be shown
	 */
	public void showMessage(String s){
		gui.beginHarambeAnimation();
		renderer.setMessage(s, time, 4);
	}
}

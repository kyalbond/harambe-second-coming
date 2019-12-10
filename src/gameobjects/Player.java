package gameobjects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import core.Board;
import core.GameSystem;
import core.GameSystem.Direction;
import items.FloatingDevice;
import items.Item;
import core.Location;
import tile.Tile;
import tile.WaterTile;
import util.Position;

/**
 * Class that holds all the information for the players within the game, such as their name and their current location
 *
 * @author Jack Slater
 */
public class Player extends GameObject {

	private String userName;
	private ArrayList<Item> inventory;
	private int locationID;
	private Position pos;
	private Board board;
	private Direction facing = Direction.SOUTH;

	private int numOfBananas;
	private boolean hasFloatingDevice = false;

	private final String IMG_PRE = "assets/game_objects/player/player";
	private final String IMG_POST = ".png";

	private final Integer INVENTORY_LIMIT = 10;

	private boolean loggedIn = false;

	/**
	 * Constructor of the player that puts all the values into the required variables and gives the player a empty inventory
	 *
	 * @param name - Name of player
	 * @param locationID - location of player
	 * @param pos - position of player
	 * @param board - current board
	 */
	public Player(String name, int locationID, Position pos, Board board) {
		this.locationID = locationID;
		this.inventory = new ArrayList<Item>();
		this.pos = pos;
		this.board = board;
		this.userName = name;
	}

	/**
	 * Sets the number of bananas for the player
	 *
	 * @param numOfBananas - number of bananas the player will have
	 */
	public void setNumOfBananas(int numOfBananas) {
		this.numOfBananas = numOfBananas;
	}

	/**
	 * Getter for the players username
	 *
	 * @return username - Player's username
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Getter for the direction that the player is currently facing
	 *
	 * @return facing - direction that player is facing
	 */
	public Direction getFacing() {
		return facing;
	}

	/**
	 * Getter for the boolean to see if the player is logged in
	 *
	 * @return loggedIn - boolean if player is logged in, true is yes, false if not
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}

	/**
	 * Setter for if the player is logged in
	 *
	 * @param loggedIn - boolean if player is logged in, true is yes, false if not
	 */
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	/**
	 * Getter for the inventory of the player
	 *
	 * @return inventory - inventory of player
	 */
	public ArrayList<Item> getInventory() {
		return inventory;
	}

	/**
	 * Check to see if current inventory is full
	 *
	 * @return boolean - weather inventory is full or not
	 */
	public boolean inventoryIsFull() {
		return inventory.size() == INVENTORY_LIMIT;
	}

	/**
	 * Getter for the current location of the player
	 *
	 * @return location - current location of the player
	 */
	public Location getLocation() {
		return board.getLocationById(locationID);
	}

	/**
	 * Setter for the current location of the player by using a location
	 *
	 * @param location - location that player is moved too
	 */
	public void setLocation(Location location) {
		this.locationID = location.getId();
	}

	/**
	 * Setter for the current location of the player by using a location id
	 *
	 * @param locationID - location that player is moved too
	 */
	public void setLocation(int locationID) {
		this.locationID = locationID;
	}

	/**
	 * Getter for the current tile that the player is on
	 *
	 * @return Tile - current tile which player is standing on
	 */
	public Tile getTile() {
		return getLocation().getTileAtPosition(pos);
	}

	/**
	 * Setter for the current tile of the player
	 *
	 * @param tile - tile to be set to player
	 */
	public void setTile(Tile tile) {
		this.pos = tile.getPos();
	}

	/**
	 * Getter for the number of bananas of the player
	 *
	 * @return numOfBananas - number of bananas currently held by player
	 */
	public int getNumOfBananas() {
		return numOfBananas;
	}

	/**
	 * Increases the amount of bananas in a persons inventory by a specific amount
	 *
	 * @param i - Amount that the players number of bananas needs to be incremented by
	 */
	public void increaseBananaCount(int i) {
		numOfBananas += i;
	}

	/**
	 * Attempts to pick up an item and returns weather it fails or not
	 *
	 * @param item - item wanting to be added into inventory
	 * @return boolean - weather if adding the item is successful
	 */
	public boolean pickUpItem(Item item) {
		return inventory.add(item);

	}

	/**
	 * Method that gets the file name of the player to be drawn, the file name changes depending on what variables are around it
	 *
	 * @param loc - Location of player
	 * @param pos - Position of player
	 * @param viewingDir - Direction that you are viewing
	 *
	 * @return fname - File name of the player
	 */
	public String getImage(Location loc, Position pos, Direction viewingDir) {
		String fname = IMG_PRE;
		if(hasFloatingDevice){
			fname += "F";
		}
		if(getTile() instanceof WaterTile){
			fname+="W";
		}
		fname += Location.getOtherRelativeDirection(facing, viewingDir).toString() + IMG_POST;

		return fname;
	}

	public String toString() {
		return "Player(" + userName + ")";
	}

	/**
	 * Getter for the current position of the player
	 *
	 * @return pos - current position of player
	 */
	public Position getPosition() {
		return pos;
	}

	/**
	 * Returns a string of the player that is used for saving into the map
	 *
	 * @return s - String that is used for saving
	 */
	public String toSaveString() {
		String s = "Player\n{\n" + userName + "," + numOfBananas + "," + locationID + "," + pos.getX() + "," + pos.getY() + ","
				+ facing.toString() + "," + loggedIn + "," + hasFloatingDevice + ",Inventory(";
		for(Item i: inventory){
			s += i.toString();
			s += ",";
		}
		s += ")\n}";
		return s;
	}

	/**
	 * Setter for the position of the player
	 *
	 * @param position - position that you want to change to
	 */
	public void setPosition(Position position) {
		pos = position;
	}

	/**
	 * Getter for the description of the object
	 *
	 * @return String - description of object
	 */
	public String getDescription(){
		return "This is Player: " + userName;
	}

	/**
	 * Setter for the direction of what direction the player is looking
	 *
	 * @param dir - direction the player wants to face
	 */
	public void setFacing(Direction dir) {
		facing = dir;
	}

	/**
	 * Setter for the floaty device on a player
	 *
	 * @param b - boolean that indicates weather the player should have a a floaty device
	 */
	public void setHasFloatingDevice(boolean b){
		hasFloatingDevice = b;
	}

	/**
	 * Getter for the floaty device on a player
	 *
	 * @return hasFloatingDevice - boolean that indicates weather the player should have a a floaty device
	 */
	public boolean getHasFloatingDevice(){
		return hasFloatingDevice;
	}
}

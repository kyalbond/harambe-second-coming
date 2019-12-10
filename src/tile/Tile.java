package tile;

import java.awt.image.BufferedImage;

import core.GameSystem.Direction;
import gameobjects.GameObject;
import util.Position;

/**
 * Class that holds all the information for an tile, this is an abstract class that holds all the information for each of the tiles on the map
 * 
 * @author Jack Slater
 *
 */
public abstract class Tile {

	protected Position pos;
	protected GameObject gameObject;
	protected String fname;
	protected int locationID;
	
	/**
	 * Constructor that puts the parameters into local variables
	 * 
	 * @param pos - Position of tile
	 * @param gameObject - Game object on the tile
	 */
	public Tile(Position pos, GameObject gameObject) {
		this.pos = pos;
		this.gameObject = gameObject;
	}

	/**
	 * Getter for the position of the tile
	 * 
	 * @return pos - Position of the tile
	 */
	public Position getPos() {
		return pos;
	}

	/**
	 * Setter for the position of the tile
	 * 
	 * @param pos - position of the tile you are wanting to set it to
	 */
	public void setPos(Position pos) {
		this.pos = pos;
	}
	
	/**
	 * Getter for the game object on the tile
	 * 
	 * @return gameObject - game object on the tile
	 */
	public GameObject getGameObject() {
		return gameObject;
	}
	
	/**
	 * Setter for the game object on the tile
	 * 
	 * @param gameObject - game object you are trying to put onto the tile
	 */
	public void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
	}
	
	/**
	 * Gets the filename of the tile you are trying to draw
	 * 
	 * @param viewing - current viewing direction
	 * @return fname - filename of the tile
	 */
	public String getImage(Direction viewing){
		return fname;
	}
	
	/**
	 * Getter for the location ID of the tile
	 * 
	 * @return locationID - the current locationID of the tile
	 */
	public int getLocationID(){
		return locationID;
	}
	
	/**
	 * Setter for the location ID of the tile
	 * 
	 * @param i - locationID you are trying to give to the tile
	 */
	public void setLocationID(int i){
		locationID = i;
	}
	
}

package tile;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import gameobjects.GameObject;
import util.Position;

/**
 * Class that holds all the information to do with the grassTile object within the game, this is a couple of special messages that distinguish the 
 * objects such as file names and what type of item it is within the toString method 
 * 
 * @author Jack Slater
 *
 */
public class GrassTile extends Tile {

	/**
	 * Constructor that calls the super constructor
	 * 
	 * @param pos - position of tile
	 * @param gameObject - game object on tile
	 */
	public GrassTile(Position pos, GameObject gameObject) {
		super(pos, gameObject);
		fname = "assets/tiles/grassTile.png";
	}

	public String toString() {
		String s = "Grass";
		if (gameObject != null) {
			s += "(" + gameObject.toString() + ")";
		}
		return s;
	}

}

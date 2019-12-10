package gameobjects;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Class that holds all the information to do with the Building object within the game, this is a couple of special messages that distinguish the 
 * objects such as file names and what type of item it is within the toString method 
 * 
 * @author Jack Slater
 *
 */
public class Building extends GameObject {
	
	/**
	 * Constructor that sets file name of object
	 */
	public Building() {
		fname = "assets/game_objects/building/building.png";
	}
	
	/**
	 * Getter for the description of the object
	 */
	public String getDescription() {
		return "Just a building";
	}

	public String toString() {
		return "Building";
	}
}

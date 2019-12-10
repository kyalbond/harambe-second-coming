package gameobjects;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Class that holds all the information to do with the Tree object within the game, this is a couple of special messages that distinguish the 
 * objects such as file names and what type of item it is within the toString method 
 * 
 * @author Jack Slater
 *
 */
public class Tree extends GameObject{

	/**
	 * Constructor that sets file name of object
	 */
	public Tree(){
		fname = "assets/game_objects/tree/tree.png";
	}
	
	/**
	 * Getter for the description of the object
	 */
	public String getDescription(){
		return "Just a Tree";
	}
	
	public String toString() {
		return "Tree";
	}
}

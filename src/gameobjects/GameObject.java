package gameobjects;

import java.awt.image.BufferedImage;

import core.GameSystem.Direction;
import core.Location;
import util.Position;

/**
 * Abstract class that holds all the information for GameObjects, these are objects such as Doors, Wall and Fences. This class holds the filename and the
 * description of the object
 * 
 * @author Jack Slater
 */
public abstract class GameObject {
	protected String fname;
	private String description;
	
	/**
	 * Getter for a file name of the object
	 * 
	 * @param loc - Current location of the object
	 * @param pos - Current position of the object
	 * @param viewingDir - Current viewing direction of the object
	 * @return Filename - filename of the object you are wanting
	 */
	public String getImage(Location loc, Position pos, Direction viewingDir){
		return fname;
	}
	
	/**
	 * Getter for the description of the object
	 * 
	 * @return description - Description of the object
	 */
	public String getDescription(){
		return description;
	}
}

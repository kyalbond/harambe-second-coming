package gameobjects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import core.GameSystem.Direction;
import core.Location;
import tile.Tile;
import util.Position;

/**
 * Class that holds all the information to do with the Fence object within the game, such as the location of the fence
 * 
 * @author Jack Slater
 *
 */
public class Fence extends GameObject {
	
	final String IMG_PRE = "assets/game_objects/fence/fence";
	final String IMG_POST = ".png";
	
	//Calls super constructor
	public Fence(){}

	/**
	 * Method that gets the file name of the fence to be drawn, the file name changes depending on what fences are around it
	 * 
	 * @param loc - Location of fence
	 * @param pos - Position of fence
	 * @param viewing - Direction that you are viewing
	 * 
	 * @return fname - File name of the fence
	 */
	public String getImage(Location loc, Position pos, Direction viewing){
		
		String fname = IMG_PRE;

		Tile n = loc.getTileInDirection(pos, Location.getRelativeDirection(Direction.NORTH, viewing));
		if(n != null){
			if(n.getGameObject() instanceof Fence){
				fname += "N";
			}
		}
		Tile e = loc.getTileInDirection(pos, Location.getRelativeDirection(Direction.EAST, viewing));
		if(e != null){
			if(e.getGameObject() instanceof Fence){
				fname += "E";
			}
		}
		Tile s = loc.getTileInDirection(pos, Location.getRelativeDirection(Direction.SOUTH, viewing));
		if(s != null){
			if(s.getGameObject() instanceof Fence){
				fname += "S";
			}
		}
		
		Tile w = loc.getTileInDirection(pos, Location.getRelativeDirection(Direction.WEST, viewing));
		if(w != null){
			if(w.getGameObject() instanceof Fence){
				fname += "W";
			}
		}
		fname+=IMG_POST;

		return fname;
	}
	
	/**
	 * Gets the description of the object
	 * 
	 * @return description - description of the object
	 */
	public String getDescription(){
		return "Just a Fence";
	}
	
	public String toString() {
		return "Fence";
	}
}

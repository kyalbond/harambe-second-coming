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
 * Class that holds all the information to do with the wall object within the game, this is its file name and the code and its description
 * 
 * @author Jack Slater
 *
 */
public class Wall extends GameObject {
	
	final String IMG_PRE = "assets/game_objects/wall/wall";
	final String IMG_POST = ".png";
	
	public Wall(){ }

	/**
	 * Method that gets the file name of the wall to be drawn, the file name changes depending on what wall are around it
	 * 
	 * @param loc - Location of wall
	 * @param pos - Position of wall
	 * @param viewing - Direction that you are viewing
	 * 
	 * @return fname - File name of the wall
	 */
	public String getImage(Location loc, Position pos, Direction viewing){
		String fname = IMG_PRE;
		Tile n = loc.getTileInDirection(pos, Direction.NORTH);
		if(n != null){
			if(n.getGameObject() instanceof Wall){
				fname += "N";
			}
		}
		Tile e = loc.getTileInDirection(pos, Direction.EAST);
		if(e != null){
			if(e.getGameObject() instanceof Wall){
				fname += "E";
			}
		}
		Tile s = loc.getTileInDirection(pos, Direction.SOUTH);
		if(s != null){
			if(s.getGameObject() instanceof Wall){
				fname += "S";
			}
		}
		
		Tile w = loc.getTileInDirection(pos, Direction.WEST.EAST);
		if(w != null){
			if(w.getGameObject() instanceof Wall){
				fname += "W";
			}
		}
		fname+=IMG_POST;
		return fname;
	}
	
	/**
	 * Getter for the description of the wall
	 * 
	 * @return description - Description of the object
	 */
	public String getDescription(){
		return "Just a Wall";
	}
	
	public String toString() {
		return "Wall";
	}
}

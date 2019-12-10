package gameobjects;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import core.Location;
import core.GameSystem.Direction;
import util.Position;

/**
 * Class that holds all the information to do with the Door object within the game, such as the door position, the code of the door and the locationID
 * 
 * @author Jack Slater
 *
 */
public class Door extends GameObject {

	int locationID;
	int code;
	Position doorPosition;
	
	/**
	 * Sets the code and location id of the door
	 * 
	 * @param code - code of the door
	 * @param locationid - location id of the door
	 */
	public Door(int code, int locationid) {
		this.doorPosition = new Position(5, 9);
		this.code = code;
		this.locationID = locationid;
		fname = "assets/game_objects/door/doorNORTH.png";

	}
	
	/**
	 * Getter for the description of the door
	 */
	public String getDescription() {
		return "This is a door. I wonder what's inside.";
	}

	/**
	 * Getter for the position of the door
	 * 
	 * @return position - Position of the door
	 */
	public Position getDoorPosition() {
		return doorPosition;
	}
	
	public String toString() {
		return "Door(" + code + "," + locationID + "," + doorPosition.getX() + "," + doorPosition.getY() + ")";
	}
	
	/**
	 * Getter of the location id for the door
	 * 
	 * @return locationID - id of the location that the door belongs to
	 */
	public int getLocationID() {
		return locationID;
	}

	/**
	 * Setter for the door position for the door
	 * 
	 * @param position - door position that the door is going to change too
	 */
	public void setDoorPosition(Position position) {
		doorPosition = position;
	}
	
	/**
	 * Gets the file name of the door depending on different variables
	 * 
	 * @param loc - Location of the door
	 * @param pos - Position of the door
	 * @param viewingDir - Direction we are currently viewing from
	 * @return fname - filename of the door
	 */
	public String getImage(Location loc, Position pos, Direction viewingDir){
		fname = "assets/game_objects/door/door" + viewingDir.toString() + ".png";
		return fname;
	}
}
	

package tile;

import core.GameSystem.Direction;
import gameobjects.GameObject;
import util.Position;

/**
 * A tile type that will link its location to the door on the location it came from
 * @author Jack
 *
 */
public class DoorOutTile extends Tile {

	int LocationID;

	int outLocationID;

	Position doorPos;

	final String IMG_PRE = "assets/tiles/doorOut/doorOut";
	final String IMG_POST = ".png";

	public DoorOutTile(Position pos, GameObject gameObject, int locationID, Position doorPos) {
		super(pos, gameObject);
		
		this.locationID = locationID;
		this.outLocationID = locationID;
		this.doorPos = doorPos;
		fname = "assets/tiles/doorOut/doorOutNORTH.png";
	}
	
	/**
	 * Method returning the ID of the location the outTile leads too
	 * @return id
	 */
	public int getOutLocationID() {
		return outLocationID;
	}
	
	/**
	 * Method returning the position of the door the outTile leads too
	 * @return doorPos
	 */
	public Position getDoorPos() {
		return doorPos;
	}
	
	/**
	 * Method returning the toString for this object, the toString is used by the parser to generate the board
	 * @return toString
	 */
	public String toString() {
		String s = "DoorOut(" + outLocationID + "," + doorPos.getX() + "," + doorPos.getY() + ")";
		if (gameObject != null) {
			s += "(" + gameObject.toString() + ")";

		}
		return s;
	}
	
	/**
	 * Method returning the URLto the image for this object, the toString is used by the renderer to generate the board
	 * @return toString
	 */
	public String getImage(Direction viewing) {
		fname = IMG_PRE + viewing.toString() + IMG_POST;
		return fname;
	}
}

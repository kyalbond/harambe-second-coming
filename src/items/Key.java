package items;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Class that holds all the information to do with the key object within the game, this is a couple of special messages that distinguish the
 * items such as file names and what type of item it is within the toString method 
 * 
 * @author Jack Slater
 *
 */
public class Key extends Item {
	
	int code;
	
	/**
	 * Method that calls the super constructor of the item and sets the code of the key
	 * 
	 * @param name - name of item
	 * @param code - code of the key
	 */
	public Key(String name, int code) {
		super(name, "A magical key, it may open something??",false);
		this.code = code;
		fname = "assets/game_objects/key/key.png";
	}

	/**
	 * Getter for the code on the key
	 * 
	 * @return code - code corresponding to the key
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * Setter for the code on the key
	 * 
	 * @param code - code wanting to be put onto the key
	 */
	public void setCode(int code) {
		this.code = code;
	}
	
	public String toString(){
		return "Key(" + name + ", " + code + ")";
	}

}

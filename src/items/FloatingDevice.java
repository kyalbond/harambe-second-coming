package items;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
/**
 * Class that holds all the information to do with the floating device object within the game, this is a couple of special messages that distinguish the
 * items such as file names and what type of item it is within the toString method 
 * 
 * @author Jack Slater
 *
 */
public class FloatingDevice extends Item {
	
	/**
	 * Method that calls the super constructor of the item
	 * 
	 * @param name - name of item
	 */
	public FloatingDevice(String name) {
		super(name, "A floating device, perhaps this will help you swim.. not like you're a penguin or anything", true);
		fname = "assets/game_objects/floatingDevice/floatingDevice.png";
	}
	
	public String toString(){
		return "FloatingDevice";
	}

}

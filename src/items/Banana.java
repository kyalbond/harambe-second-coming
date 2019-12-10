package items;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Class that holds all the information to do with the banana object within the game, this is a couple of special messages that distinguish the items such as
 * file names and what type of item it is within the toString method 
 * 
 * @author Jack Slater
 *
 */
public class Banana extends Item{
	public static String siphonMessage = "You siphon the power from the radiating member, Haramabe is within your grasp! Carry on Soldier!";

	/**
	 * Method that calls the super constructor of the item
	 * 
	 * @param name - name of item
	 */
	public Banana(String name) {
		super(name, "A sentient being taking form in a material object, behold the force of thy Harambe!" ,false);
			fname = "assets/game_objects/banana/banana.png";

	}
	
	public String toString(){	
		return "Banana";
	}
}

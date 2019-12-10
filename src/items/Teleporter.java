package items;

/**
 * Class that holds all the information to do with the Teleporter object within the game, this is a couple of special messages that distinguish the
 * items such as file names and what type of item it is within the toString method 
 * 
 * @author Jack Slater
 *
 */
public class Teleporter extends Item{
	
	/**
	 * Method that calls the super constructor of the item
	 * 
	 * @param name - name of item
	 */
	public Teleporter(String name) {
		super(name, "A magical orb eminating with power, rumour has it this will teleport you somewhere..", true);
		fname = "assets/game_objects/teleporter/teleporter.png";
	}
	
	public String toString(){
		return "Teleporter";
	}

}

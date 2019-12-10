package items;

/**
 * Class that holds all the information to do with the fish object within the game, this is a couple of special messages that distinguish the
 * items such as file names and what type of item it is within the toString method 
 * 
 * @author Jack Slater
 *
 */
public class Fish extends Item{

	/**
	 * Method that calls the super constructor of the item
	 * 
	 * @param name - name of item
	 */
	public Fish(String name) {
		super(name, "Wet and slimey, the temptation to eat is high, maybe somebody else would like it more", false);
		fname = "assets/game_objects/fish/fish.png";
	}
	
	public String toString(){
		return "Fish";
	}

}

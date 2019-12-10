package items;

/**
 * Class that holds all the information to do with the fishing rod object within the game, this is a couple of special messages that distinguish the
 * items such as file names and what type of item it is within the toString method 
 * 
 * @author Jack Slater
 *
 */
public class FishingRod extends Item{

	/**
	 * Method that calls the super constructor of the item
	 * 
	 * @param name - name of item
	 */
	public FishingRod(String name) {
		super(name, "Not exactly new, however if we're lucky the fish might not be on their fins today", true);
		fname = "assets/game_objects/fishingRod/fishingRod.png";
	}
	
	public String toString(){
		return "FishingRod";
	}

}

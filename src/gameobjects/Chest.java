package gameobjects;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import items.Item;

/**
 * Class that holds all the information to do with the Chest object within the game, this is its file name and the code and contents of the chest
 * 
 * @author Jack Slater
 *
 */
public class Chest extends GameObject {

	private int code;
	private Item contents;
	
	/**
	 * Sets the file name of the chest and sets its contents to null
	 */
	public Chest() {
		fname = "assets/game_objects/chest/chest.png";
		this.contents = null;
	}
	
	/**
	 * Sets the file name of the chest and sets its contents to the item that is parsed in as a parameter
	 * 
	 * @param contents - Item that is in the chest
	 */
	public Chest(Item contents) {
		fname = "assets/game_objects/chest/chest.png";
		this.contents = contents;
	}
	
	/**
	 * Getter for the code of the chest
	 * 
	 * @return code - code that corresponds to the chest
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Setter for the code of the chest
	 * 
	 * @param code - code that will correspond to the chest
	 */
	public void setCode(int code) {
		this.code = code;
	}
	
	/**
	 * Getter for the contents of the chest
	 * 
	 * @return contents - Item that is inside the chest, if no contents are in the chest null is returned
	 */
	public Item getContents() {
		return contents;
	}

	/**
	 * Sets the contents of the chest 
	 * 
	 * @param contents - contests that will correspond to the chest
	 */
	public void setContents(Item contents) {
		this.contents = contents;
	}

	/**
	 * Gets a description about the chest
	 */
	public String getDescription() {
		return "A locked Chest, " + "Code: " + code;
	}

	public String toString() {
		String s = "Chest(" + code;
		if (contents != null) {
			s += "," + contents.toString();
		}
		s += ")";
		return s;
	}
}

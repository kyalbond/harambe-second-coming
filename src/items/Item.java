package items;

import gameobjects.GameObject;
/**
 * Class that holds all the information for an item, this is an abstract class that holds all the information for each of the items on the map, maining
 * their name and their description
 * 
 * @author Jack Slater
 *
 */
public abstract class Item extends GameObject {

	protected String name;
	private String description;
	private boolean usable;

	/**
	 * Constructor that sets the variables for each item when it is created
	 * 
	 * @param name - Name of item
	 * @param description - Description of item
	 * @param usable - Weather the item is usable
	 */
	public Item(String name, String description, Boolean usable) {
		this.name = name;
		this.description = description;
		this.usable = usable;
	}
	
	/**
	 * Getter for weather the item is usable
	 * 
	 * @return boolean - weather the item is usable
	 */
	public boolean isUsable() {
		return usable;
	}
	
	/**
	 * Getter for the description of the item
	 * 
	 * @return String - description of the item
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter for the description of the item
	 * 
	 * @param description - New description of item
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Getter for the name of the item
	 * 
	 * @return String - name of the item
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for the name of the item
	 * 
	 * @param name - New name of item
	 */
	public void setName(String name) {
		this.name = name;
	}

}

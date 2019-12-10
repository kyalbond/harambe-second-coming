package util;

/**
 * Class that is used to store a x and y value to use within a location 
 * 
 * @author Simon Glew
 *
 */
public class Position {
	private final int x;
	private final int y;
	
	/**
	 * Constructor that puts the parameters into the required fields
	 * 
	 * @param x - x-pos of the position
	 * @param y - y-pos of the position
	 */
	public Position(int x, int y){		
		this.x = x;
		this.y = y;		
	}
	
	/**
	 * Getter for the x part of the position
	 * 
	 * @return x - x-pos of the position 
	 */
	public int getX() {
		return x;
	}

	/**
	 * Getter for the y part of the position
	 * 
	 * @return y - y-pos of the position 
	 */
	public int getY() {
		return y;
	}
	
	public String toString(){
		return "(" + x + ","+ y + ")";	
	}
}

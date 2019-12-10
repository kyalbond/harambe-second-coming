package clientserver;

import java.io.Serializable;
/**
 * Class that holds all the information for the object that gets sent from the server back to the client
 * 
 * @author Simon Glew
 */
public class Packet implements Serializable{

	private static final long serialVersionUID = 7677212855723082352L;
	private String type;
	private String board;
	private String message;
	private int time;
	
	/**
	 * Constructor that gets called every time a message is being send from the server back to the client
	 * 
	 * @param type - Type of message being sent
	 * @param board - The string holding the current board 
	 * @param message - Message being send along with the packet
	 * @param time - Current time of the server
	 */
	public Packet(String type, String board, String message, int time){
		this.type = type;
		this.board = board;
		this.message = message;
		this.time = time;
	}
	
	/**
	 * Getter for the time held within the packet
	 * 
	 * @return time - Current time of server
	 */
	public int getTime(){
		return this.time;
	}
	
	/**
	 * Getter for the type held within the packet
	 * 
	 * @return type - Type of message being send through
	 */
	public String getType(){
		return this.type;
	}
	
	/**
	 * Getter for the board held within the packet
	 * 
	 * @return board - Current board state within the server
	 */
	public String getBoard(){
		return this.board;
	}
	
	/**
	 * Getter for the message held within the packet
	 * 
	 * @return message - Message being sent along with the packet
	 */
	public String getMessage(){
		return this.message;
	}
}

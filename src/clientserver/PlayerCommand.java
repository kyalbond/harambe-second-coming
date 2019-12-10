package clientserver;
import java.io.*;

/**
 * Class that holds all the information for the object that gets sent from the client to the server
 * 
 * @author Simon Glew
 *
 */
public class PlayerCommand implements Serializable {

	protected static final long serialVersionUID = 1112122200L;
	private String message;
	
	/**
	 * Constructor that gets called every time a message is sent from the client to the server
	 * 
	 * @param message - Message being sent across
	 */
	public PlayerCommand(String message) {
		this.message = message;
	}
	
	/**
	 * Getter for the message held within the object
	 * 
	 * @return message - Message being sent along with the object
	 */
	public String getMessage() {
		return message;
	}
}


package exceptions;
/**
 * Class that holds the the exception that gets thrown when something fails in the gameLogic
 *
 * @author Simon Glew
 */
public class GameLogicException extends RuntimeException{
	public GameLogicException(String message){
		super(message);
	}
}

package exceptions;

/**
 * Class that holds the the exception that gets thrown when something fails in the parser
 * 
 * @author Jack Slater
 */
public class ParserException extends Exception {
	public ParserException(String message){
		super(message);
	}
}

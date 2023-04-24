package exceptions;

/**
 * Class that represents an exception that is 
 * thrown when the server is started with too many arguments.
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato   | fc56372
 * @author João Vieira   | fc45677
 */
public class IncorrectArgumentsServerException extends Exception {
    private String message;
    
    /**
     * Constructor for the TooManyArgumentsServerException class.
     * 
     * @param message The message to be displayed.
     */
    public IncorrectArgumentsServerException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}

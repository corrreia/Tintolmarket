package exceptions;

public class TooManyArgumentsServerException extends Exception {
    private String message;
    
    public TooManyArgumentsServerException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}

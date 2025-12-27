package mta.patmal.enigma.engine.exceptions;

public class InvalidInputException extends EnigmaException {
    
    public InvalidInputException(String message) {
        super(message);
    }
    
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}

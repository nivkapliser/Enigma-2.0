package mta.patmal.enigma.engine.exceptions;

public class EnigmaException extends Exception {
    
    public EnigmaException(String message) {
        super(message);
    }
    
    public EnigmaException(String message, Throwable cause) {
        super(message, cause);
    }
}

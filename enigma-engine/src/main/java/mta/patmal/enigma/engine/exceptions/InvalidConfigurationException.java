package mta.patmal.enigma.engine.exceptions;

public class InvalidConfigurationException extends EnigmaException {
    
    public InvalidConfigurationException(String message) {
        super(message);
    }
    
    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

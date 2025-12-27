package mta.patmal.enigma.engine.exceptions;

public class CodeNotConfiguredException extends EnigmaException {
    
    public CodeNotConfiguredException(String message) {
        super(message);
    }
    
    public CodeNotConfiguredException() {
        super("No code configured. Please configure a code first (command 3 or 4).");
    }
}

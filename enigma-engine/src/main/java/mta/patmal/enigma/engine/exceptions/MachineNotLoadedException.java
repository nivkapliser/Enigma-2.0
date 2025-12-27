package mta.patmal.enigma.engine.exceptions;

public class MachineNotLoadedException extends EnigmaException {
    
    public MachineNotLoadedException(String message) {
        super(message);
    }
    
    public MachineNotLoadedException() {
        super("No machine loaded. Please load an XML file first (command 1).");
    }
}

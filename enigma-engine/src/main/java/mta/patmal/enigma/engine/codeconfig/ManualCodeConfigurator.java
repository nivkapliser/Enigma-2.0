package mta.patmal.enigma.engine.codeconfig;

import loader.XmlLoader;
import mta.patmal.enigma.engine.exceptions.InvalidConfigurationException;
import mta.patmal.enigma.machine.component.code.Code;
import mta.patmal.enigma.machine.component.code.CodeImpl;
import mta.patmal.enigma.machine.component.machine.Machine;
import mta.patmal.enigma.machine.component.machine.MachineImpl;
import mta.patmal.enigma.machine.component.reflector.Reflector;
import mta.patmal.enigma.machine.component.rotor.Rotor;

import java.util.ArrayList;
import java.util.List;

public class ManualCodeConfigurator {
    private static final int REQUIRED_ROTOR_COUNT = 3;

    private final Machine machine;
    private final XmlLoader xmlLoader;
    private final String abc;
    private final int totalRotors;
    private final int totalReflectors;

    public ManualCodeConfigurator(Machine machine, XmlLoader xmlLoader, String abc, int totalRotors, int totalReflectors) {
        this.machine = machine;
        this.xmlLoader = xmlLoader;
        this.abc = abc;
        this.totalRotors = totalRotors;
        this.totalReflectors = totalReflectors;
    }

    public void configure(List<Integer> rotorIds, String positionsString, int reflectorId) throws InvalidConfigurationException {
        validateRotorIds(rotorIds);
        List<Integer> rotorPositions = parseAndValidatePositions(positionsString, rotorIds);
        Reflector reflector = validateAndCreateReflector(reflectorId);
        createAndSetCode(rotorIds, rotorPositions, reflector);
    }

    private void validateRotorIds(List<Integer> rotorIds) throws InvalidConfigurationException {
        if (rotorIds == null || rotorIds.isEmpty()) {
            throw new InvalidConfigurationException("Rotor IDs cannot be empty. Please provide " + REQUIRED_ROTOR_COUNT + " rotor IDs.");
        }

        if (rotorIds.size() != rotorIds.stream().distinct().count()) {
            throw new InvalidConfigurationException("Each rotor can only be selected once. Please try again.");
        }

        if (rotorIds.size() != REQUIRED_ROTOR_COUNT) {
            throw new InvalidConfigurationException("Expected exactly " + REQUIRED_ROTOR_COUNT + 
                    " rotor IDs, but got " + rotorIds.size() + ". Please provide " + REQUIRED_ROTOR_COUNT + " rotor IDs.");
        }

        for (Integer rotorId : rotorIds) {
            if (rotorId == null) {
                throw new InvalidConfigurationException("Rotor ID cannot be null.");
            }
            if (rotorId < 1 || rotorId > totalRotors) {
                throw new InvalidConfigurationException("Rotor ID " + rotorId + " is out of range. " +
                        "Available rotor IDs are 1-" + totalRotors + ".");
            }
            
            // Verify rotor exists
            try {
                xmlLoader.createRotorById(rotorId);
            } catch (IllegalArgumentException e) {
                throw new InvalidConfigurationException("Rotor ID " + rotorId + " not found in the loaded machine. " +
                        "Available rotor IDs are 1-" + totalRotors + ".", e);
            }
        }
    }

    private List<Integer> parseAndValidatePositions(String positionsString, List<Integer> rotorIds) throws InvalidConfigurationException {
        if (positionsString == null || positionsString.trim().isEmpty()) {
            throw new InvalidConfigurationException("Initial positions cannot be empty. Please provide " + 
                    REQUIRED_ROTOR_COUNT + " characters from the ABC.");
        }

        if (positionsString.length() != REQUIRED_ROTOR_COUNT) {
            throw new InvalidConfigurationException("Expected exactly " + REQUIRED_ROTOR_COUNT + 
                    " characters for initial positions, but got " + positionsString.length() + 
                    ". Please provide " + REQUIRED_ROTOR_COUNT + " characters from the ABC.");
        }

        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < positionsString.length(); i++) {
            char positionChar = positionsString.charAt(i);
            
            if (abc.indexOf(positionChar) == -1) {
                throw new InvalidConfigurationException("Character '" + positionChar +
                        "' at position " + (i + 1) + " is not in the ABC. The ABC is: " + abc);
            }

            int rotorId = rotorIds.get(i);
            
            try {
                int zeroBasedPos = xmlLoader.getPositionIndexByRightLetter(rotorId, positionChar);
                positions.add(zeroBasedPos);
            } catch (IllegalArgumentException e) {
                throw new InvalidConfigurationException("Failed to map position character '" + positionChar + 
                        "' for rotor " + rotorId + ": " + e.getMessage(), e);
            }
        }

        return positions;
    }

    private Reflector validateAndCreateReflector(int reflectorId) throws InvalidConfigurationException {
        if (reflectorId < 1 || reflectorId > totalReflectors) {
            throw new InvalidConfigurationException("Reflector ID must be between 1 and " + 
                    totalReflectors + ", but got " + reflectorId + ".");
        }

        try {
            return xmlLoader.createReflectorByNumericId(reflectorId);
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Reflector ID " + reflectorId + 
                    " not found in the loaded machine.", e);
        }
    }

    private void createAndSetCode(List<Integer> rotorIds, List<Integer> rotorPositions, Reflector reflector) throws InvalidConfigurationException {
        // Reverse the lists because user input is left-to-right, but we store right-to-left
        List<Integer> reversedRotorIds = new ArrayList<>();
        List<Integer> reversedPositions = new ArrayList<>();
        
        for (int i = rotorIds.size() - 1; i >= 0; i--) {
            reversedRotorIds.add(rotorIds.get(i));
            reversedPositions.add(rotorPositions.get(i));
        }

        // Create rotors in the correct order (rightmost first)
        List<Rotor> rotors = new ArrayList<>();
        for (Integer rotorId : reversedRotorIds) {
            Rotor rotor = xmlLoader.createRotorById(rotorId);
            rotors.add(rotor);
        }

        // Set initial positions on rotors
        for (int i = 0; i < rotors.size(); i++) {
            rotors.get(i).setPosition(reversedPositions.get(i));
        }

        // Create Code object
        Code code = new CodeImpl(rotors, reversedPositions, reflector);
        
        // Set code on machine
        if (machine instanceof MachineImpl) {
            ((MachineImpl) machine).setCode(code);
        } else {
            throw new InvalidConfigurationException("Machine is not a valid MachineImpl instance");
        }
    }
}

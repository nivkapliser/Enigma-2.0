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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class AutomaticCodeConfigurator {
    private static final int REQUIRED_ROTOR_COUNT = 3;

    private final Machine machine;
    private final XmlLoader xmlLoader;
    private final String abc;
    private final int totalRotors;
    private final int totalReflectors;
    private final Random random;

    public AutomaticCodeConfigurator(Machine machine, XmlLoader xmlLoader, String abc, int totalRotors, int totalReflectors) {
        this.machine = machine;
        this.xmlLoader = xmlLoader;
        this.abc = abc;
        this.totalRotors = totalRotors;
        this.totalReflectors = totalReflectors;
        this.random = new Random();
    }

    public void configure() throws InvalidConfigurationException {
        try {
            List<Integer> rotorIds = generateRandomRotorIds();
            List<Integer> rotorPositions = generateRandomRotorPositions(rotorIds);
            Reflector reflector = generateRandomReflector();
            createAndSetCode(rotorIds, rotorPositions, reflector);
        } catch (Exception e) {
            throw new InvalidConfigurationException("Failed to automatically configure code: " + e.getMessage(), e);
        }
    }

    private List<Integer> generateRandomRotorIds() {
        Set<Integer> selectedRotors = new HashSet<>();
        List<Integer> rotorIds = new ArrayList<>();

        while (rotorIds.size() < REQUIRED_ROTOR_COUNT) {
            int rotorId = random.nextInt(totalRotors) + 1; // 1-based indexing
            if (selectedRotors.add(rotorId)) {
                rotorIds.add(rotorId);
            }
        }

        return rotorIds;
    }

    private List<Integer> generateRandomRotorPositions(List<Integer> rotorIds) {
        List<Integer> positions = new ArrayList<>();

        for (int i = 0; i < REQUIRED_ROTOR_COUNT; i++) {
            int rotorId = rotorIds.get(i);
            char letter = abc.charAt(random.nextInt(abc.length()));
            int positionIndex = xmlLoader.getPositionIndexByRightLetter(rotorId, letter);
            positions.add(positionIndex);
        }

        return positions;
    }

    private Reflector generateRandomReflector() {
        int reflectorId = random.nextInt(totalReflectors) + 1; // 1-based indexing
        return xmlLoader.createReflectorByNumericId(reflectorId);
    }

    private void createAndSetCode(List<Integer> rotorIds, List<Integer> rotorPositions, Reflector reflector) throws InvalidConfigurationException {
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

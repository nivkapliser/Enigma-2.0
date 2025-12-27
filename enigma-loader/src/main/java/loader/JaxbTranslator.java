package loader;

import generated.*;
import mta.patmal.enigma.machine.component.code.Code;
import mta.patmal.enigma.machine.component.code.CodeImpl;
import mta.patmal.enigma.machine.component.keyboard.Keyboard;
import mta.patmal.enigma.machine.component.keyboard.KeyboardImpl;
import mta.patmal.enigma.machine.component.machine.Machine;
import mta.patmal.enigma.machine.component.machine.MachineImpl;
import mta.patmal.enigma.machine.component.reflector.Reflector;
import mta.patmal.enigma.machine.component.reflector.ReflectorImpl;
import mta.patmal.enigma.machine.component.rotor.Rotor;
import mta.patmal.enigma.machine.component.rotor.RotorImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JaxbTranslator {
    private static final int MIN_REQUIRED_ROTORS = 3;
    private static final int INITIAL_POSITION = 0;
    private static final int INITIAL_RING_SETTING = 0;
    private static final int INDEX_NOT_FOUND = -1;

    public Machine translateToMachine(BTEEnigma enigma) {
        String abc = enigma.getABC().trim();
        Keyboard keyboard = createKeyboard(abc);
        
        List<BTERotor> selectedBteRotors = selectRotors(enigma);
        List<Rotor> selectedRotors = createRotors(selectedBteRotors, abc);
        
        Reflector selectedReflector = selectAndCreateReflector(enigma);
        List<Integer> initialPositions = createInitialPositions(selectedRotors.size());
        
        return assembleMachine(keyboard, selectedRotors, initialPositions, selectedReflector);
    }

    public Machine createMachineWithoutCode(BTEEnigma enigma) {
        String abc = enigma.getABC().trim();
        Keyboard keyboard = createKeyboard(abc);
        return new MachineImpl(keyboard);
    }
    
    private Keyboard createKeyboard(String abc) {
        return new KeyboardImpl(abc);
    }
    
    private List<BTERotor> selectRotors(BTEEnigma enigma) {
        List<BTERotor> bteRotors = enigma.getBTERotors().getBTERotor();
        return bteRotors.stream()
                .sorted((r1, r2) -> Integer.compare(r1.getId(), r2.getId()))
                .limit(MIN_REQUIRED_ROTORS)
                .collect(Collectors.toList());
    }
    
    private Reflector selectAndCreateReflector(BTEEnigma enigma) {
        List<BTEReflector> bteReflectors = enigma.getBTEReflectors().getBTEReflector();
        BTEReflector selectedBteReflector = bteReflectors.stream()
                .sorted((r1, r2) -> Integer.compare(RomanNumeralUtils.romanToInt(r1.getId()), RomanNumeralUtils.romanToInt(r2.getId())))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No reflectors available"));
        return createReflector(selectedBteReflector);
    }
    
    private List<Integer> createInitialPositions(int rotorCount) {
        List<Integer> initialPositions = new ArrayList<>();
        for (int i = 0; i < rotorCount; i++) {
            initialPositions.add(INITIAL_POSITION);
        }
        return initialPositions;
    }
    
    private Machine assembleMachine(Keyboard keyboard, List<Rotor> rotors, List<Integer> positions, Reflector reflector) {
        Code code = new CodeImpl(rotors, positions, reflector);
        Machine machine = new MachineImpl(keyboard);
        machine.setCode(code);
        return machine;
    }

    private List<Rotor> createRotors(List<BTERotor> bteRotors, String abc) {
        List<Rotor> rotors = new ArrayList<>();
        for (BTERotor bteRotor : bteRotors) {
            Rotor rotor = createRotorFromBte(bteRotor, abc);
            rotors.add(rotor);
        }
        return rotors;
    }

    public Rotor createRotorFromBte(BTERotor bteRotor, String abc) {
        RotorWiring wiring = buildRotorWiring(bteRotor, abc);
        int notch = convertNotchToZeroBased(bteRotor.getNotch());
        return createRotor(bteRotor.getId(), wiring, notch);
    }

    public Rotor createRotorById(BTEEnigma enigma, int rotorId) {
        String abc = enigma.getABC().trim();
        List<BTERotor> bteRotors = enigma.getBTERotors().getBTERotor();
        BTERotor bteRotor = bteRotors.stream()
                .filter(r -> r.getId() == rotorId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rotor with ID " + rotorId + " not found"));
        return createRotorFromBte(bteRotor, abc);
    }

    public int getPositionIndexByRightLetter(BTEEnigma enigma, int rotorId, char letter) {

        char target = Character.toUpperCase(letter);


        List<BTERotor> bteRotors = enigma.getBTERotors().getBTERotor();
        BTERotor bteRotor = bteRotors.stream()
                .filter(r -> r.getId() == rotorId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Rotor with ID " + rotorId + " not found"));


        int rowIndex = 0;
        for (BTEPositioning pos : bteRotor.getBTEPositioning()) {
            char rightChar = Character.toUpperCase(pos.getRight().charAt(0));
            if (rightChar == target) {
                return rowIndex;
            }
            rowIndex++;
        }

        throw new IllegalArgumentException(
                "Letter '" + letter + "' not found in RIGHT column of rotor " + rotorId);
    }


    public char getRightLetterByPosition(BTEEnigma enigma, int rotorId, int positionIndex) {
        List<BTERotor> bteRotors = enigma.getBTERotors().getBTERotor();
        BTERotor rotor = bteRotors.stream()
                .filter(r -> r.getId() == rotorId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Rotor with ID " + rotorId + " not found"));

        List<BTEPositioning> positions = rotor.getBTEPositioning();

        if (positionIndex < 0 || positionIndex >= positions.size()) {
            throw new IllegalArgumentException(
                    "Position index " + positionIndex + " is out of range for rotor " + rotorId);
        }

        return positions.get(positionIndex).getRight().charAt(0);
    }


    private RotorWiring buildRotorWiring(BTERotor bteRotor, String abc) {
        Map<Integer, Integer> forwardWiring = new HashMap<>();
        Map<Integer, Integer> backwardWiring = new HashMap<>();
        
        for (BTEPositioning positioning : bteRotor.getBTEPositioning()) {
            char leftChar = positioning.getLeft().charAt(0);
            char rightChar = positioning.getRight().charAt(0);
            
            int leftIndex = abc.indexOf(leftChar);
            int rightIndex = abc.indexOf(rightChar);
            
            if (leftIndex == INDEX_NOT_FOUND || rightIndex == INDEX_NOT_FOUND) {
                throw new IllegalArgumentException(
                        "Character not found in ABC: left=" + leftChar + ", right=" + rightChar);
            }
            
            forwardWiring.put(leftIndex, rightIndex);
            backwardWiring.put(rightIndex, leftIndex);
        }
        
        return new RotorWiring(forwardWiring, backwardWiring);
    }
    
    private int convertNotchToZeroBased(int notch) {
        return notch - 1;
    }
    
    private Rotor createRotor(int id, RotorWiring wiring, int notch) {
        return new RotorImpl(
                id,
                wiring.forward,
                wiring.backward,
                INITIAL_POSITION,
                notch,
                INITIAL_RING_SETTING
        );
    }
    
    private static class RotorWiring {
        final Map<Integer, Integer> forward;
        final Map<Integer, Integer> backward;
        
        RotorWiring(Map<Integer, Integer> forward, Map<Integer, Integer> backward) {
            this.forward = forward;
            this.backward = backward;
        }
    }
    
    private Reflector createReflector(BTEReflector bteReflector) {
        Map<Integer, Integer> wiring = buildReflectorWiring(bteReflector);
        int id = RomanNumeralUtils.romanToInt(bteReflector.getId());
        return new ReflectorImpl(id, wiring);
    }

    public Reflector createReflectorFromBte(BTEReflector bteReflector) {
        return createReflector(bteReflector);
    }

    public Reflector createReflectorByRomanId(BTEEnigma enigma, String romanId) {
        List<BTEReflector> bteReflectors = enigma.getBTEReflectors().getBTEReflector();
        BTEReflector bteReflector = bteReflectors.stream()
                .filter(r -> r.getId().equals(romanId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Reflector with ID " + romanId + " not found"));
        return createReflectorFromBte(bteReflector);
    }

    public Reflector createReflectorByNumericId(BTEEnigma enigma, int numericId) {
        String romanId = RomanNumeralUtils.intToRoman(numericId);
        return createReflectorByRomanId(enigma, romanId);
    }
    
    private Map<Integer, Integer> buildReflectorWiring(BTEReflector bteReflector) {
        Map<Integer, Integer> wiring = new HashMap<>();
        for (BTEReflect reflect : bteReflector.getBTEReflect()) {
            int input = convertToZeroBased(reflect.getInput());
            int output = convertToZeroBased(reflect.getOutput());
            wiring.put(input, output);
            wiring.put(output, input);
        }
        return wiring;
    }
    
    private int convertToZeroBased(int oneBasedIndex) {
        return oneBasedIndex - 1;
    }
}
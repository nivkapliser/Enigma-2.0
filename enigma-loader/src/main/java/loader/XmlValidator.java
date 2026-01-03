package loader;

import generated.*;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XmlValidator {
    private static final int MIN_REQUIRED_ROTORS = 2;
    private static final int MIN_ROTOR_ID = 1;
    private static final int MIN_REFLECTOR_ID = 1;
    private static final int MAX_REFLECTOR_ID = 5;

    public void validateMachineFormat(BTEEnigma xmlEnigma) {
        if (xmlEnigma == null) {
            throw new IllegalArgumentException("Invalid XML file: BTEEnigma is null");
        }

        validateABC(xmlEnigma);
        validateRotorsCount(xmlEnigma);
        validateRotors(xmlEnigma);
        validateReflectors(xmlEnigma);
    }

    private void validateRotorsCount(BTEEnigma bteEnigma) {
        BigInteger rotorsCount = bteEnigma.getRotorsCount();
        
        if (rotorsCount == null) {
            throw new IllegalArgumentException("rotors-count attribute is required but not specified in XML");
        }

        int count = rotorsCount.intValue();
        
        if (count < MIN_REQUIRED_ROTORS) {
            throw new IllegalArgumentException("rotors-count must be at least " + MIN_REQUIRED_ROTORS + ", got: " + count);
        }

        // Validate that rotors-count does not exceed the total number of rotors defined
        List<BTERotor> rotors = bteEnigma.getBTERotors().getBTERotor();
        int totalRotorsDefined = rotors != null ? rotors.size() : 0;
        
        if (count > totalRotorsDefined) {
            throw new IllegalArgumentException("rotors-count (" + count + ") cannot exceed the total number of rotors defined (" + totalRotorsDefined + ")");
        }
    }

    private void validateABC(BTEEnigma bteEnigma) {
        String abc = bteEnigma.getABC();
        if (abc == null) {
            throw new IllegalStateException("No ABC specified in XML");
        }
        abc = abc.trim();
        if (abc.isEmpty()) {
            throw new IllegalArgumentException("ABC cannot be empty");
        }
        if (abc.length() % 2 != 0) {
            throw new IllegalArgumentException("ABC must be even length, got: " + abc.length());
        }
    }

    private void validateRotors(BTEEnigma bteEnigma) {
        List<BTERotor> rotors = bteEnigma.getBTERotors().getBTERotor();
        int abcSize = getAbcSize(bteEnigma);

        validateRotorCount(rotors);
        validateRotorIds(rotors, abcSize);
    }

    private int getAbcSize(BTEEnigma bteEnigma) {
        String abc = bteEnigma.getABC();
        if (abc == null || abc.trim().isEmpty()) {
            throw new IllegalStateException("Cannot validate rotors: ABC is null or empty");
        }
        return abc.trim().length();
    }

    private void validateRotorCount(List<BTERotor> rotors) {
        if (rotors == null || rotors.isEmpty()) {
            throw new IllegalStateException("No rotors defined in XML");
        }
    }

    private void validateRotorIds(List<BTERotor> rotors, int abcSize) {
        Set<Integer> ids = new HashSet<>();
        int minId = Integer.MAX_VALUE;
        int maxId = Integer.MIN_VALUE;

        for (BTERotor rotor : rotors) {
            int id = rotor.getId();
            validateUniqueRotorId(ids, id);
            minId = Math.min(minId, id);
            maxId = Math.max(maxId, id);
            validateSingleRotorMappings(rotor, abcSize);
            validateNotch(rotor, abcSize);
        }
        
        validateRotorIdSequence(minId, maxId, ids);
    }

    private void validateUniqueRotorId(Set<Integer> ids, int id) {
        if (!ids.add(id)) {
            throw new IllegalArgumentException("Duplicate rotor id: " + id);
        }
    }

    private void validateRotorIdSequence(int minId, int maxId, Set<Integer> ids) {
        if (minId != MIN_ROTOR_ID) {
            throw new IllegalArgumentException("Rotor ids must start from " + MIN_ROTOR_ID + ", minimal id is " + minId);
        }
        if (ids.size() != maxId) {
            throw new IllegalArgumentException("Rotor ids must form a continuous sequence 1.." + maxId + 
                    ", but got: " + ids);
        }
    }

    private void validateSingleRotorMappings(BTERotor rotor, int abcSize) {
        List<BTEPositioning> mappings = rotor.getBTEPositioning();
        if (mappings == null || mappings.isEmpty()) {
            throw new IllegalStateException("Rotor " + rotor.getId() + " has no mappings");
        }

        Set<Character> leftChars = new HashSet<>();
        Set<Character> rightChars = new HashSet<>();

        for (BTEPositioning positioning : mappings) {
            validatePositioningFormat(rotor.getId(), positioning);
            char left = positioning.getLeft().charAt(0);
            char right = positioning.getRight().charAt(0);
            validateUniqueMappingChars(rotor.getId(), leftChars, rightChars, left, right);
        }

        validateMappingCount(rotor.getId(), mappings.size(), abcSize);
    }

    private void validatePositioningFormat(int rotorId, BTEPositioning positioning) {
        String left = positioning.getLeft();
        String right = positioning.getRight();
        if (left == null || right == null || left.length() != 1 || right.length() != 1) {
            throw new IllegalArgumentException("Rotor " + rotorId + 
                    " has invalid mapping (left/right must be single letters): left=" + left + ", right=" + right);
        }
    }

    private void validateUniqueMappingChars(int rotorId, Set<Character> leftChars, Set<Character> rightChars, 
                                           char left, char right) {
        if (!leftChars.add(left)) {
            throw new IllegalArgumentException("Rotor " + rotorId + 
                    " has duplicate mapping for LEFT letter: " + left);
        }
        if (!rightChars.add(right)) {
            throw new IllegalArgumentException("Rotor " + rotorId + 
                    " has duplicate mapping for RIGHT letter: " + right);
        }
    }

    private void validateMappingCount(int rotorId, int mappingCount, int abcSize) {
        if (mappingCount != abcSize) {
            throw new IllegalArgumentException("Rotor " + rotorId + 
                    " mapping count (" + mappingCount + ") does not match ABC size (" + abcSize + ")");
        }
    }

    private void validateNotch(BTERotor rotor, int abcSize) {
        int notch = rotor.getNotch();
        if (notch < MIN_ROTOR_ID || notch > abcSize) {
            throw new IllegalArgumentException("Rotor " + rotor.getId() + 
                    " notch (" + notch + ") is out of range " + MIN_ROTOR_ID + ".." + abcSize);
        }
    }

    private void validateReflectors(BTEEnigma machine) {
        List<BTEReflector> reflectors = machine.getBTEReflectors().getBTEReflector();
        if (reflectors == null || reflectors.isEmpty()) {
            throw new IllegalStateException("No reflectors defined in XML");
        }

        Set<Integer> ids = new HashSet<>();
        int maxId = Integer.MIN_VALUE;

        for (BTEReflector reflector : reflectors) {
            int numericId = parseAndValidateReflectorId(reflector.getId());
            validateUniqueReflectorId(ids, reflector.getId(), numericId);
            maxId = Math.max(maxId, numericId);
            validateSingleReflectorMappings(reflector);
        }

        validateReflectorIdSequence(ids, maxId);
    }

    private int parseAndValidateReflectorId(String romanId) {
        int numericId;
        try {
            numericId = RomanNumeralUtils.romanToInt(romanId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Reflector id must be in range I..V, got: " + romanId, e);
        }
        if (numericId < MIN_REFLECTOR_ID || numericId > MAX_REFLECTOR_ID) {
            throw new IllegalArgumentException("Reflector id must be in range I..V, got: " + romanId + " (=" + numericId + ")");
        }
        return numericId;
    }

    private void validateUniqueReflectorId(Set<Integer> ids, String romanId, int numericId) {
        if (!ids.add(numericId)) {
            throw new IllegalArgumentException("Duplicate reflector id: " + romanId + " (=" + numericId + ")");
        }
    }

    private void validateReflectorIdSequence(Set<Integer> ids, int maxId) {
        for (int i = MIN_REFLECTOR_ID; i <= maxId; i++) {
            if (!ids.contains(i)) {
                throw new IllegalArgumentException("Reflector ids must form a continuous roman sequence from I to " +
                        RomanNumeralUtils.intToRoman(maxId) + ", missing: " + RomanNumeralUtils.intToRoman(i));
            }
        }
    }

    private void validateSingleReflectorMappings(BTEReflector reflector) {
        List<BTEReflect> mappings = reflector.getBTEReflect();
        if (mappings == null || mappings.isEmpty()) {
            throw new IllegalStateException("Reflector " + reflector.getId() + " has no mappings");
        }

        for (BTEReflect mapping : mappings) {
            int input = mapping.getInput();
            int output = mapping.getOutput();

            if (input == output) {
                throw new IllegalArgumentException("Reflector " + reflector.getId() +
                        " has a mapping from position to itself: " + input);
            }
        }
    }
}


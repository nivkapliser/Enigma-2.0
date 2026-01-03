package mta.patmal.enigma.engine.display;

import loader.RomanNumeralUtils;
import loader.XmlLoader;
import mta.patmal.enigma.dto.MachineData;
import mta.patmal.enigma.machine.component.code.Code;
import mta.patmal.enigma.machine.component.machine.MachineImpl;
import mta.patmal.enigma.machine.component.plugboard.Plugboard;
import mta.patmal.enigma.machine.component.reflector.Reflector;
import mta.patmal.enigma.machine.component.rotor.Rotor;

import java.util.*;

public class MachineDataFormatter {

    private final XmlLoader xmlLoader;

    public MachineDataFormatter(XmlLoader xmlLoader) {
        this.xmlLoader = xmlLoader;
    }

    public MachineData createMachineData(MachineImpl machineImpl, String originalCodeString, 
                                         int totalRotors, int totalReflectors, int messagesProcessed) {
        MachineData machineData = new MachineData(totalRotors, totalReflectors, messagesProcessed);

        Code currentCode = (machineImpl != null) ? machineImpl.getCode() : null;

        if (originalCodeString != null) {
            machineData.setOriginalCode(originalCodeString);
        } else {
            machineData.setOriginalCode(null);
        }

        if (currentCode != null && machineImpl != null) {
            String stringCurrentCode = formatCodeConfiguration(currentCode, machineImpl);
            machineData.setCurrentCode(stringCurrentCode);
        } else {
            machineData.setCurrentCode(null);
        }

        return machineData;
    }

    public String formatCode(Code code, MachineImpl machineImpl) {
        return formatCodeConfiguration(code, machineImpl);
    }

    private String formatCodeConfiguration(Code code, MachineImpl machineImpl) {
        if (code == null) {
            return "No code configured.";
        }

        List<Rotor> rotors = code.getRotors();
        Reflector reflector = code.getReflector();
        int alphabetSize = machineImpl.getAlphabetSize();

        StringBuilder sb = new StringBuilder();

        // Rotor IDs section, from left (last in list) to right (first in list)
        sb.append("<");
        for (int i = rotors.size() - 1; i >= 0; i--) {
            sb.append(rotors.get(i).getId());
            if (i != 0) {
                sb.append(",");
            }
        }
        sb.append(">");

        // Positions and notch distances
        sb.append("<");
        for (int i = rotors.size() - 1; i >= 0; i--) {
            Rotor rotor = rotors.get(i);
            int position = rotor.getPosition();
            char windowChar = xmlLoader.getRightLetterByPosition(rotor.getId(), position);
            int distance = (rotor.getNotch() - position + alphabetSize) % alphabetSize;

            sb.append(windowChar)
              .append("(")
              .append(distance)
              .append(")");

            if (i != 0) {
                sb.append(",");
            }
        }
        sb.append(">");

        // Reflector section
        String romanId = RomanNumeralUtils.intToRoman(reflector.getId());
        sb.append("<").append(romanId).append(">");

        Plugboard plugboard = code.getPlugboard();
        String plugsString = formatPlugboard(plugboard);
        if (!plugsString.isEmpty()) {
            sb.append("<").append(plugsString).append(">");
        }

        return sb.toString();
    }

    private String formatPlugboard(Plugboard plugboard) {
        Map<Integer, Integer> wiring = plugboard.getWiring();

        if (wiring.isEmpty()) {
            return "";
        }

        Set<Integer> visited = new HashSet<>();
        List<String> pairs = new ArrayList<>();

        for (Map.Entry<Integer, Integer> e : wiring.entrySet()) {
            int a = e.getKey();
            int b = e.getValue();
            if (visited.contains(a) || visited.contains(b)) continue;

            visited.add(a);
            visited.add(b);

            char ca = xmlLoader.getABC().charAt(a);
            char cb = xmlLoader.getABC().charAt(b);

            pairs.add(ca + "|" + cb);
        }

        return String.join(",", pairs);
    }
}


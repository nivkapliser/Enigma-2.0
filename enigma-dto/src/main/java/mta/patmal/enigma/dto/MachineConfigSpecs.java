package mta.patmal.enigma.dto;

import java.util.List;

public class MachineConfigSpecs {
    private final List<Integer> availableRotorIds;
    private final List<Integer> availableReflectorIds;
    private final int requiredRotorsCount;
    private final String alphabet;

    public MachineConfigSpecs(List<Integer> availableRotorIds, List<Integer> availableReflectorIds,
            int requiredRotorsCount, String alphabet) {
        this.availableRotorIds = availableRotorIds;
        this.availableReflectorIds = availableReflectorIds;
        this.requiredRotorsCount = requiredRotorsCount;
        this.alphabet = alphabet;
    }

    public List<Integer> getAvailableRotorIds() {
        return availableRotorIds;
    }

    public List<Integer> getAvailableReflectorIds() {
        return availableReflectorIds;
    }

    public int getRequiredRotorsCount() {
        return requiredRotorsCount;
    }

    public String getAlphabet() {
        return alphabet;
    }
}

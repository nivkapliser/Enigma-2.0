package mta.patmal.enigma.dto;

import java.util.List;

public class CodeConfigurationRequestDTO {
    private final List<Integer> rotorIds;
    private final String positionsString;
    private final int reflectorId;
    
    public CodeConfigurationRequestDTO(List<Integer> rotorIds, String positionsString, int reflectorId) {
        this.rotorIds = rotorIds;
        this.positionsString = positionsString;
        this.reflectorId = reflectorId;
    }
    
    public List<Integer> getRotorIds() {
        return rotorIds;
    }
    
    public String getPositionsString() {
        return positionsString;
    }
    
    public int getReflectorId() {
        return reflectorId;
    }
}

package mta.patmal.enigma.dto;

import java.util.List;

public class CodeConfigurationRequestDTO {
    private final List<Integer> rotorIds;
    private final String positionsString;
    private final int reflectorId;
    private final String plugsString;
    
    public CodeConfigurationRequestDTO(List<Integer> rotorIds, String positionsString, int reflectorId, String plugsString) {
        this.rotorIds = rotorIds;
        this.positionsString = positionsString;
        this.reflectorId = reflectorId;
        this.plugsString = plugsString;
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

    public String getPlugsString() { return  plugsString; }

}

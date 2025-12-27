package mta.patmal.enigma.dto;

import mta.patmal.enigma.dto.ProcessingEntryDTO;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatisticsDTO {
    private final Map<String, List<ProcessingEntryDTO>> historyByCode;
    
    public StatisticsDTO() {
        this.historyByCode = new LinkedHashMap<>();
    }
    
    public void addEntry(String codeConfiguration, ProcessingEntryDTO entry) {
        historyByCode.computeIfAbsent(codeConfiguration, k -> new ArrayList<>()).add(entry);
    }
    
    public Map<String, List<ProcessingEntryDTO>> getHistoryByCode() {
        return historyByCode;
    }
    
    public boolean isEmpty() {
        return historyByCode.isEmpty();
    }
}

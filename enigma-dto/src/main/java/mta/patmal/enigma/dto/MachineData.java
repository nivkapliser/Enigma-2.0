package mta.patmal.enigma.dto;

import mta.patmal.enigma.machine.component.code.Code;

public class MachineData {
    private final int totalRotors;
    private final int totalReflectors;
    private final int messagesProcessed;
    private String originalCode;
    private String currentCode;

    public MachineData(int totalRotors, int totalReflectors, int messagesProcessed, String originalCode, String currentCode) {
        this.totalRotors = totalRotors;
        this.totalReflectors = totalReflectors;
        this.messagesProcessed = messagesProcessed;
        this.originalCode = originalCode;
        this.currentCode = currentCode;
    }

    public MachineData(int totalRotors, int totalReflectors, int messagesProcessed) {
        this(totalRotors, totalReflectors, messagesProcessed, null, null);
    }

    public int getTotalRotors() {
        return totalRotors;
    }
    public int getTotalReflectors() {
        return totalReflectors;
    }
    public int getMessagesProcessed() {
        return messagesProcessed;
    }
    public String getOriginalCode() {
        return originalCode;
    }
    public String getCurrentCode() {
        return currentCode;
    }
    public void setOriginalCode(String originalCode) {
        this.originalCode = originalCode;
    }
    public void setCurrentCode(String currentCode) {
        this.currentCode = currentCode;
    }
}

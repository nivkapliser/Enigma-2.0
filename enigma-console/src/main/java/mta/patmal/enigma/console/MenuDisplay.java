package mta.patmal.enigma.console;

import loader.RomanNumeralUtils;
import mta.patmal.enigma.dto.MachineConfigSpecs;
import mta.patmal.enigma.dto.MachineData;
import mta.patmal.enigma.dto.ProcessingEntryDTO;
import mta.patmal.enigma.dto.StatisticsDTO;

import java.util.List;
import java.util.Map;

public class MenuDisplay {

    public void showMainMenu() {
        System.out.println("\nEnigma Machine");
        System.out.println("==============");
        System.out.println("1. Load XML");
        System.out.println("2. Show Machine Data");
        System.out.println("3. Code Manual");
        System.out.println("4. Code Automatic");
        System.out.println("5. Process");
        System.out.println("6. Reset Current Code");
        System.out.println("7. Statistics");
        System.out.println("8. Exit");
    }

    public void displayMachineData(MachineData machineData) {
        System.out.println("\nMachine specification:");
        System.out.println("Total rotors: " + machineData.getTotalRotors());
        System.out.println("Total reflectors: " + machineData.getTotalReflectors());
        System.out.println("Messages processed since last load: " + machineData.getMessagesProcessed());

        if (machineData.getCurrentCode() != null) {
            if (machineData.getOriginalCode() != null) {
                System.out.println("Original code configuration: " + machineData.getOriginalCode());
            }
            System.out.println("Current code configuration: " + machineData.getCurrentCode());
        } else {
            System.out.println("No code configured.");
        }
    }

    public void displayStatistics(StatisticsDTO statistics) {
        if (statistics.isEmpty()) {
            System.out.println("\nNo history available yet.");
            return;
        }

        System.out.println("\nStatistics:");
        System.out.println("===========");
        
        int index = 1;
        for (Map.Entry<String, List<ProcessingEntryDTO>> entry : statistics.getHistoryByCode().entrySet()) {
            String code = entry.getKey();
            List<ProcessingEntryDTO> entries = entry.getValue();

            System.out.println("Code configuration: " + code);
            for (ProcessingEntryDTO processingEntry : entries) {
                System.out.printf(
                        "%d. <%s> --> <%s> (%d nano-seconds)%n",
                        index++,
                        processingEntry.getInput(),
                        processingEntry.getOutput(),
                        processingEntry.getDurationNanos()
                );
            }
            System.out.println();
        }
    }

    public void displayError(String message) {
        System.out.println("\nError: " + message);
    }

    public void displaySuccess(String message) {
        System.out.println("\n" + message);
    }

    public void displayProcessingResult(String result) {
        System.out.println("Processed text: " + result.toUpperCase());
    }

    public void displayAvailableRotors(int totalRotors) {
        System.out.println("\nAvailable rotors:");
        for (int i = 1; i <= totalRotors; i++) {
            System.out.println("  " + i + ". Rotor " + i);
        }
    }

    public void displayAvailablePositions(String abc) {
        System.out.println("Available position characters (ABC): " + abc);
    }

    public void displayAvailableReflectors(int totalReflectors) {
        System.out.println("\nAvailable reflectors:");
        for (int i = 1; i <= totalReflectors; i++) {
            String romanNumeral = RomanNumeralUtils.intToRoman(i);
            System.out.println("  " + i + ". " + romanNumeral);
        }
    }

    public void displayConfigSpecs(MachineConfigSpecs specs) {
        displayAvailableRotors(specs.getAvailableRotorIds().size());
        displayAvailablePositions(specs.getAlphabet());
        displayAvailableReflectors(specs.getAvailableReflectorIds().size());
    }

    public void displayExiting() {
        System.out.println("\nExiting...");
    }
}

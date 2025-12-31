package mta.patmal.enigma.console;

import mta.patmal.enigma.dto.CodeConfigurationRequestDTO;
import mta.patmal.enigma.dto.CodeConfigurationResultDTO;
import mta.patmal.enigma.dto.MachineConfigSpecs;
import mta.patmal.enigma.dto.MachineData;
import mta.patmal.enigma.dto.StatisticsDTO;
import mta.patmal.enigma.engine.Engine;
import mta.patmal.enigma.engine.exceptions.*;

import java.util.List;

public class EngineOperations {
    private final Engine engine;
    private final MenuDisplay display;
    private final InputCollector input;
    private boolean loadedXml = false;
    private boolean codeConfigured = false;

    public EngineOperations(Engine engine, MenuDisplay display, InputCollector input) {
        this.engine = engine;
        this.display = display;
        this.input = input;
    }

    public void handleLoadXml() {
        String path = input.getFilePath();
        
        try {
            engine.loadXml(path);
            display.displaySuccess("XML file loaded successfully!");
            loadedXml = true;
        } catch (XmlLoadException e) {
            display.displayError(e.getMessage());
        }
    }

    public void handleShowMachineData() {
        try {
            MachineData machineData = engine.showMachineData();
            display.displayMachineData(machineData);
        } catch (MachineNotLoadedException e) {
            display.displayError(e.getMessage());
        }
    }

    public void handleManualConfiguration() {
        try {
            MachineConfigSpecs specs = engine.getMachineConfigSpecs();
            display.displayConfigSpecs(specs);
            
            List<Integer> rotorIds = input.getRotorIds(
                specs.getAvailableRotorIds().size(), 
                specs.getRequiredRotorsCount()
            );
            
            if (rotorIds == null) {
                display.displaySuccess("Returning to main menu.");
                return;
            }
            
            String positions = input.getPositions(specs.getAlphabet(), specs.getRequiredRotorsCount());
            
            if (positions == null) {
                display.displaySuccess("Returning to main menu.");
                return;
            }
            
            int reflectorId = input.getReflectorChoice(specs.getAvailableReflectorIds().size());
            
            if (reflectorId == -1) {
                display.displaySuccess("Returning to main menu.");
                return;
            }
            String plugs = input.getPlugs(specs.getAlphabet());
            CodeConfigurationRequestDTO request = new CodeConfigurationRequestDTO(
                rotorIds, positions, reflectorId, plugs
            );
            
            CodeConfigurationResultDTO result = engine.codeManual(request);
            
            if (result.isSuccess()) {
                display.displaySuccess(result.getMessage());
                display.displaySuccess("Configuration: " + result.getFormattedCode());
                codeConfigured = true;
            } else {
                display.displayError(result.getMessage());
            }
            
        } catch (MachineNotLoadedException | InvalidConfigurationException e) {
            display.displayError(e.getMessage());
        }
    }

    public void handleAutomaticConfiguration() {
        try {
            CodeConfigurationResultDTO result = engine.codeAutomatic();
            
            if (result.isSuccess()) {
                display.displaySuccess(result.getMessage());
                display.displaySuccess("Configuration: " + result.getFormattedCode());
                codeConfigured = true;
            } else {
                display.displayError(result.getMessage());
            }
            
        } catch (MachineNotLoadedException | InvalidConfigurationException e) {
            display.displayError(e.getMessage());
        }
    }

    public void handleProcess() {
        if (!loadedXml) {
            display.displayError("No machine loaded. Please load an XML file first (command 1).");
            return;
        }
        if (!codeConfigured) {
            display.displayError("No code configured. Please configure a code first (command 3 or 4).");
            return;
        }
        String text = input.getText();
        
        try {
            String result = engine.process(text);
            display.displayProcessingResult(result);
        } catch (MachineNotLoadedException | CodeNotConfiguredException | InvalidInputException e) {
            display.displayError(e.getMessage());
        }
    }

    public void handleResetCode() {
        try {
            engine.resetCurrentCode();
            display.displaySuccess("Code was reset to original configuration.");
        } catch (MachineNotLoadedException | CodeNotConfiguredException e) {
            display.displayError(e.getMessage());
        }
    }

    public void handleStatistics() {
        try {
            StatisticsDTO statistics = engine.statistics();
            display.displayStatistics(statistics);
        } catch (MachineNotLoadedException | CodeNotConfiguredException e) {
            display.displayError(e.getMessage());
        }
    }
}


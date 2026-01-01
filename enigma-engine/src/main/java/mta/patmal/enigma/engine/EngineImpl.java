package mta.patmal.enigma.engine;

import mta.patmal.enigma.dto.CodeConfigurationRequestDTO;
import mta.patmal.enigma.dto.CodeConfigurationResultDTO;
import mta.patmal.enigma.dto.MachineConfigSpecs;
import mta.patmal.enigma.dto.MachineData;
import mta.patmal.enigma.dto.ProcessingEntryDTO;
import mta.patmal.enigma.dto.StatisticsDTO;
import mta.patmal.enigma.engine.codeconfig.AutomaticCodeConfigurator;
import mta.patmal.enigma.engine.codeconfig.ManualCodeConfigurator;
import mta.patmal.enigma.engine.display.MachineDataFormatter;
import mta.patmal.enigma.engine.exceptions.*;
import loader.XmlLoader;
import mta.patmal.enigma.machine.component.code.Code;
import mta.patmal.enigma.machine.component.code.CodeImpl;
import mta.patmal.enigma.machine.component.machine.Machine;
import mta.patmal.enigma.machine.component.machine.MachineImpl;
import mta.patmal.enigma.machine.component.plugboard.Plugboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EngineImpl implements Engine {

    private Machine machine;
    private final XmlLoader xmlLoader = new XmlLoader();
    private final MachineDataFormatter dataFormatter = new MachineDataFormatter(xmlLoader);
    private int totalRotors;
    private int totalReflectors;
    private int messagesProcessed;
    private String originalCodeString;
    private String abc;
    private Code originalCode;
    private final Map<String, List<HistoryEntry>> history = new LinkedHashMap<>();

    @Override
    public void loadXml(String path) throws XmlLoadException {
        try {
            this.machine = xmlLoader.loadMachineFromXml(path);
            this.totalRotors = xmlLoader.getTotalRotorCount();
            this.totalReflectors = xmlLoader.getTotalReflectorCount();
            this.messagesProcessed = 0;
            this.abc = xmlLoader.getABC();
            this.originalCodeString = null;
            this.originalCode = null;
            this.history.clear();
        } catch (Exception e) {
            throw new XmlLoadException("Failed to load XML file: " + e.getMessage(), e);
        }
    }

    @Override
    public MachineData showMachineData() throws MachineNotLoadedException {
        if (machine == null) {
            throw new MachineNotLoadedException();
        }

        MachineImpl machineImpl = (machine instanceof MachineImpl)
                ? (MachineImpl) machine
                : null;

        if (machineImpl == null) {
            throw new MachineNotLoadedException("Machine is not a valid MachineImpl instance.");
        }

        return dataFormatter.createMachineData(machineImpl, originalCodeString, 
                totalRotors, totalReflectors, messagesProcessed);
    }

    @Override
    public MachineConfigSpecs getMachineConfigSpecs() throws MachineNotLoadedException {
        if (machine == null || abc == null) {
            throw new MachineNotLoadedException();
        }

        List<Integer> availableRotorIds = IntStream.rangeClosed(1, totalRotors)
                .boxed()
                .collect(Collectors.toList());
        
        List<Integer> availableReflectorIds = IntStream.rangeClosed(1, totalReflectors)
                .boxed()
                .collect(Collectors.toList());

        return new MachineConfigSpecs(availableRotorIds, availableReflectorIds, 3, abc);
    }

    @Override
    public CodeConfigurationResultDTO codeManual(CodeConfigurationRequestDTO request) 
            throws MachineNotLoadedException, InvalidConfigurationException {
        if (machine == null || abc == null) {
            throw new MachineNotLoadedException();
        }

        ManualCodeConfigurator configurator = new ManualCodeConfigurator(
                machine, xmlLoader, abc, totalRotors, totalReflectors);

        configurator.configure(request.getRotorIds(), request.getPositionsString(), request.getReflectorId(), request.getPlugsString());
        
        // Update original code after successful configuration
        if (machine instanceof MachineImpl machineImpl) {
            Code currentCode = machineImpl.getCode();
            var rotors = currentCode.getRotors();
            var reflector = currentCode.getReflector();
            var positions = new ArrayList<Integer>();
            for (var rotor : rotors) {
                positions.add(rotor.getPosition());
            }
            Plugboard plugboard = currentCode.getPlugboard();
            this.originalCode = new CodeImpl(rotors, positions, reflector, plugboard);
            this.originalCodeString = dataFormatter.formatCode(currentCode, machineImpl);
            
            return CodeConfigurationResultDTO.success(originalCodeString);
        }
        
        throw new InvalidConfigurationException("Failed to retrieve configuration after setup");
    }

    @Override
    public CodeConfigurationResultDTO codeAutomatic() 
            throws MachineNotLoadedException, InvalidConfigurationException {
        if (machine == null || abc == null) {
            throw new MachineNotLoadedException();
        }

        AutomaticCodeConfigurator configurator = new AutomaticCodeConfigurator(
                machine, xmlLoader, abc, totalRotors, totalReflectors);
        
        configurator.configure();
        
        // Update original code after successful configuration
        if (machine instanceof MachineImpl machineImpl) {
            Code currentCode = machineImpl.getCode();
            var rotors = currentCode.getRotors();
            var reflector = currentCode.getReflector();
            var positions = new ArrayList<Integer>();
            for (var rotor : rotors) {
                positions.add(rotor.getPosition());
            }

            Plugboard plugboard = currentCode.getPlugboard();
            this.originalCode = new CodeImpl(rotors, positions, reflector, plugboard);
            this.originalCodeString = dataFormatter.formatCode(currentCode, machineImpl);
            
            return CodeConfigurationResultDTO.success(originalCodeString);
        }
        
        throw new InvalidConfigurationException("Failed to retrieve configuration after setup");
    }

    @Override
    public String process(String input) throws MachineNotLoadedException, CodeNotConfiguredException, InvalidInputException {
        if (machine == null) {
            throw new MachineNotLoadedException();
        }

        if (originalCodeString == null) {
            throw new CodeNotConfiguredException();
        }

        if (input == null || input.isEmpty()) {
            throw new InvalidInputException("Input cannot be empty.");
        }

        // Validate all characters are in ABC
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (abc.indexOf(c) == -1) {
                throw new InvalidInputException("Invalid character '" + c + 
                        "' at position " + (i + 1) + ". Character is not in the ABC: " + abc);
            }
        }

        long start = System.nanoTime();

        // Process the input
        char[] result = new char[input.length()];
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            result[i] = machine.process(c);
        }

        long duration = System.nanoTime() - start;
        String output = new String(result);

        messagesProcessed++;

        String codeKey = originalCodeString;
        history.computeIfAbsent(codeKey, k -> new ArrayList<>())
                .add(new HistoryEntry(input, output, duration));

        return output;
    }

    @Override
    public StatisticsDTO statistics() throws MachineNotLoadedException, CodeNotConfiguredException {
        if (machine == null) {
            throw new MachineNotLoadedException();
        }

        if (originalCodeString == null) {
            throw new CodeNotConfiguredException();
        }

        StatisticsDTO statisticsDTO = new StatisticsDTO();

        for (Map.Entry<String, List<HistoryEntry>> entry : history.entrySet()) {
            String code = entry.getKey();
            List<HistoryEntry> entries = entry.getValue();

            for (HistoryEntry h : entries) {
                ProcessingEntryDTO processingEntry = new ProcessingEntryDTO(
                    h.getInput(),
                    h.getOutput(),
                    h.getDurationNanos()
                );
                statisticsDTO.addEntry(code, processingEntry);
            }
        }

        return statisticsDTO;
    }

    @Override
    public void resetCurrentCode() throws MachineNotLoadedException, CodeNotConfiguredException {
        if (machine == null) {
            throw new MachineNotLoadedException();
        }

        if (originalCode == null) {
            throw new CodeNotConfiguredException("No original code configured. Please configure a code first (command 3 or 4).");
        }

        if (!(machine instanceof MachineImpl machineImpl)) {
            throw new MachineNotLoadedException("Machine is not a valid MachineImpl instance.");
        }

        machineImpl.setCode(originalCode);
    }
}

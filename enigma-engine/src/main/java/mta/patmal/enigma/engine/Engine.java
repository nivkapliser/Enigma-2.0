package mta.patmal.enigma.engine;

import mta.patmal.enigma.dto.CodeConfigurationRequestDTO;
import mta.patmal.enigma.dto.CodeConfigurationResultDTO;
import mta.patmal.enigma.dto.MachineConfigSpecs;
import mta.patmal.enigma.dto.MachineData;
import mta.patmal.enigma.dto.StatisticsDTO;
import mta.patmal.enigma.engine.exceptions.*;

public interface Engine {

    void loadXml(String path) throws XmlLoadException;
    
    MachineData showMachineData() throws MachineNotLoadedException;
    
    MachineConfigSpecs getMachineConfigSpecs() throws MachineNotLoadedException;
    
    CodeConfigurationResultDTO codeManual(CodeConfigurationRequestDTO request) throws MachineNotLoadedException, InvalidConfigurationException;
    
    CodeConfigurationResultDTO codeAutomatic() throws MachineNotLoadedException, InvalidConfigurationException;
    
    String process(String input) throws MachineNotLoadedException, CodeNotConfiguredException, InvalidInputException;
    
    StatisticsDTO statistics() throws MachineNotLoadedException, CodeNotConfiguredException;
    
    void resetCurrentCode() throws MachineNotLoadedException, CodeNotConfiguredException;
}

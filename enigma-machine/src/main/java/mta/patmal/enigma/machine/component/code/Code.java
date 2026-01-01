package mta.patmal.enigma.machine.component.code;

import mta.patmal.enigma.machine.component.plugboard.Plugboard;
import mta.patmal.enigma.machine.component.reflector.Reflector;
import mta.patmal.enigma.machine.component.rotor.Rotor;

import java.util.List;

public interface Code {
    List<Rotor> getRotors();
    List<Integer> getPositions();
    Reflector getReflector();
    Plugboard getPlugboard();
}

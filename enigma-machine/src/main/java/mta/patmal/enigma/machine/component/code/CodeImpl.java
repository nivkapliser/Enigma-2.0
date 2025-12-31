package mta.patmal.enigma.machine.component.code;
import mta.patmal.enigma.machine.component.plugboard.Plugboard;
import mta.patmal.enigma.machine.component.reflector.Reflector;
import mta.patmal.enigma.machine.component.rotor.Rotor;
import java.util.List;

public class CodeImpl implements Code {
    private final List<Rotor> rotors;
    private final List<Integer> positions;
    private final Reflector reflector;
    private final Plugboard plugboard;


    public CodeImpl(List<Rotor> rotors,
                    List<Integer> positions,
                    Reflector reflector, Plugboard plugboard) {
        if (rotors == null || positions == null || reflector == null || plugboard == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        if (rotors.size() != positions.size()) {
            throw new IllegalArgumentException("Rotors and positions must have same size");
        }
        this.rotors = List.copyOf(rotors);
        this.positions = List.copyOf(positions);
        this.reflector = reflector;
        this.plugboard = plugboard;
    }

    @Override
    public List<Rotor> getRotors() {
        return rotors;
    }

    @Override
    public List<Integer> getPositions() {
        return positions;
    }

    @Override
    public Reflector getReflector() {
        return reflector;
    }

    @Override
    public Plugboard getPlugboard() {return plugboard; }

}
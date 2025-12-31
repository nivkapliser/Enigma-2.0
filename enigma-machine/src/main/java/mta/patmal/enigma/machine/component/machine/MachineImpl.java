package mta.patmal.enigma.machine.component.machine;

import mta.patmal.enigma.machine.component.code.Code;
import mta.patmal.enigma.machine.component.keyboard.Keyboard;
import mta.patmal.enigma.machine.component.rotor.Direction;
import mta.patmal.enigma.machine.component.rotor.Rotor;

import java.util.List;

public class MachineImpl implements Machine{
    private Code code;
    private final Keyboard keyboard;

    public MachineImpl(Keyboard keyboard) {
        this.keyboard = keyboard;
    }
    @Override
    public void setCode(Code code) {
        this.code = code;
        List<Rotor> rotors = code.getRotors();
        List<Integer> positions = code.getPositions();

        for (int i = 0; i < rotors.size(); i++) {
            rotors.get(i).setPosition(positions.get(i));
        }
    }

    @Override
    public char process(char input) {

        int intermediate = keyboard.processChar(input);

        // plugboard in
        intermediate = code.getPlugboard().process(intermediate);

        List<Rotor> rotors = code.getRotors();

        // advance
        advance(rotors);

        // forward through rotors
        intermediate = forwardTransform(rotors, intermediate);

        // reflector
        intermediate = code.getReflector().process(intermediate);

        // backward through rotors
        intermediate = backwardTransform(rotors, intermediate);

        // plugboard out
        intermediate = code.getPlugboard().process(intermediate);

        char result = keyboard.lightALamp(intermediate);

        return result;
    }

    @Override
    public Code getCode() {
        return code;
    }

    public char indexToChar(int index) {
        return keyboard.lightALamp(index);
    }

    public int getAlphabetSize() {
        return keyboard.getAlphabetSize();
    }

    private int backwardTransform(List<Rotor> rotors, int intermediate) {
        for (int i = rotors.size()-1; i >= 0; i--) {
            intermediate = rotors.get(i).process(intermediate, Direction.BACKWARD);
        }
        return intermediate;
    }

    private int forwardTransform(List<Rotor> rotors, int intermediate) {
        for (int i = 0; i < rotors.size(); i++) {
            intermediate = rotors.get(i).process(intermediate, Direction.FORWARD);
        }
        return intermediate;
    }

    private void advance(List<Rotor> rotors) {
        int rotorIndex = 0;
        boolean shouldAdvance = false;
        do {
            shouldAdvance = rotors.get(rotorIndex).advance();
            rotorIndex++;
        } while(shouldAdvance && rotorIndex < rotors.size());
    }


}

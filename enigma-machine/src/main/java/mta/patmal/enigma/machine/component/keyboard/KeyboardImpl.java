package mta.patmal.enigma.machine.component.keyboard;

public class KeyboardImpl implements Keyboard {
    private final String alphabet;

    public KeyboardImpl(String alphabet) {
        if (alphabet == null || alphabet.isEmpty()) {
            throw new IllegalArgumentException("Alphabet cannot be empty");
        }
        this.alphabet = alphabet;
    }
    @Override
    public int processChar(char input) {
        int index = alphabet.indexOf(input);
        if (index == -1) {
            throw new IllegalArgumentException("Input character not in alphabet");
        }
        return index;
    }
    @Override
    public char lightALamp(int input) {
        if (input < 0 || input >= alphabet.length()) {
            throw new IllegalArgumentException("Input index out of bounds");
        }
        return alphabet.charAt(input);
    }

    @Override
    public int getAlphabetSize() {
        return alphabet.length();
    }
}

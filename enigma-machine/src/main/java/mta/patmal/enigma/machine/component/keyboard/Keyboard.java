package mta.patmal.enigma.machine.component.keyboard;

public interface Keyboard {
    int processChar(char input);
    char lightALamp(int input);
    int getAlphabetSize();
}

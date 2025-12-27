package mta.patmal.enigma.machine.component.rotor;

public interface Rotor {
    int process(int input, Direction direction);
    boolean advance();
    int getPosition();
    void setPosition(int position);
    int getId();
    int getNotch();
}

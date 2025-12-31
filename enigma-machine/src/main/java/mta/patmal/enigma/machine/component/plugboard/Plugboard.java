package mta.patmal.enigma.machine.component.plugboard;

import java.util.Map;

public interface Plugboard {
    int process(int input);
    Map<Integer, Integer> getWiring();
}

package mta.patmal.enigma.machine.component.plugboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

public class PlugboardImpl implements Plugboard {
    private final Map<Integer, Integer> wiring;

    public PlugboardImpl(Map<Integer, Integer> wiring) {
        if (wiring == null) {
            throw new IllegalArgumentException("Plugboard wiring cannot be null");
        }
        // defensive copy + make immutable
        this.wiring = Collections.unmodifiableMap(new HashMap<>(wiring));
    }

    @Override
    public int process(int input) {
        return wiring.getOrDefault(input,input);
    }
//
//    private void validateSymmetricNoSelf(Map<Integer, Integer> wiring) {
//        for (Map.Entry<Integer, Integer> e : wiring.entrySet()) {
//            int a = e.getKey();
//            int b = e.getValue();
//
//            if (a == b) {
//                throw new IllegalArgumentException("Plugboard cannot map an index to itself: " + a);
//            }
//
//            Integer back = wiring.get(b);
//            if (back == null || back != a) {
//                throw new IllegalArgumentException("Plugboard wiring must be symmetric: " + a + "<->" + b);
//            }
//        }
//    }
}

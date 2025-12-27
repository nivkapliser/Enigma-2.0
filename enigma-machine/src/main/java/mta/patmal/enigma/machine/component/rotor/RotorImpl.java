package mta.patmal.enigma.machine.component.rotor;

import java.util.Map;

public class RotorImpl implements Rotor {
    private final int id;
    private final Map<Integer, Integer> forwardWiring;
    private final Map<Integer, Integer> backwardWiring;
    private int position;
    private final int notch;
    private final int ringSetting;

    public RotorImpl(
            int id,
            final Map<Integer, Integer> forwardWiring,
            final Map<Integer, Integer> backwardWiring,
            int position,
            final int notch,
            int ringSetting
    ) {
        this.id = id;
        this.forwardWiring = forwardWiring;
        this.backwardWiring = backwardWiring;
        this.position = position;
        this.notch = notch;
        this.ringSetting = ringSetting;
    }

    @Override
    public int process(int input, Direction direction) {
        Map<Integer, Integer> wiring = (direction == Direction.FORWARD) ? forwardWiring : backwardWiring;
        return processWithWiring(input, wiring);
    }

    private int processWithWiring(int input, Map<Integer, Integer> wiring) {
        int size = wiring.size();
        int shift = (position - ringSetting + size) % size;
        int contact = (input + shift) % size;
        int result = (wiring.get(contact) - shift + size) % size;
        return result >= 0 ? result : result + size;
    }

    @Override
    public boolean advance() {
        int size = forwardWiring.size();
        position = (position + 1) % size;
        return position == notch;   // עכשיו בודק אחרי הפסיעה
    }

    @Override
    public int getPosition() {
        return position;
    }
    @Override
    public void setPosition(int position) {
        int size = forwardWiring.size();
        this.position = Math.floorMod(position, size);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getNotch() {
        return notch;
    }
}

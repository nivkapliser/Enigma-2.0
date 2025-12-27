package mta.patmal.enigma.engine;

public class HistoryEntry {
    private final String input;
    private final String output;
    private final long durationNanos;

    public HistoryEntry(String input, String output, long durationNanos) {
        this.input = input;
        this.output = output;
        this.durationNanos = durationNanos;
    }

    public String getInput() { return input; }
    public String getOutput() { return output; }
    public long getDurationNanos() { return durationNanos; }
}

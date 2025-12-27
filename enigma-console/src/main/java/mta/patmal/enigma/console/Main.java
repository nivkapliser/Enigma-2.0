package mta.patmal.enigma.console;

import mta.patmal.enigma.engine.Engine;
import mta.patmal.enigma.engine.EngineImpl;

public class Main {

    public static void main(String[] args) {
        Engine engine = new EngineImpl();
        new ConsoleUI(engine).run();
    }
}

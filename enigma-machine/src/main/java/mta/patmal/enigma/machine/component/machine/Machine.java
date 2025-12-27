package mta.patmal.enigma.machine.component.machine;

import mta.patmal.enigma.machine.component.code.Code;

public interface Machine {
    void setCode(Code code);
    char process(char input);
    Code getCode();
}

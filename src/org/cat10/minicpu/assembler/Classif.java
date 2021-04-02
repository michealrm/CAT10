package org.cat10.minicpu.assembler;

public enum Classif {
    EMPTY,
    MNEMONIC,   // addc, mov
    REGISTER,   // R1, R6
    OPERATOR,   // $ for integer constant
    SEPARATOR,   // '[', ']', ','
    EOF,
}

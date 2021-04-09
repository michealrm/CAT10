package org.cat10.minicpu.chips.operations.registers;

import org.cat10.minicpu.chips.Chip;

/**
 * Inputs:
 * getChip("U999").getOutput("clock")
 * getInput("ChipSelect")
 * getInput("D")
 *
 * Outputs
 * getOutput("Q")
 */
public class U10_Register0 extends Chip {

    boolean masterNandOutput1;
    boolean masterNandOutput2;
    boolean slaveNandOutput1;
    boolean slaveNandOutput2;

    public U10_Register0() {
        super("U10");
    }

    @Override
    public void evaluateOut() {

    }
}

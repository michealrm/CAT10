package org.cat10.minicpu.chips.instruction;

import org.cat10.minicpu.chips.Chip;

/**
 * Inputs:
 * 0 getChip("U15").getOutput("IP")
 * 1 getChip("U14").getOutput("SP")
 * 2 getChip("U500").getOutput("INST")
 * 3 getChip("U112").getOutput("DATA"), TODO: Put in U112 and U113 that we use U112's DATA line for both U112 and U113
 *
 * Outputs
 * getOutput("MemAddr")
 */
public class U116_4to1_Mux extends Chip {

    public U116_4to1_Mux() {
        super("U116");
    }

    @Override
    public void evaluateOut() {

    }
}
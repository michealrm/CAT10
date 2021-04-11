package org.cat10.minicpu.chips.instruction;

import org.cat10.minicpu.chips.Chip;

/**
 * CLOCKED CHIP
 *
 * Inputs:
 * getChip("U115").getOutput("IPNew")
 * getInput("ChipSelect")
 *
 * Outputs
 * getOutput("IPLower")
 * getOutput("IPUpper")
 */
public class U15_InstPointer extends Chip {

    public U15_InstPointer() {
        super("U15");
        putOutput("IPLower", (byte) 0xF0); // Default entrypoint in EPROM
        putOutput("IPUpper", (byte) 0x00);
    }

    @Override
    public void evaluateOut() {

    }
}

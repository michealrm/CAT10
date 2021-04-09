package org.cat10.minicpu.chips;

/**
 * CLOCKED CHIP
 *
 * Inputs:
 * getChip("U115").getOutput("IPNew")
 * getInput("ChipSelect")
 *
 * Outputs
 * getOutput("IP")
 */
public class U15_InstPointer extends Chip {

    public U15_InstPointer() {
        super("U15");
    }

    @Override
    public void evaluateOut() {

    }
}

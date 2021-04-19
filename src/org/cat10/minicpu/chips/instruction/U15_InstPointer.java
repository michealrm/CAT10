package org.cat10.minicpu.chips.instruction;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * CLOCKED CHIP
 * TODO: Do we need to use clock here? There's a wire coming in...
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
        putInput("ChipSelect", (byte) 1);

        putOutput("IPLower", (byte) 0xF0); // Default entrypoint in EPROM
        putOutput("IPUpper", (byte) 0x00);
    }

    @Override
    public void evaluateOut() {
        // ACTIVE LOW: If clock is low, update InstPointer
        // Clock starts at low, but first updates to high before any chips, so the first
        // execution the clock will be high. We don't want to update on the first cycle because
        // U115 MUX for IPNew will be 0 because IPInc hasn't propagated
        if(getChip("U999").getOutput("clock") == (byte)0) {
            if (getInput("ChipSelect") == (byte) 1) {
                putOutput("IPLower", getChip("U115").getOutput("IPNewLower"));
                putOutput("IPUpper", getChip("U115").getOutput("IPNewUpper"));
            }
            System.out.printf("IP: %X %X\n", getOutput("IPLower"), getOutput("IPUpper"));
        }
    }
}

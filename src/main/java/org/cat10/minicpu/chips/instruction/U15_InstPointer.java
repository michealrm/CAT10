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

    // TODO: Reset vector here?

    public U15_InstPointer() {
        super("U15");
        putInput("ChipSelect", (byte) 1);

        putOutput("IPLower", (byte) 0xF0); // Default entrypoint in EPROM
        putOutput("IPUpper", (byte) 0x00);
    }

    @Override
    public void evaluateOut() {
        if(getInput("ChipSelect") == (byte) 1) {
            putOutput("IPLower", getChip("U115").getOutput("IPNewLower"));
            putOutput("IPUpper", getChip("U115").getOutput("IPNewUpper"));
        }
        System.out.printf("%X\n", ((int)getOutput("IPLower") << 4 | (int)getOutput("IPUpper")));
    }
}

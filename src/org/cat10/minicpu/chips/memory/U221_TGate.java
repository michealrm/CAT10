package org.cat10.minicpu.chips.memory;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * This transmission gate with have Output Enabled during Read (0)
 *
 * Inputs
 * getChip("U500").getOutput("ReadWrite"), 0, a read, will enable the transmission gate
 *
 * Outputs
 * getOutput("MEM")
 */
public class U221_TGate extends Chip {

    public U221_TGate() {
        super("U221");
        putOutput("MEM", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        if(getChip("U500").getOutput("ReadWrite") == (byte) 0)
            putOutput("MEM", getChip("U220").getOutput("8BitDataBus"));
    }
}

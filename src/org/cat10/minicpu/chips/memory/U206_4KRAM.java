package org.cat10.minicpu.chips.memory;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs:
 * getInput("MemAddr"), bottom 12 bits from 4-1 MUX in instruction circuit
 * getInput("ChipSelect")
 *
 * Outputs
 * getChip("U220").getOutput("8BitDataLine")
 */
public class U206_4KRAM extends Chip {

    public byte[] memory;

    public U206_4KRAM() {
        super("U206");
        memory = new byte[0x1000];
    }

    @Override
    public void evaluateOut() {
        if(getInput("ChipSelect") != 0)
            getChip("U220").putOutput("8BitDataLine", memory[getInput("MemAddr")]);
    }
}


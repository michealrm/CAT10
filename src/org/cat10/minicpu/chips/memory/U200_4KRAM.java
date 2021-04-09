package org.cat10.minicpu.chips.memory;

import org.cat10.minicpu.chips.Chip;

/**
 * CLOCKED CHIP
 *
 * Inputs:
 * getInput("MemAddr"), bottom 12 bits from 4-1 MUX in instruction circuit
 * getInput("ChipSelect")
 *
 * Outputs
 * getChip("U220").getOutput("8BitDataLine")
 */
public class U200_4KRAM extends Chip {

    public byte[] memory;

    public U200_4KRAM() {
        super("U200");
        memory = new byte[0x1000];
    }

    @Override
    public void evaluateOut() {

    }
}

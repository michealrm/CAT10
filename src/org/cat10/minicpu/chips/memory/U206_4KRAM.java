package org.cat10.minicpu.chips.memory;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs:
 * getInput("MemAddrLower"), We just need the bottom 12 bits on address bus
 * getInput("MemAddrUpper")
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

        putInput("MemAddr", (byte) 0);
        putInput("ChipSelect", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        if(getInput("ChipSelect") != 0)
            getChip("U220").putOutput("8BitDataBus", memory[(getInput("MemAddrLower") & 0xF) << 8 | getInput("MemAddrUpper")]);
    }
}


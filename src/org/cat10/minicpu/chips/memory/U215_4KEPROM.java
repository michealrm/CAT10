package org.cat10.minicpu.chips.memory;

import org.cat10.minicpu.chips.Chip;

import java.nio.ByteBuffer;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs:
 * getInput("MemAddr"), bottom 12 bits from 4-1 MUX in instruction circuit
 * getInput("ChipSelect")
 *
 * Outputs
 * getChip("U220").getOutput("8BitDataLine")
 */
public class U215_4KEPROM extends Chip {

    public ByteBuffer readonly;

    /**
     * Creates a 4K EPROM (read only memory) from a defined memory
     * @param memory predefined memory to be used in EPROM. Will create a read only copy so we don't need the original
     *               reference after the constructor
     */
    public U215_4KEPROM(byte[] memory) {
        super("U215");
        readonly = ByteBuffer.wrap(memory);
        readonly.asReadOnlyBuffer();

        putInput("MemAddr", (byte) 0);
        putInput("ChipSelect", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        if(getInput("ChipSelect") != 0)
            getChip("U220").putOutput("8BitDataBus", readonly.get((getInput("MemAddrLower") & 0xF) << 8 | getInput("MemAddrUpper")));
    }
}


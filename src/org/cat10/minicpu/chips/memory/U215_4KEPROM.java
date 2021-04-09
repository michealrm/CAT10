package org.cat10.minicpu.chips.memory;

import org.cat10.minicpu.chips.Chip;

import java.nio.ByteBuffer;

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
    }

    @Override
    public void evaluateOut() {

    }
}


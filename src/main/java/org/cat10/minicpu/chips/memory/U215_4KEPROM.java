package org.cat10.minicpu.chips.memory;

import org.cat10.minicpu.chips.Chip;

import java.nio.ByteBuffer;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs:
 * getInput("ChipSelect"), Ex: 0x1000, so if this is the 2nd chip ChipSelect would be 1
 * getChip("U116").getOutput("MemAddrLower"), we use the upper 12 bits from 4-1 MUX in instruction circuit
 * getChip("U116").getOutput("MemAddrUpper")
 *
 * Outputs
 * getChip("U220").getOutput("8BitDataLine"), for reading
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

        putInput("ChipSelect", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        if(getInput("ChipSelect") != 0) { // If this chip is selected
            // EPROM is readonly, so we don't need to check for ReadWrite control line from U500 instruction decode

            // We effectively mask 0x0FFF, where F would be a selected 4 bits, then we take the value at that
            // addr/index and put it on U220 writing MUX, which U221 T-Gate takes input from and puts out on
            // the MEM data bus
            getChip("U220").putOutput("8BitDataBus", readonly.get((getInput("MemAddrLower") & 0xF) << 8 | getInput("MemAddrUpper")));
        }
    }
}


package org.cat10.minicpu.chips.memory;

import org.cat10.minicpu.chips.Chip;
import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs:
 * getInput("ChipSelect"), Ex: 0x1000, so if this is the 2nd chip ChipSelect would be 1
 * getChip("U500").getOutput("ReadWrite")
 * getChip("U116").getOutput("MemAddrLower"), we use the upper 12 bits from 4-1 MUX in instruction circuit
 * getChip("U116").getOutput("MemAddrUpper")
 * getChip("U220").getOutput("8BitDataLine"), for writing
 *
 * Outputs
 * getChip("U220").getOutput("8BitDataLine"), for reading
 */
public class RAM4K extends Chip {

    public byte[] memory;

    public RAM4K(String chipID, byte[] memory) {
        super(chipID); // Generic chipID because we create 13 4K RAM memory chips
        this.memory = memory;

        putInput("ChipSelect", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        if(getInput("ChipSelect") != 0) { // If this chip is selected
            if(getChip("U500").getOutput("ReadWrite") == 0) { // Read
                // We effectively mask 0x0FFF, where F would be a selected 4 bits, then we take the value at that
                // addr/index and put it on U220 writing MUX, which U221 T-Gate takes input from and puts out on
                // the MEM data bus
                getChip("U220").putOutput("8BitDataBus", memory[((getChip("U116").getOutput("MemAddrLower") & 0xF) << 8 | getChip("U116").getOutput("MemAddrUpper")) & 0xFFF]);
            } else { // Write
                // Mask 0xFFF, where F is a selected 4 bits, then write the output of U220 MUX wihch takes in DATALower,
                // DATAUpper, ALU, and INST, and write that into the calculated/masked address
                // * We mask here because upper 4 bits of memory address go to U255 decoder to ChipSelect a mem chip
                int addr = ((getChip("U116").getOutput("MemAddrLower") & 0xF) << 8) | getChip("U116").getOutput("MemAddrUpper");
                memory[addr] = getChip("U220").getOutput("8BitDataBus");
            }
        }
    }
}
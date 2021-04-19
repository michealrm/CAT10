package org.cat10.minicpu.chips.memory;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * This mux with have Output Enabled during WRITE (1)
 *
 * Inputs
 * 0 getChip("U112").getOutput("DATALower")
 * 1 getChip("U113").getOutput("DATAUpper")
 * 2 getChip("U111").getOutput("ALU")
 * 3 getChip("U500").getOutput("INSTLower")
 * getInput("sel"), 0-1
 * getChip("U500").getOutput("ReadWrite")
 *
 * Output
 * getOutput("8BitDataBus")
 */
public class U220_4to1_Mux extends Chip {

    public U220_4to1_Mux() {
        super("U220");
        putInput("sel", (byte) 0);
        putOutput("8BitDataBus", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        if(getChip("U500").getOutput("ReadWrite") == (byte)1) {
            switch (getInput("sel")) {
                case 0:
                    putOutput("8BitDataBus", getChip("U112").getOutput("DATALower"));
                    break;
                case 1:
                    putOutput("8BitDataBus", getChip("U113").getOutput("DATAUpper"));
                    break;
                case 2:
                    putOutput("8BitDataBus", getChip("U111").getOutput("ALU"));
                    break;
                case 3:
                    putOutput("8BitDataBus", getChip("U111").getOutput("INSTLower"));
                    break;
            }
        }
    }
}

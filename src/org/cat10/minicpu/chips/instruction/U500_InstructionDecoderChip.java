package org.cat10.minicpu.chips.instruction;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs:
 * getInput("MEM_1")
 * getInput("MEM_2")
 * getInput("MEM_3")
 * getInput("MEM_4")
 * getChip("U110").getOutput("FLAGS")
 *
 * Outputs
 * getChip("U105").getInput("InstrLen")
 * getChip("U106").getInput("Offset")
 * getOutput("INSTLower")
 * getOutput("INSTUpper")
 *
 * Outputs (Control)
 */
public class U500_InstructionDecoderChip extends Chip {

    public U500_InstructionDecoderChip() {
        super("U500");
    }

    @Override
    public void evaluateOut() {
        if(getChip("U15").getOutput("IP") == 0xF000) {
            putOutput("InstLen", (byte) 0);
            putOutput("Offset", (byte) 0);
            putOutput("INST", (byte) 0);

            // Control lines
            getChip("U115").putInput("sel", (byte) 2);
            getChip("U16").putInput("sel", (byte) 0);
        }
    }
}

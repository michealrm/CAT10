package org.cat10.minicpu.chips;

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
 * getOutput("INST")
 *
 * Outputs (Control)
 */
public class U500_InstructionDecoderChip extends Chip {

    public U500_InstructionDecoderChip() {
        super("U500");
    }

    @Override
    public void evaluateOut() {

    }
}

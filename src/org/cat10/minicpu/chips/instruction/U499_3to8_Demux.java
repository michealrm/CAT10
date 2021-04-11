package org.cat10.minicpu.chips.instruction;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs
 * getChip("U221").getOutput("MEM")
 * getInput("sel") 0-1
 *
 * Outputs
 * getChip("U500").getInput("MEM_1")
 * getChip("U500").getInput("MEM_2")
 * getChip("U500").getInput("MEM_3")
 * getChip("U500").getInput("MEM_4")
 */
public class U499_3to8_Demux extends Chip {
    public U499_3to8_Demux() {
        super("U499");
        putInput("sel", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        switch(getInput("sel")) {
            case 0:
                getChip("U500").putInput("MEM_1", getChip("U221").getOutput("MEM"));
                break;
            case 1:
                getChip("U500").putInput("MEM_2", getChip("U221").getOutput("MEM"));
                break;
            case 2:
                getChip("U500").putInput("MEM_3", getChip("U221").getOutput("MEM"));
                break;
            case 3:
                getChip("U500").putInput("MEM_4", getChip("U221").getOutput("MEM"));
                break;
            case 4:
                break; // Do nothing, we don't need to read into MEM
        }
    }
}

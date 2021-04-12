package org.cat10.minicpu.chips.instruction;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs:
 * 0 getChip("U112").getOutput("DATALower")
 * 0 getChip("U113").getOutput("DATAUpper")
 * 1 getChip("U500").getOutput("INSTLower")
 * 1 getChip("U500").getOutput("INSTUpper")
 * 2 getChip("U105").getOutput("IPIncLower")
 * 2 getChip("U105").getOutput("IPIncUpper")
 * 3 getChip("U106").getOutput("IPRelLower")
 * 3 getChip("U106").getOutput("IPRelUpper")
 * getInput("sel") 0-1
 *
 * Outputs
 * getOutput("IPNewLower")
 * getOutput("IPNewUpper")
 */
public class U115_4to1_Mux extends Chip {

    public U115_4to1_Mux() {
        super("U115");
        putInput("sel", (byte) 0);

        putOutput("IPNewLower", (byte) 0);
        putOutput("IPNewUpper", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        switch(getInput("sel")) {
            case 0:
                putOutput("IPNewLower", getChip("U112").getOutput("DATALower"));
                putOutput("IPNewUpper", getChip("U113").getOutput("DATAUpper"));
                break;
            case 1:
                putOutput("IPNewLower", getChip("U500").getOutput("INSTLower"));
                putOutput("IPNewUpper", getChip("U500").getOutput("INSTUpper"));
                break;
            case 2:
                putOutput("IPNewLower", getChip("U105").getOutput("IPIncLower"));
                putOutput("IPNewUpper", getChip("U105").getOutput("IPIncUpper"));
                break;
            case 3:
                putOutput("IPNewLower", getChip("U106").getOutput("IPRelLower"));
                putOutput("IPNewUpper", getChip("U106").getOutput("IPRelUpper"));
                break;
        }
    }
}
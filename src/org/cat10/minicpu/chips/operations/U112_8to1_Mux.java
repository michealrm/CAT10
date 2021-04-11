package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs:
 * 0 getChip("U10").getOutput("Q")
 * 1 getChip("U11").getOutput("Q")
 * 2 getChip("U12").getOutput("Q")
 * 3 getChip("U13").getOutput("Q")
 * 4 getChip("U221").getOutput("MEM")
 * 5 getChip("U110").getOutput("FLAGS")
 * 6 getChip("U500").getOutput("INST")
 * 7 getChip("U15").getOutput("IP")
 * getInput("sel")
 *
 * Outputs
 * getOutput("DATALower")
 */
public class U112_8to1_Mux extends Chip {

    public U112_8to1_Mux() {
        super("U112");
    }

    @Override
    public void evaluateOut() {
        switch(getInput("sel")) {
            case 0:
                putOutput("DATALower", getChip("U10").getOutput("Q"));
                break;
            case 1:
                putOutput("DATALower", getChip("U11").getOutput("Q"));
                break;
            case 2:
                putOutput("DATALower", getChip("U12").getOutput("Q"));
                break;
            case 3:
                putOutput("DATALower", getChip("U13").getOutput("Q"));
                break;
            case 4:
                putOutput("DATALower", getChip("U221").getOutput("MEM"));
                break;
            case 5:
                putOutput("DATALower", getChip("U110").getOutput("FLAGS"));
                break;
            case 6:
                putOutput("DATALower", getChip("U500").getOutput("INST"));
                break;
            case 7:
                putOutput("DATALower", getChip("U15").getOutput("IP"));
                break;
        }
    }
}

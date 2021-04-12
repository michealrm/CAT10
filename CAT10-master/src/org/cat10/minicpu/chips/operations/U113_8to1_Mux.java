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
 * getOutput("DATAUpper")
 */
public class U113_8to1_Mux extends Chip {

    public U113_8to1_Mux() {
        super("U113");
        putInput("sel", (byte) 0);
        putOutput("DATAUpper", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        switch(getInput("sel")) {
            case 0:
                putOutput("DATAUpper", getChip("U10").getOutput("Q"));
                break;
            case 1:
                putOutput("DATAUpper", getChip("U11").getOutput("Q"));
                break;
            case 2:
                putOutput("DATAUpper", getChip("U12").getOutput("Q"));
                break;
            case 3:
                putOutput("DATAUpper", getChip("U13").getOutput("Q"));
                break;
            case 4:
                putOutput("DATAUpper", getChip("U221").getOutput("MEM"));
                break;
            case 5:
                putOutput("DATAUpper", getChip("U110").getOutput("FLAGS"));
                break;
            case 6:
                putOutput("DATAUpper", getChip("U500").getOutput("INST"));
                break;
            case 7:
                putOutput("DATAUpper", getChip("U15").getOutput("IP"));
                break;
        }
    }
}
package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs
 * 0 getChip("U112").getOutput("RegA")
 * 1 getChip("U14").getOutput("SPLowerByte")
 * 2 getChip("U500").getOutput("INSTUpper")
 * 3 getChip("U111").getOutput("ALU")
 * 5 getChip("U221").getOutput("MEM")
 * getInput("sel"), 3 bits
 * 4, 6, and 7 are reserve
 *
 * Outputs
 * getOutput("B")
 */
public class U118B_8to1_Mux extends Chip {

    public U118B_8to1_Mux() {
        super("U118B");
    }

    @Override
    public void evaluateOut() {
        switch(getInput("sel")) {
            case 0:
                putOutput("B", getChip("U112").getOutput("RegA"));
                break;
            case 1:
                putOutput("B", getChip("U14").getOutput("SPLowerByte"));
                break;
            case 2:
                putOutput("B", (byte)(getChip("U500").getOutput("INSTUpper")));
                break;
            case 3:
                putOutput("B", getChip("U111").getOutput("ALU"));
                break;
            case 5:
                putOutput("B", getChip("U221").getOutput("MEM"));
                break;
        }
    }
}

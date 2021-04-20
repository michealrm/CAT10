package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs
 * 0 getChip("U112").getOutput("RegA")
 * 1 getChip("U14").getOutput("SP")
 * 2 getChip("U500").getOutput("INST")
 * 3 getChip("U111").getOutput("ALU")
 * 5 getChip("U221").getOutput("MEM")
 * getInput("sel"), 3 bits
 * 4, 6, and 7 are reserve
 *
 * Outputs
 * getOutput("A")
 */
public class U118A_8to1_Mux extends Chip {

    public U118A_8to1_Mux() {
        super("U118A");
        putInput("sel", (byte) 0);
        putOutput("A", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        switch(getInput("sel")) {
            case 0:
                putOutput("A", getChip("U112").getOutput("DATALower"));
                if(getOutput("A") != 0)
                	System.out.println("Not zero, yo!");
                if(getOutput("A") == 0)
                	System.out.println("ya wey zero!");
                break;
            case 1:
                putOutput("A", getChip("U14").getOutput("SPLower"));
                break;
            case 2:
                putOutput("A", getChip("U500").getOutput("INSTLower"));
                break;
            case 3:
                putOutput("A", getChip("U111").getOutput("ALU"));
                break;
            case 5:
                putOutput("A", getChip("U221").getOutput("MEM"));
                break;
        }
    }
}

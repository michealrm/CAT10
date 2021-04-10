package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs
 * getChip("U118A").getOutput("A")
 * getChip("U118B").getOutput("B")
 * getInput("OutputEnableA")
 * getInput("SelA")
 * getInput("SelB")
 * getInput("OutputEnableB")
 *
 * Outputs
 * getChip("U10").getInput("D")
 * getChip("U11").getInput("D")
 * getChip("U12").getInput("D")
 * getChip("U13").getInput("D")
 * getChip("U10").getInput("ChipSelect")
 * getChip("U11").getInput("ChipSelect")
 * getChip("U12").getInput("ChipSelect")
 * getChip("U13").getInput("ChipSelect")
 */
public class U114_2to4_Demux extends Chip {
    public U114_2to4_Demux() {
        super("U114");
    }

    @Override
    public void evaluateOut() {
        if(getInput("OutputEnableA") != 0) {
            switch(getInput("SelA")) {
                case 0:
                    getChip("U10").putOutput("ChipSelect", (byte)1);
                    getChip("U10").putOutput("D", getChip("U118A").getOutput("A"));
                    break;
                case 1:
                    getChip("U11").putOutput("ChipSelect", (byte)1);
                    getChip("U11").putOutput("D", getChip("U118A").getOutput("A"));
                    break;
                case 2:
                    getChip("U12").putOutput("ChipSelect", (byte)1);
                    getChip("U12").putOutput("D", getChip("U118A").getOutput("A"));
                    break;
                case 3:
                    getChip("U13").putOutput("ChipSelect", (byte)1);
                    getChip("U13").putOutput("D", getChip("U118A").getOutput("A"));
                    break;
            }
        }
        if(getInput("OutputEnableB") != 0) {
            switch(getInput("SelB")) {
                case 0:
                    getChip("U10").putOutput("ChipSelect", (byte)1);
                    getChip("U11").putOutput("ChipSelect", (byte)0);
                    getChip("U12").putOutput("ChipSelect", (byte)0);
                    getChip("U13").putOutput("ChipSelect", (byte)0);
                    getChip("U10").putOutput("D", getChip("U118B").getOutput("B"));
                    break;
                case 1:
                    getChip("U10").putOutput("ChipSelect", (byte)0);
                    getChip("U11").putOutput("ChipSelect", (byte)1);
                    getChip("U12").putOutput("ChipSelect", (byte)0);
                    getChip("U13").putOutput("ChipSelect", (byte)0);
                    getChip("U11").putOutput("D", getChip("U118B").getOutput("B"));
                    break;
                case 2:
                    getChip("U10").putOutput("ChipSelect", (byte)0);
                    getChip("U11").putOutput("ChipSelect", (byte)0);
                    getChip("U12").putOutput("ChipSelect", (byte)1);
                    getChip("U13").putOutput("ChipSelect", (byte)0);
                    getChip("U12").putOutput("D", getChip("U118B").getOutput("B"));
                    break;
                case 3:
                    getChip("U10").putOutput("ChipSelect", (byte)0);
                    getChip("U11").putOutput("ChipSelect", (byte)0);
                    getChip("U12").putOutput("ChipSelect", (byte)0);
                    getChip("U13").putOutput("ChipSelect", (byte)1);
                    getChip("U13").putOutput("D", getChip("U118A").getOutput("A"));
                    break;
            }
        }
    }
}

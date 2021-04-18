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
        putInput("OutputEnableA", (byte) 0);
        putInput("SelA", (byte) 0);
        putInput("SelB", (byte) 0);
        putInput("OutputEnableB", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        demux("A");
        demux("B");
    }

    /**
     * A function to handle A and B lines and selections for U114 DEMUX
     * The `type` concatenation (for the A demux) is used for "SelA", "OutputEnableA", and the "A" output wire on U118A chip
     * @param type Either A or B
     */
    private void demux(String type) {
        if(getInput("OutputEnable" + type) != 0) {
            switch(getInput("Sel" + type)) {
                case 0:
                    getChip("U10").putOutput("ChipSelect", (byte)1);
                    getChip("U11").putOutput("ChipSelect", (byte)0);
                    getChip("U12").putOutput("ChipSelect", (byte)0);
                    getChip("U13").putOutput("ChipSelect", (byte)0);
                    getChip("U10").putOutput("D", getChip("U118" + type).getOutput(type));
                    break;
                case 1:
                    getChip("U10").putOutput("ChipSelect", (byte)0);
                    getChip("U11").putOutput("ChipSelect", (byte)1);
                    getChip("U12").putOutput("ChipSelect", (byte)0);
                    getChip("U13").putOutput("ChipSelect", (byte)0);
                    getChip("U11").putOutput("D", getChip("U118" + type).getOutput(type));
                    break;
                case 2:
                    getChip("U10").putOutput("ChipSelect", (byte)0);
                    getChip("U11").putOutput("ChipSelect", (byte)0);
                    getChip("U12").putOutput("ChipSelect", (byte)1);
                    getChip("U13").putOutput("ChipSelect", (byte)0);
                    getChip("U12").putOutput("D", getChip("U118" + type).getOutput("A"));
                    break;
                case 3:
                    getChip("U13").putOutput("ChipSelect", (byte)1);
                    getChip("U13").putOutput("D", getChip("U118" + type).getOutput("A"));
                    break;
            }
        }
    }
}

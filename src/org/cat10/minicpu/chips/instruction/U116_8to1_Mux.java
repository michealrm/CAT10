package org.cat10.minicpu.chips.instruction;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs:
 * 0 getChip("U15").getOutput("IPLower")
 * 0 getChip("U15").getOutput("IPUpper")
 * 1 getChip("U14").getOutput("SPLower")
 * 1 getChip("U14").getOutput("SPUpper")
 * 2 getChip("U500").getOutput("INSTLower")
 * 2 getChip("U500").getOutput("INSTUpper")
 * 3 getChip("U112").getOutput("DATALower")
 * 3 getChip("U113").getOutput("DATAUpper")
 * 4 getChip("U500").getOutput("MemFetchLower")
 * 4 getChip("U500").getOutput("MemFetchUpper")
 * getInput("sel") 0-1
 *
 * Outputs
 * getOutput("MemAddrLower")
 * getOutput("MemAddrUpper")
 */
public class U116_8to1_Mux extends Chip {

    public U116_8to1_Mux() {
        super("U116");
        putInput("sel", (byte) 0);
        putOutput("MemAddr", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        switch(getInput("sel")) {
            case 0:
                putOutput("MemAddrLower", getChip("U15").getOutput("IPLower"));
                putOutput("MemAddrUpper", getChip("U15").getOutput("IPUpper"));
                break;
            case 1:
                putOutput("MemAddrLower", getChip("U14").getOutput("SPLower"));
                putOutput("MemAddrUpper", getChip("U14").getOutput("SPUpper"));
                break;
            case 2:
                putOutput("MemAddrLower", getChip("U500").getOutput("INSTLower"));
                putOutput("MemAddrUpper", getChip("U500").getOutput("INSTUpper"));
                break;
            case 3:
                putOutput("MemAddrLower", getChip("U112").getOutput("DATALower"));
                putOutput("MemAddrUpper", getChip("U113").getOutput("DATAUpper"));
                break;
            case 4:
                putOutput("MemAddrLower", getChip("U500").getOutput("MemFetchLower"));
                putOutput("MemAddrUpper", getChip("U500").getOutput("MemFetchUpper"));
                break;
        }
    }
}
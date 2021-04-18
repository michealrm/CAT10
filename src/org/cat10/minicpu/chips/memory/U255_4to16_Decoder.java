package org.cat10.minicpu.chips.memory;

import org.cat10.minicpu.chips.Chip;

import java.util.ArrayList;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs
 * getChip("U116").getOutput("MemAddrLower"), will only use lower 4 bits, so mast
 *
 * Output
 * All mem chips' ChipSelect input
 */
public class U255_4to16_Decoder extends Chip {

    private final ArrayList<Chip> memChips;

    public U255_4to16_Decoder() {
        super("U255");

        memChips = new ArrayList<>();
        memChips.add(getChip("U200"));
        memChips.add(getChip("U201"));
        memChips.add(getChip("U202"));
        memChips.add(getChip("U203"));
        memChips.add(getChip("U204"));
        memChips.add(getChip("U205"));
        memChips.add(getChip("U206"));
        memChips.add(getChip("U207"));
        memChips.add(getChip("U208"));
        memChips.add(getChip("U209"));
        memChips.add(getChip("U210"));
        memChips.add(getChip("U211"));
        memChips.add(getChip("U212"));
        memChips.add(getChip("U213"));
        memChips.add(null); // IO Chip is not implemented
        memChips.add(getChip("U215"));
    }

    @Override
    public void evaluateOut() {
        // Select the index of the first 4 bits of the address
        int selectedMemChip = (getChip("U116").getOutput("MemAddrLower") & 0xF0) >> 4;
        // Select that chip. EX: Addr is 0xF000, we mask and chip to 0xF, and grab the 15th index which is the EPROM
        memChips.get(selectedMemChip).putOutput("ChipSelect", (byte)1);
        // Now set other chips' ChipSelect to 0
        for(int i = 0; i < memChips.size(); i++) {
            Chip chip = memChips.get(i);
            if (chip != null && i != selectedMemChip)
                chip.putInput("ChipSelect", (byte)0);
        }
    }
}

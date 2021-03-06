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

    private ArrayList<Chip> memChips;
    // Can't update mem chips in constructor because this chip comes before mem chips
    private boolean memChipsEntered = false;

    public U255_4to16_Decoder() {
        super("U255");

        memChips = new ArrayList<>();
    }

    @Override
    public void evaluateOut() {
        // Mem chips come after this decoder in CPU order, so we can't add these adds to the constructor
        if(!memChipsEntered) {
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
            memChipsEntered = true;
        }

        // Select the index of the first 4 bits of the address
        byte lower = getChip("U116").getOutput("MemAddrLower");
        byte selectedMemChip = (byte)((lower >> 4) & 0xF); // F0 -> F
        // Don't access IO chip because it's null
        if(selectedMemChip != 14)
            // Select that chip. EX: Addr is 0xF000, we mask and chip to 0xF, and grab the 15th index which is the EPROM
            memChips.get(selectedMemChip).putInput("ChipSelect", (byte) 1);
        // Now set other chips' ChipSelect to 0
        for(int i = 0; i < memChips.size(); i++) {
            Chip chip = memChips.get(i);
            if (chip != null && i != selectedMemChip)
                chip.putInput("ChipSelect", (byte)0);
        }
    }
}

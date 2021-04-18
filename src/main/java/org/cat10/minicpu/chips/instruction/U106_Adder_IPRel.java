package org.cat10.minicpu.chips.instruction;

import org.cat10.minicpu.chips.Chip;
import org.cat10.minicpu.util.CAT10Util;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Input
 * getChip("U105").getOutput("IPIncUpper")
 * getChip("U105").getOutput("IPIncUpper")
 * getChip("U500").getOutput("Offset")
 *
 * Output
 * getOutput("IPRelLower")
 * getOutput("IPRelUpper")
 */
public class U106_Adder_IPRel extends Chip {
    public U106_Adder_IPRel() {
        super("U106");
    }

    @Override
    public void evaluateOut() {
        CAT10Util.AdderOutput upperByteOutput = CAT10Util.fullAdderByte((byte) 0, getChip("U105").getOutput("IPIncLower"), getChip("U500").getOutput("Offset"));
        CAT10Util.AdderOutput lowerByteOutput = CAT10Util.fullAdderByte((byte) 0, getChip("U105").getOutput("IPIncUpper"), upperByteOutput.carryOut);
        putOutput("IPRelLower", lowerByteOutput.sum);
        putOutput("IPRelUpper", upperByteOutput.sum);
    }
}

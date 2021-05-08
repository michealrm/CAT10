package org.cat10.minicpu.chips.instruction;

import org.cat10.minicpu.chips.Chip;
import org.cat10.minicpu.util.CAT10Util;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Input
 * getChip("U15").getOutput("IPLower")
 * getChip("U15").getOutput("IPUpper")
 * getChip("U500").getOutput("InstLen")
 *
 * Output
 * getOutput("IPIncLower")
 * getOutput("IPIncUpper")
 */
public class U105_Adder_IPInc extends Chip {
    public U105_Adder_IPInc() {
        super("U105");
        putOutput("IPIncLower", (byte) 0);
        putOutput("IPIncUpper", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        CAT10Util.AdderOutput upperByteOutput = CAT10Util.fullAdderByte((byte) 0, getChip("U15").getOutput("IPUpper"), getChip("U500").getOutput("InstLen"));
        CAT10Util.AdderOutput lowerByteOutput = CAT10Util.fullAdderByte((byte) 0, getChip("U15").getOutput("IPLower"), upperByteOutput.carryFlag);
        putOutput("IPIncLower", lowerByteOutput.sum);
        putOutput("IPIncUpper", upperByteOutput.sum);
    }
}

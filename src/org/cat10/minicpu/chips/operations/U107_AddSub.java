package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;
import org.cat10.minicpu.util.CAT10Util;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Input
 * getChip("U14").getOutput("SPLower")
 * getChip("U14").getOutput("SPUpper")
 * getInput("CarryIn")
 *
 * Output
 * getOutput("SPSumLower")
 * getOutput("SPSumUpper")
 */
public class U107_AddSub extends Chip {
	public U107_AddSub() {
		super("U107");
		putOutput("IPIncLower", (byte) 0);
		putOutput("IPIncUpper", (byte) 0);
	}

	@Override
	public void evaluateOut() {
		CAT10Util.AdderOutput upperByteOutput = CAT10Util.fullAdderByte(getInput("CarryIn"), getChip("U14").getOutput("SPLower"), (byte) 2);
		CAT10Util.AdderOutput lowerByteOutput = CAT10Util.fullAdderByte((byte) 0, getChip("U14").getOutput("SPUpper"), upperByteOutput.carryOut);
		putOutput("SPSumLower", lowerByteOutput.sum);
		putOutput("SPSumUpper", upperByteOutput.sum);
	}
}

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
		putInput("CarryIn", (byte) 0);
		putOutput("SPSumLower", (byte) 0);
		putOutput("SPSumUpper", (byte) 0);
	}

	@Override
	public void evaluateOut() {
		CAT10Util.AdderOutput upperByteOutput = CAT10Util.fullAdderByte(getInput("CarryIn"), getChip("U14").getOutput("SPUpper"), (byte) 2);
		CAT10Util.AdderOutput lowerByteOutput = new CAT10Util.AdderOutput();
		lowerByteOutput.sum = getChip("U14").getOutput("SPLower"); // Default if we don't add from carry or sub from overflow

		if(getInput("CarryIn") == (byte) 0 && upperByteOutput.carryFlag != (byte) 0)
			lowerByteOutput = CAT10Util.fullAdderByte((byte) 0, getChip("U14").getOutput("SPLower"), (byte) 1);
		else if(getInput("CarryIn") == (byte) 1 && upperByteOutput.overflowFlag != (byte) 0)
			lowerByteOutput = CAT10Util.fullAdderByte((byte) 1, getChip("U14").getOutput("SPLower"), (byte) 1);
		putOutput("SPSumLower", lowerByteOutput.sum);
		putOutput("SPSumUpper", upperByteOutput.sum);
	}
}

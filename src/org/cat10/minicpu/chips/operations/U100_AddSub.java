package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;
import org.cat10.minicpu.util.CAT10Util;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Input
 * getChip("U112").getOutput("DATALower")
 * getChip("U113").getOutput("DATAUpper")
 * getChip("U500").getOutput("ALUAdderCarryIn")
 *
 * Output
 * getOutput("SUM")
 * getOutput("FlagsOut")
 */
public class U100_AddSub extends Chip {
	public U100_AddSub() {
		super("U100");
	}

	@Override
	public void evaluateOut() {
		CAT10Util.AdderOutput out = CAT10Util.fullAdderByte(getChip("U500").getOutput("ALUAdderCarryIn"), getChip("U112").getOutput("DATALower"), getChip("U113").getOutput("DATAUpper"));
		putOutput("SUM", out.sum);
		putOutput("FlagsOut", out.carryFlag);
	}
}

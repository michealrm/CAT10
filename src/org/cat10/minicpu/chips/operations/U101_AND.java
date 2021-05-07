package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;
import org.cat10.minicpu.util.CAT10Util;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * AND
 * Inputs
 * getChip("U112").getOutput("DATALower")
 * getChip("U113").getOutput("DATAUpper")
 * Outputs
 * getOutput("AND")
 */

public class U101_AND extends Chip{
	public U101_AND() {
		super("U101");
		putOutput("AND", (byte) 0);
	}

	@Override
	public void evaluateOut() {
		byte and = (byte)(getChip("U112").getOutput("DATALower") & getChip("U113").getOutput("DATAUpper"));
		putOutput("AND", and);
		putOutput("FlagsOut", CAT10Util.getSignAndZeroFlag(and));
	}
}

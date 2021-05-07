package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;
import org.cat10.minicpu.util.CAT10Util;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * OR
 * Inputs
 * getChip("U112").getOutput("DATALower")
 * getChip("U113").getOutput("DATAUpper")
 * Outputs
 * getOutput("OR")
 */

public class U102_OR extends Chip{
	/*
	 * OR
	 * Inputs
	 * getChip("U112").getOutput("DATALower")
	 * getChip("U113").getOutput("DATAUpper")
	 * Outputs
	 * getOutput("OR")
	 */
	public U102_OR() {
		super("U102");
		putOutput("OR", (byte) 0);
	}

	@Override
	public void evaluateOut() {
		byte or = (byte)(getChip("U112").getOutput("DATALower") | getChip("U113").getOutput("DATAUpper"));
		putOutput("OR", or);
		putOutput("FlagsOut", CAT10Util.getSignAndZeroFlag(or));
	}
}

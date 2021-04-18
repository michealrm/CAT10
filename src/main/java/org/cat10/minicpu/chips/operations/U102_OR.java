package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;

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
		putOutput("OR", (byte)(getChip("U112").getOutput("DATALower") | getChip("U113").getOutput("DATAUpper")));
	}
}

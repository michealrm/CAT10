package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;
import static org.cat10.minicpu.util.CAT10Util.Not;
import static org.cat10.minicpu.util.CAT10Util.NotByte;

/**
 * NOT
 * Inputs
 * getChip("U112").getOutput("DATALower")
 * getChip("U113").getOutput("DATAUpper")
 * Outputs
 * getOutput("NOT")
 */

public class U104_NOT extends Chip{
	/*
	 * NOT
	 * Inputs
	 * getChip("U112").getOutput("DATALower")
	 * getChip("U113").getOutput("DATAUpper")
	 * Outputs
	 * getOutput("NOT")
	 */
	public U104_NOT() {
		super("U104");
		putOutput("NOT", (byte) 0);
	}

	@Override
	public void evaluateOut() {
		putOutput("NOT", NotByte(getChip("U113").getOutput("DATAUpper")));
	}
}

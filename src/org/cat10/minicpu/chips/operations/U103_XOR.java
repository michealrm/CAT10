package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * XOR
 * Inputs
 * getChip("U112").getOutput("DATALower")
 * getChip("U113").getOutput("DATAUpper")
 * Outputs
 * getOutput("XOR")
 */

public class U103_XOR extends Chip{
	public U103_XOR() {
		super("U103");
		putOutput("XOR", (byte) 0);
	}

	@Override
	public void evaluateOut() {
		putOutput("XOR", (byte)(getChip("U112").getOutput("DATALower") ^ getChip("U113").getOutput("DATAUpper")));
	}

	/* Output Xor(0-7) to U111 */
}

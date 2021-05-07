package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;
import org.cat10.minicpu.util.CAT10Util;

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
		byte xor = (byte)(getChip("U112").getOutput("DATALower") ^ getChip("U113").getOutput("DATAUpper"));
		putOutput("XOR", xor);
		putOutput("FlagsOut", CAT10Util.getSignAndZeroFlag(xor));
	}

	/* Output Xor(0-7) to U111 */
}

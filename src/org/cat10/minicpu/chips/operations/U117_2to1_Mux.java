package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs:
 * 0 getChip("U112").getOutput("DATALower")
 * 0 getChip("U113").getOutput("DATAUpper")
 * 1 getChip("U107").getOutput("SPSumLower")
 * 1 getChip("U107").getOutput("SPSumUpper")
 * getInput("sel"), 0-1
 *
 * Outputs
 * getOutput("SPNewLower")
 * getOutput("SPNewUpper")
 */

public class U117_2to1_Mux extends Chip{
	public U117_2to1_Mux() {
		super("U117");
		putOutput("SPNewLower", (byte) 0);
		putOutput("SPNewUpper", (byte) 0);
	}

	@Override
	public void evaluateOut() {
		switch(getInput("sel")) {
			case 0:
				putOutput("SPNewLower", getChip("U112").getOutput("DATALower"));
				putOutput("SPNewUpper", getChip("U113").getOutput("DATAUpper"));
				break;
			case 1:
				putOutput("SPNewLower", getChip("U107").getOutput("SPSumLower"));
				putOutput("SPNewUpper", getChip("U107").getOutput("SPSumUpper"));
				break;
		}
	}

}

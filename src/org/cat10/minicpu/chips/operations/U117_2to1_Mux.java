package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs:
 * 0 getChip("U117").getOutput("SPLower")
 * 1 getChip("U117").getOutput("SPUpper")
 * 2 getChip("U107").getOutput("SP_SUM")
 * getInput("sel")
 *
 * Outputs
 * getOutput("FLAGS")
 */

public class U117_2to1_Mux extends Chip{
	public U117_2to1_Mux() {
		super("U117");
		putInput("sel", (byte) 0);
		putInput("SPLower", (byte) 0);
		putInput("SPUpper", (byte) 0);
		putOutput("R", (byte) 0);
	}

	@Override
	public void evaluateOut() {
		
	}

}

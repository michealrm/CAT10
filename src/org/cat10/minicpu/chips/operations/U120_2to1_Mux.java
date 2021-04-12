package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs:
 * 0 getChip("U100").getOutput("FLAGS")
 * 1 getChip("U111").getOutput("ALU")
 * getInput("sel")
 *
 * Outputs
 * getOutput("FlagsOut")
 */

public class U120_2to1_Mux extends Chip{
	public U120_2to1_Mux() {
		super("U120");
		putInput("sel", (byte) 0);
		putInput("ALU", (byte) 0);
		putInput("FLAGS", (byte) 0);
	}

	@Override
	public void evaluateOut() {
		switch(getInput("FLAGS")) {
			case 0:
				putOutput("FlagsOut", getChip("U100").getOutput(""));
				break;
			case 1:
				putOutput("FlagsOut", getChip("U100").getOutput(""));
				break;
			case 2:
				putOutput("FlagsOut", getChip("U100").getOutput(""));
				break;
			case 3:
				putOutput("FlagsOut", getChip("U100").getOutput(""));
				break;
		}
		
	}

}

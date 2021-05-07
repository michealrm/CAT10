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
 * getOutput("FLAGS")
 */

public class U120_2to1_Mux extends Chip{
	public U120_2to1_Mux() {
		super("U120");
		putInput("sel", (byte) 0);
		//putInput("ALU", (byte) 0);
		putOutput("FlagsOut", (byte) 0);

	}

	@Override
	public void evaluateOut() {
		switch(getInput("sel")) {
			case 0:
				putOutput("FlagsOut", getChip("U100").getOutput("FlagsOut"));
				break;
			case 1:
				putOutput("FlagsOut", getChip("U101").getOutput("AND"));
				break;
			case 2:
				putOutput("FlagsOut", getChip("U102").getOutput("OR"));
				break;
			case 3:
				putOutput("FlagsOut", getChip("U103").getOutput("XOR"));
				break;
			case 4:
				putOutput("FlagsOut", getChip("U104").getOutput("NOT"));
				break;
		}
		
	}

}

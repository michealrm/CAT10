package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs:
 * 0 getChip("U100").getOutput("SUM")
 * 1 getChip("U101").getOutput("AND")
 * 2 getChip("U102").getOutput("OR")
 * 3 getChip("U103").getOutput("XOR")
 * 4 getChip("U104").getOutput("NOT")
 * getInput("sel")
 *
 * Outputs
 * getOutput("ALU")
 */

public class U111_8to1_Mux extends Chip{
	public U111_8to1_Mux() {
		super("U111");
		putInput("sel", (byte) 0);
		putOutput("ALU", (byte) 0);
	}

	@Override
	public void evaluateOut() {
		switch(getInput("sel")) {
		case 0:
			putOutput("ALU", getChip("U100").getOutput("SUM"));
			break;
		case 1:
			putOutput("ALU", getChip("U101").getOutput("AND"));
			break;
		case 2:
			putOutput("ALU", getChip("U102").getOutput("OR"));
			break;
		case 3:
			putOutput("ALU", getChip("U103").getOutput("XOR"));
			break;
		case 4:
			putOutput("ALU", getChip("U104").getOutput("NOT"));
			break;
		}
		
	}

}

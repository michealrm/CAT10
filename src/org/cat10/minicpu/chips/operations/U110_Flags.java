package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;

/* Inputs:
 * 0 getChip("U100").getOutput("FLAGS")
 * 1 getChip("U111").getOutput("ALU")
 * getInput("CS")
 * getInput("Clk", .getOutput("U999")
 * 
 * Outputs:
 * getOutput("FLAGS")
 */

public class U110_Flags extends Chip{
	public U110_Flags() {
		super("U110");
		putInput("CS", (byte) 0);
		putInput("Clk", (byte) 0);
		putInput("FLAGS", (byte) 0);
		putInput("ALU", (byte) 0);
	}

	@Override
	public void evaluateOut() {
		
		
	}
}
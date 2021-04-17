package org.cat10.minicpu.chips.instruction;

import org.cat10.minicpu.chips.Chip;

/*
 * Iterate until count =< 4
 * MEM_1 == 0
 * MEM_2 == 1
 * MEM_3 == 2
 * MEM_4 == 4
 * 
 * Output:
 * getChip(U499).getInput(Cell)
 */

public class U498_Select_Adder extends Chip{
	public U498_Select_Adder() {
		super("U498");
		putInput("MEM_1", (byte) 0);
		putInput("MEM_2", (byte) 0);
		putInput("MEM_3", (byte) 0);
		putInput("MEM_4", (byte) 0);
	}

	@Override
	public void evaluateOut() {
		// TODO Auto-generated method stub
		
	}

}

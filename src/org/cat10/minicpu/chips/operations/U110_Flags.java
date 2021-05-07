package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;
import static org.cat10.minicpu.util.CAT10Util.*;
import static org.cat10.minicpu.util.CAT10Util.Nand;

/* Inputs:
 * getChip("U120").getOutput("FlagsOut")
 * getInput("CS")
 * getInput("Clk", .getOutput("U999")
 * 
 * Outputs:
 * getOutput("FLAGS")
 */

public class U110_Flags extends Chip{
	byte masterNandOutput1a;
	byte masterNandOutput1b;
	byte masterNandOutput2a;
	byte masterNandOutput2b;
	byte slaveNandOutput1a;
	byte slaveNandOutput1b;
	byte slaveNandOutput2a;
	byte slaveNandOutput2b;
	public U110_Flags() {
		super("U110");
		putInput("ChipSelect", (byte) 0);
		putInput("CS", (byte) 0);
		putInput("Clk", (byte) 0);
		putOutput("FLAGS", (byte) 0);
		putInput("ALU", (byte) 0);
	}

	@Override
	public void evaluateOut() {
		byte Flags = getChip("U120").getOutput("FlagsOut");
		byte clock = getChip("U999").getOutput("clock");
		byte notClock = Not(clock);

		if(clock == 1)
			clock = (byte)0xFF;
		if(notClock == 1)
			notClock = (byte)0xFF;


		if(getInput("ChipSelect") != 0) {
			masterNandOutput1a = Nand(Flags, clock);
			masterNandOutput1b = Nand(NotByte(Flags), clock);
			masterNandOutput2b = Nand(masterNandOutput1b, masterNandOutput2a);
			masterNandOutput2a = Nand(masterNandOutput1a, masterNandOutput2b);

			slaveNandOutput1a = Nand(masterNandOutput2a, notClock);
			slaveNandOutput1b = Nand(notClock, masterNandOutput2b);
			slaveNandOutput2b = Nand(slaveNandOutput2a, slaveNandOutput1b);
			slaveNandOutput2a = Nand(slaveNandOutput1a, slaveNandOutput2b);

			putOutput("FLAGS", slaveNandOutput2a);
			putInput("ChipSelect", (byte) 0);
		}
	}
}
package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;
import static org.cat10.minicpu.util.CAT10Util.*;
import static org.cat10.minicpu.util.CAT10Util.Nand;

/**
 * Pop = increment
 * Push = decrement
 *
 * Inputs:
 * getChip("U999").getOutput("clock")
 * getInput("ChipSelect")
 * getChip("U117").getOutput("SPNewLower")
 * getChip("U117").getOutput("SPNewUpper")
 *
 * Outputs
 * getOutput("SPLower")
 * getOutput("SPUpper")
 */
public class U14_StackPointer extends Chip {

    short masterNandOutput1a;
    short masterNandOutput1b;
    short masterNandOutput2a;
    short masterNandOutput2b;
    short slaveNandOutput1a;
    short slaveNandOutput1b;
    short slaveNandOutput2a;
    short slaveNandOutput2b;

    public U14_StackPointer() {
        super("U14");
        putInput("ChipSelect", (byte) 0);
        putOutput("SPLower", (byte) 0xC0); // SP set to 0xC000
        putOutput("SPUpper", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        short newSP = (short) ((getChip("U117").getOutput("SPNewLower") << 8) | getChip("U117").getOutput("SPNewUpper"));
        short clock = getChip("U999").getOutput("clock");
        short notClock = Not((byte)clock);

        if(clock == 1)
            clock = (short)0xFFFF;
        if(notClock == 1)
            notClock = (short)0xFFFF;


        if(getInput("ChipSelect") != 0) {
            masterNandOutput1a = Nand(newSP, clock);
            masterNandOutput1b = Nand(NotShort(newSP), clock);
            masterNandOutput2b = Nand(masterNandOutput1b, masterNandOutput2a);
            masterNandOutput2a = Nand(masterNandOutput1a, masterNandOutput2b);

            slaveNandOutput1a = Nand(masterNandOutput2a, notClock);
            slaveNandOutput1b = Nand(notClock, masterNandOutput2b);
            slaveNandOutput2b = Nand(slaveNandOutput2a, slaveNandOutput1b);
            slaveNandOutput2a = Nand(slaveNandOutput1a, slaveNandOutput2b);

            putOutput("SPLower", (byte) ((slaveNandOutput2a & 0xFF00) >> 8));
            putOutput("SPUpper", (byte) (slaveNandOutput2a & 0xFF));

            putInput("ChipSelect", (byte) 0);
        }
    }
}

package org.cat10.minicpu.chips.operations.registers;

import org.cat10.minicpu.CPU;
import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;
import static org.cat10.minicpu.util.CAT10Util.*;

/**
 * Inputs:
 * getChip("U999").getOutput("clock")
 * getInput("ChipSelect")
 * getInput("D")
 *
 * Outputs
 * getOutput("Q")
 */
public class U13_Register3 extends Chip {

    byte masterNandOutput1a;
    byte masterNandOutput1b;
    byte masterNandOutput2a;
    byte masterNandOutput2b;
    byte slaveNandOutput1a;
    byte slaveNandOutput1b;
    byte slaveNandOutput2a;
    byte slaveNandOutput2b;

    public U13_Register3() {
        super("U13");
        putInput("ChipSelect", (byte) 0);
        putInput("D", (byte) 0);
        putOutput("Q", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        byte D = getInput("D");
        byte clock = getChip("U999").getOutput("clock");
        byte notClock = Not(clock);

        if(clock == 1)
            clock = (byte)0xFF;
        if(notClock == 1)
            notClock = (byte)0xFF;


        if(getInput("ChipSelect") != 0) {
            masterNandOutput1a = Nand(D, clock);
            masterNandOutput1b = Nand(NotByte(D), clock);
            masterNandOutput2b = Nand(masterNandOutput1b, masterNandOutput2a);
            masterNandOutput2a = Nand(masterNandOutput1a, masterNandOutput2b);

            slaveNandOutput1a = Nand(masterNandOutput2a, notClock);
            slaveNandOutput1b = Nand(notClock, masterNandOutput2b);
            slaveNandOutput2b = Nand(slaveNandOutput2a, slaveNandOutput1b);
            slaveNandOutput2a = Nand(slaveNandOutput1a, slaveNandOutput2b);

            putOutput("Q", slaveNandOutput2a);
        }

        // Print all regs since reg 3 is last
        if(CPU.DEBUG_REGS) {
            System.out.printf("REGISTERS: \n\tRegister 0 = x%02X \n\tRegister 1 = x%02X \n\tRegister 2 = x%02X \n\tRegister 3 = x%02X\n"
                    , getChip("U10").getOutput("Q")
                    , getChip("U11").getOutput("Q")
                    , getChip("U12").getOutput("Q")
                    , getOutput("Q"));
        }
    }

}

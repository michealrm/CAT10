package org.cat10.minicpu.chips.operations.registers;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;
import static org.cat10.minicpu.util.CAT10Util.Not;

/**
 * Inputs:
 * getChip("U999").getOutput("clock")
 * getInput("ChipSelect")
 * getInput("D")
 *
 * Outputs
 * getOutput("Q")
 */
public class U10_Register0 extends Chip {

    byte masterNandOutput1a;
    byte masterNandOutput1b;
    byte masterNandOutput2a;
    byte masterNandOutput2b;
    byte slaveNandOutput1a;
    byte slaveNandOutput1b;
    byte slaveNandOutput2a;
    byte slaveNandOutput2b;

    public U10_Register0() {
        super("U10");
        putInput("ChipSelect", (byte) 0);
        putInput("D", (byte) 0);
        putOutput("Q", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        byte D = getInput("D");
        byte clock = getChip("U999").getOutput("clock");
        byte notClock = Not(clock);
        if(getInput("ChipSelect") != 0) {
            masterNandOutput1a = Not((byte) (D & notClock));
            masterNandOutput1b = Not((byte) (notClock & Not(D)));
            masterNandOutput2b = Not((byte) (masterNandOutput2a & masterNandOutput1b));
            masterNandOutput2a = Not((byte) (masterNandOutput1a & masterNandOutput2b));

            slaveNandOutput1a = Not((byte) (masterNandOutput2a & clock));
            slaveNandOutput1b = Not((byte) (clock & Not(masterNandOutput2a)));
            slaveNandOutput2b = Not((byte) (slaveNandOutput2a & slaveNandOutput1b));
            slaveNandOutput2a = Not((byte) (slaveNandOutput1a & slaveNandOutput2b));

            putOutput("Q", slaveNandOutput2a);
        }
        System.out.println("Reg 0");
        System.out.println(getOutput("Q"));
    }
}

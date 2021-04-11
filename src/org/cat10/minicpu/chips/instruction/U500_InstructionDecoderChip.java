package org.cat10.minicpu.chips.instruction;

import org.cat10.minicpu.chips.Chip;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * Inputs:
 * getInput("MEM_1")
 * getInput("MEM_2")
 * getInput("MEM_3")
 * getInput("MEM_4")
 * getChip("U110").getOutput("FLAGS")
 *
 * Outputs
 * getChip("U105").getInput("InstrLen")
 * getChip("U106").getInput("Offset")
 * getOutput("INSTLower")
 * getOutput("INSTUpper")
 *
 * Outputs (Control)
 */
public class U500_InstructionDecoderChip extends Chip {

    boolean isNewInstruction = true;
    boolean isOpcode = false; // MEM_1 contains opcode that has already been read from memory using isNewInstruction
                              // in the previous cycle
    byte opCode = 0;
    boolean onCycle2 = false;
    boolean onReg1 = false;
    boolean onReg2 = false;
    boolean onMem1 = false;
    boolean onMem2 = false;
    boolean onConst2 = false;

    byte regOperand1;
    byte regOperand2;

    public U500_InstructionDecoderChip() {
        super("U500");
    }

    @Override
    public void evaluateOut() {
        putInput("MEM_1", getChip("U221").getOutput("MEM"));

        if(isNewInstruction) {
            isOpcode = true; // Next cycle will be start of instruction with opcode loaded in MEM_1

            putOutput("InstLen", (byte) 0);

            getChip("U115").putInput("sel", (byte) 2);
            getChip("U116").putInput("sel", (byte) 0);
            putOutput("ReadWrite", (byte) 0);
        } else {
            if(isOpcode) {
                isOpcode = false;
                opCode = getInput("MEM_1");

                getChip("U115").putInput("sel", (byte) 2); // Select IPInc since instruction length for fetch is 0
                getChip("U116").putInput("sel", (byte) 0); // Select IP
                putOutput("ReadWrite", (byte) 0); // Read

                putOutput("InstLen", (byte) 1);
            } else {
                if(opCode == (byte) 0x80) {
                    if(!onCycle2) { // We're on cycle 1. We've read opcode and now we're on the registers byte
                        regOperand1 = (byte) ((getInput("MEM_1") & 0xC0) >> 6); // XX00 0000
                        regOperand2 = (byte) ((getInput("MEM_1") & 0x0C) >> 2); // 0000 XX00

                        // Select register operand 2 to be selected in U112 MUX to DATALower bus
                        getChip("U112").putInput("sel", regOperand2);

                        // Don't increment IP because we need another cycle to propagate the value into the register
                        putOutput("InstLen", (byte) 0);
                    } else {
                        getChip("U118A").putInput("sel", (byte) 0); // Select DATA
                        getChip("U114").putInput("SelA", regOperand1); // Select register in `regOperand1` to be destination
                        getChip("U114").putInput("OutputEnableB", (byte) 0);

                        onCycle2 = false;
                        putOutput("InstLen", (byte) 1); // Increment to the next instruction
                        isNewInstruction = true;
                    }
                }
            }
        }

    }
}

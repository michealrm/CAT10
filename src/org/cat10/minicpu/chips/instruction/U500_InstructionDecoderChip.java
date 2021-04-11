package org.cat10.minicpu.chips.instruction;

import org.cat10.minicpu.chips.Chip;
import org.cat10.minicpu.util.CAT10Util;

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

    byte selMemMux = 0;
    boolean startOfExecution = true;
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
        putInput("MEM_1", (byte) 0);
        putInput("MEM_2", (byte) 0);
        putInput("MEM_3", (byte) 0);
        putInput("MEM_4", (byte) 0);
        putOutput("INSTLower", (byte) 0);
        putOutput("INSTUpper", (byte) 0);
        putOutput("InstLen", (byte) 0);
        putOutput("Offset", (byte) 0);

        // Control
        putOutput("ReadWrite", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        putInput("MEM_1", getChip("U221").getOutput("MEM"));

        if(isNewInstruction) {
            isOpcode = true; // Next cycle will be start of instruction with opcode loaded in MEM_1

            // Shift memory registers by instruction length EX: Length=3, shift 000X
            // 2 to 4 DEMUX, sel=InstLen
            for(byte i = getOutput("InstLen"); i < 4; i++) {
                switch(i) { // Shift i - InstLen
                    case 0:
                        putInput("MEM_1", mem_2to4_Demux(CAT10Util.subtractor(i, getOutput("InstLen")).sum));
                    case 1:
                        putInput("MEM_2", mem_2to4_Demux(CAT10Util.subtractor(i, getOutput("InstLen")).sum));
                    case 2:
                        putInput("MEM_3", mem_2to4_Demux(CAT10Util.subtractor(i, getOutput("InstLen")).sum));
                    case 3:
                        putInput("MEM_4", mem_2to4_Demux(CAT10Util.subtractor(i, getOutput("InstLen")).sum));
                }
            }

            // Now we need to read from memory. If we just shifted the memory registers, that should be easy.
            // We would just set selMemMux to 4 - InstLen, demux that, and perform read, decrementing the offset as we go
            // TODO: We need this working through multiple cycles
            if(startOfExecution) {
                selMemMux = 0;
                startOfExecution = false;
            } else {
                selMemMux = CAT10Util.subtractor((byte) 4, getOutput("InstLen")).sum;
            }
            while(selMemMux < 4) {
                // 2 to 4 DEMUX
                switch(selMemMux) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }

                selMemMux = CAT10Util.fullAdderByte((byte) 0, selMemMux, (byte) 1).sum;
            }

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

    private byte mem_2to4_Demux(int sel) {
        switch(sel) {
            case 0:
                return getInput("MEM_1");
            case 1:
                return getInput("MEM_2");
            case 2:
                return getInput("MEM_3");
            case 3:
                return getInput("MEM_4");
        }
        return (byte) 0; // Should never happen
    }
}

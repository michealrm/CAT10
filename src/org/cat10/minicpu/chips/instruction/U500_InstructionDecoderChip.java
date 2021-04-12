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
    boolean latch_MemSetMux = true; // Open
    boolean startOfExecution = true;
    boolean isNewInstruction = true;
    boolean readingMemory = true;
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
        putOutput("InstLen", (byte) 4);
        putOutput("Offset", (byte) 0);

        // Control
        putOutput("ReadWrite", (byte) 0);
    }

    @Override
    public void evaluateOut() {

        if(startOfExecution) {
            isNewInstruction = true;
            putOutput("InstLen", (byte) 4); // So selMemMux is set to 0 to read all MEM registers
        }

        if(isNewInstruction) {
            readingMemory = true;
            selMemMux = CAT10Util.subtractor((byte) 4, getOutput("InstLen")).sum;

            // SHIFT memory registers by instruction length EX: Length=3, shift into XXXX -> X000 where X are the valid bits
            for(byte i = getOutput("InstLen"); i != 4; i = CAT10Util.fullAdderByte((byte) 0, i, (byte) 1).sum) {
                // Shifting using a 2 to 4 DEMUX, sel=InstLen
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

            getChip("U15").putInput("ChipSelect", (byte) 1);

            // Read instruction from memory
            getChip("U115").putInput("sel", (byte) 2);
            getChip("U116").putInput("sel", (byte) 0);
            putOutput("ReadWrite", (byte) 0);
        }

        // Instruction decode
        if(isOpcode) {
            switch (getInput("MEM_1")) {
                case (byte) 0x80:
                    opCode = (byte) 0x80;
                    putOutput("InstLen", (byte) 2); // May not stay, will be changed to 0 if we need more cycles
            }
            isOpcode = false;
        }

        if(readingMemory) {
            // 2 to 4 DEMUX that places memory each cycle
            switch(selMemMux) {
                case 0:
                    putInput("MEM_1", getChip("U221").getOutput("MEM"));
                    break;
                case 1:
                    putInput("MEM_2", getChip("U221").getOutput("MEM"));
                    break;
                case 2:
                    putInput("MEM_3", getChip("U221").getOutput("MEM"));
                    break;
                case 3:
                    putInput("MEM_4", getChip("U221").getOutput("MEM"));
                    break;
            }

            // Increment sel to fill the next memory space
            if(selMemMux != 4) {
                selMemMux = CAT10Util.fullAdderByte((byte) 0, selMemMux, (byte) 1).sum;
                putOutput("InstLen", (byte) 1);
            } else {
                // We've read all memory
                readingMemory = false;
            }
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

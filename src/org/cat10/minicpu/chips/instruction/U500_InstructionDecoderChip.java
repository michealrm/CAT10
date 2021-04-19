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
    boolean readingMemory = true;
    boolean isOpcode = false; // MEM_1 contains opcode that has already been read from memory using isNewInstruction
                              // in the previous cycle
    byte opCode = 0;
    boolean onCycle2 = false;

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
        putOutput("ALUAdderCarryIn", (byte) 0);

        // Control
        putOutput("ReadWrite", (byte) 0);

        putOutput("InstLen", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        try {
            if(getChip("U999").getOutput("clock") == (byte)1) {
                if (startOfExecution) {
                    isNewInstruction = true;
                    startOfExecution = false;
                    putOutput("InstLen", (byte) 4); // So selMemMux is set to 0 to read all MEM registers
                }

                if (isNewInstruction) {
                    // Next cycle we readMemory. This cycle we shift registers
                    readingMemory = true;
                    // Will be set to true again after a instruction ends
                    isNewInstruction = false;
                    // selMemMux for later when readingMemory we want to start at 4-InstLen. Ex: InstLen=2, after shifting
                    // start reading at 4-2=2. You shifted 3 (MEM_4) and 2 into 0 and 1, so you need to start reading at 2.
                    selMemMux = (byte) (4 - getOutput("InstLen"));

                    // SHIFT memory registers by instruction length EX: Length=3, shift into XXXX -> X000 where X are the valid bits
                    for (byte i = 0; (i + getOutput("InstLen")) != 4; i++) {
                        // Shifting using a 2 to 4 DEMUX, sel=i to be set the offset of InstLen
                        //
                        // For example InstLen = 2, i starts at 0
                        // switch i=0, select MEM_1, put MEM_3 into MEM_1
                        // switch i=1, select MEM_2, put MEM_4 into MEM_2
                        // Done!
                        switch (i) { // Shift i <- i + InstLen
                            case 0:
                                putInput("MEM_1", mem_2to4_Demux(i + getOutput("InstLen")));
                                break;
                            case 1:
                                putInput("MEM_2", mem_2to4_Demux(i + getOutput("InstLen")));
                                break;
                            case 2:
                                putInput("MEM_3", mem_2to4_Demux(i + getOutput("InstLen")));
                                break;
                            case 3:
                                putInput("MEM_4", mem_2to4_Demux(i + getOutput("InstLen")));
                        }
                    }

                    // Read instruction from memory
                    // Select IPInc for U115 4-1 MUX into IPnew that feeds into the IP register
                    getChip("U115").putInput("sel", (byte) 2);
                    // Select 16 bit U15 IP register to output on MemAddr
                    getChip("U116").putInput("sel", (byte) 0);
                    // Put read on control line to output enable T Gate in memory
                    putOutput("ReadWrite", (byte) 0);
                    // Set instruction len to 0, we'll set it to one once we're on the reading memory cycle
                    putOutput("InstLen", (byte) 0);
                    return;
                }

                // Instruction decode
                if (isOpcode) {
                    switch (getInput("MEM_1")) {
                        case (byte) 0x80:
                            opCode = (byte) 0x80;
                            putOutput("InstLen", (byte) 0); // Will be set to 2 after we process the instruction

                            // TODO: More instructions here
                    }
                    isOpcode = false;
                }

                if (readingMemory) {
                    // 2 to 4 DEMUX that places memory each cycle
                    switch (selMemMux) {
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
                    if (selMemMux != 3) {
                        selMemMux = CAT10Util.fullAdderByte((byte) 0, selMemMux, (byte) 1).sum;
                        putOutput("InstLen", (byte) 1);
                    } else {
                        selMemMux = 4; // Set to unused value so it won't set any memory
                        // We've read MEM_1-4, now to read opcode
                        isOpcode = true;
                        readingMemory = false;
                    }
                } else {
                    if (opCode == (byte) 0x80) {
                        if (!onCycle2) { // We're on cycle 1. We've read opcode and now we're on the registers byte
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000
                            regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00

                            // Select register operand 2 to be selected in U112 MUX to DATALower bus
                            getChip("U112").putInput("sel", regOperand2);

                            // Next cycle go to cycle 2 below
                            onCycle2 = true;
                        } else {
                            getChip("U118A").putInput("sel", (byte) 0); // Select DATA
                            getChip("U114").putInput("SelA", regOperand1); // Select register in `regOperand1` to be destination
                            getChip("U114").putInput("OutputEnableB", (byte) 0);

                            onCycle2 = false;
                            isNewInstruction = true; // IP is already on next instruction. We'll read memory later to inc IP
                            opCode = 0;
                        }
                    }
                }
            }
        } finally {
            System.out.printf("MEMORY: (MEM_1=x%02X), (MEM_2=x%02X), (MEM_3=x%02X), (MEM_4=x%02X)\n", getInput("MEM_1"), getInput("MEM_2"), getInput("MEM_3"), getInput("MEM_4"));
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

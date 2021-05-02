package org.cat10.minicpu.chips.instruction;

import org.cat10.minicpu.CPU;
import org.cat10.minicpu.chips.Chip;
import org.cat10.minicpu.util.CAT10Util;

import static org.cat10.minicpu.ChipManager.getChip;

/**
 * The instruction decoder chip reads from memory, manages instruction cycles, and sets control lines to
 * propagate data from instructions.
 *
 * Refer to the constructor for inputs and outputs
 */
public class U500_InstructionDecoderChip extends Chip {

    byte selMemMux = 0;
    boolean startOfExecution = true;
    boolean isNewInstruction = true;
    boolean readingMemory = true;
    boolean isOpcode = false; // MEM_1 contains opcode that has already been read from memory using isNewInstruction
                              // in the previous cycle
    byte opCode = 0;

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
        putOutput("OffsetCarryIn", (byte) 0);
        putOutput("ALUAdderCarryIn", (byte) 0);
        putOutput("MemFetchLower", (byte) 0xF0);
        putOutput("MemFetchUpper", (byte) 0);

        // Control
        putOutput("ReadWrite", (byte) 0);

        putOutput("InstLen", (byte) 0);
    }

    @Override
    public void evaluateOut() {
        try {
            if(getChip("U999").getOutput("clock") == (byte)1) {
                if(getChip("U12") != null && getChip("U12").getOutput("Q") == (byte)0xAA)
                    System.out.println();
                if (startOfExecution) {
                    isNewInstruction = true;
                    startOfExecution = false;
                    putOutput("InstLen", (byte) 4); // So selMemMux is set to 0 to read all MEM registers
                }

                // Prepares for memory read by CS=0 for IP since IP is already on next instruction, setting selMemMux
                // (selMemMux is used for a mux that takes in data from MEM bus, and selects between the 4 MEM registers
                // in decode) to 4 - InstLen because after we shift, for example a 2 byte instruction, we would move
                // MEM_3 and 4 into 1 and 2, so we need to start filling memory starting at index 2 = MEM_3 = 4 - InstLen
                //
                // When isNewInstruction is enabled, we expect that InstLen is set to the instruction length of the
                // last instruction executed and MEM_1 contains the beginning of the last instruction executed.
                if (isNewInstruction) {

                    // Don't update IP for new instruction because we're already on the next instruction to read
                    getChip("U15").putInput("ChipSelect", (byte) 0);

                    // Next cycle we readMemory. This cycle we shift registers and prepare for first memory read
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
                    // Select 16 bit MEM FETCH register to output on MemAddr
                    getChip("U116").putInput("sel", (byte) 4);
                    // Put read on control line to output enable T Gate in memory
                    putOutput("ReadWrite", (byte) 0);
                    return;
                }



                if (readingMemory) {
                    // We're using MemFetch register into U116 MUX instead of incrementing IP so we don't have to
                    // backtrack after reading into MEM fetch registers
                    // IP ChipSelect is already disabled from isNewInstruction

                    // 2 to 4 DEMUX that places next valid memory each cycle, which has been put on the data line
                    // using the internal MemFetch register in the getOutput of this chip
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

                    // Increment next valid instruction since we just set memory from MEM bus
                    CAT10Util.AdderOutput memFetchAddUpper = CAT10Util.fullAdderByte((byte) 0, getOutput("MemFetchUpper"), (byte)1);
                    putOutput("MemFetchUpper", memFetchAddUpper.sum);
                    putOutput("MemFetchLower", (byte)(getOutput("MemFetchLower") + memFetchAddUpper.carryOut));

                    // Increment sel to fill the next memory space
                    if (selMemMux != 3) {
                        selMemMux += 1;
                    } else { // selMemMux made the last read into MEM_3
                        // ChipSelect IP to increment using InstLen to move IP to next instruction
                        getChip("U15").putInput("ChipSelect", (byte) 1);

                        selMemMux = 4; // Set to unused value
                        isOpcode = true;
                        readingMemory = false;

                        getChip("U115").putInput("sel", (byte) 2); // Select IPInc for IPNew
                        switch(getInput("MEM_1")) {
                            case (byte)0x80:
                                opCode = (byte)0x80;
                                putOutput("InstLen", (byte) 2); // To increment IP to next instruction
                                break;
                            case (byte)0x81:
                                opCode = (byte)0x81;
                                putOutput("InstLen", (byte) 3); // To increment IP to next instruction
                                break;
                            case (byte)0x82:
                                opCode = (byte)0x82;
                                putOutput("InstLen", (byte) 4);
                                break;
                            case (byte)0x83:
                                opCode = (byte)0x83;
                                putOutput("InstLen", (byte) 4);
                                break;
                            default:
                                opCode = (byte)0;
                                putOutput("InstLen", (byte)1); // To increment IP to next instruction

                                isNewInstruction = true;
                        }
                    }
                }
                // If we're not reading memory we're either classifying opcode or processing instruction
                else {
                    // Do not update IP while processing instruction. IP should stay on next instruction
                    getChip("U15").putInput("ChipSelect", (byte) 0);

                    // First cycle of instruction. Second and later cycles are in the else block.
                    if (isOpcode) {
                        // Setup and first cycle of instructions
                        switch (opCode) {
                            case (byte) 0x80:
                                // mov R8, R8
                                // First cycle
                                regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000
                                regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00

                                // Select register operand 2 to be selected in U112 MUX to DATALower bus
                                getChip("U112").putInput("sel", regOperand2);
                                // Next cycle goes to cycle 2 later in the else block
                                break;

                            case (byte) 0x81:
                                // mov R8, $HH
                                getChip("U118A").putInput("sel", (byte) 2);

                                // First cycle
                                regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000
                                //regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00
                                byte intConstant = getInput("MEM_3");

                                // Select register operand 2 to be selected in U112 MUX to DATALower bus
                                putOutput("INSTLower", intConstant);
                                getChip("U114").putInput("SelA", regOperand1);
                                getChip("U114").putInput("OutputEnableA", (byte) 1);
                                getChip("U114").putInput("OutputEnableB", (byte) 0);
                                isNewInstruction = true; // IP is already on next instruction. We'll read memory later to inc IP
                                opCode = 0;
                                break;
                            case (byte) 0x82:
                                // mov R8, [$MMMM]
                                // 0x82 [register byte] [mem lower] [mem upper]

                                regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000

                                // The idea is we put $MMMM on INST, R/W=R, [$MMMM] is on MEM bus, sel MEM to be put in reg
                                putOutput("INSTLower", getInput("MEM_3"));
                                putOutput("INSTUpper", getInput("MEM_4"));

                                getChip("U116").putInput("sel", (byte) 2);

                                putOutput("ReadWrite", (byte) 0); // Read

                                // Wait till [$MMMM] is read and put on MEM bus by T-Gate then in Cycle 2 we put in reg
                                break;
                            case (byte) 0x83:
                                // mov [$MMMM], R8
                                // 0x82 [register byte] [mem lower] [mem upper]

                                regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00

                                putOutput("INSTLower", getInput("MEM_3"));
                                putOutput("INSTUpper", getInput("MEM_4"));

                                getChip("U112").putInput("sel", regOperand2);
                                break;
                            default:
                                isNewInstruction = true;
                                putOutput("InstLen", (byte)1); // Skip the no-op
                        }
                        isOpcode = false;
                    } else {
                        // Second and further cycles

                        // Second cycle of 0x80 MOV R8,R8
                        if (opCode == (byte) 0x80) {
                            getChip("U118A").putInput("sel", (byte) 0); // Select DATA
                            getChip("U114").putInput("SelA", regOperand1); // Select register in `regOperand1` to be destination
                            getChip("U114").putInput("OutputEnableA", (byte) 1);
                            getChip("U114").putInput("OutputEnableB", (byte) 0);

                            isNewInstruction = true; // IP is already on next instruction. We'll read memory later to inc IP
                            opCode = 0;
                        }
                        // Second cycle of 0x82 MOV R8, [$MMMM]
                        else if(opCode == (byte) 0x82) {
                            getChip("U118A").putInput("sel", (byte) 5);
                            getChip("U114").putInput("SelA", regOperand1);
                            getChip("U114").putInput("OutputEnableA", (byte) 1);
                            getChip("U114").putInput("OutputEnableB", (byte) 0);

                            isNewInstruction = true;
                            opCode = 0;
                        }
                        // Second cycle of 0x83 MOV [$MMMM], R8
                        else if(opCode == (byte) 0x83) {
                            getChip("U116").putInput("sel", (byte) 2);

                            putOutput("ReadWrite", (byte) 1); // Write
                            getChip("U220").putInput("sel", (byte) 0);

                            isNewInstruction = true;
                            opCode = 0;
                        }
                        // Need to handle default case here if isOpcode=false
                        else {
                            isNewInstruction = true;
                            putOutput("InstLen", (byte)1);
                        }
                    }
                }
            }
        } finally {
            if(CPU.DEBUG_MEMFETCH);
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

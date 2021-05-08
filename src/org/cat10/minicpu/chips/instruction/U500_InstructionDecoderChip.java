package org.cat10.minicpu.chips.instruction;

import org.cat10.minicpu.CPU;
import org.cat10.minicpu.chips.Chip;
import org.cat10.minicpu.util.CAT10Util;

import java.util.HashMap;

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
    int cycle = 0;

    byte opCode = 0;

    byte regOperand1;
    byte regOperand2;

    public HashMap<Byte, Byte> instLens;

    public U500_InstructionDecoderChip() {
        super("U500");
        putInput("MEM_1", (byte) 0);
        putInput("MEM_2", (byte) 0);
        putInput("MEM_3", (byte) 0);
        putInput("MEM_4", (byte) 0);
        putOutput("INSTLower", (byte) 0);
        putOutput("INSTUpper", (byte) 0);
        putOutput("InstLen", (byte) 4);
        //putOutput("Offset", (byte) 0);
        putOutput("OffsetLower", (byte) 0);
        putOutput("OffsetUpper", (byte) 0);
        //putOutput("OffsetCarryIn", (byte) 0);
        putOutput("ALUAdderCarryIn", (byte) 0);
        putOutput("MemFetchLower", (byte) 0xF0);
        putOutput("MemFetchUpper", (byte) 0);

        // Control
        putOutput("ReadWrite", (byte) 0);

        putOutput("InstLen", (byte) 0);

        instLens = new HashMap<>();
        instLens.put((byte) 0x10, (byte) 2); //Addc R8,R8
        instLens.put((byte) 0x11, (byte) 3); //Addc R8,$HH
        instLens.put((byte) 0x12, (byte) 4); //Addc R8,[$MMMM]
        instLens.put((byte) 0x13, (byte) 4); //Addc [$MMMM],R8
        instLens.put((byte) 0x20, (byte) 2); //Subb R8,R8
        instLens.put((byte) 0x21, (byte) 3); //Subb R8,$HH
        instLens.put((byte) 0x22, (byte) 4); //Subb R8,[$MMMM]
        instLens.put((byte) 0x23, (byte) 4); //Subb [$MMMM],R8
        instLens.put((byte) 0x30, (byte) 2); //Cmp R8,R8
        instLens.put((byte) 0x31, (byte) 2); //Cmp R8,$HH
        instLens.put((byte) 0x32, (byte) 4); //Cmp R8,[$MMMM]
        instLens.put((byte) 0x33, (byte) 4); //Cmp [$MMMM],R8
        instLens.put((byte) 0x80, (byte) 2);    //Mov R8,R8
        instLens.put((byte) 0x81, (byte) 3);    //Mov R8,$HH
        instLens.put((byte) 0x82, (byte) 4);    //Mov R8,[$MMMM]
        instLens.put((byte) 0x83, (byte) 4);    //Mov [$MMMM],R8
        instLens.put((byte) 0x90, (byte) 2); //Push R8
        instLens.put((byte) 0xA0, (byte) 2); //Pop R8
        instLens.put((byte) 0xB9, (byte) 3); //jmp $MMMM
        instLens.put((byte) 0xD6, (byte) 2); //Jlo (jcs)
    }

    @Override
    public void evaluateOut() {
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
                putOutput("MemFetchLower", (byte)(getOutput("MemFetchLower") + memFetchAddUpper.carryFlag));

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

                    // Set InstLen and opCode
                    opCode = getInput("MEM_1");
                    Byte instLen = instLens.get(opCode);
                    if(instLen == null) { // Instruction not found, treat as nop
                        instLen = 1;
                        isNewInstruction = true;
                    }
                    putOutput("InstLen", instLen);
                    cycle = 0;
                }
            }
            // If we're not reading memory we're either classifying opcode or processing instruction
            else {
                // Do not update IP while processing instruction. IP should stay on next instruction
                getChip("U15").putInput("ChipSelect", (byte) 0);

                switch(opCode) {
                    case (byte) 0x10:
                        // addc R8, R8

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000
                            regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00
                            getChip("U112").putInput("sel", regOperand1);
                            getChip("U113").putInput("sel", regOperand2);
                            getChip("U111").putInput("sel", (byte) 0);
                            putOutput("ALUAdderCarryIn", (byte) 0); //Add = 0 Sub = 1
                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U118A").putInput("sel", (byte) 3); // Select ALU
                            getChip("U114").putInput("SelA", regOperand1); // Select register in `regOperand1` to be destination
                            getChip("U114").putInput("OutputEnableA", (byte) 1);
                            getChip("U114").putInput("OutputEnableB", (byte) 0);
                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x11:
                        // addc R8, $HH

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000
                            //regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00
                            byte intConstant = getInput("MEM_3");
                            putOutput("INSTUpper", getInput("MEM_3"));
                            getChip("U112").putInput("sel", regOperand1);
                            getChip("U113").putInput("sel", (byte) 6);
                            //getChip("U113").putInput("sel", regOperand2);
                            getChip("U111").putInput("sel", (byte) 0);
                            putOutput("ALUAdderCarryIn", (byte) 0); //Add = 0 Sub = 1
                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U118A").putInput("sel", (byte) 3); // Select ALU
                            getChip("U114").putInput("SelA", regOperand1); // Select register in `regOperand1` to be destination
                            getChip("U114").putInput("OutputEnableA", (byte) 1);
                            getChip("U114").putInput("OutputEnableB", (byte) 0);
                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x12:
                        // addc R8,[$MMMM]
                        // 0x12 [register byte] [mem lower] [mem upper]

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000

                            putOutput("INSTLower", getInput("MEM_3"));
                            putOutput("INSTUpper", getInput("MEM_4"));

                            getChip("U116").putInput("sel", (byte) 2);
                            putOutput("ReadWrite", (byte) 0); // Read

                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U112").putInput("sel", regOperand1); // Select regOperand1
                            getChip("U113").putInput("sel", (byte) 4);
                            getChip("U111").putInput("sel", (byte) 0);
                            putOutput("ALUAdderCarryIn", (byte) 0); //Add = 0 Sub = 1

                            cycle++;
                        }else{
                            getChip("U118A").putInput("sel", (byte) 3);

                            getChip("U114").putInput("SelA", regOperand1);
                            getChip("U114").putInput("OutputEnableA", (byte) 1);
                            getChip("U114").putInput("OutputEnableB", (byte) 0);

                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x13:
                        // addc [$MMMM], R8

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00

                            putOutput("INSTLower", getInput("MEM_3"));
                            putOutput("INSTUpper", getInput("MEM_4"));

                            getChip("U116").putInput("sel", (byte) 2);
                            putOutput("ReadWrite", (byte) 0); // Read

                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U112").putInput("sel", (byte) 4); // Select MEM
                            getChip("U113").putInput("sel", regOperand2);
                            getChip("U111").putInput("sel", (byte) 0);
                            putOutput("ALUAdderCarryIn", (byte) 0); //Add = 0 Sub = 1

                            cycle++;
                        } else if(cycle == (byte) 2) {
                            getChip("U116").putInput("sel", (byte) 2);
                            putOutput("ReadWrite", (byte) 1); // Write
                            getChip("U220").putInput("sel", (byte) 2);
                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x20:
                        // subb R8, R8

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000
                            regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00
                            getChip("U112").putInput("sel", regOperand1);
                            getChip("U113").putInput("sel", regOperand2);
                            getChip("U111").putInput("sel", (byte) 0);
                            putOutput("ALUAdderCarryIn", (byte) 1); //Add = 0 Sub = 1
                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U118A").putInput("sel", (byte) 3); // Select ALU
                            getChip("U114").putInput("SelA", regOperand1); // Select register in `regOperand1` to be destination
                            getChip("U114").putInput("OutputEnableA", (byte) 1);
                            getChip("U114").putInput("OutputEnableB", (byte) 0);
                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x21:
                        // subb R8, $HH

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000
                            //regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00
                            byte intConstant = getInput("MEM_3");
                            putOutput("INSTUpper", getInput("MEM_3"));
                            getChip("U112").putInput("sel", regOperand1);
                            getChip("U113").putInput("sel", (byte) 6);
                            //getChip("U113").putInput("sel", regOperand2);
                            getChip("U111").putInput("sel", (byte) 0);
                            putOutput("ALUAdderCarryIn", (byte) 1); //Add = 0 Sub = 1
                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U118A").putInput("sel", (byte) 3); // Select ALU
                            getChip("U114").putInput("SelA", regOperand1); // Select register in `regOperand1` to be destination
                            getChip("U114").putInput("OutputEnableA", (byte) 1);
                            getChip("U114").putInput("OutputEnableB", (byte) 0);
                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x22:
                        // subb R8,[$MMMM]
                        // 0x12 [register byte] [mem lower] [mem upper]

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000

                            putOutput("INSTLower", getInput("MEM_3"));
                            putOutput("INSTUpper", getInput("MEM_4"));

                            getChip("U116").putInput("sel", (byte) 2);
                            putOutput("ReadWrite", (byte) 1); // Read

                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U112").putInput("sel", regOperand1); // Select regOperand1
                            getChip("U113").putInput("sel", (byte) 4);
                            getChip("U111").putInput("sel", (byte) 0);
                            putOutput("ALUAdderCarryIn", (byte) 0); //Add = 0 Sub = 1

                            cycle++;
                        }else{
                            getChip("U118A").putInput("sel", (byte) 3);

                            getChip("U114").putInput("SelA", regOperand1);
                            getChip("U114").putInput("OutputEnableA", (byte) 1);
                            getChip("U114").putInput("OutputEnableB", (byte) 0);

                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x23:
                        // subb [$MMMM], R8

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00

                            putOutput("INSTLower", getInput("MEM_3"));
                            putOutput("INSTUpper", getInput("MEM_4"));

                            getChip("U116").putInput("sel", (byte) 2);
                            putOutput("ReadWrite", (byte) 0); // Read

                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U112").putInput("sel", (byte) 4); // Select MEM
                            getChip("U113").putInput("sel", regOperand2);
                            getChip("U111").putInput("sel", (byte) 0);
                            putOutput("ALUAdderCarryIn", (byte) 1); //Add = 0 Sub = 1

                            cycle++;
                        } else if(cycle == (byte) 2) {
                            getChip("U116").putInput("sel", (byte) 2);
                            putOutput("ReadWrite", (byte) 1); // Write
                            getChip("U220").putInput("sel", (byte) 2);
                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x30:
                        // cmp R8, R8

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000
                            regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00
                            getChip("U112").putInput("sel", regOperand1);
                            getChip("U113").putInput("sel", regOperand2);
                            putOutput("ALUAdderCarryIn", (byte) 1);
                            getChip("U120").putInput("sel", (byte) 0);
                            getChip("U110").putInput("ChipSelect", (byte) 1);
                            isNewInstruction = true; // This instruction is only one cycle
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x31:
                        // cmp R8, $HH

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000
                            regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00
                            putOutput("INSTLower", getInput("MEM_3"));
                            getChip("U112").putInput("sel", regOperand1);
                            getChip("U113").putInput("sel", (byte) 6);
                            getChip("U120").putInput("sel", (byte) 0);
                            getChip("U110").putInput("ChipSelect", (byte) 1);
                            isNewInstruction = true; // This instruction is only one cycle
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x32:
                        // cmp R8, [$MMMM]
                        // 0x32 [register byte] [mem lower] [mem upper]

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000

                            // The idea is we put $MMMM on INST, R/W=R, [$MMMM] is on MEM bus, sel MEM to be put in reg
                            putOutput("INSTLower", getInput("MEM_3"));
                            putOutput("INSTUpper", getInput("MEM_4"));

                            getChip("U116").putInput("sel", (byte) 2);

                            putOutput("ReadWrite", (byte) 0); // Read

                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U112").putInput("sel", regOperand1); // Select regOperand1
                            getChip("U113").putInput("sel", (byte) 4);
                            getChip("U220").putInput("sel", (byte) 0);
                            getChip("U110").putInput("ChipSelect", (byte) 1);
                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x33:
                        // cmp [$MMMM], R8

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            // Memory read
                            regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00

                            putOutput("INSTLower", getInput("MEM_3"));
                            putOutput("INSTUpper", getInput("MEM_4"));
                            getChip("U116").putInput("sel", (byte) 2);
                            putOutput("ReadWrite", (byte) 0);

                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U112").putInput("sel", (byte) 4); // Select regOperand1
                            getChip("U113").putInput("sel", regOperand2);
                            getChip("U220").putInput("sel", (byte) 0);
                            getChip("U110").putInput("ChipSelect", (byte) 1);
                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x40:
                        // not r8

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000

                            getChip("U113").putInput("sel", regOperand1);
                            getChip("U111").putInput("sel", (byte) 4);

                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U118A").putInput("sel", (byte) 3); // Select ALU
                            getChip("U114").putInput("SelA", regOperand1); // Select register in `regOperand1` to be destination
                            getChip("U114").putInput("OutputEnableA", (byte) 1);
                            getChip("U114").putInput("OutputEnableB", (byte) 0);

                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;

                    case (byte) 0x43:
                        // not [$MMMM]
                        // Cycle 1
                        if(cycle == (byte) 0) {
                            putOutput("INSTLower", getInput("MEM_2"));
                            putOutput("INSTUpper", getInput("MEM_3"));

                            getChip("U116").putInput("sel", (byte) 2);

                            putOutput("ReadWrite", (byte) 0);

                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U113").putInput("sel", (byte) 4); // Select MEM
                            getChip("U111").putInput("sel", (byte) 4); // Select NOT on ALU

                            cycle++;
                        }
                        // Cycle 3
                        else if(cycle == (byte) 2) {
                            getChip("U116").putInput("sel", (byte) 2);
                            putOutput("ReadWrite", (byte) 1);
                            getChip("U220").putInput("sel", (byte) 2);

                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x80:
                        // mov R8, R8

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000
                            regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00

                            // Select register operand 2 to be selected in U112 MUX to DATALower bus
                            getChip("U112").putInput("sel", regOperand2);

                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U118A").putInput("sel", (byte) 0); // Select DATA
                            getChip("U114").putInput("SelA", regOperand1); // Select register in `regOperand1` to be destination
                            getChip("U114").putInput("OutputEnableA", (byte) 1);
                            getChip("U114").putInput("OutputEnableB", (byte) 0);

                            isNewInstruction = true; // IP is already on next instruction. We'll read memory later to inc IP
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x81:
                        // mov R8, $HH

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            getChip("U118A").putInput("sel", (byte) 2);

                            // First cycle
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000
                            //regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00
                            byte intConst = getInput("MEM_3");

                            // Select register operand 2 to be selected in U112 MUX to DATALower bus
                            putOutput("INSTLower", intConst);
                            getChip("U114").putInput("SelA", regOperand1);
                            getChip("U114").putInput("OutputEnableA", (byte) 1);
                            getChip("U114").putInput("OutputEnableB", (byte) 0);
                            isNewInstruction = true; // This instruction is only one cycle
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x82:
                        // mov R8, [$MMMM]
                        // 0x82 [register byte] [mem lower] [mem upper]

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000

                            // The idea is we put $MMMM on INST, R/W=R, [$MMMM] is on MEM bus, sel MEM to be put in reg
                            putOutput("INSTLower", getInput("MEM_3"));
                            putOutput("INSTUpper", getInput("MEM_4"));

                            getChip("U116").putInput("sel", (byte) 2);

                            putOutput("ReadWrite", (byte) 0); // Read

                            // Wait till [$MMMM] is read and put on MEM bus by T-Gate then in Cycle 2 we put in reg

                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U118A").putInput("sel", (byte) 5);
                            getChip("U114").putInput("SelA", regOperand1);
                            getChip("U114").putInput("OutputEnableA", (byte) 1);
                            getChip("U114").putInput("OutputEnableB", (byte) 0);

                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x83:
                        // mov [$MMMM], R8
                        // 0x82 [register byte] [mem lower] [mem upper]

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand2 = (byte) ((getInput("MEM_2") & 0x0C) >> 2); // 0000 XX00

                            putOutput("INSTLower", getInput("MEM_3"));
                            putOutput("INSTUpper", getInput("MEM_4"));

                            getChip("U112").putInput("sel", regOperand2);

                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U116").putInput("sel", (byte) 2);

                            putOutput("ReadWrite", (byte) 1); // Write
                            getChip("U220").putInput("sel", (byte) 0);

                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0x90:
                        // push R8
                        // 0x90 [register byte]

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000
                            getChip("U112").putInput("sel", regOperand1);

                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            getChip("U116").putInput("sel", (byte) 1);
                            putOutput("ReadWrite", (byte) 1);
                            getChip("U220").putInput("sel", (byte) 0);

                            getChip("U107").putInput("CarryIn", (byte) 1);
                            getChip("U117").putInput("sel", (byte) 1);
                            getChip("U14").putInput("ChipSelect", (byte) 1);

                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0xA0:
                        // pop r8

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            getChip("U107").putInput("CarryIn", (byte) 0);
                            getChip("U117").putInput("sel", (byte) 1);
                            getChip("U14").putInput("ChipSelect", (byte) 1);

                            cycle++;
                        }
                        // Cycle 2
                        else if(cycle == (byte) 1) {
                            // Need this intermediary cycle instead of putting it into
                            regOperand1 = (byte) ((getInput("MEM_2") & 0xC0) >> 6); // XX00 0000
                            getChip("U116").putInput("sel", (byte) 1);
                            putOutput("ReadWrite", (byte) 0);

                            cycle++;
                        }
                        else if(cycle == (byte) 2) {
                            getChip("U118A").putInput("sel", (byte) 5); // Select MEM
                            getChip("U114").putInput("SelA", regOperand1); // Select register in `regOperand1` to be destination
                            getChip("U114").putInput("OutputEnableA", (byte) 1);
                            getChip("U114").putInput("OutputEnableB", (byte) 0);

                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0xB9:
                        // JMP Absolute

                        // Cycle 1 - this instruction is only 1 cycle
                        if(cycle == (byte) 0) {
                            putOutput("INSTLower", getInput("MEM_2"));
                            putOutput("INSTUpper", getInput("MEM_3"));

                            getChip("U115").putInput("sel", (byte) 1);

                            getChip("U15").putInput("ChipSelect", (byte) 1);

                            putOutput("MemFetchLower", getInput("MEM_2"));
                            putOutput("MemFetchUpper", getInput("MEM_3"));

                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
                    case (byte) 0xD6:
                        //jlo aka Jump If Lower

                        // Cycle 1
                        if(cycle == (byte) 0) {
                            if ((getChip("U120").getOutput("FlagsOut") & 0x8) != 0){
                                //JMP
                                putOutput("OffsetLower", getInput("MEM_2"));
                                putOutput("OffsetUpper", getInput("MEM_3"));
                                getChip("U115").putInput("ChipSelect", (byte) 3);
                                getChip("U15").putInput("ChipSelect", (byte) 1);

                                cycle++;
                            }
                            // Flag not set, go to next instruction
                            else {
                                isNewInstruction = true;
                                opCode = 0;
                            }
                        }
                        // Cycle 2
                        if(cycle == (byte) 1) {
                            // TODO: Not done
                            isNewInstruction = true;
                            opCode = 0;
                        }
                        break;
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

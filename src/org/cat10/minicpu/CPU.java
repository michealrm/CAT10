package org.cat10.minicpu;

import org.cat10.minicpu.chips.U999_Clock;
import org.cat10.minicpu.chips.instruction.*;
import org.cat10.minicpu.chips.memory.*;
import org.cat10.minicpu.chips.operations.*;
import org.cat10.minicpu.chips.operations.registers.U10_Register0;
import org.cat10.minicpu.chips.operations.registers.U11_Register1;
import org.cat10.minicpu.chips.operations.registers.U12_Register2;
import org.cat10.minicpu.chips.operations.registers.U13_Register3;
import org.cat10.minicpu.ChipManager;

/**
 * @see ChipManager for note about static CPU
 */
public class CPU extends ChipManager{

    public static boolean CPU_IS_ON = true;
    public static boolean DEBUG_REGS = false;
    public static boolean DEBUG_MEMFETCH = false;
    public static boolean DEBUG_IP = false;
    public static boolean DEBUG_EXECUTION_DELAY = false;

    /*
     * Set chips
     */
    static {
        // First, "combinational" chips (really sequential since we're not in parallel)

        // MUX TO DATA LINE
        // Register mux put on data line before memory for things like mov [$ABCD], R1
        // We need the register mux's output to be put on the data line before clock ticks
        // to pass into the U116 mux to address bus
        ChipManager.chipMap.put("U112", new U112_8to1_Mux());
        ChipManager.chipMap.put("U113", new U113_8to1_Mux());

        // OPERATIONS CHIPS
        // Get ALU output before we get to memory (which is after clock ticks to process instr decode)
        ChipManager.chipMap.put("U100", new U100_AddSub());
        ChipManager.chipMap.put("U101", new U101_AND());
        ChipManager.chipMap.put("U102", new U102_OR());
        ChipManager.chipMap.put("U103", new U103_XOR());
        ChipManager.chipMap.put("U104", new U104_NOT());
        ChipManager.chipMap.put("U120", new U120_2to1_Mux());
        ChipManager.chipMap.put("U110", new U110_Flags());

        // ALU
        ChipManager.chipMap.put("U111", new U111_8to1_Mux());

        // Stack
        ChipManager.chipMap.put("U107", new U107_AddSub());
        ChipManager.chipMap.put("U117", new U117_2to1_Mux());
        ChipManager.chipMap.put("U14", new U14_StackPointer());

        // (DE)MUXes to registers
        ChipManager.chipMap.put("U118A", new U118A_8to1_Mux());
        ChipManager.chipMap.put("U118B", new U118B_8to1_Mux());
        ChipManager.chipMap.put("U114", new U114_2to4_Demux());

        // Then, clock ticks
        ChipManager.chipMap.put("U999", new U999_Clock());

        // INSTRUCTION CHIPS
        ChipManager.chipMap.put("U15", new U15_InstPointer());
        ChipManager.chipMap.put("U500", new U500_InstructionDecoderChip());
        ChipManager.chipMap.put("U105", new U105_Adder_IPInc());
        ChipManager.chipMap.put("U106", new U106_Adder_IPRel());
        // MUX to set IPNew output for new instr pointer
        ChipManager.chipMap.put("U115", new U115_4to1_Mux());



        // REGISTER CHIPS
        ChipManager.chipMap.put("U10", new U10_Register0());
        ChipManager.chipMap.put("U11", new U11_Register1());
        ChipManager.chipMap.put("U12", new U12_Register2());
        ChipManager.chipMap.put("U13", new U13_Register3());

        // MEMORY CHIPS
        // Instruction/data to memory address MUX
        ChipManager.chipMap.put("U116", new U116_8to1_Mux());

        // Address to chip select
        ChipManager.chipMap.put("U255", new U255_4to16_Decoder());

        // MUX for writing byte from DATA, ALU, or INST to memory
        ChipManager.chipMap.put("U220", new U220_4to1_Mux());

        ChipManager.chipMap.put("U200", new RAM4K("U200", Main.parser.mems[0]));
        ChipManager.chipMap.put("U201", new RAM4K("U201", Main.parser.mems[1]));
        ChipManager.chipMap.put("U202", new RAM4K("U202", Main.parser.mems[2]));
        ChipManager.chipMap.put("U203", new RAM4K("U203", Main.parser.mems[3]));
        ChipManager.chipMap.put("U204", new RAM4K("U204", Main.parser.mems[4]));
        ChipManager.chipMap.put("U205", new RAM4K("U205", Main.parser.mems[5]));
        ChipManager.chipMap.put("U206", new RAM4K("U206", Main.parser.mems[6]));
        ChipManager.chipMap.put("U207", new RAM4K("U207", Main.parser.mems[7]));
        ChipManager.chipMap.put("U208", new RAM4K("U208", Main.parser.mems[8]));
        ChipManager.chipMap.put("U209", new RAM4K("U209", Main.parser.mems[9]));
        ChipManager.chipMap.put("U210", new RAM4K("U210", Main.parser.mems[10]));
        ChipManager.chipMap.put("U211", new RAM4K("U211", Main.parser.mems[11]));
        ChipManager.chipMap.put("U212", new RAM4K("U212", Main.parser.mems[12]));
        ChipManager.chipMap.put("U213", new RAM4K("U213", Main.parser.mems[13]));
        ChipManager.chipMap.put("U215", new U215_4KEPROM(Main.parser.mems[15]));

        // T-Gate
        ChipManager.chipMap.put("U221", new U221_TGate());
    }
    
    private static void printHeader(){
    	
    		
    	
    		System.out.print("-------------------------------------------------------------------------\n");
    		System.out.print("MEM_1\t| MEM_2\t| MEM_3\t| MEM_4\t| REG_0\t| REG_1\t| REG_2\t| REG_3\t| IP\t| SP\t|\n");
    		System.out.printf("x%02X\t| x%02X\t| x%02X\t| x%02X\t| x%02X\t| x%02X\t| x%02X\t| x%02X\t| x%02X%02X\t| x%02X%02X\t|\n", 
    				getChip("U500").getInput("MEM_1"), getChip("U500").getInput("MEM_2"), getChip("U500").getInput("MEM_3"), getChip("U500").getInput("MEM_4"),
    				getChip("U10").getOutput("Q"), getChip("U11").getOutput("Q"), getChip("U12").getOutput("Q"), getChip("U13").getOutput("Q"),
    				getChip("U15").getOutput("IPLower"), getChip("U15").getOutput("IPUpper"),
    				getChip("U14").getOutput("SPLower"), getChip("U14").getOutput("SPUpper"));
    		System.out.print("");
    		
    }

    public static void run() {
    	int count=0;
        while(CPU_IS_ON) {
            ChipManager.updateChips();
            if ((count++ % 20) == 0) {
            	System.out.print("-----------------------------------------------------------------------------------------\n");
        		System.out.print("MEM_1\t| MEM_2\t| MEM_3\t| MEM_4\t| REG_0\t| REG_1\t| REG_2\t| REG_3\t| IP\t| SP\t| FLAGS\t|\n");
            }
            System.out.printf("x%02X\t\t| x%02X\t| x%02X\t| x%02X\t| x%02X\t| x%02X\t| x%02X\t| x%02X\t| x%02X%02X\t| x%04X\t| x%02X\t|\n",
    				getChip("U500").getInput("MEM_1"), getChip("U500").getInput("MEM_2"), getChip("U500").getInput("MEM_3"), getChip("U500").getInput("MEM_4"),
    				getChip("U10").getOutput("Q"), getChip("U11").getOutput("Q"), getChip("U12").getOutput("Q"), getChip("U13").getOutput("Q"),
    				getChip("U15").getOutput("IPLower"), getChip("U15").getOutput("IPUpper"),
    				(short)(getChip("U14").getOutput("SPLower")<<8|getChip("U14").getOutput("SPUpper")),
                    getChip("U110").getOutput("FLAGS"));
            if(DEBUG_EXECUTION_DELAY) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
        }
    }

}

package org.cat10.minicpu;

import org.cat10.minicpu.chips.U999_Clock;
import org.cat10.minicpu.chips.instruction.*;
import org.cat10.minicpu.chips.memory.*;
import org.cat10.minicpu.chips.operations.*;
import org.cat10.minicpu.chips.operations.registers.U10_Register0;
import org.cat10.minicpu.chips.operations.registers.U11_Register1;
import org.cat10.minicpu.chips.operations.registers.U12_Register2;
import org.cat10.minicpu.chips.operations.registers.U13_Register3;

/**
 * @see ChipManager for note about static CPU
 */
public class CPU {

    public static boolean CPU_IS_ON = true;

    /*
     * Set chips
     */
    static {
    	// INSTRUCTION CHIPS
        ChipManager.chipMap.put("U500", new U500_InstructionDecoderChip());
        ChipManager.chipMap.put("U105", new U105_Adder_IPInc());
        ChipManager.chipMap.put("U106", new U106_Adder_IPRel());

        ChipManager.chipMap.put("U115", new U115_4to1_Mux());

        ChipManager.chipMap.put("U15", new U15_InstPointer());

        // Register mux put on data line before memory for things like mov [$ABCD], R1
        // We need the register mux's output to be put on the data line to pass into the U116 mux to address bus
        // REGISTER MUX
        ChipManager.chipMap.put("U112", new U112_8to1_Mux());
        ChipManager.chipMap.put("U113", new U113_8to1_Mux());

        // OPERATIONS CHIPS
        ChipManager.chipMap.put("U100", new U100_AddSub());
        ChipManager.chipMap.put("U101", new U101_AND());
        ChipManager.chipMap.put("U102", new U102_OR());
        ChipManager.chipMap.put("U103", new U103_XOR());
        ChipManager.chipMap.put("U104", new U104_NOT());
        ChipManager.chipMap.put("U107", new U107_AddSub());
        ChipManager.chipMap.put("U110", new U110_Flags());
        
        // MEMORY CHIPS
        ChipManager.chipMap.put("U116", new U116_4to1_Mux());

        ChipManager.chipMap.put("U220", new U220_4to1_Mux());

        ChipManager.chipMap.put("U200", new U200_4KRAM());
        ChipManager.chipMap.put("U201", new U201_4KRAM());
        ChipManager.chipMap.put("U202", new U202_4KRAM());
        ChipManager.chipMap.put("U203", new U203_4KRAM());
        ChipManager.chipMap.put("U204", new U204_4KRAM());
        ChipManager.chipMap.put("U205", new U205_4KRAM());
        ChipManager.chipMap.put("U206", new U206_4KRAM());
        ChipManager.chipMap.put("U207", new U207_4KRAM());
        ChipManager.chipMap.put("U208", new U208_4KRAM());
        ChipManager.chipMap.put("U209", new U209_4KRAM());
        ChipManager.chipMap.put("U210", new U210_4KRAM());
        ChipManager.chipMap.put("U211", new U211_4KRAM());
        ChipManager.chipMap.put("U212", new U212_4KRAM());
        ChipManager.chipMap.put("U213", new U213_4KRAM());
        ChipManager.chipMap.put("U215", new U215_4KEPROM(Main.code));

        ChipManager.chipMap.put("U221", new U221_TGate());

        // OPERATIONS CHIPS
        ChipManager.chipMap.put("U118A", new U118A_8to1_Mux());
        ChipManager.chipMap.put("U118B", new U118B_8to1_Mux());
        ChipManager.chipMap.put("U114", new U114_2to4_Demux());

        // REGISTER CHIPS
        ChipManager.chipMap.put("U10", new U10_Register0());
        ChipManager.chipMap.put("U11", new U11_Register1());
        ChipManager.chipMap.put("U12", new U12_Register2());
        ChipManager.chipMap.put("U13", new U13_Register3());

        ChipManager.chipMap.put("U999", new U999_Clock());
    }

    public static void run() {
        while(CPU_IS_ON) {
            ChipManager.updateChips();
        }
    }

}

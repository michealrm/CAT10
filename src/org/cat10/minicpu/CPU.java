package org.cat10.minicpu;

import org.cat10.minicpu.chips.instruction.U15_InstPointer;
import org.cat10.minicpu.chips.instruction.U500_InstructionDecoderChip;
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
        ChipManager.chipMap.put("U500", new U500_InstructionDecoderChip());
        ChipManager.chipMap.put("U15", new U15_InstPointer());

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

        ChipManager.chipMap.put("U112", new U112_8to1_Mux());
        ChipManager.chipMap.put("U113", new U113_8to1_Mux());

        // And, or, etc to get ALU and SP

        ChipManager.chipMap.put("U118A", new U118A_8to1_Mux());
        ChipManager.chipMap.put("U118B", new U118B_8to1_Mux());
        ChipManager.chipMap.put("U114", new U114_2to4_Demux());

        ChipManager.chipMap.put("U10", new U10_Register0());
        ChipManager.chipMap.put("U11", new U11_Register1());
        ChipManager.chipMap.put("U12", new U12_Register2());
        ChipManager.chipMap.put("U13", new U13_Register3());

    }

    public static void run() {
        while(CPU_IS_ON) {
            ChipManager.updateChips();
        }
    }

}

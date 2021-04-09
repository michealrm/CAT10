package org.cat10.minicpu;

import org.cat10.minicpu.chips.instruction.U15_InstPointer;
import org.cat10.minicpu.chips.instruction.U500_InstructionDecoderChip;
import org.cat10.minicpu.chips.memory.*;

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
    }

    public static void run() {
        while(CPU_IS_ON) {
            ChipManager.updateChips();
        }
    }

}

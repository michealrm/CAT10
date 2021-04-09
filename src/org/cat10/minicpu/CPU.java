package org.cat10.minicpu;

import org.cat10.minicpu.chips.U15_InstPointer;
import org.cat10.minicpu.chips.U500_InstructionDecoderChip;

/**
 * @see ChipManager for note about static CPU
 */
public class CPU {

    public static boolean CPU_IS_ON = true;

    /*
     * Set chips
     */
    static {
        // Combinational chips
        ChipManager.chipMap.put("U500", new U500_InstructionDecoderChip());

        // Clocked Chips
        ChipManager.chipMap.put("U15", new U15_InstPointer());
    }

    public static void run() {
        while(CPU_IS_ON) {
        }
    }

}

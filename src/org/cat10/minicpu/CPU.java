package org.cat10.minicpu;

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
        ChipManager.chipMap.put("U500", new U500_InstructionDecoderChip());
    }

    public static void run() {
        while(CPU_IS_ON) {
        }
    }

}

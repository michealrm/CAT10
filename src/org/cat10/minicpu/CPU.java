package org.cat10.minicpu;

/**
 * @see ChipManager for note about static CPU
 */
public class CPU {

    public static boolean CPU_IS_ON = true;

    public static void run() {
        while(CPU_IS_ON) {
        }
    }

}

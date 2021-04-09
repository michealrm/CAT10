package org.cat10.minicpu;

import org.cat10.minicpu.chips.Chip;

import java.util.HashMap;

/**
 * ChipManager handles execution of clocked and combinational chips. For the order of execution, you should place
 * chips in order of: combinational chips in order of flow, clocked chips. You can think of it like this: all the
 * inputs propagate, then the clock ticks.
 *
 * ChipManager is static, so you can only manage one set of chips during execution, i.e. the CPU cannot run multiple
 * instances in one execution. Maybe some use cases will pop up later, but for this project it's unnecessary. Also,
 * it's nice and a bit more clean to static import ChipManager.getChip and be able to access a chip with getChip("...")
 * within another chip.
 */
public class ChipManager {

    /**
     * Order matters. If you want sequential inputs, place the Chips in order of when they're used.
     * Clocked Chips should be last to ensure their combinational inputs have propagated
     */
    public static HashMap<String, Chip> chipMap = new HashMap<>();


    public static Chip getChip(String chipID) {
        return chipMap.get(chipID);
    }

    /**
     * For each chip: update input and execute output
     */
    public static void updateChips() {
        for(Chip chip : chipMap.values()) {
            chip.evaluateOut();
        }
    }

}

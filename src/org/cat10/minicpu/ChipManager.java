package org.cat10.minicpu;

import org.cat10.minicpu.chips.Chip;

import java.util.HashMap;

/**
 * Handles combinational and clocked chips.
 * Combinational chips should access chipMap in their eval
 */
public class ChipManager {

    /**
     * Order matters because for each chip, we update the input and evaluate the output. So new outputs will only
     * be used in another input if that Chip we're using for output is before the Chip using it for input.
     * Basically, if you want sequential inputs, place the Chips in order of when they're used
     * Clocked Chips should be first
     */
    public HashMap<String, Chip> chipHM = new HashMap<>();


    /**
     * For each chip: update input and execute output
     * Order matters.
     */
    public void updateChips() {
        for(Chip chip : chipHM.values()) {
            chip.updateInput();
            chip.evaluateOut();
        }
    }

}

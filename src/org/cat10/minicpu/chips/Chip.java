package org.cat10.minicpu.chips;

import java.util.HashMap;

/**
 * The basic chip.
 * You can change input wires using the `inputs` HashMap where the key is the name of the wire
 * You can change outputs (inputs to other chips) by using getChip("U105").put("InstrLen", 3);
 */
public abstract class Chip {

    public String chipID = "";
    public HashMap<String, Byte> inputs;

    // May be used for outputs with many wires, so we keep the reference local to the Chip outputting the wires
    // Note that all outputs of a chip are assigned in `localOutputs`. The standard may to update another chip's
    //  input is, for example, getChip("U105").putInput("InstrLen", 3)
    public HashMap<String, Byte> localOutputs;

    public Chip(String chipID) {
        this.chipID = chipID;
    }

    public byte getInput(String key) {
        return inputs.get(key);
    }

    public void putInput(String key, byte value) {
        inputs.put(key, value);
    }

    public byte getOutput(String key) {
        return localOutputs.get(key);
    }

    public void putOutput(String key, byte value) {
        localOutputs.put(key, value);
    }

    public abstract void updateInput();
    public abstract void evaluateOut();

}

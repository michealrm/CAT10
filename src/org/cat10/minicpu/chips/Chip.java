package org.cat10.minicpu.chips;

/**
 * The basic combinational chip.
 * Inputs and outputs should be defined as instance variables
 * `evaluateOut` should evaluate the outputs from the inputs
 */
public abstract class Chip {

    public String chipID = "";

    public Chip(String chipID) {
        this.chipID = chipID;
    }

    public abstract void updateInput();
    public abstract void evaluateOut();

}

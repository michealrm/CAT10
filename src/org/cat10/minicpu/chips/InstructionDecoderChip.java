package org.cat10.minicpu.chips;

public class InstructionDecoderChip extends Chip {

    // Inputs
    byte MEM_1;
    byte MEM_2;
    byte MEM_3;
    byte MEM_4;

    // Output

    public InstructionDecoderChip() {
        super("U500");
    }

    @Override
    public void updateInput() {
        // Read MEMs from memory
    }

    @Override
    public void evaluateOut() {

    }
}

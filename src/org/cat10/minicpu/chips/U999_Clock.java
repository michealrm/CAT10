package org.cat10.minicpu.chips;

public class U999_Clock extends Chip {
    public U999_Clock() {
        super("U999");
        putOutput("clock", (byte)0);
    }

    @Override
    public void evaluateOut() {
        if(getOutput("clock") == 0)
            putOutput("clock", (byte)1);
        else
            putOutput("clock", (byte)0);
    }
}

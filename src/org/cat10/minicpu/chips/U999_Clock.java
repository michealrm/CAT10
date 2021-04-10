package org.cat10.minicpu.chips;

import static org.cat10.minicpu.util.CAT10Util.Not;

public class U999_Clock extends Chip {
    public U999_Clock() {
        super("U999");
        putOutput("clock", (byte)0);
    }

    @Override
    public void evaluateOut() {
        putOutput("clock", Not(getOutput("clock")));
    }
}

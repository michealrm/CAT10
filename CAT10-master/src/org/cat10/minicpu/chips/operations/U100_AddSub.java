package org.cat10.minicpu.chips.operations;

import org.cat10.minicpu.chips.Chip;
/* Add/Sub */
/* Take in A(0-7), B(0-7), C(in) */

/* Output Flags(0-3) to U120, Sum(0-7) to U111 */

public class U100_AddSub extends Chip{
    public U100_AddSub() {
    	super("U100");
    }

	@Override
	public void evaluateOut() {
		// TODO Auto-generated method stub
		
	}
}

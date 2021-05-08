package org.cat10.minicpu.tests;

import org.cat10.minicpu.util.CAT10Util;
import org.cat10.minicpu.util.CAT10Util.*;

public class AdderTest {

    public static void main(String[] args) {
        // Sums
        testAdder(false, 0x52, 0x08, 0x5A, 0x0);
        testAdder(false, 0x9A, 0x07, 0xA1, 0x0);

        // Add and Sub zero
        testAdder(false, 0x52, 0x0, 0x52, 0x0);
        testAdder(true, 0x9A, 0x0, 0x9A, 0x0);


    }

    public static void testAdder(boolean sub, int a, int b, int sum, int carry) {
        testAdder(sub, (byte)a, (byte)b, (byte)sum, (byte)carry);
    }

    public static void testAdder(boolean sub, byte a, byte b, byte sum, byte carry) {
        char op = sub ? '-' : '+';
        System.out.printf("%X %s %X, should have sum=%X, carry=%X\n", a, op, b, sum, carry);
        AdderOutput output = null;
        if(sub)
            output = CAT10Util.fullAdderByte((byte) 1, a, b);
        else
            output = CAT10Util.fullAdderByte((byte) 0, a, b);
        if(output.sum != sum)
            System.out.printf("\tTest FAILED. Sum was %d, not %X\n", output.sum, sum);
        else
            System.out.printf("\tTest passed. Sum was %X\n", output.sum);
        System.out.println();
    }

}

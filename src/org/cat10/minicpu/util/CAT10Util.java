package org.cat10.minicpu.util;

import static org.cat10.minicpu.ChipManager.getChip;

public class CAT10Util {

    public static class AdderOutput {
        public byte sum;
        public byte carryOut;
        public byte flags;

        public AdderOutput() { }
    }

    public static byte NotByte(byte b) {
        return (byte)(b ^ 0xFF);
    }

    public static short NotShort(short b) {
        return (short)(b ^ 0xFFFF);
    }

    public static byte Not(byte b) {
        return b == 0 ? (byte)1 : (byte)0;
        /*
        int lengthInBits = 0;
        byte temp = b;
        if((temp & 0x80) >> 7 == (byte)1 || temp == 0) {
            temp &= 0x7F; // Get rid of sign extension AND if value is already 0, we need to flip to 1. Normally length for
            lengthInBits++; // ... 0 would be 0
        }
        while(temp != 0) {
            temp >>= 1;
            lengthInBits++;
        }

        for(int i = 0; i < lengthInBits; i++) {
            b ^= 1 << i; // Flip the ith bit up to lengthInBits-1
        }

        // If we have 0, flip it to 1 even though length in bits is 0
        if(lengthInBits == 0)
            b = 1;
        return b;
         */
    }

    public static byte Nand(byte a, byte b) {
        return (byte) (NotByte(a) | NotByte(b));
    }

    public static short Nand(short a, short b) {
        return (short) (NotShort(a) | NotShort(b));
    }

    public static AdderOutput subtractor(byte a, byte b) {
        return fullAdderByte((byte)1, a, b);
    }

    public static AdderOutput fullAdderByte(byte carryIn, byte a, byte b) {
        AdderOutput out = new AdderOutput();

        if(carryIn == 1)
            b = (byte)-b;

        short sum = (short)(a + b);
        byte carry = (byte)((sum & 0x100) >> 8);
        sum = (short)(sum & 0xFF);

        out.sum = (byte)sum;
        out.carryOut = carry;

        return out;
    }

    public static AdderOutput fullAdder(byte carryIn, byte a, byte b) {
        AdderOutput out = new AdderOutput();
        byte sum, sumANDCin, carryOut;
        sum = (byte) (a ^ b);
        sumANDCin = (byte) (sum & carryIn);
        sum = (byte) (sum ^ carryIn);
        carryOut = (byte) (sumANDCin | (a & b));

        out.sum = sum;
        out.carryOut = carryOut;
        return out;
    }

    public static byte getSignAndZeroFlag(byte val) {
        byte out = (byte) 0;
        out |= val == 0 ? (byte) 1 : (byte) 0; // Set zero flag 0000 000X
        out |= ((val & 0x80) != 0) ? (byte) 1 << 2 : (byte) 0; // Set sign flag 0000 0X00
        return out;
    }

}

package org.cat10.minicpu.util;

public class CAT10Util {

    public static class AdderOutput {
        public byte sum;
        public byte carryOut;

        public AdderOutput() { }
    }

    public static byte Not(byte b) {
        return b == 0 ? (byte)1 : (byte)0;
    }

    public static AdderOutput subtractor(byte a, byte b) {
        return fullAdderByte((byte)1, a, b);
    }

    public static AdderOutput fullAdderByte(byte carryIn, byte a, byte b) {
        AdderOutput out = new AdderOutput();
        AdderOutput s0 = fullAdder(carryIn, (byte)(a & 0x1), (byte)(b & 0x1 ^ carryIn));
        AdderOutput s1 = fullAdder(carryIn, (byte)(a & 0x2), (byte) (b & 0x2 ^ s0.carryOut));
        AdderOutput s2 = fullAdder(carryIn, (byte)(a & 0x4), (byte) (b & 0x4 ^ s1.carryOut));
        AdderOutput s3 = fullAdder(carryIn, (byte)(a & 0x8), (byte) (b & 0x8 ^ s2.carryOut));
        AdderOutput s4 = fullAdder(carryIn, (byte)(a & 0x10), (byte) (b & 0x10 ^ s3.carryOut));
        AdderOutput s5 = fullAdder(carryIn, (byte)(a & 0x20), (byte) (b & 0x20 ^ s4.carryOut));
        AdderOutput s6 = fullAdder(carryIn, (byte)(a & 0x40), (byte) (b & 0x40 ^ s5.carryOut));
        AdderOutput s7 = fullAdder(carryIn, (byte)(a & 0x80), (byte) (b & 0x80 ^ s6.carryOut));

        out.sum = (byte) (s0.sum | s1.sum << 1 | s2.sum << 2 | s3.sum << 3 |s4.sum << 4 |s5.sum << 5 |s6.sum << 6 |s7.sum << 7);
        out.carryOut = s7.carryOut;

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

}

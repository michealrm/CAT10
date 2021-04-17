package org.cat10.minicpu.util;

public class CAT10Util {

    public static class AdderOutput {
        public byte sum;
        public byte carryOut;
        public byte flags;

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
        AdderOutput s1 = fullAdder(s0.carryOut, (byte)((a & 0x2)>>1), (byte) ((byte) ((b & 0x2)>>1) ^ carryIn));
        AdderOutput s2 = fullAdder(s1.carryOut, (byte)((a & 0x4)>>2), (byte) ((byte) ((b & 0x4)>>2) ^ carryIn));
        AdderOutput s3 = fullAdder(s2.carryOut, (byte)((a & 0x8)>>3), (byte) ((byte) ((b & 0x8)>>3) ^ carryIn));
        AdderOutput s4 = fullAdder(s3.carryOut, (byte)((a & 0x10)>>4), (byte) ((byte) ((b & 0x10)>>4) ^ carryIn));
        AdderOutput s5 = fullAdder(s4.carryOut, (byte)((a & 0x20)>>5), (byte) ((byte) ((b & 0x20)>>5) ^ carryIn));
        AdderOutput s6 = fullAdder(s5.carryOut, (byte)((a & 0x40)>>6), (byte) ((byte) ((b & 0x40)>>6) ^ carryIn));
        AdderOutput s7 = fullAdder(s6.carryOut, (byte)((a & 0x80)>>7), (byte) ((byte) ((b & 0x80)>>7) ^ carryIn));

        out.sum = (byte) (s0.sum | s1.sum << 1 | s2.sum << 2 | s3.sum << 3 |s4.sum << 4 |s5.sum << 5 |s6.sum << 6 |s7.sum << 7);
        out.carryOut = s7.carryOut;

        // TODO: Add flags

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

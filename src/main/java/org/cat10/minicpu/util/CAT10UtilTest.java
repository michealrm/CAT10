package org.cat10.minicpu.util;

import org.junit.jupiter.api.Test;

class CAT10UtilTest {

    @Test
    void fullAdderByte() {
        assert(CAT10Util.fullAdderByte((byte)0, (byte)0x83, (byte)0x17).sum == (byte)0x9A);
    }

    @Test
    void fullAdder() {
        assert(CAT10Util.fullAdder((byte)0, (byte)0, (byte)0).sum == (byte)0);
        assert(CAT10Util.fullAdder((byte)0, (byte)0, (byte)1).sum == (byte)1);
        assert(CAT10Util.fullAdder((byte)0, (byte)1, (byte)0).sum == (byte)1);
        assert(CAT10Util.fullAdder((byte)0, (byte)1, (byte)1).sum == (byte)0);
        assert(CAT10Util.fullAdder((byte)0, (byte)((0x35 & 0x10)>>4), (byte) ((byte) ((0x19 & 0x10)>>4) ^ 0)).sum == 0);
        assert(CAT10Util.fullAdder((byte)0, (byte)((0x35 & 0x10)>>4), (byte) ((byte) ((0x19 & 0x10)>>4) ^ 0)).carryOut == 1);
        assert(CAT10Util.fullAdder((byte)1, (byte)((0x35 & 0x10)>>4), (byte) ((byte) ((0x19 & 0x10)>>4))).sum == 1);
        assert(CAT10Util.fullAdder((byte)1, (byte)((0x35 & 0x10)>>4), (byte) ((byte) ((0x19 & 0x10)>>4))).carryOut == 1);
    }
}
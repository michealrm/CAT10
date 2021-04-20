package org.cat10.minicpu;

import org.cat10.minicpu.assembler.Parser;
import org.cat10.minicpu.assembler.Scanner;
import org.cat10.minicpu.util.CAT10Util;

public class Main {

    public static byte[] code;

    public static void main(String[] args) {
        System.out.println(CAT10Util.Not((byte)1));
        System.out.println(CAT10Util.Not((byte)0));
        code = new byte[0x1000]; // 4K
        Scanner scan = new Scanner(args[0]);

        try {
            Parser parser = new Parser(scan, code);

            parser.parseSourceToBytecode();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Print bytecode!
        for(byte b : code)
            System.out.printf("%x ", b);
        System.out.println();

        CPU.run();
    }

}

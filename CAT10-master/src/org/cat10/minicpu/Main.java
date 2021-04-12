package org.cat10.minicpu;

import org.cat10.minicpu.assembler.Parser;
import org.cat10.minicpu.assembler.Scanner;

public class Main {

    public static byte[] code;

    public static void main(String[] args) {
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

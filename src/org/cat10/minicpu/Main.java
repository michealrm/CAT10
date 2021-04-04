package org.cat10.minicpu;

import org.cat10.minicpu.assembler.Parser;
import org.cat10.minicpu.assembler.Scanner;

public class Main {

    public static void main(String[] args) {
        byte[] mem = new byte[0x1000]; // 4K
        Scanner scan = new Scanner(args[0]);

        try {
            Parser parser = new Parser(scan, mem);

            parser.parseSourceToBytecode();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Print bytecode!
        for(byte b : mem)
            System.out.printf("%x ", b);
    }

}

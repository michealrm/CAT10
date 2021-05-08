package org.cat10.minicpu;

import org.cat10.minicpu.assembler.Parser;
import org.cat10.minicpu.assembler.Scanner;
import org.cat10.minicpu.util.CAT10Util;

public class Main {

    public static Parser parser;

    public static void main(String[] args) {
        Scanner scan = new Scanner(args[0]);

        System.out.println("Filename: " + args[0]);
        try {
            parser = new Parser(scan);

            parser.parseSourceToBytecode();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Print bytecode!
        int chipNum = 200;
        for(int i = 0; i < 16; i++) {
            if(i != 14) {
                System.out.println("U" + chipNum++ + (i == 15 ? " 4K EPROM" : " 4K RAM"));
                for (byte b : parser.mems[i])
                    System.out.printf("%x ", b);
                System.out.println();
            }
        }

        CPU.run();
    }

}

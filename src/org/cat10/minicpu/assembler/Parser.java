package org.cat10.minicpu.assembler;

import org.cat10.minicpu.exception.ParserException;

public class Parser {

    private Scanner scan;
    private byte[] sourceByteCode;  // Will place into RAM
    private int bcIndex;            // Index in sourceByteCode. Placed on a valid byte to write

    // *** Bytes defined in order of: R8:R8, R8:$HH, R8:[MEM], [MEM]R8 ***
    private static final byte[] addcOpcodes = {0x10, 0x11, 0x12, 0x13};
    private static final byte[] subbOpcodes = {0x20, 0x21, 0x22, 0x23};
    private static final byte[] cmpOpcodes = {0x30, 0x31, 0x32, 0x33};

    public Parser(Scanner scan, byte[] codeMemory) {
        this.scan = scan;
        this.sourceByteCode = codeMemory;
    }

    /**
     * Parses the entire source file using Scanner to handle instructions token by token
     * Converts assembly instructions to bytecode and places into sourceByteCode / codeMemory
     * @throws Exception
     */
    public void parseSourceToBytecode() throws Exception {
        while(scan.currentToken.classif != Classif.EOF) {
            // Read statement
            scan.getNext();
            if(scan.currentToken.classif != Classif.MNEMONIC)
                errorWithCurrent("Expected a mnemonic for the start of a statement");

            switch(scan.currentToken.tokenStr) {
                // Data manipulation
                case "mov":
                    MOVInstruction();
                    break;

                // Math
                case "addc":
                case "subb":
                case "cmp":
                    MATHInstructions();
                    break;

                // Logical
                case "not":
                    break;
                case "and":
                    break;
                case "or":
                    break;
                case "xor":
                    break;

                // Stack
                case "push":
                    break;
                case "pop":
                    break;
            }
        }
    }

    private void MOVInstruction() throws Exception {
        scan.getNext(); // Skip past mnemonic

        boolean isOp1Reg8Bits = true;
        boolean isOp2Reg8Bits = true;
        byte op1Reg;
        byte op2Reg;
        byte op1LowerMem;   // Big endian, so most significant is at the lower address
        byte op1UpperMem;
        byte op2LowerMem;
        byte op2UpperMem;
        short op2Const;     // Short to support 1 and 2 byte const operands
        /*
         * FOR FIRST OPERAND = 1 BYTE REGISTER
         * 0x80 1 byte register
         * 0x81 hex byte
         * 0x82 memory
         * 0x84 2 byte displacement
    `    *
         * FOR FIRST OPERAND = 2 BYTE REGISTER
         * 0x89 2 byte hex
         * 0x88 2 byte register
         */
        if(scan.currentToken.tokenStr.startsWith("r")) {
            if(scan.currentToken.tokenStr.length() == 1 || scan.currentToken.tokenStr.length() > 3)
                errorWithCurrent("but register operands must contain either 1 or 2 concatenated registers");

            // 16 bit reg
            if(scan.currentToken.tokenStr.length() == 3)
                isOp1Reg8Bits = false;

            op1Reg = regToByte(scan.currentToken.tokenStr, "1", "mov"); // This will parse either a 8-bit reg or 16-bit reg
            scan.getNext();
            if(!scan.currentToken.tokenStr.equals(","))
                errorWithCurrent("Expected ',' after first operand");
            scan.getNext();

            // Reg-reg 0x80 for 8 bit, 0x88 for 16 bit
            if(scan.currentToken.classif == Classif.REGISTER) {
                op2Reg = regToByte(scan.currentToken.tokenStr, "2", "mov");

                // Second operand is 8 bit reg. First operand must be 8 bits
                if(scan.currentToken.tokenStr.length() == 2) {
                    if(isOp1Reg8Bits) {
                        // *** Write MOV REG8-REG8 to byte code ***
                        writeBytes((byte) 0x80, op1Reg, op2Reg);
                    }
                    else {
                        errorWithCurrent("but first operand was a 8 bit register, so we're expecting an 8 bit operand");
                    }
                }
                // Second operand is 16 bit reg. First operand must be 16 bits
                else if(scan.currentToken.tokenStr.length() == 3) {
                    if(!isOp1Reg8Bits) {
                        // *** Write MOV REG16-REG16 to byte code ***
                        writeBytes((byte) 0x88, op1Reg, op2Reg);
                    }
                    else {
                        errorWithCurrent("but first operand was a 16 bit register, so we're expecting a 16 bit operand");
                    }
                }
                else {
                    errorWithCurrent("but register operands must contain either 1 or 2 concatenated registers");
                }

            }
            // Reg-mem 0x82
            else if(scan.currentToken.tokenStr.equals("[")){
                if(!isOp1Reg8Bits) {
                    errorWithCurrent("but the first register operand must only 8 bits for memory as the second operand");
                }
                scan.getNext();
                if(!scan.currentToken.tokenStr.equals("$"))
                    errorWithCurrent("Expected a memory location starting with '$' in the second operand following a '[' for 8REG as the first operand");
                scan.getNext();
                op2LowerMem = strByteToByte(scan.currentToken.tokenStr.substring(0, 2));
                op2UpperMem = strByteToByte(scan.currentToken.tokenStr.substring(2, 4));

                // Write MOV REG16-MEM to byte code
                writeBytes((byte)0x82, op1Reg, op2LowerMem, op2UpperMem);
            }
            // Reg-const 1 and 2 byte
            else if(scan.currentToken.tokenStr.equals("$")) {
                scan.getNext();
                op2Const = constStrToShort(scan.currentToken.tokenStr);

                // 0x81 1 byte const second operand
                if((op2Const & 0xFF) == op2Const) {
                    // Write MOV REG-CONST8 to byte code
                    writeBytes((byte) 0x81, op1Reg, (byte)op2Const);
                }
                // 0x89 2 byte const second operand
                else {
                    // Write MOV REG-CONST16 to byte code
                    writeBytes((byte) 0x89, op1Reg, (byte)(op2Const >> 4), (byte)(op2Const & 0xFF));
                }
            }
        }
        // Else, we're expecting either 2 byte register displacement or memory as first operand, which both should start with a '['
        /*
         * FOR FIRST OPERAND = MEMORY DISPLACEMENT
         * 0x83 1 byte register
         *
         * FOR FIRST OPERAND = 2 BYTE REGISTER DISPLACEMENT
         * 0x85 1 byte register
         */
        else if(scan.currentToken.tokenStr.equals("[")){
            scan.getNext();

            // Memory
            if(scan.currentToken.tokenStr.equals("$")) {
                scan.getNext();
                op1LowerMem = strByteToByte(scan.currentToken.tokenStr.substring(0, 2));
                op1UpperMem = strByteToByte(scan.currentToken.tokenStr.substring(2, 4));

                scan.getNext();
                if(!scan.currentToken.tokenStr.equals(","))
                    errorWithCurrent("Expected ',' after first operand");
                scan.getNext();

                if(scan.currentToken.classif != Classif.REGISTER)
                    errorWithCurrent("Second operand must be 8 bit register for first operand memory");

                op2Reg = regToByte(scan.currentToken.tokenStr, "2", "mov displacement");

                // *** Write MOV MEMORY-REG8 to byte code ***
                writeBytes((byte) 0x83, op2Reg, op1LowerMem, op1UpperMem);
            }
            // Displacement
            else if(scan.currentToken.tokenStr.startsWith("r")) {
                op1Reg = regToByte(scan.currentToken.tokenStr, "1", "mov displacement");

                scan.getNext();
                if(!scan.currentToken.tokenStr.equals(","))
                    errorWithCurrent("Expected ',' after first operand");
                scan.getNext();

                if(scan.currentToken.classif != Classif.REGISTER)
                    errorWithCurrent("Second operand must be 8 bit register for first operand 16 bit register displacement");

                op2Reg = regToByte(scan.currentToken.tokenStr, "2", "mov displacement");

                // *** Write MOV REG16 DISPLACEMENT-REG8 to byte code ***
                writeBytes((byte) 0x85, op1Reg, op2Reg);
            }
            // Error, operand is not a memory or register displacement
            else {
                errorWithCurrent("but displacement must either start with '$' for a memory or 'R' for a 16 bit register displacement");
            }
        }
        // Unknown instruction for `mov`. Error.
        else {
            error("No known `mov` instructions starting with %s. A `mov` instruction's first operand " +
                    "must be either a 8 bit or 16 bit register, 16 bit register displacement, or memory", scan.currentToken.tokenStr);
        }
    }

    /**
     * Reads instruction for MATH instructions: addc, subb, and cmp
     * @throws Exception
     */
    private void MATHInstructions() throws Exception {
        String mnemonic = scan.currentToken.tokenStr;

        if(!(mnemonic.equals("addc") || mnemonic.equals("subb") || mnemonic.equals("cmp")))
            errorWithCurrent("ASSEMBLER ERROR: Called MATHInstructions on a opcode that was no addc, subb, or cmp");

        scan.getNext(); // Skip past mnemonic

        byte op1Reg;
        byte op2Reg;
        byte op1LowerMem;   // Big endian, so most significant is at the lower address
        byte op1UpperMem;
        byte op2LowerMem;
        byte op2UpperMem;
        short op2Const;     // Short to support 1 and 2 byte const operands

        if(scan.currentToken.tokenStr.startsWith("r")) {
            if(scan.currentToken.tokenStr.length() != 2)
                errorWithCurrent("but register operands for ADDC must be 1 byte");

            op1Reg = regToByte(scan.currentToken.tokenStr, "1", mnemonic); // This will parse either a 8-bit reg or 16-bit reg

            scan.getNext();
            if(!scan.currentToken.tokenStr.equals(","))
                errorWithCurrent("Expected ',' after first operand");
            scan.getNext();

            // 0x10 Reg8-reg8
            if(scan.currentToken.classif == Classif.REGISTER) {
                // Second operand 2 byte reg, which isn't specified as a first operand for ADDC
                if(scan.currentToken.tokenStr.length() != 2)
                    errorWithCurrent("but only 1 byte register operands are supported for instruction %s", mnemonic);

                op2Reg = regToByte(scan.currentToken.tokenStr, "2", mnemonic);

                writeBytes(mathToOpcode(mnemonic, 0), op1Reg, op2Reg);

            }
            // 0x11 Reg8-const8
            else if(scan.currentToken.tokenStr.equals("$")) {
                scan.getNext();
                op2Const = constStrToShort(scan.currentToken.tokenStr);

                // 0x81 1 byte const second operand
                if((op2Const & 0xFF) == op2Const) {
                    // Write MOV REG-CONST8 to byte code
                    writeBytes(mathToOpcode(mnemonic, 1), op1Reg, (byte)op2Const);
                }
                // Reg8-const16 error, type's sizes do not match
                else {
                    errorWithCurrent("which is bigger than 1 byte. For a 1 byte register, you must use a 1 byte constant");
                }
            }
            // 0x12 Reg8-mem16 displacement
            else if(scan.currentToken.tokenStr.equals("[")){
                scan.getNext();
                if(!scan.currentToken.tokenStr.equals("$"))
                    errorWithCurrent("Expected a memory location starting with '$' in the second operand following a '['");
                scan.getNext();
                op2LowerMem = strByteToByte(scan.currentToken.tokenStr.substring(0, 2));
                op2UpperMem = strByteToByte(scan.currentToken.tokenStr.substring(2, 4));

                // Write MOV REG16-MEM to byte code
                writeBytes(mathToOpcode(mnemonic, 2), op1Reg, op2LowerMem, op2UpperMem);
            }
        }
        // Else, we're expecting memory as first operand, which should start with a '['
        else if(scan.currentToken.tokenStr.equals("[")){
            scan.getNext();

            // Memory
            if(scan.currentToken.tokenStr.equals("$")) {
                scan.getNext();
                op1LowerMem = strByteToByte(scan.currentToken.tokenStr.substring(0, 2));
                op1UpperMem = strByteToByte(scan.currentToken.tokenStr.substring(2, 4));

                scan.getNext();
                if(!scan.currentToken.tokenStr.equals(","))
                    errorWithCurrent("Expected ',' after first operand");
                scan.getNext();

                if(scan.currentToken.classif != Classif.REGISTER)
                    errorWithCurrent("Second operand must be 8 bit register for first operand memory");

                op2Reg = regToByte(scan.currentToken.tokenStr, "2", "mov displacement");

                // *** Write MOV MEMORY-REG8 to byte code ***
                writeBytes(mathToOpcode(mnemonic, 3), op2Reg, op1LowerMem, op1UpperMem);
            }
            // Error, operand is not a memory
            else {
                errorWithCurrent("but 2nd operand displacement is not a memory displacement. The only valid displacement (1st operand) for addc is `addc [MEM], R8`");
            }
        }
        // Unknown instruction for `mov`. Error.
        else {
            error("No known `mov` instructions starting with %s. A `mov` instruction's first operand " +
                    "must be either a 8 bit or 16 bit register, 16 bit register displacement, or memory", scan.currentToken.tokenStr);
        }
    }

    // Util

    private void writeBytes(byte... byteArr) {
        for(byte b : byteArr)
            sourceByteCode[bcIndex++] = b;
    }

    private short constStrToShort(String str) throws Exception {
        short num = 0;
        int index = 0;
        for(int i = str.length() - 1; i >= 0; i--) {
            num |= hexDigitToByte(str.charAt(i)) << index * 4;
            index++;
        }
        return num;
    }

    private byte strByteToByte(String str) throws Exception {
        if(str.length() != 2)
            throw new Exception("Cannot convert \"" + str + "\" to a byte. Bytes are 2 hex digits or 8 bits");

        return (byte)(hexDigitToByte(str.charAt(0)) << 4 | hexDigitToByte(str.charAt(1)));
    }

    /**
     * Will only populate lower 4 bits, since a hex digit is only 4 bits.
     * @param c
     * @return
     * @throws Exception
     */
    private byte hexDigitToByte(char c) throws Exception {
        if(Character.isDigit(c))
            return (byte)Integer.parseInt(String.valueOf(c));
        switch(c) {
            case 'a':
            case 'A':
                return 10;
            case 'b':
            case 'B':
                return 11;
            case 'c':
            case 'C':
                return 12;
            case 'd':
            case 'D':
                return 13;
            case 'e':
            case 'E':
                return 14;
            case 'f':
            case 'F':
                return 15;
            default:
                throw new Exception(c + " is not a hex digit");
        }
    }

    private byte regToByte(String tokenStr, String operandNumber, String mnemonic) throws Exception {
        byte regByte;

        if(tokenStr.length() < 2 || tokenStr.length() > 3)
            throw new Exception(tokenStr + " register must be in the form of either R3 or R21");

        if(!Character.isDigit(tokenStr.charAt(1)))
            error("Register 1 in operand %d of the %s instruction was not a number", operandNumber, mnemonic);

        Integer reg1 = Integer.parseInt(tokenStr.substring(1, 2));

        if(reg1 > 3)
            error("Register 1 in operand %d of the %s instruction was not a number", operandNumber, mnemonic);

        regByte = (byte) (reg1 << 4);

        // Only one byte register
        if(tokenStr.length() != 3)
            return regByte;

        // 2 byte concatenated register
        if(!Character.isDigit(tokenStr.charAt(2)))
            error("Register 2 in operand %d of the %s instruction was not a number", operandNumber, mnemonic);

        Integer reg2 = Integer.parseInt(tokenStr.substring(2, 3));

        if(reg2 > 3)
            error("Register 2 in operand %d of the %s instruction was not a number", operandNumber, mnemonic);

        regByte |= reg2;

        return regByte;
    }

    private byte mathToOpcode(String mnemonic, int index) throws Exception {
        switch(mnemonic) {
            case "addc":
                return addcOpcodes[index];
            case "subb":
                return subbOpcodes[index];
            case "cmp":
                return cmpOpcodes[index];
            default:
                throw new Exception("Tried to find " + mnemonic + "'s opcodes, but this is not a defined math operation");
        }
    }

    // Exceptions

    public void error(String fmt) throws Exception {
        throw new ParserException(scan.iSourceLineNr, fmt, scan.sourceFileNm);
    }

    public void error(String fmt, Object... varArgs) throws Exception
    {
        String diagnosticTxt = String.format(fmt, varArgs);
        throw new ParserException(scan.iSourceLineNr
                , diagnosticTxt
                , scan.sourceFileNm);
    }

    /**
     * Error with the current token. Usually "Read X, Expected Y" when we expect a certain token to follow another token
     * @param fmt The error message to be printed
     */
    public void errorWithCurrent(String fmt) throws Exception {
        error("Read \"%s\", " + fmt, scan.currentToken.tokenStr);
    }

    public void errorWithCurrent(String fmt, Object... varArgs) throws Exception {
        error("Read \"%s\", " + fmt, scan.currentToken.tokenStr, varArgs);
    }
}

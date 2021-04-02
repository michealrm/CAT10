package org.cat10.minicpu.assembler;

import org.cat10.minicpu.exception.ParserException;

public class Parser {

    private Scanner scan;
    private byte[] sourceByteCode;  // Will place into RAM
    private int bcIndex;            // Index in sourceByteCode. Placed on a valid byte to write

    public Parser(Scanner scan, byte[] codeMemory) {
        this.scan = scan;
        this.sourceByteCode = codeMemory;
    }

    public void scanSourceFileToRAM() throws Exception {
        while(scan.currentToken.classif != Classif.EOF) {
            // Read statement
            scan.getNext();
            if(scan.currentToken.classif != Classif.MNEMONIC)
                errorWithCurrent("Expected a mnemonic for the start of a statement");

            switch(scan.currentToken.tokenStr) {
                // Data manipulation
                case "mov":
                    scan.getNext();

                    boolean isOp1Reg8Bits = true;
                    boolean isOp2Reg8Bits = true;
                    byte op1Reg;
                    byte op2Reg;
                    byte op1LowerMem;   // Big endian, so most significant is at the lower address
                    byte op1UpperMem;
                    byte op2LowerMem;
                    byte op2UpperMem;
                    short op2Const;     // To support 1 and 2 byte const operands
                    /*
                    FOR FIRST OPERAND REGISTER

                    8 bit Register
                    0x80 8 byte register        DONE
                    0x81 hex byte               DONE
                    0x82 memory                 DONE
                    0x84 16 byte displacement

                    16 bit Register
                    0x89 2 byte hex             DONE
                    0x88 16 byte register       DONE
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
                                error("Read '[', ethe first register operand must only 8 bits for memory as the second operand");
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
                    // Else, we're expecting either 16 byte register displacement or memory as first operand, which both should start with a '['
                    /*
                    FOR FIRST OPERAND MEMORY DISPLACEMENT
                    0x83 8 bit register

                    FOR FIRST OPERAND 16 BIT REGISTER DISPLACEMENT
                    0x85 8 bit register
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
                            writeBytes((byte) 0x83, op1LowerMem, op1UpperMem, op2Reg); // TODO: Is this right? Do we write the memory address first?
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
                    break;

                // Math
                case "addc":
                    break;
                case "subb":
                    break;
                case "cmp":
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
            return Integer.parseInt(String.valueOf(c))
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

package org.cat10.minicpu.assembler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Scanner {

    private class InvalidHexConstException extends Exception {
        public InvalidHexConstException(String intConst) {
            super(String.format("Line %d, Col %d: Invalid integer const %s", iSourceLineNr, iColPos, intConst));
        }
    }

    private class InvalidRegisterException extends Exception {
        public InvalidRegisterException(String register) {
            super(String.format("Line %d, Col %s: You entered \"%s\", but registers may only contain numbers. To avoid an exception you" +
                    "may encounter later: registers only go from 0 to 3.", iSourceLineNr, iColPos, register));
        }
    }

    public String sourceFileNm;
    public ArrayList<String> sourceLineM;
    public int iSourceLineNr;
    public int iColPos;
    public int iSavedSourceLineNr;
    public int iSavedColPos;
    public int iNextSourceLineNr;
    public int iNextColPos;

    public Token currentToken;

    public Scanner(String sourceFileNm) {
        this.sourceFileNm = sourceFileNm;
        sourceLineM = new ArrayList<>();

        iSourceLineNr = 0;
        iColPos = 0;

        try {
            readSourceLineM();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }

        currentToken = new Token();
    }

    /**
     * Opens a file using sourceFileNm and reads source lines using [] into sourceLineM
     */
    public void readSourceLineM() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(sourceFileNm));
        String line;
        while((line = br.readLine()) != null) {
            sourceLineM.add(line);
        }
    }

    public Token getNext() throws Exception {
        Token t = new Token();

        // We need to save the values of `iSourceLineNr` and `iColPos` because when we read a token we our index
        // variables advance past the token, BUT we need `iSourceLineNr` and `iColPos` to be at the start of a token
        // for meaningful error messages. Showing the `iSourceLineNr` and `iColPos` after the token can be confusing.
        //
        // We'll save the advanced indexes in `iNextSourceLineNr` and `iNextColPos` and we'll start at those indexes
        // when scanning the next token.
        iSourceLineNr = iNextSourceLineNr;
        iColPos = iNextColPos;
        int iLineNumber = iNextSourceLineNr;
        int iColNumber = iNextColPos;

        if(iLineNumber >= sourceLineM.size()) {
            t.classif = Classif.EOF;
            currentToken = t;
            return t;
        }

        int sourceLineBefore;
        char[] textCharM = sourceLineM.get(iLineNumber).toCharArray();

        do {
            int[] nextPos = skipEmptyLine(iLineNumber, iColNumber);
            sourceLineBefore = iLineNumber;
            iLineNumber = nextPos[0];
            iColNumber = nextPos[1];

            if (sourceLineBefore != iLineNumber) {
                textCharM = sourceLineM.get(iLineNumber).toCharArray();
            }

            t.tokenSB.append(Character.toUpperCase(textCharM[iColNumber]));
            setClassification(t);

            // Calculate next position
            nextPos = nextPos(iLineNumber, iColNumber); // We don't want to skip whitespace because that delimits continuesToken
            sourceLineBefore = iLineNumber;
            iLineNumber = nextPos[0];   // nextPos[2] has first element as line number, second element as column number
            iColNumber = nextPos[1];

            if (sourceLineBefore != iLineNumber) {
                if (iLineNumber >= sourceLineM.size()) {
                    //t.primClassif = Classif.EOF;
                    break;
                } else {
                    textCharM = sourceLineM.get(iLineNumber).toCharArray();

                }
            }
        } while(sourceLineBefore == iLineNumber && continuesToken(t, Character.toUpperCase(textCharM[iColNumber])));

        checkForExceptions(t);

        t.tokenStr = t.tokenSB.toString();
        currentToken = t;

        iNextSourceLineNr = iLineNumber;
        iNextColPos = iColNumber;

        // Skip whitespace
        while(isWhitespace(t))
            t = getNext();

        return t;
    }

    private boolean continuesToken(Token token, char c) throws Exception {
        Token copy = new Token(token.tokenSB.toString() + c); // Kinda defeats the purpose of a SB, at least here
        setClassification(copy);
        if(copy.classif != Classif.EMPTY && copy.classif != token.classif) {
            token.classif = copy.classif;
        }

        switch(token.classif) {
            case EMPTY:
                return true;
            case EOF:
            default:
                return false;
            case REGISTER:
            case INTCONST:
                // Keep reading REGISTER ('R') and INTCONST ('$') until we hit a separator
                // We'll handle exceptions like R5a or $12PV in getNext()
                return !isSeparator(c);
            case SEPARATOR:
                return isWhitespace(token) && isWhitespace(c);
            case MNEMONIC:
                return startsWithMnemonic(token.tokenSB.toString() + c);
            case IDENTIFIER:
                return !isWhitespace(c) && !isSeparator(c);
        }
    }

    /**
     * Sets the classification and subclassification of the token
     * Token may not be fully scaaned in, so these cases try to set classification for partially scanned tokens. This
     * also means we use String Buffer instead of tokenStr in token, since we copy tokenSB to tokenStr at the END
     * of getNext()
     * @param token Token that will be classified
     */
    public void setClassification(Token token) throws Exception {
        if(token.tokenSB.length() == 0) {
            token.classif = Classif.EMPTY;
        }  else if(isWhitespace(token)) {
            token.classif = Classif.SEPARATOR;
        } else if(isSeparator(token)) {
            token.classif = Classif.SEPARATOR;
        } else if(isIntConst(token)) {
            token.classif = Classif.INTCONST;
        } else if(isRegister(token)) {
            token.classif = Classif.REGISTER;
        } else if(startsWithMnemonic(token.tokenSB.toString())) {
            token.classif = Classif.MNEMONIC;
        } else {
            token.classif = Classif.IDENTIFIER;
        }
    }

    private void checkForExceptions(Token t) throws Exception {
        // Register exceptions
        if(t.classif == Classif.REGISTER) {
            // We're only checking if the register name has all numbers (excluding the 'R'), because the Scanner
            // doesn't know how many registers are available, that's the Parser's job.
            boolean allNumbers = true;
            for(int i = 1; i < t.tokenSB.length(); i++)
                if(!Character.isDigit(t.tokenSB.charAt(i)))
                    allNumbers = false;

            if(!allNumbers)
                throw new InvalidRegisterException(t.tokenSB.toString());

        }

        // Integer exceptions
        if(t.classif == Classif.INTCONST) {
            // Check if all characters are hex. If not throw an exception
            boolean allHex = true;
            for(int i = 1; i < t.tokenSB.length(); i++) {
                char c = t.tokenSB.charAt(i);
                if (!(Character.isDigit(c) ||
                        c == 'A' ||
                        c == 'B' ||
                        c == 'C' ||
                        c == 'D' ||
                        c == 'E' ||
                        c == 'F')) {
                    allHex = false;
                }
            }
            if(!allHex)
                throw new InvalidHexConstException(t.tokenSB.toString());
        }
    }

    /**
     * Returns the next character, skipping blank lines if necessary.
     * This does not skip whitespace
     *
     * @param iLineNumber
     * @param iColNumber
     * @return
     */
    public int[] nextPos(int iLineNumber, int iColNumber) {
        if(iLineNumber >= sourceLineM.size())
            return packagePositions(iLineNumber, iColNumber);

        int[] pos = skipEmptyLine(iLineNumber, iColNumber);
        iLineNumber = pos[0];
        iColNumber = pos[1];

        // Increment
        int[] ret;
        if(iColNumber + 1 >= sourceLineM.get(iLineNumber).length()) {
            iLineNumber++;
            iColNumber = 0;
        } else {
            iColNumber++;
        }

        ret = packagePositions(iLineNumber, iColNumber);    // The start and end position of the currently built token
        return ret;
    }

    private int[] skipEmptyLine(int r, int c) {
        if(r < sourceLineM.size() && sourceLineM.get(r).length() == 0) {
            r++;
            c = 0;
        }
        return packagePositions(r, c);
    }

    private int[] packagePositions(int r, int c) {
        int[] ret = new int[2];
        ret[0] = r;
        ret[1] = c;
        return ret;
    }

    private boolean startsWithMnemonic(String str) {
        return containsIn(str, "MOV", "ADDC", "SUBB", "CMP", "NOT", "AND", "OR", "XOR", "PUSH", "POP", "JMP",
                                    "JLO", "JHS", "JEQ", "JNE", "JMI", "JPL", "NOP");
    }

    private boolean isWhitespace(Token token) {
        return token.tokenSB.charAt(0) == ' ';
    }

    private boolean isWhitespace(char c) {
        return c == ' ';
    }

    private boolean isSeparator(Token token) {
        char c = token.tokenSB.charAt(0);
        return c == ',' || c == '[' || c == ']' || c == '*' || c == '=' || c == ':';
    }

    private boolean isSeparator(char c) {
        return c == ',' || c == '[' || c == ']' || c == '*' || c == '=' || c == ':';
    }

    private boolean isIntConst(Token token) {
        return token.tokenSB.charAt(0) == '$';
    }

    private boolean isRegister(Token token) {
        return token.tokenSB.charAt(0) == 'R';
    }

    public boolean containsIn(String match, String... in) {
        for(String s : in)
            if(s.startsWith(match))
                return true;
        return false;
    }

    public void saveLocation() {
        iSavedSourceLineNr = iSourceLineNr;
        iSavedColPos = iColPos;
    }

    public void restoreLocation() throws Exception {
        iSourceLineNr = iSavedSourceLineNr;
        iColPos = iSavedColPos;
        getNext();
    }

}

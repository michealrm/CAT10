package org.cat10.minicpu.assembler;

public class Token {
    public String tokenStr = "";
    // String Buffer used for concatenating token strings in the Scanner. For this to be effective, tokenSB.toString()
    // should be copied to tokenStr at the end of token concatenation
    public StringBuffer tokenSB = new StringBuffer();
    public Classif classif = Classif.EMPTY;
    public int iSourceLineNr;
    public int iColPos;

    public Token(int iSourceLineNr, int iColPos) {
        this.iSourceLineNr = iSourceLineNr;
        this.iColPos = iColPos;
    }
    public Token(String value)
    {
        this.tokenStr = value;
    }
    public Token()
    {
        this("");   // invoke the other constructor
    }

}

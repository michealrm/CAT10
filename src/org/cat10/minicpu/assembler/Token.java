package org.cat10.minicpu.assembler;

public class Token {
    public String tokenStr = "";
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

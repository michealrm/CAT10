package org.cat10.minicpu.assembler;

public class ParserException extends Exception
{
    public int iLineNr;
    public int iColNr;
    public String diagnostic;
    public String sourceFileName;
    // constructor
    public ParserException(int iLineNr, int iColNr, String diagnostic, String sourceFileName)
    {
        this.iLineNr = iLineNr + 1;
        this.iColNr = iColNr;
        this.diagnostic = diagnostic;
        this.sourceFileName = sourceFileName;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Line ");
        sb.append(iLineNr);
        sb.append(" ");
        sb.append("Col ");
        sb.append(iColNr);
        sb.append(" ");
        sb.append(diagnostic);
        sb.append(", File: ");
        sb.append(sourceFileName);
        return sb.toString();
    }
}


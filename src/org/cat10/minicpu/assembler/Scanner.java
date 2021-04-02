package org.cat10.minicpu.assembler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Scanner {

    public String sourceFileNm;
    private ArrayList<String> sourceLineM;
    public int iSourceLineNr;
    public int iColPos;
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

    public Token getNext() {
        return null;
    }

}

package com.bradleyeaton.barebones;

import java.util.regex.Pattern;

public class WhileLoop {
    public  WhileLoop(int startLineNo){
        this.startLineNo = startLineNo;
    }

    public int startLineNo;

    public int endLineNo = -1;

    public boolean goToEnd(){
        return finished && endLineNo == -1;
    };

    public boolean finished = false;

    private static Pattern validOperandPattern = Pattern.compile("\\w+ (not )?[0-9]+");

    public static Pattern getValidOperandPattern(){
        return validOperandPattern;
    }

    public static boolean isValidOperand(String operand){
        return validOperandPattern.matcher(operand).matches();
    }
}

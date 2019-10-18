package com.bradleyeaton.barebones;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Compiler {

    public static void main(String[] args) {

        System.out.println("SHould be false: " + WhileLoop.isValidOperand("bnsanc") + "Should be true: " + WhileLoop.isValidOperand("X not 0"));

	    // start object oriented
        String fileLocation = null;
        if(args.length > 0){
            fileLocation = args[0];
        }
        Compiler c = new Compiler();
        c.start(fileLocation);
    }

    /**
     * Starts the object oriented section
     */
    public void start(String fileLocation){
        File sourceCode = new File(fileLocation);
        if(!sourceCode.exists() || sourceCode.isDirectory()){
            //Not a file we can read
            return;
        }

        //Otherwise try to read the file
        FileReader fileReader = null;
        LineNumberReader sourceReader = null;
        try {
             fileReader = new FileReader(fileLocation);
             sourceReader = new LineNumberReader(fileReader);
             String currentLine = null;

             while((currentLine = sourceReader.readLine()) != null){
                 //Get all lines of code and store them in memory
                 System.out.println("Current line of code:" +currentLine + "\n" +
                         "Variables");
                 Object[] variableKeys = variables.keySet().toArray();
                 for (int i = 0; i < variables.size(); i++){
                     String variable = variableKeys[i].toString();
                     Integer value = variables.get(variable);
                     System.out.println(variable + ":" + value);
                 }
                 int nextLineNo = decode(currentLine);
                 if(nextLineNo > -1){
                     //The program wants us to skip to a line

                 }
                 lineNo++;
             }
             System.out.println("Completed");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fileReader != null){
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    int lineNo = 0;

    Map<String, Integer> variables = new HashMap<String, Integer>();

    Stack<WhileLoop> whileLoops = new Stack<WhileLoop>();

    /**
     * Determines which instruction has been given and where its operand should be
     * @param instruction The instruction to decode
     * @return returns either the value of the line to continue to or -1 if to continue as normal
     */
    public int decode(String instruction) throws Exception {
        instruction = instruction.trim();
        if(whileLoops.size() > 0 && whileLoops.peek().goToEnd() && instruction != "end;"){
           //Check first if a whileloop has ended and we dont know the end just skip to end
            return -1;
        }
        else if(instruction.startsWith("clear")){
            String operand = instruction.replaceAll("clear\\ |;","");
            if(checkVariableExists(operand)) {
                variables.replace(operand, 0);
            }
            else{
                variables.put(operand,0);
            }
        }
        else if(instruction.startsWith("incr")){
            String operand = instruction.replaceAll("incr\\ |;", "");
            if(checkVariableExists(operand)) {
                Integer newValue = variables.get(operand) + 1;
                variables.replace(operand, newValue);
            }
            else{
                Integer newValue = 1;
                variables.put(operand, newValue);
            }
        }
        else if(instruction.startsWith("decr")){
            String operand = instruction.replaceAll("decr\\ |;", "");
            if(checkVariableExists(operand)) {
                Integer newValue = variables.get(operand) - 1;
                if(newValue < 0)
                    newValue = 0;
                variables.replace(operand, newValue);
            }
            else{
                Integer newValue = 0;
                variables.put(operand, newValue);
            }
        }
        else if(instruction.startsWith("while")){
            //Check to see if it is a new while or old
            if(whileLoops.size() > 0 && whileLoops.peek().startLineNo != lineNo || whileLoops.size() == 0){
                //This is a new while loop
                whileLoops.push(new WhileLoop(lineNo));
            }


            String operand = instruction.replaceAll("(while )|( do;)", "");

            if(!WhileLoop.isValidOperand(operand)){
                throw new Exception("Operand is invalid for while loops: " + operand);
            }

            boolean equalsValue = !operand.contains("not");
            String variableName = operand.replaceAll(" (not )?[0-9]+",""); //replace the not and number part with nothing to get the variable name
            Integer operandValue = Integer.parseInt(operand.replaceAll("\\w+ ",""));

            if(!checkVariableExists(operand.substring(0,1))) { //If variable doesn't exist return since default int is 0 and 0 would mean that loop wont continue
                return -1;
            }



            //Otherwise check value of variable
            Integer operandVariable = variables.get(variableName);
            if((operandVariable == operandValue && !equalsValue)||(operandVariable != operandValue && equalsValue)){
                //if the continue condition for the while loop is false, end the while loop
                //if we know the end no skip to it otherwise loop until we find it
                if(whileLoops.peek().endLineNo != -1) {
                    lineNo = whileLoops.peek().endLineNo;
                }

                whileLoops.peek().finished = true;
            }

        }
        else if(instruction.contains("end;")){
            if(whileLoops.peek().finished) {
                whileLoops.pop();
            }
            else{
                //Otherwise go back to previous line
                lineNo = whileLoops.peek().startLineNo;
                return lineNo;
            }
        }
        else{
            throw new Exception("No opcode found for: " + instruction);
        }
        return -1;
    }

    public boolean checkVariableExists(String variableName){
        return variables.containsKey(variableName);
    }

    // int variableName = { get; set; }
}
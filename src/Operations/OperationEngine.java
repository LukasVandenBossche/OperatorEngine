package Operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

/**
 * @author lukas on 19/12/2019
 * @project OperatorEngine
 */

public class OperationEngine {
    private Operation operation;
    private String operandsRegex;
    private String doubleRegex;
    private String opString;

    public OperationEngine(String input, boolean print) {
        BinaryOperation.Operand[] operands = BinaryOperation.Operand.values();
        StringBuilder temp = new StringBuilder("([");
        // create operandsRegex
        for (BinaryOperation.Operand operand : operands) {
            if (operand.getOp() == '-' || operand.getOp() == '^') {
                temp.append("\\").append(operand.getOp());
            } else {
                temp.append(operand.getOp());
            }
        }
        this.operandsRegex = temp.append("])").toString();
        this.doubleRegex = "([0-9]+(\\.[0-9]*)?)";
        this.opString = input;
        try {            
            if (validateOperationString()) {
                if (print) System.out.println(opString);
                this.operation = createOperation(new ArrayList<>(Arrays.asList(opString.split(" "))), new ArrayList<>());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * validates the input string of the constructor:
     *
     * @return true if 'input' is a valid operation string
     */
    private boolean validateOperationString() {
        // - check if string is empty
        if (opString.length() == 0) throw new InputMismatchException("\nInput \"" + opString + "\" is an empty String.");
        // - replace all ',' separators with a '.' separator so Double.parseDouble() detects the correct separator
        opString = opString.replaceAll(",", ".");
        // - check for invalid characters
        if (!opString.matches("( |\\(|\\)|" + doubleRegex + "|" + operandsRegex + ")+")) {
            throw new InputMismatchException("\nInput \"" + opString + "\" contains invalid characters.\n" +
                    "It can only contain numbers, " + operandsRegex + ", parentheses or spaces.");
        }
        // - add spaces where needed and remove unnecessary spaces
        opString = opString.replaceAll("(" + doubleRegex + "|" + operandsRegex + ")", " $0 ");
        opString = opString.replaceAll("\\s{2,}", " ");
        opString = opString.replaceAll("(\\A[ ]+)|([ ]+\\z)", "");
        // - check if operations without the parentheses are correct
        String noParentheses = this.opString.replaceAll("(\\( )|( \\))", "");
        if (!noParentheses.matches(doubleRegex + "( " + operandsRegex + " " + doubleRegex + ")*")) {
            throw new InputMismatchException("\nInput \"" + opString + "\" without parentheses isn't a valid operation. \n" +
                    "A valid operation should start with a number, possibly followed by multiple pairs of an operand and a number.");
        }
        // - check if parentheses are placed correctly (for every opening '(' a closing ')')
        return true;
    }

    /**
     * create the Operation object based on the input string
     * order of operation is based on the Operand class
     *
     * @param input      inputString
     * @param operations List to keep track of already created operations
     * @return last element of the operations List
     */
    private Operation createOperation(List<String> input, List<Operation> operations) {
        if (input.size() == 0) throw new NullPointerException("Input is an empty array.");
        if (input.size() == 1 && input.get(0).matches("[0-9]+(.[0-9]*)?")) {
            return new Operation(Double.parseDouble(input.get(0)));
        } else if (input.size() == 3) {
            Operation op1;
            Operation op2;
            if (input.get(0).matches("[A-Z]")) {
                op1 = operations.get(input.get(0).charAt(0) - 'A');
            } else {
                op1 = new Operation(Double.parseDouble(input.get(0)));
            }
            if (input.get(2).matches("[A-Z]")) {
                op2 = operations.get(input.get(2).charAt(0) - 'A');
            } else {
                op2 = new Operation(Double.parseDouble(input.get(2)));
            }
            return new BinaryOperation(op1, input.get(1).charAt(0), op2);
        } else {
            // iterate following the order of operations
            // parentheses first
            while (input.contains("(")) {
                int lastOpening = input.lastIndexOf("(");
                int firstClosing = lastOpening + input.subList(lastOpening, input.size()).indexOf(")");
                operations.add(createOperation(input.subList(lastOpening + 1, firstClosing), operations));
                if (firstClosing >= lastOpening) {
                    input.subList(lastOpening, firstClosing + 1).clear();
                }
                char operationIndex = (char) ('A' + operations.size() - 1);
                input.add(lastOpening, String.valueOf(operationIndex));
            }
            // now the operands defined in BinaryOperation's inner class Operand
            for (int i = 1; i < 4; i++) {
                // iterate over input List
                int j = 0;
                while (j < input.size()) {
                    if (input.get(j).matches(operandsRegex.toString()) && BinaryOperation.Operand.matchesOperand(i, input.get(j))) {
                        operations.add(createOperation(input.subList(j - 1, j + 2), operations));
                        input.subList(j - 1, j + 2).clear();
                        char operationIndex = (char) ('A' + operations.size() - 1);
                        input.add(j - 1, String.valueOf(operationIndex));
                    } else {
                        j++;
                    }
                }
            }
        }
        if (operations.size() != 0) {
            return operations.get(operations.size() - 1);

        }
        throw new UnsupportedOperationException("Couldn't create any valid Operation objects.");
    }

    /**
     * calculates the total from operation
     *
     * @return the result
     */
    public double calculate() {
        return operation.calculate();
    }

    public static void main(String[] args) {
        String[] inputs = new String[]{
                " (100.0-50)/(5^2)", "50 + 10 + 20 - 30", "7 / 7 + 8 / 8", "4 + 50 * 50 / 10", "500", "8 ^ 2"
        };
        for (String input : inputs) {
            try {
                System.out.println(" = " + new OperationEngine(input, true).calculate() + "\n");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

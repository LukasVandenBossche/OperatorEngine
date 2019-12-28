package Operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author lukas on 19/12/2019
 * @project OperatorEngine
 */

public class OperationEngine {
    private Operation operation;
    private StringBuilder operandsRegex;

    public OperationEngine(String input, boolean print) {
        BinaryOperation.Operand[] operands = BinaryOperation.Operand.values();
        this.operandsRegex = new StringBuilder("[");
        for (BinaryOperation.Operand operand : operands) {
            if (operand.getOp() == '-' || operand.getOp() == '^') {
                operandsRegex.append("\\").append(operand.getOp());
            } else {
                operandsRegex.append(operand.getOp());
            }
        }
        operandsRegex.append("]");
        String opRegex = "[0-9]+(.[0-9]*)?( " + operandsRegex + " [0-9]+(.[0-9]*)?)*";
        List<Operation> temp = new ArrayList<>();
        this.operation = createOperation(new ArrayList<>(Arrays.asList(input.split(" "))), temp);
//        if (input.matches(opRegex)) {
//            if (print) System.out.println(input);
//            List<String> opString = addParentheses(input);
//            if (print) System.out.println(" = " + opString.toString().replaceAll("[,\\[\\]]", ""));
//            this.operation = createOp(opString);
//        } else {
//            throw new InputMismatchException("\nInput \"" + input + "\" contains invalid characters.\n" +
//                    "It can only contain numbers or " + operandsRegex + ", all separated by a space.");
//        }
    }

    /**
     * creates an operation based on a string
     * order of operations is defined by parentheses
     *
     * @param input list of inputString
     * @return BinaryOperation or Operation object
     */
    private Operation createOp(List<String> input) {
        if (input.size() == 0) throw new NullPointerException("Input is an empty array.");
        if (input.size() == 1) {
            if (input.get(0).contains("(")) {
                String noParentheses = input.get(0).substring(1, input.get(0).length() - 1);
                if (noParentheses.charAt(0) == '(') {
                    List<String> temp = new ArrayList<>();
                    temp.add(noParentheses.replaceAll("(\\(.*\\)).*", "$1"));
                    temp.addAll(Arrays.asList(noParentheses.replaceAll("( \\(.*\\))|(\\(.*\\) )", "")
                            .split(" ")));
                    return createOp(temp);
                } else if (noParentheses.charAt(noParentheses.length() - 1) == ')') {
                    List<String> temp = new ArrayList<>(Arrays.asList(noParentheses
                            .replaceAll("( \\(.*\\))|(\\(.*\\) )", "").split(" ")));
                    temp.add(noParentheses.replaceAll("(\\(.*\\)).*", "$1"));
                    return createOp(temp);
                } else {
                    return createOp(new ArrayList<>(Arrays.asList(noParentheses.split(" "))));
                }
            } else {
                return new Operation(Double.parseDouble(input.get(0)));
            }
        }
        Operation lastOp = createOp(new ArrayList<>(Collections.singleton(input.get(0))));
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).matches(operandsRegex.toString())) {
                lastOp = new BinaryOperation(lastOp, input.get(i).charAt(0),
                        createOp(new ArrayList<>(Collections.singleton(input.get(i + 1)))));
            }
        }
        return lastOp;
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
     * creates an order of operations by adding parentheses where needed
     *
     * @param input inputString
     * @return list of input string with added "()", operands are surrounded by spaces
     */
    private static List<String> addParentheses(String input) {
        if (input.length() == 0) throw new NullPointerException("Input is an empty string.");
        List<String> list = Arrays.asList(input.split(" "));
        if (!input.contains("*") && !input.contains("/")) {
            return list;
        }
        boolean done = false;
        while (!done) {
            List<String> temp = new ArrayList<>(list);
            for (int i = 0; i <= list.size(); i++) {
                if (i == list.size()) {
                    done = true;
                    break;
                }
                if (list.get(i).equals("*") || list.get(i).equals("/")) {
                    String op = "(" + list.get(i - 1) + " " + list.get(i) + " " + list.get(i + 1) + ")";
                    temp.remove(i - 1);
                    temp.remove(i - 1);
                    temp.remove(i - 1);
                    temp.add(i - 1, op);
                    break;
                }
            }
            list = new ArrayList<>(temp);
        }
        return list;
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
                "( 100 - 50 ) / 5 ^ 2", "50 + 10 + 20 - 30", "7 / 7 + 8 / 8", "4 + 50 * 50 / 10", "500", "8 ^ 2"
        };
        for (String input : inputs) {
            try {
                System.out.println(input);
                System.out.println(" = " + new OperationEngine(input, true).calculate() + "\n");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

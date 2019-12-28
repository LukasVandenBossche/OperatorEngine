package Operations;

import java.util.Arrays;
import java.util.InputMismatchException;

/**
 * @author lukas on 19/12/2019
 * @project OperatorEngine
 */

class BinaryOperation extends Operation {
    /**
     * enum class for mathematical operands
     * char op is just the visual representation of the operand
     * int order holds track of the order of operations
     * order 0 = Parentheses
     * order 1 = Exponents (and Roots)
     * order 2 = Multiplication and Division
     * order 3 = Addition and Subtraction
     */
    enum Operand {
        POW('^', 1),
        ROT('√', 1),
        MUL('*', 2),
        DIV('/', 2),
        MOD('%', 2),
        ADD('+', 3),
        SUB('-', 3);

        private char op;
        private int order;

        Operand(char op, int order) {
            this.op = op;
            this.order = order;
        }

        public char getOp() {
            return op;
        }

        public int getOrder() {
            return order;
        }

        public static boolean matchesOperand(int order, String opString) {
            if (opString.length() > 1) throw new InputMismatchException("Operand representation can only be a single character.");
            for (Operand operand : Operand.values()) {
                if (operand.order == order && operand.op == opString.charAt(0)) {
                    return true;
                }
            }
            return false;
        }

        static Operand getOperand(char opString) {
            for (Operand operand : Operand.values()) {
                if (operand.op == opString) {
                    return operand;
                }
            }
            throw new InputMismatchException("Variable opString is an invalid operation, it can only be " + Arrays.toString(Operand.values()));
        }
    }

    private Operation a;
    private Operand operand;
    private Operation b;

    BinaryOperation(Operation a, char opString, Operation b) {
        super(0);
        this.a = a;
        this.operand = Operand.getOperand(opString);
        this.b = b;
    }

    @Override
    double calculate() {
        switch (operand) {
            case ADD:
                return a.calculate() + b.calculate();
            case SUB:
                return a.calculate() - b.calculate();
            case MUL:
                return a.calculate() * b.calculate();
            case DIV:
                return a.calculate() / b.calculate();
            case MOD:
                return a.calculate() % b.calculate();
            case POW:
                return Math.pow(a.calculate(), b.calculate());
            case ROT:
                return Math.pow(b.calculate(), 1/a.calculate());
        }
        return 0.0;
    }
}



package Operations;

/**
 * @author LukasVandenBossche on 19/12/2019
 * @project OperatorEngine
 */

class Operation {
    private double a;

    /**
     * this class only exists to be able to create recursive BinaryOperation objects
     *
     * @param a numeric value of this unary operation
     */
    Operation(double a) {
        this.a = a;
    }

    double calculate() {
        return a;
    }
}

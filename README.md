# OperatorEngine
An engine that can convert a String to mathematical operations and can calculate the result.
It keeps the order of operations in mind 
Examples:
- "50 + 4" = 50 + 4 = 54.0
- "50 / 5 ^ 2" = 50 / 5 ^ 2 = 2.0

The engine can read:
- Doubles
- Addition(+), Subtraction(-), Multiplication(*), Division(/), Modulus(%), Exponents(^), and Roots(√)
- Parentheses

How to use the engine:
- download and move the package "Operations" to your source folder
- add following import statement where you want to use the engine "import Operations.OperationEngine;"
- then you can use following line of code to run the engine
  "double d = new OperationEngine("2 + 2", false).calculate();"
  

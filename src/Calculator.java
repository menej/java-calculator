import java.util.ArrayDeque;
import java.util.EmptyStackException;
import java.util.Queue;
import java.util.Stack;

public class Calculator {

    public static double calculateDouble(String evaluationString) throws EmptyStackException {
        return postfixEvaluator(shuntingYard(evaluationString));
    }

    public static int calculateInteger(String evaluationString) throws EmptyStackException {
        return (int) calculateDouble(evaluationString);
    }

    public static String[] toPostfixNotationArray(String evaluationString) {
        return shuntingYard(evaluationString).toArray(new String[0]);
    }

    public static String toPostfixNotationStringSpaces(String evaluationString) {
        String[] postfixArray = toPostfixNotationArray(evaluationString);
        StringBuilder sb = new StringBuilder();
        for (String s : postfixArray) {
            sb.append(s).append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String toPostfixNotationString(String evaluationString) {
        String[] postfixArray = toPostfixNotationArray(evaluationString);
        StringBuilder sb = new StringBuilder();
        for (String s : postfixArray) {
            sb.append(s);
        }
        return sb.toString();
    }

    private static double postfixEvaluator(Queue<String> evaluationQueue) throws EmptyStackException {
        Stack<Double> stack = new Stack<>();
        while (!evaluationQueue.isEmpty()) {
            String element = evaluationQueue.poll();
            if (element.matches("^-?\\d+(.\\d+)?$")) {
                stack.push(Double.parseDouble(element));
            } else {
                double secondOperand = stack.pop();
                double firstOperand = stack.pop();
                switch (element) {
                    case ("+") -> stack.push(firstOperand + secondOperand);
                    case ("-") -> stack.push(firstOperand - secondOperand);
                    case ("*") -> stack.push(firstOperand * secondOperand);
                    case ("/") -> stack.push(firstOperand / secondOperand);
                    default -> throw new ArithmeticException();
                }
            }
        }
        return stack.pop();
    }

    /**
     * Converst infix notation to postfix notation (reverse polish notation).
     *
     * @param evaluationString the string that gets evaluated
     * @return double
     */
    private static Queue<String> shuntingYard(String evaluationString) throws ArithmeticException {
        Queue<String> queue = new ArrayDeque<>();
        Stack<String> stack = new Stack<>();
        evaluationString = evaluationString.replace(" ", "");

        int i = 0;
        while (i < evaluationString.length()) {
            String element = String.valueOf(evaluationString.charAt(i));

            // Example: -(5 * 4) gets evaluated as -1 * (5 * 4)
            if (element.equals("-") && i + 1 < evaluationString.length() && evaluationString.charAt(i + 1) == '(') {
                queue.add("-1");
                stack.push("*");
                i++;
                continue;
            }


            // Condition 1 catches negative number: -5 + 2
            // Condition 2 catches negative number: (-2) + 3; does not catch (5 + 2) - 2
            // Condition 3 catches any positive digit.
            if (i == 0 && element.equals("-") ||
                    element.equals("-") &&
                            !Character.isDigit(evaluationString.charAt(i - 1)) &&
                            evaluationString.charAt(i - 1) != ')' ||
                    element.matches("\\d")) {

                // Build a number if the number contains more digits or . and decimals
                StringBuilder sb = new StringBuilder();
                sb.append(element);
                while (i + 1 < evaluationString.length() &&
                        (Character.isDigit(evaluationString.charAt(i + 1)) || evaluationString.charAt(i + 1) == '.')) {
                    sb.append(evaluationString.charAt(++i));
                }

                queue.add(sb.toString());
                i++;
                continue;
            }


            // Element is an operator, check what type it is and initiate action accordingly
            switch (element) {
                case "(", "/", "*" -> stack.push(element);
                case "-", "+" -> {
                    // If the stack is not empty and the top element is * and /, then enqueue that element (PEMDAS rule)
                    if (!stack.isEmpty() && (stack.peek().equals("*") || stack.peek().equals("/"))) {
                        String poppedElement = stack.pop();
                        queue.add(poppedElement);
                    }
                    // Add current element to stack
                    stack.push(element);
                }
                case ")" -> {
                    if (stack.isEmpty())
                        throw new ArithmeticException();

                    // Keep enqueueing elements from stack to queue until string "(" is reached
                    String poppedElement = stack.pop();
                    while (!poppedElement.equals("(")) {
                        queue.add(poppedElement);
                        if (stack.isEmpty())
                            throw new ArithmeticException();
                        poppedElement = stack.pop();
                    }
                }
                default -> throw new ArithmeticException();
            }
            i++;
        }

        while (!stack.isEmpty())
            queue.add(stack.pop());

        return queue;
    }

}

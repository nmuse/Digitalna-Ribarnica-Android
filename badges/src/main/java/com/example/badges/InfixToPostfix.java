package com.example.badges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class InfixToPostfix {

    static int Prec(String ch)
    {
        switch (ch)
        {
            case "|":
                return 1;
            case "&":
                return 2;
            case "=":
                return 3;
            case ">":
            case "<":
                return 4;
        }
        return -1;
    }

    // The main method that converts
    // given infix expression
    // to postfix expression.
    static String infixToPostfix(String exp)
    {
        // initializing empty String for result
        String result = new String("");

        // initializing empty stack
        Stack<String> stack = new Stack<>();

        for (int i = 0; i<exp.length(); i++)
        {
            String c = "";
            c = exp.substring(i,i+1);

            // If the scanned character is an
            // operand, add it to output.
            if (c.matches(".*\\d.*")) {
                result += c;
                //result += " ";
            }

            // If the scanned character is an '(',
            // push it to the stack.
            //c == '('
            else if (c.equals("("))
                stack.push(c);

                // If the scanned character is an ')',
                // pop and output from the stack
                // until an '(' is encountered.
            else if (c.equals(")"))
            {
                //stack.peek() != '('
                while (!stack.isEmpty() &&
                        !stack.peek().equals("(")) {
                    result += stack.pop();
                    result += " ";
                }


                stack.pop();
            }
            else // an operator is encountered
            {
                result += " ";
                while (!stack.isEmpty() && Prec(c)
                        <= Prec(stack.peek())){

                    result += stack.pop();
                    result += " ";
                }
                stack.push(c);
            }

        }

        // pop all the operators from the stack
        while (!stack.isEmpty()){
            //stack.peek() == '('
            if(stack.peek().equals("("))
                return "Invalid Expression";
            result += stack.pop();
        }
        return result;
    }

    // Driver method
    public static String calculateInfixToPostfix(String condition)
    {
        String postfix;
        postfix = infixToPostfix(condition);
        //String exp = "a+b*(c^d-e)^(f+g*h)-i";
        //System.out.println(infixToPostfix(exp));
        return postfix;
    }

    /*public static final int                LEFT_ASSOC      = 0;
    public static final int                RIGHT_ASSOC     = 1;
    public static final Map<String, int[]> ARITH_OPERATORS = new HashMap<String, int[]>();
    public static final Map<String, int[]> REL_OPERATORS   = new HashMap<String, int[]>();
    public static final Map<String, int[]> LOG_OPERATORS   = new HashMap<String, int[]>();
    public static final Map<String, int[]> OPERATORS       = new HashMap<String, int[]>();

    static {
        ARITH_OPERATORS.put("+", new int[] { 25, LEFT_ASSOC });
        ARITH_OPERATORS.put("-", new int[] { 25, LEFT_ASSOC });
        ARITH_OPERATORS.put("*", new int[] { 30, LEFT_ASSOC });
        ARITH_OPERATORS.put("/", new int[] { 30, LEFT_ASSOC });
        ARITH_OPERATORS.put("%", new int[] { 30, LEFT_ASSOC });
        ARITH_OPERATORS.put("^", new int[] { 35, RIGHT_ASSOC });
        ARITH_OPERATORS.put("**", new int[] { 30, LEFT_ASSOC });

        REL_OPERATORS.put("<", new int[] { 20, LEFT_ASSOC });
        REL_OPERATORS.put("<=", new int[] { 20, LEFT_ASSOC });
        REL_OPERATORS.put(">", new int[] { 20, LEFT_ASSOC });
        REL_OPERATORS.put(">=", new int[] { 20, LEFT_ASSOC });
        REL_OPERATORS.put("==", new int[] { 20, LEFT_ASSOC });
        REL_OPERATORS.put("!=", new int[] { 20, RIGHT_ASSOC });

        LOG_OPERATORS.put("!", new int[] { 15, RIGHT_ASSOC });

        LOG_OPERATORS.put("&&", new int[] { 10, LEFT_ASSOC });

        LOG_OPERATORS.put("||", new int[] { 5, LEFT_ASSOC });

        LOG_OPERATORS.put("EQV", new int[] { 0, LEFT_ASSOC });
        LOG_OPERATORS.put("NEQV", new int[] { 0, LEFT_ASSOC });

        OPERATORS.putAll(ARITH_OPERATORS);
        OPERATORS.putAll(REL_OPERATORS);
        OPERATORS.putAll(LOG_OPERATORS);
    }

    private static boolean isAssociative(String token, int type) {
        if (!isOperator(token)) {
            System.out.println("");
        }
        if (OPERATORS.get(token)[1] == type) {
            return true;
        }
        return false;
    }

    private static boolean isOperator(String token) {
        return OPERATORS.containsKey(token);
    }

    private static int cmpPrecedence(String token1, String token2) {
        if (!isOperator(token1) || !isOperator(token2)) {
            System.out.println("");
        }
        return OPERATORS.get(token1)[0] - OPERATORS.get(token2)[0];
    }

    public static ArrayList<String> infixToRPN(String[] inputTokens) {
        ArrayList<String> out = new ArrayList<String>();
        Stack<String> stack = new Stack<String>();
        // For all the input tokens [S1] read the next token [S2]
        for (String token : inputTokens) {
            if (isOperator(token)) {
                // If token is an operator (x) [S3]
                while (!stack.empty() && isOperator(stack.peek())) {
                    // [S4]
                    if ((isAssociative(token, LEFT_ASSOC) && cmpPrecedence(token, stack.peek()) <= 0)
                            || (isAssociative(token, RIGHT_ASSOC) && cmpPrecedence(token, stack.peek()) < 0)) {
                        out.add(stack.pop()); // [S5] [S6]
                        continue;
                    }
                    break;
                }
                // Push the new operator on the stack [S7]
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token); // [S8]
            } else if (token.equals(")")) {
                // [S9]
                while (!stack.empty() && !stack.peek().equals("(")) {
                    out.add(stack.pop()); // [S10]
                }
                stack.pop(); // [S11]
            } else {
                out.add(token); // [S12]
            }
        }
        while (!stack.empty()) {
            out.add(stack.pop()); // [S13]
        }
        return out;
    }*/

}

package com.example.badges;

import java.util.Stack;

public class CalculatePostfix {

    public static int evaluatePostfix(String exp)
    {
        Stack<Integer> stack = new Stack<>();
        for(int i = 0; i < exp.length(); i++)
        {
            char c = exp.charAt(i);

            if(c == ' ')
                continue;
            else if(Character.isDigit(c))
            {
                int n = 0;
                while(Character.isDigit(c))
                {
                    n = n*10 + (int)(c-'0');
                    i++;
                    c = exp.charAt(i);
                }
                i--;
                stack.push(n);
            }

            else
            {
                int val1 = stack.pop();
                int val2 = stack.pop();
                switch(c)
                {
                    case '+':
                        stack.push(val2+val1);
                        break;

                    case '-':
                        stack.push(val2- val1);
                        break;

                    case '/':
                        stack.push(val2/val1);
                        break;

                    case '*':
                        stack.push(val2*val1);
                        break;

                    case '=':
                        if(val2==val1){
                            stack.push(1);
                        }else{
                            stack.push(0);
                        }
                        break;

                    case '<':
                        if(val2<val1){
                            stack.push(1);
                        }else{
                            stack.push(0);
                        }
                        break;

                    case '>':
                        if(val2>val1){
                            stack.push(1);
                        }else{
                            stack.push(0);
                        }
                        break;

                    case '|':
                        if(val2==1 || val1==1){
                            stack.push(1);
                        }else{
                            stack.push(0);
                        }
                        break;

                    case '&':
                        if(val2==1 && val1==1){
                            stack.push(1);
                        }else{
                            stack.push(0);
                        }
                        break;
                }
            }
        }
        return stack.pop();
    }
}

package pixie.parser;

import pixie.parser.values.BoolValue;
import pixie.parser.values.NumValue;
import pixie.parser.values.TextValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathParser {
     static final Map<String, Integer> operators = LineParser.ofEntries(
             LineParser.entry("-", 2),
             LineParser.entry("+", 2),
             LineParser.entry("/", 3),
             LineParser.entry("*", 3),
             LineParser.entry("^", 4),
             LineParser.entry("%", 4),
             LineParser.entry("&&", 1),
             LineParser.entry("||", 1),
             LineParser.entry("==", 0),
             LineParser.entry("!=", 0),
             LineParser.entry("<", 0),
             LineParser.entry("<=", 0),
             LineParser.entry(">", 0),
             LineParser.entry(">=", 0)
     );

     public static LineParser.Value parseBaseValue(String name, String line) {
          Operable value;
          try {
               // Number
               value = new NumValue(Float.parseFloat(line));
          } catch (Exception ignored) {
               // Bool
               String w = LineParser.removeWhitespaces(line);
               if (w.equalsIgnoreCase("true") || w.equalsIgnoreCase("false"))
                    value = new BoolValue(LineParser.removeWhitespaces(line).equalsIgnoreCase("true"));
                    // Text
               else value = new TextValue(line);
          }

          return new LineParser.Value(name, value);
     }

     /**
      * Performs the Shunting Yard algorithm
      *
      * @param expression the mathematical expression
      * @return the result, if found
      * @throws IllegalArgumentException invalid expression exception
      * @throws NumberFormatException    error while parsing a number, probably due to an invalid expression
      * @throws ArithmeticException      division by 0
      */
     public static LineParser.Value compute(String name, String expression, LineParser parser) throws IllegalArgumentException, SyntaxException {
          // throw an exception if the string is null or empty
          if (expression == null || expression.trim().length() == 0)
               throw new IllegalArgumentException("Empty expression or null");

          //remove all blank spaces
          expression = expression.replaceAll("\\s+", "");

          //add a 0 before the "-" in order to consider the "-" as a standalone operator
          expression = expression.replace("(-", "(0-");

          //same thing here
          if (expression.startsWith("-")) {
               expression = "0" + expression;
          }

          //read the expression and check if it contains only allowed token
          Pattern pattern = Pattern.compile("(([0-9]*[.])?[0-9]+)|(==|<=|>=|[\\+\\-\\*\\/\\(\\)\\^\\%\\>\\<])");
          Matcher matcher = pattern.matcher(expression);

          int counter = 0; //must be equal to the index of the end of the last matching group
          List<String> tokens = new ArrayList<>();
          while (matcher.find()) {
               if (matcher.start() != counter) {
                    //at this point counter indicates the end of the last matching group. If the next matching group
                    //doesn't start at this index, it means that some characters were skipped
                    throw new IllegalArgumentException("Invalid Expression:" + expression + ". Error between " + counter + " end " + matcher.start());
               }
               tokens.add(matcher.group().trim());//add the token if it's okay
               counter += tokens.get(tokens.size() - 1).length();//update the counter
          }
          if (counter != expression.length()) {
               //if the matcher reaches the end of the string, we want to check if the last matching group ends at the end of the expression
               throw new IllegalArgumentException("Invalid end of expression");
          }

          //if we are here, it means that all the concatenation of all matching group = whole expression.
          // /!\ it doesn't means that the expression is valid ! For example, this string would be acepted:
          // 7/*-7, and the result would be 0

          Stack<String> stack = new Stack<>(); //operators stack
          List<String> output = new ArrayList<>(); //output queue

          //the main algorithm
          for (String token : tokens) {
               //read the token. We have 4 options:
               // - it's an operator
               // - it's a (
               // - it's a )
               // - it's a number
               if (operators.containsKey(token)) {
                    //it's an operator.
                    //We have to check:
                    // - if the stack is not empty
                    // - if the element on the top of the stack is an operator
                    // - if the operator represented by the token has a priority less or equal than the operator
                    //   represented by the element on the top of the stack, and if is left-associated
                    //   OR
                    //   if the operator represented by the token has a priority less than the operator represented
                    //   by the element on the top of the stacke, and is right associated
                    while (!stack.empty() &&
                            operators.containsKey(stack.peek()) &&
                            ((operators.get(token) <= operators.get(stack.peek()) && !token.equals("^")) ||
                                    (operators.get(token) < operators.get(stack.peek()) && token.equals("^")))) {
                         output.add(stack.pop()); //pop the element on the top of the stack and add it to the output
                    }
                    stack.push(token); // finally, push the token on the top of the stack

               } else if (token.equals("(")) {
                    //if its a left parenthesis, push it onto the stack. It will be removed as the associted right parenthesis will be found
                    stack.push(token);
               } else if (token.equals(")")) {
                    //if it's a right parenthesis, pop the stack until a left parenthesis is found, or the the stack is empty
                    while (!stack.empty()) {
                         if (!stack.peek().equals("(")) {
                              output.add(stack.pop());
                         } else {
                              break;
                         }
                    }
                    if (!stack.empty()) {
                         stack.pop();// finally, remove the left parenthesis
                    }
               } else {
                    output.add(token); //numbers are immediatly added to the output queue
               }
          }

          while (!stack.empty()) {
               output.add(stack.pop());// while the stack is not empty, pop elements (normally there are only operators in the stack
               // at this point) and add it to the ouput queue
          }

          // Then, the output queue represents normally the RPN
          Stack<Operable> result = new Stack<>();
          for (String token : output) {
               //for each token (normally there are only numbers OR operators)

               //if the token is not an operator and is a number, try to parse it and push it onto the stack
               if (!operators.containsKey(token) && token.matches("([0-9]*[.])?[0-9]+")) {
                    try {
                         result.push(parseBaseValue(name, token).value);
                    } catch (NumberFormatException n) {
                         throw n;
                    }
               } else {
                    // if it's an operator, get the 2 elements at the top and perform the right operation.
                    // you should remind that is always op2 (operator) op1
                    if (result.size() > 1) {
                         Operable op1 = result.pop();
                         Operable op2 = result.pop();
                         switch (token) {
                              case "+":
                                   result.push(op2.add(op1, parser));
                                   break;
                              case "-":
                                   result.push(op2.sub(op1, parser));
                                   break;
                              case "*":
                                   result.push(op2.mul(op1, parser));
                                   break;
                              case "/":
                                   result.push(op2.div(op1, parser));
                                   break;
                              case "^":
                                   result.push(op2.pow(op1, parser));
                                   break;
                              case "%":
                                   result.push(op2.mod(op1, parser));
                                   break;
                              case "&&":
                                   result.push(op2.and(op1, parser));
                                   break;
                              case "||":
                                   result.push(op2.or(op1, parser));
                                   break;
                              case "==":
                                   result.push(op2.equals(op1, parser));
                                   break;
                              case "!=":
                                   result.push(op2.notEquals(op1, parser));
                                   break;
                              case "<":
                                   result.push(op2.less(op1, parser));
                                   break;
                              case "<=":
                                   result.push(op2.lessEqu(op1, parser));
                                   break;
                              case ">":
                                   result.push(op2.more(op1, parser));
                                   break;
                              case ">=":
                                   result.push(op2.moreEqu(op1, parser));
                                   break;
                              default:
                                   throw new IllegalArgumentException(token + " is not an operator or is not handled");
                         }
                    }
               }
          }
          //normally, it remains only one number in the stack
          if (result.empty() || result.size() > 1) {
               throw new IllegalArgumentException("Invalid expression, could not find a result. An operator seems to be absent");
          }
          return new LineParser.Value(name, result.peek());
     }
}

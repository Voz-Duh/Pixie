package pixie.parser;

import pixie.parser.values.*;

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

     public static LineParser.Value parseValue(String name, String line, LineParser parser) throws SyntaxException {
          Operable value = null;

          if (line.contains("(") && line.contains("[") && line.contains("{")) {
               String[] move = LineParser.removeWhitespaces(LineParser.getInsideBoxBrackets(line)).split(",");
               String[] args = LineParser.removeWhitespaces(LineParser.getInsideBrackets(line)).split(",");
               String code = LineParser.getInsideBraces(line);
               return new LineParser.Value(name, new FunctionValue(code, args, move));
          }

          if (line.contains("(")) {
               Function function = parser.function(line.split("\\(")[0].trim());
               if (!function.code.code.equals("")) {
                    LineParser.Result p = parser.parse(name, function.code.code);
                    value = p.value;
               }
               else value = function.code.lamda.apply(line, LineParser.words(line), parser);
          }

          if (line.contains("[")) {
               Operable variable = parser.variable(line.split("\\[")[0].trim());
               if (variable instanceof ListValue) {
                    ((ListValue) variable).value.get((int) ((NumValue) compute(name, LineParser.getInsideBoxBrackets(line), parser).value).value);
               }
          }

          return value == null ? parseBaseValue(name, line) : new LineParser.Value(name, value);
     }
     /*
     public Value parseValue(String name, String value) throws SyntaxException {
          if (value.contains("(") && value.contains("[") && value.contains("{")) {
               if (removeWhitespaces(value).startsWith("lambda")) {
                    String[] move = getInside(0, value, '[', ']').split(",");
                    String[] args = removeWhitespaces(getInsideBracketsFixed(0, value)).split(",");
                    String code = getInside(0, value, '{', '}').replace("->", "\n");
                    return new Value(name, new FunctionValue(code, args, move));
               }
          }

          boolean isInversed = false;
          if (removeWhitespaces(value).startsWith("-")) {
               value = removeWhitespaces(value).substring(1);
               isInversed = true;
          }

          List<CharSequence> operators = listOf("&&", "||");

          boolean haveOperation = false;
          for (CharSequence s : operators) {
               if (value.contains(s)) {
                    haveOperation = true;
                    break;
               }
          }

          if (haveOperation) {
               String[] adds = getNextString(0, value, operators);

               Operable parsedValue = parseValue(name, adds[0]).value;
               if (isInversed) parsedValue = parsedValue.inv(this);

               switch (adds[2]) {
                    case "&&":
                         return new Value(name, parsedValue.and(parseValue(name, adds[1]).value, this));
                    case "||":
                         return new Value(name, parsedValue.or(parseValue(name, adds[1]).value, this));
               }
          }

          operators = listOf(">=", ">", "<=", "<", "==", "!=");

          haveOperation = false;
          for (CharSequence s : operators) {
               if (value.contains(s)) {
                    haveOperation = true;
                    break;
               }
          }

          if (haveOperation) {
               String[] adds = getNextString(0, value, operators);

               Operable parsedValue = parseValue(name, adds[0]).value;
               if (isInversed) parsedValue = parsedValue.inv(this);


               switch (adds[2]) {
                    case ">":
                         return new Value(name, parsedValue.more(parseValue(name, adds[1]).value, this));
                    case ">=":
                         return new Value(name, parsedValue.moreEqu(parseValue(name, adds[1]).value, this));
                    case "<":
                         return new Value(name, parsedValue.less(parseValue(name, adds[1]).value, this));
                    case "<=":
                         return new Value(name, parsedValue.lessEqu(parseValue(name, adds[1]).value, this));
                    case "==":
                         return new Value(name, parsedValue.equals(parseValue(name, adds[1]).value, this));
                    case "!=":
                         return new Value(name, parsedValue.notEquals(parseValue(name, adds[1]).value, this));
               }
          }

          if (value.contains("**")) {
               String[] adds = getNextString(0, value, listOf("**"));

               Operable parsedValue = parseValue(name, adds[0]).value;
               if (isInversed) parsedValue = parsedValue.inv(this);

               return new Value(name, parsedValue.pow(parseValue(name, adds[1]).value, this));
          }

          operators = listOf("+", "-", "*", "/");

          haveOperation = false;
          for (CharSequence s : operators) {
               if (value.contains(s)) {
                    haveOperation = true;
                    break;
               }
          }

          if (haveOperation) {
               String[] adds = getNextString(0, value, operators);

               Operable parsedValue = parseValue(name, adds[0]).value;
               if (isInversed) parsedValue = parsedValue.inv(this);

               switch (adds[2]) {
                    case "+":
                         return new Value(name, parsedValue.add(parseValue(name, adds[1]).value, this));
                    case "-":
                         return new Value(name, parsedValue.sub(parseValue(name, adds[1]).value, this));
                    case "*":
                         return new Value(name, parsedValue.mul(parseValue(name, adds[1]).value, this));
                    case "/":
                         return new Value(name, parsedValue.div(parseValue(name, adds[1]).value, this));
               }
          }

          if (value.contains("instof")) {
               String[] adds = getNextString(0, value, listOf("instof"));

               Value parsed = parseValue(name, adds[0]);
               String cleared = removeWhitespaces(adds[1]);
               if (values.containsKey(cleared))
                    return new Value(name, new BoolValue(parsed.value.getClass() == values.get(cleared)));
               else if (classes.containsKey(cleared) && parsed.value instanceof InstanceValue)
                    return new Value(name, new BoolValue(((InstanceValue) parsed.value).currentClass.name.equals(cleared)));
               else
                    return new Value(name, new BoolValue(parsed.value.getClass() == parseValue(name, adds[1]).value.getClass()));
          }

          if (removeWhitespaces(value).startsWith("\'")) {
               String inside = getString(0, value);
               return new Value(name, new TextValue(inside
                       .replace("\\\\", "\\")
                       .replace("\\n", "\n")
                       .replace("\\'", "'")));
          }

          if (value.contains("[")) {
               String[] inside = split(getInside(0, value, '[', ']'), ',');
               String[] args = removeWhitespaces(value).split("\\[", 2);
               if (variables.containsKey(args[0])) {
                    Operable list = variables.get(args[0]);
                    if (list instanceof ListValue) {
                         return new Value(name, ((ListValue) list).value.get(((Float) parseValue("", inside[0]).value.get(this)).intValue()));
                    }
               } else {
                    ListValue list = new ListValue();
                    if (!(inside.length == 1 && inside[0].equals("")))
                         for (String item : inside) {
                              list.value.add(parseValue("", item).value);
                         }
                    return new Value(name, list);
               }
          }

          if (removeWhitespaces(value).startsWith("init")) {
               int length = getNextString(0, value, listOf("init"))[0].length();
               int classlength = getNextString(0, value, listOf("("))[0].length();
               String currentClass = removeWhitespaces(value.substring(length + 4, classlength));

               InstanceValue inst = new InstanceValue();
               if (classes.containsKey(currentClass) && classes.get(currentClass).functions.containsKey("@init")) {
                    inst = new InstanceValue(classes.get(currentClass));
                    String inside = getInsideBrackets(getNextString(0, value, listOf(currentClass))[0].length() + currentClass.length(), value);
                    String[] splited = split(inside, ',');

                    Function function = classes.get(currentClass).functions.get("@init");
                    for (int i = 0; i < function.arguments.length; i++) {
                         if (classes.get(currentClass).variables.containsKey(removeWhitespaces(function.arguments[i])))
                              inst.variables.put(removeWhitespaces(function.arguments[i]), parseValue("", splited[i]).value);
                    }

                    inst.parse(this, function);
               } else {
                    String inside = getInsideBrackets(length + "init".length(), value);
                    for (String i : split(inside, ',')) {
                         String[] _var = i.split(":", 2);

                         Value parsed = parseValue(removeWhitespaces(_var[0]), _var[1]);
                         inst.variables.put(parsed.name, parsed.value);
                    }
               }

               return new Value(name, inst);
          }

          if (value.contains("(")) {
               String current = getNextString(0, removeWhitespaces(value), listOf("("))[0];

               if (haveVariable(current)) {
                    Value variable = new Value(name, variables.get(current));
                    if (variable.value instanceof InstanceValue) {
                         String inside = getInsideBrackets(getNextString(0, value, listOf(current))[0].length() + current.length(), value);

                         variable.value = ((InstanceValue) variable.value).get(this).get(removeWhitespaces(inside));
                    }
                    if (variable.value instanceof FunctionValue) {
                         FunctionValue fun = ((FunctionValue) variable.value);
                         Value[] movables = new Value[fun.movables.length + fun.arguments.length];

                         for (int i = 0; i < fun.movables.length; i++)
                              movables[i] = new Value(fun.movables[i], variables.get(fun.movables[i]));

                         String[] inside = split(getInsideBrackets(0, value), ',');
                         for (int i = 0; i < fun.arguments.length; i++) {
                              Value parsed = parseValue(fun.arguments[i], inside[i]);
                              movables[fun.movables.length + i] = new Value(parsed.name, parsed.value);
                         }

                         return new Value(name, parse(fun.value, movables).value);
                    }
                    return variable;
               }

               if (haveFunction(current)) {
                    Function fun = functions.get(current);
                    if (!fun.code.code.equals("")) return new Value(name, parse(current, fun.code.code).value);
                    else return new Value(name, fun.code.lamda.apply(this));
               }

               String[] splited = current.split("\\.", 2);
               InstanceValue variable = ((InstanceValue) variables.get(splited[0]));
               if (variables.containsKey(splited[0]) && variable.functions.containsKey(splited[1])) {
                    Function fun = variable.functions.get(splited[1]);
                    if (!fun.code.code.equals("")) return new Value(name, variable.parse(this, current, fun));
                    else return new Value(name, fun.code.lamda.apply(this));
               }

               if (classes.containsKey(splited[0]) && classes.get(splited[0]).staticFunctions.containsKey(splited[1])) {
                    Function fun = classes.get(splited[0]).staticFunctions.get(splited[1]);
                    if (!fun.code.code.equals("")) return new Value(name, parse(current, splited[0], fun).value);
                    else return new Value(name, fun.code.lamda.apply(this));
               }
          } else {
               String current = removeWhitespaces(value);
               if (haveVariable(current)) {
                    return new Value(name, variables.get(current));
               }

               String[] splited = current.split("\\.", 2);
               InstanceValue variable = ((InstanceValue) variables.get(splited[0]));
               if (variables.containsKey(splited[0]) && variable.variables.containsKey(splited[1])) {
                    return new Value(name, variable.variables.get(splited[1]));
               }

               if (classes.containsKey(splited[0]) && classes.get(splited[0]).staticVariables.containsKey(splited[1])) {
                    return new Value(name, classes.get(splited[0]).staticVariables.get(splited[1]));
               }
          }

          String nsValue = removeWhitespaces(value);
          if (nsValue.startsWith("!")) {
               String removeValue = nsValue.substring(1);
               if (haveVariable(removeValue) && variables.get(removeValue) instanceof InstanceValue)
                    return new Value(name, variables.get(removeValue).inv(this));
               else return new Value(name, parseBaseValue(name, removeValue).value.inv(this));
          } else return parseBaseValue(name, nsValue);
     }
      */

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
          Pattern pattern = Pattern.compile("\\[[^\\[\\]]*\\]\\s*\\([^()]*\\)\\s*\\{[^{}]*\\}|\\s*\\w+\\s*\\([^()]*\\)|\\s*\\w+\\s*\\[[^\\[\\]]*\\]|(([0-9]*[.])?[0-9]+)|(==|<=|>=|[\\+\\-\\*\\/\\(\\)\\^\\%\\>\\<])/");
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
               return parseValue(name, expression, parser);
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
                         result.push(parseValue(name, token, parser).value);
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

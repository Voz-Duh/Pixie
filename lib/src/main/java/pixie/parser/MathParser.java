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
          line = line.trim();
          try {
               // Number
               value = new NumValue(Float.parseFloat(line));
          } catch (Exception ignored) {
               // Bool
               if (line.equalsIgnoreCase("true") || line.equalsIgnoreCase("false"))
                    value = new BoolValue(LineParser.removeWhitespaces(line).equalsIgnoreCase("true"));
               else {
                    // Text
                    if (line.startsWith("\"") && line.endsWith("\""))
                         value = new TextValue(line.substring(1, line.length() - 1));
                    else value = new NullValue();
               }
          }

          return new LineParser.Value(name, value);
     }

     public static LineParser.Value parseValue(String name, String line, LineParser parser) throws SyntaxException {
          if (line.startsWith("!")) {
               LineParser.Value v = parseValue(name, line.replaceFirst("!", ""), parser);
               v.value = v.value.inv(parser);
               return v;
          }

          Operable value = null;

          if (line.contains("[") && line.contains("(") && line.contains("=>") && line.contains("{")) {
               String[] move = LineParser.removeWhitespaces(LineParser.getInsideBoxBrackets(line)).split(",");
               String[] args = LineParser.removeWhitespaces(LineParser.getInsideBrackets(line)).split(",");
               String code = LineParser.getInsideBraces(line);
               return new LineParser.Value(name, new FunctionValue(code, args, move));
          }

          if (line.contains("(")) {
               String[] strs = line.split("\\(", 2)[0].trim().split(" ", 2);
               int i = strs.length;
               if (i == 2) {
                    InstanceValue inst = new InstanceValue();
                    ClassConstructor _class;
                    String className = strs[1].trim();
                    String[] inside = LineParser.split(LineParser.getInsideBrackets(line), ',');
                    if (parser.classes.containsKey(className) && (_class = parser.classes.get(className)).functions.containsKey("@__constructor__" + inside.length)) {
                         inst = new InstanceValue(_class);

                         Function function = _class.functions.get("@__constructor__" + inside.length);

                         inst.parse(parser, function, inside);
                    } else if (className.equals("instance")) {
                         String ins = LineParser.getInsideBrackets(line);
                         for (String arg : LineParser.split(ins, ',')) {
                              String[] _var = arg.split(":", 2);

                              LineParser.Value parsed = compute(_var[0].trim(), _var[1], parser);
                              inst.variables.put(parsed.name, parsed.value);
                         }
                    }
                    return new LineParser.Value(name, inst);
               } else {
                    Function function = parser.function(strs[0].trim(), line);
                    Operable var = parser.variable(strs[0].trim());
                    if (function != null) {
                         if (!function.code.code.equals("")) {
                              LineParser.Result p = parser.parse(name, function.code.code);
                              value = p.value;
                         } else value = function.code.lamda.apply(line, LineParser.words(line), parser);
                    } else if (var instanceof FunctionValue) {
                         parser.parse((FunctionValue) var, line, null);
                    } else throw new SyntaxException("Cannot find function");
               }
          }

          if (line.contains("[")) {
               String s = line.split("\\[", 2)[0].trim();
               if (s.equals("")) {
                    String[] inside = LineParser.split(LineParser.getInsideBoxBrackets(line), ',');
                    Operable[] values = new Operable[inside.length];
                    for (int i = 0; i < inside.length; i++) {
                         values[i] = parseValue(name, inside[i], parser).value;
                    }
                    return new LineParser.Value(name, new ListValue(values));
               } else {
                    Operable variable = parser.variable(s);
                    if (variable instanceof ListValue) {
                         return new LineParser.Value(name, ((ListValue) variable).value.get((int) ((NumValue) compute(name, LineParser.getInsideBoxBrackets(line), parser).value).value));
                    }
               }
          }

          Operable variable = parser.variable(line);
          if (variable != null)
               value = variable;

          return value == null ? parseBaseValue(name, line) : new LineParser.Value(name, value);
     }

     public static final Pattern PATTERN = Pattern.compile(
             "(\\s*|\\b*)\\[[^\\[\\]]*\\]\\s*\\([^()]*\\)\\s*(=>)\\s*\\{[^{}]*\\}|" +
                     "(\\s*|\\b*)\\w+\\s*\\w+\\([^()]*\\)|" +
                     "(\\s*|\\b*)\\w+\\([^()]*\\)|" +
                     "(\\s*|\\b*)\\w+\\[[^\\[\\]]*\\]|" +
                     "(\\s*|\\b*)\\w+\\s*\\b\\w+(\\.\\w+)+\\b\\([^()]*\\)|" +
                     "(\\s*|\\b*)\\b\\w+(\\.\\w+)+\\b\\([^()]*\\)|" +
                     "(\\s*|\\b*)\\b\\w+(\\.\\w+)+\\b\\[[^\\[\\]]*\\]|" +
                     "\\b\\w+(\\.\\w+)+\\b|" +
                     "\"(\\\\.|[^\"])*\"|" +
                     "\\[[^\\[\\]]*\\]|" +
                     "\\d+|" +
                     "!*\\w+|" +
                     "\\w+|" +
                     "\\(|" +
                     "\\)|" +
                     "\\+|" +
                     "-|" +
                     "\\*|" +
                     "/|" +
                     "&&|" +
                     "\\|\\||" +
                     "==|" +
                     "!=|" +
                     ">=|" +
                     "<=|" +
                     "<|" +
                     ">|" +
                     "\\s+");

     public static LineParser.Value compute(String name, String expression, LineParser parser) throws IllegalArgumentException, SyntaxException {
          if (expression == null || expression.trim().length() == 0)
               throw new IllegalArgumentException("Empty expression or null");

          expression = expression.replace("(-", "(0-");

          if (expression.startsWith("-")) {
               expression = "0" + expression;
          }

          Matcher matcher = PATTERN.matcher(expression);

          List<String> tokens = new ArrayList<>();
          while (matcher.find()) {
               String token = matcher.group().trim();
               if (!token.equals("")) {
                    tokens.add(token);//add the token if it's okay
               }
          }
          if (tokens.size() == 1) {
               return parseValue(name, tokens.get(0), parser);
          }

          Stack<String> stack = new Stack<>();
          List<String> output = new ArrayList<>();

          for (String token : tokens) {
               if (operators.containsKey(token)) {
                    while (!stack.empty() &&
                            operators.containsKey(stack.peek()) &&
                            ((operators.get(token) <= operators.get(stack.peek()) && !token.equals("^")) ||
                                    (operators.get(token) < operators.get(stack.peek()) && token.equals("^")))) {
                         output.add(stack.pop());
                    }
                    stack.push(token);

               } else if (token.equals("(")) {
                    stack.push(token);
               } else if (token.equals(")")) {
                    while (!stack.empty()) {
                         if (!stack.peek().equals("(")) {
                              output.add(stack.pop());
                         } else {
                              break;
                         }
                    }
                    if (!stack.empty()) {
                         stack.pop();
                    }
               } else {
                    output.add(token);
               }
          }

          while (!stack.empty()) {
               output.add(stack.pop());
          }

          Stack<Operable> result = new Stack<>();
          for (String token : output) {
               if (!operators.containsKey(token)) {
                    try {
                         result.push(parseValue("", token, parser).value);
                    } catch (NumberFormatException n) {
                         throw n;
                    } catch (SyntaxException e) {
                         e.printStackTrace();
                    }
               } else {
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

          if (result.empty() || result.size() > 1) {
               throw new IllegalArgumentException("Invalid expression, could not find a result. An operator seems to be absent");
          }
          return new LineParser.Value(name, result.peek());
     }
}

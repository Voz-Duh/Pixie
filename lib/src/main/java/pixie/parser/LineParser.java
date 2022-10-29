package pixie.parser;

import pixie.parser.modules.FileModule;
import pixie.parser.modules.MathModule;
import pixie.parser.modules.PixieModule;
import pixie.parser.modules.SocketModule;
import pixie.parser.values.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineParser {
     public final static String executableType = "px";

     public final static Map<String, PixieModule> modules = new HashMap<>(Map.ofEntries(
             Map.entry("math", new MathModule()),
             Map.entry("files", new FileModule()),
             Map.entry("sockets", new SocketModule())
     ));
     public final Map<String, Operable> variables = new HashMap<>();
     public final Map<String, Function> functions = new HashMap<>(Map.ofEntries(
             Map.entry("for",
                     PixieModule.function(
                             (LineParser self) -> {
                                  try {
                                       var name = "for";
                                       var inside = self.getInsideBrackets(getNextString(0, self.line, List.of(name))[0].length() + name.length(), self.line).split(",");

                                       StringBuilder resultCode = new StringBuilder();
                                       int end = self.getEnd(self.line_index, self.lines);
                                       for (int i = self.line_index + 1; i < end; i++)
                                            resultCode.append(self.lines[i]).append('\n');
                                       self.setLine(end);

                                       var parsedLeft = self.parseValue("", inside[0]).value;
                                       if (parsedLeft instanceof NumValue) {
                                            var operator = removeWhitespaces(inside[2]);
                                            var parsedRight = self.parseValue("", inside[3]).value.get();
                                            if (parsedRight instanceof Float) {
                                                 var move = (Float) self.parseValue("", inside[4]).value.get();
                                                 for (float i = (Float) parsedLeft.get(); (
                                                         operator.equals("<") ? i < (Float) parsedRight :
                                                                 (operator.equals(">") ? i > (Float) parsedRight :
                                                                         (operator.equals(">=") ? i >= (Float) parsedRight :
                                                                                 i <= (Float) parsedRight))
                                                 ); i += move)
                                                      self.parse(resultCode.toString(), new Value(removeWhitespaces(inside[1]), new NumValue(i)));
                                            }
                                       }
                                       if (parsedLeft instanceof BoolValue) {
                                            var b = ((BoolValue) parsedLeft).get();
                                            while (b)
                                                 b = ((BoolValue) self.parse(resultCode.toString()).value).get();
                                       }
                                       if (parsedLeft instanceof InstanceValue) {
                                            var values = ((InstanceValue) parsedLeft).get();
                                            values.forEach((String key, Operable o) -> {
                                                 try {
                                                      if (inside.length == 2) {
                                                           var result = self.parse(resultCode.toString(), new Value(removeWhitespaces(inside[1]), o));

                                                           self.variables.putAll(result.vars);
                                                           self.functions.putAll(result.functs);
                                                      } else {
                                                           var result = self.parse(resultCode.toString(), new Value(removeWhitespaces(inside[1]), o), new Value(removeWhitespaces(inside[2]), new TextValue(key)));

                                                           self.variables.putAll(result.vars);
                                                           self.functions.putAll(result.functs);
                                                      }
                                                 } catch (SyntaxException e) {
                                                      e.printStackTrace();
                                                 }
                                            });
                                       }
                                  } catch (SyntaxException e) {
                                       e.printStackTrace();
                                  }

                                  return new NullValue();
                             }
                     )
             ),
             Map.entry("if",
                     PixieModule.function(
                             (LineParser self) -> {
                                  try {
                                       var name = "if";
                                       var inside = self.getInsideBrackets(getNextString(0, self.line, List.of(name))[0].length() + name.length(), self.line);

                                       StringBuilder resultCode = new StringBuilder();
                                       int end = self.getEnd(self.line_index, self.lines);
                                       for (int i = self.line_index + 1; i < end; i++)
                                            resultCode.append(self.lines[i]).append('\n');
                                       self.setLine(end);


                                       var value = self.parseValue("", inside).value.get();
                                       if (value instanceof Boolean) {
                                            if ((Boolean) value) {
                                                 var result = self.parse(resultCode.toString());

                                                 self.variables.putAll(result.vars);
                                                 self.functions.putAll(result.functs);
                                            }

                                            if (self.line_index + 1 != self.lines.length) {
                                                 if (removeWhitespaces(self.lines[self.line_index + 1]).startsWith("elif")) {
                                                      self.setLine(self.line_index + 1);
                                                      self.reactElif((Boolean) value);
                                                 }
                                                 if (removeWhitespaces(self.lines[self.line_index + 1]).startsWith("else")) {
                                                      self.setLine(self.line_index + 1);
                                                      self.reactElse((Boolean) value);
                                                 }
                                            }
                                       }
                                  } catch (SyntaxException e) {
                                       e.printStackTrace();
                                  }

                                  return new NullValue();
                             }
                     )
             )
     ));

     private final String code;
     private String[] lines;
     public String line;
     public int line_index = 0;
     private int position = 0;

     public void setLine(int line_index) {
          SyntaxException.line = line_index;
          this.line_index = line_index;
     }

     public void reactElif(boolean last) throws SyntaxException {
          var name = "elif";
          var inside = getInsideBrackets(getNextString(0, line, List.of(name))[0].length() + name.length(), line);

          StringBuilder resultCode = new StringBuilder();
          int end = getEnd(line_index, lines, 1);
          for (int i = line_index + 1; i < end; i++)
               resultCode.append(lines[i]).append('\n');
          setLine(end);


          var value = parseValue("", inside).value.get();
          if (value instanceof Boolean) {
               if (!last && (Boolean) value) {
                    var result = parse(resultCode.toString());

                    variables.putAll(result.vars);
                    functions.putAll(result.functs);
               }

               if (line_index + 1 != lines.length) {
                    if (removeWhitespaces(lines[line_index + 1]).startsWith("elif")) {
                         setLine(line_index + 1);
                         reactElif((Boolean) value);
                    }
                    if (removeWhitespaces(lines[line_index + 1]).startsWith("else")) {
                         setLine(line_index + 1);
                         reactElse((Boolean) value);
                    }
               }
          }
     }

     public void reactElse(boolean last) throws SyntaxException {
          StringBuilder resultCode = new StringBuilder();
          int end = getEnd(line_index, lines, 0);
          for (int i = line_index + 1; i < end; i++)
               resultCode.append(lines[i]).append('\n');
          setLine(end);


          if (!last) {
               var result = parse(resultCode.toString());

               variables.putAll(result.vars);
               functions.putAll(result.functs);
          }
     }

     public LineParser(String code) {
          this.code = code;
     }

     public boolean haveFunction(String value) {
          return functions.containsKey(value);
     }

     public boolean haveVariable(String value) {
          return variables.containsKey(value);
     }

     public int getEnd(int from, String[] where) throws SyntaxException {
          return getEnd(from, where, 0);
     }

     public int getEnd(int from, String[] where, int inited) throws SyntaxException {
          return getEnd(from, where, "do", "end", inited, Integer.MAX_VALUE);
     }

     public int getEnd(int from, String[] where, int inited, int max) throws SyntaxException {
          return getEnd(from, where, "do", "end", inited, max);
     }

     public int getEnd(int from, String[] where, String starter, String ender) throws SyntaxException {
          return getEnd(from, where, starter, ender, 0, Integer.MAX_VALUE);
     }

     public int getEnd(int from, String[] where, String starter, String ender, int inited, int max) throws SyntaxException {
          for (int i = from; i < where.length; i++) {
               if (removeWhitespaces(where[i]).endsWith(starter) || removeWhitespaces(where[i]).startsWith(starter)) {
                    inited++;
                    if (inited > max) inited--;
               }
               if (removeWhitespaces(where[i]).endsWith(ender) || removeWhitespaces(where[i]).startsWith(ender)) {
                    inited--;
               }
               if (inited == 0) {
                    return i;
               }
          }

          throw new SyntaxException("Missing end '" + ender + "'", from);
     }

     public static Value parseBaseValue(String name, String line) {
          Operable value;
          try {
               value = new NumValue(Float.parseFloat(line));
          } catch (Exception ignored) {
               if (removeWhitespaces(line).equalsIgnoreCase("true") || removeWhitespaces(line).equalsIgnoreCase("false"))
                    value = new BoolValue(removeWhitespaces(line).equalsIgnoreCase("true"));
               else value = new TextValue(line);
          }

          return new Value(name, value);
     }

     public Value parseValue(String name, String value) throws SyntaxException {
          var operators = List.of("&&", "||");

          boolean haveOperation = false;
          for (var s : operators) {
               if (value.contains(s)) {
                    haveOperation = true;
                    break;
               }
          }

          if (haveOperation) {
               var adds = getNextString(0, value, operators);


               switch (adds[2]) {
                    case "&&":
                         return new Value(name, parseValue(name, adds[0]).value.and(parseValue(name, adds[1]).value));
                    case "||":
                         return new Value(name, parseValue(name, adds[0]).value.or(parseValue(name, adds[1]).value));
               }
          }

          operators = List.of(">=", ">", "<=", "<", "==", "!=");

          haveOperation = false;
          for (var s : operators) {
               if (value.contains(s)) {
                    haveOperation = true;
                    break;
               }
          }

          if (haveOperation) {
               var adds = getNextString(0, value, operators);

               switch (adds[2]) {
                    case ">":
                         return new Value(name, parseValue(name, adds[0]).value.more(parseValue(name, adds[1]).value));
                    case ">=":
                         return new Value(name, parseValue(name, adds[0]).value.moreEqu(parseValue(name, adds[1]).value));
                    case "<":
                         return new Value(name, parseValue(name, adds[0]).value.less(parseValue(name, adds[1]).value));
                    case "<=":
                         return new Value(name, parseValue(name, adds[0]).value.lessEqu(parseValue(name, adds[1]).value));
                    case "==":
                         return new Value(name, parseValue(name, adds[0]).value.equals(parseValue(name, adds[1]).value));
                    case "!=":
                         return new Value(name, parseValue(name, adds[0]).value.notEquals(parseValue(name, adds[1]).value));
               }
          }

          operators = List.of("**");

          haveOperation = false;
          for (var s : operators) {
               if (value.contains(s)) {
                    haveOperation = true;
                    break;
               }
          }

          if (haveOperation) {
               var adds = getNextString(0, value, operators);

               if ("**".equals(adds[2]))
                    return new Value(name, parseValue(name, adds[0]).value.pow(parseValue(name, adds[1]).value));
          }

          operators = List.of("+", "-", "*", "/");

          haveOperation = false;
          for (var s : operators) {
               if (value.contains(s)) {
                    haveOperation = true;
                    break;
               }
          }

          if (haveOperation) {
               var adds = getNextString(0, value, operators);

               switch (adds[2]) {
                    case "+":
                         return new Value(name, parseValue(name, adds[0]).value.add(parseValue(name, adds[1]).value));
                    case "-":
                         return new Value(name, parseValue(name, adds[0]).value.sub(parseValue(name, adds[1]).value));
                    case "*":
                         return new Value(name, parseValue(name, adds[0]).value.mul(parseValue(name, adds[1]).value));
                    case "/":
                         return new Value(name, parseValue(name, adds[0]).value.div(parseValue(name, adds[1]).value));
               }
          }

          if (removeWhitespaces(value).startsWith("'")) {
               var inside = getInside(0, value, '\'', '\'');
               return new Value(name, new TextValue(inside
                       .replace("\\\\", "\\")
                       .replace("\\n", "\n")
                       .replace("\\.", ",")));
          }

          if (removeWhitespaces(value).startsWith("init")) {
               var inst = new InstanceValue();

               var inside = getInsideBrackets(getNextString(0, value, List.of("init"))[0].length() + "init".length(), value);
               for (var i : inside.split(",")) {
                    var _var = i.split(":");

                    var parsed = parseValue(removeWhitespaces(_var[0]), _var[1]);
                    inst.value.put(parsed.name, parsed.value);
               }

               return new Value(name, inst);
          }

          if (value.contains("(")) {
               var current = getNextString(0, removeWhitespaces(value), List.of("("))[0];
               if (haveVariable(current)) {
                    var variable = new Value(name, variables.get(current));
                    if (variable.value instanceof InstanceValue) {
                         var inside = getInsideBrackets(getNextString(0, value, List.of(current))[0].length() + current.length(), value);

                         variable.value = ((InstanceValue) variable.value).get().get(removeWhitespaces(inside));
                    }
                    return variable;
               }
               if (haveFunction(current)) {
                    var fun = functions.get(current);
                    if (!fun.code.code.equals("")) return new Value(name, parse(current, fun.code.code).value);
                    else return new Value(name, fun.code.lamda.apply(this));
               }
          } else {
               var current = removeWhitespaces(value);
               if (haveVariable(current)) {
                    return new Value(name, variables.get(current));
               }
               if (haveFunction(current)) {
                    return new Value(name, new FunctionValue(functions.get(current)));
               }
          }

          var nsValue = removeWhitespaces(value);
          if (nsValue.startsWith("!")) return new Value(name, parseBaseValue(name, nsValue.substring(1)).value.inv());
          else return parseBaseValue(name, nsValue);
     }

     public static String removeWhitespaces(String str) {
          return str.replaceAll(" ", "").replaceAll("\n", "");
     }

     private List<Character> charOpeartors = List.of(
             '(', ')', '[', ']', ':', ','
     );

     public String[] getNextString(int position, String line, List<String> operators) {
          var resultArg = "";
          for (int i = position; i < line.length(); i++) {
               var breakMain = false;
               for (var operator : operators) {
                    if (position + operator.length() >= line.length()) continue;
                    var arg = line.substring(position, position + operator.length());
                    if (operator.equals(arg)) {
                         resultArg = arg;
                         breakMain = true;
                         break;
                    }
               }
               if (breakMain) break;
               position++;
          }

          return new String[]{
                  line.substring(0, Math.max(position, 0)),
                  line.substring(Math.min(Math.max(position + resultArg.length(), 0), line.length() - 1)),
                  resultArg
          };
     }

     public String getInsideBrackets(int position, String line) throws SyntaxException {
          return getInside(position, line, '(', ')');
     }

     public String getInside(int position, String line, char startChar, char endChar) throws SyntaxException {
          int i;
          int start = -1;
          for (i = position; i < line.length(); i++) {
               if (line.charAt(i) == startChar) {
                    start = i + 1;
                    break;
               }
          }
          if (start == -1) throw new SyntaxException("Waiting for starter '" + startChar + "'");

          int end = -1;
          for (i = start; i < line.length(); i++) {
               if (line.charAt(i) == endChar) {
                    end = i;
                    break;
               }
          }
          if (end == -1) throw new SyntaxException("Waiting for ender '" + endChar + "'");

          return line.substring(start, end);
     }

     public String getInsideBracketsMove(int position, String line) throws SyntaxException {
          int i;
          int start = -1;
          for (i = position; i < line.length(); i++) {
               if (line.charAt(i) == '(') {
                    start = i + 1;
                    break;
               }
          }
          if (start == -1) throw new SyntaxException("Waiting for '('");

          int end = -1;
          for (i = position; i < line.length(); i++) {
               if (line.charAt(i) == ')') {
                    end = i;
                    break;
               }
          }
          if (end == -1) throw new SyntaxException("Waiting for ')'");

          this.position = end + 1;
          return line.substring(start, end);
     }

     public String next() {
          boolean startInited = false;
          int startPosition = 0;
          for (int i = position; i < line.length(); i++) {
               var arg = line.charAt(i);
               if (charOpeartors.contains(arg)) {
                    if (!startInited) {
                         position++;
                         return "" + arg;
                    } else break;
               }
               if (!startInited) {
                    if (arg != ' ') {
                         startPosition = position;
                         startInited = true;
                    }
               } else if (arg == ' ')
                    break;
               position++;
          }

          return line.substring(startPosition, Math.max(position, 0));
     }

     public String get_next(int position) {
          boolean startInited = false;
          int startPosition = 0;
          for (int i = position; i < line.length(); i++) {
               var arg = line.charAt(i);
               if (charOpeartors.contains(arg)) {
                    if (!startInited) {
                         return "" + arg;
                    } else break;
               }
               if (!startInited) {
                    if (arg != ' ') {
                         startPosition = position;
                         startInited = true;
                    }
               } else if (arg == ' ')
                    break;
               position++;
          }

          return line.substring(startPosition, Math.max(position, 0));
     }

     public String getAllNext() {
          return line.substring(position);
     }

     public void parse() throws SyntaxException {
          lines = code.split("\n");
          for (line_index = 0; line_index < lines.length; line_index++) {
               SyntaxException.line = line_index;
               line = lines[line_index];
               position = 0;

               var token = removeWhitespaces(next());

               if (token.startsWith("#*")) {
                    int end = getEnd(line_index, lines, "#*", "*#");

                    setLine(end);

                    continue;
               }

               if (token.startsWith("#")) {
                    continue;
               }

               if (token.equals("import")) {
                    var value = getAllNext();
                    for (String module : removeWhitespaces(value).split(",")) {
                         if (modules.containsKey(module)) {
                              var parsed = modules.get(module);

                              variables.putAll(parsed.variables);
                              functions.putAll(parsed.functions);
                         } else throw new SyntaxException("Cannot import " + module);
                    }

                    continue;
               }

               if (token.equals("var")) {
                    var name = next();
                    next();
                    var value = getAllNext();
                    var parsed = parseValue(name, value);
                    variables.put(parsed.name, parsed.value);

                    continue;
               }

               if (token.equals("print")) {
                    var inside = getInsideBrackets(getNextString(0, line, List.of("print"))[0].length() + "print".length(), line);
                    var parsed = parseValue("print", inside);

                    System.out.println(parsed.value.get().toString());

                    continue;
               }

               if (token.equals("def")) {
                    var name = next();
                    var inside = getInsideBrackets(getNextString(0, line, List.of(name))[0].length() + name.length(), line);

                    StringBuilder resultCode = new StringBuilder();
                    int end = getEnd(line_index, lines);
                    for (int i = line_index + 1; i < end; i++)
                         resultCode.append(lines[i]).append('\n');

                    setLine(end);
                    functions.put(name, new Function(inside.split(","), new Function.Code(resultCode.toString(), i -> new NullValue())));

                    continue;
               }

               if (token.equals("return")) {
                    var inside = getInsideBrackets(getNextString(0, line, List.of("return"))[0].length() + "return".length(), line);

                    var parsed = parseValue("return", inside);
                    variables.put(parsed.name, parsed.value);

                    break;
               }

               if (haveFunction(removeWhitespaces(token))) {
                    var fun = functions.get(removeWhitespaces(token));
                    if (!fun.code.code.equals("")) parse(token, fun.code.code);
                    else fun.code.lamda.apply(this);
               }
          }
     }

     public Result parse(String name, String code) throws SyntaxException {
          var parser = new LineParser(code);

          var inside = getInsideBrackets(getNextString(0, line, List.of(name))[0].length() + name.length(), line).split(",");
          int val = 0;
          var funct = functions.get(removeWhitespaces(name));
          for (var i : inside) {
               var parsed = parseValue(removeWhitespaces(funct.arguments[val]), i);
               parser.variables.put(parsed.name, parsed.value);
               val++;
          }
          parser.functions.putAll(functions);
          parser.parse();
          return new Result(parser.variables, parser.functions, parser.variables.get("return"));
     }

     public Result parse(String code, Value... values) throws SyntaxException {
          var parser = new LineParser(code);

          parser.variables.putAll(variables);
          for (var value : values) parser.variables.put(value.name, value.value);
          parser.functions.putAll(functions);
          parser.parse();
          return new Result(parser.variables, parser.functions, parser.variables.get("return"));
     }

     public static class Value {
          public String name;
          public Operable value;

          public Value(String name, Operable value) {
               this.name = name;
               this.value = value;
          }
     }

     public static class Result {
          public Map<String, Operable> vars;
          public Map<String, Function> functs;
          public Operable value;

          public Result(Map<String, Operable> vars, Map<String, Function> functs, Operable value) {
               this.vars = vars;
               this.functs = functs;
               this.value = value;
          }
     }
}

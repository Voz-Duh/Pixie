package pixie.parser;

import pixie.parser.modules.FileModule;
import pixie.parser.modules.MathModule;
import pixie.parser.modules.PixieModule;
import pixie.parser.modules.SocketModule;
import pixie.parser.values.*;

import java.util.*;

public class LineParser {
     public int executorLine;
     public final static String executableType = "px";
     public final static Scanner scaner = new Scanner(System.in);
     public final Map<String, ClassConstructor> classes = new HashMap<>();

     public static <K, V> Map<K, V> ofEntries(Map.Entry<K, V>... entries) {
          Map<K, V> map = new HashMap<>();
          for (Map.Entry<K, V> entry : entries) {
               map.put(entry.getKey(), entry.getValue());
          }
          return map;
     }

     public static <V> List<V> listOf(V... values) {
          List<V> list = new ArrayList<>();
          for (V entry : values) {
               list.add(entry);
          }
          return list;
     }

     public static <K, V> Map.Entry entry(K key, V value) {
          return new Entry(key, value);
     }

     public static class Entry<K, V> implements Map.Entry<K, V> {
          V value;
          K key;

          public Entry(K key, V value) {
               this.key = key;
               this.value = value;
          }

          @Override
          public K getKey() {
               return key;
          }

          @Override
          public V getValue() {
               return value;
          }

          @Override
          public V setValue(V value) {
               this.value = value;
               return value;
          }
     }

     public final static Map<String, PixieModule> modules = new HashMap<>(ofEntries(
             entry("math", new MathModule()),
             entry("files", new FileModule()),
             entry("sockets", new SocketModule())
     ));
     public final Map<String, Class> values = new HashMap<>(ofEntries(
             entry("number", NumValue.class),
             entry("instance", InstanceValue.class),
             entry("bool", BoolValue.class),
             entry("text", TextValue.class),
             entry("null", NullValue.class),
             entry("lambda", FunctionValue.class)
     ));
     public final Map<String, Operable> variables = new HashMap<>();
     public final Map<String, Function> functions = new HashMap<>(ofEntries(
             entry("for",
                     PixieModule.function(
                             (LineParser self) -> {
                                  try {
                                       String name = "for";
                                       String[] inside = self.getInsideBrackets(getNextString(0, self.line, listOf(name))[0].length() + name.length(), self.line).split(",");

                                       StringBuilder resultCode = new StringBuilder();
                                       int end = self.getEnd(self.lineIndex, self.lines);
                                       for (int i = self.lineIndex + 1; i < end; i++)
                                            resultCode.append(self.lines[i]).append('\n');
                                       self.setLine(end);

                                       Operable parsedLeft = self.parseValue("", inside[0]).value;
                                       if (parsedLeft instanceof NumValue) {
                                            String operator = removeWhitespaces(inside[2]);
                                            Object parsedRight = self.parseValue("", inside[3]).value.get(self);
                                            if (parsedRight instanceof Float) {
                                                 float move = (Float) self.parseValue("", inside[4]).value.get(self);
                                                 for (float i = (Float) parsedLeft.get(self); (
                                                         operator.equals("<") ? i < (Float) parsedRight :
                                                                 (operator.equals(">") ? i > (Float) parsedRight :
                                                                         (operator.equals(">=") ? i >= (Float) parsedRight :
                                                                                 i <= (Float) parsedRight))
                                                 ); i += move)
                                                      self.parse(resultCode.toString(), new Value(removeWhitespaces(inside[1]), new NumValue(i)));
                                            }
                                       }
                                       if (parsedLeft instanceof BoolValue) {
                                            boolean b = ((BoolValue) parsedLeft).get(self);
                                            while (b)
                                                 b = ((BoolValue) self.parse(resultCode.toString()).value).get(self);
                                       }
                                       if (parsedLeft instanceof InstanceValue) {
                                            Map<String, Operable> values = ((InstanceValue) parsedLeft).get(self);
                                            values.forEach((String key, Operable o) -> {
                                                 try {
                                                      if (inside.length == 2) {
                                                           Result result = self.parse(resultCode.toString(), new Value(removeWhitespaces(inside[1]), o));

                                                           self.variables.putAll(result.vars);
                                                           self.functions.putAll(result.functs);
                                                      } else {
                                                           Result result = self.parse(resultCode.toString(), new Value(removeWhitespaces(inside[1]), o), new Value(removeWhitespaces(inside[2]), new TextValue(key)));

                                                           self.variables.putAll(result.vars);
                                                           self.functions.putAll(result.functs);
                                                      }
                                                 } catch (SyntaxException e) {
                                                      new SyntaxException(e.getMessage()).printStackTrace();
                                                 }
                                            });
                                       }
                                       if (parsedLeft instanceof ListValue) {
                                            int value = 0;
                                            for (Operable item : ((ListValue) parsedLeft).value) {
                                                 Result result;
                                                 if (inside.length == 2)
                                                      result = self.parse(resultCode.toString(), new Value(removeWhitespaces(inside[1]), item));
                                                 else
                                                      result = self.parse(resultCode.toString(), new Value(removeWhitespaces(inside[1]), item), new Value(removeWhitespaces(inside[2]), new NumValue(value)));

                                                 self.variables.putAll(result.vars);
                                                 self.functions.putAll(result.functs);
                                                 value++;
                                            }
                                       }
                                  } catch (SyntaxException e) {
                                       e.printStackTrace();
                                  }

                                  return new NullValue();
                             }
                     )
             ),
             entry("if",
                     PixieModule.function(
                             (LineParser self) -> {
                                  try {
                                       String name = "if";
                                       String inside = self.getInsideBrackets(getNextString(0, self.line, listOf(name))[0].length() + name.length(), self.line);

                                       StringBuilder resultCode = new StringBuilder();
                                       int end = self.getEnd(self.lineIndex, self.lines);
                                       for (int i = self.lineIndex + 1; i < end; i++)
                                            resultCode.append(self.lines[i]).append('\n');
                                       self.setLine(end);


                                       Object value = self.parseValue("", inside).value.get(self);
                                       if (value instanceof Boolean) {
                                            if ((Boolean) value) {
                                                 Result result = self.parse(resultCode.toString());

                                                 self.variables.putAll(result.vars);
                                                 self.functions.putAll(result.functs);
                                            }

                                            if (self.lineIndex + 1 != self.lines.length) {
                                                 if (removeWhitespaces(self.lines[self.lineIndex + 1]).startsWith("elif")) {
                                                      self.setLine(self.lineIndex + 1);
                                                      reactElif((Boolean) value, self);
                                                 }
                                                 if (removeWhitespaces(self.lines[self.lineIndex + 1]).startsWith("else")) {
                                                      self.setLine(self.lineIndex + 1);
                                                      self.reactElse((Boolean) value);
                                                 }
                                            }
                                       } else throw new SyntaxException("Not bool value provided to if");
                                  } catch (SyntaxException e) {
                                       new Exception(e.getMessage()).printStackTrace();
                                  }

                                  return new NullValue();
                             }
                     )
             ),
             entry("class",
                     PixieModule.function(
                             (LineParser self) -> {
                                  try {
                                       String name = next();

                                       StringBuilder resultCode = new StringBuilder();
                                       int end = self.getEnd(self.lineIndex, self.lines);
                                       for (int i = self.lineIndex + 1; i < end; i++)
                                            resultCode.append(self.lines[i]).append('\n');
                                       self.setLine(end);


                                       ClassConstructor result = self.parseClass(name, resultCode.toString());
                                       self.classes.put(name, result);

                                       //self.variables.putAll(result.vars);
                                       //self.functions.putAll(result.functs);
                                  } catch (SyntaxException e) {
                                       new SyntaxException(e.getMessage()).printStackTrace();
                                  }

                                  return new NullValue();
                             }
                     )
             ),
             entry(
                     "input",
                     PixieModule.function(
                             (LineParser self) -> {
                                  try {
                                       String name = "input";
                                       String inside = self.getInsideBrackets(getNextString(0, self.line, listOf(name))[0].length() + name.length(), self.line);

                                       System.out.print(parseValue("", inside).value.get(self).toString());

                                       return new TextValue(scaner.next());
                                  } catch (SyntaxException e) {
                                       new SyntaxException(e.getMessage()).printStackTrace();
                                  }

                                  return new NullValue();
                             }
                     )
             )
     ));

     protected final String code;
     protected String[] lines;
     public String line;
     public int lineIndex = 0;
     protected int position = 0;

     public LineParser(String code, int executorLine) {
          this.code = code;
          this.executorLine = executorLine;
     }

     public void setLine(int lineIndex) {
          SyntaxException.line = lineIndex;
          this.lineIndex = lineIndex;
          this.line = lines[lineIndex];
     }

     public static void reactElif(boolean last, LineParser self) throws SyntaxException {
          String name = "elif";
          String inside = self.getInsideBrackets(getNextString(0, self.line, listOf(name))[0].length() + name.length(), self.line);

          StringBuilder resultCode = new StringBuilder();
          int end = self.getEnd(self.lineIndex, self.lines);
          for (int i = self.lineIndex + 1; i < end; i++)
               resultCode.append(self.lines[i]).append('\n');
          self.setLine(end);


          Object value = self.parseValue("", inside).value.get(self);
          if (value instanceof Boolean) {
               if (!last && (Boolean) value) {
                    Result result = self.parse(resultCode.toString());

                    self.variables.putAll(result.vars);
                    self.functions.putAll(result.functs);
               }

               if (self.lineIndex + 1 != self.lines.length) {
                    if (removeWhitespaces(self.lines[self.lineIndex + 1]).startsWith("elif")) {
                         self.setLine(self.lineIndex + 1);
                         reactElif((Boolean) value, self);
                    }
                    if (removeWhitespaces(self.lines[self.lineIndex + 1]).startsWith("else")) {
                         self.setLine(self.lineIndex + 1);
                         self.reactElse((Boolean) value);
                    }
               }
          } else throw new SyntaxException("Not bool value provided to elif");
     }

     public void reactElse(boolean last) throws SyntaxException {
          StringBuilder resultCode = new StringBuilder();
          int end = getEnd(lineIndex, lines, 0);
          for (int i = lineIndex + 1; i < end; i++)
               resultCode.append(lines[i]).append('\n');
          setLine(end);


          if (!last) {
               Result result = parse(resultCode.toString());

               variables.putAll(result.vars);
               functions.putAll(result.functs);
          }
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
          return getEnd(from, where, "{", "}", inited, Integer.MAX_VALUE);
     }

     public int getEnd(int from, String[] where, int inited, int max) throws SyntaxException {
          return getEnd(from, where, "{", "}", inited, max);
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

     public Value parseBaseValue(String name, String line) {
          Operable value;
          try {
               //if (removeWhitespaces(line).startsWith("-")) value = new NumValue(-Float.parseFloat(line.substring(1)));
               value = new NumValue(Float.parseFloat(line));
          } catch (Exception ignored) {
               if (removeWhitespaces(line).equalsIgnoreCase("true") || removeWhitespaces(line).equalsIgnoreCase("false"))
                    value = new BoolValue(removeWhitespaces(line).equalsIgnoreCase("true"));
               else value = new TextValue(line);
          }

          return new Value(name, value);
     }

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

     public static String removeWhitespaces(String str) {
          return str.replaceAll(" ", "").replaceAll("\n", "");
     }

     private static final List<Character> splitIgnore = listOf(
             '(', ')', '[', ']', ':', '{', '}'
     );

     public String[] split(String string, char split) {
          Queue<String> result = new ArrayDeque<>();

          boolean add = true;
          boolean lastIsBack = false;
          boolean isString = false;
          int last = 0;
          for (int i = 0; i < string.length(); i++) {
               if (!lastIsBack && string.charAt(i) == '\'') isString = !isString;

               if (!isString) {
                    if (splitIgnore.contains(string.charAt(i))) {
                         add = !add;
                         if (add) last = i + 1;
                    }

                    if (string.charAt(i) == split) {
                         if (add) result.add(string.substring(last, i));
                         last = i + 1;
                    }
               }

               lastIsBack = line.charAt(i) == '\\';
          }

          result.add(string.substring(last));

          return result.toArray(new String[0]);
     }

     private final List<Character> charOperators = listOf(
             '(', ')', '[', ']', ':', ',', '{', '}'
     );

     public static String[] getNextString(int position, String line, List<CharSequence> operators) {
          String resultArg = "";
          for (int i = position; i < line.length(); i++) {
               boolean breakMain = false;
               for (CharSequence operator : operators) {
                    if (position + operator.length() >= line.length()) continue;
                    String arg = line.substring(position, position + operator.length());
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

     public String getString(int position, String line) throws SyntaxException {
          int i;
          int start = -1;
          boolean lastIsBack = false;
          for (i = position; i < line.length(); i++) {
               if (!lastIsBack && line.charAt(i) == '\'') {
                    start = i + 1;
                    break;
               }

               lastIsBack = line.charAt(i) == '\\';
          }
          if (start == -1) throw new SyntaxException("Waiting for starter '''");

          int end = -1;
          lastIsBack = false;
          for (i = start; i < line.length(); i++) {
               if (!lastIsBack && line.charAt(i) == '\'') {
                    end = i;
                    break;
               }

               lastIsBack = line.charAt(i) == '\\';
          }
          if (end == -1) throw new SyntaxException("Waiting for ender '''");

          return line.substring(start, end);
     }

     public String getInsideBracketsFixed(int position, String line) throws SyntaxException {
          int i;
          int start = -1;
          boolean lastIsBack = false;
          boolean inString = false;
          for (i = position; i < line.length(); i++) {
               if (!lastIsBack && line.charAt(i) == '\'') inString = !inString;

               if (!inString && line.charAt(i) == '(') {
                    start = i + 1;
                    break;
               }

               lastIsBack = line.charAt(i) == '\\';
          }
          if (start == -1) throw new SyntaxException("Waiting for starter '('");

          lastIsBack = false;
          inString = false;
          int end = -1;
          for (i = start; i < line.length(); i++) {
               if (!lastIsBack && line.charAt(i) == '\'') inString = !inString;

               if (!inString && line.charAt(i) == ')') {
                    end = i;
                    break;
               }

               lastIsBack = line.charAt(i) == '\\';
          }
          if (end == -1) throw new SyntaxException("Waiting for ender ')'");

          return line.substring(start, end);
     }

     public String getInside(int position, String line, char startChar, char endChar) throws SyntaxException {
          int i;
          int sides = 0;
          int start = -1;
          int end = -1;
          boolean lastIsBack = false;
          boolean inString = false;
          for (i = position; i < line.length(); i++) {
               if (!lastIsBack && line.charAt(i) == '\'') inString = !inString;

               if (!inString && line.charAt(i) == startChar) {
                    if (start == -1) start = i + 1;
                    sides++;
               }

               lastIsBack = line.charAt(i) == '\\';
          }
          if (start == -1) throw new SyntaxException("Waiting for starter '" + startChar + "'");

          lastIsBack = false;
          inString = false;
          for (i = position; i < line.length(); i++) {
               if (!lastIsBack && line.charAt(i) == '\'') inString = !inString;

               if (!inString && line.charAt(i) == endChar) {
                    sides--;
                    if (sides == 0) {
                         end = i;
                         break;
                    }
               }

               lastIsBack = line.charAt(i) == '\\';
          }
          if (end == -1) throw new SyntaxException("Waiting for ender '" + endChar + "'");

          return line.substring(start, end);
     }

     public String next() {
          boolean startInited = false;
          int startPosition = 0;
          for (int i = position; i < line.length(); i++) {
               char arg = line.charAt(i);
               if (charOperators.contains(arg)) {
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

     public String getAllNext() {
          return line.substring(position);
     }

     public void parse() throws SyntaxException {
          lines = code.split("\n");
          for (lineIndex = 0; lineIndex < lines.length; lineIndex++) {
               SyntaxException.line = lineIndex;
               line = lines[lineIndex];
               position = 0;

               String token = removeWhitespaces(next());

               if (token.startsWith("#*")) {
                    int end = getEnd(lineIndex, lines, "#*", "*#");

                    setLine(end);

                    continue;
               }

               if (token.startsWith("#")) {
                    continue;
               }

               if (token.equals("import")) {
                    String value = getAllNext();
                    for (String module : removeWhitespaces(value).split(",")) {
                         if (modules.containsKey(module)) {
                              PixieModule parsed = modules.get(module);

                              variables.putAll(parsed.variables);
                              functions.putAll(parsed.functions);
                              values.putAll(parsed.values);
                         } else throw new SyntaxException("Cannot import " + module);
                    }

                    continue;
               }

               if (token.equals("var")) {
                    String name = next();
                    next();
                    String value = getAllNext();
                    Value parsed = parseValue(name, value);
                    variables.put(parsed.name, parsed.value);

                    continue;
               }

               if (token.equals("print")) {
                    String inside = getInsideBrackets(getNextString(0, line, listOf("print"))[0].length() + "print".length(), line);
                    Value parsed = parseValue("print", inside);

                    System.out.println(parsed.value.get(this).toString());

                    continue;
               }

               if (token.equals("def")) {
                    String name = next();
                    String inside = getInsideBrackets(getNextString(0, line, listOf(name))[0].length() + name.length(), line);

                    StringBuilder resultCode = new StringBuilder();
                    int end = getEnd(lineIndex, lines);
                    for (int i = lineIndex + 1; i < end; i++)
                         resultCode.append(lines[i]).append('\n');

                    setLine(end);
                    functions.put(name, new Function(inside.split(","), new Function.Code(resultCode.toString(), i -> new NullValue())));

                    continue;
               }

               if (token.equals("return")) {
                    String inside = getAllNext();

                    Value parsed = parseValue("return", inside);
                    variables.put(parsed.name, parsed.value);

                    break;
               }

               if (haveFunction(token)) {
                    Function fun = functions.get(token);
                    if (!fun.code.code.equals("")) parse(token, fun.code.code);
                    else fun.code.lamda.apply(this);

                    continue;
               }

               if (haveVariable(token)) {
                    String operator = next();
                    String isList = "";
                    if (operator.equals("[")) {
                         isList = next();
                         next();
                         operator = next();
                    }
                    String value = getAllNext();
                    Value parsed = parseValue(token, value);
                    Operable last = variables.get(token);
                    if (isList.equals("")) {
                         switch (operator) {
                              case "=":
                                   variables.put(parsed.name, parsed.value);
                                   break;
                              case "+=":
                                   variables.put(parsed.name, last.add(parsed.value, this));
                                   break;
                              case "-=":
                                   variables.put(parsed.name, last.sub(parsed.value, this));
                                   break;
                              case "*=":
                                   variables.put(parsed.name, last.mul(parsed.value, this));
                                   break;
                              case "/=":
                                   variables.put(parsed.name, last.div(parsed.value, this));
                                   break;
                              case "===":
                                   variables.put(parsed.name, last.equals(parsed.value, this));
                                   break;
                              case "!==":
                                   variables.put(parsed.name, last.notEquals(parsed.value, this));
                                   break;
                              case "!=":
                                   variables.put(parsed.name, parsed.value.inv(this));
                                   break;
                              case ">=":
                                   variables.put(parsed.name, last.less(parsed.value, this));
                                   break;
                              case "<=":
                                   variables.put(parsed.name, last.more(parsed.value, this));
                                   break;
                              case "<==":
                                   variables.put(parsed.name, last.moreEqu(parsed.value, this));
                                   break;
                              case ">==":
                                   variables.put(parsed.name, last.lessEqu(parsed.value, this));
                                   break;
                              case "&=":
                                   variables.put(parsed.name, last.and(parsed.value, this));
                                   break;
                              case "|=":
                                   variables.put(parsed.name, last.or(parsed.value, this));
                                   break;
                              case "**=":
                                   variables.put(parsed.name, last.pow(parsed.value, this));
                                   break;
                              default:
                                   throw new SyntaxException("Cannot execute " + operator + " operator");
                         }
                    } else if (last instanceof ListValue) {
                         Value parsedGetter = parseValue("", isList);
                         if (!(parsedGetter.value instanceof NumValue))
                              throw new SyntaxException("List getter is not number");

                         int getter = ((Float) parsedGetter.value.get(this)).intValue();
                         List<Operable> list = ((ListValue) last).get(this);

                         if (getter >= list.size())
                              throw new SyntaxException("Index " + getter + " out of bounds for length " + list.size());

                         switch (operator) {
                              case "=":
                                   list.set(getter, parsed.value);
                                   break;
                              case "+=":
                                   list.set(getter, last.add(parsed.value, this));
                                   break;
                              case "-=":
                                   list.set(getter, last.sub(parsed.value, this));
                                   break;
                              case "*=":
                                   list.set(getter, last.mul(parsed.value, this));
                                   break;
                              case "/=":
                                   list.set(getter, last.div(parsed.value, this));
                                   break;
                              case "===":
                                   list.set(getter, last.equals(parsed.value, this));
                                   break;
                              case "!==":
                                   list.set(getter, last.notEquals(parsed.value, this));
                                   break;
                              case "!=":
                                   list.set(getter, parsed.value.inv(this));
                                   break;
                              case ">=":
                                   list.set(getter, last.less(parsed.value, this));
                                   break;
                              case "<=":
                                   list.set(getter, last.more(parsed.value, this));
                                   break;
                              case "<==":
                                   list.set(getter, last.moreEqu(parsed.value, this));
                                   break;
                              case ">==":
                                   list.set(getter, last.lessEqu(parsed.value, this));
                                   break;
                              case "&=":
                                   list.set(getter, last.and(parsed.value, this));
                                   break;
                              case "|=":
                                   list.set(getter, last.or(parsed.value, this));
                                   break;
                              case "**=":
                                   list.set(getter, last.pow(parsed.value, this));
                                   break;
                              default:
                                   throw new SyntaxException("Cannot execute " + operator + " operator");
                         }
                    } else {
                         throw new SyntaxException("Using [] on not list");
                    }
               }
          }
     }

     public Result parse(String name, String code) throws SyntaxException {
          LineParser parser = new LineParser(code, lineIndex + 1 + executorLine);

          String[] inside = split(getInsideBrackets(getNextString(0, line, listOf(name))[0].length() + name.length(), line), ',');
          int val = 0;
          Function funct = functions.get(removeWhitespaces(name));
          for (String i : inside) {
               Value parsed = parseValue(removeWhitespaces(funct.arguments[val]), i);
               parser.variables.put(parsed.name, parsed.value);
               val++;
          }
          parser.functions.putAll(functions);
          parser.parse();
          return new Result(parser.variables, parser.functions, parser.variables.get("return"));
     }

     public Result parse(String code, Value... values) throws SyntaxException {
          LineParser parser = new LineParser(code, lineIndex + 1 + executorLine);

          parser.variables.putAll(variables);
          for (Value value : values) parser.variables.put(value.name, value.value);
          parser.functions.putAll(functions);
          parser.parse();
          return new Result(parser.variables, parser.functions, parser.variables.get("return"));
     }

     public Result parse(String name, String currentClass, Function function) throws SyntaxException {
          LineParser parser = new LineParser(function.code.code, lineIndex + 1 + executorLine);

          String[] inside = split(getInsideBrackets(getNextString(0, line, listOf(name))[0].length() + name.length(), line), ',');
          int val = 0;
          for (String i : inside) {
               Value parsed = parseValue(removeWhitespaces(function.arguments[val]), i);
               parser.variables.put(parsed.name, parsed.value);
               val++;
          }
          parser.functions.putAll(functions);

          parser.variables.putAll(classes.get(currentClass).staticVariables);
          parser.functions.putAll(classes.get(currentClass).staticFunctions);

          parser.parse();
          return new Result(parser.variables, parser.functions, parser.variables.get("return"));
     }

     public ClassConstructor parseClass(String className, String code) throws SyntaxException {
          ClassParser parser = new ClassParser(code, lineIndex + 1 + executorLine);

          parser.functions.putAll(functions);
          return parser.parseClass(className);
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

     public static class ClassParser extends LineParser {
          public final Map<String, Operable> staticVariables = new HashMap<>();
          public Map<String, Function> staticFunctions = new HashMap<>();

          public ClassParser(String code, int executorLine) {
               super(code, executorLine);
          }

          public ClassConstructor parseClass(String className) throws SyntaxException {
               lines = code.split("\n");
               for (lineIndex = 0; lineIndex < lines.length; lineIndex++) {
                    SyntaxException.line = lineIndex;
                    line = lines[lineIndex];
                    position = 0;

                    String token = removeWhitespaces(next());

                    if (token.startsWith("#*")) {
                         int end = getEnd(lineIndex, lines, "#*", "*#");

                         setLine(end);

                         continue;
                    }

                    if (token.startsWith("#")) {
                         continue;
                    }

                    boolean isStatic;
                    if (isStatic = token.startsWith("static")) {
                         token = next();
                    }

                    if (token.equals("import")) {
                         String value = getAllNext();
                         for (String module : removeWhitespaces(value).split(",")) {
                              if (modules.containsKey(module)) {
                                   PixieModule parsed = modules.get(module);

                                   staticVariables.putAll(parsed.variables);
                                   staticFunctions.putAll(parsed.functions);
                              } else throw new SyntaxException("Cannot import " + module);
                         }

                         continue;
                    }

                    if (token.equals("field")) {
                         String name = next();
                         next();
                         String value = getAllNext();
                         Value parsed = parseValue(name, value);

                         (isStatic ? staticVariables : variables).put(parsed.name, parsed.value);

                         continue;
                    }

                    if (token.equals("function")) {
                         String name = next();
                         String inside = getInsideBrackets(getNextString(0, line, listOf(name))[0].length() + name.length(), line);

                         StringBuilder resultCode = new StringBuilder();
                         int end = getEnd(lineIndex, lines);
                         for (int i = lineIndex + 1; i < end; i++)
                              resultCode.append(lines[i]).append('\n');
                         setLine(end);

                         (isStatic ? staticFunctions : functions).put(name, new Function(inside.split(","), new Function.Code(resultCode.toString(), i -> new NullValue())));
                    }
               }
               return new ClassConstructor(className, functions, staticFunctions, variables, staticVariables, executorLine + 1);
          }
     }
}

package pixie.parser;

import pixie.parser.modules.FileModule;
import pixie.parser.modules.MathModule;
import pixie.parser.modules.PixieModule;
import pixie.parser.modules.SocketModule;
import pixie.parser.values.*;

import java.util.*;

public class LineParser implements VariableContainer {
     public int executorLine;
     public final static String executableType = "pixie";
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
     public Map<String, Operable> variables = new HashMap<>();
     public Map<String, Function> functions = baseFunctions();

     protected final String code;
     public String line;
     public int lineIndex = 0;
     public VariableContainer instance = null;

     public LineParser(String code, int executorLine) {
          this.code = code;
          this.executorLine = executorLine;
     }

     public LineParser(String code, int executorLine, VariableContainer instance) {
          this.code = code;
          this.instance = instance;
          this.executorLine = executorLine;

          VariableContainer _instance = this.instance != null ? this.instance : this;

          variables = _instance.getVar();
          functions = _instance.getFunc();
     }

     @Override
     public Map<String, Operable> getVar() {
          return variables;
     }

     @Override
     public Map<String, Function> getFunc() {
          return functions;
     }

     public VariableContainer getInstance(String value) throws SyntaxException {
          if (value.contains(".")) {
               VariableContainer instance = this;

               String[] values = value.split("\\.");

               int i = 0;
               String start = values[0].trim();

               if (start.equals("this")) {
                    instance = this.instance;
                    i = 1;
               } else if (classes.containsKey(start)) {
                    instance = classes.get(start);
                    i = 1;
               }

               for (; i < values.length - 1; i++) {
                    String s = values[i].trim();

                    try {
                         instance = (InstanceValue) instance.getVar().get(s);
                    } catch (ClassCastException e) {
                         throw new SyntaxException(". after not instance");
                    }
               }

               return instance;
          }
          return null;
     }

     public Function function(String value, String line) throws SyntaxException {
          String[] args = split(getInsideBrackets(line), ',');

          value += args.length;

          if (value.contains(".")) {
               VariableContainer instance = this.instance != null ? this.instance : this;

               String[] values = value.split("\\.");

               int i = 0;
               String start = values[0].trim();

               if (start.equals("this")) {
                    instance = this.instance;
                    i = 1;
               } else if (classes.containsKey(start)) {
                    instance = classes.get(start);
                    i = 1;
               }

               for (; i < values.length - 1; i++) {
                    String s = values[i].trim();

                    try {
                         instance = (InstanceValue) instance.getVar().get(s);
                    } catch (ClassCastException e) {
                         throw new SyntaxException(". after not instance");
                    }
               }

               String r = values[values.length - 1];
               if (instance != null) {
                    Map<String, Function> functions = instance.getFunc();
                    if (functions.containsKey(r)) {
                         return functions.get(r);
                    }
               }
               return null;
          }
          if (functions.containsKey(value)) return functions.get(value);
          return null;
     }

     public Operable parseFunction(String value, String line, String[] words) throws SyntaxException {
          String[] args = split(getInsideBrackets(line), ',');

          value += args.length;

          if (value.contains(".")) {
               VariableContainer instance = this;

               String[] values = value.split("\\.");

               int i = 0;
               String start = values[0].trim();

               if (start.equals("this")) {
                    instance = this.instance;
                    i = 1;
               } else if (classes.containsKey(start)) {
                    instance = classes.get(start);
                    i = 1;
               }

               for (; i < values.length - 1; i++) {
                    String s = values[i].trim();

                    try {
                         instance = (InstanceValue) instance.getVar().get(s);
                    } catch (ClassCastException e) {
                         throw new SyntaxException(". after not instance");
                    }
               }

               String r = values[values.length - 1];
               if (instance != null) {
                    Map<String, Function> functions = instance.getFunc();
                    if (functions.containsKey(r)) {
                         return parseInstance(functions.get(r), line, words, instance);
                    }
               }
               return null;
          }
          if (functions.containsKey(value)) return parseInstance(functions.get(value), line, words, this);
          return null;
     }

     public Operable parseInstance(Function function, String line, String[] words, VariableContainer value) throws SyntaxException {
          if (function != null) {
               if (!function.code.code.equals("")) return parse(function, line, value).value;
               else return function.code.lamda.apply(line, words, this);
          }
          return new NullValue();
     }

     public Operable variable(String value) throws SyntaxException {
          if (value.contains(".")) {
               VariableContainer instance = this;

               String[] values = value.split("\\.");

               int i = 0;
               String start = values[0].trim();

               if (start.equals("this")) {
                    instance = this.instance;
                    i = 1;
               } else if (classes.containsKey(start)) {
                    instance = classes.get(start);
                    i = 1;
               }

               for (; i < values.length - 1; i++) {
                    String s = values[i].trim();

                    try {
                         instance = (InstanceValue) instance.getVar().get(s);
                    } catch (ClassCastException e) {
                         throw new SyntaxException(". after not instance");
                    }
               }

               String r = values[values.length - 1];
               if (instance != null) {
                    Map<String, Operable> variables = instance.getVar();
                    if (variables.containsKey(r)) {
                         return variables.get(r);
                    }
               }
               return null;
          }
          if (variables.containsKey(value)) return variables.get(value);
          return null;
     }

     public Value parseValue(String name, String value) throws SyntaxException {
          return MathParser.compute(name, value, this);
     }

     public static String removeWhitespaces(String str) {
          return str.replaceAll(" ", "").replaceAll("\n", "");
     }

     private static String getAfter(String text, String character) {
          int index = text.lastIndexOf(character);
          if (index != -1) {
               return text.substring(index + character.length());
          } else {
               return text;
          }
     }

     public static String getAfter(String text, char start, char end) {
          int level = 0;
          boolean insideQuotes = false;
          for (int i = 0; i < text.length(); i++) {
               char c = text.charAt(i);
               if (c == '\"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                    insideQuotes = !insideQuotes;
               } else {
                    if (!insideQuotes && c == start) {
                         level++;
                    } else if (!insideQuotes && c == end) {
                         level--;
                         if (level == 0) {
                              return text.substring(i + 1);
                         }
                    }
               }
          }
          return "";
     }

     public static String[] split(String text, char delimiter) {
          if (text.trim().equals("")) return new String[0];

          List<String> parts = new ArrayList<>();
          int level = 0;
          boolean insideQuotes = false;
          StringBuilder builder = new StringBuilder();
          for (int i = 0; i < text.length(); i++) {
               char c = text.charAt(i);
               if (c == '\"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                    insideQuotes = !insideQuotes;
               }
               if (!insideQuotes && level == 0 && c == delimiter) {
                    parts.add(builder.toString());
                    builder = new StringBuilder();
               } else {
                    builder.append(c);
                    if (!insideQuotes && (c == '{' || c == '[' || c == '(')) {
                         level++;
                    } else if (!insideQuotes && (c == '}' || c == ']' || c == ')')) {
                         level--;
                    }
               }
          }
          parts.add(builder.toString());
          return parts.toArray(new String[0]);
     }

     private static String getBetween(String text, char start, char end) {
          int startIndex = text.indexOf(start);

          if (startIndex == -1) return "";

          int level = 0;
          boolean insideQuotes = false;
          for (int i = startIndex; i < text.length(); i++) {
               char c = text.charAt(i);
               if (c == '\"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                    insideQuotes = !insideQuotes;
               } else {
                    if (!insideQuotes && c == start) {
                         level++;
                    } else if (!insideQuotes && c == end) {
                         level--;
                         if (level == 0) {
                              return text.substring(startIndex + 1, i);
                         }
                    }
               }
          }

          return "";
     }

     public static String getInsideChevrons(String code) {
          return getBetween(code, '<', '>');
     }

     public static String getInsideBraces(String code) {
          return getBetween(code, '{', '}');
     }

     public static String getInsideBoxBrackets(String code) {
          return getBetween(code, '[', ']');
     }

     public static String getInsideBrackets(String code) {
          return getBetween(code, '(', ')');
     }

     public static boolean token(String token, String[] words) {
          return words[0].equals(token);
     }

     public static boolean token(String token, String[] words, int i) {
          return words[i].equals(token);
     }

     public static String[] words(String line) {
          String[] words = new String[0];
          {
               List<String> wordsList = new ArrayList<>();
               for (String s : line.split("\\s+|\\w+(\\.\\w+)+|\\b+|\\{|\\}|\\(|\\)|\\[|\\]|\\<|\\>")) {
                    s = s.trim();
                    if (!s.equals("")) wordsList.add(s);
               }
               words = wordsList.toArray(words);
          }
          return words;
     }

     public static String rWord(String line, String[] words, int i) {
          return line.replaceFirst(words[i], "").trim();
     }

     public static boolean sbool(String b) {
          return !b.equals("");
     }

     public void parse() throws SyntaxException {
          String[] lines = split(code, ';');

          Boolean lb = null;
          for (String line : lines) {
               line = line.trim();

               if (line.equals("")) continue;

               String[] words = words(line);

               if (token("else", words)) {
                    if (lb == null) continue;

                    String s = getAfter(line, "else").trim();
                    String resultCode = s.startsWith("{") ? getInsideBraces(line) : s;

                    if (!lb) {
                         Result result = parse(resultCode);

                         variables.replaceAll((k, v) -> result.vars.get(k));
                    } else {
                         lb = null;
                         continue;
                    }

                    boolean elif = resultCode.startsWith("if");
                    if (elif) {
                         String l = getInsideBrackets(resultCode);
                         lb = (boolean) parseValue("", l).value.get(this);
                    }
                    continue;
               }
               lb = null;

               if (token("import", words)) {
                    line = rWord(line, words, 0);

                    for (String module : line.split(",")) {
                         module = module.trim();
                         if (modules.containsKey(module)) {
                              PixieModule parsed = modules.get(module);

                              variables.putAll(parsed.variables);
                              functions.putAll(parsed.functions);
                              values.putAll(parsed.values);
                         } else throw new SyntaxException("Cannot import " + module);
                    }

                    continue;
               }

               if (token("var", words)) {
                    line = rWord(rWord(rWord(line, words, 0), words, 1), words, 2);

                    Value parsed = parseValue(words[1], line);
                    variables.put(parsed.name, parsed.value);

                    continue;
               }

               if (token("print", words)) {
                    Value parsed = parseValue("print", getInsideBrackets(line));

                    System.out.println(parsed.value.get(this));

                    continue;
               }

               if (token("if", words)) {
                    String inside = getInsideBrackets(line);

                    String s = getAfter(line, '(', ')').trim();
                    String resultCode = s.startsWith("{") ? getInsideBraces(line) : s;

                    Object value = parseValue("", inside).value.get(this);
                    if (value instanceof Boolean) {
                         lb = (Boolean) value;
                         if (lb) {
                              Result result = parse(resultCode);

                              variables.replaceAll((k, v) -> result.vars.get(k));
                         }
                    } else throw new SyntaxException("Not bool value provided to if");

                    continue;
               }

               if (token("for", words)) {
                    String inside = getInsideBrackets(line);

                    String s = getAfter(line, '(', ')').trim();
                    String resultCode = s.startsWith("{") ? getInsideBraces(line) : s;

                    String[] args = split(inside, ';');
                    if (args.length == 4) {
                         Result result = parse(args[1]);
                         String i = args[0].trim();
                         variables.put(i, result.vars.get(i));
                         while (((BoolValue) parseValue("", args[2]).value).value) {
                              parse(resultCode);
                              result = parse(args[3]);
                              variables.put(i, result.vars.get(i));
                         }
                    } else {
                         args = split(inside, ':');

                         if (args.length == 2) {
                              for (Operable i : ((ListValue) parseValue("", args[1]).value).value) {
                                   parse(resultCode, new Value(args[0].trim(), i));
                              }
                         } else throw new SyntaxException("Current for type not valid");
                    }
                    continue;
               }

               if (token("class", words)) {
                    ClassConstructor result = parseClass(words[1], getInsideBraces(line));
                    classes.put(words[1], result);

                    continue;
               }

               if (token("def", words)) {
                    String[] inside = split(getInsideBrackets(line), ',');
                    functions.put(words[1] + inside.length, new Function(inside, new Function.Code(getInsideBraces(line), (i, j, h) -> new NullValue())));

                    continue;
               }

               if (token("return", words)) {
                    line = rWord(line, words, 0);

                    Value parsed = parseValue("return", line);
                    variables.put(parsed.name, parsed.value);

                    break;
               }

               if (parseFunction(words[0], line, words) != null) continue;

               {
                    VariableContainer container = getInstance(words[0]);
                    Map<String, Operable> variables = container != null ? container.getVar() : this.variables;

                    String name = container != null ? getAfter(words[0], ".") : words[0];

                    if (variables.containsKey(name)) {
                         Operable variable = variables.get(name);

                         line = rWord(line, words, 0);

                         String isList = getInsideBoxBrackets(line);

                         if (sbool(isList)) {
                              line = line.substring(isList.length() + 2).trim();
                         }

                         String operator;
                         if (!line.startsWith("=")) {
                              operator = line.substring(0, 2);

                              if (!operator.equals("++") && !operator.equals("--") && !operator.endsWith("=") && line.contains("(")) {
                                   parse((FunctionValue) variable, line, this);
                                   continue;
                              }

                              line = line.substring(2).trim();
                         } else {
                              operator = line.substring(0, 1);
                              line = line.substring(1).trim();
                         }

                         Value parsed = (operator.equals("++") || operator.equals("--")) ? null : parseValue(name, line);
                         if (!sbool(isList)) {
                              switch (operator) {
                                   case "=":
                                        variables.put(name, parsed.value);
                                        break;
                                   case "+=":
                                        variables.put(name, variable.add(parsed.value, this));
                                        break;
                                   case "-=":
                                        variables.put(name, variable.sub(parsed.value, this));
                                        break;
                                   case "*=":
                                        variables.put(name, variable.mul(parsed.value, this));
                                        break;
                                   case "/=":
                                        variables.put(name, variable.div(parsed.value, this));
                                        break;
                                   case "^=":
                                        variables.put(name, variable.pow(parsed.value, this));
                                        break;
                                   case "%=":
                                        variables.put(name, variable.mod(parsed.value, this));
                                        break;
                                   case "++":
                                        variables.put(name, variable.add(new NumValue(1), this));
                                        break;
                                   case "--":
                                        variables.put(name, variable.sub(new NumValue(1), this));
                                        break;
                                   default:
                                        throw new SyntaxException("Cannot execute " + operator + " operator");
                              }
                         } else if (variable instanceof ListValue) {
                              Value parsedGetter = parseValue("", isList);
                              if (!(parsedGetter.value instanceof NumValue))
                                   throw new SyntaxException("List getter is not number");

                              int getter = ((Float) parsedGetter.value.get(this)).intValue();
                              List<Operable> list = ((ListValue) variable).get(this);

                              if (getter >= list.size())
                                   throw new SyntaxException("Index " + getter + " out of bounds for length " + list.size());

                              switch (operator) {
                                   case "=":
                                        list.set(getter, parsed.value);
                                        break;
                                   case "+=":
                                        list.set(getter, variable.add(parsed.value, this));
                                        break;
                                   case "-=":
                                        list.set(getter, variable.sub(parsed.value, this));
                                        break;
                                   case "*=":
                                        list.set(getter, variable.mul(parsed.value, this));
                                        break;
                                   case "/=":
                                        list.set(getter, variable.div(parsed.value, this));
                                        break;
                                   case "^=":
                                        list.set(getter, variable.pow(parsed.value, this));
                                        break;
                                   case "%=":
                                        list.set(getter, variable.mod(parsed.value, this));
                                        break;
                                   case "++":
                                        variables.put(name, variable.add(new NumValue(1), this));
                                        break;
                                   case "--":
                                        variables.put(name, variable.sub(new NumValue(1), this));
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
     }


     public Result parse(String name, String code) throws SyntaxException {
          name = name.trim();

          LineParser parser = new LineParser(code, lineIndex + 1 + executorLine);

          String[] inside = split(getInsideBrackets(line), ',');
          int val = 0;
          Function funct = functions.get(name);
          for (String i : inside) {
               Value parsed = parseValue(removeWhitespaces(funct.arguments[val]), i);
               parser.variables.put(parsed.name, parsed.value);
               val++;
          }
          parser.functions.putAll(functions);
          parser.parse();
          return new Result(parser.variables, parser.functions, parser.variables.get("return"));
     }


     public Result parse(Function function, String line, VariableContainer instance) throws SyntaxException {
          LineParser parser = new LineParser(function.code.code, lineIndex + 1 + executorLine, instance);

          String[] inside = split(getInsideBrackets(line), ',');
          int val = 0;
          for (String i : inside) {
               Value parsed = parseValue(removeWhitespaces(function.arguments[val]), i);
               parser.variables.put(parsed.name, parsed.value);
               val++;
          }
          parser.functions.putAll(functions);
          parser.parse();
          return new Result(parser.variables, parser.functions, parser.variables.get("return"));
     }

     public Result parse(FunctionValue function, String line, VariableContainer instance) throws SyntaxException {
          LineParser parser = new LineParser(function.value, lineIndex + 1 + executorLine, instance);

          String[] inside = split(getInsideBrackets(line), ',');

          if (!(inside.length == 1 && inside[0].equals(""))) {
               int val = 0;
               for (String i : inside) {
                    Value parsed = parseValue(function.arguments[val].trim(), i);
                    parser.variables.put(parsed.name, parsed.value);
                    val++;
               }
          }
          for (String i : function.movables) {
               i = i.trim();
               parser.variables.put(i, variables.get(i));
          }

          parser.functions.putAll(functions);

          parser.parse();

          for (String i : function.movables) {
               i = i.trim();
               variables.put(i, parser.variables.get(i));
          }

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

     public Result parse(String currentClass, Function function) throws SyntaxException {
          LineParser parser = new LineParser(function.code.code, lineIndex + 1 + executorLine);

          String[] inside = split(getInsideBrackets(line), ',');
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
               for (String line : split(code, ';')) {
                    line = line.trim();

                    if (line.equals("")) continue;

                    String[] words = words(line);
                    boolean isStatic = words[0].equals("static");
                    if (isStatic) line = rWord(line, words, 0);
                    int i = isStatic ? 1 : 0;

                    if (token("import", words)) {
                         if (isStatic) throw new SyntaxException("Import cannot be static");

                         line = rWord(line, words, 0);

                         for (String module : removeWhitespaces(line).split(",")) {
                              if (modules.containsKey(module)) {
                                   PixieModule parsed = modules.get(module);

                                   staticVariables.putAll(parsed.variables);
                                   staticFunctions.putAll(parsed.functions);
                              } else throw new SyntaxException("Cannot import " + module);
                         }

                         continue;
                    }

                    if (token("field", words, i)) {
                         line = rWord(rWord(rWord(line, words, i), words, i + 1), words, i + 2);

                         Value parsed = parseValue(words[i + 1], line);

                         (isStatic ? staticVariables : variables).put(parsed.name, parsed.value);

                         continue;
                    }

                    if (token("function", words, i)) {
                         String[] args = split(getInsideBrackets(line), ',');
                         (isStatic ? staticFunctions : functions).put(words[i + 1] + args.length, new Function(args, new Function.Code(getInsideBraces(line), (k, j, h) -> new NullValue())));
                    }

                    if (token(className, words)) {
                         String[] args = split(getInsideBrackets(line), ',');
                         (isStatic ? staticFunctions : functions).put("@__constructor__" + args.length, new Function(args, new Function.Code(getInsideBraces(line), (k, j, h) -> new NullValue())));
                    }
               }
               return new ClassConstructor(className, functions, staticFunctions, variables, staticVariables, executorLine + 1);
          }
     }

     public static Map<String, Function> baseFunctions() {
          return new HashMap(ofEntries(
                  PixieModule.function("input", 0,
                          (String line, String[] words, LineParser p) -> new TextValue(scaner.next())
                  ),
                  PixieModule.function("input", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = PixieModule.get(line, words);

                                    System.out.print(p.parseValue("", inside[0]).value.get(p).toString());

                                    return new TextValue(scaner.next());
                               } catch (SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }

                               return new NullValue();
                          }
                  )
          ));
     }
}

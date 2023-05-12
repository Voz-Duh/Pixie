package pixie.parser;

import pixie.parser.modules.FileModule;
import pixie.parser.modules.MathModule;
import pixie.parser.modules.PixieModule;
import pixie.parser.modules.SocketModule;
import pixie.parser.values.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineParser {
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
     public final Map<String, Operable> variables = new HashMap<>();
     public final Map<String, Function> functions = baseFunctions();

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

     public Function function(String value) throws SyntaxException {
          if (have('.', value))
          {
               ClassConstructor _class = null;
               InstanceValue instance = null;

               String[] values = value.split("\\.");
               for (int i = 0; i < values.length-1; i++) {
                    String s = values[i].trim();

                    if (_class == null && instance == null) {
                         if (s.equals("this")) {
                              continue;
                         }

                         if (classes.containsKey(s)) {
                              _class = classes.get(s);
                         }

                         if (variables.containsKey(s)) {
                              instance = (InstanceValue) variables.get(s);
                         }
                    }
                    else {
                         if (_class != null) {
                              instance = (InstanceValue) _class.staticVariables.get(s);
                              _class = null;
                         }
                         else {
                              try {
                                   instance = (InstanceValue) instance.variables.get(s);
                              }
                              catch (NullPointerException e) {
                                   throw new SyntaxException("Instance dont have " + s);
                              }
                              catch (ClassCastException e) {
                                   throw new SyntaxException(". after not instance");
                              }
                         }
                    }
               }

               String r = values[value.length()-1];
               if (_class != null) {
                    if (_class.staticFunctions.containsKey(r))
                         return _class.staticFunctions.get(r);
               }
               else if (instance.functions.containsKey(r)) {
                    return instance.functions.get(r);
               }
               return null;
          }
          return functions.get(value);
     }


     public Operable variable(String value) throws SyntaxException {
          if (have('.', value))
          {
               ClassConstructor _class = null;
               InstanceValue instance = null;

               String[] values = value.split("\\.");
               for (int i = 0; i < values.length-1; i++) {
                    String s = values[i].trim();

                    if (_class == null && instance == null) {
                         if (s.equals("this")) {
                              continue;
                         }

                         if (classes.containsKey(s)) {
                              _class = classes.get(s);
                         }

                         if (variables.containsKey(s)) {
                              instance = (InstanceValue) variables.get(s);
                         }
                    }
                    else {
                         if (_class != null) {
                              instance = (InstanceValue) _class.staticVariables.get(s);
                              _class = null;
                         }
                         else {
                              try {
                                   instance = (InstanceValue) instance.variables.get(s);
                              }
                              catch (NullPointerException e) {
                                   throw new SyntaxException("Instance dont have " + s);
                              }
                              catch (ClassCastException e) {
                                   throw new SyntaxException(". after not instance");
                              }
                         }
                    }
               }

               String r = values[value.length()-1];
               if (_class != null) {
                    if (_class.staticVariables.containsKey(r))
                         return _class.staticVariables.get(r);
               }
               else if (instance.variables.containsKey(r)) {
                    return instance.variables.get(r);
               }
               return null;
          }
          if (variables.containsKey(value)) return variables.get(value);
          return null;
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

     public Value parseValue(String name, String value) throws SyntaxException {
          return MathParser.compute(name, value, this);
     }

     public static String removeWhitespaces(String str) {
          return str.replaceAll(" ", "").replaceAll("\n", "");
     }

     private static final List<Character> splitIgnore = listOf(
             '(', ')', '[', ']', ':', '{', '}'
     );

     public static String[] split(String string, char split) {
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

               lastIsBack = string.charAt(i) == '\\';
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

     public static boolean have(char letter, String text) {
          return text.indexOf(letter) != -1;
     }

     public static final Pattern PATTERN_CHEVRONS = Pattern.compile("\\<(.*?)\\>");

     public static String getInsideChevrons(String code) {
          Matcher matcher = PATTERN_CHEVRONS.matcher(code);

          while (matcher.find()) {
               return matcher.group().trim();
          }

          return "";
     }

     public static final Pattern PATTERN_BRACES = Pattern.compile("\\{(.*?)\\}");

     public static String getInsideBraces(String code) {
          Matcher matcher = PATTERN_BRACES.matcher(code);

          while (matcher.find()) {
               return matcher.group().trim();
          }

          return "";
     }

     public static final Pattern PATTERN_BOX_BRACKETS = Pattern.compile("\\[(.*?)\\]");

     public static String getInsideBoxBrackets(String code) {
          Matcher matcher = PATTERN_BOX_BRACKETS.matcher(code);

          while (matcher.find()) {
               return matcher.group().trim();
          }

          return "";
     }

     public static final Pattern PATTERN_BRACKETS = Pattern.compile("\\((.*?)\\)");

     public static String getInsideBrackets(String code) {
          Matcher matcher = PATTERN_BRACKETS.matcher(code);

          while (matcher.find()) {
               return matcher.group().trim();
          }

          return "";
     }

     public static final Pattern PATTERN_LINES = Pattern.compile("[^{}\\[\\];\"]*(\\{[^{}]*?\\}|\\[[^\\[\\]]*?\\]|\"(?:(?<!\\\\)\")*[^\"]*\"|\\([^\\(\\)]*?\\)|;)");

     public static boolean token(String token, String[] words) {
          return words[0].equals(token);
     }

     public static String[] words(String line) {
          String[] words = new String[0];
          {
               String regex = "\\b\\w+";
               Matcher wordsMatcher = Pattern.compile(regex).matcher(line);
               List<String> wordsList = new ArrayList<>();
               while (wordsMatcher.find()) {
                    if (wordsMatcher.group().matches(".*[({\\[<].*")) {
                         break;
                    }
                    wordsList.add(wordsMatcher.group().trim());
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
          Matcher matcher = PATTERN_LINES.matcher(code);

          while (matcher.find()) {
               String line = matcher.group().trim();

               String[] words = new String[0];
               {
                    String regex = "\\b\\w+";
                    Matcher wordsMatcher = Pattern.compile(regex).matcher(line);
                    List<String> wordsList = new ArrayList<>();
                    while (wordsMatcher.find()) {
                         if (wordsMatcher.group().matches(".*[({\\[<].*")) {
                              break;
                         }
                         wordsList.add(wordsMatcher.group().trim());
                    }
                    words = wordsList.toArray(words);
               }

               if (token("import", words)) {
                    line = rWord(line, words, 0);

                    for (String module : removeWhitespaces(line).split(",")) {
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

                    System.out.println(parsed.value.get(this).toString());

                    continue;
               }

               if (token("def", words)) {
                    functions.put(words[1], new Function(getInsideBrackets(line).split(","), new Function.Code(getInsideBraces(line), (i, j, h) -> new NullValue())));

                    continue;
               }

               if (token("return", words)) {
                    line = rWord(line, words, 0);

                    Value parsed = parseValue("return", line);
                    variables.put(parsed.name, parsed.value);

                    break;
               }

               Function function = function(words[0]);
               if (function != null) {
                    if (!function.code.code.equals("")) parse(words[0], function.code.code);
                    else function.code.lamda.apply(line, words, this);

                    continue;
               }

               Operable variable = variable(words[0]);
               if (variable != null) {
                    line = rWord(line, words, 0);

                    String isList = getInsideBoxBrackets(line);

                    if (sbool(isList)) {
                         line = line.substring(isList.length() + 2).trim();
                    }

                    String operator = "=";
                    if (!line.startsWith("=")) {
                         operator = line.substring(0, 2);
                         line = line.substring(2).trim();
                    }

                    Value parsed = parseValue(words[0], line);
                    if (!sbool(isList)) {
                         switch (operator) {
                              case "=":
                                   variables.put(parsed.name, parsed.value);
                                   break;
                              case "+=":
                                   variables.put(parsed.name, variable.add(parsed.value, this));
                                   break;
                              case "-=":
                                   variables.put(parsed.name, variable.sub(parsed.value, this));
                                   break;
                              case "*=":
                                   variables.put(parsed.name, variable.mul(parsed.value, this));
                                   break;
                              case "/=":
                                   variables.put(parsed.name, variable.div(parsed.value, this));
                                   break;
                              case "^=":
                                   variables.put(parsed.name, variable.pow(parsed.value, this));
                                   break;
                              case "%=":
                                   variables.put(parsed.name, variable.mod(parsed.value, this));
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

          String[] inside = split(getInsideBrackets(line), ',');
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
               Matcher matcher = PATTERN_LINES.matcher(code);

               while (matcher.find()) {
                    String line = matcher.group().trim();

                    boolean isStatic = false;
                    String[] words = new String[0];
                    {
                         String regex = "\\b\\w+";
                         Matcher wordsMatcher = Pattern.compile(regex).matcher(line);
                         List<String> wordsList = new ArrayList<>();
                         while (wordsMatcher.find()) {
                              if (wordsMatcher.group().matches(".*[({\\[<].*")) {
                                   break;
                              }
                              String g = wordsMatcher.group().trim();
                              if (wordsList.size() == 0) {
                                   isStatic = g.startsWith("static");
                                   line = line.replaceFirst(g, "").trim();
                              }
                              else {
                                   wordsList.add(g);
                              }
                         }
                         words = wordsList.toArray(words);
                    }

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

                    if (token("field", words)) {
                         line = rWord(rWord(rWord(line, words, 0), words, 1), words, 2);

                         Value parsed = parseValue(words[1], line);
                         variables.put(parsed.name, parsed.value);

                         (isStatic ? staticVariables : variables).put(parsed.name, parsed.value);

                         continue;
                    }

                    if (token("function", words)) {
                         (isStatic ? staticFunctions : functions).put(words[1], new Function(getInsideBrackets(line).split(","), new Function.Code(getInsideBraces(line), (i, j, h) -> new NullValue())));
                    }
               }
               return new ClassConstructor(className, functions, staticFunctions, variables, staticVariables, executorLine + 1);
          }
     }

     public static Map<String, Function> baseFunctions()
     {
          return new HashMap(ofEntries(/*
                  entry("for",
                          PixieModule.function(
                                  (String line, String[] words, LineParser p) -> {
                                       try {
                                            String inside = getInsideBrackets(line);

                                            String resultCode = getInsideBraces(line);

                                            Operable parsedLeft = p.parseValue("", inside[0]).value;
                                            if (parsedLeft instanceof NumValue) {
                                                 String operator = removeWhitespaces(inside[2]);
                                                 Object parsedRight = self.parseValue("", inside[3]).value.get(self);
                                                 if (parsedRight instanceof Float) {
                                                      Result result = self.parse(inside[0]);

                                                      self.variables.putAll(result.vars);
                                                      self.functions.putAll(result.functs);

                                                      while (((BoolValue) self.parseValue("", resultCode.toString()).value).get(self)) {
                                                           result = self.parse(resultCode.toString());

                                                           self.variables.putAll(result.vars);
                                                           self.functions.putAll(result.functs);

                                                           self.parse(inside[2]);
                                                      }
                                                 }
                                            }
                                            if (parsedLeft instanceof BoolValue) {
                                                 boolean b = ((BoolValue) parsedLeft).get(self);
                                                 while (b) {
                                                      Result result = self.parse(resultCode.toString());

                                                      self.variables.putAll(result.vars);
                                                      self.functions.putAll(result.functs);

                                                      b = ((BoolValue)result.value).get(self);
                                                 }
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
                  ),*/
                  /*
                  entry("if",
                          PixieModule.function(
                                  (String line, String[] words, LineParser p) -> {
                                       try {
                                            String inside = getInsideBrackets(line);

                                            String resultCode = getInsideBraces(line);

                                            Object value = p.parseValue("", inside).value.get(p);
                                            if (value instanceof Boolean) {
                                                 if ((Boolean) value) {
                                                      Result result = p.parse(sbool(resultCode) ? resultCode : );

                                                      p.variables.putAll(result.vars);
                                                      p.functions.putAll(result.functs);
                                                 }

                                            } else throw new SyntaxException("Not bool value provided to if");
                                       } catch (SyntaxException e) {
                                            new Exception(e.getMessage()).printStackTrace();
                                       }

                                       return new NullValue();
                                  }
                          )
                  ),
                   */
                  entry("class",
                          PixieModule.function(
                                  (String line, String[] words, LineParser p) -> {
                                       try {
                                            ClassConstructor result = p.parseClass(words[1], getInsideBraces(line));
                                            p.classes.put(words[1], result);
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
                                  (String line, String[] words, LineParser p) -> {
                                       try {
                                            String inside = p.getInsideBrackets(line);

                                            System.out.print(p.parseValue("", inside).value.get(p).toString());

                                            return new TextValue(scaner.next());
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }

                                       return new NullValue();
                                  }
                          )
                  )
          ));
     }
}

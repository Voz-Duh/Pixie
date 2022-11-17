package pixie.parser.values;

import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstanceValue implements Operable<Map<String, Operable>> {
     public Map<String, Operable> variables = new HashMap<>();
     public Map<String, Function> functions = new HashMap<>();
     public ClassConstructor currentClass;

     public InstanceValue() {
     }

     public InstanceValue(Map.Entry... entries) {
          variables = new HashMap<>(Map.ofEntries(entries));
     }

     public InstanceValue(ClassConstructor currentClass, Map.Entry... entries) {
          variables = new HashMap<>(Map.ofEntries(entries));
          functions = new HashMap<>(currentClass.functions);
          variables = new HashMap<>(currentClass.variables);
          this.currentClass = currentClass;
     }

     @Override
     public Map<String, Operable> get(LineParser parser) {
          return variables;
     }

     @Override
     public Operable add(Operable other, LineParser parser) throws SyntaxException {
          if (!functions.containsKey("@add")) throw new SyntaxException("Instance value not support '+' operator");

          return parse(parser, functions.get("@add"), other);
     }

     @Override
     public Operable sub(Operable other, LineParser parser) throws SyntaxException {
          if (!functions.containsKey("@sub")) throw new SyntaxException("Instance value not support '-' operator");

          return parse(parser, functions.get("@sub"), other);
     }

     @Override
     public Operable mul(Operable other, LineParser parser) throws SyntaxException {
          if (!functions.containsKey("@mul")) throw new SyntaxException("Instance value not support '*' operator");

          return parse(parser, functions.get("@mul"), other);
     }

     @Override
     public Operable div(Operable other, LineParser parser) throws SyntaxException {
          if (!functions.containsKey("@div")) throw new SyntaxException("Instance value not support '/' operator");

          return parse(parser, functions.get("@idv"), other);
     }

     @Override
     public Operable pow(Operable other, LineParser parser) throws SyntaxException {
          if (!functions.containsKey("@pow")) throw new SyntaxException("Instance value not support '**' operator");

          return parse(parser, functions.get("@pow"), other);
     }

     @Override
     public Operable and(Operable other, LineParser parser) throws SyntaxException {
          if (!functions.containsKey("@and")) throw new SyntaxException("Instance value not support '&&' operator");

          return parse(parser, functions.get("@and"), other);
     }

     @Override
     public Operable or(Operable other, LineParser parser) throws SyntaxException {
          if (!functions.containsKey("@or")) throw new SyntaxException("Instance value not support '||' operator");

          return parse(parser, functions.get("@or"), other);
     }

     @Override
     public BoolValue more(Operable other, LineParser parser) throws SyntaxException {
          if (!functions.containsKey("@more")) throw new SyntaxException("Instance value not support '>' operator");

          return (BoolValue) parse(parser, functions.get("@more"), other);
     }

     @Override
     public BoolValue less(Operable other, LineParser parser) throws SyntaxException {
          if (!functions.containsKey("@less")) throw new SyntaxException("Instance value not support '<' operator");

          return (BoolValue) parse(parser, functions.get("@less"), other);
     }

     @Override
     public BoolValue moreEqu(Operable other, LineParser parser) throws SyntaxException {
          if (!functions.containsKey("@more_equals")) throw new SyntaxException("Instance value not support '>=' operator");

          return (BoolValue) parse(parser, functions.get("@more_equals"), other);
     }

     @Override
     public BoolValue lessEqu(Operable other, LineParser parser) throws SyntaxException {
          if (!functions.containsKey("@less_equals")) throw new SyntaxException("Instance value not support '<=' operator");

          return (BoolValue) parse(parser, functions.get("@less_equals"), other);
     }

     @Override
     public BoolValue equals(Operable other, LineParser parser) throws SyntaxException {
          if (!functions.containsKey("@equals")) throw new SyntaxException("Instance value not support '==' operator");

          return (BoolValue) parse(parser, functions.get("@equals"), other);
     }

     @Override
     public BoolValue notEquals(Operable other, LineParser parser) throws SyntaxException {
          if (!functions.containsKey("@not_equals")) throw new SyntaxException("Instance value not support '!=' operator");

          return (BoolValue) parse(parser, functions.get("@not_equals"), other);
     }

     @Override
     public Operable inv(LineParser parser) throws SyntaxException {
          if (!functions.containsKey("@inv")) throw new SyntaxException("Instance value not support '!' operator");

          return parse(parser, functions.get("@inv"));
     }

     public Operable parse(LineParser self, Function function, Operable other) throws SyntaxException {
          LineParser parser = new LineParser(function.code.code, currentClass.line);

          parser.classes.putAll(self.classes);

          parser.variables.putAll(currentClass.staticVariables);
          parser.variables.putAll(variables);
          parser.variables.put("other", other);

          parser.functions.putAll(currentClass.staticFunctions);
          parser.functions.putAll(currentClass.functions);
          parser.parse();

          variables.forEach((String key, Operable i) -> {
               variables.put(key, parser.variables.get(key));
          });
          currentClass.staticVariables.forEach((String key, Operable i) -> {
               currentClass.staticVariables.put(key, parser.variables.get(key));
          });

          return parser.variables.get("return");
     }

     public Operable parse(LineParser self, Function function) throws SyntaxException {
          LineParser parser = new LineParser(function.code.code, currentClass.line);

          parser.classes.putAll(self.classes);

          parser.variables.putAll(currentClass.staticVariables);
          parser.variables.putAll(variables);

          parser.functions.putAll(currentClass.staticFunctions);
          parser.functions.putAll(currentClass.functions);
          parser.parse();

          variables.forEach((String key, Operable i) -> {
               variables.put(key, parser.variables.get(key));
          });
          currentClass.staticVariables.forEach((String key, Operable i) -> {
               currentClass.staticVariables.put(key, parser.variables.get(key));
          });

          return parser.variables.get("return");
     }

     public Operable parse(LineParser self, String name, Function function) throws SyntaxException {
          LineParser parser = new LineParser(function.code.code, currentClass.line);

          String[] inside = self.split(self.getInsideBrackets(self.getNextString(0, self.line, List.of(name))[0].length() + name.length(), self.line), ',');
          int val = 0;
          for (String i : inside) {
               LineParser.Value parsed = self.parseValue(LineParser.removeWhitespaces(function.arguments[val]), i);
               parser.variables.put(parsed.name, parsed.value);
               val++;
          }
          parser.classes.putAll(self.classes);

          parser.variables.putAll(currentClass.staticVariables);
          parser.variables.putAll(variables);

          parser.functions.putAll(currentClass.staticFunctions);
          parser.functions.putAll(currentClass.functions);
          parser.parse();

          variables.forEach((String key, Operable i) -> {
               variables.put(key, parser.variables.get(key));
          });
          currentClass.staticVariables.forEach((String key, Operable i) -> {
               currentClass.staticVariables.put(key, parser.variables.get(key));
          });

          return parser.variables.get("return");
     }
}

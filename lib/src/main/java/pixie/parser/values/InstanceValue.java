package pixie.parser.values;

import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;
import java.util.HashMap;
import java.util.Map;

import static pixie.parser.values.NullValue.*;

public class InstanceValue implements Operable<Map<String, Operable>>, VariableContainer {
     public Map<String, Operable> variables = new HashMap<>();
     public Map<String, Function> functions = new HashMap<>();
     public ClassConstructor currentClass;

     public InstanceValue() {
     }

     public InstanceValue(Map.Entry... entries) {
          variables = LineParser.ofEntries(entries);
     }

     public InstanceValue(ClassConstructor currentClass, Map.Entry... entries) {
          variables = new HashMap<>(LineParser.ofEntries(entries));
          functions = new HashMap<>(currentClass.functions);
          variables = new HashMap<>(currentClass.variables);
          this.currentClass = currentClass;
     }

     public Map<String, Operable> getVar() {
          return variables;
     }

     @Override
     public Map<String, Function> getFunc() {
          return functions;
     }

     @Override
     public Map<String, Operable> get(LineParser parser) {
          return variables;
     }

     Operable operator(Operable other, String func, String operator) throws SyntaxException {
          if (!currentClass.staticFunctions.containsKey(func)) throw new SyntaxException(currentClass.name + " dont have " + operator + " operator");

          return parse(currentClass.staticFunctions.get(func), this, other);
     }

     @Override
     public Operable add(Operable other, LineParser parser) throws SyntaxException {
          return operator(other, "@add", "+");
     }

     @Override
     public Operable sub(Operable other, LineParser parser) throws SyntaxException {
          return operator(other, "@sub", "-");
     }

     @Override
     public Operable mul(Operable other, LineParser parser) throws SyntaxException {
          return operator(other, "@mul", "*");
     }

     @Override
     public Operable div(Operable other, LineParser parser) throws SyntaxException {
          return operator(other, "@div", "/");
     }

     @Override
     public Operable pow(Operable other, LineParser parser) throws SyntaxException {
          return operator(other, "@pow", "^");
     }

     @Override
     public Operable mod(Operable other, LineParser parser) throws SyntaxException {
          return operator(other, "@mod", "%");
     }

     @Override
     public Operable and(Operable other, LineParser parser) throws SyntaxException {
          return operator(other, "@and", "&&");
     }

     @Override
     public Operable or(Operable other, LineParser parser) throws SyntaxException {
          return operator(other, "@or", "||");
     }

     @Override
     public BoolValue more(Operable other, LineParser parser) throws SyntaxException {
          return (BoolValue) operator(other, "@more", ">");
     }

     @Override
     public BoolValue less(Operable other, LineParser parser) throws SyntaxException {
          return (BoolValue) operator(other, "@less", "<");
     }

     @Override
     public BoolValue moreEqu(Operable other, LineParser parser) throws SyntaxException {
          return (BoolValue) operator(other, "@more_equals", ">=");
     }

     @Override
     public BoolValue lessEqu(Operable other, LineParser parser) throws SyntaxException {
          return (BoolValue) operator(other, "@less_equals", "<=");
     }

     @Override
     public BoolValue equals(Operable other, LineParser parser) throws SyntaxException {
          return (BoolValue) operator(other, "@equals", "==");
     }

     @Override
     public BoolValue notEquals(Operable other, LineParser parser) throws SyntaxException {
          return (BoolValue) operator(other, "@not_equals", "!=");
     }

     @Override
     public Operable inv(LineParser parser) throws SyntaxException {
          if (!currentClass.staticFunctions.containsKey("@inv")) throw new SyntaxException(currentClass.name + " dont have ! operator");

          return parse(currentClass.staticFunctions.get("@inv"), this);
     }

     public Operable parse(Function function, Operable... values) throws SyntaxException {
          LineParser parser = new LineParser(function.code.code, 0, null);

          int val = 0;
          for (Operable i : values) {
               parser.variables.put(function.arguments[val].trim(), i);
               val++;
          }
          parser.functions.putAll(functions);
          parser.parse();
          return parser.variables.get("return");
     }

     public Operable parse(LineParser self, Function function, String[] inside) throws SyntaxException {
          LineParser parser = new LineParser(function.code.code, currentClass.line, this);

          parser.classes.putAll(self.classes);

          int val = 0;
          for (String i : inside) {
               LineParser.Value value = self.parseValue(function.arguments[val].trim(), i);
               parser.variables.put(value.name, value.value);
               val++;
          }
          parser.functions.putAll(functions);

          parser.parse();

          variables.forEach((key, i) -> variables.put(key, parser.variables.get(key)));
          currentClass.staticVariables.forEach((key, i) -> currentClass.staticVariables.put(key, parser.variables.get(key)));

          return parser.variables.get("return");
     }
}

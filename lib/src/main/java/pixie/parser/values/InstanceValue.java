package pixie.parser.values;

import pixie.parser.Operable;
import pixie.parser.SyntaxException;
import java.util.HashMap;
import java.util.Map;

public class InstanceValue implements Operable<Map<String, Operable>> {
     public Map<String, Operable> value = new HashMap<>();

     public InstanceValue() {
     }

     public InstanceValue(Map.Entry... entries) {
          value = new HashMap<>(Map.ofEntries(entries));
     }

     @Override
     public Map<String, Operable> get() {
          return value;
     }

     @Override
     public Operable add(Operable other) throws SyntaxException {
          throw new SyntaxException("Instance value not support '+' operator");
     }

     @Override
     public Operable sub(Operable other) throws SyntaxException {
          throw new SyntaxException("Instance value not support '-' operator");
     }

     @Override
     public Operable mul(Operable other) throws SyntaxException {
          throw new SyntaxException("Instance value not support '*' operator");
     }

     @Override
     public Operable div(Operable other) throws SyntaxException {
          throw new SyntaxException("Instance value not support '/' operator");
     }

     @Override
     public Operable pow(Operable other) throws SyntaxException {
          throw new SyntaxException("Instance value not support '**' operator");
     }

     @Override
     public Operable and(Operable other) throws SyntaxException {
          throw new SyntaxException("Instance value not support '&&' operator");
     }

     @Override
     public Operable or(Operable other) throws SyntaxException {
          throw new SyntaxException("Instance value not support '||' operator");
     }

     @Override
     public BoolValue more(Operable other) throws SyntaxException {
          throw new SyntaxException("Instance value not support '>' operator");
     }

     @Override
     public BoolValue less(Operable other) throws SyntaxException {
          throw new SyntaxException("Instance value not support '<' operator");
     }

     @Override
     public BoolValue moreEqu(Operable other) throws SyntaxException {
          throw new SyntaxException("Instance value not support '>=' operator");
     }

     @Override
     public BoolValue lessEqu(Operable other) throws SyntaxException {
          throw new SyntaxException("Instance value not support '<=' operator");
     }

     @Override
     public BoolValue equals(Operable other) throws SyntaxException {
          throw new SyntaxException("Instance value not support '==' operator");
     }

     @Override
     public BoolValue notEquals(Operable other) throws SyntaxException {
          throw new SyntaxException("Instance value not support '!=' operator");
     }

     @Override
     public Operable inv() throws SyntaxException {
          throw new SyntaxException("Instance value not support '!' operator");
     }
}

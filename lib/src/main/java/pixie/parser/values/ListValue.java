package pixie.parser.values;

import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;

import java.util.ArrayList;
import java.util.List;

public class ListValue implements Operable<List<Operable>> {
     public List<Operable> value;

     public ListValue(Operable... values) {
          value = new ArrayList<>(List.of(values));
     }

     @Override
     public List<Operable> get(LineParser parser) {
          return value;
     }

     @Override
     public Operable add(Operable other, LineParser parser) throws SyntaxException {
          value.add(other);
          return this;
     }

     @Override
     public Operable sub(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("List value not support '-' operator");
     }

     @Override
     public Operable mul(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("List value not support '*' operator");
     }

     @Override
     public Operable div(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("List value not support '/' operator");
     }

     @Override
     public Operable pow(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("List value not support '**' operator");
     }

     @Override
     public Operable and(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("List value not support '&&' operator");
     }

     @Override
     public Operable or(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("List value not support '||' operator");
     }

     @Override
     public BoolValue more(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("List value not support '>' operator");
     }

     @Override
     public BoolValue less(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("List value not support '<' operator");
     }

     @Override
     public BoolValue moreEqu(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("List value not support '>=' operator");
     }

     @Override
     public BoolValue lessEqu(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("List value not support '<=' operator");
     }

     @Override
     public BoolValue equals(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("List value not support '==' operator");
     }

     @Override
     public BoolValue notEquals(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("List value not support '!=' operator");
     }

     @Override
     public Operable inv(LineParser parser) throws SyntaxException {
          throw new SyntaxException("List value not support '!' operator");
     }
}

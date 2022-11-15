package pixie.parser.values;

import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;

public class NullValue implements Operable<Object> {
     @Override
     public Object get(LineParser parser)  {
          return null;
     }

     @Override
     public NullValue add(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Null value not support '+' operator");
     }

     @Override
     public NullValue sub(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Null value not support '-' operator");
     }

     @Override
     public NullValue mul(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Null value not support '*' operator");
     }

     @Override
     public NullValue div(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Null value not support '/' operator");
     }

     @Override
     public Operable pow(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Null value not support '**' operator");
     }

     @Override
     public NullValue and(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Null value not support '&&' operator");
     }

     @Override
     public NullValue or(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Null value not support '||' operator");
     }

     @Override
     public BoolValue more(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Null value not support '>' operator");
     }

     @Override
     public BoolValue less(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Null value not support '<' operator");
     }

     @Override
     public BoolValue moreEqu(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Null value not support '>=' operator");
     }

     @Override
     public BoolValue lessEqu(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Null value not support '<=' operator");
     }

     @Override
     public BoolValue equals(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Null value not support '==' operator");
     }

     @Override
     public BoolValue notEquals(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Null value not support '!=' operator");
     }

     @Override
     public NullValue inv(LineParser parser) throws SyntaxException {
          throw new SyntaxException("Null value not support '!' operator");
     }
}

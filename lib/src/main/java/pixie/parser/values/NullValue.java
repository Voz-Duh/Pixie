package pixie.parser.values;

import pixie.parser.Operable;
import pixie.parser.SyntaxException;

public class NullValue implements Operable<Object> {
     @Override
     public Object get()  {
          return null;
     }

     @Override
     public NullValue add(Operable other) throws SyntaxException {
          throw new SyntaxException("Null value not support '+' operator");
     }

     @Override
     public NullValue sub(Operable other) throws SyntaxException {
          throw new SyntaxException("Null value not support '-' operator");
     }

     @Override
     public NullValue mul(Operable other) throws SyntaxException {
          throw new SyntaxException("Null value not support '*' operator");
     }

     @Override
     public NullValue div(Operable other) throws SyntaxException {
          throw new SyntaxException("Null value not support '/' operator");
     }

     @Override
     public Operable pow(Operable other) throws SyntaxException {
          throw new SyntaxException("Null value not support '**' operator");
     }

     @Override
     public NullValue and(Operable other) throws SyntaxException {
          throw new SyntaxException("Null value not support '&&' operator");
     }

     @Override
     public NullValue or(Operable other) throws SyntaxException {
          throw new SyntaxException("Null value not support '||' operator");
     }

     @Override
     public BoolValue more(Operable other) throws SyntaxException {
          throw new SyntaxException("Null value not support '>' operator");
     }

     @Override
     public BoolValue less(Operable other) throws SyntaxException {
          throw new SyntaxException("Null value not support '<' operator");
     }

     @Override
     public BoolValue moreEqu(Operable other) throws SyntaxException {
          throw new SyntaxException("Null value not support '>=' operator");
     }

     @Override
     public BoolValue lessEqu(Operable other) throws SyntaxException {
          throw new SyntaxException("Null value not support '<=' operator");
     }

     @Override
     public BoolValue equals(Operable other) throws SyntaxException {
          throw new SyntaxException("Null value not support '==' operator");
     }

     @Override
     public BoolValue notEquals(Operable other) throws SyntaxException {
          throw new SyntaxException("Null value not support '!=' operator");
     }

     @Override
     public NullValue inv() throws SyntaxException {
          throw new SyntaxException("Null value not support '!' operator");
     }
}

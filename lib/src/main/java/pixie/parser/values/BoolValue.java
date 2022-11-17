package pixie.parser.values;


import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;

public class BoolValue implements Operable<Boolean> {
     public boolean value;

     public BoolValue(boolean value){
          this.value = value;
     }

     @Override
     public Boolean get(LineParser parser) {
          return value;
     }

     @Override
     public Operable add(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof String)
               return new TextValue(String.valueOf(value) + getter);

          throw new SyntaxException("Bool value not support '+' operator");
     }

     @Override
     public Operable sub(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Bool value not support '-' operator");
     }

     @Override
     public Operable mul(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Bool value not support '*' operator");
     }

     @Override
     public Operable div(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Bool value not support '/' operator");
     }

     @Override
     public Operable pow(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Bool value not support '**' operator");
     }

     @Override
     public Operable and(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Boolean)
               return new BoolValue(value && (Boolean) getter);

          throw new SyntaxException("Bool value not support '&&' operator");
     }

     @Override
     public Operable or(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Boolean)
               return new BoolValue(value || (Boolean) getter);

          throw new SyntaxException("Bool value not support '||' operator");
     }

     @Override
     public BoolValue more(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Bool value not support '>' operator");
     }

     @Override
     public BoolValue less(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Bool value not support '<' operator");
     }

     @Override
     public BoolValue moreEqu(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Bool value not support '>=' operator");
     }

     @Override
     public BoolValue lessEqu(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Bool value not support '<=' operator");
     }

     @Override
     public BoolValue equals(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Boolean)
               return new BoolValue(value == (Boolean) getter);

          throw new SyntaxException("Bool value not support '==' operator");
     }

     @Override
     public BoolValue notEquals(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Boolean)
               return new BoolValue(value != (Boolean) getter);

          throw new SyntaxException("Bool value not support '!=' operator");
     }

     @Override
     public Operable inv(LineParser parser) {
          return new BoolValue(!value);
     }
}

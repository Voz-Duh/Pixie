package pixie.parser.values;


import pixie.parser.Operable;
import pixie.parser.SyntaxException;

public class BoolValue implements Operable<Boolean> {
     public boolean value;

     public BoolValue(boolean value){
          this.value = value;
     }

     @Override
     public Boolean get() {
          return value;
     }

     @Override
     public Operable add(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof String)
               return new TextValue(String.valueOf(value) + getter);

          throw new SyntaxException("Bool value not support '+' operator");
     }

     @Override
     public Operable sub(Operable other) throws SyntaxException {
          throw new SyntaxException("Bool value not support '-' operator");
     }

     @Override
     public Operable mul(Operable other) throws SyntaxException {
          throw new SyntaxException("Bool value not support '*' operator");
     }

     @Override
     public Operable div(Operable other) throws SyntaxException {
          throw new SyntaxException("Bool value not support '/' operator");
     }

     @Override
     public Operable pow(Operable other) throws SyntaxException {
          throw new SyntaxException("Bool value not support '**' operator");
     }

     @Override
     public Operable and(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Boolean)
               return new BoolValue(value && (Boolean) getter);

          throw new SyntaxException("Bool value not support '&&' operator");
     }

     @Override
     public Operable or(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Boolean)
               return new BoolValue(value || (Boolean) getter);

          throw new SyntaxException("Bool value not support '||' operator");
     }

     @Override
     public BoolValue more(Operable other) throws SyntaxException {
          throw new SyntaxException("Bool value not support '>' operator");
     }

     @Override
     public BoolValue less(Operable other) throws SyntaxException {
          throw new SyntaxException("Bool value not support '<' operator");
     }

     @Override
     public BoolValue moreEqu(Operable other) throws SyntaxException {
          throw new SyntaxException("Bool value not support '>=' operator");
     }

     @Override
     public BoolValue lessEqu(Operable other) throws SyntaxException {
          throw new SyntaxException("Bool value not support '<=' operator");
     }

     @Override
     public BoolValue equals(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Boolean)
               return new BoolValue(value == (Boolean) getter);

          throw new SyntaxException("Bool value not support '==' operator");
     }

     @Override
     public BoolValue notEquals(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Boolean)
               return new BoolValue(value != (Boolean) getter);

          throw new SyntaxException("Bool value not support '!=' operator");
     }

     @Override
     public Operable inv() {
          return new BoolValue(!value);
     }
}

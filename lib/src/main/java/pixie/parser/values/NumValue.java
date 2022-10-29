package pixie.parser.values;


import pixie.parser.Operable;
import pixie.parser.SyntaxException;

public class NumValue implements Operable<Float> {
     public float value;

     public NumValue(float value){
          this.value = value;
     }

     @Override
     public Float get() {
          return value;
     }

     @Override
     public Operable add(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Float)
               return new NumValue(value + (Float) getter);
          if (getter instanceof String)
               return new TextValue(String.valueOf(value) + getter);

          throw new SyntaxException("Number value not support '+' operator");
     }

     @Override
     public Operable sub(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Float)
               return new NumValue(value - (Float) getter);

          throw new SyntaxException("Number value not support '-' operator");
     }

     @Override
     public Operable mul(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Float)
               return new NumValue(value * (Float) getter);

          throw new SyntaxException("Number value not support '*' operator");
     }

     @Override
     public Operable div(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Float)
               return new NumValue(value / (Float) getter);

          throw new SyntaxException("Number value not support '/' operator");
     }

     @Override
     public Operable pow(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Float)
               return new NumValue((float) Math.pow(value, (Float) getter));

          throw new SyntaxException("Number value not support '**' operator");
     }

     @Override
     public Operable and(Operable other) throws SyntaxException {
          throw new SyntaxException("Number value not support '&&' operator");
     }

     @Override
     public Operable or(Operable other) throws SyntaxException {
          throw new SyntaxException("Number value not support '||' operator");
     }

     @Override
     public BoolValue more(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Float)
               return new BoolValue(value > (Float) getter);

          throw new SyntaxException("Number value not support '>' operator");
     }

     @Override
     public BoolValue less(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Float)
               return new BoolValue(value < (Float) getter);

          throw new SyntaxException("Number value not support '<' operator");
     }

     @Override
     public BoolValue moreEqu(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Float)
               return new BoolValue(value >= (Float) getter);

          throw new SyntaxException("Number value not support '>=' operator");
     }

     @Override
     public BoolValue lessEqu(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Float)
               return new BoolValue(value <= (Float) getter);

          throw new SyntaxException("Number value not support '<=' operator");
     }

     @Override
     public BoolValue equals(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Float)
               return new BoolValue(value == (Float) getter);

          throw new SyntaxException("Number value not support '==' operator");
     }

     @Override
     public BoolValue notEquals(Operable other) throws SyntaxException {
          var getter = other.get();
          if (getter instanceof Float)
               return new BoolValue(value != (Float) getter);

          throw new SyntaxException("Number value not support '!=' operator");
     }

     @Override
     public Operable inv() {
          return new NumValue(-value);
     }
}

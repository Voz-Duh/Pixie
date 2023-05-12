package pixie.parser.values;


import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;

import static pixie.parser.values.NullValue.*;

public class NumValue implements Operable<Float> {
     public float value;

     public NumValue(float value){
          this.value = value;
     }

     @Override
     public Float get(LineParser parser) {
          return value;
     }

     @Override
     public Operable add(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Float)
               return new NumValue(value + (Float) getter);
          if (getter instanceof String)
               return new TextValue(String.valueOf(value) + getter);

          throw addExc(this, other);
     }

     @Override
     public Operable sub(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Float)
               return new NumValue(value - (Float) getter);

          throw subExc(this, other);
     }

     @Override
     public Operable mul(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Float)
               return new NumValue(value * (Float) getter);

          throw mulExc(this, other);
     }

     @Override
     public Operable div(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Float)
               return new NumValue(value / (Float) getter);

          throw divExc(this, other);
     }

     @Override
     public Operable pow(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Float)
               return new NumValue((float) Math.pow(value, (Float) getter));

          throw powExc(this, other);
     }

     @Override
     public Operable mod(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Float)
               return new NumValue(value % (Float) getter);

          throw modExc(this, other);
     }

     @Override
     public NullValue and(Operable other, LineParser parser) throws SyntaxException {
          throw andExc(this, other);
     }

     @Override
     public NullValue or(Operable other, LineParser parser) throws SyntaxException {
          throw orExc(this, other);
     }

     @Override
     public BoolValue more(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Float)
               return new BoolValue(value > (Float) getter);

          throw moreExc(this, other);
     }

     @Override
     public BoolValue less(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Float)
               return new BoolValue(value < (Float) getter);

          throw lessExc(this, other);
     }

     @Override
     public BoolValue moreEqu(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Float)
               return new BoolValue(value >= (Float) getter);

          throw moreEquExc(this, other);
     }

     @Override
     public BoolValue lessEqu(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Float)
               return new BoolValue(value <= (Float) getter);

          throw lessEquExc(this, other);
     }

     @Override
     public BoolValue equals(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Float)
               return new BoolValue(value == (Float) getter);

          throw equalsExc(this, other);
     }

     @Override
     public BoolValue notEquals(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Float)
               return new BoolValue(value != (Float) getter);

          throw notEqualsExc(this, other);
     }

     @Override
     public NullValue inv(LineParser parser) throws SyntaxException {
          throw invExc(this);
     }
}

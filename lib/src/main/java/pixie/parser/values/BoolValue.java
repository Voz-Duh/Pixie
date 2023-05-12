package pixie.parser.values;


import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;

import static pixie.parser.values.NullValue.*;

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

          throw NullValue.addExc(this, other);
     }

     @Override
     public NullValue sub(Operable other, LineParser parser) throws SyntaxException {
          throw subExc(this, other);
     }

     @Override
     public NullValue mul(Operable other, LineParser parser) throws SyntaxException {
          throw mulExc(this, other);
     }

     @Override
     public NullValue div(Operable other, LineParser parser) throws SyntaxException {
          throw divExc(this, other);
     }

     @Override
     public Operable pow(Operable other, LineParser parser) throws SyntaxException {
          throw powExc(this, other);
     }

     @Override
     public Operable mod(Operable other, LineParser parser) throws SyntaxException {
          throw modExc(this, other);
     }

     @Override
     public Operable and(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Boolean)
               return new BoolValue(value && (Boolean) getter);

          throw NullValue.addExc(this, other);
     }

     @Override
     public Operable or(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Boolean)
               return new BoolValue(value || (Boolean) getter);

          throw NullValue.orExc(this, other);
     }

     @Override
     public BoolValue more(Operable other, LineParser parser) throws SyntaxException {
          throw NullValue.moreExc(this, other);
     }

     @Override
     public BoolValue less(Operable other, LineParser parser) throws SyntaxException {
          throw NullValue.lessExc(this, other);
     }

     @Override
     public BoolValue moreEqu(Operable other, LineParser parser) throws SyntaxException {
          throw NullValue.moreExc(this, other);
     }

     @Override
     public BoolValue lessEqu(Operable other, LineParser parser) throws SyntaxException {
          throw NullValue.lessEquExc(this, other);
     }

     @Override
     public BoolValue equals(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Boolean)
               return new BoolValue(value == (Boolean) getter);

          throw NullValue.equalsExc(this, other);
     }

     @Override
     public BoolValue notEquals(Operable other, LineParser parser) throws SyntaxException {
          Object getter = other.get(parser);
          if (getter instanceof Boolean)
               return new BoolValue(value != (Boolean) getter);

          throw NullValue.notEqualsExc(this, other);
     }

     @Override
     public Operable inv(LineParser parser) {
          return new BoolValue(!value);
     }
}

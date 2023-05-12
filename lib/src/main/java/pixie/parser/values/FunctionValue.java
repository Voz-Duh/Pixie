package pixie.parser.values;

import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;

import static pixie.parser.values.NullValue.*;

public class FunctionValue implements Operable<String> {
     public Argument[] arguments;
     public String[] movables;
     public String value;

     public FunctionValue(String code, Argument[] arguments, String[] movables) {
          value = code;
          this.arguments = arguments;
          this.movables = movables;
     }

     @Override
     public String get(LineParser parser) {
          return value;
     }

     @Override
     public NullValue add(Operable other, LineParser parser) throws SyntaxException {
          throw addExc(this, other);
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
     public NullValue and(Operable other, LineParser parser) throws SyntaxException {
          throw andExc(this, other);
     }

     @Override
     public NullValue or(Operable other, LineParser parser) throws SyntaxException {
          throw orExc(this, other);
     }

     @Override
     public BoolValue more(Operable other, LineParser parser) throws SyntaxException {
          throw moreExc(this, other);
     }

     @Override
     public BoolValue less(Operable other, LineParser parser) throws SyntaxException {
          throw lessExc(this, other);
     }

     @Override
     public BoolValue moreEqu(Operable other, LineParser parser) throws SyntaxException {
          throw moreEquExc(this, other);
     }

     @Override
     public BoolValue lessEqu(Operable other, LineParser parser) throws SyntaxException {
          throw lessEquExc(this, other);
     }

     @Override
     public BoolValue equals(Operable other, LineParser parser) throws SyntaxException {
          throw equalsExc(this, other);
     }

     @Override
     public BoolValue notEquals(Operable other, LineParser parser) throws SyntaxException {
          throw notEqualsExc(this, other);
     }

     @Override
     public NullValue inv(LineParser parser) throws SyntaxException {
          throw invExc(this);
     }
}

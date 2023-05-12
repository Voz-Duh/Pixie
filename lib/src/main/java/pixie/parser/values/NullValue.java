package pixie.parser.values;

import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;

import java.lang.reflect.Type;

public class NullValue implements Operable<Object> {
     @Override
     public Object get(LineParser parser)  {
          return null;
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

     public static SyntaxException addExc(Operable a, Operable b) {
          String sa = a.getClass().getTypeName();
          sa = sa.substring(0, sa.length() - 5);
          String s = b.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(sa + " value not support '+' operator with " + s);
     }

     public static SyntaxException subExc(Operable a, Operable b) {
          String sa = a.getClass().getTypeName();
          sa = sa.substring(0, sa.length() - 5);
          String s = b.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(sa + " value not support '-' operator with " + s);
     }

     public static SyntaxException mulExc(Operable a, Operable b) {
          String sa = a.getClass().getTypeName();
          sa = sa.substring(0, sa.length() - 5);
          String s = b.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(sa + " value not support '*' operator with " + s);
     }

     public static SyntaxException divExc(Operable a, Operable b) {
          String sa = a.getClass().getTypeName();
          sa = sa.substring(0, sa.length() - 5);
          String s = b.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(sa + " value not support '/' operator with " + s);
     }

     public static SyntaxException powExc(Operable a, Operable b) {
          String sa = a.getClass().getTypeName();
          sa = sa.substring(0, sa.length() - 5);
          String s = b.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(sa + " value not support '^' operator with " + s);
     }

     public static SyntaxException modExc(Operable a, Operable b) {
          String sa = a.getClass().getTypeName();
          sa = sa.substring(0, sa.length() - 5);
          String s = b.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(sa + " value not support '%' operator with " + s);
     }

     public static SyntaxException andExc(Operable a, Operable b) {
          String sa = a.getClass().getTypeName();
          sa = sa.substring(0, sa.length() - 5);
          String s = b.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(sa + " value not support '&&' operator with " + s);
     }

     public static SyntaxException orExc(Operable a, Operable b) {
          String sa = a.getClass().getTypeName();
          sa = sa.substring(0, sa.length() - 5);
          String s = b.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(sa + " value not support '||' operator with " + s);
     }

     public static SyntaxException moreExc(Operable a, Operable b) {
          String sa = a.getClass().getTypeName();
          sa = sa.substring(0, sa.length() - 5);
          String s = b.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(sa + " value not support '>' operator with " + s);
     }

     public static SyntaxException lessExc(Operable a, Operable b) {
          String sa = a.getClass().getTypeName();
          sa = sa.substring(0, sa.length() - 5);
          String s = b.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(sa + " value not support '<' operator with " + s);
     }

     public static SyntaxException moreEquExc(Operable a, Operable b) {
          String sa = a.getClass().getTypeName();
          sa = sa.substring(0, sa.length() - 5);
          String s = b.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(sa + " value not support '>=' operator with " + s);
     }

     public static SyntaxException lessEquExc(Operable a, Operable b) {
          String sa = a.getClass().getTypeName();
          sa = sa.substring(0, sa.length() - 5);
          String s = b.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(sa + " value not support '<=' operator with " + s);
     }

     public static SyntaxException equalsExc(Operable a, Operable b) {
          String sa = a.getClass().getTypeName();
          sa = sa.substring(0, sa.length() - 5);
          String s = b.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(sa + " value not support '==' operator with " + s);
     }

     public static SyntaxException notEqualsExc(Operable a, Operable b) {
          String sa = a.getClass().getTypeName();
          sa = sa.substring(0, sa.length() - 5);
          String s = b.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(sa + " value not support '!=' operator with " + s);
     }

     public static SyntaxException invExc(Operable type) {
          String s = type.getClass().getTypeName();
          s = s.substring(0, s.length() - 5);
          return new SyntaxException(s + " cannot be inversed");
     }
}

package pixie.parser.values;

import pixie.parser.Operable;
import pixie.parser.SyntaxException;

public class TextValue implements Operable<String> {
     public String value;

     public TextValue(String value){
          this.value = value;
     }

     @Override
     public String get() {
          return value;
     }

     @Override
     public Operable add(Operable other) {
          var getter = other.get();
          return new TextValue(value + getter);
     }

     @Override
     public Operable sub(Operable other) throws SyntaxException {
          throw new SyntaxException("Text value not support '-' operator");
     }

     @Override
     public Operable mul(Operable other) throws SyntaxException {
          throw new SyntaxException("Text value not support '*' operator");
     }

     @Override
     public Operable div(Operable other) throws SyntaxException {
          throw new SyntaxException("Text value not support '/' operator");
     }

     @Override
     public Operable pow(Operable other) throws SyntaxException {
          throw new SyntaxException("Text value not support '**' operator");
     }

     @Override
     public Operable and(Operable other) throws SyntaxException {
          throw new SyntaxException("Text value not support '&&' operator");
     }

     @Override
     public Operable or(Operable other) throws SyntaxException {
          throw new SyntaxException("Text value not support '||' operator");
     }

     @Override
     public BoolValue more(Operable other) throws SyntaxException {
          throw new SyntaxException("Text value not support '>' operator");
     }

     @Override
     public BoolValue less(Operable other) throws SyntaxException {
          throw new SyntaxException("Text value not support '<' operator");
     }

     @Override
     public BoolValue moreEqu(Operable other) throws SyntaxException {
          throw new SyntaxException("Text value not support '>=' operator");
     }

     @Override
     public BoolValue lessEqu(Operable other) throws SyntaxException {
          throw new SyntaxException("Text value not support '<=' operator");
     }

     @Override
     public BoolValue equals(Operable other) {
          var getter = other.get();
          return new BoolValue(value == getter);
     }

     @Override
     public BoolValue notEquals(Operable other) {
          var getter = other.get();
          return new BoolValue(value != getter);
     }

     @Override
     public Operable inv() throws SyntaxException {
          throw new SyntaxException("Text value not support '!' operator");
     }
}

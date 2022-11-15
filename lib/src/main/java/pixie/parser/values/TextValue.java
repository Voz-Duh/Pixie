package pixie.parser.values;

import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;

public class TextValue implements Operable<String> {
     public String value;

     public TextValue(String value){
          this.value = value;
     }

     @Override
     public String get(LineParser parser) {
          return value;
     }

     @Override
     public Operable add(Operable other, LineParser parser) {
          var getter = other.get(parser);
          return new TextValue(value + getter);
     }

     @Override
     public Operable sub(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Text value not support '-' operator");
     }

     @Override
     public Operable mul(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Text value not support '*' operator");
     }

     @Override
     public Operable div(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Text value not support '/' operator");
     }

     @Override
     public Operable pow(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Text value not support '**' operator");
     }

     @Override
     public Operable and(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Text value not support '&&' operator");
     }

     @Override
     public Operable or(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Text value not support '||' operator");
     }

     @Override
     public BoolValue more(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Text value not support '>' operator");
     }

     @Override
     public BoolValue less(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Text value not support '<' operator");
     }

     @Override
     public BoolValue moreEqu(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Text value not support '>=' operator");
     }

     @Override
     public BoolValue lessEqu(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Text value not support '<=' operator");
     }

     @Override
     public BoolValue equals(Operable other, LineParser parser) {
          var getter = other.get(parser);
          return new BoolValue(value.equals(getter));
     }

     @Override
     public BoolValue notEquals(Operable other, LineParser parser) {
          var getter = other.get(parser);
          return new BoolValue(!value.equals(getter));
     }

     @Override
     public Operable inv(LineParser parser) throws SyntaxException {
          throw new SyntaxException("Text value not support '!' operator");
     }
}

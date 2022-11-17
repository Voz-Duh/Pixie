package pixie.parser.values;

import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;

public class FunctionValue implements Operable<String> {
     public String[] arguments;
     public String[] movables;
     public String value;

     public FunctionValue(String code, String[] arguments, String[] movables) {
          value = code;
          this.arguments = arguments;
          this.movables = movables;
     }

     @Override
     public String get(LineParser parser) {
          return value;
     }

     @Override
     public Operable add(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Lambda value not support '+' operator");
     }

     @Override
     public Operable sub(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Lambda value not support '-' operator");
     }

     @Override
     public Operable mul(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Lambda value not support '*' operator");
     }

     @Override
     public Operable div(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Lambda value not support '/' operator");
     }

     @Override
     public Operable pow(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Lambda value not support '**' operator");
     }

     @Override
     public Operable and(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Lambda value not support '&&' operator");
     }

     @Override
     public Operable or(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Lambda value not support '||' operator");
     }

     @Override
     public BoolValue more(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Lambda value not support '>' operator");
     }

     @Override
     public BoolValue less(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Lambda value not support '<' operator");
     }

     @Override
     public BoolValue moreEqu(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Lambda value not support '>=' operator");
     }

     @Override
     public BoolValue lessEqu(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Lambda value not support '<=' operator");
     }

     @Override
     public BoolValue equals(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Lambda value not support '==' operator");
     }

     @Override
     public BoolValue notEquals(Operable other, LineParser parser) throws SyntaxException {
          throw new SyntaxException("Lambda value not support '!=' operator");
     }

     @Override
     public Operable inv(LineParser parser) throws SyntaxException {
          throw new SyntaxException("Lambda value not support '!' operator");
     }
}

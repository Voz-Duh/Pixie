package pixie.parser.values;

import pixie.parser.LineParser;
import pixie.parser.Operable;

public class Function {
     public Argument[] arguments;
     public Code code;

     public Function(Argument[] arguments, Code code) {
          this.arguments = arguments;
          this.code = code;
     }

     public static class Code {
          public String code;
          public java.util.function.Function<LineParser, Operable> lamda;

          public Code(String code, java.util.function.Function<LineParser, Operable> lamda) {
               this.code = code;
               this.lamda = lamda;
          }
     }
}

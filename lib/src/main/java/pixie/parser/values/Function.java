package pixie.parser.values;

import pixie.parser.LineParser;
import pixie.parser.Operable;

public class Function {
     public String[] arguments;
     public Code code;

     public Function(String[] arguments, Code code) {
          this.arguments = arguments;
          this.code = code;
     }

     public static class Code {
          public String code;
          public Function3<String, String[], LineParser, Operable> lamda;

          public Code(String code, Function3<String, String[], LineParser, Operable> lamda) {
               this.code = code;
               this.lamda = lamda;
          }
     }

     @FunctionalInterface
     public interface Function3<T0, T1, T2, R> {
          public R apply(T0 t0, T1 t1, T2 t2);
     }
}

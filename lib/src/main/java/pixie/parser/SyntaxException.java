package pixie.parser;

public class SyntaxException extends Exception {
     public static int line = 0;

     public SyntaxException(String error) {
          super(error + " on line " + (line + 1));
     }

     public SyntaxException(String error, int line) {
          super(error + " on line " + (line + 1));
     }
}

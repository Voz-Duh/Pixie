package pixie.parser;

public class SyntaxException extends Exception {
     public static String file;
     public static int line = 0;

     public SyntaxException(String error) {
          super("Pixie executor in " + file + ": \n         " + error + " on line " + (line + 1));
     }

     public SyntaxException(String error, int line) {
          super("Pixie executor in " + file + ": \n         " + error + " on line " + (line + 1));
     }
}

package pixie;

import java.io.*;

import pixie.parser.*;
import pixie.parser.modules.PixieModule;

public class Pixie {
     public static String result = "";

     public static void addModule(String name, PixieModule module) {
          LineParser.modules.put(name, module);
     }

     public static void execute(String path) {
          try {
               if (!path.endsWith("." + LineParser.executableType))
                    throw new SyntaxException("Pixie cannot execute not ." + LineParser.executableType, 0);

               File file = new File(path);

               SyntaxException.file = file.getName();

               BufferedReader br = new BufferedReader(new FileReader(file));

               br.lines().forEach(s -> result += s + '\n');

               LineParser parser = new LineParser(result, 0);
               parser.parse();
          } catch (SyntaxException | FileNotFoundException syntaxException) {
               System.err.println(syntaxException.getMessage());
          }
     }

     public static void executeResource(String path, Class importer) {
          execute(importer.getClassLoader().getResource(path).getPath());
     }

     public static void main(String[] args) {
          execute(args[0]);
     }
}
package pixie;

import java.io.*;

import pixie.parser.*;
import pixie.parser.modules.PixieModule;

public class Pixie {
     public static String result = "";

     public static void addModule(String name, PixieModule module) {
          LineParser.modules.put(name, module);
     }

     public static void execute(String path) throws SyntaxException, IOException {
          if (!path.endsWith("." + LineParser.executableType))
               throw new SyntaxException("Pixie cannot execute not ." + LineParser.executableType, 0);

          File file = new File(path);

          BufferedReader br = new BufferedReader(new FileReader(file));

          br.lines().forEach(s -> result += s + '\n');

          var parser = new LineParser(result);
          parser.parse();
     }

     public static void executeResource(String path, Class importer) throws SyntaxException, IOException {
          execute(importer.getClassLoader().getResource("test.px").getPath());
     }
}
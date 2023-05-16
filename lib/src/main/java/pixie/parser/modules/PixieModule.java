package pixie.parser.modules;


import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;
import pixie.parser.values.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PixieModule {
     public Map<String, Class> values = LineParser.ofEntries();
     public Map<String, Operable> variables = LineParser.ofEntries();
     public Map<String, Function> functions = LineParser.ofEntries();

     public static Map.Entry function(String name, int args, Function.Function3<String, String[], LineParser, Operable> function) {
          return LineParser.entry(name + args, new Function(new String[0], new Function.Code("", function)));
     }

     public static String[] get(String line, String[] words) throws SyntaxException {
          line = LineParser.rWord(line, words, 0);
          return LineParser.getInsideBrackets(line).split(",");
     }

     public static float parseNum(LineParser self, String value) throws SyntaxException {
          return ((NumValue) self.parseValue("", value).value).get(self);
     }

     public static boolean parseBool(LineParser self, String value) throws SyntaxException {
          return ((BoolValue) self.parseValue("", value).value).get(self);
     }

     public static String parseText(LineParser self, String value) throws SyntaxException {
          return ((TextValue) self.parseValue("", value).value).get(self);
     }

     public static Map<String, Operable> parseInst(LineParser self, String value) throws SyntaxException {
          return ((InstanceValue) self.parseValue("", value).value).get(self);
     }
}

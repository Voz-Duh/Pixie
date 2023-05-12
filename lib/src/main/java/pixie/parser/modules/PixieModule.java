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

     public static Function function(Function.Function3<String, String[], LineParser, Operable> function) {
          return new Function(new String[0], new Function.Code("", function));
     }

     public static String[] base(LineParser self, String name) throws SyntaxException {
          return self.getInsideBrackets(self.getNextString(0, self.line, LineParser.listOf(name))[0].length() + name.length(), self.line).split(",");
     }

     public static String one(LineParser self, String name) throws SyntaxException {
          return self.getInsideBrackets(self.getNextString(0, self.line, LineParser.listOf(name))[0].length() + name.length(), self.line);
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

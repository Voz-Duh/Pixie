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
     //TODO: Class support
     //public Map<String, ClassConstructor> classes = LineParser.ofEntries();

     public static Map.Entry function(String name, int args, Function.Function3<String, String[], LineParser, Operable> function) {
          return LineParser.entry(name + args, new Function(new String[0], new Function.Code("", function)));
     }

     //TODO: Class support
     /*
     public static Map.Entry _class(String name, Map<String, Function> function, Map<String, Function> staticFunctions, Map variables, Map staticVariables) {
          return LineParser.entry(name, new ClassConstructor(name, function, staticFunctions, variables, staticVariables));
     }
     */

     public static String[] get(String line, String[] words) throws SyntaxException {
          return LineParser.split(LineParser.getInsideBrackets(line), ',');
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

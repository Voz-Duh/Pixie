package pixie.parser.values;

import pixie.parser.Operable;

import java.util.HashMap;
import java.util.Map;

public class ClassConstructor {

     public Map<String, Operable> variables, staticVariables;
     public Map<String, Function> functions, staticFunctions;

     public ClassConstructor(Map function, Map staticFunctions, Map variables, Map staticVariables) {
          this.staticFunctions = new HashMap(staticFunctions);
          this.variables = new HashMap(variables);
          this.staticVariables = new HashMap(staticVariables);
          this.functions = new HashMap(function);
     }
}

package pixie.parser.values;

import pixie.parser.Operable;

import java.util.HashMap;
import java.util.Map;

public class ClassConstructor implements VariableContainer {
     public String name;
     public Map<String, Operable> variables, staticVariables;
     public Map<String, Function> functions, staticFunctions;

     public ClassConstructor(String name, Map<String, Function> function, Map<String, Function> staticFunctions, Map variables, Map staticVariables) {
          this.name = name;
          this.staticFunctions = new HashMap(staticFunctions);
          this.variables = new HashMap(variables);
          this.staticVariables = new HashMap(staticVariables);
          this.functions = new HashMap(function);
     }

     @Override
     public Map<String, Operable> getVar() {
          return staticVariables;
     }

     @Override
     public Map<String, Function> getFunc() {
          return staticFunctions;
     }
}

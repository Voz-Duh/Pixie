package pixie.parser.values;

import pixie.parser.Operable;

import java.util.Map;

public interface VariableContainer {
     public Map<String, Operable> getVar();
     public Map<String, Function> getFunc();
}

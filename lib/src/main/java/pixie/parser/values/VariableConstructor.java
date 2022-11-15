package pixie.parser.values;

import pixie.parser.Operable;

public class VariableConstructor {
     public boolean isStatic;
     public Operable value;

     public VariableConstructor(Operable value, boolean isStatic) {
          this.value = value;
          this.isStatic = isStatic;
     }
}

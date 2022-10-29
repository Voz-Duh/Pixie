package pixie.parser.modules;

import pixie.parser.LineParser;
import pixie.parser.SyntaxException;
import pixie.parser.values.NumValue;

import java.util.Map;
import java.util.Random;

public class MathModule extends PixieModule {
     public MathModule() {
          variables = Map.ofEntries(
                  Map.entry("pi", new NumValue((float) Math.PI))
          );
          functions = Map.ofEntries(
                  Map.entry("sin",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "sin");
                                            return new NumValue((float) Math.sin(parseNum(self, inside)));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("cos",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "cos");
                                            return new NumValue((float) Math.cos(parseNum(self, inside)));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("dsin",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "dsin");
                                            return new NumValue((float) Math.sin(Math.toRadians(parseNum(self, inside))));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("dcos",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "dcos");
                                            return new NumValue((float) Math.cos(Math.toRadians(parseNum(self, inside))));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("asin",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "asin");
                                            return new NumValue((float) Math.asin(parseNum(self, inside)));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("acos",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "caos");
                                            return new NumValue((float) Math.acos(parseNum(self, inside)));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("dasin",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "dasin");
                                            return new NumValue((float) Math.asin(Math.toRadians(parseNum(self, inside))));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("dacos",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "dacos");
                                            return new NumValue((float) Math.acos(Math.toRadians(parseNum(self, inside))));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("sinh",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "sinh");
                                            return new NumValue((float) Math.sinh(parseNum(self, inside)));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("cosh",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "cosh");
                                            return new NumValue((float) Math.cosh(parseNum(self, inside)));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("dsinh",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "dsinh");
                                            return new NumValue((float) Math.sinh(Math.toRadians(parseNum(self, inside))));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("dcosh",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "dcosh");
                                            return new NumValue((float) Math.cosh(Math.toRadians(parseNum(self, inside))));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("rad",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "rad");
                                            return new NumValue((float) Math.toRadians(parseNum(self, inside)));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("deg",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "deg");
                                            return new NumValue((float) Math.toDegrees(parseNum(self, inside)));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("tan",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "tan");
                                            return new NumValue((float) Math.tan(parseNum(self, inside)));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("atan2",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = base(self, "atan2");
                                            return new NumValue((float) Math.atan2(parseNum(self, inside[0]), parseNum(self, inside[0])));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("atan",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "atan");
                                            return new NumValue((float) Math.atan(parseNum(self, inside)));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("clamp",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = base(self, "clamp");
                                            return new NumValue(Math.max(Math.min(parseNum(self, inside[0]), parseNum(self, inside[2])), parseNum(self, inside[1])));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("max",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = base(self, "max");
                                            return new NumValue(Math.max(parseNum(self, inside[0]), parseNum(self, inside[2])));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("min",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = base(self, "min");
                                            return new NumValue(Math.min(parseNum(self, inside[0]), parseNum(self, inside[1])));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("lerp_deg",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = base(self, "lerp_deg");
                                            return new NumValue(angleLerp((float) Math.toRadians(parseNum(self, inside[0])), (float) Math.toRadians(parseNum(self, inside[1])), parseNum(self, inside[2])));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("lerp_rad",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = base(self, "lerp_rad");
                                            return new NumValue(angleLerp(parseNum(self, inside[0]), parseNum(self, inside[1]), parseNum(self, inside[2])));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("lerp",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = base(self, "lerp");
                                            return new NumValue(lerp(parseNum(self, inside[0]), parseNum(self, inside[1]), parseNum(self, inside[2])));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("random",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = base(self, "random");
                                            if (inside.length == 0)
                                                 return new NumValue(random());
                                            else if (inside.length == 1)
                                                 return new NumValue(random(parseNum(self, inside[0])));
                                            else
                                                 return new NumValue(random(parseNum(self, inside[0]), parseNum(self, inside[1])));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("floor",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "floor");
                                            return new NumValue((float) Math.floor(parseNum(self, inside)));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("ceil",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "ceil");
                                            return new NumValue((float) Math.ceil(parseNum(self, inside)));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("round",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "round");
                                            return new NumValue((float) Math.round(parseNum(self, inside)));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("sqrt",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "sqrt");
                                            return new NumValue((float) Math.sqrt(parseNum(self, inside)));
                                       } catch (SyntaxException e) {
                                            e.printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  )
          );
     }

     public static Random random = new Random();

     public static float random() {
          return random.nextFloat();
     }

     public static float random(float to) {
          return random.nextFloat() * to;
     }

     public static float random(float from, float to) {
          return from + (random.nextFloat() * (to - from));
     }

     public static float lerp(float v0, float v1, float t) {
          return (1 - t) * v0 + t * v1;
     }

     public static float angleLerp(float v0, float v1, float t) {
          return (float) Math.toDegrees(v0 + shortAngleDist(v0, v1) * t);
     }

     public static float shortAngleDist(float v0, float v1) {
          float max = (float) Math.PI * 2;
          float da = (v1 - v0) % max;
          return 2 * da % max - da;
     }
}

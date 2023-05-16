package pixie.parser.modules;

import pixie.parser.LineParser;
import pixie.parser.SyntaxException;
import pixie.parser.values.NumValue;

import java.util.Random;

public class MathModule extends PixieModule {
     public MathModule() {
          variables = LineParser.ofEntries(
                  LineParser.entry("pi", new NumValue((float) Math.PI))
          );
          functions = LineParser.ofEntries(
                  function("sin", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.sin(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("cos", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.cos(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("dsin", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.sin(Math.toRadians(parseNum(p, inside[0]))));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("dcos", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.cos(Math.toRadians(parseNum(p, inside[0]))));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("asin", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.asin(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("acos", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.acos(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("dasin", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.asin(Math.toRadians(parseNum(p, inside[0]))));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("dacos", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.acos(Math.toRadians(parseNum(p, inside[0]))));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("sinh", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.sinh(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("cosh", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.cosh(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("dsinh", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.sinh(Math.toRadians(parseNum(p, inside[0]))));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("dcosh", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.cosh(Math.toRadians(parseNum(p, inside[0]))));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("rad", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.toRadians(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("deg", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.toDegrees(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("tan", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.tan(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("atan2", 2,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.atan2(parseNum(p, inside[0]), parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("atan", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.atan(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("clamp", 3,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue(Math.max(Math.min(parseNum(p, inside[0]), parseNum(p, inside[2])), parseNum(p, inside[1])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("max", 2,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue(Math.max(parseNum(p, inside[0]), parseNum(p, inside[2])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("min", 2,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue(Math.min(parseNum(p, inside[0]), parseNum(p, inside[1])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("lerp_deg", 3,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue(angleLerp((float) Math.toRadians(parseNum(p, inside[0])), (float) Math.toRadians(parseNum(p, inside[1])), parseNum(p, inside[2])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("lerp_rad", 3,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue(angleLerp(parseNum(p, inside[0]), parseNum(p, inside[1]), parseNum(p, inside[2])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("lerp", 3,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue(lerp(parseNum(p, inside[0]), parseNum(p, inside[1]), parseNum(p, inside[2])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("random", 2,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);

                                    return new NumValue(random(parseNum(p, inside[0]), parseNum(p, inside[1])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("random", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);

                                    return new NumValue(random(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("random", 0,
                          (String line, String[] words, LineParser p) -> new NumValue(random())
                  ),
                  function("floor", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.floor(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("ceil", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.ceil(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("round", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.round(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("sqrt", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new NumValue((float) Math.sqrt(parseNum(p, inside[0])));
                               } catch (SyntaxException e) {
                                    e.printStackTrace();
                               }
                               return null;
                          }
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

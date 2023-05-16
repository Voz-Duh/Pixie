package pixie.parser.modules;


import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;
import pixie.parser.values.BoolValue;
import pixie.parser.values.InstanceValue;
import pixie.parser.values.NullValue;
import pixie.parser.values.TextValue;

import java.io.*;
import java.util.Map;

import static pixie.parser.values.NullValue.*;

public class FileModule extends PixieModule {
     public FileModule() {
          values = LineParser.ofEntries(
                  LineParser.entry("file", FileValue.class)
          );
          functions = LineParser.ofEntries(
                  function("get_file", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new FileValue(new File(parseText(p, inside[0])));
                               } catch (SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("have_file", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);

                                    try {
                                         return new BoolValue(parseFile(p, inside[0]).exists());
                                    } catch (Exception e) {
                                         return new BoolValue(new File(parseText(p, inside[0])).exists());
                                    }
                               } catch (SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("have_file", 2,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    StringBuilder result = new StringBuilder();

                                    File file = parseFile(p, inside[1]);
                                    if (file.isFile()) result.append(file.getPath()).append('\\');
                                    else result.append(file.getAbsoluteFile()).append('\\');

                                    return new BoolValue(new File(result + parseText(p, inside[0])).exists());
                               } catch (SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("create_file", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    File newFile = new File(parseText(p, inside[0]));
                                    return new FileValue(newFile);
                               } catch (SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("create_file", 2,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    StringBuilder result = new StringBuilder();

                                    File file = parseFile(p, inside[1]);
                                    if (file.isFile()) result.append(file.getPath()).append('\\');
                                    else result.append(file.getAbsoluteFile()).append('\\');

                                    File newFile = new File(result + parseText(p, inside[0]));
                                    return new FileValue(newFile);
                               } catch (SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("read_file", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);

                                    final StringBuilder result = new StringBuilder();
                                    BufferedReader br = new BufferedReader(new FileReader(parseFile(p, inside[0])));

                                    br.lines().forEach(s -> result.append(s).append('\n'));

                                    return new TextValue(result.toString());
                               } catch (SyntaxException | FileNotFoundException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("delete_file", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    parseFile(p, inside[0]).delete();
                               } catch (SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("is_folder", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new BoolValue(parseFile(p, inside[0]).isDirectory());
                               } catch (SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("is_file", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new BoolValue(parseFile(p, inside[0]).isFile());
                               } catch (SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("file_name", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new TextValue(parseFile(p, inside[0]).getName());
                               } catch (SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("file_path", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    return new TextValue(parseFile(p, inside[0]).getPath());
                               } catch (SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("get_files", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    File[] files = parseFile(p, inside[0]).listFiles();
                                    Map.Entry[] entries = new Map.Entry[files.length];
                                    for (int i = 0; i < files.length; i++) {
                                         entries[i] = LineParser.entry("f" + i, new FileValue(files[i]));
                                    }

                                    return new InstanceValue(entries);
                               } catch (SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("is_executable", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    File file = parseFile(p, inside[0]);
                                    return new BoolValue(file.isFile() && file.getName().endsWith("." + LineParser.executableType));
                               } catch (SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("execute", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);

                                    final StringBuilder result = new StringBuilder();
                                    BufferedReader br = new BufferedReader(new FileReader(parseFile(p, inside[0])));

                                    br.lines().forEach(s -> result.append(s).append('\n'));

                                    LineParser.Result parse_result = p.parse(result.toString());

                                    p.variables.putAll(parse_result.vars);
                                    p.functions.putAll(parse_result.functs);
                               } catch (SyntaxException | FileNotFoundException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("print_to_file", 2,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);
                                    File file = parseFile(p, inside[0]);

                                    FileWriter myWriter = new FileWriter(file);
                                    myWriter.write(parseText(p, inside[1]));
                                    myWriter.close();
                               } catch (SyntaxException | IOException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  )
          );
     }

     public static File parseFile(LineParser self, String value) throws SyntaxException {
          return ((FileValue) self.parseValue("", value).value).get(self);
     }

     public class FileValue implements Operable<File> {
          public File value;

          public FileValue(File file) {
               value = file;
          }

          @Override
          public File get(LineParser parser) {
               return value;
          }

          @Override
          public NullValue add(Operable other, LineParser parser) throws SyntaxException {
               throw addExc(this, other);
          }

          @Override
          public NullValue sub(Operable other, LineParser parser) throws SyntaxException {
               throw subExc(this, other);
          }

          @Override
          public NullValue mul(Operable other, LineParser parser) throws SyntaxException {
               throw mulExc(this, other);
          }

          @Override
          public NullValue div(Operable other, LineParser parser) throws SyntaxException {
               throw divExc(this, other);
          }

          @Override
          public Operable pow(Operable other, LineParser parser) throws SyntaxException {
               throw powExc(this, other);
          }

          @Override
          public Operable mod(Operable other, LineParser parser) throws SyntaxException {
               throw modExc(this, other);
          }

          @Override
          public NullValue and(Operable other, LineParser parser) throws SyntaxException {
               throw andExc(this, other);
          }

          @Override
          public NullValue or(Operable other, LineParser parser) throws SyntaxException {
               throw orExc(this, other);
          }

          @Override
          public BoolValue more(Operable other, LineParser parser) throws SyntaxException {
               throw moreExc(this, other);
          }

          @Override
          public BoolValue less(Operable other, LineParser parser) throws SyntaxException {
               throw lessExc(this, other);
          }

          @Override
          public BoolValue moreEqu(Operable other, LineParser parser) throws SyntaxException {
               throw moreEquExc(this, other);
          }

          @Override
          public BoolValue lessEqu(Operable other, LineParser parser) throws SyntaxException {
               throw lessEquExc(this, other);
          }

          @Override
          public BoolValue equals(Operable other, LineParser parser) throws SyntaxException {
               throw equalsExc(this, other);
          }

          @Override
          public BoolValue notEquals(Operable other, LineParser parser) throws SyntaxException {
               throw notEqualsExc(this, other);
          }

          @Override
          public NullValue inv(LineParser parser) throws SyntaxException {
               throw invExc(this);
          }
     }
}

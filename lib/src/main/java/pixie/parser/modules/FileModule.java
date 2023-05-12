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
                  LineParser.entry("get_file",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String inside = one(self, "get_file");
                                            return new FileValue(new File(parseText(self, inside)));
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  LineParser.entry("have_file",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String[] inside = base(self, "have_file");
                                            StringBuilder result = new StringBuilder();
                                            if (inside.length == 2) {
                                                 File file = parseFile(self, inside[1]);
                                                 if (file.isFile()) result.append(file.getPath()).append('\\');
                                                 else result.append(file.getAbsoluteFile()).append('\\');

                                                 return new BoolValue(new File(result + parseText(self, inside[0])).exists());
                                            }
                                            if (inside.length == 1) {
                                                 try {
                                                      return new BoolValue(parseFile(self, inside[0]).exists());
                                                 } catch (Exception e) {
                                                      return new BoolValue(new File(parseText(self, inside[0])).exists());
                                                 }
                                            }
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  LineParser.entry("create_file",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String[] inside = base(self, "create_file");
                                            StringBuilder result = new StringBuilder();
                                            if (inside.length == 2) {
                                                 File file = parseFile(self, inside[1]);
                                                 if (file.isFile()) result.append(file.getPath()).append('\\');
                                                 else result.append(file.getAbsoluteFile()).append('\\');
                                            }
                                            File newFile = new File(result + parseText(self, inside[0]));
                                            boolean success = newFile.createNewFile();
                                            return new FileValue(newFile);
                                       } catch (SyntaxException | IOException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  LineParser.entry("read_file",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String inside = one(self, "read_file");

                                            final StringBuilder result = new StringBuilder();
                                            BufferedReader br = new BufferedReader(new FileReader(parseFile(self, inside)));

                                            br.lines().forEach(s -> result.append(s).append('\n'));

                                            return new TextValue(result.toString());
                                       } catch (SyntaxException | FileNotFoundException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  LineParser.entry("delete_file",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String inside = one(self, "delete_file");
                                            parseFile(self, inside).delete();
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  LineParser.entry("is_folder",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String inside = one(self, "is_folder");
                                            return new BoolValue(parseFile(self, inside).isDirectory());
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  LineParser.entry("is_file",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String inside = one(self, "is_file");
                                            return new BoolValue(parseFile(self, inside).isFile());
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  LineParser.entry("file_name",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String inside = one(self, "file_name");
                                            return new TextValue(parseFile(self, inside).getName());
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  LineParser.entry("file_path",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String inside = one(self, "file_path");
                                            return new TextValue(parseFile(self, inside).getPath());
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  LineParser.entry("get_files",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String inside = one(self, "get_files");
                                            File[] files = parseFile(self, inside).listFiles();
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
                          )
                  ),
                  LineParser.entry("is_executable",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String inside = one(self, "is_executable");
                                            File file = parseFile(self, inside);
                                            return new BoolValue(file.isFile() && file.getName().endsWith("." + LineParser.executableType));
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  LineParser.entry("execute",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String inside = one(self, "execute");

                                            final StringBuilder result = new StringBuilder();
                                            BufferedReader br = new BufferedReader(new FileReader(parseFile(self, inside)));

                                            br.lines().forEach(s -> result.append(s).append('\n'));

                                            LineParser.Result parse_result = self.parse(result.toString());

                                            self.variables.putAll(parse_result.vars);
                                            self.functions.putAll(parse_result.functs);
                                       } catch (SyntaxException | FileNotFoundException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  LineParser.entry("print_to_file",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String[] inside = base(self, "print_to_file");
                                            File file = parseFile(self, inside[0]);

                                            FileWriter myWriter = new FileWriter(file);
                                            myWriter.write(parseText(self, inside[1]));
                                            myWriter.close();
                                       } catch (SyntaxException | IOException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
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

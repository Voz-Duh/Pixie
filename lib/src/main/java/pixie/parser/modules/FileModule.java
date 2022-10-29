package pixie.parser.modules;


import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;
import pixie.parser.values.BoolValue;
import pixie.parser.values.InstanceValue;
import pixie.parser.values.TextValue;

import java.io.*;
import java.util.Map;

public class FileModule extends PixieModule {
     public FileModule() {
          variables = Map.ofEntries();
          functions = Map.ofEntries(
                  Map.entry("get_file",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "get_file");
                                            return new FileValue(new File(parseText(self, inside)));
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("have_file",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = base(self, "have_file");
                                            StringBuilder result = new StringBuilder();
                                            if (inside.length == 2) {
                                                 var file = parseFile(self, inside[1]);
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
                  Map.entry("create_file",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = base(self, "create_file");
                                            StringBuilder result = new StringBuilder();
                                            if (inside.length == 2) {
                                                 var file = parseFile(self, inside[1]);
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
                  Map.entry("read_file",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "read_file");

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
                  Map.entry("delete_file",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "delete_file");
                                            parseFile(self, inside).delete();
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("is_folder",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "is_folder");
                                            return new BoolValue(parseFile(self, inside).isDirectory());
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("is_file",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "is_file");
                                            return new BoolValue(parseFile(self, inside).isFile());
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("file_name",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "file_name");
                                            return new TextValue(parseFile(self, inside).getName());
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("file_path",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "file_path");
                                            return new TextValue(parseFile(self, inside).getPath());
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("get_files",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "get_files");
                                            var files = parseFile(self, inside).listFiles();
                                            Map.Entry[] entries = new Map.Entry[files.length];
                                            for (int i = 0; i < files.length; i++) {
                                                 entries[i] = Map.entry("f" + i, new FileValue(files[i]));
                                            }

                                            return new InstanceValue(entries);
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("is_executable",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "is_executable");
                                            var file = parseFile(self, inside);
                                            return new BoolValue(file.isFile() && file.getName().endsWith("." + LineParser.executableType));
                                       } catch (SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("execute",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "execute");

                                            final StringBuilder result = new StringBuilder();
                                            BufferedReader br = new BufferedReader(new FileReader(parseFile(self, inside)));

                                            br.lines().forEach(s -> result.append(s).append('\n'));

                                            var parse_result = self.parse(result.toString());

                                            self.variables.putAll(parse_result.vars);
                                            self.functions.putAll(parse_result.functs);
                                       } catch (SyntaxException | FileNotFoundException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("print_to_file",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = base(self, "print_to_file");
                                            var file = parseFile(self, inside[0]);

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
          return ((FileValue) self.parseValue("", value).value).get();
     }

     public class FileValue implements Operable<File> {
          public File value;

          public FileValue(File file) {
               value = file;
          }

          @Override
          public File get() {
               return value;
          }

          @Override
          public Operable add(Operable other) throws SyntaxException {
               throw new SyntaxException("Instance value not support '+' operator");
          }

          @Override
          public Operable sub(Operable other) throws SyntaxException {
               throw new SyntaxException("Instance value not support '-' operator");
          }

          @Override
          public Operable mul(Operable other) throws SyntaxException {
               throw new SyntaxException("Instance value not support '*' operator");
          }

          @Override
          public Operable div(Operable other) throws SyntaxException {
               throw new SyntaxException("Instance value not support '/' operator");
          }

          @Override
          public Operable pow(Operable other) throws SyntaxException {
               throw new SyntaxException("Instance value not support '**' operator");
          }

          @Override
          public Operable and(Operable other) throws SyntaxException {
               throw new SyntaxException("Instance value not support '&&' operator");
          }

          @Override
          public Operable or(Operable other) throws SyntaxException {
               throw new SyntaxException("Instance value not support '||' operator");
          }

          @Override
          public BoolValue more(Operable other) throws SyntaxException {
               throw new SyntaxException("Instance value not support '>' operator");
          }

          @Override
          public BoolValue less(Operable other) throws SyntaxException {
               throw new SyntaxException("Instance value not support '<' operator");
          }

          @Override
          public BoolValue moreEqu(Operable other) throws SyntaxException {
               throw new SyntaxException("Instance value not support '>=' operator");
          }

          @Override
          public BoolValue lessEqu(Operable other) throws SyntaxException {
               throw new SyntaxException("Instance value not support '<=' operator");
          }

          @Override
          public BoolValue equals(Operable other) throws SyntaxException {
               throw new SyntaxException("Instance value not support '==' operator");
          }

          @Override
          public BoolValue notEquals(Operable other) throws SyntaxException {
               throw new SyntaxException("Instance value not support '!=' operator");
          }

          @Override
          public Operable inv() throws SyntaxException {
               throw new SyntaxException("Instance value not support '!' operator");
          }
     }
}

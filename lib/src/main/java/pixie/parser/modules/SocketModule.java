package pixie.parser.modules;

import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;
import pixie.parser.values.BoolValue;
import pixie.parser.values.InstanceValue;
import pixie.parser.values.TextValue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class SocketModule extends PixieModule {
     public SocketModule() {
          variables = Map.ofEntries();
          functions = Map.ofEntries(
                  Map.entry("create_socket",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "create_socket");

                                            Socket socket;
                                            if (!LineParser.removeWhitespaces(inside).equals(""))
                                                 socket = new Socket(parseText(self, inside), 1869);
                                            else socket = new Socket("localhost", 1869);

                                            return new SocketValue(socket);
                                       } catch (SyntaxException | IOException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("create_clients",
                          function(
                                  (LineParser self) -> new InstanceValue()
                          )
                  ),
                  Map.entry("create_server",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            return new ServerSocketValue(new ServerSocket(1869));
                                       } catch (IOException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("server_accept",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = base(self, "server_accept");

                                            Socket s = parseServer(self, inside[0]).accept();
                                            parseInst(self, inside[1]).put(s.getRemoteSocketAddress().toString(), new SocketValue(s));
                                            DataInputStream dis = new DataInputStream(s.getInputStream());
                                            return new TextValue(dis.readUTF());
                                       } catch (IOException | SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("server_send",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = base(self, "server_send");

                                            var sockets = parseInst(self, inside[0]);
                                            for (var socket : sockets.values()) {
                                                 DataOutputStream dout = new DataOutputStream(((SocketValue) socket).get().getOutputStream());
                                                 dout.writeUTF(parseText(self, inside[1]));
                                                 dout.flush();
                                            }
                                       } catch (IOException | SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("socket_send",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = base(self, "socket_send");

                                            Socket s = parseSocket(self, inside[0]);
                                            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                                            dout.writeUTF(parseText(self, inside[1]));
                                            dout.flush();
                                       } catch (IOException | SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  ),
                  Map.entry("socket_get",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            var inside = one(self, "socket_get");

                                            Socket s = parseSocket(self, inside);
                                            DataInputStream din = new DataInputStream(s.getInputStream());
                                            return new TextValue(din.readUTF());
                                       } catch (IOException | SyntaxException e) {
                                            new SyntaxException(e.getMessage()).printStackTrace();
                                       }
                                       return null;
                                  }
                          )
                  )
          );
     }

     public static ServerSocket parseServer(LineParser self, String value) throws SyntaxException {
          return ((ServerSocketValue) self.parseValue("", value).value).get();
     }

     public class ServerSocketValue implements Operable<ServerSocket> {
          public ServerSocket value;

          public ServerSocketValue(ServerSocket serverSocket) {
               value = serverSocket;
          }

          @Override
          public ServerSocket get() {
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


     public static Socket parseSocket(LineParser self, String value) throws SyntaxException {
          return ((SocketValue) self.parseValue("", value).value).get();
     }

     public class SocketValue implements Operable<Socket> {
          public Socket value;

          public SocketValue(Socket socket) {
               value = socket;
          }

          @Override
          public Socket get() {
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

package pixie.parser.modules;

import pixie.parser.LineParser;
import pixie.parser.Operable;
import pixie.parser.SyntaxException;
import pixie.parser.values.BoolValue;
import pixie.parser.values.InstanceValue;
import pixie.parser.values.NullValue;
import pixie.parser.values.TextValue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import static pixie.parser.values.NullValue.*;

public class SocketModule extends PixieModule {
     public SocketModule() {

          values = LineParser.ofEntries(
                  LineParser.entry("socket", SocketValue.class),
                  LineParser.entry("server", ServerSocketValue.class)
          );
          functions = LineParser.ofEntries(
                  LineParser.entry("create_socket",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String inside = one(self, "create_socket");

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
                  LineParser.entry("create_clients",
                          function(
                                  (LineParser self) -> new InstanceValue()
                          )
                  ),
                  LineParser.entry("create_server",
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
                  LineParser.entry("server_accept",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String[] inside = base(self, "server_accept");

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
                  LineParser.entry("server_send",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String[] inside = base(self, "server_send");

                                            Map<String, Operable> sockets = parseInst(self, inside[0]);
                                            for (Operable socket : sockets.values()) {
                                                 DataOutputStream dout = new DataOutputStream(((SocketValue) socket).get(self).getOutputStream());
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
                  LineParser.entry("socket_send",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String[] inside = base(self, "socket_send");

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
                  LineParser.entry("socket_get",
                          function(
                                  (LineParser self) -> {
                                       try {
                                            String inside = one(self, "socket_get");

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
          return ((ServerSocketValue) self.parseValue("", value).value).get(self);
     }

     public class ServerSocketValue implements Operable<ServerSocket> {
          public ServerSocket value;

          public ServerSocketValue(ServerSocket serverSocket) {
               value = serverSocket;
          }

          @Override
          public ServerSocket get(LineParser parser) {
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


     public static Socket parseSocket(LineParser self, String value) throws SyntaxException {
          return ((SocketValue) self.parseValue("", value).value).get(self);
     }

     public class SocketValue implements Operable<Socket> {
          public Socket value;

          public SocketValue(Socket socket) {
               value = socket;
          }

          @Override
          public Socket get(LineParser parser) {
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

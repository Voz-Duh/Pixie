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
                  function("create_socket", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);

                                    Socket socket;
                                    if (!LineParser.removeWhitespaces(inside[0]).equals(""))
                                         socket = new Socket(parseText(p, inside[0]), 1869);
                                    else socket = new Socket("localhost", 1869);

                                    return new SocketValue(socket);
                               } catch (SyntaxException | IOException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("create_clients", 0,
                          (String line, String[] words, LineParser p) -> new InstanceValue()
                  ),
                  function("create_server", 0,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    return new ServerSocketValue(new ServerSocket(1869));
                               } catch (IOException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("server_accept", 2,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);

                                    Socket s = parseServer(p, inside[0]).accept();
                                    parseInst(p, inside[1]).put(s.getRemoteSocketAddress().toString(), new SocketValue(s));
                                    DataInputStream dis = new DataInputStream(s.getInputStream());
                                    return new TextValue(dis.readUTF());
                               } catch (IOException | SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("server_send", 2,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);

                                    Map<String, Operable> sockets = parseInst(p, inside[0]);
                                    for (Operable socket : sockets.values()) {
                                         DataOutputStream dout = new DataOutputStream(((SocketValue) socket).get(p).getOutputStream());
                                         dout.writeUTF(parseText(p, inside[1]));
                                         dout.flush();
                                    }
                               } catch (IOException | SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("socket_send", 2,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);

                                    Socket s = parseSocket(p, inside[0]);
                                    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                                    dout.writeUTF(parseText(p, inside[1]));
                                    dout.flush();
                               } catch (IOException | SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
                  ),
                  function("socket_get", 1,
                          (String line, String[] words, LineParser p) -> {
                               try {
                                    String[] inside = get(line, words);

                                    Socket s = parseSocket(p, inside[0]);
                                    DataInputStream din = new DataInputStream(s.getInputStream());
                                    return new TextValue(din.readUTF());
                               } catch (IOException | SyntaxException e) {
                                    new SyntaxException(e.getMessage()).printStackTrace();
                               }
                               return null;
                          }
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

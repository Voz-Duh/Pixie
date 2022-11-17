package pixie;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import pixie.parser.*;
import pixie.parser.modules.PixieModule;

import javax.sound.sampled.Line;
import javax.swing.*;
import javax.swing.text.JTextComponent;

public class Pixie {
     public static LineParser parser;
     public static String result = "";

     public static void addModule(String name, PixieModule module) {
          LineParser.modules.put(name, module);
     }

     public static void execute(String path) throws Exception {
          if (!path.endsWith("." + LineParser.executableType))
               throw new Exception("Pixie cannot execute not ." + LineParser.executableType);

          File file = new File(path);

          SyntaxException.file = file.getName();

          BufferedReader br = new BufferedReader(new FileReader(file));

          br.lines().forEach(s -> result += s.equals("") ? "" : s + "\n");

          parser = new LineParser(result, 0);
          parser.parse();
     }

     public static void executeResource(String path, Class importer) throws Exception {
          execute(importer.getClassLoader().getResource(path).getPath());
     }

     public static void main(String[] args) throws Exception {
          if (args.length != 0) {
               execute(args[0]);
               return;
          }

          JFrame frame = new JFrame("Pixie Launcher");
          frame.setVisible(true);
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.setBounds(50, 50, 400, 200);

          Container container = frame.getContentPane();
          container.setLayout(null);

          JTextField textField = new JTextField();
          textField.setBounds(10, 25, 370, 50);
          textField.setFont(new Font("Arial", Font.BOLD, 15));

          container.add(textField);

          JButton executeButton = new JButton();
          executeButton.setFont(new Font("Arial", Font.BOLD, 15));
          executeButton.setBounds(125, 90, 150, 50);
          executeButton.setAction(
                  new AbstractAction() {
                       @Override
                       public void actionPerformed(ActionEvent e) {
                            Thread thread = new Thread(() -> {
                                         try {
                                              execute(textField.getText());
                                         } catch (Exception exception) {
                                              if (exception.getMessage().equals("end")) System.exit(0);
                                              JOptionPane.showMessageDialog(null, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                         }
                                    });
                            thread.start();
                       }
                  }
          );

          JPanel text = new JPanel(){
               @Override
               public void paint(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g;
                    g2.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    Font font = getFont();
                    g2.setFont(font);
                    g2.drawString("Execute", getX(), getY());
               }
          };
          text.setFont(new Font("Arial", Font.BOLD, 15));
          text.setBounds(22, 15, 150, 50);
          executeButton.add(text);

          frame.add(executeButton);


          frame.setResizable(false);
     }
}
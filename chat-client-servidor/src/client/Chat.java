package client;

/**
 * @Title Trabalho de Sistemas Distribuídos 
 * @Subtitle Aplicação Socket Client & Socket Server
 * @author THIAGO SANTOS TASSINARI, GABRIEL SILVA MARQUES, BRENO NICACIO LIMA NUNES
 * @CPD 53680, 71099 , 71942  
 * @data 18/09/2020
 * @version 0.01
 */

import common.Utils;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Chat extends JFrame {

    private JLabel jl_title;
    private JEditorPane messages;
    private JTextField jt_message;
    private JTextField jb_message;
    private JPanel panel;
    private JScrollPane scroll;
    
    private Home home;
    private Socket connection;
    private String connection_info;
    private ArrayList<String> message_list;

    public Chat(Home home, Socket connection,  String connection_info, String title) {
        super("Chat " + title);
        this.home = home;
        this.connection = connection;
        this.connection_info = connection_info;
        initComponents();
        configComponents();
        insertComponents();
        insertAction();
        start();
     }

     private void initComponents() {
        message_list = new ArrayList<String>(); // ArrayList faz parte da bibilioteca util
        jl_title = new JLabel(connection_info.split(":")[0], SwingConstants.CENTER);
        messages = new JEditorPane();
        scroll = new JScrollPane(messages);
        jt_message = new JTextField();
        jb_message = new JTextField();
        panel = new JPanel(new BorderLayout());        
    }

    private void configComponents( ) {
        this.setMinimumSize(new Dimension(480, 720));
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fechar toda a operação
        messages.setContentType("text/html");
        messages.setEditable(false);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jb_message.setSize(100, 40);
    } 

    private void insertComponents() {
      this.add(jl_title, BorderLayout.NORTH);
      this.add(scroll, BorderLayout.CENTER);
      this.add(panel, BorderLayout.SOUTH);
      panel.add(jt_message, BorderLayout.CENTER);
      panel.add(jb_message, BorderLayout.EAST);
    }

    private void insertAction() {
        jb_message.addActionListener(event -> {
            try {
                send();
            } catch (IOException ex) {
                Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        jt_message.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    try {
                        send();
                    } catch (IOException ex) {
                        Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            } 
        });
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    Utils.sendMessage(connection, "CHAT_CLOSE");
                } catch (IOException ex) {
                    Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                }
                home.getOpened_chats().remove(connection_info); // Removido a lista de chats abertos
                home.getConnected_listeners().get(connection_info).setChatOpen(false);  // Definido que o chat está fechado
                home.getConnected_listeners().get(connection_info).setRunning(false);   // Vai parar de rodar aquele Listerner
                home.getConnected_listeners().remove(connection_info);  // Removo da lista de escuta
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
            
        });
    }
    public void append_message(String received) {   // Quando receber uma mensagem no editor a nossa área será atualizada
         message_list.add(received);
         String message = "";
         for (String str : message_list) {
             message += str;
         }
        messages.setText(message);
    }

    private void send() throws IOException {
        if (jt_message.getText().length() > 0) {
            DateFormat df = new SimpleDateFormat("hh:mm:ss");
            Utils.sendMessage(connection, "MESSAGE;" + "<b>[" + df.format(new Date()) + "] " + this.jl_title + ": </b><i>" + jt_message.getText() + "</i><br>");
            append_message("");
            jt_message.setText(""); // O método send já limpa a caixa de mensagem
        }
    }
    private void start() {
        this.pack();
        this.setVisible(true);
    }
    
    public static void main(String[] args) {
        Chat chat = new Chat("Thiago:127.0.0.1", "Breno");
    }
}
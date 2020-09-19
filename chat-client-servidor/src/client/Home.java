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
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Home extends JFrame {
            
            private ArrayList<String> opened_chats; // Essa lista guardamos as chaves que estão abertas (connection_info)
            private Map<String, ClientListener> connected_listeners;    // Guarda a referêrncia de todos os ClientListeners desses chats abertos
            private ArrayList<String> connected_users;
            private String connection_info;
            private Socket connection;
            private ServerSocket server;
            private boolean running;
            
            private JLabel  jl_title;
            private JButton jb_get_connected, jb_start_talk;  // Usuários conectados com o servidor e abrir uma conexão entre os usuários para conversar no chat
            private JList jlist;
            private JScrollPane scroll;

        public Home(Socket connection, String connection_info) {
            super("Chat - Home");
            this.connection = connection;
            this.connection_info = connection_info;
            initComponents();
            configComponents();
            insertComponents();
            insertAction();
            start();
         }
    
            private void initComponents() {
                running = false;
                server = null;
                connected_listeners = new HashMap<String, ClientListener>();
                opened_chats = new ArrayList<String>();
                connected_users = new ArrayList<String>();
                jl_title = new JLabel("<Usuário : " + connection_info.split(":")[0] + " > ", SwingConstants.CENTER);
                jb_get_connected = new JButton("Atualizar Contatos");
                jb_start_talk = new JButton("Abrir Conversa");
                jlist = new JList();
                scroll = new JScrollPane(jlist);
            }
            private void configComponents( ) {
                this.setLayout(null);
                this.setMinimumSize(new Dimension(600, 480));
                this.setResizable(false);
                this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                this.getContentPane().setBackground(Color.WHITE);

                jl_title.setBounds(10, 10, 370, 40);
                jl_title.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                jb_get_connected.setBounds(400, 10, 180, 40);
                jb_get_connected.setFocusable(false);

                jb_start_talk.setBounds(10, 400, 575, 40);
                jb_start_talk.setFocusable(false);

                jlist.setBorder(BorderFactory.createTitledBorder("Usuário Online"));
                jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                scroll.setBounds(10, 60, 575, 335);
                scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scroll.setBorder(null); // N�o ter bordas no scroll, pois senão vai ficar disputando borda com o JList
            }

            private void insertComponents() {
                this.add(jl_title);
                this.add(jb_get_connected);
                this.add(scroll);
                this.add(jb_start_talk);
            }

            private void insertAction() {
                this.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        running = false;
                        try {
                            Utils.sendMessage(connection, "QUIT");
                        } catch (IOException ex) {
                            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println("> Conexão encerrada.");  
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
                jb_get_connected.addActionListener(event -> {
                    try {
                        getConnectedUsers();
                    } catch (IOException ex) {
                        Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                jb_start_talk.addActionListener(event -> openChat());
            }

            private void start() {
                this.pack();
                this.setVisible(true);
                StartServer(this, Integer.parseInt(connection_info.split(":")[2]));
            }
            
            private void getConnectedUsers() throws IOException {
                Utils.sendMessage(connection, "GET_CONNECTED_USERS");
                String response = Utils.receiveMessage(connection); // response pegou todas as Strings cadastradas no servidor
                jlist.removeAll();
                connected_users.clear();
                for (String info: response.split(";")) {
                    // connection_info é nossa String própria
                    if (!info.equals(connection_info)) {    // Pegando todas as String de conexão que estão no servidor, pórem só quero somente as diferentes da String própria     
                        connected_users.add(info);
                    }
                }
                jlist.setListData(connected_users.toArray());
            }

    public ArrayList<String> getOpened_chats() {
        return opened_chats;
    }

    public void setOpened_chats(ArrayList<String> opened_chats) {
        this.opened_chats = opened_chats;
    }

    public Map<String, ClientListener> getConnected_listeners() {
        return connected_listeners;
    }                   
    
    public String getConnection_info() {
        return connection_info;
    }
    
    private void openChat() {
        int index = jlist.getSelectedIndex();
        if (index != -1) {                      // Se tiver alguém selecionado                          
            String connection_info = jlist.getSelectedValue().toString();   // Salva a conexão criada na connection_info que pertence ao método openChat() 
            String[] splited = connection_info.split(":");  // Partir essa conexão em vários pedacinhos para poder saber seu NICKNAME, HOST e PORT desse usuário
            if (!opened_chats.contains(connection_info)) {  // Se o chat da conexão está aberta ou não, tento conectar com o outro usuário e :   
                try {
                    Socket connection = new Socket(splited[1], Integer.parseInt(splited[2]));   // Cria uma conexão com o usuário no Socket Client na abertura do chat
                    Utils.sendMessage(connection, "OPEN_CHAT;" + this.connection_info);          // Estou enviando mensagem de acordo com a lista de usuários online presente no servidor e fazendo conexão com meu endereço dentro do chat com o usuário que eu escolhi
                    ClientListener cl = new ClientListener(this, connection);   // Aqui o ClientListener pede a referência da Home
                    cl.setChat(new Chat(this, connection, connection_info, this.connection_info.split(":")[0]));
                    cl.setChatOpen(true);
                    connected_listeners.put(connection_info, cl);
                    opened_chats.add(connection_info);
                    new Thread(cl).start();
                } catch (IOException ex) {
                    System.err.println("[Home: openChat] -> " + ex.getMessage());
                }
            }
        }
    }
    
    private void StartServer(Home home, int port) {
        new Thread() {
            @Override
            public void run() {
                running = true;
                try {
                    server = new ServerSocket(port);
                    System.out.println("Servidor cliente iniicado na port: " + port + "...");
                    while (running) {
                        Socket connection = server.accept(); // Vai ter uma nova conexão
                        ClientListener cl = new ClientListener(home, connection);
                        new Thread(cl).start(); // Quando um cliente quiser conexão a Thread intercepta o sinal e libera a conexão entre os usuários
                    }
                } catch (IOException ex) {
                    System.err.println("[Home:startServer] -> " +ex.getMessage());
                }
            }
        }.start(); 
    }
}
package server;

/**
 * @Title Trabalho de Sistemas Distribuídos 
 * @Subtitle Aplicação Socket Client & Socket Server
 * @author THIAGO SANTOS TASSINARI, GABRIEL SILVA MARQUES, BRENO NICACIO LIMA NUNES
 * @CPD 53680, 71099 , 71942  
 * @data 18/09/2020
 * @version 0.01
 */

import common.Utils;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 4444;

    private ServerSocket server;
    private Map<String, ClientListener> clients;

    public Server() {
        try {
            String connection_info;
            clients = new HashMap<String, ClientListener>();
            server = new ServerSocket(PORT);
            System.out.println("Servidor iniciado no host: " + HOST + " e porta " + PORT);

            while (true) {
                Socket connection = server.accept();
                connection_info = Utils.receiveMessage(connection);
                if (checkLogin(connection_info)) {
                    ClientListener cl = new ClientListener(connection_info, connection, this);
                    clients.put(connection_info, cl);
                    Utils.sendMessage(connection, "SUCESS");
                    new Thread(cl).start();
                } else {
                    Utils.sendMessage(connection, "ERROR");
                }
            }
        } catch (IOException ex) {
            System.err.println("[ERROR:Server] -> " + ex.getMessage());
        }
    }

    public Map<String, ClientListener> getClients() {
        return clients;
    }
    
    private boolean checkLogin(String connection_info) {
        String[] splited = connection_info.split(":");
        for (Map.Entry<String, ClientListener> pair: clients.entrySet()) {
            String[] parts = pair.getKey().split(":");
            if (parts[0].toLowerCase().equals(splited[0].toLowerCase())) {
                return false;
            } else if ((parts[1] + parts[2]).equals(splited[1] + splited[2])) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Server server = new Server();
    }
}


package client;

import common.Utils;
import java.io.IOException;
import java.net.Socket;

/**
 * @Title Trabalho de Sistemas Distribuídos 
 * @Subtitle Aplicação Socket Client & Socket Server
 * @author THIAGO SANTOS TASSINARI, GABRIEL SILVA MARQUES, BRENO NICACIO LIMA NUNES
 * @CPD 53680, 71099 , 71942  
 * @data 18/09/2020
 * @version 0.01
 */

public class ClientListener implements Runnable {
    
    private boolean running;
    private boolean chatOpen;  // Vai guardar se o chat está aberto ou não
    private Socket connection;
    private Home home;
    private String connection_info;
    private Chat chat;
    
    public ClientListener(Home home, Socket connection) {
        this.chatOpen = false;
        this.running = false;
        this.home = home;
        this.connection = connection;
        this.connection_info = null;
        this.chat = null;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isChatOpen() {
        return chatOpen;
    }

    public void setChatOpen(boolean chatOpen) {
        this.chatOpen = chatOpen;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
    
    
    @Override
    public void run() {
        running = true;
        String message;
        while (running) {
            message = Utils.receiveMessage(connection); 
            if (message == null || message.equals("CHAT_CLOSE")) {
                // Se o chat estiver aberto
                if (chatOpen) { 
                    home.getOpened_chats().remove(connection_info); // Removo o usuário da lista de chats abertos
                    home.getConnected_listeners().remove(connection_info); // Removo o usuário da lista de Listeners(escutas)
                    chatOpen = false;
                    try {
                        connection.close();
                    } catch(IOException ex) {
                        System.err.println("[ClientListener:run] -> " +ex.getMessage());
                    }
                    chat.dispose();
                }
                running = false;
            } else {  // Mas pode ser que o usuário tenha enviado outra coisa, invés do chatClose();
                String[] fields = message.split(";");
                if (fields.length > 1) {
                    if (fields[0].equals("OPEN_CHAT")) {    // Quando o usuário tentar abrir o chat
                        String[] splited = fields[1].split(":");
                        connection_info = fields[1];
                        if(!chatOpen) { // O chat não pode está aberto
                            home.getOpened_chats().add(connection_info);
                            home.getConnected_listeners().put(connection_info, this);
                            chatOpen = true;
                            chat = new Chat(home, connection, connection_info, home.getConnection_info());
                        }
                    } else if (fields[0].equals("MESSAGE")) {
                        String msg = ";";
                        for (int i = 1; i <fields.length; i++) {
                            msg += fields[i];
                            if (i > 1) msg += ";";
                        }
                        chat.append_message(msg);
                    }
                }
            }
            System.out.println(">> Mensagem>: " + message);
        }
    }
}

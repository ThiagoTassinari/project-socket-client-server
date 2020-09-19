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
import java.net.Socket;
import java.util.Map;
 
/**
 * @author THIAGO SANTOS TASSINARI, GABRIEL SILVA, BRENON 
 * @data: 18/09/2020
 * @version: 0.01
 */
public class ClientListener  implements Runnable{
    
    private String connection_info;
    private Socket connection;
    private Server server;
    private boolean running;

    public ClientListener(String connection_info, Socket connection, Server server)  {
        
        this.connection_info = connection_info;
        this.connection = connection;
        this.server = server;
        this.running = false; // A thread inicialmente não estará rodando
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        running = true; // A thread começou a rodar
        String message;
        while (running) {   
            // Enquanto rodar receberá apenas mensagens na conexão
            message = Utils.receiveMessage(connection);
            if (message.equals("QUIT")) { 
                // Quando o usuário estiver logado e fecha a janela Home, então o servidor remove o usuário conectado. Contudo, se ele quiser voltar com o mesmo nome e na mesma 
                // porta não exibirá mensagem de erro "Algum usuário já está conectado com este apelido ou nesse host e porta, tente outra porta!"
                server.getClients().remove(connection_info);    
                try {
                    connection.close(); // Encerra a conexão
                } catch (IOException ex) {
                    System.out.println("[ClientListener:Run] -> " + ex.getMessage());   // Caso tenha alguma exceção, apareça na tela do servidor
                }
                running = false;    // Interromper o laço e parar de rodar a thread
            } else if (message.equals("GET_CONNECTED_USERS")) {
                System.out.println("Solicitação de atualizar lista de contatos ...");
                String response = "";
                for (Map.Entry<String, ClientListener> pair : server.getClients().entrySet()) {
                    response += (pair.getKey() + ";");
                }
                try {
                    Utils.sendMessage(connection, response);
                } catch (IOException ex) {
                    System.err.println("Sua mensagem foi recebida");
                }
            } else {
                System.out.println("Recebido: " + message);
             }
        }
    }
}


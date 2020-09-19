package common;

/**
 * @Title Trabalho de Sistemas Distribuídos 
 * @Subtitle Aplicação Socket Client & Socket Server
 * @author THIAGO SANTOS TASSINARI, GABRIEL SILVA MARQUES, BRENO NICACIO LIMA NUNES
 * @CPD 53680, 71099 , 71942  
 * @data 18/09/2020
 * @version 0.01
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Utils {
    
    public static boolean sendMessage(Socket connection, String message) throws IOException {
        
        try {
            ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
            output.flush(); // Libera o fluxo. Isso gravará todos os bytes de saída armazenados em buffer e fluirá para o fluxo subjacente.
            output.writeObject(message); //Grava o objeto especificado no ObjectOutputStream. A classe do objeto, a assinatura da classe e os valores dos campos não transitórios e não estáticos da classe e todos os seus supertipos são escritos.
            return true;
        } catch (IOException ex) {
            System.err.println("[ERROR:sendMessage] -> " + ex.getMessage());
        }
        return false;
    }

    public static String receiveMessage(Socket connection) {
        String response = null;
        try {
            ObjectInputStream input = new ObjectInputStream(connection.getInputStream());
            response = (String) input.readObject();  // Recebe o objeto especificado no ObjectOutputStream. O objeto raiz é completamente restaurado quando todos os seus campos e os objetos aos quais faz referência são completamente restaurados.
                                                    // Neste ponto, os callbacks de validação do objeto são executados em ordem com base em suas prioridades registradas.
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("[ERROR: receiveMessage] -> " + ex.getMessage());      
        }
        return response;
    }
}


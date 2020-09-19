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
import java.io.IOException;
import java.awt.*;
import java.net.Socket;
import javax.swing.*;
import server.Server;

public class Login extends JFrame {

    private JButton jb_login;
    private JLabel jl_user, jl_port, jl_title;
    private JTextField jt_user, jt_port;

    public Login() {
        super("Login");
        initComponents();
        configComponents();
        insertComponents();
        insertAction();
        start();
    }

    private void initComponents() {
        jb_login = new JButton("Enter");
        jl_user = new JLabel("Nickname", SwingConstants.CENTER);
        jl_port = new JLabel("Port", SwingConstants.CENTER);
        jl_title = new JLabel();
        jt_user = new JTextField();
        jt_user = new JTextField();
        jt_port = new JTextField();
    }

    private void configComponents( ) {
        // Estou colocando o parâmetro null, pois poderei colocar as coisas onde eu quiser
        // se não tiver o parâmetro null, então será implantado a configuração padrão Flow Layout
        // onde ele coloca tudo um do lado do outro até não caber mais, logo joga pra baixo.
        this. setLayout(null);
        this.setMinimumSize(new Dimension(400, 300));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        this.getContentPane().setBackground(Color.WHITE); 

        jl_title.setBounds(10, 10, 375, 100);   // Move e redimensiona este componente para adaptá-lo ao novo retângulo delimitador Rx, Ry, Width, Height
        ImageIcon icon = new ImageIcon("images/logo.png");
        // Estou definindo por LP que esse icon do jl_title será exatamente a nova imagem Icon e será reescalada 
        jl_title.setIcon(new ImageIcon(icon.getImage().getScaledInstance(375, 100, Image.SCALE_SMOOTH)));
        
        jb_login.setBounds(10, 220, 375, 40);

        jl_user.setBounds(10, 120, 100, 40);
        jl_user.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        jl_port.setBounds(10, 170, 100, 40);
        jl_port.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        jt_user.setBounds(120, 120, 265, 40);
        jt_port.setBounds(120, 170, 265, 40);
    } 

    private void insertComponents() {
        this.add(jb_login);
        this.add(jl_user);
        this.add(jl_port);
        this.add(jl_title);
        this.add(jt_user);
        this.add(jt_port);
    }

    private void insertAction() {
        jb_login.addActionListener(event -> {
            try {
                String nickname = jt_user.getText();
                jt_user.setText("");    //Limpa o campo
                int port = Integer.parseInt(jt_port.getText());
                jt_port.setText("");
                Socket connection = new Socket(Server.HOST, Server.PORT);
                String connection_info = (nickname + ":" + connection.getLocalAddress().getHostAddress() + ":" + port );
                Utils.sendMessage(connection, connection_info);
                if (common.Utils.receiveMessage(connection).toLowerCase().equals("SUCESS")) {
                    new Home(connection, connection_info);
                    this.dispose();
                } else{
                    JOptionPane.showMessageDialog(null, "Algum usuário já está conectado com este apelido ou nesse host e porta, tente outra porta!");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao conectar. Verifique se o servidor está em execução!");
            }
        });
    }

    private void start() {
        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        Login login = new Login();
    }

}
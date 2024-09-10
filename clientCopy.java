import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class clientCopy extends JFrame {

    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;

    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private JTextField usernameInput = new JTextField();
    private JPasswordField passwordInput = new JPasswordField();

    private Font font = new Font("Roboto", Font.PLAIN, 20);

    public clientCopy() {
        try {
            System.out.println("Sending the request to the server");
            socket = new Socket("192.168.1.3", 9504);
            System.out.println("Connection done.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            authenticateUser();

            createGUI();
            handleEvents();
            startReading();
            // startWriting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void authenticateUser() throws Exception {
        // Implement a simple username/password authentication
        String username = JOptionPane.showInputDialog("Enter your username:");
        char[] passwordChars = JOptionPane.showInputDialog("Enter your password:").toCharArray();
        String password = new String(passwordChars);

        // Send the username and password to the server
        out.println(username);
        out.println(password);
        out.flush();

        // Receive the authentication status from the server
        String authenticationStatus = br.readLine();
        System.out.println(authenticationStatus);

        if (!"Authentication successful.".equals(authenticationStatus)) {
            System.out.println("Authentication failed. Closing connection.");
            socket.close();
            System.exit(0); // Terminate the client
        }
    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me: " + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }

    private void createGUI()
    {
        this.setTitle("Client Messager[END]");
        this.setSize(500,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageArea.setFont(font);

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        //set the frame layout
        this.setLayout(new BorderLayout());

        //Adding the two component to frame
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane= new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);


        this.setVisible(true);
    }



    //Start reading
    public void startReading()
    {
        //thread read the data
        Runnable r1=()->{
            System.out.println("reader....");
            try {
                while(true)
                {
                    String msg = br.readLine();
                    if(msg.equals("exist"))
                    {
                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this,"Server Terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    messageArea.append("Server: "+msg+"\n");

                }
            } catch (Exception e) {
                System.out.println("Connection is closed");
            }
        };
        new Thread(r1).start();
    }


    // start writing....
    public void startWriting() {
        //thread the data from the user to server
        Runnable r2 = () -> {
            System.out.println("Writer...");
            try {
                while (!socket.isClosed()) {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));

                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if(content.equals("exist")){
                        socket.close();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Connection is close");
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        new clientCopy();
    }
}

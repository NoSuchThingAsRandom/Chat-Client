/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author samra
 */
public class Networking implements KeyListener {

    private BufferedReader in;
    private PrintWriter out;
    private String sessKey;
    private Asymetric_Key ak;
    private JPasswordField getPassword;
    private JPasswordField passwordCheck;

    private boolean[] checks = new boolean[4];
    private JLabel lengthCheck;
    private JLabel caseCheck;
    private JLabel numberCheck;
    private JLabel matchCheck;
    private boolean vis = false;
    private boolean wait = true;

    public String getMsg() {
        try {
            String iv = in.readLine();
            String input = in.readLine();

            System.out.println("Received: "+iv+"\n" +
                               "          "+input);
            String msg = (ak.DecryptMessage(input, sessKey, iv));
            System.out.println("Message: " + msg);
            return msg;
        } catch (IOException ex) {
            Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        return null;
    }

    public void sendMsg(String msg) {
        System.out.println("Sending Message: " + msg);
        String[] toSend = ak.EncryptMessage(msg, sessKey);
        out.println(toSend[0]);
        out.println(toSend[1]);
    }

    public void init() {
        try {
            Socket socket = new Socket(Encrypt_Messages.serverIP, Encrypt_Messages.portNumber);
            System.out.println("Connected");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            int portNumber = Integer.parseInt(in.readLine());
            System.out.println("Port number is " + portNumber);
            in.close();
            socket.close();
            System.out.println("Closed");
            connect(portNumber);
        } catch (IOException ex) {
            System.out.println("Can't connect to server.\nTry again later.");
            System.exit(1);
        }

    }

    public final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

    public void connect(int portNumber) {
        try {
            Socket socket = new Socket(Encrypt_Messages.serverIP, portNumber);
            System.out.println("Connected to " + portNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ak = new Asymetric_Key();
            String[] keys = ak.Key_Generation();
            System.out.println("pub key is " + keys[0]);

            out.println(keys[0]);
            sessKey = ak.DecryptKey(in.readLine(), keys[1]);
            System.out.println("sess key is " + sessKey);
            queue.add(new Runnable() {
                @Override
                public void run() {
                    logIn("");
                }
            });
            while (true){
                queue.take().run();
            }



        } catch (IOException ex) {
            Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void logIn(String msg) {
        System.out.println("Log in");
        JPanel dialogue = new JPanel();
        dialogue.setLayout(new BoxLayout(dialogue, BoxLayout.Y_AXIS));
        Object[] options = {"Ok", "New Account", "Cancel"};
        JTextField getName = new JTextField("");
        JPasswordField getPassword = new JPasswordField("");
        dialogue.add(new JLabel("Enter Username"));
        dialogue.add(getName);
        dialogue.add(new JLabel("Enter Password"));
        dialogue.add(getPassword);
        dialogue.add(new JLabel(msg));

        int result = JOptionPane.showOptionDialog(null, dialogue, "Log in", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        // int result =  JOptionPane.showOptionDialog(null, "Log this operation?", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[0]);
        switch (result) {
            case 0:
                sendMsg("//LOGIN//");
                Encrypt_Messages.username=getName.getText();
                //String[] data = new String[]{, getPassword.getText()};
                sendMsg(getName.getText());
                char[] pass = getPassword.getPassword();
                sendMsg(Integer.toString(pass.length));
                for (int x = 0; x < pass.length; x++) {
                    sendMsg(String.valueOf(pass[x]));
                    pass[x] = 0;
                }

                //DataInputStream din=new DataInputStream(socket.);

                //sendMsg(data[1]);

                String mess = getMsg();
                if (mess.equals("//INVALID_LOGIN//")) {
                    System.out.println("Invalid login");
                    logIn("Invalid username or password");
                } else if (!mess.equals("//SUCCESSFUL//")) {
                    System.out.println("Not successful");
                    System.exit(1);
                }else {
                    Encrypt_Messages.startGui();
                }
                break;
            case 1:
                System.out.println("NEW USER");
                sendMsg("//NEW_USER//");
                createUser();
                break;

            case 2:
                System.out.println("THIS");
                 System.exit(0);

        }

    }

    public void createUser() {
        JFrame frame = new JFrame("Create an account.");
        Container pane = frame.getContentPane();
        JPanel dialogue = new JPanel();

        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        dialogue.setLayout(new BoxLayout(dialogue, BoxLayout.Y_AXIS));

        dialogue.add(new JLabel("Create a Username"));
        JTextField getName = new JTextField("");
        dialogue.add(getName);
        JLabel taken = new JLabel("Username is already taken.");
        taken.setForeground(Color.red);
        taken.setVisible(false);

        dialogue.add(taken);

        dialogue.add(new JLabel("Create a Password"));
        lengthCheck = new JLabel("<html><ul><li>" + "It must be between 8 and 16  characters" + "</li></ul><html>");
        lengthCheck.setForeground(Color.red);

        dialogue.add(lengthCheck);
        caseCheck = new JLabel("<html><ul><li>" + "It must contain upper and lower case" + "</li></ul><html>");
        caseCheck.setForeground(Color.red);
        dialogue.add(caseCheck);
        numberCheck = new JLabel("<html><ul><li>" + "It must contain numbers" + "</li></ul><html>");
        numberCheck.setForeground(Color.red);
        dialogue.add(numberCheck);
        JLabel enterPassword = new JLabel("Enter Password");
        dialogue.add(enterPassword);

        getPassword = new JPasswordField("");
        dialogue.add(getPassword);
        matchCheck = new JLabel("Enter Password Again");
        matchCheck.setForeground(Color.red);
        dialogue.add(matchCheck);

        Font f = lengthCheck.getFont();
        taken.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        lengthCheck.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        caseCheck.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        numberCheck.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        enterPassword.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        matchCheck.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));

        passwordCheck = new JPasswordField("");
        dialogue.add(passwordCheck);

        getPassword.addKeyListener((KeyListener) this);
        passwordCheck.addKeyListener((KeyListener) this);

        JPanel buttons = new JPanel();
        JButton okButton = new JButton("Ok");

        okButton.addActionListener(new ActionListener() {
                                       @Override
                                       public void actionPerformed(ActionEvent e) {
                                           if (passwordCheck()) {
                                               System.out.println("Send request to data base");
                                               System.out.println("User name is " + getName.getText());
                                               sendMsg(getName.getText());
                                               System.out.println("Sent name");
                                               if (getMsg().equals("//VALID//")) {
                                                   System.out.println("Valid");

                                                   char[] pass = getPassword.getPassword();
                                                   sendMsg(Integer.toString(pass.length));
                                                   for (int x = 0; x < pass.length; x++) {
                                                       sendMsg(String.valueOf(pass[x]));
                                                       pass[x] = 0;
                                                   }

                                                   System.out.println("Sent hashed");
                                                   String mess = getMsg();
                                                   System.out.println("Decryprws mess mess: " + mess);
                                                   if (mess.equals("//SUCCESS//")) {
                                                       System.out.println("Success");
                                                       frame.setVisible(false);
                                                       wait = false;
                                                       logIn("");
                                                   } else {
                                                       System.out.println("SOMETHING FAILED");
                                                       System.exit(1);
                                                   }
                                               } else {
                                                   System.out.println("Name is invalid");
                                                   taken.setVisible(true);
                                                   sendMsg("//NEW_USER//");
                                               }
                                           }
                                       }
                                   }
        );
        buttons.add(okButton);
        JButton cancelButton = new JButton("Cancel");

        cancelButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e
                    ) {
                        System.exit(0);
                    }
                }
        );
        buttons.add(cancelButton);

        pane.add(dialogue);

        pane.add(buttons);

        frame.setContentPane(pane);

        frame.setSize(
                300, 320);
        ;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setContentPane(pane);

        frame.setVisible(true);
        while (wait) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
     //   frame.dispose();
    }

    private boolean passwordCheck() {
        for (boolean x : checks) {
            if (x == false) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        String password = new String(getPassword.getPassword());
        //Length Check
        if (password.matches(".{8,16}$")) {
            lengthCheck.setForeground(Color.green);
            checks[0] = true;
        } else {
            lengthCheck.setForeground(Color.red);
            checks[0] = false;
        }
        //Upper and lower case check
        if (password.matches("^(?=.*[a-z])(?=.*[A-Z]).+$")) {
            caseCheck.setForeground(Color.green);
            checks[1] = true;
        } else {
            caseCheck.setForeground(Color.red);
            checks[1] = false;
        }
        //Number check
        if (password.matches(".*\\d.*")) {
            numberCheck.setForeground(Color.green);
            checks[2] = true;
        } else {
            numberCheck.setForeground(Color.red);
            checks[2] = false;
        }
        //Typo check
        if (password.equals(new String(passwordCheck.getPassword())) && password != null) {
            matchCheck.setForeground(Color.green);
            checks[3] = true;
        } else {
            matchCheck.setForeground(Color.red);
            checks[3] = false;
        }
    }

    public ArrayList<String> loadNames() {
        sendMsg("//LOAD_CHAT_NAMES//");
        String msg;
        ArrayList<String> names = new ArrayList();
        while (!(msg = getMsg()).equals("//DONE//")) {
            names.add(msg);
        }
        System.out.println("Received names from server");
        return names;

    }

    public ArrayList<String> loadMessages(String name) {
        sendMsg("//LOAD_CHAT//");
        sendMsg(name);
        String msg;
        ArrayList<String> messages=new ArrayList<>();
        while(!(msg=getMsg()).equals("//DONE//")){
            messages.add(msg);
        }
        return messages;
    }

    public boolean newChat(String user) {
        sendMsg("//NEW_CHAT//");
        sendMsg(user);
        System.out.println("Sent username");
        String response=getMsg();
        System.out.println("The response is:" +response);
        if(response.equals("//SUCCESS//")){
            System.out.println("Success");
            return true;
        }else{
            System.out.println("Failed");
            return false;
        }
    }

    public boolean newMessage(String user, String message){
        sendMsg("//NEW_MESSAGE//");
        sendMsg(user);
        sendMsg(message);
        return getMsg().equals("//SUCCESS//");
    }

    public ArrayList<String> checkMessages(){
        sendMsg("//CHECK_MESSAGES//");
        String msg;
        ArrayList<String> chats=new ArrayList();
        while(!(msg=getMsg()).equals("//DONE//")){
            chats.add(msg);
        }
        System.out.println("Finished asking server");
        return chats;
    }

}

//All required imports

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;



public class ChatGui extends JFrame implements ActionListener, KeyListener {
//Creates variables

    private static Logger logger;

    public String currentUser;
    JScrollPane UserScroll;
    private JTextArea AllMessages;
    private JTextArea UserText;
    private JFrame Messaging;
    private ArrayList<String> chatNames = new ArrayList();
    private ArrayList<JButton> users = new ArrayList<JButton>();
    private JPanel SelectUser = new JPanel(new GridBagLayout());
    private GridBagConstraints gbc;
    private Container pane = getContentPane();
    private Networking server;

    public ChatGui(Networking n) {

        Encrypt_Messages em= new Encrypt_Messages();
        server = n;

    }

    public static void createLogger(String name) {
        try {
            logger = Logger.getLogger(name);
            FileHandler fh;
            fh = new FileHandler("Logs/" + name + ".log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.setLevel(logger.getLevel());
            System.out.println("Log Folder Created");
        } catch (IOException ex) {
            logger.severe((Supplier<String>) ex);
            Logger.getLogger(Encrypt_Messages.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            logger.severe((Supplier<String>) ex);
            Logger.getLogger(Encrypt_Messages.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void changeButtonColour(String user, Color color) {
        int x = 0;
        System.out.println("Setting the user: "+user+" to color "+ color);
        for (String current : chatNames) {
            if (current.equals(user)) {
                users.get(x).setBackground(color);
            }
            x++;
        }

    }

    public void fatalError(String message) {
        logger.severe("Fatal Error recieved, " + message);
        JPanel dialogue = new JPanel();
        dialogue.setLayout(new BoxLayout(dialogue, BoxLayout.Y_AXIS));
        try {
            ServerSocket serverSocket = new ServerSocket(1);
        } catch (IOException ex) {
            Logger.getLogger(ChatGui.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Creating/adding dialogue components
        JLabel error = new JLabel(message);
        dialogue.add(error);

        //Creating the dialogue box
        JOptionPane.showMessageDialog(null, dialogue, "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    public void warning(String message) {
        logger.warning("Warning error recieved, " + message);
        JPanel dialogue = new JPanel();
        dialogue.setLayout(new BoxLayout(dialogue, BoxLayout.Y_AXIS));
        //Adding message
        JLabel warning = new JLabel(message);
        dialogue.add(warning);
        //Creating the dialogue box
        JOptionPane.showMessageDialog(null, dialogue, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    //For adding a new message to the chat
    public void addMessage(String message, String userName) {
        /*try {
            System.out.println("addMessage");
            message = message.replace("\n", "");
//Deciding which user created the message
            if (me == true) {
                server.sendMsg("");
                message = "\n" + "Me://" + message;
                AllMessages.append(message + "\n");
           //     Messages.get(currentUser).add(message);

            } else {
                System.out.println("Adding Message");
                int count = 0;
                for (String test : chatNames) {
                    if (test.equals(userName)) {
                        break;
                    } else {
                        count++;
                    }
                }
                message = "\n" + userName + "://" + message;//Gets user name
                Messages.get(count).add(message);
                if (!currentChat.equals(userName)) {
                    changeButtonColour(Color.green, count);
                }
            }
            //Adding message to array
            //THIS LINE
            //Debugging
            if (message.contains("\n")) {
                logger.fine("New line");
            }
            pane.validate();
            //MAJOR WORKAROUND
            loadUser(currentUser);

        } catch (IndexOutOfBoundsException ex) {
            //  logger.severe((Supplier<String>) ex);
            logger.severe("Something not right... in addMessage");
        }*/
    }


    public void newUserAttempt() {
        System.out.println("Create new user");
        JPanel dialogue = new JPanel();
        dialogue.setLayout(new BoxLayout(dialogue, BoxLayout.Y_AXIS));

        //Creating/adding dialogue components
        JTextField getName = new JTextField("");
        dialogue.add(new JLabel("Enter the username"));
        dialogue.add(getName);
        dialogue.add(Box.createHorizontalStrut(15));

        //Creating the dialogue box
        int result = JOptionPane.showConfirmDialog(null, dialogue, "New Chat", JOptionPane.OK_CANCEL_OPTION);
        logger.fine("Dialogue box built");
        if (result == JOptionPane.OK_OPTION) {
            Encrypt_Messages.n.queue.add(new Runnable() {
                @Override
                public void run() {
                    //Getting data from dialogue box
                    String name = getName.getText();
                    if (!server.newChat(name)) {
                        System.out.println("Invalid user");
                        warning("Username not found.\nPlease try again");
                        logger.warning("Invalid username");
                        newUserAttempt();

                    } else {
                        System.out.println("User success");

                        //Try connecting to other user here
                        System.out.println("Connected to new chat.");
                        chatNames.add(name);
                        newUserButton();

                        System.out.println("Connected to client");
                    }
                }
            });
        }
        logger.fine("User cancelled");
    }

    public void newUserButton() {
        System.out.println("New user button requested");
        Encrypt_Messages.Messages.add(new ArrayList());//New user
        //adds new UserButton
        //SelectUser.add(users.get(0), gbc);
        int temp = users.size();
        users.add(new JButton(chatNames.get(chatNames.size() - 1)));
        users.get(temp).addActionListener(this);
        users.get(temp).setBackground(Color.CYAN);
        users.get(temp).setSize(new Dimension(500, 500));
        SelectUser.add(users.get(temp), gbc);
        //pane.invalidate();
        pane.validate();
        System.out.println("New user button added");
        Encrypt_Messages.Messages.get(Encrypt_Messages.Messages.size() - 1).add("Chat Established");
    }

    ArrayList<String> buttonColors=new ArrayList<>();
    public void updateNames() {
        Encrypt_Messages.n.queue.add(new Runnable() {
            @Override
            public void run() {
                int count=0;
                for(JButton b:users){
                    Color c=b.getBackground();
                    if(c.equals(Color.red)){
                        System.out.println("User has a color: "+b.getText());
                        buttonColors.add(chatNames.get(count));
                    }
                    System.out.println(b.getBackground());
                    count++;
                }
                SelectUser.removeAll();
                chatNames.clear();
                users.clear();
                JButton newUser = new JButton("+");
                newUser.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        newUserAttempt();
                    }
                });
                SelectUser.add(newUser);
                for (String name : server.loadNames()) {
                    chatNames.add(name);
                    users.add(new JButton(name));
                    System.out.println("Adding: "+name);
                    if(buttonColors.contains(name)){
                        users.get(users.size()-1).setBackground(Color.red);
                    }else{
                        users.get(users.size()-1).setBackground(Color.cyan);
                    }
                    users.get(users.size() - 1).addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            System.out.println("Button pressed");
                            String source = (String) evt.getActionCommand();
                            int x = 0;
                            for (String name : chatNames) {
                                System.out.println("Testing: " + name);//THIS LINE
                                if (source.equals(name)) {       //THIS LINE
                                    changeButtonColour(currentUser, Color.CYAN);
                                    pane.validate();
                                    currentUser = name;
                                    System.out.println("Current user changed to, " + currentUser + ", " + chatNames.get(x));
                                    loadUser(currentUser);
                                    System.out.println("Done");
                                    break;
                                }
                                x++;
                            }
                            System.out.println("Finished button press");
                        }
                    });
                    SelectUser.add(users.get(users.size() - 1), gbc);

                }
                changeButtonColour(currentUser, Color.green);
                buttonColors.clear();
                pane.validate();
                System.out.println("Updated names");
            }

        });



    }
    public void updateNamesOLD() {
        SelectUser.removeAll();
        chatNames.clear();
        users.clear();
        Encrypt_Messages.n.queue.add(new Runnable() {
            @Override
            public void run() {
                for (String name : server.loadNames()) {
                    chatNames.add(name);
                    users.add(new JButton(name));
                    System.out.println("Adding: " + name);
                    users.get(users.size() - 1).addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            System.out.println("Button pressed");
                            String source = (String) evt.getActionCommand();
                            int x = 0;
                            for (String name : chatNames) {
                                System.out.println("Testing: " + name);//THIS LINE
                                if (source.equals(name)) {       //THIS LINE
                                    currentUser = name;
                                    System.out.println("Current user changed to, " + currentUser + ", " + chatNames.get(x));
                                    loadUser(currentUser);
                                    System.out.println("Done");
                                    break;
                                }
                                x++;
                            }
                            System.out.println("Finished button press");
                        }
                    });
                    SelectUser.add(users.get(users.size() - 1), gbc);

                    JButton newUser = new JButton("+");
                    newUser.addActionListener(e -> newUserAttempt());
                    SelectUser.add(newUser);

                }
            }
        });
    }


    public void loadUser(String user) {
        if(!(user == null)) {
            changeButtonColour(user, Color.green);
            System.out.println("Loading user");
            Encrypt_Messages.userLoaded = true;
            AllMessages.setText("");
            currentUser = user;
            Encrypt_Messages.n.queue.add(new Runnable() {
                @Override
                public void run() {
                    for (String msg : server.loadMessages(user)) {             //THIS LINE
                        AllMessages.append(msg + "\n");         //THIS LINE

                    }
                }
            });
            pane.validate();
        }

 /*       logger.severe("no user data found -User_Messages_Data out of bounds line 97 [if (Users_Messages_Data.get(currentUser) != null)]");
        userLoaded = false;
        AllMessages.append("No data found\n");*/
    }


    public void messagingGUI() {
        createLogger("Gui log");
        System.out.println("Messaging GUI started");
//Creates JFrame and pane         
        Messaging = new JFrame();
//Creates Clock        
        String date = null;
        String time = null;
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
        JLabel info = new JLabel();
        info.setText(df.format(new Date()));
        Timer SimpleTimer = new Timer(30000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                info.setText(df.format(new Date()));
                // System.out.println("The time is " + df.format(new Date()));
            }
        });
        SimpleTimer.start();
        logger.fine("Clock Created");
//Creates user chats panel        
        gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;

        SelectUser.setLayout(new BoxLayout(SelectUser, BoxLayout.Y_AXIS));
        SelectUser.setSize(new Dimension(500, 500));
        System.out.println("User Chats Panel Created");
//Adds different chats
        updateNames();
        UserScroll = new JScrollPane(SelectUser,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        System.out.println("Added multiple chats");
//Creates messages feed        
        JPanel Messages = new JPanel();
        Messages.setLayout(new BoxLayout(Messages, BoxLayout.Y_AXIS));
        AllMessages = new JTextArea(10, 30);
        AllMessages.setBackground(Color.WHITE);
        AllMessages.setEditable(false);
        AllMessages.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
        JScrollPane MessageScroll = new JScrollPane(AllMessages,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        System.out.println("Messages feed created");
//Creates user text entry box        
        UserText = new JTextArea(5, 30);
        //UserText.setLineWrap(true);
        //UserText.setWrapStyleWord(true);
        UserText.setBorder(BorderFactory.createLineBorder(Color.CYAN, 1));
        UserText.setText("Enter Message. Press enter to send");
        UserText.setFocusable(true);
        UserText.addKeyListener(this);
        UserText.setPreferredSize(new Dimension(5, 20));
        logger.fine("User text box created");
//Adds all components to pane        
        Messages.add(info);
        Messages.add(MessageScroll);
        Messages.add(UserText);
        pane.add(UserScroll, BorderLayout.WEST);
        pane.add(Messages, BorderLayout.CENTER);
        System.out.println("All components added");

//Load all chat names              

//JFrame setup        
        Messaging.setTitle("Messaging - "+Encrypt_Messages.username);
        Messaging.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Messaging.setContentPane(pane);
        Messaging.setVisible(true);
        Messaging.setSize(400, 350);
        updateNames();
        loadUser(currentUser);
        System.out.println("Gui created");
    }
//When button pressed    

    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Button pressed");
        String source = (String) evt.getActionCommand();
        if ("+".equals(source)) {
            newUserAttempt();
        }
        int x = 0;
        for (String name : chatNames) {
            System.out.println("Testing: " + name);//THIS LINE
            if (source.equals(name)) {       //THIS LINE
                currentUser = name;
                System.out.println("Current user changed to, " + currentUser + ", " + chatNames.get(x));
                loadUser(currentUser);
                System.out.println("Done");
                break;
            }
            x++;
        }
        System.out.println("Finished button press");
    }
//Not used but needed for abstract error 

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    private String message;
    @Override

    public void keyTyped(KeyEvent e) {
        //    System.out.println("key typed");
        if (e.getKeyChar() == (KeyEvent.VK_ENTER)) {
            System.out.println("New message");
            message = UserText.getText();
            message = message.replaceAll("\n", "");
            System.out.println("The message is: " + message);
            if (Encrypt_Messages.userLoaded) {
                if (message.length() >= 1) {
                    System.out.println("The user is: " + currentUser);
                    Encrypt_Messages.n.queue.add(new Runnable() {
                        @Override
                        public void run() {
                            if(!server.newMessage(currentUser, message)){
                                System.out.println("Sending the message: "+message+" to "+ currentUser+" has failed");
                                System.exit(1);
                            }
                        }
                    });
                    AllMessages.append(currentUser+": "+message+"\n");
                } else {
                    System.out.println("Empty Message");
                    AllMessages.append("Empty Message");
                }
            } else {
                logger.warning("No user found");
                AllMessages.append("No user found!!!\n");
            }
            UserText.setText("");
            UserText.setCaretPosition(0);
            updateNames();
        }
    }

}

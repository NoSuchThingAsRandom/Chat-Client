//All required imports

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
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

    String currentUser;
    private JTextArea chatHistory;
    private JTextArea userText;
    private ArrayList<String> chatNames = new ArrayList<>();
    private ArrayList<JButton> users = new ArrayList<>();
    private JPanel selectUser = new JPanel(new GridBagLayout());
    private GridBagConstraints gbc;
    private Container pane = getContentPane();
    private Networking server;
    private ArrayList<String> buttonColors = new ArrayList<>();

    ChatGui(Networking n) {
        server = n;

    }

    private static void createLogger() {
        try {
            logger = Logger.getLogger("Gui log");
            FileHandler fh;
            fh = new FileHandler("Logs/" + "Gui log" + ".log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.setLevel(logger.getLevel());
        } catch (IOException | SecurityException ex) {
            logger.severe((Supplier<String>) ex);
            Logger.getLogger(Encrypt_Messages.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void messagingGUI() {
        createLogger();
        System.out.println("Messaging GUI started");
        JFrame messaging = new JFrame();
//Creates Clock
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
        JLabel info = new JLabel();
        info.setText(df.format(new Date()));
        Timer SimpleTimer = new Timer(30000, e -> {
            info.setText(df.format(new Date()));
        });
        SimpleTimer.start();
        logger.fine("Clock Created");
//Creates user chats panel
        gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        selectUser.setLayout(new BoxLayout(selectUser, BoxLayout.Y_AXIS));
        selectUser.setSize(new Dimension(500, 500));
        updateNames();
        JScrollPane userScroll = new JScrollPane(selectUser,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        System.out.println("User Chats Panel Created");
//Creates messages feed
        JPanel Messages = new JPanel();
        Messages.setLayout(new BoxLayout(Messages, BoxLayout.Y_AXIS));
        chatHistory = new JTextArea(10, 30);
        chatHistory.setBackground(Color.WHITE);
        chatHistory.setEditable(false);
        chatHistory.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
        JScrollPane MessageScroll = new JScrollPane(chatHistory,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        System.out.println("Messages feed created");
//Creates user text entry box
        userText = new JTextArea(5, 30);
        userText.setBorder(BorderFactory.createLineBorder(Color.CYAN, 1));
        userText.setText("Enter Message. Press enter to send");
        userText.setFocusable(true);
        userText.addKeyListener(this);
        userText.setPreferredSize(new Dimension(5, 20));
        System.out.println("User text box created");
//Adds all components to pane
        Messages.add(info);
        Messages.add(MessageScroll);
        Messages.add(userText);
        pane.add(userScroll, BorderLayout.WEST);
        pane.add(Messages, BorderLayout.CENTER);
        System.out.println("All components added");
//JFrame setup
        messaging.setTitle("Messaging - " + Encrypt_Messages.username);
        messaging.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        messaging.setContentPane(pane);
        messaging.setVisible(true);
        messaging.setSize(400, 350);
        updateNames();
        loadUser(currentUser);
        System.out.println("Gui created");
    }

    void changeButtonColour(String user, Color color) {
        int x = 0;
        System.out.println("Setting the user: "+user+" to color "+ color);
        for (String current : chatNames) {
            if (current.equals(user)) {
                users.get(x).setBackground(color);
            }
            x++;
        }

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

    public void fatalError(String message) {
        logger.severe("Fatal Error received, " + message);
        JPanel dialogue = new JPanel();
        dialogue.setLayout(new BoxLayout(dialogue, BoxLayout.Y_AXIS));
        //Creating/adding dialogue components
        JLabel error = new JLabel(message);
        dialogue.add(error);

        //Creating the dialogue box
        JOptionPane.showMessageDialog(null, dialogue, "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    private void newUserAttempt() {
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
            Encrypt_Messages.n.queue.add(() -> {
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
            });
        }
        logger.fine("User cancelled");
    }

    private void newUserButton() {
        System.out.println("New user button requested");
        Encrypt_Messages.Messages.add(new ArrayList<>());//New user
        //adds new UserButton
        //SelectUser.add(users.get(0), gbc);
        int temp = users.size();
        users.add(new JButton(chatNames.get(chatNames.size() - 1)));
        users.get(temp).addActionListener(this);
        users.get(temp).setBackground(Color.CYAN);
        users.get(temp).setSize(new Dimension(500, 500));
        selectUser.add(users.get(temp), gbc);
        //pane.invalidate();
        pane.validate();
        System.out.println("New user button added");
        Encrypt_Messages.Messages.get(Encrypt_Messages.Messages.size() - 1).add("Chat Established");
    }

    void updateNames() {
        Encrypt_Messages.n.queue.add(() -> {
            int count = 0;
            for (JButton b : users) {
                Color c = b.getBackground();
                if (c.equals(Color.red)) {
                    System.out.println("User has a color: " + b.getText());
                    buttonColors.add(chatNames.get(count));
                }
                System.out.println(b.getBackground());
                count++;
            }
            selectUser.removeAll();
            chatNames.clear();
            users.clear();
            JButton newUser = new JButton("+");
            newUser.addActionListener(e -> newUserAttempt());
            selectUser.add(newUser);
            for (String name : server.loadNames()) {
                chatNames.add(name);
                users.add(new JButton(name));
                System.out.println("Adding: " + name);
                if (buttonColors.contains(name)) {
                    users.get(users.size() - 1).setBackground(Color.red);
                } else {
                    users.get(users.size() - 1).setBackground(Color.cyan);
                }
                users.get(users.size() - 1).addActionListener(evt -> {
                    System.out.println("Button pressed");
                    String source = evt.getActionCommand();
                    int x = 0;
                    for (String name1 : chatNames) {
                        System.out.println("Testing: " + name1);//THIS LINE
                        if (source.equals(name1)) {       //THIS LINE
                            changeButtonColour(currentUser, Color.CYAN);
                            pane.validate();
                            currentUser = name1;
                            System.out.println("Current user changed to, " + currentUser + ", " + chatNames.get(x));
                            loadUser(currentUser);
                            System.out.println("Done");
                            break;
                        }
                        x++;
                    }
                    System.out.println("Finished button press");
                });
                selectUser.add(users.get(users.size() - 1), gbc);

            }
            changeButtonColour(currentUser, Color.green);
            buttonColors.clear();
            pane.validate();
            System.out.println("Updated names");
        });



    }

    void loadUser(String user) {
        if(!(user == null)) {
            changeButtonColour(user, Color.green);
            System.out.println("Loading user");
            Encrypt_Messages.userLoaded = true;
            chatHistory.setText("");
            currentUser = user;
            Encrypt_Messages.n.queue.add(() -> {
                for (String msg : server.loadMessages(user)) {             //THIS LINE
                    chatHistory.append(msg + "\n");         //THIS LINE

                }
            });
            pane.validate();
        }

 /*       logger.severe("no user data found -User_Messages_Data out of bounds line 97 [if (Users_Messages_Data.get(currentUser) != null)]");
        userLoaded = false;
        AllMessages.append("No data found\n");*/
    }



//When button pressed    

    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Button pressed");
        String source = evt.getActionCommand();
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
            message = userText.getText();
            message = message.replaceAll("\n", "");
            System.out.println("The message is: " + message);
            if (Encrypt_Messages.userLoaded) {
                if (message.length() >= 1) {
                    System.out.println("The user is: " + currentUser);
                    Encrypt_Messages.n.queue.add(() -> {
                        if (!server.newMessage(currentUser, message)) {
                            System.out.println("Sending the message: " + message + " to " + currentUser + " has failed");
                            System.exit(1);
                        }
                    });
                    chatHistory.append(currentUser + ": " + message + "\n");
                } else {
                    System.out.println("Empty Message");
                    chatHistory.append("Empty Message");
                }
            } else {
                logger.warning("No user found");
                chatHistory.append("No user found!!!\n");
            }
            userText.setText("");
            userText.setCaretPosition(0);
            updateNames();
        }
    }

}

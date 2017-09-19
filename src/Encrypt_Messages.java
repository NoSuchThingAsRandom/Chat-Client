/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.*;

import static java.util.logging.Level.FINEST;

/**
 * Server Sends data to client Client receives data from server
 *
 * @author Sam
 */
public class Encrypt_Messages extends Thread {
//Public Variables    

    // public static final String serverIP="81.98.97.160";
    public static final String serverIP = "192.168.0.100";
    public static final int portNumber = 4000;
    public static String username = "No username";
    public static boolean userLoaded = false;

    public static int[] key;
    public static String currentChat;
    // public static ArrayList<ArrayList<ArrayList<String>>> Users_Messages_Data = new ArrayList<ArrayList<ArrayList<String>>>();//Format equals below 

    public static ArrayList<String> keys = new ArrayList();
    public static ArrayList<String> chatNames = new ArrayList();
    public static ArrayList<ArrayList<String>> Messages = new ArrayList<ArrayList<String>>();
    // public static ArrayList<ArrayList<String>> DataOLD = new ArrayList<ArrayList<String>>();//Format equals below 
    //public static ArrayList<SendingMessageClient> smc = new ArrayList();
    //public static ArrayList<Boolean> aliveUsers = new ArrayList();
    public static Logger logger;
    public static Logger l;
    ;
    public static Networking n;
    private static FileHandler fh;

    //Runs the methods
    public static void createLogger(String name) {

        try {

            logger = Logger.getLogger(name);

            fh = new FileHandler("Logs/" + name + ".log");
            fh.setLevel(FINEST);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.info("Log Folder Created");
            logger.setLevel(Level.FINEST);
            logger.finest("Hey");
            ConsoleHandler ch = new ConsoleHandler();
            ch.setLevel(Level.FINEST);
            logger.addHandler(ch);
            //   logger.setLevel(Level.FINEST);
            logger.finest("Hey2");
        } catch (IOException ex) {
            Logger.getLogger(Encrypt_Messages.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Encrypt_Messages.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static void saveData() {
        logger.info("Saving Data");
        File logFolder = new File("Logs");
        if (!logFolder.exists()) {
            logFolder.mkdir();
        } else {
            for (File f : logFolder.listFiles()) {
                f.delete();
            }
        }

    }


    public static void startGui() {
        System.out.println("Start gui");
        ChatGui cg = new ChatGui(n);
        cg.messagingGUI();
        new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        while (true) {

                        System.out.println("Slept");
                            n.queue.add(() -> {
                                System.out.println("Check messages");
                                ArrayList<String> chats = n.checkMessages();
                                for (String user : chats) {
                                    System.out.println("Found users: " +user);
                                    cg.changeButtonColour(user, Color.red);
                                    if (user.equals(cg.currentUser)) {
                                        cg.loadUser(cg.currentUser);
                                    }
                                }
                                System.out.println("Done");
                            });
                            cg.updateNames();
                            Thread.sleep(5000);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

        }).start();

    }


    public static void main(String[] args) throws IOException {
        //  System.exit(1);

        n = new Networking();
        n.init();

//        n.loadData();
        System.out.println("Startup complete");



        /*
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                killChats();
                saveData();
            }
        });
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tT %4$s  %5$s%6$s%n");//Reduced
        //  "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");Full log info
        //https://docs.oracle.com/javase/7/docs/api/java/util/logging/SimpleFormatter.html 
        File logFolder = new File("Logs");
        if (!logFolder.exists()) {
            logFolder.mkdir();
        } else {
            for (File f : logFolder.listFiles()) {
                f.delete();
            }
        }
        createLogger("MainLog");


/*        Data.add(new ArrayList());//Adds client port array          2
        Data.add(new ArrayList());//Adds server port array          3
        Data.add(new ArrayList());//Local Adds public key array     4
        Data.add(new ArrayList());//Local Adds private key array    5
        Data.add(new ArrayList());//Local Adds session key array    6
        Data.add(new ArrayList());//Recieved Adds public key array  7
        Data.add(new ArrayList());//Recieved Adds session key array 8

        logger.info("Data arraylist created.");

        ChatGui exc = new ChatGui();
        username = exc.getInput("What is your username?");
        if (username == null) {
            username = "Dipshit";
        }

        exc.messagingGUI();
        logger.info("Gui Started");
        Thread initServer = new Thread(new Initialisation(exc));
        logger.info("Listening Server Started");
        initServer.start();
         */
    }
}

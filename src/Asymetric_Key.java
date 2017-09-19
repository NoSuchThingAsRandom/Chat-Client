import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class Asymetric_Key {

    private static Logger logger;

/*    public static void createLogger(String name) {
        try {
            logger = Logger.getLogger(name);
            FileHandler fh;
            fh = new FileHandler("Logs/" + name + ".log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.setLevel(logger.getLevel());
            logger.info(name + " file was created");
        } catch (IOException | SecurityException ex) {
            logger.severe((Supplier<String>) ex);
        }
    }*/

    public String[] Key_Generation() {
     //   createLogger("Keys");
System.out.println("Creating new keys");      
//  logger.info("Creating new keys");
        PublicKey pubKey;
        PrivateKey privKey;
        SecretKey SessionKey;
        KeyPairGenerator KeyPairGen = null;
        KeyGenerator KeyGen = null;

        try {
            //Generates session key
            KeyGen = KeyGenerator.getInstance("AES");
            //Generates key_pair
            KeyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException ex) {
           // logger.severe((Supplier<String>) ex);
        }
        //Creates session key

        KeyGen.init(128);
        SessionKey = KeyGen.generateKey();
        String strSessionKey = Base64.getEncoder().encodeToString(SessionKey.getEncoded());
        // byte[] byteSessionKey = SessionKey.getEncoded();//Converts session key to bytes
        //  String StrSessionKey = new String(byteSessionKey);//Converts key to String

        //Creates Key Pair
        KeyPairGen.initialize(2048);
        KeyPair key = KeyPairGen.genKeyPair();
        pubKey = key.getPublic();
        privKey = key.getPrivate();

        byte[] bytePubKey = pubKey.getEncoded();//Converts key to bytes
        String StrPubKey = Base64.getEncoder().encodeToString(bytePubKey);//Converts key to String

        byte[] bytePrivKey = privKey.getEncoded();//Converts key to bytes
        String StrPrivKey = Base64.getEncoder().encodeToString(bytePrivKey);//Converts key to String
        //Stores the keys in the main array
        //Data.get(4).add(StrPubKey);
        //Data.get(5).add(StrPrivKey);
        //Data.get(6).add(strSessionKey);
        System.out.println("keys generated");
      //  logger.info("Keys generated");
        return new String[]{StrPubKey, StrPrivKey};

    }

    public String encryptKey(String message, String StrPubKey) {
    //    logger.info("Encrypting keys for sending");
        PublicKey pubKey = null;
        try {
            byte[] bytePubKey = Base64.getDecoder().decode(StrPubKey);
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytePubKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            pubKey = keyFactory.generatePublic(pubKeySpec);

        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            logger.severe((Supplier<String>) ex);
        }
      //  logger.fine("Data converted");
        String encoded = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");//Creates cipher            
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
         //   logger.fine("Original Length: " + (message.getBytes()).length);
            byte[] cipherText = cipher.doFinal(message.getBytes());//Encrypts message  
      //      logger.fine("Encrypted Length: " + cipherText.length);
            encoded = Base64.getEncoder().encodeToString(cipherText);//Converts message to string  
       //     logger.fine("Cipher completed");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            logger.severe((Supplier<String>) ex);
        }
    //    logger.info("Keys encrypted");
        return encoded;
    }

    public String DecryptKey(String message, String StrPrivKey) {
     //   logger.info("Decrypting recieved keys");
        PrivateKey privKey = null;
        try {

            byte[] bytePrivKey = Base64.getDecoder().decode(StrPrivKey);
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(bytePrivKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privKey = keyFactory.generatePrivate(privKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            logger.severe((Supplier<String>) ex);
        }
      //  logger.fine("Decoded Data");
        String decoded = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");//Creates cipher
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            byte[] decodedMsg = Base64.getDecoder().decode(message);
      //      logger.fine("Encrypted recieved Length: " + decodedMsg.length);
            byte[] plainText = cipher.doFinal(decodedMsg);//Decrypting Message
       //     logger.fine("Decrypted Length: " + plainText.length);
            decoded = new String(plainText);//Converts to String
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            logger.severe((Supplier<String>) ex);
        }
     //   logger.info("Keys decrypted");
        return (decoded);
    }

    public byte[] iv;//Remember to send this with every message

    public String[] EncryptMessage(String message, String StrSessionKey) {
      //  logger.info("Encrypting Message");
        try {
            SecureRandom rand = new SecureRandom();
            byte[] iv = new byte[16];
            rand.nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);
       //     logger.fine("The iv is: " + Arrays.toString(iv));
            //   SecretKey SessionKey = new SecretKeySpec(StrSessionKey.getBytes(), "AES");
            byte[] decodedKey = Base64.getDecoder().decode(StrSessionKey);
            SecretKey SessionKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
         //   logger.fine("Key converted");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//Creates cipher            
            cipher.init(Cipher.ENCRYPT_MODE, SessionKey, ivspec);
            byte[] cipherText = cipher.doFinal(message.getBytes());//Encrypts message       
            String encoded = Base64.getEncoder().encodeToString(cipherText);//Converts message to string  
       //     logger.fine("Message encrypted and base 64");
      //      logger.fine("The iv spec is" + Arrays.toString(iv) + "\nand the encrypted message is: " + encoded);
        //    logger.info("Encryption complete");
            String ivStr = Base64.getEncoder().encodeToString(iv);
            return new String[]{ivStr, encoded};
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            logger.severe((Supplier<String>) ex);
            logger.severe("MAY NEED TO KILL PROGRAM HERE");
            //System.exit(1);
            return null;
        }
    }

    public String DecryptMessage(String message, String StrSessionKey, String iv) {
      //  logger.info("Decrypting Message");
        byte[] messagebytes = Base64.getDecoder().decode(message);
        IvParameterSpec ivspec = new IvParameterSpec(Base64.getDecoder().decode(iv));

        byte[] ByteKey = Base64.getDecoder().decode(StrSessionKey);
        SecretKey SessionKey = new SecretKeySpec(ByteKey, 0, ByteKey.length, "AES");
        String decrypted = null;
     //   logger.fine("Data decoded");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//Creates cipher            
            cipher.init(Cipher.DECRYPT_MODE, SessionKey, ivspec);

            byte[] cipherText = cipher.doFinal(messagebytes);//Encrypts message       
            decrypted = new String(cipherText);//Converts message to string  
          //  logger.info("Decryption successful");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            logger.severe((Supplier<String>) ex);
        }
     //   logger.info("Returning Message");
        return decrypted;
    }
}

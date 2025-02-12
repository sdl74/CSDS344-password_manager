import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.Scanner;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordManager {

    // string used to verify user entered the correct password
    private static String verificationString = "verify";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // User inputs the initial passcode
        System.out.print("Enter the passcode to access your passwords: ");
        String passcode = scanner.nextLine();

        // Check if the "password" file already exists in the default directory
        Path defaultDir = Paths.get("").toAbsolutePath();
        File directory = new File(defaultDir.toString());
        File file = new File(directory, "password.txt");
        boolean exists = file.exists() && file.isFile();

        // If a file already exists in the default directory
        if(exists) {
            try {
                FileWriter writer = new FileWriter("password.txt", true);
            } catch (Exception e) {
            }
        }

        else {
            // creating a new file
            // creating the salt and verification token
            // writing those into the first line
            createPasswordFile(passcode);
            try {
                FileWriter writer = new FileWriter("password.txt", true);
                writer.write("hello");
                writer.close();
                System.out.println("No password file detected. Creating a new password file.");
                String option = "";

                while(!option.equals("q")) {
                    System.out.println("a : Add Password \nr : Read Password \nq : Quit");
                    System.out.print("Enter choice: ");
                    option = scanner.nextLine();
                    switch(option) {
                        case "a":
                            break;

                        case "r":
                            break;

                        case "q":
                            System.out.println("Quitting");
                            System.exit(0);
                            break;

                        default:
                            System.out.println("Error: Invalid input");
                            break;
                    }
                }
            } catch (Exception e) {
                
            }
        }
    }

    // takes a string pass and salt salt and returns the hashed string using PBKDF2
    // this function is used to generate the key for encryption and decryption
    private static SecretKey hash(String pass, byte[] salt){
        // generate the key
        try{
            KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, 1024, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // this function generates the password.txt file and populates it with the salt string (encoded in base64) and a verification token
    // input is the super secret password which will be used to generate the key used for encryption and decryption
    private static void createPasswordFile(String password){
        // generate random salt string
        byte[] salt = getNewSalt();

        // hash the password to make the key
        SecretKey key = hash(password, salt);

        // encrypt "verify" to create verification token
        String verificationToken = encrypt(verificationString, key);

        // combine the salt and verification token into one line
        String firstLine = new String(salt, StandardCharsets.UTF_8) + ":" + verificationToken;

        // create password.txt file
        // write firstLine to password.txt
        Path defaultDir = Paths.get("").toAbsolutePath();
        File directory = new File(defaultDir.toString());
        File file = new File(directory, "password.txt");
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter("password.txt", true);
            writer.write(firstLine + "\n");
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // generates a random salt string
    private static byte[] getNewSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    // encrypts a password using the given key
    // returns the encrypted password in base64
    private static String encrypt(String password, SecretKey privateKey) {
        try {
            // initialize cipher
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec key = new SecretKeySpec(privateKey.getEncoded(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // encrypt key
            byte [] encryptedData = cipher.doFinal(password.getBytes());
            return new String(Base64.getEncoder().encode(encryptedData));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // decrypts the given token with the key and returns the decrypted password
    private static String decrypt(String token, SecretKey privateKey){
        try{
            // initialize cipher
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec key = new SecretKeySpec(privateKey.getEncoded(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            // decrypt token
            byte [] encryptedData = Base64.getDecoder().decode(token);
            byte [] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}

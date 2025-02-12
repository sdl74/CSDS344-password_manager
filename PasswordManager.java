import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

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
    private static SecretKey hash(String pass, String salt){
        // generate the key
        try{
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt.getBytes(), 10000, 256);
            SecretKey secret = new SecretKeySpec(skf.generateSecret(spec).getEncoded(), "AES");

            // return the key encoded in base64
            return secret;
        }catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    // this function generates the password.txt file and populates it with the salt string (encoded in base64) and a verification token
    // input is the super secret password which will be used to generate the key used for encryption and decryption
    private static void createPasswordFile(String password){
        // generate random salt string
        String salt = getNewSalt();

        // hash the password to make the key
        SecretKey key = hash(password, salt);

        // encrypt "verify" to create verification token
        String verificationToken = encrypt(verificationString, key);

        // store the salt string in base64
        byte[] encodedBytes = Base64.getEncoder().encode(salt.getBytes(StandardCharsets.UTF_8));

        // combine the salt and verification token into one line
        String firstLine = new String(encodedBytes, StandardCharsets.UTF_8) + ":" + verificationToken;

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
        }
    }

    // generates a random salt string
    private static String getNewSalt(){
        return "salt";
    }

    // encrypts a password using the given key
    // returns the encrypted password in base64
    private static String encrypt(String password, SecretKey key){
        return "";
    }

    // decrypts the given token with the key and returns the decrypted password
    private static String decrypt(String token, SecretKey key){
        return "";
    }

    // generates initialilzation vector for AES algorithm
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}

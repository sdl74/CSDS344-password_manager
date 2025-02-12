import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class PasswordManager {
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
        FileWriter writer;
        if(exists) {
            try {
                writer = new FileWriter("password.txt");
            } catch (Exception e) {

            }
        }

        else {
            // creating a new file
            try {
                file.createNewFile();
            } catch (Exception e) {
                
            }
            try {
                writer = new FileWriter("password.txt");
                System.out.println("No password file detected. Creating a new password file.");
                // create key
                // store salt
                String option = "";

                while(!option.equals("q")) {
                    System.out.println("a : Add Password \n r : Read Password \n q : Quit");
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
                writer.write("hello");
                writer.close();
            } catch (Exception e) {
                
            }
        }
    }
}

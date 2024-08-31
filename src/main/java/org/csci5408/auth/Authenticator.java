package org.csci5408.auth;

import org.csci5408.cli.CLIHandler;
import org.csci5408.util.Constants;
import org.csci5408.util.FileUtils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


import static org.csci5408.auth.CaptchaGenerator.captchaGenerator;


public class Authenticator {

    public static String username;

    /**
     *The method regiserUser() takes the input from the user for creating an account in the system. It logs in the user
     * directly after signing up.
     * @return
     */

    public static boolean registerUser() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            System.out.print("Security Question - What is your favourite sport ? ");
            String securityQuestionAnswer = scanner.nextLine();

            // Hash the password for security
            String hashedPassword = null;
            try {
                hashedPassword = secureHashPassword(password);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            String captcha=captchaGenerator();
            System.out.println("Enter the captcha :"+ captcha);
            String user_input= scanner.nextLine();
            if(captcha.equals(user_input)){
                try {

                    String authFile = Constants.DATABASE_PATH + Constants.AUTH_FILE;
                    FileWriter writer = new FileWriter(authFile, true);
                    writer.write(username + Constants.DELIMINATOR + hashedPassword + Constants.DELIMINATOR + securityQuestionAnswer + "\n");
                    writer.close();
                    System.out.println("User signed up successfully.");
                    Authenticator.username=username;
                    return true;
                } catch (Exception e) {
                    System.out.println("Error signing up: " + e.getMessage());
                    return false;
                }
            }
            else {
                System.out.println("Incorrect captcha entered .");
            }



        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     *The method generated hashPassword based on the password input by the user.
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String secureHashPassword(String password) throws NoSuchAlgorithmException {
        // Obtain an instance of MessageDigest for MD5
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        // Update the digest using the byte array of the password
        messageDigest.update(password.getBytes());

        // Complete the hash computation
        byte[] hashedBytes = messageDigest.digest();

        // Convert the byte array into a hexadecimal string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashedBytes) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }

    /**
     *The method logs in the user. It takes input from the user and then if the input is correct, the user can log in
     * @return
     */
    public static boolean loginUser(){
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            System.out.print("Enter security answer: ");
            String securityAnswer = scanner.nextLine();

            // Hash the password for comparison
            String hashedPassword = secureHashPassword(password);

            // Define the file path for the authentication file
            String authFilePath = Constants.DATABASE_PATH + Constants.AUTH_FILE;

            String captcha=captchaGenerator();
            System.out.println("Enter the captcha : "+ captcha);
            String user_input= scanner.nextLine();

            // Read the authentication file to find a matching user
            File file = new File(authFilePath);
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] userDetails = line.split(Constants.DELIMINATOR);

                    if (userDetails.length == 3 && userDetails[0].equals(username)
                            && userDetails[1].equals(hashedPassword) && userDetails[2].equals(securityAnswer)
                            && captcha.equals(user_input)) {

                        Authenticator.username=username;

                        return true;
                    }
                }
            }

            // If no match was found

            return false;
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    /**
     *The method asks user whether to signup or login.
     * @throws IOException
     */
    public static void authenticate() throws IOException {
        FileUtils.ensureDirectoryExists(Constants.DATABASE_PATH + Constants.AUTH_FILE);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 0 to sign up, 1 to log in, or any other key to exit:");

        String input = scanner.nextLine();

        switch (input) {
            case "0":
                // Call the signup method
                if (registerUser()) {
                    System.out.println("Signup successful. ");
                } else {
                    System.out.println("Signup failed. Please try again.");
                }
                break;
            case "1":
                // Call the login method
                if (loginUser()) {

                    System.out.println("Login successful. Welcome back!");

                } else {
                    System.out.println("Login failed. Please try again.");
                    System.exit(0);
                }
                break;
            default:
                // Exit the system
                System.out.println("Authentication failed. Exiting system.");
                System.exit(0);
        }
        return ;
    }



}



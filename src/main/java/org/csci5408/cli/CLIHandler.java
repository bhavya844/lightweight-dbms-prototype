package org.csci5408.cli;


import org.csci5408.commands.IQueryCommand;
import org.csci5408.commands.QueryFactory;
import org.csci5408.util.Constants;
import org.csci5408.util.FileUtils;

import java.util.Scanner;

import static org.csci5408.util.FileUtils.ensureDirectoryExists;

public class CLIHandler {
    private static Scanner scanner=new Scanner(System.in);

    public static String databaseName = null;

    /**
     * The method takes the name of the database from the user
     * @return
     */
    private static String promptForDatabaseName() {
        String dbName = FileUtils.findExistingDirectory(Constants.DATABASE_PATH);
        while (dbName == null || dbName.isEmpty()) {
            System.out.print("Enter a name for a new database: ");
            dbName = scanner.nextLine().trim();
        }
        return dbName;
    }

    /**
     *The method takes the input query from the user.
     * @return
     */
    private static String collectInputFromUser() {
        StringBuilder inputBuilder = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            inputBuilder.append(line).append("\n");
            if (line.contains(";")) {
                break;
            }
            if(line.equalsIgnoreCase("exit")){
                System.exit(0);
            }
            System.out.print("\t-> ");
        }
        return inputBuilder.toString();
    }

    /**
     *The method splits the query separated by ; . It then passes the individual queries to the parser for processing.
     */
    public static void run(){
        ensureDirectoryExists(Constants.DATABASE_PATH);
        databaseName = promptForDatabaseName();
        ensureDirectoryExists(Constants.DATABASE_PATH + databaseName + "/" + Constants.META_DIRECTORY);
        ensureDirectoryExists(Constants.DATABASE_PATH + databaseName + "/" + Constants.DATA_DIRECTORY);

        while (true) {
            try {
                System.out.print("MyDB [" + databaseName + "]> ");
                String queryInput = collectInputFromUser();
                String[] queries = queryInput.split(";");
                for (String query : queries) {
                    if (!query.trim().isEmpty()) {
                        processQuery(query.trim());
                        System.out.println(); // Ensure a newline after each query execution
                    }
                }
            } catch (Exception exception) {
                System.out.println("Error executing query: " + exception.getClass() + ": " + exception.getMessage());
            }
        }
    }

    /**
     *It processes the query input from the user.
     * @param query
     */
    public static void processQuery(String query)
         {
            try {
                IQueryCommand command = QueryFactory.command(query);
                command.execute();
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }





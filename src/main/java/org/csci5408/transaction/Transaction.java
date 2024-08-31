package org.csci5408.transaction;

import org.csci5408.auth.Authenticator;
import org.csci5408.commands.*;
import org.csci5408.cli.CLIHandler;
import org.csci5408.util.Constants;
import org.csci5408.util.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.csci5408.cli.CLIHandler.processQuery;

public class Transaction implements IQueryCommand {

        private String query;
        private String tableName;
        private List<ColumnDefinition> columnDefinition = new ArrayList<>();
        private List<String> queryList = new ArrayList<>();

        Scanner sc = new Scanner(System.in);

        /**
         *This method basically calls the collectQueries method
         */
        public Transaction() {
                collectQueries();
        }

        /**
         *This method starts the transaction based on the input entered by the user.
         */
        private void collectQueries() {
                System.out.println("Enter your queries (type 'BEGIN TRANSACTION' to start, 'COMMIT;' to execute, 'ROLLBACK;' to cancel):");

                String userQuery;
                boolean inTransaction = false;

                while (true) {
                        userQuery = sc.nextLine();
                        Pattern beginPattern = Pattern.compile("^BEGIN TRANSACTION;$", Pattern.CASE_INSENSITIVE);
                        Pattern commitPattern = Pattern.compile("^COMMIT;?$", Pattern.CASE_INSENSITIVE);
                        Pattern rollbackPattern = Pattern.compile("^ROLLBACK;?$", Pattern.CASE_INSENSITIVE);

                        if (beginPattern.matcher(userQuery).matches()) {
                                inTransaction = true;
                                System.out.println("Transaction started. Please enter your queries.");
                                String filePath= Constants.DATABASE_PATH + Constants.CSV_FILE ;
                                String user_info= Authenticator.username;
                                FileUtils.writeToCsv(filePath, "Transaction Started", userQuery, user_info );
                        } else if (commitPattern.matcher(userQuery).matches() && inTransaction) {
                                String filePath= Constants.DATABASE_PATH + Constants.CSV_FILE ;
                                String user_info= Authenticator.username;
                                FileUtils.writeToCsv(filePath, "Commit", userQuery, user_info );
                                System.out.println("Committing transaction...");
                                execute();
                                break;
                        } else if (rollbackPattern.matcher(userQuery).matches() && inTransaction) {
                                String filePath= Constants.DATABASE_PATH + Constants.CSV_FILE ;
                                String user_info= Authenticator.username;
                                FileUtils.writeToCsv(filePath, "Rollback", userQuery, user_info );
                                System.out.println("Transaction rolled back.");
                                queryList.clear();

                        } else if (inTransaction && isValidQuery(userQuery)) {
                                queryList.add(userQuery);
                                System.out.println("Query added to transaction.");
                        } else {
                                System.out.println("Invalid command. Please start a transaction with 'BEGIN TRANSACTION' or enter a valid SQL command.");
                        }
                }

        }

        /**
         * This method checks whether the query entered by the user is correct or not.
         * @param query
         * @return
         */
        private boolean isValidQuery(String query) {
                String upperCaseQuery = query.toUpperCase();
                return upperCaseQuery.startsWith("SELECT") || upperCaseQuery.startsWith("INSERT");
        }

        /**
         * This method executes the query if it starts with select or insert
         */
        @Override
        public void execute() {
                        for (String query : queryList) {
                                  if (query.split(" ")[0].equalsIgnoreCase("select")) {
                                        SelectCommand selectCommand = new SelectCommand(query);
                                        selectCommand.execute();
                                } else if (query.split(" ")[0].equalsIgnoreCase("insert")) {
                                        InsertCommand insertCommand = new InsertCommand(query);
                                        insertCommand.execute();
                                }
                        }
                        queryList.clear();
                }
        }

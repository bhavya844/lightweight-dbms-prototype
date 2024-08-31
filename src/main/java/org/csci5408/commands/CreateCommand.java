package org.csci5408.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.util.stream.Collectors;

import org.csci5408.auth.Authenticator;
import org.csci5408.cli.CLIHandler;
import org.csci5408.util.Constants;
import org.csci5408.util.FileUtils;

public class CreateCommand implements IQueryCommand {
    private String query;
    private String tableName;
    private List<ColumnDefinition> columnDefinition = new ArrayList<>();

    /**
     *The method is the parser for Create queries
     * @param query
     */
    public CreateCommand(String query) {
        this.query = query;

        String[] parts = query.split("\\s+", 3);
        this.tableName = parts[2].substring(0, parts[2].indexOf('(')).trim();
        String columnsString = query.substring(query.indexOf('(') + 1, query.lastIndexOf(')'));
        for (String columnEntry : columnsString.split(",")) {
            String[] columnDetail = columnEntry.trim().split("\\s+");
            columnDefinition.add(new ColumnDefinition(columnDetail[0], columnDetail[1]));
        }
        String filePath= Constants.DATABASE_PATH + Constants.CSV_FILE ;
        String user_info= Authenticator.username;
        FileUtils.writeToCsv(filePath, this.tableName, query, user_info );
}

    /**
     *The query parsed is now executed based on the table names and the data types input from the user
     */
    @Override
    public void execute() {
        File schemaFile = new File(Constants.DATABASE_PATH + CLIHandler.databaseName + "/" + Constants.META_DIRECTORY + this.tableName + Constants.EXTENSION);
        File tableFile = new File(Constants.DATABASE_PATH + CLIHandler.databaseName + "/" + Constants.DATA_DIRECTORY + this.tableName + Constants.EXTENSION);

        try {
            // Ensure the table and its schema don't already exist
            if (schemaFile.createNewFile() && tableFile.createNewFile()) {
//                // Construct the header (column names) as the first row for the table file
            List<List<String>> headerRow = new ArrayList<>();
            headerRow.add(columnDefinition.stream()
                    .map(ColumnDefinition::getName)
                    .collect(Collectors.toList()));

//            // Write the header row to the table file
            FileUtils.writeLinesToFile(tableFile, headerRow, false);
//                }
                List<List<String>> schemaData = new ArrayList<>();
                for (ColumnDefinition colDef : columnDefinition) {
                    // Each column definition is now an inner list of strings
                    List<String> columnData = Arrays.asList(colDef.getName(), colDef.getDataType());
                    schemaData.add(columnData);
                }

                // Assuming a method in FileUtils to write a List<String> to a file
                FileUtils.writeLinesToFile(schemaFile, schemaData,false);
                System.out.println("Table successfully created");
            } else {
                System.out.println("Creation failed: Table " + tableName + " already exists.");
            }
        } catch (Exception e) {
            System.err.println("Failed to create table " + tableName + ": " + e.getMessage());
        }
    }

    }



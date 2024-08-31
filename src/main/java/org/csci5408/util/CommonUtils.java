package org.csci5408.util;

import org.csci5408.cli.CLIHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CommonUtils {
    /**
     * This method basically gets the name of the columns in the table
     * @param tableName
     * @return
     */
    public static List<String> getTableColumnNames(String tableName) {
        List<String> columnNames = new ArrayList<>();
        String schemaFilePath = Constants.DATABASE_PATH  + CLIHandler.databaseName + "/" + Constants.META_DIRECTORY  + tableName + Constants.EXTENSION;

        File schemaFile = new File(schemaFilePath);
        if (!schemaFile.exists()) {
            System.err.println("Schema file for table " + tableName + " does not exist.");
            return columnNames; // Return an empty list if the schema file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(schemaFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Assuming the schema file format is "columnName dataType"
                // or just "columnName" if types are not specified.
                String columnName = line.split("\\s+")[0]; // Extract the column name
                columnNames.add(columnName);
            }
        } catch (Exception e) {
            System.err.println("Error reading schema file for table " + tableName + ": " + e.getMessage());
        }
        System.out.println(columnNames);
        return columnNames;
    }
    }


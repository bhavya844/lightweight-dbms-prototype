package org.csci5408.commands;

import org.csci5408.auth.Authenticator;
import org.csci5408.cli.CLIHandler;
import org.csci5408.util.CommonUtils;
import org.csci5408.util.Constants;
import org.csci5408.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InsertCommand implements IQueryCommand{
    private String query;
    private String tableName;
    private List<String> columnNames= new ArrayList<>();
    private List<List<String>> values=new ArrayList<>();

    /**
     * The method is used for parsing the Insert queries.
     * @param query
     */
    public InsertCommand(String query) {

        this.query = query;
        Pattern insertPattern= Pattern.compile("INSERT\\s+INTO\\s+(\\w+)\\s*(?:\\(([^)]+)\\))?\\s*VALUES\\s+((?:\\([^)]+\\),?\\s*)+);?",Pattern.CASE_INSENSITIVE);
        Matcher matcher=insertPattern.matcher(query);
        if (matcher.matches()) {
            tableName = matcher.group(1);
            String columnsPart = matcher.group(2);
            String valuesPart = matcher.group(3);

            if (columnsPart != null && !columnsPart.trim().isEmpty()) {
                columnNames = parseColumns(columnsPart);
            }

            values = parseValues(valuesPart);

//            String filePath_1= Constants.DATABASE_PATH + Constants.LOGGER_FILE ;
            String filePath= Constants.DATABASE_PATH + Constants.CSV_FILE ;
            String user_info= Authenticator.username;
            FileUtils.writeToCsv(filePath, matcher.group(3), query, user_info );
        } else {
            throw new IllegalArgumentException("Invalid INSERT query format.");
        }
    }

    /**
     *This method parses the name of the columns from the query
     * @param columnsPart
     * @return
     */
    private List<String> parseColumns(String columnsPart) {
        columnsPart = columnsPart.trim();
        return List.of(columnsPart.split("\\s*,\\s*"));
    }

    /**
     *This method parses the values required to be entered to the columns
     * @param valuesPart
     * @return
     */
    private List<List<String>> parseValues(String valuesPart) {
        List<List<String>> parsedValues = new ArrayList<>();
        Pattern valuePattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher valueMatcher = valuePattern.matcher(valuesPart);

        while (valueMatcher.find()) {
            String valueGroup = valueMatcher.group(1);
            List<String> rowValues = List.of(valueGroup.split("\\s*,\\s*"));
            parsedValues.add(rowValues);
        }
        return parsedValues;

    }

    /**
     * This method performs the execution based on the parsed values and columns
     */
    @Override
    public void execute(){


        // Fithrow new RuntimeException(e);rst, check if the table exists by attempting to retrieve its column order
        List<String> schemaColumns = CommonUtils.getTableColumnNames(tableName);
        List<String> schemaColumnNames = schemaColumns.stream()
                .map(col -> col.split(";")[0]) // Assumes every entry is in the format "name;type"
                .collect(Collectors.toList());
        if (schemaColumns.isEmpty()) {
            throw new IllegalStateException("Table " + tableName + " does not exist.");
        }
        // If no columns were specified in the query, assume all columns in schema order
        if (columnNames == null || columnNames.isEmpty()) {
            columnNames = new ArrayList<>(schemaColumnNames);

        }
        else{

        }
        // Verify all specified columns exist in the table schema
        for (String colName : columnNames) {
            colName=colName.trim();
            if (!schemaColumnNames.contains(colName)) {
                throw new IllegalArgumentException("Column " + colName + " does not exist in table " + tableName);
            }
        }
        // Prepare data for insertion: match the schema's column order, filling missing values with NULL or default values
        List<List<String>> dataToInsert = alignDataWithSchema(values, columnNames, schemaColumnNames);

        // Path to the data file for the table
        String dataFilePath = Constants.DATABASE_PATH + CLIHandler.databaseName + "/" + Constants.DATA_DIRECTORY + this.tableName + Constants.EXTENSION;
        File dataFile = new File(dataFilePath);

        // Ensure the data directory exists
        dataFile.getParentFile().mkdirs();

        // Write the data to the file
        try {
            FileUtils.writeLinesToFile(dataFile, dataToInsert, true); // Append the data
            System.out.println("Data successfully inserted into " + tableName);
        } catch (Exception e) {
            System.err.println("Error during data insertion into " + tableName + ": " + e.getMessage());
        }

        }
    private List<List<String>> alignDataWithSchema(List<List<String>> values, List<String> columnNames, List<String> schemaColumnNames) {
        List<List<String>> alignedData = new ArrayList<>();

        for (List<String> row : values) {
            List<String> newRow = new ArrayList<>();
            // Initialize newRow with NULLs or default values for all schema columns
            schemaColumnNames.forEach(col -> newRow.add("NULL"));

            // Place each value in its correct position according to the schema
            for (int i = 0; i < columnNames.size(); i++) {
                int schemaIndex = schemaColumnNames.indexOf(columnNames.get(i));
                newRow.set(schemaIndex, row.get(i));
            }

            alignedData.add(newRow);
        }

        return alignedData;
    }

}




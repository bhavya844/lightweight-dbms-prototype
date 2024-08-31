package org.csci5408.commands;

import org.csci5408.auth.Authenticator;
import org.csci5408.cli.CLIHandler;
import org.csci5408.util.Constants;
import org.csci5408.util.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SelectCommand implements IQueryCommand{

    private String query;
    private String tableName;
    private List<String> columnNames= new ArrayList<>();

    /**
     *It parses the query which perform the select operation
     * @param query
     */
    public SelectCommand(String query) {
        this.query = query;

        Pattern queryPattern = Pattern.compile(
                "SELECT\\s+(\\*|[^\\s]+(?:\\s*,\\s*[^\\s]+)*)\\s+FROM\\s+(\\w+);?",
                Pattern.CASE_INSENSITIVE
        );
        Matcher queryMatcher = queryPattern.matcher(query);
        List<String> columns;
        String columnsPart = null;
        if (queryMatcher.matches()) {
            this.tableName = queryMatcher.group(2);
            columnsPart = queryMatcher.group(1);

            columns = new ArrayList<>();
            if ("*".equals(columnsPart)) {
                columns = null;
        } else {
            this.columnNames = Arrays.asList(columnsPart.split("\\s*,\\s*"));
        }
            String filePath= Constants.DATABASE_PATH + Constants.CSV_FILE ;
            String user_info= Authenticator.username;
            FileUtils.writeToCsv(filePath, queryMatcher.group(2), query, user_info );
    }}

    /**
     *The method executes the select queries after parsing it
     */
    @Override
    public void execute() {
        String filePath = Constants.DATABASE_PATH + CLIHandler.databaseName + "/" + Constants.DATA_DIRECTORY + this.tableName + Constants.EXTENSION;

        List<List<String>> tableData = FileUtils.readFileByLine(filePath);
        if (tableData.isEmpty()) {
            System.out.println("The table " + tableName + " is empty or does not exist.");
        }
        List<String> allColumnNames = tableData.get(0);
        if (this.columnNames == null || this.columnNames.isEmpty()) {
            this.columnNames = new ArrayList<>(allColumnNames);
        }
        // Determine indices of the selected columns
        List<Integer> selectedColumnIndices = getColumnIndices(allColumnNames, this.columnNames);
        // Print selected column names
        System.out.println(columnNames.stream().collect(Collectors.joining(", ")));
        tableData.stream().skip(1) // Skip header row
                .map(row -> selectColumns(row, selectedColumnIndices))
                .forEach(row -> System.out.println(String.join(", ", row)));
    }

    /**
     *
     * @param allColumnNames
     * @param selectedColumnNames
     * @return
     */
    private List<Integer> getColumnIndices(List<String> allColumnNames, List<String> selectedColumnNames) {
        return selectedColumnNames.stream()
                .map(selColName -> allColumnNames.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList())
                        .indexOf(selColName.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     *
     * @param row
     * @param selectedColumnIndices
     * @return
     */
    private List<String> selectColumns(List<String> row, List<Integer> selectedColumnIndices) {
        return selectedColumnIndices.stream()
                .filter(index -> index >= 0) // Ensure the index is valid
                .map(row::get)
                .collect(Collectors.toList());
    }


}






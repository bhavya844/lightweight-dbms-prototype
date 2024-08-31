package org.csci5408.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FileUtils {
    /**
     * This method generated a directory if it does not exist.
     * @param path
     */
    public static void ensureDirectoryExists(String path) {
        File directory = new File(path);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Directory could not be created: " + path);
        }
    }

    /**
     * This method finds the path of an existing directory
     * @param path
     * @return
     */
    public static String findExistingDirectory(String path) {
        File directory = new File(path);
        if (directory.isDirectory()) {
            String[] subdirectories = directory.list();
            if (subdirectories != null) {
                for (String subdir : subdirectories) {
                    File file = new File(directory, subdir);
                    if (file.isDirectory()) {
                        return subdir;
                    }
                }
            }
        }
        return null;
    }

    /**
     * This method performs writing to a file
     *
     * @param file
     * @param data
     * @param append
     * @throws IOException
     */
    public static void writeLinesToFile(File file, List<List<String>> data, boolean append) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), append))) {
            for (List<String> row : data) {
                String rowContent = String.join(Constants.DELIMINATOR, row);
                bw.write(rowContent);
                bw.newLine();
            }

        }
    }

    /**
     * This method performs reading from a file
     * @param filePath
     * @return
     */
    public static List<List<String>> readFileByLine(String filePath) {
        List<List<String>> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> row = Arrays.asList(line.split(Constants.DELIMINATOR));
                data.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * This method writes to the CSV
     * Reference -  https://www.baeldung.com/java-csv
     * @param filepath
     * @param data
     * @param query
     * @param userinfo
     */
    public static void writeToCsv (String filepath, String data, String query, String userinfo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath, true))) {
            String removeNewLine = data.replace("\n", " ");
            bw.write(removeNewLine.replace(",", ";") + "," + query.replace(",",";") + "," + userinfo + "," + new Date());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

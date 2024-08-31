package org.csci5408.commands;

public class ColumnDefinition {
    private String name;
    private String dataType;

    public ColumnDefinition(String name, String dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    /**
     *The method performs getter and setter of the column name and data type for the columns
     * @return
     */
    // Getter for column name
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    // Setter for column name
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    // Getter for data type
    public String getDataType() {
        return dataType;
    }

    /**
     *
     * @param dataType
     */
    // Setter for data type
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}

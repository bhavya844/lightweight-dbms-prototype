package org.csci5408.commands;

import org.csci5408.transaction.Transaction;

public class QueryFactory {
    /**
     *This method decides which method to be called based on the input from the user
     * @param query
     * @return
     */
    public static IQueryCommand command(String query) {
        if (query.trim().toUpperCase().startsWith("CREATE")) {
            return new CreateCommand(query);
        } else if (query.trim().toUpperCase().startsWith("SELECT")) {
            return new SelectCommand(query);
        } else if (query.trim().toUpperCase().startsWith("INSERT")) {
            return new InsertCommand(query);
        } else if (query.trim().toUpperCase().startsWith("START")){
            return new Transaction();
        }
        else {
            throw new IllegalArgumentException("Unsupported query: " + query);
        }
    }
}

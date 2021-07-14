package com.galvanize.invoicify.repository.repositories.sharedfiles;

import com.sun.istack.NotNull;

/**
 * <h1>QueryTableNameModifier</h1>
 *
 * <p>Provides helper method/s to assist in query-tablename resolution.</p>
 */
public class QueryTableNameModifier {
    /**
     * Replaces the targeted query with all nominal place holders (tableName)
     * with the selected table targeted for the repository.
     * @param query: String query to manipulate (final member) so new copy given
     * @param tableName: String name of table for which entity repository corresponds with
     * @return amended query with table names inserted at placeholders.
     */
    public static @NotNull String insertTableNameIntoQuery(
            @NotNull final String query,
            @NotNull final String tableName){
        //todo: refactor for multi-table modification
        return null; // query.replaceAll("tableName",tableName );
    }
}

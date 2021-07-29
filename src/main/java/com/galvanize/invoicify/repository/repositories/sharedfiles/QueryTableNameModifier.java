package com.galvanize.invoicify.repository.repositories.sharedfiles;

import com.sun.istack.NotNull;

/**
 * <h1>QueryTableNameModifier</h1>
 *
 * <p>Provides helper method/s to assist in query-tablename resolution.</p>
 */
public class QueryTableNameModifier {
    /**
     * <p>
     * Replaces the targeted query with all nominal place holders (t#) to their corresponding tableName.
     * NOTE: t1 - tableName[0]
     *
     * ex:
     *  - input:
     *    -  query: Select t1.*, t2.* From t1, t2 Where t1.id = t2.childId
     *    - tableNames: table1, table2
     *  - output: Select table1.*, table2.* From table1, table2 Where table1.id = table2.childId
     *
     *  ex:
     *   - input:
     *    - query: Select t1.* From t1
     *    - tableNames: table1
     *   - output: Select table1.* From table1
     *   </p>
     *
     * @param query: String query to manipulate
     * @param tableNames: String name of tables for which entity repository corresponds with
     * @return amended query with table names inserted at placeholders.
     */
    public static @NotNull String insertTableNamesIntoQuery(
            @NotNull String query,
            @NotNull final String... tableNames){

        for(int i = 0; i < tableNames.length; i++){
            final String tableStringId = String.format("t%d", i + 1);
            query = query.replaceAll(tableStringId, tableNames[i]);
        }

        return query;
    }
}

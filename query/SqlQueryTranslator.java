package query;

import datasets.Dataset;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SqlQueryTranslator extends QueryTranslator{


    @Override
    public String translate(QueryType type) {
        return "SELECT * FROM test_table;";
    }

    @Override
    public String translateCreateTable(Dataset dataset) {
        var columnsChunk = String.join(", ",
                dataset.getColumnNamesWithTypes()
                        .stream()
                        .map(kv -> String.format("\n%s %s", kv.getKey(), kv.getValue()))
                        .toArray(String[]::new)
        );

        return String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s\n);", dataset.getTableName(), columnsChunk
        );
    }

//    @Override
//    public String translateInsertInto(Dataset dataset, List<String> values) {
//        var columns = dataset.getColumnNamesWithTypes();
//        var columnsChunk = String.join(", ",
//                columns
//                        .stream()
//                        .map(kv -> kv.getKey())
//                        .toArray(String[]::new)
//        );
//
//        // setting questions marks instead of values in order to insert values in a proper way (by "setString" etc. methods)
//        var questionMarksChunk = String.join(", ",
//                columns
//                        .stream()
//                        .map(kv -> "?")
//                        .toArray(String[]::new)
//        );
//
//        return String.format(
//                "INSERT INTO %s (%s) VALUES (%s);", dataset.getTableName(), columnsChunk, questionMarksChunk
//        );
//
//    }

}

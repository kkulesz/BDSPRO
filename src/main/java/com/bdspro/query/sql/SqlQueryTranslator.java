package com.bdspro.query.sql;

import com.bdspro.datasets.Dataset;
import com.bdspro.query.QueryTranslator;
import com.bdspro.query.QueryType;

import java.sql.Timestamp;
import java.util.Map;
import java.util.stream.IntStream;

public class SqlQueryTranslator extends QueryTranslator {
    @Override
    public String translateCreateTable(Dataset dataset) {
        var columnsChunk = String.join(", ",
                dataset.getColumnNamesWithTypes()
                        .stream()
                        .map(kv -> String.format("\n%s %s", kv.getKey(), SqlColumnTypeMapper.mapColumnType(kv.getValue())))
                        .toArray(String[]::new)
        );

        return String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s\n);", dataset.getTableName(), columnsChunk
        );
    }

    @Override
    public String translateInsertInto(Dataset dataset, String[][] values) {
        var columnNamesWithTypes = dataset.getColumnNamesWithTypes();
        var columnNamesChunk = String.join(", ",
                columnNamesWithTypes
                        .stream()
                        .map(kv -> kv.getKey())
                        .toArray(String[]::new)
        );

        var valuesChunk = String.join(", ",
                IntStream.range(0, columnNamesWithTypes.size())
                        .mapToObj(i -> Map.entry(columnNamesWithTypes.get(i).getValue(), values[0][i]))
                        .map(kv -> SqlColumnTypeMapper.mapValueToMatchTypeFormat(kv))
                        .toArray(String[]::new));


        return  String.format(
                "INSERT INTO %s \n\t(%s) \n\tVALUES \n\t(%s);", dataset.getTableName(), columnNamesChunk, valuesChunk
        );
    }

    @Override
    public String translateExactPoint(Dataset dataset, Timestamp timestamp) {
        var table = dataset.getTableName();
        var tsColumn = dataset.getTimeStampColumnName();
        return String.format(
                "SELECT * FROM %s \n\t WHERE %s='%s'", table, tsColumn, timestamp
        );
    }

    @Override
    public String translateRangeAnyEntity(Dataset dataset, Timestamp from, Timestamp until) {
        var table = dataset.getTableName();
        var tsColumn = dataset.getTimeStampColumnName();
        return String.format(
                "SELECT * FROM %s \n\t WHERE %s>='%s' AND %s<='%s';", table, tsColumn, from, tsColumn, until
        );
    }

    @Override
    public String translateRangeSingleEntity(Dataset dataset, String entity, Timestamp from, Timestamp until) {
        var table = dataset.getTableName();
        var tsColumn = dataset.getTimeStampColumnName();
        var entityColumn = dataset.getEntityColumnName();
        return String.format(
                "SELECT * FROM %s \n\t WHERE %s='%s' AND %s>='%s' AND %s<='%s';", table, entityColumn, entity, tsColumn, from, tsColumn, until
        );
    }

    @Override
    public String translateRangeQueryWithAggregation(Dataset dataset, Timestamp from, Timestamp until) {
        var table = dataset.getTableName();
        var tsColumn = dataset.getTimeStampColumnName();
        return String.format(
                "SELECT MAX(%s) FROM %s \n\t WHERE %s>='%s' AND %s<='%s';", tsColumn, table, tsColumn, from, tsColumn, until
        );
    }

}

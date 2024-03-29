package com.bdspro.query.sql;

import com.bdspro.datasets.ColumnType;
import com.bdspro.datasets.Dataset;
import com.bdspro.query.QueryTranslator;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class SqlQueryTranslator extends QueryTranslator {
    @Override
    public String translateCreateTable(Dataset dataset) {
        String columnsChunk = String.join(", ",
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
    public String translateSelectCount(String tableName) {
        return String.format(
                "SELECT COUNT(*) FROM %s", tableName
        );
    }

    @Override
    public String translateInsertInto(Dataset dataset, String[] values) {
        String columnNamesChunk = _getColumnNamesChunk(dataset);
        String valuesChunk = _getSingleRowValuesChunk(dataset, values);

        return  String.format(
                "INSERT INTO %s \n\t(%s) \n\tVALUES \n\t%s;", dataset.getTableName(), columnNamesChunk, valuesChunk
        );
    }

    @Override
    public String translateBatchInsertInto(Dataset dataset, String[][] batch) {
        String columnNamesChunk = _getColumnNamesChunk(dataset);
        String valuesChunk = String.join(",",
                Arrays.stream(batch)
                        .map(row -> _getSingleRowValuesChunk(dataset, row))
                        .toArray(String[]::new)
                );
        return  String.format(
                "INSERT INTO %s \n\t(%s) \n\tVALUES \n\t%s;", dataset.getTableName(), columnNamesChunk, valuesChunk
        );
    }

    @Override
    public String translateExactPoint(Dataset dataset, Timestamp timestamp) {
        String table = dataset.getTableName();
        String tsColumn = dataset.getTimeStampColumnName();
        return String.format(
                "SELECT * FROM %s \n\t WHERE %s='%s'", table, tsColumn, timestamp
        );
    }

    @Override
    public String translateRangeAnyEntity(Dataset dataset, Timestamp from, Timestamp until) {
        String table = dataset.getTableName();
        String tsColumn = dataset.getTimeStampColumnName();
        return String.format(
                "SELECT * FROM %s \n\t WHERE %s>='%s' AND %s<='%s';", table, tsColumn, from, tsColumn, until
        );
    }

    @Override
    public String translateRangeSingleEntity(Dataset dataset, String entity, Timestamp from, Timestamp until) {
        String table = dataset.getTableName();
        String tsColumn = dataset.getTimeStampColumnName();
        String entityColumn = dataset.getEntityColumnName();
        return String.format(
                "SELECT * FROM %s \n\t WHERE %s='%s' AND %s>='%s' AND %s<='%s';", table, entityColumn, entity, tsColumn, from, tsColumn, until
        );
    }

    @Override
    public String translateRangeWithAggregationOnTimeColumn(Dataset dataset, Timestamp from, Timestamp until) {
        String table = dataset.getTableName();
        String tsColumn = dataset.getTimeStampColumnName();
        return String.format(
                "SELECT MAX(%s) FROM %s \n\t WHERE %s>='%s' AND %s<='%s';", tsColumn, table, tsColumn, from, tsColumn, until
        );
    }

    @Override
    public String translateRangeWithValueFilter(Dataset dataset, Timestamp from, Timestamp until, double value) {
        String table = dataset.getTableName();
        String tsColumn = dataset.getTimeStampColumnName();
        String valueColumn = dataset.getValueColumnName();

        return String.format(
                "SELECT * FROM %s \n\t WHERE %s>='%s' AND %s<='%s' AND %s=%s;", table, tsColumn, from, tsColumn, until, valueColumn, value
        );
    }

    @Override
    public String translateLastNRecords(Dataset dataset, int numberOfRecords) {
        String table = dataset.getTableName();
        String tsColumn = dataset.getTimeStampColumnName();
        return String.format(
                "SELECT * FROM %s \n\t ORDER BY %s LIMIT %s;", table, tsColumn, numberOfRecords
        );
    }

    @Override
    public String translateRangeWithAggregation(Dataset dataset, Timestamp from, Timestamp until) {
        String table = dataset.getTableName();
        String tsColumn = dataset.getTimeStampColumnName();
        String valueColumn = dataset.getValueColumnName();

        return String.format(
                "SELECT MAX(%s) FROM %s \n\t WHERE %s>='%s' AND %s<='%s';",valueColumn, table, tsColumn, from, tsColumn, until
        );
    }

    @Override
    public String translateRangeWithAggregationWithinGroup(Dataset dataset, Timestamp from, Timestamp until) {
        String table = dataset.getTableName();
        String tsColumn = dataset.getTimeStampColumnName();
        String valueColumn = dataset.getValueColumnName();
        String entityColumn = dataset.getEntityColumnName();
        return String.format(
                "SELECT MAX(%s), %s FROM %s \n\t WHERE %s>='%s' AND %s<='%s' GROUP BY %s;", valueColumn, entityColumn, table, tsColumn, from, tsColumn, until, entityColumn
        );
    }

    @Override
    public String translateRangeWithAggregationAndValueFilter(Dataset dataset, Timestamp from, Timestamp until, double value) {
        String table = dataset.getTableName();
        String tsColumn = dataset.getTimeStampColumnName();
        String valueColumn = dataset.getValueColumnName();

        return String.format(
                "SELECT SUM(%s) FROM %s \n\t WHERE %s>='%s' AND %s<='%s' AND %s>%s;", valueColumn, table, tsColumn, from, tsColumn, until, valueColumn, value
        );
    }

    @Override
    public String translateRangeWithLimit(Dataset dataset, Timestamp from, Timestamp until, int limit) {
        String table = dataset.getTableName();
        String tsColumn = dataset.getTimeStampColumnName();

        return String.format(
                "SELECT * FROM %s \n\t WHERE %s>='%s' AND %s<='%s' LIMIT %s;", table, tsColumn, from, tsColumn, until, limit
        );
    }

    @Override
    public String translateRangeWithOrderByValue(Dataset dataset, Timestamp from, Timestamp until) {
        String table = dataset.getTableName();
        String tsColumn = dataset.getTimeStampColumnName();
        String valueColumn = dataset.getValueColumnName();

        return String.format(
                "SELECT * FROM %s \n\t WHERE %s>='%s' AND %s<='%s' ORDER BY %s;", table, tsColumn, from, tsColumn, until, valueColumn
        );
    }

    @Override
    public String translateLatestPoint(Dataset dataset) {
        String table = dataset.getTableName();
        String tsColumn = dataset.getTimeStampColumnName();

        return String.format(
                "SELECT * FROM %s \n\t ORDER BY %s DESC LIMIT 1;", table, tsColumn
        );
    }

    private String _getColumnNamesChunk(Dataset dataset){
        List<Map.Entry<String, ColumnType>> columnNamesWithTypes = dataset.getColumnNamesWithTypes();
        return String.join(", ",
                columnNamesWithTypes
                        .stream()
                        .map(Map.Entry::getKey)
                        .toArray(String[]::new)
        );
    }

    private String _getSingleRowValuesChunk(Dataset dataset, String[] values){
        List<Map.Entry<String, ColumnType>> columnNamesWithTypes = dataset.getColumnNamesWithTypes();
        String valuesChunk = String.join(", ",
                IntStream.range(0, columnNamesWithTypes.size())
                        .mapToObj(i -> Map.entry(columnNamesWithTypes.get(i).getValue(), values[i]))
                        .map(SqlColumnTypeMapper::mapValueToMatchTypeFormat)
                        .toArray(String[]::new));

        return String.format("(%s)", valuesChunk);
    }

}

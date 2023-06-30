package com.bdspro.query;

import com.bdspro.datasets.Dataset;

import java.sql.Timestamp;

public abstract class QueryTranslator {
    public abstract String translateCreateTable(Dataset dataset);

    public abstract String translateSelectCount(String tableName);

    public abstract String translateInsertInto(Dataset dataset, String[] values);

    public abstract String translateBatchInsertInto(Dataset dataset, String[][] batch);

    public abstract String translateExactPoint(Dataset dataset, Timestamp timestamp);

    public abstract String translateRangeAnyEntity(Dataset dataset, Timestamp from, Timestamp until);

    public abstract String translateRangeSingleEntity(Dataset dataset, String entity, Timestamp from, Timestamp until);

    public abstract String translateRangeWithAggregationOnTimeColumn(Dataset dataset, Timestamp from, Timestamp until);

    public abstract String translateRangeWithValueFilter(Dataset dataset,  Timestamp from, Timestamp until, double value);

    public abstract String translateLastNRecords(Dataset dataset, int numberOfRecords);

    public abstract String translateRangeWithAggregation(Dataset dataset,  Timestamp from, Timestamp until);

    public abstract String translateRangeWithAggregationWithinGroup(Dataset dataset,  Timestamp from, Timestamp until);

    public abstract String translateRangeWithAggregationAndValueFilter(Dataset dataset, Timestamp from, Timestamp until, double value);

    public abstract String translateRangeWithLimit(Dataset dataset, Timestamp from, Timestamp until, int limit);

//    public abstract String translateRangeWithGroupByTime(Dataset dataset, Timestamp from, Timestamp until);

    public abstract String translateRangeWithOrderByValue(Dataset dataset, Timestamp from, Timestamp until);

    public abstract String translateLatestPoint(Dataset dataset);
    //TODO: rest of queries

}

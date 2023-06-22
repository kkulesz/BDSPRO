package com.bdspro.query;

import com.bdspro.datasets.Dataset;

import java.sql.Timestamp;

public abstract class QueryTranslator {
    public abstract String translateCreateTable(Dataset dataset);

    public abstract String translateInsertInto(Dataset dataset, String[] values);

    public abstract String translateBatchInsertInto(Dataset dataset, String[][] batch);

    public abstract String translateExactPoint(Dataset dataset, Timestamp timestamp);

    public abstract String translateRangeAnyEntity(Dataset dataset, Timestamp from, Timestamp until);

    public abstract String translateRangeSingleEntity(Dataset dataset, String entity, Timestamp from, Timestamp until);

    public abstract String translateRangeQueryWithAggregation(Dataset dataset, Timestamp from, Timestamp until);


    //TODO: rest of queries

}

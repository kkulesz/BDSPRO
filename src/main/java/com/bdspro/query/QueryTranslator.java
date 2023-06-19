package com.bdspro.query;

import com.bdspro.datasets.Dataset;

import java.sql.Timestamp;

public abstract class QueryTranslator {
    public abstract String translate(QueryType type);

    public abstract String translateCreateTable(Dataset dataset);

    public abstract String translateInsertInto(Dataset dataset, String[][] values);

    public abstract String translateExactPoint(Dataset dataset, Timestamp timestamp);

    public abstract String translateRangeAnyEntity(Dataset dataset, Timestamp from, Timestamp until);

    public abstract String translateRangeQueryWithAggregation(Dataset dataset, Timestamp from, Timestamp until);


    //TODO: rest of queries

}

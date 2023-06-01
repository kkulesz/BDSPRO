package query;

import datasets.Dataset;

import java.sql.Timestamp;
import java.util.List;

public abstract class QueryTranslator {
    // TODO: 2 or 3 queries (exact point, general range and one aggregate ), insert into, create table,
    public abstract String translate(QueryType type);

    public abstract String translateCreateTable(Dataset dataset); // CREATE TABLE ? (? ?, ? ?)
//    String translateInsertInto(Dataset dataset, List<String> values);
//    String translateExactPointQuery(Timestamp time, String entity);
//    String translateRangeSingleEntity(Timestamp from, Timestamp until, String entity);
//    String translateRangeAnyEntity(Timestamp from, Timestamp until);

    //TODO: rest of queries

}

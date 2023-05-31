package query;

import datasets.Dataset;

import java.sql.Timestamp;
import java.util.List;

public interface QueryTranslator {

    String translateCreateTable(Dataset dataset);
    String translateInsertInto(Dataset dataset, List<String> values);
    String translateExactPointQuery(Timestamp time, String entity);
    String translateRangeSingleEntity(Timestamp from, Timestamp until, String entity);
    String translateRangeAnyEntity(Timestamp from, Timestamp until);

    //TODO: rest of queries

}

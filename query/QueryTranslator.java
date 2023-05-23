package query;

import java.sql.Timestamp;

public interface QueryTranslator {

    String translateExactPointQuery(Timestamp time, String entity);
    String translateRangeSingleEntity(Timestamp from, Timestamp until, String entity);
    String translateRangeAnyEntity(Timestamp from, Timestamp until);

    //TODO: rest of queries

}

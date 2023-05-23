package query;

import java.sql.Timestamp;

public class SqlQueryTranslator implements QueryTranslator{


    @Override
    public String translateExactPointQuery(Timestamp time, String entity) {
        return null;
    }

    @Override
    public String translateRangeSingleEntity(Timestamp from, Timestamp until, String entity) {
        return null;
    }

    @Override
    public String translateRangeAnyEntity(Timestamp from, Timestamp until) {
        return null;
    }
}

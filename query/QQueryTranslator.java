package query;

import datasets.Dataset;

import java.sql.Timestamp;
import java.util.List;

public class QQueryTranslator implements QueryTranslator{

    @Override
    public String translateCreateTable(Dataset dataset) {
        return null;
    }

    @Override
    public String translateInsertInto(Dataset dataset, List<String> values) {
        return null;
    }

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

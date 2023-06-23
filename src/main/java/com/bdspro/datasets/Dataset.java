package com.bdspro.datasets;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface Dataset {
    String getTableName();
    List<Map.Entry<String, ColumnType>> getColumnNamesWithTypes();
    String getCsvName();
    String getTimeStampColumnName();
    String getEntityColumnName();
    String getValueColumnName();

    // those methods below are for query generation purpose
    String getExampleEntity();
    Timestamp getExamplePointTimeStamp();
    Map.Entry<Timestamp, Timestamp> getExampleSmallRange();
    Map.Entry<Timestamp, Timestamp> getExampleBigRange();

}

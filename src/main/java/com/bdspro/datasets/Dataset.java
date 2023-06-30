package com.bdspro.datasets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface Dataset {
    String getTableName();
    List<Map.Entry<String, ColumnType>> getColumnNamesWithTypes();
    String getCsvName();
    default long getCsvFileSize() {
        try {
            return Files.size(Paths.get(getCsvName()));
        }
        catch (IOException e) {
            return -1L;
        }
    }
    String getTimeStampColumnName();
    String getEntityColumnName();
    String getValueColumnName();

    // those methods below are for query generation purpose
    String getExampleEntity();
    Timestamp getExamplePointTimeStamp();
    Map.Entry<Timestamp, Timestamp> getExampleSmallRange();
    Map.Entry<Timestamp, Timestamp> getExampleBigRange();
    double getExampleValue();

}

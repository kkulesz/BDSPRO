package com.bdspro.query.sql;

import com.bdspro.datasets.ColumnType;

import java.util.Map;


public final class SqlColumnTypeMapper {
    public static String mapColumnType(ColumnType type){
        if (type == ColumnType.TIMESTAMP)
            return "DATE";
        else if (type == ColumnType.NUMERIC)
            return "NUMERIC";
        else if (type == ColumnType.STRING)
            return "TEXT";
        else
            return ""; // TODO: its ugly but exception makes it uglier, let's leave it for now in order to not handle exception
    }

    public static String mapValueToMatchTypeFormat(Map.Entry<ColumnType, String> kv){
        ColumnType type = kv.getKey();
        String value = kv.getValue();

        if (type == ColumnType.TIMESTAMP || type == ColumnType.STRING)
            return String.format("'%s'", value);
        else if (type == ColumnType.NUMERIC)
            return value;
        else
            return ""; // TODO: its ugly but exception makes it uglier, let's leave it for now in order to not handle exception
    }
}

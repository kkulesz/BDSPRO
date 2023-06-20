package com.bdspro.datasets;

import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestDataset implements Dataset{
    @Override
    public String getTableName() {
        return "test_table";
    }

    @Override
    public List<Map.Entry<String, ColumnType>> getColumnNamesWithTypes() {
        var list = new ArrayList<Map.Entry<String, ColumnType>>(){};

        list.add(new AbstractMap.SimpleEntry<>("Period", ColumnType.TIMESTAMP));
        list.add(new AbstractMap.SimpleEntry<>("Revenue", ColumnType.NUMERIC));
        list.add(new AbstractMap.SimpleEntry<>("Sales_quantity", ColumnType.NUMERIC));
        list.add(new AbstractMap.SimpleEntry<>("Average_cost", ColumnType.NUMERIC));
        list.add(new AbstractMap.SimpleEntry<>("The_average_annual_payroll_of_the_region", ColumnType.NUMERIC));

        return list;
    }

    @Override
    public String getCsvName() {
        return "TestData/test.csv";
    }

    @Override
    public String getTimeStampColumnName() {
        return "Period";
    }

    @Override
    public String getEntityColumnName() { return "The_average_annual_payroll_of_the_region"; }

    @Override
    public String getExampleEntity() { return "30024676"; }

    @Override
    public Timestamp getExamplePointTimeStamp() {
        return Timestamp.valueOf("2015-01-01 00:00:00");
    }

    @Override
    public Map.Entry<Timestamp, Timestamp> getExampleSmallRange() {
        return new AbstractMap.SimpleEntry<Timestamp, Timestamp>(
                Timestamp.valueOf("2014-01-01 00:00:00"),
                Timestamp.valueOf("2015-01-01 00:00:00")
        );
    }

    @Override
    public Map.Entry<Timestamp, Timestamp> getExampleBigRange() {
        return new AbstractMap.SimpleEntry<Timestamp, Timestamp>(
                Timestamp.valueOf("1999-01-01 00:00:00"),
                Timestamp.valueOf("2025-01-01 00:00:00")
        );
    }
}

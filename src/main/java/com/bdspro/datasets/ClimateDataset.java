package com.bdspro.datasets;

import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClimateDataset implements Dataset{

    @Override
    public String getTableName() {
        return "climate2019";
    }

    @Override
    public List<Map.Entry<String, ColumnType>> getColumnNamesWithTypes() {

        List<Map.Entry<String, ColumnType>> list = new ArrayList<>(){};

        list.add(new AbstractMap.SimpleEntry<>("SensorID", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("Date", ColumnType.TIMESTAMP));
        list.add(new AbstractMap.SimpleEntry<>("MeasurementType", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("Value1", ColumnType.NUMERIC));
        list.add(new AbstractMap.SimpleEntry<>("AddData1", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("AddData2", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("AddData3", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("AddData4", ColumnType.STRING));


        return list;
    }

    @Override
    public String getCsvName() {
        return "dataFiles/2019_c.csv";
    }

    @Override
    public String getTimeStampColumnName() {
        return "Date";
    }

    @Override
    public String getEntityColumnName() {
        return "SensorID";
    }

    @Override
    public String getValueColumnName() { return "Value1"; }

    @Override
    public String getExampleEntity() {
        return "USS0011G06S";
    }

    @Override
    public Timestamp getExamplePointTimeStamp() {
        return Timestamp.valueOf("2019-01-01 00:00:00");
    }

    @Override
    public Map.Entry<Timestamp, Timestamp> getExampleSmallRange() {
        return new AbstractMap.SimpleEntry<>(
                Timestamp.valueOf("2019-01-05 00:00:00"),
                Timestamp.valueOf("2019-01-10 00:00:00")
        );
    }

    @Override
    public Map.Entry<Timestamp, Timestamp> getExampleBigRange() {
        return new AbstractMap.SimpleEntry<>(
                Timestamp.valueOf("2019-01-15 00:00:00"),
                Timestamp.valueOf("2019-08-10 00:00:00")
        );
    }

    @Override
    public double getExampleValue() {
        return 39; //TODO: insert some value that actually is present in dataset
    }
}

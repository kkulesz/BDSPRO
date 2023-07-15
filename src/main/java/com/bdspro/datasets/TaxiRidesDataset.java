package com.bdspro.datasets;

import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaxiRidesDataset implements Dataset{
    @Override
    public String getTableName() {
        return "taxi_rides";
    }

    @Override
    public List<Map.Entry<String, ColumnType>> getColumnNamesWithTypes() {
        var list = new ArrayList<Map.Entry<String, ColumnType>>(){};

        list.add(new AbstractMap.SimpleEntry<>("VendorID", ColumnType.STRING));                 //entity column
        list.add(new AbstractMap.SimpleEntry<>("tpep_pickup_datetime", ColumnType.TIMESTAMP));  // ts column
        list.add(new AbstractMap.SimpleEntry<>("tpep_dropoff_datetime", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("passenger_count", ColumnType.NUMERIC));         //value column
        list.add(new AbstractMap.SimpleEntry<>("trip_distance", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("pickup_longitude", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("pickup_latitude", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("RateCodeID", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("store_and_fwd_flag", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("dropoff_longitude", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("dropoff_latitude", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("payment_type", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("fare_amount", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("extra", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("mta_tax", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("tip_amount", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("tolls_amount", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("improvement_surcharge", ColumnType.STRING));
        list.add(new AbstractMap.SimpleEntry<>("total_amount", ColumnType.STRING));

        return list;
    }

    @Override
    public String getCsvName() { return "src/main/java/com/bdspro/test/TestData/taxi_rides-small.csv"; }

    @Override
    public String getTimeStampColumnName() { return "tpep_pickup_datetime"; }

    @Override
    public String getEntityColumnName() { return "VendorID"; }

    @Override
    public String getValueColumnName() { return "passenger_count";}

    @Override
    public String getExampleEntity() { return "1"; }

    @Override
    public Timestamp getExamplePointTimeStamp() { return Timestamp.valueOf("2015-01-15 19:05:39"); }

    @Override
    public Map.Entry<Timestamp, Timestamp> getExampleSmallRange() {
        return new AbstractMap.SimpleEntry<>(
                Timestamp.valueOf("2015-01-15 19:05:39"),
                Timestamp.valueOf("2015-01-10 20:33:39")
        );
    }

    @Override
    public Map.Entry<Timestamp, Timestamp> getExampleBigRange() {
        return new AbstractMap.SimpleEntry<>(
                Timestamp.valueOf("2015-01-15 19:05:39"),
                Timestamp.valueOf("2015-01-24 00:26:42")
        );
    }

    @Override
    public double getExampleValue() {
        return 1;
    }
}

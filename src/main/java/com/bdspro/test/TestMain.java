package com.bdspro.test;

import com.bdspro.databases.TimescaleDb;
import com.bdspro.datasets.TestDataset;

import java.sql.SQLException;

public class TestMain {

    public static void main(String... args) throws SQLException{
        var ds = new TestDataset();
        var db = new TimescaleDb();
        //var benchmark = new Benchmark(50, db, 0,ds);
        //benchmark.run();
    }
}
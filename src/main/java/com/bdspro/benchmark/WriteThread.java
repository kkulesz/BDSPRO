package com.bdspro.benchmark;

import com.bdspro.databases.Database;

import java.util.ArrayList;
import java.util.List;

public class WriteThread extends Thread {

    private final int writeFrequency;
    private final Database db;
    private final String[] queries;

    final List<BenchmarkResult.WriteQueryResult> results;


    public WriteThread(int writeFrequency, Database db, String[]  queries) {
        super();
        this.db = db;
        this.writeFrequency = writeFrequency;
        this.queries = queries;
        results = new ArrayList<>();
    }
    public void run(){
        for (String query : queries) {
            long start = System.nanoTime();
            db.runStatement(query);
            long finish = System.nanoTime();
            long elapsedTime = finish - start;
            BenchmarkResult.WriteQueryResult result = new BenchmarkResult.WriteQueryResult(elapsedTime);
            results.add(result);
            try {
                Thread.sleep(writeFrequency);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

package com.bdspro.benchmark;

import com.bdspro.databases.Database;
import com.bdspro.query.QueryType;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.bdspro.benchmark.BenchmarkResult.*;

public class ReadThread  extends Thread{

    Database db;
    Pair<QueryType, String>[] queries;
    List<ReadQueryResult> results;

    private final int rowCount;

    public ReadThread(Database db, Pair<QueryType, String>[] queries, int rowCount){
        super();
        this.db = db;
        this.queries = queries;
        results = new ArrayList<>();
        this.rowCount = rowCount;
    }

    @Override
    public void run(){
        for (int i=0; i<queries.length; i++) {
            long start = System.nanoTime();
            int count = db.runQuery(queries[i].component2());
            long finish = System.nanoTime();
            long elapsedTime = finish - start;
            ReadQueryResult result = new ReadQueryResult(count*1.0/rowCount, elapsedTime, queries[i].component1());
            results.add(result);
        }
    }
}

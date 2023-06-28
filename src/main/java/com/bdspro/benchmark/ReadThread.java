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

    public ReadThread(Database db, Pair<QueryType, String>[] queries){
        super();
        this.db = db;
        this.queries = queries;
        results = new ArrayList<>();
    }

    @Override
    public void run(){
        for (int i=0; i<queries.length; i++) {
            long start = System.nanoTime();
            int count = db.runQuery(queries[i].component2());
            long finish = System.nanoTime();
            long elapsedTime = finish - start;
            // TODO: actually store selectivity instead of count here -> needs parameter total number of rows
            // we can either run a ```SELECT COUNT(*) FROM TABLE;``` every time to be precice (keep in mind that the writer thread
            // is permanently adding data, or we store the count once, then every selectivity will be off by a little
            ReadQueryResult result = new ReadQueryResult(count, elapsedTime, queries[i].component1());
            results.add(result);
        }
    }
}

package com.bdspro.benchmark;

import com.bdspro.databases.Database;

public class ReadThread  extends Thread{

    Database db;
    String[] queries;

    public ReadThread(Database db, String[] queries){
        //TODO: change data type of queries to be abe to pass not just sql string but also query type (for logging)
        super();
        this.db = db;
        this.queries = queries;
    }

    public void run(){
        for (int i=0; i<queries.length; i++) {
            long start = System.nanoTime();
            db.runQuery(queries[i]);
            long finish = System.nanoTime();
            long elapsedTime = finish - start;
            //TODO: log time and return
        }
    }
}

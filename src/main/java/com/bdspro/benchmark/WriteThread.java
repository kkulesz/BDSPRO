package com.bdspro.benchmark;

import com.bdspro.databases.Database;

public class WriteThread extends Thread {

    private int writeFrequency;
    private Database db;
    private String[] queries;

    public WriteThread(int writeFrequency, Database db, String[]  queries) {
        super();
        this.db = db;
        this.writeFrequency = writeFrequency;
        this.queries = queries;
    }
    public void run(){
        for (int i=0; i<queries.length; i++) {
            long start = System.nanoTime();
            db.runStatement(queries[i]);
            long finish = System.nanoTime();
            long elapsedTime = finish - start;
            //TODO: log time and return
            try {
                Thread.sleep(writeFrequency);
                //Todo: technically we should subtract elapsed time here from writeFrequency, but then we
                // have to make sure that time units match and that we never get a negative number
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

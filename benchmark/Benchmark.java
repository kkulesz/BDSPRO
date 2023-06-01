package benchmark;

import databases.Database;
import datasets.Dataset;
import query.QueryTranslator;
import query.QueryType;

public class Benchmark {


    private int writePercentage;
    private Database database;
    private int numberOfNodes;
    private Dataset dataset;
    private QueryTranslator queryTranslator;

    public Benchmark(int writePercentage, Database database, int numberOfNodes, Dataset dataset) {
        this.writePercentage = writePercentage;
        this.database = database;
        this.numberOfNodes = numberOfNodes;
        this.dataset = dataset;

        this.database.setup(this.dataset);
        this.queryTranslator = database.getQueryTranslator();
    }

    public void run(QueryType type){
        var queryString = queryTranslator.translate(type);
//        var measurements =

        // simulate workload - meaning we insert some data at some capacity and run read queries as well (like 50/50)
        // run specific query type in order to see how it behaves

        for (int i = 0; i<5; i++){

            this.database.runQuery(queryString);
        }
        return;
    }

}

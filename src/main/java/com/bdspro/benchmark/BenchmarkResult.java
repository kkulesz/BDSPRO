package com.bdspro.benchmark;

import com.bdspro.databases.Database;
import com.bdspro.datasets.Dataset;
import com.bdspro.query.QueryType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BenchmarkResult {

    private int writePercentage;
    private int writeFrequency;
    private Database[] databases;
    int[] compressionRates;
    private int numberOfNodes;
    private Dataset dataset;
    private int batchSize;

    public final Map<String, List<ReadQueryResult>> readResults = new HashMap<>();
    public final Map<String, List<WriteQueryResult>> writeResults = new HashMap<>();

    public BenchmarkResult(int writePercentage, int writeFrequency, Database[] databases, int numberOfNodes, Dataset dataset, int batchSize) {
        this.writePercentage = writePercentage;
        this.writeFrequency = writeFrequency;
        this.databases = databases;
        this.numberOfNodes = numberOfNodes;
        this.dataset = dataset;
        this.batchSize = batchSize;
        this.compressionRates = new int[databases.length];
    }

    public static class ReadQueryResult {
        double selectivity;
        long latency;
        QueryType queryType;

        public ReadQueryResult(double selectivity, long latency, QueryType queryType) {
            this.selectivity = selectivity;
            this.latency = latency;
            this.queryType = queryType;
        }
    }

    public class WriteQueryResult {
        //TODO: what do we need here? ingestion rate??
    }
}

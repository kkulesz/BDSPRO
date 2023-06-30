package com.bdspro.benchmark;

import com.bdspro.databases.Database;
import com.bdspro.datasets.Dataset;
import com.bdspro.query.QueryType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BenchmarkResult {

    private int writePercentage;
    private int writeFrequency;
    @JsonIgnore
    private Database[] databases;
    double[] compressionRates;
    private int numberOfNodes;
    @JsonIgnore
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
        this.compressionRates = new double[databases.length];
    }

    public static class ReadQueryResult {
        public double getSelectivity() {
            return selectivity;
        }

        public long getLatency() {
            return latency;
        }

        public QueryType getQueryType() {
            return queryType;
        }

        double selectivity;
        long latency;
        QueryType queryType;

        public ReadQueryResult(double selectivity, long latency, QueryType queryType) {
            this.selectivity = selectivity;
            this.latency = latency;
            this.queryType = queryType;
        }
    }

    public static class WriteQueryResult {
        //TODO: what do we need here? ingestion rate??
        long latency;

        public long getLatency() {
            return latency;
        }

        public WriteQueryResult(long latency) {
            this.latency = latency;
        }
    }


    public int getWritePercentage() {
        return writePercentage;
    }

    public int getWriteFrequency() {
        return writeFrequency;
    }

    public double[] getCompressionRates() {
        return compressionRates;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    @JsonProperty("dataset")
    public String getDatasetName() {
        return dataset.getClass().getSimpleName();
    }

    public int getBatchSize() {
        return batchSize;
    }
}

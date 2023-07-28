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
    private int datasetRows;
    private int batchSize;

    public final Map<String, List<ReadQueryResult>> readResults = new HashMap<>();
    public final Map<String, List<WriteQueryResult>> writeResults = new HashMap<>();

    public void setDatasetRows(int datasetRows) {
        this.datasetRows = datasetRows;
    }

    public int getDatasetRows() {
        return datasetRows;
    }

    public BenchmarkResult(int writePercentage, int writeFrequency, Database[] databases, int numberOfNodes, Dataset dataset, int rowCount, int batchSize) {
        this.writePercentage = writePercentage;
        this.writeFrequency = writeFrequency;
        this.databases = databases;
        this.numberOfNodes = numberOfNodes;
        this.dataset = dataset;
        this.datasetRows = rowCount;
        this.batchSize = batchSize;
        this.compressionRates = new double[databases.length];
    }

    public static class ReadQueryResult {
        public double getRowCount() {
            return rowCount;
        }

        public long getLatency() {
            return latency;
        }

        public QueryType getQueryType() {
            return queryType;
        }

        double rowCount;
        long latency;
        QueryType queryType;

        public ReadQueryResult(double selectivity, long latency, QueryType queryType) {
            this.rowCount = selectivity;
            this.latency = latency;
            this.queryType = queryType;
        }
    }

    public static class WriteQueryResult {
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

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

    private final int writePercentage;
    private final int writeFrequency;
    final Map<String, Double> compressionRates;
    private final int numberOfNodes;
    @JsonIgnore
    private final Dataset dataset;
    private int datasetRows;
    private final int batchSize;

    public final Map<String, List<ReadQueryResult>> readResults = new HashMap<>();
    public final Map<String, List<WriteQueryResult>> writeResults = new HashMap<>();

    public void setDatasetRows(int datasetRows) {
        this.datasetRows = datasetRows;
    }

    public int getDatasetRows() {
        return datasetRows;
    }

    public BenchmarkResult(int writePercentage, int writeFrequency, int numberOfNodes, Dataset dataset, int rowCount, int batchSize) {
        this.writePercentage = writePercentage;
        this.writeFrequency = writeFrequency;
        this.numberOfNodes = numberOfNodes;
        this.dataset = dataset;
        this.datasetRows = rowCount;
        this.batchSize = batchSize;
        this.compressionRates = new HashMap<>();
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

    public Map<String, Double> getCompressionRates() {
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

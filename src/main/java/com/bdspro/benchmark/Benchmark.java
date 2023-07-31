package com.bdspro.benchmark;

import com.bdspro.databases.Database;
import com.bdspro.datasets.Dataset;
import com.bdspro.query.QueryTranslator;
import com.bdspro.query.QueryType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kotlin.Pair;

import java.sql.Timestamp;
import java.util.*;


// This class should be called with the different possible values for the parameters to test different scenarios.
// Databases should always include all available databases
public class Benchmark {


    private int writePercentage;

    //write frequency in millis
    private int writeFrequency;
    private Database[] databases;
    private int numberOfNodes;
    private Dataset dataset;
    private int numberOfReadQueries;
    private int numberOfWriteQueries;

    private int batchSize;

    private DataGenerator dataGenerator;

    BenchmarkResult result;

    public Benchmark(int writePercentage, int writeFrequency, Database[] databases, int numberOfNodes, Dataset dataset, int numberOfQueries, int batchSize) {
        this.writePercentage = writePercentage;
        this.writeFrequency = writeFrequency;
        this.databases = databases;
        this.numberOfNodes = numberOfNodes;
        this.dataset = dataset;
        this.numberOfWriteQueries = (int)Math.round(numberOfQueries * writePercentage / 100.0);
        this.numberOfReadQueries = numberOfQueries - numberOfWriteQueries;
        this.batchSize = batchSize;

        this.dataGenerator = new DataGenerator(dataset);
        result = new BenchmarkResult(writePercentage, writeFrequency, databases, numberOfNodes, dataset, 0, batchSize);
    }

    private String[][] generateWriteQueryWorkload() {
        String[][] writeQueries = new String[databases.length][numberOfWriteQueries];
        for (int i = 0; i < numberOfWriteQueries; i++) {
            String[][] data = dataGenerator.generateData(batchSize);
            for (int j = 0; j < databases.length; j++) {
                writeQueries[j][i] = databases[j].getQueryTranslator().translateBatchInsertInto(dataset, data);
            }
        }
        return writeQueries;
    }

    private Pair<QueryType, String>[][] generateReadQueryWorkload() {
        Pair<QueryType, String>[][] readQueries = new Pair[databases.length][numberOfReadQueries];
        Random random = new Random();
        for (int i = 0; i < numberOfReadQueries; i++) {
             QueryType type = QueryType.values()[random.nextInt(QueryType.values().length)];
            for (int j=0; j<databases.length; j++) {
                readQueries[j][i] = new Pair<>(type, generateQuery(databases[j].getQueryTranslator(), type));
            }
        }
        return readQueries;
    }

    private String generateQuery(QueryTranslator queryTranslator, QueryType type) {
        switch (type){
            case EXACT_POINT -> {
                return queryTranslator.translateExactPoint(dataset, dataset.getExamplePointTimeStamp());
            }
            case RANGE_ANY_ENTITY -> {
                Map.Entry<Timestamp, Timestamp> timeRange = dataset.getExampleSmallRange();
                return queryTranslator.translateRangeAnyEntity(dataset, timeRange.getKey(), timeRange.getValue());
            }
            case RANGE_SINGLE_ENTITY -> {
                Map.Entry<Timestamp, Timestamp> timeRange = dataset.getExampleSmallRange();
                String entity = dataset.getExampleEntity();
                return queryTranslator.translateRangeSingleEntity(dataset, entity, timeRange.getKey(), timeRange.getValue());
            }
            case RANGE_WITH_VALUE_FILTER -> {
                Map.Entry<Timestamp, Timestamp> timeRange = dataset.getExampleSmallRange();
                double value = dataset.getExampleValue();
                return queryTranslator.translateRangeWithValueFilter(dataset, timeRange.getKey(), timeRange.getValue(), value);
            }
            case RANGE_WITH_AGGREGATION_ON_TIME_COLUMN -> {
                Map.Entry<Timestamp, Timestamp> timeRange = dataset.getExampleSmallRange();
                return queryTranslator.translateRangeWithAggregationOnTimeColumn(dataset, timeRange.getKey(), timeRange.getValue());
            }
            case LAST_N_RECORDS -> {
                return queryTranslator.translateLastNRecords(dataset, 100);
            }
            case RANGE_WITH_AGGREGATION -> {
                Map.Entry<Timestamp, Timestamp> timeRange = dataset.getExampleSmallRange();
                return queryTranslator.translateRangeWithAggregation(dataset, timeRange.getKey(), timeRange.getValue());
            }
            case RANGE_WITH_AGGREGATION_WITHIN_GROUP -> {
                Map.Entry<Timestamp, Timestamp> timeRange = dataset.getExampleSmallRange();
                return queryTranslator.translateRangeWithAggregationWithinGroup(dataset, timeRange.getKey(), timeRange.getValue());
            }
            case RANGE_WITH_AGGREGATION_AND_VALUE_FILTER -> {
                Map.Entry<Timestamp, Timestamp> timeRange = dataset.getExampleSmallRange();
                double value = dataset.getExampleValue();
                return queryTranslator.translateRangeWithAggregationAndValueFilter(dataset, timeRange.getKey(), timeRange.getValue(), value);
            }
            case RANGE_WITH_LIMIT -> {
                Map.Entry<Timestamp, Timestamp> timeRange = dataset.getExampleSmallRange();
                return queryTranslator.translateRangeWithLimit(dataset, timeRange.getKey(), timeRange.getValue(), 100);
            }
//            case RANGE_WITH_GROUP_BY_TIME -> {
//
//            }
            case RANGE_WITH_ORDER_BY_VALUE -> {
                Map.Entry<Timestamp, Timestamp> timeRange = dataset.getExampleSmallRange();
                return queryTranslator.translateRangeWithOrderByValue(dataset, timeRange.getKey(), timeRange.getValue());
            }
            case LATEST_POINT -> {
                return queryTranslator.translateLatestPoint(dataset);
            }
        }
        return "";
    }

    public void run(){
        // generate queries for all databases
        Pair<QueryType, String>[][] readQueries = generateReadQueryWorkload();
        String[][] writeQueries = generateWriteQueryWorkload();

        //loop through all databases
        for (int j = 0; j < databases.length; j++){
            Database db = databases[j];

            System.out.println("Benchmarking Database: " + db.getClass().getSimpleName());

            //setup database
            db.setup(dataset);

            //write whole dataset
            db.load(dataset.getCsvName(), dataset);

            result.setDatasetRows(db.getRowCount(dataset.getTableName()));
            result.compressionRates[j] = db.getSize(dataset.getTableName()) * 1.0 / dataset.getCsvFileSize();

            //start reader and writer thread
            ReadThread reader = new ReadThread(db, readQueries[j], db.getRowCount(dataset.getTableName()));
            WriteThread writer = new WriteThread(writeFrequency, db, writeQueries[j]);


            reader.start();
            writer.start();
            try{
                reader.join();
                writer.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            result.readResults.put(db.getClass().getSimpleName(), reader.results);
            result.writeResults.put(db.getClass().getSimpleName(), writer.results);

            //TODO: do something with the results
            System.out.println("Compression Rate: " + result.compressionRates[j]);

            // cleanup database
            db.cleanup(dataset.getTableName());
        }
    }

    public String getResultAsJSONString(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}

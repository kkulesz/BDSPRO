package com.bdspro.benchmark;

import com.bdspro.databases.ClickHouse;
import com.bdspro.databases.Database;
import com.bdspro.databases.TimescaleDb;
import com.bdspro.datasets.ClimateDataset;
import com.bdspro.datasets.Dataset;
import com.bdspro.datasets.TaxiRidesDataset;
import com.bdspro.query.QueryTranslator;
import com.bdspro.query.QueryType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kotlin.Pair;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;


// This class should be called with the different possible values for the parameters to test different scenarios.
// Databases should always include all available databases
public class Benchmark {

    //write frequency in millis
    private final int writeFrequency;
    private final Database[] databases;
    private final int numberOfNodes;
    private final Dataset dataset;
    private final int numberOfReadQueries;
    private final int numberOfWriteQueries;

    private final int batchSize;

    private final DataGenerator dataGenerator;

    BenchmarkResult result;

    public Benchmark(int writePercentage, int writeFrequency, Database[] databases, int numberOfNodes, Dataset dataset, int numberOfQueries, int batchSize) {
        this.writeFrequency = writeFrequency;
        this.databases = databases;
        this.numberOfNodes = numberOfNodes;
        this.dataset = dataset;
        this.numberOfWriteQueries = (int)Math.round(numberOfQueries * writePercentage / 100.0);
        this.numberOfReadQueries = numberOfQueries - numberOfWriteQueries;
        this.batchSize = batchSize;

        this.dataGenerator = new DataGenerator(dataset);
        result = new BenchmarkResult(writePercentage, writeFrequency, numberOfNodes, dataset, 0, batchSize);
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
            db.setup(dataset, numberOfNodes > 1);

            //write whole dataset
            db.load(dataset.getCsvName(), dataset);

            result.setDatasetRows(db.getRowCount(dataset.getTableName()));
            result.compressionRates.put(db.getClass().getSimpleName(), db.getSize(dataset.getTableName()) * 1.0 / dataset.getCsvFileSize());

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
            System.out.println("Compression Rate: " + result.compressionRates.get(db.getClass().getSimpleName()));

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

    public static void main(String[] args) {
        int j = 0;
        int wp = 0;
        int wf= 0;
        int noq = 0;
        int bs = 0;
        int non = 1;
        Dataset ds = null;
        Database[] databases = new Database[1];
        try (BufferedReader reader = new BufferedReader(new FileReader("/app/parameters.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                switch (parts[0]) {
                    case "counter" -> j = Integer.parseInt(parts[1]);
                    case "wp" -> wp = Integer.parseInt(parts[1]);
                    case "wf" -> wf = Integer.parseInt(parts[1]);
                    case "nq" -> noq = Integer.parseInt(parts[1]);
                    case "bs" -> bs = Integer.parseInt(parts[1]);
                    case "db" -> {
                        switch (parts[1]) {
                            case "TimescaleDB" -> databases[0] = new TimescaleDb();
                            case "Clickhouse" -> databases[0] = new ClickHouse();
                        }
                    }
                    case "ds" -> ds = switch (parts[1]) {
                        case "Climate" -> new ClimateDataset();
                        case "Taxi" -> new TaxiRidesDataset();
                        default -> throw new IllegalStateException("Unexpected value: " + parts[1]);
                    };
                    default -> throw new IllegalStateException("Unexpected Parameter: " + parts[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder resultJson = new StringBuilder();
        String msg = String.format(
                "Running benchmark with configuration: write_percentage=%s, write_frequency=%s, number_of_nodes=%s, dataset=%s, number_of_queries=%s, batch_size=%s",
                wp, wf, non, ds.getTableName(), noq, bs
        );
        System.out.println(msg);
        Benchmark b = (new Benchmark(wp, wf, databases, non, ds, noq, bs));
        b.run();
        resultJson.append(b.getResultAsJSONString());

        //save intermediate result ,so we do not lose them when tasks fails
        //file is "benchmark_result-{NUMBER}", where NUMBER is number of successful runs during this execution
        saveResult(resultJson.toString(), "/results/benchmark_result-" + j);

        resultJson.append(",");
    }

    private static void saveResult(String jsonString, String fileName){
        String resultString = "[" + jsonString + "]";
        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println(resultString);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

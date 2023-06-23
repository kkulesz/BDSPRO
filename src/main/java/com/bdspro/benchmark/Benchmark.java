package com.bdspro.benchmark;

import com.bdspro.databases.Database;
import com.bdspro.datasets.Dataset;
import com.bdspro.query.QueryTranslator;
import com.bdspro.query.QueryType;

import java.util.Random;


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
    }

    public String[][] generateWriteQueryWorkload() {
        String[][] writeQueries = new String[databases.length][numberOfWriteQueries];
        for (int i = 0; i < numberOfWriteQueries; i++) {
            String[][] data = dataGenerator.generateData(batchSize);
            for (int j = 0; j < databases.length; j++) {
                writeQueries[j][i] = databases[j].getQueryTranslator().translateBatchInsertInto(dataset, data);
            }
        }
        return writeQueries;
    }

    public String[][] generateReadQueryWorkload() {
        String[][] readQueries = new String[databases.length][numberOfReadQueries];
        Random random = new Random();
        for (int i = 0; i < numberOfReadQueries; i++) {
             QueryType type = QueryType.values()[random.nextInt(QueryType.values().length)];
            for (int j=0; j<databases.length; j++) {
                readQueries[j][i] = generateQuery(databases[j].getQueryTranslator(), type);
            }
        }
        return readQueries;
    }

    private String generateQuery(QueryTranslator queryTranslator, QueryType type) {
//        System.out.println(type);
        switch (type){
            case EXACT_POINT -> {
                return queryTranslator.translateExactPoint(dataset, dataset.getExamplePointTimeStamp());
            }
            case RANGE_SINGLE_ENTITY -> {
                var timeRange = dataset.getExampleSmallRange();
                var entity = dataset.getExampleEntity();
                return queryTranslator.translateRangeSingleEntity(dataset, entity, timeRange.getKey(), timeRange.getValue());
            }
            case RANGE_ANY_ENTITY -> {
                var timeRange = dataset.getExampleSmallRange();
                return queryTranslator.translateRangeAnyEntity(dataset, timeRange.getKey(), timeRange.getValue());
            }
            // TODO rest of queries
        }
        return "";
    }



    public void run(){
        // generate queries for all databases
        String[][] readQueries = generateReadQueryWorkload();
        String[][] writeQueries = generateWriteQueryWorkload();

        //loop through all databases
        for (int j = 0; j < databases.length; j++){
            Database db = databases[j];

            System.out.println("Benchmarking Database: " + db.getClass().getSimpleName());

            //setup database
            db.setup(dataset);

            //write whole dataset
            db.load(dataset.getCsvName(), dataset);

            System.out.println("Size of table: " + db.getSize(dataset.getTableName()) + " bytes");

            // TODO: check compression

            // start reader and writer thread
            ReadThread reader = new ReadThread(db, readQueries[j]);
            WriteThread writer = new WriteThread(writeFrequency, db, writeQueries[j]);


            reader.start();
            writer.start();
            //TODO: do something with the results

//            db.cleanup(dataset.getTableName());
        }
    }

}

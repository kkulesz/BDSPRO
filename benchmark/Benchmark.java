package benchmark;

import databases.DBMeasurement;

public class Benchmark {


    private int writePercentage;
    private DBMeasurement database;
    private int numberOfNodes;

    public Benchmark(int writePercentage, DBMeasurement database, int numberOfNodes) {
        this.writePercentage = writePercentage;
        this.database = database;
        this.numberOfNodes = numberOfNodes;
    }

    public void run(){
        return;
    }
}

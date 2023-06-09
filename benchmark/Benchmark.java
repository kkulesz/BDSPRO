import java.util.Random;


// This class should be called with the different possible values for the parameters to test different scenarios.
// Databases should always include all available databases
public class Benchmark {


    private int writePercentage;
    private Database[] databases;
    private int numberOfNodes;
    private Dataset dataset;
    private int numberOfQueries;
    private int batchSize;

    public Benchmark(int writePercentage, Database[] databases, int numberOfNodes, Dataset dataset, int numberOfQueries, int batchSize) {
        this.writePercentage = writePercentage;
        this.databases = databases;
        this.numberOfNodes = numberOfNodes;
        this.dataset = dataset;
        this.numberOfQueries = numberOfQueries;
        this.batchSize = batchSize;

    }

    public String[][] generateQueryWorkload() {
        String[][] queries = new String[databases.length][numberOfQueries];
        Random random = new Random();
        for (int i = 0; i < numberOfQueries; i++) {
            int x = random.nextInt(100);

            //write query
            if (x < writePercentage) {
                String[][] data = generateData(batchSize);
                for (int j=0; j<databases.length; j++){
                    queries[j][i] = databases[j].getQueryTranslator().translateInsertInto(dataset, data);
                }
            }
            //read query
            else {
                QueryType type = QueryType.values()[random.nextInt(QueryType.values().length)];
                for (int j=0; j<databases.length; j++) {
                    queries[j][i] = generateQuery(databases[j].getQueryTranslator());
                }
            }
        }
        return queries;
    }

    private String generateQuery(QueryTranslator queryTranslator) {
        //this should probably have a switch with all possible query types
        return "";
    }

    private String[][] generateData(int batchSize) {
        // generates batchSize many records that match the dataset given as a class variable
        return null;
    }

    public void run(){
        // generate queries for all databases
        String[][] queries = generateQueryWorkload();

        //loop through all databases
        for (int j = 0; j < databases.length; j++){

            //setup database
            databases[j].setup(dataset);

            //TODO: write whole dataset and check compression

            // execute all queries and measure time
            for (int i = 0; i < numberOfQueries; i++) {
                //TODO: start a measurement
                databases[j].runQuery(queries[j][i]);
                // TODO: stop the measurement and log results
            }

            //TODO: to something with the results
        }
    }

}

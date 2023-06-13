import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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


    private DataGenerator dataGenerator;

    public Benchmark(int writePercentage, Database[] databases, int numberOfNodes, Dataset dataset, int numberOfQueries, int batchSize) {
        this.writePercentage = writePercentage;
        this.databases = databases;
        this.numberOfNodes = numberOfNodes;
        this.dataset = dataset;
        this.numberOfQueries = numberOfQueries;
        this.batchSize = batchSize;

        this.dataGenerator = new DataGenerator(dataset);
    }

    public String[][] generateQueryWorkload() {
        String[][] queries = new String[databases.length][numberOfQueries];
        Random random = new Random();
        for (int i = 0; i < numberOfQueries; i++) {
            int x = random.nextInt(100);

            //write query
            if (x < writePercentage) {
                String[][] data = dataGenerator.generateData(batchSize);
                for (int j=0; j<databases.length; j++){
                    queries[j][i] = databases[j].getQueryTranslator().translateInsertInto(dataset, data);
                }
            }
            //read query
            else {
                QueryType type = QueryType.values()[random.nextInt(QueryType.values().length)];
                for (int j=0; j<databases.length; j++) {
                    queries[j][i] = generateQuery(databases[j].getQueryTranslator(), type);
                }
            }
        }
        return queries;
    }

    private String generateQuery(QueryTranslator queryTranslator, QueryType type) {
        switch (type){
            case EXACT_POINT -> {
                //return queryTranslator.translateExactPoint(dataset, );
            }
            case RANGE_ANY_ENTITY -> {
                //
            }
            // TODO
        }
        return "";
    }



    public void run(){
        // generate queries for all databases
        String[][] queries = generateQueryWorkload();

        //loop through all databases
        for (int j = 0; j < databases.length; j++){
            Database db = databases[j];

            System.out.println("Benchmarking Database: " + db.getClass().getSimpleName());

            //setup database
            db.setup(dataset);

            //write whole dataset
            db.load(dataset.getCsvName());

            // TODO: and check compression

            // execute all queries and measure time
            for (int i = 0; i < numberOfQueries; i++) {
                long start = System.nanoTime();
                databases[j].runQuery(queries[j][i]);
                long finish = System.nanoTime();
                long elapsedTime = finish - start;
                // TODO: log results
            }

            //TODO: do something with the results
        }
    }

}

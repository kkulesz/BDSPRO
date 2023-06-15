import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DataGenerator {

    Dataset dataset;
    List<Map.Entry<String, ColumnType>> columns;

    public DataGenerator(Dataset dataset) {
        this.dataset = dataset;
        this.columns = dataset.getColumnNamesWithTypes();
    }

    public String[][] generateData(int batchSize) {
        // generates batchSize many records that match the dataset given as a class variable
        String[][] data = new String[batchSize][columns.size()];
        for (int i=0; i<batchSize; i++){
            data[i] = generateRow();
        }
        return data;
    }

    private String[] generateRow() {
        Random random = new Random();
        String[] row = new String[columns.size()];
        for (int i=0; i<columns.size(); i++) {
            switch (columns.get(i).getValue()){
                case TIMESTAMP -> row[i] = Timestamp.from(Instant.now()).toString();
                case NUMERIC -> row[i] = String.valueOf(random.nextDouble());
                case STRING -> row[i] = java.util.UUID.randomUUID().toString();
            }
        }
        return row;
    }
}

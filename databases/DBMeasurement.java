package databases;

import query.QueryType;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


public class DBMeasurement implements Database{

    private final Database database;

    List<Map.Entry<QueryType, Long>> measurements;

    public DBMeasurement(Database database){
        this.database = database;
    }


    @Override
    public int setup() {
        // start measuring
        int return_value = database.setup();
        // stop measuring
        return return_value;
    }

    @Override
    public int load(String csvFile) {
        return database.load(csvFile);
    }

    @Override
    public int cleanup() {
        return database.cleanup();
    }

    @Override
    public int insert(Object data, Timestamp timestamp) {
        return database.insert(data, timestamp);
    }

    @Override
    public int read(String query) {
        return database.read(query);
    }
}

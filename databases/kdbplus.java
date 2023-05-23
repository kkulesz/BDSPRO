package databases;


import java.sql.Timestamp;

public class kdbplus implements Database {

    @Override
    public int setup(){
        return 0;
    }

    @Override
    public int load(String csvFile) {
        return 0;
    }

    @Override
    public int cleanup() {
        return 0;
    }

    @Override
    public int insert(Object data, Timestamp timestamp) {
        return 0;
    }

    @Override
    public int read(String query) {
        return 0;
    }
}

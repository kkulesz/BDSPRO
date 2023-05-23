package databases;

import java.sql.Timestamp;

public interface Database {

    public int setup();

    public int load(String csvFile);

    public int cleanup();

    public int insert(Object data, Timestamp timestamp);

    public int read(String query);

}

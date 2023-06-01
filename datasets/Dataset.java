package datasets;



import java.util.List;
import java.util.Map;

public interface Dataset {
    public String getTableName();
    public List<Map.Entry<String, String>> getColumnNamesWithTypes();
    public String getCsvName();
    public String getTimeStampColumnName();

}
